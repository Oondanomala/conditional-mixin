package net.ornithemc.conditionalmixin.mixin;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import com.llamalad7.mixinextras.utils.MixinInternals;

import net.ornithemc.conditionalmixin.ConditionalMixin;
import net.ornithemc.conditionalmixin.conditional.AnnotationProcessor;

public class ConditionalMixinPlugin implements IMixinConfigPlugin {

	private static ConditionalMixinPlugin instance;

	private AnnotationProcessor processor;

	public ConditionalMixinPlugin() {
		// only one instance needs to exist
		if (instance == null) {
			ConditionalMixin.LOGGER.info("Initializing Conditional Mixin.");

			instance = this;

			this.processor = new AnnotationProcessor();

			ConditionalMixinTransformer transformer = new ConditionalMixinTransformer(this.processor);
			ConditionalMixinExtension extension = new ConditionalMixinExtension(transformer);

			MixinInternals.registerExtension(extension);
		}
	}

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (this.processor != null) {
			try {
				ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);

				if (classNode != null) {
					return !this.processor.shouldRemove(classNode);
				}
			} catch (ClassNotFoundException | IOException e) {
				throw new RuntimeException(e);
			}
		}

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
