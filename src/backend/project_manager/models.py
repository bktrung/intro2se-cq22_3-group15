from django.db import models
from django.contrib.auth import get_user_model
from django.core.exceptions import ValidationError
from django.utils import timezone

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
    
    def remove_member(self, user):
        """Remove member and reassign their tasks"""
        if user in self.members.all():
            # Update tasks first
            Task.objects.filter(
                project=self,
                assignee=user
            ).update(assignee=None)
            
            Issue.objects.filter(
                project=self,
                assignee=user
            ).update(assignee=None)
            
            # Remove member
            self.members.remove(user)
    
    
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
    assignee = models.ForeignKey(User, on_delete=models.SET_NULL, related_name='assigned_tasks', blank=True, null=True)
  

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
    reporter = models.ForeignKey(User, on_delete=models.SET_NULL, related_name='reported_issues', null=True)
    assignee = models.ForeignKey(User, on_delete=models.SET_NULL, related_name='assigned_issues', blank=True, null=True)
    task = models.ForeignKey(Task, on_delete=models.CASCADE, related_name='task_issues', blank=True, null=True)

    
# Why do i use uppercase name (Ex: "TASK" instead of "Task")? Because it is a convention to use uppercase for constants in Python
# And it looks nicer :p . However, I still have to convert it to Task when I use it in views.py, which is a nuisance.
class RequestStatus(models.TextChoices):
    PENDING = 'PENDING', 'Pending'
    APPROVED = 'APPROVED', 'Approved'
    REJECTED = 'REJECTED', 'Rejected'


class RequestType(models.TextChoices):
    CREATE = 'CREATE', 'Create'
    UPDATE = 'UPDATE', 'Update'
    DELETE = 'DELETE', 'Delete'


class TargetTable(models.TextChoices):
    TASK = 'TASK', 'Task'
    ROLE = 'ROLE', 'Role' 


class ChangeRequest(models.Model):
    project = models.ForeignKey('Project', on_delete=models.CASCADE, related_name='change_requests') 
    requester = models.ForeignKey(User, on_delete=models.CASCADE, related_name='requests_sent')
    request_type = models.CharField(choices=RequestType.choices, max_length=6)
    target_table = models.CharField(choices=TargetTable.choices, max_length=4)
    target_table_id = models.IntegerField()
    status = models.CharField(choices=RequestStatus.choices, max_length=8, default=RequestStatus.PENDING)
    description = models.TextField(blank=True, null=True)
    new_data = models.JSONField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    reviewed_by = models.ForeignKey(User, on_delete=models.CASCADE, related_name='requests_reviewed', blank=True, null=True)
    reviewed_at = models.DateTimeField(blank=True, null=True)
    declined_reason = models.TextField(blank=True, null=True)
    
    def clean(self):
        if self.status != 'REJECTED' and self.declined_reason:
            self.declined_reason = None
        if (self.request_type == 'CREATE' or self.request_type == 'UPDATE') and (not self.new_data or self.new_data == {}):
            raise ValidationError("Creation/Updation requests should include new data.")
        if self.request_type == 'DELETE' and self.new_data:
            raise ValidationError("Deletion requests should not include new data.")
        
        if self.target_table == 'TASK':
            allowed_fields = ['title', 'description', 'start_date', 'end_date', 'status', 'priority']
        elif self.target_table == 'ROLE':
            allowed_fields = ['role_name', 'description', 'users']
        else:
            raise ValidationError(f"Invalid target_table: {self.target_table}")

        if self.new_data:
            for key in self.new_data.keys():
                if key not in allowed_fields:
                    raise ValidationError(f"Field '{key}' is not allowed for {self.target_table}.")
