package com.forgeessentials.client.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet4PlotsUpdate;
import com.forgeessentials.commons.network.Packet6SyncPlots;
import com.forgeessentials.commons.selections.PlotArea;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class PlotsRenderer implements IMessageHandler<Packet4PlotsUpdate, IMessage> {

	private static final float ALPHA = .25f;

	public static Map<Integer, List<PlotArea>> plots = new HashMap<>();

	private static void drawName(String name, WorldRenderer wr, FontRenderer fr) {
		float textSize = 1;

		GlStateManager.disableTexture2D();
		String symbol = name.substring(0, 1);
		int s = fr.getStringWidth(symbol) / 2;
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		GlStateManager.color(1, 1, 1, 0.47058824F);
		wr.pos(-5.0, -9.0, 0.0).endVertex();
		wr.pos(-5.0, 0.0, 0.0).endVertex();
		wr.pos(4.0, 0.0, 0.0).endVertex();
		wr.pos(4.0, -9.0, 0.0).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture2D();
		fr.drawString(symbol, -s, -8, 553648127);
		fr.drawString(symbol, -s, -8, -1);
		if (Minecraft.getMinecraft().isUnicode()) {
			textSize *= 1.5f;
		}
		GlStateManager.translate(0.0f, 1.0f, 0.0f);
		GlStateManager.scale(textSize / 2.0f, textSize / 2.0f, 1.0f);

		int t = fr.getStringWidth(name) / 2;
		GlStateManager.disableTexture2D();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		GlStateManager.color(0, 0, 0, 0.27450982F);
		wr.pos(-t - 1.0, 0.0, 0.0).endVertex();
		wr.pos(-t - 1.0, 9.0, 0.0).endVertex();
		wr.pos(t, 9.0, 0.0).endVertex();
		wr.pos(t, 0.0, 0.0).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture2D();

		fr.drawString(name, -t, 1, 553648127);
		fr.drawString(name, -t, 1, -1);
	}

	/**
	 * must be translated to proper point before calling
	 */
	private static void renderBox(WorldRenderer wr) {

		wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		// FRONT
		wr.pos(-0.5, -0.5, -0.5).endVertex();
		wr.pos(-0.5, 0.5, -0.5).endVertex();

		wr.pos(-0.5, 0.5, -0.5).endVertex();
		wr.pos(0.5, 0.5, -0.5).endVertex();

		wr.pos(0.5, 0.5, -0.5).endVertex();
		wr.pos(0.5, -0.5, -0.5).endVertex();

		wr.pos(0.5, -0.5, -0.5).endVertex();
		wr.pos(-0.5, -0.5, -0.5).endVertex();

		// BACK
		wr.pos(-0.5, -0.5, 0.5).endVertex();
		wr.pos(-0.5, 0.5, 0.5).endVertex();

		wr.pos(-0.5, 0.5, 0.5).endVertex();
		wr.pos(0.5, 0.5, 0.5).endVertex();

		wr.pos(0.5, 0.5, 0.5).endVertex();
		wr.pos(0.5, -0.5, 0.5).endVertex();

		wr.pos(0.5, -0.5, 0.5).endVertex();
		wr.pos(-0.5, -0.5, 0.5).endVertex();

		// betweens.
		wr.pos(0.5, 0.5, -0.5).endVertex();
		wr.pos(0.5, 0.5, 0.5).endVertex();

		wr.pos(0.5, -0.5, -0.5).endVertex();
		wr.pos(0.5, -0.5, 0.5).endVertex();

		wr.pos(-0.5, -0.5, -0.5).endVertex();
		wr.pos(-0.5, -0.5, 0.5).endVertex();

		wr.pos(-0.5, 0.5, -0.5).endVertex();
		wr.pos(-0.5, 0.5, 0.5).endVertex();

		Tessellator.getInstance().draw();
	}

	@SubscribeEvent
	public void connectionOpened(ClientConnectedToServerEvent e) {
		plots = new HashMap<>();
		if (ForgeEssentialsClient.serverHasFE()) {
			NetworkUtils.netHandler.sendToServer(new Packet6SyncPlots());
		}
	}

	@Override
	public IMessage onMessage(Packet4PlotsUpdate message, MessageContext ctx) {
		if (plots != null) {
			if (message.shouldAdd()) {
				if (plots.containsKey(message.getOwnership())) {
					plots.get(message.getOwnership()).add(message.getArea());
				} else {
					ArrayList<PlotArea> temp = new ArrayList<>();
					temp.add(message.getArea());
					plots.put(message.getOwnership(), temp);
				}
			} else {
				for (Integer key : plots.keySet()) {
					for (PlotArea plot : plots.get(key)) {
						if (plot.getCenter().equals(message.getArea().getCenter())) {
							plots.get(key).remove(plot);
							LoggingHandler.felog.info("Deleted plot registry " + message.getArea());
							break;
						}
					}
				}
			}
		} else {
			LoggingHandler.felog.error("Plots map is null?");
		}
		return null;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		FontRenderer fr = rm.getFontRenderer();

		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		if (player == null) {
			return;
		}

		if (plots == null) {
			return;
		}

		double renderPosX = TileEntityRendererDispatcher.staticPlayerX;
		double renderPosY = TileEntityRendererDispatcher.staticPlayerY;
		double renderPosZ = TileEntityRendererDispatcher.staticPlayerZ;
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(-renderPosX + 0.5, -renderPosY + 0.5, -renderPosZ + 0.5);

			GlStateManager.disableTexture2D();
			GlStateManager.enableRescaleNormal();
			GlStateManager.disableLighting();
			GL11.glLineWidth(3);

			boolean seeThrough = false;
			while (true) {
				if (seeThrough) {
					GlStateManager.disableDepth();
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				} else {
					GlStateManager.disableBlend();
					GlStateManager.enableDepth();
				}

				for (Integer type : plots.keySet()) {
					switch (type) {
					case 0: // ownerless
						if (seeThrough) {
							GlStateManager.color(1, 1, 1, ALPHA);
						} else {
							GlStateManager.color(1, 1, 1);
						}
						break;
					case 1: // players
						if (seeThrough) {
							GlStateManager.color(0, 0.5f, 0.75f, ALPHA);
						} else {
							GlStateManager.color(0, 0.5f, 0.75f);
						}
						break;
					case 2: // teams
						if (seeThrough) {
							GlStateManager.color(1, 0, 1, ALPHA);
						} else {
							GlStateManager.color(1, 0, 1);
						}
						break;
					case 3: // other non team player
						if (seeThrough) {
							GlStateManager.color(1, 0, 0, ALPHA);
						} else {
							GlStateManager.color(1, 0, 0);
						}
						break;
					}
					for (PlotArea plot : plots.get(type)) {
						// only render plots in this dimension
						if (plot.getDimension() == FMLClientHandler.instance().getClient().thePlayer.dimension) {
							// render start

							Point p1 = plot.getHighPoint();
							Point p2 = plot.getLowPoint();
							Point size = plot.getSize();
							GlStateManager.pushMatrix();
							{
								GlStateManager.translate((float) (p1.getX() + p2.getX()) / 2,
										(float) (p1.getY() + p2.getY()) / 2, (float) (p1.getZ() + p2.getZ()) / 2);
								GlStateManager.scale(1 + size.getX(), 1 + size.getY(), 1 + size.getZ());
								renderBox(wr);
								GlStateManager.enableTexture2D();
							}
							GlStateManager.popMatrix();
						}
					}

				}

				if (!seeThrough) {
					break;
				}
				seeThrough = false;
			}
			GlStateManager.enableTexture2D();
		}
		GlStateManager.popMatrix();

		for (Integer type : plots.keySet()) {
			for (PlotArea plot : plots.get(type)) {
				Point center = plot.getCenter();
				double d3 = player.lastTickPosX + ((player.posX - player.lastTickPosX) * event.partialTicks);
				double d4 = player.lastTickPosY + ((player.posY - player.lastTickPosY) * event.partialTicks);
				double d5 = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * event.partialTicks);
				float offX = (center.getX() - (float) d3) + 0.5f;
				double offY = (player.posY - (float) d4) + 1.0f;
				float offZ = (center.getZ() - (float) d5) + 0.5f;

				float f = 1.6f;
				float f2 = 0.016666668f * f;
				GlStateManager.pushMatrix();
				{
					GlStateManager.translate(offX, offY, offZ);
					GL11.glNormal3f(0.0f, 1.0f, 0.0f);
					GlStateManager.rotate(-rm.playerViewY, 0.0f, 1.0f, 0.0f);
					GlStateManager.rotate(rm.playerViewX, 1.0f, 0.0f, 0.0f);
					GlStateManager.scale(-f2, -f2, f2);
					GlStateManager.disableLighting();
					GlStateManager.depthMask(false);
					GlStateManager.disableDepth();
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
					GlStateManager.scale(3.0, 3.0, 1.0);
					drawName(plot.getName(), wr, fr);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.enableDepth();
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				}
				GlStateManager.popMatrix();
			}
		}
	}
}
