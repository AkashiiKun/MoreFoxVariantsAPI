package io.github.akashiikun.mfvapi.impl.mixin.client;

import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import io.github.akashiikun.mfvapi.api.v1.FoxVariants;
import io.github.akashiikun.mfvapi.impl.extension.client.FoxRenderStateExtension;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.fox.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxRenderer.class)
public class FoxRendererMixin {
    /**
     * @author AkashiiKun
     * @reason Temporary
     */
    @Overwrite
    public Identifier getTextureLocation(FoxRenderState state) {
        return ((FoxRenderStateExtension) state).getVariant() == null ? MissingTextureAtlasSprite.getLocation() : getFoxTextureFromVariant(((FoxRenderStateExtension) state).getVariant(), state.isBaby, state.isSleeping);
    }

    @Unique
    private Identifier getFoxTextureFromVariant(FoxVariant foxVariant, boolean isBaby, boolean isSleeping) {
        FoxVariant.AssetInfo info = isBaby ? foxVariant.babyInfo() : foxVariant.adultInfo();
        return (isSleeping ? info.sleeping() : info.awake()).texturePath();
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/fox/Fox;Lnet/minecraft/client/renderer/entity/state/FoxRenderState;F)V", at = @At("RETURN"))
    void mfvapi$extractRenderState(Fox fox, FoxRenderState state, float partialTicks, CallbackInfo ci) {
        ((FoxRenderStateExtension) state).setVariant(FoxVariants.getVariant(fox).value());
    }
}
