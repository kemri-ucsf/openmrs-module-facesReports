package org.openmrs.module.FacesRegister;

import org.openmrs.Patient;

public class PatientStatus {
	
	private Patient patient;
	private String cd4;
	private String weight;
	private String tbStatus;
	
	
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getCd4() {
		return cd4;
	}
	public void setCd4(String cd4) {
		this.cd4 = cd4;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getTbStatus() {
		return tbStatus;
	}
	public void setTbStatus(String tbStatus) {
		this.tbStatus = tbStatus;
	}
	
	
}
