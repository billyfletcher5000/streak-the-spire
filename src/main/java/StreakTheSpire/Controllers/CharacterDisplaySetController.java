package StreakTheSpire.Controllers;

import StreakTheSpire.Models.CharacterDisplayModel;
import StreakTheSpire.Models.CharacterDisplaySetModel;
import com.badlogic.gdx.graphics.Texture;

import java.util.Optional;

public class CharacterDisplaySetController {
    private CharacterDisplaySetModel model;

    public CharacterDisplaySetController(CharacterDisplaySetModel model) {
        this.model = model;
    }

    public Optional<CharacterDisplayModel> getCharacterDisplay(String playerClass) {
        return model.characterDisplayModels.stream().filter(model -> model.playerClass.equals(playerClass)).findFirst();
    }

    public CharacterDisplayModel addCharacterDisplay(String playerClass, Texture iconTexture) {
        Optional<CharacterDisplayModel> existingDisplayModel = getCharacterDisplay(playerClass);
        CharacterDisplayModel displayModel = null;
        if(existingDisplayModel.isPresent())
            displayModel = existingDisplayModel.get();
        else
            displayModel = new CharacterDisplayModel();

        displayModel.playerClass.setValue(playerClass);
        displayModel.iconTexture.setValue(iconTexture);

        return displayModel;
    }
}
