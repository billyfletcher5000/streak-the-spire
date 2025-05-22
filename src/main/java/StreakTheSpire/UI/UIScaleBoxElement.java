package StreakTheSpire.UI;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.math.Vector2;

public class UIScaleBoxElement extends UIElement {
    private Property<Vector2> baseDimensions = new Property<>(null);

    public Vector2 getBaseDimensions() { return baseDimensions.get(); }
    public Property<Vector2> getBaseDimensionsProperty() { return baseDimensions; }
    public void setBaseDimensions(Vector2 baseDimensions) { this.baseDimensions.set(baseDimensions); }

    public UIScaleBoxElement(Vector2 dimensions) {
        super();
        baseDimensions.set(dimensions.cpy());
        setDimensions(dimensions);
    }

    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        Vector2 baseDimensions = getBaseDimensions();
        for(UIElement child : getChildren()) {
            child.setLocalScale(new Vector2(dimensions.x / baseDimensions.x, dimensions.y / baseDimensions.y));
        }
    }
}
