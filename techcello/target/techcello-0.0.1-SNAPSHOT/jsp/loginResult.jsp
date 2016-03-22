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

</p>

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
</p>
<% } else if (displayBean.isLoginFailure() || displayBean.isError()) { %>
<div class="error">
	<i class="fa fa-times-circle"></i>
	<%= displayBean.getAuthStatusMessage() %>
</div>
<% } else { %>
No Information...
<% }%>

<a href="javascript:deleteAllCookies()">Delete all cookies</a>
</body>
<script type="text/javascript">
function clear() {
	alert("alert");
}
//Delete all cookies
function deleteAllCookies() {
    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + '=;' +
            'expires=Thu, 01-Jan-1970 00:00:01 GMT;' +
            'path=' + '/;' +
            'domain=' + window.location.host + ';' +
            'secure=;';
    }
 window.location = "<%= displayBean.getLoginURL() %>";
}
</script>
</html>
