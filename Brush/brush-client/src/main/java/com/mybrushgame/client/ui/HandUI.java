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
    private TableUI tableUI; // <--- NEW
    private Runnable onCardPlayed;

    public HandUI(Player player, BrushGame gameLogic, TableUI tableUI, Runnable onCardPlayed) {
        table = new Table();
        table.bottom();
        this.player = player;
        this.gameLogic = gameLogic;
        this.tableUI = tableUI; // <--- NEW
        this.onCardPlayed = onCardPlayed;
    }

    public Table getTable() {
        return table;
    }

    private Image createCardImage(Card c) {
        Texture texture = new Texture("cards/" + c.getImageName());
        Image img = new Image(texture);

        img.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                List<Card> selected = tableUI.getSelectedCards();
                if (!selected.isEmpty()) {
                    // Use manual capture
                    gameLogic.playCardWithSelection(player, c, selected);
                    tableUI.clearSelection(); // reset highlight
                } else {
                    // Default auto logic
                    gameLogic.playCard(player, c, selected);
                }

                if (onCardPlayed != null) onCardPlayed.run();
            }
        });

        return img;
    }

    public void update() {
        table.clear();
        for (Card c : player.getHand()) {
            table.add(createCardImage(c)).size(80, 100).pad(2);
        }
    }
}
