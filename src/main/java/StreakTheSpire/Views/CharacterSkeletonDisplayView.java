package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterSkeletonDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.SkeletonModifier;
import StreakTheSpire.UI.Layout.UIScaleBoxElement;
import StreakTheSpire.UI.UISpineAnimationElement;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;

public class CharacterSkeletonDisplayView extends UIScaleBoxElement implements IView {

    private final CharacterSkeletonDisplayModel model;
    private final UISpineAnimationElement skeletonAnimation;
    private AnimationState.AnimationStateListener animStateListener = null;

    public CharacterSkeletonDisplayModel getModel() { return model; }
    public UISpineAnimationElement getSkeletonAnimation() { return skeletonAnimation; }

    public CharacterSkeletonDisplayView(CharacterSkeletonDisplayModel model) {
        super(model.baseDimensions.get());
        this.model = model;

        SkeletonModifier skeletonModifier = !model.skeletonBonesToKeep.isEmpty() || !model.skeletonBonesToRemove.isEmpty() ? new SkeletonModifier(model.skeletonBonesToRemove, model.skeletonBonesToKeep) : null;

        skeletonAnimation = new UISpineAnimationElement(model.skeletonOffset.get(), model.skeletonAtlasUrl.get(), model.skeletonJsonUrl.get(), new SkeletonModifier(model.skeletonBonesToRemove, model.skeletonBonesToKeep));
        skeletonAnimation.setLocalRotation(model.skeletonRotationAdjustment.get());
        skeletonAnimation.getAnimationStateData().setMix(model.skeletonHitAnimationName.get(), model.skeletonIdleAnimationName.get(), model.skeletonAnimationMixDuration.get());
        addChild(skeletonAnimation);

        if(model.skeletonHitAnimationSpeed.get() != 1.0f || model.skeletonIdleAnimationSpeed.get() != 1.0f) {
            animStateListener = new AnimationState.AnimationStateListener() {

                @Override
                public void event(int i, Event event) {}

                @Override
                public void complete(int i, int i1) {}

                @Override
                public void start(int i) {
                    AnimationState state = skeletonAnimation.getAnimationState();
                    AnimationState.TrackEntry entry = state.getCurrent(i);
                    String animName = entry.getAnimation().getName();
                    if(animName.equals(model.skeletonHitAnimationName.get())) {
                        state.setTimeScale(model.skeletonHitAnimationSpeed.get());
                    } else if(animName.equals(model.skeletonIdleAnimationName.get())) {
                        state.setTimeScale(model.skeletonIdleAnimationSpeed.get());
                    }
                }

                @Override
                public void end(int i) {
                }
            };
            skeletonAnimation.getAnimationState().addListener(animStateListener);
        }

        immediatelyPlayIdleAnimation(true);
    }

    @Override
    protected void elementDestroy() {
        super.elementDestroy();
        if(animStateListener != null) {
            skeletonAnimation.getAnimationState().removeListener(animStateListener);
        }
    }

    public void immediatelyPlayIdleAnimation(boolean loop) {
        AnimationState state = skeletonAnimation.getAnimationState();
        state.setTimeScale(model.skeletonIdleAnimationSpeed.get());
        state.setAnimation(0, model.skeletonIdleAnimationName.get(), loop);
    }

    public void enqueueIdleAnimation(boolean loop) {
        AnimationState state = skeletonAnimation.getAnimationState();

        if(state.getCurrent(0) == null)
            state.setTimeScale(model.skeletonIdleAnimationSpeed.get());

        state.addAnimation(0, model.skeletonIdleAnimationName.get(), loop, 0f);
    }

    public void immediatelyPlayHitAnimation() {
        AnimationState state = skeletonAnimation.getAnimationState();
        state.setTimeScale(model.skeletonHitAnimationSpeed.get());
        state.setAnimation(0, model.skeletonHitAnimationName.get(), false);
    }

    public void enqueueHitAnimation() {
        AnimationState state = skeletonAnimation.getAnimationState();

        if(state.getCurrent(0) == null)
            state.setTimeScale(model.skeletonHitAnimationSpeed.get());

        state.addAnimation(0, model.skeletonHitAnimationName.get(), false, 0f);
    }

    public float getHitAnimationDuration() {
        Animation anim = skeletonAnimation.getSkeleton().getData().findAnimation(model.skeletonHitAnimationName.get());
        return anim.getDuration();
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
