package StreakTheSpire.Views;

import StreakTheSpire.Models.UIButtonDataModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UIButtonElement;
import StreakTheSpire.Utils.TextureCache;
import com.badlogic.gdx.math.Vector2;

public class AttachedPreferencesButtonDisplay extends UIButtonElement {
    private UIButtonDataModel model;

    public UIButtonDataModel getModel() { return model; }
    public void setModel(UIButtonDataModel model) {
        if(this.model != model) {
            this.model = model;

            TextureCache textureCache = StreakTheSpire.get().getTextureCache();

            try {
                if(model.backgroundNormalPath.get() != null) setBackgroundNormal(textureCache.getTexture(model.backgroundNormalPath.get()));
                if(model.backgroundHoverPath.get() != null) setBackgroundHover(textureCache.getTexture(model.backgroundHoverPath.get()));
                if(model.backgroundPressedPath.get() != null) setBackgroundPressed(textureCache.getTexture(model.backgroundPressedPath.get()));
                if(model.midgroundPath.get() != null) setMidground(textureCache.getTexture(model.midgroundPath.get()));
                if(model.foregroundPath.get() != null) setForeground(textureCache.getTexture(model.foregroundPath.get()));
                setPressedOffset(model.pressedOffset.get());
            }
            catch (Exception e) {
                StreakTheSpire.logError(e.getMessage());
            }
        }
    }

    public AttachedPreferencesButtonDisplay(Vector2 localPosition) {
        super();
        initialise(localPosition);
    }
}
