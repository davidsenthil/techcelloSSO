<%@page import="com.aspire.techcello.handlers.LoginDisplayBean"%>
<%
	LoginDisplayBean displayBean = (LoginDisplayBean) request.getAttribute("DisplayBean");
%>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="<%= displayBean.getImagePath() %>/styles/style.css">
</head>
<body>
<h2>Techcello - "Single Sign On" Demo</h2>
<p>
<table style="width:100%;border:none">
<tr>
<td style="text-align:left">Welcome <%= displayBean.getUserFullName() %>,</td>
<td style="text-align:right"><a href="<%= displayBean.getLoginURL() %>">Logout</a></td>
</tr>
</table>

<% if (displayBean.isLoginSuccess()) { %>
<div class="success">
	<i class="fa fa-check"></i>
	<%= displayBean.getAuthStatusMessage() %>
</div>

<p>
<div class="info">
<% for (String valueRow : displayBean.getTokenValues()) { %>
		<ul><%= valueRow %></ul>
<% } %>
</div>
<% } else if (displayBean.isLoginFailure() || displayBean.isError()) { %>
<div class="error">
	<i class="fa fa-times-circle"></i>
	<%= displayBean.getAuthStatusMessage() %>
</div>
<% } else { %>
No Information...
<% }%>

</body>
</html>
