from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver
from project_manager.models import Task
from .utils import send_task_notification

@receiver(post_save, sender=Task)
def task_saved_handler(sender, instance, created, **kwargs):
    event_type = "task_created" if created else "task_updated"
    send_task_notification(event_type, instance)

@receiver(post_delete, sender=Task)
def task_deleted_handler(sender, instance, **kwargs):
    send_task_notification("task_deleted", instance)
