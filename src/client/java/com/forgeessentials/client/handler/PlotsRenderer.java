package com.forgeessentials.client.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.commons.network.Packet4PlotsUpdate;
import com.forgeessentials.commons.network.Packet6SyncPlots;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
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

	public static Map<Integer, List<WorldArea>> plots = new HashMap<Integer, List<WorldArea>>();

	/**
	 * must be translated to proper point before calling
	 */
	private static void renderBox() {

		WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		// FRONT
		renderer.pos(-0.5, -0.5, -0.5).endVertex();
		renderer.pos(-0.5, 0.5, -0.5).endVertex();

		renderer.pos(-0.5, 0.5, -0.5).endVertex();
		renderer.pos(0.5, 0.5, -0.5).endVertex();

		renderer.pos(0.5, 0.5, -0.5).endVertex();
		renderer.pos(0.5, -0.5, -0.5).endVertex();

		renderer.pos(0.5, -0.5, -0.5).endVertex();
		renderer.pos(-0.5, -0.5, -0.5).endVertex();

		// BACK
		renderer.pos(-0.5, -0.5, 0.5).endVertex();
		renderer.pos(-0.5, 0.5, 0.5).endVertex();

		renderer.pos(-0.5, 0.5, 0.5).endVertex();
		renderer.pos(0.5, 0.5, 0.5).endVertex();

		renderer.pos(0.5, 0.5, 0.5).endVertex();
		renderer.pos(0.5, -0.5, 0.5).endVertex();

		renderer.pos(0.5, -0.5, 0.5).endVertex();
		renderer.pos(-0.5, -0.5, 0.5).endVertex();

		// betweens.
		renderer.pos(0.5, 0.5, -0.5).endVertex();
		renderer.pos(0.5, 0.5, 0.5).endVertex();

		renderer.pos(0.5, -0.5, -0.5).endVertex();
		renderer.pos(0.5, -0.5, 0.5).endVertex();

		renderer.pos(-0.5, -0.5, -0.5).endVertex();
		renderer.pos(-0.5, -0.5, 0.5).endVertex();

		renderer.pos(-0.5, 0.5, -0.5).endVertex();
		renderer.pos(-0.5, 0.5, 0.5).endVertex();

		Tessellator.getInstance().draw();

	}

	@SubscribeEvent
	public void connectionOpened(ClientConnectedToServerEvent e) {
		plots = new HashMap<Integer, List<WorldArea>>();
		if (ForgeEssentialsClient.serverHasFE()) {
			System.out.println("Requesting plots");
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
					ArrayList<WorldArea> temp = new ArrayList<WorldArea>();
					temp.add(message.getArea());
					plots.put(message.getOwnership(), temp);
				}
			} else {
				for(Integer key : plots.keySet()){
					for (WorldArea plot : plots.get(key)) {
						if (plot.getCenter().equals(message.getArea().getCenter())) {
							plots.get(key).remove(plot);
							System.out.println("Deleted plot registry " + message.getArea());
							break;
						}
					}
				}
			}
		} else {
			System.out.println("Plots map is null?");
		}
		return null;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
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
		GL11.glPushMatrix();
		GL11.glTranslated(-renderPosX + 0.5, -renderPosY + 0.5, -renderPosZ + 0.5);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glLineWidth(2);

		boolean seeThrough = false;
		while (true) {
			if (seeThrough) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			} else {
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}

			for (Integer type : plots.keySet()) {
				switch (type) {
				case 0: // ownerless
					if (seeThrough) {
						GL11.glColor4f(1, 1, 1, ALPHA);
					} else {
						GL11.glColor3f(1, 1, 1);
					}
					break;
				case 1: // players
					if (seeThrough) {
						GL11.glColor4f(0, 0.5f, 0.75f, ALPHA);
					} else {
						GL11.glColor3f(0, 0.5f, 0.75f);
					}
					break;
				case 2: // teams
					if (seeThrough) {
						GL11.glColor4f(1, 0, 1, ALPHA);
					} else {
						GL11.glColor3f(1, 0, 1);
					}
					break;
				case 3: // other non team player
					if (seeThrough) {
						GL11.glColor4f(1, 0, 0, ALPHA);
					} else {
						GL11.glColor3f(1, 0, 0);
					}
					break;
				}
				for (WorldArea plot : plots.get(type)) {
					// only render plots in this dimension
					if (plot.getDimension() == FMLClientHandler.instance().getClient().thePlayer.dimension) {
						// render start
						

						Point p1 = plot.getHighPoint();
						Point p2 = plot.getLowPoint();
						Point size = plot.getSize();
						GL11.glPushMatrix();
						GL11.glTranslated((float) (p1.getX() + p2.getX()) / 2, (float) (p1.getY() + p2.getY()) / 2,
								(float) (p1.getZ() + p2.getZ()) / 2);
						GL11.glScalef(1 + size.getX(), 1 + size.getY(), 1 + size.getZ());
						renderBox();
						GL11.glPopMatrix();
					}
				}

			}

			if (!seeThrough) {
				break;
			}
			seeThrough = false;
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

}
