from rest_framework import generics, status
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.exceptions import ValidationError
from models import DeviceToken
from serializers import DeviceTokenSerializer

class DeviceTokenCreateView(generics.CreateAPIView):
    permission_classes = [AllowAny]
    queryset = DeviceToken.objects.all()
    serializer_class = DeviceTokenSerializer
    
class DeviceTokenManageView(generics.GenericAPIView):
    queryset = DeviceToken.objects.all()

    def _get_device_token(self, token):
        if not token:
            return Response()
        
        try:
            return DeviceToken.objects.get(token=token)
        except DeviceToken.DoesNotExist:
            raise ValidationError("Device token not found.")

    def post(self, request, *args, **kwargs):
        token = request.data.get('token')
        device_token = self._get_device_token(token)
        
        device_token.user = request.user
        device_token.save()
        
        return Response(
            {"message": "Device token assigned successfully."}, 
            status=status.HTTP_200_OK
        )

    def delete(self, request, *args, **kwargs):
        token = request.data.get('token')
        device_token = self._get_device_token(token)
        
        if device_token.user != request.user:
            raise ValidationError("You are not the father.")
        
        device_token.user = None
        device_token.save()
        
        return Response(
            {"message": "Device token unassigned successfully."},
            status=status.HTTP_200_OK
        )