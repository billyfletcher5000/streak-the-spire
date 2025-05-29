package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

public class UIToggleButtonElement extends UIButtonElement {

    private boolean selected = false;
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            foregroundElement.setVisible(selected);
            dispatchOnToggled();
        }
    }

    public void setSelectedTexture(Texture texture) { setForeground(texture); }
    public void setSelectedTexture(TextureRegion texture) { setForeground(texture); }

    public UIToggleButtonElement() {}
    public UIToggleButtonElement(Vector2 localPosition, Texture backgroundNormal, Texture backgroundHover, Texture backgroundPressed, Texture midground, Texture selectedTexture, Vector2 pressedOffset) {
        super(localPosition, backgroundNormal, backgroundHover, backgroundPressed, midground, selectedTexture, pressedOffset);
    }

    public UIToggleButtonElement(Vector2 localPosition, TextureRegion backgroundNormal, TextureRegion backgroundHover, TextureRegion backgroundPressed, TextureRegion midground, TextureRegion selectedTexture, Vector2 pressedOffset)  {
        super(localPosition, backgroundNormal, backgroundHover, backgroundPressed, midground, selectedTexture, pressedOffset);
    }

    public void initialise(Vector2 localPosition) {
        super.initialise(localPosition);
        addOnClickedSubscriber(this::toggle);
    }

    public void initialise(Vector2 localPosition, TextureRegion backgroundNormal, TextureRegion backgroundHover, TextureRegion backgroundPressed, TextureRegion midground, TextureRegion foreground, Vector2 pressedOffset) {
        super.initialise(localPosition, backgroundNormal, backgroundHover, backgroundPressed, midground, foreground, pressedOffset); // TODO: Work out a much better way of doing initialisation/constructors, this sucks
        addOnClickedSubscriber(this::toggle);
    }

    @Override
    public void close() {
        super.close();
        removeOnClickedSubscriber(this::toggle);
    }

    public void toggle() {
        setSelected(!isSelected());
    }

    public interface OnToggledSubscriber {
        void onToggled(boolean isSelected);
    }

    private HashSet<OnToggledSubscriber> onToggledSubscribers = new HashSet<>();

    public OnToggledSubscriber addOnToggledSubscriber(OnToggledSubscriber subscriber) {
        if (onToggledSubscribers == null)
            onToggledSubscribers = new HashSet<>();

        onToggledSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnToggledSubscriber(OnToggledSubscriber subscriber) {
        if(onToggledSubscribers != null)
            onToggledSubscribers.removeIf(element -> element == null || element == subscriber);
    }

    public void clearOnToggledSubscribers() {
        onToggledSubscribers = null;
    }

    public void dispatchOnToggled() {
        for(OnToggledSubscriber subscriber : onToggledSubscribers)
            subscriber.onToggled(isSelected());
    }
}
