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
package org.openmrs.module.FacesRegister.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.FacesRegister.ArtChanges;
import org.openmrs.module.FacesRegister.PatientStatus;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(RegisterService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface RegisterService extends OpenmrsService {
     
	/*
	 * Add service methods here
	 * 
	 */
	public List<Patient> getPatientsInLocation(int locationId);
	public HashMap<Patient, String> getPatientsGivenArtDates(Integer locationId,Date startDate, Date endDate);
	public HashMap<Patient, Date> getArtStartDates(List<Person> whom,int locationId);
	public HashMap<Patient, String> getStatusatStartArtWHO(List<Person> whom,Date fromDate, Date toDate);
	public HashMap<Patient, String> getWeightatStart(List<Person> whom,Date fromDate, Date toDate);
	public HashMap<Patient, String> getEligibility(List<Person> whom,Date fromDate, Date toDate);
	public HashMap<Patient, String> getCD4CountAtStart(List<Person> whom,Date fromDate, Date toDate);
	public HashMap<Patient, String> getOriginalRegimen(List<Person> who);
	public HashMap<Patient, String> getCtxStart(List<Person> who);
	public HashMap<Patient, String> getTBStart(List<Person> who);
	public HashMap<Patient, String> getINHStart(List <Person> who);
	public Integer getMonths(Date endDate);
	public HashMap<Patient, String> getMonthlyReg(List<Person> who, Date endDate, Integer monthN);
	public List<ArtChanges> getFirstLineHAARTsub(List<Person> who);
	public HashMap<Patient,PatientStatus> getSixMonthStatus(List<Person>who, Date startDate,Integer n1,Integer n2);
	public HashMap<Patient,ArtChanges> getSecondlineHAARTReg(List<Person> who);
	public List<ArtChanges> getSecondLineHAARTSub(List<Person> who);
	public HashMap<Patient, String> getHeightatStart(List<Person> who,Date fromDate, Date toDate);
	public HashMap<Patient, String> getPatientStatusByMonth(List<Person> who, Date endDate,Integer monthN);
}