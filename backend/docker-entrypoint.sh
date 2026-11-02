#!/bin/bash
set -euo pipefail

UPLOAD_DIR="${FILE_UPLOAD_PATH:-/var/data/uploads}"
mkdir -p "$UPLOAD_DIR"

# Seed the bundled catalogue images once. User-uploaded files remain on the
# Render persistent disk across future deploys.
if [ -d /opt/seed-uploads ] && [ ! -f "$UPLOAD_DIR/.seed-complete" ]; then
  cp -a /opt/seed-uploads/. "$UPLOAD_DIR"/
  touch "$UPLOAD_DIR/.seed-complete"
fi

if [ -n "${DB_HOST:-}" ]; then
  until (echo > "/dev/tcp/${DB_HOST}/${DB_PORT:-3306}") >/dev/null 2>&1; do
    echo "Waiting for MySQL at ${DB_HOST}:${DB_PORT:-3306}..."
    sleep 2
  done
fi

exec java ${JAVA_OPTS:-} -jar /app/app.jar
