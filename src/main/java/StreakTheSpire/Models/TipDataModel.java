package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class TipDataModel {

    public Property<Boolean> isActive = new Property<>(true);
    public Property<Hitbox> triggerHitbox = new Property<>(new Hitbox(0,0,0,0));
    public Property<String> tipHeaderText = new Property<>("");
    public Property<String> tipBodyText = new Property<>("");
    public Property<String> tipAdditionalLocalBodyText = new Property<>(null);

    public TipDataModel() {}
}
