package io.heinzer.funar.hearts.v2;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import io.heinzer.funar.hearts.v2.Game.GameId;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class Games {

    private final ApplicationEventPublisher eventPublisher;
    
    private Map<GameId, Game> store = new ConcurrentHashMap<>();

    Game save(Game game) {
        game.getEventsToPublishAndClear()
            .forEach(eventPublisher::publishEvent);
        store.put(game.getId(), game);
        return game;
    }

    Optional<Game> findById(GameId id) {
        return Optional.ofNullable(store.get(id));
    }

}
