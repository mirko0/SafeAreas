package com.mcodelogic.safeareas.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark configuration fields for automatic codec generation.
 * Fields annotated with this will automatically have codec serialization/deserialization setup.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {
    
    /**
     * The key name used in the config file.
     * If not specified, uses the field name.
     */
    String value() default "";
    
    /**
     * The codec type to use for this field.
     * If CUSTOM is specified, you must provide a codec via customCodec().
     */
    CodecType type() default CodecType.AUTO;
    
    /**
     * Optional custom codec class for complex types.
     * Only used when type = CodecType.CUSTOM
     */
    Class<?> customCodec() default void.class;
    
    enum CodecType {
        AUTO,      // Auto-detect based on field type
        STRING,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CUSTOM     // Use customCodec()
    }
}
