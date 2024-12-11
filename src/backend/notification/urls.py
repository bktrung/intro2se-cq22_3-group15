from django.urls import path
from .views import DeviceTokenCreateView, DeviceTokenManageView

urlpatterns = [
    path('device-token/', DeviceTokenCreateView.as_view(), name='device-token-create'),
    path('device-token/user/', DeviceTokenManageView.as_view(), name='device-token-manage'),
]
