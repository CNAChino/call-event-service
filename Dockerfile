FROM cnaureusdev/java8-ubuntu1804
MAINTAINER  Carlo Feliciano Aureus <chino.aureus@gmail.com>

# Setup Image
# ============

WORKDIR /app
ENV JAVA_HOME /usr/lib/jvm/default-java
ENV PATH ".:$JAVA_HOME/bin:${PATH}"

# Spring App Configuration.
# Override the settings in application.properties in the section
# Example:  ENV APP_SERVER_LISTEN_PORT 9090

# By default, the server listens on port 9090 as configured in application.properties
EXPOSE 9090
COPY target/call-event-service-1.0.jar /app/
COPY ./ubuntu-linux-run.sh /app/
RUN chmod +x /app/ubuntu-linux-run.sh

# command to run the server
ENTRYPOINT ["/app/ubuntu-linux-run.sh"]

# argument for the shell specified in ENTRYPOINT
# Instead of running the spring app, one can run specific commands inside the docker image by passing
# an argument in the docker run <image> <command-arg>
# Note:  CMD is overriden if <command-arg is present
CMD ["runapp"]