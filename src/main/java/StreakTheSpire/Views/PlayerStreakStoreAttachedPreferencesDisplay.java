package StreakTheSpire.Views;

import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIExpandBoxElement;
import StreakTheSpire.UI.Layout.UIGridLayoutGroup;
import StreakTheSpire.UI.UIElement;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;

public class PlayerStreakStoreAttachedPreferencesDisplay extends UIElement implements IView {

    private final PlayerStreakStoreView playerStreakStoreView;
    private UIGridLayoutGroup gridLayoutGroup = null;
    private float minimumSize = 48f;
    private boolean locationDirty = false;

    @Override
    public void setDimensions(Vector2 dimensions) {
        super.setDimensions(dimensions);
        // TODO: With a proper layouting system this would be handled by the child's anchoring
        if(gridLayoutGroup != null)
            gridLayoutGroup.setDimensions(dimensions);
    }

    private static class BorderStyleButtonDataPair {
        public BorderStyleButtonDataPair(String identifier, UIButtonDataModel buttonData) {
            this.identifier = identifier;
            this.buttonData = buttonData;
        }
        public String identifier;
        public UIButtonDataModel buttonData;
    }

    private final ArrayList<BorderStyleButtonDataPair> borderStyleIdentifierToButtonData = new ArrayList<>();

    private AttachedPreferencesButtonDisplay borderStyleButton;

    public PlayerStreakStoreAttachedPreferencesDisplay(PlayerStreakStoreView streakStoreView) {
        super();

        this.playerStreakStoreView = streakStoreView;
        playerStreakStoreView.getLocalPositionProperty().addOnChangedSubscriber(this::markLocationDirty);
        playerStreakStoreView.getDimensionsProperty().addOnChangedSubscriber(this::markLocationDirty);

        gridLayoutGroup = new UIGridLayoutGroup();
        gridLayoutGroup.setRestrictToSmallestSize(true);
        addChild(gridLayoutGroup);

        createButtons();

        recalculateOptimalLocation();

        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();
        gsm.editModeActive.addOnChangedSubscriber(this::onEditModeChanged);
        setVisibleRecursive(gsm.editModeActive.get());
    }

    @Override
    public void close() {
        super.close();

        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();
        gsm.editModeActive.removeOnChangedSubscriber(this::onEditModeChanged);

        if(playerStreakStoreView != null) {
            playerStreakStoreView.getLocalPositionProperty().removeOnChangedSubscriber(this::markLocationDirty);
            playerStreakStoreView.getDimensionsProperty().removeOnChangedSubscriber(this::markLocationDirty);
        }

        if(borderStyleButton != null)
            borderStyleButton.removeOnClickedSubscriber(this::cycleBorderStyle);

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        preferences.borderStyle.removeOnChangedSubscriber(this::updateBorderStyleButton);
    }

    private void onEditModeChanged() {
        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();
        setVisibleRecursive(gsm.editModeActive.get());
    }

    private void createButtons() {
        createBorderStyleButton();
    }

    private void createBorderStyleButton() {

        BorderStyleSetModel borderStyles = StreakTheSpire.get().getBorderStyles();

        for(BorderStyleModel style : borderStyles.borderStyles) {
            UIButtonDataModel buttonDataModel = new UIButtonDataModel();
            buttonDataModel.backgroundNormalPath.set("StreakTheSpire/textures/ui/button_normal.png");
            buttonDataModel.backgroundHoverPath.set("StreakTheSpire/textures/ui/button_highlighted.png");
            buttonDataModel.backgroundPressedPath.set("StreakTheSpire/textures/ui/button_pressed.png");
            buttonDataModel.midgroundPath.set("StreakTheSpire/textures/ui/button_overlay_border.png");
            buttonDataModel.foregroundPath.set(style.buttonOverlayTexturePath.get());

            borderStyleIdentifierToButtonData.add(new BorderStyleButtonDataPair(style.identifier.get(), buttonDataModel));
        }

        borderStyleButton = new AttachedPreferencesButtonDisplay(Vector2.Zero);
        borderStyleButton.addOnClickedSubscriber(this::cycleBorderStyle);

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        preferences.borderStyle.addOnChangedSubscriber(this::updateBorderStyleButton);

        updateBorderStyleButton();

        Vector2 buttonDimensions = borderStyleButton.getDimensions();
        float maxButtonSize = Math.max(buttonDimensions.x, buttonDimensions.y);
        if(maxButtonSize > minimumSize)
            minimumSize = maxButtonSize;

        UIExpandBoxElement expandBoxElement = new UIExpandBoxElement();
        expandBoxElement.addChild(borderStyleButton);
        expandBoxElement.setPreserveAspectRatio(true);
        gridLayoutGroup.addChild(expandBoxElement);
    }

    private void updateBorderStyleButton() {
        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        UIButtonDataModel buttonData = getButtonDataForIdentifier(preferences.borderStyle.get());
        if(buttonData != null) {
            borderStyleButton.setModel(buttonData);
        } else {
            StreakTheSpire.logError("Border style button data not found for style identifier: " + preferences.borderStyle.get());
        }
    }

    private void cycleBorderStyle() {
        UIButtonDataModel buttonData = borderStyleButton.getModel();
        int index = IntStream.range(0, borderStyleIdentifierToButtonData.size())
                .filter(streamIndex -> buttonData.equals(borderStyleIdentifierToButtonData.get(streamIndex).buttonData))
                .findFirst()
                .orElse(-1);

        index = (index + 1) % borderStyleIdentifierToButtonData.size();
        BorderStyleButtonDataPair pair = borderStyleIdentifierToButtonData.get(index);
        borderStyleButton.setModel(pair.buttonData);

        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        preferences.borderStyle.set(pair.identifier);
    }

    private void markLocationDirty() {
        locationDirty = true;
    }

    private enum Direction {
        Left, Right, Up, Down
    }

    private void recalculateOptimalLocation() {
        Vector2 targetPosition = playerStreakStoreView.getLocalPosition();
        Vector2 targetDimensions = playerStreakStoreView.getDimensions();
        Vector2 targetHalfDimensions = targetDimensions.cpy().scl(0.5f);
        Vector2 targetBottomLeft = targetPosition.cpy().sub(targetHalfDimensions);
        Vector2 targetTopRight = targetPosition.cpy().add(targetHalfDimensions);

        Vector2 screenDimensions = new Vector2(Settings.WIDTH / Settings.xScale, Settings.HEIGHT / Settings.yScale);

        // This seems like a horrendous way to do this but it's easy to follow at least
        Direction direction = Direction.Left;
        float maxDistance = targetBottomLeft.x - 0f;

        float bottomDistance = targetBottomLeft.y - 0f;
        float rightDistance = screenDimensions.x - targetTopRight.x;
        float topDistance = screenDimensions.y - targetTopRight.y;

        float screenAspectRatio = (float)Settings.WIDTH / Settings.HEIGHT;
        bottomDistance *= screenAspectRatio;
        topDistance *= screenAspectRatio;

        float targetAspectRatio = targetDimensions.x / targetDimensions.y;
        bottomDistance *= targetAspectRatio;
        topDistance *= targetAspectRatio;

        if(bottomDistance > maxDistance) {
            maxDistance = bottomDistance;
            direction = Direction.Down;
        }
        if(rightDistance > maxDistance) {
            maxDistance = rightDistance;
            direction = Direction.Right;
        }
        if(topDistance > maxDistance) {
            maxDistance = topDistance;
            direction = Direction.Up;
        }

        Vector2 newPosition = null;
        Vector2 newDimensions = null;

        switch (direction) {
            case Left:
                newPosition = new Vector2(targetBottomLeft.x - (minimumSize * 0.5f), targetPosition.y);
                newDimensions = new Vector2(minimumSize, targetDimensions.y);
                break;
            case Right:
                newPosition = new Vector2(targetTopRight.x + (minimumSize * 0.5f), targetPosition.y);
                newDimensions = new Vector2(minimumSize, targetDimensions.y);
                break;
            case Up:
                newPosition = new Vector2(targetPosition.x, targetTopRight.y + (minimumSize * 0.5f));
                newDimensions = new Vector2(targetDimensions.x, minimumSize);
                break;
            case Down:
            default:
                newPosition = new Vector2(targetPosition.x, targetBottomLeft.y - (minimumSize * 0.5f));
                newDimensions = new Vector2(targetDimensions.x, minimumSize);
                break;
        }

        setLocalPosition(newPosition);
        setDimensions(newDimensions);
    }

    private String getIdentifierForButtonData(UIButtonDataModel buttonData) {
        BorderStyleButtonDataPair pair = borderStyleIdentifierToButtonData.stream().filter(testPair -> testPair.buttonData == buttonData).findAny().orElse(null);
        if(pair != null) {
            return pair.identifier;
        }

        return null;
    }

    private UIButtonDataModel getButtonDataForIdentifier(String identifier) {
        BorderStyleButtonDataPair pair = borderStyleIdentifierToButtonData.stream().filter(testPair -> Objects.equals(testPair.identifier, identifier)).findAny().orElse(null);
        if(pair != null) {
            return pair.buttonData;
        }

        return null;
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);

        if(locationDirty) {
            recalculateOptimalLocation();
            locationDirty = false;
        }
    }
}
