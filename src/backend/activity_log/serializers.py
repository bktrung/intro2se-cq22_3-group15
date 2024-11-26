from rest_framework import serializers
from .models import ActivityLog

class ActivityLogSerializer(serializers.ModelSerializer):
    
    class Meta:
        model = ActivityLog
        fields = [
            'id', 'project', 'user', 'action', 'changes', 'description', 'timestamp'
        ]
        read_only_fields = ['id', 'timestamp']
    
    def validate_user(self, value):
        if not value:
            raise serializers.ValidationError("User cannot be null.")
        return value