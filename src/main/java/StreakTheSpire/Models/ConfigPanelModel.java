package StreakTheSpire.Models;


import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ConfigPanelModel implements IModel {
    public Property<UIStrings> localisationSource = new Property<UIStrings>(null);
    public Property<UIButtonDataModel> toggleButtonData = new Property<>(null);
    public Property<UIButtonDataModel> labelButtonData = new Property<>(null);
    public Property<BitmapFont> labelFont = new Property<>(null);
    public Property<Boolean> labelFontIsSDF = new Property<>(Boolean.FALSE);

    public ConfigPanelModel() {}
}
