from django.contrib import admin
from .models import ActivityLog

class ActivityLogAdmin(admin.ModelAdmin):
    list_display = ('timestamp', 'project', 'user', 'action', 'get_description')
    list_filter = ('action', 'timestamp', 'project')
    search_fields = ('project__name', 'user', 'description')
    readonly_fields = ('timestamp', 'changes')
    
    def get_description(self, obj):
        return obj.description[:100] + '...' if len(obj.description) > 100 else obj.description
    get_description.short_description = 'Description'

admin.site.register(ActivityLog, ActivityLogAdmin)