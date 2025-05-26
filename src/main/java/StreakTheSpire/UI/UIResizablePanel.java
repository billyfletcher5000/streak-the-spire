package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UIResizablePanel extends UINineSliceElement implements HitboxListener {

    private final Property<Boolean> resizeEnabled = new Property<>(false);
    private final Property<Float> hitboxWidth = new Property<>(16.0f);
    private final Property<Vector2> minimumSize = new Property<>(Vector2.Zero.cpy());

    private final HashMap<UIElementHitbox, Integer> hitboxToDragDirection = new HashMap<>();
    private UIElementHitbox moveHitbox;
    private UIElementHitbox currentHitbox = null;
    private int currentDragDirection = DragDirectionFlags.NONE;
    private Vector2 currentOffset = new Vector2();

    private HashSet<PanelResizedSubscriber> panelResizedSubscribers = new HashSet<>();
    private HashSet<PanelMovedSubscriber> panelMovedSubscribers = new HashSet<>();

    // Intently disabled rotation for these, see UIElementHitbox comments
    // Note: This will not prevent setting rotation higher, but any rotation higher will cause issues with
    //       this element.
    //@Override
    //public void setLocalRotation(float localRotation) { }

    public boolean isResizeEnabled() { return resizeEnabled.get(); }
    public Property<Boolean> getResizeEnabledProperty() { return resizeEnabled; }
    public void setResizeEnabled(boolean resizeEnabled) { this.resizeEnabled.set(resizeEnabled); if(!resizeEnabled) clearCurrentSelection(); }

    public float getHitboxWidth() { return hitboxWidth.get(); }
    public Property<Float> getHitboxWidthProperty() { return hitboxWidth; }
    public void setHitboxWidth(float hitboxWidth) { this.hitboxWidth.set(hitboxWidth); }

    public Vector2 getMinimumSize() { return minimumSize.get().cpy(); }
    public Property<Vector2> getMinimumSizeProperty() { return minimumSize; }
    public void setMinimumSize(Vector2 minimumSize) { this.minimumSize.set(minimumSize.cpy()); }

    public UIResizablePanel() {
        setMinimumSize(new Vector2(hitboxWidth.get() * 6.0f, hitboxWidth.get() * 6.0f));
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size) {
        super(position, texture, size);
        setMinimumSize(new Vector2(hitboxWidth.get() * 6.0f, hitboxWidth.get() * 6.0f));
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size, Vector2 minimumSize) {
        super(position, texture, size);
        setMinimumSize(minimumSize.cpy());
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size, Color color) {
        super(position, texture, size, color);
        setMinimumSize(new Vector2(hitboxWidth.get() * 6.0f, hitboxWidth.get() * 6.0f));
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, NineSliceTexture texture, Vector2 size, Vector2 minimumSize, Color color) {
        super(position, texture, size, color);
        setMinimumSize(minimumSize.cpy());
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, Vector2 scale, NineSliceTexture texture, Vector2 size, Color color) {
        super(position, scale, texture, size, color);
        setMinimumSize(new Vector2(hitboxWidth.get() * 6.0f, hitboxWidth.get() * 6.0f));
        createHitboxes();
    }

    public UIResizablePanel(Vector2 position, Vector2 scale, NineSliceTexture texture, Vector2 size, Vector2 minimumSize, Color color) {
        super(position, scale, texture, size, color);
        setMinimumSize(minimumSize.cpy());
        createHitboxes();
    }

    public interface PanelResizedSubscriber {
        void onPanelResized();
    }

    public interface PanelMovedSubscriber {
        void onPanelMoved();
    }

    public PanelResizedSubscriber addOnPanelResizedSubscriber(PanelResizedSubscriber subscriber) {
        if (panelResizedSubscribers == null)
            panelResizedSubscribers = new HashSet<>();

        panelResizedSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnPanelResizedSubscriber(PanelResizedSubscriber subscriber) {
        if(panelResizedSubscribers != null)
            panelResizedSubscribers.remove(subscriber);
    }

    public PanelMovedSubscriber addOnPanelMovedSubscriber(PanelMovedSubscriber subscriber) {
        if (panelMovedSubscribers == null)
            panelMovedSubscribers = new HashSet<>();

        panelMovedSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnPanelMovedSubscriber(PanelMovedSubscriber subscriber) {
        if(panelMovedSubscribers != null)
            panelMovedSubscribers.remove(subscriber);
    }

    private void createHitboxes() {
        hitboxToDragDirection.clear();

        Vector2 dimensions = getDimensions();
        Vector2 halfDimensions = getDimensions().scl(0.5f);

        float hitboxWidth = getHitboxWidth();
        float cardinalWidth = dimensions.x - (hitboxWidth * 2.0f);
        float cardinalHeight = dimensions.y - (hitboxWidth * 2.0f);

        moveHitbox = new UIElementHitbox(0f, 0f, cardinalWidth, cardinalHeight, this);

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
        if(currentHitbox != null) {
            StreakTheSpire.logError("Current hitbox already exists!");
            return;
        }

        UIElementHitbox uiElementHitbox = (UIElementHitbox) hitbox;
        if(uiElementHitbox == moveHitbox) {
            currentDragDirection = DragDirectionFlags.NONE;
        }
        else if(hitboxToDragDirection.containsKey(uiElementHitbox)) {
            currentDragDirection = hitboxToDragDirection.get(uiElementHitbox);
        }
        else {
            StreakTheSpire.logError("UIElementHitbox does not exist or is invalid!");
            return;
        }

        currentHitbox = uiElementHitbox;
        Affine2 worldToLocal = getWorldToLocalTransform();
        Vector2 mousePosition = new Vector2(InputHelper.mX, InputHelper.mY);
        worldToLocal.applyTo(mousePosition);
        currentOffset.set(mousePosition.cpy().sub(currentHitbox.getLocalPosition()));
    }

    @Override
    public void clicked(Hitbox hitbox) {
        if(currentHitbox == hitbox) {
            if(currentHitbox == moveHitbox) {
                panelMovedSubscribers.forEach(PanelMovedSubscriber::onPanelMoved);
            }
            else if (hitboxToDragDirection.containsKey(hitbox)) {
                panelResizedSubscribers.forEach(PanelResizedSubscriber::onPanelResized);
            }
        }

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

        if(!InputHelper.isMouseDown && currentHitbox != null) {
            clicked(currentHitbox);
        }

        if(currentHitbox != null) {
            Vector2 dimensions = getDimensions();

            Affine2 worldToLocal = getWorldToLocalTransform();
            Vector2 mousePosition = new Vector2(InputHelper.mX, InputHelper.mY);
            worldToLocal.applyTo(mousePosition);

            Vector2 minimumSize = getMinimumSize();
            Vector2 difference = mousePosition.cpy().sub(currentHitbox.getLocalPosition().cpy().add(currentOffset));

            if (currentHitbox == moveHitbox) {
                setLocalPosition(getLocalPosition().add(difference));
            } else if (currentDragDirection != DragDirectionFlags.NONE) {
                if ((currentDragDirection & DragDirectionFlags.LEFT) == DragDirectionFlags.LEFT) {
                    float newSizeX = dimensions.x - difference.x;
                    setDimensions(new Vector2(Math.max(newSizeX, minimumSize.x), dimensions.y));
                    Vector2 postChangeDimensions = getDimensions();
                    postChangeDimensions.sub(dimensions);
                    setLocalPosition(getLocalPosition().add(postChangeDimensions.scl(-0.5f)));
                } else if ((currentDragDirection & DragDirectionFlags.RIGHT) == DragDirectionFlags.RIGHT) {
                    float newSizeX = dimensions.x + difference.x;
                    setDimensions(new Vector2(Math.max(newSizeX, minimumSize.x), dimensions.y));
                    Vector2 postChangeDimensions = getDimensions();
                    postChangeDimensions.sub(dimensions);
                    setLocalPosition(getLocalPosition().add(postChangeDimensions.scl(0.5f)));
                }

                dimensions = getDimensions();

                if ((currentDragDirection & DragDirectionFlags.UP) == DragDirectionFlags.UP) {
                    float newSizeY = dimensions.y + difference.y;
                    setDimensions(new Vector2(dimensions.x, Math.max(newSizeY, minimumSize.y)));
                    Vector2 postChangeDimensions = getDimensions();
                    postChangeDimensions.sub(dimensions);
                    setLocalPosition(getLocalPosition().add(postChangeDimensions.scl(0.5f)));
                } else if ((currentDragDirection & DragDirectionFlags.DOWN) == DragDirectionFlags.DOWN) {
                    float newSizeY = dimensions.y - difference.y;
                    setDimensions(new Vector2(dimensions.x, Math.max(newSizeY, minimumSize.y)));
                    Vector2 postChangeDimensions = getDimensions();
                    postChangeDimensions.sub(dimensions);
                    setLocalPosition(getLocalPosition().add(postChangeDimensions.scl(-0.5f)));
                }
            }
        }

        updateHitboxes(getLocalToWorldTransform());
    }

    private void updateHitboxes(Affine2 worldTransform) {
        Vector2 baseSize = new Vector2(getDimensions());
        float hitboxWidth = getHitboxWidth();
        baseSize.sub(new Vector2(hitboxWidth * 2, hitboxWidth * 2));

        moveHitbox.setLocalSize(baseSize);
        moveHitbox.update(worldTransform);

        Vector2 halfDimensions = getDimensions().scl(0.5f);
        for(Map.Entry<UIElementHitbox, Integer> entry : hitboxToDragDirection.entrySet()) {
            UIElementHitbox uiElementHitbox = entry.getKey();
            Integer dragDirection = entry.getValue();

            Vector2 localPosition = new Vector2();
            Vector2 size = baseSize.cpy();

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
        moveHitbox.render(spriteBatch);
        hitboxToDragDirection.forEach((uiElementHitbox, integer) -> uiElementHitbox.render(spriteBatch));
    }

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
}
