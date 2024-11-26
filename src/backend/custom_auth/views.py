from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework import generics, status
from rest_framework.permissions import AllowAny
from django.contrib.auth import get_user_model
from django.contrib.sessions.backends.db import SessionStore
from django.utils import timezone
from django.db.models import Q
from rest_framework_simplejwt.tokens import RefreshToken
from google.oauth2 import id_token
from google.auth.transport import requests as google_requests
from .serializers import UserSerializer
from .utils import logout_user_from_other_devices
from .models import OTPModel
from .utils import send_otp_to_email
import uuid

User = get_user_model()

class RegisterView(generics.CreateAPIView):
    serializer_class = UserSerializer
    permission_classes = [AllowAny]
    
    def perform_create(self, serializer):
        user = serializer.save()
        send_otp_to_email(user, 'Email verification')
        return user
    
class LoginView(APIView):
    permission_classes = [AllowAny]
    
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
    
    def post(self, request):
        id_token_str = request.data.get('id_token')
        if not id_token_str:
            return Response({'error': 'ID token is required'}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            # will set to os.environ.get('GOOGLE_CLIENT_ID') in future
            CLIENT_ID = '76922283431-11olbqs5uu5fmq37m33svsrlam19pnt5.apps.googleusercontent.com'
            id_info = id_token.verify_oauth2_token(id_token_str, google_requests.Request(), CLIENT_ID)
            
            user, created = User.objects.get_or_create(email=id_info['email'], defaults={'username': id_info.get('name'), 'is_verified': True})
            
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
    permission_classes = [AllowAny]

    def post(self, request, action):
        if action == 'send_otp':
            return self.send_otp(request)
        elif action == 'verify':
            return self.verify(request)
        else:
            return Response({'error': 'Invalid action'}, status=status.HTTP_400_BAD_REQUEST)
        
    def send_otp(self, request):
        email = request.data.get('email')
        user = User.objects.filter(email=email).first()
        
        if not user:
            return Response({'error': 'No user found with the provided email'}, status=status.HTTP_404_NOT_FOUND)
        if user.is_verified:
            return Response({'error': 'Email already verified'}, status=status.HTTP_400_BAD_REQUEST)

        send_otp_to_email(user, 'Email verification')
        return Response({'message': 'OTP sent to email'}, status=status.HTTP_200_OK)
    
    def verify(self, request):
        email = request.data.get('email')
        otp = request.data.get('otp')
        
        if not email or not otp:
            return Response({'error': 'Email and OTP are required'}, status=status.HTTP_400_BAD_REQUEST)
        
        user = User.objects.filter(email=email).first()
        if not user:
            return Response({'error': 'No user found with the provided email'}, status=status.HTTP_404_NOT_FOUND)
        if user.is_verified:
            return Response({'error': 'Email already verified'}, status=status.HTTP_400_BAD_REQUEST)
        
        current_otp = OTPModel.objects.filter(user=user).order_by('-created_at').first()
        
        if not current_otp:
            return Response({'error': 'No OTP found. Please request a new one.'}, status=status.HTTP_400_BAD_REQUEST)
        
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

    def post(self, request, action):
        if action == 'check_email':
            return self.check_email(request)
        elif action == 'send_otp':
            return self.send_otp(request)
        elif action == 'verify_otp':
            return self.verify_otp(request)
        elif action == 'change_password':
            return self.change_password(request)
        else:
            return Response({'error': 'Invalid action'}, status=status.HTTP_400_BAD_REQUEST)
    
    def check_email(self, request):
        email = request.data.get('email')
        if not email:
            return Response({'error': 'Email is required'}, status=status.HTTP_400_BAD_REQUEST)
        
        user = User.objects.filter(email=email).first()
        if not user:
            return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
        if not user.is_verified:
            return Response({'error': 'Email not verified'}, status=status.HTTP_400_BAD_REQUEST)
        
        return Response({'message': 'Email found'}, status=status.HTTP_200_OK)
        
    def send_otp(self, request):
        email = request.data.get('email')
        user = User.objects.filter(email=email).first()
        send_otp_to_email(user, 'Password reset')
        return Response({'message': 'OTP sent to email'}, status=status.HTTP_200_OK)
    
    def verify_otp(self, request):
        email = request.data.get('email')
        otp = request.data.get('otp')
        
        if not email or not otp:
            return Response({'error': 'Email and OTP are required'}, status=status.HTTP_400_BAD_REQUEST)

        user = User.objects.filter(email=email).first()
        if not user:
            return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)

        current_otp = OTPModel.objects.filter(user=user, otp=otp).order_by('-created_at').first()
        if not current_otp:
            return Response({'error': 'Invalid OTP'}, status=status.HTTP_400_BAD_REQUEST)
        
        if not current_otp.isValid():
            current_otp.delete()
            return Response({'error': 'OTP expired'}, status=status.HTTP_400_BAD_REQUEST)

        # Create a unique reset token
        reset_token = str(uuid.uuid4())
        
        # Create a new session for password reset
        session = SessionStore()
        session['reset_user_id'] = user.id
        session['reset_timestamp'] = str(timezone.now())
        session.create()
        
        request.session[f'pwd_reset_{reset_token}'] = session.session_key
        
        return Response({
            'message': 'OTP verified successfully',
            'reset_token': reset_token
        }, status=status.HTTP_200_OK)
    
    def change_password(self, request):
        new_password = request.data.get('new_password')
        confirm_password = request.data.get('confirm_password')
        reset_token = request.data.get('reset_token')
        
        if not new_password or not confirm_password or not reset_token:
            return Response({'error': 'New password and reset token are required'}, status=status.HTTP_400_BAD_REQUEST)

        session_key = request.session.get(f'pwd_reset_{reset_token}')
        if not session_key:
            return Response({'error': 'Invalid reset token'}, status=status.HTTP_400_BAD_REQUEST)
        
        session = SessionStore(session_key=session_key)
        
        user_id = session.get('reset_user_id')
        if not user_id:
            return Response({'error': 'Invalid reset request'}, status=status.HTTP_400_BAD_REQUEST)
            
        user = User.objects.filter(id=user_id).first()
        if not user:
            return Response({'error': 'User not found'}, status=status.HTTP_400_BAD_REQUEST)
        
        reset_time = timezone.datetime.fromisoformat(session.get('reset_timestamp'))
        if timezone.now() > reset_time + timezone.timedelta(minutes=5):
            session.delete()
            del request.session[f'pwd_reset_{reset_token}']
            return Response({'error': 'Reset token expired'}, status=status.HTTP_400_BAD_REQUEST)

        if new_password != confirm_password:
            return Response({'error': 'Passwords do not match'}, status=status.HTTP_400_BAD_REQUEST)

        user.set_password(new_password)
        user.save()

        session.delete()
        del request.session[f'pwd_reset_{reset_token}']
        OTPModel.objects.filter(user=user).delete()

        return Response({'message': 'Password reset successfully'}, status=status.HTTP_200_OK)