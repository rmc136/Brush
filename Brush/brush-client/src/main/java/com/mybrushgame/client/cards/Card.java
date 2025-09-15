package com.mybrushgame.client.cards;

public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank; // must return Rank, not boolean
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public String getImageName() {
        String rankName;
        switch (rank) {
            case ACE -> rankName = "ace";
            case JACK -> rankName = "jack";
            case QUEEN -> rankName = "queen";
            case KING -> rankName = "king";
            default -> rankName = String.valueOf(rank.getValue()); // number cards 2-7
        }
        return rankName + "_of_" + suit.toString().toLowerCase() + ".png";
    }
}
