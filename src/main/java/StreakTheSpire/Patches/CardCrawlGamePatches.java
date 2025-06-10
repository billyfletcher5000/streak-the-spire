package StreakTheSpire.Patches;

import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.PlayerStreakModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.StreakTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class CardCrawlGamePatches {
    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "reloadPrefs"
    )
    public static class ReloadPrefsPatch
    {
        @SpirePostfixPatch
        public static void Postfix()
        {
            PlayerStreakStoreModel playerStreakStoreModel = StreakTheSpire.get().getStreakStoreDataModel();
            PlayerStreakStoreController playerStreakStoreController = new PlayerStreakStoreController(playerStreakStoreModel);
            StreakCriteriaModel criteriaModel = StreakTheSpire.get().getStreakCriteriaModel();
            playerStreakStoreController.calculateStreakData(criteriaModel, true);
        }
    }
}
