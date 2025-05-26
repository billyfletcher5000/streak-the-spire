package StreakTheSpire.Models;

import StreakTheSpire.Ceremonies.Panel.LightFlourishScoreDecreaseCeremony;
import StreakTheSpire.Ceremonies.Panel.LightFlourishScoreIncreaseCeremony;
import StreakTheSpire.Utils.Properties.Property;

public class CeremonyPreferencesModel implements IConfigDataModel, IModel {
    public Property<String> scoreIncreaseCeremony = new Property<>(LightFlourishScoreIncreaseCeremony.class.getName());
    public Property<String> scoreDecreaseCeremony = new Property<>(LightFlourishScoreDecreaseCeremony.class.getName());

    //region IConfigModel
    private String configID = "CeremonyPreferencesModel";

    @Override
    public String getConfigID() {
        return configID;
    }

    @Override
    public void setConfigID(String ID) {
        configID = ID;
    }
    //endregion
}
