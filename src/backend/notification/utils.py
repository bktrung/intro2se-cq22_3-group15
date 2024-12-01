from project_manager.serializers import *
from project_manager.models import *
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
    
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
        "model_type": instance.__class__.__name__.lower(),  # e.g. 'task' or 'project'
        "object": serializer(instance).data
    }
    
    async_to_sync(channel_layer.group_send)(
        group_name,
        {"type": "object.update", "data": data}
    )