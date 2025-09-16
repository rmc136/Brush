package com.mybrushgame.client.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mybrushgame.client.cards.Player;
import com.mybrushgame.client.ui.HandUI;
import com.mybrushgame.client.ui.PlayerSeatUI;
import com.mybrushgame.client.ui.ScoreUI;
import com.mybrushgame.client.ui.TableUI;

public class BrushGameUI extends ApplicationAdapter {

    private Stage stage;
    private Skin skin;

    private BrushGame gameLogic;
    private Player humanPlayer;

    // Modular UI components
    private ScoreUI scoreUI;
    private TableUI tableUI;
    private HandUI handUI;
    PlayerSeatUI seatUI;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize game logic
        gameLogic = new BrushGame();
        gameLogic.startGame();

        humanPlayer = gameLogic.getPlayers().get(0); // first player is human
        Player humanPlayer = gameLogic.getPlayers().get(0);
        Player leftPlayer = gameLogic.getPlayers().get(1);
        Player rightPlayer = gameLogic.getPlayers().get(2);

        // Initialize modular UI
        scoreUI = new ScoreUI(skin, gameLogic.getPlayers());
        tableUI = new TableUI();
        handUI = new HandUI(humanPlayer, gameLogic, this::refreshUI);
        seatUI = new PlayerSeatUI(skin, leftPlayer, rightPlayer);

        // Parent table to layout everything
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        mainTable.top().add(scoreUI.getTable()).expandX().row();
        mainTable.center().add(tableUI.getTable()).expand().row();
        mainTable.bottom().add(handUI.getTable()).expandX();

        stage.addActor(mainTable);
        stage.addActor(seatUI.getTable());
        seatUI.update();

        // Initial render
        refreshUI();
    }

    private void refreshUI() {
        scoreUI.update(gameLogic.getPlayers());
        tableUI.update(gameLogic.getTableCards());
        handUI.update();
        // Let AI play if it's their turn
        while (!(gameLogic.getCurrentPlayer().equals(humanPlayer)) && !gameLogic.isGameOver()) {
            playAITurns();
            seatUI.update();
        }

        // If all players are out of cards but deck still has cards → deal new ones
        if (gameLogic.allHandsEmpty() && !gameLogic.getDeck().isEmpty()) {
            gameLogic.dealNewRound();
            handUI.update(); // refresh human hand
        }
        if (gameLogic.isGameOver()) {
            gameLogic.finishGame();
            tableUI.update(gameLogic.getTableCards());
            Player winner = gameLogic.getWinner();
            if (winner != null) {
                showWinner(winner.getName());
            }
        }
    }

    // Simple AI turn logic
    private void playAITurns() {
        for (int i = 1; i < gameLogic.getPlayers().size(); i++) {
            Player ai = gameLogic.getPlayers().get(i);
            if (!ai.getHand().isEmpty()) {
                gameLogic.playCard(ai, ai.getHand().get(0)); // AI plays first card
            }
        }
        refreshUI();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void showWinner(String winnerName) {
        Label winnerLabel = new Label("Winner: " + winnerName + "!", skin);
        winnerLabel.setFontScale(2f);

        // Place in center
        winnerLabel.setPosition(stage.getWidth() / 2f - winnerLabel.getWidth() / 2f,
                stage.getHeight() / 2f);

        stage.addActor(winnerLabel);

        // Simple animation: fade in + scale bounce
        winnerLabel.getColor().a = 0;
        winnerLabel.addAction(
                Actions.sequence(
                        Actions.fadeIn(1f),
                        Actions.forever(
                                Actions.sequence(
                                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                                        Actions.scaleTo(1f, 1f, 0.5f)
                                )
                        )
                )
        );

        // Show replay button separately
        showReplayButton();
    }

    private void showReplayButton() {
        TextButton replayBtn = new TextButton("Replay", skin);

        replayBtn.setPosition(stage.getWidth() / 2f - replayBtn.getWidth() / 2f,
                stage.getHeight() / 2f - 100);

        replayBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetGame();
            }
        });

        stage.addActor(replayBtn);
    }

    private void resetGame() {
        // Dispose old stage before reinitializing
        stage.dispose();

        // Recreate stage & UI
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Call your existing create() to rebuild everything
        create();
    }



}
