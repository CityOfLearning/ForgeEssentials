package com.forgeessentials.core.preloader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.forgeessentials.core.preloader.asminjector.ASMClassWriter;
import com.forgeessentials.core.preloader.asminjector.ASMUtil;
import com.forgeessentials.core.preloader.asminjector.ClassInjector;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class EventTransformer implements IClassTransformer {

	public static final boolean isObfuscated = !((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

	private ClassInjector attackEntityFromInjector;

	private ClassInjector fireEventInjector;

	private ClassInjector testInjector;

	public EventTransformer() {
		testInjector = ClassInjector.create("com.forgeessentials.core.preloader.injections.MixinTest", isObfuscated);
		attackEntityFromInjector = ClassInjector.create("com.forgeessentials.core.preloader.injections.MixinEntity",
				isObfuscated);
		fireEventInjector = ClassInjector.create("com.forgeessentials.core.preloader.injections.MixinBlockFire",
				isObfuscated);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ClassNode classNode = ASMUtil.loadClassNode(bytes);
		boolean transformed = false;

		// Apply transformers
		transformed |= testInjector == null ? false : testInjector.inject(classNode);
		transformed |= attackEntityFromInjector.inject(classNode);
		transformed |= fireEventInjector.inject(classNode);

		if (!transformed) {
			return bytes;
		}
		ClassWriter writer = new ASMClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}