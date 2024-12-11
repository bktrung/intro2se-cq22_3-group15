from rest_framework import serializers
from django.contrib.auth import get_user_model
from models import DeviceToken

User = get_user_model()

class DeviceTokenSerializer(serializers.ModelSerializer):
    class Meta:
        model = DeviceToken
        fields = ['token']