package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.PropertyHashSet;

public class BorderStyleSetModel implements IModel {
    public PropertyHashSet<BorderStyleModel> borderStyles = new PropertyHashSet<>();

    public BorderStyleSetModel() {}
}
