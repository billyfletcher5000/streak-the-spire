package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Texture;

public class NineSliceTexture {
    public Texture texture;
    public int leftMargin;
    public int rightMargin;
    public int topMargin;
    public int bottomMargin;

    public NineSliceTexture(Texture texture, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
        this.texture = texture;
        this.leftMargin = Math.max(leftMargin, 0);
        this.rightMargin = Math.max(leftMargin, Math.min(rightMargin, texture.getWidth()));
        this.topMargin = Math.max(topMargin, 0);
        this.bottomMargin = Math.max(topMargin, Math.min(bottomMargin, texture.getHeight()));
    }
}
