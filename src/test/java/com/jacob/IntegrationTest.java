package com.jacob;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

/*
 참고 코드
 https://velog.io/@zxshinxz/Flink%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-Stream-processing-4-Testing
 */
public class IntegrationTest {
    private static final int PARALLELISM = 1;

    public static MiniClusterWithClientResource flinkCluster =
            new MiniClusterWithClientResource(
                    new MiniClusterResourceConfiguration.Builder()
                            .setNumberSlotsPerTaskManager(PARALLELISM)
                            .setNumberTaskManagers(1)
                            .build());

    @Test
    @DisplayName("should have 2 resulting event")
    public void testHello() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setClosureCleanerLevel(ExecutionConfig.ClosureCleanerLevel.NONE);
        // 두개의 이벤트만 글자가 5글자 이기 때문에, 2개의 이벤트가 결과적으로 나와야함
        env.fromElements("should not go through", "test1", "test2", "should not go through")
                .flatMap(new BlogFlatList())
                .addSink(new TestSink<>("result"));

        JobExecutionResult jobResult = env.execute();

        Map<String, Object> accumMap = jobResult.getAllAccumulatorResults();
        List<String> output = (List<String>) accumMap.get("result");

        Assertions.assertThat(output.size()).isEqualTo(2);
        System.out.println("finished");
    }
}
