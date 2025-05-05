package StreakTheSpire;

import StreakTheSpire.Data.CharacterDisplayModel;
import StreakTheSpire.Data.CharacterDisplaySetModel;
import StreakTheSpire.Data.GameStateModel;
import StreakTheSpire.Data.StreakDataModel;
import StreakTheSpire.Utils.Property;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import basemod.BaseMod;
import basemod.ModImage;
import basemod.ModPanel;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
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

    private static StreakTheSpire instance;
    public static StreakTheSpire getInstance() { return instance; }

    // Config
    private static final String configFileName = "Config";
    private static SpireConfig modSpireConfig = null;

    public static void saveModSpireConfig() throws IOException { modSpireConfig.save(); }
    // ~Config

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



    private ModPanel settingsPanel;
    private ModImage testImage;

    private GameStateModel gameStateModel;
    private StreakDataModel streakDataModel;
    private CharacterDisplaySetModel characterDisplaySetModel;

    private TestModel testModel;

    public StreakTheSpire() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receivePostInitialize() {

        testModel = new TestModel();
        testModel.testString.addOnChangedSubscriber(new Property.ValueChangedSubscriber() {
            public void onValueChanged(Property value) {
                String s = (String)value.getValue();
                logger.info("testString changed to: " + s);
            }
        });

        logger.info("testString value: " + testModel.testString.getValue());
        testModel.testString.setValue("Blamonge!");

        StreakTheSpireTextureDatabase.loadAll();

        testImage = new ModImage(1000, 800, StreakTheSpireTextureDatabase.MOD_ICON.getTexture());


        settingsPanel = createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);
    }

    private ModPanel createModPanel() {
        ModPanel panel = new ModPanel();

        return panel;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        testImage.render(sb);
    }


    @Override
    public void receivePostUpdate() {
        gameStateModel.gameMode.setValue(CardCrawlGame.mode);
    }

    @Override
    public void receiveAddAudio() {
        //BaseMod.addAudio("AUDIO_ID", "StreakTheSpire/AUDIO_WAV.wav");
    }


    protected void initialiseGameStateModel() {
        gameStateModel = new GameStateModel();

        gameStateModel.gameMode.setValue(CardCrawlGame.mode);
    }

    protected void initialiseStreakDataModel() {
        streakDataModel = new StreakDataModel();

        //Test data
        streakDataModel.setStreak(AbstractPlayer.PlayerClass.IRONCLAD, 4);
        streakDataModel.setStreak(AbstractPlayer.PlayerClass.THE_SILENT, 2);
        streakDataModel.setStreak(AbstractPlayer.PlayerClass.DEFECT, 0);
        streakDataModel.setStreak(AbstractPlayer.PlayerClass.WATCHER, 69);
    }

    protected void initialiseCharacterDisplays() {
        characterDisplaySetModel = new CharacterDisplaySetModel();

        characterDisplaySetModel.addCharacterDisplayModel(new CharacterDisplayModel(
                AbstractPlayer.PlayerClass.IRONCLAD,
                StreakTheSpireTextureDatabase.IRONCLAD_ICON.getTexture()
        ));

        characterDisplaySetModel.addCharacterDisplayModel(new CharacterDisplayModel(
                AbstractPlayer.PlayerClass.THE_SILENT,
                StreakTheSpireTextureDatabase.SILENT_ICON.getTexture()
        ));

        characterDisplaySetModel.addCharacterDisplayModel(new CharacterDisplayModel(
                AbstractPlayer.PlayerClass.DEFECT,
                StreakTheSpireTextureDatabase.DEFECT_ICON.getTexture()
        ));

        characterDisplaySetModel.addCharacterDisplayModel(new CharacterDisplayModel(
                AbstractPlayer.PlayerClass.WATCHER,
                StreakTheSpireTextureDatabase.WATCHER_ICON.getTexture()
        ));
    }
}