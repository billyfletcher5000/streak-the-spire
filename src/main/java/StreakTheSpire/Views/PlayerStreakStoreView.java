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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;

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

        //TODO: Work out why this is necessary, it shouldn't be if the screen size scaling is applied appropriately.
        //      Could be that the order of operations is wrong or we should save the location divided by Settings.xScale/yScale
        ensureOnScreen();

        this.streakStoreModel = model;

        addOnPanelResizedSubscriber(this::saveModel);
        addOnPanelMovedSubscriber(this::saveModel);

        gridLayoutGroup = new UIGridLayoutGroup();
        addChild(gridLayoutGroup);

        PlayerStreakModel[] sortedStreakModels = streakStoreModel.playerToStreak.stream().sorted((a, b) -> {
            int indexA = preferences.characterOrder.indexOf(a.identifier.get());
            int indexB = preferences.characterOrder.indexOf(b.identifier.get());
            indexA = indexA == -1 ? preferences.characterOrder.indexOf(DisplayPreferencesModel.CharacterWildcard) : indexA;
            indexB = indexB == -1 ? preferences.characterOrder.indexOf(DisplayPreferencesModel.CharacterWildcard) : indexB;
            return Integer.compare(indexA, indexB);
        }).toArray(PlayerStreakModel[]::new);

        for(PlayerStreakModel streakModel : sortedStreakModels) {
            UIElement element = createStreakModelDisplay(streakModel);
            gridLayoutGroup.addChild(element);
        }

        if(streakStoreModel.rotatingPlayerStreakModel.get() != null) {
            UIElement element = createStreakModelDisplay(streakStoreModel.rotatingPlayerStreakModel.get());
            gridLayoutGroup.addChild(element);
        }

        gridLayoutGroup.setDimensions(getDimensions());
    }

    private void ensureOnScreen() {
        Affine2 localToWorld = getLocalToWorldTransform();
        Vector2 bottomLeft = getDimensions().scl(-0.5f);
        localToWorld.applyTo(bottomLeft);
        Vector2 topRight = getDimensions().scl(-0.5f);
        localToWorld.applyTo(topRight);

        // Check for resolution changes pushing this off screen, note that this is dependent (unnecessarily so I imagine)
        // on it being a 'top level' item and not a child element of anything other than the root UI element
        Vector2 halfDimensions = getDimensions().scl(0.5f);
        Vector2 localPosition = getLocalPosition();
        float xScale = Settings.xScale;
        float yScale = Settings.yScale;
        float unscaledWidth = Settings.WIDTH / xScale;
        float unscaledHeight = Settings.HEIGHT / yScale;

        if(bottomLeft.x > unscaledWidth)
            localPosition.x = unscaledWidth - halfDimensions.x;
        else if (topRight.x < 0)
            localPosition.x = halfDimensions.x;

        if(bottomLeft.y > unscaledHeight)
            localPosition.y = unscaledHeight - halfDimensions.y;
        else if(topRight.y < 0)
            localPosition.y = halfDimensions.y;

        setLocalPosition(localPosition);
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
        StreakTheSpire.logInfo("Saving panel model: " + streakStoreModel.panelModel.get().toString());
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
