package StreakTheSpire.UI;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.SkeletonModifier;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.io.File;
import java.io.IOException;

public class UISpineAnimationElement extends UIVisualElement {

    private static Matrix4 previousTransformMatrix = new Matrix4();
    protected SkeletonMeshRenderer skeletonMeshRenderer = new SkeletonMeshRenderer();

    private final Property<Boolean> flipX = new Property<>(false);
    private final Property<Boolean> flipY = new Property<>(false);

    protected TextureAtlas atlas;
    protected Skeleton skeleton;
    protected AnimationState animationState;
    protected AnimationStateData animationStateData;

    public boolean getFlipX() { return flipX.get(); }
    public Property<Boolean> getFlipXProperty() { return flipX; }
    public void setFlipX(boolean flipX) { this.flipX.set(flipX); }

    public boolean getFlipY() { return flipY.get(); }
    public Property<Boolean> getFlipYProperty() { return flipY; }
    public void setFlipY(boolean flipY) { this.flipY.set(flipY); }

    public Skeleton getSkeleton() { return skeleton; }
    public AnimationState getAnimationState() { return animationState; }
    public AnimationStateData getAnimationStateData() { return animationStateData; }

    public UISpineAnimationElement() {}

    public UISpineAnimationElement(String atlasUrl, String skeletonUrl) {
        this(Vector2.Zero, atlasUrl, skeletonUrl);
    }

    public UISpineAnimationElement(String atlasUrl, String skeletonUrl, SkeletonModifier modifier) {
        this(Vector2.Zero, atlasUrl, skeletonUrl, modifier);
    }

    public UISpineAnimationElement(Vector2 position, String atlasUrl, String skeletonUrl) {
        //setLocalPosition(position);
        loadSkeleton(atlasUrl, skeletonUrl, null, position);
    }

    public UISpineAnimationElement(Vector2 position, String atlasUrl, String skeletonUrl, SkeletonModifier modifier) {
        //setLocalPosition(position);
        loadSkeleton(atlasUrl, skeletonUrl, modifier, position);
    }

    public void loadSkeleton(String atlasUrl, String skeletonUrl, SkeletonModifier modifier, Vector2 positionOffset) {
        if(skeletonMeshRenderer == null) {
            skeletonMeshRenderer = new SkeletonMeshRenderer();
            skeletonMeshRenderer.setPremultipliedAlpha(true);
        }

        try {
            FileHandle tempFileHandle;
            if(modifier != null) {
                File tempFile = File.createTempFile("temp_skel_" + skeletonUrl, null);
                tempFileHandle = new FileHandle(tempFile);
                modifier.modifySkeletonData(skeletonUrl, tempFileHandle);
            }
            else {
                tempFileHandle = Gdx.files.internal(skeletonUrl);
            }

            this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
            SkeletonJson json = new SkeletonJson(this.atlas);
            SkeletonData skeletonData = json.readSkeletonData(tempFileHandle);

            this.skeleton = new Skeleton(skeletonData);
            this.skeleton.setColor(Color.WHITE);
            this.skeleton.setPosition(positionOffset.x, positionOffset.y);
            this.animationStateData = new AnimationStateData(skeletonData);
            this.animationState = new AnimationState(this.animationStateData);
            setDimensions(new Vector2(skeletonData.getWidth(), skeletonData.getHeight()));
        } catch (IOException e) {
            StreakTheSpire.logError(e.getMessage());
        }
    }

    @Override
    protected void elementPreRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        Matrix4 matrix = new Matrix4();
        matrix.set(transformationStack);
        spriteBatch.end();
        previousTransformMatrix = CardCrawlGame.psb.getTransformMatrix().cpy();
        CardCrawlGame.psb.setTransformMatrix(matrix);
        CardCrawlGame.psb.begin();
        super.elementPreRender(transformationStack, spriteBatch, transformedAlpha);
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);
        skeleton.updateWorldTransform();
        skeleton.setColor(color.get());
        skeleton.setFlip(flipX.get(), flipY.get());

        try {
            skeletonMeshRenderer.draw(CardCrawlGame.psb, skeleton);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            StreakTheSpire.logError(e.getMessage());
        }
    }

    @Override
    protected void elementPostRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPostRender(transformationMatrix, spriteBatch, transformedAlpha);
        CardCrawlGame.psb.end();
        CardCrawlGame.psb.setTransformMatrix(previousTransformMatrix);
        spriteBatch.begin();
    }

    @Override
    protected void applyMaskColorPreRender(Batch spriteBatch) {
        super.applyMaskColorPreRender(CardCrawlGame.psb);
    }

    @Override
    protected void revertMaskColorPostRender(Batch spriteBatch) {
        super.revertMaskColorPostRender(CardCrawlGame.psb);
    }

    @Override
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        animationState.update(deltaTime);
        animationState.apply(skeleton);
    }
}
