
# Apache Flink and Apache Kafka
This project is use a simple Flink job to show how to integrate Apache Kafka to Flink using the Flink Connector for Kafka.
flink 1.18.0
jdk 8 or higher

## Build && Running
jdk11 && mvn clean package -DskipTests && jdk8 &&  mvn exec:java -Dexec.mainClass=com.jacob.ReadFromKafka

## How to submit to flink cluster
flink run ./target/filink-hello-1.0-SNAPSHOT.jar
flink cancel $JOB_ID

## flink command-cli
https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/cli/


## flink job checked
flink list

-- output --
Waiting for response...
------------------ Running/Restarting Jobs -------------------
17.02.2024 12:24:24 : 8f33fd9eedd4310aa8a7bb6714f63a35 : kafka consumer (RUNNING)
--------------------------------------------------------------

## Web UI
http://localhost:8081/#/overview