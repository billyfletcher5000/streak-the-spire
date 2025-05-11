package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.HashMap;

public class UIResizablePanel extends UINineSliceElement implements HitboxListener {
    private float hitboxWidth = 5.0f;
    private HashMap<UIElementHitbox, Integer> hitboxToDragDirection = new HashMap<>();
    private UIElementHitbox currentHitbox = null;
    private int currentDragDirection = DragDirectionFlags.NONE;
    private Vector2 currentOffset = new Vector2();

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
    }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size, Color color) {
        super(position, texture, size, color);
    }

    public UIResizablePanel(Vector2 position, Vector2 scale, NineSliceTexture texture, Vector2 size, Color color) {
        super(position, scale, texture, size, color);
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
        currentOffset.set((float)InputHelper.mX - uiElementHitbox.x, (float)InputHelper.mY - uiElementHitbox.y);
    }

    @Override
    public void clicked(Hitbox hitbox) {
        // Maybe play a sound or commit to prefs?
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);

        Affine2 worldTransform = getLocalToWorldTransform();
        updateHitboxes(worldTransform);

        if(currentHitbox != null && currentDragDirection != DragDirectionFlags.NONE) {
            Vector2 dimensions = getDimensions();

            // TODO: Work out how on earth to keep this transformation safe, I imagine it involves inverse transforms
            //       but it's a bit confusing, i think if the offset and mouse position are inverse transformed it just
            //       works out but I'm very tired right now.
            Vector2 difference = new Vector2((float)InputHelper.mX - (currentHitbox.x + currentOffset.x), (float)InputHelper.mY - (currentHitbox.y + currentOffset.y));
            Vector2 halfDifference = difference.cpy().scl(0.5f);

            if((currentDragDirection & DragDirectionFlags.LEFT) == DragDirectionFlags.LEFT) {
                setDimensions(new Vector2(dimensions.x + difference.x, dimensions.y));

            }
            else if((currentDragDirection & DragDirectionFlags.RIGHT) == DragDirectionFlags.RIGHT) {

            }

            if((currentDragDirection & DragDirectionFlags.UP) == DragDirectionFlags.UP) {

            }
            else if((currentDragDirection & DragDirectionFlags.DOWN) == DragDirectionFlags.DOWN) {

            }

            updateHitboxes(worldTransform);
        }
    }

    private void updateHitboxes(Affine2 worldTransform) {
        hitboxToDragDirection.forEach((uiElementHitbox, integer) -> uiElementHitbox.update(worldTransform));
    }
}
