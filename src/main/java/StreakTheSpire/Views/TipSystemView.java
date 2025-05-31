package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;
import StreakTheSpire.Models.TipDataModel;
import StreakTheSpire.Models.TipSystemModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Lifetime.IDestroyable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TipSystemView implements IView, IDestroyable {

    private enum DisplayQuadrant {
        BottomRight,
        TopRight,
        BottomLeft,
        TopLeft,
    }

    HashMap<DisplayQuadrant, Rectangle> quadrantToArea = new HashMap<>();
    HashMap<DisplayQuadrant, Vector2> quadrantToTipBoxPosition = new HashMap<>();

    private static class TipView {
        private final TipDataModel model;
        private final ArrayList<PowerTip> localPowerTips = new ArrayList<>();
        private final ArrayList<PowerTip> slayTheRelicsPowerTips = new ArrayList<>();

        public TipView(TipDataModel model) {
            this.model = model;

            PowerTip tip = new PowerTip();
            tip.header = model.tipHeaderText.get();
            tip.body = model.tipBodyText.get() + " NL " + model.tipAdditionalLocalBodyText;
            localPowerTips.add(tip);

            PowerTip tip2 = new PowerTip();
            tip2.header = model.tipHeaderText.get();
            tip2.body = model.tipBodyText.get();
            slayTheRelicsPowerTips.add(tip2);
        }

        public TipDataModel getModel() { return model; }
        public Hitbox getHitbox() { return model.triggerHitbox.get(); }
        public ArrayList<PowerTip> getLocalPowerTips() { return localPowerTips; }
        public ArrayList<PowerTip> getSlayTheRelicsPowerTips() { return slayTheRelicsPowerTips; }
    }

    private final TipSystemModel model;
    private final ArrayList<TipView> views = new ArrayList<>();

    public TipSystemView(TipSystemModel model) {
        this.model = model;

        buildQuadrantMap();

        model.tipData.addOnItemAddedSubscriber(this::onTipDataAdded);
        model.tipData.addOnItemRemovedSubscriber(this::onTipDataRemoved);
        populateViews();
    }

    @Override
    public void onDestroy() {
        model.tipData.removeOnItemAddedSubscriber(this::onTipDataAdded);
        model.tipData.removeOnItemRemovedSubscriber(this::onTipDataRemoved);
    }

    private void populateViews() {
        for(TipDataModel tipDataModel : model.tipData) {
            createView(tipDataModel);
        }
    }

    private void onTipDataAdded(Object tipDataObj) {
        TipDataModel tipData = (TipDataModel) tipDataObj;
        createView(tipData);
    }

    private void onTipDataRemoved(Object tipDataObj) {
        TipDataModel tipData = (TipDataModel) tipDataObj;
        removeView(tipData);
    }

    private void createView(TipDataModel tipDataModel) {
        if(views.stream().noneMatch(view -> view.model == tipDataModel))
            views.add(new TipView(tipDataModel));
    }

    private void removeView(TipDataModel tipDataModel) {
        views.removeIf(view -> view.model == tipDataModel);
    }

    public void update() {
        StreakTheSpire.clearSlayTheRelicsData();

        boolean hasRenderedATip = false;

        for (TipView view : views) {
            if(view.getModel().isActive.get() && view.getHitbox().hovered && !hasRenderedATip) {
                DisplayQuadrant quadrant = getBestQuadrant();
                //Vector2 position = quadrantToTipBoxPosition.get(quadrant);
                Vector2 position = new Vector2(InputHelper.mX, InputHelper.mY);
                TipHelper.queuePowerTips(position.x, position.y, view.getLocalPowerTips());
                hasRenderedATip = true;
            }

            StreakTheSpire.slayTheRelicsHitboxes.add(view.getHitbox());
            StreakTheSpire.slayTheRelicsPowerTips.add(view.getSlayTheRelicsPowerTips());
        }
    }

    private DisplayQuadrant getBestQuadrant() {
        HashMap<DisplayQuadrant, Float> quadrantToCumulativeCoverage = new HashMap<>();

        for(Rectangle avoidArea : model.areasToAvoid) {
            for(Map.Entry<DisplayQuadrant, Rectangle> quadrantToAreaEntry : quadrantToArea.entrySet()) {
                Rectangle quadrantArea = quadrantToAreaEntry.getValue();
                DisplayQuadrant displayQuadrant = quadrantToAreaEntry.getKey();

                float prevCoverage = quadrantToCumulativeCoverage.getOrDefault(displayQuadrant, 0f);
                float xCoverage = Math.min(quadrantArea.x + quadrantArea.width, avoidArea.x + avoidArea.width) - Math.max(quadrantArea.x, avoidArea.x);
                float yCoverage = Math.min(quadrantArea.y + quadrantArea.height, avoidArea.y + avoidArea.height) - Math.max(quadrantArea.y, avoidArea.y);

                quadrantToCumulativeCoverage.put(displayQuadrant, prevCoverage + (xCoverage * yCoverage));
            }
        }

        DisplayQuadrant result = DisplayQuadrant.BottomRight;
        float highestCoverage = 0f;
        for(Map.Entry<DisplayQuadrant, Float> entry : quadrantToCumulativeCoverage.entrySet()) {
            if(entry.getValue() > highestCoverage) {
                highestCoverage = entry.getValue();
                result = entry.getKey();
            }
        }

        return result;
    }

    private void buildQuadrantMap() {
        float screenWidth = Settings.WIDTH;
        float screenHeight = Settings.HEIGHT;
        float halfScreenWidth = screenWidth / 2.0f;
        float halfScreenHeight = screenHeight / 2.0f;

        quadrantToArea.put(DisplayQuadrant.TopLeft, new Rectangle(0, halfScreenHeight, halfScreenWidth, halfScreenHeight));
        quadrantToArea.put(DisplayQuadrant.BottomLeft, new Rectangle(0, 0, halfScreenWidth, halfScreenHeight));
        quadrantToArea.put(DisplayQuadrant.TopRight, new Rectangle(halfScreenWidth, halfScreenHeight, halfScreenWidth, halfScreenHeight));
        quadrantToArea.put(DisplayQuadrant.BottomRight, new Rectangle(halfScreenWidth, 0, halfScreenWidth, halfScreenHeight));

        quadrantToTipBoxPosition.put(DisplayQuadrant.TopLeft, new Vector2(20.0f * Settings.xScale, screenHeight - (200.0f * Settings.yScale)));
        quadrantToTipBoxPosition.put(DisplayQuadrant.BottomLeft, new Vector2( 20.0f * Settings.xScale, 240.0f * Settings.yScale));
        quadrantToTipBoxPosition.put(DisplayQuadrant.TopRight, new Vector2(screenWidth - (340.0f * Settings.xScale), screenHeight - (200.0f * Settings.yScale)));
        quadrantToTipBoxPosition.put(DisplayQuadrant.BottomRight, new Vector2(screenWidth - (340.0f * Settings.xScale), 240.0f * Settings.yScale));
    }


    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            TipSystemModel tipSystemModel = (TipSystemModel) model;
            if(tipSystemModel != null) {
                StreakTheSpire.logDebug("TipSystemView created!");
                return (TView) new TipSystemView(tipSystemModel);
            }

            StreakTheSpire.logWarning("TipSystemView failed to create view!");
            return null;
        }
    };
}
