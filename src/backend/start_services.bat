@echo off

REM Check if Docker is running
docker info > nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Start Redis in Docker
echo Starting Redis...
start cmd /k "docker run --rm -p 6379:6379 redis:7"

REM Wait for Redis to be ready
timeout /t 10 /nobreak

REM Start Django server
echo Starting Django server...
start cmd /k "python manage.py runserver"

REM Start Celery worker
echo Starting Celery worker...
start cmd /k "celery -A project_management worker -l info"

REM Start Celery beat
echo Starting Celery beat...
start cmd /k "celery -A project_management beat -l info"

echo.
echo Services started:
echo - Redis (Docker)
echo - Django Development Server
echo - Celery Worker
echo - Celery Beat
echo.
echo Press any key to exit...
pause > nul