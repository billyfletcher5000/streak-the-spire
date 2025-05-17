package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class UIGridContainerElement extends UIElement {
    // TODO: Investigate an appropriate way to do layouting properly, it's too complicated for this mod's scope though really
    private boolean expandToParent = true;
    private Padding expandPadding = new Padding(10, 10, 10, 10);

    private static final float VERTICAL_ASPECT_RATIO = 1.5f;
    private static final float HORIZONTAL_ASPECT_RATIO = 0.5f;

    private static enum PackingMode {
        Rectangular,
        Vertical,
        Horizontal
    }

    public UIGridContainerElement() {
        getDimensionsProperty().addOnChangedSubscriber(new Property.ValueChangedSubscriber() {
            @Override
            public void onValueChanged() {
                onParentResize();
            }
        });
    }

    private void onParentResize() {
        updateLayout();
    }

    @Override
    public void addChild(UIElement child) {
        super.addChild(child);
        updateLayout();
    }

    @Override
    public void removeChild(UIElement child) {
        super.removeChild(child);
        updateLayout();
    }

    private void updateLayout() {
        // Update expand position
        Vector2 parentDimensions = getDimensions();
        Vector2 dimensions = new Vector2(parentDimensions.x - (expandPadding.left + expandPadding.right), parentDimensions.y - (expandPadding.up + expandPadding.down));
        this.setDimensions(dimensions);
        this.setLocalPosition(new Vector2(expandPadding.right - expandPadding.left, expandPadding.up - expandPadding.down));

        Rectangle gridRect = new Rectangle(expandPadding.left, expandPadding.down, dimensions.x, dimensions.y);

        UIElement[] children = getChildren();
        int numChildren = children.length;

        float aspectRatio = (float) gridRect.width / gridRect.height;
        PackingMode packingMode = getPackingMode(aspectRatio);

        float elementSize = 0f;
        int gridWidth = 0;
        int gridHeight = 0;

        switch (packingMode) {
            case Rectangular:
                float maxSize = 0f;
                int chosenWidth = 0; int chosenHeight = 0;
                for(int w = 1; w < numChildren; w++) {
                    for(int h = 1; h * w < numChildren; h++) {
                        float testMaxSize = Math.min(gridRect.width / w, gridRect.height / h);
                        if (testMaxSize > maxSize) {
                            maxSize = testMaxSize;
                            chosenWidth = w;
                            chosenHeight = h;
                        }
                    }
                }

                elementSize = maxSize;
                gridWidth = chosenWidth;
                gridHeight = chosenHeight;
                break;
            case Vertical:
                gridWidth = 1;
                gridHeight = numChildren;
                elementSize = gridRect.width;
                break;
            case Horizontal:
                gridWidth = numChildren;
                gridHeight = 1;
                elementSize = gridRect.height;
                break;
        }

        Vector2 position = new Vector2();
        Vector2 childDimensions = new Vector2(elementSize, elementSize);
        for(int w = 0; w < gridWidth; w++) {
            for(int h = 0; h < gridHeight; h++) {
                UIElement child = children[w * gridHeight + h];
                position.x = (gridRect.width * -0.5f) + (w * elementSize);
                position.y = (gridRect.height * -0.5f) + (h * elementSize);
                child.setLocalPosition(position);
                child.setDimensions(childDimensions);
            }
        }
    }

    private PackingMode getPackingMode(float aspectRatio) {
        if(aspectRatio > VERTICAL_ASPECT_RATIO)
            return PackingMode.Vertical;
        if(aspectRatio < HORIZONTAL_ASPECT_RATIO)
            return PackingMode.Horizontal;

        return PackingMode.Rectangular;
    }
}
