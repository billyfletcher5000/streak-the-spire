package StreakTheSpire.Data;

import StreakTheSpire.Utils.Property;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class CharacterDisplayModel {
    public Property<AbstractPlayer.PlayerClass> playerClass;
    public Property<Texture> iconTexture;

    public CharacterDisplayModel(AbstractPlayer.PlayerClass playerClass, Texture iconTexture) {
        this.playerClass = new Property<>(playerClass);
        this.iconTexture = new Property<>(iconTexture);
    }
}
