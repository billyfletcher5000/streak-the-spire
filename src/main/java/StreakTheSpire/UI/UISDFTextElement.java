package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class UISDFTextElement extends UITextElement {
    private float fontScale = 1.0f;
    private float outlineDistance = 0.1f; // Between 0 and 0.5, 0 = thick outline, 0.5 = no outline
    private Color outlineColor = new Color(0.175f,0.175f,0.175f,1f);// new Color(0.24705882f, 0.24705882f, 0.24705882f, 1.0f);
    private Vector2 shadowOffset = new Vector2(4f, 7f);
    private float shadowSmoothing = 0.5f; // Between 0 and 0.5

    public float getFontScale() { return fontScale; }
    public void setFontScale(float fontScale) { this.fontScale = fontScale; }
    public float getOutlineDistance() { return outlineDistance; }
    public void setOutlineDistance(float outlineDistance) { this.outlineDistance = outlineDistance; }
    public Color getOutlineColor() { return outlineColor; }
    public void setOutlineColor(Color outlineColor) { this.outlineColor = outlineColor; }
    public Vector2 getShadowOffset() { return shadowOffset; }
    public void setShadowOffset(Vector2 shadowOffset) { this.shadowOffset = shadowOffset; }
    public float getShadowSmoothing() { return shadowSmoothing; }
    public void setShadowSmoothing(float shadowSmoothing) { this.shadowSmoothing = shadowSmoothing; }

    public UISDFTextElement() {}

    public UISDFTextElement(Vector2 position, BitmapFont font, String text) {
        super(position, VectorOne.cpy(), font, text, VectorOne.cpy(), Color.WHITE.cpy(), Align.center, true);
    }

    @Override
    protected void elementPreRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPreRender(transformationStack, spriteBatch, transformedAlpha);

        Texture fontTexture = getFont().getRegion().getTexture();
        float convertedShadowOffsetX = getShadowOffset().x / fontTexture.getWidth();
        float convertedShadowOffsetY = getShadowOffset().y / fontTexture.getWidth();

        Color maskColor = getMaskColor();

        ShaderProgram fontShader = UIShaderRepository.getSDFOutlineShadowFontShader();
        spriteBatch.setShader(fontShader);
        fontShader.setUniformf("u_scale", fontScale);
        fontShader.setUniformf("u_outlineDistance", outlineDistance);
        fontShader.setUniformf("u_outlineColor", outlineColor.r, outlineColor.g, outlineColor.b, outlineColor.a);
        fontShader.setUniformf("u_shadowOffset", convertedShadowOffsetX, convertedShadowOffsetY);
        fontShader.setUniformf("u_shadowSmoothing", shadowSmoothing);
        fontShader.setUniformf("u_mask_color", maskColor.r, maskColor.g, maskColor.b, maskColor.a);
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);
    }

    @Override
    protected void elementPostRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        spriteBatch.setShader(null);
        super.elementPostRender(transformationMatrix, spriteBatch, transformedAlpha);
    }

    @Override
    protected void applyMaskColorPreRender(Batch spriteBatch) {
    }

    @Override
    protected void revertMaskColorPostRender(Batch spriteBatch) {
    }
}
