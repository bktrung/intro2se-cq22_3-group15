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
    )
    
    project = models.ForeignKey('project_manager.Project', on_delete=models.CASCADE, related_name='activities')
    user = models.CharField(max_length=150)
    action = models.CharField(max_length=20, choices=ACTION_CHOICES)
    changes = models.JSONField(default=dict)
    description = models.TextField(blank=True)
    timestamp = models.DateTimeField(auto_now_add=True)