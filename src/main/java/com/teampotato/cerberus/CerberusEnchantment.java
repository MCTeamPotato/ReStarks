package com.teampotato.cerberus;


import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import static com.teampotato.cerberus.CerberusConfig.*;

public class CerberusEnchantment extends Enchantment {
    public static Enchantment.Rarity rarity = Enchantment.Rarity.UNCOMMON;

    public CerberusEnchantment() {
        super(rarity, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    public boolean isTreasureOnly() {
        return isTreasureOnly.get();
    }

    public boolean isCurse() {
        return isCurse.get();
    }

    public boolean isTradeable() {
        return isTradeable.get();
    }

    public boolean isDiscoverable() {
        return isDiscoverable.get();
    }

    public boolean isAllowedOnBooks() {
        return isAllowedOnBooks.get();
    }
}