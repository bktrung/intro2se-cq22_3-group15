import os
import requests
from django.conf import settings
from celery import shared_task
from google.oauth2 import service_account
from google.auth.transport.requests import Request
from django.core.cache import cache
import logging
from decouple import config

logger = logging.getLogger(__name__)

SERVICE_ACCOUNT_FILE = os.path.join(settings.BASE_DIR, config('FIREBASE_ADMIN_SDK'))

@shared_task
def get_firebase_access_token():
    try:
        access_token = cache.get('firebase_access_token')
        if access_token:
            return access_token
        
        credentials = service_account.Credentials.from_service_account_file(
            SERVICE_ACCOUNT_FILE,
            scopes=["https://www.googleapis.com/auth/cloud-platform"]
        )
        credentials.refresh(Request())
        access_token = credentials.token
        
        cache.set('firebase_access_token', access_token, timeout=55 * 60)
        return access_token
    except Exception as e:
        logger.error(f"Failed to obtain Firebase access token: {e}")
        return None

@shared_task
def send_fcm_notification(access_token, device_token, title, body):
    """
    Celery task to send FCM notification asynchronously
    """
    fcm_url = config('FCM_URL')
    
    headers = {
        'Authorization': f'Bearer {access_token}',
        'Content-Type': 'application/json',
    }
    
    payload = {
        "message": {
            "token": device_token,
            "notification": {
                "title": title,
                "body": body
            }
        }
    }
    
    try:
        response = requests.post(fcm_url, headers=headers, json=payload)
        return {
            'success': response.status_code == 200,
            'status_code': response.status_code
        }
    except Exception as e:
        return {
            'success': False,
            'error': str(e)
        }