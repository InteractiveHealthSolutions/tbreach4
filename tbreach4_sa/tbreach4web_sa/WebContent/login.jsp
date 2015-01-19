<%-- Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. --%>
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
