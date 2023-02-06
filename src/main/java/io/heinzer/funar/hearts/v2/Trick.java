package io.heinzer.funar.hearts.v2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.heinzer.funar.hearts.v2.Card.Suit;
import io.heinzer.funar.hearts.v2.Game.CardPlayed;
import io.heinzer.funar.hearts.v2.Game.Event;
import io.heinzer.funar.hearts.v2.Game.GameId;
import io.heinzer.funar.hearts.v2.Game.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
class Trick {

    private final Collection<CardPlayed> playedCards = new LinkedList<>();

    private final GameId gameId;
    private final int numberOfPlayers;
    private final Consumer<Event> eventPublisher;

    Trick play(Card card, Player player) {
        if (isComplete()) {
            throw new IllegalStateException("Cannot play card, since trick is complete");
        }
        if (hasPlayed(player)) {
            throw new IllegalArgumentException("Player " + player + " already played");
        }
        val event = new CardPlayed(gameId, card, player);
        playedCards.add(event);
        eventPublisher.accept(event);
        return this;
    }

    /**
     * Returns the suit of this trick, which corresponds to the suit of the first
     * card played, or Optional.empty() if the trick is empty.
     */
    Optional<Suit> getSuit() {
        return playedCards.stream()
            .findFirst()
            .map(CardPlayed::card)
            .map(Card::getSuit);
    }

    boolean isSuitOkay(Suit suit) {
        return getSuit()
            .map(suit::equals)
            .orElse(true);
    }

    public boolean isComplete() {
        return playedCards.size() == numberOfPlayers;
    }

    public List<Card> getCards() {
        return playedCards.stream()
            .map(CardPlayed::card)
            .toList();
    }

    /**
     * Returns the winner if all players did play, or {@link Optional.empty()}
     * if the trick is not yet complete.
     */
    public Optional<Player> getWinner() {
        return isComplete()
            ? playedCards.stream()
                .reduce((c1, c2) -> c1.card().beats(c2.card()) 
                    ? c1 
                    : c2)
                .map(CardPlayed::player)
            : Optional.empty();
    }

    boolean hasPlayed(Player player) {
        return playedCards.stream()
            .anyMatch(it -> it.player().equals(player));
    }

}