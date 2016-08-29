package com.forgeessentials.client.gui.forge;

import com.forgeessentials.client.core.ClientProxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class FEConfigGUI extends GuiConfig {

	public FEConfigGUI(GuiScreen parentScreen) {
		super(parentScreen,
				new ConfigElement<Object>(ClientProxy.getConfig().getCategory(Configuration.CATEGORY_GENERAL))
						.getChildElements(),
				"TestMod", false, false, "FE Client Addon Config");
	}

}
