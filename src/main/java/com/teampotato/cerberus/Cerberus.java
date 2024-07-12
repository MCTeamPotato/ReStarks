package com.teampotato.cerberus;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.teampotato.cerberus.CerberusConfig.*;

@Mod(Cerberus.ID)
@Mod.EventBusSubscriber
public class Cerberus {
    public static final String ID = "cerberus";
    public static final Logger LOGGER = LogManager.getLogger("cerberus");
    public static final DeferredRegister<Enchantment> ENCHANTMENT_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ID);

    @SuppressWarnings("unused")
    public static final RegistryObject<Enchantment> RESTARKS = ENCHANTMENT_DEFERRED_REGISTER.register(ID, com.teampotato.cerberus.CerberusEnchantment::new);

    private static boolean isCerberusPresent(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentTags().toString().contains(ID);
    }

    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof Player player) {
            if (!isCerberusPresent(player)) return;
            AABB playerAABB = player.getBoundingBox();
            List<TamableAnimal> pets = player.level().getEntitiesOfClass(TamableAnimal.class,
                    new AABB(
                            playerAABB.minX - playerAroundX.get(),
                            playerAABB.minY - playerAroundY.get(),
                            playerAABB.minZ - playerAroundZ.get(),
                            playerAABB.maxX + playerAroundX.get(),
                            playerAABB.maxY + playerAroundY.get(),
                            playerAABB.maxZ + playerAroundZ.get()
                    )
            );
            float amount;
            if(isAverageHealAmounts.get()){
                amount = event.getAmount() / pets.size();
            } else {
                amount = event.getAmount();
            }
            for (TamableAnimal pet : pets) {
                pet.setHealth(pet.getHealth() + amount);
            }
        }
    }

    @SubscribeEvent
    public static void onPetHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof TamableAnimal pet && !invalidDamageSourceTypes.get().contains(event.getSource().getMsgId())) {
            if (pet.getOwner() instanceof Player player) {
                DamageSource source = event.getSource();
                double amount = event.getAmount();
                double health = player.getHealth();
                double maxHealth = player.getMaxHealth();
                if (
                        health >= maxHealth * playerHealthPercentage.get() &&
                        health > amount &&
                        isCerberusPresent(player)
                ) {
                    player.hurt(source, (float) amount);
                    event.setCanceled(true);
                }
            }
        }
    }

    public Cerberus() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
        ENCHANTMENT_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }
}