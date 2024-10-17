from django.urls import re_path
from .consumers import TaskUpdateConsumer

websocket_urlpatterns = [
    re_path(r'ws/project/(?P<project_id>\d+)/$', TaskUpdateConsumer.as_asgi()),
]
