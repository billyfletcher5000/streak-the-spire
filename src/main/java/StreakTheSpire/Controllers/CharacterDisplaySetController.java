package StreakTheSpire.Controllers;

import StreakTheSpire.Models.CharacterDisplayModel;
import StreakTheSpire.Models.DisplayPreferencesModel;
import StreakTheSpire.Models.CharacterDisplaySetModel;
import StreakTheSpire.StreakTheSpire;

import java.util.List;
import java.util.stream.Stream;

public class CharacterDisplaySetController {
    private CharacterDisplaySetModel model;

    public CharacterDisplaySetController(CharacterDisplaySetModel model) {
        this.model = model;
    }

    public CharacterDisplayModel getCharacterDisplay(String playerClass, DisplayPreferencesModel preferences) {
        return getCharacterDisplay(playerClass, preferences.characterStyle.get());
    }

    public CharacterDisplayModel getCharacterDisplay(String playerClass, DisplayPreferencesModel.CharacterStyle style) {
        List<Class<? extends CharacterDisplayModel>> preferredModelClasses = CharacterDisplayPreferencesController.getPreferredDisplayModelClassesInOrder(style);

        Stream<CharacterDisplayModel> possibleDisplays = model.characterDisplayModels.stream()
                .filter(model -> model.identifier.get().equals(playerClass))
                .sorted((modelA, modelB) -> {
                    int indexA = preferredModelClasses.indexOf(modelA.getClass());
                    int indexB = preferredModelClasses.indexOf(modelB.getClass());
                    return Integer.compare(indexA, indexB);
                });

        return possibleDisplays.findFirst().orElse(null);
    }

    public void addCharacterDisplayModel(CharacterDisplayModel displayModel) {
        Stream<CharacterDisplayModel> possibleDisplays = model.characterDisplayModels.stream()
                .filter(model -> model.identifier.get().equals(displayModel.identifier) && model.getClass() == displayModel.getClass());

        if(possibleDisplays.findAny().isPresent()) {
            StreakTheSpire.logWarning("Attempt to add duplicate display model " + displayModel.identifier + " with class " + displayModel.getClass());
            return;
        }

        this.model.characterDisplayModels.add(displayModel);
    }
}
