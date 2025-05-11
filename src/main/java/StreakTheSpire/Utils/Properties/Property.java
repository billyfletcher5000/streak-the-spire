package StreakTheSpire.Utils.Properties;

import java.util.HashSet;

public class Property<T> {
    private T value;
    private HashSet<ValueChangedSubscriber> onChangedSubscribers;

    public Property(T value) { this.value = value; }

    public T getValue() { return value; }

    public void setValue(T value) {
        if(this.value != value)
        {
            this.value = value;
            if (onChangedSubscribers != null) {
                for (ValueChangedSubscriber subscriber : onChangedSubscribers) {
                    subscriber.onValueChanged();
                }
            }
        }
    }

    public static class ValueChangedSubscriber {
        public void onValueChanged() {}
    }

    public void addOnChangedSubscriber(ValueChangedSubscriber subscriber) {
        if (onChangedSubscribers == null)
            onChangedSubscribers = new HashSet<>();

        onChangedSubscribers.add(subscriber);
    }

    public void removeOnChangedSubscriber(ValueChangedSubscriber subscriber) {
        if(onChangedSubscribers != null)
            onChangedSubscribers.remove(subscriber);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}
