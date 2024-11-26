from rest_framework import generics, permissions
from rest_framework.exceptions import PermissionDenied
from .models import ChatMessage
from .serializers import ChatMessageSerializer
from .paginations import ChatMessagePagination

class ChatMessageListView(generics.ListAPIView):
    serializer_class = ChatMessageSerializer
    pagination_class = ChatMessagePagination


    def get_queryset(self):
        project_id = self.kwargs['project_id']
        return ChatMessage.objects.filter(project_id=project_id).order_by('timestamp')

class ChatMessageListCreateView(generics.ListCreateAPIView):
    serializer_class = ChatMessageSerializer
    permission_classes = [permissions.IsAuthenticated]
    pagination_class = ChatMessagePagination

    def get_queryset(self):
        # Get project_id from URL kwargs
        project_id = self.kwargs['project_id']
        if not project_id:
            raise PermissionDenied(detail="A valid 'project_id' is required.")
        return ChatMessage.objects.filter(project_id=project_id).order_by('timestamp')

    def perform_create(self, serializer):
        # Get project_id from URL kwargs
        project_id = self.kwargs['project_id']
        if not project_id:
            raise PermissionDenied(detail="A valid 'project_id' is required.")
        # Save the message with the currently logged-in user as the author and correct project_id
        serializer.save(author=self.request.user, project_id=project_id)

class ChatMessageDetailView(generics.RetrieveDestroyAPIView):
    queryset = ChatMessage.objects.all()
    serializer_class = ChatMessageSerializer
    permission_classes = [permissions.IsAuthenticated]