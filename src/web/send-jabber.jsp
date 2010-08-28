<%@ page import="java.util.*,
		 java.net.URLEncoder,
		 org.jivesoftware.util.*,
                 de.warteschlange.wildfire.plugin.*,
                 org.jivesoftware.wildfire.XMPPServer"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<html>
    <head>
        <title>SMSGateway send Message</title>
        <meta name="pageID" content="smsgateway-users"/>
    </head>
    <body>

<% // Get parameters
    boolean save = ParamUtils.getBooleanParameter(request,"save");
    String userJID = ParamUtils.getParameter(request,"user");
    
    boolean sendsucess = ParamUtils.getBooleanParameter(request, "sendsucess", false);

     SMSGatewayPlugin plugin = (SMSGatewayPlugin)XMPPServer.getInstance().getPluginManager(
            ).getPlugin("smsgateway");
            
  boolean send = false;
    // Handle a save
    if (send) {
          String msg  = ParamUtils.getParameter(request,"msg");
          
          
        // Send good, so redirect
        response.sendRedirect("users.jsp?savesuccess=true");
        return;
    }
%>

<form action="send-jabber.jsp">

<br><br>

<input type="submit" value="<fmt:message key="global.save_properties" />">
<input type="submit" name="cancel" value="<fmt:message key="global.cancel" />">

</form>

    </body>
</html>