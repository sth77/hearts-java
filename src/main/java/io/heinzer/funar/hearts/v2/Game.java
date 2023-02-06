package io.heinzer.funar.hearts.v2;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import io.heinzer.funar.hearts.v2.Card.Suit;

/**
 * Hearts game, consisting of playing 52 cards with four players.
 */
@Slf4j
public class Game {

    private static final int NUMBER_OF_PLAYERS = 4;
    private static final int NUMBER_OF_CARDS = Cards.ALL.size();
    private static final int SIZE_OF_HAND = NUMBER_OF_CARDS / NUMBER_OF_PLAYERS;
    private static final List<Player> PLAYERS = IntStream.range(0, NUMBER_OF_PLAYERS)
        .mapToObj(Player::new)
        .toList();

    private List<Event> events = new LinkedList<>();
    private List<Event> eventsToPublish = new LinkedList<>();

    @Getter
    private final GameId id;

    @Getter
    private Trick trick;

    @Getter
    private Optional<Player> nextPlayer = Optional.empty();

    private Game(GameId id) {
        this.id = id;
        this.trick = new Trick(id, NUMBER_OF_PLAYERS, this::registerEvent);
    }

    static Game create() {
        return new Game(GameId.random());
    }

    public Map<Player, Collection<Card>> getHands() {
        val result = new HashMap<Player, Collection<Card>>();
        events.stream()
            .filter(HandDealt.class::isInstance)
            .map(HandDealt.class::cast)
            .forEach(it -> result.put(it.player(), it.hand().cards));
        return result;
    }

    Game playCard(Card card, Player player) {
        validatePlayCardAttempt(card, player);
        trick.play(card, player);
        tryTurnOverTrick();
        return this;
    }

    private void validatePlayCardAttempt(Card card, Player player) {
        if (!isTurnOf(player)) {
            throw new IllegalArgumentException("It is not the turn of player " + player);
        }
        if (!isCardValid(player, card)) {
            registerEvent(new IllegalCardAttempted(id, player, card));
            throw new IllegalArgumentException("Player " + player + " is not allowed to play card " + card);
        }
    }

    boolean isCardValid(Player player,  Card card) {
        return getHandOf(player)
            .map(hand -> hand.cards.contains(card)
                && trick.isSuitOkay(card.getSuit()) || !hand.hasSuit(card.getSuit()))
            .orElse(false);
    }

    Optional<Hand> getHandOf(Player player) {
        return events.stream()
            .filter(HandDealt.class::isInstance)
            .map(HandDealt.class::cast)
            .filter(it -> it.player.equals(player))
            .map(HandDealt::hand)
            .findFirst();
    }

    boolean tryTurnOverTrick() {
        return trick.getWinner()
            .map(winner -> new TrickTaken(id, winner, trick))
            .map(event -> {
                trick = new Trick(id, NUMBER_OF_PLAYERS, this::registerEvent);
                registerEvent(event);
                return true;
            })
            .orElse(false);
    }

    Game dealHand() {
        val shuffledCards = Cards.shuffle();
        IntStream.range(0, NUMBER_OF_PLAYERS)
            .mapToObj(i -> new HandDealt(
                id,
                new Player(i), 
                new Hand(shuffledCards.subList(
                    i * SIZE_OF_HAND, 
                    (i + 1) * SIZE_OF_HAND))))
            .forEach(this::registerEvent);
        nextPlayer = identifyStartingPlayer();
        trick = new Trick(id, NUMBER_OF_PLAYERS, this::registerEvent);
        return this;
    }

    private Optional<Player> identifyStartingPlayer() {
        return PLAYERS.stream()
            .filter(p -> getHandOf(p)
                .orElseThrow()
                .containsStartCard())
            .findFirst();
    }

    Player playerAfter(Player player) {
        return PLAYERS.get(player.id + 1 % NUMBER_OF_PLAYERS);
    }

    private boolean isTurnOf(Player player) {
        return nextPlayer.map(player::equals).orElse(false);
    }

    boolean isGameOver() {
        return events.stream()
            .filter(CardPlayed.class::isInstance)
            .count() == Cards.ALL.size();
    }

    // Event handling

    void registerEvent(Event event) {
        log.info("<e> " + event);
        events.add(event);
        eventsToPublish.add(event);
    }

    List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    List<Event> getEventsToPublishAndClear() {
        val result = eventsToPublish;
        eventsToPublish = new LinkedList<>();
        return result;
    }

    // helpers

    public record GameId (String intValue) {
        static GameId random() {
            return new GameId(UUID.randomUUID().toString());
        }
    }
    public record WonTrick (Trick trick, Player winner) {}
    public record Player (int id) {}
    public record Hand(Collection<Card> cards) {
        boolean hasSuit(Suit suit) {
            return cards.stream()
                .map(Card::getSuit)
                .anyMatch(suit::equals);
        }
        boolean containsStartCard() {
            return cards.stream()
                .filter(Cards.START_CARD::equals)
                .findFirst()
                .isPresent();
        }
    }

    // events
    interface Event { GameId gameId(); }
    public record HandDealt (GameId gameId, Player player, Hand hand) implements Event { }
    public record PlayerTurnChanged(GameId gameId, Player player) implements Event { }
    public record CardPlayed (GameId gameId, Card card, Player player) implements Event { }    
    public record TrickTaken(GameId gameId, Player player, Trick trick) implements Event { }
    public record IllegalCardAttempted(GameId gameId, Player player, Card card) implements Event { }
    public record GameEnded(GameId gameId, Player player) implements Event { }

}
