package com.mcodelogic.safeareas.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class to automatically build codecs for configuration classes.
 * Uses reflection to scan @ConfigField annotations and generate the codec boilerplate.
 * 
 * <p>Example usage:
 * <pre>{@code
 * public class MyConfig {
 *     public static final BuilderCodec<MyConfig> CODEC = 
 *         ConfigCodecBuilder.create(MyConfig.class, MyConfig::new);
 *     
 *     @ConfigField
 *     private String serverName = "default";
 *     
 *     @ConfigField("max_players")
 *     private int maxPlayers = 20;
 * }
 * }</pre>
 */
public class ConfigCodecBuilder {
    
    /**
     * Creates a BuilderCodec for the given configuration class.
     * Automatically scans all fields annotated with @ConfigField and sets up serialization.
     * 
     * @param configClass The configuration class to build a codec for
     * @param constructor A supplier that creates new instances of the config class
     * @param <T> The type of the configuration class
     * @return A fully configured BuilderCodec
     */
    public static <T> BuilderCodec<T> create(Class<T> configClass, Supplier<T> constructor) {
        BuilderCodec.Builder<T> builder = BuilderCodec.builder(configClass, constructor);
        
        // Collect all fields annotated with @ConfigField
        List<Field> configFields = new ArrayList<>();
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigField.class)) {
                configFields.add(field);
            }
        }
        
        // Build codec for each field
        for (Field field : configFields) {
            field.setAccessible(true);
            ConfigField annotation = field.getAnnotation(ConfigField.class);
            
            String key = annotation.value().isEmpty() ? field.getName() : annotation.value();
            Codec<?> codec = getCodecForField(field, annotation);
            
            builder = appendField(builder, field, key, codec);
        }
        
        return builder.build();
    }
    
    /**
     * Determines the appropriate codec for a field based on its type and annotation.
     */
    @SuppressWarnings("unchecked")
    private static <T> Codec<T> getCodecForField(Field field, ConfigField annotation) {
        ConfigField.CodecType type = annotation.type();
        
        // Auto-detect codec type if AUTO is specified
        if (type == ConfigField.CodecType.AUTO) {
            Class<?> fieldType = field.getType();
            
            if (fieldType == String.class) {
                return (Codec<T>) Codec.STRING;
            } else if (fieldType == int.class || fieldType == Integer.class) {
                return (Codec<T>) Codec.INTEGER;
            } else if (fieldType == long.class || fieldType == Long.class) {
                return (Codec<T>) Codec.LONG;
            } else if (fieldType == float.class || fieldType == Float.class) {
                return (Codec<T>) Codec.FLOAT;
            } else if (fieldType == double.class || fieldType == Double.class) {
                return (Codec<T>) Codec.DOUBLE;
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                return (Codec<T>) Codec.BOOLEAN;
            } else {
                throw new IllegalArgumentException(
                    "Cannot auto-detect codec for field " + field.getName() + 
                    " of type " + fieldType.getName() + 
                    ". Please specify a CodecType or use CUSTOM with customCodec()."
                );
            }
        }
        
        // Use explicit codec type
        switch (type) {
            case STRING:
                return (Codec<T>) Codec.STRING;
            case INT:
                return (Codec<T>) Codec.INTEGER;
            case LONG:
                return (Codec<T>) Codec.LONG;
            case FLOAT:
                return (Codec<T>) Codec.FLOAT;
            case DOUBLE:
                return (Codec<T>) Codec.DOUBLE;
            case BOOLEAN:
                return (Codec<T>) Codec.BOOLEAN;
            case CUSTOM:
                return getCustomCodec(annotation);
            default:
                throw new IllegalArgumentException("Unknown codec type: " + type);
        }
    }
    
    /**
     * Retrieves a custom codec from the annotation.
     */
    @SuppressWarnings("unchecked")
    private static <T> Codec<T> getCustomCodec(ConfigField annotation) {
        Class<?> codecClass = annotation.customCodec();
        if (codecClass == void.class) {
            throw new IllegalArgumentException(
                "CUSTOM codec type specified but no customCodec() class provided"
            );
        }
        
        try {
            // Try to get a static CODEC field from the class
            Field codecField = codecClass.getDeclaredField("CODEC");
            codecField.setAccessible(true);
            return (Codec<T>) codecField.get(null);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Failed to retrieve CODEC from custom codec class " + codecClass.getName() +
                ". Ensure the class has a public static CODEC field.", e
            );
        }
    }
    
    /**
     * Appends a field to the builder with appropriate getter/setter lambdas.
     */
    @SuppressWarnings("unchecked")
    private static <T, V> BuilderCodec.Builder<T> appendField(
            BuilderCodec.Builder<T> builder,
            Field field,
            String key,
            Codec<V> codec) {
        
        KeyedCodec<V> keyedCodec = new KeyedCodec<>(key, codec);
        
        return builder.append(
            keyedCodec,
            (config, value, extra) -> {
                try {
                    field.set(config, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to set field " + field.getName(), e);
                }
            },
            (config, extra) -> {
                try {
                    return (V) field.get(config);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to get field " + field.getName(), e);
                }
            }
        ).add();
    }
}
