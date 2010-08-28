package de.warteschlange.wildfire.plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.container.PluginManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * A sample plugin for Wildfire.
 */
public class SMSGatewayPlugin extends InterceptorPlugin {

	// mesage IDs
	private static final int OK = 1;

	private static final int HELP = 2;

	private static final int INFO = 3;

	private static final int ADDNUMBER = 4;

	private static final int WELCOME = 5;

	private static final int BOT_WELCOME = 6;

	private static final int BOT_NONUMBER = 7;

	private static final int BOT_NOTEXT = 8;

	private static final int BOT_CLEAR = 9;

	private static final int WELCOMEUSER = 13;

	private static final int NOCREDITS = 14;

	private static final String BOT_NAME = "sms";

	// ######################### MYSQL Data
	private static final String GET_USERS = "SELECT name, language, number, credits, id "
			+ "FROM smsUser";

	private static final String GET_ALL_CONTACTS = "SELECT number, name, owner FROM smsContacts";

	// TODO do reaular cleanups
	// private static final String DELETE_MESSAGES = "DELETE FROM smsMessages
	// WHERE send < 1 AND time < ?";

	// ###################################################################
	public SMSGatewayPlugin() {
	}

	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		super.initializePlugin(manager, pluginDirectory);
	}

	public void destroyPlugin() {
		super.destroyPlugin();
	}

	// //////////////////////////////////////////////////////////
	// Component Interface

	public void processPacket(Packet packet) {
		// TODO react on IQ Info messages
		if (packet instanceof Message || packet instanceof Presence) {
			SMSGatewayUser user = null;
			try {
				user = new SMSGatewayUser(packet.getFrom());
				// Respond to presence subscription request or presence
				if (packet instanceof Presence) {
					processPresence((Presence) packet, user);
				}
				// Respond to message, command, botmessage
				if (packet instanceof Message) {
					processMessage((Message) packet, user);
				}
			} catch (UnkownUserException e) {
				e.user.update();
				sendReply(packet, getMessageBody(WELCOMEUSER, e.user));
				return;
			} catch (NoCreditsException e) {
				sendReply(packet, getMessageBody(NOCREDITS));
			} catch (SMSGatewayException e) { // NoNumber, Shortnumber,
				// MessageToLong
				sendReply(packet, e.getMessage());
			}
		}
	}

	// //////////////////////////////////////////////////////////

	private void processMessage(Message message, SMSGatewayUser user)
			throws SMSGatewayException {
		if (message.getBody() == null) {
			// Log.info("empty body. " + message.getTo().toString() + " . "
			// + message.getFrom().toString());
			return;
		}

		if (message.getBody().indexOf('\\') == 0) {
			processCommandMessage(message, user);
			return;
		}

		if (message.getTo().getNode().equals(BOT_NAME)) {
			processBotMessage(message, user);
			return;
		}
		SMSGatewayMessage smsMessage = new SMSGatewayMessage(user, message);
		smsMessage.send(); // thorws MessageSend on success
	}

	private void processCommandMessage(Message message, SMSGatewayUser user)
			throws MobileNumberException {
		// test for Commands
		String body = message.getBody();

		// info Usage/credits/language/from
		if (body.indexOf("\\i") == 0) { // info - Shows info for user & gateway
			sendReply(message, getMessageBody(INFO, user));
			return;
		} else if (body.indexOf("\\t") == 0) { // test - Test if given number
			// is Valid
			String[] res = body.split(" ");
			if (res.length < 2)
				throw new NoNumberException();
			sendReply(message, MobileNumber.validate(res[1]) );
			return;
		} else if (body.indexOf("\\c") == 0) { // clear - Clears all
			// uncompletet messages
			// (onlyusefull in botmode)
			user.clearMessages();
			sendReply(message, getMessageBody(BOT_CLEAR));
			return;
		} else if (body.indexOf("\\a") == 0) { // add - Send a Reply from the
			// given numer, so user can
			// easily added to roaster
			String[] res = body.split(" ");
			String number = MobileNumber.validate(res[1]);
			JID from = new JID(number, getPluginName(), "");
			String[] bodyParams = { number };
			// send subscription
			Presence reply = new Presence();
			reply.setTo(message.getFrom());
			reply.setFrom(from);
			reply.setType(Presence.Type.subscribe);
			sendPacket(reply);
			// send message
			sendMessage(message.getFrom(), from, getMessageBody(ADDNUMBER,
					user, bodyParams), message.getType());
			return;
		}
		// \help \setfrom \setlanguage
		sendReply(message, getMessageBody(HELP, user));
	}

	private void processBotMessage(Message message, SMSGatewayUser user)
			throws SMSGatewayException, MobileNumberException {
		String body = message.getBody();
		JID from = new JID(BOT_NAME, getPluginName(), "");
		SMSGatewayMessage smsMessage = new SMSGatewayMessage(user);
		try { // decide what kind of content we have
			smsMessage.number = MobileNumber.validate(body);
			if ( !smsMessage.hasText() ) {
				String[] bodyParams = { smsMessage.number };
				sendMessage(message.getFrom(), from, getMessageBody(BOT_NOTEXT,
						user, bodyParams), message.getType());
			}
		} catch (MobileNumberException e) {
			if (smsMessage.hasText() )
				throw e;
			smsMessage.setText( body );
			if (smsMessage.number == null) {
				sendMessage(message.getFrom(), from,
						getMessageBody(BOT_NONUMBER), message.getType());
			}
		}
		try { // test if message can be send..
			smsMessage.send(); // throws MessageSend
		} catch (MessageIncompleteException e) {
			e.smsMessage.update();
		}
	}

	private void processPresence(Presence presence, SMSGatewayUser user)
			throws SMSGatewayException {
		if (Presence.Type.subscribe == presence.getType()) {
			int msg = BOT_WELCOME;
			// Accept all presence requests if user has permissions
			// Reply that the subscription request was approved or rejected
			String number = presence.getTo().getNode();
			if (!presence.getTo().getNode().equals(BOT_NAME)) {
				SMSGatewayContact contact = new SMSGatewayContact(number, user);
				contact.update();
				msg = WELCOME;
			}
			Presence reply = new Presence();
			reply.setTo(presence.getFrom());
			reply.setFrom(presence.getTo());
			reply.setType(Presence.Type.subscribed);
			sendPacket(reply);
			Presence reply2 = new Presence(); // Send Status: online
			reply2.setTo(presence.getFrom());
			reply2.setFrom(presence.getTo());
			sendPacket(reply2);
			String[] bodyParams = { number }; // Send Welcome
			sendReply(presence, getMessageBody(msg, null, bodyParams));
		} else if (Presence.Type.unsubscribe == presence.getType()) {
			// Send confirmation of unsubscription
			// TODO use static call here?
			SMSGatewayContact contact = new SMSGatewayContact(presence.getTo()
					.getNode(), user);
			contact.delete();
			Presence reply = new Presence();
			reply.setTo(presence.getFrom());
			reply.setFrom(presence.getTo());
			reply.setType(Presence.Type.unsubscribed);
			sendPacket(reply);
		} else if (Presence.Type.probe == presence.getType()) {
			Presence reply = new Presence(); // Send Status: online
			reply.setTo(presence.getFrom());
			reply.setFrom(presence.getTo());
			sendPacket(reply);
		}
	}

	// ////////////////////////////////////////////////////////////
	private boolean sendReply(Packet packet, String body) {
		Message.Type type = (packet instanceof Message) ? ((Message) packet)
				.getType() : Message.Type.normal;
		return sendMessage(packet.getFrom(), packet.getTo(), body, type);
	}

	private boolean sendMessage(JID to, JID from, String body, Message.Type type) {
		Message reply = new Message();
		reply.setTo(to);
		reply.setFrom(from);
		// reply.setSubject(subject);
		reply.setBody(body);
		reply.setType(type);
		sendPacket(reply);
		return true;
	}

	public Vector<SMSGatewayUser> getUsers() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<SMSGatewayUser> users = new Vector<SMSGatewayUser>();
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(GET_USERS);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				SMSGatewayUser user = new SMSGatewayUser(rs.getString(5), rs
						.getString(1), rs.getString(3), rs.getInt(4), rs
						.getString(2));
				users.add(user);
			}
			rs.close();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
		}
		return users;
	}

	public Vector<SMSGatewayContact> getAllContacts() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<SMSGatewayContact> contacts = new Vector<SMSGatewayContact>();
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(GET_ALL_CONTACTS);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				try {
					SMSGatewayContact contact = new SMSGatewayContact(rs
							.getString(1));
					contact.name = rs.getString(2);
					contact.setOwner(rs.getString(3));
					contacts.add(contact);
				} catch (MobileNumberException e) {
				}
			}
			rs.close();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
		}
		return contacts;
	}

	private String getMessageBody(int id) {
		return getMessageBody(id, null, null);
	}

	private String getMessageBody(int id, SMSGatewayUser user) {
		return getMessageBody(id, user, null);
	}

	private String getMessageBody(int id, SMSGatewayUser user, String[] params) {
		switch (id) {
		case HELP:
			return "Hallo "
					+ user.id
					+ ". Willkommen beim "
					+ getPluginName()
					+ " SMS Versanddienst.\n"
					+ "Folgende Befehle stehen zur Verfuegung:\n"
					+ " \\info - Zeigt Informationen ueber Ihren Account an \n"
					+ " \\add NUMMER - Schickt eine Nachricht um den SMSEmpfänger hinzuzufügen \n"
					+ " \\test NUMMER - Testet NUMMER auf Gültigkeit \n"
					+ " \\clear - Löscht bereit eingegeben Daten beim SMSBot \n"
					+ "Für gelegentliche Nachrichten steht der SMSBot unter "
					+ BOT_NAME + "@" + getPluginName() + " zur Verfügung. \n"
					+ "Ihr Konto können Sie unter http://" + getPluginName()
					+ " aufladen. Viel Spaß.";
			// Sie koennen SMS versenden in dem Sie credits erwerben.
		case INFO:
			return "Ihre Benutzerdaten für " + user.id + ":\n Name: "
					+ user.name + "\n Absendernummer: " + user.number
					+ "\n Vorhandenes Guthaben: " + user.credits + " Credits";
		case WELCOMEUSER:
			return "Hallo "
					+ user.id
					+ ". Willkommen beim "
					+ getPluginName()
					+ " SMS Versanddienst.\n"
					+ " Sobald Sie Guthaben auf Ihr Konto aufgeladen haben, können Sie einfach und schnell"
					+ "SMS Nachrichten verschicken, indem Sie eine Nachricht an <NUMMER>@"
					+ getPluginName()
					+ " senden.\n "
					+ "Ihr Konto können Sie unter http://"
					+ getPluginName()
					+ " aufladen."
					+ "Für gelegentliche Nachrichten steht der SMSBot unter "
					+ BOT_NAME
					+ "@"
					+ getPluginName()
					+ " zur Verfügung. \n"
					+ "Weitere Informationen erhalten Sie bei Eingabe von \\help. Viel Spaß.";
		case NOCREDITS:
			return "Kein Guthaben verfuegbar. Ihr Konto können Sie unter http://"
					+ getPluginName() + " aufladen.";
		case ADDNUMBER:
			return "Fügen Sie diesen Absender (" + params[0] + "@"
					+ getPluginName()
					+ ") zu Ihrer Kontaktliste hinzu, um SMS Nachrichten an "
					+ params[0] + " zu schicken.";
		case BOT_NONUMBER:
			return "An welche Nummer möchten Sie die SMS senden? (Zum Löschen \\clear eingeben)";
		case BOT_CLEAR:
			return "Eingegebene Daten gelöscht!";
		case BOT_NOTEXT:
			return "Welchen Text wollen Sie an " + params[0]
					+ " senden? (Zum Löschen \\clear eingeben)";
		case WELCOME:
			return "Willkommen beim "
					+ getPluginName()
					+ " SMS Versanddienst.\n"
					+ "Jede Nachricht an diesen Kontakt wird als SMS an "
					+ params[0]
					+ " zugestellt. \n"
					+ "Weitere Informationen erhalten Sie bei Eingabe von \\help. Viel Spaß.";
		case BOT_WELCOME:
			return "Willkommen beim "
					+ getPluginName()
					+ " SMSBot.\n"
					+ "Hier können Sie Nachrichten an gelgenheits Kontakte versenden. Geben Sie einfach "
					+ "zuerst die nachricht und dann die Nummer des Empfängers an."
					+ "Weitere Informationen erhalten Sie bei Eingabe von \\help. Viel Spaß.";
		}
		return "Es ist ein unbekannter Fehler aufgetreten: Errornr" + id;
	}

}
