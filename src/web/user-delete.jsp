<%@ page import="org.jivesoftware.util.*,
                 de.warteschlange.wildfire.plugin.*,
                 org.jivesoftware.wildfire.XMPPServer,
                 java.net.URLEncoder"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%  // Get parameters //
    boolean cancel = request.getParameter("cancel") != null;
    boolean delete = request.getParameter("delete") != null;
    String userJID = ParamUtils.getParameter(request,"user");

    // Handle a cancel
    if (cancel) {
        response.sendRedirect("users.jsp");
        return;
    }

    SMSGatewayPlugin plugin = (SMSGatewayPlugin)XMPPServer.getInstance().getPluginManager().getPlugin("smsgateway");
    SMSGatewayUser user = null;
    try {  
              user = new SMSGatewayUser( userJID);
        }
    catch(SMSGatewayException e) {
    }

    // Handle a user delete:
    if (delete) {
        // Delete the user
        user.delete();
        // Done
        response.sendRedirect("users.jsp?deletesuccess=true");
        return;
    }
%>

<html>
    <head>
        <title><fmt:message key="user.delete.title"/></title>
        <meta name="subPageID" content="smsgateway-users"/>
        <meta name="extraParams" content="<%= "user="+URLEncoder.encode(userJID, "UTF-8") %>"/>
        <meta name="helpPage" content="remove_a_user_from_the_system.html"/>
    </head>
    <body>

      <p>
      <fmt:message key="user.delete.info" />
      <b><%= user.name %></a></b>
      <fmt:message key="user.delete.info1" />
      </p>
      
      <form action="user-delete.jsp">
      <input type="hidden" name="username" value="<%= userJID %>">
      <input type="submit" name="delete" value="<fmt:message key="user.delete.delete" />">
      <input type="submit" name="cancel" value="<fmt:message key="global.cancel" />">
      </form>

    </body>
</html>
