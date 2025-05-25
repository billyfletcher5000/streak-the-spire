package StreakTheSpire.UI;

import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public class UIDebugCentreDisplay extends UIImageElement {

    private UIElement targetElement;

    public UIDebugCentreDisplay(UIElement targetElement) {
        super(Vector2.Zero, StreakTheSpireTextureDatabase.DEBUG_CENTRE.getTexture(), targetElement.getDebugColor());
        this.targetElement = targetElement;
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        setColor(targetElement.getDebugColor());
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(targetElement.getLocalToWorldTransform(), spriteBatch, transformedAlpha);
    }
}
