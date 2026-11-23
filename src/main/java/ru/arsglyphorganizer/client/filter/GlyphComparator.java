package ru.arsglyphorganizer.client.filter;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import ru.arsglyphorganizer.data.model.GlyphCatalog;
import ru.arsglyphorganizer.data.model.GlyphCatalogEntry;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public final class GlyphComparator implements Comparator<AbstractSpellPart> {
    private static final Collator RUSSIAN = Collator.getInstance(Locale.forLanguageTag("ru"));
    public static final GlyphComparator INSTANCE = new GlyphComparator();

    private GlyphComparator() {
        RUSSIAN.setStrength(Collator.PRIMARY);
    }

    @Override
    public int compare(AbstractSpellPart left, AbstractSpellPart right) {
        GlyphCatalog catalog = GlyphCatalog.get();
        GlyphCatalogEntry leftEntry = catalog.find(left.getRegistryName());
        GlyphCatalogEntry rightEntry = catalog.find(right.getRegistryName());

        int result = Integer.compare(runtimeTier(left), runtimeTier(right));
        if (result != 0) {
            return result;
        }
        result = RUSSIAN.compare(subcategory(leftEntry), subcategory(rightEntry));
        if (result != 0) {
            return result;
        }
        result = RUSSIAN.compare(left.getLocaleName(), right.getLocaleName());
        if (result != 0) {
            return result;
        }
        return String.valueOf(left.getRegistryName()).compareTo(String.valueOf(right.getRegistryName()));
    }

    public static int runtimeTier(AbstractSpellPart part) {
        try {
            return part.getConfigTier() == null ? Integer.MAX_VALUE : part.getConfigTier().value;
        } catch (RuntimeException ignored) {
            return Integer.MAX_VALUE;
        }
    }

    private static String subcategory(GlyphCatalogEntry entry) {
        return entry == null ? "" : entry.subcategory();
    }
}
