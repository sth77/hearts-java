package io.heinzer.funar.hearts.v2;

import lombok.Value;

import lombok.NonNull;

@Value
public class Card {

    @NonNull
    Suit suit;

    @NonNull
    Rank rank;

    boolean beats(Card other) {
        return suit == other.suit 
            && rank.ordinal() > other.rank.ordinal();
    }

    @Override
    public String toString() {
        return suit + " " + rank;
    }

    enum Suit { Spades, Hearts, Diamonds, Clubs }
    enum Rank { Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace }

}