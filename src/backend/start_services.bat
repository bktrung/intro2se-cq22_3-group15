@echo off

REM Filepath: /c:/Users/trung/OneDrive/Máy tính/New folder/intro2se-cq22_3-group15/src/backend/start_services.bat

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
echo Waiting for Redis to initialize...
timeout /t 10 /nobreak

REM Start Django ASGI server with Uvicorn
echo Starting Django ASGI server...
start cmd /k "uvicorn project_management.asgi:application --host 0.0.0.0 --port 8000"

REM Start Celery worker with solo pool in windows, remove it in linux
echo Starting Celery worker...
start cmd /k "celery -A project_management worker -l info --pool=solo"

REM Start Celery beat
echo Starting Celery beat...
start cmd /k "celery -A project_management beat -l info"

echo.
echo Services started:
echo - Redis (Docker)
echo - Django ASGI Server (Uvicorn)
echo - Celery Worker (Solo Pool)
echo - Celery Beat
echo.
echo Press any key to exit...
pause > nul