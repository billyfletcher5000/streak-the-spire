package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterSkeletonDisplayModel;
import StreakTheSpire.UI.SkeletonModifier;
import StreakTheSpire.UI.UISpineAnimationElement;

public class CharacterSkeletonDisplayView extends UISpineAnimationElement implements IView {

    private CharacterSkeletonDisplayModel model;

    public CharacterSkeletonDisplayView(CharacterSkeletonDisplayModel model) {
        super(model.skeletonAtlasUrl.get(), model.skeletonJsonUrl.get(), new SkeletonModifier(model.skeletonBonesToRemove, model.skeletonBonesToKeep));
        this.model = model;
        setLocalRotation(model.skeletonRotationAdjustment.get());
    }
}
