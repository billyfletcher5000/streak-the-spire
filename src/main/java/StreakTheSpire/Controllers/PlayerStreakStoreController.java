package StreakTheSpire.Controllers;

import StreakTheSpire.Data.RotatingConstants;
import StreakTheSpire.Data.RunDataSubset;
import StreakTheSpire.Models.PlayerStreakModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.ExceptionUtil;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.io.File;
import java.util.*;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreController {
    private final PlayerStreakStoreModel model;

    private interface DisqualifyingCondition { boolean test(RunDataSubset data, ArrayList<String> currentStreakCharacterIDs, StreakCriteriaModel criteria); }
    private static final LinkedHashMap<DisqualifyingCondition, String> disqualifyingConditions = createDisqualifyingConditions();

    private interface LosingCondition { boolean test(RunDataSubset data, ArrayList<String> currentStreakCharacterIDs, StreakCriteriaModel criteria); }
    private static final LinkedHashMap<LosingCondition, String> losingConditions = createLosingConditions();

    public PlayerStreakStoreController(PlayerStreakStoreModel model) {
        this.model = model;
    }

    public PlayerStreakModel getStreakModel(String playerClass) {
        return model.playerToStreak.stream().filter(model -> model.identifier.get().equals(playerClass)).findAny().orElse(null);
    }

    public void calculateStreakData(StreakCriteriaModel criteria, boolean recalculateAll) {
        // This is heavily based on RunHistoryScreen.refreshData to preserve some of the odd legacy bugfixes around files and whatnot

        HashMap<PlayerStreakModel, PlayerStreakModel> temporaryStreakDuplicateMap = new HashMap<>();

        if(recalculateAll) {
            for (PlayerStreakModel streakModel : model.playerToStreak) {
                streakModel.reset();
            }

            if(criteria.trackContinuous.get()) {
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
        else if (model.rotatingPlayerStreakModel.get() == null && criteria.trackContinuous.get()) {
            createRotatingModel();
        }

        model.playerToStreak.removeIf(m -> !criteria.trackedCharacterClasses.contains(m.identifier.get()) && !m.identifier.get().equals(RotatingConstants.Identifier));

        try {
            for(String playerClass : criteria.trackedCharacterClasses) {
                PlayerStreakModel streakModel = getStreakModel(playerClass);
                if (streakModel == null) {
                    streakModel = new PlayerStreakModel();
                    streakModel.identifier.set(playerClass);
                    model.playerToStreak.add(streakModel);
                }
            }
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed while creating missing streak models: " + ExceptionUtil.getFullMessage(e));
            return;
        }

        FileHandle[] baseSubFolders;
        FileHandle[] profileFilteredSubFolders;
        FileHandle[] subfolders;

        try {
            baseSubFolders = Gdx.files.local("runs" + File.separator).list();
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed to find runs subfolders: " + ExceptionUtil.getFullMessage(e));
            return;
        }

        try {
            profileFilteredSubFolders = Arrays.stream(baseSubFolders).filter(subFolder -> {
                switch (CardCrawlGame.saveSlot) {
                    case 0:
                        if (subFolder.name().contains("0_") || subFolder.name().contains("1_") || subFolder.name().contains("2_")) {
                            return false;
                        }
                        break;
                    default:
                        if (!subFolder.name().contains(CardCrawlGame.saveSlot + "_")) {
                            return false;
                        }
                }

                return true;
            }).toArray(FileHandle[]::new);
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed to find runs subfolders: " + ExceptionUtil.getFullMessage(e));
            return;
        }

        try {
            subfolders = Arrays.stream(profileFilteredSubFolders).filter(fileHandle ->
                    criteria.trackedCharacterClasses.contains(getProfileIndependentFolderName(fileHandle.name()))
            ).toArray(FileHandle[]::new);
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed to filter subfolders by tracked character names: " + ExceptionUtil.getFullMessage(e));
            return;
        }

        ArrayList<RunDataSubset> allCharacterSubsets = new ArrayList<>();

        for (FileHandle subFolder : subfolders) {
            if(subFolder == null || !subFolder.isDirectory() || !subFolder.exists()) {
                StreakTheSpire.logError("Failed to load subfolder: " + (subFolder != null ? subFolder.path() : "null"));
                continue;
            }

            StreakTheSpire.logDebug("Evaluating Subfolder: " + subFolder.path());

            String playerClass = getProfileIndependentFolderName(subFolder.name());

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
                } catch (Exception e) {
                    StreakTheSpire.logError("Failed to load RunDataSubset from JSON file: " + file.path() + "\nException: " + ExceptionUtil.getFullMessage(e));
                }
            }

            try {
                // Same, somewhat bizarre way of doing a string compare on what is saved as a long integer as RunData does,
                // but reversed, as we want the order to be from the first run rather than from the most recent.
                // I'm presuming done this way because of some shortcoming in deserialising long ints in java appropriately?
                runDataToProcess.sort((runA, runB) -> runA.timestamp.compareTo(runB.timestamp));

                ArrayList<String> currentStreakCharacterIDs = streakModel.currentStreakCharacterIDs;

                if (!temporaryStreakDuplicateMap.containsKey(streakModel)) {
                    temporaryStreakDuplicateMap.put(streakModel, streakModel.cpy());
                }

                PlayerStreakModel streakModelDuplicate = temporaryStreakDuplicateMap.get(streakModel);

                for (RunDataSubset data : runDataToProcess) {
                    if(data == null) {
                        StreakTheSpire.logError("Failed during loop of runDataToProcess due to null runData!");
                        continue;
                    }

                    try {
                        if (!data.character_chosen.equals(playerClass)) {
                            StreakTheSpire.logError("{}: character_chosen \"{}\" differs from player class: {}", data.filename, data.character_chosen, playerClass);
                            continue;
                        }
                    }
                    catch (Exception e) {
                        StreakTheSpire.logError("Failed during check of character chosen before processing run data \"" + data.filename + "\": " + ExceptionUtil.getFullMessage(e));
                        continue;
                    }

                    ProcessResult result = processRunData(criteria, data, currentStreakCharacterIDs, streakModelDuplicate, playerClass);
                    try {
                        if (result != ProcessResult.Disqualified && criteria.trackContinuous.get()) {
                            allCharacterSubsets.add(data);

                            if (result == ProcessResult.StreakIncreased)
                                currentStreakCharacterIDs.add(0, data.character_chosen);
                            else if (result == ProcessResult.StreakReset)
                                currentStreakCharacterIDs.clear();
                        }
                    }
                    catch (Exception e) {
                        StreakTheSpire.logError("Failed after processing run data \"" + data.filename + "\": " + ExceptionUtil.getFullMessage(e));
                    }
                }
            }
            catch (Exception e) {
                StreakTheSpire.logError("Failed prior to processing runDataLoop: " + ExceptionUtil.getFullMessage(e));
            }
        }

        try {
            if (criteria.trackContinuous.get()) {
                boolean addedRotatingCondition = false;
                DisqualifyingCondition uniqueCharacterCondition = PlayerStreakStoreController::isUniqueCharacterInRotation;
                try {
                    if (criteria.enforceRotating.get()) {
                        disqualifyingConditions.put(uniqueCharacterCondition, "was_not_rotating_streak");
                        addedRotatingCondition = true;
                    }
                }
                catch (Exception e) {
                    StreakTheSpire.logError("Failed adding rotating streak disqualifying condition: " + ExceptionUtil.getFullMessage(e));
                }

                try {
                    // Now process rotating streaks
                    allCharacterSubsets.sort((runA, runB) -> runA.timestamp.compareTo(runB.timestamp));
                }
                catch (Exception e) {
                    StreakTheSpire.logError("Failed when sorting rotating streak data subsets: " + ExceptionUtil.getFullMessage(e));
                }


                PlayerStreakModel streakModel = null;
                PropertyList<String> currentStreakCharacterIDs = null;
                PlayerStreakModel streakModelDuplicate = null;
                boolean errorOccurred = false;

                try {
                    streakModel = model.rotatingPlayerStreakModel.get();
                    currentStreakCharacterIDs = new PropertyList<>(streakModel.currentStreakCharacterIDs);

                    if (!temporaryStreakDuplicateMap.containsKey(streakModel)) {
                        temporaryStreakDuplicateMap.put(streakModel, streakModel.cpy());
                    }

                    streakModelDuplicate = temporaryStreakDuplicateMap.get(streakModel);
                }
                catch (Exception e) {
                    StreakTheSpire.logError("Failed when duplicating rotating streak model: " + ExceptionUtil.getFullMessage(e));
                    errorOccurred = true;
                }

                if(!errorOccurred) {
                    for (RunDataSubset data : allCharacterSubsets) {
                        if(data == null) {
                            StreakTheSpire.logError("Failed during loop of runDataToProcess for rotating streak due to null runData!");
                            continue;
                        }

                        ProcessResult result = processRunData(criteria, data, currentStreakCharacterIDs, streakModelDuplicate, RotatingConstants.Identifier);
                        try {
                            if (result == ProcessResult.StreakIncreased) {
                                currentStreakCharacterIDs.add(0, data.character_chosen);
                            } else if (result == ProcessResult.StreakReset) {
                                currentStreakCharacterIDs.clear();
                            }
                        }
                        catch (Exception e) {
                            StreakTheSpire.logError("Failed after processing run data for rotating streak \"" + data.filename + "\": " + ExceptionUtil.getFullMessage(e));
                        }
                    }
                }

                try {
                    if (addedRotatingCondition) {
                        disqualifyingConditions.remove(uniqueCharacterCondition);
                    }
                }
                catch (Exception e) {
                    StreakTheSpire.logError("Failed removing rotating streak disqualifying condition: " + ExceptionUtil.getFullMessage(e));
                }

                try {
                    if(!errorOccurred) {
                        streakModelDuplicate.currentStreakCharacterIDs.clear();
                        streakModelDuplicate.currentStreakCharacterIDs.addAll(currentStreakCharacterIDs);
                    }
                }
                catch (Exception e) {
                    StreakTheSpire.logError("Failed when updating current streak rotating character IDs: " + ExceptionUtil.getFullMessage(e));
                }
            }
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed when processing continuous, prior to runDataLoop: " + ExceptionUtil.getFullMessage(e));
        }

        try {
            for (Map.Entry<PlayerStreakModel, PlayerStreakModel> entry : temporaryStreakDuplicateMap.entrySet()) {
                entry.getKey().set(entry.getValue());
            }
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed when copying temporary streak map to data models: " + ExceptionUtil.getFullMessage(e));
        }
    }

    private void createRotatingModel() {
        PlayerStreakModel rotatingModel = new PlayerStreakModel();
        rotatingModel.identifier.set(RotatingConstants.Identifier);
        model.rotatingPlayerStreakModel.set(rotatingModel);
    }

    private static enum ProcessResult {
        StreakIncreased,
        StreakReset,
        Disqualified,
        Undefined
    }

    public static final int MAX_NUM_PROFILES = 3;
    private String getProfileIndependentFolderName(String folderName) {
        for(int i = 0; i < MAX_NUM_PROFILES; i++) {
            String testPrefix = i + "_";
            if(folderName.startsWith(testPrefix)) {
                return folderName.substring(testPrefix.length());
            }
        }

        return folderName;
    }

    // Returns whether or not the run qualified for victory testing, not whether it was a pass or not, to aid in filtering
    private static ProcessResult processRunData(StreakCriteriaModel criteria, RunDataSubset data, ArrayList<String> currentStreakCharacterIDs, PlayerStreakModel streakModel, String identifier) {
        try {
            String currentStreakTimestamp = streakModel.highestStreakTimestamp.get();
            if (currentStreakTimestamp != null && data.timestamp.compareTo(currentStreakTimestamp) < 0) {
                StreakTheSpire.logError("{} {}: Highest streak timestamp \"{}\" appears to be from after data.timestamp: {}", data.filename, identifier, currentStreakTimestamp, data.timestamp);
                return ProcessResult.Disqualified;
            }

            int streakCount = streakModel.currentStreak.get();

            ProcessResult processResult = ProcessResult.Undefined;
            for (Map.Entry<DisqualifyingCondition, String> entry : disqualifyingConditions.entrySet()) {
                DisqualifyingCondition condition = entry.getKey();
                String reason = entry.getValue();

                StreakTheSpire.logDebug("{} {}: Testing disqualifying condition: {}", data.filename, identifier, reason);
                if (condition.test(data, currentStreakCharacterIDs, criteria)) {
                    processResult = ProcessResult.Disqualified;
                    StreakTheSpire.logDebug("{} {}: Disqualified due to: {}", data.filename, identifier, reason);
                    break;
                }
            }

            if (processResult != ProcessResult.Disqualified) {
                boolean failed = false;
                for (Map.Entry<LosingCondition, String> entry : losingConditions.entrySet()) {
                    LosingCondition condition = entry.getKey();
                    String reason = entry.getValue();

                    StreakTheSpire.logDebug("{} {}: Testing losing condition: {}", data.filename, identifier, reason);
                    if (condition.test(data, currentStreakCharacterIDs, criteria)) {
                        failed = true;
                        StreakTheSpire.logDebug("{} {}: Lost due to: {}", data.filename, identifier, reason);
                        break;
                    }
                }

                if (failed) {
                    streakCount = 0;
                    streakModel.totalValidLosses.set(streakModel.totalValidLosses.get() + 1);
                    processResult = ProcessResult.StreakReset;
                } else {
                    streakCount++;
                    streakModel.totalValidWins.set(streakModel.totalValidWins.get() + 1);
                    processResult = ProcessResult.StreakIncreased;
                }

                streakModel.currentStreak.set(streakCount);
                streakModel.currentStreakTimestamp.set(data.timestamp);

                if (streakModel.highestStreak.get() < streakCount) {
                    streakModel.highestStreak.set(streakCount);
                    streakModel.highestStreakTimestamp.set(data.timestamp);
                }
            }

            streakModel.processedFilenames.add(data.filename);
            return processResult;
        }
        catch (Exception e) {
            StreakTheSpire.logError("Failed to process runData \"" + (data != null ? data.filename : "null") + "\": " + ExceptionUtil.getFullMessage(e));
            return null;
        }
    }

    public String createStreakDebugReport() {
        StringBuilder report = new StringBuilder();

        report.append("Streak Report:\n\n");

        ArrayList<PlayerStreakModel> playerStreakModels = new ArrayList<>(model.playerToStreak);
        if(model.rotatingPlayerStreakModel.get() != null)
            playerStreakModels.add(model.rotatingPlayerStreakModel.get());

        for(PlayerStreakModel playerStreakModel : playerStreakModels)
        {
            report.append("Character: ").append(playerStreakModel.identifier.get()).append("\n");
            report.append("\tHighest Streak: ").append(playerStreakModel.highestStreak.get()).append("\n");
            report.append("\tCurrent Streak: ").append(playerStreakModel.currentStreak.get()).append("\n");
            report.append("\tHighest Streak Timestamp: ").append(playerStreakModel.highestStreakTimestamp.get()).append("\n");
            report.append("\tCurrent Streak Timestamp: ").append(playerStreakModel.currentStreakTimestamp.get()).append("\n");
            report.append("\tTotal Wins: ").append(playerStreakModel.totalValidWins.get()).append("\n");
            report.append("\tTotal Losses: ").append(playerStreakModel.totalValidLosses.get()).append("\n");
            report.append("\tWin Rate: ").append((float) playerStreakModel.totalValidWins.get() / (float) playerStreakModel.totalValidLosses.get()).append("\n");
            report.append("\tProcessed Filenames: ").append(String.join(", ", playerStreakModel.processedFilenames)).append("\n");
        }

        return report.toString();
    }

    private static LinkedHashMap<DisqualifyingCondition, String> createDisqualifyingConditions() {
        LinkedHashMap<DisqualifyingCondition, String> disqualifyingConditions = new LinkedHashMap<>();

        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (!data.is_ascension_mode && criteria.requiredAscensionLevel.get() > 0)), "not_ascension");
        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (data.ascension_level < criteria.requiredAscensionLevel.get())), "ascension_level_too_low");
        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (data.chose_seed && criteria.allowCustomSeeds.get() == false)), "chose_seed");
        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (data.is_daily && criteria.allowDailies.get() == false)), "is_daily");
        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (data.is_trial && criteria.allowDemo.get() == false)), "is_demo");
        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (data.is_prod && criteria.allowBeta.get() == false)), "is_beta");
        disqualifyingConditions.put(((data, currentStreakCharacterIDs, criteria) -> (data.is_endless && criteria.allowEndless.get() == false)), "is_endless");

        return disqualifyingConditions;
    }

    private static LinkedHashMap<LosingCondition, String> createLosingConditions() {
        LinkedHashMap<LosingCondition, String> losingConditions = new LinkedHashMap<>();

        losingConditions.put(((data, previousData, criteria) -> !data.victory), "victory_failed");
        losingConditions.put(((data, previousData, criteria) ->
                (data.floor_reached < StreakCriteriaModel.HeartKillFloorReached && criteria.requireHeartKill.get() == true)), "did_not_kill_heart");

        return losingConditions;
    }

    private static boolean isUniqueCharacterInRotation(RunDataSubset data, ArrayList<String> currentStreakCharacterIDs, StreakCriteriaModel criteria) {
        int numTrackedCharacterClasses = criteria.trackedCharacterClasses.size();

        for(int i = 0; i < currentStreakCharacterIDs.size() && i < numTrackedCharacterClasses - 1; i++) {
            if(data.victory && data.character_chosen.equals(currentStreakCharacterIDs.get(i))) {
                return true;
            }
        }

        return false;
    }
}
