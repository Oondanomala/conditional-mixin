package net.ornithemc.conditionalmixin.conditional;

import java.util.Collection;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;

public abstract class CompoundVersionPredicate implements VersionPredicate {

	public static VersionPredicate or(VersionPredicate[] predicates) {
		return predicates.length == 0 ? VersionPredicateParser.getAny() : new Or(predicates);
	}

	final VersionPredicate[] predicates;

	CompoundVersionPredicate(VersionPredicate[] predicates) {
		this.predicates = predicates;
	}

	@Override
	public Collection<? extends PredicateTerm> getTerms() {
		throw new UnsupportedOperationException();
	}

	@Override
	public VersionInterval getInterval() {
		throw new UnsupportedOperationException();
	}

	private static class Or extends CompoundVersionPredicate {

		private Or(VersionPredicate[] predicates) {
			super(predicates);
		}

		@Override
		public boolean test(Version t) {
			for (VersionPredicate predicate : this.predicates) {
				if (predicate.test(t)) {
					return true;
				}
			}

			return false;
		}
	}
}
