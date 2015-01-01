<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="icon.jsp" %> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>

<%
	String userName = null;
	Cookie[] cookies = request.getCookies ();
	if (cookies != null)
	{
		for (Cookie cookie : cookies)
		{
			if (cookie.getName ().equals ("dashboard_user"))
				userName = cookie.getValue ();
		}
	}
	if (!(userName == null))
		response.sendRedirect ("dashboard.jsp");
%>


<form id="login" name="login" action="DashboardServlet" method="post">

<h2>Dashboard Login </h2>

<table align="center">
			<tr><td>
			<div align="center" class="divCenter">

Please provide OpenMRS Login Credentials. <br><br>
<table>

<tr>
<td>Username:</td> <td> <input type="text" name="username" id="usernameId"></td></tr>
<tr><td>Password:</td> <td> <input type="password" name="password" id="passwordId"></td></tr>

</table>

<br>

<input type="submit" value="Login" name="authenticate"/>
<input type="hidden" id="reqType" name="reqType" value = "Login">
</div>
</td></tr>
</table>

</form>
</body>
<script type="text/javascript">
if('${authentic}' == "failed")
{
	alert("Authentication Failed. Please check username and password again.");
	document.getElementById('usernameId').style.borderColor = "red";
	document.getElementById('passwordId').style.borderColor = "red";
}
</script>
</html>