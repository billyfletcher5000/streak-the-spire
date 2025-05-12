package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.HashMap;
import java.util.Map;

public class UIResizablePanel extends UINineSliceElement implements HitboxListener {
    private boolean resizeEnabled = false;
    private float hitboxWidth = 16.0f;
    private HashMap<UIElementHitbox, Integer> hitboxToDragDirection = new HashMap<>();
    private UIElementHitbox currentHitbox = null;
    private int currentDragDirection = DragDirectionFlags.NONE;
    private Vector2 currentOffset = new Vector2();

    // Intently disabled rotation for these, see UIElementHitbox comments
    // Note: This will not prevent setting rotation higher, but any rotation higher will cause issues with
    //       this element.
    @Override
    public void setLocalRotation(float localRotation) { }


    // TODO: Add on resize event
    // TODO: Add ability to move panel and also on move event

    private static class DragDirectionFlags {
        public static final int NONE = 0;
        public static final int UP = 1;
        public static final int DOWN = 2;
        public static final int LEFT = 4;
        public static final int RIGHT = 8;

        public static boolean isCardinal(int direction) {
            return direction == UP || direction == DOWN || direction == LEFT || direction == RIGHT;
        }

        public static boolean isVertical(int direction) {
            return direction == UP || direction == DOWN;
        }

        public static boolean isBottomToTopDiagonal(int direction) {
            return ((direction & DragDirectionFlags.RIGHT) == DragDirectionFlags.RIGHT && (direction & DragDirectionFlags.UP) == DragDirectionFlags.UP) ||
                    ((direction & DragDirectionFlags.LEFT) == DragDirectionFlags.LEFT && (direction & DragDirectionFlags.DOWN) == DragDirectionFlags.DOWN);
        }
    }

    public float getHitboxWidth() { return hitboxWidth; }
    public void setHitboxWidth(float hitboxWidth) { this.hitboxWidth = hitboxWidth; }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size) {
        super(position, texture, size);
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size, Color color) {
        super(position, texture, size, color);
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, Vector2 scale, NineSliceTexture texture, Vector2 size, Color color) {
        super(position, scale, texture, size, color);
        createHitboxes();
    }

    private void createHitboxes() {
        hitboxToDragDirection.clear();

        Vector2 dimensions = getDimensions();
        Vector2 halfDimensions = getDimensions().scl(0.5f);

        float cardinalWidth = dimensions.x - (hitboxWidth * 2.0f);
        float cardinalHeight = dimensions.y - (hitboxWidth * 2.0f);

        // Cardinals
        hitboxToDragDirection.put(new UIElementHitbox(-halfDimensions.x, 0f, hitboxWidth, cardinalHeight, this), DragDirectionFlags.LEFT);
        hitboxToDragDirection.put(new UIElementHitbox(halfDimensions.x, 0f, hitboxWidth, cardinalHeight, this), DragDirectionFlags.RIGHT);
        hitboxToDragDirection.put(new UIElementHitbox(0, halfDimensions.y, cardinalWidth, hitboxWidth, this), DragDirectionFlags.UP);
        hitboxToDragDirection.put(new UIElementHitbox(0, -halfDimensions.y, cardinalWidth, hitboxWidth, this), DragDirectionFlags.DOWN);

        // Diagonals
        hitboxToDragDirection.put(new UIElementHitbox(-halfDimensions.x, halfDimensions.y, hitboxWidth, hitboxWidth, this), DragDirectionFlags.LEFT | DragDirectionFlags.UP);
        hitboxToDragDirection.put(new UIElementHitbox(-halfDimensions.x, -halfDimensions.y, hitboxWidth, hitboxWidth, this), DragDirectionFlags.LEFT | DragDirectionFlags.DOWN);
        hitboxToDragDirection.put(new UIElementHitbox(halfDimensions.x, halfDimensions.y, hitboxWidth, hitboxWidth, this), DragDirectionFlags.RIGHT | DragDirectionFlags.UP);
        hitboxToDragDirection.put(new UIElementHitbox(halfDimensions.x, -halfDimensions.y, hitboxWidth, hitboxWidth, this), DragDirectionFlags.RIGHT | DragDirectionFlags.DOWN);
    }

    @Override
    public void hoverStarted(Hitbox hitbox) {
        UIElementHitbox uiElementHitbox = (UIElementHitbox) hitbox;
        if(uiElementHitbox == null || !hitboxToDragDirection.containsKey(uiElementHitbox)) {
            StreakTheSpire.logError("UIElementHitbox does not exist or is invalid!");
            return;
        }

        int dragDirection = hitboxToDragDirection.get(uiElementHitbox);
        if(DragDirectionFlags.isCardinal(dragDirection)) {
            if(DragDirectionFlags.isVertical(dragDirection)) {
                // Set mouse cursor to vertical resize cursor
            }
            else {
                // Set mouse cursor to horizontal mouse cursor
            }
        }
        else {
            if(DragDirectionFlags.isBottomToTopDiagonal(dragDirection)) {
                // Set mouse cursor to bottom-to-top diagonal resize cursor
            }
            else {
                // Set mouse cursor to top-to-bottom diagonal resize cursor
            }
        }
    }

    @Override
    public void startClicking(Hitbox hitbox) {
        UIElementHitbox uiElementHitbox = (UIElementHitbox) hitbox;
        if(uiElementHitbox == null || !hitboxToDragDirection.containsKey(uiElementHitbox)) {
            StreakTheSpire.logError("UIElementHitbox does not exist or is invalid!");
            return;
        }

        currentHitbox = uiElementHitbox;
        currentDragDirection = hitboxToDragDirection.get(uiElementHitbox);
        Affine2 worldToLocal = getWorldToLocalTransform();
        Vector2 mousePosition = new Vector2(InputHelper.mX, InputHelper.mY);
        worldToLocal.applyTo(mousePosition);
        currentOffset.set(mousePosition.cpy().sub(currentHitbox.getLocalPosition()));
    }

    @Override
    public void clicked(Hitbox hitbox) {
        // Maybe play a sound or commit to prefs?
        clearCurrentSelection();
    }

    private void clearCurrentSelection() {
        currentHitbox = null;
        currentDragDirection = DragDirectionFlags.NONE;
        currentOffset.set(0, 0);
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);

        Affine2 worldTransform = getLocalToWorldTransform();
        updateHitboxes(worldTransform);

        if(currentHitbox != null && currentDragDirection != DragDirectionFlags.NONE) {
            Vector2 dimensions = getDimensions();

            Affine2 worldToLocal = getWorldToLocalTransform();
            Vector2 mousePosition = new Vector2(InputHelper.mX, InputHelper.mY);
            worldToLocal.applyTo(mousePosition);


            Vector2 difference = mousePosition.cpy().sub(currentHitbox.getLocalPosition().cpy().add(currentOffset));

            Vector2 halfDifference = difference.cpy().scl(0.5f);

            StreakTheSpire.logInfo("Drag: mousePosition: " + mousePosition + ", difference: " + difference + ", halfDifference: " + halfDifference);

            if((currentDragDirection & DragDirectionFlags.LEFT) == DragDirectionFlags.LEFT) {
                setDimensions(new Vector2(dimensions.x - difference.x, dimensions.y));
                setLocalPosition(getLocalPosition().add(halfDifference.x, 0));
            }
            else if((currentDragDirection & DragDirectionFlags.RIGHT) == DragDirectionFlags.RIGHT) {
                setDimensions(new Vector2(dimensions.x + difference.x, dimensions.y));
                setLocalPosition(getLocalPosition().add(halfDifference.x, 0));
            }

            dimensions = getDimensions();

            if((currentDragDirection & DragDirectionFlags.UP) == DragDirectionFlags.UP) {
                setDimensions(new Vector2(dimensions.x, dimensions.y + difference.y));
                setLocalPosition(getLocalPosition().add(0, halfDifference.y));
            }
            else if((currentDragDirection & DragDirectionFlags.DOWN) == DragDirectionFlags.DOWN) {
                setDimensions(new Vector2(dimensions.x, dimensions.y - difference.y));
                setLocalPosition(getLocalPosition().add(0, halfDifference.y));
            }

            updateHitboxes(getLocalToWorldTransform());
        }
    }

    private void updateHitboxes(Affine2 worldTransform) {
        Vector2 halfDimensions = getDimensions().scl(0.5f);
        for(Map.Entry<UIElementHitbox, Integer> entry : hitboxToDragDirection.entrySet()) {
            UIElementHitbox uiElementHitbox = entry.getKey();
            Integer dragDirection = entry.getValue();

            Vector2 localPosition = new Vector2();
            Vector2 size = new Vector2(getDimensions());
            size.sub(new Vector2(hitboxWidth * 2, hitboxWidth * 2));

            if((dragDirection & DragDirectionFlags.LEFT) == DragDirectionFlags.LEFT) {
                size.x = hitboxWidth;
                localPosition.x = -halfDimensions.x;
            }
            else if((dragDirection & DragDirectionFlags.RIGHT) == DragDirectionFlags.RIGHT) {
                size.x = hitboxWidth;
                localPosition.x = halfDimensions.x;
            }

            if((dragDirection & DragDirectionFlags.UP) == DragDirectionFlags.UP) {
                size.y = hitboxWidth;
                localPosition.y = halfDimensions.y;
            }
            else if((dragDirection & DragDirectionFlags.DOWN) == DragDirectionFlags.DOWN) {
                size.y = hitboxWidth;
                localPosition.y = -halfDimensions.y;
            }

            uiElementHitbox.setLocalPosition(localPosition);
            uiElementHitbox.setLocalSize(size);
            uiElementHitbox.update(worldTransform);
        }
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);
        hitboxToDragDirection.forEach((uiElementHitbox, integer) -> uiElementHitbox.render(spriteBatch));
    }
}
