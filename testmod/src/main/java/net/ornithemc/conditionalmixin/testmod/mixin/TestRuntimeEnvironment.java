package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.RuntimeEnvironment;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)
@Mixin(Minecraft.class)
public class TestRuntimeEnvironment {

	@Conditional(runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)
	@Shadow
	private static Minecraft instance;

	@Conditional(runtimeEnvironment = RuntimeEnvironment.PRODUCTION)
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit(CallbackInfo ci) {
		boolean success = true;

		try {
			@SuppressWarnings("unused")
			Minecraft mc = instance;
		} catch (Throwable t) {
			success = false;
		}

		try {
			@SuppressWarnings("unused")
			boolean bl = thisfielddoesnotexist;

			success = false;
		} catch (Throwable t) {
		}

		if (success) {
			TestMod.success(Test.RUNTIME_ENVIRONMENT);
		} else {
			TestMod.failure(Test.RUNTIME_ENVIRONMENT);
		}
	}

	@Conditional(runtimeEnvironment = RuntimeEnvironment.PRODUCTION)
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			TestMod.failure(Test.RUNTIME_ENVIRONMENT);
		}
	}
}
