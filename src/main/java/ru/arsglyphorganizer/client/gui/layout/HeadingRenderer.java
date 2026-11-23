package ru.arsglyphorganizer.client.gui.layout;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class HeadingRenderer {
    private static final int TEXT_COLOR = 0xFF808080;
    private static final int MAX_WIDTH = 116;

    private HeadingRenderer() {
    }

    public static void draw(GuiGraphics graphics, Font font, int bookLeft, int bookTop,
                            List<SpellBookLayout.Heading> headings) {
        for (SpellBookLayout.Heading heading : headings) {
            int x = bookLeft + (heading.side() == 0
                    ? SpellBookLayout.LEFT_PAGE_X
                    : SpellBookLayout.RIGHT_PAGE_X);
            int y = bookTop + 5 + (heading.row() + 1) * SpellBookLayout.ROW_STEP;
            Component title = heading.continuation()
                    ? Component.translatable("ars_glyph_organizer.category.continued", heading.category().title())
                    : heading.category().title();
            drawFitted(graphics, font, title, x, y);
        }
    }

    private static void drawFitted(GuiGraphics graphics, Font font, Component title, int x, int y) {
        int width = font.width(title);
        float scale = width <= MAX_WIDTH ? 1.0F : (float) MAX_WIDTH / (float) width;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 500.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.drawString(font, title, 0, 0, TEXT_COLOR, false);
        graphics.pose().popPose();
    }
}
