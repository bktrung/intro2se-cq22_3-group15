from django.contrib import admin
from .models import Project, Task, Comment, Role, Issue, ChangeRequest

class ProjectAdmin(admin.ModelAdmin):
    list_display = ('name', 'description', 'duedate', 'host', 'created_at', 'updated_at', 'get_members')
    list_filter = ('duedate', 'created_at')
    search_fields = ('name', 'description', 'host__username')
    filter_horizontal = ('members',)
    readonly_fields = ('created_at', 'updated_at',)
    
    def get_members(self, obj):
        return ", ".join([user.username for user in obj.members.all()])
    get_members.short_description = 'Project Members'  # Column header in admin

class TaskAdmin(admin.ModelAdmin):
    list_display = ('title', 'description', 'status', 'start_date', 'end_date', 'priority', 'assignee', 'project', 'created_at', 'updated_at')
    list_filter = ('status', 'priority', 'start_date', 'end_date', 'created_at')
    search_fields = ('title', 'description', 'assignee__username', 'project__name')
    readonly_fields = ('project', 'created_at', 'updated_at',)

class CommentAdmin(admin.ModelAdmin):
    list_display = ('content', 'task', 'author', 'created_at')
    list_filter = ('created_at',)  # Fix: Add comma for single-item tuple
    search_fields = ('content', 'task__title', 'author__username')
    readonly_fields = ('task', 'author', 'created_at',)

class RoleAdmin(admin.ModelAdmin):
    list_display = ('role_name', 'description', 'project', 'get_users')
    search_fields = ('role_name', 'description', 'project__name', 'users__username')
    filter_horizontal = ('users',)
    readonly_fields = ('project',)

    def get_users(self, obj):
        return ", ".join([user.username for user in obj.users.all()])
    get_users.short_description = 'Users'

class IssueAdmin(admin.ModelAdmin):
    list_display = ('title', 'status', 'project', 'reporter', 'assignee', 'task', 'created_at', 'updated_at')
    list_filter = ('status', 'created_at', 'project')
    search_fields = ('title', 'description', 'reporter__username', 'assignee__username', 'project__name')
    readonly_fields = ('project', 'reporter', 'created_at', 'updated_at',)

class ChangeRequestAdmin(admin.ModelAdmin):
    list_display = ('project', 'requester', 'request_type', 'target_table', 'status', 'created_at', 'reviewed_by')
    list_filter = ('status', 'request_type', 'target_table', 'created_at')
    search_fields = ('project__name', 'requester__username', 'reviewed_by__username', 'description')
    readonly_fields = ('system_description',)
    readonly_fields = ('project', 'requester', 'created_at', 'reviewed_by', 'reviewed_at', 'status', 'system_description')

admin.site.register(Project, ProjectAdmin)
admin.site.register(Task, TaskAdmin)
admin.site.register(Comment, CommentAdmin)
admin.site.register(Role, RoleAdmin)
admin.site.register(Issue, IssueAdmin)
admin.site.register(ChangeRequest, ChangeRequestAdmin)