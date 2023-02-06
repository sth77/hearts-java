package io.heinzer.funar.hearts.v2;

import io.heinzer.funar.hearts.v2.Game.Event;

public interface EventPublisher {
    void publish(Event event);
}
