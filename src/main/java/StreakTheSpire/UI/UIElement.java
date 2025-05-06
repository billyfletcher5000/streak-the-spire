package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Map;

public class UIElement {
    public static final Vector2 VectorOne = new Vector2(1f, 1f);
    protected Vector2 localPosition;
    protected Vector2 localScale;
    protected Vector2 dimensions = Vector2.Zero;
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

    public Vector2 getLocalPosition() { return localPosition; }
    public Vector2 getLocalScale() { return localScale; }

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
