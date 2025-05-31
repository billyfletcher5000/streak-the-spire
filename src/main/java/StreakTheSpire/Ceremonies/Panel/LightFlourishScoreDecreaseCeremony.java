package StreakTheSpire.Ceremonies.Panel;

import StreakTheSpire.Ceremonies.IScoreChangeCeremony;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UITextElement;
import StreakTheSpire.UI.UIVisualElement;
import StreakTheSpire.Views.CharacterSkeletonDisplayView;
import StreakTheSpire.Views.IView;
import StreakTheSpire.Views.PlayerStreakView;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import dorkbox.tweenEngine.*;

import java.util.ArrayList;

public class LightFlourishScoreDecreaseCeremony extends IScoreChangeCeremony {

    public float scaleMultiplier = 1.5f;
    public float duration = 1.0f;
    public int colorPulseCount = 4;
    public float colorPulseAlpha = 0.75f;

    // So we require to use multiple timelines because you can't put sequential timelines in a parallel timeline,
    // or at least I couldn't get it to work, this tween library is pretty bad for lack of documentation and I would
    // not be surprised if this very common use case was never tested as it's not present in their examples.
    private final ArrayList<Timeline> sequences = new ArrayList<>();
    private UITextElement scoreText = null;
    private Vector2 textStartingScale = null;
    private UIVisualElement visualElement = null;
    private int newScore = 0;

    Color prevVisualElementMaskColor = Color.BLACK;
    Color prevTextMaskColor = Color.BLACK;

    @Override
    public String getCeremonyDisplayText() {
        return "Light Score Decrease Flourish";
    }

    @Override
    public void start(int newScore, PlayerStreakView streakView) {
        this.newScore = newScore;
        scoreText = streakView.getScoreDisplayElement();

        TweenEngine tweenEngine = StreakTheSpire.get().getTweenEngine();

        textStartingScale = scoreText.getLocalScale();
        Vector2 largerScale = textStartingScale.cpy().scl(scaleMultiplier);

        StreakTheSpire.logInfo("LightScoreDecrease: start");

        TweenCallback setNewScoreCallback = new TweenCallback(TweenCallback.Events.START) {
            public void onEvent(int t, BaseTween<?> source) {
                scoreText.setText(String.valueOf(newScore));
                StreakTheSpire.logInfo("LightScoreDecrease: setText");
            }
        };

        TweenCallback completeCallback = new TweenCallback(TweenCallback.Events.END) {
            public void onEvent(int t, BaseTween<?> source) {
                checkCompletion();
            }
        };

        IView characterDisplayView = streakView.getCharacterDisplayView();
        if(characterDisplayView instanceof CharacterSkeletonDisplayView) {
            CharacterSkeletonDisplayView skeletonView = (CharacterSkeletonDisplayView) characterDisplayView;

            float hitAnimDuration = skeletonView.getHitAnimationDuration();
            int numHits = (int)Math.ceil(duration / hitAnimDuration);
            skeletonView.immediatelyPlayHitAnimation();

            for(int i = 1; i < numHits; i++) {
                skeletonView.enqueueHitAnimation();
            }

            skeletonView.enqueueIdleAnimation(true);
            visualElement = skeletonView.getSkeletonAnimation();
        }

        if(visualElement == null && characterDisplayView instanceof UIVisualElement) {
            visualElement = (UIVisualElement) characterDisplayView;
        }

        if(visualElement != null) {
            prevVisualElementMaskColor = visualElement.getMaskColor();
            visualElement.setMaskColor(new Color(1.0f, 0f, 0f, 0f));
            Timeline sequence = tweenEngine.createSequential();

            float pulseHalfDuration = duration / (colorPulseCount * 2);

            for(int i = 0; i < colorPulseCount; i++) {
                sequence.push(tweenEngine.to(visualElement, UIVisualElement.TweenTypes.MASK_A, pulseHalfDuration).target(colorPulseAlpha).ease(TweenEquations.Linear));
                sequence.push(tweenEngine.to(visualElement, UIVisualElement.TweenTypes.MASK_A, pulseHalfDuration).target(0.0f).ease(TweenEquations.Linear));
            }

            sequence.addCallback(new TweenCallback(TweenCallback.Events.END) {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    StreakTheSpire.logInfo("LightScoreDecrease: revert maskColor");
                    visualElement.setMaskColor(prevVisualElementMaskColor);
                    checkCompletion();
                }
            });

            sequence.start();
            sequences.add(sequence);
        }

        prevTextMaskColor = scoreText.getMaskColor();
        scoreText.setMaskColor(new Color(1.0f, 0f, 0f, 0f));
        Timeline textColorSequence = tweenEngine.createSequential();
        float pulseHalfDuration = duration / (colorPulseCount * 2);

        for(int i = 0; i < colorPulseCount; i++) {
            textColorSequence.push(tweenEngine.to(scoreText, UIVisualElement.TweenTypes.MASK_A, pulseHalfDuration).target(colorPulseAlpha).ease(TweenEquations.Linear));
            textColorSequence.push(tweenEngine.to(scoreText, UIVisualElement.TweenTypes.MASK_A, pulseHalfDuration).target(0.0f).ease(TweenEquations.Linear));
        }

        textColorSequence.addCallback(new TweenCallback(TweenCallback.Events.END) {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                scoreText.setMaskColor(prevTextMaskColor);
                checkCompletion();
            }
        });
        textColorSequence.start();

        sequences.add(textColorSequence);

        Timeline textScaleSequence = tweenEngine.createSequential()
            .push(tweenEngine.to(scoreText, UIElement.TweenTypes.SCALE_XY, duration * 0.5f).target(largerScale.x, largerScale.y).ease(TweenEquations.Bounce_Out))
            .push(tweenEngine.call( new TweenCallback(TweenCallback.Events.START) {
                public void onEvent(int t, BaseTween<?> source) {
                    scoreText.setText(String.valueOf(newScore));
                    StreakTheSpire.logInfo("LightScoreDecrease: setText");
                }
            }))
            .push(tweenEngine.to(scoreText, UIElement.TweenTypes.SCALE_XY, duration * 0.5f).target(textStartingScale.x, textStartingScale.y).ease(TweenEquations.Bounce_In))
            .addCallback(new TweenCallback(TweenCallback.Events.END) {
                public void onEvent(int t, BaseTween<?> source) {
                    checkCompletion();
                }
            })
        .start();

        sequences.add(textScaleSequence);
    }

    private void checkCompletion() {
        for(Timeline sequence : sequences) {
            if(!sequence.isFinished())
                return;
        }

        StreakTheSpire.logInfo("LightScoreDecrease: complete");
        completeCeremony();
    }

    @Override
    public void forceEnd() {
        super.forceEnd();

        scoreText.setText(String.valueOf(newScore));
        for(Timeline sequence : sequences) {
            if(!sequence.isFinished())
                sequence.cancel();
        }

        if(visualElement != null)
            visualElement.setMaskColor(prevVisualElementMaskColor);

        if(scoreText != null) {
            scoreText.setMaskColor(prevTextMaskColor);
            if(textStartingScale != null)
                scoreText.setLocalScale(textStartingScale);
        }

        completeCeremony();
    }

    @Override
    public void close() {
        super.close();
        sequences.clear();
    }
}
