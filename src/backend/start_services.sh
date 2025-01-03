#!/bin/bash

# Start Django development server
echo "Starting Django development server..."
python manage.py runserver 0.0.0.0:8000 &

# Start Celery worker
echo "Starting Celery worker..."
celery -A project_management worker -l info &

# Start Celery beat
echo "Starting Celery beat..."
celery -A project_management beat -l info &

echo
echo "Services started:"
echo "- Django Development Server"
echo "- Celery Worker"
echo "- Celery Beat"

# Keep the script running
wait