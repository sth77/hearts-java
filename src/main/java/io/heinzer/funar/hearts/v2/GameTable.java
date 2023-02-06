package io.heinzer.funar.hearts.v2;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.heinzer.funar.hearts.v2.Game.GameId;
import io.heinzer.funar.hearts.v2.Game.Player;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameTable {

    private final Games games;

    public synchronized Game startNewGame() {
        return games.save(Game.create());
    }

    public synchronized Optional<Game> dealHands(GameId gameId) {
        return games.findById(gameId)
            .map(Game::dealHand)
            .map(games::save);
    }

    @EventListener
    public synchronized Game on(DealHands command) {
        return games.findById(command.gameId())
            .map(Game::dealHand)
            .orElseThrow(() -> new GameNotFoundException(
                "Game " + command.gameId() + " not found")); 
    }

    @EventListener
    public synchronized Game on(PlayCard command) {
        return games.findById(command.gameId())
            .map(it -> it.playCard(command.card(), command.playerId()))
            .orElseThrow(() -> new GameNotFoundException(
                "Game " + command.gameId() + " not found")); 
    }

    // commands

    interface Command {}
    public record PlayCard(GameId gameId, Player playerId, Card card) implements Command { }
    public record DealHands (GameId gameId) {}

}
