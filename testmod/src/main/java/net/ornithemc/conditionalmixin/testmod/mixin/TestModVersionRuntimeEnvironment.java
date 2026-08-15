package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;

import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.Mod;
import net.ornithemc.conditionalmixin.annotations.RuntimeEnvironment;
import net.ornithemc.conditionalmixin.annotations.Version;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(modLoaded = @Mod(value = "testmod", version = @Version(value = "1.0.0", runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)))
@Mixin(Minecraft.class)
public class TestModVersionRuntimeEnvironment {

	@Conditional(modAbsent = @Mod(value = "testmod", version = @Version(value = "9999.99.99", runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)))
	@Shadow
	private static Minecraft instance;

	@Conditional(modLoaded = @Mod(value = "testmod", version = @Version(value = "9999.99.99", runtimeEnvironment = RuntimeEnvironment.PRODUCTION)))
	@Shadow
	private static boolean ON_OSX;

	@Conditional(modLoaded = @Mod("testmod"))
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit(CallbackInfo ci) {
		boolean success = true;

		try {
			@SuppressWarnings("unused")
			Minecraft mc = instance;
			@SuppressWarnings("unused")
			boolean bl = ON_OSX;
		} catch (Throwable t) {
			success = false;
		}

		if (success) {
			TestMod.success(Test.MOD_VERSION_RUNTIME_ENVIRONMENT);
		} else {
			TestMod.failure(Test.MOD_VERSION_RUNTIME_ENVIRONMENT);
		}
	}

	@Conditional(modAbsent = @Mod(value = "testmod", version = @Version(value = "1.0.0", runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)))
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			TestMod.failure(Test.MOD_VERSION_RUNTIME_ENVIRONMENT);
		}
	}
}
