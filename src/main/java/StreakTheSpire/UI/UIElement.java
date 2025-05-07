package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import dorkbox.tweenEngine.TweenAccessor;

import java.util.*;

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
    public static final float Epsilon = 0.000000000001f;
    private Vector2 localPosition = Vector2.Zero.cpy();
    private float localRotation = 0f; //degrees
    private Vector2 localScale = VectorOne.cpy();
    private Vector2 dimensions = Vector2.Zero.cpy();
    protected int layer = 0;
    protected UIElement parent = null;
    protected ArrayList<UIElement> children = new ArrayList<UIElement>();

    protected Matrix3 localTransform;
    protected Matrix3 localToWorldTransform;
    protected boolean localTransformDirty = true;
    protected boolean worldTransformDirty = true;

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
    public float getLocalRotation() { return localRotation; }
    public Vector2 getLocalScale() { return localScale.cpy(); }
    public Vector2 getDimensions() { return dimensions.cpy(); }

    public void setLocalPosition(Vector2 localPosition) {
        if(!this.localPosition.epsilonEquals(localPosition, Epsilon)) {
            this.localPosition.set(localPosition);
            invalidateLocalTransform();
            invalidateWorldTransform();
        }
    }

    public void setLocalRotation(float localRotation) {
        if(Math.abs(this.localRotation - localRotation) > Epsilon) {
            this.localRotation = localRotation;
            invalidateLocalTransform();
            invalidateWorldTransform();
        }
    }

    public void setLocalScale(Vector2 localScale) {
        if(!this.localScale.epsilonEquals(localScale, Epsilon)) {
            this.localScale.set(localScale);
            invalidateLocalTransform();
            invalidateWorldTransform();
        }
    }

    public void setDimensions(Vector2 dimensions) { this.dimensions.set(dimensions); }
    public int getLayer() { return layer; }
    public void setLayer(int layer) { this.layer = layer; }

    public Matrix3 getLocalTransform() {
        if(localTransformDirty) {
            localTransform = new Matrix3();
            localTransform.setToTranslation(localPosition);
            Matrix3 rotation = new Matrix3();
            rotation.setToRotation(-localRotation); // Matrix3.setToRotation appears to be the wrong way around and goes counter-clockwise
            Matrix3 scale = new Matrix3();
            scale.setToScaling(localScale);
            localTransform.mul(rotation).mul(scale);

            localTransformDirty = false;
        }

        return localTransform;
    }

    public Matrix3 getLocalToWorldTransform() {
        if(worldTransformDirty) {
            ArrayList<Matrix3> transforms = new ArrayList<>();
            UIElement element = this;
            while (element != null) {
                transforms.add(element.getLocalTransform());
                element = element.parent;
            }

            localToWorldTransform = new Matrix3().idt();
            for (int i = transforms.size() - 1; i >= 0; i--) {
                Matrix3 matrix = transforms.get(i);
                localToWorldTransform.mul(matrix);
            }
        }

        return localToWorldTransform;
    }

    public Matrix3 getWorldToLocalTransform() {
        Matrix3 localToWorld = getLocalToWorldTransform();
        return localToWorld.inv();
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

    protected void invalidateLocalTransform() {
        localTransformDirty = true;
    }

    protected void invalidateWorldTransform() {
        worldTransformDirty = true;
        for(UIElement child : children)
            child.invalidateWorldTransform();
    }

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
                target.setLocalPosition(new Vector2(newValues[0], newValues[1]));
                break;

            case TweenTypes.POSITION_X:
                target.setLocalPosition(new Vector2(newValues[0], target.localPosition.y));
                break;

            case TweenTypes.POSITION_Y:
                target.setLocalPosition(new Vector2(target.localPosition.x, newValues[0]));
                break;

            case TweenTypes.ROTATION:
                target.setLocalRotation(newValues[0]);
                break;

            case TweenTypes.SCALE_XY:
                target.setLocalScale(new Vector2(newValues[0], newValues[1]));
                break;

            case TweenTypes.SCALE_X:
                target.setLocalScale(new Vector2(newValues[0], target.localScale.y));
                break;

            case TweenTypes.SCALE_Y:
                target.setLocalScale(new Vector2(target.localScale.x, newValues[0]));
                break;
        }
    }
}
