from django.urls import re_path
from .consumers import ProjectUpdateConsumer, UserNotifyConsumer

websocket_urlpatterns = [
    re_path(r'ws/project/(?P<project_id>\d+)/$', ProjectUpdateConsumer.as_asgi()),
    re_path(r'ws/user/(?P<user_id>\d+)/$', UserNotifyConsumer.as_asgi()),
]
