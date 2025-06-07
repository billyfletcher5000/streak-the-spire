package StreakTheSpire.Utils;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class MultiImageFixedModButton implements IUIElement {
    private Consumer<MultiImageFixedModButton> click;
    private Hitbox hb;
    private Texture normalTexture;
    private Texture hoverTexture;
    private Texture pressedTexture;
    private float x;
    private float y;
    private float w;
    private float h;
    public ModPanel parent;

    public MultiImageFixedModButton(float xPos, float yPos, Texture normalTex, Texture hoverTex, Texture pressedTex, ModPanel p, Consumer<MultiImageFixedModButton> c) {
        this.normalTexture = normalTex;
        this.hoverTexture = hoverTex;
        this.pressedTexture = pressedTex;
        this.x = xPos * Settings.xScale;
        this.y = yPos * Settings.yScale;
        this.w = (float)this.normalTexture.getWidth();
        this.h = (float)this.hoverTexture.getHeight();
        this.hb = new Hitbox(this.x * Settings.xScale, this.y * Settings.yScale, this.w * Settings.xScale, this.h * Settings.scale);
        this.parent = p;
        this.click = c;
    }

    public void render(SpriteBatch sb) {
        Texture tex = this.hb.clickStarted ? this.pressedTexture : this.hb.hovered ? this.hoverTexture : this.normalTexture;

        sb.setColor(Color.WHITE);
        sb.draw(tex, this.x, this.y, this.w * Settings.xScale, this.h * Settings.yScale);
        this.hb.render(sb);
    }

    public void update() {
        this.hb.update();
        if (this.hb.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }

        if (this.hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
            this.hb.clickStarted = true;
        }

        if (this.hb.clicked) {
            this.hb.clicked = false;
            this.onClick();
        }
    }

    private void onClick() {
        this.click.accept(this);
    }

    public int renderLayer() {
        return 1;
    }

    public int updateOrder() {
        return 1;
    }
}