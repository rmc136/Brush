package com.mybrushgame.client.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mybrushgame.client.cards.Card;

import java.util.List;

public class TableUI {

    private Table table;

    public TableUI() {
        table = new Table();
        table.center();
    }

    public Table getTable() {
        return table;
    }

    public void update(List<Card> tableCards) {
        table.clear();

        float cardWidth = 125;
        float cardHeight = 150;

        Table cardsRow = new Table();
        cardsRow.center(); // center cards horizontally
        for (Card c : tableCards) {
            Texture texture = new Texture("cards/" + c.getImageName());
            Image img = new Image(texture);
            cardsRow.add(img).size(cardWidth, cardHeight); // no padding
        }

        table.add(cardsRow).center().expand(); // center nested table in parent
    }

}
