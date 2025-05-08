package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class UINineSliceElement extends UIElement {
    protected NineSliceTexture nineSliceTexture;
    protected Color color = Color.WHITE;

    public UINineSliceElement(Vector2 position, NineSliceTexture texture, Vector2 size) {
        this(position, VectorOne.cpy(), texture, size, Color.WHITE);
    }

    public UINineSliceElement(Vector2 position, NineSliceTexture texture, Vector2 size, Color color) {
        this(position, VectorOne.cpy(), texture, size, color);
    }

    public UINineSliceElement(Vector2 position, Vector2 scale, NineSliceTexture texture, Vector2 size, Color color) {
        setLocalPosition(position);
        setLocalScale(scale);
        this.nineSliceTexture = texture;
        setDimensions(size);
        this.color = color;
    }

    @Override
    protected void elementRender(Matrix3 transformationMatrix, SpriteBatch spriteBatch) {
        super.elementRender(transformationMatrix, spriteBatch);

        // So this is the CPU way of doing things, which will preserve batching because if we did it via changing shader
        // it would mean another draw call/invalidating the batch. In the bizarre event you're in a situation where you
        // need hyper performant 9-slice images AND you're ordering rendering to allow it, this could be more efficient
        // as a shader process.
        int vertexCount = VertexWindingID.NUM * VertexComponent.NUM * 9;
        float[] vertices = new float[vertexCount];

        Vector2 extents = getDimensions().cpy().scl(0.5f);
        Vector2 outerTopLeft = extents.cpy().scl(-1f, 1f).mul(transformationMatrix);
        Vector2 outerBottomRight = extents.cpy().scl(1f, -1f).mul(transformationMatrix);

        float width = outerBottomRight.x - outerTopLeft.x;
        float height = outerBottomRight.y - outerTopLeft.y;

        float colorBits = color.toFloatBits();

        // top left
        Vector2 sectionTopLeft = outerTopLeft.cpy();
        Vector2 sectionBottomRight = new Vector2(nineSliceTexture.leftMargin * width, nineSliceTexture.topMargin * height);
        drawSection(spriteBatch, vertices, 0, sectionTopLeft, sectionBottomRight);

        // top middle
        sectionTopLeft.x = sectionBottomRight.x;
        sectionBottomRight.x += (nineSliceTexture.rightMargin - nineSliceTexture.leftMargin) * width;
        drawSection(spriteBatch, vertices, VertexWindingID.NUM, sectionTopLeft, sectionBottomRight);

        // top right
        sectionTopLeft.x = sectionBottomRight.x;
        sectionBottomRight.x += (nineSliceTexture.rightMargin - nineSliceTexture.leftMargin) * width;
        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 2, sectionTopLeft, sectionBottomRight);

        // middle left

        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 3, sectionTopLeft, sectionBottomRight);

        // middle middle

        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 4, sectionTopLeft, sectionBottomRight);

        // middle right

        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 5, sectionTopLeft, sectionBottomRight);

        // bottom left

        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 6, sectionTopLeft, sectionBottomRight);

        // bottom middle

        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 7, sectionTopLeft, sectionBottomRight);

        // bottom right

        drawSection(spriteBatch, vertices, VertexWindingID.NUM * 8, sectionTopLeft, sectionBottomRight);


        spriteBatch.draw(nineSliceTexture.texture, vertices, 0, vertexCount);
    }

    protected void drawSection(SpriteBatch spriteBatch, float[] vertices, int offset, Vector2 topLeft, Vector2 bottomRight, Vector2 uvs) {

    }
}
