from django.contrib.auth import get_user_model
from rest_framework import serializers
from .models import ChatMessage

User = get_user_model()

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email']
        ref_name = 'ChatUser'

class ChatMessageSerializer(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)
    image_url = serializers.SerializerMethodField()

    class Meta:
        model = ChatMessage
        fields = ['id', 'project', 'author', 'content', 'image_url', 'timestamp']
        read_only_fields = ['project', 'timestamp']

    def get_image_url(self, obj):
        return obj.get_image_url()