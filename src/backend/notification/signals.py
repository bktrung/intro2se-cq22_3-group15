from django.db.models.signals import post_save, pre_save, post_delete, m2m_changed
from django.dispatch import receiver
from project_manager.models import Task, Project, Issue, Comment, Role, ChangeRequest
from chat.models import ChatMessage
from .utils import send_object_notification, log_notification, send_notification_to_user
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from django.contrib.auth import get_user_model
from .tasks import get_firebase_access_token, send_fcm_notification
from .models import DeviceToken
from django.core.cache import cache

User = get_user_model()

### Signal handlers for real time updates via WebSocket

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

@receiver(m2m_changed, sender=Project.members.through)
def project_members_changed_handler(sender, instance, action, pk_set, **kwargs):
    """Handle member changes and send WebSocket notifications"""
    if action in ["post_add", "post_remove"]:
        users = User.objects.filter(pk__in=pk_set)
        
        user_data = [
            {
                "id": user.id,
                "username": user.username,
                "email": user.email
            } for user in users
        ]
        
        event_type = "member_added" if action == "post_add" else "member_removed"
        data = {
            "type": event_type,
            "model_type": "member",
            "object": {
                "project_id": instance.id,
                "affected_members": user_data
            }
        }
        
        channel_layer = get_channel_layer()
        async_to_sync(channel_layer.group_send)(
            f"project_{instance.id}",
            {"type": "object.update", "data": data}
        )
        
###

### Signal handlers for Firebase Cloud Messaging

@receiver(m2m_changed, sender=Project.members.through)
def notify_group_membership(sender, instance, action, pk_set, **kwargs):
    if action in ["post_add", "post_remove"]:
        users = User.objects.filter(pk__in=pk_set)
        title = "Project Membership Update"
        for user in users:
            body = f"You have been {'added to' if action == 'post_add' else 'removed from'} project {instance.name}"
            
            # Log notification
            log_notification(title, body, user)
            send_notification_to_user(title, body, user)
            
@receiver(post_save, sender=ChatMessage)
def notify_new_chat_message(sender, instance, created, **kwargs):
    if not created:
        return
        
    project = instance.project
    author = instance.author
    
    project_members = project.members.exclude(id=author.id).prefetch_related('device_tokens')
    
    # Key format: project_messages_{project_id}_{user_id}
    for user in project_members:
        cache_key = f"project_messages_{project.id}_{user.id}"
        
        # Increment message count in cache
        message_count = cache.incr(cache_key, delta=1, timeout=300)  # Atomic increment
        if message_count == 1:
            cache.expire(cache_key, 300)  # Set expiry only on first increment
                
        # Only send notification if:
        # 1. First message (count=1)
        # 2. Count reaches 5
        # 3. No notification sent in last 2 minutes
        throttle_key = f"notification_sent_{project.id}_{user.id}"
        
        if (message_count == 1 or message_count % 5 == 0) and not cache.get(throttle_key):
                
            title = f"New messages in {project.name}"
            body = f"{message_count} new message{'s' if message_count > 1 else ''}"
            if message_count == 1:
                body = f"{author.username}: {instance.content[:50]}"
                
            send_notification_to_user(title, body, user)
                    
            # Set throttle for 2 minutes
            cache.set(throttle_key, True, timeout=120)
            
@receiver(post_delete, sender=Project)
def notify_project_deleted(sender, instance, **kwargs):
    title = f"Project {instance.name} has been deleted"
    body = "This project has been deleted and all associated data has been removed"
    
    for member in instance.members.all():
        log_notification(title, body, member)
        send_notification_to_user(title, body, member)
            
@receiver(pre_save, sender=Task)
def store_task_state(sender, instance, **kwargs):
    if instance.id:
        try:
            old_instance = Task.objects.get(id=instance.id)
            instance._old_assignee_id = old_instance.assignee_id 
            instance._old_status = old_instance.status
        except Task.DoesNotExist:
            instance._old_assignee_id = None
            instance._old_status = None
            
@receiver(post_save, sender=Task)
def notify_task_assignee(sender, instance, created, **kwargs):
    """Notify user when they are assigned to a task"""
    # Early return if no assignee
    if not instance.assignee:
        return
        
    # Only notify on creation or assignee change
    if not created and instance.assignee_id == instance._old_assignee_id:
        return
        
    # Format notification message
    title = "New Task Assignment"
    body = f"You have been assigned to task: {instance.title} in project {instance.project.name}"
    
    # Send notifications
    log_notification(title, body, instance.assignee)
    send_notification_to_user(title, body, instance.assignee)
                
@receiver(post_save, sender=Task)
def notify_task_completion_to_host(sender, instance, created, **kwargs):
    """Notify project host when task is completed"""
    if not instance.status == "COMPLETED":
        return
        
    title = "Task Completed"
    body = f"Task: {instance.title} has been completed in project {instance.project.name}"
    
    log_notification(title, body, instance.project.host)
    send_notification_to_user(title, body, instance.project.host)
    
@receiver(pre_save, sender=Issue)
def store_issue_state(sender, instance, **kwargs):
    if instance.id:
        try:
            old_instance = Issue.objects.get(id=instance.id)
            instance._old_assignee_id = old_instance.assignee_id
            instance._old_status = old_instance.status
        except Issue.DoesNotExist:
            instance._old_assignee_id = None
            instance._old_status = None
            
@receiver(post_save, sender=Issue)
def notify_issue_assignee(sender, instance, created, **kwargs):
    """Notify user when they are assigned to an issue"""
    # Early return if no assignee
    if not instance.assignee:
        return
        
    # Only notify on creation or assignee change
    if not created and instance.assignee_id == instance._old_assignee_id:
        return
        
    # Format notification message
    title = "New Issue Assignment"
    body = f"You have been assigned to issue: {instance.title} in project {instance.project.name}"
    
    # Send notifications
    log_notification(title, body, instance.assignee)
    send_notification_to_user(title, body, instance.assignee)
    
@receiver(post_save, sender=Issue)
def notify_issue_completion_to_host(sender, instance, created, **kwargs):
    """Notify project host when issue is completed"""
    if not instance.status == "COMPLETED":
        return
        
    title = "Issue Completed"
    body = f"Issue: {instance.title} has been completed in project {instance.project.name}"
    
    log_notification(title, body, instance.project.host)
    send_notification_to_user(title, body, instance.project.host)
    
@receiver(post_save, sender=Issue)
def notify_issue_creation(sender, instance, created, **kwargs):
    if not created:
        return
        
    title = "New Issue Created"
    body = f"New issue: {instance.title} has been created in project {instance.project.name}"
    
    log_notification(title, body, instance.project.host)
    send_notification_to_user(title, body, instance.project.host)
    
@receiver(post_save, sender=Comment)
def notify_comment_creation(sender, instance, created, **kwargs):
    if not created:
        return
        
    title = "New Comment"
    body = f"New comment by {instance.author.username} in task: {instance.task.title}"
    
    log_notification(title, body, instance.task.assignee)
    send_notification_to_user(title, body, instance.task.assignee)
    
@receiver(post_save, sender=ChangeRequest)
def notify_change_request_creation(sender, instance, created, **kwargs):
    if not created:
        return
        
    title = "New Change Request"
    body = f"New change request by {instance.requester.username} in project {instance.project.name}"
    
    log_notification(title, body, instance.project.host)
    send_notification_to_user(title, body, instance.project.host)
    
@receiver(post_save, sender=ChangeRequest)
def notify_change_request_approval(sender, instance, created, **kwargs):
    if not instance.status == "APPROVED":
        return
        
    title = "Change Request Approved"
    body = f"Your change request has been approved in project {instance.project.name}"
    
    log_notification(title, body, instance.requester)
    send_notification_to_user(title, body, instance.requester)
    
@receiver(post_save, sender=ChangeRequest)
def notify_change_request_rejection(sender, instance, created, **kwargs):
    if not instance.status == "REJECTED":
        return
        
    title = "Change Request Rejected"
    body = f"Your change request has been rejected in project {instance.project.name}"
    
    log_notification(title, body, instance.requester)
    send_notification_to_user(title, body, instance.requester)
    
@receiver(m2m_changed, sender=Role.users.through)
def notify_role_assignment(sender, instance, action, pk_set, **kwargs):
    if action in ["post_add", "post_remove"]:
        users = User.objects.filter(pk__in=pk_set)
        title = "Role Assignment"
        for user in users:
            body = f"You have been {'assigned to' if action == 'post_add' else 'removed from'} role {instance.role_name} in project {instance.project.name}"
            
            # Log notification
            log_notification(title, body, user)
            send_notification_to_user(title, body, user)
            