package StreakTheSpire.Views;

import StreakTheSpire.Controllers.CharacterDisplaySetController;
import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIExpandBoxElement;
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
    private UITextElement scoreDisplayElement2 = null;

    public PlayerStreakView(PlayerStreakModel model) {
        this.model = model;

        CharacterDisplaySetModel characterDisplaySet = StreakTheSpire.getInstance().getCharacterDisplaySetModel();
        CharacterDisplayPreferencesModel preferences = StreakTheSpire.getInstance().getCharacterDisplayPreferencesModel();

        CharacterDisplaySetController displaySetController = new CharacterDisplaySetController(characterDisplaySet);

        CharacterDisplayModel displayModel = displaySetController.getCharacterDisplay(model.identifier.get(), preferences);
        if(displayModel == null) {
            StreakTheSpire.logError("Character Display Model is null for Player Streak View: " + model.identifier.get());
            destroy();
            return;
        }

        characterDisplayView = ViewFactoryManager.get().createView(displayModel);
        addChild((UIElement) characterDisplayView);

        scoreDisplayElement = new UITextElement(Vector2.Zero, FontHelper.tipBodyFont, "99");
        scoreDisplayElement.setHAlign(Align.center);
        addChild(scoreDisplayElement);

        model.currentStreak.addOnChangedSubscriber(this::onStreakChanged);
    }

    private void onStreakChanged() {

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
