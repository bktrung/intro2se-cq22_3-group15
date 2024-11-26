from django.db.models.signals import pre_save, post_save, m2m_changed, pre_delete, post_delete
from django.dispatch import receiver
from django.contrib.auth import get_user_model
from project_manager.models import Project, Task
from .serializers import ActivityLogSerializer

User = get_user_model()

@receiver(pre_save, sender=Project)
def store_previous_state(sender, instance, **kwargs):
    """Store previous state before saving"""
    if instance.pk:
        try:
            instance._previous_state = Project.objects.get(pk=instance.pk)
        except Project.DoesNotExist:
            instance._previous_state = None

@receiver(post_save, sender=Project)
def log_project_changes(sender, instance, created, **kwargs):
    """Log project creation and updates"""
    changes = {}
    
    if created:
        action = 'CREATE'
        description = f'Project "{instance.name}" was created'
    else:
        action = 'UPDATE'
        prev_state = getattr(instance, '_previous_state', None)
        if prev_state:
            for field in ['name', 'description', 'duedate']:
                old_value = getattr(prev_state, field)
                new_value = getattr(instance, field)
                if old_value != new_value:
                    changes[field] = {
                        'old': str(old_value or ''),
                        'new': str(new_value or '')
                    }
        description = f'Project "{instance.name}" was updated'

    if created or changes:
        serializer = ActivityLogSerializer(data={
            'project': instance.id,
            'user': instance.host.username,
            'action': action,
            'changes': changes,
            'description': description
        })
        if serializer.is_valid():
            serializer.save()
        else:
            print(serializer.errors)

@receiver(m2m_changed, sender=Project.members.through)
def log_member_changes(sender, instance, action, pk_set, **kwargs):
    """Log member additions and removals"""
    if action in ["post_add", "post_remove"]:
        users = User.objects.filter(pk__in=pk_set)
        action_type = 'MEMBER_ADD' if action == "post_add" else 'MEMBER_REMOVE'
        description = (
            f'Members {", ".join(users.values_list("username", flat=True))} were '
            f'{"added to" if action == "post_add" else "removed from"} project'
        )
        
        serializer = ActivityLogSerializer(data={
            'project': instance.id,
            'user': instance.host.username,
            'action': action_type,
            'changes': {'users': list(users.values_list('username', flat=True))},
            'description': description
        })
        if serializer.is_valid():
            serializer.save()
        else:
            print(serializer.errors)

# @receiver(m2m_changed, sender=Role.users.through)
# def log_role_user_changes(sender, instance, action, pk_set, **kwargs):
#     """Log role assignments and removals"""
#     if action in ["post_add", "post_remove"]:
#         users = User.objects.filter(pk__in=pk_set)
#         action_type = 'ROLE_ASSIGN' if action == "post_add" else 'ROLE_UNASSIGN'
#         description = (
#             f'Users {", ".join(users.values_list("username", flat=True))} were '
#             f'{"assigned to" if action == "post_add" else "removed from"} '
#             f'role "{instance.role_name}" in project "{instance.project.name}"'
#         )
        
#         serializer = ActivityLogSerializer(data={
#             'project': instance.project.id,
#             'user': instance.project.host.id,
#             'action': action_type,
#             'changes': {
#                 'role': instance.role_name,
#                 'users': list(users.values_list('username', flat=True))
#             },
#             'description': description
#         })
#         if serializer.is_valid():
#             serializer.save()
#         else:
#             print(serializer.errors)

@receiver(pre_save, sender=Task)
def store_previous_task_state(sender, instance, **kwargs):
    """
    Stores the previous state of a Task instance before saving.
    """
    if instance.pk:
        try:
            instance._previous_state = Task.objects.get(pk=instance.pk)
        except Task.DoesNotExist:
            instance._previous_state = None

@receiver(post_save, sender=Task)
def log_task_save(sender, instance, created, **kwargs):
    """
    Logs creation and updates of Task instances.
    """
    user = getattr(instance, '_current_user', None)
    username = user.username if user and user.is_authenticated else 'Anonymous'
    
    changes = {}

    if created:
        action_type = 'TASK_ADD'
        description = f'Task "{instance.title}" was created.'
    else:
        action_type = 'TASK_UPDATE'
        prev_state = getattr(instance, '_previous_state', None)
        if prev_state:
            for field in ['title', 'description', 'start_date', 'end_date', 'status', 'priority', 'assignee']:
                old_value = getattr(prev_state, field)
                new_value = getattr(instance, field)
                if old_value != new_value:
                    changes[field] = {
                        'old': str(old_value or ''),
                        'new': str(new_value or '')
                    }
        description = f'Task "{instance.title}" was updated.'
        
    if created or changes:
        serializer = ActivityLogSerializer(data={
            'project': instance.project.id,
            'user': username,
            'action': action_type,
            'changes': changes,
            'description': description
        })
        if serializer.is_valid():
            serializer.save()
        else:
            print(serializer.errors)
            
@receiver(pre_delete, sender=Project)
def store_previous_project_state(sender, instance, **kwargs):
    """
    Stores the previous state of a Project instance before deletion.
    """
    try:
        instance._previous_state = Project.objects.get(pk=instance.pk)
    except Project.DoesNotExist:
        instance._previous_state = None

import json

@receiver(post_delete, sender=Task)
def log_task_delete(sender, instance, **kwargs):
    """
    Logs deletion of Task instances.
    """
    user = getattr(instance, '_current_user', None)
    username = user.username if user and user.is_authenticated else 'Anonymous'

    action_type = 'TASK_REMOVE'
    description = f'Task "{instance.title}" was deleted.'
    changes = {
        'title': instance.title,
        'description': instance.description,
        'status': instance.status,
        'priority': instance.priority,
        'assignee': instance.assignee.username if instance.assignee else None,
        'start_date': instance.start_date.isoformat() if instance.start_date else None,
        'end_date': instance.end_date.isoformat() if instance.end_date else None
    }
    
    try:
        json.dumps(changes)
    except (TypeError, ValueError) as e:
        print(f"Non-serializable data in changes: {changes} | Error: {e}")
        changes = {}  # Fallback to empty changes or handle accordingly

    serializer = ActivityLogSerializer(data={
        'project': instance.project.id,
        'user': username,
        'action': action_type,
        'changes': changes,
        'description': description
    })
    if serializer.is_valid():
        serializer.save()
    else:
        print(serializer.errors)