from django.contrib.auth.models import AbstractUser
from django.db import models

# Create your models here.
class Role(models.Model):
    role_name = models.CharField(max_length=100, unique=True)

        