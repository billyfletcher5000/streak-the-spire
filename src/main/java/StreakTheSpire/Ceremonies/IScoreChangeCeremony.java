package StreakTheSpire.Ceremonies;

import StreakTheSpire.Views.PlayerStreakView;

public abstract class IScoreChangeCeremony extends ICeremony {
    public abstract String getCeremonyDisplayText();
    public abstract void start(int newScore, PlayerStreakView streakView);
}
