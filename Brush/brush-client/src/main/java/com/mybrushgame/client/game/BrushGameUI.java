package com.mybrushgame.client.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mybrushgame.client.cards.Card;
import com.mybrushgame.client.cards.Player;
import com.mybrushgame.client.ui.*;

import java.util.ArrayList;
import java.util.List;

public class BrushGameUI implements Screen {

    private Stage stage;
    private Skin skin;

    private BrushGame gameLogic;
    private Player humanPlayer;

    // Modular UI components
    private ScoreUI scoreUI;
    private TableUI tableUI;
    private HandUI handUI;
    private PlayerSeatUI seatUI;
    private final Game game;
    private final String mode;

    public BrushGameUI(Game game, String mode) {
        this.game = game;
        this.mode = mode;
    }
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize game logic
        gameLogic = new BrushGame();
        gameLogic.startGame();

        humanPlayer = gameLogic.getPlayers().get(0);
        Player leftPlayer = gameLogic.getPlayers().get(1);
        Player rightPlayer = gameLogic.getPlayers().get(2);

        // Initialize modular UI
        scoreUI = new ScoreUI(skin, gameLogic.getPlayers());
        tableUI = new TableUI();
        handUI = new HandUI(humanPlayer, gameLogic, tableUI, this::refreshUI);
        seatUI = new PlayerSeatUI(skin, leftPlayer, rightPlayer);

        // Layout table
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        mainTable.top().add(scoreUI.getTable()).expandX().row();
        mainTable.center().add(tableUI.getTable()).expand().row();
        mainTable.bottom().add(handUI.getTable()).expandX();

        stage.addActor(mainTable);
        stage.addActor(seatUI.getTable());
        seatUI.update();

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
            handUI.update();
        }

        if (gameLogic.isGameOver()) {
            gameLogic.finishGame();
            tableUI.update(gameLogic.getTableCards());
            Player winner = gameLogic.getWinner();
            if (winner != null) {
                scoreUI.update(gameLogic.getPlayers());
                showWinner(winner.getName());
            }
        }
    }

    // Simple AI turn logic
    private void playAITurns() {
        for (int i = 1; i < gameLogic.getPlayers().size(); i++) {
            Player ai = gameLogic.getPlayers().get(i);
            if (!ai.getHand().isEmpty()) {
                Card cardToPlay = ai.getHand().get(0);
                List<Card> selectedForAI = new ArrayList<>();
                selectedForAI.add(cardToPlay);
                gameLogic.playCard(ai, cardToPlay, selectedForAI);
                List<Card> collected = new ArrayList<>(gameLogic.getLastCollectedCards());
                animateAICards(ai, cardToPlay, collected);
            }
        }
        refreshUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void showWinner(String winnerName) {
        Player player = null;
        for (Player p : gameLogic.getPlayers()) {
            if (winnerName.equals(p.getName())) {
                player = p;
            }
        }
        int points = player.calculatePoints() + player.getBrushes();
        Label winnerLabel = new Label("Winner: " + winnerName + "! With " + points + " points!", skin);
        winnerLabel.setFontScale(2f);

        winnerLabel.setPosition(stage.getWidth() / 2f - winnerLabel.getWidth() / 2f,
                stage.getHeight() / 2f);

        stage.addActor(winnerLabel);

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

        showReplayButton();
        showHomeButton();
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

    private void showHomeButton() {
        TextButton homeBtn = new TextButton("Home", skin);

        homeBtn.setPosition(stage.getWidth() / 2f - homeBtn.getWidth() / 2f,
                stage.getHeight() / 2.2f - 100);

        homeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnHome();
            }
        });

        stage.addActor(homeBtn);
    }

    private void animateAICards(Player ai, Card aiCard, List<Card> collectedCards) {
        Stage stage = this.stage;
        if (stage == null) return;

        // AI hand position
        float aiX = (ai == gameLogic.getPlayers().get(1)) ? 100 : stage.getWidth() - 200;
        float aiY = stage.getHeight() - 120;

        // Table center
        Table tableParent = tableUI.getTable();
        float centerX = tableParent.getX() + tableParent.getWidth() / 2f;
        float centerY = tableParent.getY() + tableParent.getHeight() / 2f;

        // Build animation list: always include AI card, then collected table cards
        List<Card> animCards = new ArrayList<>();
        animCards.add(aiCard);
        for (Card c : collectedCards) {
            if (!c.equals(aiCard)) animCards.add(c);
        }

        int n = animCards.size();
        float spacing = 30f;

        for (int i = 0; i < n; i++) {
            Card card = animCards.get(i);
            Texture texture = new Texture("cards/" + card.getImageName());
            Image img = new Image(texture);
            img.setSize(120, 140);
            img.setPosition(aiX, aiY);
            stage.addActor(img);

            float targetX = centerX - ((n - 1) * spacing) / 2f + i * spacing;
            float targetY = centerY;

            boolean moveToAIHand = !collectedCards.isEmpty(); // only move back if AI captured

            if (moveToAIHand) {
                img.addAction(Actions.sequence(
                        Actions.moveTo(targetX, targetY, 0.5f),
                        Actions.delay(1f),
                        Actions.moveTo(aiX, aiY, 0.5f),
                        Actions.run(img::remove)
                ));
            } else {
                img.addAction(Actions.sequence(
                        Actions.moveTo(targetX, targetY, 0.5f),
                        Actions.run(img::remove)
                ));
            }
        }
    }

    private void resetGame() {
        game.setScreen(new BrushGameUI(game, mode));
        // rebuild everything
    }

    private void returnHome() {
        game.setScreen(new HomeScreenUI(game));
    }
}
