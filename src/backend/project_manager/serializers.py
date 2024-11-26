from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import Project, Task, Comment, Role, Issue

User = get_user_model()

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email']
        ref_name = 'ProjectManagerUser'
        

class ProjectSerializer(serializers.ModelSerializer):
    host = UserSerializer(read_only=True)
    members = UserSerializer(many=True, read_only=True)

    class Meta:
        model = Project
        fields = ['id', 'name', 'description', 'duedate', 'host', 'members', 'created_at', 'updated_at']
        read_only_fields = ['host', 'created_at', 'updated_at']
        
        
class IssueSerializer(serializers.ModelSerializer):
    reporter = UserSerializer(read_only=True)
    assignee = UserSerializer(read_only=True)
    assignee_id = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), source='assignee', write_only=True, required=False)
    task_id = serializers.PrimaryKeyRelatedField(queryset=Task.objects.all(), required=False, allow_null=True)

    class Meta:
        model = Issue
        fields = [
            'id', 
            'title', 
            'description', 
            'status', 
            'project', 
            'reporter', 
            'assignee', 
            'assignee_id', 
            'task_id',
        ]
        read_only_fields = ['reporter', 'project']

    def validate_assignee_id(self, value):
        project = self.context['project']
        if value not in project.members.all():
            raise serializers.ValidationError("Assignee must be a member of the project.")
        return value
    
        
class TaskSerializer(serializers.ModelSerializer):
    assignee = UserSerializer(read_only=True)
    assignee_id = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), source='assignee', write_only=True, required=False)
    issues = IssueSerializer(many=True, read_only=True, source='task_issues')

    class Meta:
        model = Task
        fields = [
            'id', 
            'title', 
            'description', 
            'start_date', 
            'end_date', 
            'actual_start_date', 
            'actual_end_date', 
            'status',
            'priority',
            'project', 
            'assignee',
            'assignee_id',
            'created_at',
            'updated_at',
            'issues'
            ]
        read_only_fields = ['project', 'created_at', 'updated_at']

    def validate_assignee_id(self, value):
        project = self.context['project']
        if value not in project.members.all():
            raise serializers.ValidationError("Assignee must be a member of the project.")
        return value
    

class CommentSerializer(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)

    class Meta:
        model = Comment
        fields = ['id', 'content', 'author', 'created_at', 'updated_at']
        read_only_fields = ['author', 'created_at', 'updated_at']
        
        
class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Role
        fields = ['id', 'role_name', 'description', 'project']
        read_only_fields = ['project']
    

class ProjectMemberSerializer(serializers.ModelSerializer):
    members = UserSerializer(many=True, read_only=True)
    
    class Meta:
        model = Project
        fields = ['members']