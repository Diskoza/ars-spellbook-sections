package ru.arsglyphorganizer.client.filter;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import ru.arsglyphorganizer.data.model.GlyphCatalog;
import ru.arsglyphorganizer.data.model.GlyphCatalogEntry;
import ru.arsglyphorganizer.data.model.GlyphCategory;

import java.util.Locale;

public final class GlyphClassifier {
    private GlyphClassifier() {
    }

    public static GlyphCategory categoryOf(AbstractSpellPart part) {
        if (part == null) {
            return GlyphCategory.OTHER;
        }
        GlyphCatalogEntry catalogEntry = GlyphCatalog.get().find(part.getRegistryName());
        if (catalogEntry != null) {
            return catalogEntry.category();
        }
        if (part instanceof AbstractCastMethod) {
            return GlyphCategory.FORMS;
        }
        if (part instanceof AbstractAugment) {
            return GlyphCategory.AUGMENTS;
        }

        String registryName = String.valueOf(part.getRegistryName()).toLowerCase(Locale.ROOT);
        String path = registryName.indexOf(':') >= 0
                ? registryName.substring(registryName.indexOf(':') + 1)
                : registryName;

        if (containsAny(path, "filter", "target", "delay", "rune", "wall", "linger", "zone", "field",
                "redirect", "conditional", "condition", "repeat", "loop", "chain", "sequence", "reset",
                "swap", "propagate", "trigger")) {
            return GlyphCategory.LOGIC_ZONES;
        }
        if (containsAny(path, "summon", "familiar", "minion", "servant", "decoy", "clone", "steed", "wolf",
                "undead", "vex", "shikigami", "shadow")) {
            return GlyphCategory.SUMMONING;
        }
        if (containsAny(path, "blink", "teleport", "warp", "dash", "leap", "jump", "glide", "flight", "fly",
                "slowfall", "slow_fall", "orbit", "bounce", "movement")) {
            return GlyphCategory.MOBILITY;
        }
        if (containsAny(path, "heal", "shield", "barrier", "protect", "ward", "resist", "cleanse", "dispel",
                "invisibility", "invisible", "sense", "rewind", "restore", "support")) {
            return GlyphCategory.SUPPORT;
        }
        if (containsAny(path, "pull", "push", "gust", "launch", "snare", "root", "freeze", "slow", "stun",
                "gravity", "fracture", "hex", "wither", "silence", "limitless", "soulrime", "raiseearth",
                "raise_earth", "knockback")) {
            return GlyphCategory.CONTROL;
        }
        if (containsAny(path, "harm", "damage", "attack", "fire", "flame", "ignite", "lightning", "thunder",
                "explode", "explosion", "blast", "meteor", "obliterate", "annihilate", "dismantle", "smite",
                "fang", "missile", "projectile", "arrow", "blade", "cut", "crush", "eruption", "iceburst",
                "ice_burst", "cold_snap", "windshear", "wind_shear")) {
            return GlyphCategory.DAMAGE;
        }
        if (containsAny(path, "break", "harvest", "grow", "fell", "smelt", "craft", "place", "block", "water",
                "evaporate", "pickup", "inventory", "redstone", "rotate", "exchange", "infuse", "interact",
                "name", "polish", "press", "compact", "extract", "fabricate", "create", "utility", "toss")) {
            return GlyphCategory.UTILITY;
        }
        return GlyphCategory.OTHER;
    }

    private static boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
