package StreakTheSpire;

import StreakTheSpire.Ceremonies.CeremonyManager;
import StreakTheSpire.Ceremonies.Panel.LightFlourishScoreDecreaseCeremony;
import StreakTheSpire.Ceremonies.Panel.LightFlourishScoreIncreaseCeremony;
import StreakTheSpire.Ceremonies.Panel.SimpleTextScoreChangeCeremony;
import StreakTheSpire.Config.*;
import StreakTheSpire.Controllers.BorderStyleSetController;
import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Data.RotatingConstants;
import StreakTheSpire.Models.*;
import StreakTheSpire.UI.*;
import StreakTheSpire.Utils.*;
import StreakTheSpire.Utils.Lifetime.LifetimeManager;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyTypeAdapters;
import StreakTheSpire.Views.*;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import dorkbox.tweenEngine.TweenEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpireInitializer
public class StreakTheSpire implements PostInitializeSubscriber, PostUpdateSubscriber, RenderSubscriber, PostRenderSubscriber, PreRoomRenderSubscriber {

    //region Static Data
    private static final Logger logger = LogManager.getLogger(StreakTheSpire.class);
    public static final LoggingLevel loggingLevel = LoggingLevel.INFO;
    private static final StringBuilder errorLogBuilder = new StringBuilder();
    public static String getErrorLog() { return errorLogBuilder.toString(); }
    public static float getDeltaTime() { return Gdx.graphics.getDeltaTime(); }
    public static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(PropertyTypeAdapters.PropertyTypeAdapter.FACTORY).create();

    public static final String modID = "streak_the_spire";
    public static final String modName = "StreakTheSpire";
    public static final String modDisplayName = "Streak The Spire";
    public static final String modAuthorName = "billyfletcher5000";
    public static final String modDescription = "A Slay The Spire mod to automatically track your streaks, both individual and rotating, with each character.";

    private static String prefixLocalizationID(String localizationID) { return modID + ":" + localizationID; }

    private static StreakTheSpire instance;
    public static StreakTheSpire get() { return instance; }

    // Config
    private static final String configFileName = "Config";
    private static SpireConfig modSpireConfig = null;

    public static SpireConfig getConfig() { return modSpireConfig; }
    //endregion

    //region Instance data
    private final TextureCache textureCache = new TextureCache();
    private final FontCache fontCache = new FontCache();
    private TweenEngine tweenEngine;
    private UIElement rootUIElement;
    private UIElement debugRootUIElement;
    private CursorOverride cursorOverride;
    private ConfigModPanel settingsPanel;
    private boolean trueVictoryCutsceneActive = false;
    private TipSystemView tipSystemView;
    private boolean uiDidRenderThisFrame = false;

    private final HashMap<Property<? extends IConfigDataModel>, String> configDataModelToConfigID = new HashMap<>();

    private Property<GameStateModel> gameStateModel;
    private Property<StreakCriteriaModel> streakCriteriaModel;
    private Property<DisplayPreferencesModel> displayPreferencesModel;
    private Property<PlayerStreakStoreModel> streakStoreDataModel;
    private Property<CharacterDisplaySetModel> characterDisplaySetModel;
    private Property<CharacterCoreDataSetModel> characterCoreDataSetModel;
    private Property<CeremonyPreferencesModel> ceremonyPreferencesModel;
    private Property<BorderStyleSetModel> borderStyleSetModel;
    private Property<TipSystemModel> tipSystemModel;
    //endregion

    //region Public data access
    public TextureCache getTextureCache() { return textureCache; }
    public FontCache getFontCache() { return fontCache; }
    public TweenEngine getTweenEngine() { return tweenEngine; }
    public CursorOverride getCursorOverride() { return cursorOverride; }
    public boolean didUIRenderThisFrame() { return uiDidRenderThisFrame; }
    public GameStateModel getGameStateModel() { return gameStateModel.get(); }
    public StreakCriteriaModel getStreakCriteriaModel() { return streakCriteriaModel.get(); }
    public DisplayPreferencesModel getDisplayPreferencesModel() { return displayPreferencesModel.get(); }
    public PlayerStreakStoreModel getStreakStoreDataModel() { return streakStoreDataModel.get(); }
    public CharacterDisplaySetModel getCharacterDisplaySetModel() { return characterDisplaySetModel.get(); }
    public CharacterCoreDataSetModel getCharacterCoreDataSetModel() { return characterCoreDataSetModel.get(); }
    public CeremonyPreferencesModel getCeremonyPreferences() { return ceremonyPreferencesModel.get(); }
    public BorderStyleSetModel getBorderStyles() { return borderStyleSetModel.get(); }
    public TipSystemModel getTipSystemModel() { return tipSystemModel.get(); }

    public UIStrings getConfigUIStrings() { return CardCrawlGame.languagePack.getUIString(prefixLocalizationID(LocalizationConstants.Config.ID)); }
    public UIStrings getTipUIStrings() { return CardCrawlGame.languagePack.getUIString(prefixLocalizationID(LocalizationConstants.StreakTips.ID));  }
    public CharacterStrings getCharacterStrings(String characterLocalisationID) { return CardCrawlGame.languagePack.getCharacterString(characterLocalisationID); }
    //endregion

    //region Base Initialisation
    public static void initialize() {
        errorLogBuilder.append("Error log:\n");
        new StreakTheSpire();

        logInfo("Initializing StreakTheSpire!");
        try {
            logInfo("Creating SpireConfig!");
            modSpireConfig = new SpireConfig(modName, configFileName);
        } catch (Exception e) {
            logError("Initialize exception:" + e.getMessage());
        }
    }

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
        initialiseLocalisation();
        initialiseFonts();

        initialisePreferenceModels();
        initialiseGameStateModel();
        initialiseCharacterDisplayModels();
        initialiseCharacterDataModels();
        initialiseCeremonyModels();
        initialiseStreakDataModel();
        initialiseBorderStyleModels();
        initialiseTipSystemModel();

        loadConfig();

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
        createTipSystemView();

        createModPanel();
        BaseMod.registerModBadge(StreakTheSpireTextureDatabase.MOD_ICON.getTexture(), modDisplayName, modAuthorName, modDescription, settingsPanel);
    }
    //endregion

    //region Configuration
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

    protected <T extends IConfigDataModel> void registerConfigModel(Property<T> dataModel) {
        if(dataModel == null || dataModel.get() == null)
            return;

        IConfigDataModel configModel = dataModel.get();
        String configID = configModel.getConfigID();
        if(configDataModelToConfigID.containsValue(configID))
            throw new IllegalArgumentException("ConfigID \"" + configID + "\" is already registered!");

        configDataModelToConfigID.put(dataModel, configID);
    }

    private void createModPanel() {
        settingsPanel = new ConfigModPanel();

        settingsPanel.addPage(new CriteriaModPanelPage());
        settingsPanel.addPage(new CharactersModPanelPage());
        settingsPanel.addPage(new DisplayPreferencesModPanelPage());
        settingsPanel.addPage(new TroubleshootingModPanelPage());
    }
    //endregion

    //region StS Hook Responses
    @Override
    public void receivePreRoomRender(SpriteBatch spriteBatch) {
        if(!gameStateModel.get().previewModeActive.get() &&  !trueVictoryCutsceneActive && displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.PreRoom)
            performRender(spriteBatch);
    }

    public void receiveTopPanelRender(SpriteBatch spriteBatch) {
        if(!gameStateModel.get().previewModeActive.get() && (trueVictoryCutsceneActive || displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.TopPanel))
            performRender(spriteBatch);
    }

    @Override
    public void receiveRender(SpriteBatch spriteBatch) {
        if(gameStateModel.get().previewModeActive.get() || (!trueVictoryCutsceneActive && displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.AboveMost))
            performRender(spriteBatch);
    }

    @Override
    public void receivePostRender(SpriteBatch spriteBatch) {
        if (!gameStateModel.get().previewModeActive.get() && !trueVictoryCutsceneActive && displayPreferencesModel.get().renderLayer.get() == DisplayPreferencesModel.RenderLayer.AboveAll)
            performRender(spriteBatch);

        cursorOverride.render(spriteBatch);
    }

    private void performRender(SpriteBatch spriteBatch) {
        rootUIElement.render(spriteBatch);
        debugRootUIElement.render(spriteBatch);
        uiDidRenderThisFrame = true;
    }


    @Override
    public void receivePostUpdate() {
        gameStateModel.get().gameMode.set(CardCrawlGame.mode);
        gameStateModel.get().editModeActive.set(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT));
        gameStateModel.get().previewModeActive.set(settingsPanel.isUp);

        rootUIElement.update(getDeltaTime());

        tweenEngine.update(getDeltaTime());

        debugRootUIElement.update(getDeltaTime());

        tipSystemView.update();

        LifetimeManager.ProcessDestroyed();

        uiDidRenderThisFrame = false;
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
    //endregion

    //region View & Ceremony Class Registration
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
    //endregion

    //region Text & Localisation
    private void initialiseLocalisation() {
        // Fallback
        loadLocalisation("eng");

        String langKey = Settings.language.toString().toLowerCase();
        loadLocalisation(langKey);
    }

    private void loadLocalisation(String langKey) {
        String filepath = "StreakTheSpire/localization/" + langKey + "/UIStrings.json";
        if (Gdx.files.internal(filepath).exists()) {
            BaseMod.loadCustomStringsFile(UIStrings.class, filepath);
        }

        filepath = "StreakTheSpire/localization/" + langKey + "/characters.json";
        if (Gdx.files.internal(filepath).exists()) {
            BaseMod.loadCustomStringsFile(CharacterStrings.class, filepath);
        }
    }

    private void initialiseFonts() {
        fontCache.createSDFFont("Kreon_SDF_Outline_Shadow",
                "StreakTheSpire/fonts/Kreon_Bold_SDF_Numbers.fnt",
                "StreakTheSpire/fonts/Kreon_Bold_SDF_Numbers.png",
                textureCache);
    }
    //endregion

    //region Data Initialisation
    private void initialisePreferenceModels() {
        displayPreferencesModel = new Property<>(new DisplayPreferencesModel());
        registerConfigModel(displayPreferencesModel);
    }

    protected void initialiseGameStateModel() {
        gameStateModel = new Property<>(new GameStateModel());

        gameStateModel.get().gameMode.set(CardCrawlGame.mode);
    }

    protected void initialiseTipSystemModel() {
        tipSystemModel = new Property<>(new TipSystemModel());
    }

    protected void createTipSystemView() {
        tipSystemView = new TipSystemView(tipSystemModel.get());
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
        rotatingSkeletonDisplayModel.identifier.set(RotatingConstants.Identifier);
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

    private void initialiseCharacterDataModels() {
        characterCoreDataSetModel = new Property<>(new CharacterCoreDataSetModel());

        CharacterCoreDataModel ironcladCoreModel = new CharacterCoreDataModel();
        ironcladCoreModel.identifier.set(AbstractPlayer.PlayerClass.IRONCLAD.toString());
        ironcladCoreModel.localisationID.set("Ironclad");
        ironcladCoreModel.streakTextColor.set(Settings.RED_TEXT_COLOR);
        ironcladCoreModel.displayOrderPriority.set(1);
        characterCoreDataSetModel.get().characterLocalisations.add(ironcladCoreModel);

        CharacterCoreDataModel silentCoreModel = new CharacterCoreDataModel();
        silentCoreModel.identifier.set(AbstractPlayer.PlayerClass.THE_SILENT.toString());
        silentCoreModel.localisationID.set("Silent");
        silentCoreModel.streakTextColor.set(Settings.GREEN_TEXT_COLOR);
        silentCoreModel.displayOrderPriority.set(2);
        characterCoreDataSetModel.get().characterLocalisations.add(silentCoreModel);

        CharacterCoreDataModel defectCoreModel = new CharacterCoreDataModel();
        defectCoreModel.identifier.set(AbstractPlayer.PlayerClass.DEFECT.toString());
        defectCoreModel.localisationID.set("Defect");
        defectCoreModel.streakTextColor.set(Settings.BLUE_TEXT_COLOR);
        defectCoreModel.displayOrderPriority.set(3);
        characterCoreDataSetModel.get().characterLocalisations.add(defectCoreModel);

        CharacterCoreDataModel watcherCoreModel = new CharacterCoreDataModel();
        watcherCoreModel.identifier.set(AbstractPlayer.PlayerClass.WATCHER.toString());
        watcherCoreModel.localisationID.set("Watcher");
        watcherCoreModel.streakTextColor.set(Settings.PURPLE_COLOR);
        watcherCoreModel.displayOrderPriority.set(4);
        characterCoreDataSetModel.get().characterLocalisations.add(watcherCoreModel);

        CharacterCoreDataModel rotatingCoreModel = new CharacterCoreDataModel();
        rotatingCoreModel.identifier.set(RotatingConstants.Identifier);
        rotatingCoreModel.localisationID.set(RotatingConstants.LocalisationID);
        characterCoreDataSetModel.get().characterLocalisations.add(rotatingCoreModel);
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
    //endregion

    //region Views & UI
    private void initialiseUI() {
        rootUIElement = new UIElement();
        rootUIElement.setLocalScale(new Vector2(Settings.scale, Settings.scale));

        debugRootUIElement = new UIElement();
        debugRootUIElement.setLocalScale(new Vector2(Settings.scale, Settings.scale));

        cursorOverride = new CursorOverride(gameStateModel.get().editModeActive);
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
    //endregion

    //region Debugging
    public UIDebugDimensionsDisplay createDebugDimensionsDisplay(UIElement uiElement) {
        UIDebugDimensionsDisplay display = new UIDebugDimensionsDisplay(uiElement);
        debugRootUIElement.addChild(display);
        return display;
    }

    public void removeDebugDimensionsDisplay(UIDebugDimensionsDisplay debugDisplay) {
        debugRootUIElement.removeChild(debugDisplay);
        debugDisplay.destroy(true);
    }
    //endregion

    //region Logging
    public static void logError(String message) {
        logger.error(message);
        errorLogBuilder.append(message).append("\n");
    }

    public static void logError(String message, Object... params) {
        logger.error(message, params);
        errorLogBuilder.append(String.format(message, params)).append("\n");
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
            logger.info(message);
        }
    }

    public static void logDebug(String message, Object... params) {
        if(loggingLevel.ordinal() >= LoggingLevel.DEBUG.ordinal()) {
            logger.info(message, params);
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
    //endregion

    //region Tip & Slay The Relics Support
    public String getDateTimeString(String timestamp, String formatString) {
        SimpleDateFormat dateFormat;
        if (Settings.language == Settings.GameLanguage.JPN) {
            dateFormat = new SimpleDateFormat(formatString, Locale.JAPAN);
        } else {
            dateFormat = new SimpleDateFormat(formatString);
        }

        return dateFormat.format(Long.parseLong(timestamp) * 1000L);
    }

    public static ArrayList<Hitbox> slayTheRelicsHitboxes = new ArrayList<>();
    public static ArrayList<ArrayList<PowerTip>> slayTheRelicsPowerTips = new ArrayList<>();

    public static void clearSlayTheRelicsData() {
        slayTheRelicsHitboxes.clear();
        slayTheRelicsPowerTips.clear();
    }
    //endregion
}