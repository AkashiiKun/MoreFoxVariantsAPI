package io.github.akashiikun.mfvapi.api.v1;

import io.github.akashiikun.mfvapi.impl.extension.FoxExtension;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

@SuppressWarnings("NullableProblems")
public class FoxVariants {
	public static final ResourceKey<FoxVariant> RED = createKey("red");
	public static final ResourceKey<FoxVariant> SNOW = createKey("snow");
	public static final ResourceKey<FoxVariant> DEFAULT = RED;

	private static ResourceKey<FoxVariant> createKey(final String name) {
		return ResourceKey.create(MfvApiRegistries.FOX_VARIANT, Identifier.withDefaultNamespace(name));
	}

	private static void register(
			final BootstrapContext<FoxVariant> context, final ResourceKey<FoxVariant> name, final String fileName, final ResourceKey<Biome> spawnBiome
	) {
		register(context, name, fileName, highPrioBiome(HolderSet.direct(context.lookup(Registries.BIOME).getOrThrow(spawnBiome))));
	}

	private static void register(
			final BootstrapContext<FoxVariant> context, final ResourceKey<FoxVariant> name, final String fileName, final TagKey<Biome> spawnBiome
	) {
		register(context, name, fileName, highPrioBiome(context.lookup(Registries.BIOME).getOrThrow(spawnBiome)));
	}

	private static SpawnPrioritySelectors highPrioBiome(final HolderSet<Biome> biomes) {
		return SpawnPrioritySelectors.single(new BiomeCheck(biomes), 1);
	}

	private static void register(
			final BootstrapContext<FoxVariant> context, final ResourceKey<FoxVariant> name, final String fileName, final SpawnPrioritySelectors selectors
	) {
		Identifier wildTexture = Identifier.withDefaultNamespace("entity/fox/" + fileName);
		Identifier wildSleepTexture = Identifier.withDefaultNamespace("entity/fox/" + fileName+ "_sleep");
		Identifier babyTexture = Identifier.withDefaultNamespace("entity/fox/" + fileName + "_baby");
		Identifier babySleepTexture = Identifier.withDefaultNamespace("entity/fox/" + fileName + "_sleep_baby");
		context.register(
				name,
				new FoxVariant(
						new FoxVariant.AssetInfo(
								new ClientAsset.ResourceTexture(wildTexture), new ClientAsset.ResourceTexture(wildSleepTexture)
						),
						new FoxVariant.AssetInfo(
								new ClientAsset.ResourceTexture(babyTexture), new ClientAsset.ResourceTexture(babySleepTexture)
						),
						selectors
				)
		);
	}

	public static ResourceKey<FoxVariant> fromVanilla(Fox.Variant variant) {
		return switch (variant) {
			case RED -> RED;
			case SNOW -> SNOW;
		};
	}

	public static Holder<FoxVariant> getVariant(Fox fox) {
		return ((FoxExtension) fox).getVariant();
	}
	
	public static void setVariant(Fox fox, Holder<FoxVariant> variantHolder) {
		((FoxExtension) fox).setVariant(variantHolder);
	}
}