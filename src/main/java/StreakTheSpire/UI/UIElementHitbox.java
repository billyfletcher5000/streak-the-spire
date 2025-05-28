package StreakTheSpire.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;

// This class is meant to act as a bridge between the transform hierarchy based UIElements and the
// Hitbox class used in StS and more specifically for Slay the Relics
public class UIElementHitbox extends Hitbox {

    private Vector2 localPosition = new Vector2();
    private Vector2 localSize = new Vector2();
    private HitboxListener hitboxListener = null;

    public Vector2 getLocalPosition() { return localPosition.cpy(); }
    public Vector2 getLocalSize() { return localSize.cpy(); }
    public HitboxListener getHitboxListener() { return hitboxListener; }

    public void setLocalPosition(Vector2 localPosition) { this.localPosition.set(localPosition); }
    public void setLocalSize(Vector2 localSize) { this.localSize.set(localSize); }
    public void setHitboxListener(HitboxListener hitboxListener) { this.hitboxListener = hitboxListener; }

    public UIElementHitbox(float width, float height) {
        super(0f, 0f);
        localSize.set(width, height);
    }

    public UIElementHitbox(float x, float y, float width, float height) {
        super(-1000.0f, -1000.0f, 0f, 0f);
        localPosition.set(x, y);
        localSize.set(width, height);
    }

    public UIElementHitbox(float x, float y, float width, float height, HitboxListener listener) {
        super(-1000.0f, -1000.0f, 0f, 0f);
        localPosition.set(x, y);
        localSize.set(width, height);
        hitboxListener = listener;
    }

    public void update(Affine2 transformationMatrix) {
        Vector2 transformedPosition = localPosition.cpy();
        transformationMatrix.applyTo(transformedPosition);

        // This assumes that there's no rotation, if there were rotation the whole hitbox would need to be an
        // oriented bounding box and use separate intersection tests, which would be fine but would prevent
        // any interoperability with Slay the Relics. Ideally Slay the Relics would support other ways of
        // integration (maybe it does?).
        this.resize(localSize.x * transformationMatrix.m00, localSize.y * transformationMatrix.m11);
        this.move(transformedPosition.x, transformedPosition.y);

        if(hitboxListener != null)
            encapsulatedUpdate(hitboxListener);
        else
            update();
    }

    public void clearState() {
        this.clickStarted = false;
        this.clicked = false;
        this.justHovered = false;
        this.hovered = false;
    }
}
