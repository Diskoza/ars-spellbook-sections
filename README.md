# Ars Spellbook Sections

Client-side mod for Forge 1.20.1 and Ars Nouveau 4.12.7. It sorts the available glyphs into meaningful sections and prints the section titles directly on the book pages.

- No extra buttons or panels.
- The regular and the infinite spell book are supported equally.
- Search works by Russian and English name, description, ID and catalog aliases.
- The glyph tier is read at runtime via `AbstractSpellPart#getConfigTier()`.
- Registers no items, blocks, packets or server handlers.
- Not required on the server (`displayTest="IGNORE_ALL_VERSION"`, dependencies are marked `CLIENT`).

The glyph translation is intentionally not included: it can be used separately as `Ars-Glyphs-RU-1.0.1-resource-only.jar`.

## Building

Java 17–21 is required. Put `ars_nouveau-1.20.1-4.12.7-all.jar` into `libs/`, then:

```powershell
.\gradlew.bat clean build
```

Output: `build/libs/Ars-Spellbook-Sections-1.2.3.jar`.
