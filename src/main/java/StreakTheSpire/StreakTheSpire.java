package StreakTheSpire;

import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.*;
import StreakTheSpire.UI.*;
import StreakTheSpire.Utils.LoggingLevel;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyTypeAdapters;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import StreakTheSpire.Views.IView;
import StreakTheSpire.Views.PlayerStreakStoreView;
import StreakTheSpire.Views.ViewFactoryManager;
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
import com.megacrit.cardcrawl.core.Settings;
import dorkbox.tweenEngine.TweenEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpireInitializer
public class StreakTheSpire implements PostInitializeSubscriber, PostUpdateSubscriber, RenderSubscriber, AddAudioSubscriber {

    private static final Logger logger = LogManager.getLogger(StreakTheSpire.class);
    public static final LoggingLevel loggingLevel = LoggingLevel.DEBUG;
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

    public static SpireConfig getConfig() { return modSpireConfig; }
    // ~Config

    public static void initialize() {
        new StreakTheSpire();

        logInfo("Initializing StreakTheSpire!");
        try {
            logInfo("Creating SpireConfig!");
            modSpireConfig = new SpireConfig(modName, configFileName);
        } catch (Exception e) {
            logError("Initialize exception:" + e.getMessage());
        }
    }

    private TweenEngine tweenEngine;
    private UIElement rootUIElement;
    private ModPanel settingsPanel;

    private final HashMap<Property<? extends IConfigDataModel>, String> configDataModelToConfigID = new HashMap<>();

    private Property<GameStateModel> gameStateModel;
    private Property<StreakCriteriaModel> streakCriteriaModel;
    private Property<PlayerStreakStoreModel> streakDataModel;
    private Property<CharacterDisplaySetModel> characterDisplaySetModel;

    public StreakTheSpire() {
        BaseMod.subscribe(this);
        instance = this;
    }

    @Override
    public void receivePostInitialize() {
        registerViewFactories();

        tweenEngine = TweenEngine.build();

        StreakTheSpireTextureDatabase.loadAll();

        initialiseGameStateModel();
        initialiseCharacterDisplayModels();
        initialiseStreakDataModel();

        loadConfig();

        PlayerStreakStoreController controller = new PlayerStreakStoreController(streakDataModel.get());

        String report = controller.createStreakDebugReport();
        logDebug(report);

        controller.CalculateStreakData(streakCriteriaModel.get(), false);

        report = controller.createStreakDebugReport();
        logDebug(report);

        logDebug("saveConfig");
        saveConfig();

        initialiseUIRoot();
        createViews();

        SkeletonModifier modifier = new SkeletonModifier();
        modifier.bonesToKeep.add("Neck_");
        modifier.bonesToRemove.add("Chest");
        modifier.bonesToRemove.add("root");
        modifier.bonesToRemove.add("Hips");

        UISpineAnimationElement spineAnimationElement = new UISpineAnimationElement(new Vector2(500, 500), "images/characters/defect/idle/skeleton.atlas", "images/characters/defect/idle/skeleton.json", modifier);
        spineAnimationElement.getAnimationState().setAnimation(0, "Idle", true);
        rootUIElement.addChild(spineAnimationElement);

        settingsPanel = createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);
    }

    private void initialiseUIRoot() {
        rootUIElement = new UIElement();
        rootUIElement.setLocalScale(new Vector2(Settings.xScale, Settings.yScale));
    }

    public void saveConfig() {
        for(Map.Entry<Property<? extends IConfigDataModel>, String> entry : configDataModelToConfigID.entrySet()) {
            IConfigDataModel dataModel = entry.getKey().get();
            IModel iModel = (IModel) dataModel;
            entry.getKey().get().beforeSaveToConfig(modSpireConfig);
            modSpireConfig.setString(entry.getValue(), gson.toJson(entry.getKey().get()));
            logDebug("Saved config: configID: " + entry.getValue() + " class: " + entry.getKey().get().getClass().getName() + " modelProp.uuid: " + entry.getKey().getUUID() + " iModel.uuid: " + iModel.getUUID() + "\njson: " + modSpireConfig.getString(entry.getValue()));
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
        try {
            modSpireConfig.load();
        }
        catch (IOException e) {
            logError("Failed to load config file: " + e);
            return;
        }

        for(Map.Entry<Property<? extends IConfigDataModel>, String> entry : configDataModelToConfigID.entrySet()) {
            String configID = entry.getValue();
            if(modSpireConfig.has(configID)) {
                String configString = modSpireConfig.getString(configID);
                Property<? extends IConfigDataModel> configModelProp = entry.getKey();
                IConfigDataModel configModel = configModelProp.get();
                IModel oldConfigIModel = (IModel) configModel;

                IConfigDataModel loadedConfigModel = gson.fromJson(configString, configModel.getClass());
                configModelProp.setObject(loadedConfigModel);
                IModel newConfigIModel = (IModel) loadedConfigModel;

                loadedConfigModel.afterLoadFromConfig(modSpireConfig);

                logDebug("Loading config ID: " + configID + " configPropUUID: " + configModelProp.getUUID() + " oldConfigModel.uuid: " + oldConfigIModel.getUUID() + " newConfigModel.uuid: " + newConfigIModel.getUUID() + " configString: " + configString);
            }
            else {
                logDebug("Did not load config ID: " + configID);
            }
        }
    }

    private ModPanel createModPanel() {
        ModPanel panel = new ModPanel();

        return panel;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        rootUIElement.render(sb);
    }


    @Override
    public void receivePostUpdate() {
        gameStateModel.get().gameMode.set(CardCrawlGame.mode);
        rootUIElement.update(getDeltaTime());

        tweenEngine.update(getDeltaTime());
    }

    @Override
    public void receiveAddAudio() {
        //BaseMod.addAudio("AUDIO_ID", "StreakTheSpire/AUDIO_WAV.wav");
    }

    private void registerViewFactories() {
        ViewFactoryManager.get().registerViewFactory(PlayerStreakStoreModel.class, PlayerStreakStoreView.FACTORY);
    }

    protected <T extends IConfigDataModel> void registerConfigModel(Property<T> dataModel) {
        if(dataModel == null || dataModel.get() == null)
            return;

        IConfigDataModel configModel = dataModel.get();
        String configID = configModel.getConfigID();
        if(configDataModelToConfigID.values().contains(configID))
            throw new IllegalArgumentException("ConfigID \"" + configID + "\" is already registered!");

        configDataModelToConfigID.put(dataModel, configID);
    }

    protected void initialiseGameStateModel() {
        gameStateModel = new Property<>(new GameStateModel());

        gameStateModel.get().gameMode.set(CardCrawlGame.mode);
    }

    protected void initialiseStreakDataModel() {
        streakDataModel = new Property<>(new PlayerStreakStoreModel());
        streakCriteriaModel = new Property<>(new StreakCriteriaModel());

        registerConfigModel(streakDataModel);
        registerConfigModel(streakCriteriaModel);
    }

    protected void initialiseCharacterDisplayModels() {
        characterDisplaySetModel = new Property<>(new CharacterDisplaySetModel());
        CharacterDisplaySetController controller = new CharacterDisplaySetController(characterDisplaySetModel.get());

        controller.addCharacterIconDisplay(
                AbstractPlayer.PlayerClass.IRONCLAD.toString(),
                StreakTheSpireTextureDatabase.IRONCLAD_ICON.getTexture()
        );

        controller.addCharacterIconDisplay(
                AbstractPlayer.PlayerClass.THE_SILENT.toString(),
                StreakTheSpireTextureDatabase.SILENT_ICON.getTexture()
        );

        controller.addCharacterIconDisplay(
                AbstractPlayer.PlayerClass.DEFECT.toString(),
                StreakTheSpireTextureDatabase.DEFECT_ICON.getTexture()
        );

        controller.addCharacterIconDisplay(
                AbstractPlayer.PlayerClass.WATCHER.toString(),
                StreakTheSpireTextureDatabase.WATCHER_ICON.getTexture()
        );
    }

    private void createViews() {
        createView(streakDataModel.get());
    }

    public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
        IView view = ViewFactoryManager.get().CreateView(model);

        logDebug("View created: " + (view == null ? "null" : view.getClass().getSimpleName()) + " viewIsUIElement: " + (view instanceof UIElement ? "yes" : "no"));
        if(view instanceof UIElement) {
            rootUIElement.addChild((UIElement) view);
        }

        return (TView) view;
    }

    public static void logError(String message) {
        logger.error(message);
    }

    public static void logError(String message, Object... params) {
        logger.error(message, params);
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