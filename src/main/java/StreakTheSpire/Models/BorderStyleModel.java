package StreakTheSpire.Models;

import StreakTheSpire.UI.IntMargins;
import StreakTheSpire.Utils.Properties.Property;

public class BorderStyleModel implements IModel {
    public Property<String> identifier = new Property<>(null);
    public Property<Boolean> showInGameMode = new Property<>(true);
    public Property<String> texturePath = new Property<>(null);
    public Property<IntMargins> textureMargins = new Property<>(new IntMargins());

    public BorderStyleModel() {}
}
