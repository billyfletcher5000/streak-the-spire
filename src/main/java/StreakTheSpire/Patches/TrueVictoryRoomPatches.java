package StreakTheSpire.Patches;

import StreakTheSpire.StreakTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;

public class TrueVictoryRoomPatches {
    @SpirePatch(
            clz = TrueVictoryRoom.class,
            method = "onPlayerEntry"
    )
    public static class TrueVictoryScreenOnPlayerEntryPatch
    {
        @SpirePostfixPatch
        public static void Postfix(TrueVictoryRoom __instance)
        {
            StreakTheSpire.get().notifyTrueVictoryCutsceneStart();
        }
    }

    @SpirePatch(
            clz = TrueVictoryRoom.class,
            method = "dispose"
    )
    public static class TrueVictoryScreenDisposePatch
    {
        @SpirePostfixPatch
        public static void Postfix(TrueVictoryRoom __instance)
        {
            StreakTheSpire.get().notifyTrueVictoryCutsceneEnd();
        }
    }
}
