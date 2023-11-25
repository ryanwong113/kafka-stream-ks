package org.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SimpleTopology {

    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Integer> INTEGER_SERDE = Serdes.Integer();

    @Autowired
    void build(StreamsBuilder streamsBuilder) {
        KStream<String, Integer> stream1 = streamsBuilder
                .stream("input-topic-1", Consumed.with(STRING_SERDE, INTEGER_SERDE));

        KStream<String, Integer> stream2 = streamsBuilder
                .stream("input-topic-2", Consumed.with(STRING_SERDE, INTEGER_SERDE));

        stream1.join(stream2,
                     Integer::sum,
                     JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(5L)))
               .to("output-topic");
    }

}
