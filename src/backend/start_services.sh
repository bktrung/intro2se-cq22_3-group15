#!/bin/bash

# Start Django development server
echo "Starting Django development server..."
python manage.py runserver 0.0.0.0:8000 &

# Start Celery worker
echo "Starting Celery worker..."
celery -A /home/c0smic/Github Repo/intro2se-cq22_3-group15/src/backend/project_management worker -l info &

# Start Celery beat
echo "Starting Celery beat..."
celery -A /home/c0smic/Github Repo/intro2se-cq22_3-group15/src/backend/project_management beat -l info &

echo
echo "Services started:"
echo "- Django Development Server"
echo "- Celery Worker"
echo "- Celery Beat"

# Keep the script running
wait