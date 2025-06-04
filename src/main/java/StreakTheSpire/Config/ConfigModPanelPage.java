package StreakTheSpire.Config;

import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import StreakTheSpire.Utils.ImprovedModButton;
import basemod.*;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class ConfigModPanelPage {
    private final ArrayList<IUIElement> elements = new ArrayList<>();

    public ArrayList<IUIElement> getElements() { return elements; }
    public void addElement(IUIElement element) { elements.add(element); }
    public void removeElement(IUIElement element) { elements.remove(element); }
    public void clearElements() { elements.clear(); }

    public ConfigModPanelPage() {}

    public abstract String getTitleLocalizationID();
    public abstract void initialise(ModPanel modPanel, Vector2 contentTopLeft, Vector2 contentDimensions);

    protected FixedModLabeledToggleButton createBooleanPropElement(float x, float y, Property<Boolean> property, String labelTitle, ModPanel modPanel) {
        return createBooleanPropElement(x, y, property, labelTitle, null, modPanel, null);
    }

    protected FixedModLabeledToggleButton createBooleanPropElement(float x, float y, Property<Boolean> property, String labelTitle, String tooltipText, ModPanel modPanel) {
        return createBooleanPropElement(x, y, property, labelTitle, tooltipText, modPanel, null);
    }

    protected FixedModLabeledToggleButton createBooleanPropElement(float x, float y, Property<Boolean> property, String labelTitle, String tooltipText, ModPanel modPanel, Consumer<Property<Boolean>> callback) {
        FixedModLabeledToggleButton button = new FixedModLabeledToggleButton(
                labelTitle,
                tooltipText,
                x,
                y,
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                property.get(),
                modPanel,
                (label) -> {},
                (toggleButton) -> {
                    property.set(toggleButton.enabled);
                    if(callback != null) callback.accept(property);
                }
        );

        return button;
    }

    protected IUIElement[] createIntPropTextInputElements(float x, float y, float textInputWidth, float height, Property<Integer> property, int min, int max, String labelTitle, ModPanel modPanel) {
        return createIntPropTextInputElements(x, y, textInputWidth, height, property, min, max, labelTitle, modPanel, null);
    }

    protected IUIElement[] createIntPropTextInputElements(float x, float y, float textInputWidth, float height, Property<Integer> property, int min, int max, String labelTitle, ModPanel modPanel, Consumer<Property<Integer>> callback) {
        ModTextInput textInput = new ModTextInput(
                property.get().toString(),
                x,
                y,
                textInputWidth,
                height,
                modPanel,
                (input) -> {
                    if(input.text != null && !input.text.isEmpty()) {
                        try {
                            int parsedValue = Integer.parseInt(input.text);
                            parsedValue = Math.min(Math.max(parsedValue, min), max);
                            input.text = Integer.toString(parsedValue);
                            property.set(parsedValue);
                            if(callback != null) callback.accept(property);
                        } catch (NumberFormatException e) {
                            input.text = property.get().toString();
                        }
                    }
                }
        );

        textInput.backColor = Color.DARK_GRAY;
        textInput.color = Settings.CREAM_COLOR;

        FixedModLabel label = new FixedModLabel(
                labelTitle,
                x + textInputWidth + 10f,
                y + (FontHelper.charDescFont.getLineHeight() / 2f),
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                modPanel,
                (labelObj) -> {}
        );

        return new IUIElement[] {textInput, label};
    }

    protected IUIElement[] createIntPropButtonsElements(float x,
                                                        float y,
                                                        Property<Integer> property,
                                                        int min,
                                                        int max,
                                                        String labelTitle,
                                                        Texture minNormalTexture,
                                                        Texture minHoverTexture,
                                                        Texture minPressedTexture,
                                                        Texture maxNormalTexture,
                                                        Texture maxHoverTexture,
                                                        Texture maxPressedTexture,
                                                        ModPanel modPanel,
                                                        Consumer<Property<Integer>> callback) {
        final float buttonWidth = 48.0f * Settings.scale;
        final float labelWidthPerCharacter = 16.0f * Settings.scale;
        final float labelYOffset = 12.0f * Settings.scale;
        final float minMaxYOffset = 5.0f * Settings.scale;
        final float overallYOffset = -4.0f * Settings.scale;
        final float buttonPadding = 4.0f * Settings.scale;

        y += overallYOffset;
        
        int maxNumCharacters = String.valueOf(max).length();

        float displayLabelBaseX = x + buttonWidth;
        float displayLabelMaxWidth = (maxNumCharacters * labelWidthPerCharacter);

        String displayLabelText = property.get().toString();
        float displayLabelX = displayLabelBaseX + (displayLabelMaxWidth * 0.5f) - ((labelWidthPerCharacter * displayLabelText.length()) * 0.5f);

        FixedModLabel displayLabel = new FixedModLabel(
                displayLabelText,
                displayLabelX,
                y + labelYOffset,
                FontHelper.charDescFont,
                modPanel,
                label -> {
                    int numCharacters = label.text.length();
                    label.x = displayLabelBaseX + (displayLabelMaxWidth * 0.5f) - ((labelWidthPerCharacter * numCharacters) * 0.5f);
                }
        );

        IUIElement leftButton = new ModButton(
                x,
                y,
                ImageMaster.loadImage("img/tinyLeftArrow.png"),
                modPanel,
                (btn) -> {
                    if(addToIntPropValue(property, -1, min, max, displayLabel))
                        callback.accept(property);
                }
        );

        float newX = x + buttonWidth + displayLabelMaxWidth;

        IUIElement rightButton = new ModButton(
                newX,
                y,
                ImageMaster.loadImage("img/tinyRightArrow.png"),
                modPanel,
                (btn) -> {
                    if(addToIntPropValue(property, 1, min, max, displayLabel))
                        callback.accept(property);
                }
        );

        newX += buttonWidth;

        IUIElement minButton = new ImprovedModButton(
                newX,
                y + minMaxYOffset,
                minNormalTexture,
                minHoverTexture,
                minPressedTexture,
                modPanel,
                modLabeledButton -> {
                    if(addToIntPropValue(property, -max, min, max, displayLabel))
                        callback.accept(property);
                }
        );

        newX += minNormalTexture.getWidth() + buttonPadding;

        IUIElement maxButton = new ImprovedModButton(
                newX,
                y + minMaxYOffset,
                maxNormalTexture,
                maxHoverTexture,
                maxPressedTexture,
                modPanel,
                modLabeledButton -> {
                    if(addToIntPropValue(property, max, min, max, displayLabel))
                        callback.accept(property);
                }
        );

        newX += maxNormalTexture.getWidth() + buttonPadding + buttonPadding;

        IUIElement titleLabel = new FixedModLabel(
                labelTitle,
                newX,
                y + labelYOffset,
                FontHelper.charDescFont,
                modPanel,
                label -> {}
        );

        return new IUIElement[] {leftButton, displayLabel, rightButton, minButton, maxButton, titleLabel};
    }

    private static boolean addToIntPropValue(Property<Integer> property, int amount, int min, int max, FixedModLabel displayLabel) {
        int currentValue = property.get();
        int newValue = currentValue + amount;
        newValue = Math.min(Math.max(newValue, min), max);

        if(newValue != currentValue) {
            property.set(newValue);
            displayLabel.text = Integer.toString(newValue);
            return true;
        }

        return false;
    }
}
