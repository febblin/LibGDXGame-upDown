package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.physics.box2d.*;
import ru.kolobkevic.libgdxgame.screens.GameScreen;

public class MyContactListener implements ContactListener {
    public static int cnt = 0;
    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (a.getUserData() != null && b.getUserData() != null) {
            if (a.getUserData().equals("hero") && b.getUserData().equals("coins")) {
                GameScreen.bodyToDelete.add(b.getBody());
            }
            if (b.getUserData().equals("hero") && a.getUserData().equals("coins")) {
                GameScreen.bodyToDelete.add(a.getBody());
            }
            if (a.getUserData().equals("legs") && b.getUserData().equals("barrier")) {
                cnt++;
            }
            if (b.getUserData().equals("legs") && a.getUserData().equals("barrier")) {
                cnt++;
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (a.getUserData() != null && b.getUserData() != null) {
            if (a.getUserData().equals("legs") && b.getUserData().equals("barrier")) {
                cnt--;
            }
            if (b.getUserData().equals("legs") && a.getUserData().equals("barrier")) {
                cnt--;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
