from django.db import models
from django.contrib.auth import get_user_model
from project_manager.models import TimeStampedModel

User = get_user_model()

class DeviceToken(TimeStampedModel):
    token = models.CharField(max_length=255, unique=True)
    user = models.ForeignKey(User, on_delete=models.IS_NULL, null=True, related_name='device_tokens')