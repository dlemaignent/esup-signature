FROM alpine:latest

# Installer Ghostscript dans l'image
RUN apk --no-cache add ghostscript

# Ajouter /usr/bin de l'hôte au PATH du conteneur
ENV PATH="/usr/bin:${PATH}"

# Commande par défaut (peut être remplacée par votre propre commande)
CMD ["sh", "-c", "echo 'Ghostscript container is running...'; sleep infinity"]
