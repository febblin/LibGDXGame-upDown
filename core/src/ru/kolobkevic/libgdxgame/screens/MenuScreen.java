package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen implements Screen {
    private Game game;
    private Texture background, sign;
    private SpriteBatch spriteBatch;
    private Rectangle rectangle;
    int x, y;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public MenuScreen(Game game) {
        this.game = game;
        background = new Texture("backgrounds/City.png");
        sign = new Texture("start.png");
        x = Gdx.graphics.getWidth() / 2 - sign.getWidth() / 2;
        y = Gdx.graphics.getHeight() / 2 - sign.getHeight() / 2;
        rectangle = new Rectangle(x, y, sign.getWidth(), sign.getHeight());
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.draw(sign, x, y);
        spriteBatch.end();
        if (Gdx.input.isTouched()) {
            if (rectangle.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                dispose();
                game.setScreen(new GameScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.background.dispose();
        this.sign.dispose();
        this.spriteBatch.dispose();
    }
}
