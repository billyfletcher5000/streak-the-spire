package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Texture;

public class CharacterIconDisplayModel extends CharacterDisplayModel {
    public Property<Texture> iconTexture = new Property<>(null);
}
