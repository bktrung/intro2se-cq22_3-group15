FROM ubuntu:latest
WORKDIR /usr/src
EXPOSE 8000

RUN apt update && \
    apt install -y python3 python3-pip python3.12-venv build-essential libffi-dev libpq-dev postgresql-client && \
    apt-get clean && rm -rf /var/lib/apt/lists/* && \
    python3 -m venv /venv

ENV PATH="/venv/bin:$PATH"

COPY ./requirements.txt ./backend/requirements.txt
COPY ./wait-for-postgres.sh /wait-for-postgres.sh
RUN chmod +x /wait-for-postgres.sh

RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r ./backend/requirements.txt 

COPY . ./backend

CMD ["/wait-for-postgres.sh", "python", "./backend/manage.py", "runserver", "0.0.0.0:8000"]