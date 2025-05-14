package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;
import StreakTheSpire.Models.PlayerStreakModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.Models.SavedPanelModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.NineSliceTexture;
import StreakTheSpire.UI.UIResizablePanel;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import com.badlogic.gdx.graphics.Color;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreView extends UIResizablePanel implements IView {
    private PlayerStreakStoreModel streakModel;
    private SavedPanelModel savedPanelModel;

    public PlayerStreakStoreView(PlayerStreakStoreModel model) {
        super(model.panelModel.getValue().position.getValue(),
                model.panelModel.getValue().scale.getValue(),
                new NineSliceTexture(StreakTheSpireTextureDatabase.TIP_BOX_NINESLICE.getTexture(), 48, 48, 35, 35),
                model.panelModel.getValue().dimensions.getValue(),
                Color.WHITE.cpy());

        this.streakModel = model;
        this.savedPanelModel = model.panelModel.getValue();

        PanelResizedSubscriber resizeSubscriber = new PanelResizedSubscriber() {
            @Override
            public void onPanelResized() {
                saveModel();
            }
        };
        this.addOnPanelResizedSubscriber(resizeSubscriber);

        PanelMovedSubscriber movedSubscriber = new PanelMovedSubscriber() {
            @Override
            public void onPanelMoved() {
                saveModel();
            }
        };
        this.addOnPanelMovedSubscriber(movedSubscriber);
    }

    private void saveModel() {
        savedPanelModel.position.setValue(getLocalPosition());
        savedPanelModel.dimensions.setValue(getDimensions());
        savedPanelModel.scale.setValue(getLocalScale());
        //streakModel.saveToConfig(StreakTheSpire.getConfig());
        //StreakTheSpire.saveModSpireConfig();
        String json = gson.toJson(streakModel);
        StreakTheSpire.logInfo(json);
    }

    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            PlayerStreakStoreModel playerStreakStoreModel = new PlayerStreakStoreModel();
            if(playerStreakStoreModel != null) {
                StreakTheSpire.logInfo("PlayStreakStoreView created!");
                return (TView) new PlayerStreakStoreView(playerStreakStoreModel);
            }

            StreakTheSpire.logInfo("PlayStreakStoreViewFactory failed to create view!");
            return null;
        }
    };
}
