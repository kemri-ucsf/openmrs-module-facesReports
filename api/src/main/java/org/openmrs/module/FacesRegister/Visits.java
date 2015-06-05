package org.openmrs.module.FacesRegister;

import java.util.Date;

import org.openmrs.Patient;


public class Visits {
	Patient patient;
	Date encDate;
	Date ctxStartDate;
	Date inhStartDate;
	Date tbStartDate;
	String tbNo;
	String Regimen;
	double cd4;
	int discontinued;
	String disconReason;
	float weight;
	float height;
	String tbStatus;
	String who;
	
	
    
    
    
    public Date getTbStartDate() {
    	return tbStartDate;
    }



	
    public void setTbStartDate(Date tbStartDate) {
    	this.tbStartDate = tbStartDate;
    }



	public String getWho() {
    	return who;
    }


	
    public void setWho(String who) {
    	this.who = who;
    }


	public Date getCtxStartDate() {
    	return ctxStartDate;
    }

	
    public void setCtxStartDate(Date ctxStartDate) {
    	this.ctxStartDate = ctxStartDate;
    }

	
    public Date getInhStartDate() {
    	return inhStartDate;
    }

	
    public void setInhStartDate(Date inhStartDate) {
    	this.inhStartDate = inhStartDate;
    }

	
    public String getTbNo() {
    	return tbNo;
    }

	
    public void setTbNo(String tbNo) {
    	this.tbNo = tbNo;
    }

	
    public float getHeight() {
    	return height;
    }

	
    public void setHeight(float height) {
    	this.height = height;
    }

	public Patient getPatient() {
    	return patient;
    }
	
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }
        
    public int getDiscontinued() {
    	return discontinued;
    }

	
    public void setDiscontinued(int discontinued) {
    	this.discontinued = discontinued;
    }

	
    public String getDisconReason() {
    	return disconReason;
    }

	
    public void setDisconReason(String disconReason) {
    	this.disconReason = disconReason;
    }

	public Date getEncDate() {
    	return encDate;
    }
	
    public void setEncDate(Date encDate) {
    	this.encDate = encDate;
    }
	
    public String getRegimen() {
    	return Regimen;
    }
	
    public void setRegimen(String regimen) {
    	Regimen = regimen;
    }
	
    public double getCd4() {
    	return cd4;
    }
	
    public void setCd4(double cd4) {
    	this.cd4 = cd4;
    }
	
    public float getWeight() {
    	return weight;
    }
	
    public void setWeight(float weight) {
    	this.weight = weight;
    }
	
    public String getTbStatus() {
    	return tbStatus;
    }
	
    public void setTbStatus(String tbStatus) {
    	this.tbStatus = tbStatus;
    }
	
	
}
