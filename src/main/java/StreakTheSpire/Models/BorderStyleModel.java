package StreakTheSpire.Models;

import StreakTheSpire.UI.IntMargins;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;

public class BorderStyleModel implements IModel {
    public Property<String> identifier = new Property<>(null);
    public Property<Boolean> showInGameMode = new Property<>(true);
    public Property<String> texturePath = new Property<>(null);
    public Property<Color> color = new Property<>(Color.WHITE);
    public Property<IntMargins> textureMargins = new Property<>(new IntMargins());
    public Property<String> buttonOverlayTexturePath = new Property<>(null);

    public BorderStyleModel() {}
}
