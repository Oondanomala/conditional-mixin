package net.ornithemc.conditionalmixin.conditional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.tree.AnnotationNode;

import org.spongepowered.asm.util.Annotations;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;
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

	public static ModCondition parse(AnnotationNode modCondition) {
		return parse(modCondition, false);
	}

	private static ModCondition parse(AnnotationNode modCondition, boolean inverted) {
		List<String> value = Annotations.getValue(modCondition, "value", Collections.emptyList());
		List<String> version = Annotations.getValue(modCondition, "version", Collections.emptyList());
		GameSide gameSide = Annotations.getValue(modCondition, "gameSide", GameSide.class, GameSide.ANY);
		RuntimeEnvironment runtimeEnvironment = Annotations.getValue(modCondition, "runtimeEnvironment", RuntimeEnvironment.class, RuntimeEnvironment.ANY);

		if (value.isEmpty()) {
			throw new InvalidConditionException("Empty mod ID");
		}

		if (value.size() > 1) {
			if (!version.isEmpty()) {
				throw new InvalidConditionException("Mod version predicate specified with multiple mod IDs");
			}

			return new ModCondition(value.toArray(new String[0]), gameSide, runtimeEnvironment, inverted);
		} else {
			VersionPredicate[] predicates = new VersionPredicate[version.size()];

			for (int i = 0; i < predicates.length; i++) {
				String predicate = version.get(i);

				try {
					predicates[i] = VersionPredicateParser.parse(predicate);
				} catch (VersionParsingException e) {
					throw new InvalidConditionException("Invalid Minecraft version predicate '" + predicate + "'", e);
				}
			}

			return new ModCondition(value.get(0), CompoundVersionPredicate.or(predicates), gameSide, runtimeEnvironment, inverted);
		}
	}

	private final String[] ids;
	private final VersionPredicate version;
	private final GameSide gameSide;
	private final RuntimeEnvironment runtimeEnvironment;
	private final boolean inverted;

	private ModCondition(String id, VersionPredicate version, GameSide gameSide, RuntimeEnvironment runtimeEnvironment, boolean inverted) {
		this.ids = new String[] { id };
		this.version = version;
		this.gameSide = gameSide;
		this.runtimeEnvironment = runtimeEnvironment;
		this.inverted = inverted;
	}

	private ModCondition(String[] ids, GameSide gameSide, RuntimeEnvironment runtimeEnvironment, boolean inverted) {
		this.ids = ids;
		this.version = VersionPredicateParser.getAny();
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
						.map(this.version::test);

				if (this.inverted != present.orElse(false)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}
}
