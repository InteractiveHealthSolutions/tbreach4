<%-- Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

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
<script type="text/javascript">

function load(){
	<%
	    String usernames[][] = MobileService.getService().getUsernameList();	
	    for(int i =0; i< usernames.length; i++){%>
	    	var username = "<%=usernames[i][0]%>";
	    	addOption(username,username);
	   <% }
	%>
	addOption('-----------------------------','');
}

//Add new option to the dropdown (use for them month dropdown)
function addOption(text,value)
{
 var select = document.getElementById("usernames");
 var optn = document.createElement("OPTION");
 optn.text = text;
 optn.value = value;
 select.add(optn, 0);
 }
 
function addMessage()
{
    var e = document.getElementById("usernames");
	var strUser = e.options[e.selectedIndex].value;
	var flag = true;
	var errorMessage = "";
	if(strUser == ''){
	   errorMessage = errorMessage.concat("Missing Username. \n");
	   flag = false;
	}   
	var dateFrom = document.getElementById('dateFrom').value;
	if (dateFrom == ''){
		errorMessage = errorMessage.concat("Missing Date. \n");
		flag = false;
	}
	var text = document.getElementById('text').value;
	if (text == ''){
		errorMessage = errorMessage.concat("Missing Text. \n");
		flag = false;
	}
	else{
		var separators = [' ', '\\.', ',', ':' , '-'];
		var tokens = text.split(new RegExp(separators.join('|'), 'g'));
		if(tokens.length != 4){
			var text = text.replace(/ /g, '');
			document.getElementById('text').value = text;
			tokens = text.split(new RegExp(separators.join('|'), 'g'));
			if(tokens.length != 4){
				errorMessage = errorMessage.concat("Invalid Message Content. \n");
				flag = false;	
			}	
		}
		
		if(tokens.length == 4){
			var check = true;
			<% for( int i = 0 ; i<4 ; i++) {%>
			      var isnum = /^\d+$/.test(tokens[<%=i%>]);
			      if(!isnum){
			      	alert(tokens[<%=i%>]);
					check = false;
			      }	
			<%}%>
			if(check == false){
				errorMessage = errorMessage.concat("Invalid Message Content. \n");
				flag = false;
			}
		}
		
	}
	if(flag == false){
		errorMessage = errorMessage.concat("Please fix above error(s) to continue. \n");
		alert(errorMessage);
		return false; 
	} 
}

</script>
</head>
<body onload="load()">

<h2>Add new Message</h2>

<table align="center">
<tr><td>
<div align="center" class="divCenter">
<form name="view_form" action="DashboardServlet" method="post" ONSUBMIT="return addMessage()">
<table>
<tr>
<td>Date:</td>
<td><input type="date" name="dateFrom" id="dateFrom"></td>
</tr>
<tr>
<td>Username:</td>
<td>
   <select name="usernames" id="usernames">
   </select>
</td>
</tr>
<tr>
<td>Message</td>
<td>
<input type="text" name="text" id="text"><br>
</td>
</tr>
</table>
<font color="red">
<%
if(request.getAttribute("success") != null){
%>	
   <%=request.getAttribute("success")%>
   <br>
<% 	
}
%>
</font>
<input type="hidden" id="user" name="user" value = "<%=userName%>">
<input type="hidden" id="reqType" name="reqType" value = "Add">
<br>
<input type="submit" value="Add Message" >
</form>
<font size=2>
Message Format: <br>
<i>  TotalScreened,TotalSuspect,TotalNonSuspect,TotalSputumSubmitted </i>
</font>
</div>
</td></tr>
</table>
</body>
</html>
