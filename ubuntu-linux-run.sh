#!/bin/bash
set -e

export JAVA_HOME=/usr/lib/jvm/default-java
export PATH=/app:$JAVA_HOME/bin:${PATH}

if [ "$1" = 'runapp' ]; then
	cd /app
    java -jar call-event-service-0.0.1-SNAPSHOT.jar
fi

exec "$@"
