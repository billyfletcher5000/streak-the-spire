package StreakTheSpire.UI;

import com.badlogic.gdx.math.Vector2;

public class UIExpandBoxElement extends UIElement {
    public UIExpandBoxElement() { super(); }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        for(UIElement child : getChildren()) {
            child.setDimensions(dimensions);
        }
    }
}
