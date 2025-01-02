FROM ubuntu:latest
WORKDIR /usr/src
EXPOSE 8000

RUN apt update && \
    apt install -y python3 python3-pip python3.12-venv build-essential libffi-dev libpq-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/* && \
    python3 -m venv /venv

ENV PATH="/venv/bin:$PATH"

COPY ./requirements.txt ./backend/requirements.txt

RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r ./backend/requirements.txt 

COPY . ./backend

RUN python ./backend/manage.py makemigrations && \
    python ./backend/manage.py migrate

CMD ["python", "./backend/manage.py", "runserver", "0.0.0.0:8000"]

# ? Even though my multi-stage build's image size is less than the single-stage build (~20MiB), the time taken to build the image is noticably more (~5 mins).
# ! Definitely not worth it for a small project like this. I'll stick to single-stage builds for now.