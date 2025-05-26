package StreakTheSpire.Controllers;

import StreakTheSpire.Models.BorderStyleModel;
import StreakTheSpire.Models.BorderStyleSetModel;

public class BorderStyleSetController {
    private BorderStyleSetModel model;

    public BorderStyleSetController(BorderStyleSetModel model) {
        this.model = model;
    }

    public BorderStyleModel getModel(String identifier) {
        return model.borderStyles.stream().filter(style -> style.identifier.get().equals(identifier)).findFirst().orElse(null);
    }

    public void addStyle(BorderStyleModel borderStyle) {
        if(getModel(borderStyle.identifier.get()) == null) {
            model.borderStyles.add(borderStyle);
        }
    }
}
