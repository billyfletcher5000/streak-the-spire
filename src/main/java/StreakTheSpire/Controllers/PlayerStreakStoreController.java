package StreakTheSpire.Controllers;

import StreakTheSpire.Data.RunDataSubset;
import StreakTheSpire.Models.PlayerStreakModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.StreakTheSpire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonSyntaxException;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreController {
    private PlayerStreakStoreModel model;

    public PlayerStreakStoreController(PlayerStreakStoreModel model) {
        this.model = model;
    }

    public Optional<PlayerStreakModel> getStreakModel(String playerClass) {
        return model.playerToStreak.stream().filter(model -> model.playerClass.equals(playerClass)).findFirst();
    }

    void CalculateStreakData(StreakCriteriaModel criteria, boolean recalculateAll) {
        // This is heavily based on RunHistoryScreen.refreshData to preserve some of the odd legacy bugfixes around files and whatnot

        if(recalculateAll) {
            model.playerToStreak.clear();
        }

        FileHandle[] subfolders = Gdx.files.local("runs" + File.separator).list();

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

            String playerClass = null;
            for(String testPlayerClass : criteria.trackedCharacterClasses) {
                if(folderName.contains(testPlayerClass))
                    playerClass = testPlayerClass;
            }

            if(playerClass == null)
                continue;

            PlayerStreakModel streakModel = null;
            Optional<PlayerStreakModel> existingStreakModel = getStreakModel(playerClass);
            if(existingStreakModel.isPresent()) {
                streakModel = existingStreakModel.get();
            }
            else {
                streakModel = new PlayerStreakModel();
                streakModel.playerClass.setValue(playerClass);
                model.playerToStreak.add(streakModel);
            }

            ArrayList<RunDataSubset> runDataToProcess = new ArrayList<>();

            for (FileHandle file : subFolder.list()) {
                try {
                    String filename = file.nameWithoutExtension();
                    if(streakModel.processedFilenames.contains(filename))
                        continue;

                    RunDataSubset data = gson.fromJson(file.readString(), RunDataSubset.class);
                    if (data != null) {
                        data.filename = filename;

                        if (data.timestamp == null) {
                            data.timestamp = file.nameWithoutExtension();
                            String exampleDaysSinceUnixStr = "17586";
                            boolean assumeDaysSinceUnix = data.timestamp.length() == exampleDaysSinceUnixStr.length();
                            if (assumeDaysSinceUnix) {
                                try {
                                    long secondsInDay = 86400L;
                                    long days = Long.parseLong(data.timestamp);
                                    data.timestamp = Long.toString(days * 86400L);
                                } catch (NumberFormatException var18) {
                                    StreakTheSpire.logger.info("Run file " + file.path() + " name is could not be parsed into a Timestamp.");
                                    data = null;
                                }
                            }
                        }
                    }

                    runDataToProcess.add(data);
                } catch (JsonSyntaxException var19) {
                    StreakTheSpire.logger.info("Failed to load RunDataSubset from JSON file: " + file.path());
                }
            }

            // Same, somewhat bizarre way of doing a string compare on what is saved as a long integer as RunData does,
            // I'm presuming because of some shortcoming in deserialising long ints in java appropriately?
            runDataToProcess.sort((runA, runB) -> runB.timestamp.compareTo(runA.timestamp));

            for (RunDataSubset data : runDataToProcess) {
                if(!data.character_chosen.equals(playerClass)) {
                    StreakTheSpire.logger.error("{}: character_chosen \"{}\" differs from player class: {}", data.filename, data.character_chosen, playerClass);
                    continue;
                }

                String currentStreakTimestamp = streakModel.highestStreakTimestamp.getValue();
                if(currentStreakTimestamp != null && data.timestamp.compareTo(currentStreakTimestamp) < 0) {
                    StreakTheSpire.logger.error("{} {}: Highest streak timestamp \"{}\" appears to be from after data.timestamp: {}", data.filename, playerClass, currentStreakTimestamp, data.timestamp);
                    continue;
                }

                int streakCount = streakModel.currentStreak.getValue();

                if(!data.victory ||
                   (!data.is_ascension_mode && criteria.requiredAscensionLevel.getValue() > 0) ||
                   (data.ascension_level < criteria.requiredAscensionLevel.getValue()) ||
                   (data.chose_seed && criteria.allowCustomSeeds.getValue() == false) ||
                   (data.is_daily && criteria.allowDailies.getValue() == false) ||
                   (data.is_trial && criteria.allowDemo.getValue() == false) ||
                   (data.is_prod && criteria.allowBeta.getValue() == false) ||
                   (data.is_endless && criteria.allowEndless.getValue() == false) ||
                   (data.floor_reached < StreakCriteriaModel.HeartKillFloorReached && criteria.requireHeartKill.getValue() == true)) {
                    streakCount = 0;
                }
                else {
                    streakCount++;
                }

                streakModel.currentStreak.setValue(streakCount);
                streakModel.currentStreakTimestamp.setValue(data.timestamp);

                if(streakModel.highestStreak.getValue() < streakCount) {
                    streakModel.highestStreak.setValue(streakCount);
                    streakModel.highestStreakTimestamp.setValue(data.timestamp);
                }

                streakModel.processedFilenames.add(data.filename);
            }
        }
    }
}
