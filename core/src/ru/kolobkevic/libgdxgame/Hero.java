package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.kolobkevic.libgdxgame.enums.Actions;

import java.util.HashMap;

public class Hero {
    private HashMap<Actions, Animation<TextureRegion>> heroAssets;
    private final float FPS = 1 / 7f;
    private float time;
    public static boolean canJump;
    private Animation<TextureRegion> baseAnim;
    private boolean loop;
    private TextureAtlas atlas;
    private Body body;
    private Dir dir;
    public static float dScale = 2.8f;

    private enum Dir {LEFT, RIGHT};

    public Hero(Body body) {
        this.body = body;
        heroAssets = new HashMap<>();
        atlas = new TextureAtlas("atlas/MyAtlas");
        heroAssets.put(Actions.RUN, new Animation<TextureRegion>(FPS, atlas.findRegions("run")));
        heroAssets.put(Actions.STAND, new Animation<TextureRegion>(FPS, atlas.findRegions("stand")));
        heroAssets.put(Actions.JUMP, new Animation<TextureRegion>(FPS, atlas.findRegions("jump")));
        heroAssets.put(Actions.DEATH, new Animation<TextureRegion>(FPS, atlas.findRegions("death")));
        baseAnim = heroAssets.get(Actions.STAND);
        loop = true;
        dir = Dir.RIGHT;
    }

    public static void setCanJump(boolean isJump) {
        canJump = isJump;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setFPS(Vector2 vector, boolean onGround) {
        if (vector.x > 0.1f) {
            setDir(Dir.RIGHT);
        }
        if (vector.x < -0.1f) {
            setDir(Dir.LEFT);
        }
        float tmp = (float) (Math.sqrt(vector.x * vector.x + vector.y * vector.y) * 10);
        setState(Actions.STAND);
        if (Math.abs(vector.x) > 0.25f && Math.abs(vector.y) < 10 && onGround) {
            setState(Actions.RUN);
            baseAnim.setFrameDuration(1 / tmp);
        }
        if (Math.abs(vector.y) > 1 && canJump) {
            setState(Actions.JUMP);
            baseAnim.setFrameDuration(FPS);
        }
    }

    public float setTime(float deltaTime) {
        time += deltaTime;
        return time;
    }

    private void setState(Actions state) {
        baseAnim = heroAssets.get(state);
        switch (state) {
            case STAND:
                loop = true;
                baseAnim.setFrameDuration(FPS);
                break;
            case JUMP:
                loop = false;
                break;
            default:
                loop = true;
        }
    }

    public TextureRegion getFrame() {
        if (time > baseAnim.getAnimationDuration() && loop) {
            time = 0;
        }
        if (time > baseAnim.getAnimationDuration()) {
            time = 0;
        }
        TextureRegion tr = baseAnim.getKeyFrame(time);
        if (!tr.isFlipX() && dir == Dir.LEFT) {
            tr.flip(true, false);
        }
        if (tr.isFlipX() && dir == Dir.RIGHT) {
            tr.flip(true, false);
        }
        return tr;
    }

    public Rectangle getRect(OrthographicCamera camera) {
        TextureRegion tr = baseAnim.getKeyFrame(time);
        float cX = body.getPosition().x * PhysX.PPM - tr.getRegionWidth() / 2f / dScale;
        float cY = body.getPosition().y * PhysX.PPM - tr.getRegionHeight() / 2f / dScale;
        float cH = tr.getRegionHeight() / PhysX.PPM / dScale;
        float cW = tr.getRegionWidth() / PhysX.PPM / dScale;
        return new Rectangle(cX, cY, cW, cH);
    }

    public void dispose() {
        atlas.dispose();
        this.heroAssets.clear();
    }
}
