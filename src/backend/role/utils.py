from rest_framework import permissions
from django.contrib.auth import get_user_model

user = get_user_model()
class IsProjectManager(permissions.BasePermission):
    def has_permission(self, request, view):
        # Check if the user is authenticated and has the 'Project Manager' role
        return request.user.is_authenticated and request.user.role and request.user.role.role_name == 'Project Manager'
