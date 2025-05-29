package StreakTheSpire.UI;

import StreakTheSpire.Models.UIButtonDataModel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class UILabeledToggleButtonElement extends UIElement {

    private final UIToggleButtonElement toggleButton;
    private final UITextElement label;

    public Color getTextColor() { return label.getColor(); }
    public void setTextColor(Color textColor) { label.setColor(textColor); }

    public void addOnToggledSubscriber(UIToggleButtonElement.OnToggledSubscriber subscriber) { toggleButton.addOnToggledSubscriber(subscriber); }
    public void removeOnToggledSubscriber(UIToggleButtonElement.OnToggledSubscriber subscriber) { toggleButton.removeOnToggledSubscriber(subscriber); }
    public void clearOnToggledSubscribers() { toggleButton.clearOnToggledSubscribers(); }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        updateButtonAndLabel();
    }

    public UILabeledToggleButtonElement(Vector2 localPosition, Vector2 dimensions, Texture backgroundNormal, Texture backgroundHover, Texture backgroundPressed, Texture midground, Texture selectedTexture, Vector2 pressedOffset, String labelText, BitmapFont font) {
        this(localPosition,
                dimensions,
                backgroundNormal != null ? new TextureRegion(backgroundNormal) : null,
                backgroundHover != null ? new TextureRegion(backgroundHover) : null,
                backgroundPressed != null ? new TextureRegion(backgroundPressed) : null,
                midground != null ? new TextureRegion(midground) : null,
                selectedTexture != null ? new TextureRegion(selectedTexture) : null,
                pressedOffset,
                labelText,
                font);
    }

    public UILabeledToggleButtonElement(Vector2 localPosition, Vector2 dimensions, TextureRegion backgroundNormal, TextureRegion backgroundHover, TextureRegion backgroundPressed, TextureRegion midground, TextureRegion selectedTexture, Vector2 pressedOffset, String labelText, BitmapFont font) {
        super();
        setLocalPosition(localPosition);

        toggleButton = new UIToggleButtonElement(Vector2.Zero, backgroundNormal, backgroundHover, backgroundPressed, midground, selectedTexture, pressedOffset);
        addChild(toggleButton);

        label = new UITextElement(Vector2.Zero, font, labelText, Vector2.Zero, Align.left);
        addChild(label);

        setDimensions(dimensions);
    }

    private void updateButtonAndLabel() {
        Vector2 dimensions = getDimensions();

        toggleButton.setDimensions(new Vector2(dimensions.y, dimensions.y));
        label.setDimensions(new Vector2(dimensions.x - dimensions.y, dimensions.y));

        toggleButton.setLocalPosition(new Vector2(dimensions.x * -0.5f, 0f));
        label.setLocalPosition(new Vector2((dimensions.x * -0.5f) + dimensions.y, 0f));
    }
}
