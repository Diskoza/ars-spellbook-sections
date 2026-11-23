package ru.arsglyphorganizer.data.model;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public final class GlyphCatalogEntry {
    private String id;
    private String category;
    private String subcategory;
    private String proposed_russian_name;
    private String english_name;
    private String current_russian_name;
    private List<String> aliases;
    private transient ResourceLocation parsedId;

    public ResourceLocation id() {
        if (parsedId == null) {
            parsedId = ResourceLocation.tryParse(id);
        }
        return parsedId;
    }

    public GlyphCategory category() {
        return GlyphCategory.fromId(category);
    }

    public String subcategory() {
        return subcategory == null ? "" : subcategory;
    }

    public String proposedRussianName() {
        return proposed_russian_name == null ? "" : proposed_russian_name;
    }

    public String englishName() {
        return english_name == null ? "" : english_name;
    }

    public String oldRussianName() {
        return current_russian_name == null ? "" : current_russian_name;
    }

    public List<String> aliases() {
        return aliases == null ? Collections.emptyList() : aliases;
    }
}
