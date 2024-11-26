from rest_framework import generics, permissions
from rest_framework.exceptions import PermissionDenied, NotFound
from .models import ActivityLog
from .serializers import ActivityLogSerializer
from project_manager.models import Project

class ProjectActivityLogListView(generics.ListAPIView):
    """
    API view to retrieve a list of activity logs for a specific project.
    Only accessible to users who are members of the project.
    """
    serializer_class = ActivityLogSerializer

    def get_queryset(self):
        user = self.request.user
        project_id = self.kwargs.get('project_id')

        try:
            project = Project.objects.get(pk=project_id)
        except Project.DoesNotExist:
            raise NotFound(detail="Project not found.")

        # Check if the user is a member of the project
        if not project.members.filter(id=user.id).exists() and project.host != user:
            raise PermissionDenied(detail="You do not have permission to view this project's activity logs.")

        return ActivityLog.objects.filter(project=project).select_related('project', 'user').order_by('-timestamp')