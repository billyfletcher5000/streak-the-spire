package StreakTheSpire.Views;

import StreakTheSpire.Models.GameStateModel;
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

    // Copied from AbstractCreature.java, then adjusted
    protected static final float TIP_X_THRESHOLD = 1564.0F * Settings.scale;
    protected static final float TIP_OFFSET_R_X = 4.0F * Settings.scale;
    protected static final float TIP_OFFSET_L_X = -320.0F * Settings.scale;

    private static class TipView {
        private final TipDataModel model;
        private final ArrayList<PowerTip> localPowerTips = new ArrayList<>();
        private final ArrayList<PowerTip> slayTheRelicsPowerTips = new ArrayList<>();

        public TipView(TipDataModel model) {
            this.model = model;

            PowerTip tip = new PowerTip();
            localPowerTips.add(tip);

            PowerTip tip2 = new PowerTip();
            slayTheRelicsPowerTips.add(tip2);

            updateTips();

            model.tipHeaderText.addOnChangedSubscriber(this::updateTips);
            model.tipBodyText.addOnChangedSubscriber(this::updateTips);
            model.tipAdditionalLocalBodyText.addOnChangedSubscriber(this::updateTips);
        }

        public void onDestroy() {
            model.tipHeaderText.removeOnChangedSubscriber(this::updateTips);
            model.tipBodyText.removeOnChangedSubscriber(this::updateTips);
            model.tipAdditionalLocalBodyText.removeOnChangedSubscriber(this::updateTips);
        }

        private void updateTips() {
            PowerTip tip = localPowerTips.get(0);
            tip.header = model.tipHeaderText.get();
            tip.body = model.tipBodyText.get() + " NL " + model.tipAdditionalLocalBodyText;

            PowerTip tip2 = getSlayTheRelicsPowerTips().get(0);
            tip2.header = model.tipHeaderText.get();
            tip2.body = model.tipBodyText.get();
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
        for(TipView tipView : views) {
            if(tipView.getModel() == tipDataModel)
                tipView.onDestroy();
        }

        views.removeIf(view -> view.getModel() == tipDataModel);
    }

    public void update() {
        StreakTheSpire.clearSlayTheRelicsData();

        boolean hasRenderedATip = false;
        boolean didRenderUIThisFrame = StreakTheSpire.get().didUIRenderThisFrame();
        GameStateModel gsm = StreakTheSpire.get().getGameStateModel();

        for (TipView view : views) {
            if(didRenderUIThisFrame
                    && view.getModel().isActive.get()
                    && (!gsm.editModeActive.get() || (gsm.editModeActive.get() && view.getModel().showDuringEditMode.get()))) {
                Hitbox hitbox = view.getHitbox();
                if (hitbox.hovered && !hasRenderedATip) {
                    ArrayList<PowerTip> tips = view.getLocalPowerTips();

                    float x = 0f;
                    float y = 0f;
                    if (hitbox.cX + hitbox.width / 2.0F < TIP_X_THRESHOLD) {
                        x = hitbox.cX + hitbox.width / 2.0F + TIP_OFFSET_R_X;
                        y = hitbox.cY + TipHelper.calculateToAvoidOffscreen(tips, hitbox.cY);
                    } else {
                        x = hitbox.cX - hitbox.width / 2.0F + TIP_OFFSET_L_X;
                        y = hitbox.cY + TipHelper.calculateToAvoidOffscreen(tips, hitbox.cY);
                    }
                    
                    TipHelper.queuePowerTips(x, y, tips);
                    hasRenderedATip = true;
                }

                StreakTheSpire.slayTheRelicsHitboxes.add(view.getHitbox());
                StreakTheSpire.slayTheRelicsPowerTips.add(view.getSlayTheRelicsPowerTips());
            }
        }
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
