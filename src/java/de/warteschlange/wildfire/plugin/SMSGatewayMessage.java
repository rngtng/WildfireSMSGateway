package de.warteschlange.wildfire.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.xmpp.packet.Message;

public class SMSGatewayMessage {

	public int no;

	public SMSGatewayUser owner;

	public String number = null;

	private String text = null;

	public boolean send = false;

	// ################# SMS GATEWAY DATA
	private Hashtable<String, String> paramsTable;

	private static String GATEWAY_URL = "http://gateway.any-sms.de/send_sms.php";

	private static String GATEWAY_HIDEFROM = "11"; // gateway 11 - ohne

	private static String GATEWAY_SHOWFROM = "31"; // gateway 31 - ohne

	private static String GATEWAY_ID = "xxxx";

	private static String GATEWAY_PASSWORD = "xxxxx";

	// ################# MYSQL DATA
	private static final String UPDATE_MESSAGE = "REPLACE INTO smsMessages SET no=?, number=?, text=?, owner=?, send=?, time=?";

	private static final String GET_LATEST_MESSAGE = "SELECT no, number, text FROM smsMessages WHERE owner LIKE ? AND send < 1 AND time > ? ORDER BY time DESC LIMIT 0,1";

	// /
	private static final int RESETTIME = 90; // seconds

	public SMSGatewayMessage(SMSGatewayUser _owner) {
		owner = _owner;
		getLatest();
	}

	public SMSGatewayMessage(SMSGatewayUser _owner, Message _message) throws MessageTooLongException {
		this(_owner, _message.getTo().getNode(), _message.getBody());
	}

	public SMSGatewayMessage(SMSGatewayUser _owner, String _number, String _text) throws MessageTooLongException {
		owner = _owner;
		number = _number;
		this.setText( _text );
	}

	public boolean setText(String text) throws MessageTooLongException {
		text = text.trim();
		if (text.length() > 160)
			throw (new MessageTooLongException( text ));
		this.text = text;
		return true;
	}

	public boolean hasText()
	{
		return (this.text != null);
	}
	
	
	public int getLength()
	{
	  if( this.text == null) return -1;
	  return text.length();
	}
	
	public boolean send() throws SMSGatewayException {
		// no user
		if (owner == null || text == null || number == null)
			throw (new MessageIncompleteException(this));
		// no credit
		if (owner.credits < 1)
			throw (new NoCreditsException());
		number = MobileNumber.validate(number);

		String gateway = GATEWAY_HIDEFROM;
		try {
			MobileNumber.validate(owner.number);
			gateway = GATEWAY_SHOWFROM;
		} catch (MobileNumberException e) {
		}
		paramsTable = new Hashtable<String, String>();
		paramsTable.put("id", GATEWAY_ID);
		paramsTable.put("pass", GATEWAY_PASSWORD);
		// paramsTable.put( "notify", "1");
		// TODO: react on notify
		paramsTable.put("gateway", gateway);
		paramsTable.put("text", text);
		paramsTable.put("nummer", number);
		paramsTable.put("absender", owner.number);
		String url;
		try {
			url = GATEWAY_URL + "?" + http_build_query(paramsTable);
			Log.info(url);

			URL u = new URL(url);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.connect();
			huc.getResponseCode();
                        Log.info( huc.getResponseMessage() );
			huc.disconnect();

			// TODO: react on response Code
			send = true;
			owner.decreaseCredits(1);
			update();
			throw new MessageSend(this);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
		return true;
	}

	private String http_build_query(Hashtable<String, String> map)
			throws UnsupportedEncodingException {
		String query = "";
		Enumeration<String> keys = map.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			query += key + "="
					+ URLEncoder.encode((String) map.get(key), "ISO-8859-15")
					+ "&"; // UTF-8
		}
		return query;
	}

	// ///////////////////////////////////////////////////////////

	public boolean getLatest() {
		Connection my_con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			my_con = (Connection) DbConnectionManager.getConnection();
			pstmt = my_con.prepareStatement(GET_LATEST_MESSAGE);
			pstmt.setString(1, owner.id);
			Date date = new Date();
			pstmt.setLong(2, (date.getTime() / 1000) - RESETTIME);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				no = rs.getInt(1);
				number = rs.getString(2);
				text = rs.getString(3);
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
			pstmt = my_con.prepareStatement(UPDATE_MESSAGE);
			// o=?, number=?, text=?, owner=?, send=?, time=?
			pstmt.setInt(1, no);
			pstmt.setString(2, number);
			pstmt.setString(3, text);
			pstmt.setString(4, owner.id);
			pstmt.setBoolean(5, send);
			pstmt.setLong(6, (new Date()).getTime() / 1000);

			pstmt.executeUpdate();
			pstmt.close();
			my_con.close();
		} catch (SQLException e) {
			Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
			return false;
		}
		return true;
	}

	/*
	 * public SMSGatewayMessage delete() { Connection my_con = null;
	 * PreparedStatement pstmt = null; try { my_con = (Connection)
	 * DbConnectionManager.getConnection(); pstmt =
	 * my_con.prepareStatement(DELETE_MESSAGES_USER); pstmt.setString(1,
	 * owner.id); pstmt.executeUpdate(); pstmt.close(); my_con.close(); } catch
	 * (SQLException e) {
	 * Log.error(LocaleUtils.getLocalizedString("admin.error"), e); return this; }
	 * return null; }
	 */
}