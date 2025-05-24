package StreakTheSpire.Utils.Properties;

import StreakTheSpire.StreakTheSpire;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.UUID;

public class Property<T> {
    UUID uuid = UUID.randomUUID();
    public UUID getUUID() {
        return uuid;
    }

    private T value;
    private HashSet<WeakReference<ValueChangedSubscriber>> onChangedSubscribers;

    public Property(T value) { this.value = value; }

    public T get() { return value; }

    public void set(T value) {
        if(this.value != value)
        {
            this.value = value;
            if (onChangedSubscribers != null) {
                for (WeakReference<ValueChangedSubscriber> subscriber : onChangedSubscribers) {
                    if(subscriber.get() != null)
                        subscriber.get().onValueChanged();
                }
            }
        }
    }

    // Do not use this unless you have a very specific, generics related reason to do so,
    // it will set the value to null if it cannot be casted to the type T
    public void setObject(Object value) {
        StreakTheSpire.logDebug("Setting object to " + value.getClass().getName());
        set((T) value);
    }

    public static interface ValueChangedSubscriber {
        void onValueChanged();
    }

    public ValueChangedSubscriber addOnChangedSubscriber(ValueChangedSubscriber subscriber) {
        if (onChangedSubscribers == null)
            onChangedSubscribers = new HashSet<>();

        onChangedSubscribers.add(new WeakReference<>(subscriber));
        return subscriber;
    }

    public void removeOnChangedSubscriber(ValueChangedSubscriber subscriber) {
        if(onChangedSubscribers != null)
            onChangedSubscribers.removeIf(element -> element.get() == null || element.get() == subscriber);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}
