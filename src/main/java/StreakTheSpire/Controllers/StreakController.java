package StreakTheSpire.Controllers;

import StreakTheSpire.Data.RunDataSubset;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.Models.StreakModel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonSyntaxException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.stats.RunData;

import java.io.File;
import java.util.ArrayList;

import static basemod.BaseMod.gson;

public class StreakController {
    private StreakModel model;

    public StreakController(StreakModel model) {
        this.model = model;
    }

    void CalculateStreakData(StreakCriteriaModel criteria, boolean recalculateAll) {
        if(recalculateAll) {
            model.clearData();
        }

        FileHandle[] subfolders = Gdx.files.local("runs" + File.separator).list();
        ArrayList<RunDataSubset> runDataToProcess = new ArrayList<>();

        for (FileHandle subFolder : subfolders) {
            String folderName = subFolder.name();

            switch (CardCrawlGame.saveSlot) {
                case 0:
                    if (folderName.contains("0_") || folderName.contains("1_") || folderName.contains("2_")) {
                        continue;
                    }
                    break;
                default:
                    if (!folderName.contains(CardCrawlGame.saveSlot + "_")) {
                        continue;
                    }
            }

            for(String characterClassName : criteria.trackedCharacterClasses)



            for (FileHandle file : subFolder.list()) {
                try {
                    RunData data = gson.fromJson(file.readString(), RunData.class);
                    if (data != null && data.timestamp == null) {
                        data.timestamp = file.nameWithoutExtension();
                        String exampleDaysSinceUnixStr = "17586";
                        boolean assumeDaysSinceUnix = data.timestamp.length() == exampleDaysSinceUnixStr.length();
                        if (assumeDaysSinceUnix) {
                            try {
                                long secondsInDay = 86400L;
                                long days = Long.parseLong(data.timestamp);
                                data.timestamp = Long.toString(days * 86400L);
                            } catch (NumberFormatException var18) {
                                logger.info("Run file " + file.path() + " name is could not be parsed into a Timestamp.");
                                data = null;
                            }
                        }
                    }

                    if (data != null) {
                        try {
                            AbstractPlayer.PlayerClass.valueOf(data.character_chosen);
                            this.unfilteredRuns.add(data);
                        } catch (NullPointerException | IllegalArgumentException var17) {
                            logger.info("Run file " + file.path() + " does not use a real character: " + data.character_chosen);
                        }
                    }
                } catch (JsonSyntaxException var19) {
                    logger.info("Failed to load RunData from JSON file: " + file.path());
                }
            }
        }
    }
}
