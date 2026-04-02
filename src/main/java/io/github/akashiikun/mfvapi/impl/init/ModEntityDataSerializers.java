package io.github.akashiikun.mfvapi.impl.init;
import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;

@SuppressWarnings("NullableProblems")
public class ModEntityDataSerializers {
	public static final EntityDataSerializer<Holder<FoxVariant>> FOX_VARIANT = EntityDataSerializer.forValueType(FoxVariant.STREAM_CODEC);

	public static void register() {
		FabricEntityDataRegistry.register(Identifier.parse("mfvapi:fox_variant"), FOX_VARIANT);
	}
}