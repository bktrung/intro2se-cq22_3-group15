from channels.generic.websocket import AsyncWebsocketConsumer
from channels.db import database_sync_to_async
import json
from django.contrib.auth import get_user_model
from rest_framework_simplejwt.tokens import UntypedToken
from rest_framework_simplejwt.exceptions import InvalidToken, TokenError
from .models import ChatMessage
import base64
import uuid
from django.core.files.base import ContentFile

CustomUser = get_user_model()

class ChatConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        # Extract token from query parameters
        token = self.scope['query_string'].decode().split('=')[1]

        # Verify the token and get the user
        try:
            decoded_data = UntypedToken(token)
            user = await self.get_user(decoded_data['user_id'])
            self.scope['user'] = user  # Set user in scope
        except (InvalidToken, TokenError) as e:
            await self.close(code=401)  # Invalid token
            return

        self.project_id = self.scope['url_route']['kwargs']['project_id']
        self.group_name = f"chat_{self.project_id}"

        await self.channel_layer.group_add(self.group_name, self.channel_name)
        await self.accept()

    async def disconnect(self, close_code):
        await self.channel_layer.group_discard(self.group_name, self.channel_name)

    async def receive(self, text_data):
        data = json.loads(text_data)
        message_content = data.get('message')
        image_data = data.get('image')

        user = self.scope['user']

        # Text only
        if message_content and not image_data:
            message = await self.create_chat_message(user, message_content)
            serialized_data = {
                "id": message.id,
                "author": user.username,
                "content": message.content,
                "image_url": None,
                "timestamp": message.timestamp.isoformat()
            }
        
        # Image only
        elif image_data and not message_content:
            format, imgstr = image_data.split(';base64,')
            ext = format.split('/')[-1]
            image_file = ContentFile(base64.b64decode(imgstr), name=f'{uuid.uuid4()}.{ext}')
            
            message = await self.create_chat_message_with_image(
                user, 
                '',
                image_file
            )
            serialized_data = {
                "id": message.id,
                "author": user.username,
                "content": '',
                "image_url": message.get_image_url(),
                "timestamp": message.timestamp.isoformat()
            }
        
        # Both text and image
        elif message_content and image_data:
            format, imgstr = image_data.split(';base64,')
            ext = format.split('/')[-1]
            image_file = ContentFile(base64.b64decode(imgstr), name=f'{uuid.uuid4()}.{ext}')
            
            message = await self.create_chat_message_with_image(
                user, 
                message_content, 
                image_file
            )
            serialized_data = {
                "id": message.id,
                "author": user.username,
                "content": message.content,
                "image_url": message.get_image_url(),
                "timestamp": message.timestamp.isoformat()
            }

        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'chat_message',
                'data': serialized_data
            }
        )

    async def chat_message(self, event):
        data = event['data']
        await self.send(text_data=json.dumps(data))

    @database_sync_to_async
    def get_user(self, user_id):
        try:
            return CustomUser.objects.get(id=user_id)
        except CustomUser.DoesNotExist:
            return None

    @database_sync_to_async
    def create_chat_message(self, user, content):
        return ChatMessage.objects.create(
            project_id=self.project_id, author=user, content=content
        )
        
    @database_sync_to_async
    def create_chat_message_with_image(self, user, content, image_file):
        return ChatMessage.objects.create(
            project_id=self.project_id, 
            author=user, 
            content=content,
            image=image_file
        )
