package StreakTheSpire;

import StreakTheSpire.utils.StreakTheSpireTextureDatabase;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import easel.utils.textures.TextureLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@SpireInitializer
public class StreakTheSpire implements PostInitializeSubscriber, PostUpdateSubscriber, RenderSubscriber, AddAudioSubscriber {
    public static final Logger logger = LogManager.getLogger(StreakTheSpire.class);

    public static final String modId = "streak_the_spire";
    public static final String modName = "StreakTheSpire";
    public static final String modDisplayName = "Streak The Spire";
    public static final String modAuthorName = "billyfletcher5000";
    public static final String modDescription = "A Slay The Spire mod to automatically track your streaks, both individual and rotating, with each character.";

    // Config
    private static final String configFileName = "Config";
    private static SpireConfig modSpireConfig = null;

    public static void saveModSpireConfig() throws IOException { modSpireConfig.save(); }
    // ~Config

    private ModPanel settingsPanel;

    public static void initialize() {
        new StreakTheSpire();

        logger.info("Initializing StreakTheSpire!");
        try {
            logger.info("Creating SpireConfig!");
            modSpireConfig = new SpireConfig(modName, configFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StreakTheSpire() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receivePostInitialize() {
        StreakTheSpireTextureDatabase.loadAll();

        settingsPanel = createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);
    }

    private ModPanel createModPanel() {
        ModPanel panel = new ModPanel();

        return panel;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
    }

    private boolean rightMouseDown = false;
    private int previouslySelectedIndex = -1;

    private enum RightMouseDownMode {
        RADIAL_MENU, HIGHLIGHTING, UNHIGHLIGHTING, NONE;
    }

    private RightMouseDownMode rightMouseDownMode = RightMouseDownMode.NONE;


    @Override
    public void receivePostUpdate() {
        // No updates required if we're not on the map screen
        if (!CardCrawlGame.isInARun() || AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) {
            rightMouseDownMode = RightMouseDownMode.NONE;
            return;
        }
    }

    @Override
    public void receiveAddAudio() {
        //BaseMod.addAudio("AUDIO_ID", "StreakTheSpire/AUDIO_WAV.wav");
    }
}