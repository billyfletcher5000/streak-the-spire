package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;
import StreakTheSpire.Models.PlayerStreakModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIHorizontalLayoutGroup;

public class PlayerStreakView extends UIHorizontalLayoutGroup implements IView {

    private PlayerStreakModel model = null;

    public PlayerStreakView(PlayerStreakModel model) {
        this.model = model;


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
