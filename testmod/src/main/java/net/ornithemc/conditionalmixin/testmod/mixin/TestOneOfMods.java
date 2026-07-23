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

@Conditional(modLoaded = @Mod({"testmod", "thismoddoesnotexist"}))
@Mixin(Minecraft.class)
public class TestOneOfMods {

	@Conditional(modAbsent = @Mod({"testmod", "thismoddoesnotexist"}))
	@Shadow
	private static Minecraft instance;

	@Conditional(modLoaded = @Mod({"thismoddoesnotexist", "thismodalsodoesnotexist"}))
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(modLoaded = @Mod({"testmod", "minecraft"}))
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
			TestMod.success(Test.ONE_OF_MODS);
		} else {
			TestMod.failure(Test.ONE_OF_MODS);
		}
	}

	@Conditional(modAbsent = @Mod({"testmod", "minecraft"}))
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		TestMod.failure(Test.ONE_OF_MODS);
	}
}
