package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public class UINineSliceElement extends UIVisualElement {
    private NineSliceTexture nineSliceTexture;

    public UINineSliceElement(Vector2 position, NineSliceTexture texture, Vector2 size) {
        this(position, VectorOne.cpy(), texture, size, Color.WHITE.cpy());
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
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);

        // So this is a (very, very verbose) CPU way of doing things, which will preserve batching because if we did it
        // via changing shader it would mean another draw call/invalidating the batch. In the bizarre event you're in a
        // situation where you need hyper performant 9-slice images AND you're ordering rendering to allow it, this could
        // be more efficient as a shader process.
        int vertexCount = VertexWindingID.NUM * VertexComponent.NUM * 9;
        float[] vertexComponents = new float[vertexCount];

        float textureWidth = nineSliceTexture.texture.getWidth();
        float textureHeight = nineSliceTexture.texture.getHeight();

        Vector2 dimensions = getDimensions();
        Vector2 extents = dimensions.cpy().scl(0.5f);
        Vector2 outerBottomLeft = extents.cpy().scl(-1f, -1f);
        Vector2 outerTopRight = extents.cpy().scl(1f, 1f);

        float middleMarginX = dimensions.x - nineSliceTexture.leftMargin - nineSliceTexture.rightMargin;
        float middleMarginY = dimensions.y - nineSliceTexture.topMargin - nineSliceTexture.bottomMargin;

        float normalisedLeftSize = (float)nineSliceTexture.leftMargin / textureWidth;
        float normalisedTopSize = (float)nineSliceTexture.topMargin / textureHeight;

        float normalisedMiddleSizeX = (textureWidth - nineSliceTexture.leftMargin - nineSliceTexture.rightMargin) / textureWidth;
        float normalisedMiddleSizeY = (textureHeight - nineSliceTexture.topMargin - nineSliceTexture.bottomMargin) / textureHeight;

        float colorBits = getTransformedColor(transformedAlpha).toFloatBits();

        // TODO: Add tiling support via repeat drawing section quads

        // bottom left
        Vector2 sectionBottomLeft = outerBottomLeft.cpy();
        Vector2 sectionTopRight = new Vector2(outerBottomLeft.x + nineSliceTexture.leftMargin, outerBottomLeft.y + nineSliceTexture.topMargin);
        Vector2 uv1 = new Vector2(0, 0);
        Vector2 uv2 = new Vector2(normalisedLeftSize, normalisedTopSize);
        drawSection(vertexComponents, 0, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // bottom middle
        sectionBottomLeft.x += nineSliceTexture.leftMargin;
        sectionTopRight.x += middleMarginX;
        uv1.x += normalisedLeftSize;
        uv2.x += normalisedMiddleSizeX;
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // bottom right
        sectionBottomLeft.x += middleMarginX;
        sectionTopRight.x = outerTopRight.x;
        uv1.x += normalisedMiddleSizeX;
        uv2.x = 1;
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 2, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // middle left
        sectionBottomLeft.set(outerBottomLeft.x, outerBottomLeft.y + nineSliceTexture.topMargin);
        sectionTopRight.set(outerBottomLeft.x + nineSliceTexture.leftMargin, outerBottomLeft.y + nineSliceTexture.topMargin + middleMarginY);
        uv1.set(0, normalisedTopSize);
        uv2.set(normalisedLeftSize, normalisedTopSize + normalisedMiddleSizeY);
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 3, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // middle middle
        sectionBottomLeft.x += nineSliceTexture.leftMargin;
        sectionTopRight.x += middleMarginX;
        uv1.x += normalisedLeftSize;
        uv2.x += normalisedMiddleSizeX;
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 4, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // middle right
        sectionBottomLeft.x += middleMarginX;
        sectionTopRight.x = outerTopRight.x;
        uv1.x += normalisedMiddleSizeX;
        uv2.x = 1;
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 5, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // top left
        sectionBottomLeft.set(outerBottomLeft.x, outerBottomLeft.y + nineSliceTexture.topMargin + middleMarginY);
        sectionTopRight.set(outerBottomLeft.x + nineSliceTexture.leftMargin, outerTopRight.y);
        uv1.set(0, normalisedTopSize + normalisedMiddleSizeY);
        uv2.set(normalisedLeftSize, 1);
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 6, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // top middle
        sectionBottomLeft.x += nineSliceTexture.leftMargin;
        sectionTopRight.x += middleMarginX;
        uv1.x += normalisedLeftSize;
        uv2.x += normalisedMiddleSizeX;
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 7, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // top right
        sectionBottomLeft.x += middleMarginX;
        sectionTopRight.x = outerTopRight.x;
        uv1.x += normalisedMiddleSizeX;
        uv2.x = 1;
        drawSection(vertexComponents, VertexWindingID.NUM * VertexComponent.NUM * 8, transformationMatrix, sectionBottomLeft, sectionTopRight, uv1, uv2, colorBits);

        // Finally, draw everything!
        spriteBatch.draw(nineSliceTexture.texture, vertexComponents, 0, vertexCount);
    }

    protected void drawSection(float[] vertices, int offset, Affine2 transformMatrix, Vector2 topLeft, Vector2 bottomRight, Vector2 uv1, Vector2 uv2, float colorBits) {
        Vector2 topLeftTransformed = topLeft.cpy(); transformMatrix.applyTo(topLeftTransformed);
        Vector2 bottomRightTransformed = bottomRight.cpy(); transformMatrix.applyTo(bottomRightTransformed);


        int vertexIndex = offset;
        vertices[vertexIndex + VertexComponent.X] = topLeftTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = topLeftTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv1.x;
        vertices[vertexIndex + VertexComponent.V] = 1.0f - uv1.y; // Invert y as UV space has Y=0 at the top and Y=1 at the bottom

        vertexIndex = offset + (VertexWindingID.TR * VertexComponent.NUM);
        vertices[vertexIndex + VertexComponent.X] = bottomRightTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = topLeftTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv2.x;
        vertices[vertexIndex + VertexComponent.V] = 1.0f - uv1.y;

        vertexIndex = offset + (VertexWindingID.BR * VertexComponent.NUM);
        vertices[vertexIndex + VertexComponent.X] = bottomRightTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomRightTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv2.x;
        vertices[vertexIndex + VertexComponent.V] = 1.0f - uv2.y;

        vertexIndex = offset + (VertexWindingID.BL * VertexComponent.NUM);
        vertices[vertexIndex + VertexComponent.X] = topLeftTransformed.x;
        vertices[vertexIndex + VertexComponent.Y] = bottomRightTransformed.y;
        vertices[vertexIndex + VertexComponent.COLOR] = colorBits;
        vertices[vertexIndex + VertexComponent.U] = uv1.x;
        vertices[vertexIndex + VertexComponent.V] = 1.0f - uv2.y;
    }
}
