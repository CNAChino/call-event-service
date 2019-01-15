FROM ubuntu:latest
MAINTAINER  Carlo Feliciano Aureus <chino.aureus@gmail.com>

COPY target/call-event-service-1.0.jar /tmp/call-event-service-1.0.jar
COPY ubuntu-linux-run.sh /tmp/ubuntu-linux-run.sh

RUN apt-get update &&
    apt-get install -y openjdk-8-jre &&
    mkdir /app &&
    mv /tmp/call-event-service-1.0.jar /app &&
    mv /tmp/ubuntu-linux-run.sh /app &&
    chmod +x /app/*.sh &&

# Setup Image
# ============
WORKDIR /app
ENV JAVA_HOME /usr/lib/jvm/default-java
ENV PATH ".:$JAVA_HOME/bin:${PATH}"

# Spring App Configuration.
# Override the settings in application.properties in the section
# Example:  ENV APP_SERVER_LISTEN_PORT 9090



# the server listens on port 9090 as configured in application.properties
EXPOSE 9090

# command to run the server
ENTRYPOINT ["/app/ubuntu-linux-run.sh"]

# argument for the shell specified in ENTRYPOINT
# Instead of running the spring app, one can run specific commands inside the docker image by passing
# an argument in the docker run <image> <command-arg>
# Note:  CMD is overriden if <command-arg is present
CMD ["runapp"]