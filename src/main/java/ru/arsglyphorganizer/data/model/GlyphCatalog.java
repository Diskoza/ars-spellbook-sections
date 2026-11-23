package ru.arsglyphorganizer.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GlyphCatalog {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RESOURCE = "/assets/ars_glyph_organizer/glyph_catalog_plan.json";
    private static final GlyphCatalog INSTANCE = load();

    private final Map<ResourceLocation, GlyphCatalogEntry> entries;

    private GlyphCatalog(Map<ResourceLocation, GlyphCatalogEntry> entries) {
        this.entries = Collections.unmodifiableMap(entries);
    }

    public static GlyphCatalog get() {
        return INSTANCE;
    }

    public GlyphCatalogEntry find(ResourceLocation id) {
        return id == null ? null : entries.get(id);
    }

    public int size() {
        return entries.size();
    }

    private static GlyphCatalog load() {
        Map<ResourceLocation, GlyphCatalogEntry> result = new LinkedHashMap<>();
        try (InputStream stream = GlyphCatalog.class.getResourceAsStream(RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Missing glyph catalog resource " + RESOURCE);
            }
            CatalogDocument document = new Gson().fromJson(
                    new InputStreamReader(stream, StandardCharsets.UTF_8), CatalogDocument.class);
            for (GlyphCatalogEntry entry : document == null || document.glyphs == null
                    ? List.<GlyphCatalogEntry>of()
                    : document.glyphs) {
                ResourceLocation id = entry.id();
                if (id == null) {
                    LOGGER.warn("Skipping invalid glyph catalog id");
                } else if (result.putIfAbsent(id, entry) != null) {
                    throw new IllegalStateException("Duplicate glyph catalog id: " + id);
                }
            }
            LOGGER.info("Loaded {} semantic glyph catalog entries", result.size());
        } catch (Exception exception) {
            LOGGER.error("Unable to load Ars Spellbook Sections catalog", exception);
        }
        return new GlyphCatalog(result);
    }

    private static final class CatalogDocument {
        @SerializedName("glyphs")
        private List<GlyphCatalogEntry> glyphs = List.of();
    }
}
