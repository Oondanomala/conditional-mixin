package net.ornithemc.conditionalmixin.conditional;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.tree.AnnotationNode;

import org.spongepowered.asm.util.Annotations;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;

import net.ornithemc.conditionalmixin.annotations.GameSide;
import net.ornithemc.conditionalmixin.annotations.RuntimeEnvironment;

public final class VersionCondition {

	public static VersionCondition[] getMinecraftVersions(AnnotationNode parent) {
		return parseList(parent, "minecraftVersion", true);
	}

	public static VersionCondition[] getModVersions(AnnotationNode parent) {
		return parseList(parent, "version", false);
	}

	private static VersionCondition[] parseList(AnnotationNode parent, String key, boolean parseAsMinecraftVersion) {
		List<AnnotationNode> annotations = Annotations.getValue(parent, key);

		if (annotations == null || annotations.isEmpty()) {
			return new VersionCondition[0];
		}

		VersionCondition[] versions = new VersionCondition[annotations.size()];

		for (int i = 0; i < versions.length; i++) {
			versions[i] = parse(annotations.get(i), parseAsMinecraftVersion);
		}

		return versions;
	}

	private static VersionCondition parse(AnnotationNode versionCondition, boolean parseAsMinecraftVersion) {
		List<String> value = Annotations.getValue(versionCondition, "value", Collections.emptyList());
		GameSide gameSide = Annotations.getValue(versionCondition, "gameSide", GameSide.class, GameSide.ANY);
		RuntimeEnvironment runtimeEnvironment = Annotations.getValue(versionCondition, "runtimeEnvironment", RuntimeEnvironment.class, RuntimeEnvironment.ANY);

		if (value.isEmpty()) {
			throw new InvalidConditionException("Empty version predicate!");
		}

		VersionPredicate[] predicates = new VersionPredicate[value.size()];

		for (int i = 0; i < predicates.length; i++) {
			String predicate = value.get(i);

			try {
				if (parseAsMinecraftVersion) {
					predicates[i] = MinecraftVersionPredicateParser.parse(predicate);
				} else {
					predicates[i] = VersionPredicateParser.parse(predicate);
				}
			} catch (VersionParsingException e) {
				throw new InvalidConditionException("Invalid " + (parseAsMinecraftVersion ? "Minecraft" : "mod") + " version predicate '" + predicate + "'", e);
			}
		}

		return new VersionCondition(CompoundVersionPredicate.or(predicates), gameSide, runtimeEnvironment);
	}

	private final VersionPredicate version;
	private final GameSide gameSide;
	private final RuntimeEnvironment runtimeEnvironment;

	private VersionCondition(VersionPredicate version, GameSide gameSide, RuntimeEnvironment runtimeEnvironment) {
		this.version = version;
		this.gameSide = gameSide;
		this.runtimeEnvironment = runtimeEnvironment;
	}

	public boolean test(Version version) {
		if (Condition.checkGameSide(this.gameSide) && Condition.checkRuntimeEnvironment(this.runtimeEnvironment)) {
			return this.version.test(version);
		}

		return true;
	}
}
