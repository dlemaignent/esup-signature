# Dockerfile.app

FROM python:3.9-slim

# Installer Ghostscript
RUN apt-get update && apt-get install -y ghostscript && apt-get install -y python3-selenium

# Configuration de l'application
WORKDIR /app

COPY . .

CMD ["python", "app.py"]
