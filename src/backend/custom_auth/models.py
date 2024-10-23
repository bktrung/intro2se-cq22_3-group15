from django.contrib.auth.models import AbstractUser
from django.utils import timezone
from django.db import models

class CustomUser(AbstractUser):
    email = models.EmailField(unique=True)
    is_verified = models.BooleanField(default=False)
    
class OTPModel(models.Model):
    user = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
    otp = models.CharField(max_length=6)
    #! If django based its time on the device, it would be a security issue
    created_at = models.DateTimeField(auto_now_add=True)
    
    def isValid(self):
        if timezone.now() > self.created_at + timezone.timedelta(minutes=5):
            return False
        else:
            return True