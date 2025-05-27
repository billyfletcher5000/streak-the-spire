package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Lifetime.IDestroyable;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class CursorOverride implements IDestroyable {

    private static final Color SHADOW_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.15F);
    private static final float SHADOW_OFFSET_X = -10.0F * Settings.scale;
    private static final float SHADOW_OFFSET_Y = 8.0F * Settings.scale;

    private Property<Boolean> overrideEnabledProperty = null;
    private CursorOverrideData data = null;

    public CursorOverrideData getData() { return data; }
    public void setData(CursorOverrideData data) { this.data = data; updateCursorVisibility(); }

    public CursorOverride(Property<Boolean> overrideEnabledProperty) {
        this.overrideEnabledProperty = overrideEnabledProperty;
        overrideEnabledProperty.addOnChangedSubscriber(this::updateCursorVisibility);
    }

    public void onDestroy() {
        GameCursor.hidden = false;
    }

    private void updateCursorVisibility() {
        GameCursor.hidden = overrideEnabledProperty.get() && data != null && data.texture != null;
    }

    public void render(SpriteBatch sb) {
        if(overrideEnabledProperty.get() && data != null && data.texture != null && (!Settings.isTouchScreen || Settings.isDev)) {
            Vector2 scaledOffset = new Vector2(data.offset.x * Settings.scale, data.offset.y * Settings.scale);
            Vector2 texSize = new Vector2(data.texture.getWidth(), data.texture.getHeight());
            Vector2 texHalfSize = texSize.cpy().scl(0.5f);

            sb.setColor(SHADOW_COLOR);
            sb.draw(data.texture, (float) InputHelper.mX - texHalfSize.x - SHADOW_OFFSET_X + scaledOffset.x, (float)InputHelper.mY - texHalfSize.y - SHADOW_OFFSET_Y + scaledOffset.y, texHalfSize.x, texHalfSize.y, texSize.x, texSize.y, Settings.scale, Settings.scale, data.rotation, 0, 0, data.texture.getWidth(), data.texture.getHeight(), data.flipX, data.flipY);
            sb.setColor(Color.WHITE);
            sb.draw(data.texture, (float)InputHelper.mX - texHalfSize.x + scaledOffset.x, (float)InputHelper.mY - texHalfSize.y + scaledOffset.y, texHalfSize.x, texHalfSize.y, texSize.x, texSize.y, Settings.scale, Settings.scale, data.rotation, 0, 0, data.texture.getWidth(), data.texture.getHeight(), data.flipX, data.flipY);
        }
    }
}
