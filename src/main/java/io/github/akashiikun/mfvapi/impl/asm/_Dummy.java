package io.github.akashiikun.mfvapi.impl.asm;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.storage.ValueInput;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * Crossplatform way of getting the names of classes and methods, since NeoForge has no API for looking up mappings.
 * When using Arch Loom with non-Mojmap mappings, NeoForge may use mappings other than Mojang's official ones.
 */
@SuppressWarnings({"DataFlowIssue", "NullableProblems", "unused"})
public class _Dummy extends Fox {
	@Retention(RetentionPolicy.CLASS)
	@interface Name {
		String value();
	}

	public _Dummy() {
		super(null, null);
	}

	@Name("readAdditionalSaveData")
	@Override
	public native void readAdditionalSaveData(ValueInput input);

	@Name("getBreedOffspring")
	@Override
	public native @Nullable Fox getBreedOffspring(ServerLevel level, AgeableMob otherParent);

	@Name("setPersistenceRequired")
	@Override
	public native void setPersistenceRequired();

	@Name("Fox$Variant")
	public static native Fox.Variant _1();

	@Name("RandomSource")
	public static native RandomSource _2();
}