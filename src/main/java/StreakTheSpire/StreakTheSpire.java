package StreakTheSpire;

import StreakTheSpire.Ceremonies.CeremonyManager;
import StreakTheSpire.Ceremonies.Panel.LightFlourishScoreDecreaseCeremony;
import StreakTheSpire.Ceremonies.Panel.LightFlourishScoreIncreaseCeremony;
import StreakTheSpire.Ceremonies.Panel.SimpleTextScoreChangeCeremony;
import StreakTheSpire.Controllers.BorderStyleSetController;
import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.*;
import StreakTheSpire.UI.*;
import StreakTheSpire.Utils.FontCache;
import StreakTheSpire.Utils.Lifetime.LifetimeManager;
import StreakTheSpire.Utils.LoggingLevel;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyTypeAdapters;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import StreakTheSpire.Utils.TextureCache;
import StreakTheSpire.Views.*;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
public class StreakTheSpire implements PostInitializeSubscriber, PostUpdateSubscriber, RenderSubscriber, PostRenderSubscriber, PreRoomRenderSubscriber, AddAudioSubscriber {

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
    public static StreakTheSpire get() { return instance; }

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

    private TextureCache textureCache = new TextureCache();
    private FontCache fontCache = new FontCache();
    private TweenEngine tweenEngine;
    private UIElement rootUIElement;
    private UIElement debugRootUIElement;
    private CursorOverride cursorOverride;
    private ModPanel settingsPanel;
    private boolean trueVictoryCutsceneActive = false;

    private final HashMap<Property<? extends IConfigDataModel>, String> configDataModelToConfigID = new HashMap<>();

    private Property<GameStateModel> gameStateModel;
    private Property<StreakCriteriaModel> streakCriteriaModel;
    private Property<DisplayPreferencesModel> displayPreferencesModel;
    private Property<PlayerStreakStoreModel> streakStoreDataModel;
    private Property<CharacterDisplaySetModel> characterDisplaySetModel;
    private Property<CeremonyPreferencesModel> ceremonyPreferencesModel;
    private Property<BorderStyleSetModel> borderStyleSetModel;

    private boolean showDebug = false;

    public TextureCache getTextureCache() { return textureCache; }
    public FontCache getFontCache() { return fontCache; }
    public TweenEngine getTweenEngine() { return tweenEngine; }
    public CursorOverride getCursorOverride() { return cursorOverride; }
    public GameStateModel getGameStateModel() { return gameStateModel.get(); }
    public StreakCriteriaModel getStreakCriteriaModel() { return streakCriteriaModel.get(); }
    public DisplayPreferencesModel getDisplayPreferencesModel() { return displayPreferencesModel.get(); }
    public PlayerStreakStoreModel getStreakStoreDataModel() { return streakStoreDataModel.get(); }
    public CharacterDisplaySetModel getCharacterDisplaySetModel() { return characterDisplaySetModel.get(); }
    public CeremonyPreferencesModel getCeremonyPreferences() { return ceremonyPreferencesModel.get(); }
    public BorderStyleSetModel getBorderStyles() { return borderStyleSetModel.get(); }

    public StreakTheSpire() {
        BaseMod.subscribe(this);
        instance = this;
    }

    @Override
    public void receivePostInitialize() {
        registerViewFactories();
        registerCeremonies();

        tweenEngine = TweenEngine.build();

        StreakTheSpireTextureDatabase.loadAll();
        initialiseFonts();

        initialisePreferenceModels();
        initialiseGameStateModel();
        initialiseCharacterDisplayModels();
        initialiseCeremonyModels();
        initialiseStreakDataModel();
        initialiseBorderStyleModels();

        loadConfig();

        displayPreferencesModel.get().renderLayer.set(DisplayPreferencesModel.RenderLayer.Default);

        PlayerStreakStoreController controller = new PlayerStreakStoreController(streakStoreDataModel.get());

        String report = controller.createStreakDebugReport();
        logDebug(report);

        controller.calculateStreakData(streakCriteriaModel.get(), false);

        report = controller.createStreakDebugReport();
        logDebug(report);

        logDebug("saveConfig");
        saveConfig();

        initialiseUI();
        createViews();

        Texture tex = textureCache.getTexture("StreakTheSpire/textures/ui/button_highlighted.png");
        UIElement elem = new UIImageElement(new Vector2(500f, 500f), tex);
        tex = textureCache.getTexture("StreakTheSpire/textures/ui/button_overlay_border.png");
        elem.addChild(new UIImageElement(new Vector2(0f, 0f), tex));
        tex = textureCache.getTexture("StreakTheSpire/textures/ui/button_overlay_1.png");
        elem.addChild(new UIImageElement(new Vector2(0f, 0f), tex));
        rootUIElement.addChild(elem);

        tex = textureCache.getTexture("StreakTheSpire/textures/ui/button_normal.png");
        elem = new UIImageElement(new Vector2(550f, 500f), tex);
        tex = textureCache.getTexture("StreakTheSpire/textures/ui/button_overlay_border.png");
        elem.addChild(new UIImageElement(new Vector2(0f, 0f), tex));
        tex = textureCache.getTexture("StreakTheSpire/textures/ui/button_overlay_off.png");
        elem.addChild(new UIImageElement(new Vector2(0f, 0f), tex));
        rootUIElement.addChild(elem);

        settingsPanel = createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);
    }

    private void initialiseUI() {
        rootUIElement = new UIElement();
        rootUIElement.setLocalScale(new Vector2(Settings.scale, Settings.scale));

        debugRootUIElement = new UIElement();
        debugRootUIElement.setLocalScale(new Vector2(Settings.scale, Settings.scale));

        cursorOverride = new CursorOverride(gameStateModel.get().editModeActive);
    }

    public void saveConfig() {
        for(Map.Entry<Property<? extends IConfigDataModel>, String> entry : configDataModelToConfigID.entrySet()) {
            entry.getKey().get().beforeSaveToConfig(modSpireConfig);
            modSpireConfig.setString(entry.getValue(), gson.toJson(entry.getKey().get()));
            logDebug("Saved config: configID: " + entry.getValue() + " class: " + entry.getKey().get().getClass().getName() + "\njson: " + modSpireConfig.getString(entry.getValue()));
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

                IConfigDataModel loadedConfigModel = gson.fromJson(configString, configModel.getClass());
                configModelProp.setObject(loadedConfigModel);

                loadedConfigModel.afterLoadFromConfig(modSpireConfig);

                logDebug("Loading config ID: " + configID);
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
    public void receivePreRoomRender(SpriteBatch spriteBatch) {
        if(!trueVictoryCutsceneActive && displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.PreRoom)
            performRender(spriteBatch);
    }

    public void receiveTopPanelRender(SpriteBatch spriteBatch) {
        if(trueVictoryCutsceneActive || displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.TopPanel)
            performRender(spriteBatch);
    }

    @Override
    public void receiveRender(SpriteBatch spriteBatch) {
        if(!trueVictoryCutsceneActive && displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.Default)
            performRender(spriteBatch);
    }

    @Override
    public void receivePostRender(SpriteBatch spriteBatch) {
        if(!trueVictoryCutsceneActive && displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.AboveAll)
            performRender(spriteBatch);

        cursorOverride.render(spriteBatch);
    }

    private void performRender(SpriteBatch spriteBatch) {
        rootUIElement.render(spriteBatch);
        debugRootUIElement.render(spriteBatch);
    }


    @Override
    public void receivePostUpdate() {
        gameStateModel.get().gameMode.set(CardCrawlGame.mode);
        gameStateModel.get().editModeActive.set(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT));

        //region Debug inputs
        if(Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            PlayerStreakStoreController controller = new PlayerStreakStoreController(streakStoreDataModel.get());
            PlayerStreakModel defectModel = controller.getStreakModel("DEFECT");
            defectModel.currentStreak.set(defectModel.currentStreak.get() + 1);
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            PlayerStreakStoreController controller = new PlayerStreakStoreController(streakStoreDataModel.get());
            PlayerStreakModel defectModel = controller.getStreakModel("DEFECT");
            defectModel.currentStreak.set(0);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            displayPreferencesModel.get().borderStyle.set("Invisible");
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            displayPreferencesModel.get().borderStyle.set("TipBox");
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            displayPreferencesModel.get().borderStyle.set("TopBar");
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            showDebug = !showDebug;
            if(showDebug)
                rootUIElement.showDebugDimensionsDisplay(true);
            else
                rootUIElement.hideDebugDimensionsDisplay(true);
        }
        //endregion

        rootUIElement.update(getDeltaTime());

        tweenEngine.update(getDeltaTime());

        debugRootUIElement.update(getDeltaTime());

        LifetimeManager.ProcessDestroyed();
    }

    @Override
    public void receiveAddAudio() {
        //BaseMod.addAudio("AUDIO_ID", "StreakTheSpire/AUDIO_WAV.wav");
    }

    public void notifyRunEnd() {
        PlayerStreakStoreController controller = new PlayerStreakStoreController(streakStoreDataModel.get());
        controller.calculateStreakData(streakCriteriaModel.get(), false);
    }

    public void notifyTrueVictoryCutsceneStart() {
        trueVictoryCutsceneActive = true;
    }

    public void notifyTrueVictoryCutsceneEnd() {
        trueVictoryCutsceneActive = false;
    }

    private void registerViewFactories() {
        ViewFactoryManager.get().registerViewFactory(PlayerStreakStoreModel.class, PlayerStreakStoreView.FACTORY);
        ViewFactoryManager.get().registerViewFactory(PlayerStreakModel.class, PlayerStreakView.FACTORY);
        ViewFactoryManager.get().registerViewFactory(CharacterSkeletonDisplayModel.class, CharacterSkeletonDisplayView.FACTORY);
        ViewFactoryManager.get().registerViewFactory(CharacterIconDisplayModel.class, CharacterIconDisplayView.FACTORY);
        ViewFactoryManager.get().registerViewFactory(CharacterTextDisplayModel.class, CharacterTextDisplayView.FACTORY);
    }

    private void registerCeremonies() {
        CeremonyManager.get().registerScoreChangeCeremony(SimpleTextScoreChangeCeremony.class);
        CeremonyManager.get().registerScoreChangeCeremony(LightFlourishScoreIncreaseCeremony.class);
        CeremonyManager.get().registerScoreChangeCeremony(LightFlourishScoreDecreaseCeremony.class);
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

    private void initialiseFonts() {
        fontCache.createSDFFont("Kreon_SDF_Outline_Shadow",
                "StreakTheSpire/fonts/Kreon_Bold_SDF_Numbers.fnt",
                "StreakTheSpire/fonts/Kreon_Bold_SDF_Numbers.png",
                textureCache);
    }

    private void initialisePreferenceModels() {
        displayPreferencesModel = new Property<>(new DisplayPreferencesModel());
        registerConfigModel(displayPreferencesModel);
    }

    protected void initialiseGameStateModel() {
        gameStateModel = new Property<>(new GameStateModel());

        gameStateModel.get().gameMode.set(CardCrawlGame.mode);
    }

    protected void initialiseStreakDataModel() {
        streakStoreDataModel = new Property<>(new PlayerStreakStoreModel());
        streakCriteriaModel = new Property<>(new StreakCriteriaModel());

        registerConfigModel(streakStoreDataModel);
        registerConfigModel(streakCriteriaModel);
    }

    protected void initialiseCharacterDisplayModels() {
        characterDisplaySetModel = new Property<>(new CharacterDisplaySetModel());

        // TODO: Evaluate loading all this from Json, it does work, but doing it in code ensures it's always correct to
        //       version and StS main game constants can be used. Should add a mechanism for loading additional models though.
        CharacterDisplaySetController controller = new CharacterDisplaySetController(characterDisplaySetModel.get());

        CharacterIconDisplayModel ironcladIconModel = new CharacterIconDisplayModel();
        ironcladIconModel.identifier.set(AbstractPlayer.PlayerClass.IRONCLAD.toString());
        ironcladIconModel.iconTexture.set(StreakTheSpireTextureDatabase.IRONCLAD_ICON.getTexture());
        controller.addCharacterDisplayModel(ironcladIconModel);

        CharacterIconDisplayModel silentIconModel = new CharacterIconDisplayModel();
        silentIconModel.identifier.set(AbstractPlayer.PlayerClass.THE_SILENT.toString());
        silentIconModel.iconTexture.set(StreakTheSpireTextureDatabase.SILENT_ICON.getTexture());
        controller.addCharacterDisplayModel(silentIconModel);

        CharacterIconDisplayModel defectIconModel = new CharacterIconDisplayModel();
        defectIconModel.identifier.set(AbstractPlayer.PlayerClass.DEFECT.toString());
        defectIconModel.iconTexture.set(StreakTheSpireTextureDatabase.DEFECT_ICON.getTexture());
        controller.addCharacterDisplayModel(defectIconModel);

        CharacterIconDisplayModel watcherIconModel = new CharacterIconDisplayModel();
        watcherIconModel.identifier.set(AbstractPlayer.PlayerClass.WATCHER.toString());
        watcherIconModel.iconTexture.set(StreakTheSpireTextureDatabase.WATCHER_ICON.getTexture());
        controller.addCharacterDisplayModel(watcherIconModel);

        CharacterSkeletonDisplayModel ironcladSkeletonDisplayModel = new CharacterSkeletonDisplayModel();
        ironcladSkeletonDisplayModel.identifier.set(AbstractPlayer.PlayerClass.IRONCLAD.toString());
        ironcladSkeletonDisplayModel.baseDimensions.set(new Vector2(60, 60));
        ironcladSkeletonDisplayModel.skeletonOffset.set(new Vector2(-20, 2)); // Note: This is applied BEFORE rotation
        ironcladSkeletonDisplayModel.skeletonAtlasUrl.set("images/characters/ironclad/idle/skeleton.atlas");
        ironcladSkeletonDisplayModel.skeletonJsonUrl.set("images/characters/ironclad/idle/skeleton.json");
        ironcladSkeletonDisplayModel.skeletonBonesToKeep.add("Head");
        ironcladSkeletonDisplayModel.skeletonBonesToRemove.add("Neck_2");
        ironcladSkeletonDisplayModel.skeletonBonesToRemove.add("root");
        ironcladSkeletonDisplayModel.skeletonBonesToRemove.add("Hips");
        ironcladSkeletonDisplayModel.skeletonRotationAdjustment.set(100.0f);
        ironcladSkeletonDisplayModel.skeletonIdleAnimationSpeed.set(0.6f);
        controller.addCharacterDisplayModel(ironcladSkeletonDisplayModel);

        CharacterSkeletonDisplayModel silentSkeletonDisplayModel = new CharacterSkeletonDisplayModel();
        silentSkeletonDisplayModel.identifier.set(AbstractPlayer.PlayerClass.THE_SILENT.toString());
        silentSkeletonDisplayModel.baseDimensions.set(new Vector2(112, 112));
        silentSkeletonDisplayModel.skeletonOffset.set(new Vector2(-55, 4)); // Note: This is applied BEFORE rotation
        silentSkeletonDisplayModel.skeletonAtlasUrl.set("images/characters/theSilent/idle/skeleton.atlas");
        silentSkeletonDisplayModel.skeletonJsonUrl.set("images/characters/theSilent/idle/skeleton.json");
        silentSkeletonDisplayModel.skeletonBonesToKeep.add("Spine_3");
        silentSkeletonDisplayModel.skeletonBonesToRemove.add("Spine_2");
        silentSkeletonDisplayModel.skeletonBonesToRemove.add("root");
        silentSkeletonDisplayModel.skeletonBonesToRemove.add("Hips");
        silentSkeletonDisplayModel.skeletonBonesToRemove.add("Tail_1");
        silentSkeletonDisplayModel.skeletonBonesToRemove.add("Shadow");
        silentSkeletonDisplayModel.skeletonBonesToRemove.add("Robe_down");
        silentSkeletonDisplayModel.skeletonRotationAdjustment.set(80f);
        silentSkeletonDisplayModel.skeletonIdleAnimationSpeed.set(0.6f);
        controller.addCharacterDisplayModel(silentSkeletonDisplayModel);

        CharacterSkeletonDisplayModel defectSkeletonDisplayModel = new CharacterSkeletonDisplayModel();
        defectSkeletonDisplayModel.identifier.set(AbstractPlayer.PlayerClass.DEFECT.toString());
        defectSkeletonDisplayModel.baseDimensions.set(new Vector2(64, 64));
        defectSkeletonDisplayModel.skeletonOffset.set(new Vector2(-24, 8)); // Note: This is applied BEFORE rotation
        defectSkeletonDisplayModel.skeletonAtlasUrl.set("images/characters/defect/idle/skeleton.atlas");
        defectSkeletonDisplayModel.skeletonJsonUrl.set("images/characters/defect/idle/skeleton.json");
        defectSkeletonDisplayModel.skeletonBonesToKeep.add("Neck_3");
        defectSkeletonDisplayModel.skeletonBonesToRemove.add("Neck_2");
        defectSkeletonDisplayModel.skeletonBonesToRemove.add("Chest");
        defectSkeletonDisplayModel.skeletonBonesToRemove.add("root");
        defectSkeletonDisplayModel.skeletonBonesToRemove.add("Hips");
        defectSkeletonDisplayModel.skeletonRotationAdjustment.set(60.0f);
        defectSkeletonDisplayModel.skeletonIdleAnimationSpeed.set(0.2f);
        controller.addCharacterDisplayModel(defectSkeletonDisplayModel);

        CharacterSkeletonDisplayModel watcherSkeletonDisplayModel = new CharacterSkeletonDisplayModel();
        watcherSkeletonDisplayModel.identifier.set(AbstractPlayer.PlayerClass.WATCHER.toString());
        watcherSkeletonDisplayModel.baseDimensions.set(new Vector2(64, 64));
        watcherSkeletonDisplayModel.skeletonOffset.set(new Vector2(-20, 0)); // Note: This is applied BEFORE rotation
        watcherSkeletonDisplayModel.skeletonAtlasUrl.set("images/characters/watcher/idle/skeleton.atlas");
        watcherSkeletonDisplayModel.skeletonJsonUrl.set("images/characters/watcher/idle/skeleton.json");
        watcherSkeletonDisplayModel.skeletonBonesToKeep.add("Head");
        watcherSkeletonDisplayModel.skeletonBonesToRemove.add("IK_leg_L_goal");
        watcherSkeletonDisplayModel.skeletonBonesToRemove.add("root");
        watcherSkeletonDisplayModel.skeletonBonesToRemove.add("HIPS");
        watcherSkeletonDisplayModel.skeletonBonesToRemove.add("Arm_R_");
        watcherSkeletonDisplayModel.skeletonBonesToRemove.add("Neck");
        watcherSkeletonDisplayModel.skeletonRotationAdjustment.set(80f);
        watcherSkeletonDisplayModel.skeletonIdleAnimationSpeed.set(0.8f);
        controller.addCharacterDisplayModel(watcherSkeletonDisplayModel);

        CharacterSkeletonDisplayModel rotatingSkeletonDisplayModel = new CharacterSkeletonDisplayModel();
        rotatingSkeletonDisplayModel.identifier.set(PlayerStreakStoreModel.RotatingPlayerIdentifier);
        rotatingSkeletonDisplayModel.baseDimensions.set(new Vector2(61.64f * 1.325f, 64.4f * 1.325f));
        rotatingSkeletonDisplayModel.skeletonOffset.set(new Vector2(4, 0)); // Note: This is applied BEFORE rotation
        rotatingSkeletonDisplayModel.skeletonAtlasUrl.set("StreakTheSpire/skeletons/rotating_streak/skeleton.atlas");
        rotatingSkeletonDisplayModel.skeletonJsonUrl.set("StreakTheSpire/skeletons/rotating_streak/skeleton.json");
        rotatingSkeletonDisplayModel.skeletonIdleAnimationSpeed.set(1.0f);
        controller.addCharacterDisplayModel(rotatingSkeletonDisplayModel);

        CharacterTextDisplayModel ironcladTextDisplayModel = new CharacterTextDisplayModel();
        ironcladTextDisplayModel.identifier.set(AbstractPlayer.PlayerClass.IRONCLAD.toString());
        ironcladTextDisplayModel.displayText.set("Ironclad");
        controller.addCharacterDisplayModel(ironcladTextDisplayModel);

        CharacterTextDisplayModel silentTextDisplayModel = new CharacterTextDisplayModel();
        silentTextDisplayModel.identifier.set(AbstractPlayer.PlayerClass.THE_SILENT.toString());
        silentTextDisplayModel.displayText.set("Silent");
        controller.addCharacterDisplayModel(silentTextDisplayModel);

        CharacterTextDisplayModel defectTextDisplayModel = new CharacterTextDisplayModel();
        defectTextDisplayModel.identifier.set(AbstractPlayer.PlayerClass.DEFECT.toString());
        defectTextDisplayModel.displayText.set("Defect");
        controller.addCharacterDisplayModel(defectTextDisplayModel);

        CharacterTextDisplayModel watcherTextDisplayModel = new CharacterTextDisplayModel();
        watcherTextDisplayModel.identifier.set(AbstractPlayer.PlayerClass.WATCHER.toString());
        watcherTextDisplayModel.displayText.set("Watcher");
        controller.addCharacterDisplayModel(watcherTextDisplayModel);
    }

    private void initialiseCeremonyModels() {
        ceremonyPreferencesModel = new Property<>(new CeremonyPreferencesModel());
        registerConfigModel(ceremonyPreferencesModel);
    }

    private void initialiseBorderStyleModels() {
        borderStyleSetModel = new Property<>(new BorderStyleSetModel());
        BorderStyleSetController controller = new BorderStyleSetController(borderStyleSetModel.get());

        // TODO: Also loadable from Json for extensibility, add mechanism to do so.
        BorderStyleModel invisibleStyle = new BorderStyleModel();
        invisibleStyle.identifier.set("Invisible");
        invisibleStyle.showInGameMode.set(false);
        invisibleStyle.texturePath.set("StreakTheSpire/textures/ui/tip_box_9slice_sq.png");
        invisibleStyle.color.set(new Color(1f, 1f, 1f, 0.5f));
        invisibleStyle.textureMargins.set(new IntMargins(48, 48, 35, 35));
        invisibleStyle.buttonOverlayTexturePath.set("StreakTheSpire/textures/ui/button_overlay_off.png");
        controller.addStyle(invisibleStyle);

        BorderStyleModel tipBoxStyle = new BorderStyleModel();
        tipBoxStyle.identifier.set("TipBox");
        tipBoxStyle.showInGameMode.set(true);
        tipBoxStyle.texturePath.set("StreakTheSpire/textures/ui/tip_box_9slice_sq.png");
        tipBoxStyle.textureMargins.set(new IntMargins(48, 48, 35, 35));
        tipBoxStyle.buttonOverlayTexturePath.set("StreakTheSpire/textures/ui/button_overlay_1.png");
        controller.addStyle(tipBoxStyle);

        BorderStyleModel topBarStyle = new BorderStyleModel();
        topBarStyle.identifier.set("TopBar");
        topBarStyle.showInGameMode.set(true);
        topBarStyle.texturePath.set("StreakTheSpire/textures/ui/top_bar_9slice.png");
        topBarStyle.textureMargins.set(new IntMargins(16, 16, 16, 16));
        topBarStyle.buttonOverlayTexturePath.set("StreakTheSpire/textures/ui/button_overlay_2.png");
        controller.addStyle(topBarStyle);

        displayPreferencesModel.get().borderStyle.set(tipBoxStyle.identifier.get());
    }

    private void createViews() {
        PlayerStreakStoreView streakStoreView = createView(streakStoreDataModel.get());

        // TODO: Have to create this 'unautomatically' as it tracks a specific PlayerStreakStoreView to attach, rather than
        //       by being part of the same hierarchy. It probably would be better to do it that way, but it means the store view
        //       needs to contain its resizable panel as a separate element. Which would be better, but it's a lot of work.
        PlayerStreakStoreAttachedPreferencesDisplay attachedPreferencesDisplay = new PlayerStreakStoreAttachedPreferencesDisplay(streakStoreView);
        rootUIElement.addChild(attachedPreferencesDisplay);
    }

    public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
        IView view = ViewFactoryManager.get().createView(model);

        logDebug("View created: " + (view == null ? "null" : view.getClass().getSimpleName()) + " viewIsUIElement: " + (view instanceof UIElement ? "yes" : "no"));
        if(view instanceof UIElement) {
            rootUIElement.addChild((UIElement) view);
        }

        return (TView) view;
    }

    public UIDebugDimensionsDisplay createDebugDimensionsDisplay(UIElement uiElement) {
        UIDebugDimensionsDisplay display = new UIDebugDimensionsDisplay(uiElement);
        debugRootUIElement.addChild(display);
        return display;
    }

    public void removeDebugDimensionsDisplay(UIDebugDimensionsDisplay debugDisplay) {
        debugRootUIElement.removeChild(debugDisplay);
        debugDisplay.destroy(true);
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