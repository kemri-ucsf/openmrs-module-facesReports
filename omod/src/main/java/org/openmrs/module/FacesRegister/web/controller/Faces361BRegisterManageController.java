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
package org.openmrs.module.FacesRegister.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.FacesRegister.ArtChanges;
import org.openmrs.module.FacesRegister.ArtRegister;
import org.openmrs.module.FacesRegister.PatientStatus;
import org.openmrs.module.FacesRegister.RegisterRenderer;
import org.openmrs.module.FacesRegister.api.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The main controller.
 */
@Controller
public class  Faces361BRegisterManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/FacesRegister/manage", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		//model.addAttribute("user", Context.getAuthenticatedUser());
		model.addAttribute("Facility", Context.getLocationService().getAllLocations(true)); //all locations that are not voided 
	}
	@RequestMapping(value = "/module/FacesRegister/excelDownload", method = RequestMethod.GET)
	public void downLoadExcel(ModelMap model) {
		//model.addAttribute("user", Context.getAuthenticatedUser());
		model.addAttribute("Facility", Context.getLocationService().getAllLocations(true)); //all locations that are not voided 
	}
	@RequestMapping(value = "/module/FacesRegister/patientList", method = RequestMethod.POST)
    public void artStart(Model model,  
    							@RequestParam(value="location", required=true) String location, 
    							@RequestParam(value="Startdate", required=true) String artsDate,
    							@RequestParam(value="Enddate", required=true) String cendDate) {
		
        // Do Stuff with the location, and date
		
		log.info(location);
		log.info(artsDate);
		log.info(location);
		log.info(cendDate);
		log.info(dateFormat(cendDate));
		RegisterService service = Context.getService(RegisterService.class);
		Location l=Context.getLocationService().getLocation(location); //get the Id for the selected site/facility
		log.info("Selected Location "+l.getLocationId()+" - "+ l.getName());
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("dd/mm/yyyy");
			HashMap<Patient, String> patients=service.getPatientsGivenArtDates(l.getLocationId(), sdf.parse(artsDate), sdf.parse(cendDate));
			//Convert patients to persons
			List<Person> person=new ArrayList<Person>();
			for(Patient px:patients.keySet())
				person.add(px);
					
			List<ArtRegister> artRegister=new ArrayList<ArtRegister>();
			//HashMap<Patient, Date> artdates=service.getArtStartDates(person,l.getLocationId());
			HashMap<Patient, String> whoStage=service.getStatusatStartArtWHO(person, sdf.parse(artsDate), sdf.parse(cendDate));
			//HashMap<Patient, String>cd4=service.getCD4CountAtStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> cd4Count=service.getCD4CountAtStart(person, sdf.parse(artsDate),sdf.parse(cendDate));
			HashMap<Patient, String> weight=service.getWeightatStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> originalRegimen=service.getOriginalRegimen(person);
			HashMap<Patient, String> ctx=service.getCtxStart(person);
			HashMap<Patient, String> tb=service.getTBStart(person);
			HashMap<Patient, String> inh=service.getINHStart(person);
			HashMap<Patient, String>monthly[];
			Integer n= service.getMonths(sdf.parse(cendDate));
			monthly=new HashMap[n];
			for (int i=0;i<n;i++)
			{
				monthly[i]=service.getMonthlyReg(person, sdf.parse(cendDate), i+1);
			}
			
			int serial=1;
			
			for(Patient p: patients.keySet())
			{
				ArtRegister artRcd=new ArtRegister();
				artRcd.setPatient(p);
				artRcd.setId(serial);
				artRcd.setArtStartDate(patients.get(p));
				artRcd.setWhoStage(whoStage.get(p));
				artRcd.setCd4Count(cd4Count.get(p));
				artRcd.setWeightStartArt(weight.get(p));
				artRcd.setOriginalReg(originalRegimen.get(p));
				artRcd.setCtxStart(ctx.get(p));
				artRcd.setInhStart(inh.get(p));
				artRcd.setTbStart(tb.get(p));
				artRcd.setnMonths(n);
				/*
				 * Creating a list of monthly regimen prescriptions for the current patient
				 */
				List<String> monthlyReg = new ArrayList<String>();
				for(int x=0;x<n;x++)
				{
					monthlyReg.add(monthly[x].get(p));
				}
				artRcd.setMonthlyVisit(monthlyReg);
				
				serial++;
				artRegister.add(artRcd);
				
			}
			//model.addAttribute("artLocationResults", patients); 
			model.addAttribute("artLocationResults", artRegister);
	       // RedirectView rv = new RedirectView("/module/FacesRegister/manage");
	        //return rv;  
		}
		catch(Exception e)
		{
			log.error("Error "+ e.getMessage()+" \n "+e.fillInStackTrace());
		}
		
            
    }
	private String dateFormat(String s)
	{
		/*
		 * this method formats the date string
		 * it takes input in the format of dd/mm/yyyy and returns yyyy-mm-dd
		 * This is done so that we can ensure correct sql date format
		 */
		String temp="";
		StringBuffer st=new StringBuffer(s);
		for(int i=st.length()-1;i>=0;i--)
		{
			if(st.charAt(i)=='/')
				st.replace(i, i+1, "-"); //replace the slash "/" with the dash "-"
		}
		temp=st.substring(6, 10)+"-"+st.substring(3, 5)+"-"+st.substring(0, 2); // format date (yyyy-mm-dd)
		return temp;
		
	}
	
	@RequestMapping(value="/module/FacesRegister/download",method = RequestMethod.POST) //called when the user clicks on the download link
	public void getCSV(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="location", required=true) String location, 
			@RequestParam(value="Startdate", required=true) String artsDate,
			@RequestParam(value="Enddate", required=true) String cendDate)  {
	
 // Do Stuff with the location, and date
		
		log.info(location);
		log.info(artsDate);
		log.info(location);
		log.info(cendDate);
		log.info(dateFormat(cendDate));
		RegisterService service = Context.getService(RegisterService.class);
		Location l=Context.getLocationService().getLocation(location); //get the Id for the selected site/facility
		log.info("Selected Location "+l.getLocationId()+" - "+ l.getName());
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("dd/mm/yyyy");
			HashMap<Patient, String> patients=service.getPatientsGivenArtDates(l.getLocationId(), sdf.parse(artsDate), sdf.parse(cendDate));
			//Convert patients to persons
			List<Person> person=new ArrayList<Person>();
			for(Patient px:patients.keySet())
				person.add(px);
					
			List<ArtRegister> artRegister=new ArrayList<ArtRegister>();
			//HashMap<Patient, Date> artdates=service.getArtStartDates(person,l.getLocationId());
			HashMap<Patient, String> whoStage=service.getStatusatStartArtWHO(person, sdf.parse(artsDate), sdf.parse(cendDate));
			//HashMap<Patient, String>cd4=service.getCD4CountAtStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> cd4Count=service.getCD4CountAtStart(person, sdf.parse(artsDate),sdf.parse(cendDate));
			HashMap<Patient, String> weight=service.getWeightatStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> height=service.getWeightatStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> originalRegimen=service.getOriginalRegimen(person);
			HashMap<Patient, ArtChanges> secondLineRegList=service.getSecondlineHAARTReg(person);
			HashMap<Patient, String> ctx=service.getCtxStart(person);
			HashMap<Patient, String> tb=service.getTBStart(person);
			HashMap<Patient, String> inh=service.getINHStart(person);
			List<ArtChanges> firstlineSub=service.getFirstLineHAARTsub(person);
			List<ArtChanges> secondlineSub=service.getSecondLineHAARTSub(person);
			HashMap<Patient, String> monthly[];
			HashMap<Patient, String> patientMonthlyStatus[];
			Integer n= service.getMonths(sdf.parse(cendDate));
			monthly=new HashMap[n];
			
			for (int i=0;i<n;i++)
			{
				monthly[i]=service.getMonthlyReg(person, sdf.parse(cendDate), i+1);
			}
			
			patientMonthlyStatus=new HashMap[n];
			for (int i=0;i<n;i++)
			{
				patientMonthlyStatus[i]=service.getPatientStatusByMonth(person, sdf.parse(cendDate), i+1);
			}
			/*
			 * Get six month interval patient status
			 * n guides the number six monthly status that can be fetched  
			 */
						
			HashMap<Patient,PatientStatus> sixMonStatus[]=new HashMap[8];//
			if (n>=6) // get month 6 status (Cd4, tb status and weight)
				sixMonStatus[0]=service.getSixMonthStatus(person, sdf.parse(artsDate), 0, 6);
			if (n>=12) // get month 12 status (Cd4, tb status and weight)
				sixMonStatus[1]=service.getSixMonthStatus(person, sdf.parse(artsDate), 6, 12);
			if (n>=18) // get month 18 status (Cd4, tb status and weight)
				sixMonStatus[2]=service.getSixMonthStatus(person, sdf.parse(artsDate), 12, 18);
			if (n>=24) // get month 24 status (Cd4, tb status and weight)
				sixMonStatus[3]=service.getSixMonthStatus(person, sdf.parse(artsDate), 18, 24);
			if (n>=30) // get month 30 status (Cd4, tb status and weight)
				sixMonStatus[4]=service.getSixMonthStatus(person, sdf.parse(artsDate), 24, 30);
			if (n>=36) // get month 36 status (Cd4, tb status and weight)
				sixMonStatus[5]=service.getSixMonthStatus(person, sdf.parse(artsDate), 30, 36);
			if (n>=42) // get month 42 status (Cd4, tb status and weight)
				sixMonStatus[6]=service.getSixMonthStatus(person, sdf.parse(artsDate), 36, 42);
			if (n>=48) // get month 48 status (Cd4, tb status and weight)
				sixMonStatus[7]=service.getSixMonthStatus(person, sdf.parse(artsDate), 42, 48);
			
			
			int serial=1;
			
			for(Patient p: patients.keySet())
			{
				ArtRegister artRcd=new ArtRegister();
				artRcd.setPatient(p);
				artRcd.setId(serial);
				artRcd.setArtStartDate(patients.get(p));
				artRcd.setWhoStage(whoStage.get(p));
				artRcd.setCd4Count(cd4Count.get(p));
				artRcd.setWeightStartArt(weight.get(p));
				artRcd.setHeightStartArt(height.get(p));
				artRcd.setOriginalReg(originalRegimen.get(p));
				artRcd.setCtxStart(ctx.get(p));
				artRcd.setInhStart(inh.get(p));
				artRcd.setTbStart(tb.get(p));
				artRcd.setnMonths(n);
				
				/*
				 * Creating a list of monthly regimen prescriptions for the current patient
				 */
				List<String> monthlyReg = new ArrayList<String>();
				for(int x=0;x<n;x++)
				{
					monthlyReg.add(monthly[x].get(p));
				}
				artRcd.setMonthlyVisit(monthlyReg);
				
				/*
				 * Creating a list of monthly patient status STOP, DEAD, LOST or TO
				 */
				List<String> monthlyStatus = new ArrayList<String>();
				for(int x=0;x<n;x++)
				{
					monthlyStatus.add(patientMonthlyStatus[x].get(p));
				}
				artRcd.setPatientStatus(monthlyStatus);
				
				/*
				 * Creating a list of current patient first HAART sub
				 */
				List<ArtChanges> curPatientHaartSubs = new ArrayList<ArtChanges>();
				for (int x=0; x<firstlineSub.size();x++)
				{
					if(p.equals(firstlineSub.get(x).getPatient()))
						curPatientHaartSubs.add(firstlineSub.get(x));
				}
				artRcd.setFirstLineSub(curPatientHaartSubs);
				
				/*
				 * Creating a list of current patient second line HAART substitutions
				 */
				List<ArtChanges> curPatientSecondSubs = new ArrayList<ArtChanges>();
				for (int x=0; x<secondlineSub.size();x++)
				{
					if(p.equals(secondlineSub.get(x).getPatient()))
						curPatientSecondSubs.add(secondlineSub.get(x));
				}
				artRcd.setSecondLineSub(curPatientSecondSubs);
				
				/*
				 * Get six month interval patient status
				 * n guides the number monthly status fetched  
				 */
				//month 6 status
				List<PatientStatus> curPatStatus=new ArrayList<PatientStatus>();
				if (n>=6)
					curPatStatus.add(sixMonStatus[0].get(p));
				if (n>=12)
					curPatStatus.add(sixMonStatus[1].get(p));
				if (n>=18)
					curPatStatus.add(sixMonStatus[2].get(p));
				if (n>=24)
					curPatStatus.add(sixMonStatus[3].get(p));
				if (n>=30)
					curPatStatus.add(sixMonStatus[4].get(p));
				if (n>=36)
					curPatStatus.add(sixMonStatus[5].get(p));
				if (n>=42)
					curPatStatus.add(sixMonStatus[6].get(p));
				if (n>=48)
					curPatStatus.add(sixMonStatus[7].get(p));
				
				artRcd.setSixMonthlyStatus(curPatStatus);
				
				/*
				 * Get second line regimen
				 * 
				 */
				artRcd.setSecondLineReg(secondLineRegList.get(p));
				
				serial++;
				artRegister.add(artRcd);
				
			}
			
			RegisterRenderer renderer = new RegisterRenderer();
			 //call the save/open dialog box of the web browser
			  response.setHeader("Content-Disposition", "attachment;filename=ART_REGISTER_361B.csv");
			  response.setContentType("application/csv");	
			  try {
				//response.getWriter().write(csv.toString()); // this is usually called when using string builder and should be placed in the controller class. see the definition of string builder in render2 in CSVRenderer
				renderer.csvRender(artRegister, response.getOutputStream(),n,sdf.parse(cendDate));//call the render method from CsvRenderer
			  	} 
			  catch (IOException e) {
				// TODO Auto-generated catch block
				log.info("Error when creating the CSV file");
			}
		}
		catch(Exception e)
		{
			log.error("Error "+ e.getMessage()+" \n "+e.fillInStackTrace());
		}
	  
	
}
	@RequestMapping(value="/module/FacesRegister/downloadExcel",method = RequestMethod.POST) //called when the user clicks on the download link
	public void getExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="location", required=true) String location, 
			@RequestParam(value="Startdate", required=true) String artsDate,
			@RequestParam(value="Enddate", required=true) String cendDate)  {
	
 // Do Stuff with the location, and date
		
		log.info(location);
		log.info(artsDate);
		log.info(location);
		log.info(cendDate);
		log.info(dateFormat(cendDate));
		RegisterService service = Context.getService(RegisterService.class);
		Location l=Context.getLocationService().getLocation(location); //get the Id for the selected site/facility
		log.info("Selected Location "+l.getLocationId()+" - "+ l.getName());
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("dd/mm/yyyy");
			HashMap<Patient, String> patients=service.getPatientsGivenArtDates(l.getLocationId(), sdf.parse(artsDate), sdf.parse(cendDate));
			//Convert patients to persons
			List<Person> person=new ArrayList<Person>();
			for(Patient px:patients.keySet())
				person.add(px);
					
			List<ArtRegister> artRegister=new ArrayList<ArtRegister>();
			//HashMap<Patient, Date> artdates=service.getArtStartDates(person,l.getLocationId());
			HashMap<Patient, String> whoStage=service.getStatusatStartArtWHO(person, sdf.parse(artsDate), sdf.parse(cendDate));
			//HashMap<Patient, String>cd4=service.getCD4CountAtStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> cd4Count=service.getCD4CountAtStart(person, sdf.parse(artsDate),sdf.parse(cendDate));
			HashMap<Patient, String> weight=service.getWeightatStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> height=service.getWeightatStart(person, sdf.parse(artsDate), sdf.parse(cendDate));
			HashMap<Patient, String> originalRegimen=service.getOriginalRegimen(person);
			HashMap<Patient, ArtChanges> secondLineRegList=service.getSecondlineHAARTReg(person);
			HashMap<Patient, String> ctx=service.getCtxStart(person);
			HashMap<Patient, String> tb=service.getTBStart(person);
			HashMap<Patient, String> inh=service.getINHStart(person);
			List<ArtChanges> firstlineSub=service.getFirstLineHAARTsub(person);
			List<ArtChanges> secondlineSub=service.getSecondLineHAARTSub(person);
			HashMap<Patient, String> monthly[];
			HashMap<Patient, String> patientMonthlyStatus[];
			Integer n= service.getMonths(sdf.parse(cendDate));
			monthly=new HashMap[n];
			
			for (int i=0;i<n;i++)
			{
				monthly[i]=service.getMonthlyReg(person, sdf.parse(cendDate), i+1);
			}
			
			patientMonthlyStatus=new HashMap[n];
			for (int i=0;i<n;i++)
			{
				patientMonthlyStatus[i]=service.getPatientStatusByMonth(person, sdf.parse(cendDate), i+1);
			}
			/*
			 * Get six month interval patient status
			 * n guides the number six monthly status that can be fetched  
			 */
						
			HashMap<Patient,PatientStatus> sixMonStatus[]=new HashMap[8];//
			if (n>=6) // get month 6 status (Cd4, tb status and weight)
				sixMonStatus[0]=service.getSixMonthStatus(person, sdf.parse(artsDate), 0, 6);
			if (n>=12) // get month 12 status (Cd4, tb status and weight)
				sixMonStatus[1]=service.getSixMonthStatus(person, sdf.parse(artsDate), 6, 12);
			if (n>=18) // get month 18 status (Cd4, tb status and weight)
				sixMonStatus[2]=service.getSixMonthStatus(person, sdf.parse(artsDate), 12, 18);
			if (n>=24) // get month 24 status (Cd4, tb status and weight)
				sixMonStatus[3]=service.getSixMonthStatus(person, sdf.parse(artsDate), 18, 24);
			if (n>=30) // get month 30 status (Cd4, tb status and weight)
				sixMonStatus[4]=service.getSixMonthStatus(person, sdf.parse(artsDate), 24, 30);
			if (n>=36) // get month 36 status (Cd4, tb status and weight)
				sixMonStatus[5]=service.getSixMonthStatus(person, sdf.parse(artsDate), 30, 36);
			if (n>=42) // get month 42 status (Cd4, tb status and weight)
				sixMonStatus[6]=service.getSixMonthStatus(person, sdf.parse(artsDate), 36, 42);
			if (n>=48) // get month 48 status (Cd4, tb status and weight)
				sixMonStatus[7]=service.getSixMonthStatus(person, sdf.parse(artsDate), 42, 48);
			
			
			int serial=1;
			
			for(Patient p: patients.keySet())
			{
				ArtRegister artRcd=new ArtRegister();
				artRcd.setPatient(p);
				artRcd.setId(serial);
				artRcd.setArtStartDate(patients.get(p));
				artRcd.setWhoStage(whoStage.get(p));
				artRcd.setCd4Count(cd4Count.get(p));
				artRcd.setWeightStartArt(weight.get(p));
				artRcd.setHeightStartArt(height.get(p));
				artRcd.setOriginalReg(originalRegimen.get(p));
				artRcd.setCtxStart(ctx.get(p));
				artRcd.setInhStart(inh.get(p));
				artRcd.setTbStart(tb.get(p));
				artRcd.setnMonths(n);
				
				/*
				 * Creating a list of monthly regimen prescriptions for the current patient
				 */
				List<String> monthlyReg = new ArrayList<String>();
				for(int x=0;x<n;x++)
				{
					monthlyReg.add(monthly[x].get(p));
				}
				artRcd.setMonthlyVisit(monthlyReg);
				
				/*
				 * Creating a list of monthly patient status STOP, DEAD, LOST or TO
				 */
				List<String> monthlyStatus = new ArrayList<String>();
				for(int x=0;x<n;x++)
				{
					monthlyStatus.add(patientMonthlyStatus[x].get(p));
				}
				artRcd.setPatientStatus(monthlyStatus);
				
				/*
				 * Creating a list of current patient first HAART sub
				 */
				List<ArtChanges> curPatientHaartSubs = new ArrayList<ArtChanges>();
				for (int x=0; x<firstlineSub.size();x++)
				{
					if(p.equals(firstlineSub.get(x).getPatient()))
						curPatientHaartSubs.add(firstlineSub.get(x));
				}
				artRcd.setFirstLineSub(curPatientHaartSubs);
				
				/*
				 * Creating a list of current patient second line HAART substitutions
				 */
				List<ArtChanges> curPatientSecondSubs = new ArrayList<ArtChanges>();
				for (int x=0; x<secondlineSub.size();x++)
				{
					if(p.equals(secondlineSub.get(x).getPatient()))
						curPatientSecondSubs.add(secondlineSub.get(x));
				}
				artRcd.setSecondLineSub(curPatientSecondSubs);
				
				/*
				 * Get six month interval patient status
				 * n guides the number monthly status fetched  
				 */
				//month 6 status
				List<PatientStatus> curPatStatus=new ArrayList<PatientStatus>();
				if (n>=6)
					curPatStatus.add(sixMonStatus[0].get(p));
				if (n>=12)
					curPatStatus.add(sixMonStatus[1].get(p));
				if (n>=18)
					curPatStatus.add(sixMonStatus[2].get(p));
				if (n>=24)
					curPatStatus.add(sixMonStatus[3].get(p));
				if (n>=30)
					curPatStatus.add(sixMonStatus[4].get(p));
				if (n>=36)
					curPatStatus.add(sixMonStatus[5].get(p));
				if (n>=42)
					curPatStatus.add(sixMonStatus[6].get(p));
				if (n>=48)
					curPatStatus.add(sixMonStatus[7].get(p));
				
				artRcd.setSixMonthlyStatus(curPatStatus);
				
				/*
				 * Get second line regimen
				 * 
				 */
				artRcd.setSecondLineReg(secondLineRegList.get(p));
				
				serial++;
				artRegister.add(artRcd);
				
			}
			
			RegisterRenderer renderer = new RegisterRenderer();
			 //call the save/open dialog box of the web browser
			  response.setHeader("Content-Disposition", "attachment;filename=/resources/RegisterTemplate.xls");
			  response.setContentType("application/vnd.ms-excel");	
			  try {
				//response.getWriter().write(csv.toString()); // this is usually called when using string builder and should be placed in the controller class. see the definition of string builder in render2 in CSVRenderer
				  renderer.excelRender((List)patients.keySet(), response.getOutputStream());//call the render method from ExcelRenderer
			  	} 
			  catch (IOException e) {
				// TODO Auto-generated catch block
				log.info("Error when creating the CSV file");
			}
		}
		catch(Exception e)
		{
			log.error("Error "+ e.getMessage()+" \n "+e.fillInStackTrace());
		}
	  
	
}

}
