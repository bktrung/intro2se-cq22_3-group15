from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import generics, status
from rest_framework.permissions import IsAuthenticated
from .serializers import RoleSerializer
from .models import *
# Create your views here.
class RoleListView(generics.ListAPIView):
    def post(self, request, action):
        if action == 'create':
            serializer = RoleSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class DeleteRoleView(APIView):
    ...
        