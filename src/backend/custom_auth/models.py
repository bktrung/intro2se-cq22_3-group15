from django.contrib.auth.models import AbstractUser
from django.utils import timezone
from django.db import models
from role.models import Role

class CustomUser(AbstractUser):
    email = models.EmailField(unique=True)
    role = models.ForeignKey(Role, on_delete=models.SET_NULL, null=True)
    is_project_manager = models.BooleanField(default=False)
    is_verified = models.BooleanField(default=False)
    
    def set_role(self, role_name : str):
        role = Role.objects.filter(role_name=role_name).first()
        if not role.exists():
            return False
        self.role = role
        self.save()
    
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