from django.db import models
from django.contrib.auth import get_user_model
from django.core.exceptions import ValidationError

User = get_user_model()

def validate_image_size(image):
    FILE_SIZE_MB = 5
    if image.size > FILE_SIZE_MB * 1024 * 1024:
        raise ValidationError(f"Image size must be less than {FILE_SIZE_MB}MB")


class ChatMessage(models.Model):
    project = models.ForeignKey('project_manager.Project', on_delete=models.CASCADE)
    author = models.ForeignKey(User, on_delete=models.CASCADE)
    content = models.TextField()
    image = models.ImageField(upload_to='chat_images/', null=True, blank=True)
    timestamp = models.DateTimeField(auto_now_add=True)
    
    def get_image_url(self):
        if self.image:
            return self.image.url
        return None