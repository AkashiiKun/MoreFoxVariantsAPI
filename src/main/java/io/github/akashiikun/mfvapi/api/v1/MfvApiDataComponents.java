package io.github.akashiikun.mfvapi.api.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class MfvApiDataComponents {
    public static final DataComponentType<Holder<FoxVariant>> FOX_VARIANT = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.parse("mfvapi:fox/variant"), DataComponentType.<Holder<FoxVariant>>builder().persistent(FoxVariant.CODEC).networkSynchronized(FoxVariant.STREAM_CODEC).build());

    public static void register() {

    }
}
