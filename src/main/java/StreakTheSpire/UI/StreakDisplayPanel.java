package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.ui.panels.AbstractPanel;

public class StreakDisplayPanel extends AbstractPanel {

    public StreakDisplayPanel(float show_x, float show_y, float hide_x, float hide_y, float shadow_offset_x, float shadow_offset_y, Texture img, boolean startHidden) {
        super(show_x, show_y, hide_x, hide_y, shadow_offset_x, shadow_offset_y, img, startHidden);
    }

    public StreakDisplayPanel(float show_x, float show_y, float hide_x, float hide_y, Texture img, boolean startHidden) {
        super(show_x, show_y, hide_x, hide_y, img, startHidden);
    }
}
