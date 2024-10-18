from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework import generics, status
from rest_framework.permissions import AllowAny, IsAuthenticated
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
from .utils import send_otp_to_email

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

class EmailAuthenticationView(APIView):
    permission_classes = [IsAuthenticated]

    # User clicks on a button to request an OTP
    def post(self, request):
        user = request.user
        if user.is_verified:
            return Response({'error': 'Email already verified'}, status=status.HTTP_400_BAD_REQUEST)
        
        send_otp_to_email(user, 'Email verification')
        return Response({'message': 'OTP sent to email'}, status=status.HTTP_200_OK)
    
    # User enters OTP to verify email
    def patch(self, request):
        user = request.user
        otp = request.data.get('otp')

        if not otp:
            return Response({'error': 'OTP is required'}, status=status.HTTP_400_BAD_REQUEST)

        if user.is_verified:
            return Response({'error': 'Email already verified'}, status=status.HTTP_400_BAD_REQUEST)

        current_otp = OTPModel.objects.filter(user=user).order_by('-created_at').first()

        if not current_otp:
            return Response({'error': 'No OTP found. Please request a new one.'}, status=status.HTTP_404_NOT_FOUND)

        if current_otp.otp == otp:
            if current_otp.isValid():
                user.is_verified = True
                user.save()
                return Response({'message': 'Email verified successfully'}, status=status.HTTP_200_OK)
            else:
                return Response({'error': 'OTP expired'}, status=status.HTTP_400_BAD_REQUEST)
        else:
            return Response({'error': 'Invalid OTP'}, status=status.HTTP_400_BAD_REQUEST)
        
class ForgotPasswordView(APIView):
    permission_classes = [AllowAny]

    # User clicks on a button to request an OTP for password reset
    def post(self, request):
        email = request.data.get('email')
        if not email:
            return Response({'error': 'Email is required'}, status=status.HTTP_400_BAD_REQUEST)
        
        user = User.objects.filter(email=email).first()
        if not user:
            return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
        
        send_otp_to_email(user, 'Password reset')
        return Response({'message': 'OTP sent to email'}, status=status.HTTP_200_OK)
    
    # User enters OTP 
    def put(self, request):
        email = request.data.get('email')
        otp = request.data.get('otp')
        
        if not email or not otp:
            return Response({'error': 'Email and OTP are required'}, status=status.HTTP_400_BAD_REQUEST)

        user = User.objects.filter(email=email).first()
        if not user:
            return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)

        current_otp = OTPModel.objects.filter(user=user).order_by('-created_at').first()
        if not current_otp:
            return Response({'error': 'No OTP found. Please request a new one.'}, status=status.HTTP_404_NOT_FOUND)

        if current_otp.otp != otp:
            return Response({'error': 'Invalid OTP'}, status=status.HTTP_400_BAD_REQUEST)

        if not current_otp.isValid():
            return Response({'error': 'OTP expired'}, status=status.HTTP_400_BAD_REQUEST)

        return Response({'message': 'OTP is valid, proceed to reset password'}, status=status.HTTP_200_OK)
    
    # User enters new password
    def patch(self, request):
        email = request.data.get('email')
        new_password = request.data.get('new_password')

        if not email or not new_password:
            return Response({'error': 'Email and new password are required'}, status=status.HTTP_400_BAD_REQUEST)

        user = User.objects.filter(email=email).first()
        if not user:
            return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)

        user.set_password(new_password)
        user.save()

        return Response({'message': 'Password reset successfully'}, status=status.HTTP_200_OK)
