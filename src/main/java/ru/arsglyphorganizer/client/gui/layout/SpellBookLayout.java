package ru.arsglyphorganizer.client.gui.layout;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import ru.arsglyphorganizer.client.filter.GlyphClassifier;
import ru.arsglyphorganizer.client.filter.GlyphComparator;
import ru.arsglyphorganizer.data.model.GlyphCategory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SpellBookLayout {
    public static final int COLUMNS = 6;
    public static final int ROWS_PER_SIDE = 7;
    public static final int COLUMN_STEP = 20;
    public static final int ROW_STEP = 18;
    public static final int LEFT_PAGE_X = 20;
    public static final int RIGHT_PAGE_X = 154;
    public static final int TOP_Y = 20;

    private SpellBookLayout() {
    }

    public static List<Spread> build(List<AbstractSpellPart> parts) {
        Map<GlyphCategory, List<AbstractSpellPart>> grouped = new EnumMap<>(GlyphCategory.class);
        for (GlyphCategory category : GlyphCategory.configuredOrder()) {
            grouped.put(category, new ArrayList<>());
        }
        for (AbstractSpellPart part : parts == null ? List.<AbstractSpellPart>of() : parts) {
            grouped.computeIfAbsent(GlyphClassifier.categoryOf(part), ignored -> new ArrayList<>()).add(part);
        }
        grouped.values().forEach(values -> values.sort(GlyphComparator.INSTANCE));

        Builder builder = new Builder();
        for (GlyphCategory category : GlyphCategory.configuredOrder()) {
            List<AbstractSpellPart> categoryParts = grouped.get(category);
            if (categoryParts == null || categoryParts.isEmpty()) {
                continue;
            }
            builder.startCategory(category);
            for (AbstractSpellPart part : categoryParts) {
                builder.addGlyph(category, part);
            }
            builder.finishCategory();
        }
        return builder.finish();
    }

    private static final class Builder {
        private final List<Spread> spreads = new ArrayList<>();
        private final List<Heading> headings = new ArrayList<>();
        private final List<PlacedGlyph> glyphs = new ArrayList<>();
        private int side;
        private int row;
        private int column;

        void startCategory(GlyphCategory category) {
            finishGlyphRow();
            ensureHeadingAndGlyphRow();
            headings.add(new Heading(category, side, row, false));
            row++;
        }

        void addGlyph(GlyphCategory category, AbstractSpellPart part) {
            if (row >= ROWS_PER_SIDE) {
                nextSide();
                headings.add(new Heading(category, side, row, true));
                row++;
            }
            glyphs.add(new PlacedGlyph(part, side, row, column));
            column++;
            if (column >= COLUMNS) {
                column = 0;
                row++;
            }
        }

        void finishCategory() {
            finishGlyphRow();
        }

        List<Spread> finish() {
            finishGlyphRow();
            if (!headings.isEmpty() || !glyphs.isEmpty() || spreads.isEmpty()) {
                flushSpread();
            }
            return List.copyOf(spreads);
        }

        private void ensureHeadingAndGlyphRow() {
            if (row > ROWS_PER_SIDE - 2) {
                nextSide();
            }
        }

        private void finishGlyphRow() {
            if (column != 0) {
                column = 0;
                row++;
            }
        }

        private void nextSide() {
            finishGlyphRow();
            if (side == 0) {
                side = 1;
                row = 0;
            } else {
                flushSpread();
            }
        }

        private void flushSpread() {
            spreads.add(new Spread(headings, glyphs));
            headings.clear();
            glyphs.clear();
            side = 0;
            row = 0;
            column = 0;
        }
    }

    public record Spread(List<Heading> headings, List<PlacedGlyph> glyphs) {
        public Spread {
            headings = List.copyOf(headings);
            glyphs = List.copyOf(glyphs);
        }
    }

    public record PlacedGlyph(AbstractSpellPart part, int side, int row, int column) {
    }

    public record Heading(GlyphCategory category, int side, int row, boolean continuation) {
    }
}
