from rest_framework.pagination import PageNumberPagination

class ActivityLogPagination(PageNumberPagination):
    page_size = 20  # Number of messages to load per request
    page_size_query_param = 'page_size'
    max_page_size = 100
