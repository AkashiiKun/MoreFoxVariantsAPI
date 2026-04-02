package io.github.akashiikun.mfvapi.impl.mixin.client;

import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import io.github.akashiikun.mfvapi.impl.extension.client.FoxRenderStateExtension;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FoxRenderState.class)
public class FoxRenderStateMixin implements FoxRenderStateExtension {
	@Unique
	private FoxVariant variant;
	@Override
	public FoxVariant getVariant() {
		return this.variant;
	}

	public FoxRenderStateMixin() {
	}

	@Override
	public void setVariant(FoxVariant variant) {
		this.variant = variant;
	}
}