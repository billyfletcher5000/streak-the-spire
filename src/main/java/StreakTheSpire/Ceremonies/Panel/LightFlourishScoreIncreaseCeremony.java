package StreakTheSpire.Ceremonies.Panel;

import StreakTheSpire.Ceremonies.IScoreChangeCeremony;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UITextElement;
import StreakTheSpire.Views.PlayerStreakView;
import com.badlogic.gdx.math.Vector2;
import dorkbox.tweenEngine.*;

public class LightFlourishScoreIncreaseCeremony extends IScoreChangeCeremony {

    public float scaleMultiplier = 1.5f;
    public float duration = 1.0f;

    private Timeline sequence = null;
    private UITextElement scoreText = null;
    private int newScore = 0;

    @Override
    public String getCeremonyDisplayText() {
        return "Light Score Increase Flourish";
    }

    @Override
    public void start(int newScore, PlayerStreakView streakView) {
        this.newScore = newScore;
        scoreText = streakView.getScoreDisplayElement();

        TweenEngine tweenEngine = StreakTheSpire.get().getTweenEngine();

        Vector2 startingScale = scoreText.getLocalScale();
        Vector2 largerScale = startingScale.cpy().scl(scaleMultiplier);

        TweenCallback setNewScoreCallback = new TweenCallback(TweenCallback.Events.START) {
            public void onEvent(int t, BaseTween<?> source) {
                scoreText.setText(String.valueOf(newScore));
            }
        };

        TweenCallback completeCallback = new TweenCallback(TweenCallback.Events.END) {
            public void onEvent(int t, BaseTween<?> source) {
                completeCeremony();
            }
        };

        sequence = tweenEngine.createSequential();
        sequence.push(tweenEngine.to(scoreText, UIElement.TweenTypes.SCALE_XY, duration * 0.5f).target(largerScale.x, largerScale.y).ease(TweenEquations.Bounce_Out));
        sequence.push(tweenEngine.call(setNewScoreCallback));
        sequence.push(tweenEngine.to(scoreText, UIElement.TweenTypes.SCALE_XY, duration * 0.5f).target(startingScale.x, startingScale.y).ease(TweenEquations.Bounce_In));
        sequence.addCallback(completeCallback);
        sequence.start();
    }

    @Override
    public void forceEnd() {
        super.forceEnd();

        scoreText.setText(String.valueOf(newScore));
        sequence.cancel();
        completeCeremony();
    }

    @Override
    public void close() {
        super.close();
        sequence = null;
    }
}
