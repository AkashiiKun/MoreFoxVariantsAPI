package io.github.akashiikun.mfvapi.api.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

import java.util.List;

public record FoxVariant(FoxVariant.AssetInfo adultInfo, FoxVariant.AssetInfo babyInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
	public static final Codec<FoxVariant> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(FoxVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(FoxVariant::adultInfo), FoxVariant.AssetInfo.CODEC.fieldOf("baby_assets").forGetter(FoxVariant::babyInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(FoxVariant::spawnConditions)).apply(i, FoxVariant::new));
	public static final Codec<FoxVariant> NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(FoxVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(FoxVariant::adultInfo), FoxVariant.AssetInfo.CODEC.fieldOf("baby_assets").forGetter(FoxVariant::babyInfo)).apply(i, FoxVariant::new));
	public static final Codec<Holder<FoxVariant>> CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FoxVariant>> STREAM_CODEC;

	private FoxVariant(final FoxVariant.AssetInfo adultInfo, final FoxVariant.AssetInfo babyInfo) {
		this(adultInfo, babyInfo, SpawnPrioritySelectors.EMPTY);
	}

	public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
		return this.spawnConditions.selectors();
	}

	static {
		CODEC = RegistryFixedCodec.create(MfvApiRegistries.FOX_VARIANT);
		STREAM_CODEC = ByteBufCodecs.holderRegistry(MfvApiRegistries.FOX_VARIANT);
	}

	public record AssetInfo(ClientAsset.ResourceTexture awake, ClientAsset.ResourceTexture sleeping) {
		public static final Codec<FoxVariant.AssetInfo> CODEC = RecordCodecBuilder.create((instance) -> instance.group(ClientAsset.ResourceTexture.CODEC.fieldOf("awake").forGetter(FoxVariant.AssetInfo::awake), ClientAsset.ResourceTexture.CODEC.fieldOf("sleeping").forGetter(FoxVariant.AssetInfo::sleeping)).apply(instance, FoxVariant.AssetInfo::new));
	}
}