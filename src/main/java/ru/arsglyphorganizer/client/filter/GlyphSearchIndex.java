package ru.arsglyphorganizer.client.filter;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import ru.arsglyphorganizer.data.model.GlyphCatalog;
import ru.arsglyphorganizer.data.model.GlyphCatalogEntry;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public final class GlyphSearchIndex {
    private GlyphSearchIndex() {
    }

    public static boolean matches(AbstractSpellPart part, String query) {
        String needle = normalize(query);
        if (needle.isEmpty()) {
            return true;
        }

        GlyphCatalogEntry entry = GlyphCatalog.get().find(part.getRegistryName());
        Stream.Builder<String> fields = Stream.builder();
        fields.add(part.getLocaleName());
        fields.add(part.getBookDescription());
        fields.add(String.valueOf(part.getRegistryName()));
        fields.add(part.getLocalizationKey());
        if (entry != null) {
            fields.add(entry.proposedRussianName());
            fields.add(entry.oldRussianName());
            fields.add(entry.englishName());
            fields.add(entry.subcategory());
            entry.aliases().forEach(fields::add);
        }
        return fields.build().filter(Objects::nonNull)
                .map(GlyphSearchIndex::normalize)
                .anyMatch(value -> value.contains(needle));
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String lowered = value.toLowerCase(Locale.ROOT).replace('ё', 'е');
        return Normalizer.normalize(lowered, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim();
    }
}
