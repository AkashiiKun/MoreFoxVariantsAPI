package io.github.akashiikun.mfvapi.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class MultiversionHelper {
	public static Identifier toIdentifier(ResourceKey<?> key) {
		return key.identifier();
	}
}