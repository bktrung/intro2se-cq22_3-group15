FROM ubuntu:latest
WORKDIR /usr/src
EXPOSE 8000

RUN apt update && \
    apt install -y python3 python3-pip python3.12-venv build-essential libffi-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/* && \
    python3 -m venv /venv

COPY ./requirements.txt ./backend/requirements.txt

RUN /venv/bin/pip install --no-cache-dir -r ./backend/requirements.txt && \
    /venv/bin/pip install --upgrade pip setuptools wheel

COPY . ./backend

RUN /venv/bin/python ./backend/manage.py migrate

CMD ["/venv/bin/python", "./backend/manage.py", "runserver", "0.0.0.0:8000"]
