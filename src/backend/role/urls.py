from django.urls import path
from .views import *

urlpatterns = [
    path('role_list/<str:action>', CreateRoleView.as_view(), name='create_role'),
    path('user_role/<str:action>', AssignRoleView.as_view(), name='assign_role'),
]
