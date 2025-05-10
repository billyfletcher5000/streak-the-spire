package StreakTheSpire.Utils;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

import static basemod.BaseMod.gson;

public class PropertyList<T extends Serializable> extends Collection<T> {
    private ArrayList<T> elements;

    public PropertyList() {
        elements = new ArrayList<>();
    }

    public PropertyList(ArrayList<T> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public PropertyList(T... elements) {
        this.elements = new ArrayList<>(elements.length);
        Stream.of(elements).forEach(element -> {
            if(element != null)
                this.elements.add(element);
        });
    }

    public boolean add(T element) {
        boolean result = elements.add(element);

        if (onItemAddedSubscribers != null) {
            for (PropertyList.ItemAddedSubscriber subscriber : onItemAddedSubscribers) {
                subscriber.onPropertyListItemAdded(element);
            }
        }
        return result;
    }

    public boolean remove(T element) {
        if(elements.remove(element)) {
            if (onItemRemovedSubscribers != null) {
                for (PropertyList.ItemRemovedSubscriber subscriber : onItemRemovedSubscribers) {
                    subscriber.onPropertyListItemRemoved(element);
                }
            }

            if (elements.isEmpty()) {
                if (onClearedSubscribers != null) {
                    for (PropertyList.ListClearedSubscriber subscriber : onClearedSubscribers) {
                        subscriber.onPropertyListCleared();
                    }
                }
            }

            return true;
        }

        return false;
    }

    public void removeAll(T value) {
        Stream<T> stream = elements.stream().filter(element -> element.equals(value));
        stream.forEach(element -> remove(element));
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

    public void clear() {
        if(!elements.isEmpty()) {
            elements.clear();

            if (onClearedSubscribers != null) {
                for (PropertyList.ListClearedSubscriber subscriber : onClearedSubscribers) {
                    subscriber.onPropertyListCleared();
                }
            }
        }
    }

    public String toSerialisationString() {
        return gson.toJson(elements);
    }

    public void fromSerialisationString(String serialisationString) {
        Type setType = new TypeToken<ArrayList<T>>(){}.getType();
        elements = gson.fromJson(serialisationString, setType);
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
