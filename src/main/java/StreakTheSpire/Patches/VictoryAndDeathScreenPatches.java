package StreakTheSpire.Patches;

import StreakTheSpire.StreakTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

public class VictoryAndDeathScreenPatches {
    @SpirePatch(
            clz = DeathScreen.class,
            method = "submitDefeatMetrics",
            paramtypez = {MonsterGroup.class}
    )
    public static class DeathScreenDefeatMetricsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(DeathScreen __instance, MonsterGroup m)
        {
            StreakTheSpire.logInfo("Game End: Submitting Death on Death Screen");
            StreakTheSpire.get().notifyRunEnd();
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "submitVictoryMetrics"
    )
    public static class DeathScreenVictoryMetricsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(DeathScreen __instance)
        {
            StreakTheSpire.logInfo("Game End: Submitting Victory on Death Screen");
            StreakTheSpire.get().notifyRunEnd();
        }
    }

    @SpirePatch(
            clz = VictoryScreen.class,
            method = "submitVictoryMetrics"
    )
    public static class VictoryScreenVictoryMetricsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(VictoryScreen __instance)
        {
            StreakTheSpire.logInfo("Game End: Submitting Victory on Victory Screen");
            StreakTheSpire.get().notifyRunEnd();
        }
    }
}
