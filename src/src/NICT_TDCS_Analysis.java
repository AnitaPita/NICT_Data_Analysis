package tdcs;

//import NICTAnalysisTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NICT_TDCS_Analysis {

	public static void main(String[] args) throws Exception
	{
		new NICT_TDCS_Analysis().run();
	}
	private void run() throws ParserConfigurationException, SAXException, IOException {

		final int NB_LINES =300;

		String[][] nbackBySession_table = new String[NB_LINES][400];

		
		File[] reportFiles = null;
		File[] responseFiles = null;
		try {
			//Start with output files; store them in an array files[]
			File inputFolder = new File("All_TDCS_Reports_updated"); //this will implements the single file algorithm below
			File correctedReportsFolder = null;
	    	if(inputFolder.isDirectory()) //check if said file is a directory
	    	{
	    		removeLeadingSpaces(inputFolder); //Removes first x spaces in each file
	    		correctedReportsFolder = new File("All_TDCS_Reports_updated/correctedReports");
	    		//The following MUST OCCUR:
	    		reportFiles = correctedReportsFolder.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
//	    		for (int i = 0; i < files.length; i++) 
//	    		{
	    			//System.out.println(files[i].getName()); //get name of each file in the directory; print out
//				}
	    	}
	    	
	    	//Get the text files containing data with response times
	    	File nbkResponses = new File("All_TDCS_Reports_updated/NbkResponses");
	    	
	    	if(nbkResponses.isDirectory()) //check if said file is a directory
	    	{
	    		//The following MUST OCCUR:
	    		responseFiles = nbkResponses.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
	    		/*for (int i = 0; i < responses.length; i++) 
	    		{
	    			System.out.println(responses[i].getName()); //get name of each file in the directory; print out
				}*/
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    	//Now, initialize all sheets
		    Workbook outputFile = new XSSFWorkbook(); // Empty Excel output file is initialized
					
	    	//Initialize table with titles
		   	nbackBySession_table = nbackBySessionInit(nbackBySession_table);
		   	
		   	//Fill in data
		   	if(reportFiles!=null)
		    {
		    	for(int i = 0; i<reportFiles.length;i++)//For each user's file
		    	{
		    		nbackBySessionSheet(reportFiles[i],responseFiles,nbackBySession_table,i+1); //Adds a new row to nbackByID at line i+1; NEEDS ARRAY OF TEXT FILES A.P
		    	}
		    	
		    }
		   	else
		   	{
		   		System.out.println("The spreadsheet could not be generated");
		   	}
		   	
		   	//Create output sheet from finalized nbackBySession_table
		   	Sheet nbackBySession_sheet = outputFile.createSheet("nbackByID");
			
			Cell cell_w = null;
			
			for(int r = 0 ; r < reportFiles.length+1 ; r++)//+1 b/c one row for the titles
			{
				Row row = nbackBySession_sheet.createRow(r);
				for(int c = 0 ; c < 400 ; c++) //348 is the number of columns
				{
					/*if(r == 0)
					{
						cell_w = row.createCell(c);
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue(nbackByID_table[r][c].toString());
					}
					else
					{
						cell_w = row.createCell(c);
						if(nbackByID_table[r][c].equals(""))
						{
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("");
						}	
						else
						{
							cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
							cell_w.setCellValue(Double.parseDouble(nbackByID_table[r][c].toString()));
						}
					}*/
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					//System.out.println(r+ " "+c);
					if(nbackBySession_table[r][c] == null)
					{
						//System.out.println();
						cell_w.setCellValue("");
					}
					else
					{
						cell_w.setCellValue(nbackBySession_table[r][c].toString());
					}
					
				}
			}
			
			//Get the date and time  	
		    DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");	
		    Date date = new Date();
		    String fileDate = dateFormat.format(date);
		    //System.out.println(fileDate); //Print out today's date
			
		
	try{	
    	FileOutputStream output = new FileOutputStream("C:/Users/Anita/Documents/School/uOttawa/FilteredTDCSData_"+fileDate+".xlsx"); //Changed directory to fit comp A.P
    	outputFile.write(output);
    	output.close();
    	outputFile.close();
    	System.out.println(fileDate); //Print out today's date
    }
    catch (Exception e)
	{
		e.printStackTrace();
	}
	}
	private void nbackBySessionSheet(File file, File[] responses,String[][] nbackBySession_table, int index) { //i = row to construct on
		try{
			//First, get the ID of the user that this file pertains to. The file name is of the format "Report_ID_TDCS-.xls"
			String id_string = file.getName();
			String id = id_string.substring(15,id_string.length()-4); //from 15 to .xml
			
			//Now parse the file.
			String[][] raw_table = new String[1000][20]; //will take in ALL the data from the file; all sheets combined into one, per row
			//int sheet1Start = 0;
			int sheet2Start = 0;
			int sheet3Start = 0;
			int sheet4Start = 0; //Will be used to split the raw data into data for each sheet
			
			DocumentBuilderFactory dbFactory 
	           = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(file);
	        doc.getDocumentElement().normalize();
	        NodeList rowList = doc.getElementsByTagName("Row");
	        for (int temp = 0; temp < rowList.getLength(); temp++) {
	        	Element nNode = (Element) rowList.item(temp);
	        	NodeList cellList = nNode.getElementsByTagName("Cell");
	        	//System.out.println("Row "+temp);
	        	//StringBuffer content = new StringBuffer();
	        	for (int j = 0; j < cellList.getLength(); j++) {
	        		Node abc = cellList.item(j);
	        		String text = ((Element)abc.getChildNodes()).getTextContent();
	        		//content.append(text+",");
	        		raw_table[temp][j] = ((Element)abc.getChildNodes()).getTextContent(); //Loads into the array
	        		
	        		//Check to see if we got to a new sheet; since finished shows up in many places, check cellList.item(j+1)
	        		
	        		Node def = cellList.item(j+1);
	        		String nextString = null;
	        		if(def!= null)
	        		{
	        			nextString = ((Element)def.getChildNodes()).getTextContent();
	        		}

	        		if(text.equals("Date")) //If second sheet 
	        		{
	        			sheet2Start = temp;
	        		}
	        		else if(text.equals("Finished")&&sheet3Start == 0&&nextString.equals("Game ID"))//If third sheet (sheet3Start not initialized)
	        		{
	        			sheet3Start = temp;
	        		}
	        		else if(text.equals("Finished")&&nextString.equals("Game ID"))//If fourth sheet (sheet3Start initialized)
	        		{
	        			sheet4Start = temp;
	        		}
	        		
	           	}
	       		//System.out.println("Row "+temp + "="+content);
	        }
	        
	        String[][] avgs_report = new String[sheet2Start][20]; //max 20 columns
	        for(int i = 0; i<sheet2Start;i++)
	        {
	        	for(int j = 0;j<20;j++)
	        	{
	        		avgs_report[i][j] = raw_table[i][j];
	        	}
	        }
	        
	        String[][] nback_report = new String[raw_table.length - sheet4Start][20];
	        for(int i = 0; i<nback_report.length;i++)
	        {
	        	for(int j = 0;j<nback_report[i].length;j++)
	        	{
	        		nback_report[i][j] = raw_table[i+sheet4Start][j]; //changed from sheet3 to sheet4
	        	}
	        }
	        
	        //Done parsing file into login_report, nback_report
	        
	        //Initialize variables for calculations.
	        double totalHROverall = 0.0;
	        double totalEROverall = 0.0;
	        double totalERIOverall = 0.0, totalEROOverall = 0.0; //NEW
	        int totalGamesOverall = 0;
	        int timeOverall = 0;
	        double totalRTOverall = 0.0;
	        double avgHROverall = 0.0,  avgEROverall = 0.0, avgERIOverall = 0.0, avgEROOverall = 0.0, avgRTOverall = 0.0;
	        int totalHitsOverall = 0;
	        int totalErrOverall = 0; double totalErrIOverall = 0.0, totalErrOOverall = 0.0; //included for type of error
	        int numCompletedOverall = 0;
	        
	        double t1_totalHROverall = 0.0;
	        double t1_totalEROverall = 0.0;
	        double t1_totalERIOverall = 0.0, t1_totalEROOverall = 0.0;
	        int t1_totalGamesOverall = 0;
	        int t1_timeOverall = 0;
	        double t1_totalRTOverall = 0.0;
	        double t1_avgHROverall = 0.0,  t1_avgEROverall = 0.0, t1_avgERIOverall = 0.0, t1_avgEROOverall = 0.0, t1_avgRTOverall = 0.0;
	        int t1_totalHitsOverall = 0;
	        int t1_totalErrOverall = 0; double t1_totalErrIOverall = 0.0, t1_totalErrOOverall = 0.0;
	        int t1_numCompletedOverall = 0;
	        
	        double t1_totalHRSpatial = 0.0;
	        double t1_totalERSpatial = 0.0;
	        double t1_totalERISpatial = 0.0, t1_totalEROSpatial = 0.0;
	        int t1_totalGamesSpatial = 0;
	        int t1_timeSpatial = 0;
	        double t1_totalRTSpatial = 0.0;
	        double t1_avgHRSpatial = 0.0, t1_avgERSpatial = 0.0, t1_avgERISpatial = 0.0, t1_avgEROSpatial = 0.0, t1_avgRTSpatial = 0.0;
	        int t1_totalHitsSpatial = 0;
	        int t1_totalErrSpatial = 0; double t1_totalErrISpatial = 0.0, t1_totalErrOSpatial = 0.0;
	        int t1_numCompletedSpatial = 0;
	        
	        double t1_totalHRLetNum = 0.0;
	        double t1_totalERLetNum = 0.0;
	        double t1_totalERILetNum = 0.0, t1_totalEROLetNum = 0.0;
	        int t1_totalGamesLetNum = 0;
	        int t1_timeLetNum = 0;
	        double t1_totalRTLetNum = 0.0;
	        double t1_avgHRLetNum = 0.0, t1_avgERLetNum = 0.0, t1_avgERILetNum = 0.0, t1_avgEROLetNum = 0.0, t1_avgRTLetNum = 0.0;
	        int t1_totalHitsLetNum = 0;
	        int t1_totalErrLetNum = 0; double t1_totalErrILetNum = 0.0, t1_totalErrOLetNum = 0.0;
	        int t1_numCompletedLetNum = 0;
	        
	        double t1_totalHRImg = 0.0;
	        double t1_totalERImg = 0.0;
	        double t1_totalERIImg = 0.0, t1_totalEROImg = 0.0;
	        int t1_totalGamesImg = 0;
	        int t1_timeImg = 0;
	        double t1_totalRTImg = 0.0;
	        double t1_avgHRImg = 0.0, t1_avgERImg = 0.0, t1_avgERIImg = 0.0, t1_avgEROImg = 0.0, t1_avgRTImg = 0.0;
	        int t1_totalHitsImg = 0;
	        int t1_totalErrImg = 0; double t1_totalErrIImg = 0.0, t1_totalErrOImg = 0.0;
	        int t1_numCompletedImg = 0;
	        
	        double t2_totalHROverall = 0.0;
	        double t2_totalEROverall = 0.0;
	        double t2_totalERIOverall = 0.0, t2_totalEROOverall = 0.0;
	        int t2_totalGamesOverall = 0;
	        int t2_timeOverall = 0;
	        double t2_totalRTOverall = 0.0;
	        double t2_avgHROverall = 0.0, t2_avgEROverall = 0.0, t2_avgERIOverall = 0.0, t2_avgEROOverall = 0.0, t2_avgRTOverall = 0.0;
	        int t2_totalHitsOverall = 0;
	        int t2_totalErrOverall = 0; double t2_totalErrIOverall = 0.0, t2_totalErrOOverall = 0.0;
	        int t2_numCompletedOverall = 0;
	        
	        double t2_totalHRSpatial = 0.0;
	        double t2_totalERSpatial = 0.0;
	        double t2_totalERISpatial = 0.0, t2_totalEROSpatial = 0.0;
	        int t2_totalGamesSpatial = 0;
	        int t2_timeSpatial = 0;
	        double t2_totalRTSpatial = 0.0;
	        double t2_avgHRSpatial = 0.0, t2_avgERSpatial = 0.0, t2_avgERISpatial = 0.0, t2_avgEROSpatial = 0.0, t2_avgRTSpatial = 0.0;
	        int t2_totalHitsSpatial = 0;
	        int t2_totalErrSpatial = 0; double t2_totalErrISpatial = 0.0, t2_totalErrOSpatial = 0.0;
	        int t2_numCompletedSpatial = 0;
	        
	        double t2_totalHRLetNum = 0.0;
	        double t2_totalERLetNum = 0.0;
	        double t2_totalERILetNum = 0.0, t2_totalEROLetNum = 0.0;
	        int t2_totalGamesLetNum = 0;
	        int t2_timeLetNum = 0;
	        double t2_totalRTLetNum = 0.0;
	        double t2_avgHRLetNum = 0.0, t2_avgERLetNum = 0.0, t2_avgERILetNum = 0.0, t2_avgEROLetNum = 0.0, t2_avgRTLetNum = 0.0;
	        int t2_totalHitsLetNum = 0;
	        int t2_totalErrLetNum = 0; double t2_totalErrILetNum = 0.0, t2_totalErrOLetNum = 0.0;
	        int t2_numCompletedLetNum = 0;
	        
	        double t2_totalHRImg = 0.0;
	        double t2_totalERImg = 0.0;
	        double t2_totalERIImg = 0.0, t2_totalEROImg = 0.0;
	        int t2_totalGamesImg = 0;
	        int t2_timeImg = 0;
	        double t2_totalRTImg = 0.0;
	        double t2_avgHRImg = 0.0, t2_avgERImg = 0.0, t2_avgERIImg = 0.0, t2_avgEROImg = 0.0, t2_avgRTImg = 0.0;
	        int t2_totalHitsImg = 0;
	        int t2_totalErrImg = 0; double t2_totalErrIImg = 0.0, t2_totalErrOImg = 0.0;
	        int t2_numCompletedImg = 0;
	        
	        //For each game; one line each
	        
	        double g1_HR = 0.0; double g1_ER = 0.0; double g1_RTAverage = 0.0; int g1_timeSpent = 0; String g1_type = null; String g1which = "";
	        	int g1_hits = 0, g1_errors = 0; String g1_complete = ""; double g1_errI = 0, g1_errO = 0;
	        	double g1_ERI = 0.0, g1_ERO = 0.0;
	        double g2_HR = 0.0; double g2_ER = 0.0; double g2_RTAverage = 0.0; int g2_timeSpent = 0; String g2_type = null; String g2which = "";
        		int g2_hits = 0, g2_errors = 0; String g2_complete = ""; double g2_errI = 0, g2_errO = 0;
        		double g2_ERI = 0.0, g2_ERO = 0.0;
	        double g3_HR = 0.0; double g3_ER = 0.0; double g3_RTAverage = 0.0; int g3_timeSpent = 0; String g3_type = null; String g3which = "";
    			int g3_hits = 0, g3_errors = 0; String g3_complete = ""; double g3_errI = 0, g3_errO = 0;
    			double g3_ERI = 0.0, g3_ERO = 0.0;
	        double g4_HR = 0.0; double g4_ER = 0.0; double g4_RTAverage = 0.0; int g4_timeSpent = 0; String g4_type = null; String g4which = "";
    			int g4_hits = 0, g4_errors = 0; String g4_complete = ""; double g4_errI = 0, g4_errO = 0;
    			double g4_ERI = 0.0, g4_ERO = 0.0;
	        double g5_HR = 0.0; double g5_ER = 0.0; double g5_RTAverage = 0.0; int g5_timeSpent = 0; String g5_type = null; String g5which = "";
	        	int g5_hits = 0, g5_errors = 0; String g5_complete = ""; double g5_errI = 0, g5_errO = 0;
	        	double g5_ERI = 0.0, g5_ERO = 0.0;
	        double g6_HR = 0.0; double g6_ER = 0.0; double g6_RTAverage = 0.0; int g6_timeSpent = 0; String g6_type = null; String g6which = "";
	        	int g6_hits = 0, g6_errors = 0; String g6_complete = ""; double g6_errI = 0, g6_errO = 0;
	        	double g6_ERI = 0.0, g6_ERO = 0.0;
	        double g7_HR = 0.0; double g7_ER = 0.0; double g7_RTAverage = 0.0; int g7_timeSpent = 0; String g7_type = null; String g7which = "";
	        	int g7_hits = 0, g7_errors = 0; String g7_complete = ""; double g7_errI = 0, g7_errO = 0;
	        	double g7_ERI = 0.0, g7_ERO = 0.0;
	        double g8_HR = 0.0; double g8_ER = 0.0; double g8_RTAverage = 0.0; int g8_timeSpent = 0; String g8_type = null; String g8which = "";
	        	int g8_hits = 0, g8_errors = 0; String g8_complete = ""; double g8_errI = 0, g8_errO = 0;
	        	double g8_ERI = 0.0, g8_ERO = 0.0;
	        double g9_HR = 0.0; double g9_ER = 0.0; double g9_RTAverage = 0.0; int g9_timeSpent = 0; String g9_type = null; String g9which = "";
	        	int g9_hits = 0, g9_errors = 0; String g9_complete = ""; double g9_errI = 0, g9_errO = 0;
	        	double g9_ERI = 0.0, g9_ERO = 0.0;
	        double g10_HR = 0.0; double g10_ER = 0.0; double g10_RTAverage = 0.0; int g10_timeSpent = 0; String g10_type = null; String g10which = "";
	        	int g10_hits = 0, g10_errors = 0; String g10_complete = ""; double g10_errI = 0, g10_errO = 0;
	        	double g10_ERI = 0.0, g10_ERO = 0.0;
	        double g11_HR = 0.0; double g11_ER = 0.0; double g11_RTAverage = 0.0; int g11_timeSpent = 0; String g11_type = null; String g11which = "";
	        	int g11_hits = 0, g11_errors = 0; String g11_complete = ""; double g11_errI = 0, g11_errO = 0;
	        	double g11_ERI = 0.0, g11_ERO = 0.0;
	        double g12_HR = 0.0; double g12_ER = 0.0; double g12_RTAverage = 0.0; int g12_timeSpent = 0; String g12_type = null; String g12which = "";
	        	int g12_hits = 0, g12_errors = 0; String g12_complete = ""; double g12_errI = 0, g12_errO = 0;
	        	double g12_ERI = 0.0, g12_ERO = 0.0;
	        double g13_HR = 0.0; double g13_ER = 0.0; double g13_RTAverage = 0.0; int g13_timeSpent = 0; String g13_type = null; String g13which = "";
	        	int g13_hits = 0, g13_errors = 0; String g13_complete = ""; double g13_errI = 0, g13_errO = 0;
	        	double g13_ERI = 0.0, g13_ERO = 0.0;
	        double g14_HR = 0.0; double g14_ER = 0.0; double g14_RTAverage = 0.0; int g14_timeSpent = 0; String g14_type = null; String g14which = "";
	        	int g14_hits = 0, g14_errors = 0; String g14_complete = ""; double g14_errI = 0, g14_errO = 0;
	        	double g14_ERI = 0.0, g14_ERO = 0.0;
	        double g15_HR = 0.0; double g15_ER = 0.0; double g15_RTAverage = 0.0; int g15_timeSpent = 0; String g15_type = null; String g15which = "";
	        	int g15_hits = 0, g15_errors = 0; String g15_complete = ""; double g15_errI = 0, g15_errO = 0;
	        	double g15_ERI = 0.0, g15_ERO = 0.0;
	        
	        double tot1 = 120.0; //total pictures for spatial/letterNumber
	        double tot2 = 720.0; //total pictures for imgs.
	        
	        /*ADD FOR EACH GAME:
	         * 1) change hitrate and errRate of COMPLETED (And only keep completed rates) to be out of total # of pics/frames shown
	         * 2) hr = correct clicks/total, er = incorrect clicks+omissions/total
	         * 3) indicator for if the game is complete ADDED
	         * 4) Indicator if it belonged to t1 or t2 ADDED
	         * 5) columns for total hits and total misclicks and total omission clicks
	         * 6) add 5) and number of games complete column for each tx_subtype, tx, and overall 
	         * +7) overallavgHR and ER read directly from nbackreport; AutoHR and AutoER 
	         * +8) Reformat to add columns ADDED
	         */
	       
	        //boolean hasDuration = false;

	        //Begin calculations.
	        //int[] gameIndex = new int[nback_report.length];

	        for(int i = 0;i<nback_report.length;i++)
	        {
	        	if(nback_report[i][10]!=null && nback_report[i][10].contains("Retrieve"))//this row has a game b/c it has a game ID here 
	        	{
	        		int gameID = Integer.parseInt(nback_report[i][1]);
	        		int duration = 0;
	        		double rt = 0;
	        		boolean rtFound = false;
	        		String isComplete = "";
	        		int complete = 0;
	        		//Date thisDate = createDate(nback_report[i][3]); //check if date is actually valid; if it's not then disregard (for now) A.P
	        		String type = nback_report[i][7];
	        		int hits = Integer.parseInt(nback_report[i][8]);
	        		int errors = Integer.parseInt(nback_report[i][9]);
	        		double errOmission = 0;
	        		double errIncorrect = 0;
	        		if(hits+errors == 0)
	        		{
	        			//System.out.println(nback_report[i][8]);
	        			continue;
	        		}
	        		double hr = 0.0;
	        		double er = 0.0;
	        		double eri = 0.0; //incorrect click error rate
	        		double ero = 0.0; //omission click error rate
	        		
	        		
	        		//now for the response rate: cross-reference game ID with the nbackresponses folder
	        		for (int j = 0; j < responses.length; j++) {
						if(responses[j].getName().contains("_"+gameID+".txt")) //if the files has this particular ending (prevents problems like containing "1" vs "10")
						{
							Double[] results = getResponseRate(responses[j].getPath());
							rt = results[0];
							errOmission = results[1];
							//System.out.println("Omission errors for game "+gameID+" is "+errOmission);
							errIncorrect = results[2];
							//System.out.println("Incorrect errors for game "+gameID+" is "+errIncorrect);
							if(rt!=0.0)
							{
								rtFound = true;
							}
							break;
						}
	        		}
	        		if(!rtFound)
	        		{
	        			System.out.println("Average response rate could not be determined for game "+gameID);
	        		}
	        		
	        		if(!nback_report[i][2].equals("Unfinished"))
	        		{
	        			//hasDuration = true;
	        			duration = Integer.parseInt(nback_report[i][2]);
	        			isComplete = "Yes";
	        			complete = 1;
	        			if(type == "Images from folders")
	        			{
	        				hr = hits/tot2;
		        			er = errors/tot2;
		        			eri = errIncorrect/tot2;
		        			ero = errOmission/tot2;
	        			}
	        			else
	        			{
	        				hr = hits/tot1;
	        				er = errors/tot1;
	        				eri = errIncorrect/tot1;
		        			ero = errOmission/tot1;
	        			}
	        		}
	        		else
	        		{
	        			isComplete = "No";
	        			complete = 0;
	        		}
	        		//Figure out if it's the first or second session; assign the "which" variable of the game
	        		int whichSession = Integer.parseInt(nback_report[i][5]);
	        		String whichAsString = "T";
	        		
	        		//If 1, is part of t1, else part of t2
	        		
	        		//Got hr, er, rt, duration, type, and if it's in t1 or t2 for this game.
	        		
	        		//ADD TO OVERALLS
	        		//totalHROverall += hr;
	        		//totalEROverall += er;
	        		//numCompletedOverall+= complete;
	        			
	        		totalHROverall += hr;
	        		totalEROverall += er;
	        		totalERIOverall += eri;
	        		totalEROOverall += ero;
	        		numCompletedOverall+= complete;
	        		totalGamesOverall++;
	        		timeOverall += duration;
	        		totalRTOverall += rt;
	        		totalHitsOverall += hits;
        			totalErrOverall += errors;
        			totalErrIOverall = errIncorrect;
        			totalErrOOverall += errOmission;
	        		
	        		if(whichSession == 1)
	        		{
	        			//Add to t1
	        			t1_totalHROverall += hr;
	        			t1_totalEROverall += er;
	        			t1_totalERIOverall += eri;
		        		t1_totalEROOverall += ero;
	        			t1_totalGamesOverall++;
	        			t1_timeOverall += duration;
	        			t1_totalRTOverall += rt;
	        			t1_totalHitsOverall += hits;
	        			t1_totalErrOverall += errors;
	        			t1_numCompletedOverall += complete;
	        			t1_totalErrIOverall += errIncorrect;
	        			t1_totalErrOOverall += errOmission;
	        			
	        			whichAsString +="1";
	        			
	        			if(type.equals("Spatial Grid with Brain"))
	        			{
	        				t1_totalHRSpatial += hr;
		        			t1_totalERSpatial += er;
		        			t1_totalERISpatial += eri;
			        		t1_totalEROSpatial += ero;
		        			t1_totalGamesSpatial++;
		        			t1_timeSpatial += duration;
		        			t1_totalRTSpatial += rt;
		        			t1_totalHitsSpatial += hits;
		        			t1_totalErrSpatial += errors;
		        			t1_numCompletedSpatial += complete;
		        			t1_totalErrISpatial += errIncorrect;
		        			t1_totalErrOSpatial += errOmission;
	        			}
	        			else if(type.equals("Letters and Numbers"))
	        			{
	        				t1_totalHRLetNum += hr;
		        			t1_totalERLetNum += er;
		        			t1_totalERILetNum += eri;
			        		t1_totalEROLetNum += ero;
		        			t1_totalGamesLetNum++;
		        			t1_timeLetNum += duration;
		        			t1_totalRTLetNum += rt;
		        			t1_totalHitsLetNum += hits;
		        			t1_totalErrLetNum += errors;
		        			t1_numCompletedLetNum += complete;
		        			t1_totalErrILetNum += errIncorrect;
		        			t1_totalErrOLetNum += errOmission;
	        			}
	        			else if(type.equals("Images from folders"))
	        			{
	        				t1_totalHRImg += hr;
		        			t1_totalERImg += er;
		        			t1_totalERIImg += eri;
			        		t1_totalEROImg += ero;
		        			t1_totalGamesImg++;
		        			t1_timeImg += duration;
		        			t1_totalRTImg += rt;
		        			t1_totalHitsImg += hits;
		        			t1_totalErrImg += errors;
		        			t1_numCompletedImg += complete;
		        			t1_totalErrIImg += errIncorrect;
		        			t1_totalErrOImg += errOmission;
	        			}
	        		}
	        		else
	        		{
	        			//Add to t2
	        			t2_totalHROverall += hr;
	        			t2_totalEROverall += er;
	        			t2_totalERIOverall += eri;
		        		t2_totalEROOverall += ero;
	        			t2_totalGamesOverall++;
	        			t2_timeOverall += duration;
	        			t2_totalRTOverall += rt;
	        			t2_totalHitsOverall += hits;
	        			t2_totalErrOverall += errors;
	        			t2_numCompletedOverall += complete;
	        			t2_totalErrIOverall += errIncorrect;
	        			t2_totalErrOOverall += errOmission;
	        			
	        			whichAsString +="2";
	        			
	        			if(type.equals("Spatial Grid with Brain"))
	        			{
	        				t2_totalHRSpatial += hr;
		        			t2_totalERSpatial += er;
		        			t2_totalERISpatial += eri;
			        		t2_totalEROSpatial += ero;
		        			t2_totalGamesSpatial++;
		        			t2_timeSpatial += duration;
		        			t2_totalRTSpatial += rt;
		        			t2_totalHitsSpatial += hits;
		        			t2_totalErrSpatial += errors;
		        			t2_numCompletedSpatial += complete;
		        			t2_totalErrISpatial += errIncorrect;
		        			t2_totalErrOSpatial += errOmission;
	        			}
	        			else if(type.equals("Letters and Numbers"))
	        			{
	        				t2_totalHRLetNum += hr;
		        			t2_totalERLetNum += er;
		        			t2_totalERILetNum += eri;
			        		t2_totalEROLetNum += ero;
		        			t2_totalGamesLetNum++;
		        			t2_timeLetNum += duration;
		        			t2_totalRTLetNum += rt;
		        			t2_totalHitsLetNum += hits;
		        			t2_totalErrLetNum += errors;
		        			t2_numCompletedLetNum += complete;
		        			t2_totalErrILetNum += errIncorrect;
		        			t2_totalErrOLetNum += errOmission;
	        			}
	        			else if(type.equals("Images from folders"))
	        			{
	        				t2_totalHRImg += hr;
		        			t2_totalERImg += er;
		        			t2_totalERIImg += eri;
			        		t2_totalEROImg += ero;
		        			t2_totalGamesImg++;
		        			t2_timeImg += duration;
		        			t2_totalRTImg += rt;
		        			t2_totalHitsImg += hits;
		        			t2_totalErrImg += errors;
		        			t2_numCompletedImg += complete;
		        			t1_totalErrIImg += errIncorrect;
		        			t1_totalErrOImg += errOmission;
	        			}
	        		}
	        		
	        		//Set data for each game
	        		if(g1_type == null)
	        		{
	        			g1_HR = hr;
	        			g1_ER = er;
	        			g1_ERI = eri;
	        			g1_ERO = ero;
	        			g1_RTAverage = rt;
	        			g1_type = type;
	        			g1_timeSpent = duration;
	        			g1_hits = hits;
	        			g1_errors = errors;
	        			g1which = whichAsString;
	        			g1_complete = isComplete;
	        			g1_errI = errIncorrect;
	        			g1_errO = errOmission;
	        		}
	        		else if(g2_type == null)
	        		{
	        			g2_HR = hr;
	        			g2_ER = er;
	        			g2_ERI = eri;
	        			g2_ERO = ero;
	        			g2_RTAverage = rt;
	        			g2_type = type;
	        			g2_timeSpent = duration;
	        			g2_hits = hits;
	        			g2_errors = errors;
	        			g2which = whichAsString;
	        			g2_complete = isComplete;
	        			g2_errI = errIncorrect;
	        			g2_errO = errOmission;

	        		}
	        		else if(g3_type == null)
	        		{
	        			g3_HR = hr;
	        			g3_ER = er;
	        			g3_ERI = eri;
	        			g3_ERO = ero;
	        			g3_RTAverage = rt;
	        			g3_type = type;
	        			g3_timeSpent = duration;
	        			g3_hits = hits;
	        			g3_errors = errors;
	        			g3which = whichAsString;
	        			g3_complete = isComplete;
	        			g3_errI = errIncorrect;
	        			g3_errO = errOmission;

	        		}
	        		else if(g4_type == null)
	        		{
	        			g4_HR = hr;
	        			g4_ER = er;
	        			g4_ERI = eri;
	        			g4_ERO = ero;
	        			g4_RTAverage = rt;
	        			g4_type = type;
	        			g4_timeSpent = duration;
	        			g4_hits = hits;
	        			g4_errors = errors;
	        			g4which = whichAsString;
	        			g4_complete = isComplete;
	        			g4_errI = errIncorrect;
	        			g4_errO = errOmission;

	        		}
	        		else if(g5_type == null)
	        		{
	        			g5_HR = hr;
	        			g5_ER = er;
	        			g5_ERI = eri;
	        			g5_ERO = ero;
	        			g5_RTAverage = rt;
	        			g5_type = type;
	        			g5_timeSpent = duration;
	        			g5_hits = hits;
	        			g5_errors = errors;
	        			g5which = whichAsString;
	        			g5_complete = isComplete;
	        			g5_errI = errIncorrect;
	        			g5_errO = errOmission;

	        		}
	        		else if(g6_type == null)
	        		{
	        			g6_HR = hr;
	        			g6_ER = er;
	        			g6_ERI = eri;
	        			g6_ERO = ero;
	        			g6_RTAverage = rt;
	        			g6_type = type;
	        			g6_timeSpent = duration;
	        			g6_hits = hits;
	        			g6_errors = errors;
	        			g6which = whichAsString;
	        			g6_complete = isComplete;
	        			g6_errI = errIncorrect;
	        			g6_errO = errOmission;

	        		}
	        		else if(g7_type == null)
	        		{
	        			g7_HR = hr;
	        			g7_ER = er;
	        			g7_ERI = eri;
	        			g7_ERO = ero;
	        			g7_RTAverage = rt;
	        			g7_type = type;
	        			g7_timeSpent = duration;
	        			g7_hits = hits;
	        			g7_errors = errors;
	        			g7which = whichAsString;
	        			g7_complete = isComplete;
	        			g7_errI = errIncorrect;
	        			g7_errO = errOmission;

	        		}
	        		else if(g8_type == null)
	        		{
	        			g8_HR = hr;
	        			g8_ER = er;
	        			g8_ERI = eri;
	        			g8_ERO = ero;
	        			g8_RTAverage = rt;
	        			g8_type = type;
	        			g8_timeSpent = duration;
	        			g8_hits = hits;
	        			g8_errors = errors;
	        			g8which = whichAsString;
	        			g8_complete = isComplete;
	        			g8_errI = errIncorrect;
	        			g8_errO = errOmission;
	        		}
	        		else if(g9_type == null)
	        		{
	        			g9_HR = hr;
	        			g9_ER = er;
	        			g9_ERI = eri;
	        			g9_ERO = ero;
	        			g9_RTAverage = rt;
	        			g9_type = type;
	        			g9_timeSpent = duration;
	        			g9_hits = hits;
	        			g9_errors = errors;
	        			g9which = whichAsString;
	        			g9_complete = isComplete;
	        			g9_errI = errIncorrect;
	        			g9_errO = errOmission;
	        		}
	        		else if(g10_type == null)
	        		{
	        			g10_HR = hr;
	        			g10_ER = er;
	        			g10_ERI = eri;
	        			g10_ERO = ero;
	        			g10_RTAverage = rt;
	        			g10_type = type;
	        			g10_timeSpent = duration;
	        			g10_hits = hits;
	        			g10_errors = errors;
	        			g10which = whichAsString;
	        			g10_complete = isComplete;
	        			g10_errI = errIncorrect;
	        			g10_errO = errOmission;
	        		}
	        		else if(g11_type == null)
	        		{
	        			g11_HR = hr;
	        			g11_ER = er;
	        			g11_ERI = eri;
	        			g11_ERO = ero;
	        			g11_RTAverage = rt;
	        			g11_type = type;
	        			g11_timeSpent = duration;
	        			g11_hits = hits;
	        			g11_errors = errors;
	        			g11which = whichAsString;
	        			g11_complete = isComplete;
	        			g11_errI = errIncorrect;
	        			g11_errO = errOmission;
	        		}
	        		else if(g12_type == null)
	        		{
	        			g12_HR = hr;
	        			g12_ER = er;
	        			g12_ERI = eri;
	        			g12_ERO = ero;
	        			g12_RTAverage = rt;
	        			g12_type = type;
	        			g12_timeSpent = duration;
	        			g12_hits = hits;
	        			g12_errors = errors;
	        			g12which = whichAsString;
	        			g12_complete = isComplete;
	        			g12_errI = errIncorrect;
	        			g12_errO = errOmission;
	        		}
	        		else if(g13_type == null)
	        		{
	        			g13_HR = hr;
	        			g13_ER = er;
	        			g13_ERI = eri;
	        			g13_ERO = ero;
	        			g13_RTAverage = rt;
	        			g13_type = type;
	        			g13_timeSpent = duration;
	        			g13_hits = hits;
	        			g13_errors = errors;
	        			g13which = whichAsString;
	        			g13_complete = isComplete;
	        			g13_errI = errIncorrect;
	        			g13_errO = errOmission;
	        		}
	        		else if(g14_type == null)
	        		{
	        			g14_HR = hr;
	        			g14_ER = er;
	        			g14_ERI = eri;
	        			g14_ERO = ero;
	        			g14_RTAverage = rt;
	        			g14_type = type;
	        			g14_timeSpent = duration;
	        			g14_hits = hits;
	        			g14_errors = errors;
	        			g14which = whichAsString;
	        			g14_complete = isComplete;
	        			g14_errI = errIncorrect;
	        			g14_errO = errOmission;
	        		}
	        		else if(g15_type == null)
	        		{
	        			g15_HR = hr;
	        			g15_ER = er;
	        			g15_ERI = eri;
	        			g15_ERO = ero;
	        			g15_RTAverage = rt;
	        			g15_type = type;
	        			g15_timeSpent = duration;
	        			g15_hits = hits;
	        			g15_errors = errors;
	        			g15which = whichAsString;
	        			g15_complete = isComplete;
	        			g15_errI = errIncorrect;
	        			g15_errO = errOmission;
	        		}
	        		
	        	}
//	        	else
//	        	{
//	        		System.out.println("NOTHING ON THIS ROW");
//	        	}
	        }
	        
	        //Calculate averages.
	        avgHROverall = totalHROverall/numCompletedOverall;
	        avgEROverall = totalEROverall/numCompletedOverall;
	        avgERIOverall = totalERIOverall/numCompletedOverall;
	        avgEROOverall = totalEROOverall/numCompletedOverall;
	        avgRTOverall = totalRTOverall/numCompletedOverall;
	        
	        t1_avgHROverall = t1_totalHROverall/t1_numCompletedOverall;
	        t1_avgHRSpatial = t1_totalHRSpatial/t1_numCompletedSpatial;
	        t1_avgHRLetNum = t1_totalHRLetNum/t1_numCompletedLetNum;
	        t1_avgHRImg = t1_totalHRImg/t1_numCompletedImg;
	        
	        t1_avgEROverall = t1_totalEROverall/t1_numCompletedOverall;
	        t1_avgERSpatial = t1_totalERSpatial/t1_numCompletedSpatial;
	        t1_avgERLetNum = t1_totalERLetNum/t1_numCompletedLetNum;
	        t1_avgERImg = t1_totalERImg/t1_numCompletedImg;
	        
	        t1_avgERIOverall = t1_totalERIOverall/t1_numCompletedOverall;
	        t1_avgERISpatial = t1_totalERISpatial/t1_numCompletedSpatial;
	        t1_avgERILetNum = t1_totalERILetNum/t1_numCompletedLetNum;
	        t1_avgERIImg = t1_totalERIImg/t1_numCompletedImg;
	        
	        t1_avgEROOverall = t1_totalEROOverall/t1_numCompletedOverall;
	        t1_avgEROSpatial = t1_totalEROSpatial/t1_numCompletedSpatial;
	        t1_avgEROLetNum = t1_totalEROLetNum/t1_numCompletedLetNum;
	        t1_avgEROImg = t1_totalEROImg/t1_numCompletedImg;

	        t1_avgRTOverall = t1_totalRTOverall/t1_totalGamesOverall; //RT out of everything since they all should have an RT if they have hits/errors
	        t1_avgRTSpatial = t1_totalRTSpatial/t1_totalGamesSpatial;
	        t1_avgRTLetNum = t1_totalRTLetNum/t1_totalGamesLetNum;
	        t1_avgRTImg = t1_totalRTImg/t1_totalGamesImg;


	        t2_avgHROverall = t2_totalHROverall/t2_numCompletedOverall;
	        t2_avgHRSpatial = t2_totalHRSpatial/t2_numCompletedSpatial;
	        t2_avgHRLetNum = t2_totalHRLetNum/t2_numCompletedLetNum;
	        t2_avgHRImg = t2_totalHRImg/t2_numCompletedImg;
	        
	        t2_avgEROverall = t2_totalEROverall/t2_numCompletedOverall;
	        t2_avgERSpatial = t2_totalERSpatial/t2_numCompletedSpatial;
	        t2_avgERLetNum = t2_totalERLetNum/t2_numCompletedLetNum;
	        t2_avgERImg = t2_totalERImg/t2_numCompletedImg;
	        
	        t2_avgERIOverall = t2_totalERIOverall/t2_numCompletedOverall;
	        t2_avgERISpatial = t2_totalERISpatial/t2_numCompletedSpatial;
	        t2_avgERILetNum = t2_totalERILetNum/t2_numCompletedLetNum;
	        t2_avgERIImg = t2_totalERIImg/t2_numCompletedImg;
	        
	        t2_avgEROOverall = t2_totalEROOverall/t2_numCompletedOverall;
	        t2_avgEROSpatial = t2_totalEROSpatial/t2_numCompletedSpatial;
	        t2_avgEROLetNum = t2_totalEROLetNum/t2_numCompletedLetNum;
	        t2_avgEROImg = t2_totalEROImg/t2_numCompletedImg;

	        t2_avgRTOverall = t2_totalRTOverall/t2_totalGamesOverall; //RT out of everything
	        t2_avgRTSpatial = t2_totalRTSpatial/t2_totalGamesSpatial;
	        t2_avgRTLetNum = t2_totalRTLetNum/t2_totalGamesLetNum;
	        t2_avgRTImg = t2_totalRTImg/t2_totalGamesImg;
	        
	        //Finally, put everything into the table.
	        nbackBySession_table[index][0] = id; //from spreadsheet name
			nbackBySession_table[index][1] = ""; //blank
			nbackBySession_table[index][2] = ""; //blank
			nbackBySession_table[index][3] = ""; //blank
			nbackBySession_table[index][4] = ""; //blank
			nbackBySession_table[index][5] = ""; //blank
			nbackBySession_table[index][6] = ""; //blank
			nbackBySession_table[index][7] = ""; //blank
			
			//Now for the data
			nbackBySession_table[index][8] = String.valueOf(avgHROverall); //"HitRateAvg_Overall";
			nbackBySession_table[index][9] = String.valueOf(avgEROverall); //"ErrRateAvg_Overall";
			nbackBySession_table[index][10] = String.valueOf(totalGamesOverall); //"NumGamesPlayed_Overall";
			nbackBySession_table[index][11] = String.valueOf(timeOverall); //"TimeSpent_Overall"; //only finished ones
			nbackBySession_table[index][12] = String.valueOf(avgRTOverall); //"RTAverage_Overall";
			nbackBySession_table[index][13] = String.valueOf(totalHitsOverall); //"Total_Hits_Overall";
			nbackBySession_table[index][14] = String.valueOf(totalErrOverall); //"Total_Errors_Overall";
			nbackBySession_table[index][15] = String.valueOf(totalErrIOverall);//"TotalErrors_IncorrectOverall"; //NEW
			nbackBySession_table[index][16] = String.valueOf(totalErrOOverall);//"TotalErrors_OmissionOverall"; //NEW
			nbackBySession_table[index][17] = String.valueOf(numCompletedOverall); //"Games_Completed_Overall";
			
			nbackBySession_table[index][18] = String.valueOf(t1_avgHROverall); //"T1_HitRateAvg_Overall";
			nbackBySession_table[index][19] = String.valueOf(t1_avgEROverall); //"T1_ErrRateAvg_Overall";
			nbackBySession_table[index][20] = String.valueOf(t1_totalGamesOverall); //"T1_NumGamesPlayed_Overall";
			nbackBySession_table[index][21] = String.valueOf(t1_timeOverall); //"T1_TimeSpent_Overall";
			nbackBySession_table[index][22] = String.valueOf(t1_avgRTOverall); //"T1_RTAverage_Overall";
			nbackBySession_table[index][23] = String.valueOf(t1_totalHitsOverall);//"T1_TotalHits_Overall"; 
			nbackBySession_table[index][24] = String.valueOf(t1_totalErrOverall);//"T1_TotalErrors_Overall";
			nbackBySession_table[index][25] = String.valueOf(t1_totalErrIOverall);//"T1_TotalErrors_IncorrectOverall"; //NEW
			nbackBySession_table[index][26] = String.valueOf(t1_totalErrOOverall);//"T1_TotalErrors_OmissionOverall"; //NEW
			nbackBySession_table[index][27] = String.valueOf(t1_numCompletedOverall);//"T1_GamesCompleted_Overall"; 
			
			nbackBySession_table[index][28] = String.valueOf(t1_avgHRSpatial); //"T1_HitRateAvg_Spatial";
			nbackBySession_table[index][29] = String.valueOf(t1_avgHRLetNum); //"T1_HitRateAvg_LetterNumber"; 
			nbackBySession_table[index][30] = String.valueOf(t1_avgHRImg); //"T1_HitRateAvg_Image";
			
			nbackBySession_table[index][31] = String.valueOf(t1_avgERSpatial); //"T1_ErrRateAvg_Spatial"; 
			nbackBySession_table[index][32] = String.valueOf(t1_avgERLetNum); //"T1_ErrRateAvg_LetterNumber"; 
			nbackBySession_table[index][33] = String.valueOf(t1_avgERImg); //"T1_ErrRateAvg_Image"; 
			
			nbackBySession_table[index][34] = String.valueOf(t1_totalGamesSpatial); //"T1_NumGamesPlayed_Spatial";
			nbackBySession_table[index][35] = String.valueOf(t1_totalGamesLetNum); //"T1_NumGamesPlayed_LetterNumber";
			nbackBySession_table[index][36] = String.valueOf(t1_totalGamesImg); //"T1_NumGamesPlayed_Image";

			nbackBySession_table[index][37] = String.valueOf(t1_timeSpatial); //"T1_TimeSpent_Spatial";
			nbackBySession_table[index][38] = String.valueOf(t1_timeLetNum); //"T1_TimeSpent_LetterNumber";
			nbackBySession_table[index][39] = String.valueOf(t1_timeImg); //"T1_TimeSpent_Image";
			
			nbackBySession_table[index][40] = String.valueOf(t1_avgRTSpatial); //"T1_RTAverage_Spatial";
			nbackBySession_table[index][41] = String.valueOf(t1_avgRTLetNum); //"T1_RTAverage_LetterNumber";
			nbackBySession_table[index][42] = String.valueOf(t1_avgRTImg); //"T1_RTAverage_Image";
			
			nbackBySession_table[index][43] = String.valueOf(t1_totalHitsSpatial);//"T1_TotalHits_Spatial"; //START NEW
			nbackBySession_table[index][44] = String.valueOf(t1_totalHitsLetNum);//"T1_TotalHits_LetterNumber";
			nbackBySession_table[index][45] = String.valueOf(t1_totalHitsImg);//"T1_TotalHits_Image";		
		
			nbackBySession_table[index][46] = String.valueOf(t1_totalErrSpatial);//"T1_TotalErrors_Spatial";
			nbackBySession_table[index][47] = String.valueOf(t1_totalErrLetNum);//"T1_TotalErrors_LetterNumber";
			nbackBySession_table[index][48] = String.valueOf(t1_totalErrImg);//"T1_TotalErrors_Image";
			
			nbackBySession_table[index][49] = String.valueOf(t1_totalErrISpatial);//"T1_TotalErrors_IncorrectSpatial"; //NEW SECTION
			nbackBySession_table[index][50] = String.valueOf(t1_totalErrILetNum);//"T1_TotalErrors_IncorrectLetterNumber";
			nbackBySession_table[index][51] = String.valueOf(t1_totalErrIImg);//"T1_TotalErrors_IncorrectImage";
			
			nbackBySession_table[index][52] = String.valueOf(t1_totalErrOSpatial);//"T1_TotalErrors_OmissionSpatial"; //NEW SECTION
			nbackBySession_table[index][53] = String.valueOf(t1_totalErrOLetNum);//"T1_TotalErrors_OmissionLetterNumber";
			nbackBySession_table[index][54] = String.valueOf(t1_totalErrOImg);//"T1_TotalErrors_OmissionImage";

			nbackBySession_table[index][55] = String.valueOf(t1_numCompletedSpatial);//"T1_GamesCompleted_Spatial";
			nbackBySession_table[index][56] = String.valueOf(t1_numCompletedLetNum);//"T1_GamesCompleted_LetterNumber";
			nbackBySession_table[index][57] = String.valueOf(t1_numCompletedImg);//"T1_GamesCompleted_Image";
			
			
			nbackBySession_table[index][58] = String.valueOf(t2_avgHROverall); //"T2_HitRateAvg_Overall";
			nbackBySession_table[index][59] = String.valueOf(t2_avgEROverall); //"T2_ErrRateAvg_Overall";
			nbackBySession_table[index][60] = String.valueOf(t2_totalGamesOverall); //"T2_NumGamesPlayed_Overall";
			nbackBySession_table[index][61] = String.valueOf(t2_timeOverall); //"T2_TimeSpent_Overall";
			nbackBySession_table[index][62] = String.valueOf(t2_avgRTOverall); //"T2_RTAverage_Overall";
			nbackBySession_table[index][63] = String.valueOf(t2_totalHitsOverall);//"T2_TotalHits_Overall";
			nbackBySession_table[index][64] = String.valueOf(t2_totalErrOverall);//"T2_TotalErrors_Overall";
			nbackBySession_table[index][65] = String.valueOf(t2_totalErrIOverall);//"T2_TotalErrors_IncorrectOverall"; //NEW
			nbackBySession_table[index][66] = String.valueOf(t2_totalErrOOverall);//"T2_TotalErrors_OmissionOverall"; //NEW
			nbackBySession_table[index][67] = String.valueOf(t2_numCompletedOverall);//"T2_GamesCompleted_Overall"; 
			
			nbackBySession_table[index][68] = String.valueOf(t2_avgHRSpatial); //"T2_HitRateAvg_Spatial";
			nbackBySession_table[index][69] = String.valueOf(t2_avgHRLetNum); //"T2_HitRateAvg_LetterNumber"; 
			nbackBySession_table[index][70] = String.valueOf(t2_avgHRImg); //"T2_HitRateAvg_Image";

			nbackBySession_table[index][71] = String.valueOf(t2_avgERSpatial); //"T2_ErrRateAvg_Spatial"; 
			nbackBySession_table[index][72] = String.valueOf(t2_avgERLetNum); //"T2_ErrRateAvg_LetterNumber"; 
			nbackBySession_table[index][73] = String.valueOf(t2_avgERImg); //"T2_ErrRateAvg_Image"; 
			
			nbackBySession_table[index][74] = String.valueOf(t2_totalGamesSpatial); //"T2_NumGamesPlayed_Spatial";
			nbackBySession_table[index][75] = String.valueOf(t2_totalGamesLetNum); //"T2_NumGamesPlayed_LetterNumber";
			nbackBySession_table[index][76] = String.valueOf(t2_totalGamesImg); //"T2_NumGamesPlayed_Image";

			nbackBySession_table[index][77] = String.valueOf(t2_timeSpatial); //"T2_TimeSpent_Spatial";
			nbackBySession_table[index][78] = String.valueOf(t2_timeLetNum); //"T2_TimeSpent_LetterNumber";
			nbackBySession_table[index][79] = String.valueOf(t2_timeImg); //"T2_TimeSpent_Image";
			
			nbackBySession_table[index][80] = String.valueOf(t2_avgRTSpatial); //"T2_RTAverage_Spatial";
			nbackBySession_table[index][81] = String.valueOf(t2_avgRTLetNum); //"T2_RTAverage_LetterNumber";
			nbackBySession_table[index][82] = String.valueOf(t2_avgRTImg); //"T2_RTAverage_Image";
		
			nbackBySession_table[index][83] = String.valueOf(t2_totalHitsSpatial);//"T2_TotalHits_Spatial";
			nbackBySession_table[index][84] = String.valueOf(t2_totalHitsLetNum); //"T2_TotalHits_LetterNumber";
			nbackBySession_table[index][85] = String.valueOf(t2_totalHitsImg); //"T2_TotalHits_Image";		
				
			nbackBySession_table[index][86] = String.valueOf(t2_totalErrSpatial); //"T2_TotalErrors_Spatial";
			nbackBySession_table[index][87] = String.valueOf(t2_totalErrLetNum); //"T2_TotalErrors_LetterNumber";
			nbackBySession_table[index][88] = String.valueOf(t2_totalErrImg); //"T2_TotalErrors_Image";
			
			nbackBySession_table[index][89] = String.valueOf(t2_totalErrISpatial);//"T2_TotalErrors_IncorrectSpatial"; //NEW SECTION
			nbackBySession_table[index][90] = String.valueOf(t2_totalErrILetNum);//"T2_TotalErrors_IncorrectLetterNumber";
			nbackBySession_table[index][91] = String.valueOf(t2_totalErrIImg);//"T2_TotalErrors_IncorrectImage";
			
			nbackBySession_table[index][92] = String.valueOf(t2_totalErrOSpatial);//"T2_TotalErrors_OmissionSpatial"; //NEW SECTION
			nbackBySession_table[index][93] = String.valueOf(t2_totalErrOLetNum);//"T2_TotalErrors_OmissionLetterNumber";
			nbackBySession_table[index][94] = String.valueOf(t2_totalErrOImg);//"T2_TotalErrors_OmissionImage";

			nbackBySession_table[index][95] = String.valueOf(t2_numCompletedSpatial); //"T2_GamesCompleted_Spatial";
			nbackBySession_table[index][96] = String.valueOf(t2_numCompletedLetNum); //"T2_GamesCompleted_LetterNumber";
			nbackBySession_table[index][97] = String.valueOf(t2_numCompletedImg); //"T2_GamesCompleted_Image";
			
			//Now for each individual Game
			nbackBySession_table[index][98] = String.valueOf(g1_HR); //"Game1_HitRate";
			nbackBySession_table[index][99] = String.valueOf(g1_ER); //"Game1_ErrRate";
			nbackBySession_table[index][100] = String.valueOf(g1_RTAverage); //"Game1_RTAverage";
			nbackBySession_table[index][101] = String.valueOf(g1_timeSpent); //"Game1_TimeSpent"; //Seconds
			nbackBySession_table[index][102] = g1_type; //"Game1_Type";
			nbackBySession_table[index][103] = String.valueOf(g1_hits);//"Game1_TotalHits";
			nbackBySession_table[index][104] = String.valueOf(g1_errors);//"Game1_TotalErrors";
			nbackBySession_table[index][105] = String.valueOf(g1_errI);//"Game1_TotalIncorrectErrors";
			nbackBySession_table[index][106] = String.valueOf(g1_errO);//"Game1_TotalOmissionErrors";
			nbackBySession_table[index][107] = g1_complete;//"Game1_NumberComplete";
			nbackBySession_table[index][108] = g1which;//"Game1_T1orT2?";

			nbackBySession_table[index][109] = String.valueOf(g2_HR); //"Game2_HitRate";
			nbackBySession_table[index][110] = String.valueOf(g2_ER); //"Game2_ErrRate";
			nbackBySession_table[index][111] = String.valueOf(g2_RTAverage); //"Game2_RTAverage";
			nbackBySession_table[index][112] = String.valueOf(g2_timeSpent); //"Game2_TimeSpent"; //Seconds
			nbackBySession_table[index][113] = g2_type; //"Game2_Type";
			nbackBySession_table[index][114] = String.valueOf(g2_hits);//"Game2_TotalHits";
			nbackBySession_table[index][115] = String.valueOf(g2_errors);//"Game2_TotalErrors";
			nbackBySession_table[index][116] = String.valueOf(g2_errI);//"Game2_TotalIncorrectErrors";
			nbackBySession_table[index][117] = String.valueOf(g2_errO);//"Game2_TotalOmissionErrors";
			nbackBySession_table[index][118] = g2_complete;//"Game2_NumberComplete";
			nbackBySession_table[index][119] = g2which;//"Game2_T1orT2?";

			nbackBySession_table[index][120] = String.valueOf(g3_HR); //"Game3_HitRate";
			nbackBySession_table[index][121] = String.valueOf(g3_ER); //"Game3_ErrRate";
			nbackBySession_table[index][122] = String.valueOf(g3_RTAverage); //"Game3_RTAverage";
			nbackBySession_table[index][123] = String.valueOf(g3_timeSpent); //"Game3_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][124] = g3_type; //"Game3_Type";
			nbackBySession_table[index][125] = String.valueOf(g3_hits);//"Game3_TotalHits";
			nbackBySession_table[index][126] = String.valueOf(g3_errors);//"Game3_TotalErrors";
			nbackBySession_table[index][127] = String.valueOf(g3_errI);//"Game3_TotalIncorrectErrors";
			nbackBySession_table[index][128] = String.valueOf(g3_errO);//"Game3_TotalOmissionErrors";
			nbackBySession_table[index][129] = g3_complete;//"Game3_NumberComplete";
			nbackBySession_table[index][130] = g3which;//"Game3_T1orT2?";

			nbackBySession_table[index][131] = String.valueOf(g4_HR); //"Game4_HitRate";
			nbackBySession_table[index][132] = String.valueOf(g4_ER); //"Game4_ErrRate";
			nbackBySession_table[index][133] = String.valueOf(g4_RTAverage); //"Game4_RTAverage";
			nbackBySession_table[index][134] = String.valueOf(g4_timeSpent); //"Game4_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][135] = g4_type; //"Game4_Type";
			nbackBySession_table[index][136] = String.valueOf(g4_hits);//"Game4_TotalHits";
			nbackBySession_table[index][137] = String.valueOf(g4_errors);//"Game4_TotalErrors";
			nbackBySession_table[index][138] = String.valueOf(g4_errI);//"Game4_TotalIncorrectErrors";
			nbackBySession_table[index][139] = String.valueOf(g4_errO);//"Game4_TotalOmissionErrors";
			nbackBySession_table[index][140] = g4_complete;//"Game4_NumberComplete";
			nbackBySession_table[index][141] = g4which;//"Game4_T1orT2?";

			nbackBySession_table[index][142] = String.valueOf(g5_HR); //"Game5_HitRate";
			nbackBySession_table[index][143] = String.valueOf(g5_ER); //"Game5_ErrRate";
			nbackBySession_table[index][144] = String.valueOf(g5_RTAverage); //"Game5_RTAverage";
			nbackBySession_table[index][145] = String.valueOf(g5_timeSpent); //"Game5_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][146] = g5_type; //"Game5_Type";
			nbackBySession_table[index][147] = String.valueOf(g5_hits);//"Game5_TotalHits";
			nbackBySession_table[index][148] = String.valueOf(g5_errors);//"Game5_TotalErrors";
			nbackBySession_table[index][149] = String.valueOf(g5_errI);//"Game5_TotalIncorrectErrors";
			nbackBySession_table[index][150] = String.valueOf(g5_errO);//"Game5_TotalOmissionErrors";
			nbackBySession_table[index][151] = g5_complete;//"Game5_NumberComplete";
			nbackBySession_table[index][152] = g5which;//"Game5_T1orT2?";

			nbackBySession_table[index][153] = String.valueOf(g6_HR); //"Game6_HitRate";
			nbackBySession_table[index][154] = String.valueOf(g6_ER); //"Game6_ErrRate";
			nbackBySession_table[index][155] = String.valueOf(g6_RTAverage); //"Game6_RTAverage";
			nbackBySession_table[index][156] = String.valueOf(g6_timeSpent); //"Game6_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][157] = g6_type; //"Game6_Type";
			nbackBySession_table[index][158] = String.valueOf(g6_hits);//"Game6_TotalHits";
			nbackBySession_table[index][159] = String.valueOf(g6_errors);//"Game6_TotalErrors";
			nbackBySession_table[index][160] = String.valueOf(g6_errI);//"Game6_TotalIncorrectErrors";
			nbackBySession_table[index][161] = String.valueOf(g6_errO);//"Game6_TotalOmissionErrors";
			nbackBySession_table[index][162] = g6_complete;//"Game6_NumberComplete";
			nbackBySession_table[index][163] = g6which;//"Game6_T1orT2?";

			nbackBySession_table[index][164] = String.valueOf(g7_HR); //"Game7_HitRate";
			nbackBySession_table[index][165] = String.valueOf(g7_ER); //"Game7_ErrRate";
			nbackBySession_table[index][166] = String.valueOf(g7_RTAverage); //"Game7_RTAverage";
			nbackBySession_table[index][167] = String.valueOf(g7_timeSpent); //"Game7_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][168] = g7_type; //"Game7_Type";
			nbackBySession_table[index][169] = String.valueOf(g7_hits);//"Game7_TotalHits";
			nbackBySession_table[index][170] = String.valueOf(g7_errors);//"Game7_TotalErrors";
			nbackBySession_table[index][171] = String.valueOf(g7_errI);//"Game7_TotalIncorrectErrors";
			nbackBySession_table[index][172] = String.valueOf(g7_errO);//"Game7_TotalOmissionErrors";
			nbackBySession_table[index][173] = g7_complete;//"Game7_NumberComplete";
			nbackBySession_table[index][174] = g7which;//"Game7_T1orT2?";

			nbackBySession_table[index][175] = String.valueOf(g8_HR); //"Game8_HitRate";
			nbackBySession_table[index][176] = String.valueOf(g8_ER); //"Game8_ErrRate";
			nbackBySession_table[index][177] = String.valueOf(g8_RTAverage); //"Game8_RTAverage";
			nbackBySession_table[index][178] = String.valueOf(g8_timeSpent); //"Game8_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][179] = g8_type; //"Game8_Type";
			nbackBySession_table[index][180] = String.valueOf(g8_hits);//"Game8_TotalHits";
			nbackBySession_table[index][181] = String.valueOf(g8_errors);//"Game8_TotalErrors";
			nbackBySession_table[index][182] = String.valueOf(g8_errI);//"Game8_TotalIncorrectErrors";
			nbackBySession_table[index][183] = String.valueOf(g8_errO);//"Game8_TotalOmissionErrors";
			nbackBySession_table[index][184] = g8_complete;//"Game8_NumberComplete";
			nbackBySession_table[index][185] = g8which;//"Game8_T1orT2?";

			nbackBySession_table[index][186] = String.valueOf(g9_HR); //"Game9_HitRate";
			nbackBySession_table[index][187] = String.valueOf(g9_ER); //"Game9_ErrRate";
			nbackBySession_table[index][188] = String.valueOf(g9_RTAverage); //"Game9_RTAverage";
			nbackBySession_table[index][189] = String.valueOf(g9_timeSpent); //"Game9_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][190] = g9_type; //"Game9_Type";
			nbackBySession_table[index][191] = String.valueOf(g9_hits);//"Game9_TotalHits";
			nbackBySession_table[index][192] = String.valueOf(g9_errors);//"Game9_TotalErrors";
			nbackBySession_table[index][193] = String.valueOf(g9_errI);//"Game9_TotalIncorrectErrors";
			nbackBySession_table[index][194] = String.valueOf(g9_errO);//"Game9_TotalOmissionErrors";
			nbackBySession_table[index][195] = g9_complete;//"Game9_NumberComplete";
			nbackBySession_table[index][196] = g9which;//"Game9_T1orT2?";
			
			nbackBySession_table[index][197] = String.valueOf(g10_HR); //"Game10_HitRate";
			nbackBySession_table[index][198] = String.valueOf(g10_ER); //"Game10_ErrRate";
			nbackBySession_table[index][199] = String.valueOf(g10_RTAverage); //"Game10_RTAverage";
			nbackBySession_table[index][200] = String.valueOf(g10_timeSpent); //"Game10_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][201] = g10_type; //"Game10_Type";
			nbackBySession_table[index][202] = String.valueOf(g10_hits);//"Game10_TotalHits";
			nbackBySession_table[index][203] = String.valueOf(g10_errors);//"Game10_TotalErrors";
			nbackBySession_table[index][204] = String.valueOf(g10_errI);//"Game10_TotalIncorrectErrors";
			nbackBySession_table[index][205] = String.valueOf(g10_errO);//"Game10_TotalOmissionErrors";
			nbackBySession_table[index][206] = g10_complete;//"Game10_NumberComplete";
			nbackBySession_table[index][207] = g10which;//"Game10_T1orT2?";

			nbackBySession_table[index][208] = String.valueOf(g11_HR); //"Game11_HitRate";
			nbackBySession_table[index][209] = String.valueOf(g11_ER); //"Game11_ErrRate";
			nbackBySession_table[index][210] = String.valueOf(g11_RTAverage); //"Game11_RTAverage";
			nbackBySession_table[index][211] = String.valueOf(g11_timeSpent); //"Game11_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][212] = g11_type; //"Game11_Type";
			nbackBySession_table[index][213] = String.valueOf(g11_hits);//"Game11_TotalHits";
			nbackBySession_table[index][214] = String.valueOf(g11_errors);//"Game11_TotalErrors";
			nbackBySession_table[index][215] = String.valueOf(g11_errI);//"Game11_TotalIncorrectErrors";
			nbackBySession_table[index][216] = String.valueOf(g11_errO);//"Game11_TotalOmissionErrors";
			nbackBySession_table[index][217] = g11_complete;//"Game11_NumberComplete";
			nbackBySession_table[index][218] = g11which;//"Game11_T1orT2?";

			nbackBySession_table[index][219] = String.valueOf(g12_HR); //"Game12_HitRate";
			nbackBySession_table[index][220] = String.valueOf(g12_ER); //"Game12_ErrRate";
			nbackBySession_table[index][221] = String.valueOf(g12_RTAverage); //"Game12_RTAverage";
			nbackBySession_table[index][222] = String.valueOf(g12_timeSpent); //"Game12_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][223] = g12_type; //"Game12_Type";
			nbackBySession_table[index][224] = String.valueOf(g12_hits);//"Game12_TotalHits";
			nbackBySession_table[index][225] = String.valueOf(g12_errors);//"Game12_TotalErrors";
			nbackBySession_table[index][226] = String.valueOf(g12_errI);//"Game12_TotalIncorrectErrors";
			nbackBySession_table[index][227] = String.valueOf(g12_errO);//"Game12_TotalOmissionErrors";
			nbackBySession_table[index][228] = g12_complete;//"Game12_NumberComplete";
			nbackBySession_table[index][229] = g12which;//"Game12_T1orT2?";

			nbackBySession_table[index][230] = String.valueOf(g13_HR); //"Game13_HitRate";
			nbackBySession_table[index][231] = String.valueOf(g13_ER); //"Game13_ErrRate";
			nbackBySession_table[index][232] = String.valueOf(g13_RTAverage); //"Game13_RTAverage";
			nbackBySession_table[index][233] = String.valueOf(g13_timeSpent); //"Game13_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][234] = g13_type; //"Game13_Type";
			nbackBySession_table[index][235] = String.valueOf(g13_hits);//"Game13_TotalHits";
			nbackBySession_table[index][236] = String.valueOf(g13_errors);//"Game13_TotalErrors";
			nbackBySession_table[index][237] = String.valueOf(g13_errI);//"Game13_TotalIncorrectErrors";
			nbackBySession_table[index][238] = String.valueOf(g13_errO);//"Game13_TotalOmissionErrors";
			nbackBySession_table[index][239] = g13_complete;//"Game13_NumberComplete";
			nbackBySession_table[index][240] = g13which;//"Game13_T1orT2?";

			nbackBySession_table[index][241] = String.valueOf(g14_HR); //"Game14_HitRate";
			nbackBySession_table[index][242] = String.valueOf(g14_ER); //"Game14_ErrRate";
			nbackBySession_table[index][243] = String.valueOf(g14_RTAverage); //"Game14_RTAverage";
			nbackBySession_table[index][244] = String.valueOf(g14_timeSpent); //"Game14_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][245] = g14_type; //"Game14_Type";
			nbackBySession_table[index][246] = String.valueOf(g14_hits);//"Game14_TotalHits";
			nbackBySession_table[index][247] = String.valueOf(g14_errors);//"Game14_TotalErrors";
			nbackBySession_table[index][248] = String.valueOf(g14_errI);//"Game14_TotalIncorrectErrors";
			nbackBySession_table[index][249] = String.valueOf(g14_errO);//"Game14_TotalOmissionErrors";
			nbackBySession_table[index][250] = g14_complete;//"Game14_NumberComplete";
			nbackBySession_table[index][251] = g14which;//"Game14_T1orT2?";

			nbackBySession_table[index][252] = String.valueOf(g15_HR); //"Game15_HitRate";
			nbackBySession_table[index][253] = String.valueOf(g15_ER); //"Game15_ErrRate";
			nbackBySession_table[index][254] = String.valueOf(g15_RTAverage); //"Game15_RTAverage";
			nbackBySession_table[index][255] = String.valueOf(g15_timeSpent); //"Game15_TimeSpent"; //Either seconds or unfinished
			nbackBySession_table[index][256] = g15_type; //"Game15_Type";
			nbackBySession_table[index][257] = String.valueOf(g15_hits);//"Game15_TotalHits";
			nbackBySession_table[index][258] = String.valueOf(g15_errors);//"Game15_TotalErrors";
			nbackBySession_table[index][259] = String.valueOf(g15_errI);//"Game15_TotalIncorrectErrors";
			nbackBySession_table[index][260] = String.valueOf(g15_errO);//"Game15_TotalOmissionErrors";
			nbackBySession_table[index][261] = g15_complete;//"Game15_NumberComplete";
			nbackBySession_table[index][262] = g15which;//"Game15_T1orT2?";
			
			nbackBySession_table[index][263] = avgs_report[42][1];
			nbackBySession_table[index][264] = avgs_report[42][3];
			
			nbackBySession_table[index][265] = String.valueOf(avgERIOverall); //"ErrRateIncorrect_AvgOverall";
			nbackBySession_table[index][266] = String.valueOf(avgEROOverall); //"ErrRateOmission_AvgOverall";
			nbackBySession_table[index][267] = String.valueOf(t1_avgERIOverall); //"T1_ErrRateIncorrect_AvgOverall";
			nbackBySession_table[index][268] = String.valueOf(t1_avgERISpatial); //"T1_ErrRateIncorrect_AvgSpatial";
			nbackBySession_table[index][269] = String.valueOf(t1_avgERILetNum); //"T1_ErrRateIncorrect_AvgLetterNumber";
			nbackBySession_table[index][270] = String.valueOf(t1_avgERIImg); //"T1_ErrRateIncorrect_AvgImage";
			nbackBySession_table[index][271] = String.valueOf(t1_avgEROOverall); //"T1_ErrRateOmission_AvgOverall";
			nbackBySession_table[index][272] = String.valueOf(t1_avgEROSpatial); //"T1_ErrRateOmission_AvgSpatial";
			nbackBySession_table[index][273] = String.valueOf(t1_avgEROLetNum); //"T1_ErrRateOmission_AvgLetterNumber";
			nbackBySession_table[index][274] = String.valueOf(t1_avgEROImg); //"T1_ErrRateOmission_AvgImage";
			nbackBySession_table[index][275] = String.valueOf(t2_avgERIOverall); //"T2_ErrRateIncorrect_AvgOverall";
			nbackBySession_table[index][276] = String.valueOf(t2_avgERISpatial); //"T2_ErrRateIncorrect_AvgSpatial";
			nbackBySession_table[index][277] = String.valueOf(t2_avgERILetNum); //"T2_ErrRateIncorrect_AvgLetterNumber";
			nbackBySession_table[index][278] = String.valueOf(t2_avgERIImg); //"T2_ErrRateIncorrect_AvgImage";
			nbackBySession_table[index][279] = String.valueOf(t2_avgEROOverall); //"T2_ErrRateOmission_AvgOverall";
			nbackBySession_table[index][280] = String.valueOf(t2_avgEROSpatial); //"T2_ErrRateOmission_AvgSpatial";
			nbackBySession_table[index][281] = String.valueOf(t2_avgEROLetNum); //"T2_ErrRateOmission_AvgLetterNumber";
			nbackBySession_table[index][282] = String.valueOf(t2_avgEROImg); //"T2_ErrRateOmission_AvgImage";
			nbackBySession_table[index][283] = String.valueOf(g1_ERI); //"Game1_ErrRateIncorrect";
			nbackBySession_table[index][284] = String.valueOf(g1_ERO); //"Game1_ErrRateOmission";
			nbackBySession_table[index][285] = String.valueOf(g2_ERI); //"Game2_ErrRateIncorrect";
			nbackBySession_table[index][286] = String.valueOf(g2_ERO); //"Game2_ErrRateOmission";
			nbackBySession_table[index][287] = String.valueOf(g3_ERI); //"Game3_ErrRateIncorrect";
			nbackBySession_table[index][288] = String.valueOf(g3_ERO); //"Game3_ErrRateOmission";
			nbackBySession_table[index][289] = String.valueOf(g4_ERI); //"Game4_ErrRateIncorrect";
			nbackBySession_table[index][290] = String.valueOf(g4_ERO); //"Game4_ErrRateOmission";
			nbackBySession_table[index][291] = String.valueOf(g5_ERI); //"Game5_ErrRateIncorrect";
			nbackBySession_table[index][292] = String.valueOf(g5_ERO); //"Game5_ErrRateOmission";
			nbackBySession_table[index][293] = String.valueOf(g6_ERI); //"Game6_ErrRateIncorrect";
			nbackBySession_table[index][294] = String.valueOf(g6_ERO); //"Game6_ErrRateOmission";
			nbackBySession_table[index][295] = String.valueOf(g7_ERI); //"Game7_ErrRateIncorrect";
			nbackBySession_table[index][296] = String.valueOf(g7_ERO); //"Game7_ErrRateOmission";
			nbackBySession_table[index][297] = String.valueOf(g8_ERI); //"Game8_ErrRateIncorrect";
			nbackBySession_table[index][298] = String.valueOf(g8_ERO); //"Game8_ErrRateOmission";
			nbackBySession_table[index][299] = String.valueOf(g9_ERI); //"Game9_ErrRateIncorrect";
			nbackBySession_table[index][300] = String.valueOf(g9_ERO); //"Game9_ErrRateOmission";
			nbackBySession_table[index][301] = String.valueOf(g10_ERI); //"Game10_ErrRateIncorrect";
			nbackBySession_table[index][302] = String.valueOf(g10_ERO); //"Game10_ErrRateOmission";
			nbackBySession_table[index][303] = String.valueOf(g11_ERI); //"Game11_ErrRateIncorrect";
			nbackBySession_table[index][304] = String.valueOf(g11_ERO); //"Game11_ErrRateOmission";
			nbackBySession_table[index][305] = String.valueOf(g12_ERI); //"Game12_ErrRateIncorrect";
			nbackBySession_table[index][306] = String.valueOf(g12_ERO); //"Game12_ErrRateOmission";
			nbackBySession_table[index][307] = String.valueOf(g13_ERI); //"Game13_ErrRateIncorrect";
			nbackBySession_table[index][308] = String.valueOf(g13_ERO); //"Game13_ErrRateOmission";
	        
	        
		}
		catch(Exception e) //If it finds an error, don't continue
		{
			e.printStackTrace();
		}
		
	}
	private String[][] nbackBySessionInit(String[][] nbackBySession_table) {
		
		nbackBySession_table[0][0] = "ID"; //from spreadsheet name
		nbackBySession_table[0][1] = "Order"; //blank
		nbackBySession_table[0][2] = "Sex"; //blank
		nbackBySession_table[0][3] = "Group"; //blank
		nbackBySession_table[0][4] = "Stimulation"; //blank
		nbackBySession_table[0][5] = "Expectation"; //blank
		nbackBySession_table[0][6] = "Handedness"; //blank
		nbackBySession_table[0][7] = "N"; //blank
		
		//Now for the data
		nbackBySession_table[0][8] = "HitRateAvg_Overall";
		nbackBySession_table[0][9] = "ErrRateAvg_Overall";
		nbackBySession_table[0][10] = "NumGamesPlayed_Overall";
		nbackBySession_table[0][11] = "TimeSpent_Overall"; //only finished ones
		nbackBySession_table[0][12] = "RTAverage_Overall";
		nbackBySession_table[0][13] = "TotalHits_Overall";
		nbackBySession_table[0][14] = "TotalErrors_Overall";
		nbackBySession_table[0][15] = "TotalErrors_IncorrectOverall"; //NEW
		nbackBySession_table[0][16] = "TotalErrors_OmissionOverall"; //NEW
		nbackBySession_table[0][17] = "GamesCompleted_Overall";
		
		nbackBySession_table[0][18] = "T1_HitRateAvg_Overall";
		nbackBySession_table[0][19] = "T1_ErrRateAvg_Overall";
		nbackBySession_table[0][20] = "T1_NumGamesPlayed_Overall";
		nbackBySession_table[0][21] = "T1_TimeSpent_Overall";
		nbackBySession_table[0][22] = "T1_RTAverage_Overall";
		nbackBySession_table[0][23] = "T1_TotalHits_Overall";
		nbackBySession_table[0][24] = "T1_TotalErrors_Overall";
		nbackBySession_table[0][25] = "T1_TotalErrors_IncorrectOverall"; //NEW
		nbackBySession_table[0][26] = "T1_TotalErrors_OmissionOverall"; //NEW
		nbackBySession_table[0][27] = "T1_GamesCompleted_Overall";
		
		nbackBySession_table[0][28] = "T1_HitRateAvg_Spatial";
		nbackBySession_table[0][29] = "T1_HitRateAvg_LetterNumber"; 
		nbackBySession_table[0][30] = "T1_HitRateAvg_Image";
		
		nbackBySession_table[0][31] = "T1_ErrRateAvg_Spatial"; 
		nbackBySession_table[0][32] = "T1_ErrRateAvg_LetterNumber"; 
		nbackBySession_table[0][33] = "T1_ErrRateAvg_Image"; 
		
		nbackBySession_table[0][34] = "T1_NumGamesPlayed_Spatial";
		nbackBySession_table[0][35] = "T1_NumGamesPlayed_LetterNumber";
		nbackBySession_table[0][36] = "T1_NumGamesPlayed_Image";

		nbackBySession_table[0][37] = "T1_TimeSpent_Spatial";
		nbackBySession_table[0][38] = "T1_TimeSpent_LetterNumber";
		nbackBySession_table[0][39] = "T1_TimeSpent_Image";
		
		nbackBySession_table[0][40] = "T1_RTAverage_Spatial";
		nbackBySession_table[0][41] = "T1_RTAverage_LetterNumber";
		nbackBySession_table[0][42] = "T1_RTAverage_Image";

		nbackBySession_table[0][43] = "T1_TotalHits_Spatial";
		nbackBySession_table[0][44] = "T1_TotalHits_LetterNumber";
		nbackBySession_table[0][45] = "T1_TotalHits_Image";		

		nbackBySession_table[0][46] = "T1_TotalErrors_Spatial";
		nbackBySession_table[0][47] = "T1_TotalErrors_LetterNumber";
		nbackBySession_table[0][48] = "T1_TotalErrors_Image";
		
		nbackBySession_table[0][49] = "T1_TotalErrors_IncorrectSpatial"; //NEW SECTION
		nbackBySession_table[0][50] = "T1_TotalErrors_IncorrectLetterNumber";
		nbackBySession_table[0][51] = "T1_TotalErrors_IncorrectImage";
		
		nbackBySession_table[0][52] = "T1_TotalErrors_OmissionSpatial"; //NEW SECTION
		nbackBySession_table[0][53] = "T1_TotalErrors_OmissionLetterNumber";
		nbackBySession_table[0][54] = "T1_TotalErrors_OmissionImage";

		nbackBySession_table[0][55] = "T1_GamesCompleted_Spatial";
		nbackBySession_table[0][56] = "T1_GamesCompleted_LetterNumber";
		nbackBySession_table[0][57] = "T1_GamesCompleted_Image";

		
		nbackBySession_table[0][58] = "T2_HitRateAvg_Overall";
		nbackBySession_table[0][59] = "T2_ErrRateAvg_Overall";
		nbackBySession_table[0][60] = "T2_NumGamesPlayed_Overall";
		nbackBySession_table[0][61] = "T2_TimeSpent_Overall";
		nbackBySession_table[0][62] = "T2_RTAverage_Overall";
		nbackBySession_table[0][63] = "T2_TotalHits_Overall";
		nbackBySession_table[0][64] = "T2_TotalErrors_Overall";
		nbackBySession_table[0][65] = "T2_TotalErrors_IncorrectOverall"; //NEW
		nbackBySession_table[0][66] = "T2_TotalErrors_OmissionOverall"; //NEW
		nbackBySession_table[0][67] = "T2_GamesCompleted_Overall";
		
		nbackBySession_table[0][68] = "T2_HitRateAvg_Spatial";
		nbackBySession_table[0][69] = "T2_HitRateAvg_LetterNumber"; 
		nbackBySession_table[0][70] = "T2_HitRateAvg_Image";
	
		nbackBySession_table[0][71] = "T2_ErrRateAvg_Spatial"; 
		nbackBySession_table[0][72] = "T2_ErrRateAvg_LetterNumber"; 
		nbackBySession_table[0][73] = "T2_ErrRateAvg_Image"; 
	
		nbackBySession_table[0][74] = "T2_NumGamesPlayed_Spatial";
		nbackBySession_table[0][75] = "T2_NumGamesPlayed_LetterNumber";
		nbackBySession_table[0][76] = "T2_NumGamesPlayed_Image";

		nbackBySession_table[0][77] = "T2_TimeSpent_Spatial";
		nbackBySession_table[0][78] = "T2_TimeSpent_LetterNumber";
		nbackBySession_table[0][79] = "T2_TimeSpent_Image";
		
		nbackBySession_table[0][80] = "T2_RTAverage_Spatial";
		nbackBySession_table[0][81] = "T2_RTAverage_LetterNumber";
		nbackBySession_table[0][82] = "T2_RTAverage_Image";
		
		nbackBySession_table[0][83] = "T2_TotalHits_Spatial";
		nbackBySession_table[0][84] = "T2_TotalHits_LetterNumber";
		nbackBySession_table[0][85] = "T2_TotalHits_Image";		
			
		nbackBySession_table[0][86] = "T2_TotalErrors_Spatial";
		nbackBySession_table[0][87] = "T2_TotalErrors_LetterNumber";
		nbackBySession_table[0][88] = "T2_TotalErrors_Image";
		
		nbackBySession_table[0][89] = "T2_TotalErrors_IncorrectSpatial"; //NEW SECTION
		nbackBySession_table[0][90] = "T2_TotalErrors_IncorrectLetterNumber";
		nbackBySession_table[0][91] = "T2_TotalErrors_IncorrectImage";
		
		nbackBySession_table[0][92] = "T2_TotalErrors_OmissionSpatial"; //NEW SECTION
		nbackBySession_table[0][93] = "T2_TotalErrors_OmissionLetterNumber";
		nbackBySession_table[0][94] = "T2_TotalErrors_OmissionImage";
		
		nbackBySession_table[0][95] = "T2_GamesCompleted_Spatial";
		nbackBySession_table[0][96] = "T2_GamesCompleted_LetterNumber";
		nbackBySession_table[0][97] = "T2_GamesCompleted_Image";
		
		//Now for each individual Game
		nbackBySession_table[0][98] = "Game1_HitRate";
		nbackBySession_table[0][99] = "Game1_ErrRate";
		nbackBySession_table[0][100] = "Game1_RTAverage";
		nbackBySession_table[0][101] = "Game1_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][102] = "Game1_Type";
		nbackBySession_table[0][103] = "Game1_TotalHits";
		nbackBySession_table[0][104] = "Game1_TotalErrors";
		nbackBySession_table[0][105] = "Game1_TotalIncorrectErrors";
		nbackBySession_table[0][106] = "Game1_TotalOmissionErrors";
		nbackBySession_table[0][107] = "Game1_Complete?";
		nbackBySession_table[0][108] = "Game1_T1orT2?";
		
		nbackBySession_table[0][109]= "Game2_HitRate";
		nbackBySession_table[0][110] = "Game2_ErrRate";
		nbackBySession_table[0][111] = "Game2_RTAverage";
		nbackBySession_table[0][112] = "Game2_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][113] = "Game2_Type";
		nbackBySession_table[0][114] = "Game2_TotalHits";
		nbackBySession_table[0][115] = "Game2_TotalErrors";
		nbackBySession_table[0][116] = "Game2_TotalIncorrectErrors";
		nbackBySession_table[0][117] = "Game2_TotalOmissionErrors";
		nbackBySession_table[0][118] = "Game2_Complete?";
		nbackBySession_table[0][119] = "Game2_T1orT2?";

		nbackBySession_table[0][120] = "Game3_HitRate";
		nbackBySession_table[0][121] = "Game3_ErrRate";
		nbackBySession_table[0][122] = "Game3_RTAverage";
		nbackBySession_table[0][123] = "Game3_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][124] = "Game3_Type";
		nbackBySession_table[0][125] = "Game3_TotalHits";
		nbackBySession_table[0][126] = "Game3_TotalErrors";
		nbackBySession_table[0][127] = "Game3_TotalIncorrectErrors";
		nbackBySession_table[0][128] = "Game3_TotalOmissionErrors";
		nbackBySession_table[0][129] = "Game3_Complete?";
		nbackBySession_table[0][130] = "Game3_T1orT2?";

		nbackBySession_table[0][131] = "Game4_HitRate";
		nbackBySession_table[0][132] = "Game4_ErrRate";
		nbackBySession_table[0][133] = "Game4_RTAverage";
		nbackBySession_table[0][134] = "Game4_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][135] = "Game4_Type";
		nbackBySession_table[0][136] = "Game4_TotalHits";
		nbackBySession_table[0][137] = "Game4_TotalErrors";
		nbackBySession_table[0][138] = "Game4_TotalIncorrectErrors";
		nbackBySession_table[0][139] = "Game4_TotalOmissionErrors";
		nbackBySession_table[0][140] = "Game4_Complete?";
		nbackBySession_table[0][141] = "Game4_T1orT2?";
		
		nbackBySession_table[0][142] = "Game5_HitRate";
		nbackBySession_table[0][143] = "Game5_ErrRate";
		nbackBySession_table[0][144] = "Game5_RTAverage";
		nbackBySession_table[0][145] = "Game5_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][146] = "Game5_Type";
		nbackBySession_table[0][147] = "Game5_TotalHits";
		nbackBySession_table[0][148] = "Game5_TotalErrors";
		nbackBySession_table[0][149] = "Game5_TotalIncorrectErrors";
		nbackBySession_table[0][150] = "Game5_TotalOmissionErrors";
		nbackBySession_table[0][151] = "Game5_Complete?";
		nbackBySession_table[0][152] = "Game5_T1orT2?";

		nbackBySession_table[0][153] = "Game6_HitRate";
		nbackBySession_table[0][154] = "Game6_ErrRate";
		nbackBySession_table[0][155] = "Game6_RTAverage";
		nbackBySession_table[0][156] = "Game6_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][157] = "Game6_Type";
		nbackBySession_table[0][158] = "Game6_TotalHits";
		nbackBySession_table[0][159] = "Game6_TotalErrors";
		nbackBySession_table[0][160] = "Game6_TotalIncorrectErrors";
		nbackBySession_table[0][161] = "Game6_TotalOmissionErrors";
		nbackBySession_table[0][162] = "Game6_Complete?";
		nbackBySession_table[0][163] = "Game6_T1orT2?";

		nbackBySession_table[0][164] = "Game7_HitRate";
		nbackBySession_table[0][165] = "Game7_ErrRate";
		nbackBySession_table[0][166] = "Game7_RTAverage";
		nbackBySession_table[0][167] = "Game7_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][168] = "Game7_Type";
		nbackBySession_table[0][169] = "Game7_TotalHits";
		nbackBySession_table[0][170] = "Game7_TotalErrors";
		nbackBySession_table[0][171] = "Game7_TotalIncorrectErrors";
		nbackBySession_table[0][172] = "Game7_TotalOmissionErrors";
		nbackBySession_table[0][173] = "Game7_Complete?";
		nbackBySession_table[0][174] = "Game7_T1orT2?";

		nbackBySession_table[0][175] = "Game8_HitRate";
		nbackBySession_table[0][176] = "Game8_ErrRate";
		nbackBySession_table[0][177] = "Game8_RTAverage";
		nbackBySession_table[0][178] = "Game8_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][179] = "Game8_Type";
		nbackBySession_table[0][180] = "Game8_TotalHits";
		nbackBySession_table[0][181] = "Game8_TotalErrors";
		nbackBySession_table[0][182] = "Game8_TotalIncorrectErrors";
		nbackBySession_table[0][183] = "Game8_TotalOmissionErrors";
		nbackBySession_table[0][184] = "Game8_Complete?";
		nbackBySession_table[0][185] = "Game8_T1orT2?";

		nbackBySession_table[0][186] = "Game9_HitRate";
		nbackBySession_table[0][187] = "Game9_ErrRate";
		nbackBySession_table[0][188] = "Game9_RTAverage";
		nbackBySession_table[0][189] = "Game9_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][190] = "Game9_Type";
		nbackBySession_table[0][191] = "Game9_TotalHits";
		nbackBySession_table[0][192] = "Game9_TotalErrors";
		nbackBySession_table[0][193] = "Game9_TotalIncorrectErrors";
		nbackBySession_table[0][194] = "Game9_TotalOmissionErrors";
		nbackBySession_table[0][195] = "Game9_Complete?";
		nbackBySession_table[0][196] = "Game9_T1orT2?";

		nbackBySession_table[0][197] = "Game10_HitRate";
		nbackBySession_table[0][198] = "Game10_ErrRate";
		nbackBySession_table[0][199] = "Game10_RTAverage";
		nbackBySession_table[0][200] = "Game10_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][201] = "Game10_Type";
		nbackBySession_table[0][202] = "Game10_TotalHits";
		nbackBySession_table[0][203] = "Game10_TotalErrors";
		nbackBySession_table[0][204] = "Game10_TotalIncorrectErrors";
		nbackBySession_table[0][205] = "Game10_TotalOmissionErrors";
		nbackBySession_table[0][206] = "Game10_Complete?";
		nbackBySession_table[0][207] = "Game10_T1orT2?";

		nbackBySession_table[0][208] = "Game11_HitRate";
		nbackBySession_table[0][209] = "Game11_ErrRate";
		nbackBySession_table[0][210] = "Game11_RTAverage";
		nbackBySession_table[0][211] = "Game11_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][212] = "Game11_Type";
		nbackBySession_table[0][213] = "Game11_TotalHits";
		nbackBySession_table[0][214] = "Game11_TotalErrors";
		nbackBySession_table[0][215] = "Game11_TotalIncorrectErrors";
		nbackBySession_table[0][216] = "Game11_TotalOmissionErrors";
		nbackBySession_table[0][217] = "Game11_Complete?";
		nbackBySession_table[0][218] = "Game11_T1orT2?";

		nbackBySession_table[0][219] = "Game12_HitRate";
		nbackBySession_table[0][220] = "Game12_ErrRate";
		nbackBySession_table[0][221] = "Game12_RTAverage";
		nbackBySession_table[0][222] = "Game12_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][223] = "Game12_Type";
		nbackBySession_table[0][224] = "Game12_TotalHits";
		nbackBySession_table[0][225] = "Game12_TotalErrors";
		nbackBySession_table[0][226] = "Game12_TotalIncorrectErrors";
		nbackBySession_table[0][227] = "Game12_TotalOmissionErrors";
		nbackBySession_table[0][228] = "Game12_Complete?";
		nbackBySession_table[0][229] = "Game12_T1orT2?";

		nbackBySession_table[0][230] = "Game13_HitRate";
		nbackBySession_table[0][231] = "Game13_ErrRate";
		nbackBySession_table[0][232] = "Game13_RTAverage";
		nbackBySession_table[0][233] = "Game13_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][234] = "Game13_Type";
		nbackBySession_table[0][235] = "Game13_TotalHits";
		nbackBySession_table[0][236] = "Game13_TotalErrors";
		nbackBySession_table[0][237] = "Game13_TotalIncorrectErrors";
		nbackBySession_table[0][238] = "Game13_TotalOmissionErrors";
		nbackBySession_table[0][239] = "Game13_Complete?";
		nbackBySession_table[0][240] = "Game13_T1orT2?";

		nbackBySession_table[0][241] = "Game14_HitRate";
		nbackBySession_table[0][242] = "Game14_ErrRate";
		nbackBySession_table[0][243] = "Game14_RTAverage";
		nbackBySession_table[0][244] = "Game14_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][245] = "Game14_Type";
		nbackBySession_table[0][246] = "Game14_TotalHits";
		nbackBySession_table[0][247] = "Game14_TotalErrors";
		nbackBySession_table[0][248] = "Game14_TotalIncorrectErrors";
		nbackBySession_table[0][249] = "Game14_TotalOmissionErrors";
		nbackBySession_table[0][250] = "Game14_Complete?";
		nbackBySession_table[0][251] = "Game14_T1orT2?";

		nbackBySession_table[0][252] = "Game15_HitRate";
		nbackBySession_table[0][253] = "Game15_ErrRate";
		nbackBySession_table[0][254] = "Game15_RTAverage";
		nbackBySession_table[0][255] = "Game15_TimeSpent"; //Either seconds or unfinished
		nbackBySession_table[0][256] = "Game15_Type";
		nbackBySession_table[0][257] = "Game15_TotalHits";
		nbackBySession_table[0][258] = "Game15_TotalErrors";
		nbackBySession_table[0][259] = "Game15_TotalIncorrectErrors";
		nbackBySession_table[0][260] = "Game15_TotalOmissionErrors";
		nbackBySession_table[0][261] = "Game15_Complete?";
		nbackBySession_table[0][262] = "Game15_T1orT2?";
		
		nbackBySession_table[0][263] = "AutoHR";
		nbackBySession_table[0][264] = "AutoER";
		
		nbackBySession_table[0][265] = "ErrRateIncorrect_AvgOverall";
		nbackBySession_table[0][266] = "ErrRateOmission_AvgOverall";
		nbackBySession_table[0][267] = "T1_ErrRateIncorrect_AvgOverall";
		nbackBySession_table[0][268] = "T1_ErrRateIncorrect_AvgSpatial";
		nbackBySession_table[0][269] = "T1_ErrRateIncorrect_AvgLetterNumber";
		nbackBySession_table[0][270] = "T1_ErrRateIncorrect_AvgImage";
		nbackBySession_table[0][271] = "T1_ErrRateOmission_AvgOverall";
		nbackBySession_table[0][272] = "T1_ErrRateOmission_AvgSpatial";
		nbackBySession_table[0][273] = "T1_ErrRateOmission_AvgLetterNumber";
		nbackBySession_table[0][274] = "T1_ErrRateOmission_AvgImage";
		nbackBySession_table[0][275] = "T2_ErrRateIncorrect_AvgOverall";
		nbackBySession_table[0][276] = "T2_ErrRateIncorrect_AvgSpatial";
		nbackBySession_table[0][277] = "T2_ErrRateIncorrect_AvgLetterNumber";
		nbackBySession_table[0][278] = "T2_ErrRateIncorrect_AvgImage";
		nbackBySession_table[0][279] = "T2_ErrRateOmission_AvgOverall";
		nbackBySession_table[0][280] = "T2_ErrRateOmission_AvgSpatial";
		nbackBySession_table[0][281] = "T2_ErrRateOmission_AvgLetterNumber";
		nbackBySession_table[0][282] = "T2_ErrRateOmission_AvgImage";
		nbackBySession_table[0][283] = "Game1_ErrRateIncorrect";
		nbackBySession_table[0][284] = "Game1_ErrRateOmission";
		nbackBySession_table[0][285] = "Game2_ErrRateIncorrect";
		nbackBySession_table[0][286] = "Game2_ErrRateOmission";
		nbackBySession_table[0][287] = "Game3_ErrRateIncorrect";
		nbackBySession_table[0][288] = "Game3_ErrRateOmission";
		nbackBySession_table[0][289] = "Game4_ErrRateIncorrect";
		nbackBySession_table[0][290] = "Game4_ErrRateOmission";
		nbackBySession_table[0][291] = "Game5_ErrRateIncorrect";
		nbackBySession_table[0][292] = "Game5_ErrRateOmission";
		nbackBySession_table[0][293] = "Game6_ErrRateIncorrect";
		nbackBySession_table[0][294] = "Game6_ErrRateOmission";
		nbackBySession_table[0][295] = "Game7_ErrRateIncorrect";
		nbackBySession_table[0][296] = "Game7_ErrRateOmission";
		nbackBySession_table[0][297] = "Game8_ErrRateIncorrect";
		nbackBySession_table[0][298] = "Game8_ErrRateOmission";
		nbackBySession_table[0][299] = "Game9_ErrRateIncorrect";
		nbackBySession_table[0][300] = "Game9_ErrRateOmission";
		nbackBySession_table[0][301] = "Game10_ErrRateIncorrect";
		nbackBySession_table[0][302] = "Game10_ErrRateOmission";
		nbackBySession_table[0][303] = "Game11_ErrRateIncorrect";
		nbackBySession_table[0][304] = "Game11_ErrRateOmission";
		nbackBySession_table[0][305] = "Game12_ErrRateIncorrect";
		nbackBySession_table[0][306] = "Game12_ErrRateOmission";
		nbackBySession_table[0][307] = "Game13_ErrRateIncorrect";
		nbackBySession_table[0][308] = "Game13_ErrRateOmission";

		
		
		return nbackBySession_table;
	}
	Double[] getResponseRate(String fileName) throws Exception //changed from Double to Double[]
	{
		FileInputStream fs= new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line = br.readLine();
		String prevLine = line;
		double errOmission = 0;
		double errIncorrect = 0;
		while(line!=null){
			if(prevLine.contains("No Input Received"))
			{
				errOmission++;
			}
			else if(prevLine.contains("Wrong")) //Just wrong, will only happen if 1st evaluates to false
			{
				errIncorrect++;
			}
			prevLine = line;
			line = br.readLine();
		}				  
		br.close();
		Double rt = 0.0;
		if(prevLine.startsWith("The average response time "))
		{
			int index1 = prevLine.indexOf("inputs is ");
			index1+= 10; //length of above string
			int index2 = prevLine.indexOf("ms.");
			String rtAsString = prevLine.substring(index1, index2);
			//System.out.println(prevLine);
			if(!rtAsString.isEmpty())
			{
				rt = Double.parseDouble(rtAsString);
			}
		}
		
		//Create the return array
		Double[] results = {rt, errOmission, errIncorrect};
		return results;
		//return rt;
	}
	private void removeLeadingSpaces(File inputFolder) {
		File dir = new File("All_TDCS_Reports_updated/correctedReports");
		if(dir.exists())
		{
			return;
		}
		dir.mkdir();
		File[] files = inputFolder.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
		for (int i = 0; i < files.length; i++) 
		{
			if(!files[i].isDirectory()){
				String filePath = files[i].getPath();
				String fileName = files[i].getName();
				
				try {
					FileOutputStream os= new FileOutputStream("All_TDCS_Reports_updated/correctedReports/"+fileName);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
					
					FileInputStream fs= new FileInputStream(filePath);
					BufferedReader br = new BufferedReader(new InputStreamReader(fs));
					String line = br.readLine();
					line = line.trim();
					bw.write(line);
					bw.newLine();
					line = br.readLine();
					while(line!=null){
						bw.write(line);
						bw.newLine();
						line = br.readLine();
					}				  
					br.close();
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}	
}