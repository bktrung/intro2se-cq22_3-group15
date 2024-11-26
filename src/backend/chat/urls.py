from django.urls import path
from .views import ChatMessageListView
from .views import ChatMessageListCreateView, ChatMessageDetailView

urlpatterns = [
    path('messages/<int:project_id>/', ChatMessageListView.as_view(), name='chat-messages'),

    # List/Create Chat Messages
    path('messages/', ChatMessageListCreateView.as_view(), name='chat-list-create'),

    # Retrieve/Delete a specific Chat Message
    path('messages/<int:pk>/', ChatMessageDetailView.as_view(), name='chat-detail'),

]
