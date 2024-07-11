package com.teampotato.restarks;

import com.google.common.collect.Lists;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(modid = ReStarks.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ReStarksConfig {
    public static ForgeConfigSpec configSpec;
    public static ForgeConfigSpec.BooleanValue isTradeable, isCurse, isTreasureOnly, isDiscoverable, isAllowedOnBooks, isAverageHealAmounts;
    public static final ForgeConfigSpec.ConfigValue<String> rarity;
    public static ForgeConfigSpec.DoubleValue playerAroundX, playerAroundY, playerAroundZ, playerHealthPercentage;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> invalidDamageSourceTypes;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ReStarks");
        builder.comment(
                "The enchantment makes your attack to creatures heal your pets, but the pets should be close to you",
                "Here are the expanded coordinates data of the judgment, centered on your location"
        );
        playerAroundX = builder.defineInRange("playerAroundX", 10.00, 1.00, Double.MAX_VALUE);
        playerAroundY = builder.defineInRange("playerAroundY", 10.00, 1.00, Double.MAX_VALUE);
        playerAroundZ = builder.defineInRange("playerAroundZ", 10.00, 1.00, Double.MAX_VALUE);
        builder.comment("\n");
        playerHealthPercentage = builder
                .comment("If player's current health is less than 'maxHealth * playerHealthPercentage', players will not take damage for their pets")
                .defineInRange("playerHealthPercentage", 0.30, 0.00, 1.00);
        builder.comment("\n");
        builder.comment("Enchantment's properties");
        isTradeable = builder.define("isTradeable", true);
        isCurse = builder.define("isCurse", false);
        isTreasureOnly = builder.define("isTreasure", false);
        isDiscoverable = builder.define("canBeFoundInLoot", true);
        rarity = builder
                .comment("Allowed value: COMMON, UNCOMMON, RARE, VERY_RARE")
                .define("rarity", "UNCOMMON");
        isAllowedOnBooks = builder.define("isAllowedOnBooks", true);
        builder.comment("\n");
        isAverageHealAmounts = builder
                .comment(
                        "Your attack to creatures will heal your pets based on the damage amounts.",
                        "But if you turn this off, the damage amounts won't be average according to the pets number, but all amounts persent on each heal."
                )
                .define("isAverageHealAmounts", true);
        invalidDamageSourceTypes = builder
                .comment(
                        "Here are the types of damage source that the player will not take instead for the pets.",
                        "Allowed values: " +
                                "inFire, lightningBolt, onFire, " +
                                "lava, hotFloor, inWall, " +
                                "cramming, drown, starve, " +
                                "cactus, fall, flyIntoWall, " +
                                "outOfWorld, generic, magic, " +
                                "wither, anvil, fallingBlock, " +
                                "dragonBreath, dryout, sweetBerryBush"
                )
                .defineList("invalidDamageSourceTypes", Lists.newArrayList("onFire", "drown", "fall", "outOfWorld", "cactus", "lava"), o -> o instanceof String);
        builder.pop();
        configSpec = builder.build();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onConfigLoad(ModConfigEvent.Loading event) {
       if (event.getConfig().getSpec() == configSpec) {
            updateEnchantmentRarity();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == configSpec) {
            updateEnchantmentRarity();
        }
    }

    private static void updateEnchantmentRarity() {
        // 根据配置更新附魔品质
        Enchantment.Rarity Rarity = switch (rarity.get().toUpperCase()) {
            case "COMMON" -> Enchantment.Rarity.COMMON;
            case "RARE" -> Enchantment.Rarity.RARE;
            case "VERY_RARE" -> Enchantment.Rarity.VERY_RARE;
            default -> Enchantment.Rarity.UNCOMMON;
        };

        // 更新附魔实例的品质字段
        try {
            Field rarityField = ReStarksEnchantment.class.getDeclaredField("rarity");
            rarityField.setAccessible(true);
            rarityField.set(null, Rarity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ReStarks.LOGGER.error("Can't switch the rarity");
        }
    }
}
