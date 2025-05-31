package StreakTheSpire.Controllers;

import StreakTheSpire.Models.TipDataModel;
import StreakTheSpire.Models.TipSystemModel;
import com.badlogic.gdx.math.Rectangle;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class TipSystemController {
    private TipSystemModel model;

    public TipSystemController(TipSystemModel model) {
        this.model = model;
    }

    public TipDataModel createTipDataModel(boolean isActive, Hitbox hitbox, String headerText, String bodyText, String additionalLocalBodyText) {
        TipDataModel tipDataModel = new TipDataModel();

        tipDataModel.isActive.set(isActive);
        tipDataModel.triggerHitbox.set(hitbox);
        tipDataModel.tipHeaderText.set(headerText);
        tipDataModel.tipBodyText.set(bodyText);
        tipDataModel.tipAdditionalLocalBodyText.set(additionalLocalBodyText);

        model.tipData.add(tipDataModel);

        return tipDataModel;
    }

    public void destroyTipDataModel(TipDataModel tipDataModel) {
        model.tipData.remove(tipDataModel);
    }

    public void addAreaToAvoid(Rectangle tipAvoidanceRectangle) {
        model.areasToAvoid.add(tipAvoidanceRectangle);
    }

    public void removeAreaFromAvoid(Rectangle tipAvoidanceRectangle) {
        model.areasToAvoid.remove(tipAvoidanceRectangle);
    }
}
