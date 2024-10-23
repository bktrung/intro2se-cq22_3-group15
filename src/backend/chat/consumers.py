from channels.generic.websocket import AsyncWebsocketConsumer
from channels.db import database_sync_to_async
import json
from django.contrib.auth import get_user_model
from rest_framework_simplejwt.tokens import UntypedToken
from rest_framework_simplejwt.exceptions import InvalidToken, TokenError
from django.conf import settings
from .models import ChatMessage

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
            await self.close(code=4001)  # Invalid token
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

        user = self.scope['user']
        message = await self.create_chat_message(user, message_content)

        serialized_message = {
            "id": message.id,
            "author": user.username,
            "content": message.content,
            "timestamp": message.timestamp.isoformat()
        }

        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'chat_message',
                'data': serialized_message
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
