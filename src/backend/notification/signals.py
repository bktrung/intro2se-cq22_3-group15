from django.db.models.signals import post_save, post_delete
from django.dispatch import receiver
from project_manager.models import Task, Project, Issue, Comment, Role, ChangeRequest
from .utils import send_object_notification

@receiver(post_save, sender=Task)
def task_saved_handler(sender, instance, created, **kwargs):
    event_type = "task_created" if created else "task_updated"
    send_object_notification(event_type, instance)

@receiver(post_delete, sender=Task)
def task_deleted_handler(sender, instance, **kwargs):
    send_object_notification("task_deleted", instance)
    
@receiver(post_save, sender=Project)
def project_saved_handler(sender, instance, created, **kwargs):
    event_type = "project_created" if created else "project_updated"
    send_object_notification(event_type, instance)
    
@receiver(post_delete, sender=Project)
def project_deleted_handler(sender, instance, **kwargs):
    send_object_notification("project_deleted", instance)
    
@receiver(post_save, sender=Issue)
def issue_saved_handler(sender, instance, created, **kwargs):
    event_type = "issue_created" if created else "issue_updated"
    send_object_notification(event_type, instance)
    
@receiver(post_delete, sender=Issue)
def issue_deleted_handler(sender, instance, **kwargs):
    send_object_notification("issue_deleted", instance)
    
@receiver(post_save, sender=Comment)
def comment_saved_handler(sender, instance, created, **kwargs):
    event_type = "comment_created" if created else "comment_updated"
    send_object_notification(event_type, instance)
    
@receiver(post_delete, sender=Comment)
def comment_deleted_handler(sender, instance, **kwargs):
    send_object_notification("comment_deleted", instance)
    
@receiver(post_save, sender=Role)
def role_saved_handler(sender, instance, created, **kwargs):
    event_type = "role_created" if created else "role_updated"
    send_object_notification(event_type, instance)
    
@receiver(post_delete, sender=Role)
def role_deleted_handler(sender, instance, **kwargs):
    send_object_notification("role_deleted", instance)
    
@receiver(post_save, sender=ChangeRequest)
def change_request_saved_handler(sender, instance, created, **kwargs):
    event_type = "change_request_created" if created else "change_request_updated"
    send_object_notification(event_type, instance)
    
@receiver(post_delete, sender=ChangeRequest)
def change_request_deleted_handler(sender, instance, **kwargs):
    send_object_notification("change_request_deleted", instance)
