package StreakTheSpire.Views;

import StreakTheSpire.Controllers.BorderStyleSetController;
import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.*;
import StreakTheSpire.UI.Layout.UIGridLayoutGroup;
import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;

public class PlayerStreakStoreView extends UIResizablePanel implements IView {

    private final PlayerStreakStoreModel streakStoreModel;
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

        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();
        gsm.editModeActive.addOnChangedSubscriber(this::onEditModeChanged);

        updateBorder();

        setDimensions(panelModel.dimensions.get());
        setMinimumSize(new Vector2(64, 64));

        initialiseCursorOverrides();

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

        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();
        gsm.editModeActive.removeOnChangedSubscriber(this::onEditModeChanged);
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

    private void onEditModeChanged() {
        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();
        setResizeEnabled(gsm.editModeActive.get());
        updateBorderVisibility();
    }

    private void updateBorder() {
        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        BorderStyleSetModel borderStyles = StreakTheSpire.get().getBorderStyles();
        BorderStyleSetController borderStyleSetController = new BorderStyleSetController(borderStyles);

        BorderStyleModel borderStyle = borderStyleSetController.getModel(preferences.borderStyle.get());
        Texture texture = StreakTheSpire.get().getTextureCache().getTexture(borderStyle.texturePath.get());
        NineSliceTexture nineSliceTexture = new NineSliceTexture(texture, borderStyle.textureMargins.get());
        setNineSliceTexture(nineSliceTexture);

        setColor(borderStyle.color.get());

        updateBorderVisibility();
    }

    private void updateBorderVisibility() {
        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        BorderStyleSetModel borderStyles = StreakTheSpire.get().getBorderStyles();
        BorderStyleSetController borderStyleSetController = new BorderStyleSetController(borderStyles);

        BorderStyleModel borderStyle = borderStyleSetController.getModel(preferences.borderStyle.get());

        setVisible(gsm.editModeActive.get() || borderStyle.showInGameMode.get());
    }

    private void initialiseCursorOverrides() {
        CursorOverrideData moveOverride = new CursorOverrideData();
        moveOverride.texture = StreakTheSpireTextureDatabase.CURSOR_MOVE.getTexture();
        setCursorOverrideMove(moveOverride);

        Texture texture = StreakTheSpireTextureDatabase.CURSOR_RESIZE.getTexture();

        CursorOverrideData topOverride = new CursorOverrideData();
        topOverride.texture = texture;
        topOverride.rotation = 90.0f;
        setCursorOverrideTop(topOverride);

        CursorOverrideData bottomOverride = new CursorOverrideData();
        bottomOverride.texture = texture;
        bottomOverride.rotation = -90.0f;
        setCursorOverrideBottom(bottomOverride);

        CursorOverrideData leftOverride = new CursorOverrideData();
        leftOverride.texture = texture;
        leftOverride.rotation = 180.0f;
        setCursorOverrideLeft(leftOverride);

        CursorOverrideData rightOverride = new CursorOverrideData();
        rightOverride.texture = texture;
        setCursorOverrideRight(rightOverride);

        CursorOverrideData diagonalTopLeftOverride = new CursorOverrideData();
        diagonalTopLeftOverride.texture = texture;
        diagonalTopLeftOverride.rotation = 135.0f;
        setCursorOverrideDiagonalTopLeft(diagonalTopLeftOverride);

        CursorOverrideData diagonalTopRightOverride = new CursorOverrideData();
        diagonalTopRightOverride.texture = texture;
        diagonalTopRightOverride.rotation = 45.0f;
        setCursorOverrideDiagonalTopRight(diagonalTopRightOverride);

        CursorOverrideData diagonalBottomLeftOverride = new CursorOverrideData();
        diagonalBottomLeftOverride.texture = texture;
        diagonalBottomLeftOverride.rotation = -135.0f;
        setCursorOverrideDiagonalBottomLeft(diagonalBottomLeftOverride);

        CursorOverrideData diagonalBottomRightOverride = new CursorOverrideData();
        diagonalBottomRightOverride.texture = texture;
        diagonalBottomRightOverride.rotation = -45.0f;
        setCursorOverrideDiagonalBottomRight(diagonalBottomRightOverride);
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
