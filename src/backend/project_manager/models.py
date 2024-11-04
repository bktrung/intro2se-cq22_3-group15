from django.db import models
from django.contrib.auth import get_user_model

User = get_user_model()

class TimeStampedModel(models.Model):
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        abstract = True
        

class Project(TimeStampedModel):
    name = models.CharField(max_length=255)
    description = models.TextField(blank=True, null=True)
    duedate = models.DateField()
    host = models.ForeignKey(User, on_delete=models.CASCADE, related_name='hosted_projects')
    members = models.ManyToManyField(User, related_name='projects')
    
    
class Status(models.TextChoices):
    PENDING = 'PENDING', 'Pending'
    IN_PROGRESS = 'IN_PROGRESS', 'In Progress'
    COMPLETED = 'COMPLETED', 'Completed'
    
    
class Priority(models.TextChoices):
    LOW = 'LOW', 'Low'
    MEDIUM = 'MEDIUM', 'Medium'
    HIGH = 'HIGH', 'High'
    
    
class Task(TimeStampedModel):
    title = models.CharField(max_length=255)
    description = models.TextField(blank=True, null=True)
    start_date = models.DateField()
    end_date = models.DateField()
    actual_start_date = models.DateField(blank=True, null=True)
    actual_end_date = models.DateField(blank=True, null=True)
    status = models.CharField(max_length=20, choices=Status.choices, default=Status.PENDING)
    priority = models.CharField(max_length=20, choices=Priority.choices, blank=True, null=True)
    project = models.ForeignKey(Project, on_delete=models.CASCADE, related_name='tasks')
    assignee = models.ForeignKey(User, on_delete=models.CASCADE, related_name='assigned_tasks', blank=True, null=True)
    

class Comment(TimeStampedModel):
    content = models.TextField()
    task = models.ForeignKey(Task, on_delete=models.CASCADE, related_name='comments')
    author = models.ForeignKey(User, on_delete=models.CASCADE, related_name='authored_comments')


class Role(models.Model):
    role_name = models.CharField(max_length=50)
    description = models.TextField(blank=True, null=True)
    project = models.ForeignKey(Project, on_delete=models.CASCADE, related_name='roles')
    users = models.ManyToManyField(User, related_name='project_roles')

    class Meta:
        unique_together = ('role_name', 'project')
    

class Issue(TimeStampedModel):
    title = models.CharField(max_length=255)
    description = models.TextField(blank=True, null=True)
    status = models.CharField(max_length=20, choices=Status.choices, default=Status.PENDING)
    project = models.ForeignKey(Project, on_delete=models.CASCADE, related_name='issues')
    reporter = models.ForeignKey(User, on_delete=models.CASCADE, related_name='reported_issues')
    assignee = models.ForeignKey(User, on_delete=models.CASCADE, related_name='assigned_issues', blank=True, null=True)
    task = models.ForeignKey(Task, on_delete=models.CASCADE, related_name='task_issues', blank=True, null=True)