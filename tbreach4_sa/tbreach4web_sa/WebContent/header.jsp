<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<title>SA Mine TB - Dashboard</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />

<%
String userName = null;
Cookie[] cookies = request.getCookies();
if(cookies !=null){
for(Cookie cookie : cookies){
    if(cookie.getName().equals("dashboard_user")) {
    	userName = cookie.getValue();
    	cookie.setMaxAge(30*60);
    }
}
}
if(userName == null) response.sendRedirect("login.jsp");
%>


<div align="center">
<a href="${pageContext.request.contextPath}/login.jsp">
<img src="images/ihsLogo.png"/>
</a>
<ul>
 <font color="#F58220"> Currently logged in as <i><b> <%=userName%> </b></i>  </font> &nbsp;&nbsp;&nbsp; <font color="#C5E3BF"> || </font> &nbsp;&nbsp;&nbsp;
 <a href="dashboard.jsp">Dashboard</a> &nbsp;&nbsp;&nbsp; <font color="#C5E3BF"> || </font> &nbsp;&nbsp;&nbsp;
 <a href="logout.jsp">Logout</a> 
</ul>
</div>