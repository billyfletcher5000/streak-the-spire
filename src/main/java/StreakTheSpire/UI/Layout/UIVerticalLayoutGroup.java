package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.UIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class UIVerticalLayoutGroup extends UIElement {

    private float fixedItemHeight = 0f;
    public float getFixedItemHeight() { return fixedItemHeight; }
    public void setFixedItemHeight(float fixedItemHeight) { this.fixedItemHeight = fixedItemHeight; }

    // This is essentially meant to be X/Y but in our case it will always be 1/X
    @Override
    public float getPreferredAspectRatio() { return 1.0f / getChildren().length; }

    public UIVerticalLayoutGroup() {
        setDebugColor(Color.CORAL);
    }
    public UIVerticalLayoutGroup(float fixedItemHeight) {
        super();
        setFixedItemHeight(fixedItemHeight);
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

        float itemHeight = dimensions.y / children.length;
        if(fixedItemHeight > 0f)
            itemHeight = Math.min(itemHeight, fixedItemHeight);

        for(int i = 0; i < children.length; i++) {
            UIElement child = children[i];
            child.setDimensions(new Vector2(dimensions.x, itemHeight));
            child.setLocalPosition(new Vector2(0f, (itemHeight * (i + 1)) - (dimensions.y * 0.5f) - (itemHeight * 0.5f)));
        }
    }
}
