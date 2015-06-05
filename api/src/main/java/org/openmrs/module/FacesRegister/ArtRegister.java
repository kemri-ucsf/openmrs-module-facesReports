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
package org.openmrs.module.FacesRegister;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class ArtRegister extends BaseOpenmrsObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Patient patient;
	private Date artStart;
	private String artStartDate;
	private String whoStage;
	private String eligibilityReason;
	private String originalReg;
	private String weightStartArt;
	private String heightStartArt;
	private String cd4Count;
	private String ctxStart;
	private String tbStart;
	private String inhStart;
	private List<String> monthlyVisit;
	private Integer nMonths;
	private List<PatientStatus> sixMonthlyStatus; //patient status at end of every sixth month (6,12,18,24,30,36,42,48)
	private List<ArtChanges> firstLineSub;
	private List<ArtChanges> secondLineSub;
	private ArtChanges secondLineReg;
	private List<String> patientStatus; //STOP, DEAD, LOST, TO
	

	

	public List<String> getPatientStatus() {
		return patientStatus;
	}

	public void setPatientStatus(List<String> patientStatus) {
		this.patientStatus = patientStatus;
	}

	public List<ArtChanges> getSecondLineSub() {
		return secondLineSub;
	}

	public void setSecondLineSub(List<ArtChanges> secondLineSub) {
		this.secondLineSub = secondLineSub;
	}

	public ArtChanges getSecondLineReg() {
		return secondLineReg;
	}

	public void setSecondLineReg(ArtChanges secondLineReg) {
		this.secondLineReg = secondLineReg;
	}

	public List<ArtChanges> getFirstLineSub() {
		return firstLineSub;
	}

	public void setFirstLineSub(List<ArtChanges> firstLineSub) {
		this.firstLineSub = firstLineSub;
	}

	public List<PatientStatus> getSixMonthlyStatus() {
		return sixMonthlyStatus;
	}

	public void setSixMonthlyStatus(List<PatientStatus> sixMonthlyStatus) {
		this.sixMonthlyStatus = sixMonthlyStatus;
	}

	public List<String> getMonthlyVisit() {
		return monthlyVisit;
	}

	public void setMonthlyVisit(List<String> monthlyVisit) {
		this.monthlyVisit = monthlyVisit;
	}

	public Integer getnMonths() {
		return nMonths;
	}

	public void setnMonths(Integer nMonths) {
		this.nMonths = nMonths;
	}

	public String getTbStart() {
		return tbStart;
	}

	public void setTbStart(String tbStart) {
		this.tbStart = tbStart;
	}

	public String getInhStart() {
		return inhStart;
	}

	public void setInhStart(String inhStart) {
		this.inhStart = inhStart;
	}

	public String getArtStartDate() {
		return artStartDate;
	}

	public void setArtStartDate(String artStartDate) {
		this.artStartDate = artStartDate;
	}

	public String getCtxStart() {
		return ctxStart;
	}

	public void setCtxStart(String ctxStart) {
		this.ctxStart = ctxStart;
	}

	public String getEligibilityReason() {
		return eligibilityReason;
	}
	public void setEligibilityReason(String eligibilityReason) {
		this.eligibilityReason = eligibilityReason;
	}
	
	public String getOriginalReg() {
		return originalReg;
	}

	public void setOriginalReg(String originalReg) {
		this.originalReg = originalReg;
	}

	public String getCd4Count() {
		return cd4Count;
	}

	public void setCd4Count(String cd4Count) {
		this.cd4Count = cd4Count;
		
		
	}

	public String getWeightStartArt() {
		return weightStartArt;
	}

	public void setWeightStartArt(String weightStartArt) {
		this.weightStartArt = weightStartArt;
	}

	public String getHeightStartArt() {
		return heightStartArt;
	}

	public void setHeightStartArt(String heightStartArt) {
		this.heightStartArt = heightStartArt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
	public String getWhoStage() {
		return whoStage;
		
	}

	public void setWhoStage(String whoStage) {
		this.whoStage = whoStage;
	}

	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		//patient.g
		return patient;
	}

	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the artStart
	 */
	public Date getArtStart() {
		return artStart;
	}

	/**
	 * @param artStart the artStart to set
	 */
	public void setArtStart(Date artStart) {
		this.artStart = artStart;
	}

	
	
}