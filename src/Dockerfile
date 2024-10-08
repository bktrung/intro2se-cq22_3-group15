FROM ubuntu:latest
WORKDIR /usr/src
EXPOSE 8000

# Install Python, pip, and necessary system dependencies in a single RUN command to reduce layers
RUN apt update && \
    apt install -y python3 python3-pip python3.12-venv && \
    apt install -y build-essential libffi-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy project files to leverage Docker caching
COPY ./backend ./backend

# Create and activate virtual environment, then install Python dependencies
RUN python3 -m venv venv && \
    ./venv/bin/pip install --no-cache-dir -r ./backend/requirements.txt && \
    ./venv/bin/pip install --upgrade pip setuptools wheel && \
# Run Django migrations
    ./venv/bin/python ./backend/manage.py migrate

# Default command to run the Django development server
CMD ["./venv/bin/python", "./backend/manage.py", "runserver", "0.0.0.0:8000"]
