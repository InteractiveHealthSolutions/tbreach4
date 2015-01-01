<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="header.jsp" %> 
<%@ page import="com.ihsresearch.tbr4web.server.MobileService"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">




function importMessage()
{
   document.getElementById("status_span").innerHTML="... Importing ..."; 
   alert('Importing... Please wait.');
   document.import_form.submit();   
}


</script>
</head>
<body>


<h2>Import Messages from SMSTarseel</h2>

<table align="center">
<tr><td>
<div align="center" class="divCenter">
Last Import on: <i>  <%= MobileService.getService().getLastImport() %> SAST</i>

<form name="import_form" action="DashboardServlet" method="post" ONSUBMIT="return importMessage()">
<br>
<font size="3">'Click on import button to import new messages from SMSTarseel'</font>
<input type="hidden" id="reqType" name="reqType" value = "Import">
<input type="submit" value="Import" >
<br>
<br>
<span id="status_span"><% if (request.getAttribute ("imported") != null) %> Messages imported successfully. </span>
</form>

</div>
</td></tr>

<tr><td>
<br>
</td></tr>

<tr><td>
<center><b><font size=5>Screener's List</font></b></center>
</td></tr>

<tr><td>
<div align="center">

<table class="myTable">
<tr>
<th>NAME</th>
<th>LOCATION</th>
<th>PRIMARY NUMBER</th>
<th>SECONDARY NUMBER</th>
</tr>

<%
String screenerList[][] = MobileService.getService().getAllScreeners();
for(int i = 0; i <screenerList.length; i++){
%>

<tr>
<td> <%=screenerList[i][1]%> <br> (<%=screenerList[i][0]%>) </td>
<td> <%=screenerList[i][2]%> </td>
<td> <%=screenerList[i][3]%> </td>
<td> <%=screenerList[i][4]%> </td>
</tr>	
<%	
}
%>

</table>

</div>
</td></tr>

</table>

</body>

</html>