package net.ornithemc.conditionalmixin.conditional;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import org.spongepowered.asm.util.Annotations;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.GameSide;
import net.ornithemc.conditionalmixin.annotations.RuntimeEnvironment;

public final class Condition {

	public static final String DESCRIPTOR = Type.getDescriptor(Conditional.class);

	public static Condition parse(AnnotationNode condition) {
		VersionCondition[] minecraftVersion = VersionCondition.getMinecraftVersions(condition);
		GameSide gameSide = Annotations.getValue(condition, "gameSide", GameSide.class, GameSide.ANY);
		RuntimeEnvironment runtimeEnvironment = Annotations.getValue(condition, "runtimeEnvironment", RuntimeEnvironment.class, RuntimeEnvironment.ANY);
		ModCondition[] modsLoaded = ModCondition.getModsLoaded(condition);
		ModCondition[] modsAbsent = ModCondition.getModsAbsent(condition);

		return new Condition(minecraftVersion, gameSide, runtimeEnvironment, modsLoaded, modsAbsent);
	}

	private static final Version MINECRAFT_VERSION = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();

	private final VersionCondition[] minecraftVersions;
	private final GameSide gameSide;
	private final RuntimeEnvironment runtimeEnvironment;
	private final ModCondition[] modsLoaded;
	private final ModCondition[] modsAbsent;

	private Condition(VersionCondition[] minecraftVersions, GameSide gameSide, RuntimeEnvironment runtimeEnvironment, ModCondition[] modsLoaded, ModCondition[] modsAbsent) {
		this.minecraftVersions = minecraftVersions;
		this.gameSide = gameSide;
		this.runtimeEnvironment = runtimeEnvironment;
		this.modsLoaded = modsLoaded;
		this.modsAbsent = modsAbsent;
	}

	public boolean test() {
		if (!Condition.checkGameSide(this.gameSide)) {
			return false;
		}

		if (!Condition.checkRuntimeEnvironment(this.runtimeEnvironment)) {
			return false;
		}

		for (VersionCondition minecraftVersion : this.minecraftVersions) {
			if (!minecraftVersion.test(MINECRAFT_VERSION)) {
				return false;
			}
		}

		for (ModCondition modLoaded : this.modsLoaded) {
			if (!modLoaded.test()) {
				return false;
			}
		}

		for (ModCondition modAbsent : this.modsAbsent) {
			if (!modAbsent.test()) {
				return false;
			}
		}

		return true;
	}

	public static boolean checkGameSide(GameSide gameSide) {
		if (gameSide != GameSide.ANY) {
			EnvType env = FabricLoader.getInstance().getEnvironmentType();

			if (gameSide == GameSide.CLIENT) {
				return env == EnvType.CLIENT;
			}
			if (gameSide == GameSide.SERVER) {
				return env == EnvType.SERVER;
			}
		}

		return true;
	}

	public static boolean checkRuntimeEnvironment(RuntimeEnvironment runtimeEnvironment) {
		if (runtimeEnvironment != RuntimeEnvironment.ANY) {
			if (runtimeEnvironment == RuntimeEnvironment.DEVELOPMENT) {
				return FabricLoader.getInstance().isDevelopmentEnvironment();
			}
			if (runtimeEnvironment == RuntimeEnvironment.PRODUCTION) {
				return !FabricLoader.getInstance().isDevelopmentEnvironment();
			}
		}

		return true;
	}
}
