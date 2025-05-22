package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterSkeletonDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.SkeletonModifier;
import StreakTheSpire.UI.UIScaleBoxElement;
import StreakTheSpire.UI.UISpineAnimationElement;

public class CharacterSkeletonDisplayView extends UIScaleBoxElement implements IView {

    private CharacterSkeletonDisplayModel model;
    private UISpineAnimationElement skeletonAnimation;

    public CharacterSkeletonDisplayModel getModel() { return model; }
    public UISpineAnimationElement getSkeletonAnimation() { return skeletonAnimation; }

    public CharacterSkeletonDisplayView(CharacterSkeletonDisplayModel model) {
        super(model.baseDimensions.get());
        this.model = model;

        skeletonAnimation = new UISpineAnimationElement(model.skeletonOffset.get(), model.skeletonAtlasUrl.get(), model.skeletonJsonUrl.get(), new SkeletonModifier(model.skeletonBonesToRemove, model.skeletonBonesToKeep));
        skeletonAnimation.setLocalRotation(model.skeletonRotationAdjustment.get());
        skeletonAnimation.getAnimationStateData().setMix(model.skeletonHitAnimationName.get(), model.skeletonIdleAnimationName.get(), model.skeletonAnimationMixDuration.get());
        addChild(skeletonAnimation);
    }

    public void enqueueIdleAnimation(boolean loop) {
        skeletonAnimation.getAnimationState().addAnimation(0, model.skeletonIdleAnimationName.get(), loop, 0f);
    }

    public void enqueueHitAnimation() {
        skeletonAnimation.getAnimationState().addAnimation(0, model.skeletonHitAnimationName.get(), false, 0.0f);
    }

    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            CharacterSkeletonDisplayModel characterSkeletonDisplayModel = (CharacterSkeletonDisplayModel) model;
            if(characterSkeletonDisplayModel != null) {
                StreakTheSpire.logDebug("CharacterSkeletonDisplayView created!");
                return (TView) new CharacterSkeletonDisplayView(characterSkeletonDisplayModel);
            }

            StreakTheSpire.logWarning("CharacterSkeletonDisplayViewFactory failed to create view!");
            return null;
        }
    };
}
