package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

public class UIImageElement extends UIElement {
    private static class VertexComponent {
        public static final int X = 0;
        public static final int Y = 1;
        public static final int COLOR = 2;
        public static final int U = 3;
        public static final int V = 4;
        public static final int NUM = 5;
    }

    private static class VertexID {
        public static final int TL = 0;
        public static final int TR = 1;
        public static final int BR = 2;
        public static final int BL = 3;
        public static final int NUM = 4;
    }

    protected Color color = Color.WHITE;
    protected Texture texture;
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public Texture getTexture() { return texture; }
    public void setTexture(Texture texture) { this.texture = texture; }

    public UIImageElement(Vector2 position, Texture texture) {
        this(position, VectorOne, texture);
    }

    public UIImageElement(Vector2 position, Texture texture, Vector2 size) {
        this(position, VectorOne, texture, size, Color.WHITE);
    }
    
    public UIImageElement(Vector2 position, Texture texture, Color color) {
        this(position, VectorOne, texture, color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, Texture texture) {
        this(position, scale, texture, Color.WHITE);
    }

    public UIImageElement(Vector2 position, Vector2 scale, Texture texture, Color color) {
        this(position, scale, texture, new Vector2(texture.getWidth(), texture.getHeight()), color);
    }

    public UIImageElement(Vector2 position, Vector2 scale, Texture texture, Vector2 size, Color color) {
        this.localPosition = position;
        this.localScale = scale;
        this.texture = texture;
        this.dimensions = size;
        this.color = color;
    }


    @Override
    protected void elementRender(Matrix3 transformationMatrix, SpriteBatch spriteBatch) {
        super.elementRender(transformationMatrix, spriteBatch);

        float[] vertices = new float[VertexID.NUM * VertexComponent.NUM];

        Vector2 extents = dimensions.cpy().scl(0.5f);
        Vector2 topLeft = extents.cpy().scl(-1f, 1f).mul(transformationMatrix);
        Vector2 topRight = extents.cpy().scl(1f, 1f).mul(transformationMatrix);
        Vector2 bottomRight = extents.cpy().scl(1f, -1f).mul(transformationMatrix);
        Vector2 bottomLeft = extents.cpy().scl(-1f, -1f).mul(transformationMatrix);
        
        float colorBits = color.toFloatBits();

        int vertexIndex = VertexID.TL * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = topLeft.x;
        vertices[vertexIndex + VertexComponent.Y] = topLeft.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = 0f;
        vertices[vertexIndex + VertexComponent.V] = 0f;

        vertexIndex = VertexID.TR * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = topRight.x;
        vertices[vertexIndex + VertexComponent.Y] = topRight.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = 1f;
        vertices[vertexIndex + VertexComponent.V] = 0f;

        vertexIndex = VertexID.BR * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = bottomRight.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomRight.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = 1f;
        vertices[vertexIndex + VertexComponent.V] = 1f;

        vertexIndex = VertexID.BL * VertexComponent.NUM;
        vertices[vertexIndex + VertexComponent.X] = bottomLeft.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomLeft.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = 0f;
        vertices[vertexIndex + VertexComponent.V] = 1f;
        
        spriteBatch.draw(texture, vertices, 0, VertexID.NUM * VertexComponent.NUM);
    }
}
