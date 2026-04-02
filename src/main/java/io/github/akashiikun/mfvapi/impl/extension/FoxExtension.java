package io.github.akashiikun.mfvapi.impl.extension;

import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import net.minecraft.core.Holder;

@SuppressWarnings("NullableProblems")
public interface FoxExtension {
	void setVariant(Holder<FoxVariant> holder);

	public Holder<FoxVariant> getVariant();
}