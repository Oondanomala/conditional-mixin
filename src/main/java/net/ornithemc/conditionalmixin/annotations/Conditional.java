package net.ornithemc.conditionalmixin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies that a Mixin class, field, or method should only
 * apply if certain conditions are met. This annotation can be use on classes,
 * fields, and methods. If the conditional on a Mixin class fails, the entire
 * Mixin will be discarded, regardless of the conditionals on any fields or
 * methods contained within.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface Conditional {

	/**
	 * Specifies one or more Minecraft version predicates for this condition. If any
	 * one of the specified predicates matches the runtime Minecraft version, this
	 * condition is met. Leaving this field empty is equivalent to setting the value
	 * to {@code minecraftVersion = "*"}, i.e. allowing all Minecraft versions.
	 * 
	 * @return the Minecraft version predicates for this condition.
	 */
	String[] minecraftVersion() default {};

	/**
	 * Specifies the required game side for this condition to be met.
	 * 
	 * @return the required game side.
	 */
	GameSide gameSide() default GameSide.ANY;

	/**
	 * Specifies the required runtime environment for this condition to be met.
	 * 
	 * @return the required runtime environment.
	 */
	RuntimeEnvironment runtimeEnvironment() default RuntimeEnvironment.ANY;

	/**
	 * Specifies one or {@linkplain Mod} conditions for mods that are required to be
	 * loaded for this condition to be met. All of the specified mod conditions must
	 * be met.
	 * 
	 * @return the {@linkplain Mod} conditions for required loaded mods.
	 */
	Mod[] modLoaded() default {};

	/**
	 * Specifies one or {@linkplain Mod} conditions for mods that are required to be
	 * absent for this condition to be met. All of the specified mod conditions must
	 * be met.
	 * 
	 * @return the {@linkplain Mod} conditions for required absent mods.
	 */
	Mod[] modAbsent() default {};

}
