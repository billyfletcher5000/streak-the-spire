package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.UIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class UIHorizontalLayoutGroup extends UIElement {

    private float fixedItemWidth = 0f;
    public float getFixedItemWidth() { return fixedItemWidth; }
    public void setFixedItemWidth(float fixedItemWidth) { this.fixedItemWidth = fixedItemWidth; }

    // This is essentially meant to be X/Y but in our case it will always be X/1
    @Override
    public float getPreferredAspectRatio() { return getChildren().length; }

    public UIHorizontalLayoutGroup() {
        setDebugColor(Color.PURPLE);
    }
    public UIHorizontalLayoutGroup(float fixedItemWidth) {
        super();
        setFixedItemWidth(fixedItemWidth);
    }

    @Override
    public void setDimensions(Vector2 dimensions) {
        Vector2 prevDimensions = getDimensions();
        super.setDimensions(dimensions);

        if(Math.abs(prevDimensions.x - getDimensions().x) > Epsilon)
            updateLayout();
        else
            updateLayout();
    }

    private void updateLayout() {
        // TODO: Add support for specified preferred widths in both pixel and relative forms
        Vector2 dimensions = getDimensions();
        UIElement[] children = getChildren();

        float itemWidth = dimensions.x / children.length;
        if(fixedItemWidth > 0f)
            itemWidth = Math.min(itemWidth, fixedItemWidth);

        for(int i = 0; i < children.length; i++) {
            UIElement child = children[i];
            child.setDimensions(new Vector2(itemWidth, dimensions.y));
            child.setLocalPosition(new Vector2((itemWidth * (i + 1)) - (dimensions.x * 0.5f) - (itemWidth * 0.5f), 0));
        }
    }
}
