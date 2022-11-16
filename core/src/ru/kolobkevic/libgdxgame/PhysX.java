package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class PhysX {
    public final MyContactListener contactListener;
    private final World world;
    public final static float PPM = 100;
    private final Box2DDebugRenderer debugRenderer;
    public Body body1;

    public World getWorld() {
        return world;
    }

    public PhysX() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.setDrawVelocities(true);
        contactListener = new MyContactListener();
        world.setContactListener(contactListener);

        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        ChainShape shape = new ChainShape();
        Vector2[] ground = {
                new Vector2(1250, -250),
                new Vector2(1350, -250)};
        def.type = BodyDef.BodyType.StaticBody;
        shape.createChain(ground);
        def.position.set(0,0);
        fdef.shape = shape;
        fdef.friction = 5;
        fdef.restitution = 0;
        Body body;
        body = world.createBody(def);
        body.createFixture(fdef);
        body.setUserData("ground");
        body.createFixture(fdef).setUserData("ground");
        shape.dispose();

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(5f, 5f);
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(1300, -250+2.5f);
        fdef.shape = polygonShape;
        fdef.friction = 5;
        fdef.density = 0;
        fdef.restitution = 0;

        body1 = world.createBody(def);
        body1.createFixture(fdef);
        body1.setUserData("name");
        body1.createFixture(fdef).setUserData("name");
        polygonShape.dispose();
    }

    public Array<Body> getBodies(String name){
        Array<Body> tmp = new Array<>();
        world.getBodies(tmp);
        Iterator<Body> iterator = tmp.iterator();
        while (iterator.hasNext()){
            Body body = iterator.next();
            if (!body.getUserData().equals(name))
                iterator.remove();
        }
        return tmp;
    }

    public void debugDraw(@NotNull OrthographicCamera camera) {
        debugRenderer.render(world, camera.combined);
    }

    public void step() {
        world.step(1 / 60f, 3, 3);
    }

    public Body addObject(@NotNull RectangleMapObject rectangleMapObject) {
        Rectangle rectangle = rectangleMapObject.getRectangle();
        String type = (String) rectangleMapObject.getProperties().get("BodyType");
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        if (type.equals("StaticBody")) {
            def.type = BodyDef.BodyType.StaticBody;
        }
        if (type.equals("DynamicBody")) {
            def.type = BodyDef.BodyType.DynamicBody;
        }

        def.position.set((rectangle.x + rectangle.width / 2) / PPM, (rectangle.y + rectangle.height / 2) / PPM);
        def.gravityScale = 0; //(float) rectangleMapObject.getProperties().get("gravityScale");
        shape.setAsBox(rectangle.getWidth() / 2 / PPM, rectangle.getHeight() / 2 / PPM);
        fdef.shape = shape;
        fdef.friction = (float) rectangleMapObject.getProperties().get("friction");
        fdef.density = 1;
        fdef.restitution = (float) rectangleMapObject.getProperties().get("restitution");

        String name = "";
        if (rectangleMapObject.getName() != null) {
            name = rectangleMapObject.getName();
        }
        Body body;
        body = world.createBody(def);
        body.createFixture(fdef);
        body.setUserData(name);
        body.createFixture(fdef).setUserData(name);
        if(name.equals("hero")){
            shape.setAsBox(rectangle.getWidth() / 3 / PPM, rectangle.getHeight() / 10 / PPM, new Vector2(0, -rectangle.height/2/PPM), 0);
            body.createFixture(fdef).setUserData("legs");
            body.getFixtureList().get(1).setSensor(true);
        }
        shape.dispose();
        return body;
    }

    public void deleteBody(Body body){
        world.destroyBody(body);
    }

    public void dispose() {
        this.world.dispose();
        this.debugRenderer.dispose();
    }
}