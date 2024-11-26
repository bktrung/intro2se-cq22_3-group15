from project_manager.serializers import TaskSerializer
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync

def send_task_notification(event_type, instance):
    """
    Sends task notifications over WebSocket with the event type and serialized task data.
    """
    channel_layer = get_channel_layer()
    data = {
        "type": event_type,
        "task": TaskSerializer(instance).data
    }
    # Send to a group named after the project ID
    async_to_sync(channel_layer.group_send)(
        f"project_{instance.project.id}",
        {"type": "task.update", "data": data}
    )