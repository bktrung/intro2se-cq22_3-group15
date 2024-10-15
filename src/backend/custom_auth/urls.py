from rest_framework_simplejwt.views import TokenRefreshView
from django.urls import path
from .views import Home, RegisterView, LoginView, LogoutView, GoogleLoginView


urlpatterns = [
    path('signup/', RegisterView.as_view(), name='register'),
    path('login/', LoginView.as_view(), name='login'),
    path('google-login/', GoogleLoginView.as_view(), name='google-login'),
    path('logout/', LogoutView.as_view(), name='logout'),
    path('token/refresh/', TokenRefreshView.as_view(), name='token-refresh'),
    path('test_token/', Home.as_view(), name='test_token'),
]