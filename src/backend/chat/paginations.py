from rest_framework.pagination import CursorPagination

class ChatMessagePagination(CursorPagination):
    page_size = 20  # Number of messages to load per request
    ordering = '-timestamp'  # Load messages by ascending timestamp
