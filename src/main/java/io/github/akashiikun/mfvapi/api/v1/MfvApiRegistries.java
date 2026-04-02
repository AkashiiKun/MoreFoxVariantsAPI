package io.github.akashiikun.mfvapi.api.v1;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import static net.minecraft.resources.ResourceKey.createRegistryKey;

@SuppressWarnings("NullableProblems")
public class MfvApiRegistries {
	public static final ResourceKey<Registry<FoxVariant>> FOX_VARIANT = createRegistryKey(Identifier.parse("mfvapi:fox_variant"));
	public static final TagKey<Biome> FOX_SPAWNS = TagKey.create(Registries.BIOME, Identifier.parse("mfvapi:fox_spawn"));
}