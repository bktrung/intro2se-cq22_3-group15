from rest_framework import generics, permissions, status
from rest_framework.exceptions import PermissionDenied, ValidationError
from rest_framework.response import Response
from django.shortcuts import get_object_or_404
from django.contrib.auth import get_user_model
from django.utils import timezone
from .models import Project, Task, Role, Issue, Comment, ChangeRequest, RequestStatus, RequestType, TargetTable
from .serializers import ProjectSerializer, TaskSerializer, CommentSerializer, RoleSerializer, IssueSerializer, ProjectMemberSerializer, ChangeRequestSerializer
from .permissons import IsProjectHostOrReadOnly, IsHostOrAssignee, IsHostOrAssigneeOrReporter

User = get_user_model()    

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
        project = self.get_serializer_context()['project']
        
        # Create validated data with _current_user
        validated_data = serializer.validated_data
        validated_data['project'] = project
        
        # Create instance manually without saving
        task = Task(**validated_data)
        task._current_user = self.request.user
        
        # Now save to DB
        task.save()
        
        # Update serializer instance
        serializer.instance = task
        

class TaskRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = TaskSerializer
    permission_classes = [IsHostOrAssignee]

    def get_queryset(self):
        project_id = self.kwargs['project_id']
        return Task.objects.filter(project_id=project_id).select_related('assignee', 'project')

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['project'] = get_object_or_404(Project, id=self.kwargs['project_id'])
        return context
    
    def perform_update(self, serializer):
        instance = serializer.instance
        instance._current_user = self.request.user
        serializer.save()

    def perform_destroy(self, instance):
        instance._current_user = self.request.user
        instance.delete()
    

class CommentListCreateView(generics.ListCreateAPIView):
    serializer_class = CommentSerializer

    def get_queryset(self):
        task = get_object_or_404(Task, id=self.kwargs['task_id'], project_id=self.kwargs['project_id'])
        return task.comments.select_related('author').order_by('-created_at')

    def perform_create(self, serializer):
        task = get_object_or_404(Task, id=self.kwargs['task_id'], project_id=self.kwargs['project_id'])
        if self.request.user not in task.project.members.all():
            raise PermissionDenied("You must be a project member to comment.")
        
        validated_data = serializer.validated_data
        validated_data.update({
            'author': self.request.user,
            'task': task
        })
        
        # Create instance with tracking
        instance = Comment(**validated_data)
        instance._current_user = self.request.user
        
        # Save to DB
        instance.save()
        
        # Update serializer instance
        serializer.instance = instance
        

class CommentRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = CommentSerializer

    def get_queryset(self):
        task = get_object_or_404(Task, id=self.kwargs['task_id'], project_id=self.kwargs['project_id'])
        return task.comments.select_related('author')

    def perform_update(self, serializer):
        if serializer.instance.author != self.request.user:
            raise PermissionDenied("You can only update your own comments.")
        instance = serializer.instance
        instance._current_user = self.request.user
        serializer.save()

    def perform_destroy(self, instance):
        if instance.author != self.request.user and not instance.task.project.host == self.request.user:
            raise PermissionDenied("You can only delete your own comments or as the project host.")
        instance._current_user = self.request.user
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
    
    
class IssueListCreateView(generics.ListCreateAPIView):
    serializer_class = IssueSerializer

    def get_queryset(self):
        return Issue.objects.filter(project_id=self.kwargs['project_id'])

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['project'] = get_object_or_404(Project, id=self.kwargs['project_id'])
        return context

    def perform_create(self, serializer):
        project = self.get_serializer_context()['project']
        if self.request.user not in project.members.all():
            raise PermissionDenied("You must be a project member to report an issue.")
        
        validated_data = serializer.validated_data
        validated_data.update({
            'project': project,
            'reporter': self.request.user,
        })
        
        instance = Issue(**validated_data)
        instance._current_user = self.request.user
        instance.save()
        
        serializer.instance = instance


class IssueRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = IssueSerializer
    permission_classes = [IsHostOrAssigneeOrReporter]

    def get_queryset(self):
        return Issue.objects.filter(project_id=self.kwargs['project_id'])
    
    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['project'] = get_object_or_404(Project, id=self.kwargs['project_id'])
        return context
    
    def perform_update(self, serializer):
        instance = serializer.instance
        instance._current_user = self.request.user
        serializer.save()
    
    def perform_destroy(self, instance):
        instance._current_user = self.request.user
        instance.delete()
    
class ProjectMemberRetrieveView(generics.RetrieveAPIView):
    serializer_class = ProjectMemberSerializer
    
    def get_queryset(self):
        return Project.objects.filter(members=self.request.user).select_related('host').prefetch_related('members')
    
class ChangeRequestListCreateView(generics.ListCreateAPIView):
    queryset = ChangeRequest.objects.all()
    serializer_class = ChangeRequestSerializer

    def get_queryset(self):
        project_id = self.kwargs['project_id']
        project = get_object_or_404(Project, id=project_id)

        if self.request.user not in project.members.all():
            raise PermissionDenied("You must be a project member to view change requests.")

        return ChangeRequest.objects.filter(project=project)

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context['project'] = get_object_or_404(Project, id=self.kwargs['project_id'])
        return context

    def perform_create(self, serializer):
        user = self.request.user
        project_id = self.kwargs['project_id']
        project = get_object_or_404(Project, id=project_id)
        
        if user not in project.members.all():
            raise PermissionDenied("You must be a project member to create a change request.")
        
        # save method does not automatically call the clean method, so we need to call it manually using full_clean
        change_request = ChangeRequest(
            project=project,
            requester=user,
            request_type=serializer.validated_data.get('request_type'),
            target_table=serializer.validated_data.get('target_table'),
            target_table_id=serializer.validated_data.get('target_table_id'),
            description=serializer.validated_data.get('description'),
            new_data=serializer.validated_data.get('new_data'),
        )

        try:
            change_request.full_clean()  
        except ValidationError as e:
            raise ValidationError(e.message_dict)

        change_request.save()
        return Response({"message": "Change request created."}, status=status.HTTP_201_CREATED)
        
        
class ChangeRequestActionView(generics.GenericAPIView):
    def post(self, request, project_id, pk):
        change_request = get_object_or_404(ChangeRequest, pk=pk)
        project = get_object_or_404(Project, id=project_id)
        if request.user != project.host:
            raise PermissionDenied("Only the project host can approve or reject change requests.")
        if change_request.status != RequestStatus.PENDING:
            return Response({"error": "Request has already been reviewed."}, status=status.HTTP_400_BAD_REQUEST)
        
        action = request.data.get('action')
        if action == "approve":
            return self.approve_request(request, change_request)
        elif action == "reject":
            return self.reject_request(request, change_request)
        else:
            return Response({"error": "Invalid action."}, status=status.HTTP_400_BAD_REQUEST)
        
    def approve_request(self, request, change_request):
        model_map = {
            TargetTable.TASK: Task,
            TargetTable.ROLE: Role
        }
        model_class = model_map.get(change_request.target_table)
        
        action_map = {
            RequestType.CREATE: self.generic_create,
            RequestType.UPDATE: self.generic_update,
            RequestType.DELETE: self.generic_delete
        }
        action = action_map.get(change_request.request_type)
        
        return action(request, change_request, model_class)
        
    def reject_request(self, request, change_request):
        declined_reason = request.data.get('declined_reason')
        if not declined_reason:
            return Response({"error": "Declined reason is required when declining a request."}, status=status.HTTP_400_BAD_REQUEST)
        
        change_request.status = RequestStatus.REJECTED
        change_request.declined_reason = declined_reason
        change_request.reviewed_by = request.user
        change_request.reviewed_at = timezone.now()
        change_request.save()
        return Response({"message": "Request has been declined."}, status=status.HTTP_200_OK)
    
    def generic_create(self, request, change_request, model_class):
        try:
            data = change_request.new_data.copy()
            data['project'] = change_request.project

            # Create the object
            obj = model_class.objects.create(**data)
            serializer_class = TaskSerializer if model_class == Task else RoleSerializer
            serializer = serializer_class(obj)
            
            change_request.status = RequestStatus.APPROVED
            change_request.reviewed_by = request.user
            change_request.reviewed_at = timezone.now()
            change_request.save()
            
            return Response(serializer.data, status=status.HTTP_200_OK)
        
        except Exception as e:
            # Auto reject the request if there is an error
            change_request.reviewed_by = request.user
            change_request.reviewed_at = timezone.now()
            change_request.status = RequestStatus.REJECTED
            change_request.declined_reason = str(e)
            change_request.save()
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

    def generic_update(self, request, change_request, model_class):
        try:
            obj = model_class.objects.get(
                id=change_request.target_table_id, 
                project=change_request.project
            )
    
            update_data = change_request.new_data.copy()
            
            # Update object fields
            for key, value in update_data.items():
                setattr(obj, key, value)
            obj.save()
            
            change_request.status = RequestStatus.APPROVED
            change_request.reviewed_by = request.user
            change_request.reviewed_at = timezone.now()
            change_request.save()
            
            return Response({"message": f"{model_class.__name__} updated successfully."}, status=status.HTTP_200_OK)
        
        except Exception as e:
            change_request.reviewed_by = request.user
            change_request.reviewed_at = timezone.now()
            change_request.status = RequestStatus.REJECTED
            change_request.declined_reason = str(e)
            change_request.save()
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

    def generic_delete(self, request, change_request, model_class):
        try:
            obj = model_class.objects.get(id=change_request.target_table_id, project=change_request.project)
            
            obj.delete()

            change_request.status = RequestStatus.APPROVED
            change_request.reviewed_by = request.user
            change_request.reviewed_at = timezone.now()
            change_request.save()
            
            return Response({"message": f"{model_class.__name__} deleted successfully."}, status=status.HTTP_200_OK)
        
        except Exception as e:
            change_request.reviewed_by = request.user
            change_request.reviewed_at = timezone.now()
            change_request.status = RequestStatus.REJECTED
            change_request.declined_reason = str(e)
            change_request.save()
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)