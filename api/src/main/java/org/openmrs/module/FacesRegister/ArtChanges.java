package org.openmrs.module.FacesRegister;

import java.util.Date;

import org.openmrs.Patient;

public class ArtChanges {

	private Patient patient;
	private Date subDate;
	private String subReg;
	private String subReason;
	
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Date getSubDate() {
		return subDate;
	}
	public void setSubDate(Date subDate) {
		this.subDate = subDate;
	}
	public String getSubReg() {
		return subReg;
	}
	public void setSubReg(String subReg) {
		this.subReg = subReg;
	}
	public String getSubReason() {
		return subReason;
	}
	public void setSubReason(String subReason) {
		this.subReason = subReason;
	}
	
	
}
