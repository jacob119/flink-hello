ARG FLINK_VERSION=1.17.1
FROM flink:${FLINK_VERSION}

#  variable setup
ARG JAR_FILE_PATH=target/*.jar
RUN mkdir -p /opt/flink-example

# copy to app.jar file
COPY ${JAR_FILE_PATH} /opt/flink-example/app.jar