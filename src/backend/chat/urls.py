from django.urls import path
from .views import ChatMessageListView
from django.conf import settings
from django.conf.urls.static import static

urlpatterns = [
    path('projects/<int:project_id>/messages/', ChatMessageListView.as_view(), name='chat-messages'),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
