import json
from channels.generic.websocket import AsyncWebsocketConsumer

class ProjectUpdateConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.project_id = self.scope['url_route']['kwargs']['project_id']
        self.group_name = f"project_{self.project_id}"

        # Join the project group
        await self.channel_layer.group_add(self.group_name, self.channel_name)
        await self.accept()

    async def disconnect(self, close_code):
        # Leave the project group
        await self.channel_layer.group_discard(self.group_name, self.channel_name)

    async def object_update(self, event):
        # Send message to WebSocket
        await self.send(text_data=json.dumps(event['data']))
