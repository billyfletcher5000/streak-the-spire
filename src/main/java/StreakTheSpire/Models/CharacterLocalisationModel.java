package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;

public class CharacterLocalisationModel implements IModel {
    public Property<String> identifier = new Property<>(null);
    public Property<String> localisationID = new Property<>(null);

    public CharacterLocalisationModel() {}
}
