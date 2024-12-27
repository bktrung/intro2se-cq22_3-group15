from django.urls import path
from .views import *

urlpatterns = [
    path('projects/', ProjectListCreateView.as_view(), name='project-list-create'),
    path('projects/<int:pk>/', ProjectRetrieveUpdateDestroyView.as_view(), name='project-detail'),
    path('projects/<int:pk>/members/', ProjectMemberRetrieveView.as_view(), name='project-member-retrieve'),
    path('projects/<int:pk>/members/<str:action>/', ProjectMemberManagementView.as_view(), name='project-member-management'),
    path('projects/<int:project_id>/tasks/', TaskListCreateView.as_view(), name='task-list-create'),
    path('projects/<int:project_id>/tasks/<int:pk>/', TaskRetrieveUpdateDestroyView.as_view(), name='task-detail'),
    path('projects/<int:project_id>/tasks/<int:task_id>/comments/', CommentListCreateView.as_view(), name='comment-list-create'),
    path('projects/<int:project_id>/tasks/<int:task_id>/comments/<int:pk>/', CommentRetrieveUpdateDestroyView.as_view(), name='comment-detail'),
    path('projects/<int:pk>/roles/', RoleListCreateView.as_view(), name='role-list-create'),
    path('projects/<int:project_id>/roles/<int:role_id>/non-members/', RoleNonMembersListView.as_view(), name='role-non-members-list'),
    path('projects/<int:project_id>/roles/<int:pk>/', RoleRetrieveUpdateDestroyView.as_view(), name='role-retrieve-update-destroy'),
    path('projects/<int:project_id>/roles/<int:pk>/<str:action>/', RoleManagementView.as_view(), name='role-management'),
    path('projects/<int:project_id>/members/<int:pk>/roles/', MemberRoleListView.as_view(), name='member-role-list'),
    path('projects/<int:project_id>/issues/', IssueListCreateView.as_view(), name='issue-list-create'),
    path('projects/<int:project_id>/issues/<int:pk>/', IssueRetrieveUpdateDestroyView.as_view(), name='issue-retrieve-update-destroy'),
    path('projects/<int:project_id>/change-requests/', ChangeRequestListCreateView.as_view(), name='change-request-list-create'),
    path('projects/<int:project_id>/change-requests/<int:pk>/', ChangeRequestActionView.as_view(), name='change-request-action'),
    path('users/self/', UserRetrieveView.as_view(), name='user-retrieve'),
    path('projects/<int:pk>/empower/', ProjectHostEmpowerView.as_view(), name='project-host-empower'),
    path('projects/<int:pk>/progress/track/', ProjectProgressTrackingView.as_view(), name='project-progress-track'),
    path('projects/<int:project_id>/gantt-chart/', TaskGanttChartListView.as_view(), name='task-gantt-chart-list'),
    path('projects/<int:pk>/quit/', ProjectMemberQuitView.as_view(), name='project-member-quit'),
]
