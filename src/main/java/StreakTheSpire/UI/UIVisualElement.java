package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;

public class UIVisualElement extends UIElement {
    public static class TweenTypes {
        public static final int COLOR_RGBA = 10;
        public static final int COLOR_R = 11;
        public static final int COLOR_G = 12;
        public static final int COLOR_B = 13;
        public static final int COLOR_A = 14;
    }

    protected Color color = Color.WHITE.cpy();

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    @Override
    public int getValues(UIElement target, int tweenType, float[] returnValues) {
        UIVisualElement visualElement = (UIVisualElement) target;
        if(visualElement != null) {
            switch (tweenType) {
                case TweenTypes.COLOR_RGBA:
                    returnValues[0] = visualElement.color.r;
                    returnValues[1] = visualElement.color.g;
                    returnValues[2] = visualElement.color.b;
                    returnValues[3] = visualElement.color.a;
                    return 4;

                case TweenTypes.COLOR_R:
                    returnValues[0] = visualElement.color.r;
                    return 1;

                case TweenTypes.COLOR_G:
                    returnValues[0] = visualElement.color.g;
                    return 1;

                case TweenTypes.COLOR_B:
                    returnValues[0] = visualElement.color.b;
                    return 1;

                case TweenTypes.COLOR_A:
                    returnValues[0] = visualElement.color.a;
                    return 1;
            }
        }

        return super.getValues(target, tweenType, returnValues);
    }

    @Override
    public void setValues(UIElement target, int tweenType, float[] newValues) {
        UIVisualElement visualElement = (UIVisualElement) target;
        if(visualElement != null) {
            switch (tweenType) {
                case TweenTypes.COLOR_RGBA:
                    visualElement.color.r = newValues[0];
                    visualElement.color.g = newValues[1];
                    visualElement.color.b = newValues[2];
                    visualElement.color.a = newValues[3];
                    visualElement.color.clamp();
                    return;

                case TweenTypes.COLOR_R:
                    visualElement.color.r = newValues[0];
                    visualElement.color.clamp();
                    return;

                case TweenTypes.COLOR_G:
                    visualElement.color.g = newValues[0];
                    visualElement.color.clamp();
                    return;

                case TweenTypes.COLOR_B:
                    visualElement.color.b = newValues[0];
                    visualElement.color.clamp();
                    return;

                case TweenTypes.COLOR_A:
                    visualElement.color.a = newValues[0];
                    visualElement.color.clamp();

                    return;
            }
        }
        
        super.setValues(target, tweenType, newValues);
    }
}
