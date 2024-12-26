from project_manager.serializers import *
from project_manager.models import *
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from .models import NotificationLog, DeviceToken
from .tasks import get_firebase_access_token, send_fcm_notification
    
def get_serializer_for_model(instance):
    """Map models to their serializers"""
    mapping = {
        Task: TaskSerializer,
        Project: ProjectSerializer,
        Comment: CommentDetailSerializer,
        Issue: IssueSerializer,
        Role: RoleSerializer,
        ChangeRequest: ChangeRequestSerializer
    }
    return mapping.get(instance.__class__)

def send_object_notification(event_type, instance, serializer=None):
    """
    If no serializer provided, try to find one automatically
    """
    if serializer is None:
        serializer = get_serializer_for_model(instance)
    
    channel_layer = get_channel_layer()
    
    if isinstance(instance, Project):
        group_name = f"project_{instance.id}"
    elif isinstance(instance, Comment):
        group_name = f"project_{instance.task.project.id}"
    else:
        group_name = f"project_{instance.project.id}"
    
    data = {
        "type": event_type,
        "model_type": instance.__class__.__name__.lower(),
        "object": serializer(instance).data
    }
    
    async_to_sync(channel_layer.group_send)(
        group_name,
        {"type": "object.update", "data": data}
    )
    
def log_notification(title, body, user):
    NotificationLog.objects.create(title=title, body=body, user=user)
    
def send_batch_fcm_notification(access_token, device_tokens, title, body):
    for device in device_tokens:
        send_fcm_notification.delay(access_token, device.token, title, body)
    
def send_notification_to_user(title, body, user):
    device_tokens = DeviceToken.objects.filter(user=user)
    if device_tokens:
        access_token = get_firebase_access_token.delay().get()
        send_batch_fcm_notification(access_token, device_tokens, title, body)
        
def send_notification_in_app(title, body, user):
    channel_layer = get_channel_layer()
    group_name = f"user_{user.id}"
    
    data = {
        "type": "notification",
        "title": title,
        "body": body
    }
    
    async_to_sync(channel_layer.group_send)(
        group_name,
        {"type": "send.notification", "data": data}
    )