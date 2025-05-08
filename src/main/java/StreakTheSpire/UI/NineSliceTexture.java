package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Texture;

public class NineSliceTexture {
    public Texture texture;
    public float leftMargin;
    public float rightMargin;
    public float topMargin;
    public float bottomMargin;

    public NineSliceTexture(Texture texture, float leftMargin, float rightMargin, float topMargin, float bottomMargin) {
        this.texture = texture;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }
}
