package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Map;

public class UIElement {
    public static final Vector2 VectorOne = new Vector2(1f, 1f);
    protected Vector2 localPosition = Vector2.Zero.cpy();
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
    public Vector2 getLocalScale() { return localScale.cpy(); }
    public void setLocalScale(Vector2 localScale) {
        this.localScale.set(localScale);
        StreakTheSpire.logger.info("Setting local scale to " + localScale);
    }
    public Vector2 getDimensions() { return dimensions.cpy(); }
    public void setDimensions(Vector2 dimensions) { this.dimensions.set(dimensions); }
    public int getLayer() { return layer; }
    public void setLayer(int layer) { this.layer = layer; }

    public Matrix3 getLocalTransform() {
        Matrix3 translation = new Matrix3();
        translation.setToTranslation(localPosition);
        Matrix3 scale = new Matrix3();
        scale.setToScaling(localScale);
        return translation.mul(scale);
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
}
