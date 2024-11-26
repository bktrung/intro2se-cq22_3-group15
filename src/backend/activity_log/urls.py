from django.urls import path
from .views import ProjectActivityLogListView

urlpatterns = [
    path('projects/<int:project_id>/activities/', ProjectActivityLogListView.as_view(), name='project-activity-log-list'),
]