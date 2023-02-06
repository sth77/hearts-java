package io.heinzer.funar.hearts;

import lombok.Value;

import java.util.Optional;

import lombok.Builder;

interface Game {
    interface Event {}
    interface Command {}
    interface Player {}
    interface Card {}
    interface Trick {}

    boolean isCardValid(Player player,  Card card);
    void recordEvent(Event event);
    Optional<Player> turnOverTrick();
    Player playerAfter(Player player);
    Command getNextCommand(Command command);
    boolean isGameOver();

    @Value
    @Builder
    static class PlayCard implements Event {
        Card card;
        Player player;
    }
}

