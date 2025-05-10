package StreakTheSpire.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static basemod.BaseMod.gson;

public class PropertyHashSet<T extends Serializable> extends Collection {
    private HashSet<T> elements;

    public PropertyHashSet() {
        elements = new HashSet<>();
    }

    public PropertyHashSet(HashSet<T> elements) {
        this.elements = new HashSet<>(elements);
    }

    public PropertyHashSet(T... elements) {
        this.elements = new HashSet<>(elements.length);
        Stream.of(elements).forEach(element -> {
            if(element != null)
                this.elements.add(element);
        });
    }

    public boolean add(T element) {
        boolean returnValue = elements.add(element);

        if (onItemAddedSubscribers != null) {
            for (PropertyHashSet.ItemAddedSubscriber subscriber : onItemAddedSubscribers) {
                subscriber.onPropertyHashSetItemAdded(element);
            }
        }

        return returnValue;
    }

    public boolean remove(T element) {
        if(elements.remove(element)) {
            if (onItemRemovedSubscribers != null) {
                for (PropertyHashSet.ItemRemovedSubscriber subscriber : onItemRemovedSubscribers) {
                    subscriber.onPropertyHashSetItemRemoved(element);
                }
            }

            if (elements.isEmpty()) {
                if (onClearedSubscribers != null) {
                    for (PropertyHashSet.HashSetClearedSubscriber subscriber : onClearedSubscribers) {
                        subscriber.onPropertyHashSetCleared();
                    }
                }
            }

            return true;
        }

        return false;
    }

    public int removeAll(T value) {
        AtomicInteger removeCount = new AtomicInteger();
        Stream<T> stream = elements.stream().filter(element -> element.equals(value));
        stream.forEach(element -> {
            if(remove(element))
                removeCount.getAndIncrement();
        });

        return removeCount.get();
    }

    public void clear() {
        if(!elements.isEmpty()) {
            elements.clear();

            if (onClearedSubscribers != null) {
                for (PropertyHashSet.HashSetClearedSubscriber subscriber : onClearedSubscribers) {
                    subscriber.onPropertyHashSetCleared();
                }
            }
        }
    }

    //region Collection implementation
    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public Iterator iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public boolean add(Object o) {
        T item = (T) o;
        if (item != null)
            return add(item);
        return false;
    }

    @Override
    public boolean remove(Object o) {
        T item = (T) o;
        if (item != null)
            return remove(item);
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        boolean result = true;
        for(Object o : c)
        {
            if(!add(o))
                result = false;
        }

        return result;
    }

    @Override
    public boolean retainAll(Collection c) {
        ArrayList<T> elementsToRemove = new ArrayList<>();
        for(T element : elements)
        {
            if(!c.contains(element))
                elementsToRemove.add(element);
        }

        boolean anyChange = false;
        for(T element : elementsToRemove)
        {
            if(remove(element))
                anyChange = true;
        }

        return anyChange;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean anyRemoved = false;
        for(Object o : c)
        {
            if(remove(o))
                anyRemoved = true;
        }

        return anyRemoved;
    }

    @Override
    public boolean containsAll(Collection c) {
        return elements.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return elements.toArray();
    }
    //endregion

    public String toSerialisationString() {
        return gson.toJson(elements);
    }

    public void fromSerialisationString(String serialisationString) {
        Type setType = new TypeToken<HashSet<T>>(){}.getType();
        elements = gson.fromJson(serialisationString, setType);
    }

    private HashSet<PropertyHashSet.ItemAddedSubscriber> onItemAddedSubscribers;
    private HashSet<PropertyHashSet.ItemRemovedSubscriber> onItemRemovedSubscribers;
    private HashSet<PropertyHashSet.HashSetClearedSubscriber> onClearedSubscribers;

    public static class ItemAddedSubscriber {
        public void onPropertyHashSetItemAdded(Object item) {}
    }

    public static class ItemRemovedSubscriber {
        public void onPropertyHashSetItemRemoved(Object item) {}
    }

    public static class HashSetClearedSubscriber {
        public void onPropertyHashSetCleared() {}
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
