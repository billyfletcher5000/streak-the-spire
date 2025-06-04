package StreakTheSpire.Models;

import StreakTheSpire.Data.RotatingConstants;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class DisplayPreferencesModel implements IConfigDataModel, IModel {
    public final static String CharacterWildcard = "*";

    public enum CharacterStyle {
        AnimatedIcon,
        StaticIcon,
        Text
    }

    public enum RenderLayer {
        PreRoom,
        TopPanel,
        AboveMost,
        AboveAll
    }

    public Property<CharacterStyle> characterStyle = new Property<>(CharacterStyle.AnimatedIcon);
    public Property<String> borderStyle = new Property<>(null);
    public Property<RenderLayer> renderLayer = new Property<>(RenderLayer.TopPanel);
    public Property<String> fontIdentifier = new Property<>("Kreon_SDF_Outline_Shadow");
    public Property<Boolean> suppressSaveNotification = new Property<>(false);
    public Property<Boolean> colouredStreakNumbers = new Property<>(false);

    public DisplayPreferencesModel() {}

    //region IConfigModel
    private String configID = "DisplayPreferencesModel";

    @Override
    public String getConfigID() {
        return configID;
    }

    @Override
    public void setConfigID(String ID) {
        configID = ID;
    }
    //endregion
}
