package net.ornithemc.conditionalmixin.conditional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.tree.AnnotationNode;

import org.spongepowered.asm.util.Annotations;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import net.ornithemc.conditionalmixin.annotations.GameSide;
import net.ornithemc.conditionalmixin.annotations.RuntimeEnvironment;

public final class ModCondition {

	public static ModCondition[] getModsLoaded(AnnotationNode parent) {
		return parseList(parent, "modLoaded", false);
	}

	public static ModCondition[] getModsAbsent(AnnotationNode parent) {
		return parseList(parent, "modAbsent", true);
	}

	private static ModCondition[] parseList(AnnotationNode parent, String key, boolean inverted) {
		List<AnnotationNode> annotations = Annotations.getValue(parent, key);

		if (annotations == null || annotations.isEmpty()) {
			return new ModCondition[0];
		}

		ModCondition[] mods = new ModCondition[annotations.size()];

		for (int i = 0; i < mods.length; i++) {
			mods[i] = parse(annotations.get(i), inverted);
		}

		return mods;
	}

	private static ModCondition parse(AnnotationNode modCondition, boolean inverted) {
		List<String> value = Annotations.getValue(modCondition, "value", Collections.emptyList());
		VersionCondition[] version = VersionCondition.getModVersions(modCondition);
		GameSide gameSide = Annotations.getValue(modCondition, "gameSide", GameSide.class, GameSide.ANY);
		RuntimeEnvironment runtimeEnvironment = Annotations.getValue(modCondition, "runtimeEnvironment", RuntimeEnvironment.class, RuntimeEnvironment.ANY);

		if (value.isEmpty()) {
			throw new InvalidConditionException("Empty mod ID");
		}

		if (value.size() > 1) {
			if (version.length > 0) {
				throw new InvalidConditionException("Mod version predicate specified with multiple mod IDs");
			}

			return new ModCondition(value.toArray(new String[0]), gameSide, runtimeEnvironment, inverted);
		} else {
			return new ModCondition(value.get(0), version, gameSide, runtimeEnvironment, inverted);
		}
	}

	private final String[] ids;
	private final VersionCondition[] versions;
	private final GameSide gameSide;
	private final RuntimeEnvironment runtimeEnvironment;
	private final boolean inverted;

	private ModCondition(String id, VersionCondition[] versions, GameSide gameSide, RuntimeEnvironment runtimeEnvironment, boolean inverted) {
		this.ids = new String[] { id };
		this.versions = versions;
		this.gameSide = gameSide;
		this.runtimeEnvironment = runtimeEnvironment;
		this.inverted = inverted;
	}

	private ModCondition(String[] ids, GameSide gameSide, RuntimeEnvironment runtimeEnvironment, boolean inverted) {
		this.ids = ids;
		this.versions = new VersionCondition[0];
		this.gameSide = gameSide;
		this.runtimeEnvironment = runtimeEnvironment;
		this.inverted = inverted;
	}

	public boolean test() {
		if (Condition.checkGameSide(this.gameSide) && Condition.checkRuntimeEnvironment(this.runtimeEnvironment)) {
			for (String id : this.ids) {
				Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(id);
				Optional<Boolean> present = mod
						.map(ModContainer::getMetadata)
						.map(ModMetadata::getVersion)
						.map(modVersion -> {
							for (VersionCondition version : this.versions) {
								if (!version.test(modVersion)) {
									return false;
								}
							}

							return true;
						});

				if (this.inverted != present.orElse(false)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}
}
