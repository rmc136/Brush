package com.mybrushgame.client.game;

import com.mybrushgame.client.cards.Card;
import com.mybrushgame.client.cards.Deck;
import com.mybrushgame.client.cards.Player;
import java.util.*;

public class BrushGame {

    private final Deck deck = new Deck();
    private final List<Player> players = new ArrayList<>();
    private final List<Card> tableCards = new ArrayList<>();
    private int currentPlayerIndex = 0; // who plays now

    public void startGame() {
        players.clear();
        tableCards.clear();
        currentPlayerIndex = 0;

        // Create players
        players.add(new Player("Johnny"));
        players.add(new Player("Joni"));
        players.add(new Player("Rodrigo"));

        deck.shuffle();

        // Deal initial hands
        for (Player p : players) {
            for (int i = 0; i < 3; i++) p.drawCard(deck);
        }

        // Deal initial table cards
        for (int i = 0; i < 4; i++) tableCards.add(deck.draw());
    }

    public boolean isGameOver() {
        for (Player p : players) if (!p.getHand().isEmpty()) return false;
        return deck.isEmpty();
    }

    public List<Player> getPlayers() { return players; }
    public List<Card> getTableCards() { return tableCards; }

    // Play a card (called by UI)
    public void playCard(Player player, Card card) {
        if (!player.getHand().contains(card)) return;

        List<Card> collected = checkSum15(card);
        player.getHand().remove(card);

        if (!collected.isEmpty()) {
            player.collectCards(collected);
            if (collected.size() == tableCards.size()) player.incrementBrushes();
            tableCards.removeAll(collected);
        } else {
            tableCards.add(card);
        }

        advanceTurn();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private List<Card> checkSum15(Card played) {
        List<Card> collected = new ArrayList<>();
        int n = tableCards.size();
        for (int mask = 0; mask < (1 << n); mask++) {
            int sum = played.getValue();
            List<Card> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    sum += tableCards.get(i).getValue();
                    subset.add(tableCards.get(i));
                }
            }
            if (sum == 15) {
                collected.addAll(subset);
                collected.add(played);
                return collected;
            }
        }
        return collected;
    }
}
