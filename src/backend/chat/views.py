from rest_framework import generics
from .models import ChatMessage
from .serializers import ChatMessageSerializer
from .paginations import ChatMessagePagination

class ChatMessageListView(generics.ListAPIView):
    serializer_class = ChatMessageSerializer
    pagination_class = ChatMessagePagination

    def get_queryset(self):
        project_id = self.kwargs['project_id']
        return ChatMessage.objects.filter(project_id=project_id).order_by('-timestamp')
