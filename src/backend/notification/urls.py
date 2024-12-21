from django.urls import path
from .views import *

urlpatterns = [
    path('device-token/', DeviceTokenCreateView.as_view(), name='device-token-create'),
    path('device-token/user/<str:action>/', DeviceTokenManageView.as_view(), name='device-token-manage'),
    path('notifications/', NotificationLogListView.as_view(), name='notification-log-list'),
    path('notifications/unread/count/', CountUnreadNotificationsView.as_view(), name='unread-notification-count'),
    path('notifications/<int:pk>/', NotificationLogDeleteView.as_view(), name='notification-log-delete'),
    path('notifications/<int:pk>/read/', NotificationLogMarkAsReadOneView.as_view(), name='notification-log-update'),
    path('notifications/read-all/', NotificationLogMarkAsReadView.as_view(), name='notification-log-mark-as-read'),
]
