# Dockerfile.app

FROM python:3.9-slim

# Installer Ghostscript
RUN apt-get update && apt-get install -y ghostscript

# Configuration de l'application
WORKDIR /app

COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

COPY . .

CMD ["python", "app.py"]
