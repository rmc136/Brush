package com.mybrushgame.client.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mybrushgame.client.cards.Card;
import com.mybrushgame.client.cards.Player;
import com.mybrushgame.client.game.BrushGame;

import java.util.List;

public class HandUI {

    private Table table;
    private Player player;
    private BrushGame gameLogic;
    private Runnable onCardPlayed;

    public HandUI(Player player, BrushGame gameLogic, Runnable onCardPlayed) {
        table = new Table();
        table.bottom();
        this.player = player;
        this.gameLogic = gameLogic;
        this.onCardPlayed = onCardPlayed;
    }

    public Table getTable() {
        return table;
    }

    public void update() {
        table.clear();
        List<Card> hand = player.getHand();

        float cardWidth = 80;  // small width for hand cards
        float cardHeight = 100; // small height

        for (Card c : hand) {
            Texture texture = new Texture("cards/" + c.getImageName());
            Image img = new Image(texture);
            img.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameLogic.playCard(player, c);
                    if (onCardPlayed != null) onCardPlayed.run();
                }
            });
            table.add(img).size(cardWidth, cardHeight).pad(2); // sets the size directly
        }
    }
}
