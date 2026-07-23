package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.Mod;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(modAbsent = @Mod("thismoddoesnotexist"))
@Mixin(Minecraft.class)
public class TestModAbsent {

	@Conditional(modAbsent = @Mod("thismoddoesnotexist"))
	@Shadow
	private static Minecraft instance;

	@Conditional(modAbsent = @Mod("testmod"))
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(modAbsent = @Mod("thismoddoesnotexit"))
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
			TestMod.success(Test.MOD_ABSENT);
		} else {
			TestMod.failure(Test.MOD_ABSENT);
		}
	}

	@Conditional(modAbsent = @Mod("testmod"))
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		TestMod.failure(Test.MOD_ABSENT);
	}
}
