package StreakTheSpire.Controllers;

import StreakTheSpire.Models.CharacterDisplayModel;
import StreakTheSpire.Models.CharacterDisplaySetModel;
import StreakTheSpire.Models.CharacterIconDisplayModel;
import com.badlogic.gdx.graphics.Texture;

public class CharacterDisplaySetController {
    private CharacterDisplaySetModel model;

    public CharacterDisplaySetController(CharacterDisplaySetModel model) {
        this.model = model;
    }

    public CharacterDisplayModel getCharacterDisplay(String playerClass) {
        return model.characterDisplayModels.stream().filter(model -> model.identifier.get().equals(playerClass)).findAny().orElse(null);
    }

    public CharacterDisplayModel addCharacterIconDisplay(String playerClass, Texture iconTexture) {
        CharacterIconDisplayModel displayModel = (CharacterIconDisplayModel) getCharacterDisplay(playerClass);

        if(displayModel == null)
            displayModel = new CharacterIconDisplayModel();

        displayModel.identifier.set(playerClass);
        displayModel.iconTexture.set(iconTexture);

        return displayModel;
    }
}
