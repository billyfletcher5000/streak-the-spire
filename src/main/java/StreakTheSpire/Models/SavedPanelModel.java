package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import static StreakTheSpire.StreakTheSpire.gson;

public class SavedPanelModel extends IModel {
    public Property<Vector2> position = new Property<>(new Vector2(0, 0));
    public Property<Vector2> dimensions = new Property<>(new Vector2(200, 200));
    public Property<Vector2> scale = new Property<>(new Vector2(1, 1));

    public String toString() {
        String ret = "SavedPanelModel:\n";
        ret += "Position: " + position.get() + "\n";
        ret += "Dimensions: " + dimensions.get() + "\n";
        ret += "Scale: " + scale.get() + "\n";
        return ret;
    }
}
