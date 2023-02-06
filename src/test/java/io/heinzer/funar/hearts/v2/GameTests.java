package io.heinzer.funar.hearts.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import io.heinzer.funar.hearts.v2.Game.HandDealt;
import lombok.val;

public class GameTests {
    
    @Test
    void create__gameReturnedAndHandDealt() {
        // act
        val result = Game.create();

        // assert
        val dealtHands = result.getEvents().stream()
            .filter(HandDealt.class::isInstance)
            .map(HandDealt.class::cast)
            .toList();
        assertThat(dealtHands).isEmpty();
        assertThat(result.getNextPlayer()).isNotPresent();
    }

    @Test
    void dealHand__handsDealt() {
        val game = Game.create();

        // act
        val result = game.dealHand();

        // assert
        val dealtHands = result.getEvents().stream()
            .filter(HandDealt.class::isInstance)
            .map(HandDealt.class::cast)
            .toList();
        assertThat(dealtHands).hasSize(4);
        assertThat(result.getNextPlayer()).isPresent();
    }

    @Test
    void playCard_wrongPlayer_throwsException() {
        val game = Game.create().dealHand();
        val wrongPlayer = game.getNextPlayer()
            .map(p -> new Game.Player((p.id() + 1) % 4))
            .orElseThrow();

        // act & assert
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> game.playCard(Cards.START_CARD, wrongPlayer));
    }

    @Test
    void playCard_startCard_eventRecorded() {
        val game = Game.create().dealHand();

        val card = Cards.START_CARD;
        val player = game.getNextPlayer().orElseThrow();

        // act
        val result = game.playCard(Cards.START_CARD, player);

        // assert
        assertThat(result.getEventsToPublishAndClear().stream()
            .filter(it -> it.equals(new Game.CardPlayed(game.getId(), card, player))))
            .hasSize(1);            
    }

}
