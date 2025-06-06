package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Lifetime.IDestroyable;
import StreakTheSpire.Utils.Lifetime.LifetimeManager;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import dorkbox.tweenEngine.TweenAccessor;

import java.util.*;

public class UIElement implements TweenAccessor<UIElement>, IDestroyable {
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

    private final Property<Vector2> localPosition = new Property<>(Vector2.Zero.cpy());
    private final Property<Float> localRotation = new Property<>(0f); //degrees
    private final Property<Vector2> localScale = new Property<>(VectorOne.cpy());
    private final Property<Vector2> dimensions = new Property<>(Vector2.Zero.cpy());
    private final Property<Float> localAlpha = new Property<>(1.0f); // Elements have their own alpha to allow hierarchical alpha without affecting individual element color alpha choice
    private final Property<Integer> layer = new Property<>(0);
    private final Property<Boolean> visible = new Property<>(true);
    private final Property<UIElement> parent = new Property<>(null);
    private final PropertyList<UIElement> children = new PropertyList<UIElement>();

    private final Property<Color> debugColor = new Property<>(Color.GREEN);

    protected Affine2 localTransform = new Affine2();
    protected Affine2 localToWorldTransform = new Affine2();
    protected boolean localTransformDirty = true;
    protected boolean worldTransformDirty = true;

    private UIDebugDimensionsDisplay debugDimensionsElement = null;

    public UIElement getParent() { return parent.get(); }
    public Property<UIElement> getParentProperty() { return parent; }
    public void setParent(UIElement parent) {
        if(this.parent.get() != parent) {
            this.parent.set(parent);
            invalidateWorldTransform();
        }
    }

    public UIElement[] getChildren() { return children.toArray(new UIElement[children.size()]); }
    public PropertyList<UIElement> getChildrenPropertyList() { return children; }

    public void addChild(UIElement child) {
        if(child == null) {
            StreakTheSpire.logWarning("Attempted to add null child");
            return;
        }
        if(!children.contains(child))
            children.add(child);

        if(child.parent.get() == this)
            return;

        if(child.parent.get() != null)
            child.parent.get().removeChild(child);

        child.setParent(this);
    }

    public void removeChild(UIElement child) {
        if(children.contains(child))
            children.remove(child);

        if(child.parent.get() == this)
            child.setParent(null);
    }


    public Vector2 getLocalPosition() { return localPosition.get().cpy(); }
    public Property<Vector2> getLocalPositionProperty() { return localPosition; }
    public void setLocalPosition(Vector2 localPosition) {
        if(!this.localPosition.get().epsilonEquals(localPosition, Epsilon)) {
            this.localPosition.set(localPosition.cpy());
            invalidateLocalTransform();
            invalidateWorldTransform();
        }
    }

    public float getLocalRotation() { return localRotation.get(); }
    public Property<Float> getLocalRotationProperty() { return localRotation; }
    public void setLocalRotation(float localRotation) {
        if(Math.abs(this.localRotation.get() - localRotation) > Epsilon) {
            this.localRotation.set(localRotation);
            invalidateLocalTransform();
            invalidateWorldTransform();
        }
    }

    public Vector2 getLocalScale() { return localScale.get().cpy(); }
    public Property<Vector2> getLocalScaleProperty() { return localScale; }
    public void setLocalScale(Vector2 localScale) {
        if(!this.localScale.get().epsilonEquals(localScale, Epsilon)) {
            this.localScale.set(localScale.cpy());
            invalidateLocalTransform();
            invalidateWorldTransform();
        }
    }

    public Vector2 getDimensions() { return dimensions.get().cpy(); }
    public Property<Vector2> getDimensionsProperty() { return dimensions; }
    public void setDimensions(Vector2 dimensions) { this.dimensions.set(dimensions.cpy()); }

    public float getAlpha() { return localAlpha.get(); }
    public Property<Float> getAlphaProperty() { return localAlpha; }
    public void setAlpha(float alpha) { this.localAlpha.set(alpha); }

    public int getLayer() { return layer.get(); }
    public Property<Integer> getLayerProperty() { return layer; }
    public void setLayer(int layer) { this.layer.set(layer); }

    public boolean isVisible() { return visible.get(); }
    public Property<Boolean> getVisibleProperty() { return visible; }
    public void setVisible(boolean visible) { this.visible.set(visible); }
    public void setVisibleRecursive(boolean visible) {
        setVisible(visible);
        for(UIElement child : children)
            child.setVisibleRecursive(visible);
    }

    public Color getDebugColor() { return debugColor.get(); }
    public Property<Color> getDebugColorProperty() { return debugColor; }
    public void setDebugColor(Color color) { this.debugColor.set(color); }

    public float getPreferredAspectRatio() { return 1.0f; }

    public Affine2 getLocalTransform() {
        if(localTransformDirty) {
            localTransform.setToTrnRotScl(localPosition.get(), localRotation.get(), localScale.get());
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
                element = element.getParent();
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
        render(identity, spriteBatch, localAlpha.get());
    }

    public final void render(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        Affine2 newTransformationStack = new Affine2(transformationStack);
        newTransformationStack.mul(getLocalTransform());
        transformedAlpha *= getAlpha();
        if(isVisible()) {
            elementPreRender(newTransformationStack, spriteBatch, transformedAlpha);
            elementRender(newTransformationStack, spriteBatch, transformedAlpha);
            elementPostRender(newTransformationStack, spriteBatch, transformedAlpha);
        }
        children.sort(Comparator.comparingInt(UIElement::getLayer));
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

    protected void elementUpdate(float deltaTime) {
        if(debugDimensionsElement != null) {
            debugDimensionsElement.setDimensions(getDimensions());
        }
    }

    public final void destroy() {
        destroy(false);
    }

    public final void destroy(boolean destroyChildren) {
        if(destroyChildren) {
            for (UIElement element : children) {
                element.destroy();
            }
        }

        LifetimeManager.EnqueueDestroy(this);
    }

    protected void elementDestroy() {
        hideDebugDimensionsDisplay(false);
    }

    // There's probably better ways of doing this involving inheritance or something but this is meant to emulate C++'s friend/C#'s internal
    public void onDestroy() {
        elementDestroy();
        if(parent.get() != null)
            parent.get().removeChild(this);
    }

    protected void invalidateLocalTransform() {
        localTransformDirty = true;
    }

    protected void invalidateWorldTransform() {
        worldTransformDirty = true;
        for(UIElement child : children)
            child.invalidateWorldTransform();
    }

    public void showDebugDimensionsDisplay(boolean recursive) {
        if(debugDimensionsElement == null) {
            debugDimensionsElement = StreakTheSpire.get().createDebugDimensionsDisplay(this);
        }

        if(recursive) {
            for (UIElement child : children) {
                child.showDebugDimensionsDisplay(true);
            }
        }
    }

    public void hideDebugDimensionsDisplay(boolean recursive) {
        if(debugDimensionsElement != null)
            StreakTheSpire.get().removeDebugDimensionsDisplay(debugDimensionsElement);

        if(recursive) {
            for (UIElement child : children) {
                child.hideDebugDimensionsDisplay(true);
            }
        }
    }

    @Override
    public int getValues(UIElement target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case TweenTypes.POSITION_XY:
                returnValues[0] = target.localPosition.get().x;
                returnValues[1] = target.localPosition.get().y;
                return 2;

            case TweenTypes.POSITION_X:
                returnValues[0] = target.localPosition.get().x;
                return 1;

            case TweenTypes.POSITION_Y:
                returnValues[0] = target.localPosition.get().y;
                return 1;

            case TweenTypes.ROTATION:
                returnValues[0] = target.localRotation.get();
                return 1;

            case TweenTypes.SCALE_XY:
                returnValues[0] = target.localScale.get().x;
                returnValues[1] = target.localScale.get().y;
                return 2;

            case TweenTypes.SCALE_X:
                returnValues[0] = target.localScale.get().x;
                return 1;

            case TweenTypes.SCALE_Y:
                returnValues[0] = target.localScale.get().y;
                return 1;

            case TweenTypes.ALPHA:
                returnValues[0] = target.localAlpha.get();
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
                target.setLocalPosition(new Vector2(newValues[0], target.localPosition.get().y));
                break;

            case TweenTypes.POSITION_Y:
                target.setLocalPosition(new Vector2(target.localPosition.get().x, newValues[0]));
                break;

            case TweenTypes.ROTATION:
                target.setLocalRotation(newValues[0]);
                break;

            case TweenTypes.SCALE_XY:
                target.setLocalScale(new Vector2(newValues[0], newValues[1]));
                break;

            case TweenTypes.SCALE_X:
                target.setLocalScale(new Vector2(newValues[0], target.localScale.get().y));
                break;

            case TweenTypes.SCALE_Y:
                target.setLocalScale(new Vector2(target.localScale.get().x, newValues[0]));
                break;

            case TweenTypes.ALPHA:
                target.setAlpha(newValues[0]);
                break;
        }
    }
}
