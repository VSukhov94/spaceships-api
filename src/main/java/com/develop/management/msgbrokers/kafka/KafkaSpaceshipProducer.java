package com.develop.management.msgbrokers.kafka;

import com.develop.management.dto.spaceship.SpaceshipEventDto;
import com.develop.management.msgbrokers.SpaceshipProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSpaceshipProducer implements SpaceshipProducer {

    @Value("${kafka.topic.spaceship-events}")
    private String spaceshipTopic;

    private final KafkaTemplate<String, SpaceshipEventDto> kafkaTemplate;

    @Override
    public void sendSpaceshipEvent(SpaceshipEventDto event) {
        kafkaTemplate.send(spaceshipTopic, event);
        log.info("Sent spaceship event: {}", event);
    }

}
