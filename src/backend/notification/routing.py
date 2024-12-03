from django.urls import re_path
from .consumers import ProjectUpdateConsumer

websocket_urlpatterns = [
    re_path(r'ws/project/(?P<project_id>\d+)/$', ProjectUpdateConsumer.as_asgi()),
]
