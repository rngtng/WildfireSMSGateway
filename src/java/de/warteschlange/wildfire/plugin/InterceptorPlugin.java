package de.warteschlange.wildfire.plugin;

import java.io.File;

import org.jivesoftware.wildfire.PacketRouter;
import org.jivesoftware.wildfire.session.Session;
import org.jivesoftware.wildfire.XMPPServer;
import org.jivesoftware.wildfire.container.Plugin;
import org.jivesoftware.wildfire.container.PluginManager;
import org.jivesoftware.wildfire.interceptor.InterceptorManager;
import org.jivesoftware.wildfire.interceptor.PacketInterceptor;
import org.jivesoftware.wildfire.interceptor.PacketRejectedException;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

public abstract class InterceptorPlugin implements Plugin, PacketInterceptor {

	// OLD:private static final String JABBER_PREFIX = "jabber.";

	/**
	 * the hook into the inteceptor chain
	 */
	private InterceptorManager interceptorManager;

	/**
	 * used to send violation notifications
	 */
	private PacketRouter packetRouter;

	public InterceptorPlugin() {
		interceptorManager = InterceptorManager.getInstance();
		packetRouter = XMPPServer.getInstance().getPacketRouter();
	}

	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		// register with interceptor manager
		interceptorManager.addInterceptor(this);
	}

	public void destroyPlugin() {
		// unregister with interceptor manager
		interceptorManager.removeInterceptor(this);
	}

	// ////////////////////////////////////////////////////////////////
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {

		/*
		 * reroute incoming if (!processed && incoming && packet.getTo() != null &&
		 * packet.getTo().getDomain().equals( JABBER_PREFIX + getPluginName() )) {
		 * Log.info("ROUTE IN"); JID newTo = new
		 * JID(packet.getTo().toString().replace( JABBER_PREFIX, ""));
		 * packet.setTo(newTo); }
		 *  // reroute outgoing // if (packet.getTo() != null) { // String res =
		 * packet.getTo().getResource(); // if (!processed && !incoming && res !=
		 * null // && res.endsWith(JABBER_PREFIX)) { if (!processed && !incoming &&
		 * packet.getFrom() != null) { Log.info("ROUTE OUT: "); // JID to =
		 * packet.getTo(); // JID newTo = new JID(to.getNode(), to.getDomain(),
		 * to // .getResource().replace(JABBER_PREFIX, "")); //
		 * packet.setTo(newTo); String from = packet.getFrom().toString(); from =
		 * from.replace("@", "@" + JABBER_PREFIX); if (!from.contains("@")) {
		 * from = JABBER_PREFIX + from; } JID newFrom = new JID(from);
		 * packet.setFrom(newFrom); // Log.info(packet.toString()); // return; // } }
		 * 
		 * if (incoming && !processed) { // Log.info("IN: " +
		 * packet.toString()); } if (!incoming && !processed) { Log.info("OUT: " +
		 * packet.toString()); }
		 */

		// //////////////////////////////////////////////////////////////////
		if (processed
				|| !incoming
				|| (packet.getTo() != null && !packet.getTo().getDomain()
						.equals(getPluginName())))
			return;

		if (packet instanceof Message) {
			processPacket(packet);
			throw new PacketRejectedException();
		}
		if (packet instanceof Presence) {
			processPacket(packet);
			throw new PacketRejectedException();
		}

	}

	public abstract void processPacket(Packet packet);

	// //////////////////////////////////////////////////////////////////
	protected String getPluginName() {
		return XMPPServer.getInstance().getServerInfo().getName();
	}

	protected void sendPacket(Packet packet) {
		packetRouter.route(packet);
	}
}
