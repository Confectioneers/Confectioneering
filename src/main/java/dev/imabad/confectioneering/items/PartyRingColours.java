package dev.imabad.confectioneering.items;

import net.minecraft.world.item.DyeColor;

public enum PartyRingColours {

    PINK("Pink", DyeColor.PINK, DyeColor.WHITE),
    ORANGE("Orange", DyeColor.ORANGE, DyeColor.BLACK),
    WHITE("White", DyeColor.WHITE, DyeColor.PINK),
    YELLOW("Yellow", DyeColor.YELLOW, DyeColor.PINK),
    PURPLE("Purple", DyeColor.PURPLE, DyeColor.WHITE);

    private String name;
    private DyeColor dyeColor;
    private DyeColor secondaryColor;

    PartyRingColours(String name, DyeColor dyeColor, DyeColor secondaryColor) {
        this.name = name;
        this.dyeColor = dyeColor;
        this.secondaryColor = secondaryColor;
    }

    public String getName() {
        return name;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public DyeColor getSecondaryColor() {
        return secondaryColor;
    }
}
