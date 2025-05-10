package StreakTheSpire;

import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Models.CharacterDisplayModel;
import StreakTheSpire.Models.CharacterDisplaySetModel;
import StreakTheSpire.Models.GameStateModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.UI.*;
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
import dorkbox.tweenEngine.Timeline;
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.TweenEquations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@SpireInitializer
public class StreakTheSpire implements PostInitializeSubscriber, PostUpdateSubscriber, RenderSubscriber, AddAudioSubscriber {
    public static final Logger logger = LogManager.getLogger(StreakTheSpire.class);
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

    private TweenEngine tweenEngine;

    private ModPanel settingsPanel;
    private UIImageElement testImage;
    private UINineSliceElement nineSliceTest;

    private GameStateModel gameStateModel;
    private PlayerStreakStoreModel streakDataModel;
    private CharacterDisplaySetModel characterDisplaySetModel;

    private TestModel testModel;

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

        settingsPanel = createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);

        testModel = new TestModel();
        testModel.testString.addOnChangedSubscriber(new Property.ValueChangedSubscriber() {
            @Override
            public void onValueChanged() {
                logger.info("testString changed to: " + testModel.testString);
            }
        });

        logger.info("testString value: " + testModel.testString.getValue());
        testModel.testString.setValue("Blamonge!");

        testImage = new UIImageElement(new Vector2(1000, 800), StreakTheSpireTextureDatabase.IRONCLAD_ICON.getTexture());
        testImage.addChild(new UIImageElement(new Vector2(50f, 0f), new Vector2(0.25f, 0.5f), StreakTheSpireTextureDatabase.MOD_ICON.getTexture()));

        Timeline sequence = tweenEngine.createSequential();
        sequence.push(tweenEngine.to(testImage, UIElement.TweenTypes.POSITION_XY, 5.0f).target(200, 400).ease(TweenEquations.Linear));
        sequence.push(tweenEngine.to(testImage, UIElement.TweenTypes.POSITION_XY, 5.0f).target(1000, 400).ease(TweenEquations.Linear));
        sequence.repeatAutoReverse(10, 1f);
        sequence.start();



        NineSliceTexture nineSliceTexture = new NineSliceTexture(StreakTheSpireTextureDatabase.TIP_BOX_NINESLICE.getTexture(), 48, 48, 35, 35);
        nineSliceTest = new UINineSliceElement(new Vector2(1920 * 0.5f, 1080 * 0.5f), nineSliceTexture, new Vector2(450, 240));

        Timeline alphaSequence = tweenEngine.createSequential();
        alphaSequence.push(tweenEngine.to(nineSliceTest, UIVisualElement.TweenTypes.COLOR_A, 5.0f).target(0f));
        alphaSequence.push(tweenEngine.to(nineSliceTest, UIVisualElement.TweenTypes.COLOR_A, 5.0f).target(1f));
        alphaSequence.delay(10.0f);
        alphaSequence.repeat(10, 0f);
        alphaSequence.start();
    }

    private ModPanel createModPanel() {
        ModPanel panel = new ModPanel();

        return panel;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        testImage.render(sb);
        nineSliceTest.render(sb);
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
}