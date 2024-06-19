# Dockerfile.gs

FROM alpine:latest

RUN apk add --no-cache ghostscript

CMD ["sleep", "infinity"]
