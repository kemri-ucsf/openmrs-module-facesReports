<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/FacesRegister/manage.form" />




  	<form method="post"  action="download.form">
  	<fieldset>
  	<table style="padding:20px;">
  		<legend>
  			Run or Schedule Report MOH 361 b Register
  		</legend>
		<tr>
			<td><label for="Startdate">ART Cohort Start date:</label></td>
			<td><input type="text" id="Startdate" name="Startdate" onClick="showCalendar(this)"></td>
		</tr>
		<tr>
			<td><label for="Enddate">ART Cohort End date:</label></td>
			<td><input type="text" id="Enddate" name="Enddate" onClick="showCalendar(this)"></td>
		</tr>
		<tr>
			<td><label for="Location">Location:</label></td>
			<td>
			<select name="location" id="Location">
				<c:forEach var="lst" items="${Facility}">
      				<option value="${lst.name}">
      					${lst.name}
      				</option>	
  				</c:forEach>
			</select>
		</td>
		</tr>
		<tr>
			<td colspan="2" style="padding-left:50px;"><input type="submit" value="Request Report" name="submit" id="submit"></td>
		</tr>
	</table>
  	</fieldset>
  	</form>




<%@ include file="/WEB-INF/template/footer.jsp"%>