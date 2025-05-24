package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;
import StreakTheSpire.Models.PlayerStreakModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIGridLayoutGroup;
import StreakTheSpire.UI.NineSliceTexture;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UIResizablePanel;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class PlayerStreakStoreView extends UIResizablePanel implements IView {
    private PlayerStreakStoreModel streakStoreModel;

    private UIGridLayoutGroup gridLayoutGroup = null;

    private UIResizablePanel.PanelResizedSubscriber panelResizedSubscriber;
    private UIResizablePanel.PanelMovedSubscriber panelMovedSubscriber;

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        // TODO: With a proper layouting system this would be handled by the child's anchoring
        gridLayoutGroup.setDimensions(dimensions);
    }

    public PlayerStreakStoreView(PlayerStreakStoreModel model) {
        super(model.panelModel.get().position.get(),
                model.panelModel.get().scale.get(),
                new NineSliceTexture(StreakTheSpireTextureDatabase.TIP_BOX_NINESLICE.getTexture(), 48, 48, 35, 35),
                model.panelModel.get().dimensions.get(),
                Color.WHITE.cpy());

        this.streakStoreModel = model;

        panelResizedSubscriber = addOnPanelResizedSubscriber(() -> saveModel());
        panelMovedSubscriber = addOnPanelMovedSubscriber(() -> saveModel());

        gridLayoutGroup = new UIGridLayoutGroup();
        addChild(gridLayoutGroup);

        for(PlayerStreakModel streakModel : streakStoreModel.playerToStreak) {
            UIElement element = createStreakModelDisplay(streakModel);
            addChild(element);
        }

        if(streakStoreModel.rotatingPlayerStreakModel.get() != null) {
            UIElement element = createStreakModelDisplay(streakStoreModel.rotatingPlayerStreakModel.get());
            addChild(element);
        }
    }

    @Override
    public void close() {
        super.close();
        removeOnPanelResizedSubscriber(panelResizedSubscriber);
        removeOnPanelMovedSubscriber(panelMovedSubscriber);
    }

    private UIElement createStreakModelDisplay(PlayerStreakModel streakModel) {
        return ViewFactoryManager.get().createView(streakModel);
    }

    private void saveModel() {
        streakStoreModel.panelModel.get().position.set(getLocalPosition());
        streakStoreModel.panelModel.get().dimensions.set(getDimensions());
        streakStoreModel.panelModel.get().scale.set(getLocalScale());
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
