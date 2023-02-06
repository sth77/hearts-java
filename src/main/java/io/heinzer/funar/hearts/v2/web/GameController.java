package io.heinzer.funar.hearts.v2.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.heinzer.funar.hearts.v2.Card;
import io.heinzer.funar.hearts.v2.Game;
import io.heinzer.funar.hearts.v2.GameTable;
import io.heinzer.funar.hearts.v2.GameTable.PlayCard;
import io.heinzer.funar.hearts.v2.GameTable.DealHands;
import io.heinzer.funar.hearts.v2.Game.GameId;
import io.heinzer.funar.hearts.v2.Game.Player;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GameController {
    
    private final GameTable table;

    @GetMapping
    ResponseEntity<?> root() {
        return ResponseEntity.ok().body("test");
    }


    @PostMapping("/games/startNew")
    ResponseEntity<Game> startGame() {
        return ResponseEntity.ok()
            .body(table.startNewGame());
    }

    @PostMapping("/games/{gameId}/dealHands")
    ResponseEntity<Game> dealHands(GameId gameId) {
        return ResponseEntity.ok()
            .body(table.on(new DealHands(gameId)));
    }

    @PostMapping("/games/{gameId}/player/{playerId}/playCard")
    ResponseEntity<Game> playCard(GameId gameId, Player playerId, Card card) {
        return ResponseEntity.ok()
            .body(table.on(new PlayCard(gameId, playerId, card)));
    }

}
