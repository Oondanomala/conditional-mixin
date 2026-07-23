package net.ornithemc.conditionalmixin.mixin;

import org.objectweb.asm.tree.ClassNode;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.llamalad7.mixinextras.utils.MixinInternals;

public final class ConditionalMixinExtension implements IExtension {

	private final ConditionalMixinTransformer transformer;

	public ConditionalMixinExtension(ConditionalMixinTransformer transformer) {
		this.transformer = transformer;
	}

	@Override
	public boolean checkActive(MixinEnvironment environment) {
		return true;
	}

	@Override
	public void preApply(ITargetClassContext context) {
		for (Pair<IMixinInfo, ClassNode> pair : MixinInternals.getMixinsFor(context)) {
			this.transformer.transform(pair.getRight());
		}
	}

	@Override
	public void postApply(ITargetClassContext context) {
	}

	@Override
	public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {
	}
}
