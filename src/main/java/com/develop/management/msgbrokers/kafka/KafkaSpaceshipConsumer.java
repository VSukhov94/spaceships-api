package com.develop.management.msgbrokers.kafka;

import com.develop.management.dto.spaceship.SpaceshipEventDto;
import com.develop.management.msgbrokers.SpaceshipConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSpaceshipConsumer implements SpaceshipConsumer {

    @Override
    @KafkaListener(topics = "${kafka.topic.spaceship-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSpaceshipEvent(SpaceshipEventDto event) {
        log.info("Received spaceship event: {}", event);
    }

}
