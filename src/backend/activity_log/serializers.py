from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import ActivityLog

User = get_user_model()

class ActivityLogSerializer(serializers.ModelSerializer):
    username = serializers.SerializerMethodField()
    user = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), write_only=True)

    class Meta:
        model = ActivityLog
        fields = ['id', 'project', 'user', 'username', 'action', 'changes', 'description', 'timestamp']
        read_only_fields = ['id', 'timestamp']

    def get_username(self, obj):
        return obj.user.username