#!/bin/bash

# wait-for-postgres.sh
set -e

until PGPASSWORD=123456 psql -h "postgres" -U "test" -d "youmanage" -c '\q'; do
  echo "Postgres is unavailable - sleeping"
  sleep 1
done

echo "Postgres is up - creating migrations"
python ./backend/manage.py makemigrations activity_log
python ./backend/manage.py makemigrations chat
python ./backend/manage.py makemigrations custom_auth
python ./backend/manage.py makemigrations notification
python ./backend/manage.py makemigrations project_manager

echo "Applying migrations"
python ./backend/manage.py migrate

echo "Starting application"
exec "$@"