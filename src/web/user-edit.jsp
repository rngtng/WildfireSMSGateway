<%@ page import="org.jivesoftware.util.*,
                 de.warteschlange.wildfire.plugin.*,
                 org.jivesoftware.wildfire.XMPPServer,
                 java.net.URLEncoder"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<jsp:useBean id="webManager" class="org.jivesoftware.util.WebManager" />

<%  // Get parameters
    boolean save = ParamUtils.getBooleanParameter(request,"save");
    String userJID = ParamUtils.getParameter(request,"user");

    // Handle a cancel
    if (request.getParameter("cancel") != null) {
        response.sendRedirect("users.jsp?editsuccess=false");
        return;
    }

    SMSGatewayPlugin plugin = (SMSGatewayPlugin)XMPPServer.getInstance().getPluginManager().getPlugin("smsgateway");
    SMSGatewayUser user = null;
    try {  
          user = new SMSGatewayUser( userJID );
        }
    catch(SMSGatewayException e) {
      response.sendRedirect("users.jsp?editsuccess=false");
      return;
    }
    
    // Handle a save
    if (save) {
          user.name = ParamUtils.getParameter(request,"name");
          user.number = ParamUtils.getParameter(request,"number");
          user.language = ParamUtils.getParameter(request,"language");
          user.credits = ParamUtils.getIntParameter(request,"credits",0);
          user.update();
        // Changes good, so redirect
        response.sendRedirect("users.jsp?editsuccess=true");
        return;
    }
%>

<html>
    <head>
        <title>SMSGateway Edit User</title>
        <meta name="subPageID" content="smsgateway-users"/>
        <meta name="extraParams" content="<%= "username="+URLEncoder.encode(user.name, "UTF-8") %>"/>
    </head>
    <body>
<p>
<fmt:message key="user.edit.form.info" />
</p>

<form action="user-edit.jsp">

<input type="hidden" name="user" value="<%= userJID %>">
<input type="hidden" name="save" value="true">

<fieldset>
    <legend><fmt:message key="user.edit.form.property" /></legend>
    <div>
    <table cellpadding="3" cellspacing="0" border="0" width="100%">
    <tbody>
        <tr>
            <td class="c1">
                <fmt:message key="user.create.username" />:
            </td>
            <td>
                <%= user.id %>
            </td>
        </tr>
        <tr>
            <td class="c1">
                Name:
            </td>
            <td>
                <input type="text" size="30" maxlength="150" name="name"
                 value="<%= user.name %>">
            </td>
        </tr>
        <tr>
            <td class="c1">
                Number:
            </td>
            <td>
                <input type="text" size="30" maxlength="150" name="number"
                 value="<%= user.number %>">
            </td>
        </tr>
        <tr>
            <td class="c1">
                Language:
            </td>
            <td>
                <input type="text" size="30" maxlength="150" name="language"
                 value="<%= user.language %>">
            </td>
        </tr>
        <tr>
            <td class="c1">
                Credits:
            </td>
            <td>
                <input type="text" size="30" maxlength="150" name="credits"
                 value="<%= user.credits %>">
            </td>
        </tr>
     </tbody>
    </table>
    </div>
</fieldset>

<br><br>

<input type="submit" value="<fmt:message key="global.save_properties" />">
<input type="submit" name="cancel" value="<fmt:message key="global.cancel" />">

</form>

    </body>
</html>