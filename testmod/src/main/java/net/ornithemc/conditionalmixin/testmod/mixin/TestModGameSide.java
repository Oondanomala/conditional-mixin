package net.ornithemc.conditionalmixin.testmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.GameSide;
import net.ornithemc.conditionalmixin.annotations.Mod;
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(modLoaded = @Mod(value = "testmod", gameSide = GameSide.CLIENT))
@Mixin(Minecraft.class)
public class TestModGameSide {

	@Conditional(modAbsent = @Mod(value = "thismoddoesnotexist", gameSide = GameSide.CLIENT))
	@Shadow
	private static Minecraft instance;

	@Conditional(modLoaded = @Mod(value = "thismoddoesnotexist", gameSide = GameSide.SERVER))
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
			TestMod.success(Test.MOD_GAME_SIDE);
		} else {
			TestMod.failure(Test.MOD_GAME_SIDE);
		}
	}

	@Conditional(modAbsent = @Mod(value = "testmod", gameSide = GameSide.CLIENT))
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			TestMod.failure(Test.MOD_GAME_SIDE);
		}
	}
}
