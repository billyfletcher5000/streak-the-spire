package StreakTheSpire.Utils.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PropertyList<T> extends ArrayList<T> {
    public PropertyList() {
        super();
    }

    public PropertyList(ArrayList<T> elements) {
        super(elements);
    }

    public PropertyList(T... elements) {
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
    public void add(int index, T element) {
        super.add(index, element);
        notifyElementAdded(element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = super.addAll(c);
        if(result)
            c.forEach(e -> notifyElementAdded(e));

        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {

        ArrayList<T> copy = new ArrayList<>(this);
        if(super.addAll(index, c)) {
            copy.removeAll(this);
            copy.forEach(e -> notifyElementAdded(e));
            return true;
        }

        return false;
    }

    @Override
    public T remove(int index) {
        T result = super.remove(index);
        if(result != null)
            notifyElementRemoved(result);

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
            onItemAddedSubscribers.forEach(subscriber -> { subscriber.onPropertyListItemAdded(element); });
    }

    private void notifyElementRemoved(Object element) {
        if(onItemRemovedSubscribers != null)
            onItemRemovedSubscribers.forEach(subscriber -> { subscriber.onPropertyListItemRemoved(element); });

        if(isEmpty() && onClearedSubscribers != null)
            onClearedSubscribers.forEach(subscriber -> { subscriber.onPropertyListCleared(); });
    }

    private HashSet<PropertyList.ItemAddedSubscriber> onItemAddedSubscribers;
    private HashSet<PropertyList.ItemRemovedSubscriber> onItemRemovedSubscribers;
    private HashSet<PropertyList.ListClearedSubscriber> onClearedSubscribers;

    public static class ItemAddedSubscriber {
        public void onPropertyListItemAdded(Object item) {}
    }

    public static class ItemRemovedSubscriber {
        public void onPropertyListItemRemoved(Object item) {}
    }

    public static class ListClearedSubscriber {
        public void onPropertyListCleared() {}
    }

    public void addOnItemAddedSubscriber(PropertyList.ItemAddedSubscriber subscriber) {
        if (onItemAddedSubscribers == null)
            onItemAddedSubscribers = new HashSet<>();

        onItemAddedSubscribers.add(subscriber);
    }

    public void removeOnItemAddedSubscriber(PropertyList.ItemAddedSubscriber subscriber) {
        if(onItemAddedSubscribers != null)
            onItemAddedSubscribers.remove(subscriber);
    }

    public void addOnItemRemovedSubscriber(PropertyList.ItemRemovedSubscriber subscriber) {
        if (onItemRemovedSubscribers == null)
            onItemRemovedSubscribers = new HashSet<>();

        onItemRemovedSubscribers.add(subscriber);
    }

    public void removeOnItemRemovedSubscriber(PropertyList.ItemRemovedSubscriber subscriber) {
        if(onItemRemovedSubscribers != null)
            onItemRemovedSubscribers.remove(subscriber);
    }

    public void addOnListClearedSubscriber(PropertyList.ListClearedSubscriber subscriber) {
        if (onClearedSubscribers == null)
            onClearedSubscribers = new HashSet<>();

        onClearedSubscribers.add(subscriber);
    }

    public void removeOnListClearedSubscriber(PropertyList.ListClearedSubscriber subscriber) {
        if(onClearedSubscribers != null)
            onClearedSubscribers.remove(subscriber);
    }
}
