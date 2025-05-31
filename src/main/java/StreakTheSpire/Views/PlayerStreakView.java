package StreakTheSpire.Views;

import StreakTheSpire.Ceremonies.CeremonyManager;
import StreakTheSpire.Ceremonies.IScoreChangeCeremony;
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
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.screens.stats.RunData;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlayerStreakView extends UIHorizontalLayoutGroup implements IView {

    private PlayerStreakModel model = null;
    private IView characterDisplayView = null;
    private UITextElement scoreDisplayElement = null;
    private int lastProcessedScore = -1;
    private TipDataModel tipDataModel = null;
    private UIElementHitbox tipHitbox = null;

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

        CharacterDisplaySetController displaySetController = new CharacterDisplaySetController(characterDisplaySet);

        CharacterDisplayModel displayModel = displaySetController.getCharacterDisplay(model.identifier.get(), preferences);
        if(displayModel == null) {
            StreakTheSpire.logError("Character Display Model is null for Player Streak View: " + model.identifier.get());
            destroy();
            return;
        }

        characterDisplayView = ViewFactoryManager.get().createView(displayModel);
        addChild((UIElement) characterDisplayView);

        lastProcessedScore = model.currentStreak.get();
        BitmapFont font = StreakTheSpire.get().getFontCache().getFont(preferences.fontIdentifier.get());
        scoreDisplayElement = new UISDFTextElement(Vector2.Zero, font, String.valueOf(lastProcessedScore));
        scoreDisplayElement.setColor(new Color(0.95f, 0.95f, 0.95f, 1.0f));
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

        tipDataModel = tipSystemController.createTipDataModel(isVisible(), tipHitbox, headerText, bodyText, additionalLocalText);
    }

    private String getTipBodyText(UIStrings tipUIStrings, PlayerStreakModel model) {
        String output = "";

        String dateTimeFormat = tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.DateTimeFormat);
        String currentStreakAchievedDateTime = model.currentStreakTimestamp.get() != null ? StreakTheSpire.get().getDateTimeString(model.currentStreakTimestamp.get(), dateTimeFormat) : "";
        String highestStreakAchievedDateTime = model.highestStreakTimestamp.get() != null ? StreakTheSpire.get().getDateTimeString(model.highestStreakTimestamp.get(), dateTimeFormat) : "";

        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.CurrentStreakText) + model.currentStreak.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.DateTimeAchievedText) + currentStreakAchievedDateTime + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.HighestStreakText) + model.highestStreak.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.DateTimeAchievedText) + highestStreakAchievedDateTime + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.TotalWinsText) + model.totalValidWins.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.TotalLossesText) + model.totalValidLosses.get() + " NL ";
        output += tipUIStrings.TEXT_DICT.get(LocalizationConstants.StreakTips.WinRateText) + ((float)model.totalValidWins.get() / (float)model.totalValidLosses.get());

        return output;
    }

    @Override
    protected void elementDestroy() {
        super.elementDestroy();
        model.currentStreak.removeOnChangedSubscriber(this::onStreakChanged);

        if(scoreChangeCeremony != null) {
            scoreChangeCeremony.forceEnd();
            scoreChangeCeremony = null;
        }

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
