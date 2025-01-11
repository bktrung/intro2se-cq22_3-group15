from rest_framework_simplejwt.token_blacklist.models import OutstandingToken, BlacklistedToken
from django.core.mail import send_mail
import random
from .models import OTPModel

def logout_user_from_other_devices(user):
    # Get all outstanding tokens for the user
    outstanding_tokens = OutstandingToken.objects.filter(user=user)
    
    # Blacklist all outstanding tokens
    for token in outstanding_tokens:
        BlacklistedToken.objects.get_or_create(token=token)

        
def generate_otp():
    return ''.join([str(random.randint(0, 9)) for _ in range(6)])


def get_email_template(otp, phrase):
    return f"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>{phrase}</title>
        <style>
            .email-container {{
                max-width: 600px;
                margin: 0 auto;
                font-family: Arial, sans-serif;
                padding: 20px;
                background-color: #f9f9f9;
            }}
            .header {{
                background-color: #4A90E2;
                padding: 20px;
                text-align: center;
                border-radius: 8px 8px 0 0;
            }}
            .header h1 {{
                color: white;
                margin: 0;
                font-size: 24px;
            }}
            .content {{
                background-color: white;
                padding: 30px;
                border-radius: 0 0 8px 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }}
            .otp-code {{
                text-align: center;
                font-size: 32px;
                letter-spacing: 5px;
                color: #333;
                margin: 20px 0;
                padding: 15px;
                background-color: #f5f5f5;
                border-radius: 4px;
            }}
            .expiry-text {{
                color: #666;
                text-align: center;
                font-size: 14px;
                margin-top: 20px;
            }}
            .footer {{
                text-align: center;
                margin-top: 20px;
                color: #999;
                font-size: 12px;
            }}
        </style>
    </head>
    <body>
        <div class="email-container">
            <div class="header">
                <h1>{phrase}</h1>
            </div>
            <div class="content">
                <p>Hello!</p>
                <p>Your {phrase.lower()} code is:</p>
                <div class="otp-code">
                    {otp}
                </div>
                <p class="expiry-text">This code will expire in 5 minutes.</p>
                <p>If you didn't request this code, please ignore this email.</p>
            </div>
            <div class="footer">
                <p>This is an automated message, please do not reply.</p>
                <p>&copy; Youmanage</p>
            </div>
        </div>
    </body>
    </html>
    """

def send_email(email, otp, phrase):
    from django.core.mail import EmailMultiAlternatives
    from django.utils.html import strip_tags
    
    subject = phrase
    html_content = get_email_template(otp, phrase)
    text_content = strip_tags(html_content)  # Fallback plain text version
    
    email_message = EmailMultiAlternatives(
        subject,
        text_content,
        "OnlyApp <onlyapp@gmail.com>",
        [email]
    )
    
    email_message.attach_alternative(html_content, "text/html")
    email_message.send()

    
def send_otp_to_email(user, phrase):
    otp = generate_otp()
    OTPModel.objects.create(user=user, otp=otp)
    send_email(user.email, otp, phrase)