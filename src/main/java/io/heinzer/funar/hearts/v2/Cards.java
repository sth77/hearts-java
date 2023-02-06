package io.heinzer.funar.hearts.v2;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import io.heinzer.funar.hearts.v2.Card.Rank;
import io.heinzer.funar.hearts.v2.Card.Suit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Cards {

    public static final List<Card> ALL = Stream.of(Suit.values())
        .flatMap(suit -> Stream.of(Rank.values())
            .map(rank -> new Card(suit, rank)))
        .toList(); 

    public static final Card START_CARD = new Card(Suit.Clubs, Rank.Two);

    public static List<Card> shuffle() {
        return  new Random().ints(0, ALL.size())
            .distinct()
            .limit(ALL.size())
            .mapToObj(ALL::get)
            .toList();
    }

}
