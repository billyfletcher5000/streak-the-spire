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
import java.util.*;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreController {
    private PlayerStreakStoreModel model;

    private interface DisqualifyingCondition { boolean test(RunDataSubset data, StreakCriteriaModel criteria); }
    private static LinkedHashMap<DisqualifyingCondition, String> disqualifyingConditions = createDisqualifyingConditions();

    private interface LosingCondition { boolean test(RunDataSubset data, StreakCriteriaModel criteria); }
    private static LinkedHashMap<LosingCondition, String> losingConditions = createLosingConditions();

    public PlayerStreakStoreController(PlayerStreakStoreModel model) {
        this.model = model;
    }

    public Optional<PlayerStreakModel> getStreakModel(String playerClass) {
        return model.playerToStreak.stream().filter(model -> model.identifier.equals(playerClass)).findFirst();
    }

    public void CalculateStreakData(StreakCriteriaModel criteria, boolean recalculateAll) {
        // This is heavily based on RunHistoryScreen.refreshData to preserve some of the odd legacy bugfixes around files and whatnot

        if(recalculateAll) {
            model.playerToStreak.clear();
            model.rotatingPlayerStreakModel = new PlayerStreakModel();
            model.rotatingPlayerStreakModel.identifier.setValue(PlayerStreakStoreModel.RotatingPlayerIdentifier);
        }
        else if (model.rotatingPlayerStreakModel == null) {
            model.rotatingPlayerStreakModel = new PlayerStreakModel();
            model.rotatingPlayerStreakModel.identifier.setValue(PlayerStreakStoreModel.RotatingPlayerIdentifier);
        }

        FileHandle[] subfolders = Gdx.files.local("runs" + File.separator).list();

        ArrayList<RunDataSubset> allCharacterSubsets = new ArrayList<>();

        for (FileHandle subFolder : subfolders) {
            StreakTheSpire.logger.info("subfolder: " + subFolder.path());

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
                streakModel.identifier.setValue(playerClass);
                model.playerToStreak.add(streakModel);
            }

            ArrayList<RunDataSubset> runDataToProcess = new ArrayList<>();

            for (FileHandle file : subFolder.list()) {
                try {
                    StreakTheSpire.logger.info("file: " + file.path());

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
            // but reversed, as we want the order to be from the first run rather than from the most recent.
            // I'm presuming done this way because of some shortcoming in deserialising long ints in java appropriately?
            runDataToProcess.sort((runA, runB) -> runA.timestamp.compareTo(runB.timestamp));

            for (RunDataSubset data : runDataToProcess) {
                if(!data.character_chosen.equals(playerClass)) {
                    StreakTheSpire.logger.error("{}: character_chosen \"{}\" differs from player class: {}", data.filename, data.character_chosen, playerClass);
                    continue;
                }

                if(processRunData(criteria, data, streakModel, playerClass))
                    allCharacterSubsets.add(data);
            }
        }

        // Now process rotating streaks
        allCharacterSubsets.sort((runA, runB) -> runA.timestamp.compareTo(runB.timestamp));

        for(RunDataSubset data : allCharacterSubsets) {
            processRunData(criteria, data, model.rotatingPlayerStreakModel, PlayerStreakStoreModel.RotatingPlayerIdentifier);
        }
    }

    // Returns whether or not the run qualified for victory testing, not whether it was a pass or not, to aid in filtering
    private static boolean processRunData(StreakCriteriaModel criteria, RunDataSubset data, PlayerStreakModel streakModel, String identifier) {
        String currentStreakTimestamp = streakModel.highestStreakTimestamp.getValue();
        if(currentStreakTimestamp != null && data.timestamp.compareTo(currentStreakTimestamp) < 0) {
            StreakTheSpire.logger.error("{} {}: Highest streak timestamp \"{}\" appears to be from after data.timestamp: {}", data.filename, identifier, currentStreakTimestamp, data.timestamp);
            return false;
        }

        int streakCount = streakModel.currentStreak.getValue();

        // TODO: Add rotating support, somehow

        boolean disqualified = false;
        for (Map.Entry<DisqualifyingCondition, String> entry : disqualifyingConditions.entrySet()) {
            DisqualifyingCondition condition = entry.getKey();
            String reason = entry.getValue();

            StreakTheSpire.logger.info("{} {}: Testing disqualifying condition: {}", data.filename, identifier, reason);
            if(condition.test(data, criteria)) {
                disqualified = true;
                StreakTheSpire.logger.info("{} {}: Disqualified due to: {}", data.filename, identifier, reason);
                break;
            }
        }

        if(!disqualified) {
            boolean failed = false;
            for (Map.Entry<LosingCondition, String> entry : losingConditions.entrySet()) {
                LosingCondition condition = entry.getKey();
                String reason = entry.getValue();

                StreakTheSpire.logger.info("{} {}: Testing losing condition: {}", data.filename, identifier, reason);
                if (condition.test(data, criteria)) {
                    failed = true;
                    StreakTheSpire.logger.info("{} {}: Lost due to: {}", data.filename, identifier, reason);
                    break;
                }
            }

            if (failed)
                streakCount = 0;
            else
                streakCount++;

            streakModel.currentStreak.setValue(streakCount);
            streakModel.currentStreakTimestamp.setValue(data.timestamp);

            if (streakModel.highestStreak.getValue() < streakCount) {
                streakModel.highestStreak.setValue(streakCount);
                streakModel.highestStreakTimestamp.setValue(data.timestamp);
            }
        }

        streakModel.processedFilenames.add(data.filename);
        return !disqualified;
    }

    public String createStreakDebugReport() {
        StringBuilder report = new StringBuilder();

        report.append("Streak Report:\n\n");

        ArrayList<PlayerStreakModel> playerStreakModels = new ArrayList<>(model.playerToStreak);
        playerStreakModels.add(model.rotatingPlayerStreakModel);

        for(PlayerStreakModel playerStreakModel : playerStreakModels)
        {
            report.append("Character: " + playerStreakModel.identifier.getValue() + "\n");
            report.append("\tHighest Streak: " + playerStreakModel.highestStreak.getValue() + "\n");
            report.append("\tCurrent Streak: " + playerStreakModel.currentStreak.getValue() + "\n");
            report.append("\tHighest Streak Timestamp: " + playerStreakModel.highestStreakTimestamp.getValue() + "\n");
            report.append("\tCurrent Streak Timestamp: " + playerStreakModel.currentStreakTimestamp.getValue() + "\n");
            report.append("\tProcessed Filenames: " + String.join(", ", playerStreakModel.processedFilenames) + "\n");
        }

        return report.toString();
    }

    private static LinkedHashMap<DisqualifyingCondition, String> createDisqualifyingConditions() {
        LinkedHashMap<DisqualifyingCondition, String> disqualifyingConditions = new LinkedHashMap<>();

        disqualifyingConditions.put(((data, criteria) -> (!data.is_ascension_mode && criteria.requiredAscensionLevel.getValue() > 0)), "not_ascension");
        disqualifyingConditions.put(((data, criteria) -> (data.ascension_level < criteria.requiredAscensionLevel.getValue())), "ascension_level_too_low");
        disqualifyingConditions.put(((data, criteria) -> (data.chose_seed && criteria.allowCustomSeeds.getValue() == false)), "chose_seed");
        disqualifyingConditions.put(((data, criteria) -> (data.is_daily && criteria.allowDailies.getValue() == false)), "is_daily");
        disqualifyingConditions.put(((data, criteria) -> (data.is_trial && criteria.allowDemo.getValue() == false)), "is_demo");
        disqualifyingConditions.put(((data, criteria) -> (data.is_prod && criteria.allowBeta.getValue() == false)), "is_beta");
        disqualifyingConditions.put(((data, criteria) -> (data.is_endless && criteria.allowEndless.getValue() == false)), "is_endless");

        return disqualifyingConditions;
    }

    private static LinkedHashMap<LosingCondition, String> createLosingConditions() {
        LinkedHashMap<LosingCondition, String> losingConditions = new LinkedHashMap<>();

        losingConditions.put(((data, criteria) -> !data.victory), "victory_failed");
        losingConditions.put(((data, criteria) ->
                (data.floor_reached < StreakCriteriaModel.HeartKillFloorReached && criteria.requireHeartKill.getValue() == true)), "did_not_kill_heart");

        return losingConditions;
    }
}
