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
package org.openmrs.module.FacesRegister.api.db.hibernate;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.FacesRegister.ArtChanges;
import org.openmrs.module.FacesRegister.PatientStatus;
import org.openmrs.module.FacesRegister.api.db.RegisterDAO;

/**
 * It is a default implementation of  {@link RegisterDAO}.
 */
public class HibernateRegisterDAO implements RegisterDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	private Query queryRst;
	

	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }
    
    /**
	 * @return the queryRst
	 */
	public Query getQueryRst() {
		return queryRst;
	}

	/**
	 * @param queryRst the queryRst to set
	 */
	public void setQueryRst(Query queryRst) {
		this.queryRst = queryRst;
	}
	
	public HashMap<Patient, String> getPatientStatus(List<Person> who, Date endDate,Integer monthN)
	{
		HashMap<Patient, String> patientStatusList=new HashMap<Patient, String>();
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		StringBuilder strSQL= new StringBuilder();
		strSQL.append("select patient_id,if(Patient_Status is not null,Patient_Status,if(mths<=3,'ACTIVE','LOST') ) as P_Status ");
		strSQL.append("from ");
		strSQL.append("(" );
		strSQL.append("select e.patient_id,max(e.encounter_datetime) as Enc_date, datediff(date_add(:endDate, interval :n1 month),max(e.encounter_datetime)) div (365.25 div 12)  as mths, ");
		strSQL.append("mid(max(concat(encounter_datetime,if(o.concept_id=1655, case o.value_coded ");
		strSQL.append("               when 1650 then 'DEATH' ");
		strSQL.append("				  when 1654 then 'LOST' ");
		strSQL.append("				  when 1652 then 'TO' ");
		strSQL.append("				  when 1653 then 'TO' ");
		strSQL.append("				  when 1809 then 'STOP' ");
		strSQL.append("				  when 1650 then 'DEATH' ");
		strSQL.append("				when 1650 then 'Other' end,null))),20) as Patient_Status ");
		strSQL.append("from encounter e ");
		strSQL.append("join patient p on p.patient_id=e.patient_id and p.voided =0 ");
		strSQL.append("join person pa on pa.person_id=e.patient_id and pa.voided=0 ");
		strSQL.append("left outer join obs o on  o.encounter_id=e.encounter_id and o.concept_id=1655 and o.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o.value_coded and cn.concept_name_type='FULLY_SPECIFIED' ");
		strSQL.append("where e.voided=0 and e.encounter_datetime <= date_add(:endDate, interval :n1 month) and e.patient_id in (:ids) ");
		strSQL.append("group by e.patient_id)x; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		if (endDate!=null)
			queryResults.setDate("endDate", endDate);
		if(monthN!=null)
			queryResults.setInteger("n1", monthN);
			
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 
            	 if (row[1]!=null)
            		 patientStatusList.put(p, (String) row[1]);
            	 else
            		 patientStatusList.put(p, null);
           }
		return  patientStatusList;
	}
	
	public HashMap<Patient,ArtChanges> getSecondlineReg(List<Person> who)
	{
		HashMap<Patient, ArtChanges> secondLineList=new HashMap<Patient, ArtChanges>();
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		
		StringBuilder strSQL= new StringBuilder();
		strSQL.append("select pi.patient_id,max(if(o.concept_id=6751, o.value_datetime,null)) as switch_Date, ");
		strSQL.append("       group_concat(distinct if(o.concept_id=6782,cn.name,null)) as switched_Reg, ");
		strSQL.append("       group_concat(distinct if(o.concept_id=6192,cn2.name,null) order by cn2.name asc) as switch_Reason ");
		strSQL.append("from obs o ");
		strSQL.append("join person pa on pa.person_id=o.person_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=o.person_id and p.voided=0 ");
		strSQL.append("left outer join patient_identifier pi on o.person_id=pi.patient_id and pi.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o.value_coded and cn.concept_name_type='SHORT' ");
		strSQL.append("left outer join concept_name cn2 on cn2.concept_id=o.value_coded ");
		strSQL.append("where o.concept_id in(6751,6782,6192,1571) and pi.patient_id in (:ids) ");
		strSQL.append("group by o.person_id,o.obs_group_id,o.encounter_id ");
		strSQL.append("having switched_Reg is not Null ");
		strSQL.append("union ");
		strSQL.append("select e.patient_id,e.encounter_datetime as switch_date, group_concat(distinct cn.name) as Reg,group_concat(distinct cn2.name) as Switch_Rsn ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id = e.encounter_id and o.concept_id in (1255) and o.value_coded in (6185) and o.voided=0  ");
		strSQL.append("left outer join obs o1 on o1.encounter_id = e.encounter_id and o1.concept_id in (1571) and o.voided=0 ");
		strSQL.append("left outer join obs o2 on o2.encounter_id = e.encounter_id and o2.concept_id in (6192) and o2.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o1.value_coded and cn.concept_name_type='SHORT' ");
		strSQL.append("left outer join concept_name cn2 on cn2.concept_id=o2.value_coded and cn2.concept_name_type='FULLY_SPECIFIED' ");
		strSQL.append("where e.voided =0 and e.patient_id in (:ids) ");
		strSQL.append("group by e.encounter_id; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 ArtChanges secondLine=new ArtChanges();
            	 
            	 secondLine.setPatient(p);
            	 
            	 if (row[1]!=null)
            		 secondLine.setSubDate((Date)row[1]);
            	 else
            		 secondLine.setSubDate(null);
            	 if (row[2]!=null)
            		 secondLine.setSubReg((String)row[2]);
            	 else
            		 secondLine.setSubReg(null);
            	 
            	 if (row[3]!=null)
            		 secondLine.setSubReason((String)row[3]);
            	 else
            		 secondLine.setSubReason(null);
            	 
            	 secondLineList.put(p, secondLine);
           }
		return secondLineList;
	}
	
	public List<ArtChanges> getSecondLineSub(List<Person> who)
	{
		List<ArtChanges> secondLineSubList=new ArrayList<ArtChanges>();
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		
		StringBuilder strSQL= new StringBuilder();
		strSQL.append("select pi.patient_id,max(if(o.concept_id=6770, o.value_datetime,null)) as substitute_Date, ");
		strSQL.append("       group_concat(distinct if(o.concept_id=6750,cn.name,null)) as substitute_Reg, ");
		strSQL.append("       group_concat(distinct if(o.concept_id=6772,cn2.name,null) order by cn2.name asc) as substitute_Reason ");
		strSQL.append("from obs o ");
		strSQL.append("join person pa on pa.person_id=o.person_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=o.person_id and p.voided=0 ");
		strSQL.append("left outer join patient_identifier pi on o.person_id=pi.patient_id and pi.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o.value_coded and cn.concept_name_type='SHORT' ");
		strSQL.append("left outer join concept_name cn2 on cn2.concept_id=o.value_coded ");
		strSQL.append("where o.concept_id in(6770,6750,6772,1571) and pi.patient_id in (:ids) ");
		strSQL.append("group by o.person_id,o.obs_group_id,o.encounter_id ");
		strSQL.append("having substitute_Reg is not Null;");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 ArtChanges patStatus=new ArtChanges();
            	 
            	 patStatus.setPatient(p);
            	 
            	 if (row[1]!=null)
            		 patStatus.setSubDate((Date)row[1]);
            	 else
            		 patStatus.setSubDate(null);
            	 if (row[2]!=null)
            		 patStatus.setSubReg((String)row[2]);
            	 else
            		 patStatus.setSubReg(null);
            	 
            	 if (row[3]!=null)
            		 patStatus.setSubReason((String)row[3]);
            	 else
            		 patStatus.setSubReason(null);
            	 
            	 secondLineSubList.add(patStatus);
           }
		return secondLineSubList;
	}
	
	public List<ArtChanges> getFirstLineSub(List<Person> who)
	{
		List<ArtChanges> firstSubList=new ArrayList<ArtChanges>();
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		
		StringBuilder strSQL= new StringBuilder();
		strSQL.append("select pi.patient_id,max(if(o.concept_id=6748, o.value_datetime,null)) as substitute_Date, ");
		strSQL.append("       group_concat(distinct if(o.concept_id=6749,cn.name,null)) as substitute_Reg, ");
		strSQL.append("       group_concat(distinct if(o.concept_id=6771,cn2.name,null) order by cn2.name asc) as substitute_Reason ");
		strSQL.append("from obs o ");
		strSQL.append("join person pa on pa.person_id=o.person_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=o.person_id and p.voided=0 ");
		strSQL.append("left outer join patient_identifier pi on o.person_id=pi.patient_id and pi.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o.value_coded and cn.concept_name_type='SHORT' ");
		strSQL.append("left outer join concept_name cn2 on cn2.concept_id=o.value_coded ");
		strSQL.append("where o.concept_id in(6748,6749,6771,1571) and pi.patient_id in (:ids) ");
		strSQL.append("group by o.person_id,o.obs_group_id,o.encounter_id ");
		strSQL.append("having substitute_Reg is not Null ");
		strSQL.append("union ");
		strSQL.append("select e.patient_id,e.encounter_datetime as Sub_date, group_concat(distinct cn.name) as Sub_Reg,group_concat(distinct cn2.name) as Sub_Rsn ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id = e.encounter_id and o.concept_id in (1255) and o.value_coded in (1258) and o.voided=0  ");
		strSQL.append("left outer join obs o1 on o1.encounter_id = e.encounter_id and o1.concept_id in (1571) and o.voided=0 ");
		strSQL.append("left outer join obs o2 on o2.encounter_id = e.encounter_id and o2.concept_id in (6188) and o2.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o1.value_coded and cn.concept_name_type='SHORT' ");
		strSQL.append("left outer join concept_name cn2 on cn2.concept_id=o2.value_coded and cn2.concept_name_type='FULLY_SPECIFIED' ");
		strSQL.append("where e.voided =0 and e.patient_id in (:ids) ");
		strSQL.append("group by e.encounter_id ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 ArtChanges patStatus=new ArtChanges();
            	 
            	 patStatus.setPatient(p);
            	 
            	 if (row[1]!=null)
            		 patStatus.setSubDate((Date)row[1]);
            	 else
            		 patStatus.setSubDate(null);
            	 if (row[2]!=null)
            		 patStatus.setSubReg((String)row[2]);
            	 else
            		 patStatus.setSubReg(null);
            	 
            	 if (row[3]!=null)
            		 patStatus.setSubReason((String)row[3]);
            	 else
            		 patStatus.setSubReason(null);
            	 
            	 firstSubList.add(patStatus);
           }
		return firstSubList;
	}
	public HashMap<Patient,PatientStatus> getSixMonthStatus(List<Person>who, Date startDate,Integer n1,Integer n2)
	{
		HashMap<Patient,PatientStatus> statusList = new HashMap<Patient,PatientStatus>();
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		StringBuilder strSQL= new StringBuilder();
		strSQL.append("select e.patient_id, mid(max(concat(e.encounter_datetime,if(o.concept_id in (6150),cn.name,null))),20) as TB_Status, ");
		strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in (5089),o.value_numeric,null))),20) as weight, ");
		strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in (5497,6314),o.value_numeric,null))),20) as cd4_count ");
		strSQL.append("from encounter e ");
		strSQL.append("join person pa on pa.person_id=e.patient_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=e.patient_id and p.voided=0 ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (6150,5089,5497,6314) and o.voided=0 ");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o.value_coded and cn.concept_name_type='FULLY_SPECIFIED' ");
		strSQL.append("where e.voided=0 and e.patient_id in (:ids) ");
		strSQL.append("and e.encounter_datetime>= date_add(:startDate, interval :n1 month) and e.encounter_datetime< date_add(:startDate, interval :n2 month) ");
		strSQL.append("group by e.patient_id;");
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		if (startDate !=null)
			queryResults.setDate("startDate", startDate);
		if(n1 !=null)
			queryResults.setInteger("n1", n1);
		if(n2 !=null)
			queryResults.setInteger("n2", n2);
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 PatientStatus patStatus=new PatientStatus();
            	 patStatus.setPatient(p);
            	 if (row[1]!=null)
            		 patStatus.setTbStatus((String)row[1]);
            	 else
            		 patStatus.setTbStatus(null);
            	 if (row[2]!=null)
            		 patStatus.setWeight((String)row[2]);
            	 else
            		 patStatus.setWeight(null);
            	 
            	 if (row[3]!=null)
            		 patStatus.setCd4((String)row[3]);
            	 else
            		 patStatus.setCd4(null);
            	 
            	 statusList.put(p, patStatus);
           }
		return statusList;
		
	}
	
	public Integer nMonths(Date endDate)
	{
		/*
		 * Get the Number of months since the start of ART
		 */
		StringBuilder strSQL= new StringBuilder();
		strSQL.append("select datediff(curdate(),date_add(:endDate,interval 1 day))div (365.25 div 12) as Nmonths;");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString()).addScalar("Nmonths", Hibernate.INTEGER);
		
		//Pass Parameters
				if (endDate != null)
					queryResults.setDate("endDate", endDate);
					
		Integer n=0;
		List list =queryResults.list(); //convert the generated list into a list object
    	n=(Integer) list.get(0);
		/*
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
             	n = ((Integer) row[0]).intValue();	 //call openmrs API
           }
           */
    	 return n;
    	
	}
	
	public HashMap<Patient, String> getMonthlyReg(List<Person>who,Date endDate, Integer monthN)
	{
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select x.patient_id, mid(max(x.Reg1),20) as reg ");
		strSQL.append("from "); 
		strSQL.append("( ");
		strSQL.append("select e.encounter_id,e.patient_id, ");
		strSQL.append("concat(e.encounter_datetime,if(o.concept_id=1571,group_concat(distinct c.name order by c.name asc),null)) as Reg1 ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (1571) and o.voided=0 ");
		strSQL.append("left outer join concept_name c on c.concept_id=o.value_coded and c.concept_name_type='SHORT' ");
		strSQL.append("where e.voided=0 and e.patient_id in (:ids) and e.encounter_datetime between date_add(:endDate, interval 1 day) and date_add(:endDate, interval :monthN month) ");
		strSQL.append("group by  e.encounter_Id)x ");
		strSQL.append("group by x.patient_id ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		if (endDate !=null)
			queryResults.setDate("endDate", endDate);
		if(monthN !=null)
			queryResults.setInteger("monthN", monthN);
		
		
		HashMap<Patient, String> patientRegList=new HashMap<Patient, String>();
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	
            	 if (row[1]!=null)
            		 patientRegList.put(p,(String) row[1]);
            	 else
            		 patientRegList.put(p,null);
           }
    	 return patientRegList;
		
	}
	
	
	public HashMap<Patient, String> getINHStart(List<Person> who)
	{
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		
		/* 
		 * query to get INH Start Date
		 * It based on the INH Dispensed concept 6785
		 * Gets the minimum encounter date when the client had INH dispensed
		 */
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, left(min(concat(e.encounter_datetime,if(o.concept_id in (6785) and o.value_coded=1065,1,0))),7) as INH_Start, ");
		strSQL.append("mid(min(concat(e.encounter_datetime,if(o.concept_id in (6785) and o.value_coded=1065,1,0))),20) as on_INH ");
		strSQL.append("from encounter e ");
		strSQL.append("join person pa on pa.person_id=e.patient_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=e.patient_id and p.voided=0 ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (6785) and o.voided=0 ");
		strSQL.append("where e.voided=0 and e.patient_id in (:ids) ");
		strSQL.append("group by e.patient_id ");
		strSQL.append("having on_INH=1; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		//Pass Parameters
				if (who != null)
					queryResults.setParameterList("ids", ids);
				
		HashMap<Patient, String> patientINHList=new HashMap<Patient, String>();
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	
            	 if (row[1]!=null)
            		 patientINHList.put(p,(String) row[1]);
            	 else
            		 patientINHList.put(p,null);
           }
    	 return patientINHList;
	}
	
	public HashMap<Patient, String> getTbRxStart(List<Person> who)
	{
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		
		/* 
		 * query to get Tb Start Date
		 * It based on the TB assessment concept 6150
		 * Gets the minimum encounter date when the client said on tb treatment
		 */
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, left(min(concat(e.encounter_datetime,if(o.concept_id in (6150) and o.value_coded=6183,1,0))),7) as TB_Start, ");
		strSQL.append("mid(min(concat(e.encounter_datetime,if(o.concept_id in (6150) and o.value_coded=6183,1,0))),20) as on_Tb ");
		strSQL.append("from encounter e ");
		strSQL.append("join person pa on pa.person_id=e.patient_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=e.patient_id and p.voided=0 ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (6150) and o.voided=0 ");
		strSQL.append("where e.voided=0 and e.patient_id in (:ids) ");
		strSQL.append("group by e.patient_id ");
		strSQL.append("having on_Tb=1; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		//Pass Parameters
				if (who != null)
					queryResults.setParameterList("ids", ids);
		HashMap<Patient, String> patientTBList = new HashMap<Patient, String>();
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	
            	 if (row[1]!=null)
            		 patientTBList.put(p,(String) row[1]);
            	 else
            		 patientTBList.put(p,null);
           }
    	 return patientTBList;
	
	}
	
	public HashMap<Patient, String> getCtxStart(List<Person> who)
	{
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, left(min(concat(e.encounter_datetime,if(o.concept_id in (1497,1565) and o.value_coded=1427,1,0))),7) as Ctx_Start, ");
		strSQL.append("mid(min(concat(e.encounter_datetime,if(o.concept_id in (1497,1565) and o.value_coded=1427,1,0))),20) as on_Ctx ");
		strSQL.append("from encounter e ");
		strSQL.append("join person pa on pa.person_id=e.patient_id and pa.voided=0 ");
		strSQL.append("join patient p on p.patient_id=e.patient_id and p.voided=0 ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (1497,1565,6509,6508) and o.voided=0 ");
		strSQL.append("where e.voided=0 and e.patient_id in (:ids) ");
		strSQL.append("group by e.patient_id ");
		strSQL.append("having on_Ctx=1; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		HashMap<Patient, String> patientCtxList=new HashMap<Patient, String>();
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	
            	 if (row[1]!=null)
            		 patientCtxList.put(p,(String) row[1]);
            	 else
            		 patientCtxList.put(p,null);
           }
	
		return patientCtxList;
	}
	
	public HashMap<Patient, String> getOriginalRegimen(List<Person> who)
	{
		List<Integer> ids= new ArrayList<Integer>();
		for (Person p: who)
		{
			ids.add(p.getPersonId()); //cast int to Integer
		}
		
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select x.patient_id, mid(min(coalesce(x.Reg2,x.Reg1)),20) as reg ");
		strSQL.append("from "); 
		strSQL.append("( ");
		strSQL.append("select e.encounter_id,e.patient_id,concat(e.encounter_datetime,if(o.concept_id=6747,c.name,null)) as Reg2, ");
		strSQL.append("concat(e.encounter_datetime,if(o.concept_id=1571,group_concat(distinct c.name order by c.name asc),null)) as Reg1 ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (1571,6747) ");
		strSQL.append("left outer join concept_name c on c.concept_id=o.value_coded and c.concept_name_type='SHORT' ");
		strSQL.append("where e.voided=0 and e.patient_id in (:ids) ");
		strSQL.append("group by  e.encounter_Id)x ");
		strSQL.append("group by x.patient_id ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		HashMap<Patient, String> patientRegList=new HashMap<Patient, String>();
		List list =queryResults.list(); //convert the generated list into a list object
    	
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	
            	 if (row[1]!=null)
            		 patientRegList.put(p,(String) row[1]);
            	 else
            		 patientRegList.put(p,null);
           }
	
		return patientRegList;
	}
    public List<Patient> getPatientsInLocation(Integer locationId)
    {
    	/* 
    	 * get a list of patients given the location Id
    	 * 
    	 */
    	List<Patient> patient=new ArrayList<Patient>();
    	String strSQL="select p.patient_id, p.voided ";
    		strSQL+="from patient p ";
    		strSQL+="join encounter e on e.patient_id=p.patient and e.voided=0 ";
    		strSQL+="where p.voided=0 and e.location_id=:locationId; ";
    		log.info("Executing Query" + strSQL); 
    	// For now am using a query with at least two columns. This makes my work easy for now 
    	Query query=sessionFactory.getCurrentSession().createSQLQuery(strSQL);
    	if (locationId != null)
			query.setInteger("locationId", locationId);
    	
    	query.setCacheable(true);
    	 List list = query.list(); //convert the generated list into an list object
    	 Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 patient.add(p);    
           }
    	return patient;
    }
	public HashMap<Patient,String> getPatientsGivenArtDates(Integer locationId,Date startDate, Date endDate)
	{
		
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, max(if(o.concept_id in (6746,6739),date(o.value_datetime),null)) as Date_Started_ART,max(if(o.concept_id=1592 and o.value_coded=1405,1,0))as PPCT ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (6746,6739,1592) ");
		strSQL.append("where e.encounter_type in (21,22) ");
		strSQL.append("and e.location_id=:locationId and date(o.value_datetime) between :startDate and :endDate ");
		strSQL.append("group by e.patient_id ");
		strSQL.append("having PPCT=0 and Date_Started_ART is not Null ");
		strSQL.append("union ");
		strSQL.append("select e.patient_id,max(if(o.concept_id=1255 and o.value_coded=1256,e.encounter_datetime,null)) as Date_Started_ART,max(if(o.concept_id=1592 and o.value_coded=1405,1,0))as PPCT ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in(1255,1592) and o.value_coded in (1256,1405,1407)");
		strSQL.append("left outer join concept_name cn on cn.concept_id=o.value_coded ");
		strSQL.append("where e.location_id=:locationId ");
		//strSQL.append("and date(e.encounter_datetime) between '" + startDate +"' and '"+endDate+"' ");
		strSQL.append("and date(e.encounter_datetime) between :startDate and :endDate ");
		strSQL.append("and e.voided=0 ");
		strSQL.append("group by e.patient_id ");
		strSQL.append("having PPCT=0 and Date_Started_ART is not Null ");
	
		log.info("Executing Query: " + strSQL.toString()); 
		log.debug("Executing Query: " + strSQL.toString());
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		//pass parameters to the query
		if (startDate != null)
			queryResults.setDate("startDate", startDate);
		if (endDate != null)
			queryResults.setDate("endDate", endDate);
		if (locationId != null)
			queryResults.setInteger("locationId", locationId);
		
		
		HashMap<Patient, String> patientStartDate=new HashMap<Patient,String>(); //patients art start date
		List<Patient> patient=new ArrayList<Patient>();
		
		//query(locationId,startDate,endDate);
		    	 List list =queryResults.list(); //convert the generated list into a list object
		    	 Iterator it = list.iterator();
		    	 while(it.hasNext()){
		             Object[] row = (Object[])it.next();
		            	 Patient p=new Patient();
		            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
		            	 patient.add(p);    
		            	 patientStartDate.put(p, (String) row[1].toString());
		            	 
		           }
		    	return patientStartDate;
	}
	
	public HashMap<Patient,Date> artStartDates(List<Person> who,Integer locationId)
	{
		List<Integer> ids=new ArrayList<Integer>();
		for(Person p: who) //get Ids of all patients from the selected cohort
			ids.add(p.getPersonId()); 
				
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, date(mid(max(concat(e.encounter_datetime,o.value_datetime)),20))as Date_Started_ART ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (6746,6739) ");
		strSQL.append("where e.encounter_type in (21,22) and e.location_id=:locationId ");
		strSQL.append("and  e.patient_id in (:ids) ");
		strSQL.append("group by e.patient_id ");
		strSQL.append("union ");
		strSQL.append("select e.patient_id,e.encounter_datetime as Date_Started_ART ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id=1255 and o.value_coded=1256 ");
		strSQL.append("left outer join x_concept_name cn on cn.concept_id=o.value_coded ");
		strSQL.append("where e.location_id=:locationId ");
		strSQL.append("and  e.patient_id in (:ids) ");
		strSQL.append("and e.voided=0 ");
		
		
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		//Pass Parameters
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		if (locationId != null)
			queryResults.setInteger("locationId", locationId);
		
		List list =queryResults.list(); //convert the generated list into a list object
    	
		HashMap<Patient, Date> partStartDates=new HashMap<Patient,Date>(); //patients art start date
		List<Patient> patient=new ArrayList<Patient>();
		Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 patient.add(p);
            	 if (row[1]!=null)
            		 partStartDates.put(p,(Date) row[1]);
            	 else
            		 partStartDates.put(p,null);
           }
		return partStartDates;
	}
	public void query(Integer locationId,Date startDate, Date endDate)
	{
		
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, date(o.value_datetime)as Date_Started_ART ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.concept_id in (6746,6739) ");
		strSQL.append("where e.encounter_type in (21,22) ");
		strSQL.append("and e.location_id="+locationId+" and date(o.value_datetime)  between :startDate and :endDate ");
		strSQL.append("union ");
		strSQL.append("select e.patient_id,e.encounter_datetime as Date_Started_ART ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id=1255 and o.value_coded=1256 ");
		strSQL.append("left outer join x_concept_name cn on cn.concept_id=o.value_coded ");
		strSQL.append("where e.location_id=:locationId ");
		//strSQL.append("and date(e.encounter_datetime) between '" + startDate +"' and '"+endDate+"' ");
		strSQL.append("and date(e.encounter_datetime) between :startDate and :endDate ");
		strSQL.append("and e.voided=0 ");
	
		log.info("Executing Query: " + strSQL.toString()); 
		
		//Passing parameters to the query
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		if (startDate != null)
			queryResults.setDate("startDate", startDate);
		if (endDate != null)
			queryResults.setDate("endDate", endDate);
		if (locationId != null)
			queryResults.setInteger("locationId", locationId);
		
		
		queryResults.setCacheable(true);
		setQueryRst(queryResults);
		
	}

	public void queryCD4Count(List<Person> who, String endDate)
	{
		//get list of CD4 count for a list of patients when starting art
		try
		{
			StringBuilder strSQL=new StringBuilder();
			strSQL.append("select e.patient_id, ");
			strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in(5497,6314),o.value_numeric,null))),20)as CD4_Count ");
			strSQL.append("from encounter e ");
			strSQL.append("join obs o on o.encounter_id =e.encounter_id and o.voided=0 and o.concept_id in (5497,6314) ");
			strSQL.append("where e.voided=0 and date(e.encounter_datetime)<=:endDate and e.patient_id in "+ who.toString().substring(1, who.toString().length()-1));
			strSQL.append("group by e.patient_id; ");
		
			log.info("Executing Query: " + strSQL.toString()); 
			Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
				

		}
		catch(Exception e)
		{
			log.error(e.getStackTrace())	;	
		}
	}
	
	
	public HashMap<Patient,String> getEligibilityCriteria(List<Person> who, Date endDate)//gets reason for eligibility
	{
		List<Integer> ids=new ArrayList<Integer>();
		for(Person p: who) //get Ids of all patients from the selected cohort
			ids.add(p.getPersonId()); 
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, ");
		strSQL.append("max(if(o.concept_id in (6796,6318,6317),c.name,null)) as Eligible_Thro ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id =e.encounter_id and o.voided=0 and o.concept_id in (6796,6318,6317) ");
		strSQL.append("left outer join concept_name c on c.concept_id=o.value_coded ");
		strSQL.append("where e.voided=0 and date(e.encounter_datetime)<=:endDate and patient_id in (:ids) ");
		strSQL.append("group by e.patient_id; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		
		
		if (endDate != null)
			queryResults.setDate("endDate", endDate);
		if (who != null)
			queryResults.setParameterList("ids", ids);
		//pass parameters to the query
		
		HashMap<Patient, String> eligibility=new HashMap<Patient,String>();
		List list =queryResults.list(); //convert the generated list into a list object
    	Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 eligibility.put(p,(String) row[1]); 
           }
		//List<Obs> obs=Context.getObsService().getObservationsByPersonAndConcept(who, question);
		return eligibility;
	}
	
	public HashMap<Patient, String> getWHOatStart(List<Person> who,Date fromDate, Date toDate)
	{
		List<Integer> ids=new ArrayList<Integer>();
		for(Person p: who) //get Ids of all patients from the selected cohort
			ids.add(p.getPersonId()); 
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, ");
		strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in(6745,6794,1901,5356,6377),c.name,null))),20)as Who_Stage ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id =e.encounter_id and o.voided=0 and o.concept_id in (6745,6794,1901,5356,6377) ");
		strSQL.append("left outer join concept_name c on c.concept_id=o.value_coded and c.concept_name_type='SHORT' ");
		strSQL.append("where e.voided=0 and date(e.encounter_datetime) between :fromDate and :toDate and patient_id in (:ids) ");
		strSQL.append("group by e.patient_id; ");
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());

		//pass parameters to the query
		if (fromDate != null)
			queryResults.setDate("fromDate", fromDate);
		if (toDate != null)
			queryResults.setDate("toDate", toDate);
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		
		HashMap<Patient, String> whoStage=new HashMap<Patient,String>();
		List list =queryResults.list(); //convert the generated list into a list object
	
    	Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 whoStage.put(p,(String) row[1]); 
           }
		//List<Obs> obs=Context.getObsService().getObservationsByPersonAndConcept(who, question);
		return whoStage;
		
	}
	
	public HashMap<Patient, String> getWeightatStart(List<Person> who,Date fromDate, Date toDate)
	{
		List<Integer> ids=new ArrayList<Integer>();
		for(Person p: who) //get Ids of all patients from the selected cohort
			ids.add(p.getPersonId()); 
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, ");
		strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in(5089),o.value_numeric,null))),20) as Who_Stage ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id =e.encounter_id and o.voided=0 and o.concept_id in (5089) ");
		strSQL.append("where e.voided=0 and date(e.encounter_datetime) between :fromDate and :toDate and patient_id in (:ids) ");
		strSQL.append("group by e.patient_id; ");
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());

		//pass parameters to the query
		if (fromDate != null)
			queryResults.setDate("fromDate", fromDate);
		if (toDate != null)
			queryResults.setDate("toDate", toDate);
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		
		HashMap<Patient, String> weight=new HashMap<Patient,String>();
		List list =queryResults.list(); //convert the generated list into a list object
	
    	Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 weight.put(p,(String) row[1]); 
           }
		//List<Obs> obs=Context.getObsService().getObservationsByPersonAndConcept(who, question);
		return weight;
		
	}

	public HashMap<Patient, String> getHeightatStart(List<Person> who,Date fromDate, Date toDate)
	{
		List<Integer> ids=new ArrayList<Integer>();
		for(Person p: who) //get Ids of all patients from the selected cohort
			ids.add(p.getPersonId()); 
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, ");
		strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in(6744),o.value_numeric,null))),20) as Who_Stage ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id =e.encounter_id and o.voided=0 and o.concept_id in (6744) ");
		strSQL.append("where e.voided=0 and date(e.encounter_datetime) between :fromDate and :toDate and patient_id in (:ids) ");
		strSQL.append("group by e.patient_id; ");
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());

		//pass parameters to the query
		if (fromDate != null)
			queryResults.setDate("fromDate", fromDate);
		if (toDate != null)
			queryResults.setDate("toDate", toDate);
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		
		HashMap<Patient, String> height=new HashMap<Patient,String>();
		List list =queryResults.list(); //convert the generated list into a list object
	
    	Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 height.put(p,(String) row[1]); 
           }
		//List<Obs> obs=Context.getObsService().getObservationsByPersonAndConcept(who, question);
		return height;
		
	}
	
	public List<Obs> getObsGivenDates(List<Person> whom,List<Concept>questions,Date fromDate, Date toDate)
	{
		/*
		 * Get List of observations for a given list of patients given dates
		 */
		List<Obs> obsList=Context.getObsService().getObservations(whom, null, questions, null, null, null, null, null, null, fromDate, toDate, false);
		return obsList;
	}
	
	public HashMap<Patient, String> getCD4atStart(List<Person> who,Date fromDate, Date toDate)
	{
		List<Integer> ids=new ArrayList<Integer>();
		for(Person p: who) //get Ids of all patients from the selected cohort
			ids.add(p.getPersonId()); 
		StringBuilder strSQL=new StringBuilder();
		strSQL.append("select e.patient_id, ");
		strSQL.append("mid(max(concat(e.encounter_datetime,if(o.concept_id in(5497,6314),o.value_numeric,null))),20) as CD4_Count ");
		strSQL.append("from encounter e ");
		strSQL.append("join obs o on o.encounter_id =e.encounter_id and o.voided=0 and o.concept_id in (5497,6314) ");
		strSQL.append("where e.voided=0 and date(e.encounter_datetime)<=:toDate and e.patient_id in (:ids) ");
		strSQL.append("group by e.patient_id; ");
		
		log.info("Executing Query: " + strSQL.toString()); 
		Query queryResults=sessionFactory.getCurrentSession().createSQLQuery(strSQL.toString());
		 //pass parameters to the query
		if (toDate != null)
			queryResults.setDate("toDate", toDate);
		if (who != null)
			queryResults.setParameterList("ids", ids);
		
		
		HashMap<Patient, String> cd4=new HashMap<Patient,String>();
		List list =queryResults.list(); //convert the generated list into a list object
	
    	Iterator it = list.iterator();
    	 while(it.hasNext()){
             Object[] row = (Object[])it.next();
            	 Patient p=new Patient();
            	 p=Context.getPatientService().getPatient((Integer) row[0]);	 //call openmrs API
            	 if (row[1]!=null)
            		 cd4.put(p,(String) row[1]); 
            	 else
            		 cd4.put(p,null);
            		 
           }
		
		return cd4;
		
	}

	
}