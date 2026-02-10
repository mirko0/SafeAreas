package com.mcodelogic.safeareas.lang;

import com.hypixel.hytale.server.core.Message;
import com.mcodelogic.safeareas.config.ConfigField;
import com.mcodelogic.safeareas.config.LangConfig;
import com.mcodelogic.safeareas.utils.TinyMsg;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to configured messages. Use {@link #get(String)} for plain strings
 * and {@link #get(String, Object...)} for messages with placeholders ({0}, {1}, ...).
 * Use {@link #getMessage(String)} / {@link #getMessage(String, Object...)} for messages
 * that support TinyMsg styling (e.g. &lt;red&gt;, &lt;green&gt;, &lt;bold&gt;) and return a {@link Message}.
 * <p>
 * Config keys are PascalCase (e.g. CommandRegionDeleted, ProtectionBuildPrimary).
 */
public class Lang {

    private final Map<String, String> messages = new HashMap<>();

    public Lang(LangConfig config) {
        if (config == null) {
            return;
        }
        for (Field field : LangConfig.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigField.class)) {
                continue;
            }
            ConfigField annotation = field.getAnnotation(ConfigField.class);
            String key = annotation.value().isEmpty() ? field.getName() : annotation.value();
            field.setAccessible(true);
            try {
                Object value = field.get(config);
                if (value != null) {
                    messages.put(key, value.toString());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to read lang field: " + field.getName(), e);
            }
        }
    }

    /**
     * Returns the message for the given key. If the key is missing, returns the key itself.
     */
    public String get(String key) {
        return messages.getOrDefault(key, key);
    }

    /**
     * Returns the message for the given key with placeholders {0}, {1}, {2}, ... replaced
     * by the corresponding arguments. If the key is missing, returns the key with placeholders
     * replaced in it (e.g. useful for debugging).
     */
    public String get(String key, Object... args) {
        String template = messages.getOrDefault(key, key);
        if (args == null || args.length == 0) {
            return template;
        }
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String value = args[i] == null ? "" : args[i].toString();
            template = template.replace(placeholder, value);
        }
        return template;
    }

    /**
     * Returns true if a message is configured for the given key.
     */
    public boolean has(String key) {
        return messages.containsKey(key);
    }

    /**
     * Returns the message for the given key as a styled {@link Message} using TinyMsg tags.
     * Use for player-facing messages that support formatting (e.g. &lt;red&gt;, &lt;green&gt;, &lt;bold&gt;).
     */
    public Message getMessage(String key) {
        return TinyMsg.parse(get(key));
    }

    /**
     * Returns the message for the given key with placeholders replaced, as a styled {@link Message}.
     */
    public Message getMessage(String key, Object... args) {
        return TinyMsg.parse(get(key, args));
    }
}
