package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyAtlasAnimation {
    private TextureAtlas atlas;
    Animation<TextureAtlas.AtlasRegion> animation;
    private float time;

    public MyAtlasAnimation(String atlas, String name, float fps, Animation.PlayMode playMode) {
        time = 0;
        this.atlas = new TextureAtlas(atlas);
        animation = new Animation<>(1 / fps, this.atlas.findRegions(name));
        animation.setPlayMode(playMode);
    }

    public TextureRegion draw() {
        return animation.getKeyFrame(time);
    }

    public void setTime(float deltaTime) {
        time += deltaTime;
    }

    public void dispose() {
        atlas.dispose();
    }
}
