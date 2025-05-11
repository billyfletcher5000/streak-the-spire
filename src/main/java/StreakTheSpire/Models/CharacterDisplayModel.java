package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Texture;

public class CharacterDisplayModel {
    public Property<String> identifier = new Property<>(null);
    public Property<Texture> iconTexture = new Property<>(null);

    public CharacterDisplayModel() {}
}
