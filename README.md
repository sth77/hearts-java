# Hearts Java

Attempt to implement core components of the the hearts application of the iSAQB FUNAR training in Java to compare the Java / OO implementation with the one in Haskell / FP.

## Disclaimer

Besides functional incompleteness, missing tests, etc., the code would require some more iterations and has many loose ends.

## Domain

The application models the hearts game, consiting of 52 cards, each with one of four suits and one of twelve ranks (2 .. Ace).

    Card

    Game --> Trick

### Domain Events

* HandDealt
* PlayerTurnChanged
* CardPlayed    
* TrickTaken
* IllegalCardAttempted
* GameEnded

## Architecture outline

The code follows an onion architecture:
* Domain: Forms the core, with a rich domain model (Card, Game, Trick etc.). This is also the source of domain events. 
* Service: Defines commands to interact with the domain and implements event listeners to trigger domain logic (not used so far). Currently, there is only one domain service: GameTable
* Infrastructure: Implements the technical implementation to the outside world, currently in the form of a REST API. Only class so far: GameController.

The dependencies to Spring Data JDBC and H2 would allow to persist games to a relational database. So far, "Games" is however a simplistic in-memory store. With that, the application so far misses a concept of transactionality, which otherwise could be delegated to the database.

Event publication follows the approach of Spring Data, where events are accumulated on the aggregate during a domain operation, an published when persisting the altered aggregate has succeeded.

## API

* localhost:8080/games/startNew: Creates a new game
* localhost:8080/games/{id}/dealHands: Deals the hands for the four players and identifies the starting player
* localhost:8080/games/{id}/player/{playerId}/playCard: Lets a player play a card. If it is not the player's turn or if the player is not allowed to play the card, an error code is returned.

## Comparison FP vs OOP

* The Haskell implementation is intriguing due to the pure functional domain. This is actually what many of the techniques used in the Java approach aim at: using immutable data structure (Lombok), avoid side effects in aggregate methods, etc. 
* The OO code is in my opinion more easily comprehensible, as it more directly expresses the domain, bundling data and logic in the aggregates.

It could be interesting to analyze in more depth where side effects come into play in both approaches and whether it could make sense to make also the aggregates of the OOP version immutable, returning a copy from each domain operation.

## Links

* https://github.com/active-group/funar

## License

MIT