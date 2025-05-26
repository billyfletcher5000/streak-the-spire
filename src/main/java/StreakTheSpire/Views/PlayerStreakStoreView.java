package StreakTheSpire.Views;

import StreakTheSpire.Controllers.BorderStyleSetController;
import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.IntMargins;
import StreakTheSpire.UI.Layout.UIGridLayoutGroup;
import StreakTheSpire.UI.NineSliceTexture;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UIResizablePanel;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class PlayerStreakStoreView extends UIResizablePanel implements IView {

    private PlayerStreakStoreModel streakStoreModel;
    private UIGridLayoutGroup gridLayoutGroup = null;

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        // TODO: With a proper layouting system this would be handled by the child's anchoring
        if(gridLayoutGroup != null)
            gridLayoutGroup.setDimensions(dimensions);
    }

    public PlayerStreakStoreView(PlayerStreakStoreModel model) {
        SavedPanelModel panelModel = model.panelModel.get();

        setLocalPosition(panelModel.position.get());
        setLocalScale(model.panelModel.get().scale.get());

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        preferences.borderStyle.addOnChangedSubscriber(this::updateBorder);

        updateBorder();

        setDimensions(panelModel.dimensions.get());
        setMinimumSize(new Vector2(64, 64));

        this.streakStoreModel = model;

        addOnPanelResizedSubscriber(this::saveModel);
        addOnPanelMovedSubscriber(this::saveModel);

        gridLayoutGroup = new UIGridLayoutGroup();
        addChild(gridLayoutGroup);

        for(PlayerStreakModel streakModel : streakStoreModel.playerToStreak) {
            UIElement element = createStreakModelDisplay(streakModel);
            gridLayoutGroup.addChild(element);
        }

        if(streakStoreModel.rotatingPlayerStreakModel.get() != null) {
            UIElement element = createStreakModelDisplay(streakStoreModel.rotatingPlayerStreakModel.get());
            gridLayoutGroup.addChild(element);
        }

        gridLayoutGroup.setDimensions(getDimensions());
    }

    @Override
    public void close() {
        super.close();
        removeOnPanelResizedSubscriber(this::saveModel);
        removeOnPanelMovedSubscriber(this::saveModel);

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        preferences.borderStyle.removeOnChangedSubscriber(this::updateBorder);
    }

    private UIElement createStreakModelDisplay(PlayerStreakModel streakModel) {
        return ViewFactoryManager.get().createView(streakModel);
    }

    private void saveModel() {
        streakStoreModel.panelModel.get().position.set(getLocalPosition());
        streakStoreModel.panelModel.get().dimensions.set(getDimensions());
        streakStoreModel.panelModel.get().scale.set(getLocalScale());
        StreakTheSpire.get().saveConfig();
    }

    private void updateBorder() {
        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        BorderStyleSetModel borderStyles = StreakTheSpire.get().getBorderStyles();
        BorderStyleSetController borderStyleSetController = new BorderStyleSetController(borderStyles);

        BorderStyleModel borderStyle = borderStyleSetController.getModel(preferences.borderStyle.get());
        Texture texture = StreakTheSpire.get().getTextureCache().getTexture(borderStyle.texturePath.get());
        NineSliceTexture nineSliceTexture = new NineSliceTexture(texture, borderStyle.textureMargins.get());
        setNineSliceTexture(nineSliceTexture);
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
