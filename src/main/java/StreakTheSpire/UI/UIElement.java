package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
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
        public static final int ALPHA = 7;
    }

    public static final Vector2 VectorOne = new Vector2(1f, 1f);
    public static final float Epsilon = 0.000000000001f;
    private Vector2 localPosition = Vector2.Zero.cpy();
    private float localRotation = 0f; //degrees
    private Vector2 localScale = VectorOne.cpy();
    private Vector2 dimensions = Vector2.Zero.cpy();
    private float localAlpha = 1.0f; // Elements have their own alpha to allow hierarchical alpha without affecting individual element color alpha choice
    private int layer = 0;
    private UIElement parent = null;
    private ArrayList<UIElement> children = new ArrayList<UIElement>();

    protected Affine2 localTransform = new Affine2();
    protected Affine2 localToWorldTransform = new Affine2();
    protected boolean localTransformDirty = true;
    protected boolean worldTransformDirty = true;

    public UIElement getParent() { return parent; }
    public void setParent(UIElement parent) {
        if(this.parent != parent) {
            this.parent = parent;
            invalidateWorldTransform();
        }
    }

    public UIElement[] getChildren() { return children.toArray(new UIElement[children.size()]); }

    public void addChild(UIElement child) {
        if(!children.contains(child))
            children.add(child);

        if(child.parent == this)
            return;

        if(child.parent != null)
            child.parent.removeChild(child);

        child.setParent(this);
    }

    public void removeChild(UIElement child) {
        if(children.contains(child))
            children.remove(child);

        if(child.parent == this)
            child.setParent(null);
    }

    public Vector2 getLocalPosition() { return localPosition.cpy(); }
    public float getLocalRotation() { return localRotation; }
    public Vector2 getLocalScale() { return localScale.cpy(); }
    public Vector2 getDimensions() { return dimensions.cpy(); }
    public float getAlpha() { return localAlpha; }

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
    public void setAlpha(float alpha) { this.localAlpha = alpha; }
    public int getLayer() { return layer; }
    public void setLayer(int layer) { this.layer = layer; }

    public Affine2 getLocalTransform() {
        if(localTransformDirty) {
            localTransform.setToTrnRotScl(localPosition, localRotation, localScale);
            localTransformDirty = false;
        }

        return localTransform;
    }

    public Affine2 getLocalToWorldTransform() {
        if(worldTransformDirty) {
            ArrayList<Affine2> transforms = new ArrayList<>();
            UIElement element = this;
            while (element != null) {
                transforms.add(element.getLocalTransform());
                element = element.parent;
            }

            localToWorldTransform = new Affine2();
            for (int i = transforms.size() - 1; i >= 0; i--) {
                Affine2 matrix = transforms.get(i);
                localToWorldTransform.mul(matrix);
            }
        }

        return localToWorldTransform;
    }

    public Affine2 getWorldToLocalTransform() {
        Affine2 localToWorld = getLocalToWorldTransform();
        return localToWorld.inv();
    }

    public final void render(SpriteBatch spriteBatch) {
        Affine2 identity = new Affine2();
        identity.idt();
        render(identity, spriteBatch, localAlpha);
    }

    public final void render(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        Affine2 newTransformationStack = new Affine2(transformationStack);
        newTransformationStack.mul(getLocalTransform());
        transformedAlpha *= getAlpha();
        elementPreRender(newTransformationStack, spriteBatch, transformedAlpha);
        elementRender(newTransformationStack, spriteBatch, transformedAlpha);
        elementPostRender(newTransformationStack, spriteBatch, transformedAlpha);
        children.sort((elementA, elementB) -> elementA.layer < elementB.layer ? -1 : 1);
        float finalAlpha = transformedAlpha;
        children.forEach(child -> child.render(newTransformationStack, spriteBatch, finalAlpha));
    }

    protected void elementPreRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {}
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {}
    protected void elementPostRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {}

    public final void update(float deltaTime) {
        elementUpdate(deltaTime);
        children.forEach(child -> child.update(deltaTime));
    }

    protected void elementUpdate(float deltaTime) {}

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

            case TweenTypes.ALPHA:
                returnValues[0] = target.localAlpha;
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

            case TweenTypes.ALPHA:
                target.setAlpha(newValues[0]);
                break;
        }
    }
}
