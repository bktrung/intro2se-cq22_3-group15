from rest_framework import permissions


class IsProjectHostOrReadOnly(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True
        if hasattr(obj, 'project'):
            return obj.project.host == request.user
        return obj.host == request.user
    

class IsHostOrAssignee(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True
        return obj.project.host == request.user or obj.assignee == request.user
    
    
class IsHostOrAssigneeOrReporter(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True
        return obj.project.host == request.user or obj.assignee == request.user or obj.reporter == request.user