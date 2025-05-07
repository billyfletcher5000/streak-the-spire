package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import dorkbox.tweenEngine.TweenAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class UIElement implements TweenAccessor<UIElement> {
    public static class TweenTypes {
        public static final int POSITION_XY = 0;
        public static final int POSITION_X = 1;
        public static final int POSITION_Y = 2;
        public static final int ROTATION = 3;
        public static final int SCALE_XY = 4;
        public static final int SCALE_X = 5;
        public static final int SCALE_Y = 6;
    }

    public static final Vector2 VectorOne = new Vector2(1f, 1f);
    protected Vector2 localPosition = Vector2.Zero.cpy();
    protected float localRotation = 0f; //degrees
    protected Vector2 localScale = VectorOne.cpy();
    protected Vector2 dimensions = Vector2.Zero.cpy();
    protected int layer = 0;
    protected UIElement parent = null;
    protected ArrayList<UIElement> children = new ArrayList<UIElement>();

    public UIElement getParent() { return parent; }
    public UIElement[] getChildren() { return children.toArray(new UIElement[children.size()]); }

    public void addChild(UIElement child) {
        if(!children.contains(child))
            children.add(child);

        if(child.parent == this)
            return;

        if(child.parent != null)
            child.parent.removeChild(child);

        child.parent = this;
    }

    public void removeChild(UIElement child) {
        if(children.contains(child))
            children.remove(child);

        if(child.parent == this)
            child.parent = null;
    }

    public final void update(float deltaTime) {
        elementUpdate(deltaTime);
        children.forEach(child -> child.update(deltaTime));
    }

    protected void elementUpdate(float deltaTime) {}

    public Vector2 getLocalPosition() { return localPosition.cpy(); }
    public void setLocalPosition(Vector2 localPosition) { this.localPosition.set(localPosition); }
    public float getLocalRotation() { return localRotation; }
    public void setLocalRotation(float localRotation) { this.localRotation = localRotation; }
    public Vector2 getLocalScale() { return localScale.cpy(); }
    public void setLocalScale(Vector2 localScale) { this.localScale.set(localScale); }
    public Vector2 getDimensions() { return dimensions.cpy(); }
    public void setDimensions(Vector2 dimensions) { this.dimensions.set(dimensions); }
    public int getLayer() { return layer; }
    public void setLayer(int layer) { this.layer = layer; }

    public Matrix3 getLocalTransform() {
        Matrix3 translation = new Matrix3();
        translation.setToTranslation(localPosition);
        Matrix3 rotation = new Matrix3();
        rotation.setToRotation(-localRotation); // Matrix3.setToRotation appears to be the wrong way around and goes counter-clockwise
        Matrix3 scale = new Matrix3();
        scale.setToScaling(localScale);
        return translation.mul(rotation).mul(scale);
    }

    public final void render(SpriteBatch spriteBatch) {
        Matrix3 identity = new Matrix3();
        identity.idt();
        render(identity, spriteBatch);
    }

    public final void render(Matrix3 transformationStack, SpriteBatch spriteBatch) {
        Matrix3 newTransformationStack = new Matrix3(transformationStack);
        newTransformationStack.mul(getLocalTransform());
        elementRender(newTransformationStack, spriteBatch);
        children.sort((elementA, elementB) -> elementA.layer < elementB.layer ? -1 : 1);
        children.forEach(child -> child.render(newTransformationStack, spriteBatch));
    }

    protected void elementRender(Matrix3 transformationMatrix, SpriteBatch spriteBatch) {}

    @Override
    public int getValues(UIElement target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case TweenTypes.POSITION_XY:
                returnValues[0] = target.localPosition.x;
                returnValues[1] = target.localPosition.y;
                return 2;

            case TweenTypes.POSITION_X:
                returnValues[0] = target.localPosition.x;
                return 1;

            case TweenTypes.POSITION_Y:
                returnValues[0] = target.localPosition.y;
                return 1;

            case TweenTypes.ROTATION:
                returnValues[0] = target.localRotation;
                return 1;

            case TweenTypes.SCALE_XY:
                returnValues[0] = target.localScale.x;
                returnValues[1] = target.localScale.y;
                return 2;

            case TweenTypes.SCALE_X:
                returnValues[0] = target.localScale.x;
                return 1;

            case TweenTypes.SCALE_Y:
                returnValues[0] = target.localScale.y;
                return 1;
        }

        return 0;
    }

    @Override
    public void setValues(UIElement target, int tweenType, float[] newValues) {

        switch (tweenType) {
            case TweenTypes.POSITION_XY:
                target.localPosition.x = newValues[0];
                target.localPosition.y = newValues[1];
                break;

            case TweenTypes.POSITION_X:
                target.localPosition.x = newValues[0];
                break;

            case TweenTypes.POSITION_Y:
                target.localPosition.y = newValues[0];
                break;

            case TweenTypes.ROTATION:
                target.localRotation = newValues[0];
                break;

            case TweenTypes.SCALE_XY:
                target.localScale.x = newValues[0];
                target.localScale.y = newValues[1];
                break;

            case TweenTypes.SCALE_X:
                target.localScale.x = newValues[0];
                break;

            case TweenTypes.SCALE_Y:
                target.localScale.y = newValues[0];
                break;
        }
    }
}
