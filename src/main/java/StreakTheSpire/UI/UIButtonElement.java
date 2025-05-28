package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;

import java.util.HashSet;

public class UIButtonElement extends UIImageElement implements HitboxListener {

    private UIElementHitbox hitbox;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private TextureRegion backgroundNormal;
    private TextureRegion backgroundPressed;
    private TextureRegion backgroundHover;
    private Vector2 pressedOffset = new Vector2(1f, -2f);
    private UIImageElement midgroundElement;
    private UIImageElement foregroundElement;

    public boolean isHovered() { return isHovered; }
    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
        if(!isPressed)
            setTextureRegion(isHovered ? backgroundHover : backgroundNormal);
    }

    public boolean isPressed() { return isPressed; }
    public void setPressed(boolean isPressed) {
        if(this.isPressed != isPressed) {
            this.isPressed = isPressed;
            if(isPressed) {
                midgroundElement.setLocalPosition(pressedOffset);
                foregroundElement.setLocalPosition(pressedOffset);
                setTextureRegion(backgroundPressed);
            }
            else {
                midgroundElement.setLocalPosition(Vector2.Zero);
                foregroundElement.setLocalPosition(Vector2.Zero);
                setTextureRegion(isHovered ? backgroundHover : backgroundNormal);
            }
        }
    }

    public void setBackgroundNormal(Texture texture) { setBackgroundNormal(new TextureRegion(texture)); }
    public void setBackgroundNormal(TextureRegion texture) {
        this.backgroundNormal = texture;
        if(texture != null)
            setDimensions(new Vector2(texture.getRegionWidth(), texture.getRegionHeight()));

        updateBackground();
    }

    public void setBackgroundHover(Texture texture) { setBackgroundHover(texture != null ? new TextureRegion(texture) : null); }
    public void setBackgroundHover(TextureRegion backgroundHover) { this.backgroundHover = backgroundHover; updateBackground(); }
    public void setBackgroundPressed(Texture texture) { setBackgroundPressed(new TextureRegion(texture != null ? new TextureRegion(texture) : null)); }
    public void setBackgroundPressed(TextureRegion backgroundPressed) { this.backgroundPressed = backgroundPressed; updateBackground(); }
    public void setMidground(Texture texture) { setMidground(new TextureRegion(texture != null ? new TextureRegion(texture) : null)); }
    public void setMidground(TextureRegion texture) { midgroundElement.setTextureRegion(texture); if(texture != null) midgroundElement.setDimensions(new Vector2(texture.getRegionWidth(), texture.getRegionHeight())); }
    public void setForeground(Texture texture) { setForeground(new TextureRegion(texture != null ? new TextureRegion(texture) : null)); }
    public void setForeground(TextureRegion texture) { foregroundElement.setTextureRegion(texture); if(texture != null) foregroundElement.setDimensions(new Vector2(texture.getRegionWidth(), texture.getRegionHeight())); }
    public void setPressedOffset(Vector2 offset) { pressedOffset = offset; }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        if(hitbox != null) {
            Vector2 processedDimensions = getDimensions();
            hitbox.setLocalSize(processedDimensions);
        }
    }

    public UIButtonElement() {}

    public UIButtonElement(Vector2 localPosition, Texture backgroundNormal, Texture backgroundHover, Texture backgroundPressed, Texture midground, Texture foreground, Vector2 pressedOffset) {
        this(localPosition, new TextureRegion(backgroundNormal), new TextureRegion(backgroundHover), new TextureRegion(backgroundPressed), new TextureRegion(midground), new TextureRegion(foreground), pressedOffset);
    }

    public UIButtonElement(Vector2 localPosition, TextureRegion backgroundNormal, TextureRegion backgroundHover, TextureRegion backgroundPressed, TextureRegion midground, TextureRegion foreground, Vector2 pressedOffset) {
        initialise(localPosition, backgroundNormal, backgroundHover, backgroundPressed, midground, foreground, pressedOffset);
    }

    public void initialise(Vector2 localPosition) {
        initialise(localPosition, VectorOne.cpy(), null, Vector2.Zero, Color.WHITE);
        midgroundElement = new UIImageElement(Vector2.Zero, (TextureRegion) null);
        addChild(midgroundElement);

        foregroundElement = new UIImageElement(Vector2.Zero, (TextureRegion) null);
        addChild(foregroundElement);

        Vector2 dimensions = getDimensions();
        hitbox = new UIElementHitbox(0f, 0f, dimensions.x, dimensions.y, this);
    }

    public void initialise(Vector2 localPosition, TextureRegion backgroundNormal, TextureRegion backgroundHover, TextureRegion backgroundPressed, TextureRegion midground, TextureRegion foreground, Vector2 pressedOffset) {
        initialise(localPosition, VectorOne.cpy(), backgroundNormal, new Vector2(backgroundNormal.getRegionWidth(), backgroundNormal.getRegionHeight()), Color.WHITE);

        setBackgroundNormal(backgroundNormal);
        this.backgroundHover = backgroundHover;
        this.backgroundPressed = backgroundPressed;
        this.pressedOffset = pressedOffset;

        midgroundElement = new UIImageElement(Vector2.Zero, midground);
        addChild(midgroundElement);

        foregroundElement = new UIImageElement(Vector2.Zero, foreground);
        addChild(foregroundElement);

        Vector2 dimensions = getDimensions();
        hitbox = new UIElementHitbox(0f, 0f, dimensions.x, dimensions.y, this);
    }

    public interface OnClickedSubscriber {
        void onClicked();
    }

    private HashSet<OnClickedSubscriber> onClickedSubscribers = new HashSet<>();

    public OnClickedSubscriber addOnClickedSubscriber(OnClickedSubscriber subscriber) {
        if (onClickedSubscribers == null)
            onClickedSubscribers = new HashSet<>();

        onClickedSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnClickedSubscriber(OnClickedSubscriber subscriber) {
        if(onClickedSubscribers != null)
            onClickedSubscribers.removeIf(element -> element == null || element == subscriber);
    }

    public void dispatchOnClicked() {
        for(OnClickedSubscriber subscriber : onClickedSubscribers)
            subscriber.onClicked();
    }

    @Override
    public void hoverStarted(Hitbox hitbox) {
        if(hitbox == this.hitbox) {
            setHovered(true);
        }
    }

    @Override
    public void startClicking(Hitbox hitbox) {
        setPressed(true);
    }

    @Override
    public void clicked(Hitbox hitbox) {
        setPressed(false);
        dispatchOnClicked();
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        if(hitbox != null) {
            hitbox.update(getLocalToWorldTransform());
            if (isHovered && !hitbox.hovered) {
                setHovered(false);
            }
        }
    }

    private void updateBackground() {
        setTextureRegion(isPressed ? backgroundPressed : (isHovered ? backgroundHover : backgroundNormal));
    }
}
