from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework import generics, status
from rest_framework.permissions import AllowAny
from django.contrib.auth import get_user_model
from django.db.models import Q
from rest_framework_simplejwt.tokens import RefreshToken
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from google.oauth2 import id_token
from google.auth.transport import requests as google_requests
from .serializers import UserSerializer
from .utils import logout_user_from_other_devices
from .models import OTPModel
from .utils import *

User = get_user_model()

class RegisterView(generics.CreateAPIView):
    serializer_class = UserSerializer
    permission_classes = [AllowAny]
    
class LoginView(APIView):
    permission_classes = [AllowAny]
    
    @swagger_auto_schema(
        operation_description="User login using username or email and password",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'username_or_email': openapi.Schema(type=openapi.TYPE_STRING, description='Username or email'),
                'password': openapi.Schema(type=openapi.TYPE_STRING, description='Password'),
            },
            required=['username_or_email', 'password']
        ),
        responses={200: 'Tokens returned on success', 400: 'Bad request', 401: 'Invalid credentials'}
    )
    
    def post(self, request):
        username_or_email = request.data.get('username_or_email')
        password = request.data.get('password')

        if not username_or_email or not password:
            return Response({'error': 'Please provide both username/email and password'}, status=status.HTTP_400_BAD_REQUEST)

        user = User.objects.filter(
            Q(username=username_or_email) | Q(email=username_or_email)
        ).first()

        if user and user.check_password(password):
            # logout_user_from_other_devices(user)
            
            refresh = RefreshToken.for_user(user)
            
            return Response({
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }, status=status.HTTP_200_OK)
        
        return Response({'error': 'Invalid credentials'}, status=status.HTTP_401_UNAUTHORIZED)
    
class LogoutView(APIView):
    
    @swagger_auto_schema(
        operation_description="Log out a user by blacklisting the refresh token",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'refresh': openapi.Schema(type=openapi.TYPE_STRING, description='Refresh token'),
            },
            required=['refresh'],
            example={
                'refresh': 'your-refresh-token-here'
            }
        ),
        responses={
            200: openapi.Response(description="User logged out successfully"),
            400: openapi.Response(description="Invalid or missing refresh token"),
        }
    )
    
    def post(self, request):
        refresh_token = request.data.get('refresh')
        
        if not refresh_token:
            return Response({'error': 'Refresh token is required'}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            token = RefreshToken(refresh_token)
            token.blacklist()
        except Exception as e:
            return Response({'error': 'Invalid refresh token'}, status=status.HTTP_400_BAD_REQUEST)
        
        return Response({'message': 'User logged out successfully'}, status=status.HTTP_200_OK)
    
class GoogleLoginView(APIView):
    permission_classes = [AllowAny]
    
    @swagger_auto_schema(
        operation_description="Login or register using Google ID token",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'id_token': openapi.Schema(type=openapi.TYPE_STRING, description='Google ID token'),
            },
            required=['id_token'],
            example={
                'id_token': 'your-google-id-token'
            }
        ),
        responses={
            200: openapi.Response(description="JWT access and refresh tokens"),
            400: openapi.Response(description="Bad request or invalid token")
        }
    )
    
    def post(self, request):
        id_token_str = request.data.get('id_token')
        if not id_token_str:
            return Response({'error': 'ID token is required'}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            # will set to os.environ.get('GOOGLE_CLIENT_ID') in future
            CLIENT_ID = '76922283431-11olbqs5uu5fmq37m33svsrlam19pnt5.apps.googleusercontent.com'
            id_info = id_token.verify_oauth2_token(id_token_str, google_requests.Request(), CLIENT_ID)
            
            user, created = User.objects.get_or_create(email=id_info['email'], defaults={'username': id_info.get('name')})
            
            if created:
                user.set_unusable_password()
                user.save()
            
            # logout_user_from_other_devices(user)
            
            refresh = RefreshToken.for_user(user)
            
            return Response({
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }, status=status.HTTP_200_OK)
            
        except ValueError:
            return Response({'error': 'Invalid ID token'}, status=status.HTTP_400_BAD_REQUEST)
        
        except KeyError:
            return Response({'error': 'Invalid user info received from ID token'}, status=status.HTTP_400_BAD_REQUEST)

class Home(APIView):
    def get(self, request):
        content = {'message': 'Hello, World!'}
        return Response(content)

@api_view(['PUT'])
def verify_email(request, user_id):
    user = User.objects.get(id=user_id)
    # otp_object is the current valid OTP for the user
    otp_object = OTPModel.objects.filter(user=user).order_by('-created_at').first()
    
    if request.method == 'PUT':
        # otp is the OTP entered by the user
        otp = request.data.get('otp')
        if not otp:
            return Response({'error': 'OTP is required'}, status=status.HTTP_400_BAD_REQUEST)
        
        # Assume that user registered using email has its is_active set to True
        if user.is_active == True:
            return Response({'error': 'Email already verified'}, status=status.HTTP_400_BAD_REQUEST)
        
        if otp_object.otp == otp:
            if otp_object.isValid() == True:
                user.is_active = True
                user.save()
                return Response({'message': 'Email verified successfully'}, status=status.HTTP_200_OK)
            else:
                return Response({'error': 'OTP expired'}, status=status.HTTP_400_BAD_REQUEST)
            
        else:
            return Response({'error': 'Invalid OTP'}, status=status.HTTP_400_BAD_REQUEST)
    
       