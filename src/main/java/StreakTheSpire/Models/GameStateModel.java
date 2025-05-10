package StreakTheSpire.Models;

import StreakTheSpire.Utils.Property;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class GameStateModel {
    public Property<CardCrawlGame.GameMode> gameMode = new Property<CardCrawlGame.GameMode>(CardCrawlGame.GameMode.SPLASH);
}
