package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(minecraftVersion = "1.14.4")
@Mixin(Minecraft.class)
public class TestMinecraftVersion {

	@Conditional(minecraftVersion = "=1.14.x")
	@Shadow
	private static Minecraft instance;

	@Conditional(minecraftVersion = "<1.12-rc.1")
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(minecraftVersion = ">=1.12-alpha.17.18.a")
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
			TestMod.success(Test.MINECRAFT_VERSION);
		} else {
			TestMod.failure(Test.MINECRAFT_VERSION);
		}
	}

	@Conditional(minecraftVersion = "<1.0.0-rc.2")
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		TestMod.failure(Test.MINECRAFT_VERSION);
	}
}
