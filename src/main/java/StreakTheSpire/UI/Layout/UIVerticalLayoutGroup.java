package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.UIElement;
import com.badlogic.gdx.math.Vector2;

public class UIVerticalLayoutGroup extends UIElement {

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        updateLayout();
    }

    private void updateLayout() {
        // TODO: Add support for specified preferred widths in both pixel and relative forms
        Vector2 dimensions = getDimensions();
        UIElement[] children = getChildren();

        float itemHeight = dimensions.y / children.length;

        for(int i = 0; i < children.length; i++) {
            UIElement child = children[i];
            child.setDimensions(new Vector2(dimensions.x, (itemHeight * i) - (dimensions.y / 2)));
            child.setLocalPosition(new Vector2(0, i * itemHeight));
        }
    }
}
