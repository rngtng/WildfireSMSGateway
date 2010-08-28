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
        <title>SMSGateway Contact Summary</title>
        <meta name="pageID" content="smsgateway-contacts"/>
    </head>
    <body>

<%
    String userJID = ParamUtils.getParameter(request, "user");
    //boolean moreOptions = ParamUtils.getBooleanParameter(request, "moreOptions", false);

     SMSGatewayPlugin plugin = (SMSGatewayPlugin)XMPPServer.getInstance().getPluginManager().getPlugin("smsgateway");
     SMSGatewayUser user = null;
     Vector<SMSGatewayContact> contacts = null;
     try {  
          user = new SMSGatewayUser( userJID );
          contacts = user.getContacts();
        }
    catch(SMSGatewayException e) {
          contacts = plugin.getAllContacts();
    }

%>
Contacts<%  if(user != null ) { %> of <b><%= user.name %></b>(<%= user.id %>)<% } %>:<br>
<div class="jive-table">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<thead>
    <tr>
        <th>&nbsp;</th>
        <th nowrap><fmt:message key="user.create.name" /></th>
        <th nowrap>Number</th>
        <th nowrap>Owner</th>
        <th nowrap><fmt:message key="user.summary.edit" /></th>
        <th nowrap><fmt:message key="global.delete" /></th>
    </tr>
</thead>
<tbody>

    <% if (contacts.isEmpty()) { %>
    <tr>
        <td align="center" colspan="9"><fmt:message key="user.summary.not_user" /></td>
    </tr>
	    
    <% 
    } 
    else {
       int i = 0;
       for( SMSGatewayContact contact : contacts )
       {
       i++;
    %>
    <tr class="jive-<%= (((i%2)==0) ? "even" : "odd") %>">
       <td width="1%"><%= i %></td>
       <td width="30%">
           <%= contact.name %>
       </td>
       <td width="35%"><%= contact.number %> &nbsp;</td>
       <td width="30%">
           <a href="contacts.jsp?user=<%= URLEncoder.encode(contact.getOwnerJID(), "UTF-8") %>">
             <%= contact.getOwnerJID() %>
           </a>
       </td
       <td width="1%" align="center">
          <a href="contact-edit.jsp?contact=<%= URLEncoder.encode(contact.number, "UTF-8") %>&user=<%= URLEncoder.encode(contact.getOwnerJID(), "UTF-8") %>">
               <img src="images/edit-16x16.gif" width="17" height="17" border="0">
          </a>
       </td>
       <td width="1%" align="center" style="border-right:1px #ccc solid;">
           <a href="contact-delte.jsp?contact=<%= URLEncoder.encode(contact.number, "UTF-8") %>&user=<%= URLEncoder.encode(contact.getOwnerJID(), "UTF-8") %>" title="<fmt:message key="global.click_delete" />">
              <img src="images/delete-16x16.gif" width="16" height="16" border="0">
          </a>
       </td>
   </tr>
<%
        }
    }
%>

</tbody>
</table>
</div>



<!--
<script language="JavaScript" type="text/javascript">
document.f.criteria.focus();
</script> -->

</body>
</html>