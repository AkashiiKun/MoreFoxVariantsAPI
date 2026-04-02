package io.github.akashiikun.mfvapi.impl.extension.client;

import io.github.akashiikun.mfvapi.api.v1.FoxVariant;

public interface FoxRenderStateExtension {
	FoxVariant getVariant();
	void setVariant(FoxVariant variant);
}