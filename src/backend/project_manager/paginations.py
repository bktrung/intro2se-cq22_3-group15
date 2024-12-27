from rest_framework.pagination import CursorPagination

class ChangeRequestPagination(CursorPagination):
    page_size = 10  
    ordering = '-created_at' 