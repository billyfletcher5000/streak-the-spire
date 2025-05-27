package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

public class UIVisualElement extends UIElement {
    public static class TweenTypes {
        public static final int COLOR_RGBA = 10;
        public static final int COLOR_R = 11;
        public static final int COLOR_G = 12;
        public static final int COLOR_B = 13;
        public static final int COLOR_A = 14;
        public static final int MASK_RGBA = 15;
        public static final int MASK_R = 16;
        public static final int MASK_G = 17;
        public static final int MASK_B = 18;
        public static final int MASK_A = 19;
    }

    protected Property<Color> color = new Property<>(Color.WHITE.cpy());
    protected Property<Color> maskColor = new Property<>(new Color(1f, 1f, 1f, 0f));

    public Color getColor() { return color.get(); }
    public Property<Color> getColorProperty() { return color; }
    public void setColor(Color color) { this.color.set(color); }

    public Color getMaskColor() { return maskColor.get().cpy(); }
    public Property<Color> getMaskColorProperty() { return maskColor; }
    public void setMaskColor(Color color) { this.maskColor.set(color); }

    protected Color getTransformedColor(float transformedAlpha) {
        Color color = getColor();
        return new Color(color.r, color.g, color.b, color.a * transformedAlpha);
    }

    @Override
    protected void elementPreRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPreRender(transformationStack, spriteBatch, transformedAlpha);
        applyMaskColorPreRender(spriteBatch);
    }

    @Override
    protected void elementPostRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPostRender(transformationMatrix, spriteBatch, transformedAlpha);
        revertMaskColorPostRender(spriteBatch);
    }

    protected void applyMaskColorPreRender(Batch spriteBatch) {
        Color maskColor = getMaskColor();
        if(maskColor.a > Epsilon) {
            ShaderProgram maskShader = UIShaderRepository.getMaskShader();
            spriteBatch.setShader(maskShader);
            maskShader.setUniformf("u_mask_color", maskColor.r, maskColor.g, maskColor.b, maskColor.a);
        }
    }

    protected void revertMaskColorPostRender(Batch spriteBatch) {
        if(maskColor.get().a > Epsilon) {
            spriteBatch.setShader(null);
        }
    }


    @Override
    public int getValues(UIElement target, int tweenType, float[] returnValues) {
        UIVisualElement visualElement = (UIVisualElement) target;
        if(visualElement != null) {
            Color visElementColor = visualElement.getColor();
            Color visMaskColor = visualElement.getMaskColor();
            switch (tweenType) {
                case TweenTypes.COLOR_RGBA:
                    returnValues[0] = visElementColor.r;
                    returnValues[1] = visElementColor.g;
                    returnValues[2] = visElementColor.b;
                    returnValues[3] = visElementColor.a;
                    return 4;

                case TweenTypes.COLOR_R:
                    returnValues[0] = visElementColor.r;
                    return 1;

                case TweenTypes.COLOR_G:
                    returnValues[0] = visElementColor.g;
                    return 1;

                case TweenTypes.COLOR_B:
                    returnValues[0] = visElementColor.b;
                    return 1;

                case TweenTypes.COLOR_A:
                    returnValues[0] = visElementColor.a;
                    return 1;

                case TweenTypes.MASK_RGBA:
                    returnValues[0] = visMaskColor.r;
                    returnValues[1] = visMaskColor.g;
                    returnValues[2] = visMaskColor.b;
                    returnValues[3] = visMaskColor.a;
                    return 4;

                case TweenTypes.MASK_R:
                    returnValues[0] = visMaskColor.r;
                    return 1;

                case TweenTypes.MASK_G:
                    returnValues[0] = visMaskColor.g;
                    return 1;

                case TweenTypes.MASK_B:
                    returnValues[0] = visMaskColor.b;
                    return 1;

                case TweenTypes.MASK_A:
                    returnValues[0] = visMaskColor.a;
                    return 1;
            }
        }

        return super.getValues(target, tweenType, returnValues);
    }

    @Override
    public void setValues(UIElement target, int tweenType, float[] newValues) {
        UIVisualElement visualElement = (UIVisualElement) target;
        if(visualElement != null) {
            Color visElementColor;
            switch (tweenType) {
                case TweenTypes.COLOR_RGBA:
                    visElementColor = new Color(newValues[0], newValues[1], newValues[2], newValues[3]);
                    visElementColor.clamp();
                    visualElement.setColor(visElementColor);
                    return;

                case TweenTypes.COLOR_R:
                    visElementColor = visualElement.getColor().cpy();
                    visElementColor.r = newValues[0];
                    visElementColor.clamp();
                    visualElement.setColor(visElementColor);
                    return;

                case TweenTypes.COLOR_G:
                    visElementColor = visualElement.getColor().cpy();
                    visElementColor.g = newValues[0];
                    visElementColor.clamp();
                    visualElement.setColor(visElementColor);
                    return;

                case TweenTypes.COLOR_B:
                    visElementColor = visualElement.getColor().cpy();
                    visElementColor.b = newValues[0];
                    visElementColor.clamp();
                    visualElement.setColor(visElementColor);
                    return;

                case TweenTypes.COLOR_A:
                    visElementColor = visualElement.getColor().cpy();
                    visElementColor.a = newValues[0];
                    visElementColor.clamp();
                    visualElement.setColor(visElementColor);
                    return;

                case TweenTypes.MASK_RGBA:
                    visElementColor = new Color(newValues[0], newValues[1], newValues[2], newValues[3]);
                    visElementColor.clamp();
                    visualElement.setMaskColor(visElementColor);
                    return;

                case TweenTypes.MASK_R:
                    visElementColor = visualElement.getMaskColor().cpy();
                    visElementColor.r = newValues[0];
                    visElementColor.clamp();
                    visualElement.setMaskColor(visElementColor);
                    return;

                case TweenTypes.MASK_G:
                    visElementColor = visualElement.getMaskColor().cpy();
                    visElementColor.g = newValues[0];
                    visElementColor.clamp();
                    visualElement.setMaskColor(visElementColor);
                    return;

                case TweenTypes.MASK_B:
                    visElementColor = visualElement.getMaskColor().cpy();
                    visElementColor.b = newValues[0];
                    visElementColor.clamp();
                    visualElement.setMaskColor(visElementColor);
                    return;

                case TweenTypes.MASK_A:
                    visElementColor = visualElement.getMaskColor().cpy();
                    visElementColor.a = newValues[0];
                    visElementColor.clamp();
                    visualElement.setMaskColor(visElementColor);
                    return;
            }
        }
        
        super.setValues(target, tweenType, newValues);
    }
}
