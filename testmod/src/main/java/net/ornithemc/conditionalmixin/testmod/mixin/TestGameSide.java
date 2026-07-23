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
import net.ornithemc.conditionalmixin.testmod.Test;
import net.ornithemc.conditionalmixin.testmod.TestMod;

@Conditional(gameSide = GameSide.CLIENT)
@Mixin(Minecraft.class)
public class TestGameSide {

	@Conditional(gameSide = GameSide.CLIENT)
	@Shadow
	private static Minecraft instance;

	@Conditional(gameSide = GameSide.SERVER)
	@Shadow
	private static boolean thisfielddoesnotexist;

	@Conditional(gameSide = GameSide.CLIENT)
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
			TestMod.success(Test.GAME_SIDE);
		} else {
			TestMod.failure(Test.GAME_SIDE);
		}
	}

	@Conditional(gameSide = GameSide.SERVER)
	@Inject(method = "<clinit>", at = @At("HEAD"))
	private static void clinit2(CallbackInfo ci) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			TestMod.failure(Test.GAME_SIDE);
		}
	}
}
