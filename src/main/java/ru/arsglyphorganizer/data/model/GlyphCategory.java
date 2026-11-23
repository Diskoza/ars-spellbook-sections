package ru.arsglyphorganizer.data.model;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum GlyphCategory {
    FORMS("forms"),
    AUGMENTS("augments"),
    DAMAGE("damage"),
    CONTROL("control"),
    SUPPORT("support"),
    MOBILITY("mobility"),
    UTILITY("utility"),
    SUMMONING("summoning"),
    LOGIC_ZONES("logic_zones"),
    OTHER("other");

    private static final List<GlyphCategory> ORDER = List.copyOf(Arrays.asList(values()));
    private final String id;

    GlyphCategory(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public Component title() {
        return Component.translatable("ars_glyph_organizer.category." + id);
    }

    public static GlyphCategory fromId(String value) {
        if (value == null) {
            return OTHER;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        return ORDER.stream()
                .filter(category -> category.id.equals(normalized))
                .findFirst()
                .orElse(OTHER);
    }

    public static List<GlyphCategory> configuredOrder() {
        return ORDER;
    }
}
