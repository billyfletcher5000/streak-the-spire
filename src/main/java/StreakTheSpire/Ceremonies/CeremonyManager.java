package StreakTheSpire.Ceremonies;

import java.util.HashSet;

public class CeremonyManager {
    private static CeremonyManager instance;
    public static CeremonyManager get() {
        if (instance == null) {
            instance = new CeremonyManager();
        }

        return instance;
    }

    private HashSet<Class<? extends IScoreChangeCeremony>> scoreChangeCeremonyClasses = new HashSet<>();

    public void registerScoreChangeCeremony(Class<? extends IScoreChangeCeremony> scoreChangeCeremonyClass) {
        scoreChangeCeremonyClasses.add(scoreChangeCeremonyClass);
    }

    public void unregisterScoreChangeCeremony(Class<? extends IScoreChangeCeremony> scoreChangeCeremonyClass) {
        scoreChangeCeremonyClasses.remove(scoreChangeCeremonyClass);
    }

    public Class<? extends IScoreChangeCeremony> getScoreChangeCeremonyClass(String className) {
        for (Class<? extends IScoreChangeCeremony> clazz : scoreChangeCeremonyClasses) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }

        return null;
    }
}
