package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyAnimation {
    private Texture img;
    private Animation<TextureRegion> animation;
    private float time;

    public MyAnimation(String name, int row, int col, float fps, Animation.PlayMode playMode) {
        time = 0;
        img = new Texture(name);
        TextureRegion reg1 = new TextureRegion(img);
        TextureRegion[][] regions = reg1.split(img.getWidth() / col, img.getHeight() / row);
        TextureRegion[] tmp = new TextureRegion[regions.length*regions[0].length];
        int a = 0;
        for (TextureRegion[] region: regions){
            for (TextureRegion reg: region) {
                tmp[a++] = reg;
            }
        }
        animation = new Animation<>(1/fps, tmp);
        animation.setPlayMode(playMode);
    }

    public TextureRegion draw() {
        return animation.getKeyFrame(time);
    }

    public void setTime(float deltaTime) {
        time += deltaTime;
    }

    public void dispose(){
        this.img.dispose();
    }
}
