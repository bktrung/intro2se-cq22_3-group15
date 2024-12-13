import os
import requests
from django.conf import settings
from celery import shared_task
from google.oauth2 import service_account
from google.auth.transport.requests import Request
from django.core.cache import cache


SERVICE_ACCOUNT_FILE = os.path.join(settings.BASE_DIR, "testpushnotification-286ec-firebase-adminsdk-1eda2-acb7f28157.json")

@shared_task
def get_firebase_access_token():
    access_token = cache.get('firebase_access_token')
    if access_token:
        return access_token
    
    credentials = service_account.Credentials.from_service_account_file(
        SERVICE_ACCOUNT_FILE,
        scopes=["https://www.googleapis.com/auth/cloud-platform"]
    )
    credentials.refresh(Request())
    
    cache.set('firebase_access_token', access_token, timeout=55 * 60)
    return access_token

@shared_task
def send_fcm_notification(access_token, device_token, title, body):
    """
    Celery task to send FCM notification asynchronously
    """
    fcm_url = "https://fcm.googleapis.com/v1/projects/testpushnotification-286ec/messages:send"
    
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
        
@shared_task
def send_batch_fcm_notification(access_token, device_tokens, title, body):
    """
    Send FCM notification to multiple devices in one request
    """
    fcm_url = "https://fcm.googleapis.com/v1/projects/testpushnotification-286ec/messages:send"
    
    headers = {
        'Authorization': f'Bearer {access_token}',
        'Content-Type': 'application/json',
    }
    
    # FCM supports up to 500 tokens per request
    for i in range(0, len(device_tokens), 500):
        batch = device_tokens[i:i+500]
        
        payload = {
            "message": {
                "notification": {
                    "title": title,
                    "body": body
                },
                "tokens": batch  # Send to multiple devices
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
            
