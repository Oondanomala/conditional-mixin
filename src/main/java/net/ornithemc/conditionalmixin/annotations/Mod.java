package net.ornithemc.conditionalmixin.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a mod condition used in a {@linkplain Conditional}. The requirement
 * of this mod condition depends on its usage. In
 * {@linkplain Conditional#modLoaded()} this mod condition requires the
 * specified mod(s) to be loaded. {@linkplain Conditional#modAbsent()} this mod
 * condition requires the specified mod(s) to be absent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Mod {

	/**
	 * Specifies one or more mod IDs for this mod condition. If any one of the
	 * specified mods satisfies the requirement, this mod condition is met.
	 * 
	 * @return the mod IDs for this mod condition.
	 */
	String[] value();

	/**
	 * Specifies one or more mod version predicates for this mod condition. If any
	 * one of the specified predicates matches the actually loaded mod version, this
	 * mod condition is met. Leaving this field empty is equivalent to setting the
	 * value to {@code version = "*"}, i.e. allowing all mod versions.
	 * 
	 * <p>
	 * This field must be left empty if more than one mod ID is specified.
	 * 
	 * @return the mod version predicates for this mod condition.
	 */
	String[] version() default {};

	/**
	 * Specifies the game side in which this mod condition must be met. If the game
	 * side does not match, this mod condition will always evaluate to {@code true}.
	 * 
	 * @return the game side in which this condition must be met.
	 */
	GameSide gameSide() default GameSide.ANY;

	/**
	 * Specifies the runtime environment in which this mod condition must be met. If
	 * the runtime environment does not match, this mod condition will always
	 * evaluate to {@code true}.
	 * 
	 * @return the runtime environment in which this condition must be met.
	 */
	RuntimeEnvironment runtimeEnvironment() default RuntimeEnvironment.ANY;

}
