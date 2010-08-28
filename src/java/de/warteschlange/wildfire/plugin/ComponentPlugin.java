package de.warteschlange.wildfire.plugin;

import java.io.File;

import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.container.Plugin;
import org.jivesoftware.wildfire.container.PluginManager;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public abstract class ComponentPlugin implements Plugin, Component {

	private ComponentManager componentManager;

//	private SessionManager sessionManager;

	private PluginManager pluginManager;

	protected static final String COMPONENT_ID = "sms";

	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		// Register as a component.
		pluginManager = manager;
		componentManager = ComponentManagerFactory.getComponentManager();
//		sessionManager = SessionManager.getInstance();
	}

	public void destroyPlugin() {
		// Unregister component.
		try {
			componentManager.removeComponent(COMPONENT_ID);

		} catch (Exception e) {
			// componentManager.getLog().error(e);
			Log.error(e);
		}
		componentManager = null;
	}

	// /////////////////////////////////////////////////////////////////////
	// Component Stuff
	public void initialize(JID jid, ComponentManager componentManager) {
	}

	public void start() {
	}

	public void shutdown() {
	}

	public String getName() {
		// Get the name from the plugin.xml file.
		return pluginManager.getName(this);
	}

	public String getDescription() {
		// Get the description from the plugin.xml file.
		return pluginManager.getDescription(this);
	}

	public abstract void processPacket(Packet packet);

	// //////////////////////////////////////////////////////////////////
	protected String getPluginName() {
		return COMPONENT_ID + "." + componentManager.getServerName();
	}

	protected void sendPacket(Packet packet) {
		try {
			componentManager.sendPacket(this, packet);
		} catch (ComponentException e) {
			componentManager.getLog().error(e);
		}
	}

	//public static Log getLog() {
		//return componentManager.getLog();
	//	return Log;
	//}
}
