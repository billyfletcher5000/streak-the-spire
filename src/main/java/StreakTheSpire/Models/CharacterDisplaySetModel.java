package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.PropertyHashSet;

public class CharacterDisplaySetModel implements IModel {
    public PropertyHashSet<CharacterDisplayModel> characterDisplayModels = new PropertyHashSet<>();

    public CharacterDisplaySetModel() {}
}
