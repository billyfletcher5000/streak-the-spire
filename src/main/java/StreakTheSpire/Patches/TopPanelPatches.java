package StreakTheSpire.Patches;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

public class TopPanelPatches {
    @SpirePatch(
            clz = TopPanel.class,
            method = "render",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderPatch
    {
        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance, SpriteBatch sb)
        {
            StreakTheSpire.get().receiveTopPanelRender(sb);
        }
    }
}
