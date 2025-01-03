from django.db import models
from django.contrib.auth import get_user_model
from project_manager.models import TimeStampedModel

User = get_user_model()

class DeviceToken(TimeStampedModel):
    token = models.CharField(max_length=255, unique=True)
    user = models.ForeignKey(User, on_delete=models.SET_NULL, null=True, related_name='device_tokens')
    

class NotificationLog(models.Model):
    title = models.CharField(max_length=255)
    body = models.TextField()
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=True, related_name='notifications')
    created_at = models.DateTimeField(auto_now_add=True)
    is_read = models.BooleanField(default=False)
    object = models.JSONField(null=True, blank=True)
    
    def __str__(self):
        return self.title