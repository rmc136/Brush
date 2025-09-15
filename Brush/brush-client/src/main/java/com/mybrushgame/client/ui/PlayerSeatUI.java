package com.mybrushgame.client.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mybrushgame.client.cards.Player;

public class PlayerSeatUI {

    private Table table;
    private Label leftPlayer;
    private Label rightPlayer;

    public PlayerSeatUI(Skin skin, Player left, Player right) {
        table = new Table();

        leftPlayer = new Label(left.getName(), skin);
        rightPlayer = new Label(right.getName(), skin);

        // Add labels without expanding cells
        table.add(leftPlayer).left();
        table.add().expandX(); // empty cell to push rightPlayer to the right
        table.add(rightPlayer).right();
    }

    public Table getTable() {
        return table;
    }

    // Optional: position manually on stage
    public void setPosition(float stageWidth, float stageHeight) {
        table.setPosition(0, stageHeight / 2); // y = middle of stage
        table.setWidth(stageWidth);
    }
}



