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

<a href="<%= displayBean.getTechcelloLoginActionURL() %>">Login - Basic authentication</a><br><br>
<a href="<%= displayBean.getTechcelloLoginActionURL() %>">Login - LDAP authentication</a>

<!-- 
<table class="centerTable">

	<tr>
		<td></td>
		<td>
			<a href="<%= displayBean.getGoogleLoginActionURL() %>">
				<img alt="Sign In with Google" src="<%= displayBean.getImagePath() %>/pics/btn_google_signin.png">
			</a>
		</td>
	</tr>

	<tr>
		<td>Sign in with</td>
		<td> 
			<a href="<%= displayBean.getTechcelloLoginActionURL() %>">
			<img alt="Sign In with Techcello" src="<%= displayBean.getImagePath() %>/pics/techcello_logo.png" height="50" width="150">
			</a>
		</td>
	</tr>
</table>
 -->

</body>
</html>
