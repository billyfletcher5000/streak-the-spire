package StreakTheSpire.Models;

import StreakTheSpire.Utils.Property;
import StreakTheSpire.Utils.PropertyHashSet;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static basemod.BaseMod.gson;

public class StreakModel implements IConfigDataModel{
    private HashMap<AbstractPlayer.PlayerClass, PropertyHashSet<String>> playerToProcessedRunFilename = new HashMap<>();
    private HashMap<AbstractPlayer.PlayerClass, Property<Integer>> playerToStreak = new HashMap<>();

    public Set<AbstractPlayer.PlayerClass> getPlayers() { return playerToStreak.keySet(); }

    public Property<Integer> getStreak(AbstractPlayer.PlayerClass player) {
        return playerToStreak.get(player);
    }

    public void setStreak(AbstractPlayer.PlayerClass player, int streak) {
        if (playerToStreak.containsKey(player)) {
            playerToStreak.get(player).setValue(streak);
            return;
        }

        playerToStreak.put(player, new Property<>(streak));
        playerAddedSubscribers.forEach(subscriber -> subscriber.onPlayerStreakAdded(player));
    }

    public void removeStreak(AbstractPlayer.PlayerClass player) {
        if(playerToStreak.remove(player) != null) {
            playerRemovedSubscribers.forEach(subscriber -> subscriber.onPlayerStreakRemoved(player));
        }
    }

    public void clearStreaks() {
        if(!playerToStreak.isEmpty()) {
            playerToStreak.clear();
            playerStreakListClearedSubscribers.forEach(subscriber -> subscriber.onPlayerStreakListCleared());
        }
    }

    public void notifyRunFileProcessed(AbstractPlayer.PlayerClass player, String runFilename) {
        if(!playerToProcessedRunFilename.containsKey(player)) {
            playerToProcessedRunFilename.put(player, new PropertyHashSet<>());
        }

        playerToProcessedRunFilename.get(player).add(runFilename);
    }

    public void clearData()
    {
        clearStreaks();
        playerToProcessedRunFilename.clear();
    }

    //region IConfigModel
    // We store everything along with the processed run files so we don't have to load every run every time
    // the game loads.

    private final String PlayerToStreakConfigName = "PlayerToStreak";
    private final String PlayerToProcessedFilenamesConfigName = "PlayerToProcessedFilenames";

    @Override
    public void loadFromConfig(SpireConfig config) {
        playerToStreak.clear();
        playerToProcessedRunFilename.clear();

        // TODO: I think this could be done generically by using Gson's TypeAdapter or JsonSerializer<> stuff
        Type stringIntegerMapType = new TypeToken<HashMap<String, Integer>>(){}.getType();
        Type stringStringMapType = new TypeToken<HashMap<String, String>>(){}.getType();

        String stringStreakMapSource = config.getString(PlayerToStreakConfigName);
        HashMap<String, Integer> stringStreakMap = gson.fromJson(stringStreakMapSource, stringIntegerMapType);

        stringStreakMap.forEach((playerClassStr, streakValue) -> {
           playerToStreak.put(AbstractPlayer.PlayerClass.valueOf(playerClassStr), new Property<>(streakValue));
        });

        String stringProcessedFilenamesSource = config.getString(PlayerToProcessedFilenamesConfigName);
        HashMap<String, String> stringProcessedFilenamesMap = gson.fromJson(stringProcessedFilenamesSource, stringStringMapType);
        stringProcessedFilenamesMap.forEach((playerClassStr, processedFilenameSetStr) -> {
            PropertyHashSet<String> processedFilenameSet = new PropertyHashSet<>();
            processedFilenameSet.fromSerialisationString(processedFilenameSetStr);
            playerToProcessedRunFilename.put(AbstractPlayer.PlayerClass.valueOf(playerClassStr), processedFilenameSet);
        });
    }

    @Override
    public void saveToConfig(SpireConfig config) {
        Gson gson = new Gson();

        HashMap<String, Integer> stringStreakMap = new HashMap<>();
        playerToStreak.forEach((playerClass, property) -> {
            stringStreakMap.put(playerClass.toString(), property.getValue());
        });

        config.setString(PlayerToStreakConfigName, gson.toJson(stringStreakMap));

        HashMap<String, String> stringProcessedFilenamesMap = new HashMap<>();
        playerToProcessedRunFilename.forEach((playerClass, property) -> {
            stringProcessedFilenamesMap.put(playerClass.toString(), property.toSerialisationString());
        });

        config.setString(PlayerToProcessedFilenamesConfigName, gson.toJson(stringProcessedFilenamesMap));
    }

    //endregion


    //region Events

    public class PlayerAddedSubscriber {
        public void onPlayerStreakAdded(AbstractPlayer.PlayerClass player) {}
    }

    public class PlayerRemovedSubscriber {
        public void onPlayerStreakRemoved(AbstractPlayer.PlayerClass player) {}
    }

    public class PlayerStreakListClearedSubscriber {
        public void onPlayerStreakListCleared() {}
    }

    private HashSet<PlayerAddedSubscriber> playerAddedSubscribers = new HashSet<>();
    private HashSet<PlayerRemovedSubscriber> playerRemovedSubscribers = new HashSet<>();
    private HashSet<PlayerStreakListClearedSubscriber> playerStreakListClearedSubscribers = new HashSet<>();

    public void addPlayerAddedSubscriber(PlayerAddedSubscriber subscriber) {
        playerAddedSubscribers.add(subscriber);
    }

    public void removePlayerAddedSubscriber(PlayerAddedSubscriber subscriber) {
        playerAddedSubscribers.remove(subscriber);
    }

    public void addPlayerRemovedSubscriber(PlayerRemovedSubscriber subscriber) {
        playerRemovedSubscribers.add(subscriber);
    }

    public void removePlayerRemovedSubscriber(PlayerRemovedSubscriber subscriber) {
        playerRemovedSubscribers.add(subscriber);
    }

    public void addPlayerStreakListClearedSubscriber(PlayerStreakListClearedSubscriber subscriber) {
        playerStreakListClearedSubscribers.add(subscriber);
    }

    public void removePlayerStreakListClearedSubscriber(PlayerStreakListClearedSubscriber subscriber) {
        playerStreakListClearedSubscribers.remove(subscriber);
    }

    //endregion
}
