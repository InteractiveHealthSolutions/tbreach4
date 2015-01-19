<%-- Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="header.jsp" %>
<%@ page import="com.ihsresearch.tbr4web.server.MobileService"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>

<h2>Dashboard</h2>
	<table align="center">
		<tr>
			<td><div align="center" class="divCenter">


					<table>

                     <tr><td align="center"> <a href="importMessages.jsp">Import Messages from SMSTarseel</a> </td></tr>
                     <tr><td align="center"> <a href="editMessages.jsp">View & Edit Messages</a> </td></tr>
                     <tr><td align="center"> <a href="addMessages.jsp">Add new Message</a> </td></tr>
                     <tr><td align="center"> <a href="smsStatistics.jsp">Screening Statistics from SMS</a> </td></tr>
                     <!-- <tr><td align="center"> <a href="openmrsStatistics.jsp">Screening Statistics from Openmrs</a> </td></tr> -->

					</table>


				</div></td>

		</tr>
		<tr><td>
		<center>Last Import on: <i>  <%= MobileService.getService().getLastImport() %> SAST</i></center>
		</td></tr>
	</table>
</body>

</html>
