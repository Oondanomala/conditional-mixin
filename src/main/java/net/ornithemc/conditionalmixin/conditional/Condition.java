package net.ornithemc.conditionalmixin.conditional;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import org.spongepowered.asm.util.Annotations;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.ornithemc.conditionalmixin.annotations.Conditional;
import net.ornithemc.conditionalmixin.annotations.GameSide;
import net.ornithemc.conditionalmixin.annotations.RuntimeEnvironment;

public final class Condition {

	public static final String DESCRIPTOR = Type.getDescriptor(Conditional.class);

	public static Condition parse(AnnotationNode condition) {
		List<String> minecraftVersion = Annotations.getValue(condition, "minecraftVersion", Collections.emptyList());
		GameSide gameSide = Annotations.getValue(condition, "gameSide", GameSide.class, GameSide.ANY);
		RuntimeEnvironment runtimeEnvironment = Annotations.getValue(condition, "runtimeEnvironment", RuntimeEnvironment.class, RuntimeEnvironment.ANY);
		ModCondition[] modsLoaded = ModCondition.getModsLoaded(condition);
		ModCondition[] modsAbsent = ModCondition.getModsAbsent(condition);

		VersionPredicate[] predicates = new VersionPredicate[minecraftVersion.size()];

		for (int i = 0; i < predicates.length; i++) {
			String predicate = minecraftVersion.get(i);

			try {
				predicates[i] = MinecraftVersionPredicateParser.parse(predicate);
			} catch (VersionParsingException e) {
				throw new InvalidConditionException("Invalid Minecraft version predicate '" + predicate + "'", e);
			}
		}

		return new Condition(CompoundVersionPredicate.or(predicates), gameSide, runtimeEnvironment, modsLoaded, modsAbsent);
	}

	private static final Version MINECRAFT_VERSION = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();

	private final VersionPredicate minecraftVersions;
	private final GameSide gameSide;
	private final RuntimeEnvironment runtimeEnvironment;
	private final ModCondition[] modsLoaded;
	private final ModCondition[] modsAbsent;

	private Condition(VersionPredicate minecraftVersions, GameSide gameSide, RuntimeEnvironment runtimeEnvironment, ModCondition[] modsLoaded, ModCondition[] modsAbsent) {
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

		if (!this.minecraftVersions.test(MINECRAFT_VERSION)) {
			return false;
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
