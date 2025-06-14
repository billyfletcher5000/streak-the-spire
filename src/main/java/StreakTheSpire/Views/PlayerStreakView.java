package StreakTheSpire.Views;

import StreakTheSpire.Ceremonies.CeremonyManager;
import StreakTheSpire.Ceremonies.IScoreChangeCeremony;
import StreakTheSpire.Controllers.CharacterCoreDataSetController;
import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Controllers.TipSystemController;
import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIHorizontalLayoutGroup;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UIElementHitbox;
import StreakTheSpire.UI.UISDFTextElement;
import StreakTheSpire.UI.UITextElement;
import StreakTheSpire.Utils.LocalizationConstants;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PlayerStreakView extends UIHorizontalLayoutGroup implements IView {

    private PlayerStreakModel model = null;
    private IView characterDisplayView = null;
    private UITextElement scoreDisplayElement = null;
    private int lastProcessedScore = -1;
    private TipDataModel tipDataModel = null;
    private UIElementHitbox tipHitbox = null;
    private boolean tipBodyTextDirty = false;

    private IScoreChangeCeremony scoreChangeCeremony = null;

    public PlayerStreakModel getModel() { return model; }
    public IView getCharacterDisplayView() { return characterDisplayView; }
    public UITextElement getScoreDisplayElement() { return scoreDisplayElement; }

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        if(tipDataModel != null) {
            Vector2 processedDimensions = getDimensions();
            tipHitbox.setLocalSize(processedDimensions);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(tipDataModel != null) {
            tipDataModel.isActive.set(isVisible());
        }
    }

    public PlayerStreakView(PlayerStreakModel model) {
        this.model = model;

        CharacterDisplaySetModel characterDisplaySet = StreakTheSpire.get().getCharacterDisplaySetModel();
        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        CharacterCoreDataSetModel characterCoreDataSet = StreakTheSpire.get().getCharacterCoreDataSetModel();
        CharacterCoreDataSetController characterCoreDataSetController = new CharacterCoreDataSetController(characterCoreDataSet);
        CharacterCoreDataModel characterCoreDataModel = characterCoreDataSetController.getCharacterData(model.identifier.get());

        CharacterDisplaySetController displaySetController = new CharacterDisplaySetController(characterDisplaySet);

        CharacterDisplayModel displayModel = displaySetController.getCharacterDisplay(model.identifier.get(), preferences);
        if(displayModel == null) {
            StreakTheSpire.logError("Character Display Model is null for Player Streak View: " + model.identifier.get());
            destroy();
            return;
        }

        characterDisplayView = ViewFactoryManager.get().createView(displayModel);
        addChild((UIElement) characterDisplayView);

        Color textColor =  new Color(0.95f, 0.95f, 0.95f, 1.0f);
        if(characterCoreDataModel != null && preferences.colouredStreakNumbers.get()) {
            textColor = characterCoreDataModel.streakTextColor.get();
        }

        lastProcessedScore = model.currentStreak.get();
        BitmapFont font = StreakTheSpire.get().getFontCache().getFont(preferences.fontIdentifier.get());
        scoreDisplayElement = new UISDFTextElement(Vector2.Zero, font, String.valueOf(lastProcessedScore));
        scoreDisplayElement.setColor(textColor);
        scoreDisplayElement.setHAlign(Align.center);
        scoreDisplayElement.setAutoScale(true);
        scoreDisplayElement.setAutoScalePaddingRelative(0.7f);
        addChild(scoreDisplayElement);

        model.currentStreak.addOnChangedSubscriber(this::onStreakChanged);

        Vector2 dimensions = getDimensions();
        tipHitbox = new UIElementHitbox(0f, 0f, dimensions.x, dimensions.y);
        TipSystemModel tipSystemModel = StreakTheSpire.get().getTipSystemModel();
        TipSystemController tipSystemController = new TipSystemController(tipSystemModel);

        UIStrings tipUIStrings = StreakTheSpire.get().getTipUIStrings();
        String headerText = tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.TipHeaderTitle);
        String bodyText = getTipBodyText(tipUIStrings, model);
        String additionalLocalText = tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.EditModeInstructionText);

        model.currentStreak.addOnChangedSubscriber(this::markTipBodyTextDirty);
        model.currentStreakTimestamp.addOnChangedSubscriber(this::markTipBodyTextDirty);
        model.highestStreak.addOnChangedSubscriber(this::markTipBodyTextDirty);
        model.highestStreakTimestamp.addOnChangedSubscriber(this::markTipBodyTextDirty);
        model.totalValidWins.addOnChangedSubscriber(this::markTipBodyTextDirty);
        model.totalValidLosses.addOnChangedSubscriber(this::markTipBodyTextDirty);

        preferences.colouredStreakNumbers.addOnChangedSubscriber(this::updateTextColour);

        tipDataModel = tipSystemController.createTipDataModel(isVisible(), tipHitbox, headerText, bodyText, additionalLocalText);
    }

    private void markTipBodyTextDirty() {
        tipBodyTextDirty = true;
    }

    private void updateTipBodyText() {
        UIStrings tipUIStrings = StreakTheSpire.get().getTipUIStrings();
        tipDataModel.tipBodyText.set(getTipBodyText(tipUIStrings, model));
    }

    private String getTipBodyText(UIStrings tipUIStrings, PlayerStreakModel model) {
        String output = "";

        String dateTimeFormat = tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.DateTimeFormat);
        String currentStreakAchievedDateTime = model.currentStreakTimestamp.get() != null ? StreakTheSpire.get().getDateTimeString(model.currentStreakTimestamp.get(), dateTimeFormat) : "";
        String highestStreakAchievedDateTime = model.highestStreakTimestamp.get() != null ? StreakTheSpire.get().getDateTimeString(model.highestStreakTimestamp.get(), dateTimeFormat) : "";

        float winRateDecimal = ((float)model.totalValidWins.get() / (float)(model.totalValidWins.get() + model.totalValidLosses.get()));
        int winRate = Math.round(winRateDecimal * 100.0f);

        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.CurrentStreakText) + model.currentStreak.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.DateTimeAchievedText) + currentStreakAchievedDateTime + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.HighestStreakText) + model.highestStreak.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.DateTimeAchievedText) + highestStreakAchievedDateTime + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.TotalWinsText) + model.totalValidWins.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.TotalLossesText) + model.totalValidLosses.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.WinRateText) + winRate + "%";

        return output;
    }

    private void updateTextColour() {
        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        CharacterCoreDataSetModel characterCoreDataSet = StreakTheSpire.get().getCharacterCoreDataSetModel();
        CharacterCoreDataSetController characterCoreDataSetController = new CharacterCoreDataSetController(characterCoreDataSet);
        CharacterCoreDataModel characterCoreDataModel = characterCoreDataSetController.getCharacterData(model.identifier.get());

        Color textColor =  new Color(0.95f, 0.95f, 0.95f, 1.0f);
        if(characterCoreDataModel != null && preferences.colouredStreakNumbers.get()) {
            textColor = characterCoreDataModel.streakTextColor.get();
        }

        scoreDisplayElement.setColor(textColor);
    }

    @Override
    protected void elementDestroy() {
        super.elementDestroy();
        model.currentStreak.removeOnChangedSubscriber(this::onStreakChanged);

        if(scoreChangeCeremony != null) {
            scoreChangeCeremony.forceEnd();
            scoreChangeCeremony = null;
        }

        model.currentStreak.removeOnChangedSubscriber(this::markTipBodyTextDirty);
        model.currentStreakTimestamp.removeOnChangedSubscriber(this::markTipBodyTextDirty);
        model.highestStreak.removeOnChangedSubscriber(this::markTipBodyTextDirty);
        model.highestStreakTimestamp.removeOnChangedSubscriber(this::markTipBodyTextDirty);
        model.totalValidWins.removeOnChangedSubscriber(this::markTipBodyTextDirty);
        model.totalValidLosses.removeOnChangedSubscriber(this::markTipBodyTextDirty);

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        preferences.colouredStreakNumbers.addOnChangedSubscriber(this::updateTextColour);

        if(tipDataModel != null) {
            TipSystemModel tipSystemModel = StreakTheSpire.get().getTipSystemModel();
            TipSystemController tipSystemController = new TipSystemController(tipSystemModel);
            tipSystemController.destroyTipDataModel(tipDataModel);
        }
    }

    private void onStreakChanged() {
        if(scoreChangeCeremony != null) {
            scoreChangeCeremony.forceEnd();
        }

        CeremonyPreferencesModel preferences = StreakTheSpire.get().getCeremonyPreferences();
        int newScore = model.currentStreak.get();
        if(lastProcessedScore == newScore) {
            StreakTheSpire.logWarning("Score changed to same last processed value! Very unexpected!");
            return;
        }

        String ceremonyClassName = newScore > lastProcessedScore ? preferences.scoreIncreaseCeremony.get() : preferences.scoreDecreaseCeremony.get();

        lastProcessedScore = newScore;

        Class<? extends IScoreChangeCeremony> scoreCeremonyClass = CeremonyManager.get().getScoreChangeCeremonyClass(ceremonyClassName);
        try {
            scoreChangeCeremony = scoreCeremonyClass.newInstance();
            scoreChangeCeremony.addOnCompleteSubscriber(this::cleanUpCeremony);
            scoreChangeCeremony.start(model.currentStreak.get(), this);
        }
        catch (Exception e) {
            StreakTheSpire.logError("Error while instantiating score change ceremony: " + e.getMessage());
        }
    }

    private void cleanUpCeremony() {
        scoreChangeCeremony.removeOnCompleteSubscriber(this::cleanUpCeremony);
        scoreChangeCeremony = null;
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);

        if(scoreChangeCeremony != null) {
            scoreChangeCeremony.update(deltaTime);
        }

        if(tipBodyTextDirty) {
            updateTipBodyText();
            tipBodyTextDirty = false;
        }

        if(tipHitbox != null) {
            tipHitbox.update(getLocalToWorldTransform());
        }
    }

    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            PlayerStreakModel playerStreakModel = (PlayerStreakModel) model;
            if(playerStreakModel != null) {
                StreakTheSpire.logDebug("PlayerStreakView created!");
                return (TView) new PlayerStreakView(playerStreakModel);
            }

            StreakTheSpire.logWarning("PlayerStreakView failed to create view!");
            return null;
        }
    };
}
