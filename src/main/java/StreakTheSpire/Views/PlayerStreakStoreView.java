package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;
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

    public PlayerStreakStoreView(PlayerStreakStoreModel model) {
        super(model.panelModel.get().position.get(),
                model.panelModel.get().scale.get(),
                new NineSliceTexture(StreakTheSpireTextureDatabase.TIP_BOX_NINESLICE.getTexture(), 48, 48, 35, 35),
                model.panelModel.get().dimensions.get(),
                Color.WHITE.cpy());

        this.streakModel = model;

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

        setMaskColor(new Color(0.0f, 1.0f, 0.25f, 0.33f));
        setLocalRotation(10.0f);
    }

    private void saveModel() {
        streakModel.panelModel.get().position.set(getLocalPosition());
        streakModel.panelModel.get().dimensions.set(getDimensions());
        streakModel.panelModel.get().scale.set(getLocalScale());
        StreakTheSpire.getInstance().saveConfig();
    }

    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            PlayerStreakStoreModel playerStreakStoreModel = (PlayerStreakStoreModel) model;
            if(playerStreakStoreModel != null) {
                StreakTheSpire.logDebug("PlayStreakStoreView created!");
                return (TView) new PlayerStreakStoreView(playerStreakStoreModel);
            }

            StreakTheSpire.logWarning("PlayStreakStoreViewFactory failed to create view!");
            return null;
        }
    };
}
