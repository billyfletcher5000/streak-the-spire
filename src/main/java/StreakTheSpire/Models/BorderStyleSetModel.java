package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.PropertyLinkedHashSet;

public class BorderStyleSetModel implements IModel {
    public PropertyLinkedHashSet<BorderStyleModel> borderStyles = new PropertyLinkedHashSet<>();

    public BorderStyleSetModel() {}
}
