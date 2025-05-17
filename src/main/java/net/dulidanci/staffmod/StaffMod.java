package net.dulidanci.staffmod;

import net.dulidanci.staffmod.block.ModBlocks;
import net.dulidanci.staffmod.enchantments.ModEnchantments;
import net.dulidanci.staffmod.entity.ModEntities;
import net.dulidanci.staffmod.entity.custom.TrackedAnvilEntity;
import net.dulidanci.staffmod.item.ModItemGroups;
import net.dulidanci.staffmod.item.ModItems;
import net.dulidanci.staffmod.item.custom.BellStaffItem;
import net.dulidanci.staffmod.item.custom.LapisLazuliStaffItem;
import net.dulidanci.staffmod.item.custom.MagmaStaffItem;
import net.dulidanci.staffmod.util.EntityTimerManager;
import net.dulidanci.staffmod.util.ManaSupplier;
import net.dulidanci.staffmod.util.PlayerItemTracker;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaffMod implements ModInitializer {
	public static final String MOD_ID = "staffmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		StaffMod.LOGGER.info("Initializing " + StaffMod.MOD_ID + " 'main' entrypoint!");
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModEntities.registerModEntities();
		ModItemGroups.registerItemGroups();
		ModEnchantments.registerModEnchantments();

		PlayerItemTracker.register();
		EntityTimerManager.register();
		ManaSupplier.register();

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			// Your custom logic here
			if (!world.isClient) {
				TrackedAnvilEntity.setTargetForAnvils(entity);

				if (player.getMainHandStack().isOf(ModItems.MAGMA_STAFF)) {
					if (ManaSupplier.manaCheck(player, MagmaStaffItem.mana)) {
						entity.setOnFireFor(8);
					}

				}

				if (player.getMainHandStack().isOf(ModItems.LAPIS_LAZULI_STAFF)) {
					if (entity instanceof LivingEntity livingEntity) {
						if (ManaSupplier.manaCheck(player, LapisLazuliStaffItem.manaOnHit)) {
							livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 600, 0));
						}
					}
				}

				if (player.getMainHandStack().isOf(ModItems.BELL_STAFF)) {
					if (entity instanceof MobEntity mob) {
						BellStaffItem.onHit(mob, world, player);
					}
				}
			}
			return ActionResult.PASS;
		});


	}
}