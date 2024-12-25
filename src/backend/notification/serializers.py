from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import DeviceToken, NotificationLog
from project_manager.serializers import UserSerializer

User = get_user_model()

class DeviceTokenSerializer(serializers.ModelSerializer):
    class Meta:
        model = DeviceToken
        fields = ['token']
        
class NotificationLogSerializer(serializers.ModelSerializer):    
    class Meta:
        model = NotificationLog
        fields = ['id', 'title', 'body', 'created_at', 'is_read']
        read_only_fields = ['id', 'title', 'body', 'created_at']