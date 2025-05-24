package StreakTheSpire.UI.Layout;

import StreakTheSpire.UI.UIElement;
import StreakTheSpire.Utils.Properties.Property;

public abstract class UILayoutBoxElement extends UIElement {
    private Property<Boolean> preserveAspectRatio = new Property<>(true);

    public boolean shouldPreserveAspectRatio() { return preserveAspectRatio.get(); }
    public Property<Boolean> getPreserveAspectRatioProperty() { return preserveAspectRatio; }
    public void setPreserveAspectRatio(boolean preserveAspectRatio) { this.preserveAspectRatio.set(preserveAspectRatio); }
}
