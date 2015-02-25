<%-- Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. --%>
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
