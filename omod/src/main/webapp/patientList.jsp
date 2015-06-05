<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/FacesRegister/patientList.form" />
<h2>
	<spring:message code="FacesRegistermodule.replace.this.link.name" />
</h2>
<br/>

	<!--<table border="1" cellPadding="2">  -->
<table class="box">
  <tr>
   <th>Serial Counter</th>
   <th>ART Start Date</th>
   <th>Patient Name</th>
   <th>Identifier</th>
   <th>Gender</th>
   <th>Date of Birth</th>
   <th>Age</th>
   <th>Reason for Eligibility</th>
   <th>WHO Stage</th>
   <th>CD4 Value</th>
   <th>Weight (KGS)</th>
   <th>CTX Prophylaxis Start</th>
   <th>INH Start </th>
   <th>TB Treatment Start </th>
   <th>Original Regimen</th>
   <c:forEach var="i" begin="0" end="10">
        	<th>Month-${i}</th>
        </c:forEach>
  </tr>
  <c:forEach var="pat" items="${artLocationResults}">
      <tr class="evenRow">
        <td>${pat.id}</td>
        <td>${pat.artStartDate}</td>
        <td>${pat.patient.personName}</td>
        <td>${pat.patient.patientIdentifier}</td>
        <td>${pat.patient.gender}</td>
        <td>${pat.patient.birthdate}</td>
        <td>${pat.patient.age}</td>
        <td>${pat.eligibilityReason}</td>
        <td>${pat.whoStage}</td>
        <td>${pat.cd4Count}</td>
        <td>${pat.weightStartArt}</td>
        <td>${pat.ctxStart}</td>
        <td>${pat.inhStart}</td>
        <td>${pat.tbStart}</td>
        <td>${pat.originalReg}</td>
       
       <!-- 
        <c:forEach var="monthly" items="${pat.monthlyVisit}" varStatus="status" begin="0" end="10">
        	<td>${monthly[${status.count}]}</td>
        </c:forEach>
         -->
      </tr>		
  </c:forEach>
</table>


<%@ include file="/WEB-INF/template/footer.jsp"%>
