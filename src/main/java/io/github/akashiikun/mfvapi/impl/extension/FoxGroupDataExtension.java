package io.github.akashiikun.mfvapi.impl.extension;

import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import net.minecraft.core.Holder;

public interface FoxGroupDataExtension {
	void setVariant(Holder<FoxVariant> variant);
	Holder<FoxVariant> getVariant();
}
