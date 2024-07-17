ARG FLINK_VERSION=1.17.1
FROM flink:${FLINK_VERSION}

#  variable setup
ARG JAR_FILE_PATH=target/flink-hello-1.1-SNAPSHOT.jar
#RUN mkdir -p /opt/test

# copy to app.jar file
COPY ${JAR_FILE_PATH} /opt/flink/lib/flink-hello-1.1-SNAPSHOT.jar
