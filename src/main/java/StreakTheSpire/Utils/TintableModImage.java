package StreakTheSpire.Utils;

import basemod.IUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class TintableModImage implements IUIElement {
    public Texture texture;
    public float x;
    public float y;
    public float w;
    public float h;
    public Color color;

    public TintableModImage(float x, float y, String texturePath) {
        this.texture = ImageMaster.loadImage(texturePath);
        this.x = x * Settings.scale;
        this.y = y * Settings.scale;
        this.w = (float)this.texture.getWidth() * Settings.scale;
        this.h = (float)this.texture.getHeight() * Settings.scale;
        this.color = Color.WHITE.cpy();
    }

    public TintableModImage(float x, float y, String texturePath, Color color) {
        this.texture = ImageMaster.loadImage(texturePath);
        this.x = x * Settings.scale;
        this.y = y * Settings.scale;
        this.w = (float)this.texture.getWidth() * Settings.scale;
        this.h = (float)this.texture.getHeight() * Settings.scale;
        this.color = color;
    }

    public TintableModImage(float x, float y, Texture tex) {
        this.texture = tex;
        this.x = x * Settings.scale;
        this.y = y * Settings.scale;
        this.w = (float)this.texture.getWidth() * Settings.scale;
        this.h = (float)this.texture.getHeight() * Settings.scale;
        this.color = Color.WHITE.cpy();
    }

    public TintableModImage(float x, float y, Texture tex, Color color) {
        this.texture = tex;
        this.x = x * Settings.scale;
        this.y = y * Settings.scale;
        this.w = (float)this.texture.getWidth() * Settings.scale;
        this.h = (float)this.texture.getHeight() * Settings.scale;
        this.color = color;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(this.texture, this.x, this.y, this.w, this.h);
    }

    public void update() {
    }

    public int renderLayer() {
        return 0;
    }

    public int updateOrder() {
        return 1;
    }
}