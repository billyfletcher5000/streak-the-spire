package StreakTheSpire.UI;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public class UIImageElement extends UIVisualElement {
    private final Property<TextureRegion> textureRegion = new Property<TextureRegion>(null);

    public TextureRegion getTextureRegion() { return textureRegion.get(); }
    public Property<TextureRegion> getTextureRegionProperty() { return textureRegion; }
    public void setTextureRegion(TextureRegion textureRegion) { this.textureRegion.set(textureRegion); }

    public UIImageElement() {}

    public UIImageElement(Vector2 position, Texture texture) {
        this(position, VectorOne.cpy(), texture);
    }

    public UIImageElement(Vector2 position, Texture texture, Vector2 size) {
        this(position, VectorOne.cpy(), texture, size, Color.WHITE.cpy());
    }
    
    public UIImageElement(Vector2 position, Texture texture, Color color) {
        this(position, VectorOne.cpy(), texture, color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, Texture texture) {
        this(position, scale, texture, Color.WHITE.cpy());
    }

    public UIImageElement(Vector2 position, Vector2 scale, Texture texture, Color color) {
        this(position, scale, texture, new Vector2(texture.getWidth(), texture.getHeight()), color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, Texture texture, Vector2 size, Color color) {
        this(position, scale, new TextureRegion(texture), size, color);
    }

    public UIImageElement(Vector2 position, TextureRegion textureRegion) {
        this(position, VectorOne.cpy(), textureRegion);
    }

    public UIImageElement(Vector2 position, TextureRegion textureRegion, Vector2 size) {
        this(position, VectorOne.cpy(), textureRegion, size, Color.WHITE.cpy());
    }

    public UIImageElement(Vector2 position, TextureRegion textureRegion, Color color) {
        this(position, VectorOne.cpy(), textureRegion, color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, TextureRegion textureRegion) {
        this(position, scale, textureRegion, Color.WHITE.cpy());
    }

    public UIImageElement(Vector2 position, Vector2 scale, TextureRegion textureRegion, Color color) {
        this(position, scale, textureRegion, textureRegion != null ? new Vector2(textureRegion.getRegionWidth(), textureRegion.getRegionHeight()) : Vector2.Zero, color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, TextureRegion textureRegion, Vector2 size, Color color) {
        initialise(position, scale, textureRegion, size, color);
    }

    public void initialise(Vector2 position, Vector2 scale, TextureRegion textureRegion, Vector2 size, Color color) {
        this.setLocalPosition(position);
        this.setLocalScale(scale);
        this.setTextureRegion(textureRegion);
        this.setDimensions(size);
        this.setColor(color);
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);

        TextureRegion textureRegion = getTextureRegion();

        if(textureRegion == null) {
            return;
        }

        float[] vertices = new float[VertexWindingID.NUM * VertexComponent.NUM];

        Vector2 extents = getDimensions().cpy().scl(0.5f);
        Vector2 topLeft = extents.cpy().scl(-1f, 1f); transformationMatrix.applyTo(topLeft);
        Vector2 topRight = extents.cpy().scl(1f, 1f); transformationMatrix.applyTo(topRight);
        Vector2 bottomRight = extents.cpy().scl(1f, -1f); transformationMatrix.applyTo(bottomRight);
        Vector2 bottomLeft = extents.cpy().scl(-1f, -1f); transformationMatrix.applyTo(bottomLeft);

        float colorBits = getTransformedColor(transformedAlpha).toFloatBits();


        int vertexIndex = VertexWindingID.TL * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = topLeft.x;
        vertices[vertexIndex + VertexComponent.Y] = topLeft.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = textureRegion.getU();
        vertices[vertexIndex + VertexComponent.V] = textureRegion.getV();

        vertexIndex = VertexWindingID.TR * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = topRight.x;
        vertices[vertexIndex + VertexComponent.Y] = topRight.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = textureRegion.getU2();
        vertices[vertexIndex + VertexComponent.V] = textureRegion.getV();

        vertexIndex = VertexWindingID.BR * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = bottomRight.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomRight.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = textureRegion.getU2();
        vertices[vertexIndex + VertexComponent.V] = textureRegion.getV2();

        vertexIndex = VertexWindingID.BL * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = bottomLeft.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomLeft.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = textureRegion.getU();
        vertices[vertexIndex + VertexComponent.V] = textureRegion.getV2();
        
        spriteBatch.draw(textureRegion.getTexture(), vertices, 0, VertexWindingID.NUM * VertexComponent.NUM);
    }
}
