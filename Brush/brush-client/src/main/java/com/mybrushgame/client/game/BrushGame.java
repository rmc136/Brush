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
    public void playCard(Player player, Card card, List<Card> selected) {
        if (!player.getHand().contains(card)) return;

        List<Card> collected = checkSum15(card);
        player.getHand().remove(card);

        if (selected.isEmpty()){
            tableCards.add(card);
        }
        else if (!collected.isEmpty()) {
            player.collectCards(collected);
            System.out.println("Table cards before");
            if (collected.size() == tableCards.size() + 1) player.incrementBrushes();
            tableCards.removeAll(collected);
            System.out.println("Mão do Johnny: " + collected);
        } else {
            tableCards.add(card);
        }

        advanceTurn();
    }

    public void playCardWithSelection(Player player, Card handCard, List<Card> selected) {
        if (!player.getHand().contains(handCard)) return;

        int sum = handCard.getValue();
        for (Card c : selected) sum += c.getValue();

        player.getHand().remove(handCard);

        if (sum == 15 && new HashSet<>(tableCards).containsAll(selected)) {
            // valid capture
            player.collectCards(selected);
            player.collectCards(Collections.singletonList(handCard));
            tableCards.removeAll(selected);
        } else {
            // fallback: just place card on table
            tableCards.add(handCard);
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

    public Deck getDeck() {
        return deck;
    }

    public boolean allHandsEmpty() {
        for (Player p : players) {
            if (!p.getHand().isEmpty()) return false;
        }
        return true;
    }

    // Deal 3 new cards if available
    public void dealNewRound() {
        for (Player p : players) {
            for (int i = 0; i < 3; i++) {
                if (!deck.isEmpty()) {
                    p.drawCard(deck);
                }
            }
        }
    }

    public Player getWinner() {
        if (!isGameOver()) return null;

        int maxScore = 0;
        Player winner = null;
        int count = -1;

        for (Player p : players) {
            int score = p.calculatePoints() + p.getBrushes();
            if (score == maxScore){
               winner = tiebreak(p, players.get(count));
            }
            if (score > maxScore) {
                maxScore = score;
                winner = p;
            }
            count++;
        }
        return winner;
    }

    private Player tiebreak(Player p, Player player) {
        if(p.getPointsStack().size() == player.getPointsStack().size()){
            return null;
        } else if (p.getPointsStack().size() > player.getPointsStack().size()) {
            return p;
            
        }else {
            return player;
        }
    }

    public void finishGame() {
        if (!tableCards.isEmpty()) {
            Player lastPlayer = players.get((currentPlayerIndex - 1 + players.size()) % players.size());
            lastPlayer.collectCards(new ArrayList<>(tableCards));
            tableCards.clear();
        }
    }

}
