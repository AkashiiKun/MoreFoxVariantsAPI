package io.github.akashiikun.mfvapi.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import io.github.akashiikun.mfvapi.api.v1.FoxVariant;
import io.github.akashiikun.mfvapi.api.v1.FoxVariants;
import io.github.akashiikun.mfvapi.api.v1.MfvApiDataComponents;
import io.github.akashiikun.mfvapi.api.v1.MfvApiRegistries;
import io.github.akashiikun.mfvapi.impl.extension.FoxExtension;
import io.github.akashiikun.mfvapi.impl.extension.FoxGroupDataExtension;
import io.github.akashiikun.mfvapi.impl.init.ModEntityDataSerializers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("NullableProblems")
@Mixin(Fox.class)
public abstract class FoxMixin extends Animal implements FoxExtension {
    @Shadow
    protected abstract void setTargetGoals();

    @Unique
    private static @Final
    @Mutable EntityDataAccessor<Holder<FoxVariant>> DATA_VARIANT_ID;

    protected FoxMixin(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("WrongEntityDataParameterClass")
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;defineId(Ljava/lang/Class;Lnet/minecraft/network/syncher/EntityDataSerializer;)Lnet/minecraft/network/syncher/EntityDataAccessor;"))
    private static <T> EntityDataAccessor<T> mfvapi$clinit(Class<? extends SyncedDataHolder> clazz, EntityDataSerializer<T> serializer, Operation<EntityDataAccessor<T>> original) {
        if (serializer == EntityDataSerializers.INT) {
            DATA_VARIANT_ID = SynchedEntityData.defineId(Fox.class, ModEntityDataSerializers.FOX_VARIANT);
            return null;
        }
        return original.call(clazz, serializer);
    }

    // replace DATA_VARIANT with DATA_VARIANT_ID
    @Redirect(method = "defineSynchedData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData$Builder;define(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)Lnet/minecraft/network/syncher/SynchedEntityData$Builder;", ordinal = 2))
    <T> SynchedEntityData.Builder mfvapi$defineSynchedData(SynchedEntityData.Builder instance, EntityDataAccessor<T> accessor, T value) {
        return instance.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), FoxVariants.DEFAULT));
    }

    @Redirect(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V")) // watch this just incase mojang adds more
    <T> void mfvapi$addAdditionalSaveData(ValueOutput output, String s, Codec<T> tCodec, T t) {
        VariantUtils.writeVariant(output, this.getVariant());
    }

    // make vanilla getVariant always return the default.
    @Inject(method = "getVariant", at = @At("HEAD"), cancellable = true)
    void mfvapi$getVariant(CallbackInfoReturnable<Fox.Variant> cir) {
        cir.setReturnValue(Fox.Variant.DEFAULT);
    }

    // make vanilla setVariant set the mfvapi variant if possible. vanilla setVariant should only be called in mods without mfvapi compatibility.
    @Inject(method = "setVariant", at = @At("HEAD"), cancellable = true)
    void mfvapi$setVariant(Fox.Variant variant, CallbackInfo ci) {
        registryAccess().get(FoxVariants.fromVanilla(variant)).ifPresent(this::setVariant);
        ci.cancel();
    }

    // Read mfvapi's variant, not vanilla's "Variant"
    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    void mfvapi$readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        if (!readLegacyVariant(input)) VariantUtils.readVariant(input, MfvApiRegistries.FOX_VARIANT).ifPresent(this::setVariant);
    }

    // Read vanilla's "Variant", as well as mfvapi v1's "Variant"
    @Unique
    private boolean readLegacyVariant(ValueInput input) {
        Fox.Variant legacyVariant = input.read("Variant", Fox.Variant.CODEC).orElse(null);
        // Handle vanilla's variant
        if (legacyVariant != null) {
            Optional<Holder.Reference<FoxVariant>> foxVariantReference = registryAccess().get(FoxVariants.fromVanilla(legacyVariant));
            foxVariantReference.ifPresent(this::setVariant);
            return true; // even if this fails to set, there's a Variant so "variant" shouldn't be used.
        }
        Identifier mfvapiV1Variant = input.read("Variant", Identifier.CODEC).orElse(null);
        // Handle mfvapi v1's variant
        if (mfvapiV1Variant != null) {
            Optional<Holder.Reference<FoxVariant>> foxVariantReference = registryAccess().lookupOrThrow(MfvApiRegistries.FOX_VARIANT).get(mfvapiV1Variant);
            foxVariantReference.ifPresent(this::setVariant);
            return true; // even if this fails to set, there's a Variant so "variant" shouldn't be used.
        }
        return false;
    }

    /**
     * @author Jab125
     * @reason how about no asm this time?
     */
    @Overwrite
    public @Nullable Fox getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
        Fox baby = EntityType.FOX.create(level, EntitySpawnReason.BREEDING);
        if (baby != null) {
            ((FoxExtension)baby).setVariant((this.random.nextBoolean() ? this : (FoxExtension) partner).getVariant());
        }

        return baby;
    }

    /**
     * @author Jab125
     * @reason TEMPORARY
     */
    @Overwrite
    public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        boolean isBaby = false;
        if (groupData instanceof Fox.FoxGroupData foxGroupData) {
            this.setVariant(((FoxGroupDataExtension) foxGroupData).getVariant());
            if (foxGroupData.getGroupSize() >= 2) {
                isBaby = true;
            }
        } else {
            Optional<Holder.Reference<FoxVariant>> variant = VariantUtils.selectVariantToSpawn(SpawnContext.create(level, this.blockPosition()), MfvApiRegistries.FOX_VARIANT);
            if (variant.isPresent()) {
                groupData = new Fox.FoxGroupData(null);
                setVariant(variant.get());
                ((FoxGroupDataExtension) groupData).setVariant(variant.get());
            }

        }

        if (isBaby) {
            this.setAge(-24000);
        }

        if (level instanceof ServerLevel) {
            this.setTargetGoals();
        }

        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    <T> void mfvapi$get(DataComponentType<? extends T> component, CallbackInfoReturnable<T> cir) {
        if (component == MfvApiDataComponents.FOX_VARIANT) {
            cir.setReturnValue(castComponentValue(component, this.getVariant()));
        }
    }

    @Inject(method = "applyImplicitComponents", at = @At("HEAD"))
    void mfvapi$applyImplicitComponents(DataComponentGetter componentGetter, CallbackInfo ci) {
        this.applyImplicitComponentIfPresent(componentGetter, MfvApiDataComponents.FOX_VARIANT);
    }

    @Inject(method = "applyImplicitComponent", at = @At("HEAD"), cancellable = true)
    <T> void mfvapi$applyImplicitComponent(DataComponentType<T> component, T value, CallbackInfoReturnable<Boolean> cir) {
        if (component == MfvApiDataComponents.FOX_VARIANT) {
            this.setVariant(castComponentValue(MfvApiDataComponents.FOX_VARIANT, value));
            cir.setReturnValue(true);
        }
    }

    @Override
    public void setVariant(Holder<FoxVariant> holder) {
        this.entityData.set(DATA_VARIANT_ID, holder);
    }

    @Override
    public Holder<FoxVariant> getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    // Needed since they have clashing names
    @Mixin(Fox.FoxGroupData.class)
    public static class FoxGroupDataMixin implements FoxGroupDataExtension {
        @Unique
        private Holder<FoxVariant> variantHolder;
        @Override
        public void setVariant(Holder<FoxVariant> variant) {
            this.variantHolder = variant;
        }

        @Override
        public Holder<FoxVariant> getVariant() {
            return this.variantHolder;
        }
    }
}
