package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.kolobkevic.libgdxgame.*;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Music music;
    private Sound sound;
    private MyInputProcessor myInputProcessor;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;
    private TiledMap baseMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int[] front, tL;
    private final Hero hero;
    private final MyAnimation coinAnim;

    public static List<Body> bodyToDelete;

    public GameScreen(Game game) {
        this.game = game;
        bodyToDelete = new ArrayList<>();

        coinAnim = new MyAnimation("MonedaD.png", 1, 5, 12f, Animation.PlayMode.LOOP);

        baseMap = new TmxMapLoader().load("map/BaseMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(baseMap);

        front = new int[1];
        front[0] = baseMap.getLayers().getIndex("front");
        tL = new int[1];
        tL[0] = baseMap.getLayers().getIndex("t0");

        physX = new PhysX();

        Array<RectangleMapObject> rectObjects = baseMap.getLayers().get("ObjectsStatic").getObjects().getByType(RectangleMapObject.class);
        rectObjects.addAll(baseMap.getLayers().get("ObjectsDynamic").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < rectObjects.size; i++) {
            physX.addObject(rectObjects.get(i));
        }

        body = physX.addObject((RectangleMapObject) baseMap.getLayers().get("hero").getObjects().get("hero"));
        body.setFixedRotation(true);
        hero = new Hero(body);

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("music//Juhani-Junkala-Title-Screen.mp3"));
        music.setVolume(0.005f);
        music.setLooping(true);
        music.play();
        System.out.println(music.isPlaying());

        sound = Gdx.audio.newSound(Gdx.files.internal("Car_accelerating.mp3"));

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.zoom = 2f;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLUE);

        camera.position.x = body.getPosition().x * PhysX.PPM;
        camera.position.y = body.getPosition().y * PhysX.PPM;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(tL);

        hero.setTime(delta);
        Vector2 vector = myInputProcessor.getOutForce();
//        if (MyContactListener.cnt < 1){
//             vector.set(vector.x, 0);
//        }
//        Gdx.graphics.setTitle(""+vector+"  "+MyContactListener.cnt);

        physX.body1.applyForceToCenter(vector, true);
        body.setTransform(body.getPosition(), body.getAngle() + myInputProcessor.getAng());
        body.setLinearVelocity(physX.body1.getLinearVelocity().setAngleRad(body.getAngle()));
        hero.setFPS(body.getLinearVelocity(),true);

        Rectangle tmp = hero.getRect(camera);
        float bScale = 0.2f;
//        ((PolygonShape) body.getFixtureList().get(0).getShape()).setAsBox(tmp.getWidth()/2, tmp.getHeight()/2);
//        ((PolygonShape) body.getFixtureList().get(1).getShape()).setAsBox(tmp.getWidth()/3, tmp.getHeight()/10,
//                new Vector2(0, -tmp.height/2), 0);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Sprite sprite = new Sprite(hero.getFrame());
        sprite.setPosition(tmp.x, tmp.y);
        //sprite.setScale(1/PhysX.PPM/bScale);
        sprite.rotate(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.draw(batch);

//        batch.draw(hero.getFrame(), tmp.x, tmp.y, tmp.width * PhysX.PPM/bScale, tmp.height * PhysX.PPM/bScale);

        Array<Body> bodies = physX.getBodies("coins");
        coinAnim.setTime(delta);
        TextureRegion tr = coinAnim.draw();
        float dScale = 0.3f;
        for(Body b: bodies){
            float cX = b.getPosition().x * PhysX.PPM - tr.getRegionWidth() / 2f / dScale;
            float cY = b.getPosition().y * PhysX.PPM - tr.getRegionHeight() / 2f / dScale;
            float cH = tr.getRegionHeight() / PhysX.PPM / dScale;
            float cW = tr.getRegionWidth() / PhysX.PPM / dScale;
            ((PolygonShape) b.getFixtureList().get(0).getShape()).setAsBox(cW/2, cH/2);
            batch.draw(tr, cX, cY,cW * PhysX.PPM,cH * PhysX.PPM);
        }
        batch.end();

        mapRenderer.render(front);

        for (Body b:bodyToDelete) {
            physX.deleteBody(b);
        }
        bodyToDelete.clear();
        float dx = physX.body1.getTransform().getPosition().x;
        physX.step();
        physX.debugDraw(camera);
        dx = physX.body1.getTransform().getPosition().x - dx;
        physX.body1.setTransform(physX.body1.getPosition().x - dx, physX.body1.getPosition().y, physX.body1.getAngle());
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
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
        batch.dispose();
        music.dispose();
        sound.dispose();
        physX.dispose();
        baseMap.dispose();
        mapRenderer.dispose();
        hero.dispose();
        coinAnim.dispose();
        bodyToDelete.clear();
    }
}
