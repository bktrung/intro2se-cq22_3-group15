from django.urls import path
from .views import ChatMessageListView

urlpatterns = [
    path('messages/<int:project_id>/', ChatMessageListView.as_view(), name='chat-messages'),
]
