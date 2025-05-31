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

    public PlayerStreakModel getStreakModel(String playerClass) {
        return model.playerToStreak.stream().filter(model -> model.identifier.get().equals(playerClass)).findAny().orElse(null);
    }

    public void calculateStreakData(StreakCriteriaModel criteria, boolean recalculateAll) {
        // This is heavily based on RunHistoryScreen.refreshData to preserve some of the odd legacy bugfixes around files and whatnot

        if(recalculateAll) {
            for (PlayerStreakModel streakModel : model.playerToStreak) {
                streakModel.reset();
            }

            if(criteria.trackRotating.get()) {
                if(model.rotatingPlayerStreakModel.get() != null) {
                    model.rotatingPlayerStreakModel.get().reset();
                }
                else {
                    createRotatingModel();
                }
            }
            else {
                model.rotatingPlayerStreakModel.set(null);
            }
        }
        else if (model.rotatingPlayerStreakModel.get() == null && criteria.trackRotating.get()) {
            createRotatingModel();
        }

        FileHandle[] subfolders = Arrays.stream(Gdx.files.local("runs" + File.separator).list()).filter(fileHandle ->
            criteria.trackedCharacterClasses.contains(fileHandle.name())
        ).toArray(FileHandle[]::new);

        ArrayList<RunDataSubset> allCharacterSubsets = new ArrayList<>();

        for (FileHandle subFolder : subfolders) {
            StreakTheSpire.logDebug("Evaluating Subfolder: " + subFolder.path());

            String playerClass = subFolder.name();

            if(playerClass == null)
                continue;

            for(PlayerStreakModel model : model.playerToStreak) {
                StreakTheSpire.logDebug("Streak model in Player Streak Store: " + model.identifier);
            }

            PlayerStreakModel streakModel = getStreakModel(playerClass);
            if(streakModel == null) {
                StreakTheSpire.logDebug("Streak model NOT found: " + playerClass);
                streakModel = new PlayerStreakModel();
                streakModel.identifier.set(playerClass);
                model.playerToStreak.add(streakModel);
            }
            else {
                StreakTheSpire.logDebug("Streak model found: " + playerClass);
            }

            ArrayList<RunDataSubset> runDataToProcess = new ArrayList<>();

            for (FileHandle file : subFolder.list()) {
                try {
                    StreakTheSpire.logDebug("Evaluating file: " + file.path());

                    String filename = file.nameWithoutExtension();
                    if(streakModel.processedFilenames.contains(filename)) {
                        StreakTheSpire.logDebug("Skipping already processed filename: " + filename);
                        continue;
                    }

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
                                    StreakTheSpire.logWarning("Run file " + file.path() + " name is could not be parsed into a Timestamp.");
                                    data = null;
                                }
                            }
                        }
                    }

                    runDataToProcess.add(data);
                } catch (JsonSyntaxException var19) {
                    StreakTheSpire.logError("Failed to load RunDataSubset from JSON file: " + file.path());
                }
            }

            // Same, somewhat bizarre way of doing a string compare on what is saved as a long integer as RunData does,
            // but reversed, as we want the order to be from the first run rather than from the most recent.
            // I'm presuming done this way because of some shortcoming in deserialising long ints in java appropriately?
            runDataToProcess.sort((runA, runB) -> runA.timestamp.compareTo(runB.timestamp));

            for (RunDataSubset data : runDataToProcess) {
                if(!data.character_chosen.equals(playerClass)) {
                    StreakTheSpire.logError("{}: character_chosen \"{}\" differs from player class: {}", data.filename, data.character_chosen, playerClass);
                    continue;
                }

                if(processRunData(criteria, data, streakModel, playerClass) && criteria.trackRotating.get())
                    allCharacterSubsets.add(data);
            }
        }

        if(criteria.trackRotating.get()) {
            // Now process rotating streaks
            allCharacterSubsets.sort((runA, runB) -> runA.timestamp.compareTo(runB.timestamp));

            for(RunDataSubset data : allCharacterSubsets) {
                processRunData(criteria, data, model.rotatingPlayerStreakModel.get(), PlayerStreakStoreModel.RotatingPlayerIdentifier);
            }
        }
    }

    private void createRotatingModel() {
        PlayerStreakModel rotatingModel = new PlayerStreakModel();
        rotatingModel.identifier.set(PlayerStreakStoreModel.RotatingPlayerIdentifier);
        model.rotatingPlayerStreakModel.set(rotatingModel);
    }

    // Returns whether or not the run qualified for victory testing, not whether it was a pass or not, to aid in filtering
    private static boolean processRunData(StreakCriteriaModel criteria, RunDataSubset data, PlayerStreakModel streakModel, String identifier) {
        String currentStreakTimestamp = streakModel.highestStreakTimestamp.get();
        if(currentStreakTimestamp != null && data.timestamp.compareTo(currentStreakTimestamp) < 0) {
            StreakTheSpire.logError("{} {}: Highest streak timestamp \"{}\" appears to be from after data.timestamp: {}", data.filename, identifier, currentStreakTimestamp, data.timestamp);
            return false;
        }

        int streakCount = streakModel.currentStreak.get();

        boolean disqualified = false;
        for (Map.Entry<DisqualifyingCondition, String> entry : disqualifyingConditions.entrySet()) {
            DisqualifyingCondition condition = entry.getKey();
            String reason = entry.getValue();

            StreakTheSpire.logDebug("{} {}: Testing disqualifying condition: {}", data.filename, identifier, reason);
            if(condition.test(data, criteria)) {
                disqualified = true;
                StreakTheSpire.logDebug("{} {}: Disqualified due to: {}", data.filename, identifier, reason);
                break;
            }
        }

        if(!disqualified) {
            boolean failed = false;
            for (Map.Entry<LosingCondition, String> entry : losingConditions.entrySet()) {
                LosingCondition condition = entry.getKey();
                String reason = entry.getValue();

                StreakTheSpire.logDebug("{} {}: Testing losing condition: {}", data.filename, identifier, reason);
                if (condition.test(data, criteria)) {
                    failed = true;
                    StreakTheSpire.logDebug("{} {}: Lost due to: {}", data.filename, identifier, reason);
                    break;
                }
            }

            if (failed) {
                streakCount = 0;
                streakModel.totalValidLosses.set(streakModel.totalValidLosses.get() + 1);
            }
            else {
                streakCount++;
                streakModel.totalValidWins.set(streakModel.totalValidWins.get() + 1);
            }

            streakModel.currentStreak.set(streakCount);
            streakModel.currentStreakTimestamp.set(data.timestamp);

            if (streakModel.highestStreak.get() < streakCount) {
                streakModel.highestStreak.set(streakCount);
                streakModel.highestStreakTimestamp.set(data.timestamp);
            }
        }

        streakModel.processedFilenames.add(data.filename);
        return !disqualified;
    }

    public String createStreakDebugReport() {
        StringBuilder report = new StringBuilder();

        report.append("Streak Report:\n\n");

        ArrayList<PlayerStreakModel> playerStreakModels = new ArrayList<>(model.playerToStreak);
        if(model.rotatingPlayerStreakModel.get() != null)
            playerStreakModels.add(model.rotatingPlayerStreakModel.get());

        for(PlayerStreakModel playerStreakModel : playerStreakModels)
        {
            report.append("Character: " + playerStreakModel.identifier.get() + "\n");
            report.append("\tHighest Streak: " + playerStreakModel.highestStreak.get() + "\n");
            report.append("\tCurrent Streak: " + playerStreakModel.currentStreak.get() + "\n");
            report.append("\tHighest Streak Timestamp: " + playerStreakModel.highestStreakTimestamp.get() + "\n");
            report.append("\tCurrent Streak Timestamp: " + playerStreakModel.currentStreakTimestamp.get() + "\n");
            report.append("\tTotal Wins: " + playerStreakModel.totalValidWins.get() + "\n");
            report.append("\tTotal Losses: " + playerStreakModel.totalValidLosses.get() + "\n");
            report.append("\tWin Rate: " + (float)playerStreakModel.totalValidWins.get() / (float)playerStreakModel.totalValidLosses.get() + "\n");
            report.append("\tProcessed Filenames: " + String.join(", ", playerStreakModel.processedFilenames) + "\n");
        }

        return report.toString();
    }

    private static LinkedHashMap<DisqualifyingCondition, String> createDisqualifyingConditions() {
        LinkedHashMap<DisqualifyingCondition, String> disqualifyingConditions = new LinkedHashMap<>();

        disqualifyingConditions.put(((data, criteria) -> (!data.is_ascension_mode && criteria.requiredAscensionLevel.get() > 0)), "not_ascension");
        disqualifyingConditions.put(((data, criteria) -> (data.ascension_level < criteria.requiredAscensionLevel.get())), "ascension_level_too_low");
        disqualifyingConditions.put(((data, criteria) -> (data.chose_seed && criteria.allowCustomSeeds.get() == false)), "chose_seed");
        disqualifyingConditions.put(((data, criteria) -> (data.is_daily && criteria.allowDailies.get() == false)), "is_daily");
        disqualifyingConditions.put(((data, criteria) -> (data.is_trial && criteria.allowDemo.get() == false)), "is_demo");
        disqualifyingConditions.put(((data, criteria) -> (data.is_prod && criteria.allowBeta.get() == false)), "is_beta");
        disqualifyingConditions.put(((data, criteria) -> (data.is_endless && criteria.allowEndless.get() == false)), "is_endless");

        return disqualifyingConditions;
    }

    private static LinkedHashMap<LosingCondition, String> createLosingConditions() {
        LinkedHashMap<LosingCondition, String> losingConditions = new LinkedHashMap<>();

        losingConditions.put(((data, criteria) -> !data.victory), "victory_failed");
        losingConditions.put(((data, criteria) ->
                (data.floor_reached < StreakCriteriaModel.HeartKillFloorReached && criteria.requireHeartKill.get() == true)), "did_not_kill_heart");

        return losingConditions;
    }
}
