package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.math.Vector2;

public class UIButtonDataModel implements IModel {
    public Property<String> backgroundNormalPath = new Property<>(null);
    public Property<String> backgroundPressedPath = new Property<>(null);
    public Property<String> backgroundHoverPath = new Property<>(null);
    public Property<String> midgroundPath = new Property<>(null);
    public Property<String> foregroundPath = new Property<>(null);
    public Property<Vector2> pressedOffset = new Property<>(new Vector2(1f, -2f));
}
