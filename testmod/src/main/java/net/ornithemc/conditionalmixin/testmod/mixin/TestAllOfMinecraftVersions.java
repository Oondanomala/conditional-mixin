package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;

import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.Version;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(minecraftVersion = { @Version("1.14.4"), @Version("1.14.4") })
@Mixin(Minecraft.class)
public class TestAllOfMinecraftVersions {

	@Conditional(minecraftVersion = { @Version("1.14.4"), @Version("<1.15") })
	@Shadow
	private static Minecraft instance;

	@Conditional(minecraftVersion = { @Version("1.14.4"), @Version(">1.14.4") })
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(minecraftVersion = { @Version("1.14.4"), @Version("<1.15") })
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
			TestMod.success(Test.ALL_OF_MINECRAFT_VERSIONS);
		} else {
			TestMod.failure(Test.ALL_OF_MINECRAFT_VERSIONS);
		}
	}

	@Conditional(minecraftVersion = { @Version(">1.14.4"), @Version("<1.14.4") })
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		TestMod.failure(Test.ALL_OF_MINECRAFT_VERSIONS);
	}
}
