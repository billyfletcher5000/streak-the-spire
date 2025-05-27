package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class UITextElement extends UIVisualElement {
    private static Matrix4 identityMatrix = new Matrix4();
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();
    private String text;
    private int hAlign = Align.center;
    private boolean wrap = true;
    private boolean autoScale = false;
    private float autoScalePaddingPixels = 0.0f;
    private float autoScalePaddingRelative = 0.0f;

    public BitmapFont getFont() { return font; }
    public void setFont(BitmapFont font) { this.font = font; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getHAlign() { return hAlign; }
    public void setHAlign(int hAlign) { this.hAlign = hAlign; }
    public boolean shouldWordWrap() { return wrap; }
    public void setWordWrap(boolean wrap) { this.wrap = wrap; }
    public boolean shouldAutoScale() { return autoScale; }
    public void setAutoScale(boolean autoScale) { this.autoScale = autoScale; }
    public float getAutoScalePaddingPixels() { return autoScalePaddingPixels; }
    public void setAutoScalePaddingPixels(float autoScalePaddingPixels) { this.autoScalePaddingPixels = autoScalePaddingPixels; }
    public float getAutoScalePaddingRelative() { return autoScalePaddingRelative; }
    public void setAutoScalePaddingRelative(float autoScalePaddingRelative) { this.autoScalePaddingRelative = autoScalePaddingRelative; }

    public UITextElement() {}

    public UITextElement(Vector2 position, BitmapFont font, String text) {
        this(position, VectorOne.cpy(), font, text, VectorOne.cpy(), Color.WHITE.cpy(), Align.center, true);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size) {
        this(position, VectorOne.cpy(), font, text, size, Color.WHITE.cpy(), Align.center, true);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, int hAlign) {
        this(position, VectorOne.cpy(), font, text, size, Color.WHITE.cpy(), hAlign, true);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, boolean wrap) {
        this(position, VectorOne.cpy(), font, text, size, Color.WHITE.cpy(), Align.center, wrap);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, int hAlign, boolean wrap) {
        this(position, VectorOne.cpy(), font, text, size, Color.WHITE.cpy(), hAlign, wrap);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, Color color) {
        this(position, VectorOne.cpy(), font, text, size, color, Align.center, true);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, Color color, int hAlign) {
        this(position, VectorOne.cpy(), font, text, size, color, hAlign, true);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, Color color, boolean wrap) {
        this(position, VectorOne.cpy(), font, text, size, color, Align.center, wrap);
    }
    public UITextElement(Vector2 position, BitmapFont font, String text, Vector2 size, Color color, int hAlign, boolean wrap) {
        this(position, VectorOne.cpy(), font, text, size, color, hAlign, wrap);
    }

    public UITextElement(Vector2 position, Vector2 scale, BitmapFont font, String text, Vector2 size, Color color, int hAlign, boolean wrap) {
        setLocalPosition(position);
        setLocalScale(scale);
        this.font = font;
        this.text = text;
        setDimensions(size);
        setColor(color);
        this.hAlign = hAlign;
        this.wrap = wrap;

        setDebugColor(Color.YELLOW);
    }

    @Override
    protected void elementPreRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPreRender(transformationStack, spriteBatch, transformedAlpha);
        Matrix4 matrix = new Matrix4();
        matrix.set(transformationStack);
        spriteBatch.end();
        spriteBatch.setTransformMatrix(matrix);
        spriteBatch.begin();
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);

        float oldScale = font.getData().scaleX;
        float appliedScale = oldScale;

        Vector2 dimensions = getDimensions();
        layout.setText(font, text, getTransformedColor(transformedAlpha), dimensions.x, hAlign, wrap);

        if(autoScale) {
            float paddedWidth = dimensions.x - autoScalePaddingPixels;
            float paddedHeight = dimensions.y - autoScalePaddingPixels;

            float xScale = (paddedWidth / layout.width) / (1.0f + autoScalePaddingRelative);
            float yScale = (paddedHeight / layout.height) / (1.0f + autoScalePaddingRelative);

            appliedScale = Math.min(xScale, yScale);
            font.getData().setScale(appliedScale);
        }

        float halfXDimension = (dimensions.x * -0.5f);
        float widthApplied = (layout.width * appliedScale);
        float widthDiff = widthApplied - layout.width;
        float fontDrawX = halfXDimension - (widthDiff * 0.5f);
        float fontDrawY = (layout.height * appliedScale) / 2.0F;

        font.draw(spriteBatch, layout,  fontDrawX, fontDrawY);
        font.getData().setScale(oldScale);
    }

    @Override
    protected void elementPostRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPostRender(transformationMatrix, spriteBatch, transformedAlpha);
        spriteBatch.end();
        spriteBatch.setTransformMatrix(identityMatrix);
        spriteBatch.begin();
    }
}
