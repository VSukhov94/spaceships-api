package com.develop.management.msgbrokers;

import com.develop.management.dto.spaceship.SpaceshipEventDto;

public interface SpaceshipConsumer {
    void consumeSpaceshipEvent(SpaceshipEventDto event);
}
