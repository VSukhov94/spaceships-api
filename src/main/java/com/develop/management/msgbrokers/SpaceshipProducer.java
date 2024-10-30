package com.develop.management.msgbrokers;

import com.develop.management.dto.spaceship.SpaceshipEventDto;

public interface SpaceshipProducer {
    void sendSpaceshipEvent(SpaceshipEventDto event);
}
