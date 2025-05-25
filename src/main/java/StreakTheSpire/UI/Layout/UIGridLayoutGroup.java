package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.Padding;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class UIGridLayoutGroup extends UIElement {
    // TODO: Investigate an appropriate way to do layouting properly, it's too complicated for this mod's scope though really
    private Property<Padding> outerPadding = new Property<>(new Padding(10, 10, 10, 10));
    private Property<Float> gridElementPadding = new Property<>(4.0f);

    private static final float VERTICAL_ASPECT_RATIO = 1.5f;
    private static final float HORIZONTAL_ASPECT_RATIO = 0.5f;

    private boolean layoutDirty = true;

    private enum PackingMode {
        Rectangular,
        Vertical,
        Horizontal
    }

    public UIGridLayoutGroup() {
        setDebugColor(Color.CYAN);
    }

    public UIGridLayoutGroup(Padding outerPadding) {
        this();
        this.outerPadding.set(outerPadding.cpy());
    }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        layoutDirty = true;
    }

    @Override
    public void addChild(UIElement child) {
        super.addChild(child);
        layoutDirty = true;
    }

    @Override
    public void removeChild(UIElement child) {
        super.removeChild(child);
        layoutDirty = true;
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        if(layoutDirty) {
            updateLayout();
            layoutDirty = false;
        }
    }

    private void updateLayout() {
        // Update expand position
        Vector2 parentDimensions = getDimensions();
        if(parentDimensions.len2() < Epsilon)
            return;

        Padding outerPadding = this.outerPadding.get();
        Vector2 dimensions = new Vector2(parentDimensions.x - (outerPadding.left + outerPadding.right), parentDimensions.y - (outerPadding.up + outerPadding.down));
        this.setLocalPosition(new Vector2(outerPadding.right - outerPadding.left, outerPadding.up - outerPadding.down));

        Rectangle gridRect = new Rectangle(outerPadding.left, outerPadding.down, dimensions.x, dimensions.y);

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
                for(int w = 1; w <= numChildren; w++) {
                    int h = (int)Math.ceil((double) numChildren / w);

                    float testMaxSize = Math.min(gridRect.width / w, gridRect.height / h);
                    if (testMaxSize > maxSize) {
                        maxSize = testMaxSize;
                        chosenWidth = w;
                        chosenHeight = h;
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
        Vector2 sectionSizes = new Vector2(dimensions.x / gridWidth, dimensions.y / gridHeight);
        for(int w = 0; w < gridWidth; w++) {
            for(int h = 0; h < gridHeight; h++) {
                int index = h * gridWidth + w;
                if(index >= numChildren)
                    continue;

                UIElement child = children[index];
                position.x = (w * sectionSizes.x) - (dimensions.x * 0.5f) + (childDimensions.x * 0.5f) + ((sectionSizes.x - childDimensions.x) * 0.5f);
                position.y = (h * sectionSizes.y) - (dimensions.y * 0.5f) + (childDimensions.y * 0.5f) + ((sectionSizes.y - childDimensions.y) * 0.5f);
                child.setLocalPosition(position);
                child.setDimensions(childDimensions);
            }
        }
    }

    private PackingMode getPackingMode(float aspectRatio) {
        //if(aspectRatio > VERTICAL_ASPECT_RATIO)
        //    return PackingMode.Vertical;
        //if(aspectRatio < HORIZONTAL_ASPECT_RATIO)
        //    return PackingMode.Horizontal;

        return PackingMode.Rectangular;
    }
}
