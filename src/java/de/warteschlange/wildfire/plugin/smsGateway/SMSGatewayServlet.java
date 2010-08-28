package de.warteschlange.wildfire.plugin.smsGateway;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.XMPPServer;

import de.warteschlange.wildfire.plugin.SMSGatewayPlugin;

@SuppressWarnings("serial")
public class SMSGatewayServlet extends HttpServlet {

	private SMSGatewayPlugin plugin;

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		plugin = (SMSGatewayPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin("smsGateway");

		// Exclude this servlet from requiring the user to login
		AuthCheckFilter.addExclude("smsGateway/process");
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String action = request.getParameter("action");
			// Printwriter for writing out responses to browser
			PrintWriter out = response.getWriter();
			replyMessage( action, response, out);
			//add suer
			//del user
			
			//send password
		} catch (Exception e) {
			Log.error(e);
		}
	}

	private void replyMessage(String message, HttpServletResponse response,
			PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<result>" + message + "</result>");
		out.flush();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		super.destroy();
		// Release the excluded URL
		AuthCheckFilter.removeExclude("smsGateway/process");
	}
}
