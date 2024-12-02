from django.urls import path
from .views import ChatMessageListView

urlpatterns = [
    path('projects/<int:project_id>/messages/', ChatMessageListView.as_view(), name='chat-messages'),
]
