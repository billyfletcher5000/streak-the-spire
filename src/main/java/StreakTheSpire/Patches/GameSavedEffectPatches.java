package StreakTheSpire.Patches;

import StreakTheSpire.Models.DisplayPreferencesModel;
import StreakTheSpire.StreakTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.vfx.GameSavedEffect;

public class GameSavedEffectPatches {
    @SpirePatch(
            clz = GameSavedEffect.class,
            method = "update",
            paramtypez = {}
    )
    public static class PreUpdatePatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(GameSavedEffect __instance)
        {
            DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
            if(preferences.suppressSaveNotification.get()) {
                __instance.isDone = true;
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}
