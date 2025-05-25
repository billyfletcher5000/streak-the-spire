package StreakTheSpire.UI;

import StreakTheSpire.Utils.StreakTheSpireTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;

public class UIDebugDimensionsDisplay extends UINineSliceElement {
    protected static NineSliceTexture debugBoxNinesliceTexture = null;

    private UIElement targetElement;

    public UIDebugDimensionsDisplay(UIElement targetElement) {
        super();
        this.targetElement = targetElement;

        if(debugBoxNinesliceTexture == null) {
            debugBoxNinesliceTexture = new NineSliceTexture(StreakTheSpireTextureDatabase.DEBUG_BOX_NINESLICE.getTexture(), 8, 8, 8, 8);
        }

        this.setNineSliceTexture(debugBoxNinesliceTexture);
        updateRelativeToTarget();

        addChild(new UIDebugCentreDisplay(targetElement));
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        updateRelativeToTarget();
    }

    private void updateRelativeToTarget() {
        setDimensions(targetElement.getDimensions());
        setColor(targetElement.getDebugColor());
    }

    @Override
    protected void elementRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(targetElement.getLocalToWorldTransform(), spriteBatch, transformedAlpha);
    }
}
