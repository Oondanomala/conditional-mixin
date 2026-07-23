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

@Conditional(minecraftVersion = ">19w11a")
@Mixin(Minecraft.class)
public class TestMinecraftVersionFriendlyString {

	@Conditional(minecraftVersion = "<1.15-pre1")
	@Shadow
	private static Minecraft instance;

	@Conditional(minecraftVersion = "<1.0.0-rc1")
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(minecraftVersion = ">=b1.7.3")
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
			TestMod.success(Test.MINECRAFT_VERSION_FRIENDLY_STRING);
		} else {
			TestMod.failure(Test.MINECRAFT_VERSION_FRIENDLY_STRING);
		}
	}

	@Conditional(minecraftVersion = "<c0.0.11a-launcher")
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		TestMod.failure(Test.MINECRAFT_VERSION_FRIENDLY_STRING);
	}
}
