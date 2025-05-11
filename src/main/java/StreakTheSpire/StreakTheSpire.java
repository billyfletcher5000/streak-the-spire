package StreakTheSpire;

import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.*;
import StreakTheSpire.UI.*;
import StreakTheSpire.Utils.LoggingLevel;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyTypeAdapters;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.helpers.FontHelper;
import dorkbox.tweenEngine.Timeline;
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.TweenEquations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

@SpireInitializer
public class StreakTheSpire implements PostInitializeSubscriber, PostUpdateSubscriber, RenderSubscriber, AddAudioSubscriber {
    private static final Logger logger = LogManager.getLogger(StreakTheSpire.class);
    public static final LoggingLevel loggingLevel = LoggingLevel.INFO;
    public static float getDeltaTime() { return Gdx.graphics.getDeltaTime(); }
    public static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(PropertyTypeAdapters.PropertyTypeAdapter.FACTORY).create();

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

    public static void saveModSpireConfig() throws IOException { instance.saveConfig(); }
    // ~Config

    public static void initialize() {
        new StreakTheSpire();

        logInfo("Initializing StreakTheSpire!");
        try {
            logInfo("Creating SpireConfig!");
            modSpireConfig = new SpireConfig(modName, configFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TweenEngine tweenEngine;

    private ModPanel settingsPanel;
    private UIImageElement testImage;
    private UINineSliceElement nineSliceTest;

    private ArrayList<IConfigDataModel> configDataModels = new ArrayList<>();

    private GameStateModel gameStateModel;
    private StreakCriteriaModel streakCriteriaModel;
    private PlayerStreakStoreModel streakDataModel;
    private CharacterDisplaySetModel characterDisplaySetModel;

    private TestModel testModel;

    private String testStringStore = "Crab";

    public StreakTheSpire() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receivePostInitialize() {
        tweenEngine = TweenEngine.build();

        StreakTheSpireTextureDatabase.loadAll();

        initialiseGameStateModel();
        initialiseCharacterDisplayModels();
        initialiseStreakDataModel();

        loadConfig();

        PlayerStreakStoreController controller = new PlayerStreakStoreController(streakDataModel);

        String report = controller.createStreakDebugReport();
        logDebug(report);

        controller.CalculateStreakData(streakCriteriaModel, false);

        report = controller.createStreakDebugReport();
        logDebug(report);

        logDebug("saveConfig");
        saveConfig();

        settingsPanel = createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);

        testModel = new TestModel();
        testModel.testString.addOnChangedSubscriber(new Property.ValueChangedSubscriber() {
            @Override
            public void onValueChanged() {
                logDebug("testString changed to: " + testModel.testString);
                testStringStore = testModel.testString.getValue();
            }
        });


        logDebug("testString value: " + testModel.testString.getValue());
        testModel.testString.setValue("Blamonge!");
        logDebug("testStringStore value: " + testModel.testString.getValue());
/*
        testImage = new UIImageElement(new Vector2(1000, 800), StreakTheSpireTextureDatabase.IRONCLAD_ICON.getTexture());
        testImage.addChild(new UIImageElement(new Vector2(50f, 0f), new Vector2(0.25f, 0.5f), StreakTheSpireTextureDatabase.MOD_ICON.getTexture()));

        Timeline sequence = tweenEngine.createSequential();
        sequence.push(tweenEngine.to(testImage, UIElement.TweenTypes.POSITION_XY, 5.0f).target(200, 400).ease(TweenEquations.Linear));
        sequence.push(tweenEngine.to(testImage, UIElement.TweenTypes.POSITION_XY, 5.0f).target(1000, 400).ease(TweenEquations.Linear));
        sequence.repeatAutoReverse(10, 1f);
        sequence.start();



        NineSliceTexture nineSliceTexture = new NineSliceTexture(StreakTheSpireTextureDatabase.TIP_BOX_NINESLICE.getTexture(), 48, 48, 35, 35);
        nineSliceTest = new UINineSliceElement(new Vector2(1920 * 0.5f, 1080 * 0.5f), nineSliceTexture, new Vector2(450, 240));
        logger.info("tipBodyFont:" + (FontHelper.tipBodyFont != null ? FontHelper.tipBodyFont : "null"));
        nineSliceTest.addChild(new UITextElement(new Vector2(0f, 0f), FontHelper.tipBodyFont, "Lorem ipsum hullabaloo plonk plonk flabblecrunk.", new Vector2(350, 200)));

        Timeline alphaSequence = tweenEngine.createSequential();
        alphaSequence.push(tweenEngine.to(nineSliceTest, UIElement.TweenTypes.ALPHA, 5.0f).target(0f));
        alphaSequence.push(tweenEngine.to(nineSliceTest, UIElement.TweenTypes.ALPHA, 5.0f).target(1f));
        alphaSequence.delay(10.0f);
        alphaSequence.repeat(10, 0f);
        alphaSequence.start();
 */
    }

    private void saveConfig() {
        for(IConfigDataModel dataModel : configDataModels) {
            dataModel.saveToConfig(modSpireConfig);
        }

        try {
            modSpireConfig.save();
        }
        catch (IOException e) {
            logError("Failed to save config file: " + e);
        }
    }

    private void loadConfig() {
        logInfo("Loading config!");
        for(IConfigDataModel dataModel : configDataModels) {
            dataModel.loadFromConfig(modSpireConfig);
        }
    }

    private ModPanel createModPanel() {
        ModPanel panel = new ModPanel();

        return panel;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        /*
        testImage.render(sb);
        nineSliceTest.render(sb);
         */
    }


    @Override
    public void receivePostUpdate() {
        gameStateModel.gameMode.setValue(CardCrawlGame.mode);

        tweenEngine.update(Gdx.graphics.getDeltaTime());
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
        streakDataModel = new PlayerStreakStoreModel();
        streakCriteriaModel = new StreakCriteriaModel();

        configDataModels.add(streakDataModel);
        configDataModels.add(streakCriteriaModel);
    }

    protected void initialiseCharacterDisplayModels() {
        characterDisplaySetModel = new CharacterDisplaySetModel();
        CharacterDisplaySetController controller = new CharacterDisplaySetController(characterDisplaySetModel);

        controller.addCharacterDisplay(
                AbstractPlayer.PlayerClass.IRONCLAD.toString(),
                StreakTheSpireTextureDatabase.IRONCLAD_ICON.getTexture()
        );

        controller.addCharacterDisplay(
                AbstractPlayer.PlayerClass.THE_SILENT.toString(),
                StreakTheSpireTextureDatabase.SILENT_ICON.getTexture()
        );

        controller.addCharacterDisplay(
                AbstractPlayer.PlayerClass.DEFECT.toString(),
                StreakTheSpireTextureDatabase.DEFECT_ICON.getTexture()
        );

        controller.addCharacterDisplay(
                AbstractPlayer.PlayerClass.WATCHER.toString(),
                StreakTheSpireTextureDatabase.WATCHER_ICON.getTexture()
        );
    }

    public static void logError(String message) {
        if(loggingLevel.ordinal() >= LoggingLevel.ERROR.ordinal()) {
            logger.error(message);
        }
    }

    public static void logError(String message, Object... params) {
        if(loggingLevel.ordinal() >= LoggingLevel.ERROR.ordinal()) {
            logger.error(message, params);
        }
    }

    public static void logWarning(String message) {
        if(loggingLevel.ordinal() >= LoggingLevel.WARN.ordinal()) {
            logger.warn(message);
        }
    }

    public static void logWarning(String message, Object... params) {
        if(loggingLevel.ordinal() >= LoggingLevel.WARN.ordinal()) {
            logger.warn(message, params);
        }
    }

    public static void logInfo(String message) {
        if(loggingLevel.ordinal() >= LoggingLevel.INFO.ordinal()) {
            logger.info(message);
        }
    }

    public static void logInfo(String message, Object... params) {
        if(loggingLevel.ordinal() >= LoggingLevel.INFO.ordinal()) {
            logger.info(message, params);
        }
    }

    public static void logDebug(String message) {
        if(loggingLevel.ordinal() >= LoggingLevel.DEBUG.ordinal()) {
            logger.debug(message);
        }
    }

    public static void logDebug(String message, Object... params) {
        if(loggingLevel.ordinal() >= LoggingLevel.DEBUG.ordinal()) {
            logger.debug(message, params);
        }
    }

    public static void logTrace(String message) {
        if(loggingLevel.ordinal() >= LoggingLevel.TRACE.ordinal()) {
            logger.trace(message);
        }
    }

    public static void logTrace(String message, Object... params) {
        if(loggingLevel.ordinal() >= LoggingLevel.TRACE.ordinal()) {
            logger.trace(message, params);
        }
    }
}