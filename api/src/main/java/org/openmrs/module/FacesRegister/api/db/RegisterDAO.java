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
package org.openmrs.module.FacesRegister.api.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.FacesRegister.ArtChanges;
import org.openmrs.module.FacesRegister.PatientStatus;
import org.openmrs.module.FacesRegister.api.RegisterService;

/**
 *  Database methods for {@link RegisterService}.
 */
public interface RegisterDAO {
	
	/*
	 * Add DAO methods here
	 */
	public List<Patient> getPatientsInLocation(Integer locationId);
	public HashMap<Patient,String> getPatientsGivenArtDates(Integer locationId,Date startDate, Date endDate);
	public HashMap<Patient,Date> artStartDates(List<Person> who,Integer locationId);
	public void query(Integer locationId,Date startDate, Date endDate);
	public HashMap<Patient,String> getEligibilityCriteria(List<Person> who, Date endDate);//gets reason for eligibility
	public List<Obs> getObsGivenDates(List<Person> whom,List<Concept>questions,Date fromDate, Date toDate);
	public HashMap<Patient, String> getWHOatStart(List<Person> who,Date fromDate, Date toDate);
	public HashMap<Patient, String> getCD4atStart(List<Person> who,Date fromDate, Date toDate);
	public HashMap<Patient, String> getWeightatStart(List<Person> who,Date fromDate, Date toDate);
	public HashMap<Patient, String> getOriginalRegimen(List<Person> who);
	public HashMap<Patient, String> getCtxStart(List<Person> who);
	public HashMap<Patient, String> getTbRxStart (List<Person> who);
	public HashMap<Patient, String> getINHStart(List<Person> who);
	public Integer nMonths (Date endDate);
	public HashMap<Patient, String> getMonthlyReg(List<Person>who,Date endDate, Integer monthN);
	public HashMap<Patient,PatientStatus> getSixMonthStatus(List<Person>who, Date startDate,Integer n1,Integer n2);
	public List<ArtChanges> getFirstLineSub(List<Person> who);
	public HashMap<Patient,ArtChanges> getSecondlineReg(List<Person> who);
	public List<ArtChanges> getSecondLineSub(List<Person> who);
	public HashMap<Patient, String> getHeightatStart(List<Person> who,Date fromDate, Date toDate);
	public HashMap<Patient, String> getPatientStatus(List<Person> who, Date endDate,Integer monthN);
}