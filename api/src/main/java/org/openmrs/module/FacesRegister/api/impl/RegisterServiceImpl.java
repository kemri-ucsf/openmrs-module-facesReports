/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.FacesRegister.api.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.FacesRegister.ArtChanges;
import org.openmrs.module.FacesRegister.PatientStatus;
import org.openmrs.module.FacesRegister.api.RegisterService;
import org.openmrs.module.FacesRegister.api.db.RegisterDAO;

/**
 * It is a default implementation of {@link RegisterService}.
 */
public class RegisterServiceImpl extends BaseOpenmrsService implements RegisterService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RegisterDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(RegisterDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public RegisterDAO getDao() {
	    return dao;
    }
    
    public HashMap<Patient, String> getPatientStatusByMonth(List<Person> who, Date endDate,Integer monthN)
    {
    	return dao.getPatientStatus(who, endDate, monthN);
    }
    public HashMap<Patient,ArtChanges> getSecondlineHAARTReg(List<Person> who)
    {
    	return dao.getSecondlineReg(who);
    }
    public List<ArtChanges> getSecondLineHAARTSub(List<Person> who)
    {
    	return dao.getSecondLineSub(who);
    }
    public List<ArtChanges> getFirstLineHAARTsub(List<Person> who){
    	return dao.getFirstLineSub(who);
    }
    
    public HashMap<Patient,PatientStatus> getSixMonthStatus(List<Person>who, Date startDate,Integer n1,Integer n2)
    {
    	return dao.getSixMonthStatus(who, startDate, n1, n2);
    }
    public HashMap<Patient, String> getMonthlyReg(List<Person> who, Date endDate,Integer monthN )
    {
    	return dao.getMonthlyReg(who, endDate, monthN);
    }
    public Integer getMonths(Date endDate)
    {
    	return dao.nMonths(endDate);
    }
    
    public List<Patient> getPatientsInLocation(int locationId)
    {
    	//get a list of patients in the selected location
    	return dao.getPatientsInLocation(locationId);
    }
    
    public HashMap<Patient, String> getINHStart(List<Person> who)
    {
    	/*
    	 * Call get INH start List DAO method
    	 * 
    	 */
    	return dao.getINHStart(who);
    }
    public HashMap<Patient, String> getTBStart(List<Person> who)
    {
    	/*
    	 * Call get TB start List DAO method
    	 */
    	return dao.getTbRxStart(who);
    }
    
	public HashMap<Patient,String> getPatientsGivenArtDates(Integer locationId,Date startDate, Date endDate)
	{
		//get a list of patients starting art within a the selected period
		return dao.getPatientsGivenArtDates(locationId,startDate, endDate);
	}
	
	public HashMap<Patient,Date> getArtStartDates(List<Person> whom,int locationId)
	{
		//Get clients' ART start dates
		return dao.artStartDates(whom,locationId);
	}
	public HashMap<Patient, String> getStatusatStartArtWHO(List<Person> whom,Date fromDate, Date toDate)
	{
	
		//get the clients WHo stage at the start of ART 
		HashMap<Patient, String> whoObsList=dao.getWHOatStart(whom,fromDate, toDate);
		
		return whoObsList;
	}
	public HashMap<Patient, String> getCD4CountAtStart(List<Person> whom,Date fromDate, Date toDate)
	{
		/*
		 * Cd4 count concepts
		 * 5497 -- cd4 count
		 * 6314 -- eligibility cd4 count
		 * 6313 -- eligibility cd4 percent
		 * 730 -- Cd Percent
		 */			
		//Get the clients' cd4 count at the start of ART
		HashMap<Patient, String> cd4ObsList=dao.getCD4atStart(whom, fromDate, toDate);
		return cd4ObsList;
	}
	
	public HashMap<Patient, String> getEligibility(List<Person> whom,Date fromDate, Date toDate)
	{
		//Get clients eligibility criteria
		HashMap<Patient, String> eligibleCriteria=dao.getEligibilityCriteria(whom, toDate);
		return eligibleCriteria;
	}
	public HashMap<Patient, String> getWeightatStart(List<Person> whom,Date fromDate, Date toDate)
	{
		//get clients' weight at the start of ART
		HashMap<Patient, String> weightList=dao.getWeightatStart(whom, fromDate, toDate);
		return weightList;
	}
	public HashMap<Patient, String> getHeightatStart(List<Person> who,Date fromDate, Date toDate)
	{
		HashMap<Patient, String> heightList=dao.getHeightatStart(who, fromDate, toDate);
		return heightList;
	}
	public HashMap<Patient, String> getOriginalRegimen(List<Person> who)
	{
		// get Original Regimen for patients within the selected cohort
		HashMap<Patient, String> patientRegList=dao.getOriginalRegimen(who);
		return patientRegList;
	}
	
	public HashMap<Patient, String> getCtxStart(List<Person> who)
	{
		// get the month and year the patient was started on ctx
		HashMap<Patient, String> patientCtxList=dao.getCtxStart(who);
		return patientCtxList;
	}
}