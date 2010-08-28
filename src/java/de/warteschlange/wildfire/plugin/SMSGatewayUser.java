package de.warteschlange.wildfire.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.xmpp.packet.JID;

// /////////////////////////////////////////////////////////
public class SMSGatewayUser {
	public String name;

	public String number;

	public String id;

	public int credits;

	public String language;
	
	private boolean isNew = true;
	
	//############################# MYSQL Data
	private static final String GET_USER = "SELECT name, language, number, credits, id "
		+ "FROM smsUser WHERE id=?";

	private static final String UPDATE_USER = "REPLACE INTO smsUser SET id=?, name=?, number=?, language=?, credits=?";

	private static final String DELETE_USER = "DELETE FROM smsUser WHERE id=? ";

	private static final String DELETE_MESSAGES = "DELETE FROM smsMessages WHERE send < 1 AND owner LIKE ?";
	
	private static final String GET_CONTACTS = "SELECT number, name FROM smsContacts WHERE owner LIKE ?";

	//################################
	public SMSGatewayUser(JID userJID) throws UnkownUserException {
		this(userJID.getNode() + '@' + userJID.getDomain());
	}

	public SMSGatewayUser(String _id) throws UnkownUserException {
		id = _id;
		name = "Unbekannt";
		number = "";
		credits = 0;
		language = "de";
		loadData();
		if( isNew ) throw new UnkownUserException( this );
	}

	public SMSGatewayUser(String _id, String _name, String _number, int _credits, String _language ) {
		id = _id;
		name = _name;
		number = _number;
		credits = _credits;
		language = _language;
	}
	
	private boolean loadData() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(GET_USER);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				name = rs.getString(1);
				language = rs.getString(2);
				number = rs.getString(3);
				credits = rs.getInt(4);
				isNew = false;
			}
			rs.close();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return false;
		}
		return true;
	}

	public boolean update() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(UPDATE_USER);
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, number);
			pstmt.setString(4, language);
			pstmt.setInt(5, credits);
			pstmt.executeUpdate();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return false;
		}
		return true;
	}

	public void decreaseCredits(int amount) {
		credits -= amount;
		update();
	}
	
	public Vector<SMSGatewayContact> getContacts() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<SMSGatewayContact> contacts = new Vector<SMSGatewayContact>();
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(GET_CONTACTS);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				try {
					SMSGatewayContact contact = new SMSGatewayContact(rs
							.getString(1), this);
					contact.name = rs.getString(2);
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

	public SMSGatewayUser delete() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(DELETE_USER);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return this;
		}
		return null;
	}

	public boolean clearMessages() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(DELETE_MESSAGES);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return false;
		}
		return true;
	}
}
