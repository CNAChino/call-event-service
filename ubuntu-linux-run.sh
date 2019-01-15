#!/bin/bash
set -e

export JAVA_HOME=/usr/lib/jvm/default-java
export PATH=/app:$JAVA_HOME/bin:${PATH}

if [ "$1" = 'runapp' ]; then
	cd /app
    java -jar call-event-service-1.0.jar
    exit 0
fi

exec "$@"
exit 0
