package StreakTheSpire.UI;

import com.badlogic.gdx.math.Vector2;

public class UIScaleBoxElement extends UIElement {
    private Vector2 baseDimensions;

    public UIScaleBoxElement(Vector2 dimensions) {
        super();
        baseDimensions = dimensions.cpy();
        setDimensions(dimensions);
    }

    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        for(UIElement child : getChildren()) {
            child.setLocalScale(new Vector2(dimensions.x / baseDimensions.x, dimensions.y / baseDimensions.y));
        }
    }
}
