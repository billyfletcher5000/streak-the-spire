package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterSkeletonDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
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
        super(model.dimensions.get());
        this.model = model;

        skeletonAnimation = new UISpineAnimationElement(model.skeletonOffset.get(), model.skeletonAtlasUrl.get(), model.skeletonJsonUrl.get(), new SkeletonModifier(model.skeletonBonesToRemove, model.skeletonBonesToKeep));
        skeletonAnimation.setLocalRotation(model.skeletonRotationAdjustment.get());
        skeletonAnimation.getAnimationState().setAnimation(0, model.skeletonIdleAnimationName.get(), false);
        skeletonAnimation.getAnimationStateData().setMix(model.skeletonHitAnimationName.get(), model.skeletonIdleAnimationName.get(), 0.1F);
        addChild(skeletonAnimation);
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
