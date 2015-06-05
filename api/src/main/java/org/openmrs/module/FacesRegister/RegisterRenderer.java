package org.openmrs.module.FacesRegister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Workbook;//Class search for 'org.apache.poi.ss.usermodel.Workbook'
import org.apache.poi.ss.usermodel.Sheet;//Class search for 'org.apache.poi.ss.usermodel.Sheet'
import org.apache.poi.ss.usermodel.Row;//Class search for 'org.apache.poi.ss.usermodel.Row'
import org.apache.poi.ss.usermodel.Cell;//Class search for 'org.apache.poi.ss.usermodel.Cell'
import org.apache.poi.ss.util.CellRangeAddress;//Class search for 'org.apache.poi.ss.util.CellRangeAddress'
import org.apache.poi.hssf.usermodel.HSSFWorkbook;//Class search for 'org.apache.poi.xssf.usermodel.XSSFWorkbook'
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;

public class RegisterRenderer {
	protected final Log log = LogFactory.getLog(this.getClass());	
	public RegisterRenderer(){}
		
		/*
		 * This method is used to create as CSV file given the list of patients
		 * created after reading what micheal seaton of PIH did in his openmrs Reporting Module
		 */
				
		public void csvRender(List<ArtRegister> cohort,OutputStream fileOut, int nMonth, Date cdate)throws IOException
		{
			try{
			
			Writer w = new OutputStreamWriter(fileOut,"UTF-8"); //Output streamwriter
			
			StringBuilder str=new StringBuilder();
			int n=nMonth>24?24:nMonth;
			for(int i=0;i<=n;i++)
			{
				if((i==6)||(i==12)||(i==18)||(i==24)||(i==30)||(i==36)||(i==48))
					str.append(",Mth "+i+",CD4,Weight,TB Status");
				else
					str.append(",Mth "+i);
				
			}
			
			 
			//header
			String mth=cdate.toString(); //get selected cohort month
			String yr=cdate.toString(); //get the select cohort Yr
			w.write("Month "+mth.substring(4,7)+ " Year: "+ yr.substring(yr.length()-4)+"\n");
			//w.write("a,b,c,d,e,f,g,h,i-Status at Start,j,k,l,m-Fill in When Applicable,n,o,p-PMTCT,q,r,s-1st Line Regimen,t,u,v,w-2nd Line Regimen\n");
			w.write(",,,,,,,,Status at Start,,,,Fill in When Applicable,,,PMTCT,,,1st Line Regimen,,,,2nd Line Regimen\n");
			//w.write("a,b,c,Patient's Name-d,e,Date of Birth-f,Address-g,h,i,j,k,l,Prophylaxis-m,n,TB-Treatment-o,Pregrancy 1-p,Pregrancy 2-q,Pregrancy 3-r,s,Substitutions-t,u,v,w,Substitutions-x,y,z\n");
			w.write(",,,Patient's Name,,Date of Birth,Address,,,,,,Prophylaxis,,TB-Treatment,Pregrancy 1,Pregrancy 2,Pregrancy 3,,Substitutions,,,,Substitutions,,\n");
			//w.write("a,b,c,First Name-d,e,(dd/mm/yy)-f,Physical-g,h,i,j,k,Weight (Kgs)-l,CTX Start-m,INH Start-n,Month/Year-o,EDD-p,EDD-q,EDD-r,s,1st Sub-t,u,v,Regimen-w,1st Sub-x,y,z\n");
			w.write(",,,First Name,,(dd/mm/yy),Physical,,,,,Weight (Kgs),CTX Start,INH Start,Month/Year,EDD,EDD,EDD,,1st Sub,,,Regimen,1st Sub,,\n");
/*			w.write("Serial Counter_a," +
					"ART Start Date_b," +
					"Unique_Identifier_c, " +
					"Second Name_d," +
					"Sex_e, " +
					"Age_f, " +
					"Tel# Landmark_g, " +
					"Reason for Eligibility_h, "+
					"WHO Clinial Stage_i, " +
					"CD4 Value or %_j, " +
					"Height_k, " +
					"wt 1dc_l, " +
					"CTX Month/Year_m," +
					"INH Month/Year_n," +
					"TB Reg No_o," +
					"ANC #_p," +
					"ANC #_q," +
					"ANC #_r," +
					"Original Regimen_s," +
					"2nd Sub_t," +
					"Dates_u," +
					"Reasons_v," +
					"Reason_w," +
					"2nd Sub_x," +
					"Dates_y," +
					"Reasons_z" +
					str.toString()+
					"\n");
	*/
			w.write("Serial Counter," +
					"ART Start Date," +
					"Unique_Identifier, " +
					"Second Name," +
					"Sex, " +
					"Age, " +
					"Tel# Landmark, " +
					"Reason for Eligibility, "+
					"WHO Clinial Stage, " +
					"CD4 Value or %, " +
					"Height, " +
					"wt 1dc, " +
					"CTX Month/Year," +
					"INH Month/Year," +
					"TB Reg No," +
					"ANC #," +
					"ANC #," +
					"ANC #," +
					"Original Regimen," +
					"2nd Sub," +
					"Dates," +
					"Reasons," +
					"Reason," +
					"2nd Sub," +
					"Dates," +
					"Reasons" +
					str.toString()+
					"\n");

			//create the alphabetic column headers
			StringBuilder strHeader=new StringBuilder();
			int r=0;
			for(char i='a';i<='z';i++)
			{
				strHeader.append(",(a"+i+")");
			}
			
			
			for(char i='a';i<='z';i++)
			{
				r++;
				strHeader.append(",(b"+i+")");
				if(r==11) break;
			}
			
			w.write("(a),(b),(c),(d),(e),(f),(g),(h),(i),(j),(k),(l),(m),(n),(o),(p),(q),(r),(s),(t),(u),(v),(w),(x),(y),(z)"+strHeader.toString()+"\n");
			//add other rows
			// TODO: write each row as columns separated by commas, ending with a new line
			for(ArtRegister x:cohort)
			{
					String oReg=x.getOriginalReg(); //first line original reg
					
					String slReg=null;
					String slSwitchRsn=null;
					String slDate=null;
					if (x.getSecondLineReg()!=null)
					{
						slReg=x.getSecondLineReg().getSubReg(); //secondline Reg
						slSwitchRsn=x.getSecondLineReg().getSubReason(); //switch rsn
						slDate=x.getSecondLineReg().getSubDate().toString(); //switch date
					}
					if(slDate!=null)
						slDate=formatDate(slDate);
					
					
					if (slSwitchRsn!=null)
						slSwitchRsn=slSwitchRsn.replaceAll(",", "-");
					
					if(slReg!=null)
						slReg=slReg.replaceAll(",", "-");
					
					if(oReg!=null)
						oReg=oReg.replaceAll(",", "-");
										
					//get HAART First Line substitutions if any
					String subReg = null;
					String subRsn=null;
					String subDate=null;
					if (x.getFirstLineSub().size()>0)
					{
						subReg=x.getFirstLineSub().get(0).getSubReg();
						subRsn=x.getFirstLineSub().get(0).getSubReason();
						subDate=x.getFirstLineSub().get(0).getSubDate().toString();
						if(subDate!=null)
							subDate=formatDate(subDate);
						
						if (subReg!=null)
							subReg=subReg.replaceAll(",","-");
											
						if(subRsn!=null)
							subRsn=subRsn.replaceAll(",","-");
					}
					
					//get HAART First Line 2nd substitutions if any
					String subReg2 = null;
					String subRsn2=null;
					String subDate2=null;
					if (x.getFirstLineSub().size()>1)
					{
						subReg2=x.getFirstLineSub().get(1).getSubReg();
						subRsn2=x.getFirstLineSub().get(1).getSubReason();
						subDate2=x.getFirstLineSub().get(1).getSubDate().toString();
						if(subDate2!=null)
							subDate2=formatDate(subDate2);
						
						if (subReg2!=null)
							subReg2=subReg2.replaceAll(",","-");
											
						if(subRsn2!=null)
							subRsn2=subRsn2.replaceAll(",","-");
					}
					//get HAART Second Line substitutions if any
					String slSubReg = null;
					String slSubRsn=null;
					String slSubDate=null;
					if (x.getSecondLineSub().size()>0)
					{
						slSubReg=x.getSecondLineSub().get(0).getSubReg();
						slSubRsn=x.getSecondLineSub().get(0).getSubReason();
						slSubDate=x.getSecondLineSub().get(0).getSubDate().toString();
						if(slSubDate!=null)
							slSubDate=formatDate(slSubDate);
						
						if (slSubReg!=null)
							slSubReg=slSubReg.replaceAll(",","-");
											
						if(slSubRsn!=null)
							slSubRsn=slSubRsn.replaceAll(",","-");
					}
					
					//get HAART Second Line 2nd substitutions if any
					String slSubReg2 = null;
					String slSubRsn2=null;
					String slSubDate2=null;
					if (x.getSecondLineSub().size()>1)
					{
						slSubReg2=x.getSecondLineSub().get(1).getSubReg();
						slSubRsn2=x.getSecondLineSub().get(1).getSubReason();
						slSubDate2=x.getSecondLineSub().get(1).getSubDate().toString();
						if(slSubDate2!=null)
							slSubDate2=formatDate(slSubDate2);
						
						if (slSubReg2!=null)
							slSubReg2=slSubReg2.replaceAll(",","-");
											
						if(slSubRsn2!=null)
							slSubRsn2=slSubRsn2.replaceAll(",","-");
					}
					
					//Monthly Visit details for the first 24 months
					StringBuilder strMonthly= new StringBuilder();
					if(x.getMonthlyVisit().size()>0)
					{
						String monthlyReg_Status;
						
						for(int i=0;i<(x.getMonthlyVisit().size()>24?24:x.getMonthlyVisit().size());i++) //loop thro to the speficied months. Shld loop upto 24 ideally
						{
							monthlyReg_Status=x.getMonthlyVisit().get(i);
							if(x.getPatientStatus().size()>0)
							{
								if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
									monthlyReg_Status=x.getPatientStatus().get(i);
								else
									monthlyReg_Status=x.getMonthlyVisit().get(i);
									
							}
							if (monthlyReg_Status!=null)
								monthlyReg_Status=monthlyReg_Status.replaceAll(",", "-");
							
							if(i+1==6) //month 6 status
							{
								if(x.getSixMonthlyStatus().get(0)!=null) 
								{
									//status of month 6 is stored in index 0
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(0).getCd4()+","+x.getSixMonthlyStatus().get(0).getWeight()+","+x.getSixMonthlyStatus().get(0).getTbStatus());
								}
									
								else
									strMonthly.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else if(i+1==12) //month 12 status
							{
								if(x.getSixMonthlyStatus().get(1)!=null) //status of month 6 is stored in index 0
								{
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(1).getCd4()+","+x.getSixMonthlyStatus().get(1).getWeight()+","+x.getSixMonthlyStatus().get(1).getTbStatus());
									
								}
									
								else
									strMonthly.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else if(i+1==18) // month 18 status
							{
								if(x.getSixMonthlyStatus().get(2)!=null) //status of month 6 is stored in index 0
								{
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(2).getCd4()+","+x.getSixMonthlyStatus().get(2).getWeight()+","+x.getSixMonthlyStatus().get(2).getTbStatus());
									
								}
									
								else
									strMonthly.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else if(i+1==24) //month 24 status
							{
								if(x.getSixMonthlyStatus().get(3)!=null) //status of month 6 is stored in index 0
								{
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(3).getCd4()+","+x.getSixMonthlyStatus().get(3).getWeight()+","+x.getSixMonthlyStatus().get(3).getTbStatus());
								}
									
								else
									strMonthly.append(","+monthlyReg_Status+",-,-,-");
							}
							else
								strMonthly.append(","+monthlyReg_Status);		
						}
					}
					
					//Monthly Visit details for the 25-48 months
					StringBuilder strMonthly2= new StringBuilder();
					if(x.getMonthlyVisit().size()>24)
					{
						String monthlyReg_Status;
						
						for(int i=23;i<(x.getMonthlyVisit().size()>48?48:x.getMonthlyVisit().size());i++) //loop thro to the speficied months. Shld loop upto 24 ideally
						{
							monthlyReg_Status=x.getMonthlyVisit().get(i);
							if(x.getPatientStatus().size()>0)
							{
								if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
									monthlyReg_Status=x.getPatientStatus().get(i);
								else
									monthlyReg_Status=x.getMonthlyVisit().get(i);
									
							}
							if (monthlyReg_Status!=null)
								monthlyReg_Status=monthlyReg_Status.replaceAll(",", "-");
							
							if(i+1==30) //month 30 status
							{
								if(x.getSixMonthlyStatus().get(0)!=null) 
								{
									//status of month 6 is stored in index 0
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly2.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly2.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(0).getCd4()+","+x.getSixMonthlyStatus().get(0).getWeight()+","+x.getSixMonthlyStatus().get(0).getTbStatus());
								}
									
								else
									strMonthly2.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else if(i+1==36) //month 36 status
							{
								if(x.getSixMonthlyStatus().get(1)!=null) //status of month 6 is stored in index 0
								{
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly2.append(","+monthlyReg_Status+",-,-,-"); //three commas bcoz there are three field be filled with a dash
									else
										strMonthly2.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(1).getCd4()+","+x.getSixMonthlyStatus().get(1).getWeight()+","+x.getSixMonthlyStatus().get(1).getTbStatus());
									
								}
									
								else
									strMonthly2.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else if(i+1==42) // month 42 status
							{
								if(x.getSixMonthlyStatus().get(2)!=null) //status of month 6 is stored in index 0
								{
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly2.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly2.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(2).getCd4()+","+x.getSixMonthlyStatus().get(2).getWeight()+","+x.getSixMonthlyStatus().get(2).getTbStatus());
									
								}
									
								else
									strMonthly2.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else if(i+1==48) //month 48 status
							{
								if(x.getSixMonthlyStatus().get(3)!=null) //status of month 6 is stored in index 0
								{
									if (!x.getPatientStatus().get(i).equalsIgnoreCase("ACTIVE"))
										strMonthly2.append(","+monthlyReg_Status+",-,-,-");
									else
										strMonthly2.append(","+monthlyReg_Status+","+x.getSixMonthlyStatus().get(3).getCd4()+","+x.getSixMonthlyStatus().get(3).getWeight()+","+x.getSixMonthlyStatus().get(3).getTbStatus());
								}
									
								else
									strMonthly2.append(","+monthlyReg_Status+",-,-,-");
								
							}
							else
								strMonthly2.append(","+monthlyReg_Status);
							
						}
					}
				
					//write to the Csc file					
					
					//write row one for this client
				w.write(x.getId()+","+ 
						formatDate(x.getArtStartDate().toString())+","+
						x.getPatient().getPatientIdentifier(9)+","+
						x.getPatient().getPersonName()+","+
						x.getPatient().getGender()+","+
						formatDate(x.getPatient().getBirthdate().toString())+","+
						"Address,"+
						x.getEligibilityReason()+","+
						x.getWhoStage()+","+
						x.getCd4Count()+","+
						x.getHeightStartArt()+","+
						x.getWeightStartArt()+","+
						x.getCtxStart()+","+
						x.getInhStart()+","+
						x.getTbStart()+","+
						",,,"+ // PMTCT Fields not yet done
						oReg+","+
						subReg+","+
						subDate+","+
						subRsn+","+
						slDate+ "-" + slReg + "-"+slSwitchRsn+","+
						slSubReg+","+
						slSubDate+","+
						slSubRsn+","+
						strMonthly.toString()+
						"\n");
				
				//write row 2 csv for this client
				w.write(","+ //client id on row 2 shld be blank
						","+ //ART start date on row 2 shld be blank
						","+ //client Unique Id on row 2 shld be blank
						","+ //client Name on row 2 shld be blank since it is captured on row 1
						","+ //client sex on row 2 shld be blank
						x.getPatient().getAge()+","+
						","+ //client Address on row 2 shld be blank
						","+ //client eligibility on row 2 shld be blank
						","+ //client WHO stage on row 2 shld be blank
						","+ //client CD4 count on row 2 shld be blank
						","+ //client Height at start of ART on row 2 shld be blank
						","+ //client Weight at start of ART on row 2 shld be blank
						","+ //client CTX start on row 2 shld be blank
						","+ //client INH start on row 2 shld be blank
						","+// Tb Reg# to be added
						",,,"+ //PMTC cols
						","+ //client Original ART on row 2 shld be blank
						subReg2+","+
						subDate2+","+
						subRsn2+","+
						"-,"+ //client 2nd Line switch Reg on row 2 shld be blank
						slSubReg2+","+
						slSubDate2+","+
						slSubRsn2+","+
						strMonthly2.toString()+
						"\n");

				
			}
				
			w.flush();
			}
			catch(Exception e)
			{
				log.error("Error "+ e.getMessage()+" \n "+e.fillInStackTrace());
			}
				
		}
		
		/*
		 * This method generates a  CSV file using StringBuilder Class
		 * This was adopted from Rowan of PIH Rwanda
		 */
		public void excelRender(List<Patient> patients, OutputStream fileOut) throws IOException
		{
			FileInputStream inputStream = new FileInputStream(new File("/resources/RegisterTemplate.xls"));//open the existing template
	        Workbook wb = new HSSFWorkbook(inputStream); //or new HSSFWorkbook() ;
	        Sheet sheet = wb.getSheet("register");
	        int startRow=6;
	        for(int x=0;x<patients.size();x++)
	        {
	        	Row row = sheet.createRow(startRow);
	        	Cell cell=row.createCell(0);
	        	cell.setCellValue((Integer)x+1);//serial number
	        	cell.setCellValue((String)patients.get(x).getPersonName().toString());
	        	cell.setCellValue((String) patients.get(x).getPatientIdentifier(9).toString());
	        	
	        }
	     // Write the output to a file
	        Writer w = new OutputStreamWriter(fileOut,"UTF-8"); //Output streamwriter
	       // FileOutputStream fileOut = new FileOutputStream("RegisterTemplate.xls");
	        wb.write(fileOut);
	        fileOut.flush();
	       // fileOut.close();
		}
		
		public void excelRenderTest(List<Person> patients) throws IOException
		{
			FileInputStream inputStream = new FileInputStream(new File("org/openmrs/module/FacesRegister/resources/RegisterTemplate.xls"));//open the existing template
	        Workbook wb = new HSSFWorkbook(inputStream); //or new HSSFWorkbook() ;
	        Sheet sheet = wb.getSheet("register");
	        int startRow=6;
	        for(int x=0;x<patients.size();x++)
	        {
	        	Row row = sheet.createRow(startRow);
	        	Patient p = new Patient();
	        	p.setPersonId(patients.get(x).getPersonId());
	        	Cell cell=row.createCell(0);
	        	cell.setCellValue((Integer)x+1);//serial number
	        	cell.setCellValue((String)p.getPersonName().toString());
	        	cell.setCellValue((String) p.getPatientIdentifier(9).toString());
	        	
	        }
	     // Write the output to a file
	        //Writer w = new OutputStreamWriter(fileOut,"UTF-8"); //Output streamwriter
	        FileOutputStream fileOut = new FileOutputStream("org/openmrs/module/FacesRegister/resources/RegisterTemplate.xls");
	        wb.write(fileOut);
	        fileOut.flush();
	       // fileOut.close();
		}
		
		private String formatDate(String st)
		{
			return st.substring(0,11);
		}
	

}
