from django.db import models

class ActivityLog(models.Model):
    ACTION_CHOICES = (
        ('CREATE', 'Create'),
        ('UPDATE', 'Update'),
        ('MEMBER_ADD', 'Member Add'),
        ('MEMBER_REMOVE', 'Member Remove'),
        ('TASK_ADD', 'Task Add'),
        ('TASK_UPDATE', 'Task Update'),
        ('TASK_REMOVE', 'Task Remove'),
        ('COMMENT_ADD', 'Comment Add'),
        ('COMMENT_UPDATE', 'Comment Update'),
        ('COMMENT_REMOVE', 'Comment Remove'),
        ('ISSUE_ADD', 'Issue Add'),
        ('ISSUE_UPDATE', 'Issue Update'),
        ('ISSUE_REMOVE', 'Issue Remove'),
        ('ROLE_ADD', 'Role Add'),
        ('ROLE_UPDATE', 'Role Update'),
        ('ROLE_REMOVE', 'Role Remove'),
        ('ROLE_ASSIGN', 'Role Assign'),
        ('ROLE_UNASSIGN', 'Role Unassign'),
    )
    
    project = models.ForeignKey('project_manager.Project', on_delete=models.CASCADE, related_name='activities')
    user = models.CharField(max_length=150)
    action = models.CharField(max_length=20, choices=ACTION_CHOICES)
    changes = models.JSONField(default=dict)
    description = models.TextField(blank=True)
    timestamp = models.DateTimeField(auto_now_add=True)