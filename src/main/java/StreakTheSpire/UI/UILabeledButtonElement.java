package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class UILabeledButtonElement extends UIButtonElement {

    private boolean useSDF = true;
    private String label;
    private BitmapFont font;

    private UITextElement labelText;

    public boolean usesSDF() { return useSDF; }
    public String getLabel() { return label; }
    public void setLabel(String label) {
        if(!this.label.equals(label)) {
            this.label = label;
            if(labelText != null) {
                labelText.setText(label);
            }
        }
    }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        labelText.setDimensions(getDimensions());
    }

    @Override
    public void setPressed(boolean isPressed) {
        boolean previousPressed = super.isPressed();
        super.setPressed(isPressed);
        if(previousPressed != isPressed) {
            if(isPressed)
                labelText.setLocalPosition(getPressedOffset());
            else
                labelText.setLocalPosition(Vector2.Zero);
        }
    }

    public UILabeledButtonElement(String label, BitmapFont font, boolean usesSDF) {
        super();

        this.label = label;
        this.font = font;
        this.useSDF = usesSDF;
    }

    @Override
    public void initialise(Vector2 localPosition) {
        super.initialise(localPosition);

        labelText = useSDF ? new UISDFTextElement(Vector2.Zero, font, label) : new UITextElement(Vector2.Zero, font, label);
        labelText.setText(label);
        labelText.setDimensions(getDimensions());
        addChild(labelText);
    }

    @Override
    public void initialise(Vector2 localPosition, TextureRegion backgroundNormal, TextureRegion backgroundHover, TextureRegion backgroundPressed, TextureRegion midground, TextureRegion foreground, Vector2 pressedOffset) {
        super.initialise(localPosition, backgroundNormal, backgroundHover, backgroundPressed, midground, foreground, pressedOffset);

        labelText = useSDF ? new UISDFTextElement(Vector2.Zero, font, label) : new UITextElement(Vector2.Zero, font, label);
        labelText.setText(label);
        labelText.setDimensions(getDimensions());
        addChild(labelText);
    }
}
