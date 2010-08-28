package de.warteschlange.wildfire.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;

public class SMSGatewayContact {

	public String name;

	public String number;

	private String ownerJID;

	private SMSGatewayUser owner = null;

	private static final String UPDATE_CONTACT = "REPLACE INTO smsContacts SET number=?, name=?, owner=?";

	private static final String DELETE_CONTACT = "DELETE FROM smsContacts WHERE number=? AND owner=?";

	public SMSGatewayContact(String _number) throws MobileNumberException {
		MobileNumber.validate(_number);
		name = "Unbekannt";
		number = _number;
		//ownerJID = null;
	}

	public SMSGatewayContact(String _number, SMSGatewayUser _owner) throws MobileNumberException {
		this(_number);
		setOwner(_owner);
	}

	public void setOwner(String _ownerJID) {
		ownerJID = _ownerJID;
	}
	
	
	public String getOwnerJID() {
		return ownerJID;
	}
	
	public void setOwner(SMSGatewayUser _owner) {
		owner = _owner;
		ownerJID = _owner.id;
	}

	public boolean update() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(UPDATE_CONTACT);
			pstmt.setString(1, number);
			pstmt.setString(2, name);
			pstmt.setString(3, owner.id);
			pstmt.executeUpdate();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return false;
		}
		return true;
	}

	public SMSGatewayContact delete() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(DELETE_CONTACT);
			pstmt.setString(1, number);
			pstmt.setString(2, owner.id);
			pstmt.executeUpdate();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return this;
		}
		return null;
	}
}