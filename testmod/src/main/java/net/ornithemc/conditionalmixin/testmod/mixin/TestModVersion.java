package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;

import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.Mod;
import net.ornithemc.conditionalmixin.annotations.Version;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(modLoaded = @Mod(value = "testmod", version = @Version("1.0.0")))
@Mixin(Minecraft.class)
public class TestModVersion {

	@Conditional(modAbsent = @Mod(value = "testmod", version = @Version(">99.99.99")))
	@Shadow
	private static Minecraft instance;

	@Conditional(modLoaded = @Mod(value = "testmod", version = @Version("99.99.99")))
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(modLoaded = @Mod(value = "testmod", version = @Version("=1.x")))
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
			boolean b = thisfielddoesnotexist;

			success = false;
		} catch (Throwable t) {
		}

		if (success) {
			TestMod.success(Test.MOD_VERSION);
		} else {
			TestMod.failure(Test.MOD_VERSION);
		}
	}

	@Conditional(modAbsent = @Mod(value = "testmod", version = @Version(">0.1.0")))
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		TestMod.failure(Test.MOD_VERSION);
	}
}
