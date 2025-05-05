package StreakTheSpire.Data;

import StreakTheSpire.Utils.Property;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class StreakDataModel {
    public interface IPlayerAddedSubscriber {
        void onPlayerStreakAdded(AbstractPlayer.PlayerClass player);
    }

    public interface IPlayerRemovedSubscriber {
        void onPlayerStreakRemoved(AbstractPlayer.PlayerClass player);
    }

    private HashMap<AbstractPlayer.PlayerClass, Property<Integer>> playerToStreak = new HashMap<>();
    private HashSet<IPlayerAddedSubscriber> playerAddedSubscribers = new HashSet<>();
    private HashSet<IPlayerRemovedSubscriber> playerRemovedSubscribers = new HashSet<>();

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

    public void addPlayerAddedSubscriber(IPlayerAddedSubscriber subscriber) {
        playerAddedSubscribers.add(subscriber);
    }

    public void removePlayerAddedSubscriber(IPlayerAddedSubscriber subscriber) {
        playerAddedSubscribers.remove(subscriber);
    }

    public void addPlayerRemovedSubscriber(IPlayerRemovedSubscriber subscriber) {
        playerRemovedSubscribers.add(subscriber);
    }

    public void removePlayerRemovedSubscriber(IPlayerRemovedSubscriber subscriber) {
        playerRemovedSubscribers.add(subscriber);
    }
}
