package StreakTheSpire.Utils.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.function.Predicate;

public class PropertyHashSet<T> extends HashSet<T> {
    public PropertyHashSet() {
        super();
    }

    public PropertyHashSet(HashSet<T> elements) {
        super(elements);
    }

    public PropertyHashSet(T... elements) {
        Stream.of(elements).forEach(element -> {
            if(element != null)
                add(element);
        });
    }

    @Override
    public boolean add(T t) {
        if(super.add(t)) {
            notifyElementAdded(t);
            return true;
        }

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = false;
        for(T t : c)
            result |= add(t);

        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if(result)
            notifyElementRemoved(o);

        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean anyChanged = false;

        for (Object o : c)
            anyChanged |= remove(o);

        return anyChanged;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        ArrayList<T> copy = new ArrayList<>(this);

        if(super.removeIf(filter)) {
            copy.removeAll(this);
            copy.forEach(e -> notifyElementRemoved(e));
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        if(isEmpty()) return;

        ArrayList<T> copy = new ArrayList<>(this);
        copy.forEach(e -> remove(e));
    }

    private void notifyElementAdded(T element) {
        if(onItemAddedSubscribers != null)
            onItemAddedSubscribers.forEach(subscriber -> { subscriber.onItemAdded(element); });
    }

    private void notifyElementRemoved(Object element) {
        if(onItemRemovedSubscribers != null)
            onItemRemovedSubscribers.forEach(subscriber -> { subscriber.onItemRemoved(element); });

        if(isEmpty() && onClearedSubscribers != null)
            onClearedSubscribers.forEach(subscriber -> { subscriber.onCleared(); });
    }

    private HashSet<PropertyHashSet.ItemAddedSubscriber> onItemAddedSubscribers;
    private HashSet<PropertyHashSet.ItemRemovedSubscriber> onItemRemovedSubscribers;
    private HashSet<PropertyHashSet.HashSetClearedSubscriber> onClearedSubscribers;

    public static class ItemAddedSubscriber {
        public void onItemAdded(Object item) {}
    }

    public static class ItemRemovedSubscriber {
        public void onItemRemoved(Object item) {}
    }

    public static class HashSetClearedSubscriber {
        public void onCleared() {}
    }

    public void addOnItemAddedSubscriber(PropertyHashSet.ItemAddedSubscriber subscriber) {
        if (onItemAddedSubscribers == null)
            onItemAddedSubscribers = new HashSet<>();

        onItemAddedSubscribers.add(subscriber);
    }

    public void removeOnItemAddedSubscriber(PropertyHashSet.ItemAddedSubscriber subscriber) {
        if(onItemAddedSubscribers != null)
            onItemAddedSubscribers.remove(subscriber);
    }

    public void addOnItemRemovedSubscriber(PropertyHashSet.ItemRemovedSubscriber subscriber) {
        if (onItemRemovedSubscribers == null)
            onItemRemovedSubscribers = new HashSet<>();

        onItemRemovedSubscribers.add(subscriber);
    }

    public void removeOnItemRemovedSubscriber(PropertyHashSet.ItemRemovedSubscriber subscriber) {
        if(onItemRemovedSubscribers != null)
            onItemRemovedSubscribers.remove(subscriber);
    }

    public void addOnListClearedSubscriber(PropertyHashSet.HashSetClearedSubscriber subscriber) {
        if (onClearedSubscribers == null)
            onClearedSubscribers = new HashSet<>();

        onClearedSubscribers.add(subscriber);
    }

    public void removeOnListClearedSubscriber(PropertyHashSet.HashSetClearedSubscriber subscriber) {
        if(onClearedSubscribers != null)
            onClearedSubscribers.remove(subscriber);
    }


}
