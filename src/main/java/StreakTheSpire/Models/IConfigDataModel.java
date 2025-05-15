package StreakTheSpire.Models;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

public interface IConfigDataModel
{
    String getConfigID();
    void setConfigID(String ID);
    default void afterLoadFromConfig(SpireConfig config) {}
    default void beforeSaveToConfig(SpireConfig config) {}
}
