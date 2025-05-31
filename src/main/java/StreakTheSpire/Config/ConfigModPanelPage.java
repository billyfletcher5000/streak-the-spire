package StreakTheSpire.Config;

import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import basemod.IUIElement;
import basemod.ModPanel;
import StreakTheSpire.Utils.Properties.Property;
import basemod.ModTextInput;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ConfigModPanelPage {
    private final ArrayList<IUIElement> elements = new ArrayList<>();

    public ArrayList<IUIElement> getElements() { return elements; }
    public void addElement(IUIElement element) { elements.add(element); }
    public void removeElement(IUIElement element) { elements.remove(element); }
    public void clearElements() { elements.clear(); }

    public ConfigModPanelPage() {}

    public abstract void initialise(ModPanel modPanel);

    protected FixedModLabeledToggleButton createBooleanPropElement(float x, float y, Property<Boolean> property, String labelTitle, ModPanel modPanel) {
        return createBooleanPropElement(x, y, property, labelTitle, modPanel, null);
    }

    protected FixedModLabeledToggleButton createBooleanPropElement(float x, float y, Property<Boolean> property, String labelTitle, ModPanel modPanel, Consumer<Property<Boolean>> callback) {
        FixedModLabeledToggleButton button = new FixedModLabeledToggleButton(
                labelTitle,
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

    protected IUIElement[] createIntPropElements(float x, float y, float textInputWidth, float height, Property<Integer> property, int min, int max, String labelTitle, ModPanel modPanel) {
        return createIntPropElements(x, y, textInputWidth, height, property, min, max, labelTitle, modPanel, null);
    }

    protected IUIElement[] createIntPropElements(float x, float y, float textInputWidth, float height, Property<Integer> property, int min, int max, String labelTitle, ModPanel modPanel, Consumer<Property<Integer>> callback) {
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
}
