package StreakTheSpire.Ceremonies.Panel;

import StreakTheSpire.Ceremonies.IScoreChangeCeremony;
import StreakTheSpire.Views.PlayerStreakView;

public class SimpleTextScoreChangeCeremony extends IScoreChangeCeremony {
    @Override
    public String getCeremonyDisplayText() {
        return "Simple Score Text Change";
    }

    @Override
    public void start(int newScore, PlayerStreakView streakView) {
        streakView.getScoreDisplayElement().setText(String.valueOf(newScore));
        completeCeremony();
    }
}
