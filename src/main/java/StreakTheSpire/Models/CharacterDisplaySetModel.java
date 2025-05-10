package StreakTheSpire.Models;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.HashSet;

public class CharacterDisplaySetModel {
    public interface ICharacterDisplayAddedSubscriber {
        void onCharacterDisplayModelAdded(CharacterDisplayModel characterDisplayModel);
    }

    public interface ICharacterDisplayRemovedSubscriber {
        void onCharacterDisplayModelRemoved(CharacterDisplayModel characterDisplayModel);
    }

    private HashSet<CharacterDisplayModel> characterDisplayModels = new HashSet<>();
    private HashSet<CharacterDisplaySetModel.ICharacterDisplayAddedSubscriber> characterDisplayAddedSubscribers = new HashSet<>();
    private HashSet<CharacterDisplaySetModel.ICharacterDisplayRemovedSubscriber> characterDisplayRemovedSubscribers = new HashSet<>();

    public CharacterDisplayModel getCharacterDisplayModels(AbstractPlayer.PlayerClass playerClass) {
        for (CharacterDisplayModel characterDisplayModel : characterDisplayModels) {
            if(characterDisplayModel.playerClass.getValue() == playerClass)
                return characterDisplayModel;
        }

        return null;
    }

    public void addCharacterDisplayModel(CharacterDisplayModel characterDisplayModel) {
        if(characterDisplayModels.add(characterDisplayModel))
            characterDisplayAddedSubscribers.forEach(s -> s.onCharacterDisplayModelAdded(characterDisplayModel));
    }

    public void removeCharacterDisplayModel(CharacterDisplayModel characterDisplayModel) {
        if(characterDisplayModels.remove(characterDisplayModel))
            characterDisplayRemovedSubscribers.forEach(s -> s.onCharacterDisplayModelRemoved(characterDisplayModel));
    }

    public void addCharacterDisplayAddedSubscriber(CharacterDisplaySetModel.ICharacterDisplayAddedSubscriber subscriber) {
        characterDisplayAddedSubscribers.add(subscriber);
    }

    public void removeCharacterDisplayAddedSubscriber(CharacterDisplaySetModel.ICharacterDisplayAddedSubscriber subscriber) {
        characterDisplayAddedSubscribers.remove(subscriber);
    }

    public void addCharacterDisplayRemovedSubscriber(CharacterDisplaySetModel.ICharacterDisplayRemovedSubscriber subscriber) {
        characterDisplayRemovedSubscribers.add(subscriber);
    }

    public void removeCharacterDisplayRemovedSubscriber(CharacterDisplaySetModel.ICharacterDisplayRemovedSubscriber subscriber) {
        characterDisplayRemovedSubscribers.add(subscriber);
    }
}
