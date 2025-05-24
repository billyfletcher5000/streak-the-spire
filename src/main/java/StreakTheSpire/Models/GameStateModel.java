package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class GameStateModel implements IModel {
    public Property<CardCrawlGame.GameMode> gameMode = new Property<CardCrawlGame.GameMode>(CardCrawlGame.GameMode.SPLASH);

    public GameStateModel() {}
}
