package ru.arsglyphorganizer.mixin;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.arsglyphorganizer.client.filter.GlyphSearchIndex;
import ru.arsglyphorganizer.client.gui.OrganizerScreenState;
import ru.arsglyphorganizer.client.gui.layout.HeadingRenderer;
import ru.arsglyphorganizer.client.gui.layout.SpellBookLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(GuiSpellBook.class)
public abstract class GuiSpellBookMixin extends BaseBook implements OrganizerScreenState {
    @Shadow(remap = false)
    public List<AbstractSpellPart> unlockedSpells;

    @Shadow(remap = false)
    public List<AbstractSpellPart> displayedGlyphs;

    @Shadow(remap = false)
    public List<GlyphButton> glyphButtons;

    @Shadow(remap = false)
    public NoShadowTextField searchBar;

    @Shadow(remap = false)
    public String previousString;

    @Shadow(remap = false)
    public int page;

    @Shadow(remap = false)
    public int formTextRow;

    @Shadow(remap = false)
    public int augmentTextRow;

    @Shadow(remap = false)
    public int effectTextRow;

    @Shadow(remap = false)
    public PageButton nextButton;

    @Shadow(remap = false)
    public PageButton previousButton;

    @Unique
    private String ago$query = "";

    @Unique
    private List<SpellBookLayout.Spread> ago$spreads = List.of();

    @Unique
    private List<SpellBookLayout.Heading> ago$currentHeadings = List.of();

    @Unique
    private boolean ago$applyingLayout;

    @Inject(method = "init", at = @At("TAIL"))
    private void ago$finishInit(CallbackInfo callback) {
        ago$query = "";
        ago$applyLayout(page);
    }

    @Inject(method = "layoutAllGlyphs", at = @At("HEAD"), cancellable = true, remap = false)
    private void ago$layoutSemanticSections(int requestedPage, CallbackInfo callback) {
        callback.cancel();
        ago$applyLayout(requestedPage);
    }

    @Inject(method = "getNumPages", at = @At("HEAD"), cancellable = true, remap = false)
    private void ago$getSemanticPageCount(CallbackInfoReturnable<Integer> callback) {
        ago$rebuildLayout();
        callback.setReturnValue(Math.max(1, ago$spreads.size()));
    }

    @Inject(method = "updateNextPageButtons", at = @At("HEAD"), cancellable = true, remap = false)
    private void ago$replacePageButtonState(CallbackInfo callback) {
        callback.cancel();
        ago$rebuildLayout();
        ago$updatePageButtonsWithoutRebuild();
    }

    @Inject(method = "onSearchChanged", at = @At("HEAD"), cancellable = true, remap = false)
    private void ago$replaceSearch(String query, CallbackInfo callback) {
        callback.cancel();
        String safeQuery = query == null ? "" : query;
        if (Objects.equals(safeQuery, previousString)) {
            return;
        }

        previousString = safeQuery;
        ago$query = safeQuery;
        searchBar.setSuggestion(safeQuery.isEmpty()
                ? Component.translatable("ars_nouveau.spell_book_gui.search").getString()
                : "");

        List<AbstractSpellPart> matches = new ArrayList<>();
        for (AbstractSpellPart part : unlockedSpells) {
            if (GlyphSearchIndex.matches(part, safeQuery)) {
                matches.add(part);
            }
        }
        displayedGlyphs = matches;
        ((GuiSpellBook) (Object) this).resetPageState();
    }

    @Inject(method = "drawBackgroundElements", at = @At("HEAD"), remap = false)
    private void ago$hideVanillaSectionHeadings(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                                 CallbackInfo callback) {
        formTextRow = -1;
        augmentTextRow = -1;
        effectTextRow = -1;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void ago$drawHeadingsAsTopLayer(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                             CallbackInfo callback) {
        HeadingRenderer.draw(graphics, font, bookLeft, bookTop, ago$currentHeadings);
    }

    @Unique
    private void ago$applyLayout(int requestedPage) {
        if (ago$applyingLayout) {
            return;
        }
        ago$applyingLayout = true;
        try {
            clearButtons(glyphButtons);
            formTextRow = -1;
            augmentTextRow = -1;
            effectTextRow = -1;
            ago$rebuildLayout();

            int lastPage = Math.max(0, ago$spreads.size() - 1);
            page = Math.max(0, Math.min(requestedPage, lastPage));
            SpellBookLayout.Spread spread = ago$spreads.get(page);
            ago$currentHeadings = spread.headings();

            for (SpellBookLayout.PlacedGlyph placed : spread.glyphs()) {
                int pageX = placed.side() == 0
                        ? SpellBookLayout.LEFT_PAGE_X
                        : SpellBookLayout.RIGHT_PAGE_X;
                int x = bookLeft + pageX + placed.column() * SpellBookLayout.COLUMN_STEP;
                int y = bookTop + SpellBookLayout.TOP_Y + placed.row() * SpellBookLayout.ROW_STEP;
                GlyphButton button = new GlyphButton(x, y, placed.part(),
                        clicked -> ((GuiSpellBook) (Object) this).onGlyphClick(clicked));
                addRenderableWidget(button);
                glyphButtons.add(button);
            }
            ago$updatePageButtonsWithoutRebuild();
        } finally {
            ago$applyingLayout = false;
        }
    }

    @Unique
    private void ago$rebuildLayout() {
        ago$spreads = SpellBookLayout.build(displayedGlyphs);
        if (ago$spreads.isEmpty()) {
            ago$spreads = List.of(new SpellBookLayout.Spread(List.of(), List.of()));
        }
    }

    @Unique
    private void ago$updatePageButtonsWithoutRebuild() {
        boolean hasPrevious = page > 0;
        boolean hasNext = page + 1 < ago$spreads.size();
        if (previousButton != null) {
            previousButton.visible = hasPrevious;
            previousButton.active = hasPrevious;
        }
        if (nextButton != null) {
            nextButton.visible = hasNext;
            nextButton.active = hasNext;
        }
    }

    @Override
    public boolean ago$isOrganizerEnabled() {
        return true;
    }

    @Override
    public String ago$currentQuery() {
        return ago$query;
    }
}
