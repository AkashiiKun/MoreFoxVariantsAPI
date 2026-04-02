package io.github.akashiikun.mfvapi;

import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import io.github.akashiikun.mfvapi.api.v1.MfvApiDataComponents;
import io.github.akashiikun.mfvapi.api.v1.MfvApiRegistries;
import io.github.akashiikun.mfvapi.impl.init.ModEntityDataSerializers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MfvInit implements ModInitializer {
	public static final String MOD_ID = "mfvapi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(MfvApiRegistries.FOX_VARIANT, FoxVariant.DIRECT_CODEC, FoxVariant.NETWORK_CODEC);

		MfvApiDataComponents.register();
		ModEntityDataSerializers.register();

		BiomeModifications.addSpawn(BiomeSelectors.tag(MfvApiRegistries.FOX_SPAWNS), MobCategory.CREATURE, EntityType.FOX, 8, 2, 4);
	}

}