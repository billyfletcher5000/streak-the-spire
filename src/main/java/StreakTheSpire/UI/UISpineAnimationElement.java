package StreakTheSpire.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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

    private boolean flipX = false;
    private boolean flipY = false;

    protected TextureAtlas atlas;
    protected Skeleton skeleton;
    protected AnimationState animationState;
    protected AnimationStateData animationStateData;

    public AnimationState getAnimationState() { return animationState; }

    public UISpineAnimationElement() {}

    public UISpineAnimationElement(String atlasUrl, String skeletonUrl) {
        this(Vector2.Zero, atlasUrl, skeletonUrl);
    }

    public UISpineAnimationElement(Vector2 position, String atlasUrl, String skeletonUrl) {
        setLocalPosition(position);
        loadSkeleton(atlasUrl, skeletonUrl, null);
    }

    public UISpineAnimationElement(Vector2 position, String atlasUrl, String skeletonUrl, SkeletonModifier modifier) {
        setLocalPosition(position);
        loadSkeleton(atlasUrl, skeletonUrl, modifier);
    }

    public void loadSkeleton(String atlasUrl, String skeletonUrl, SkeletonModifier modifier) {
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
            this.skeleton.setPosition(0,0);
            this.animationStateData = new AnimationStateData(skeletonData);
            this.animationState = new AnimationState(this.animationStateData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void elementPreRender(Affine2 transformationStack, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementPreRender(transformationStack, spriteBatch, transformedAlpha);
        Matrix4 matrix = new Matrix4();
        matrix.set(transformationStack);
        spriteBatch.end();
        previousTransformMatrix = CardCrawlGame.psb.getTransformMatrix();
        CardCrawlGame.psb.setTransformMatrix(matrix);
        CardCrawlGame.psb.begin();
    }

    @Override
    protected void elementRender(Affine2 transformationMatrix, SpriteBatch spriteBatch, float transformedAlpha) {
        super.elementRender(transformationMatrix, spriteBatch, transformedAlpha);
        skeleton.updateWorldTransform();
        skeleton.setColor(color);
        skeleton.setFlip(flipX, flipY);

        try {
            skeletonMeshRenderer.draw(CardCrawlGame.psb, skeleton);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
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
    protected void elementUpdate(float deltaTime) {
        super.elementUpdate(deltaTime);
        animationState.update(deltaTime);
        animationState.apply(skeleton);
    }
}
