from rest_framework import generics, permissions, status
from rest_framework.exceptions import PermissionDenied
from rest_framework.response import Response
from django.shortcuts import get_object_or_404
from django.contrib.auth import get_user_model
from .models import Project, Task, Role
from .serializers import ProjectSerializer, TaskSerializer, CommentSerializer, RoleSerializer

User = get_user_model()

class IsProjectHostOrReadOnly(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True
        if hasattr(obj, 'project'):
            return obj.project.host == request.user
        return obj.host == request.user
    

class ProjectListCreateView(generics.ListCreateAPIView):
    serializer_class = ProjectSerializer

    def get_queryset(self):
        return Project.objects.filter(members=self.request.user).select_related('host').prefetch_related('members')

    def perform_create(self, serializer):
        project = serializer.save(host=self.request.user)
        project.members.add(self.request.user)
        

class ProjectRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = ProjectSerializer
    permission_classes = [IsProjectHostOrReadOnly]

    def get_queryset(self):
        return Project.objects.filter(members=self.request.user).select_related('host').prefetch_related('members')
    

class ProjectMemberManagementView(generics.GenericAPIView):
    permission_classes = [IsProjectHostOrReadOnly]

    def post(self, request, pk, action):
        project = get_object_or_404(Project, pk=pk)
        self.check_object_permissions(request, project)

        if action == 'add':
            return self.add_member(request, project)
        elif action == 'remove':
            return self.remove_member(request, project)
        else:
            return Response({"detail": "Invalid action."}, status=status.HTTP_400_BAD_REQUEST)

    def add_member(self, request, project):
        username = request.data.get('username')
        
        if not username:
            return Response({"detail": "Please provide username."}, status=status.HTTP_400_BAD_REQUEST)

        user = get_object_or_404(User, username=username)

        if user in project.members.all():
            return Response({"detail": "User is already a member of this project."}, status=status.HTTP_400_BAD_REQUEST)

        project.members.add(user)
        return Response({"detail": "Member added successfully."}, status=status.HTTP_200_OK)

    def remove_member(self, request, project):
        user_id = request.data.get('user_id')
        
        if not user_id:
            return Response({"detail": "Please provide user_id."}, status=status.HTTP_400_BAD_REQUEST)
        
        user = get_object_or_404(User, id=user_id)
        
        if user == project.host:
            return Response({"detail": "Cannot remove the host from the project."}, status=status.HTTP_400_BAD_REQUEST)
        
        if user not in project.members.all():
            return Response({"detail": "User is not a member of this project."}, status=status.HTTP_400_BAD_REQUEST)
        
        project.members.remove(user)
        return Response({"detail": "Member removed successfully."}, status=status.HTTP_200_OK)
    

class TaskListCreateView(generics.ListCreateAPIView):
    serializer_class = TaskSerializer

    def get_queryset(self):
        project_id = self.kwargs['project_id']
        return Task.objects.filter(project_id=project_id).select_related('assignee', 'project')

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['project'] = get_object_or_404(Project, id=self.kwargs['project_id'])
        return context

    def perform_create(self, serializer):
        serializer.save(project=self.get_serializer_context()['project'])
        

class TaskRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = TaskSerializer

    def get_queryset(self):
        project_id = self.kwargs['project_id']
        return Task.objects.filter(project_id=project_id).select_related('assignee', 'project')

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['project'] = get_object_or_404(Project, id=self.kwargs['project_id'])
        return context
    

class CommentListCreateView(generics.ListCreateAPIView):
    serializer_class = CommentSerializer

    def get_queryset(self):
        task = get_object_or_404(Task, id=self.kwargs['task_id'], project_id=self.kwargs['project_id'])
        return task.comments.select_related('author').order_by('-created_at')

    def perform_create(self, serializer):
        task = get_object_or_404(Task, id=self.kwargs['task_id'], project_id=self.kwargs['project_id'])
        if self.request.user not in task.project.members.all():
            raise PermissionDenied("You must be a project member to comment.")
        serializer.save(author=self.request.user, task=task)
        

class CommentRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = CommentSerializer

    def get_queryset(self):
        task = get_object_or_404(Task, id=self.kwargs['task_id'], project_id=self.kwargs['project_id'])
        return task.comments.select_related('author')

    def perform_update(self, serializer):
        if serializer.instance.author != self.request.user:
            raise PermissionDenied("You can only update your own comments.")
        serializer.save()

    def perform_destroy(self, instance):
        if instance.author != self.request.user and not instance.task.project.host == self.request.user:
            raise PermissionDenied("You can only delete your own comments or as the project host.")
        instance.delete()
        
        
class RoleListCreateView(generics.ListCreateAPIView):
    serializer_class = RoleSerializer
    permission_classes = [IsProjectHostOrReadOnly]
    
    def get_queryset(self):
        return Role.objects.filter(project_id=self.kwargs['pk']).select_related('project')
    
    def perform_create(self, serializer):
        project = get_object_or_404(Project, id=self.kwargs['pk'])
        serializer.save(project=project)
        
        
class RoleRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = RoleSerializer
    permission_classes = [IsProjectHostOrReadOnly]
    
    def get_queryset(self):
        return Role.objects.filter(project_id=self.kwargs['project_id']).select_related('project')
    

class RoleManagementView(generics.GenericAPIView):
    permission_classes = [IsProjectHostOrReadOnly]
    
    def post(self, request, project_id, pk, action):
        project = get_object_or_404(Project, id=project_id)
        role = get_object_or_404(Role, pk=pk, project=project)
        self.check_object_permissions(request, role)
        
        if action == 'assign':
            return self.assign_role(request, project, role)
        elif action == 'unassign':
            return self.unassign_role(request, project, role)
        else:
            return Response({"detail": "Invalid action."}, status=status.HTTP_400_BAD_REQUEST)
        
    def assign_role(self, request, project, role):
        user_id = request.data.get('user_id')
        
        if not user_id:
            return Response({"detail": "Please provide user_id."}, status=status.HTTP_400_BAD_REQUEST)
        
        user = get_object_or_404(User, id=user_id)
        
        if user not in project.members.all():
            return Response({"detail": "User must be a project member to be assigned a role."}, status=status.HTTP_400_BAD_REQUEST)
        if user in role.users.all():
            return Response({"detail": "User is already assigned to this role."}, status=status.HTTP_400_BAD_REQUEST)
        
        role.users.add(user)
        return Response({"detail": "User assigned to role successfully."}, status=status.HTTP_200_OK)
    
    def unassign_role(self, request, project, role):
        user_id = request.data.get('user_id')
        
        if not user_id:
            return Response({"detail": "Please provide user_id."}, status=status.HTTP_400_BAD_REQUEST)
        
        user = get_object_or_404(User, id=user_id)
        
        if user not in role.users.all():
            return Response({"detail": "User is not assigned to this role."}, status=status.HTTP_400_BAD_REQUEST)
        
        role.users.remove(user)
        return Response({"detail": "User unassigned from role successfully."}, status=status.HTTP_200_OK)