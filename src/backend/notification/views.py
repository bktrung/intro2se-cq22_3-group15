from django.shortcuts import get_object_or_404
from rest_framework import generics, status
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.exceptions import ValidationError, PermissionDenied
from .models import DeviceToken, NotificationLog
from .serializers import DeviceTokenSerializer, NotificationLogSerializer
from .paginations import NotificationLogPagination

class DeviceTokenCreateView(generics.CreateAPIView):
    permission_classes = [AllowAny]
    queryset = DeviceToken.objects.all()
    serializer_class = DeviceTokenSerializer
    
class DeviceTokenManageView(generics.GenericAPIView):
    queryset = DeviceToken.objects.all()
    
    VALID_ACTIONS = ['assign', 'unassign']

    def get_device_token(self, token):
        """Get device token object or raise 404"""
        return get_object_or_404(DeviceToken, token=token)

    def post(self, request, action, *args, **kwargs):
        """Handle token assignment/unassignment based on URL action"""
        if action not in self.VALID_ACTIONS:
            raise ValidationError({"action": f"Invalid action. Must be one of: {self.VALID_ACTIONS}"})

        token = request.data.get('token')
        
        device_token = self.get_device_token(token)

        if action == 'assign':
            device_token.user = request.user
            message = "Device token assigned successfully"
        else:  # unassign
            if device_token.user != request.user:
                raise PermissionDenied("You can only unassign your own device tokens")
            device_token.user = None
            message = "Device token unassigned successfully"

        device_token.save()
        
        return Response({
            "message": message
        }, status=status.HTTP_200_OK)
        
class NotificationLogListView(generics.ListAPIView):
    serializer_class = NotificationLogSerializer
    pagination_class = NotificationLogPagination

    def get_queryset(self):
        return NotificationLog.objects.filter(user=self.request.user).select_related('user')
    
class NotificationLogDeleteView(generics.DestroyAPIView):
    queryset = NotificationLog.objects.all()
    serializer_class = NotificationLogSerializer
    
    def perform_destroy(self, instance):
        if instance.user != self.request.user:
            raise ValidationError("You are not the father.")
        return super().perform_destroy(instance)
    
class NotificationLogMarkAsReadOneView(generics.GenericAPIView):
    queryset = NotificationLog.objects.all()
    
    def post(self, request, pk, *args, **kwargs):
        try:
            notification_log = NotificationLog.objects.get(id=pk)
            if notification_log.user != request.user:
                raise ValidationError("You are not the father.")
            notification_log.is_read = True
            notification_log.save()
            return Response({"message": "Notification marked as read."}, status=status.HTTP_200_OK)
        except NotificationLog.DoesNotExist:
            raise ValidationError("Notification not found.")
    
class NotificationLogMarkAsReadView(generics.GenericAPIView):
    def post(self, request, *args, **kwargs):
        NotificationLog.objects.filter(user=request.user, is_read=False).update(is_read=True)
        return Response({"message": "All notifications marked as read."}, status=status.HTTP_200_OK)
    
class CountUnreadNotificationsView(generics.GenericAPIView):
    def get(self, request, *args, **kwargs):
        unread_count = NotificationLog.objects.filter(user=request.user, is_read=False).count()
        return Response({"unread_count": unread_count}, status=status.HTTP_200_OK)