package com.jacob;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.shaded.curator5.com.google.common.collect.ImmutableMap;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

public class IntegrationKafkaTest {
    private static final int PARALLELISM = 1;

    public static MiniClusterWithClientResource flinkCluster =
            new MiniClusterWithClientResource(
                    new MiniClusterResourceConfiguration.Builder()
                            .setNumberSlotsPerTaskManager(PARALLELISM)
                            .setNumberTaskManagers(1)
                            .build());

    protected void testKafkaFunctionality(String bootstrapServers) throws Exception {
        testKafkaFunctionality(bootstrapServers, 1, 1);
    }

    protected void testKafkaFunctionality(String bootstrapServers, int partitions, int rf) throws Exception {
        try (
                AdminClient adminClient = AdminClient.create(ImmutableMap.of(
                        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
                ));

                KafkaProducer<String, String> producer = new KafkaProducer<>(
                        ImmutableMap.of(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                        ),
                        new StringSerializer(),
                        new StringSerializer()
                )

        ) {
            String topicName = "messages";
            Collection<NewTopic> topics = singletonList(new NewTopic(topicName, partitions, (short) rf));
            adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS);
            producer.send(new ProducerRecord<>(topicName, "1", "should not go through")).get();
            producer.send(new ProducerRecord<>(topicName, "1", "test1")).get();
            producer.send(new ProducerRecord<>(topicName, "1", "test2")).get();
            producer.send(new ProducerRecord<>(topicName, "1", "should not go through")).get();
            producer.send(new ProducerRecord<>(topicName, "1", "end")).get();
        }
    }

    @Test
    @DisplayName("should have 2 resulting event")
    public void test1() throws Exception {
//        KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
//        kafka.start();
//        testKafkaFunctionality(kafka.getBootstrapServers());
        String BootstrapServers = "localhost:9094";
//        testKafkaFunctionality(BootstrapServers);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.getConfig().setClosureCleanerLevel(ExecutionConfig.ClosureCleanerLevel.NONE);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", BootstrapServers);
        properties.setProperty("group.id", "messages-group-1");

        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer("messages",
                new TestDeserializer(), properties);

        consumer.setStartFromEarliest();

        env.addSource(consumer)
                .flatMap(new BlogFlatList())
                .addSink(new TestSink<>("result"));

        System.out.println("test!!!");
        JobExecutionResult jobResult = env.execute();
        System.out.println("222");

        Map<String, Object> accumMap = jobResult.getAllAccumulatorResults();
        List<String> output = (List<String>) accumMap.get("result");
        Assertions.assertThat(output.size()).isEqualTo(2);
    }
}
