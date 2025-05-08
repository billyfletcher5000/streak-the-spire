package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

public class UINineSliceElement extends UIElement {
    private NineSliceTexture nineSliceTexture;
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

    public NineSliceTexture getNineSliceTexture() { return nineSliceTexture; }
    public void setNineSliceTexture(NineSliceTexture nineSliceTexture) {
        if(this.nineSliceTexture != nineSliceTexture)
        {
            this.nineSliceTexture = nineSliceTexture;
            if(nineSliceTexture != null)
                setDimensions(getDimensions()); // Ensures our dimensions are never below minimums
        }
    }

    @Override
    public void setDimensions(Vector2 dimensions) {
        if(nineSliceTexture != null) {
            int minWidth = nineSliceTexture.leftMargin + nineSliceTexture.rightMargin;
            int minHeight = nineSliceTexture.topMargin + nineSliceTexture.bottomMargin;
            if (dimensions.x < minWidth || dimensions.y < minHeight) {
                dimensions = dimensions.cpy();
                dimensions.x = Math.min(dimensions.x, minWidth);
                dimensions.y = Math.min(dimensions.y, minHeight);
            }
        }

        super.setDimensions(dimensions);
    }

    @Override
    protected void elementRender(Matrix3 transformationMatrix, SpriteBatch spriteBatch) {
        super.elementRender(transformationMatrix, spriteBatch);

        // So this is the CPU way of doing things, which will preserve batching because if we did it via changing shader
        // it would mean another draw call/invalidating the batch. In the bizarre event you're in a situation where you
        // need hyper performant 9-slice images AND you're ordering rendering to allow it, this could be more efficient
        // as a shader process.
        int vertexCount = VertexWindingID.NUM * VertexComponent.NUM * 9;
        float[] vertexComponents = new float[vertexCount];

        float textureWidth = nineSliceTexture.texture.getWidth();
        float textureHeight = nineSliceTexture.texture.getHeight();

        Vector2 dimensions = getDimensions();
        Vector2 extents = dimensions.cpy().scl(0.5f);
        Vector2 outerTopLeft = extents.cpy().scl(-1f, -1f);
        Vector2 outerBottomRight = extents.cpy();

        float middleMarginX = dimensions.x - nineSliceTexture.leftMargin - nineSliceTexture.rightMargin;
        float middleMarginY = dimensions.y - nineSliceTexture.topMargin - nineSliceTexture.bottomMargin;

        float normalisedLeftSize = (float)nineSliceTexture.leftMargin / textureWidth;
        float normalisedRightSize = (float)nineSliceTexture.rightMargin / textureWidth;
        float normalisedTopSize = (float)nineSliceTexture.topMargin / textureHeight;
        float normalisedBottomSize = (float)nineSliceTexture.bottomMargin / textureHeight;

        float normalisedMiddleSizeX = (textureWidth - nineSliceTexture.leftMargin - nineSliceTexture.rightMargin) / textureWidth;
        float normalisedMiddleSizeY = (textureHeight - nineSliceTexture.topMargin - nineSliceTexture.bottomMargin) / textureHeight;

        StreakTheSpire.logger.info("dimensions: " + dimensions + ", extents: " + extents + ", outerTopLeft: " + outerTopLeft + ", outerBottomRight: " + outerBottomRight);
        StreakTheSpire.logger.info("middleMarginX: " + middleMarginX + ", middleMarginY: " + middleMarginY);
        StreakTheSpire.logger.info("normalisedLeftSize: " + normalisedLeftSize + ", normalisedRightSize: " + normalisedRightSize + ", normalisedTopSize: " + normalisedTopSize + ", normalisedBottomSize: " + normalisedBottomSize);
        StreakTheSpire.logger.info("normalisedMiddleSizeX: " + normalisedMiddleSizeX + ", normalisedMiddleSizeY: " + normalisedMiddleSizeY);

        // TODO: Add tiling support via repeat drawing section quads

        // top left
        Vector2 sectionTopLeft = outerTopLeft.cpy();
        Vector2 sectionBottomRight = new Vector2(outerTopLeft.x + nineSliceTexture.leftMargin, outerTopLeft.y + nineSliceTexture.topMargin);
        Vector2 uv1 = new Vector2(0, 0);
        Vector2 uv2 = new Vector2(normalisedLeftSize, normalisedTopSize);
        StreakTheSpire.logger.info("top left");
        drawSection(vertexComponents, 0, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // top middle
        sectionTopLeft.x += nineSliceTexture.leftMargin;
        sectionBottomRight.x += middleMarginX;
        uv1.x += normalisedLeftSize;
        uv2.x += normalisedMiddleSizeX;
        StreakTheSpire.logger.info("top middle");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // top right
        sectionTopLeft.x += middleMarginX;
        sectionBottomRight.x = textureWidth;
        uv1.x += normalisedMiddleSizeX;
        uv2.x = 1;
        StreakTheSpire.logger.info("top right");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 2, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // middle left
        sectionTopLeft.set(outerTopLeft.x, outerTopLeft.y + nineSliceTexture.topMargin);
        sectionBottomRight.set(outerTopLeft.x + nineSliceTexture.leftMargin, outerTopLeft.y + nineSliceTexture.topMargin + middleMarginY);
        uv1.set(normalisedLeftSize + normalisedMiddleSizeX, normalisedTopSize);
        uv2.set(normalisedLeftSize + normalisedMiddleSizeX, normalisedTopSize + normalisedMiddleSizeY);
        StreakTheSpire.logger.info("middle left");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 3, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // middle middle
        sectionTopLeft.x += nineSliceTexture.leftMargin;
        sectionBottomRight.x += middleMarginX;
        uv1.x += normalisedLeftSize;
        uv2.x += normalisedMiddleSizeX;
        StreakTheSpire.logger.info("middle middle");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 4, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // middle right
        sectionTopLeft.x += middleMarginX;
        sectionBottomRight.x = textureWidth;
        uv1.x += normalisedMiddleSizeX;
        uv2.x = 1;
        StreakTheSpire.logger.info("middle right");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 5, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // bottom left
        sectionTopLeft.set(outerTopLeft.x, outerTopLeft.y + nineSliceTexture.topMargin + middleMarginY);
        sectionBottomRight.set(outerTopLeft.x + nineSliceTexture.leftMargin, outerTopLeft.y + textureHeight);
        uv1.set(0, normalisedTopSize + normalisedMiddleSizeY);
        uv2.set(normalisedLeftSize, 1);
        StreakTheSpire.logger.info("bottom left");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 6, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // bottom middle
        sectionTopLeft.x += nineSliceTexture.leftMargin;
        sectionBottomRight.x += middleMarginX;
        uv1.x += normalisedLeftSize;
        uv2.x += normalisedMiddleSizeX;
        StreakTheSpire.logger.info("bottom middle");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 7, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        // bottom right
        sectionTopLeft.x += middleMarginX;
        sectionBottomRight.x = textureWidth;
        uv1.x += normalisedMiddleSizeX;
        uv2.x = 1;
        StreakTheSpire.logger.info("bottom right");
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 8, transformationMatrix, sectionTopLeft, sectionBottomRight, uv1, uv2);

        StreakTheSpire.logger.info("VertexComponents: " + Arrays.toString(vertexComponents));
        // Finally, draw everything!
        spriteBatch.draw(nineSliceTexture.texture, vertexComponents, 0, vertexCount);
    }

    protected void drawSection(float[] vertices, int offset, Matrix3 transformMatrix, Vector2 topLeft, Vector2 bottomRight, Vector2 uv1, Vector2 uv2) {
        Vector2 topLeftTransformed = topLeft.cpy().mul(transformMatrix);
        Vector2 bottomRightTransformed = bottomRight.cpy().mul(transformMatrix);

        float colorBits = color.toFloatBits();


        StreakTheSpire.logger.info("offset: " + offset + ", topLeft: " + topLeft + ", bottomRight: " + bottomRight + ", uv1: " + uv1 + ", uv2: " + uv2);
        StreakTheSpire.logger.info("topLeftTransformed: " + topLeftTransformed + ", bottomRightTransformed: " + bottomRightTransformed);

        int vertexIndex = offset + (VertexWindingID.TL * VertexComponent.NUM);
        StreakTheSpire.logger.info("vertexIndex: " + vertexIndex);
        vertices[vertexIndex + VertexComponent.X] = topLeftTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = topLeftTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv1.x;
        vertices[vertexIndex + VertexComponent.V] = uv2.y;
        //vertices[vertexIndex + VertexComponent.V] = 1.0f - uv2.y;

        vertexIndex = offset + (VertexWindingID.TR * VertexComponent.NUM);
        StreakTheSpire.logger.info("vertexIndex: " + vertexIndex);
        vertices[vertexIndex + VertexComponent.X] = bottomRightTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = topLeftTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv2.x;
        vertices[vertexIndex + VertexComponent.V] = uv2.y;
        //vertices[vertexIndex + VertexComponent.V] = 1.0f - uv2.y;

        vertexIndex = offset + (VertexWindingID.BR * VertexComponent.NUM);
        StreakTheSpire.logger.info("vertexIndex: " + vertexIndex);
        vertices[vertexIndex + VertexComponent.X] = bottomRightTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomRightTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv2.x;
        vertices[vertexIndex + VertexComponent.V] = uv2.y;
        //vertices[vertexIndex + VertexComponent.V] = 1.0f - uv2.y;

        vertexIndex = offset + (VertexWindingID.BL * VertexComponent.NUM);
        StreakTheSpire.logger.info("vertexIndex: " + vertexIndex);
        vertices[vertexIndex + VertexComponent.X] = topLeftTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomRightTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv1.x;
        vertices[vertexIndex + VertexComponent.V] = uv2.y;
        //vertices[vertexIndex + VertexComponent.V] = 1.0f - uv2.y;
    }
}
