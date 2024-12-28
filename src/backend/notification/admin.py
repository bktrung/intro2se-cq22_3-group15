from django.contrib import admin
from .models import DeviceToken, NotificationLog

class DeviceTokenAdmin(admin.ModelAdmin):
    list_display = ('token', 'user', 'created_at', 'updated_at')
    list_filter = ('created_at', 'updated_at')
    search_fields = ('token', 'user__username')
    readonly_fields = ('token', 'created_at', 'updated_at')

class NotificationLogAdmin(admin.ModelAdmin):
    list_display = ('title', 'body', 'user', 'is_read', 'created_at')
    list_filter = ('is_read', 'created_at')
    search_fields = ('title', 'body', 'user__username')
    readonly_fields = ('title', 'body', 'user', 'created_at')

admin.site.register(DeviceToken, DeviceTokenAdmin)
admin.site.register(NotificationLog, NotificationLogAdmin)