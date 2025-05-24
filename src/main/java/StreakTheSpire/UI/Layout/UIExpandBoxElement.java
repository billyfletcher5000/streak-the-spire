package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.UIElement;
import com.badlogic.gdx.math.Vector2;

public class UIExpandBoxElement extends UILayoutBoxElement {
    public UIExpandBoxElement() { super(); }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        for(UIElement child : getChildren()) {
            Vector2 newDimensions = dimensions;

            if(shouldPreserveAspectRatio()) {
                Vector2 childDimensions = child.getDimensions();
                float previousAspectRatio = childDimensions.x / childDimensions.y;
                if(previousAspectRatio < 1.0f)
                    newDimensions = new Vector2(newDimensions.x, newDimensions.y * previousAspectRatio);
                else
                    newDimensions = new Vector2(newDimensions.x * previousAspectRatio, newDimensions.y);
            }

            child.setDimensions(newDimensions);
        }
    }
}
