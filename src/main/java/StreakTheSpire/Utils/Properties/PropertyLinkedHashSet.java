package StreakTheSpire.Utils.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Stream;
import java.util.function.Predicate;

public class PropertyLinkedHashSet<T> extends LinkedHashSet<T> {
    public PropertyLinkedHashSet() {
        super();
    }

    public PropertyLinkedHashSet(HashSet<T> elements) {
        super(elements);
    }

    @SafeVarargs
    public PropertyLinkedHashSet(T... elements) {
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
            copy.forEach(this::notifyElementRemoved);
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        if(isEmpty()) return;

        ArrayList<T> copy = new ArrayList<>(this);
        copy.forEach(this::remove);
    }

    private void notifyElementAdded(T element) {
        if(onItemAddedSubscribers != null)
            onItemAddedSubscribers.forEach(subscriber -> { subscriber.onItemAdded(element); });
    }

    private void notifyElementRemoved(Object element) {
        if(onItemRemovedSubscribers != null)
            onItemRemovedSubscribers.forEach(subscriber -> { subscriber.onItemRemoved(element); });

        if(isEmpty() && onClearedSubscribers != null)
            onClearedSubscribers.forEach(PropertyHashSet.HashSetClearedSubscriber::onCleared);
    }

    private HashSet<PropertyHashSet.ItemAddedSubscriber> onItemAddedSubscribers;
    private HashSet<PropertyHashSet.ItemRemovedSubscriber> onItemRemovedSubscribers;
    private HashSet<PropertyHashSet.HashSetClearedSubscriber> onClearedSubscribers;

    public interface ItemAddedSubscriber {
        void onItemAdded(Object item);
    }

    public interface ItemRemovedSubscriber {
        void onItemRemoved(Object item);
    }

    public interface HashSetClearedSubscriber {
        void onCleared();
    }

    public PropertyHashSet.ItemAddedSubscriber addOnItemAddedSubscriber(PropertyHashSet.ItemAddedSubscriber subscriber) {
        if (onItemAddedSubscribers == null)
            onItemAddedSubscribers = new HashSet<>();

        onItemAddedSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnItemAddedSubscriber(PropertyHashSet.ItemAddedSubscriber subscriber) {
        if(onItemAddedSubscribers != null)
            onItemAddedSubscribers.remove(subscriber);
    }

    public PropertyHashSet.ItemRemovedSubscriber addOnItemRemovedSubscriber(PropertyHashSet.ItemRemovedSubscriber subscriber) {
        if (onItemRemovedSubscribers == null)
            onItemRemovedSubscribers = new HashSet<>();

        onItemRemovedSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnItemRemovedSubscriber(PropertyHashSet.ItemRemovedSubscriber subscriber) {
        if(onItemRemovedSubscribers != null)
            onItemRemovedSubscribers.remove(subscriber);
    }

    public PropertyHashSet.HashSetClearedSubscriber addOnListClearedSubscriber(PropertyHashSet.HashSetClearedSubscriber subscriber) {
        if (onClearedSubscribers == null)
            onClearedSubscribers = new HashSet<>();

        onClearedSubscribers.add(subscriber);
        return subscriber;
    }

    public void removeOnListClearedSubscriber(PropertyHashSet.HashSetClearedSubscriber subscriber) {
        if(onClearedSubscribers != null)
            onClearedSubscribers.remove(subscriber);
    }
}
