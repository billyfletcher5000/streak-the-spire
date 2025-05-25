package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.UIElement;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class UIScaleBoxElement extends UILayoutBoxElement {
    private Property<Vector2> baseDimensions = new Property<>(null);

    public Vector2 getBaseDimensions() { return baseDimensions.get(); }
    public Property<Vector2> getBaseDimensionsProperty() { return baseDimensions; }
    public void setBaseDimensions(Vector2 baseDimensions) { this.baseDimensions.set(baseDimensions); }

    public UIScaleBoxElement(Vector2 dimensions) {
        super();
        baseDimensions.set(dimensions.cpy());
        setDimensions(dimensions);

        setDebugColor(Color.BLUE);
    }

    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        Vector2 baseDimensions = getBaseDimensions();
        for(UIElement child : getChildren()) {
            Vector2 adjustedScale = new Vector2(dimensions.x / baseDimensions.x, dimensions.y / baseDimensions.y);

            if(shouldPreserveAspectRatio()) {
                Vector2 childDimensions = child.getDimensions();
                float previousAspectRatio = childDimensions.x / childDimensions.y;
                if(previousAspectRatio > 1.0f)
                    adjustedScale = new Vector2(adjustedScale.x, adjustedScale.x);
                else
                    adjustedScale = new Vector2(adjustedScale.y, adjustedScale.y);
            }

            child.setLocalScale(adjustedScale);
        }
    }
}
