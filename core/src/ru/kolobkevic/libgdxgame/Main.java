package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.Game;
import ru.kolobkevic.libgdxgame.screens.MenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
