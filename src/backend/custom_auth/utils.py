from rest_framework_simplejwt.token_blacklist.models import OutstandingToken, BlacklistedToken
import random

def logout_user_from_other_devices(user):
    # Get all outstanding tokens for the user
    outstanding_tokens = OutstandingToken.objects.filter(user=user)
    
    # Blacklist all outstanding tokens
    for token in outstanding_tokens:
        BlacklistedToken.objects.get_or_create(token=token)
        
def generate_otp():
    return ''.join([str(random.randint(0, 9)) for _ in range(6)])

def create_otp(user):
    ...