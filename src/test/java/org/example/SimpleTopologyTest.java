package org.example;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTopologyTest {

    private SimpleTopology simpleTopology;

    @BeforeEach
    void setUp() {
        simpleTopology = new SimpleTopology();
    }

    @Test
    void test() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        simpleTopology.build(streamsBuilder);
        Topology topology = streamsBuilder.build();

        Properties properties = new Properties();
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.IntegerSerde.class);

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, properties)) {
            TestInputTopic<String, Integer> inputTopic1 = topologyTestDriver
                    .createInputTopic("input-topic-1", new StringSerializer(), new IntegerSerializer());

            TestInputTopic<String, Integer> inputTopic2 = topologyTestDriver
                    .createInputTopic("input-topic-2", new StringSerializer(), new IntegerSerializer());

            TestOutputTopic<String, Integer> outputTopic = topologyTestDriver
                    .createOutputTopic("output-topic", new StringDeserializer(), new IntegerDeserializer());

            inputTopic1.pipeInput("key", 1, 0L);
            inputTopic1.pipeInput("key2", 2, 0L);

            inputTopic2.pipeInput("key", 1, 0L);
            inputTopic2.pipeInput("key2", 2, 0L);

            assertThat(outputTopic.readKeyValuesToList()).containsExactly(
                    KeyValue.pair("key", 2),
                    KeyValue.pair("key2", 4)
            );
        }
    }

}
