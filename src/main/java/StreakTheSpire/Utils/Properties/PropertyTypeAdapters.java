package StreakTheSpire.Utils.Properties;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

public abstract class PropertyTypeAdapters {
    public static class PropertyTypeAdapter<E> extends TypeAdapter<Property<E>> {

        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                Class<T> rawType = (Class<T>) type.getRawType();
                if (rawType != Property.class) {
                    return null;
                }
                final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                final Type actualType = parameterizedType.getActualTypeArguments()[0];
                final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
                return new PropertyTypeAdapter(adapter);
            }
        };

        private final TypeAdapter<E> adapter;

        public PropertyTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public Property<E> read(JsonReader in) throws IOException {
            if (in.peek() != JsonToken.NULL) {
                return new Property<>(adapter.read(in));
            } else {
                in.nextNull();
                return new Property<>(null);
            }
        }

        @Override
        public void write(JsonWriter out, Property<E> value) throws IOException {
            if(value.getValue() != null){
                adapter.write(out, value.getValue());
            } else {
                out.nullValue();
            }
        }
    }

    public static class PropertyHashSetTypeAdapter<E> extends TypeAdapter<PropertyHashSet<E>> {

        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                Class<T> rawType = (Class<T>) type.getRawType();
                if (rawType != PropertyHashSet.class) {
                    return null;
                }
                final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                final Type actualType = parameterizedType.getActualTypeArguments()[0];
                final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
                return new PropertyHashSetTypeAdapter(adapter);
            }
        };
        private final TypeAdapter<E> adapter;

        public PropertyHashSetTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public PropertyHashSet<E> read(JsonReader in) throws IOException {
            in.beginArray();
            PropertyHashSet<E> ls = new PropertyHashSet<>();
            while (in.peek() != JsonToken.END_ARRAY) {
                ls.add(adapter.read(in));
            }
            in.endArray();
            return ls;
        }

        @Override
        public void write(JsonWriter out, PropertyHashSet<E> value) throws IOException {
            out.beginArray();
            for (E e : value) {
                adapter.write(out, e);
            }
            out.endArray();
        }
    }

    public static class PropertyListTypeAdapter<E> extends TypeAdapter<PropertyList<E>> {

        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                Class<T> rawType = (Class<T>) type.getRawType();
                if (rawType != PropertyList.class) {
                    return null;
                }
                final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                final Type actualType = parameterizedType.getActualTypeArguments()[0];
                final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
                return new PropertyListTypeAdapter(adapter);
            }
        };
        private final TypeAdapter<E> adapter;

        public PropertyListTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public PropertyList<E> read(JsonReader in) throws IOException {
            in.beginArray();
            PropertyList<E> ls = new PropertyList<>();
            while (in.peek() != JsonToken.END_ARRAY) {
                ls.add(adapter.read(in));
            }
            in.endArray();
            return ls;
        }

        @Override
        public void write(JsonWriter out, PropertyList<E> value) throws IOException {
            out.beginArray();
            for (E e : value) {
                adapter.write(out, e);
            }
            out.endArray();
        }
    }
}
