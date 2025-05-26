package StreakTheSpire.Ceremonies;

import java.util.HashSet;

public abstract class ICeremony {

    private HashSet<CeremonyCompleteSubscriber> onCompleteSubscribers;

    public interface CeremonyCompleteSubscriber {
        void complete();
    }


    // Used when the user wants to skip the ceremony or other event needs the ceremony to end instantly
    public void forceEnd() {}

    public void update(float deltaTime) {}

    public void close() {}

    protected final void completeCeremony() {
        if(onCompleteSubscribers != null) {
            for(CeremonyCompleteSubscriber subscriber : onCompleteSubscribers) {
                if(subscriber != null) {
                    subscriber.complete();
                }
            }
        }
    }

    public CeremonyCompleteSubscriber addOnChangedSubscriber(CeremonyCompleteSubscriber subscriber) {
        if (onCompleteSubscribers == null)
            onCompleteSubscribers = new HashSet<>();

        onCompleteSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnChangedSubscriber(CeremonyCompleteSubscriber subscriber) {
        if(onCompleteSubscribers != null)
            onCompleteSubscribers.removeIf(element -> element == null || element == subscriber);
    }
}
