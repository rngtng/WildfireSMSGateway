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
        <title>SMSGateway User Summary</title>
        <meta name="pageID" content="smsgateway-users"/>
    </head>
    <body>

<%
    //String user = ParamUtils.getParameter(request, "criteria");
    boolean editsuccess = ParamUtils.getBooleanParameter(request, "editsuccess", false);

     SMSGatewayPlugin plugin = (SMSGatewayPlugin)XMPPServer.getInstance().getPluginManager(
            ).getPlugin("smsgateway");
            
    Vector<SMSGatewayUser> users = plugin.getUsers();

%>

<%  if (editsuccess) { %>

    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0"></td>
        <td class="jive-icon-label">
        <fmt:message key="user.edit.form.update" />
        </td></tr>
    </tbody>
    </table>
    </div><br>

<%  } %>

<div class="jive-table">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<thead>
    <tr>
        <th>&nbsp;</th>
        <th nowrap><fmt:message key="user.create.username" /></th>
        <th nowrap><fmt:message key="user.create.name" /></th>
        <th nowrap>Number</th>
        <th nowrap>Language</th>
        <th nowrap>Credits</th>
        <th nowrap>Contacts</th>
        <th nowrap><fmt:message key="user.summary.edit" /></th>
        <th nowrap><fmt:message key="global.delete" /></th>
    </tr>
</thead>
<tbody>

    <% if (users.isEmpty()) { %>
    <tr>
        <td align="center" colspan="7"><fmt:message key="user.summary.not_user" /></td>
    </tr>
	    
    <% 
    } 
    else {
       int i = 0;
       for( SMSGatewayUser user : users )
       {
       i++;
    %>
    <tr class="jive-<%= (((i%2)==0) ? "even" : "odd") %>">
        <td width="1%"><%= i %></td>
       <td width="30%">
           <a href="contacts.jsp?user=<%= URLEncoder.encode(user.id, "UTF-8") %>">
             <%= user.name %>
           </a>
       </td>
       <td width="35">
         <a href="send-jabber.jsp?user=<%= URLEncoder.encode(user.id, "UTF-8") %>">
          <%= user.id %>
         </a> 
       </td>
       <td width="35%">
         <a href="send-sms.jsp?number=<%= URLEncoder.encode(user.number, "UTF-8") %>">
           <%= user.number %>
         </a>
        </td>
       <td width="35%"><%= user.language %> &nbsp;</td>
       <td width="35%"><%= user.credits %> &nbsp;</td>
       <td width="1%" align="center">
          <a href="contacts.jsp?user=<%= URLEncoder.encode(user.id, "UTF-8") %>">
               <img src="images/user.gif" width="17" height="17" border="0">
          </a>
       </td>
       <td width="1%" align="center">
          <a href="user-edit.jsp?user=<%= URLEncoder.encode(user.id, "UTF-8") %>">
               <img src="images/edit-16x16.gif" width="17" height="17" border="0">
          </a>
       </td>
       <td width="1%" align="center" style="border-right:1px #ccc solid;">
           <a href="user-delete.jsp?user=<%= URLEncoder.encode(user.id, "UTF-8") %>" title="<fmt:message key="global.click_delete" />">
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