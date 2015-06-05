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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.FacesRegister.ArtChanges;
import org.openmrs.module.FacesRegister.ArtRegister;
import org.openmrs.module.FacesRegister.PatientStatus;
import org.openmrs.module.FacesRegister.RegisterRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests {@link ${RegisterService}}.
 */
public class  RegisterServiceTest extends BaseModuleContextSensitiveTest {
	
	/*
	 * (non-Javadoc)
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 * This method (below) allows to use openmrs runtime properties file to test ur module
	 * The runtime properties points to the database to be used on ur local machine 
	 */
	/*
	 * Authenticate the user that is running the tests
	 */
	
	@Before
	public void Authenticate()
	{
		Context.authenticate("Admin", "Admin123");     //localhost
       // Context.authenticate("openmrs", "Openmrs123");//Production serve
	}
	/*
	 * (non-Javadoc)
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 * This procedure allows you access ur local database
	 * and Override Openmrs In memory db
	 */
	
	@Override //Override the In memory database
	public Boolean useInMemoryDatabase()
	{
		return false;
	}
	
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(RegisterService.class));
	}
	
	//@Ignore
	@Test
	public void testIftheGetPatientsServiceGivenDatesisWorking()
	{
		RegisterService service = Context.getService(RegisterService.class);
		SimpleDateFormat sdf=new SimpleDateFormat("dd-mm-yyyy");
		
		try
		{
			Date d1=sdf.parse("01-01-2011");//fromDate
			Date d2=sdf.parse("31-01-2011");//toDate
			HashMap<Patient, String> list =service.getPatientsGivenArtDates(2, d1,d2);
			assertNotNull(list);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	//@Ignore
	@Test
	public void testIftheGetPatientsServiceGivenDatesReturnsSomeRecords()
	{
		RegisterService service = Context.getService(RegisterService.class);
		SimpleDateFormat sdf=new SimpleDateFormat("dd-mm-yyyy");
		
		try
		{
			Date d1=sdf.parse("01-01-2011");//fromDate
			Date d2=sdf.parse("31-01-2011");//toDate
			HashMap<Patient, String> list =service.getPatientsGivenArtDates(2, d1, d2);
			List<Person> list2=new ArrayList<Person>();
			for(Patient px:list.keySet())
				list2.add(px);
					
			List<ArtRegister> artRegister=new ArrayList<ArtRegister>();
			//HashMap<Patient, Date> artdates=service.getArtStartDates(list2,2);
			HashMap<Patient, String> originalRegimen=service.getOriginalRegimen(list2);
			HashMap<Patient, String> ctx=service.getCtxStart(list2);
			HashMap<Patient, String> whostage=service.getStatusatStartArtWHO(list2, d1, d2);
			HashMap<Patient, String> tb=service.getTBStart(list2);
			HashMap<Patient, String> inh=service.getINHStart(list2);
			HashMap<Patient, String> cd4Count=service.getCD4CountAtStart(list2, d1,d2);
			HashMap<Patient, String> weight=service.getWeightatStart(list2, d1, d2);
			Integer n= service.getMonths(d2);
			List<ArtChanges> firstlineSub=service.getFirstLineHAARTsub(list2);
			List<ArtChanges> secondlineSub=service.getSecondLineHAARTSub(list2);
			HashMap<Patient, ArtChanges> secondLineRegList=service.getSecondlineHAARTReg(list2);
			HashMap<Patient, String> monthly[];
			HashMap<Patient, String> patientMonthlyStatus[];
			monthly=new HashMap[n];
			for (int i=0;i<n;i++)
			{
				monthly[i]=service.getMonthlyReg(list2, d2, i+1);
			}
			
			patientMonthlyStatus=new HashMap[n];
			for (int i=0;i<n;i++)
			{
				patientMonthlyStatus[i]=service.getPatientStatusByMonth(list2, d2, i+1);
			}
			HashMap<Patient,PatientStatus> sixMonStatus[]=new HashMap[8];//
			if (n>=6) // get month 6 status (Cd4, tb status and weight)
				sixMonStatus[0]=service.getSixMonthStatus(list2, d1, 0, 6);
			if (n>=12) // get month 12 status (Cd4, tb status and weight)
				sixMonStatus[1]=service.getSixMonthStatus(list2, d1, 6, 12);
			if (n>=18) // get month 18 status (Cd4, tb status and weight)
				sixMonStatus[2]=service.getSixMonthStatus(list2, d1, 12, 18);
			if (n>=24) // get month 24 status (Cd4, tb status and weight)
				sixMonStatus[3]=service.getSixMonthStatus(list2, d1, 18, 24);
			if (n>=30) // get month 30 status (Cd4, tb status and weight)
				sixMonStatus[4]=service.getSixMonthStatus(list2, d1, 24, 30);
			if (n>=36) // get month 36 status (Cd4, tb status and weight)
				sixMonStatus[5]=service.getSixMonthStatus(list2, d1, 30, 36);
			if (n>=42) // get month 42 status (Cd4, tb status and weight)
				sixMonStatus[6]=service.getSixMonthStatus(list2, d1, 36, 42);
			if (n>=48) // get month 48 status (Cd4, tb status and weight)
				sixMonStatus[7]=service.getSixMonthStatus(list2, d1, 42, 48);
				
			/*
			System.out.println("Month 18 status"+sixMonStatus[2].toString());	
			String mth=d1.toString();
			System.out.println(d1);
			System.out.println("substring 8-9 "+mth.substring(4,7));
			System.out.println("substring 8-9 "+mth.substring(mth.length()-4));
			*/
			int serial=1;
			for(Patient p: list.keySet())
			{
				ArtRegister artRcd=new ArtRegister();
				artRcd.setPatient(p);
				artRcd.setId(serial);
				artRcd.setArtStartDate(list.get(p));
				artRcd.setWhoStage(whostage.get(p));
				artRcd.setCd4Count(cd4Count.get(p));
				artRcd.setWeightStartArt(weight.get(p));
				artRcd.setOriginalReg(originalRegimen.get(p));
				artRcd.setCtxStart(ctx.get(p));
				artRcd.setInhStart(inh.get(p));
				artRcd.setTbStart(tb.get(p));
				artRcd.setnMonths(n);
				List<String> monthlyReg = new ArrayList<String>();
				for(int x=0;x<n;x++)
				{
					monthlyReg.add(monthly[x].get(p));
				}
				artRcd.setMonthlyVisit(monthlyReg);
				
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
			//	System.out.println("Six Months Status Row Capturer"+curPatStatus.size());
				artRcd.setSixMonthlyStatus(curPatStatus);
				artRcd.setSecondLineReg(secondLineRegList.get(p));
				serial++;
				artRegister.add(artRcd);
				//System.out.println(artRegister.size());
				
			}
			for(ArtRegister x:artRegister)
			{
					String oReg=x.getOriginalReg();
					if(oReg!=null)
						oReg=oReg.replaceAll(",", "-");
					String slReg=null;
					String slSwitchRsn=null;
					String slDate=null;
					if (x.getSecondLineReg()!=null)
					{
						slReg=x.getSecondLineReg().getSubReg(); //secondline Reg
						slSwitchRsn=x.getSecondLineReg().getSubReason(); //switch rsn
						slDate=x.getSecondLineReg().getSubDate().toString(); //switch date
					}
						
					
					//get HAART substitutions if any
					String subReg = null;
					String subRsn=null;
					String subDate=null;
					if (x.getFirstLineSub().size()>0)
					{
						subReg=x.getFirstLineSub().get(0).getSubReg();
						subRsn=x.getFirstLineSub().get(0).getSubReason();
						subDate=x.getFirstLineSub().get(0).getSubDate().toString();
					//	if(subDate!=null)
						//	subDate=formatDate(subDate);
						
						if (subReg!=null)
							subReg=subReg.replaceAll(",","-");
											
						if(subRsn!=null)
							subRsn=subRsn.replaceAll(",","-");
					}
					String slSubReg = null;
					String slSubRsn=null;
					String slSubDate=null;
					if (x.getSecondLineSub().size()>0)
					{
						slSubReg=x.getSecondLineSub().get(0).getSubReg();
						slSubRsn=x.getSecondLineSub().get(0).getSubReason();
						slSubDate=x.getSecondLineSub().get(0).getSubDate().toString();
						
						
						if (slSubReg!=null)
							slSubReg=slSubReg.replaceAll(",","-");
											
						if(slSubRsn!=null)
							slSubRsn=slSubRsn.replaceAll(",","-");
					}
					//Monthly Visit details
					StringBuilder strMonthly= new StringBuilder();
					if(x.getMonthlyVisit().size()>0)
					{
						for(int i=0;i<x.getMonthlyVisit().size();i++) //loop thro to the speficied months. Shld loop upto 24 ideally
						{
							//System.out.println(i+"TOTAL Six Months Visits for Patient "+x.getId()+"-Size- "+ x.getSixMonthlyStatus().size());
							if(i+1==6) //month 6 status
							{
								if(x.getSixMonthlyStatus().get(0)!=null) //status of month 6 is stored in index 0
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(0).getCd4()+","+x.getSixMonthlyStatus().get(0).getWeight()+","+x.getSixMonthlyStatus().get(0).getTbStatus());
									System.out.println(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(0).getCd4()+","+x.getSixMonthlyStatus().get(0).getWeight()+","+x.getSixMonthlyStatus().get(0).getTbStatus());
								}
									
								else
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+",-,-");
									System.out.println(","+x.getMonthlyVisit().get(i)+",-,-");
								}
									
								
							}
							else if(i+1==12) //month 12 status
							{
								if(x.getSixMonthlyStatus().get(1)!=null) //status of month 6 is stored in index 0
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(1).getCd4()+","+x.getSixMonthlyStatus().get(1).getWeight()+","+x.getSixMonthlyStatus().get(1).getTbStatus());
									System.out.println(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(1).getCd4()+","+x.getSixMonthlyStatus().get(1).getWeight()+","+x.getSixMonthlyStatus().get(1).getTbStatus());
								}
									
								else
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+",-,-");
									System.out.println(","+x.getMonthlyVisit().get(i)+",-,-");
								}
								
							}
							else if(i+1==18) // month 18 status
							{
								if(x.getSixMonthlyStatus().get(2)!=null) //status of month 6 is stored in index 0
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(2).getCd4()+","+x.getSixMonthlyStatus().get(2).getWeight()+","+x.getSixMonthlyStatus().get(2).getTbStatus());
									System.out.println(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(2).getCd4()+","+x.getSixMonthlyStatus().get(2).getWeight()+","+x.getSixMonthlyStatus().get(2).getTbStatus());
								}
									
								else
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+",-,-");
									System.out.println(","+x.getMonthlyVisit().get(i)+",-,-");
								}
								
							}
							else if(i+1==24) //month 24 status
							{
								if(x.getSixMonthlyStatus().get(3)!=null) //status of month 6 is stored in index 0
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(3).getCd4()+","+x.getSixMonthlyStatus().get(3).getWeight()+","+x.getSixMonthlyStatus().get(0).getTbStatus());
									System.out.println(","+x.getMonthlyVisit().get(i)+","+x.getSixMonthlyStatus().get(3).getCd4()+","+x.getSixMonthlyStatus().get(3).getWeight()+","+x.getSixMonthlyStatus().get(0).getTbStatus());
								}
									
								else
								{
									strMonthly.append(","+x.getMonthlyVisit().get(i)+",-,-");
									System.out.println(","+x.getMonthlyVisit().get(i)+",-,-");
								}
								
							}
							else
							{
								strMonthly.append(","+x.getMonthlyVisit().get(i));
								System.out.println(","+x.getMonthlyVisit().get(i));
							}
								
							
						}
					}
					
					System.out.println(x.getId()+","+ 
							x.getArtStartDate().toString()+","+
							x.getPatient().getPatientIdentifier(9)+","+
							x.getPatient().getPersonName()+","+
							x.getPatient().getGender()+","+
							x.getPatient().getBirthdate().toString()+"-("+
							x.getPatient().getAge()+"),"+
							"Address,"+
							x.getEligibilityReason()+","+
							x.getWhoStage()+","+
							x.getCd4Count()+","+
							x.getHeightStartArt()+","+
							x.getWeightStartArt()+","+
							x.getCtxStart()+","+
							x.getInhStart()+","+
							x.getTbStart()+","+
							",,,"+
							oReg+","+
							subReg+","+
							subDate+","+
							subRsn+","+
							slDate+ "-" + slReg + "-"+slSwitchRsn+","+
							slSubReg+","+
							slSubDate+","+
							slSubRsn+","+
							//	str2.toString()+
							"\n");
			}
			
			RegisterRenderer renderer = new RegisterRenderer();
			renderer.excelRenderTest(list2);
			/*
			try {
				
				ServletResponse w=new  HttpServletResponse();
					//response.getWriter().write(csv.toString()); // this is usually called when using string builder and should be placed in the controller class. see the definition of string builder in render2 in CSVRenderer
					renderer.csvRender(artRegister, w.getOutputStream(),n);//call the render method from CsvRenderer
				  	 
			  	} 
			  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			System.out.println(artRegister.size());
			System.out.println(list.size());
			Assert.assertEquals(100, list.size());
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
}
