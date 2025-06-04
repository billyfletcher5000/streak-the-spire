package StreakTheSpire.Controllers;

import StreakTheSpire.Models.CharacterCoreDataModel;
import StreakTheSpire.Models.CharacterCoreDataSetModel;

public class CharacterCoreDataSetController {
    private CharacterCoreDataSetModel model;

    public CharacterCoreDataSetController(CharacterCoreDataSetModel model) {
        this.model = model;
    }

    public CharacterCoreDataModel getCharacterData(String characterIdentifier) {
        return model.characterLocalisations.stream().filter(a -> a.identifier.get().equals(characterIdentifier)).findAny().orElse(null);
    }
}
