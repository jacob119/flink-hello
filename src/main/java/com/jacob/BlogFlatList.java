package com.jacob;

import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

@NoArgsConstructor
public class BlogFlatList implements FlatMapFunction<String, String> {
    @Override
    public void flatMap(String value, Collector<String> out) throws Exception {
        if (value.length() == 5)
            out.collect(value);
    }
}