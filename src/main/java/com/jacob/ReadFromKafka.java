package com.jacob;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class ReadFromKafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        Properties properties = new Properties();
        /*
          external service ip를 사용하려면, 아래 내용으로 진행 한다.
         */
//        properties.setProperty("bootstrap.servers", "localhost:9094");



        /* k8s에 있는 카프카와 통신할 경우는 아래를 사용 한다.
           k describe kafka -n kafka
         */
        properties.setProperty("bootstrap.servers", "my-cluster-kafka-bootstrap.kafka.svc:9092");
        DataStream<String> stream = env
                .addSource(new FlinkKafkaConsumer<>("jacob-topic", new SimpleStringSchema(), properties));

        stream.map(new MapFunction<String, String>() {
            private static final long serialVersionUID = -6867736771747690202L;

            @Override
            public String map(String value) throws Exception {
                return "Stream Value: " + value;
            }
        }).print();

        env.execute("kafka consumer");
    }
}
