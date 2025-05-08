package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class UIImageElement extends UIVisualElement {
    protected TextureRegion textureRegion;

    public Texture getTexture() { return textureRegion.getTexture(); }
    public void setTexture(Texture texture) { textureRegion.setTexture(texture); }

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
        this(position, scale, textureRegion, new Vector2(textureRegion.getRegionWidth(), textureRegion.getRegionHeight()), color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, TextureRegion textureRegion, Vector2 size, Color color) {
        this.setLocalPosition(position);
        this.setLocalScale(scale);
        this.textureRegion = textureRegion;
        this.setDimensions(size);
        this.color = color;
    }

    @Override
    protected void elementRender(Matrix3 transformationMatrix, SpriteBatch spriteBatch) {
        super.elementRender(transformationMatrix, spriteBatch);

        float[] vertices = new float[VertexWindingID.NUM * VertexComponent.NUM];

        Vector2 extents = getDimensions().cpy().scl(0.5f);
        Vector2 topLeft = extents.cpy().scl(-1f, 1f).mul(transformationMatrix);
        Vector2 topRight = extents.cpy().scl(1f, 1f).mul(transformationMatrix);
        Vector2 bottomRight = extents.cpy().scl(1f, -1f).mul(transformationMatrix);
        Vector2 bottomLeft = extents.cpy().scl(-1f, -1f).mul(transformationMatrix);
        
        float colorBits = color.toFloatBits();

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
