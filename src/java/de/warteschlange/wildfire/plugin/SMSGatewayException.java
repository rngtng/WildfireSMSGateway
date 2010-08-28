package de.warteschlange.wildfire.plugin;

public class SMSGatewayException extends Exception {
	protected static final long serialVersionUID = 1L;

	public SMSGatewayException() {
		super("Es ist ein unbekannter Fehler aufgetreten!");
	}

	public SMSGatewayException(String e) {
		super(e);
	}
}

// ///////////////////////////////////////////////////////////
class MobileNumberException extends SMSGatewayException {
	private static final long serialVersionUID = 1L;

	public MobileNumberException() {
		super();
	}

	public MobileNumberException(String e) {
		super(e);
	}
}

class NoNumberException extends MobileNumberException {
	private static final long serialVersionUID = 1L;

	public NoNumberException() {
		super(
				"Die angegeben Nummer ist ungueltig.\nGeben Sie die Nummer im internationalem Format an. z.B. 0049171987654321");
	}
}

class ShortNumberException extends MobileNumberException {
	private static final long serialVersionUID = 1L;

	public ShortNumberException() {
		super("Die angegeben Nummer ist zu kurz.");
	}
}

class NoCountryException extends MobileNumberException {
	private static final long serialVersionUID = 1L;
}

class NoProviderException extends MobileNumberException {
	private static final long serialVersionUID = 1L;
}

// ///////////////////////////////////////////////////////////
class MessageTooLongException extends SMSGatewayException {
	//public SMSGatewayMessage smsMessage = null;

	private static final long serialVersionUID = 1L;

	public MessageTooLongException(String _smsMessage) {
		super("Die Nachricht ist zu lang (" + _smsMessage.length()
				+ " Zeichen).\nEs sind maximal 160 Zeichen möglich");
		//smsMessage = _smsMessage;
	}

}

class MessageSend extends SMSGatewayException {

	public SMSGatewayMessage smsMessage = null;

	private static final long serialVersionUID = 1L;

	public MessageSend(SMSGatewayMessage _smsMessage) {
		super("Die SMS Nachricht (" + _smsMessage.getLength()
				+ " Zeichen) wurde erfolreich an " + _smsMessage.number
				+ " versendet.\nIhr Restguthaben beträgt: "
				+ _smsMessage.owner.credits + "Credits");
		smsMessage = _smsMessage;
	}
}

class MessageIncompleteException extends SMSGatewayException {

	public SMSGatewayMessage smsMessage = null;

	private static final long serialVersionUID = 1L;

	public MessageIncompleteException(SMSGatewayMessage _smsMessage) {
		smsMessage = _smsMessage;
	}
}

// /////////////////////////////////////////////////////////
class NoCreditsException extends SMSGatewayException {
	private static final long serialVersionUID = 1L;
}

class UnkownUserException extends SMSGatewayException {
	private static final long serialVersionUID = 1L;

	public SMSGatewayUser user;

	public UnkownUserException(SMSGatewayUser _user) {
		user = _user;
	}
}

class NoUserException extends SMSGatewayException {
	private static final long serialVersionUID = 1L;
}
