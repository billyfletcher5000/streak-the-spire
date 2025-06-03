package StreakTheSpire.Controllers;

import StreakTheSpire.Models.CharacterLocalisationModel;
import StreakTheSpire.Models.CharacterLocalisationSetModel;

public class CharacterLocalisationSetController {
    private CharacterLocalisationSetModel model;

    public CharacterLocalisationSetController(CharacterLocalisationSetModel model) {
        this.model = model;
    }

    public CharacterLocalisationModel getLocalisationForCharacter(String characterIdentifier) {
        return model.characterLocalisations.stream().filter(a -> a.identifier.get().equals(characterIdentifier)).findAny().orElse(null);
    }
}
