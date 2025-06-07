package StreakTheSpire.Utils;

import basemod.IUIElement;
import basemod.ModPanel;
import basemod.helpers.UIElementModificationHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class FixedModLabeledButton implements IUIElement {
    private Consumer<FixedModLabeledButton> click;
    private Hitbox hb;
    private float x;
    private float y;
    private float w;
    private float middle_width;
    private float h;
    public BitmapFont font;
    public String label;
    public ModPanel parent;
    public Color color;
    public Color colorHover;
    private static final float TEXT_OFFSET = 9.0F;
    private Texture textureLeft;
    private Texture textureRight;
    private Texture textureMiddle;

    public FixedModLabeledButton(String label, float xPos, float yPos, ModPanel p, Consumer<FixedModLabeledButton> c) {
        this(label, xPos, yPos, Color.WHITE, Color.GREEN, FontHelper.buttonLabelFont, p, c);
    }

    public FixedModLabeledButton(String label, float xPos, float yPos, Color textColor, Color textColorHover, ModPanel p, Consumer<FixedModLabeledButton> c) {
        this(label, xPos, yPos, textColor, textColorHover, FontHelper.buttonLabelFont, p, c);
    }

    public FixedModLabeledButton(String label, float xPos, float yPos, Color textColor, Color textColorHover, BitmapFont font, ModPanel p, Consumer<FixedModLabeledButton> c) {
        this.label = label;
        this.font = font;
        this.color = textColor;
        this.colorHover = textColorHover;
        this.textureLeft = ImageMaster.loadImage("img/ButtonLeft.png");
        this.textureRight = ImageMaster.loadImage("img/ButtonRight.png");
        this.textureMiddle = ImageMaster.loadImage("img/ButtonMiddle.png");
        this.x = xPos * Settings.xScale;
        this.y = yPos * Settings.yScale;
        this.middle_width = Math.max(0.0F, FontHelper.getSmartWidth(font, label, 9999.0F, 0.0F) - 18.0F * Settings.xScale);
        this.w = (float)(this.textureLeft.getWidth() + this.textureRight.getWidth()) * Settings.xScale + this.middle_width;
        this.h = (float)this.textureLeft.getHeight() * Settings.yScale;
        this.hb = new Hitbox(this.x + 1.0F * Settings.xScale, this.y + 1.0F * Settings.yScale, this.w - 2.0F * Settings.xScale, this.h - 2.0F * Settings.yScale);
        this.parent = p;
        this.click = c;
    }

    public void render(SpriteBatch sb) {
        sb.draw(this.textureLeft, this.x, this.y, (float)this.textureLeft.getWidth() * Settings.xScale, this.h);
        sb.draw(this.textureMiddle, this.x + (float)this.textureLeft.getWidth() * Settings.xScale, this.y, this.middle_width, this.h);
        sb.draw(this.textureRight, this.x + (float)this.textureLeft.getWidth() * Settings.xScale + this.middle_width, this.y, (float)this.textureRight.getWidth() * Settings.xScale, this.h);
        this.hb.render(sb);
        sb.setColor(Color.WHITE);
        if (this.hb.hovered) {
            FontHelper.renderFontCentered(sb, this.font, this.label, this.hb.cX, this.hb.cY, this.colorHover);
        } else {
            FontHelper.renderFontCentered(sb, this.font, this.label, this.hb.cX, this.hb.cY, this.color);
        }

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

    public void set(float xPos, float yPos) {
        this.x = xPos * Settings.scale;
        this.y = yPos * Settings.scale;
        UIElementModificationHelper.moveHitboxByOriginalParameters(this.hb, this.x + 1.0F * Settings.xScale, this.y + 1.0F * Settings.yScale);
    }

    public void setX(float xPos) {
        this.set(xPos, this.y / Settings.scale);
    }

    public void setY(float yPos) {
        this.set(this.x / Settings.scale, yPos);
    }

    public float getX() {
        return this.x / Settings.scale;
    }

    public float getY() {
        return this.y / Settings.scale;
    }
}
