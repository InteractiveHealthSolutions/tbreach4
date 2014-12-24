<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Non Suspect Dashboard Login</title>
</head>
<body>
<form id="nonSuspectLogin" name="NonSuspectLogin" action="NonSuspectLoginServlet" method="post">
<h3>Non Suspect Dashboard Login</h3>
Please provide OpenMRS Login Credentials.

<table style="border: 1px; border-color: black;">

<tr>
<td>Username: <input type="text" name="username" id="usernameId"></td>
<td>Password: <input type="password" name="password" id="passwordId"></td>
<tr><td><input type="submit" value="Login" name="authenticate"/></td></tr>
</tr>
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