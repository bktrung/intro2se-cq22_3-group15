def track_field_changes(instance, prev_state, fields):
    """
    Track changes between previous and current state of model instance.
    
    Args:
        instance: Current model instance
        prev_state: Previous state of model instance
        fields: List of field names to compare
        
    Returns:
        dict: Changes with old and new values
    """
    changes = {}
    
    if prev_state:
        for field in fields:
            old_value = getattr(prev_state, field)
            new_value = getattr(instance, field)
            if old_value != new_value:
                changes[field] = {
                    'old': str(old_value or ''),
                    'new': str(new_value or '')
                }
                
    return changes