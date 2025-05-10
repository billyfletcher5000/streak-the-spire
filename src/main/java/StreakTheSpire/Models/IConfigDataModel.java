package StreakTheSpire.Models;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

public interface IConfigDataModel
{
    void loadFromConfig(SpireConfig config);
    void saveToConfig(SpireConfig config);
}
