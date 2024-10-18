from rest_framework_simplejwt.token_blacklist.models import OutstandingToken, BlacklistedToken
from django.core.mail import send_mail
import random
from .models import OTPModel, CustomUser

def logout_user_from_other_devices(user):
    # Get all outstanding tokens for the user
    outstanding_tokens = OutstandingToken.objects.filter(user=user)
    
    # Blacklist all outstanding tokens
    for token in outstanding_tokens:
        BlacklistedToken.objects.get_or_create(token=token)
        
def generate_otp():
    return ''.join([str(random.randint(0, 9)) for _ in range(6)])

def send_email(email, otp, phrase):
    subject = phrase
    message = f'Your OTP for {phrase} is {otp}, valid for 5 minutes.'
    sender = "vvtung2004@gmail.com"
    receiver = [email]
    send_mail(subject, message, sender, receiver)
    
def send_otp_to_email(user, phrase):
    otp = generate_otp()
    OTPModel.objects.create(user=user, otp=otp)
    send_email(user.email, otp, phrase)