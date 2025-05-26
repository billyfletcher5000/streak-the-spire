package StreakTheSpire.Views;

import StreakTheSpire.Ceremonies.CeremonyManager;
import StreakTheSpire.Ceremonies.IScoreChangeCeremony;
import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIHorizontalLayoutGroup;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UITextElement;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class PlayerStreakView extends UIHorizontalLayoutGroup implements IView {

    private PlayerStreakModel model = null;
    private IView characterDisplayView = null;
    private UITextElement scoreDisplayElement = null;
    private int lastProcessedScore = -1;

    private IScoreChangeCeremony scoreChangeCeremony = null;

    public PlayerStreakModel getModel() { return model; }
    public IView getCharacterDisplayView() { return characterDisplayView; }
    public UITextElement getScoreDisplayElement() { return scoreDisplayElement; }

    public PlayerStreakView(PlayerStreakModel model) {
        this.model = model;

        CharacterDisplaySetModel characterDisplaySet = StreakTheSpire.get().getCharacterDisplaySetModel();
        CharacterDisplayPreferencesModel preferences = StreakTheSpire.get().getCharacterDisplayPreferencesModel();

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
        scoreDisplayElement = new UITextElement(Vector2.Zero, FontHelper.tipBodyFont, String.valueOf(lastProcessedScore));
        scoreDisplayElement.setHAlign(Align.center);
        addChild(scoreDisplayElement);

        model.currentStreak.addOnChangedSubscriber(this::onStreakChanged);
    }

    @Override
    public void close() {
        super.close();
        model.currentStreak.removeOnChangedSubscriber(this::onStreakChanged);
    }

    private void onStreakChanged() {
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
            scoreChangeCeremony.addOnChangedSubscriber(this::cleanUpCeremony);
            scoreChangeCeremony.start(model.currentStreak.get(), this);
        }
        catch (Exception e) {
            StreakTheSpire.logError("Error while instantiating score change ceremony: " + e.getMessage());
        }
    }

    private void cleanUpCeremony() {
        scoreChangeCeremony.removeOnChangedSubscriber(this::cleanUpCeremony);
        scoreChangeCeremony = null;
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        if(scoreChangeCeremony != null) {
            scoreChangeCeremony.update(deltaTime);
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
