//The CTB sheet must be the 1st one in the file
//The FLY sheet must be the 2nd one in the file
//The WCN sheet must be the 3rd one in the file
//The SWM sheet must be the 4th one in the file
//The sessionDate must be the 5th one in the file


/*import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
*/

import java.util.Iterator;
//import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; //Added A.P
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class main {

	public static void main(String[] args) throws Exception
	{
		
		//A limit of 100,000 rows in each raw data sheet was established
		//A limit of 50 columns in each raw data sheet was established
		
		//Working with excel (Reading data)
		
//		String[][] avgsReport_table = new String[100000][50];
//		String[][] loginReport_table = new String[100000][50];
//		String[][] sudokuReport_table = new String[100000][50];
//		String[][] nbackReport_table = new String[100000][50]; //Added these 4 rows for tables taking in raw data A.P
		String[][] sessionInfoData_table = new String[100][100]; // For input session data

		
		String[][] sessionDate_table = new String[100][100];
		String[][] nbackByID_table = new String[100][500];
		String[][] nbackByDate_table = new String[100][100];
		String[][] sudokuByID_table = new String[100][100];
		String[][] sudokuByDate_table = new String[100][100]; //Added these rows for the 5 organized tables, mirrors "organized table" A.P
		
//		String[][] organizedTable = new String[100000][50]; //Table that receives the data sorted by user ID
//		String[][] organizedTable2 = new String[100000][50]; // Table that receives the data sorted by Date for each user ID
//		//boolean knownID[] = new boolean[100000]; // It just checks whether a desired operation has been applied to an ID
		
//		int avgsReport_row_num = 0;
//		int avgsReport_col_num = 0;
//		int loginReport_row_num = 0;
//		int loginReport_col_num = 0;
//		int sudokuReport_row_num = 0;
//		int sudokuReport_col_num = 0;
//		int nbackReport_row_num = 0;
//		int nbackReport_col_num = 0; // Added for raw data A.P
		int sessionInfoData_row_num = 0;
		int sessionInfoData_col_num = 0;
		
		int sessionDate_row_num = 0; // Total of rows in the SWM excel sheet
		int sessionDate_col_num = 0; // Total of columns in the SWM excel sheet
		String[][] sessionDate = new String[10000][50]; // Store the information from the sessionDate excel file
		String[][] timeOfDay = new String[10000][3]; //Store the preDate and postDate time of day
		
		//In case need inputed data		
		//String input = new String();		
		//Input data by user (Sheida)
		//Input data storage
		//InputStreamReader isr = new InputStreamReader(System.in);
		//BufferedReader br = new BufferedReader(isr);
		//input = br.readLine();
		//int number = 0;
		//number = Integer.parseInt(input);
		
				
		//======================================================================================================================//
		//======================================================================================================================//
			
		/*First things first: get the output files and the session data file. Outputs are all in a folder "allReports" (to be in the system 
		 * library where all resources for a program are stored)
		 */
		File[] files = null;
		File[] responses = null;
		try {
			//Start with output files; store them in an array files[]
			File inputFolder = new File("allReports"); //this will implements the single file algorithm below
	    	if(inputFolder.isDirectory()) //check if said file is a directory
	    	{
	    		//The following MUST OCCUR:
	    		files = inputFolder.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
	    		for (int i = 0; i < files.length; i++) 
	    		{
	    			System.out.println(files[i].getName()); //get name of each file in the directory; print out
	    			//NEED TO REMOVE FIRST X SPACES IN EACH FILE
				}
	    	}
	    	
	    	//Get the text files containing data with response times
	    	
	    	File nbkResponses = new File("NbkResponses");
	    	
	    	if(nbkResponses.isDirectory()) //check if said file is a directory
	    	{
	    		//The following MUST OCCUR:
	    		responses = nbkResponses.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
	    		for (int i = 0; i < files.length; i++) 
	    		{
	    			System.out.println(responses[i].getName()); //get name of each file in the directory; print out
	    			//NEED TO REMOVE FIRST X SPACES IN EACH FILE
				}
	    	}
	    	
//	    	File inputFile = new File("Report_ID_11031301114.xml");
//	         DocumentBuilderFactory dbFactory 
//	            = DocumentBuilderFactory.newInstance();
//	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//	         Document doc = dBuilder.parse(inputFile);
//	         doc.getDocumentElement().normalize();
//	         NodeList rowList = doc.getElementsByTagName("Row");
//	         for (int temp = 0; temp < rowList.getLength(); temp++) {
//	            Element nNode = (Element) rowList.item(temp);
//	            NodeList cellList = nNode.getElementsByTagName("Cell");
//	            System.out.println("Row "+temp);
//	            StringBuffer content = new StringBuffer();
//	            for (int j = 0; j < cellList.getLength(); j++) {
//	            	Node abc = cellList.item(j);
//	            	String text = ((Element)abc.getChildNodes()).getTextContent();
//	            	content.append(text+",");
//
//	            }
//	        	System.out.println("Row "+temp + "="+content);
//	         }
	    	
	    	//Next, get the sessionDates as a .xlsx file; put everything into a 2D array "sessionInfoData_table"
	    	File a = new File("NICT_Sessiondatesjan27162016.xlsx");
	    	FileInputStream inputStream = new FileInputStream(a);
	    	Workbook sessionDatesInput = new XSSFWorkbook(inputStream);
	    	Sheet sessionInfoData = sessionDatesInput.getSheetAt(0);
	    	for(Iterator<Row> rit = sessionInfoData.rowIterator(); rit.hasNext();)
	    	{
	    		Row row = rit.next();
	    		for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
	    		{
	    			Cell cell = cit.next(); //Reads the cells in the file A.P
	    			cell.setCellType(Cell.CELL_TYPE_STRING); //reads each column as a string, and later, if it's a number, can convert to a number
	    			sessionInfoData_table[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
	    		}
	    		sessionInfoData_col_num = row.getPhysicalNumberOfCells();
	    	}
	    	sessionInfoData_row_num = sessionInfoData.getPhysicalNumberOfRows();
	    	sessionDatesInput.close();
	    	
	    	//Finished reading in all inputs
	    	
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	    
		//Now, initialize all sheets
	    Workbook outputFile = new XSSFWorkbook(); // Empty Excel output file is initialized
		
	   	//Create the sessionDates sheet and have all data stored
	   	sessionDate_table = sessionDateSheetInit(sessionInfoData_table,sessionInfoData_row_num, sessionInfoData_col_num, sessionDate_table, outputFile); //Adds to the sheet and creates the 2d array to use for other methods
	
    	//Initialize all of the following tables with titles
	    nbackByID_table = nbackByIDSheetInit(nbackByID_table);
	   	nbackByDate_table = nbackByDateSheetInit(nbackByDate_table);
	    sudokuByID_table = sudokuByIDSheetInit(sudokuByID_table);
	    sudokuByDate_table = sudokuByDateSheetInit(sudokuByDate_table);
		
	    //Now, for the ID sheets, call nbackByIDSheet and sudokuByIDSheet with outputs of 2D arrays; start inserting rows into their respective tables
	    if(files!=null)
	    {
	    	for(int i = 0; i<files.length;i++)//For each user's file
	    	{
	    		
	    		nbackByIDSheet(files[i],responses,sessionDate_table,nbackByID_table,i+1); //Adds a new row to nbackByID at line i+1; NEEDS ARRAY OF TEXT FILES A.P
	    		sudokuByIDSheet(files[i],sessionDate_table,sudokuByID_table,i+1); //Adds a new row to sudokuByID
	    	}
	    	
	    }
	    
	    
	    //Get the date and time  	
	    DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");	
	    Date date = new Date();
	    String fileDate = dateFormat.format(date);
	    System.out.println(fileDate); //Print out today's date
		
	    //Now, take all the completed 2D arrays and put them into sheets
	    //First, SessionDate
	    Sheet sessionDate_sheet = outputFile.createSheet("SessionDate");
		Cell cell_w;
		for(int r = 0 ; r < sessionInfoData_row_num ; r++) //make x rows where x is the total number of rows of users + title row
		{
			Row row = sessionDate_sheet.createRow(r);
			for(int c = 0 ; c < 14 ; c++) //There are 14 columns
			{
				/*if(r == 0||c == 11) //If it's either the titles row or the examiner column, don't turn the cell type into a number CHECK A.P
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(sessionDate_table[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					if(sessionDate_table[r][c].equals("")) //If the cell is empty
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue("");
					}	
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(sessionDate_table[r][c].toString()));
					}
				}*/
				cell_w = row.createCell(c);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(sessionDate_table[r][c].toString());
			}
		}
		
		//Next, nbackByID
		Sheet nbackByID_sheet = outputFile.createSheet("nbackByID");
		
		cell_w = null;
		
		for(int r = 0 ; r < files.length+1 ; r++)//+1 b/c one row for the titles
		{
			Row row = nbackByID_sheet.createRow(r);
			for(int c = 0 ; c < 128 ; c++) //# of columns
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
				cell_w.setCellValue(nbackByID_table[r][c].toString());
			}
		}
		
		//Next, nbackByDate
		Sheet nbackByDate_sheet = outputFile.createSheet("nbackByDate");
		
		cell_w = null;
		
		for(int r = 0 ; r < nbackByDate_table.length ; r++)
		{
			Row row = nbackByDate_sheet.createRow(r);
			for(int c = 0 ; c < 52 ; c++)
			{
				/*if(r == 0)
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(nbackByDate_table[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					if(nbackByDate_table[r][c].equals(""))
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue("");
					}	
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(nbackByDate_table[r][c].toString()));
					}
				}*/
				
			}
		}
		
		//Next, SudokuByID
		Sheet sudokuByID_sheet = outputFile.createSheet("sudokuByID");
		
		cell_w = null;
		
		for(int r = 0 ; r < files.length+1 ; r++)//num rows + 1 for the title row
		{
			Row row = sudokuByID_sheet.createRow(r);
			for(int c = 0 ; c < 52 ; c++)
			{
				/*if(r == 0)
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(sudokuByID_table[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					if(sudokuByID_table[r][c].equals(""))
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue("");
					}	
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(sudokuByID_table[r][c].toString()));
					}
				}*/
				cell_w = row.createCell(c);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(sudokuByID_table[r][c].toString());
			}
		}
		
		//Next, SudokuByID
			Sheet sudokuByDate_sheet = outputFile.createSheet("sudokuByDate");
				
			cell_w = null;
				
				for(int r = 0 ; r < sudokuByDate_table.length ; r++)
				{
					Row row = sudokuByDate_sheet.createRow(r);
					for(int c = 0 ; c < 52 ; c++)
					{
						if(r == 0)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue(sudokuByDate_table[r][c].toString());
						}
						else
						{
							cell_w = row.createCell(c);
							if(sudokuByDate_table[r][c].equals(""))
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}	
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(Double.parseDouble(sudokuByDate_table[r][c].toString()));
							}
						}
					}
				}
	    
	    //Output the completed excel file!
	    try{	
	    	FileOutputStream output = new FileOutputStream("C:/Users/Anita/Documents/School/uOttawa/FilteredData_"+fileDate+".xlsx"); //Changed directory to fit comp A.P
	    	outputFile.write(output);
	    	output.close();
	    	outputFile.close();
	    }
	    catch (Exception e)
		{
			e.printStackTrace();
		}
//		try
//			{
//				//Open an excel file
//				Workbook workbook = new XSSFWorkbook(new FileInputStream(fileChooser.getSelectedFile())); //HSSF to XSSF A.P
//				
//				//The Following reads in data from the avgsReport sheet A.P
//				Sheet avgsReport = workbook.getSheetAt(0);
//				for(Iterator<Row> rit = avgsReport.rowIterator(); rit.hasNext();)
//				{
//					Row row = rit.next();
//					for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
//					{
//						Cell cell = cit.next(); //Reads the cells in the file A.P
//						cell.setCellType(Cell.CELL_TYPE_STRING); //reads each column as a string, and later, if it's a number, can convert to a number
//						//System.out.print(cell.getStringCellValue()+"\t\t");
//						avgsReport_table[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
//					}
//					//System.out.println();
//					avgsReport_col_num = row.getPhysicalNumberOfCells();
//				}
//				avgsReport_row_num = avgsReport.getPhysicalNumberOfRows();
//				
//				//The Following reads in data from the loginReport sheet A.P
//				Sheet loginReport = workbook.getSheetAt(1);
//				for(Iterator<Row> rit = loginReport.rowIterator(); rit.hasNext();)
//				{
//					Row row = rit.next();
//					for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
//					{
//						Cell cell = cit.next(); //Reads the cells in the file A.P
//						cell.setCellType(Cell.CELL_TYPE_STRING); //reads each column as a string, and later, if it's a number, can convert to a number
//						loginReport_table[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
//					}
//					loginReport_col_num = row.getPhysicalNumberOfCells();
//				}
//				loginReport_row_num = loginReport.getPhysicalNumberOfRows();
//				
//				//The Following reads in data from the sudokuReport sheet A.P
//				Sheet sudokuReport = workbook.getSheetAt(2);
//				for(Iterator<Row> rit = sudokuReport.rowIterator(); rit.hasNext();)
//				{
//					Row row = rit.next();
//					for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
//					{
//						Cell cell = cit.next(); //Reads the cells in the file A.P
//						cell.setCellType(Cell.CELL_TYPE_STRING); //reads each column as a string, and later, if it's a number, can convert to a number
//						sudokuReport_table[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
//					}
//					sudokuReport_col_num = row.getPhysicalNumberOfCells();
//				}
//				sudokuReport_row_num = sudokuReport.getPhysicalNumberOfRows();
//				
//				//The Following reads in data from the nbackReport sheet A.P
//				Sheet nbackReport = workbook.getSheetAt(3);
//				for(Iterator<Row> rit = nbackReport.rowIterator(); rit.hasNext();)
//				{
//					Row row = rit.next();
//					for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
//					{
//						Cell cell = cit.next(); //Reads the cells in the file A.P
//						cell.setCellType(Cell.CELL_TYPE_STRING); //reads each column as a string, and later, if it's a number, can convert to a number
//						nbackReport_table[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
//					}
//					nbackReport_col_num = row.getPhysicalNumberOfCells();
//				}
//				nbackReport_row_num = nbackReport.getPhysicalNumberOfRows();
//				
//				workbook.close(); //end of reading from single output file A.P
//				
//			} catch (FileNotFoundException x){
//				x.printStackTrace();
//			} catch (IOException x) {
//				x.printStackTrace();
//			}
//		else
//		{
//			System.out.println("No file has been chosen.");
//		}	
//			//Finished copying data
		
		
		//======================================================================================================================//
		//======================================================================================================================//
		
		
		//======================================================================================================================//
		//======================================================================================================================//
		
		//General Operation
		//Convert sessionDate from string to numbers
		double [][] sessionDate_asNum = new double[10000][15];
		for(int row = 1 ; row < sessionDate_row_num ; row++)
		{
			for( int col = 1 ; col < (sessionDate_col_num-3) ; col++)
			{
				if(!(sessionDate[row][col].equals("NULL")))
				{					
					sessionDate_asNum[row][col] = (Double.parseDouble(sessionDate[row][col].toString()));
				}
				else
				{
					if( col == 6)
					{
						System.out.println("The user " + sessionDate[row][0].toString() + " doesn't have a last lab session.");
						sessionDate_asNum[row][col] = sessionDate_asNum[row][col-1] + 7; //If sessionDate is NULL then add 7 day since last lab
					}
					else
					{
						System.out.println("The user " + sessionDate[row][0].toString() + " doesn't have the week" + (col-1) + " lab session.");
						sessionDate_asNum[row][col] = sessionDate_asNum[row][col-1] + 7; //If sessionDate is NULL then add 7 day since last lab
					}
				}
			}
		}		
		//Finished converting	
	}//main()
	
	private static Date createDate(String date) //Date must be of format YYYY-MM-DD
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date x;
		try {
			x = sdf.parse(date);
			return x;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static int getDaysDiff(Date from, Date to)
	{
		long diff = to.getTime() - from.getTime();
		long y = (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
	    //System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
		int x = (int)y; //convert to integer
		return x;
	}
	
	private static void nbackByIDSheet(File file, File[] rr, String[][] sessionDate_table, String[][] nbackByID, int index) throws ParserConfigurationException, SAXException, IOException {
		//index represents the row that should be filled in of nbackByID; index starts at 0, and this should be filling in from second row (index 0)
		try{
		//First, get the ID of the user that this file pertains to. The file name is of the format "Report_ID_###########.xml"
			//may have to check; the length of the ID may be either 10 or 11 digits
		String id_string = file.getName();
		id_string = id_string.substring(9,21); //Yay, this goes in the first spot of the row!
		
		int id = Integer.parseInt(id_string); //Use ID as a referring integer now.
		
		//look for corresponding row in sessionDate_table; initialize a variable to know what row we're going to stay on to get data
		int sessionDate_keyRow = 0;
		for(int i = 1; i<sessionDate_table.length;i++) //Start from row 1 of sessionDate_table
		{
			if(Integer.parseInt(sessionDate_table[i][0])==id) //if it finds the same
			{
				sessionDate_keyRow = i;
				break;
			}
		}
		
		//Initialize the data needed from sessionDates, converting to integers where needed. NOTE: haven't checked if empty fields. Need to handle dates.
		String group = sessionDate_table[sessionDate_keyRow][1]; //col 1 of desired table
		int round = Integer.parseInt(sessionDate_table[sessionDate_keyRow][2]); //col 2 of desired table DONE
		int age = Integer.parseInt(sessionDate_table[sessionDate_keyRow][3]); //col 3 of desired table DONE
		
		Date first_lab_day; //First login date; col 4 needs to be calculated; 
		Date last_day; //last login date; col 5 needs to be calculated
		
		String pre_test_dayString = sessionDate_table[sessionDate_keyRow][4];
		Date pre_test_day = createDate(pre_test_dayString); // same format as in sessionDate; col 6 DONE
		String post_test_dayString = sessionDate_table[sessionDate_keyRow][10];
		Date post_test_day = createDate(post_test_dayString); // same format as in sessionDate; col 7 DONE
		
		int days_elapsed; //first_lab_day to last_day; col 8 needs to be calculated
		int last_day_to_post_day; // last day to post day; col 9 needs to be calculated
		int pre_test_to_post_test = getDaysDiff(post_test_day,pre_test_day); //col 10 DONE
		int pre_test_to_first_lab_day; //col 11 needs to be calculated
		
		//This data determines the dates that were actual lab days
		Date lab1 = createDate(sessionDate_table[sessionDate_keyRow][5]);
		Date lab2 = createDate(sessionDate_table[sessionDate_keyRow][6]); 
		Date lab3 = createDate(sessionDate_table[sessionDate_keyRow][7]);
		Date lab4 = createDate(sessionDate_table[sessionDate_keyRow][8]); 
		Date lab5 = createDate(sessionDate_table[sessionDate_keyRow][9]); 
	
		
//		String examiner = sessionDate_table[sessionDate_keyRow][11];
//		int pre_date_time = Integer.parseInt(sessionDate_table[sessionDate_keyRow][12]);
//		int post_date_time = Integer.parseInt(sessionDate_table[sessionDate_keyRow][13]);
		//Above 3 not needed for this table

		//Done with data needed from sessionDates
		
		//Now, start reading data from the actual file
		
		String[][] raw_table = new String[1000][20]; //will take in ALL the data from the file; all sheets combined into one, per row
		int sheet1Start = 0;
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
        		String nextString = ((Element)def.getChildNodes()).getTextContent();

        		if(text.equals("Date")) //If second sheet 
        		{
        			sheet2Start = temp;
        		}
        		else if(text.equals("Finished")&&sheet3Start==0&&nextString.equals("Game ID"))//If third sheet (sheet3Start not initialized)
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
        
        //load into separate tables
        String[][] avgs_report = new String[sheet2Start][20]; //max 20 columns
        for(int i = 0; i<sheet2Start;i++)
        {
        	for(int j = 0;j<20;j++)
        	{
        		avgs_report[i][j] = raw_table[i][j];
        	}
        }
        
        String[][] login_report = new String[sheet3Start - sheet2Start][20];
        for(int i = 0; i<login_report.length;i++)
        {
        	for(int j = 0;j<login_report[i].length;j++)
        	{
        		login_report[i][j] = raw_table[i+sheet2Start][j];
        	}
        }
        
        String[][] sudoku_report = new String[sheet4Start - sheet3Start][20];
        for(int i = 0; i<sudoku_report.length;i++)
        {
        	for(int j = 0;j<sudoku_report[i].length;j++)
        	{
        		sudoku_report[i][j] = raw_table[i+sheet3Start][j];
        	}
        }
        
        String[][] nback_report = new String[raw_table.length - sheet3Start][20];
        for(int i = 0; i<nback_report.length;i++)
        {
        	for(int j = 0;j<nback_report[i].length;j++)
        	{
        		nback_report[i][j] = raw_table[i+sheet3Start][j];
        	}
        }
        //The number of rows for all of the above should be accurate to the total number of rows of data that were filled in
        
        //Done reading everything into tables
        
        //Start to manipulate the data points; dealing with nback
        //PART 1: Get dates from the login_report array  
        
        String first_lab_dayString = login_report[1][0];
        first_lab_day = createDate(first_lab_dayString); //First entry in the login report; col 4 DONE
        String last_dayString = login_report[login_report.length-1][0];
        last_day = createDate(last_dayString); //Last entry in the login report; col 5 DONE
        
        days_elapsed = getDaysDiff(first_lab_day,last_day); //col 8 DONE
        last_day_to_post_day = getDaysDiff(last_day,post_test_day); //col 9 DONE
        pre_test_to_first_lab_day = getDaysDiff(pre_test_day,first_lab_day); //col 11 DONE
        
        //PART 2: Get definitions of weeks
        Calendar c = Calendar.getInstance();
        
        Date wk1Start = lab1;
        
        c.setTime(wk1Start);
        c.add(Calendar.DATE, 7); // Adding 7 days
        Date wk2Start = c.getTime();
        
        c.setTime(wk2Start);
        c.add(Calendar.DATE, 7); // Adding 7 days
        Date wk3Start = c.getTime();
        
        c.setTime(wk3Start);
        c.add(Calendar.DATE, 7); // Adding 7 days
        Date wk4Start = c.getTime();
        
        c.setTime(wk4Start);
        c.add(Calendar.DATE, 7); // Adding 7 days
        Date wk5Start = c.getTime();
        
        //Also do one for the end boundary date
        c.setTime(wk5Start);
        c.add(Calendar.DATE, 6); // Adding 6 days b/c LAST day of wk
        Date wk5End = c.getTime();
        
        //Finished determining "week" boundaries
        
        //PART 3: from login_report, get all login dates, then use counters to count how many logins were in each week
        
        int totalSessionsWk1 = 0;
        int totalSessionsWk2 = 0;
        int totalSessionsWk3 = 0;
        int totalSessionsWk4 = 0;
        int totalSessionsWk5 = 0;

        
        for(int i = 1; i<login_report.length;i++)
        {
        	Date d = createDate(login_report[i][0]);
        	
        	if(d.equals(wk1Start)||d.before(wk2Start))
        	{
        		totalSessionsWk1++;
        	}
        	else if(d.equals(wk2Start)||d.before(wk3Start))
        	{
        		totalSessionsWk2++;
        	}
        	else if(d.equals(wk3Start)||d.before(wk4Start))
        	{
        		totalSessionsWk3++;
        	}
        	else if(d.equals(wk4Start)||d.before(wk5Start))
        	{
        		totalSessionsWk4++;
        	}
        	else if(d.equals(wk5Start)||!d.after(wk5End))
        	{
        		totalSessionsWk5++;
        	}
        }
        
        //Each 2 logins is a session, so divide by 2; if an odd number of logins the data will truncate.
        //SEE HALF NUMBERS; will need to convert to double A.P
        totalSessionsWk1 = totalSessionsWk1/2; //col 152 DONE
        totalSessionsWk2 = totalSessionsWk2/2; //col 153 DONE
        totalSessionsWk3 = totalSessionsWk3/2; //col 154 DONE
        totalSessionsWk4 = totalSessionsWk4/2; //col 155 DONE
        totalSessionsWk5 = totalSessionsWk5/2; //col 156 DONE

        //What's left: hr avgs, er avgs, number of games 

        
        //Columns 12-26 (HRAVGnumberb), 32-46 (ERAVGnumberb), 47-61, 62-66, 67-81 (NumGamesnumberb), 82-96, 97-101 (HRAVG in each lab), 102-106 (time in lab),
        //107-111 (numGames at home per week), 112-116 (avg time per game lab), 117-131 (avgtimenumberb),132-146,147-151, 152-156 (totalSessions)
        //Ones with numberb are left blank b/c no way of knowing
        
        //We need to find:
        //Game data per week: from nback report, IF GAME COMPLETE::: # of correct submissions, # of wrong submissions, duration in seconds, type of game
        //For each game, calculate HR, ER
        //sort games into weeks
        //sort further into types of game (spatial img numberLetter)
        //for each group of data calculate avg hr, avg er, avg duration; get number of games in that week
        
        int[] gameIndex = new int[nback_report.length];
        
        double totalHRSpatialwk1 = 0.0; //total HR to be divided by number of games
        double totalERSpatialwk1 = 0.0; //total ER to be divided by number of games
        int totalGamesSpatialwk1 = 0; //totals games of this type during this week
        int timeSpatialwk1 = 0; //Time spent during this week on this type; must be completed time; int b/c it's in seconds
        double totalRTSpatialwk1 = 0.0; //Total response rate; all values to be read from corresponding game nback_responses page.
        double avgHRSpatialwk1, avgERSpatialwk1;
        double avgRTSpatialwk1;
        
        double totalHRSpatialwk2 = 0.0;
        double totalERSpatialwk2 = 0;
        int totalGamesSpatialwk2 = 0;
        int timeSpatialwk2 = 0;
        double totalRTSpatialwk2 = 0.0;
        double avgHRSpatialwk2, avgERSpatialwk2;
        double avgRTSpatialwk2;
        
        double totalHRSpatialwk3 = 0;
        double totalERSpatialwk3 = 0;
        int totalGamesSpatialwk3 = 0;
        int timeSpatialwk3 = 0;
        double totalRTSpatialwk3 = 0.0;
        double avgHRSpatialwk3, avgERSpatialwk3;
        double avgRTSpatialwk3;
        
        double totalHRSpatialwk4 = 0;
        double totalERSpatialwk4 = 0;
        int totalGamesSpatialwk4 = 0;
        int timeSpatialwk4 = 0;
        double totalRTSpatialwk4 = 0.0;
        double avgHRSpatialwk4, avgERSpatialwk4;
        double avgRTSpatialwk4;
        
        double totalHRSpatialwk5 = 0;
        double totalERSpatialwk5 = 0;
        int totalGamesSpatialwk5 = 0;
        int timeSpatialwk5 = 0;
        double totalRTSpatialwk5 = 0.0;
        double avgHRSpatialwk5, avgERSpatialwk5;
        double avgRTSpatialwk5;
        
        double totalHRLetNumwk1 = 0;
        double totalERLetNumwk1 = 0;
        int totalGamesLetNumwk1 = 0;
        int timeLetNumwk1 = 0;
        double totalRTLetNumwk1 = 0.0;
        double avgHRLetNumwk1, avgERLetNumwk1;
        double avgRTLetNumwk1;
        
        double totalHRLetNumwk2 = 0;
        double totalERLetNumwk2 = 0;
        int totalGamesLetNumwk2 = 0;
        int timeLetNumwk2 = 0;
        double totalRTLetNumwk2 = 0.0;
        double avgHRLetNumwk2, avgERLetNumwk2;
        double avgRTLetNumwk2;
        
        double totalHRLetNumwk3 = 0;
        double totalERLetNumwk3 = 0;
        int totalGamesLetNumwk3 = 0;
        int timeLetNumwk3 = 0;
        double totalRTLetNumwk3 = 0.0;
        double avgHRLetNumwk3, avgERLetNumwk3;
        double avgRTLetNumwk3;
        
        double totalHRLetNumwk4 = 0;
        double totalERLetNumwk4 = 0;
        int totalGamesLetNumwk4 = 0;
        int timeLetNumwk4 = 0;
        double totalRTLetNumwk4 = 0.0;
        double avgHRLetNumwk4, avgERLetNumwk4;
        double avgRTLetNumwk4;
        
        double totalHRLetNumwk5 = 0;
        double totalERLetNumwk5 = 0;
        int totalGamesLetNumwk5 = 0;
        int timeLetNumwk5 = 0;
        double totalRTLetNumwk5 = 0.0;
        double avgHRLetNumwk5, avgERLetNumwk5;
        double avgRTLetNumwk5;
        
        double totalHRImgwk1 = 0;
        double totalERImgwk1 = 0;
        int totalGamesImgwk1 = 0;
        int timeImgwk1 = 0;
        double totalRTImgwk1 = 0.0;
        double avgHRImgwk1, avgERImgwk1;
        double avgRTImgwk1;
        
        double totalHRImgwk2 = 0;
        double totalERImgwk2 = 0;
        int totalGamesImgwk2 = 0;
        int timeImgwk2 = 0;
        double totalRTImgwk2 = 0.0;
        double avgHRImgwk2, avgERImgwk2;
        double avgRTImgwk2;
        
        double totalHRImgwk3 = 0;
        double totalERImgwk3 = 0;
        int totalGamesImgwk3 = 0;
        int timeImgwk3 = 0;
        double totalRTImgwk3 = 0.0;
        double avgHRImgwk3, avgERImgwk3;
        double avgRTImgwk3;
        
        double totalHRImgwk4 = 0;
        double totalERImgwk4 = 0;
        int totalGamesImgwk4 = 0;
        int timeImgwk4 = 0;
        double totalRTImgwk4 = 0.0;
        double avgHRImgwk4, avgERImgwk4;
        double avgRTImgwk4;
        
        double totalHRImgwk5 = 0;
        double totalERImgwk5 = 0;
        int totalGamesImgwk5 = 0;
        int timeImgwk5 = 0;
        double totalRTImgwk5 = 0.0;
        double avgHRImgwk5, avgERImgwk5;
        double avgRTImgwk5;
        
        //Totals per week
        double totalHRwk1 = 0;
        double totalERwk1 = 0;
        int totalGameswk1 = 0;
        int timewk1 = 0;
        double totalRTwk1 = 0.0;
        double avgHRwk1, avgERwk1;
        double avgRTwk1;
        
        double totalHRwk2 = 0;
        double totalERwk2 = 0;
        int totalGameswk2 = 0;
        int timewk2 = 0;
        double totalRTwk2 = 0.0;
        double avgHRwk2, avgERwk2;
        double avgRTwk2;
        
        double totalHRwk3 = 0;
        double totalERwk3 = 0;
        int totalGameswk3 = 0;
        int timewk3 = 0;
        double totalRTwk3 = 0.0;
        double avgHRwk3, avgERwk3;
        double avgRTwk3;
        
        double totalHRwk4 = 0;
        double totalERwk4 = 0;
        int totalGameswk4 = 0;
        int timewk4 = 0;
        double totalRTwk4 = 0.0;
        double avgHRwk4, avgERwk4;
        double avgRTwk4;
        
        double totalHRwk5 = 0;
        double totalERwk5 = 0;
        int totalGameswk5 = 0;
        int timewk5 = 0;
        double totalRTwk5 = 0.0;
        double avgHRwk5, avgERwk5;
        double avgRTwk5;
        
        double totalHRSpatial = 0;
        double totalERSpatial = 0;
        int totalGamesSpatial = 0;
        int timeSpatial = 0;
        double totalRTSpatial = 0.0;
        double avgHRSpatial, avgERSpatial;
        double avgRTSpatial;
        
        double totalHRLetNum = 0;
        double totalERLetNum = 0;
        int totalGamesLetNum = 0;
        int timeLetNum = 0;
        double totalRTLetNum = 0.0;
        double avgHRLetNum, avgERLetNum;
        double avgRTLetNum;
        
        double totalHRImg = 0;
        double totalERImg = 0;
        int totalGamesImg = 0;
        int timeImg = 0;
        double totalRTImg = 0.0;
        double avgHRImg, avgERImg;
        double avgRTImg;
        //========================================================Labs now=======================================================================
 
        double totalHRSpatialLab1 = 0; //total HR to be divided by number of games
        double totalERSpatialLab1 = 0; //total ER to be divided by number of games
        int totalGamesSpatialLab1 = 0; //totals games of this type during this week
        int timeSpatialLab1 = 0; //Time spent during this week on this type; must be completed time.
        double totalRTSpatialLab1 = 0.0; //Total response rate; all values to be read from corresponding game nback_responses page.
        double avgHRSpatialLab1, avgERSpatialLab1; //total HR & ER divided by number of games
        double avgRTSpatialLab1;//total RT divided by number of games
        
        double totalHRSpatialLab2 = 0;
        double totalERSpatialLab2 = 0;
        int totalGamesSpatialLab2 = 0;
        int timeSpatialLab2 = 0;
        double totalRTSpatialLab2 = 0.0;
        double avgHRSpatialLab2, avgERSpatialLab2;
        double avgRTSpatialLab2;
        
        double totalHRSpatialLab3 = 0;
        double totalERSpatialLab3 = 0;
        int totalGamesSpatialLab3 = 0;
        int timeSpatialLab3 = 0;
        double totalRTSpatialLab3 = 0.0;
        double avgHRSpatialLab3, avgERSpatialLab3;
        double avgRTSpatialLab3;
        
        double totalHRSpatialLab4 = 0;
        double totalERSpatialLab4 = 0;
        int totalGamesSpatialLab4 = 0;
        int timeSpatialLab4 = 0;
        double totalRTSpatialLab4 = 0.0;
        double avgHRSpatialLab4, avgERSpatialLab4;
        double avgRTSpatialLab4;
        
        double totalHRSpatialLab5 = 0;
        double totalERSpatialLab5 = 0;
        int totalGamesSpatialLab5 = 0;
        int timeSpatialLab5 = 0;
        double totalRTSpatialLab5 = 0.0;
        double avgHRSpatialLab5, avgERSpatialLab5;
        double avgRTSpatialLab5;
        
        double totalHRLetNumLab1 = 0;
        double totalERLetNumLab1 = 0;
        int totalGamesLetNumLab1 = 0;
        int timeLetNumLab1 = 0;
        double totalRTLetNumLab1 = 0.0;
        double avgHRLetNumLab1, avgERLetNumLab1;
        double avgRTLetNumLab1;
        
        double totalHRLetNumLab2 = 0;
        double totalERLetNumLab2 = 0;
        int totalGamesLetNumLab2 = 0;
        int timeLetNumLab2 = 0;
        double totalRTLetNumLab2 = 0.0;
        double avgHRLetNumLab2, avgERLetNumLab2;
        double avgRTLetNumLab2;
        
        double totalHRLetNumLab3 = 0;
        double totalERLetNumLab3 = 0;
        int totalGamesLetNumLab3 = 0;
        int timeLetNumLab3 = 0;
        double totalRTLetNumLab3 = 0.0;
        double avgHRLetNumLab3, avgERLetNumLab3;
        double avgRTLetNumLab3;
        
        double totalHRLetNumLab4 = 0;
        double totalERLetNumLab4 = 0;
        int totalGamesLetNumLab4 = 0;
        int timeLetNumLab4 = 0;
        double totalRTLetNumLab4 = 0.0;
        double avgHRLetNumLab4, avgERLetNumLab4;
        double avgRTLetNumLab4;
        
        double totalHRLetNumLab5 = 0;
        double totalERLetNumLab5 = 0;
        int totalGamesLetNumLab5 = 0;
        int timeLetNumLab5 = 0;
        double totalRTLetNumLab5 = 0.0;
        double avgHRLetNumLab5, avgERLetNumLab5;
        double avgRTLetNumLab5;
        
        double totalHRImgLab1 = 0;
        double totalERImgLab1 = 0;
        int totalGamesImgLab1 = 0;
        int timeImgLab1 = 0;
        double totalRTImgLab1 = 0.0;
        double avgHRImgLab1, avgERImgLab1;
        double avgRTImgLab1;
        
        double totalHRImgLab2 = 0;
        double totalERImgLab2 = 0;
        int totalGamesImgLab2 = 0;
        int timeImgLab2 = 0;
        double totalRTImgLab2 = 0.0;
        double avgHRImgLab2, avgERImgLab2;
        double avgRTImgLab2;
        
        double totalHRImgLab3 = 0;
        double totalERImgLab3 = 0;
        int totalGamesImgLab3 = 0;
        int timeImgLab3 = 0;
        double totalRTImgLab3 = 0.0;
        double avgHRImgLab3, avgERImgLab3;
        double avgRTImgLab3;
        
        double totalHRImgLab4 = 0;
        double totalERImgLab4 = 0;
        int totalGamesImgLab4 = 0;
        int timeImgLab4 = 0;
        double totalRTImgLab4 = 0.0;
        double avgHRImgLab4, avgERImgLab4;
        double avgRTImgLab4;
        
        double totalHRImgLab5 = 0;
        double totalERImgLab5 = 0;
        int totalGamesImgLab5 = 0;
        int timeImgLab5 = 0;
        double totalRTImgLab5 = 0.0;
        double avgHRImgLab5, avgERImgLab5;
        double avgRTImgLab5;

        //total for each lab
        int totalGamesLab1 = 0;
        double totalHRLab1 = 0;
        double totalERLab1 = 0;
        int timeLab1 = 0;  //time spent on FULL nback games during this lab
        double totalRTLab1 = 0.0;
        double avgHRLab1, avgERLab1;
        double avgRTLab1;
        
        int totalGamesLab2 = 0;
        double totalHRLab2 = 0;
        double totalERLab2 = 0;
        int timeLab2 = 0;
        double totalRTLab2 = 0.0;
        double avgHRLab2, avgERLab2;
        double avgRTLab2;
        
        int totalGamesLab3 = 0;
        double totalHRLab3 = 0;
        double totalERLab3 = 0;
        int timeLab3 = 0;
        double totalRTLab3 = 0.0;
        double avgHRLab3, avgERLab3;
        double avgRTLab3;
        
        int totalGamesLab4 = 0;
        double totalHRLab4 = 0;
        double totalERLab4 = 0;
        int timeLab4 = 0;
        double totalRTLab4 = 0.0;
        double avgHRLab4, avgERLab4;
        double avgRTLab4;
        
        int totalGamesLab5 = 0;
        double totalHRLab5 = 0;
        double totalERLab5 = 0;
        int timeLab5 = 0;
        double totalRTLab5 = 0.0;
        double avgHRLab5, avgERLab5;
        double avgRTLab5;
        //========================================================Home now=======================================================================
        
        double totalHRSpatialwk1Home = 0; //total HR to be divided by number of games
        double totalERSpatialwk1Home = 0; //total ER to be divided by number of games
        int totalGamesSpatialwk1Home = 0; //totals games of this type during this week
        int timeSpatialwk1Home = 0; //Time spent during this week on this type; must be completed time.
        double totalRTSpatialwk1Home = 0.0; //Total response rate; all values to be read from corresponding game nback_responses page.
        double avgHRSpatialwk1Home, avgERSpatialwk1Home;
        double avgRTSpatialwk1Home;
        
        double totalHRSpatialwk2Home = 0;
        double totalERSpatialwk2Home = 0;
        int totalGamesSpatialwk2Home = 0;
        int timeSpatialwk2Home = 0;
        double totalRTSpatialwk2Home = 0.0;
        double avgHRSpatialwk2Home, avgERSpatialwk2Home;
        double avgRTSpatialwk2Home;
        
        double totalHRSpatialwk3Home = 0;
        double totalERSpatialwk3Home = 0;
        int totalGamesSpatialwk3Home = 0;
        int timeSpatialwk3Home = 0;
        double totalRTSpatialwk3Home = 0.0;
        double avgHRSpatialwk3Home, avgERSpatialwk3Home;
        double avgRTSpatialwk3Home;
        
        double totalHRSpatialwk4Home = 0;
        double totalERSpatialwk4Home = 0;
        int totalGamesSpatialwk4Home = 0;
        int timeSpatialwk4Home = 0;
        double totalRTSpatialwk4Home = 0.0;
        double avgHRSpatialwk4Home, avgERSpatialwk4Home;
        double avgRTSpatialwk4Home;
        
        double totalHRSpatialwk5Home = 0;
        double totalERSpatialwk5Home = 0;
        int totalGamesSpatialwk5Home = 0;
        int timeSpatialwk5Home = 0;
        double totalRTSpatialwk5Home = 0.0;
        double avgHRSpatialwk5Home, avgERSpatialwk5Home;
        double avgRTSpatialwk5Home;
        
        double totalHRLetNumwk1Home = 0;
        double totalERLetNumwk1Home = 0;
        int totalGamesLetNumwk1Home = 0;
        int timeLetNumwk1Home = 0;
        double totalRTLetNumwk1Home = 0.0;
        double avgHRLetNumwk1Home, avgERLetNumwk1Home;
        double avgRTLetNumwk1Home;
        
        double totalHRLetNumwk2Home = 0;
        double totalERLetNumwk2Home = 0;
        int totalGamesLetNumwk2Home = 0;
        int timeLetNumwk2Home = 0;
        double totalRTLetNumwk2Home = 0.0;
        double avgHRLetNumwk2Home, avgERLetNumwk2Home;
        double avgRTLetNumwk2Home;
        
        double totalHRLetNumwk3Home = 0;
        double totalERLetNumwk3Home = 0;
        int totalGamesLetNumwk3Home = 0;
        int timeLetNumwk3Home = 0;
        double totalRTLetNumwk3Home = 0.0;
        double avgHRLetNumwk3Home, avgERLetNumwk3Home;
        double avgRTLetNumwk3Home;
        
        double totalHRLetNumwk4Home = 0;
        double totalERLetNumwk4Home = 0;
        int totalGamesLetNumwk4Home = 0;
        int timeLetNumwk4Home = 0;
        double totalRTLetNumwk4Home = 0.0;
        double avgHRLetNumwk4Home, avgERLetNumwk4Home;
        double avgRTLetNumwk4Home;
        
        double totalHRLetNumwk5Home = 0;
        double totalERLetNumwk5Home = 0;
        int totalGamesLetNumwk5Home = 0;
        int timeLetNumwk5Home = 0;
        double totalRTLetNumwk5Home = 0.0;
        double avgHRLetNumwk5Home, avgERLetNumwk5Home;
        double avgRTLetNumwk5Home;
        
        double totalHRImgwk1Home = 0;
        double totalERImgwk1Home = 0;
        int totalGamesImgwk1Home = 0;
        int timeImgwk1Home = 0;
        double totalRTImgwk1Home = 0.0;
        double avgHRImgwk1Home, avgERImgwk1Home;
        double avgRTImgwk1Home;
        
        double totalHRImgwk2Home = 0;
        double totalERImgwk2Home = 0;
        int totalGamesImgwk2Home = 0;
        int timeImgwk2Home = 0;
        double totalRTImgwk2Home = 0.0;
        double avgHRImgwk2Home, avgERImgwk2Home;
        double avgRTImgwk2Home;
        
        double totalHRImgwk3Home = 0;
        double totalERImgwk3Home = 0;
        int totalGamesImgwk3Home = 0;
        int timeImgwk3Home = 0;
        double totalRTImgwk3Home = 0.0;
        double avgHRImgwk3Home, avgERImgwk3Home;
        double avgRTImgwk3Home;
        
        double totalHRImgwk4Home = 0;
        double totalERImgwk4Home = 0;
        int totalGamesImgwk4Home = 0;
        int timeImgwk4Home = 0;
        double totalRTImgwk4Home = 0.0;
        double avgHRImgwk4Home, avgERImgwk4Home;
        double avgRTImgwk4Home;
        
        double totalHRImgwk5Home = 0;
        double totalERImgwk5Home = 0;
        int totalGamesImgwk5Home = 0;
        int timeImgwk5Home = 0;
        double totalRTImgwk5Home = 0.0;
        double avgHRImgwk5Home, avgERImgwk5Home;
        double avgRTImgwk5Home;
        
        //Total for each week of home sessions
        int totalGameswk1Home = 0;
        double totalHRwk1Home = 0;
        double totalERwk1Home = 0;
        int timewk1Home = 0;  //time spent on FULL nback games during this lab
        double totalRTwk1Home = 0.0;
        double avgHRwk1Home, avgERwk1Home;
        double avgRTwk1Home;
        
        int totalGameswk2Home = 0;
        double totalHRwk2Home = 0;
        double totalERwk2Home = 0;
        int timewk2Home = 0;
        double totalRTwk2Home = 0.0;
        double avgHRwk2Home, avgERwk2Home;
        double avgRTwk2Home;
        
        int totalGameswk3Home = 0;
        double totalHRwk3Home = 0;
        double totalERwk3Home = 0;
        int timewk3Home = 0;
        double totalRTwk3Home = 0.0;
        double avgHRwk3Home, avgERwk3Home;
        double avgRTwk3Home;
        
        int totalGameswk4Home = 0;
        double totalHRwk4Home = 0;
        double totalERwk4Home = 0;
        int timewk4Home = 0;
        double totalRTwk4Home = 0.0;
        double avgHRwk4Home, avgERwk4Home;
        double avgRTwk4Home;
        
        int totalGameswk5Home = 0;
        double totalHRwk5Home = 0;
        double totalERwk5Home = 0;
        int timewk5Home = 0;
        double totalRTwk5Home = 0.0;
        double avgHRwk5Home, avgERwk5Home;
        double avgRTwk5Home;
                
        boolean hasDuration = false;
        
        for(int i = 0;i<nback_report.length;i++)
        {
        	if(!(nback_report[i][1].equals("")))//this row has a game b/c it has a game ID here
        	{
        		int gameID = Integer.parseInt(nback_report[i][1]);
        		int duration = 0;
        		double rt = 0;
        		boolean rtFound = false;
        		Date thisDate = createDate(nback_report[i][3]); //check if date is actually valid; if it's not then disregard (for now) A.P
        		String type = nback_report[i][7];
        		int hits = Integer.parseInt(nback_report[i][8]);
        		int errors = Integer.parseInt(nback_report[i][9]);
        		double hr = hits/(hits+errors);
        		double er = errors/(hits+errors);
        		if(!nback_report[i][2].equals("Unfinished"))
        		{
        			hasDuration = true;
        			duration = Integer.parseInt(nback_report[i][2]);
        		}
        		
        		//now for the response rate: cross-reference game ID with the nbackresponses folder
        		for (int j = 0; j < gameIndex.length; j++) {
					if(rr[j].getName().contains(gameID+".txt")) //if the files has this particular ending (prevents problems like containing "1" vs "10")
					{
						FileInputStream fs= new FileInputStream(rr[j]);
						BufferedReader br = new BufferedReader(new InputStreamReader(fs));
						for(int k = 0; k < 31; ++k)
						  br.readLine();
						String lineIWant = br.readLine(); //need to change so it reads LAST line A.P
						
						rt = Double.parseDouble(lineIWant); 
					}
				}
        		
        		if(thisDate.equals(wk1Start)||(thisDate.after(wk1Start)&&thisDate.before(wk2Start)))
        		{
        			totalGameswk1++;
        			totalHRwk1+=hr;
        			totalERwk1+=er;
        			timewk1+=duration;
        			totalRTwk1+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialwk1++;
        				totalHRSpatialwk1+=hr;
        				totalERSpatialwk1+=er;
        				timeSpatialwk1+= duration; //adds 0 if it was unfinished
        				totalRTSpatialwk1+=rt;
        				
        				totalGamesSpatial++;
        				totalHRSpatial+=hr;
        				totalERSpatial+=er;
        				timeSpatial+= duration; //adds 0 if it was unfinished
        				totalRTSpatial+=rt;
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesSpatialwk1Home++;
        					totalHRSpatialwk1Home+=hr;
        					totalERSpatialwk1Home+=er;
        					timeSpatialwk1Home+= duration;
        					totalRTSpatialwk1Home+=rt;
        				}
        				
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumwk1++;
        				totalHRLetNumwk1+=hr;
        				totalERLetNumwk1+=er;
        				timeLetNumwk1+= duration;
        				
        				totalGamesLetNum++;
        				totalHRLetNum+=hr;
        				totalERLetNum+=er;
        				timeLetNum+= duration; //adds 0 if it was unfinished
        				totalRTLetNum+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesLetNumwk1Home++;
        					totalHRLetNumwk1Home+=hr;
        					totalERLetNumwk1Home+=er;
        					timeLetNumwk1Home+= duration;
        					totalRTLetNumwk1Home+=rt;
        				}
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgwk1++;
        				totalHRImgwk1+=hr;
        				totalERImgwk1+=er;
        				timeImgwk1+= duration;
        				
        				totalGamesImg++;
        				totalHRImg+=hr;
        				totalERImg+=er;
        				timeImg+= duration; //adds 0 if it was unfinished
        				totalRTImg+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesImgwk1Home++;
        					totalHRImgwk1Home+=hr;
        					totalERImgwk1Home+=er;
        					timeImgwk1Home+= duration;
        					totalRTImgwk1Home+=rt;
        				}
        			}
        		}
        		else if(thisDate.equals(wk2Start)||(thisDate.after(wk2Start)&&thisDate.before(wk3Start)))
        		{
        			totalGameswk2++;
        			totalHRwk2+=hr;
        			totalERwk2+=er;
        			timewk2+=duration;
        			totalRTwk2+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialwk2++;
        				totalHRSpatialwk2+=hr;
        				totalERSpatialwk2+=er;
        				timeSpatialwk2+= duration;
        				
        				totalGamesSpatial++;
        				totalHRSpatial+=hr;
        				totalERSpatial+=er;
        				timeSpatial+= duration; //adds 0 if it was unfinished
        				totalRTSpatial+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesSpatialwk2Home++;
        					totalHRSpatialwk2Home+=hr;
        					totalERSpatialwk2Home+=er;
        					timeSpatialwk2Home+= duration;
        					totalRTSpatialwk2Home+=rt;
        				}
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumwk2++;
        				totalHRLetNumwk2+=hr;
        				totalERLetNumwk2+=er;
        				timeLetNumwk2+= duration;
        				
        				totalGamesLetNum++;
        				totalHRLetNum+=hr;
        				totalERLetNum+=er;
        				timeLetNum+= duration; //adds 0 if it was unfinished
        				totalRTLetNum+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesLetNumwk2Home++;
        					totalHRLetNumwk2Home+=hr;
        					totalERLetNumwk2Home+=er;
        					timeLetNumwk2Home+= duration;
        					totalRTLetNumwk2Home+=rt;
        				}
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgwk2++;
        				totalHRImgwk2+=hr;
        				totalERImgwk2+=er;
        				timeImgwk2+= duration;
        				
        				totalGamesImg++;
        				totalHRImg+=hr;
        				totalERImg+=er;
        				timeImg+= duration; //adds 0 if it was unfinished
        				totalRTImg+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesImgwk2Home++;
        					totalHRImgwk2Home+=hr;
        					totalERImgwk2Home+=er;
        					timeImgwk2Home+= duration;
        					totalRTImgwk2Home+=rt;
        				}
        			}
        		}
        		else if(thisDate.equals(wk3Start)||(thisDate.after(wk3Start)&&thisDate.before(wk4Start)))
        		{
        			totalGameswk3++;
        			totalHRwk3+=hr;
        			totalERwk3+=er;
        			timewk3+=duration;
        			totalRTwk3+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialwk3++;
        				totalHRSpatialwk3+=hr;
        				totalERSpatialwk3+=er;
        				timeSpatialwk3+= duration;
        				
        				totalGamesSpatial++;
        				totalHRSpatial+=hr;
        				totalERSpatial+=er;
        				timeSpatial+= duration; //adds 0 if it was unfinished
        				totalRTSpatial+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesSpatialwk3Home++;
        					totalHRSpatialwk3Home+=hr;
        					totalERSpatialwk3Home+=er;
        					timeSpatialwk3Home+= duration;
        					totalRTSpatialwk3Home+=rt;
        				}
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumwk3++;
        				totalHRLetNumwk3+=hr;
        				totalERLetNumwk3+=er;
        				timeLetNumwk3+= duration;
        				
        				totalGamesLetNum++;
        				totalHRLetNum+=hr;
        				totalERLetNum+=er;
        				timeLetNum+= duration; //adds 0 if it was unfinished
        				totalRTLetNum+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesLetNumwk3Home++;
        					totalHRLetNumwk3Home+=hr;
        					totalERLetNumwk3Home+=er;
        					timeLetNumwk3Home+= duration;
        					totalRTLetNumwk3Home+=rt;
        				}
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgwk3++;
        				totalHRImgwk3+=hr;
        				totalERImgwk3+=er;
        				timeImgwk3+= duration;
        				
        				totalGamesImg++;
        				totalHRImg+=hr;
        				totalERImg+=er;
        				timeImg+= duration; //adds 0 if it was unfinished
        				totalRTImg+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesImgwk3Home++;
        					totalHRImgwk3Home+=hr;
        					totalERImgwk3Home+=er;
        					timeImgwk3Home+= duration;
        					totalRTImgwk3Home+=rt;
        				}
        			}
        		}
        		else if(thisDate.equals(wk4Start)||(thisDate.after(wk4Start)&&thisDate.before(wk5Start)))
        		{
        			totalGameswk4++;
        			totalHRwk4+=hr;
        			totalERwk4+=er;
        			timewk4+=duration;
        			totalRTwk4+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialwk4++;
        				totalHRSpatialwk4+=hr;
        				totalERSpatialwk4+=er;
        				timeSpatialwk4+= duration;
        				
        				totalGamesSpatial++;
        				totalHRSpatial+=hr;
        				totalERSpatial+=er;
        				timeSpatial+= duration; //adds 0 if it was unfinished
        				totalRTSpatial+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesSpatialwk4Home++;
        					totalHRSpatialwk4Home+=hr;
        					totalERSpatialwk4Home+=er;
        					timeSpatialwk4Home+= duration;
        					totalRTSpatialwk4Home+=rt;
        				}
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumwk4++;
        				totalHRLetNumwk4+=hr;
        				totalERLetNumwk4+=er;
        				timeLetNumwk4+= duration;
        				
        				totalGamesLetNum++;
        				totalHRLetNum+=hr;
        				totalERLetNum+=er;
        				timeLetNum+= duration; //adds 0 if it was unfinished
        				totalRTLetNum+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesLetNumwk4Home++;
        					totalHRLetNumwk4Home+=hr;
        					totalERLetNumwk4Home+=er;
        					timeLetNumwk4Home+= duration;
        					totalRTLetNumwk4Home+=rt;
        				}
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgwk4++;
        				totalHRImgwk4+=hr;
        				totalERImgwk4+=er;
        				timeImgwk4+= duration;
        				
        				totalGamesImg++;
        				totalHRImg+=hr;
        				totalERImg+=er;
        				timeImg+= duration; //adds 0 if it was unfinished
        				totalRTImg+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesImgwk4Home++;
        					totalHRImgwk4Home+=hr;
        					totalERImgwk4Home+=er;
        					timeImgwk4Home+= duration;
        					totalRTImgwk4Home+=rt;
        				}
        			}
        		}
        		else if(thisDate.equals(wk5Start)||(thisDate.after(wk5Start)&&thisDate.before(wk5End)))
        		{
        			totalGameswk5++;
        			totalHRwk5+=hr;
        			totalERwk5+=er;
        			timewk5+=duration;
        			totalRTwk5+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialwk5++;
        				totalHRSpatialwk5+=hr;
        				totalERSpatialwk5+=er;
        				timeSpatialwk5+= duration;
        				
        				totalGamesSpatial++;
        				totalHRSpatial+=hr;
        				totalERSpatial+=er;
        				timeSpatial+= duration; //adds 0 if it was unfinished
        				totalRTSpatial+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesSpatialwk5Home++;
        					totalHRSpatialwk5Home+=hr;
        					totalERSpatialwk5Home+=er;
        					timeSpatialwk5Home+= duration;
        					totalRTSpatialwk5Home+=rt;
        				}
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumwk5++;
        				totalHRLetNumwk5+=hr;
        				totalERLetNumwk5+=er;
        				timeLetNumwk5+= duration;
        				
        				totalGamesLetNum++;
        				totalHRLetNum+=hr;
        				totalERLetNum+=er;
        				timeLetNum+= duration; //adds 0 if it was unfinished
        				totalRTLetNum+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesLetNumwk5Home++;
        					totalHRLetNumwk5Home+=hr;
        					totalERLetNumwk5Home+=er;
        					timeLetNumwk5Home+= duration;
        					totalRTLetNumwk5Home+=rt;
        				}
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgwk5++;
        				totalHRImgwk5+=hr;
        				totalERImgwk5+=er;
        				timeImgwk5+= duration;
        				
        				totalGamesImg++;
        				totalHRImg+=hr;
        				totalERImg+=er;
        				timeImg+= duration; //adds 0 if it was unfinished
        				totalRTImg+=rt;
        				
        				//Check to see if it's a home date within this week.
        				if(!(thisDate.equals(lab1)||thisDate.equals(lab2)||thisDate.equals(lab3)||thisDate.equals(lab4)||thisDate.equals(lab5)))
        				{
        					totalGamesImgwk5Home++;
        					totalHRImgwk5Home+=hr;
        					totalERImgwk5Home+=er;
        					timeImgwk5Home+= duration;
        					totalRTImgwk5Home+=rt;
        				}
        			}
        		}
        		
        		//Now, fill in lab data
        		if(thisDate.equals(lab1))
        		{
        			totalGamesLab1++;
        			totalHRLab1+=hr;
        			totalERLab1+=er;
        			timeLab1+=duration;
        			totalRTLab1+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialLab1++;
            			totalHRSpatialLab1+=hr;
            			totalERSpatialLab1+=er;
            			timeSpatialLab1+=duration;
            			totalRTSpatialLab1+=rt;
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumLab1++;
            			totalHRLetNumLab1+=hr;
            			totalERLetNumLab1+=er;
            			timeLetNumLab1+=duration;
            			totalRTLetNumLab1+=rt;
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgLab1++;
            			totalHRImgLab1+=hr;
            			totalERImgLab1+=er;
            			timeImgLab1+=duration;
            			totalRTImgLab1+=rt;
        			}
        		}
        		else if(thisDate.equals(lab2))
        		{
        			totalGamesLab2++;
        			totalHRLab2+=hr;
        			totalERLab2+=er;
        			timeLab2+=duration;
        			totalRTLab2+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialLab2++;
            			totalHRSpatialLab2+=hr;
            			totalERSpatialLab2+=er;
            			timeSpatialLab2+=duration;
            			totalRTSpatialLab2+=rt;
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumLab2++;
            			totalHRLetNumLab2+=hr;
            			totalERLetNumLab2+=er;
            			timeLetNumLab2+=duration;
            			totalRTLetNumLab2+=rt;
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgLab2++;
            			totalHRImgLab2+=hr;
            			totalERImgLab2+=er;
            			timeImgLab2+=duration;
            			totalRTImgLab2+=rt;
        			}
        		}
        		else if(thisDate.equals(lab3))
        		{
        			totalGamesLab3++;
        			totalHRLab3+=hr;
        			totalERLab3+=er;
        			timeLab3+=duration;
        			totalRTLab3+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialLab3++;
            			totalHRSpatialLab3+=hr;
            			totalERSpatialLab3+=er;
            			timeSpatialLab3+=duration;
            			totalRTSpatialLab3+=rt;
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumLab3++;
            			totalHRLetNumLab3+=hr;
            			totalERLetNumLab3+=er;
            			timeLetNumLab3+=duration;
            			totalRTLetNumLab3+=rt;
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgLab3++;
            			totalHRImgLab3+=hr;
            			totalERImgLab3+=er;
            			timeImgLab3+=duration;
            			totalRTImgLab3+=rt;
        			}
        		}
        		else if(thisDate.equals(lab4))
        		{
        			totalGamesLab4++;
        			totalHRLab4+=hr;
        			totalERLab4+=er;
        			timeLab4+=duration;
        			totalRTLab4+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialLab4++;
            			totalHRSpatialLab4+=hr;
            			totalERSpatialLab4+=er;
            			timeSpatialLab4+=duration;
            			totalRTSpatialLab4+=rt;
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumLab4++;
            			totalHRLetNumLab4+=hr;
            			totalERLetNumLab4+=er;
            			timeLetNumLab4+=duration;
            			totalRTLetNumLab4+=rt;
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgLab4++;
            			totalHRImgLab4+=hr;
            			totalERImgLab4+=er;
            			timeImgLab4+=duration;
            			totalRTImgLab4+=rt;
        			}
        		}
        		else if(thisDate.equals(lab5))
        		{
        			totalGamesLab5++;
        			totalHRLab5+=hr;
        			totalERLab5+=er;
        			timeLab5+=duration;
        			totalRTLab5+=rt;
        			if(type.equals("Spatial Grid with Brain"))
        			{
        				totalGamesSpatialLab5++;
            			totalHRSpatialLab5+=hr;
            			totalERSpatialLab5+=er;
            			timeSpatialLab5+=duration;
            			totalRTSpatialLab5+=rt;
        			}
        			else if(type.equals("Letters and Numbers"))
        			{
        				totalGamesLetNumLab5++;
            			totalHRLetNumLab5+=hr;
            			totalERLetNumLab5+=er;
            			timeLetNumLab5+=duration;
            			totalRTLetNumLab5+=rt;
        			}
        			else if(type.equals("Images from folders"))
        			{
        				totalGamesImgLab5++;
            			totalHRImgLab5+=hr;
            			totalERImgLab5+=er;
            			timeImgLab5+=duration;
            			totalRTImgLab5+=rt;
        			}
        		}        		
        	}
        }//End of for loop; 
        
        //Now for all the "totals" calculate avgs by dividing by total num games accordingly
        //60 of each of the following: avgHr, avgER, avgRT = 180 averages
        	//20 for each of weeks, lab, home
        
        //WEEKS AVERAGES
        	//HR
        avgHRSpatialwk1 = totalHRSpatialwk1/totalGamesSpatialwk1;
		avgHRSpatialwk2 = totalHRSpatialwk2/totalGamesSpatialwk2;
		avgHRSpatialwk3 = totalHRSpatialwk3/totalGamesSpatialwk3;
		avgHRSpatialwk4 = totalHRSpatialwk4/totalGamesSpatialwk4;
		avgHRSpatialwk5 = totalHRSpatialwk5/totalGamesSpatialwk5;
		
		avgHRLetNumwk1 = totalHRLetNumwk1/totalGamesLetNumwk1;
		avgHRLetNumwk2 = totalHRLetNumwk2/totalGamesLetNumwk2;
		avgHRLetNumwk3 = totalHRLetNumwk3/totalGamesLetNumwk3;
		avgHRLetNumwk4 = totalHRLetNumwk4/totalGamesLetNumwk4;
		avgHRLetNumwk5 = totalHRLetNumwk5/totalGamesLetNumwk5;
		
		avgHRImgwk1 = totalHRImgwk1/totalGamesImgwk1;
		avgHRImgwk2 = totalHRImgwk2/totalGamesImgwk2;
		avgHRImgwk3 = totalHRImgwk3/totalGamesImgwk3;
		avgHRImgwk4 = totalHRImgwk4/totalGamesImgwk4;
		avgHRImgwk5 = totalHRImgwk5/totalGamesImgwk5;
		
		avgHRwk1 = totalHRwk1/totalGameswk1;
		avgHRwk2 = totalHRwk2/totalGameswk2;
		avgHRwk3 = totalHRwk3/totalGameswk3;
		avgHRwk4 = totalHRwk4/totalGameswk4;
		avgHRwk5 = totalHRwk5/totalGameswk5;

			//ER
		avgERSpatialwk1 = totalERSpatialwk1/totalGamesSpatialwk1;
		avgERSpatialwk2 = totalERSpatialwk2/totalGamesSpatialwk2;
		avgERSpatialwk3 = totalERSpatialwk3/totalGamesSpatialwk3;
		avgERSpatialwk4 = totalERSpatialwk4/totalGamesSpatialwk4;
		avgERSpatialwk5 = totalERSpatialwk5/totalGamesSpatialwk5;
		
		avgERLetNumwk1 = totalERLetNumwk1/totalGamesLetNumwk1;
		avgERLetNumwk2 = totalERLetNumwk2/totalGamesLetNumwk2;
		avgERLetNumwk3 = totalERLetNumwk3/totalGamesLetNumwk3;
		avgERLetNumwk4 = totalERLetNumwk4/totalGamesLetNumwk4;
		avgERLetNumwk5 = totalERLetNumwk5/totalGamesLetNumwk5;
		
		avgERImgwk1 = totalERImgwk1/totalGamesImgwk1;
		avgERImgwk2 = totalERImgwk2/totalGamesImgwk2;
		avgERImgwk3 = totalERImgwk3/totalGamesImgwk3;
		avgERImgwk4 = totalERImgwk4/totalGamesImgwk4;
		avgERImgwk5 = totalERImgwk5/totalGamesImgwk5;
		
		avgERwk1 = totalERwk1/totalGameswk1;
		avgERwk2 = totalERwk2/totalGameswk2;
		avgERwk3 = totalERwk3/totalGameswk3;
		avgERwk4 = totalERwk4/totalGameswk4;
		avgERwk5 = totalERwk5/totalGameswk5;
		
			//RT
		avgRTSpatialwk1 = totalRTSpatialwk1/totalGamesSpatialwk1;
		avgRTSpatialwk2 = totalRTSpatialwk2/totalGamesSpatialwk2;
		avgRTSpatialwk3 = totalRTSpatialwk3/totalGamesSpatialwk3;
		avgRTSpatialwk4 = totalRTSpatialwk4/totalGamesSpatialwk4;
		avgRTSpatialwk5 = totalRTSpatialwk5/totalGamesSpatialwk5;
		
		avgRTLetNumwk1 = totalRTLetNumwk1/totalGamesLetNumwk1;
		avgRTLetNumwk2 = totalRTLetNumwk2/totalGamesLetNumwk2;
		avgRTLetNumwk3 = totalRTLetNumwk3/totalGamesLetNumwk3;
		avgRTLetNumwk4 = totalRTLetNumwk4/totalGamesLetNumwk4;
		avgRTLetNumwk5 = totalRTLetNumwk5/totalGamesLetNumwk5;
		
		avgRTImgwk1 = totalRTImgwk1/totalGamesImgwk1;
		avgRTImgwk2 = totalRTImgwk2/totalGamesImgwk2;
		avgRTImgwk3 = totalRTImgwk3/totalGamesImgwk3;
		avgRTImgwk4 = totalRTImgwk4/totalGamesImgwk4;
		avgRTImgwk5 = totalRTImgwk5/totalGamesImgwk5;
		
		avgRTwk1 = totalRTwk1/totalGameswk1;
		avgRTwk2 = totalRTwk2/totalGameswk2;
		avgRTwk3 = totalRTwk3/totalGameswk3;
		avgRTwk4 = totalRTwk4/totalGameswk4;
		avgRTwk5 = totalRTwk5/totalGameswk5;
		
			//TYPE HR
		avgHRSpatial = totalHRSpatial/totalGamesSpatial;
		avgHRLetNum = totalHRLetNum/totalGamesLetNum;
		avgHRImg = totalHRImg/totalGamesImg;
		
			//TYPE ER
		avgERSpatial = totalERSpatial/totalGamesSpatial;
		avgERLetNum = totalERLetNum/totalGamesLetNum;
		avgERImg = totalERImg/totalGamesImg;
		
			//TYPE RT
		avgRTSpatial = totalRTSpatial/totalGamesSpatial;
		avgRTLetNum = totalRTLetNum/totalGamesLetNum;
		avgRTImg = totalRTImg/totalGamesImg;
		
		//LABS AVERAGES
			//HR
        avgHRSpatialLab1 = totalHRSpatialLab1/totalGamesSpatialLab1;
		avgHRSpatialLab2 = totalHRSpatialLab2/totalGamesSpatialLab2;
		avgHRSpatialLab3 = totalHRSpatialLab3/totalGamesSpatialLab3;
		avgHRSpatialLab4 = totalHRSpatialLab4/totalGamesSpatialLab4;
		avgHRSpatialLab5 = totalHRSpatialLab5/totalGamesSpatialLab5;
		
		avgHRLetNumLab1 = totalHRLetNumLab1/totalGamesLetNumLab1;
		avgHRLetNumLab2 = totalHRLetNumLab2/totalGamesLetNumLab2;
		avgHRLetNumLab3 = totalHRLetNumLab3/totalGamesLetNumLab3;
		avgHRLetNumLab4 = totalHRLetNumLab4/totalGamesLetNumLab4;
		avgHRLetNumLab5 = totalHRLetNumLab5/totalGamesLetNumLab5;
		
		avgHRImgLab1 = totalHRImgLab1/totalGamesImgLab1;
		avgHRImgLab2 = totalHRImgLab2/totalGamesImgLab2;
		avgHRImgLab3 = totalHRImgLab3/totalGamesImgLab3;
		avgHRImgLab4 = totalHRImgLab4/totalGamesImgLab4;
		avgHRImgLab5 = totalHRImgLab5/totalGamesImgLab5;
		
		avgHRLab1 = totalHRLab1/totalGamesLab1;
		avgHRLab2 = totalHRLab2/totalGamesLab2;
		avgHRLab3 = totalHRLab3/totalGamesLab3;
		avgHRLab4 = totalHRLab4/totalGamesLab4;
		avgHRLab5 = totalHRLab5/totalGamesLab5;

			//ER
		avgERSpatialLab1 = totalERSpatialLab1/totalGamesSpatialLab1;
		avgERSpatialLab2 = totalERSpatialLab2/totalGamesSpatialLab2;
		avgERSpatialLab3 = totalERSpatialLab3/totalGamesSpatialLab3;
		avgERSpatialLab4 = totalERSpatialLab4/totalGamesSpatialLab4;
		avgERSpatialLab5 = totalERSpatialLab5/totalGamesSpatialLab5;
		
		avgERLetNumLab1 = totalERLetNumLab1/totalGamesLetNumLab1;
		avgERLetNumLab2 = totalERLetNumLab2/totalGamesLetNumLab2;
		avgERLetNumLab3 = totalERLetNumLab3/totalGamesLetNumLab3;
		avgERLetNumLab4 = totalERLetNumLab4/totalGamesLetNumLab4;
		avgERLetNumLab5 = totalERLetNumLab5/totalGamesLetNumLab5;
		
		avgERImgLab1 = totalERImgLab1/totalGamesImgLab1;
		avgERImgLab2 = totalERImgLab2/totalGamesImgLab2;
		avgERImgLab3 = totalERImgLab3/totalGamesImgLab3;
		avgERImgLab4 = totalERImgLab4/totalGamesImgLab4;
		avgERImgLab5 = totalERImgLab5/totalGamesImgLab5;
		
		avgERLab1 = totalERLab1/totalGamesLab1;
		avgERLab2 = totalERLab2/totalGamesLab2;
		avgERLab3 = totalERLab3/totalGamesLab3;
		avgERLab4 = totalERLab4/totalGamesLab4;
		avgERLab5 = totalERLab5/totalGamesLab5;
		
			//RT
		avgRTSpatialLab1 = totalRTSpatialLab1/totalGamesSpatialLab1;
		avgRTSpatialLab2 = totalRTSpatialLab2/totalGamesSpatialLab2;
		avgRTSpatialLab3 = totalRTSpatialLab3/totalGamesSpatialLab3;
		avgRTSpatialLab4 = totalRTSpatialLab4/totalGamesSpatialLab4;
		avgRTSpatialLab5 = totalRTSpatialLab5/totalGamesSpatialLab5;
		
		avgRTLetNumLab1 = totalRTLetNumLab1/totalGamesLetNumLab1;
		avgRTLetNumLab2 = totalRTLetNumLab2/totalGamesLetNumLab2;
		avgRTLetNumLab3 = totalRTLetNumLab3/totalGamesLetNumLab3;
		avgRTLetNumLab4 = totalRTLetNumLab4/totalGamesLetNumLab4;
		avgRTLetNumLab5 = totalRTLetNumLab5/totalGamesLetNumLab5;
		
		avgRTImgLab1 = totalRTImgLab1/totalGamesImgLab1;
		avgRTImgLab2 = totalRTImgLab2/totalGamesImgLab2;
		avgRTImgLab3 = totalRTImgLab3/totalGamesImgLab3;
		avgRTImgLab4 = totalRTImgLab4/totalGamesImgLab4;
		avgRTImgLab5 = totalRTImgLab5/totalGamesImgLab5;
		
		avgRTLab1 = totalRTLab1/totalGamesLab1;
		avgRTLab2 = totalRTLab2/totalGamesLab2;
		avgRTLab3 = totalRTLab3/totalGamesLab3;
		avgRTLab4 = totalRTLab4/totalGamesLab4;
		avgRTLab5 = totalRTLab5/totalGamesLab5;
		
		
		
		//HOME AVERAGES
			//HR
        avgHRSpatialwk1Home = totalHRSpatialwk1Home/totalGamesSpatialwk1Home;
		avgHRSpatialwk2Home = totalHRSpatialwk2Home/totalGamesSpatialwk2Home;
		avgHRSpatialwk3Home = totalHRSpatialwk3Home/totalGamesSpatialwk3Home;
		avgHRSpatialwk4Home = totalHRSpatialwk4Home/totalGamesSpatialwk4Home;
		avgHRSpatialwk5Home = totalHRSpatialwk5Home/totalGamesSpatialwk5Home;
		
		avgHRLetNumwk1Home = totalHRLetNumwk1Home/totalGamesLetNumwk1Home;
		avgHRLetNumwk2Home = totalHRLetNumwk2Home/totalGamesLetNumwk2Home;
		avgHRLetNumwk3Home = totalHRLetNumwk3Home/totalGamesLetNumwk3Home;
		avgHRLetNumwk4Home = totalHRLetNumwk4Home/totalGamesLetNumwk4Home;
		avgHRLetNumwk5Home = totalHRLetNumwk5Home/totalGamesLetNumwk5Home;
		
		avgHRImgwk1Home = totalHRImgwk1Home/totalGamesImgwk1Home;
		avgHRImgwk2Home = totalHRImgwk2Home/totalGamesImgwk2Home;
		avgHRImgwk3Home = totalHRImgwk3Home/totalGamesImgwk3Home;
		avgHRImgwk4Home = totalHRImgwk4Home/totalGamesImgwk4Home;
		avgHRImgwk5Home = totalHRImgwk5Home/totalGamesImgwk5Home;
		
		avgHRwk1Home = totalHRwk1Home/totalGameswk1Home;
		avgHRwk2Home = totalHRwk2Home/totalGameswk2Home;
		avgHRwk3Home = totalHRwk3Home/totalGameswk3Home;
		avgHRwk4Home = totalHRwk4Home/totalGameswk4Home;
		avgHRwk5Home = totalHRwk5Home/totalGameswk5Home;

			//ER
		avgERSpatialwk1Home = totalERSpatialwk1Home/totalGamesSpatialwk1Home;
		avgERSpatialwk2Home = totalERSpatialwk2Home/totalGamesSpatialwk2Home;
		avgERSpatialwk3Home = totalERSpatialwk3Home/totalGamesSpatialwk3Home;
		avgERSpatialwk4Home = totalERSpatialwk4Home/totalGamesSpatialwk4Home;
		avgERSpatialwk5Home = totalERSpatialwk5Home/totalGamesSpatialwk5Home;
		
		avgERLetNumwk1Home = totalERLetNumwk1Home/totalGamesLetNumwk1Home;
		avgERLetNumwk2Home = totalERLetNumwk2Home/totalGamesLetNumwk2Home;
		avgERLetNumwk3Home = totalERLetNumwk3Home/totalGamesLetNumwk3Home;
		avgERLetNumwk4Home = totalERLetNumwk4Home/totalGamesLetNumwk4Home;
		avgERLetNumwk5Home = totalERLetNumwk5Home/totalGamesLetNumwk5Home;
		
		avgERImgwk1Home = totalERImgwk1Home/totalGamesImgwk1Home;
		avgERImgwk2Home = totalERImgwk2Home/totalGamesImgwk2Home;
		avgERImgwk3Home = totalERImgwk3Home/totalGamesImgwk3Home;
		avgERImgwk4Home = totalERImgwk4Home/totalGamesImgwk4Home;
		avgERImgwk5Home = totalERImgwk5Home/totalGamesImgwk5Home;
		
		avgERwk1Home = totalERwk1Home/totalGameswk1Home;
		avgERwk2Home = totalERwk2Home/totalGameswk2Home;
		avgERwk3Home = totalERwk3Home/totalGameswk3Home;
		avgERwk4Home = totalERwk4Home/totalGameswk4Home;
		avgERwk5Home = totalERwk5Home/totalGameswk5Home;
		
			//RT
		avgRTSpatialwk1Home = totalRTSpatialwk1Home/totalGamesSpatialwk1Home;
		avgRTSpatialwk2Home = totalRTSpatialwk2Home/totalGamesSpatialwk2Home;
		avgRTSpatialwk3Home = totalRTSpatialwk3Home/totalGamesSpatialwk3Home;
		avgRTSpatialwk4Home = totalRTSpatialwk4Home/totalGamesSpatialwk4Home;
		avgRTSpatialwk5Home = totalRTSpatialwk5Home/totalGamesSpatialwk5Home;
		
		avgRTLetNumwk1Home = totalRTLetNumwk1Home/totalGamesLetNumwk1Home;
		avgRTLetNumwk2Home = totalRTLetNumwk2Home/totalGamesLetNumwk2Home;
		avgRTLetNumwk3Home = totalRTLetNumwk3Home/totalGamesLetNumwk3Home;
		avgRTLetNumwk4Home = totalRTLetNumwk4Home/totalGamesLetNumwk4Home;
		avgRTLetNumwk5Home = totalRTLetNumwk5Home/totalGamesLetNumwk5Home;
		
		avgRTImgwk1Home = totalRTImgwk1Home/totalGamesImgwk1Home;
		avgRTImgwk2Home = totalRTImgwk2Home/totalGamesImgwk2Home;
		avgRTImgwk3Home = totalRTImgwk3Home/totalGamesImgwk3Home;
		avgRTImgwk4Home = totalRTImgwk4Home/totalGamesImgwk4Home;
		avgRTImgwk5Home = totalRTImgwk5Home/totalGamesImgwk5Home;
		
		avgRTwk1Home = totalRTwk1Home/totalGameswk1Home;
		avgRTwk2Home = totalRTwk2Home/totalGameswk2Home;
		avgRTwk3Home = totalRTwk3Home/totalGameswk3Home;
		avgRTwk4Home = totalRTwk4Home/totalGameswk4Home;
		avgRTwk5Home = totalRTwk5Home/totalGameswk5Home;
		
        
		/**
         * The following data have been obtained:
         * 1) HRAvg per type per week 
         * 2) ERAvg per type per week 
         * 3) num of games per type per week
         * 4) total time spent per type per week 
         * 5) RT per type per week
         * 6) 1-5 per week
         * 7) 1-5 Overall for each type
         * 8) 1-5 for each lab
         * 9) 1-5 for each home week
         * 
         */
        
        
        //Finally, put data into row of nbackById 2D array using index
        nbackByID[index][0] = String.valueOf(id);
        nbackByID[index][1] = group;
        nbackByID[index][2] = String.valueOf(round);
        nbackByID[index][3] = String.valueOf(age);
        nbackByID[index][4] = first_lab_dayString;
        nbackByID[index][5] = last_dayString;
        nbackByID[index][6] = pre_test_dayString;
        nbackByID[index][7] = post_test_dayString;
        nbackByID[index][8] = String.valueOf(days_elapsed);
        nbackByID[index][9] = String.valueOf(last_day_to_post_day);
        nbackByID[index][10] = String.valueOf(pre_test_to_post_test);
        nbackByID[index][11] = String.valueOf(pre_test_to_first_lab_day);
        nbackByID[index][12] = "0";
        nbackByID[index][13] = "0";
        nbackByID[index][14] = "0";
        nbackByID[index][15] = "0";
        nbackByID[index][16] = "0";
        nbackByID[index][17] = "0";
        nbackByID[index][18] = "0";
        nbackByID[index][19] = "0";
        nbackByID[index][20] = "0";
        nbackByID[index][21] = "0";
        nbackByID[index][22] = "0";
        nbackByID[index][23] = "0";
        nbackByID[index][24] = "0";
        nbackByID[index][25] = "0";
        nbackByID[index][26] = "0";
        
        
		}
		catch(Exception e) //If it finds an error, don't continue
		{
			e.printStackTrace();
		}
	}
	
	private static String[][] nbackByDateSheet(File file,String[][] sessionDate_table, String[][]nbackByDateRows) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory 
        = DocumentBuilderFactory.newInstance();
     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
     Document doc = dBuilder.parse(file);
     doc.getDocumentElement().normalize();
     NodeList rowList = doc.getElementsByTagName("Row");
     for (int temp = 0; temp < rowList.getLength(); temp++) {
        Element nNode = (Element) rowList.item(temp);
        NodeList cellList = nNode.getElementsByTagName("Cell");
        System.out.println("Row "+temp);
        StringBuffer content = new StringBuffer();
        for (int j = 0; j < cellList.getLength(); j++) {
        	Node abc = cellList.item(j);
        	String text = ((Element)abc.getChildNodes()).getTextContent();
        	content.append(text+",");

        }
    	System.out.println("Row "+temp + "="+content);
     }
		return nbackByDateRows;
	}
	
	private static void sudokuByIDSheet(File file,String[][] sessionDate_table, String[][] nbackByID, int index) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory 
        = DocumentBuilderFactory.newInstance();
     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
     Document doc = dBuilder.parse(file);
     doc.getDocumentElement().normalize();
     NodeList rowList = doc.getElementsByTagName("Row");
     for (int temp = 0; temp < rowList.getLength(); temp++) {
        Element nNode = (Element) rowList.item(temp);
        NodeList cellList = nNode.getElementsByTagName("Cell");
        System.out.println("Row "+temp);
        StringBuffer content = new StringBuffer();
        for (int j = 0; j < cellList.getLength(); j++) {
        	Node abc = cellList.item(j);
        	String text = ((Element)abc.getChildNodes()).getTextContent();
        	content.append(text+",");
        }
    	System.out.println("Row "+temp + "="+content);
     }
	}
	
	private static String[][] sudokuByDateSheet(File file, String[][] sessionDate_table, String[][] sudokuByDateRows) {
		// TODO Auto-generated method stub
		return sudokuByDateRows;
	}

	// Functions
	
	//Functions for the file based on sessionDates
	
	private static String[][] sessionDateSheetInit(String[][] sessionInfoData_table, int sessionInfoData_row_num, int sessionInfoData_col_num,
				String[][] sessionDate_table, Workbook workbook_w) {
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
			
										/*----------------- SessionDate SHEET ------------------------*/
				
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		
		//Initializing the titles of the columns
		sessionDate_table[0][0] = "ID";
		sessionDate_table[0][1] = "Group";
		sessionDate_table[0][2] = "Round";
		sessionDate_table[0][3] = "Age";
		sessionDate_table[0][4] = "Pre_Date";
		sessionDate_table[0][5] = "Week1";
		sessionDate_table[0][6] = "Week2";
		sessionDate_table[0][7] = "Week3";
		sessionDate_table[0][8] = "Week4";
		sessionDate_table[0][9] = "Week5";
		sessionDate_table[0][10] = "Post_Date";
		sessionDate_table[0][11] = "Examiner";
		sessionDate_table[0][12] = "Pre_Date_Time"; //1 = morning = 6am-11:59am (0.25-0.5); 2 = afternoon = 12pm-5:59pm (0.5-0.75); 3 = evening = 6pm-11:59pm (0.75-1); 4 = night = 12am-5:59am (0-0.25)
		sessionDate_table[0][13] = "Post_Date_Time";
		
		String[] missingInfo = new String[13];
		
		for(int i = 1;i<sessionInfoData_row_num; i++)
		{
			if(!sessionInfoData_table[i][0].isEmpty()) //Skip the row if it does not have any data in it
			{
				sessionDate_table[i][0] = sessionInfoData_table[i][0]; //Initialize ID for the row
				if(!sessionInfoData_table[i][3].isEmpty())//If there is a GROUP specified
				{
					sessionDate_table[i][1] = sessionInfoData_table[i][3]; //GROUP is the second column
				}
				else
				{
					missingInfo[0] = "Group";
				}
				
				if(!sessionInfoData_table[i][1].isEmpty())//If there is a ROUND specified
				{
					sessionDate_table[i][2] = sessionInfoData_table[i][1]; //ROUND is the third column
				}
				else
				{
					missingInfo[1] = "Round"; 
				}
				
				if(!sessionInfoData_table[i][2].isEmpty())//If there is a AGE specified
				{
					sessionDate_table[i][3] = sessionInfoData_table[i][2]; //AGE is the fourth column
				}
				else
				{
					missingInfo[2] = "Age"; 
				}
				
				if(!sessionInfoData_table[i][4].isEmpty())//If there is a PRE-DATE specified
				{ //NEED TO CONVERT TO A NUMBER
					sessionDate_table[i][4] = sessionInfoData_table[i][4]; //PRE-DATE is the fifth Column
				}
				else
				{
					missingInfo[3] = "Pre-Date"; 
				}
				
				if(!sessionInfoData_table[i][6].isEmpty())//If there is a WEEK1 specified
				{ //NEED TO CONVERT TO A NUMBER
					sessionDate_table[i][5] = sessionInfoData_table[i][6]; //WEEK1 is the sixth Column
				}
				else
				{
					missingInfo[4] = "Week 1"; 
				}
				
				if(!sessionInfoData_table[i][7].isEmpty())//If there is a WEEK2 specified
				{//NEED TO CONVERT TO A NUMBER
					sessionDate_table[i][6] = sessionInfoData_table[i][7]; //WEEK2 is the seventh Column
				}
				else
				{
					missingInfo[5] = "Week 2"; 
				}
				
				if(!sessionInfoData_table[i][8].isEmpty())//If there is a WEEK3 specified
				{//NEED TO CONVERT TO A NUMBER
					sessionDate_table[i][7] = sessionInfoData_table[i][8]; //WEEK3 is the eighth Column
				}
				else
				{
					missingInfo[6] = "Week 3"; 
				}
				
				if(!sessionInfoData_table[i][9].isEmpty())//If there is a WEEK4 specified
				{//NEED TO CONVERT TO A NUMBER
					sessionDate_table[i][8] = sessionInfoData_table[i][9]; //WEEK4 is the ninth Column
				}
				else
				{
					missingInfo[7] = "Week 4"; 
				}
				
				if(!sessionInfoData_table[i][10].isEmpty())//If there is a WEEK5 specified
				{//NEED TO CONVERT TO A NUMBER
					sessionDate_table[i][9] = sessionInfoData_table[i][10]; //WEEK5 is the tenth Column
				}
				else
				{//NEED TO CONVERT TO A NUMBER
					missingInfo[8] = "Week 5 "; 
				}
				
				if(!sessionInfoData_table[i][12].isEmpty())//If there is a POST-DATE specified
				{
					sessionDate_table[i][10] = sessionInfoData_table[i][12]; //POST-DATE is the eleventh Column
				}
				else
				{
					missingInfo[9] = "Post-Date"; 
				}
				
				if(!sessionInfoData_table[i][11].isEmpty())//If there is an EXAMINER specified
				{
					sessionDate_table[i][11] = sessionInfoData_table[i][11]; //EXAMINER is the twelfth Column
				}
				else
				{
					missingInfo[10] = "Examiner"; 
				}
				
				if(!sessionInfoData_table[i][5].isEmpty())//If there is a PRE-DATE TIME specified
				{
					sessionDate_table[i][12] = sessionInfoData_table[i][3]; //PRE-DATE TIME is the thirteenth Column
				}
				else
				{
					missingInfo[11] = "Pre-Date_Time"; 
				}
				
				if(!sessionInfoData_table[i][13].isEmpty())//If there is a POST-DATE TIME specified
				{
					sessionDate_table[i][13] = sessionInfoData_table[i][13]; //POST-DATE Time is the fourteenth Column
				}
				else
				{
					missingInfo[12] = "Post-Date_Time"; 
				}
				
				//Now, print out what values were missing.
				System.out.println("The following data are missing for ID #"+sessionDate_table[i][0]+":");
				for(int j = 0; j<13;i++)
				{
					if(!missingInfo[j].isEmpty())
					{
						System.out.println(missingInfo[j]);
					}
				}
			}
			
		}
		
		// Done putting in values into the 2D array; now, put the array into a spreadsheet.
		Sheet sessionDates = workbook_w.createSheet("Session_Dates");
		
		Cell cell_w;
		
		for(int r = 0 ; r < sessionInfoData_row_num ; r++) //make x rows where x is the total number of rows of users + title row
		{
			Row row = sessionDates.createRow(r);
			for(int c = 0 ; c < 14 ; c++)
			{
				if(r == 0||c == 11) //If it's either the titles row or the examiner column, don't turn the cell type into a number
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(sessionDate_table[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					if(sessionDate_table[r][c].equals("")) //If the cell is empty
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue("");
					}	
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(sessionDate_table[r][c].toString()));
					}
				}
			}
		}//Finished adding sheet to output spreadsheet
		return sessionDate_table;
		
	}
	private static String[][] nbackByIDSheetInit(String[][] nbackByID_table) {
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
									/*----------------- nbackByID SHEET ------------------------*/
		//ADD 15 COLUMNS FOR RESPONSE RATES BY TYPE OF NBACK && RESPONSE RATES IN LAB VS IN HOME A.P
		//Look for parsing into correct and incorrect A.P
	//Initializing the titles of the columns
		nbackByID_table[0][0] = "ID"; //from session dates
		nbackByID_table[0][1] = "Group"; //from session dates
		nbackByID_table[0][2] = "Round"; //from session dates
		nbackByID_table[0][3] = "Age"; //from session dates
		nbackByID_table[0][4] = "First_Lab_Day"; //Very first login date, also should be from session dates
		nbackByID_table[0][5] = "Last_Day"; //last recorded login
		nbackByID_table[0][6] = "Pre-Test_Day"; //from session dates
		nbackByID_table[0][7] = "Post-Test_Day"; //from session dates
		nbackByID_table[0][8] = "Days_Elapsed"; //Last_day - First_Lab_Day
		nbackByID_table[0][9] = "Last_day_played_to_post_test_day"; //Post-Test_day - Last_Day; may be negative 
		nbackByID_table[0][10] = "Pre-Test_to_Post_Test"; //post-test_day - Pre-Test_day
		nbackByID_table[0][11] = "Pre-Test_to_First_Lab_Day"; // First_Lab_Day - Pre-Test_Day
		nbackByID_table[0][12] = "HitRateAvg_wk1_2back"; // Hit Rate = numCorrect divided by total submissions; Take avg for all games within the week
		nbackByID_table[0][13] = "HitRateAvg_wk2_2back"; // new week = every 7 days starting from the first lab day
		nbackByID_table[0][14] = "HitRateAvg_wk3_2back";
		nbackByID_table[0][15] = "HitRateAvg_wk4_2back";
		nbackByID_table[0][16] = "HitRateAvg_wk5_2back";
		nbackByID_table[0][17] = "HitRateAvg_wk1_3back";
		nbackByID_table[0][18] = "HitRateAvg_wk2_3back";
		nbackByID_table[0][19] = "HitRateAvg_wk3_3back";
		nbackByID_table[0][20] = "HitRateAvg_wk4_3back";
		nbackByID_table[0][21] = "HitRateAvg_wk5_3back";
		nbackByID_table[0][22] = "HitRateAvg_wk1_4back";
		nbackByID_table[0][23] = "HitRateAvg_wk2_4back";
		nbackByID_table[0][24] = "HitRateAvg_wk3_4back";
		nbackByID_table[0][25] = "HitRateAvg_wk4_4back";
		nbackByID_table[0][26] = "HitRateAvg_wk5_4back";
		nbackByID_table[0][27] = "HitRateAvg_wk1_img";
		nbackByID_table[0][28] = "HitRateAvg_wk2_img";
		nbackByID_table[0][29] = "HitRateAvg_wk3_img";
		nbackByID_table[0][30] = "HitRateAvg_wk4_img";
		nbackByID_table[0][31] = "HitRateAvg_wk5_img";
		nbackByID_table[0][32] = "HitRateAvg_wk1_letterNumber";
		nbackByID_table[0][33] = "HitRateAvg_wk2_letterNumber";
		nbackByID_table[0][34] = "HitRateAvg_wk3_letterNumber";
		nbackByID_table[0][35] = "HitRateAvg_wk4_letterNumber";
		nbackByID_table[0][36] = "HitRateAvg_wk5_letterNumber";
		nbackByID_table[0][37] = "HitRateAvg_wk1_spatial";
		nbackByID_table[0][38] = "HitRateAvg_wk2_spatial";
		nbackByID_table[0][39] = "HitRateAvg_wk3_spatial";
		nbackByID_table[0][40] = "HitRateAvg_wk4_spatial";
		nbackByID_table[0][41] = "HitRateAvg_wk5_spatial";
		nbackByID_table[0][42] = "ErrRateAvg_wk1_2back";
		nbackByID_table[0][43] = "ErrRateAvg_wk2_2back";
		nbackByID_table[0][44] = "ErrRateAvg_wk3_2back";
		nbackByID_table[0][45] = "ErrRateAvg_wk4_2back";
		nbackByID_table[0][46] = "ErrRateAvg_wk5_2back";
		nbackByID_table[0][47] = "ErrRateAvg_wk2_3back";
		nbackByID_table[0][48] = "ErrRateAvg_wk1_3back";
		nbackByID_table[0][49] = "ErrRateAvg_wk2_3back";
		nbackByID_table[0][50] = "ErrRateAvg_wk3_3back";
		nbackByID_table[0][51] = "ErrRateAvg_wk4_3back";
		nbackByID_table[0][52] = "ErrRateAvg_wk5_3back";
		nbackByID_table[0][53] = "ErrRateAvg_wk1_4back";
		nbackByID_table[0][54] = "ErrRateAvg_wk2_4back";
		nbackByID_table[0][55] = "ErrRateAvg_wk3_4back";
		nbackByID_table[0][56] = "ErrRateAvg_wk4_4back";
		nbackByID_table[0][57] = "ErrRateAvg_wk5_4back";
		nbackByID_table[0][58] = "ErrRateAvg_wk1_img";
		nbackByID_table[0][59] = "ErrRateAvg_wk2_img";
		nbackByID_table[0][60] = "ErrRateAvg_wk3_img";
		nbackByID_table[0][61] = "ErrRateAvg_wk4_img";
		nbackByID_table[0][62] = "ErrRateAvg_wk5_img";
		nbackByID_table[0][63] = "ErrRateAvg_wk1_letterNumber";
		nbackByID_table[0][64] = "ErrRateAvg_wk2_letterNumber";
		nbackByID_table[0][65] = "ErrRateAvg_wk3_letterNumber";
		nbackByID_table[0][66] = "ErrRateAvg_wk4_letterNumber";
		nbackByID_table[0][67] = "ErrRateAvg_wk5_letterNumber";
		nbackByID_table[0][68] = "ErrRateAvg_wk1_spatial";
		nbackByID_table[0][69] = "ErrRateAvg_wk2_spatial";
		nbackByID_table[0][70] = "ErrRateAvg_wk3_spatial";
		nbackByID_table[0][71] = "ErrRateAvg_wk4_spatial";
		nbackByID_table[0][72] = "ErrRateAvg_wk5_spatial";
		nbackByID_table[0][73] = "TotalTimeSpent_wk1";
		nbackByID_table[0][74] = "TotalTimeSpent_wk2";
		nbackByID_table[0][75] = "TotalTimeSpent_wk3";
		nbackByID_table[0][76] = "TotalTimeSpent_wk4";
		nbackByID_table[0][77] = "TotalTimeSpent_wk5";
		nbackByID_table[0][78] = "TotalNumGames_wk1_2back";
		nbackByID_table[0][79] = "TotalNumGames_wk2_2back";
		nbackByID_table[0][80] = "TotalNumGames_wk3_2back";
		nbackByID_table[0][81] = "TotalNumGames_wk4_2back";
		nbackByID_table[0][82] = "TotalNumGames_wk5_2back";
		nbackByID_table[0][83] = "TotalNumGames_wk1_3back";
		nbackByID_table[0][84] = "TotalNumGames_wk2_3back";
		nbackByID_table[0][85] = "TotalNumGames_wk3_3back";
		nbackByID_table[0][86] = "TotalNumGames_wk4_3back";
		nbackByID_table[0][87] = "TotalNumGames_wk5_3back";
		nbackByID_table[0][88] = "TotalNumGames_wk1_4back";
		nbackByID_table[0][89] = "TotalNumGames_wk2_4back";
		nbackByID_table[0][90] = "TotalNumGames_wk3_4back";
		nbackByID_table[0][91] = "TotalNumGames_wk4_4back";
		nbackByID_table[0][92] = "TotalNumGames_wk5_4back";
		nbackByID_table[0][93] = "TotalNumGames_wk1_img";
		nbackByID_table[0][94] = "TotalNumGames_wk2_img";
		nbackByID_table[0][95] = "TotalNumGames_wk3_img";
		nbackByID_table[0][96] = "TotalNumGames_wk4_img";
		nbackByID_table[0][97] = "TotalNumGames_wk5_img";
		nbackByID_table[0][98] = "TotalNumGames_wk1_letterNumber";
		nbackByID_table[0][99] = "TotalNumGames_wk2_letterNumber";
		nbackByID_table[0][100] = "TotalNumGames_wk3_letterNumber";
		nbackByID_table[0][101] = "TotalNumGames_wk4_letterNumber";
		nbackByID_table[0][102] = "TotalNumGames_wk5_letterNumber";
		nbackByID_table[0][103] = "TotalNumGames_wk1_spatial";
		nbackByID_table[0][104] = "TotalNumGames_wk2_spatial";
		nbackByID_table[0][105] = "TotalNumGames_wk3_spatial";
		nbackByID_table[0][106] = "TotalNumGames_wk4_spatial";
		nbackByID_table[0][107] = "TotalNumGames_wk5_spatial";
		nbackByID_table[0][108] = "AvgHitRate_Lab1";
		nbackByID_table[0][109] = "AvgHitRate_Lab2";
		nbackByID_table[0][110] = "AvgHitRate_Lab3";
		nbackByID_table[0][111] = "AvgHitRate_Lab4";
		nbackByID_table[0][112] = "AvgHitRate_Lab5"; //ADD ER, and PER TYPE
		nbackByID_table[0][113] = "TotalTime_Lab1"; //Time is recorded in minutes
		nbackByID_table[0][114] = "TotalTime_Lab2";
		nbackByID_table[0][115] = "TotalTime_Lab3";
		nbackByID_table[0][116] = "TotalTime_Lab4";
		nbackByID_table[0][117] = "TotalTime_Lab5";// and PER TYPE
		nbackByID_table[0][118] = "numGamesPlayed_wk1";
		nbackByID_table[0][119] = "numGamesPlayed_wk2";
		nbackByID_table[0][120] = "numGamesPlayed_wk3";
		nbackByID_table[0][121] = "numGamesPlayed_wk4";
		nbackByID_table[0][122] = "numGamesPlayed_wk5";//and PER TYPE, and IN LAB and AT HOME A.P
		nbackByID_table[0][123] = "AvgTimePerGame_Lab_wk1"; //and PER TYPE
		nbackByID_table[0][124] = "AvgTimePerGame_Lab_wk2";
		nbackByID_table[0][125] = "AvgTimePerGame_Lab_wk3";
		nbackByID_table[0][126] = "AvgTimePerGame_Lab_wk4";
		nbackByID_table[0][127] = "AvgTimePerGame_Lab_wk5";
		nbackByID_table[0][128] = "AvgTimePerGame_Home_wk1_2back"; //TOTAL TIME PER WEEK PER EACH TYPE
		nbackByID_table[0][129] = "AvgTimePerGame_Home_wk2_2back";
		nbackByID_table[0][130] = "AvgTimePerGame_Home_wk3_2back";
		nbackByID_table[0][131] = "AvgTimePerGame_Home_wk4_2back";
		nbackByID_table[0][132] = "AvgTimePerGame_Home_wk5_2back";
		nbackByID_table[0][133] = "AvgTimePerGame_Home_wk1_3back";
		nbackByID_table[0][134] = "AvgTimePerGame_Home_wk2_3back";
		nbackByID_table[0][135] = "AvgTimePerGame_Home_wk3_3back";
		nbackByID_table[0][136] = "AvgTimePerGame_Home_wk4_3back";
		nbackByID_table[0][137] = "AvgTimePerGame_Home_wk5_3back";
		nbackByID_table[0][138] = "AvgTimePerGame_Home_wk1_4back";
		nbackByID_table[0][139] = "AvgTimePerGame_Home_wk2_4back";
		nbackByID_table[0][140] = "AvgTimePerGame_Home_wk3_4back";
		nbackByID_table[0][141] = "AvgTimePerGame_Home_wk4_4back";
		nbackByID_table[0][142] = "AvgTimePerGame_Home_wk5_4back";
		nbackByID_table[0][143] = "AvgTimePerGame_Home_wk1_img";
		nbackByID_table[0][144] = "AvgTimePerGame_Home_wk2_img";
		nbackByID_table[0][145] = "AvgTimePerGame_Home_wk3_img";
		nbackByID_table[0][146] = "AvgTimePerGame_Home_wk4_img";
		nbackByID_table[0][147] = "AvgTimePerGame_Home_wk5_img";
		nbackByID_table[0][148] = "AvgTimePerGame_Home_wk1_letterNumber";
		nbackByID_table[0][149] = "AvgTimePerGame_Home_wk2_letterNumber";
		nbackByID_table[0][150] = "AvgTimePerGame_Home_wk3_letterNumber";
		nbackByID_table[0][151] = "AvgTimePerGame_Home_wk4_letterNumber";
		nbackByID_table[0][152] = "AvgTimePerGame_Home_wk5_letterNumber";
		nbackByID_table[0][153] = "AvgTimePerGame_Home_wk1_spatial";
		nbackByID_table[0][154] = "AvgTimePerGame_Home_wk2_spatial";
		nbackByID_table[0][155] = "AvgTimePerGame_Home_wk3_spatial";
		nbackByID_table[0][156] = "AvgTimePerGame_Home_wk4_spatial";
		nbackByID_table[0][157] = "AvgTimePerGame_Home_wk5_spatial";
		nbackByID_table[0][158] = "AvgTimePerGame_Home_wk1";
		nbackByID_table[0][159] = "AvgTimePerGame_Home_wk2";
		nbackByID_table[0][160] = "AvgTimePerGame_Home_wk3";
		nbackByID_table[0][161] = "AvgTimePerGame_Home_wk4";
		nbackByID_table[0][162] = "AvgTimePerGame_Home_wk5";
		nbackByID_table[0][163] = "TotalSessions_wk1"; //number of logins divided by 2
		nbackByID_table[0][164] = "TotalSessions_wk2";
		nbackByID_table[0][165] = "TotalSessions_wk3";
		nbackByID_table[0][166] = "TotalSessions_wk4";
		nbackByID_table[0][167] = "TotalSessions_wk5";
		
		return nbackByID_table;
		
	//=====================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
	
	}
	private static String[][] nbackByDateSheetInit(String[][] nbackByDate_table) {
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
									/*----------------- nbackByDate SHEET ------------------------*/
			
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
			
		//Initializing the titles of the columns
				nbackByDate_table[0][0] = "ID"; //will be many rows with same name
				nbackByDate_table[0][1] = "Date"; //numerical value; convert this column to numbers for calculations
				nbackByDate_table[0][2] = "Is_Lab_Session?"; //checks if date matches with a lab date
				nbackByDate_table[0][3] = "Time_Of_Day_Played"; //Scale 1-4: 1 is 6am-12pm, 2 is 12pm-6pm, 3 is 6pm-12am, 4 is 12am-6am
				nbackByDate_table[0][4] = "AvgHitRate";
				nbackByDate_table[0][5] = "AvgErrRate";
				nbackByDate_table[0][6] = "TotalNumGamesPlayed"; //totalImg+totalLetNum+totalSpatial
				nbackByDate_table[0][7] = "TotalGames_Img";
				nbackByDate_table[0][8] = "TotalGames_letterNumber";
				nbackByDate_table[0][9] = "TotalGames_spatial";
				nbackByDate_table[0][10] = "TotalGames_2back";
				nbackByDate_table[0][11] = "TotalGames_3back";
				nbackByDate_table[0][12] = "TotalGames_4back";
				nbackByDate_table[0][13] = "Total_Time_Spent_Img";
				nbackByDate_table[0][14] = "Total_Time_Spent_letterNumber";
				nbackByDate_table[0][15] = "Total_Time_Spent_spatial";
				nbackByDate_table[0][16] = "Total_Time_Spent_2back";
				nbackByDate_table[0][17] = "Total_Time_Spent_3back";
				nbackByDate_table[0][18] = "Total_Time_Spent_4back";
				nbackByDate_table[0][19] = "Unfinished_Img";
				nbackByDate_table[0][20] = "Unfinished_letterNumber";
				nbackByDate_table[0][21] = "Unfinished_spatial";
				nbackByDate_table[0][22] = "Unfinished_2back";
				nbackByDate_table[0][23] = "Unfinished_3back";
				nbackByDate_table[0][24] = "Unfinished_4back";
				nbackByDate_table[0][25] = "Number_of_Logins"; //
				nbackByDate_table[0][26] = "SessionNumberReached"; //Total logins/2; total sessions that have been completed by this date

				return nbackByDate_table;
				
	}
	private static String[][] sudokuByIDSheetInit(String[][] sudokuByID_table) {
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
									/*----------------- sudokuByID SHEET ------------------------*/
			
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
		
		return sudokuByID_table;
		
}
//	private static String[][] sudokuByIDSheetInit(String[][] sessionDate_table, String[][] sudokuReport_table, 
//			int sudokuReport_col_num, int sudokuReport_row_num, String[][] sudokuByID, Workbook workbook_w) { TEMPLATE
//	
	private static String[][] sudokuByDateSheetInit(String[][] sudokuByDate_table) {
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
									/*----------------- sudokuByDate SHEET ------------------------*/
			
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
			
		return sudokuByDate_table;
	}
	
	public static void SWM_GAME_sessionDates(String[][] SWM_table, int SWM_row_num, int SWM_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
			   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
	{
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		
										/*----------------- SWM SHEET ------------------------*/
		
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		
		//Operations to be done on SWM sheet DELETED A.P
		
		//Organizing the Data
		//Organizing data by ID		
		int c_position = 1; //Current position
		int oTable_row = 1; //Organized table current row
		boolean knownID[] = new boolean[100000]; // It just checks whether a desired operation has been applied to an ID
		String c_ID = new String(); //current ID
		int nextIDPosition;
			
		//reseting both organizedTables
		for(int r = 0 ; r < 100000 ; r++)
		{
			for(int c = 0 ; c < 50 ; c++)
			{
				organizedTable[r][c] = "null";
				organizedTable2[r][c] = "null";
			}
		}
					
		//Just copying the first row with the titles of each column ( ID, round, gameSession, etc...)
		for(int i = 0 ; i < SWM_col_num ; i++)
			organizedTable[0][i] = SWM_table[0][i];
		
		// Initiate all FLY_IDs as having no operation done to them.
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
		
		
		while(c_position < SWM_row_num)
		{
			c_ID = SWM_table[c_position][0].toString();
			for(int position = c_position ; position < SWM_row_num ; position++)
			{
				if(SWM_table[position][0] == c_ID && !(knownID[position]))
				{
					knownID[position] = true;
					for(int col = 0; col < SWM_col_num ; col++)
					{
						organizedTable[oTable_row][col] = SWM_table[position][col];						
					}
					oTable_row++;					
				}
			}
			nextIDPosition = 1;
			while(knownID[nextIDPosition] == true)
			{
				nextIDPosition++;
			}
			c_position = nextIDPosition;
		}
		//Finished organizing by ID (Works)
			
		//Finding the users
	//	System.out.println("\n\nCounting number of users...");
		String[][] SWM_IDs = new String[1000][50]; // Store all the SWM_IDs found and their given information (from the raw data) for SWM
		SWM_IDs[0][0] = organizedTable[1][0]; //Store the first 
		SWM_IDs[0][1] = organizedTable[1][2]; //Store the group on column 1
		SWM_IDs[0][2] = organizedTable[1][1]; //Store the round on column 2
		int[] SWM_userIndex = new int[1000]; //Store the row where a new ID starts, in the organized by ID data table
		int SWM_usersTotal = 0; //Total of users ID found
		SWM_userIndex[0] = 1;
		
	//	System.out.println("\nNew user found: " + SWM_IDs[0][0].toString() + "\nUser data starts on row: " + SWM_userIndex[SWM_usersTotal]);
		
		for(int row = 2 ; row < SWM_row_num ; row++)
		{
			if(!(SWM_IDs[SWM_usersTotal][0].equals(organizedTable[row][0])))
			{
				SWM_usersTotal++;
				SWM_IDs[SWM_usersTotal][0] = organizedTable[row][0].toString();
				SWM_IDs[SWM_usersTotal][1] = organizedTable[row][2];
				SWM_IDs[SWM_usersTotal][2] = organizedTable[row][1];
				SWM_userIndex[SWM_usersTotal] = row;
			//	System.out.println("\nNew user found: " + SWM_IDs[SWM_usersTotal][0].toString() + "\nUser data starts on row: " + SWM_userIndex[SWM_usersTotal]);
			}
		}
		SWM_userIndex[(SWM_usersTotal+1)] = SWM_row_num;
		System.out.println("\nTotal number of users on SWM game: " + (SWM_usersTotal+1));
		//Finished finding users (Works)
				
		//Organizing Table by date
		//System.out.println("\n\nOrganizing table by date...");
		
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
				
		int date_column = 4; // Find the date column later
		
		// Variables to help the execution of the algorithm to organize by date
		int j;
		int aux = 0;
		int row2 = 1;
		int holdIndex = 1; 
		
		
		while(row2 < SWM_row_num && aux <= SWM_usersTotal)
		{
			j = SWM_userIndex[aux];
			while(knownID[j] && j < SWM_userIndex[aux+1])
			{
				j++;
			}
			if( j <= (SWM_userIndex[aux+1]-1))
			{
				if(organizedTable[j][date_column] != null)
				{
					double date = Double.parseDouble(organizedTable[j][date_column]);
					for(int k = SWM_userIndex[aux] ; k < SWM_userIndex[aux+1] ; k++)
					{
						if(!knownID[k])
						{
							if(date >= (Double.parseDouble(organizedTable[k][date_column])))
							{
								date = Double.parseDouble(organizedTable[k][date_column]);
								holdIndex = k;						
							}
						}
					}
					for(int w = 0 ; w < SWM_col_num ; w++)
					{
						organizedTable2[row2][w] = organizedTable[holdIndex][w];
						knownID[holdIndex] = true;
					}
					row2++;
				}
				else
				{
					continue;
				}
			}
			else
				aux++;
		}
		for(int w = 0 ; w < SWM_col_num ; w++)
		{
			organizedTable2[0][w] = organizedTable[0][w];
		}
		//Finished organizing table by date (Works)
		//Finished organizing the data
		
		
		//Averages
		//Per Day
					
		String currentID = new String();
		int currentDay =0; //Holds the value of the current day being analyzed
		
		double correctClicks = 0; // Holds the sum of correctClicks as a "double" variable (can have decimals; not necessarily an integer)
		double percentCorrectClicks = 0; // Holds the sum of the percentage of CorrectClicks
		double duration =0; // Holds the sum of the duration
		//double timeOfDay; // Holds the information whether it's morning, afternoon, evening or night
		
		int[] SWM_day = new int[100000]; // Hold the day ignoring the hour (Vector to create the column of ID/Day)
		String[] SWM_ID_day = new String[100000]; // Hold the ID for that held specific day (Vector to create the column of ID/Day)
		int[] SWM_sessionLabDay = new int[100000]; // Hold the status isLabSessionDay for that held specific day (for the sheet that ignores hours)
		double[] SWM_logSession_day = new double[100000];
		double[] SWM_NICT_sessionNumber_day = new double[100000];
		double SWM_gameSession = 0;
		
		int[] sessionLabDay = new int[100000]; // It checks whether it is a SessionLab day (considering the hour in the day)
		boolean isLabSession = false;
		
		double[] SWM_percentCorrectClicksAvg_column = new double[100000]; // Store the average percentage of CurrentCilcks
		double[] SWM_correctClicksAvg_column = new double[100000]; // Store the average of correct clicks
		double[] SWM_durationAvg_column = new double[100000]; //Store the average of duration

		int[] SWM_occurrences = new int[3];
		
		for( int index = 0 ; index < 3 ; index++)
			SWM_occurrences[index] = 0;
		
		double[] SWM_logSession = new double[100000];
		double[] SWM_NICTSessionNum = new double[100000];
		
		int SWM_totalDays = 1;	//index of the table where the correctClicks variables are stored
		
		
		for(int row = 1 ; row < SWM_row_num ; row++)
		{		
			if(row == 1)
			{	
				currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString()));
				currentID = organizedTable2[row][0].toString(); // Holds the current ID being analyzed
				// Holds the current day being analyzed, and converts the string to a double and then to an integer to represent the current day
				
				//Checking whether current date is a lab session				
				for(int r = 1 ; r < sessionDate_row_num ; r++)
				{
					if(currentID.equals(sessionDate[r][0]))
					{
						for( int c = 2 ; c < (sessionDate_col_num-2) ; c++)
						{
							if(currentDay == sessionDate_asNum[r][c])
							{
								isLabSession = true;
							}
						}
					}
					
				}
				if(isLabSession == true)
				{
					sessionLabDay[row] = 1;
					//System.out.println("Found a lab session day");
				}
				else
					sessionLabDay[row] = 2;
				//Finished checking whether it's lab session
				
				if(!(organizedTable2[row][8].equals("NULL")))
				{
					correctClicks += Double.parseDouble(organizedTable2[row][8].toString());
					SWM_occurrences[0]++;
				}
				if((!(organizedTable2[row][8].equals("NULL"))) && (!(organizedTable2[row][6].equals("NULL"))))
				{
					percentCorrectClicks += (((Double.parseDouble(organizedTable2[row][8]))/(Double.parseDouble(organizedTable2[row][6])))*100);
					SWM_occurrences[1]++;
				}
				if(!(organizedTable2[row][9].equals("NULL")))
				{
					duration += Double.parseDouble(organizedTable2[row][9].toString());
					SWM_occurrences[2]++;
				}
				// the "++" means "add one to occurrence each time"
				SWM_logSession[row] = ((Double.parseDouble(organizedTable2[row][10].toString()))/4); // Calculating the logSession number
				SWM_NICTSessionNum[row] = ((Double.parseDouble(organizedTable2[row][10].toString()))/8); // Calculating the NICT Session number
				//reseting to false to check for the new day
				isLabSession = false;
			}
			else
			{
				
				if(currentID.equals(organizedTable2[row][0]) && ((int)(Double.parseDouble(organizedTable2[row][date_column]))) == currentDay) //Same ID and same day
				{
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						correctClicks += Double.parseDouble(organizedTable2[row][8].toString());
						SWM_occurrences[0]++;
					}
					if((!(organizedTable2[row][8].equals("NULL"))) && (!(organizedTable2[row][6].equals("NULL"))))
					{
						percentCorrectClicks += (((Double.parseDouble(organizedTable2[row][8]))/(Double.parseDouble(organizedTable2[row][6])))*100);
						SWM_occurrences[1]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						duration += Double.parseDouble(organizedTable2[row][9].toString());
						SWM_occurrences[2]++;
					}
					if(SWM_gameSession <= Double.parseDouble(organizedTable2[row][10].toString()))
						SWM_gameSession = Double.parseDouble(organizedTable2[row][10].toString());
				
				}
				else if(currentID.equals(organizedTable2[row][0]) && ((int)(Double.parseDouble(organizedTable2[row][date_column]))) != currentDay) //Same ID but new day
				{
					SWM_ID_day[SWM_totalDays] = organizedTable2[row][0].toString();
					SWM_day[SWM_totalDays] = currentDay;
					SWM_sessionLabDay[SWM_totalDays] = sessionLabDay[row-1];
					
					if(SWM_occurrences[0] != 0){SWM_correctClicksAvg_column[SWM_totalDays] = correctClicks / SWM_occurrences[0];} //average of correct clicks calculated for the day
					else {SWM_correctClicksAvg_column[SWM_totalDays] = -1;}
					
					if(SWM_occurrences[1] != 0){SWM_percentCorrectClicksAvg_column[SWM_totalDays] = percentCorrectClicks / SWM_occurrences[1];} //avg percentage of correct clicks calculated for the day
					else {SWM_percentCorrectClicksAvg_column[SWM_totalDays] = -1;}
					
					if(SWM_occurrences[2] != 0){SWM_durationAvg_column[SWM_totalDays] = duration;} // total duration
					else{SWM_durationAvg_column[SWM_totalDays] = -1;}
					
					SWM_logSession_day[SWM_totalDays] = SWM_gameSession/4; 
					SWM_NICT_sessionNumber_day[SWM_totalDays] = SWM_gameSession/8;
					
					//Reseting variables to 0
					correctClicks = 0; //now resetting the variable to 0
					percentCorrectClicks = 0; //same resetting for percent variable
					duration = 0; // same reseting for duration variable
					for( int index = 0 ; index < 3 ; index++)
						SWM_occurrences[index] = 0;
					//Finished reseting variables
					
					
					SWM_totalDays++; 
					currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString())); //Updating the day value if the date has now changed
					
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						correctClicks += Double.parseDouble(organizedTable2[row][8].toString());
						SWM_occurrences[0]++;
					}
					if((!(organizedTable2[row][8].equals("NULL"))) && (!(organizedTable2[row][6].equals("NULL"))))
					{
						percentCorrectClicks += (((Double.parseDouble(organizedTable2[row][8]))/(Double.parseDouble(organizedTable2[row][6])))*100);
						SWM_occurrences[1]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						duration += Double.parseDouble(organizedTable2[row][9].toString());
						SWM_occurrences[2]++;
					}
					
					SWM_gameSession = Double.parseDouble(organizedTable2[row][10].toString());
					
				}
				else if(!(currentID.equals(organizedTable2[row][0]))) //New ID
				{
					SWM_ID_day[SWM_totalDays] = organizedTable2[row-1][0].toString();
					SWM_day[SWM_totalDays] = currentDay;
					SWM_sessionLabDay[SWM_totalDays] = sessionLabDay[row-1];
					
					if(SWM_occurrences[0] != 0){SWM_correctClicksAvg_column[SWM_totalDays] = correctClicks / SWM_occurrences[0];} //average of correct clicks calculated for the day
					else {SWM_correctClicksAvg_column[SWM_totalDays] = -1;}
					
					if(SWM_occurrences[1] != 0){SWM_percentCorrectClicksAvg_column[SWM_totalDays] = percentCorrectClicks / SWM_occurrences[1];} //avg percentage of correct clicks calculated for the day
					else {SWM_percentCorrectClicksAvg_column[SWM_totalDays] = -1;}
					
					if(SWM_occurrences[2] != 0){SWM_durationAvg_column[SWM_totalDays] = duration;} // total duration
					else{SWM_durationAvg_column[SWM_totalDays] = -1;}
					
					SWM_logSession_day[SWM_totalDays] = SWM_gameSession/4;
					SWM_NICT_sessionNumber_day[SWM_totalDays] = SWM_gameSession/8;
					
					SWM_totalDays++;
					currentID = organizedTable2[row][0].toString();
					currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString())); //Updating the day value
					
					//Reseting variables to 0
					correctClicks = 0; //now resetting the variable to 0
					percentCorrectClicks = 0; //same resetting for percent variable
					duration = 0; // same reseting for duration variable
					for( int index = 0 ; index < 3 ; index++)
						SWM_occurrences[index] = 0;
					//Finished reseting variables
					
					//Updating new values
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						correctClicks += Double.parseDouble(organizedTable2[row][8].toString());
						SWM_occurrences[0]++;
					}
					if((!(organizedTable2[row][8].equals("NULL"))) && (!(organizedTable2[row][6].equals("NULL"))))
					{
						percentCorrectClicks += (((Double.parseDouble(organizedTable2[row][8]))/(Double.parseDouble(organizedTable2[row][6])))*100);
						SWM_occurrences[1]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						duration += Double.parseDouble(organizedTable2[row][9].toString());
						SWM_occurrences[2]++;
					}
					
					SWM_gameSession = Double.parseDouble(organizedTable2[row][10].toString());
					
				}
				else
				{
					System.out.println("error!!");
				}
				
				//Checking whether current date is a lab session
				for(int r = 1 ; r < sessionDate_row_num ; r++)
				{
					if(currentID.equals(sessionDate[r][0]))
					{
						for( int c = 2 ; c < (sessionDate_col_num-1) ; c++)
						{
							if(currentDay == sessionDate_asNum[r][c])
							{
								isLabSession = true;
							}
						}
					}
					
				}
				if(isLabSession == true)
				{
					sessionLabDay[row] = 1;
					//System.out.println("Found a lab session day");
				}
				else
					sessionLabDay[row] = 2;
				//Finished checking whether it's lab session
			
				SWM_logSession[row] = ((Double.parseDouble(organizedTable2[row][10].toString()))/4); // Calculating the logSession number
				SWM_NICTSessionNum[row] = ((Double.parseDouble(organizedTable2[row][10].toString()))/8); // Calculating the NICT Session number
			
				//reseting to false to check for the new day
				isLabSession = false;
			}
			
		}
				
		//Creating final data table
		
		String[][] SWM_finalData = new String[100000][500]; // Table that receives the final data for SWM
		
		SWM_finalData[0][0] = "ID";
		SWM_finalData[0][1] = "Group";
		SWM_finalData[0][2] = "Round";
		SWM_finalData[0][3] = "1stLabDay";
		SWM_finalData[0][4] = "LastDay";
		SWM_finalData[0][5] = "Pre_testDay";
		SWM_finalData[0][6] = "Post_testDay";
		SWM_finalData[0][7] = "1stLabDay_to_LastDay";
		SWM_finalData[0][8] = "LastDay_to_Post_testDay";
		SWM_finalData[0][9] = "Pre-test_to_Post-test";
		SWM_finalData[0][10] = "Pre-test_to_1stLabDay";
		
		SWM_finalData[0][11] = "CorrectClicksAvg_Week1";
		SWM_finalData[0][12] = "CorrectClicksAvg_Week2";
		SWM_finalData[0][13] = "CorrectClicksAvg_Week3";
		SWM_finalData[0][14] = "CorrectClicksAvg_Week4";
		SWM_finalData[0][15] = "CorrectClicksAvg_Week5";
		
		SWM_finalData[0][16] = "PercentCorrectClicksAvg_Week1";
		SWM_finalData[0][17] = "PercentCorrectClicksAvg_Week2";
		SWM_finalData[0][18] = "PercentCorrectClicksAvg_Week3";
		SWM_finalData[0][19] = "PercentCorrectClicksAvg_Week4";
		SWM_finalData[0][20] = "PercentCorrectClicksAvg_Week5";
		
		SWM_finalData[0][21] = "DurationAvg_Week1";
		SWM_finalData[0][22] = "DurationAvg_Week2";
		SWM_finalData[0][23] = "DurationAvg_Week3";
		SWM_finalData[0][24] = "DurationAvg_Week4";
		SWM_finalData[0][25] = "DurationAvg_Week5";
		
		SWM_finalData[0][26] = "CorrectClicksLab1_Avg";
		SWM_finalData[0][27] = "CorrectClicksLab2_Avg";
		SWM_finalData[0][28] = "CorrectClicksLab3_Avg";
		SWM_finalData[0][29] = "CorrectClicksLab4_Avg";
		SWM_finalData[0][30] = "CorrectClicksLab5_Avg";
		
		SWM_finalData[0][31] = "PercentCorrectClicksLab1_Avg";
		SWM_finalData[0][32] = "PercentCorrectClicksLab2_Avg";
		SWM_finalData[0][33] = "PercentCorrectClicksLab3_Avg";
		SWM_finalData[0][34] = "PercentCorrectClicksLab4_Avg";
		SWM_finalData[0][35] = "PercentCorrectClicksLab5_Avg";
		
		SWM_finalData[0][36] = "DurationLab1_Avg";
		SWM_finalData[0][37] = "DurationLab2_Avg";
		SWM_finalData[0][38] = "DurationLab3_Avg";
		SWM_finalData[0][39] = "DurationLab4_Avg";
		SWM_finalData[0][40] = "DurationLab5_Avg";
		
		SWM_finalData[0][41] = "SWM_LogSessionNumber_Week1";
		SWM_finalData[0][42] = "SWM_LogSessionNumber_Week2";
		SWM_finalData[0][43] = "SWM_LogSessionNumber_Week3";
		SWM_finalData[0][44] = "SWM_LogSessionNumber_Week4";
		SWM_finalData[0][45] = "SWM_LogSessionNumber_Week5";
		
		SWM_finalData[0][46] = "SWM_NICT_SessionNumber_Week1";
		SWM_finalData[0][47] = "SWM_NICT_SessionNumber_Week2";
		SWM_finalData[0][48] = "SWM_NICT_SessionNumber_Week3";
		SWM_finalData[0][49] = "SWM_NICT_SessionNumber_Week4";
		SWM_finalData[0][50] = "SWM_NICT_SessionNumber_Week5";
		
		SWM_finalData[0][51] = "LastDayBefore_PostDate";
	
		
		//Finding ID's group, round, 1st lab day, last day, pre_test day, and post_test day (give data) for all SWM IDs
		boolean foundID = false;
		String tempID = new String();

		
		for(int r = 0 ; r <= SWM_usersTotal ; r++)
		{
			tempID = SWM_IDs[r][0].toString();
			//Look for the ID in the session date table

			int r2 = 1; // Holds the row for the ID in sessionDate sheet
			int r5 = 0; // Holds the row where the ID is found on OrganizedTable 2 ( Table organized by ID and by Date)
			int r6 = 0; // Holds the last row for the ID 
			
			foundID = false;
			
			do{
				r5++;
				//if(tempID.equals(organizedTable2[r5][0].toString()))
					//foundID = true;
			}while(!(tempID.equals(organizedTable2[r5][0].toString())) && r5 < SWM_row_num);	
			
			r6 =r5;
			
			while(tempID.equals(organizedTable2[r6][0].toString()) && r6 < SWM_row_num)
			{
				r6++; //r6 stops one row after the last row for the ID ( always use < r6, not <= r6)
				if(r6==SWM_row_num)
					break;
			}
			
			foundID = false;	
			while((!(tempID.equals(sessionDate[r2][0].toString()))) && (r2 < (sessionDate_row_num - 1))) //Searching for the ID in the sessionDate Sheet (Try to improve with a do/while)
			{
				r2++;
			}
			if(r2 < sessionDate_row_num && (r2 != sessionDate_row_num - 1)) //Checking whether the ID was found. (Fix the second condition (it will never get the ID on last row of SessionDateSheet))
			{
				foundID = true;
			}
			else
			{
				System.out.println("ID: " + tempID + " not found on session date sheet.");
			}
			if(foundID)
			{
				SWM_finalData[r+1][0] = tempID.toString(); // current ID
				SWM_finalData[r+1][1] = SWM_IDs[r][1].toString(); // Group
				SWM_finalData[r+1][2] = SWM_IDs[r][2].toString(); // Round
				SWM_finalData[r+1][3] = sessionDate[r2][2].toString(); // First lab day
				SWM_finalData[r+1][5] = sessionDate[r2][1].toString(); // ID Pre_test day
				SWM_finalData[r+1][6] = sessionDate[r2][7].toString(); // ID Post_test day
				
				//Finding the last day for the ID
				foundID = false;
				int firstDayRow=0; //Find the first day row for the ID in the vector SWM_ID_day;
				int lastDayRow=0; // Find the last day row for the ID in the vector SWM_ID_day;
				for(int r3 = 1 ; r3 < SWM_totalDays && !foundID ; r3++)
				{
					if(tempID.equals(SWM_ID_day[r3]))
					{
						foundID = true;
						firstDayRow = r3;
						while(tempID.equals(SWM_ID_day[r3]))
						{
							r3++;
						}
						SWM_finalData[r+1][4] = Integer.toString(SWM_day[r3-1]);
						lastDayRow = (r3-1);
					}
				}
				//Finished finding the last day for the ID
				
				SWM_finalData[r+1][7] = Integer.toString((Integer.parseInt(SWM_finalData[r+1][4].toString()) - Integer.parseInt(SWM_finalData[r+1][3].toString()))); // LastDay - 1stLabDay
				SWM_finalData[r+1][8] = Integer.toString((Integer.parseInt(SWM_finalData[r+1][6].toString()) - Integer.parseInt(SWM_finalData[r+1][4].toString()))); // PostTest - LastDay
				SWM_finalData[r+1][9] = Integer.toString((Integer.parseInt(SWM_finalData[r+1][6].toString()) - Integer.parseInt(SWM_finalData[r+1][5].toString()))); // PostTest - PreTest
				SWM_finalData[r+1][10] = Integer.toString((Integer.parseInt(SWM_finalData[r+1][3].toString()) - Integer.parseInt(SWM_finalData[r+1][5].toString()))); // 1stLabDay - PreTest
				
				
				for(int col = 11 ; col <= 51 ; col++)
				{
					if(col == 51){SWM_finalData[r+1][col] = "";}
					else {SWM_finalData[r+1][col] = "0";}
					// columns 11, 12, 13, 14, 15 / CorrectClicks week1Avg, week2Avg, week3Avg, week4Avg, week5Avg
				}
				
				int r4 = firstDayRow;
				while( (SWM_day[r4] < Integer.parseInt(SWM_finalData[r+1][6])) && r4 <= lastDayRow) //Checking if the last day is bigger than postDate
				{
					r4++;
				}
				if(r4 <= lastDayRow)
				{
					//SWM_finalData[r+1][8] = Integer.toString((Integer.parseInt(SWM_finalData[r+1][6].toString()) - SWM_day[r4-1]));
					SWM_finalData[r+1][51] = Integer.toString(SWM_day[r4]);
				}
				
				
				r4 = firstDayRow;
				int occurrences2 = 0;
				if((SWM_day[r4]) < Integer.parseInt(sessionDate[r2][2].toString())) //First day <  1st Lab session day
				{
					//System.out.println("Error: There is activity before the 1st labSession for the ID " + SWM_finalData[r+1][0].toString());
					while(SWM_day[r4] <= sessionDate_asNum[r2][2] && r4 <= lastDayRow)
						r4++;
				}
				
				if((SWM_day[r4] >= sessionDate_asNum[r2][2]) && (SWM_day[r4] < sessionDate_asNum[r2][3])) // Day >=  1st Lab session and Day < 2nd LabSession (Week1)
				{
					while((SWM_day[r4]) < sessionDate_asNum[r2][3] && r4 < lastDayRow) //While Day < 2nd Lab session date
					{						
						if(SWM_sessionLabDay[r4] == 1)
						{
							SWM_finalData[r+1][26] = Double.toString(SWM_correctClicksAvg_column[r4]); // CorrectClicks 1stLabAvg 
							SWM_finalData[r+1][31] = Double.toString(SWM_percentCorrectClicksAvg_column[r4]); // PercCorrectClicks 1stLabAvg
							SWM_finalData[r+1][36] = Double.toString(SWM_durationAvg_column[r4]); // Duration 1stLabAvg
						}
						r4++;
					}
				}
			
							
				if((SWM_day[r4] >= sessionDate_asNum[r2][3]) && (SWM_day[r4] < sessionDate_asNum[r2][4])) // Day >=  2nd Lab session and Day < 3rd LabSession (Week2)
				{
					while( SWM_day[r4] < sessionDate_asNum[r2][4] && r4 < lastDayRow) //While Day < 3rd Lab session date
					{
						if(SWM_sessionLabDay[r4] == 1)
						{
							SWM_finalData[r+1][27] = Double.toString(SWM_correctClicksAvg_column[r4]); // CorrectClicks 2ndLabAvg
							SWM_finalData[r+1][32] = Double.toString(SWM_percentCorrectClicksAvg_column[r4]); // PercCorrectClicks 2ndLabAvg
							SWM_finalData[r+1][37] = Double.toString(SWM_durationAvg_column[r4]); // PercCorrectClicks 2ndLabAvg
						}
						
						r4++;
					}
				}
								
				if((SWM_day[r4] >= sessionDate_asNum[r2][4]) && (SWM_day[r4] < sessionDate_asNum[r2][5])) // Day >=  3rd Lab session and Day < 4th LabSession (Week3)
				{
					while(SWM_day[r4] < sessionDate_asNum[r2][5] && r4 < lastDayRow) //While Day < 4th Lab session date
					{
						if(SWM_sessionLabDay[r4] == 1)
						{
							SWM_finalData[r+1][28] = Double.toString(SWM_correctClicksAvg_column[r4]); // CorrectClicks 3rdLabAvg
							SWM_finalData[r+1][33] = Double.toString(SWM_percentCorrectClicksAvg_column[r4]); // PercCorrectClicks 3rdLabAvg
							SWM_finalData[r+1][38] = Double.toString(SWM_durationAvg_column[r4]);//Duration 3rdLabAvg
						}
						r4++;
					}
				}
								
				
				if((SWM_day[r4] >= sessionDate_asNum[r2][5]) && (SWM_day[r4] < sessionDate_asNum[r2][6])) // Day >=  4th Lab session and Day < 5th LabSession (Week4)
				{
					while(SWM_day[r4] < sessionDate_asNum[r2][6] && r4 < lastDayRow ) //While Day < 5th Lab session date
					{
						if(SWM_sessionLabDay[r4] == 1)
						{
							SWM_finalData[r+1][29] = Double.toString(SWM_correctClicksAvg_column[r4]); // CorrectClicks 4th LabAvg
							SWM_finalData[r+1][34] = Double.toString(SWM_percentCorrectClicksAvg_column[r4]); // PercCorrectClicks 4th LabAvg
							SWM_finalData[r+1][39] = Double.toString(SWM_durationAvg_column[r4]);//Duration 4th LabAvg
						}
						r4++;
					}
				}
								
				if((SWM_day[r4] >= sessionDate_asNum[r2][6]) && (SWM_day[r4] <= sessionDate_asNum[r2][7])) // Day >=  5th Lab session and Day <= postDate (Week5)
				{
					while(SWM_day[r4] < sessionDate_asNum[r2][7] && r4 < lastDayRow) //While Day < postdate
					{
						if(SWM_sessionLabDay[r4] == 1)
						{
							SWM_finalData[r+1][30] = Double.toString(SWM_correctClicksAvg_column[r4]); // CorrectClicks 5th LabAvg
							SWM_finalData[r+1][35] = Double.toString(SWM_percentCorrectClicksAvg_column[r4]); // PercCorrectClicks 5th LabAvg
							SWM_finalData[r+1][40] = Double.toString(SWM_durationAvg_column[r4]); //Duration 5th LabAvg
							//r4++;
						}							
						r4++;
					}
				}
				
				
				//////---------------------------------------------------------------------------------------------------------------------------------------/////
				//Average from raw data
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString())))) < Integer.parseInt(sessionDate[r2][2].toString())) //First day <  1st Lab session
				{
					System.out.println("Error: There is activity before the 1st labSession for the ID " + SWM_finalData[r+1][0].toString());
					while((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString())))) <= sessionDate_asNum[r2][2] && r5 < r6)
					{
						r5++;
						if(r5 == r6)
							break;
					}
				}
				
				// Day >=  1st Lab session and Day < 2nd LabSession (Week1)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][2]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][3])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][2]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+7))) 
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][3] && r5 < r6) //While Day < 2nd Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+7) && r5 < r6) 
					{
						
						SWM_finalData[r+1][11] = Double.toString((Double.parseDouble(SWM_finalData[r+1][11].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))) ; //CorrectClicks
						SWM_finalData[r+1][16] = Double.toString((Double.parseDouble(SWM_finalData[r+1][16].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))/(Double.parseDouble(organizedTable2[r5][6].toString()))); //Percent of CorrectClicks
						SWM_finalData[r+1][21] = Double.toString((Double.parseDouble(SWM_finalData[r+1][21].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Duration
						
						//Highest Value
						if((Double.parseDouble(SWM_finalData[r+1][41].toString())) <= (Double.parseDouble(organizedTable2[r5][10].toString())))
							SWM_finalData[r+1][41] = organizedTable2[r5][10].toString(); //GameSession
						
						r5++;
						occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					if(occurrences2 != 0)
					{
						SWM_finalData[r+1][11] = Double.toString((Double.parseDouble(SWM_finalData[r+1][11].toString())/occurrences2)); //Week1 SWM_CorrectClicksAvg
						SWM_finalData[r+1][16] = Double.toString((Double.parseDouble(SWM_finalData[r+1][16].toString())/occurrences2)); //Week1 SWM_PerctCorrectClicksAvg
						SWM_finalData[r+1][21] = Double.toString((Double.parseDouble(SWM_finalData[r+1][21].toString()))); //Week1 SWM_DurationAvg
							
						SWM_finalData[r+1][41] = Double.toString((Double.parseDouble(SWM_finalData[r+1][41].toString()))/4); //Week1 SWM_LogSessionNumber
						SWM_finalData[r+1][46] = Double.toString((Double.parseDouble(SWM_finalData[r+1][41].toString()))/2); //Week1 SWM_NICT_SessionNumber
						
					}		
					
					occurrences2 = 0;
				}
				else
				{
					System.out.println("There is no session between 1st labSession and 2nd LabSession (Week 1) for the ID " + SWM_finalData[r+1][0].toString());
				}
				
				// Day >=  2nd Lab session and Day < 3rd LabSession (Week2)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][3]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][4])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+7)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+14))) 	
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][4] && r5 < r6) //While Day < 3rd Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+14) && r5 < r6) 	
					{
						
						SWM_finalData[r+1][12] = Double.toString((Double.parseDouble(SWM_finalData[r+1][12].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))) ; //CorrectClicks
						SWM_finalData[r+1][17] = Double.toString((Double.parseDouble(SWM_finalData[r+1][17].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))/(Double.parseDouble(organizedTable2[r5][6].toString()))); //Percent of CorrectClicks
						SWM_finalData[r+1][22] = Double.toString((Double.parseDouble(SWM_finalData[r+1][22].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Duration
						
						//Highest Value
						if((Double.parseDouble(SWM_finalData[r+1][42].toString())) <= (Double.parseDouble(organizedTable2[r5][10].toString())))
							SWM_finalData[r+1][42] = organizedTable2[r5][10].toString(); // GameSession
						
						r5++;
						occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					if(occurrences2 != 0)
					{
						SWM_finalData[r+1][12] = Double.toString((Double.parseDouble(SWM_finalData[r+1][12].toString())/occurrences2)); //Week2 SWM_CorrectClicksAvg
						SWM_finalData[r+1][17] = Double.toString((Double.parseDouble(SWM_finalData[r+1][17].toString())/occurrences2)); //Week2 SWM_PerctCorrectClicksAvg
						SWM_finalData[r+1][22] = Double.toString((Double.parseDouble(SWM_finalData[r+1][22].toString()))); //Week2 SWM_DurationAvg
						
						SWM_finalData[r+1][42] = Double.toString((Double.parseDouble(SWM_finalData[r+1][42].toString()))/4); //Week2 FLY_LogSessionNumber
						SWM_finalData[r+1][47] = Double.toString((Double.parseDouble(SWM_finalData[r+1][42].toString()))/2); //Week2 FLY_NICT_SessionNumber
						
					}
					occurrences2 = 0;
				}
				else
				{
					System.out.println("There is no session between 2nd labSession and 3rd LabSession (Week 2) for the ID " + SWM_finalData[r+1][0].toString());
				}
				
				// Day >=  3rd Lab session and Day < 4th LabSession (Week3)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][4]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][5])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+14)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+21)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][5] && r5 < r6) //While Day < 4th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+21) && r5 < r6) 
					{
						
						SWM_finalData[r+1][13] = Double.toString((Double.parseDouble(SWM_finalData[r+1][13].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))) ; //CorrectClicks
						SWM_finalData[r+1][18] = Double.toString((Double.parseDouble(SWM_finalData[r+1][18].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))/(Double.parseDouble(organizedTable2[r5][6].toString()))); //Percent of CorrectClicks
						SWM_finalData[r+1][23] = Double.toString((Double.parseDouble(SWM_finalData[r+1][23].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Duration
						
						//Highest Value
						if((Double.parseDouble(SWM_finalData[r+1][43].toString())) <= (Double.parseDouble(organizedTable2[r5][10].toString())))
							SWM_finalData[r+1][43] = organizedTable2[r5][10].toString(); // GameSession
						
						r5++;
						occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					if(occurrences2 != 0)
					{
						SWM_finalData[r+1][13] = Double.toString((Double.parseDouble(SWM_finalData[r+1][13].toString())/occurrences2)); //Week3 SWM_CorrectClicksAvg
						SWM_finalData[r+1][18] = Double.toString((Double.parseDouble(SWM_finalData[r+1][18].toString())/occurrences2)); //Week3 SWM_PerctCorrectClicksAvg
						SWM_finalData[r+1][23] = Double.toString((Double.parseDouble(SWM_finalData[r+1][23].toString()))); //Week3 SWM_DurationAvg
						
						SWM_finalData[r+1][43] = Double.toString((Double.parseDouble(SWM_finalData[r+1][43].toString()))/4); //Week3 FLY_LogSessionNumber
						SWM_finalData[r+1][48] = Double.toString((Double.parseDouble(SWM_finalData[r+1][43].toString()))/2); //Week3 FLY_NICT_SessioNumber
					}
					occurrences2 = 0;
				}
				else
				{
					System.out.println("There is no session between 3rd labSession and 4th LabSession (Week 3) for the ID " + SWM_finalData[r+1][0].toString());
				}
				
				// Day >=  4th Lab session and Day < 5th LabSession (Week4)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][5]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][6])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+21)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+28)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][6] && r5 < r6) //While Day < 5th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+28) && r5 < r6) 
					{
						
						SWM_finalData[r+1][14] = Double.toString((Double.parseDouble(SWM_finalData[r+1][14].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))) ; //CorrectClicks
						SWM_finalData[r+1][19] = Double.toString((Double.parseDouble(SWM_finalData[r+1][19].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))/(Double.parseDouble(organizedTable2[r5][6].toString()))); //Percent of CorrectClicks
						SWM_finalData[r+1][24] = Double.toString((Double.parseDouble(SWM_finalData[r+1][24].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Duration
						
						//Highest Value
						if((Double.parseDouble(SWM_finalData[r+1][44].toString())) <= (Double.parseDouble(organizedTable2[r5][10].toString())))
							SWM_finalData[r+1][44] = organizedTable2[r5][10].toString(); // GameSession
						
						r5++;
						occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					if(occurrences2 != 0)
					{
						SWM_finalData[r+1][14] = Double.toString((Double.parseDouble(SWM_finalData[r+1][14].toString())/occurrences2)); //Week3 SWM_CorrectClicksAvg
						SWM_finalData[r+1][19] = Double.toString((Double.parseDouble(SWM_finalData[r+1][19].toString())/occurrences2)); //Week3 SWM_PerctCorrectClicksAvg
						SWM_finalData[r+1][24] = Double.toString((Double.parseDouble(SWM_finalData[r+1][24].toString()))); //Week3 SWM_DurationAvg
						
						SWM_finalData[r+1][44] = Double.toString((Double.parseDouble(SWM_finalData[r+1][44].toString()))/4); //Week4 FLY_LogSessionNumber
						SWM_finalData[r+1][49] = Double.toString((Double.parseDouble(SWM_finalData[r+1][44].toString()))/2); //Week4 FLY_NICT_SessionNumber
					}
					occurrences2 = 0;
				}
				else
				{
					System.out.println("There is no session between 4th labSession and 5th LabSession (Week 4) for the ID " + SWM_finalData[r+1][0].toString());
				}
				
				// Day >=  5th Lab session and Day < PostDate (Week5)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][6]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][7])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+28)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+35)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][7] && r5 < r6) //While Day < 5th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+35) && r5 < r6) 	
					{
						
						SWM_finalData[r+1][15] = Double.toString((Double.parseDouble(SWM_finalData[r+1][15].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))) ; //CorrectClicks
						SWM_finalData[r+1][20] = Double.toString((Double.parseDouble(SWM_finalData[r+1][20].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))/(Double.parseDouble(organizedTable2[r5][6].toString()))); //Percent of CorrectClicks
						SWM_finalData[r+1][25] = Double.toString((Double.parseDouble(SWM_finalData[r+1][25].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Duration
						
						//Highest Value
						if((Double.parseDouble(SWM_finalData[r+1][45].toString())) <= (Double.parseDouble(organizedTable2[r5][10].toString())))
							SWM_finalData[r+1][45] = organizedTable2[r5][10].toString(); // GameSession
						
						r5++;
						occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					if(occurrences2 != 0)
					{
						SWM_finalData[r+1][15] = Double.toString((Double.parseDouble(SWM_finalData[r+1][15].toString())/occurrences2)); //Week5 SWM_CorrectClicksAvg
						SWM_finalData[r+1][20] = Double.toString((Double.parseDouble(SWM_finalData[r+1][20].toString())/occurrences2)); //Week5 SWM_PerctCorrectClicksAvg
						SWM_finalData[r+1][25] = Double.toString((Double.parseDouble(SWM_finalData[r+1][25].toString()))); //Week5 SWM_DurationAvg
						
						SWM_finalData[r+1][45] = Double.toString((Double.parseDouble(SWM_finalData[r+1][45].toString()))/4); //Week5 FLY_LogSessionNumber
						SWM_finalData[r+1][50] = Double.toString((Double.parseDouble(SWM_finalData[r+1][45].toString()))/2); //Week5 FLY_NICT_SessionNumber
					}
					occurrences2 = 0;
				}
				else
				{
					System.out.println("There is no session between 5th labSession and PostDate (Week 5) for the ID " + SWM_finalData[r+1][0].toString());
				}					
			} // If found ID
			
		}
		
		
		System.out.println("\n");
	/*(int a = 1 ; a < SWM_totalDays ; a++)
		{
			System.out.println(SWM_ID_day[a].toString() + "\t" + SWM_day[a] + "\t" + SWM_sessionLabDay[a] + "\t" + SWM_correctClicksAvg_column[a] + "\t" + SWM_percentCorrectClicksAvg_column[a] + "\t" + SWM_durationAvg_column[a] );
		}*/
		
		//Writing the data to be stored on an excel file; at the end, will output the entire file with all the new variables that we have calculated
		
		
		
		Sheet SWM_AVG = workbook_w.createSheet("SWM_AVG_ByID");
		
		Cell cell_w;
		
		for(int r = 0 ; r < SWM_usersTotal ; r++)
		{
			Row row = SWM_AVG.createRow(r);
			for(int c = 0 ; c < 52 ; c++)
			{
				if(r == 0)
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(SWM_finalData[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					if(SWM_finalData[r][c].equals(""))
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue("");
					}	
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(SWM_finalData[r][c].toString()));
					}
				}
			}
		}
		
		Sheet SWM_AVG2 = workbook_w.createSheet("SWM_AVG_ByDate");
		
		Row row_w = SWM_AVG2.createRow(0); //Row
		
		cell_w = row_w.createCell(0);
		
		cell_w = row_w.createCell(0);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("ID");
		
		cell_w = row_w.createCell(1);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("Day");
		
		cell_w = row_w.createCell(2);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("IsSessionLab");
		
		cell_w = row_w.createCell(3);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("CorrectClicks_AVG");
		
		cell_w = row_w.createCell(4);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("PercentCorrectClicks_AVG");
		
		cell_w = row_w.createCell(5);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("Duration_AVG");
		
		cell_w = row_w.createCell(6);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("LogSessionNumber");
		
		cell_w = row_w.createCell(7);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("NICT_SessionNumber");
		
		
		for(int r = 1 ; r < SWM_totalDays ; r++)
		{
			Row row = SWM_AVG2.createRow(r);
			
			cell_w = row.createCell(0);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(Double.parseDouble(SWM_ID_day[r].toString()));
			
			cell_w = row.createCell(1);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(SWM_day[r]);
			
			cell_w = row.createCell(2);
			if(SWM_sessionLabDay[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(SWM_sessionLabDay[r]);
			}
			
			cell_w = row.createCell(3);
			if(SWM_correctClicksAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(SWM_correctClicksAvg_column[r]);
			}
			
			cell_w = row.createCell(4);
			if(SWM_percentCorrectClicksAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(SWM_percentCorrectClicksAvg_column[r]);
			}
			
			cell_w = row.createCell(5);
			if(SWM_durationAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(SWM_durationAvg_column[r]);
			}
			
			cell_w = row.createCell(6);
			if(SWM_logSession_day[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(SWM_logSession_day[r]);
			}
			
			cell_w = row.createCell(7);
			if(SWM_NICT_sessionNumber_day[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(SWM_NICT_sessionNumber_day[r]);
			}
			
		}
		
		//Sheet with IDs and Days
		Sheet SWM_AVG3 = workbook_w.createSheet("SWM_Daily_Progress");
		
		row_w = SWM_AVG3.createRow(0);
			
		for(int _cell = 0 ; _cell < 100 ; _cell++)
		{
			if(_cell == 0)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("ID");
			}
			if(_cell == 1)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("Variable");
			}
			if(_cell > 1)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(("Day_"+ Integer.toString((_cell - 1))));
			}
		}
		
		int rows = 1; // Rows on the third sheet	
		int ID_totalDays = 1; // Store the total days for an specific ID
		int index = 1; // 
		int user_row_onSessionDateSheet;
		int[] day = new int[7];
		for(int k = 0 ;  k < 7 ; k++)
		{
			day[k] = 1;
		}
		
		for(int user = 0 ; user <= SWM_usersTotal ; user++)
		{
			c_ID = SWM_ID_day[index];
			rows++;
			ID_totalDays = 0;
			while(c_ID.equals(SWM_ID_day[index].toString()))
			{
				ID_totalDays++;
				index++;
				if(index == SWM_totalDays)
					break;
			}
			for(user_row_onSessionDateSheet = 1 ;  user_row_onSessionDateSheet < sessionDate_row_num ; user_row_onSessionDateSheet++)
			{
				if(c_ID.equals(sessionDate[user_row_onSessionDateSheet][0]))
					break;
			}
			for(int r = 1 ; r < 8 ; r++)
			{
				Row row = SWM_AVG3.createRow(rows);
				for(int c = 0 ; c < (ID_totalDays + 2) ;  c++)
				{
					if(c == 0)
					{
						cell_w = row.createCell(c);
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(c_ID.toString()));
					}
					if(c == 1)
					{
						if(r == 1)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("IsLabSession");
						}
						if(r == 2)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("correctClicks_Avg");
						}
						if(r == 3)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("percentCorrectClicks_Avg");
						}
						if(r == 4)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("duration");
						}
						if(r == 5)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("logSessionNumber");
						}
						if(r == 6)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("NICT_sessionNumber");
						}
						if(r == 7)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("PostStudy");
						}
					}
					if(c > 1)
					{
						if(r == 1)
						{
							cell_w = row.createCell(c);
							if(SWM_sessionLabDay[day[0]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(SWM_sessionLabDay[day[0]]);
							}
							day[0]++;
						}
						if(r == 2)
						{
							cell_w = row.createCell(c);
							if(SWM_correctClicksAvg_column[day[1]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(SWM_correctClicksAvg_column[day[1]]);
							}
							day[1]++;
						}
						if(r == 3)
						{
							cell_w = row.createCell(c);
							if(SWM_percentCorrectClicksAvg_column[day[2]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(SWM_percentCorrectClicksAvg_column[day[2]]);
							}
							day[2]++;
						}
						if(r == 4)
						{
							cell_w = row.createCell(c);
							if(SWM_durationAvg_column[day[3]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(SWM_durationAvg_column[day[3]]);
							}
							day[3]++;
						}
						if(r == 5)
						{
							cell_w = row.createCell(c);
							if(SWM_logSession_day[day[4]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(SWM_logSession_day[day[4]]);
							}
							day[4]++;
						}
						if(r == 6)
						{
							cell_w = row.createCell(c);
							if(SWM_NICT_sessionNumber_day[day[5]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(SWM_NICT_sessionNumber_day[day[5]]);
							}
							day[5]++;
						}
						if(r == 7)
						{
							cell_w = row.createCell(c);
							if(SWM_day[day[6]] < sessionDate_asNum[user_row_onSessionDateSheet][7])
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("NICT");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("PostStudy");
							}
							day[6]++;
						}
					}
					
				}
				rows++;
			}			
		}		
		//Finished SWM
	}//SWM_GAME()
	
   public static void FLY_GAME_sessionDates(String[][] FLY_table, int FLY_row_num, int FLY_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
		   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
   {
	   
		//======================================================================================================================//
		//======================================================================================================================//		
		//======================================================================================================================//
		//======================================================================================================================//
		
							/* ----------- FLY SHEET ------------ */
		
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		
		
		
		//Operations to be done on FLY sheet
		
		//Organizing the Data
		//Organizing data by ID		
		int c_position = 1; //Current position
		int oTable_row = 1; //Organized table current row
		boolean knownID[] = new boolean[100000]; // It just checks whether a desired operation has been applied to an ID
		String c_ID = new String(); //current ID
		int nextIDPosition;
		
		//reseting both organizedTables
		for(int r = 0 ; r < 100000 ; r++)
		{
			for(int c = 0 ; c < 50 ; c++)
			{
				organizedTable[r][c] = "null";
				organizedTable2[r][c] = "null";
			}
		}
			
		//Just copying the first row with the titles of each column ( ID, round, gameSession, etc...)
		for(int i = 0 ; i < FLY_col_num ; i++)
			organizedTable[0][i] = FLY_table[0][i];
		
		// Initiate all FLY_IDs as having no operation done to them.
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
		
		while(c_position < FLY_row_num)
		{
			c_ID = FLY_table[c_position][0].toString();
			for(int position = c_position ; position < FLY_row_num ; position++)
			{
				if(FLY_table[position][0] == c_ID && !(knownID[position]))
				{
					knownID[position] = true;
					for(int col = 0; col < FLY_col_num ; col++)
					{
						organizedTable[oTable_row][col] = FLY_table[position][col];						
					}
					oTable_row++;					
				}
			}
			nextIDPosition = 1;
			while(knownID[nextIDPosition] == true)
			{
				nextIDPosition++;
			}
			c_position = nextIDPosition;
		}
		//Finished organizing by ID (Works)
			
		//Finding the users
	//	System.out.println("\n\nCounting number of users...");
		String[][] FLY_IDs = new String[1000][50]; // Store all the FLY_IDs found and their given information (from the raw data) for FLY
		FLY_IDs[0][0] = organizedTable[1][0]; //Store the first 
		FLY_IDs[0][1] = organizedTable[1][2]; //Store the group on column 1
		FLY_IDs[0][2] = organizedTable[1][1]; //Store the round on column 2
		int[] FLY_userIndex = new int[1000]; //Store the row where a new ID starts, in the organized by ID data table
		int FLY_usersTotal = 0; //Total of users ID found
		FLY_userIndex[0] = 1;
		
	//	System.out.println("\nNew user found: " + FLY_IDs[0][0].toString() + "\nUser data starts on row: " + FLY_userIndex[FLY_usersTotal]);
		
		for(int row = 2 ; row < FLY_row_num ; row++)
		{
			if(!(FLY_IDs[FLY_usersTotal][0].equals(organizedTable[row][0])))
			{
				FLY_usersTotal++;
				FLY_IDs[FLY_usersTotal][0] = organizedTable[row][0].toString();
				FLY_IDs[FLY_usersTotal][1] = organizedTable[row][2];
				FLY_IDs[FLY_usersTotal][2] = organizedTable[row][1];
				FLY_userIndex[FLY_usersTotal] = row;
			//	System.out.println("\nNew user found: " + FLY_IDs[FLY_usersTotal][0].toString() + "\nUser data starts on row: " + FLY_userIndex[FLY_usersTotal]);
			}
		}
		
		FLY_userIndex[(FLY_usersTotal+1)] = FLY_row_num;
		System.out.println("\nTotal number of users on FLY game: " + (FLY_usersTotal+1));
		//Finished finding users (Works)
				
		//Organizing Table by date
		//System.out.println("\n\nOrganizing table by date...");
		
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
				
		int date_column = 13; // date column in the fly sheet
		
		// Variables to help the execution of the algorithm to organize by date
		int j;
		int aux = 0;
		int row2 = 1;
		int holdIndex = 1; 
		
		
		while(row2 < FLY_row_num && aux <= FLY_usersTotal)
		{
			j = FLY_userIndex[aux];
			while(knownID[j] && j < FLY_userIndex[aux+1])
			{
				j++;
			}
			if( j <= (FLY_userIndex[aux+1]-1))
			{
				if(organizedTable[j][date_column] != null)
				{
					double date = Double.parseDouble(organizedTable[j][date_column]);
					for(int k = FLY_userIndex[aux] ; k < FLY_userIndex[aux+1] ; k++)
					{
						if(!knownID[k])
						{
							if(date >= (Double.parseDouble(organizedTable[k][date_column])))
							{
								date = Double.parseDouble(organizedTable[k][date_column]);
								holdIndex = k;						
							}
						}
					}
					for(int w = 0 ; w < FLY_col_num ; w++)
					{
						organizedTable2[row2][w] = organizedTable[holdIndex][w];
						knownID[holdIndex] = true;
					}
					row2++;
				}
				else
				{
					continue;
				}
			}
			else
				aux++;
		}
		for(int w = 0 ; w < FLY_col_num ; w++)
		{
			organizedTable2[0][w] = organizedTable[0][w];
		}
		//Finished organizing table by date (Works)
		//Finished organizing the data
		
		
		//Averages
		//Per Day
					
		String FLY_currentID = new String();
		int FLY_currentDay =0; //Holds the value of the current day being analyzed
		
		double FLY_hitRate = 0; // Holds the sum of FLY_hitRate as a "double" variable (can have decimals; not necessarily an integer)
		double FLY_missRate = 0; // Holds the sum of the percentage of CorrectClicks
		double FLY_totalPlayed = 0; // Holds the sum of the TotalPlayed
		double FLY_totalGraduated = 0; // Holds the sum of Graduated
		double FLY_falsePositiveOne =0; // Holds the sum of falsePositiveOne
		double FLY_falsePositiveTwo =0;
		double FLY_falsePositiveThree =0;
		double FLY_falsePositiveFour =0;
		double FLY_trialsToLevelUp =0;
		double FLY_speedAtLevelUp =0;
		double FLY_timeOnTask1 = 0;
		double FLY_timeOnTask2 = 0;
		double FLY_highestLevel = 0;
		double FLY_gameSession = 0;
		
		
		//double timeOfDay; // Holds the information whether it's morning, afternoon, evening or night
		
		int[] FLY_day = new int[100000]; // Hold the day ignoring the hour (Vector to create the column of ID/Day)
		String[] FLY_ID_day = new String[100000]; // Hold the ID for that held specific day (Vector to create the column of ID/Day)
		int[] FLY_sessionLabDay = new int[100000]; // Hold the status isLabSessionDay for that held specific day (for the sheet that ignores hours)
		
		
		//double[] FLY_gameSession_day = new double[100000]; //Holds the gameSession# per day
		double[] FLY_logSession_day = new double[100000]; //Holds the logSession# per day
		double[] FLY_NICT_sessionNumber_day = new double [100000]; //Holds the NICT_Session# per day
		
		
		int[] FsessionLabDay = new int[100000]; // It checks whether it is a SessionLab day (considering the hour in the day)
		boolean FLY_isLabSession = false;
		
		double[] FLY_missRateAvg_column = new double[100000]; // Store the average percentage of CurrentCilcks
		double[] FLY_hitRateAvg_column = new double[100000]; // Store the average of hit rate
		double[] FLY_totalPlayedAvg_column = new double[100000]; //Store the average of Total Played
		double[] FLY_totalGraduatedAvg_column = new double[100000]; //Store the total Graduated
		double[] FLY_falsePositiveOneAvg_column = new double[100000]; //Store the average of falsePositiveOne
		double[] FLY_falsePositiveTwoAvg_column = new double[100000]; //Store the average of falsePositiveTwo
		double[] FLY_falsePositiveThreeAvg_column = new double[100000]; //Store the average of falsePositiveThree
		double[] FLY_falsePositiveFourAvg_column = new double[100000]; //Store the average of falsePositiveFour
		double[] FLY_trialsToLevelUpAvg_column = new double[100000]; //Store the average of trialsToLevelUp
		double[] FLY_speedAtLevelUpAvg_column = new double[100000]; //Store the average of speedAtLevelUp
		double[] FLY_timeOnTask1Avg_column = new double[100000]; //Store the average of trialsToLevelUp
		double[] FLY_timeOnTask2Avg_column = new double[100000]; //Store the average of speedAtLevelUp
		double[] FLY_levelAvg_column = new double[100000]; //Store the highest Level

		int[] FLY_occurrences = new int[10];
		
		for( int index = 0 ; index < 10 ; index++)
			FLY_occurrences[index] = 0;
		
		int FLY_totalDays = 1;	//index of the table where the FLY_hitRate variables are stored
		
		for(int row = 1 ; row < FLY_row_num ; row++)
		{	
			if(row == 1)
			{	
				FLY_currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString()));
				FLY_currentID = organizedTable2[row][0].toString(); // Holds the current ID being analyzed
				// Holds the current day being analyzed, and converts the string to a double and then to an integer to represent the current day
				
				//Checking whether current date is a lab session				
				for(int r = 1 ; r < sessionDate_row_num ; r++)
				{
					if(FLY_currentID.equals(sessionDate[r][0]))
					{
						for( int c = 2 ; c < (sessionDate_col_num-2) ; c++)
						{
							if(FLY_currentDay == sessionDate_asNum[r][c])
							{
								FLY_isLabSession = true;
							}
						}
					}
					
				}
				if(FLY_isLabSession == true)
				{
					FsessionLabDay[row] = 1;
					//System.out.println("Found a lab session day");
				}
				else
					FsessionLabDay[row] = 2;
				//Finished checking whether it's lab session
				
				if(!(organizedTable2[row][4].equals("NULL")))
				{
					FLY_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
					FLY_occurrences[0]++;
				}
				if(!(organizedTable2[row][5].equals("NULL")))
				{
					FLY_missRate += Double.parseDouble(organizedTable2[row][5].toString());
					FLY_occurrences[1]++;
				}
				if(!(organizedTable2[row][6].equals("NULL")))
				{
					FLY_falsePositiveOne += Double.parseDouble(organizedTable2[row][6].toString());
					FLY_occurrences[2]++;
				}
				if(!(organizedTable2[row][7].equals("NULL")))
				{
					FLY_falsePositiveTwo += Double.parseDouble(organizedTable2[row][7].toString());
					FLY_occurrences[3]++;
				}
				if(!(organizedTable2[row][8].equals("NULL")))
				{
					FLY_falsePositiveThree += Double.parseDouble(organizedTable2[row][8].toString());
					FLY_occurrences[4]++;
				}
				if(!(organizedTable2[row][9].equals("NULL")))
				{
					FLY_falsePositiveFour += Double.parseDouble(organizedTable2[row][9].toString());
					FLY_occurrences[5]++;
				}
				if(!(organizedTable2[row][11].equals("NULL")))
				{
					FLY_trialsToLevelUp += Double.parseDouble(organizedTable2[row][11].toString());
					FLY_occurrences[6]++;
				}
				if(!(organizedTable2[row][12].equals("NULL")))
				{
					FLY_speedAtLevelUp += Double.parseDouble(organizedTable2[row][12].toString());
					FLY_occurrences[7]++;
				}
				if(!(organizedTable2[row][15].equals("NULL")))
				{
					FLY_timeOnTask1 += Double.parseDouble(organizedTable2[row][15].toString());
					FLY_occurrences[8]++;
				}
				if(!(organizedTable2[row][16].equals("NULL")))
				{
					FLY_timeOnTask2 += Double.parseDouble(organizedTable2[row][16].toString());
					FLY_occurrences[9]++;
				}
				
				FLY_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
				FLY_gameSession = Double.parseDouble(organizedTable2[row][17].toString());
				FLY_totalPlayed += Double.parseDouble(organizedTable2[row][14].toString());
				FLY_totalGraduated += Double.parseDouble(organizedTable2[row][10].toString());
				
				// the "++" means "add one to occurrence each time"
				//reseting to false to check for the new day
				FLY_isLabSession = false;
			}
			else
			{
				
				if(FLY_currentID.equals(organizedTable2[row][0]) && ((int)(Double.parseDouble(organizedTable2[row][date_column]))) == FLY_currentDay) //Same ID and same day
				{
					if(!(organizedTable2[row][4].equals("NULL")))
					{
						FLY_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
						FLY_occurrences[0]++;
					}
					if(!(organizedTable2[row][5].equals("NULL")))
					{
						FLY_missRate += Double.parseDouble(organizedTable2[row][5].toString());
						FLY_occurrences[1]++;
					}
					if(!(organizedTable2[row][6].equals("NULL")))
					{
						FLY_falsePositiveOne += Double.parseDouble(organizedTable2[row][6].toString());
						FLY_occurrences[2]++;
					}
					if(!(organizedTable2[row][7].equals("NULL")))
					{
						FLY_falsePositiveTwo += Double.parseDouble(organizedTable2[row][7].toString());
						FLY_occurrences[3]++;
					}
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						FLY_falsePositiveThree += Double.parseDouble(organizedTable2[row][8].toString());
						FLY_occurrences[4]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						FLY_falsePositiveFour += Double.parseDouble(organizedTable2[row][9].toString());
						FLY_occurrences[5]++;
					}
					if(!(organizedTable2[row][11].equals("NULL")))
					{
						FLY_trialsToLevelUp += Double.parseDouble(organizedTable2[row][11].toString());
						FLY_occurrences[6]++;
					}
					if(!(organizedTable2[row][12].equals("NULL")))
					{
						FLY_speedAtLevelUp += Double.parseDouble(organizedTable2[row][12].toString());
						FLY_occurrences[7]++;
					}
					if(!(organizedTable2[row][15].equals("NULL")))
					{
						FLY_timeOnTask1 += Double.parseDouble(organizedTable2[row][15].toString());
						FLY_occurrences[8]++;
					}
					if(!(organizedTable2[row][16].equals("NULL")))
					{
						FLY_timeOnTask2 += Double.parseDouble(organizedTable2[row][16].toString());
						FLY_occurrences[9]++;
					}
					
					FLY_totalPlayed += Double.parseDouble(organizedTable2[row][14].toString());
					FLY_totalGraduated += Double.parseDouble(organizedTable[row][10].toString());
					
					if(FLY_highestLevel <= Double.parseDouble(organizedTable2[row][3].toString()))
						FLY_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
					if(FLY_gameSession <= Double.parseDouble(organizedTable2[row][17].toString()))
						FLY_gameSession = Double.parseDouble(organizedTable2[row][17].toString());
					
					//timeOfDay = (Double.parseDouble(organizedTable2[row][3]) - FLY_currentDay);
				
				}
				else if(FLY_currentID.equals(organizedTable2[row][0]) && ((int)(Double.parseDouble(organizedTable2[row][date_column]))) != FLY_currentDay) //Same ID but new day
				{
					FLY_ID_day[FLY_totalDays] = organizedTable2[row][0].toString();
					FLY_day[FLY_totalDays] = FLY_currentDay;
					FLY_sessionLabDay[FLY_totalDays] = FsessionLabDay[row-1];
					
					//Taking averages and sums
					if(FLY_occurrences[0] != 0) {FLY_hitRateAvg_column[FLY_totalDays] = FLY_hitRate / FLY_occurrences[0];} //average of correct clicks calculated for the day
					else {FLY_hitRateAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[1] != 0) {FLY_missRateAvg_column[FLY_totalDays] = FLY_missRate / FLY_occurrences[1];} //avg percentage of correct clicks calculated for the day
					else {FLY_missRateAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[2] != 0) {FLY_falsePositiveOneAvg_column[FLY_totalDays] = FLY_falsePositiveOne / FLY_occurrences[2];}
					else {FLY_falsePositiveOneAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[3] != 0) {FLY_falsePositiveTwoAvg_column[FLY_totalDays] = FLY_falsePositiveTwo / FLY_occurrences[3];}
					else {FLY_falsePositiveTwoAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[4] != 0) {FLY_falsePositiveThreeAvg_column[FLY_totalDays] = FLY_falsePositiveThree / FLY_occurrences[4];}
					else {FLY_falsePositiveThreeAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[5] != 0) {FLY_falsePositiveFourAvg_column[FLY_totalDays] = FLY_falsePositiveFour / FLY_occurrences[5];}
					else {FLY_falsePositiveFourAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[6] != 0) {FLY_trialsToLevelUpAvg_column[FLY_totalDays] = FLY_trialsToLevelUp / FLY_occurrences[6];}
					else {FLY_trialsToLevelUpAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[7] != 0) {FLY_speedAtLevelUpAvg_column[FLY_totalDays] = FLY_speedAtLevelUp / FLY_occurrences[7];}
					else {FLY_speedAtLevelUpAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[8] != 0) {FLY_timeOnTask1Avg_column[FLY_totalDays] = FLY_timeOnTask1 / FLY_occurrences[8];}
					else {FLY_timeOnTask1Avg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[9] != 0) {FLY_timeOnTask2Avg_column[FLY_totalDays] = FLY_timeOnTask2 / FLY_occurrences[9];}
					else {FLY_timeOnTask2Avg_column[FLY_totalDays] = -1;}
					
					FLY_totalPlayedAvg_column[FLY_totalDays] = FLY_totalPlayed; 
					FLY_totalGraduatedAvg_column[FLY_totalDays] = FLY_totalGraduated;
					FLY_levelAvg_column[FLY_totalDays] = FLY_highestLevel;
					FLY_logSession_day[FLY_totalDays] = FLY_gameSession/4;
					FLY_NICT_sessionNumber_day[FLY_totalDays] = FLY_gameSession/8;
					//Finished taking averages and sums
					
					//Reseting variables to 0
					FLY_hitRate = 0; 
					FLY_missRate = 0; 
					FLY_totalPlayed = 0; 
					FLY_totalGraduated = 0;
					FLY_falsePositiveOne = 0;
					FLY_falsePositiveTwo = 0;
					FLY_falsePositiveThree = 0;
					FLY_falsePositiveFour = 0;
					FLY_trialsToLevelUp = 0;
					FLY_speedAtLevelUp = 0;
					FLY_timeOnTask1 = 0;
					FLY_timeOnTask2 = 0;					
					for( int index = 0 ; index < 10 ; index++)
						FLY_occurrences[index] = 0;
					//Finished reseting variables to 0
					
					//Updating the day value if the date has now changed
					FLY_totalDays++; 
					FLY_currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString())); 
					
					if(!(organizedTable2[row][4].equals("NULL")))
					{
						FLY_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
						FLY_occurrences[0]++;
					}
					if(!(organizedTable2[row][5].equals("NULL")))
					{
						FLY_missRate += Double.parseDouble(organizedTable2[row][5].toString());
						FLY_occurrences[1]++;
					}
					if(!(organizedTable2[row][6].equals("NULL")))
					{
						FLY_falsePositiveOne += Double.parseDouble(organizedTable2[row][6].toString());
						FLY_occurrences[2]++;
					}
					if(!(organizedTable2[row][7].equals("NULL")))
					{
						FLY_falsePositiveTwo += Double.parseDouble(organizedTable2[row][7].toString());
						FLY_occurrences[3]++;
					}
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						FLY_falsePositiveThree += Double.parseDouble(organizedTable2[row][8].toString());
						FLY_occurrences[4]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						FLY_falsePositiveFour += Double.parseDouble(organizedTable2[row][9].toString());
						FLY_occurrences[5]++;
					}
					if(!(organizedTable2[row][11].equals("NULL")))
					{
						FLY_trialsToLevelUp += Double.parseDouble(organizedTable2[row][11].toString());
						FLY_occurrences[6]++;
					}
					if(!(organizedTable2[row][12].equals("NULL")))
					{
						FLY_speedAtLevelUp += Double.parseDouble(organizedTable2[row][12].toString());
						FLY_occurrences[7]++;
					}
					if(!(organizedTable2[row][15].equals("NULL")))
					{
						FLY_timeOnTask1 += Double.parseDouble(organizedTable2[row][15].toString());
						FLY_occurrences[8]++;
					}
					if(!(organizedTable2[row][16].equals("NULL")))
					{
						FLY_timeOnTask2 += Double.parseDouble(organizedTable2[row][16].toString());
						FLY_occurrences[9]++;
					}
					
					FLY_totalPlayed += Double.parseDouble(organizedTable2[row][14].toString());
					FLY_totalGraduated += Double.parseDouble(organizedTable2[row][10].toString());
					FLY_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
					FLY_gameSession = Double.parseDouble(organizedTable2[row][17].toString());
					
				}
				else if(!(FLY_currentID.equals(organizedTable2[row][0]))) //New ID
				{
					FLY_ID_day[FLY_totalDays] = organizedTable2[row-1][0].toString();
					FLY_day[FLY_totalDays] = FLY_currentDay;
					FLY_sessionLabDay[FLY_totalDays] = FsessionLabDay[row-1];
					
					//Taking averages and sums
					if(FLY_occurrences[0] != 0) {FLY_hitRateAvg_column[FLY_totalDays] = FLY_hitRate / FLY_occurrences[0];} //average of correct clicks calculated for the day
					else {FLY_hitRateAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[1] != 0) {FLY_missRateAvg_column[FLY_totalDays] = FLY_missRate / FLY_occurrences[1];} //avg percentage of correct clicks calculated for the day
					else {FLY_missRateAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[2] != 0) {FLY_falsePositiveOneAvg_column[FLY_totalDays] = FLY_falsePositiveOne / FLY_occurrences[2];}
					else {FLY_falsePositiveOneAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[3] != 0) {FLY_falsePositiveTwoAvg_column[FLY_totalDays] = FLY_falsePositiveTwo / FLY_occurrences[3];}
					else {FLY_falsePositiveTwoAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[4] != 0) {FLY_falsePositiveThreeAvg_column[FLY_totalDays] = FLY_falsePositiveThree / FLY_occurrences[4];}
					else {FLY_falsePositiveThreeAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[5] != 0) {FLY_falsePositiveFourAvg_column[FLY_totalDays] = FLY_falsePositiveFour / FLY_occurrences[5];}
					else {FLY_falsePositiveFourAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[6] != 0) {FLY_trialsToLevelUpAvg_column[FLY_totalDays] = FLY_trialsToLevelUp / FLY_occurrences[6];}
					else {FLY_trialsToLevelUpAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[7] != 0) {FLY_speedAtLevelUpAvg_column[FLY_totalDays] = FLY_speedAtLevelUp / FLY_occurrences[7];}
					else {FLY_speedAtLevelUpAvg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[8] != 0) {FLY_timeOnTask1Avg_column[FLY_totalDays] = FLY_timeOnTask1 / FLY_occurrences[8];}
					else {FLY_timeOnTask1Avg_column[FLY_totalDays] = -1;}
					
					if(FLY_occurrences[9] != 0) {FLY_timeOnTask2Avg_column[FLY_totalDays] = FLY_timeOnTask2 / FLY_occurrences[9];}
					else {FLY_timeOnTask2Avg_column[FLY_totalDays] = -1;}
					
					FLY_totalPlayedAvg_column[FLY_totalDays] = FLY_totalPlayed; 
					FLY_totalGraduatedAvg_column[FLY_totalDays] = FLY_totalGraduated;
					FLY_levelAvg_column[FLY_totalDays] = FLY_highestLevel;
					FLY_logSession_day[FLY_totalDays] = FLY_gameSession/4;
					FLY_NICT_sessionNumber_day[FLY_totalDays] = FLY_gameSession/8;
					//Finished taking averages and sums
										
					
					FLY_totalDays++;
					FLY_currentID = organizedTable2[row][0].toString();
					FLY_currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString())); //Updating the day value
					
					//Reseting variables to 0
					FLY_hitRate = 0; 
					FLY_missRate = 0; 
					FLY_totalPlayed = 0; 
					FLY_totalGraduated = 0;
					FLY_falsePositiveOne = 0;
					FLY_falsePositiveTwo = 0;
					FLY_falsePositiveThree = 0;
					FLY_falsePositiveFour = 0;
					FLY_trialsToLevelUp = 0;
					FLY_speedAtLevelUp = 0;
					FLY_timeOnTask1 = 0;
					FLY_timeOnTask2 = 0;					
					for( int index = 0 ; index < 10 ; index++)
						FLY_occurrences[index] = 0;
					//Finished reseting variables to 0
					
					if(!(organizedTable2[row][4].equals("NULL")))
					{
						FLY_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
						FLY_occurrences[0]++;
					}
					if(!(organizedTable2[row][5].equals("NULL")))
					{
						FLY_missRate += Double.parseDouble(organizedTable2[row][5].toString());
						FLY_occurrences[1]++;
					}
					if(!(organizedTable2[row][6].equals("NULL")))
					{
						FLY_falsePositiveOne += Double.parseDouble(organizedTable2[row][6].toString());
						FLY_occurrences[2]++;
					}
					if(!(organizedTable2[row][7].equals("NULL")))
					{
						FLY_falsePositiveTwo += Double.parseDouble(organizedTable2[row][7].toString());
						FLY_occurrences[3]++;
					}
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						FLY_falsePositiveThree += Double.parseDouble(organizedTable2[row][8].toString());
						FLY_occurrences[4]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						FLY_falsePositiveFour += Double.parseDouble(organizedTable2[row][9].toString());
						FLY_occurrences[5]++;
					}
					if(!(organizedTable2[row][11].equals("NULL")))
					{
						FLY_trialsToLevelUp += Double.parseDouble(organizedTable2[row][11].toString());
						FLY_occurrences[6]++;
					}
					if(!(organizedTable2[row][12].equals("NULL")))
					{
						FLY_speedAtLevelUp += Double.parseDouble(organizedTable2[row][12].toString());
						FLY_occurrences[7]++;
					}
					if(!(organizedTable2[row][15].equals("NULL")))
					{
						FLY_timeOnTask1 += Double.parseDouble(organizedTable2[row][15].toString());
						FLY_occurrences[8]++;
					}
					if(!(organizedTable2[row][16].equals("NULL")))
					{
						FLY_timeOnTask2 += Double.parseDouble(organizedTable2[row][16].toString());
						FLY_occurrences[9]++;
					}
					
					FLY_totalPlayed += Double.parseDouble(organizedTable2[row][14].toString());
					FLY_totalGraduated += Double.parseDouble(organizedTable2[row][10].toString());
					FLY_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
					FLY_gameSession = Double.parseDouble(organizedTable2[row][17].toString());
				}
				else
				{
					System.out.println("error!!");
				}
				
				//Checking whether current date is a lab session
				for(int r = 1 ; r < sessionDate_row_num ; r++)
				{
					if(FLY_currentID.equals(sessionDate[r][0]))
					{
						for( int c = 2 ; c < (sessionDate_col_num-1) ; c++)
						{
							if(FLY_currentDay == sessionDate_asNum[r][c])
							{
								FLY_isLabSession = true;
							}
						}
					}
					
				}
				if(FLY_isLabSession == true)
				{
					FsessionLabDay[row] = 1;
					//System.out.println("Found a lab session day");
				}
				else
					FsessionLabDay[row] = 2;
				//Finished checking whether it's lab session
			
			//	FLY_logSession[row] = ((Double.parseDouble(organizedTable2[row][10].toString()))/4); // Calculating the logSession number
				//FLY_NICTSessionNum[row] = ((Double.parseDouble(organizedTable2[row][10].toString()))/8); // Calculating the NICT Session number
			
				//reseting to false to check for the new day
				FLY_isLabSession = false;
			}
			
		}
				
		//Creating final data table
		
		String[][] FLY_finalData = new String[100000][500]; // Table that receives the final data for FLY
		
		FLY_finalData[0][0] = "ID";
		FLY_finalData[0][1] = "Group";
		FLY_finalData[0][2] = "Round";
		FLY_finalData[0][3] = "1stLabDay";
		FLY_finalData[0][4] = "LastDay";
		FLY_finalData[0][5] = "PreTestDay";
		FLY_finalData[0][6] = "PostTestDay";
		FLY_finalData[0][7] = "1stLabDay_to_LastDay";
		FLY_finalData[0][8] = "LastDayPlayed_to_PostTestDay"; //negative number represents number of days played after the post test
		FLY_finalData[0][9] = "PreTest_to_PostTest";
		FLY_finalData[0][10] = "PreTest_to_1stLabDay";
		
		FLY_finalData[0][11] = "FLY_hitRateAvg_Week1";
		FLY_finalData[0][12] = "FLY_hitRateAvg_Week2";
		FLY_finalData[0][13] = "FLY_hitRateAvg_Week3";
		FLY_finalData[0][14] = "FLY_hitRateAvg_Week4";
		FLY_finalData[0][15] = "FLY_hitRateAvg_Week5";
		
		FLY_finalData[0][16] = "FLY_missRateAvg_Week1";
		FLY_finalData[0][17] = "FLY_missRateAvg_Week2";
		FLY_finalData[0][18] = "FLY_missRateAvg_Week3";
		FLY_finalData[0][19] = "FLY_missRateAvg_Week4";
		FLY_finalData[0][20] = "FLY_missRateAvg_Week5";
		
		FLY_finalData[0][21] = "FLY_totalPlayed_Week1";
		FLY_finalData[0][22] = "FLY_totalPlayed_Week2";
		FLY_finalData[0][23] = "FLY_totalPlayed_Week3";
		FLY_finalData[0][24] = "FLY_totalPlayed_Week4";
		FLY_finalData[0][25] = "FLY_totalPlayed_Week5";
		
		FLY_finalData[0][26] = "FLY_hitRateLab1_Avg";
		FLY_finalData[0][27] = "FLY_hitRateLab2_Avg";
		FLY_finalData[0][28] = "FLY_hitRateLab3_Avg";
		FLY_finalData[0][29] = "FLY_hitRateLab4_Avg";
		FLY_finalData[0][30] = "FLY_hitRateLab5_Avg";
		
		FLY_finalData[0][31] = "FLY_missRateLab1_Avg";
		FLY_finalData[0][32] = "FLY_missRateLab2_Avg";
		FLY_finalData[0][33] = "FLY_missRateLab3_Avg";
		FLY_finalData[0][34] = "FLY_missRateLab4_Avg";
		FLY_finalData[0][35] = "FLY_missRateLab5_Avg";
		
		FLY_finalData[0][36] = "FLY_totalPlayedLab1";
		FLY_finalData[0][37] = "FLY_totalPlayedLab2";
		FLY_finalData[0][38] = "FLY_totalPlayedLab3";
		FLY_finalData[0][39] = "FLY_totalPlayedLab4";
		FLY_finalData[0][40] = "FLY_totalPlayedLab5";
		
		FLY_finalData[0][41] = "FLY_totalGraduated_Week1";
		FLY_finalData[0][42] = "FLY_totalGraduated_Week2";
		FLY_finalData[0][43] = "FLY_totalGraduated_Week3";
		FLY_finalData[0][44] = "FLY_totalGraduated_Week4";
		FLY_finalData[0][45] = "FLY_totalGraduated_Week5";
		
		FLY_finalData[0][46] = "FLY_totalGraduated_Lab1";
		FLY_finalData[0][47] = "FLY_totalGraduated_Lab2";
		FLY_finalData[0][48] = "FLY_totalGraduated_Lab3";
		FLY_finalData[0][49] = "FLY_totalGraduated_Lab4";
		FLY_finalData[0][50] = "FLY_totalGraduated_Lab5";
		
		FLY_finalData[0][51] = "FLY_falsePositiveOneAvg_Week1";
		FLY_finalData[0][52] = "FLY_falsePositiveOneAvg_Week2";
		FLY_finalData[0][53] = "FLY_falsePositiveOneAvg_Week3";
		FLY_finalData[0][54] = "FLY_falsePositiveOneAvg_Week4";
		FLY_finalData[0][55] = "FLY_falsePositiveOneAvg_Week5";
				
		FLY_finalData[0][56] = "FLY_falsePositiveOneLab1_Avg";
		FLY_finalData[0][57] = "FLY_falsePositiveOneLab2_Avg";
		FLY_finalData[0][58] = "FLY_falsePositiveOneLab3_Avg";
		FLY_finalData[0][59] = "FLY_falsePositiveOneLab4_Avg";
		FLY_finalData[0][60] = "FLY_falsePositiveOneLab5_Avg";
		
		FLY_finalData[0][61] = "FLY_falsePositiveTwoAvg_Week1";
		FLY_finalData[0][62] = "FLY_falsePositiveTwoAvg_Week2";
		FLY_finalData[0][63] = "FLY_falsePositiveTwoAvg_Week3";
		FLY_finalData[0][64] = "FLY_falsePositiveTwoAvg_Week4";
		FLY_finalData[0][65] = "FLY_falsePositiveTwoAvg_Week5";
				
		FLY_finalData[0][66] = "FLY_falsePositiveTwoLab1_Avg";
		FLY_finalData[0][67] = "FLY_falsePositiveTwoLab2_Avg";
		FLY_finalData[0][68] = "FLY_falsePositiveTwoLab3_Avg";
		FLY_finalData[0][69] = "FLY_falsePositiveTwoLab4_Avg";
		FLY_finalData[0][70] = "FLY_falsePositiveTwoLab5_Avg";
		
		FLY_finalData[0][71] = "FLY_falsePositiveThreeAvg_Week1";
		FLY_finalData[0][72] = "FLY_falsePositiveThreeAvg_Week2";
		FLY_finalData[0][73] = "FLY_falsePositiveThreeAvg_Week3";
		FLY_finalData[0][74] = "FLY_falsePositiveThreeAvg_Week4";
		FLY_finalData[0][75] = "FLY_falsePositiveThreeAvg_Week5";
				
		FLY_finalData[0][76] = "FLY_falsePositiveThreeLab1_Avg";
		FLY_finalData[0][77] = "FLY_falsePositiveThreeLab2_Avg";
		FLY_finalData[0][78] = "FLY_falsePositiveThreeLab3_Avg";
		FLY_finalData[0][79] = "FLY_falsePositiveThreeLab4_Avg";
		FLY_finalData[0][80] = "FLY_falsePositiveThreeLab5_Avg";
		
		FLY_finalData[0][81] = "FLY_falsePositiveFourAvg_Week1";
		FLY_finalData[0][82] = "FLY_falsePositiveFourAvg_Week2";
		FLY_finalData[0][83] = "FLY_falsePositiveFourAvg_Week3";
		FLY_finalData[0][84] = "FLY_falsePositiveFourAvg_Week4";
		FLY_finalData[0][85] = "FLY_falsePositiveFourAvg_Week5";
				
		FLY_finalData[0][86] = "FLY_falsePositiveFourLab1_Avg";
		FLY_finalData[0][87] = "FLY_falsePositiveFourLab2_Avg";
		FLY_finalData[0][88] = "FLY_falsePositiveFourLab3_Avg";
		FLY_finalData[0][89] = "FLY_falsePositiveFourLab4_Avg";
		FLY_finalData[0][90] = "FLY_falsePositiveFourLab5_Avg";
		
		FLY_finalData[0][91] = "FLY_timeOnTask1Avg_Week1";
		FLY_finalData[0][92] = "FLY_timeOnTask1Avg_Week2";
		FLY_finalData[0][93] = "FLY_timeOnTask1Avg_Week3";
		FLY_finalData[0][94] = "FLY_timeOnTask1Avg_Week4";
		FLY_finalData[0][95] = "FLY_timeOnTask1Avg_Week5";
			
		FLY_finalData[0][96] = "FLY_timeOnTask1Lab1_Avg";
		FLY_finalData[0][97] = "FLY_timeOnTask1Lab2_Avg";
		FLY_finalData[0][98] = "FLY_timeOnTask1Lab3_Avg";
		FLY_finalData[0][99] = "FLY_timeOnTask1Lab4_Avg";
		FLY_finalData[0][100] = "FLY_timeOnTask1Lab5_Avg";
		
		FLY_finalData[0][101] = "FLY_timeOnTask2Avg_Week1";
		FLY_finalData[0][102] = "FLY_timeOnTask2Avg_Week2";
		FLY_finalData[0][103] = "FLY_timeOnTask2Avg_Week3";
		FLY_finalData[0][104] = "FLY_timeOnTask2Avg_Week4";
		FLY_finalData[0][105] = "FLY_timeOnTask2Avg_Week5";
				
		FLY_finalData[0][106] = "FLY_timeOnTask2Lab1_Avg";
		FLY_finalData[0][107] = "FLY_timeOnTask2Lab2_Avg";
		FLY_finalData[0][108] = "FLY_timeOnTask2Lab3_Avg";
		FLY_finalData[0][109] = "FLY_timeOnTask2Lab4_Avg";
		FLY_finalData[0][110] = "FLY_timeOnTask2Lab5_Avg";
		
		FLY_finalData[0][111] = "FLY_trialsToLevelUpAvg_Week1";
		FLY_finalData[0][112] = "FLY_trialsToLevelUpAvg_Week2";
		FLY_finalData[0][113] = "FLY_trialsToLevelUpAvg_Week3";
		FLY_finalData[0][114] = "FLY_trialsToLevelUpAvg_Week4";
		FLY_finalData[0][115] = "FLY_trialsToLevelUpAvg_Week5";
				
		FLY_finalData[0][116] = "FLY_trialsToLevelUpLab1_Avg";
		FLY_finalData[0][117] = "FLY_trialsToLevelUpLab2_Avg";
		FLY_finalData[0][118] = "FLY_trialsToLevelUpLab3_Avg";
		FLY_finalData[0][119] = "FLY_trialsToLevelUpLab4_Avg";
		FLY_finalData[0][120] = "FLY_trialsToLevelUpLab5_Avg";
		
		FLY_finalData[0][121] = "FLY_speedAtLevelUpAvg_Week1";
		FLY_finalData[0][122] = "FLY_speedAtLevelUpAvg_Week2";
		FLY_finalData[0][123] = "FLY_speedAtLevelUpAvg_Week3";
		FLY_finalData[0][124] = "FLY_speedAtLevelUpAvg_Week4";
		FLY_finalData[0][125] = "FLY_speedAtLevelUpAvg_Week5";
				
		FLY_finalData[0][126] = "FLY_speedAtLevelUpLab1_Avg";
		FLY_finalData[0][127] = "FLY_speedAtLevelUpLab2_Avg";
		FLY_finalData[0][128] = "FLY_speedAtLevelUpLab3_Avg";
		FLY_finalData[0][129] = "FLY_speedAtLevelUpLab4_Avg";
		FLY_finalData[0][130] = "FLY_speedAtLevelUpLab5_Avg";
		
		
		FLY_finalData[0][131] = "FLY_HighestLevel_Week1";
		FLY_finalData[0][132] = "FLY_HighestLevel_Week2";
		FLY_finalData[0][133] = "FLY_HighestLevel_Week3";
		FLY_finalData[0][134] = "FLY_HighestLevel_Week4";
		FLY_finalData[0][135] = "FLY_HighestLevel_Week5";
				
		FLY_finalData[0][136] = "FLY_HighestLevel_Lab1";
		FLY_finalData[0][137] = "FLY_HighestLevel_Lab2";
		FLY_finalData[0][138] = "FLY_HighestLevel_Lab3";
		FLY_finalData[0][139] = "FLY_HighestLevel_Lab4";
		FLY_finalData[0][140] = "FLY_HighestLevel_Lab5";
		
		FLY_finalData[0][141] = "FLY_LogSessionNumber_Week1";
		FLY_finalData[0][142] = "FLY_LogSessionNumber_Week2";
		FLY_finalData[0][143] = "FLY_LogSessionNumber_Week3";
		FLY_finalData[0][144] = "FLY_LogSessionNumber_Week4";
		FLY_finalData[0][145] = "FLY_LogSessionNumber_Week5";
				
		FLY_finalData[0][146] = "FLY_LogSessionNumber_Lab1";
		FLY_finalData[0][147] = "FLY_LogSessionNumber_Lab2";
		FLY_finalData[0][148] = "FLY_LogSessionNumber_Lab3";
		FLY_finalData[0][149] = "FLY_LogSessionNumber_Lab4";
		FLY_finalData[0][150] = "FLY_LogSessionNumber_Lab5";
		
		FLY_finalData[0][151] = "FLY_NICT_SessionNumber_Week1";
		FLY_finalData[0][152] = "FLY_NICT_SessionNumber_Week2";
		FLY_finalData[0][153] = "FLY_NICT_SessionNumber_Week3";
		FLY_finalData[0][154] = "FLY_NICT_SessionNumber_Week4";
		FLY_finalData[0][155] = "FLY_NICT_SessionNumber_Week5";
				
		FLY_finalData[0][156] = "FLY_NICT_SessionNumber_Lab1";
		FLY_finalData[0][157] = "FLY_NICT_SessionNumber_Lab2";
		FLY_finalData[0][158] = "FLY_NICT_SessionNumber_Lab3";
		FLY_finalData[0][159] = "FLY_NICT_SessionNumber_Lab4";
		FLY_finalData[0][160] = "FLY_NICT_SessionNumber_Lab5";
		
		
		FLY_finalData[0][161] = "LastDayBefore_PostDate";
		
		
		//Finding ID's group, round, 1st lab day, last day, pre_test day, and post_test day (give data) for all FLY IDs
		boolean FLY_foundID = false;
		String FLY_tempID = new String();

		
		for(int r = 0 ; r <= FLY_usersTotal ; r++)
		{
			FLY_tempID = FLY_IDs[r][0].toString();
			//Look for the ID in the session date table
			int r2 = 1; // Holds the row for the ID in sessionDate sheet
			int r5 = 0; // Holds the row where the ID is found on OrganizedTable 2 ( Table organized by ID and by Date)
			int r6 = 0; // Holds the last row for the ID 
			FLY_foundID = false;
			
			do{
				r5++;
				if(FLY_tempID.equals(organizedTable2[r5][0].toString()))
					FLY_foundID = true;
				
			}while(!(FLY_tempID.equals(organizedTable2[r5][0].toString())) && r5 < FLY_row_num && !(FLY_foundID));	
			
			r6 =r5;
			
			while(FLY_tempID.equals(organizedTable2[r6][0].toString()) && r6 < FLY_row_num)
			{
				r6++; //r6 stops one row after the last row for the ID ( always use < r6, not <= r6)
			}
			
			
			FLY_foundID = false;	
			while((!(FLY_tempID.equals(sessionDate[r2][0].toString()))) && (r2 < (sessionDate_row_num - 1))) //Searching for the ID in the sessionDate Sheet (Try to improve with a do/while)
			{
				r2++;
			}
			if(r2 < sessionDate_row_num && (r2 != sessionDate_row_num - 1)) //Checking whether the ID was found.
			{
				FLY_foundID = true;
			}
			else
			{
				System.out.println("ID: " + FLY_tempID + " not found on session date sheet.");
			}
			if(FLY_foundID)
			{
				FLY_finalData[r+1][0] = FLY_tempID.toString(); // current ID
				FLY_finalData[r+1][1] = FLY_IDs[r][1].toString(); // Group
				FLY_finalData[r+1][2] = FLY_IDs[r][2].toString(); // Round
				FLY_finalData[r+1][3] = sessionDate[r2][2].toString(); // First lab day
				FLY_finalData[r+1][5] = sessionDate[r2][1].toString(); // ID Pre_test day
				FLY_finalData[r+1][6] = sessionDate[r2][7].toString(); // ID Post_test day
				
				//Finding the last day for the ID
				FLY_foundID = false;
				int firstDayRow=0; //Find the first day row for the ID in the vector FLY_ID_day;
				int lastDayRow=0; // Find the last day row for the ID in the vector FLY_ID_day;
				for(int r3 = 1 ; r3 < FLY_totalDays && !FLY_foundID ; r3++)
				{
					if(FLY_tempID.equals(FLY_ID_day[r3]))
					{
						FLY_foundID = true;
						firstDayRow = r3;
						while(FLY_tempID.equals(FLY_ID_day[r3]))
						{
							r3++;
						}
						FLY_finalData[r+1][4] = Integer.toString(FLY_day[r3-1]);
						lastDayRow = (r3-1);
					}
				}
				//Finished finding the last day for the ID
				
				FLY_finalData[r+1][7] = Integer.toString((Integer.parseInt(FLY_finalData[r+1][4].toString()) - Integer.parseInt(FLY_finalData[r+1][3].toString()))); 
				FLY_finalData[r+1][8] = Integer.toString((Integer.parseInt(FLY_finalData[r+1][6].toString()) - Integer.parseInt(FLY_finalData[r+1][4].toString())));
				FLY_finalData[r+1][9] = Integer.toString((Integer.parseInt(FLY_finalData[r+1][6].toString()) - Integer.parseInt(FLY_finalData[r+1][5].toString())));
				FLY_finalData[r+1][10] = Integer.toString((Integer.parseInt(FLY_finalData[r+1][3].toString()) - Integer.parseInt(FLY_finalData[r+1][5].toString())));
				
				
				for(int col = 11 ; col <= 161 ; col++)
				{
					if(col == 161){FLY_finalData[r+1][col] = "";}
					else{FLY_finalData[r+1][col] = "0";}
					// columns 11, 12, 13, 14, 15 / FLY_hitRate week1Avg, week2Avg, week3Avg, week4Avg, week5Avg
				}
				
				int r4 = firstDayRow;
				while( (FLY_day[r4] < Integer.parseInt(FLY_finalData[r+1][6])) && r4 <= lastDayRow) //Checking if the last day is bigger than postDate
				{
					r4++;
				}
				if(r4 <= lastDayRow)
				{
					//FLY_finalData[r+1][8] = Integer.toString((Integer.parseInt(FLY_finalData[r+1][6].toString()) - FLY_day[r4-1]));
					FLY_finalData[r+1][161] = Integer.toString(FLY_day[r4]);
				}
				
				//Lab Averages
				r4 = firstDayRow;
				
				int[] occurrences2 = new int[10];
				for(int index = 0; index < 10 ; index++)
					occurrences2[index] = 0;
				
				if((FLY_day[r4]) < Integer.parseInt(sessionDate[r2][2].toString())) //First day <=  1st Lab session
				{
					//System.out.println("Error: There is activity before the 1st labSession for the ID " + FLY_finalData[r+1][0].toString());
					while(FLY_day[r4] <= sessionDate_asNum[r2][2] && r4 <= lastDayRow)
						r4++;
				}
				
				// Day >=  1st Lab session and Day < 2nd LabSession (Week1)
				if((FLY_day[r4] >= sessionDate_asNum[r2][2]) && (FLY_day[r4] < sessionDate_asNum[r2][3])) 
				{
					while((FLY_day[r4]) < sessionDate_asNum[r2][3] && r4 < lastDayRow) //While Day < 2nd Lab session date
					{
						if(FLY_sessionLabDay[r4] == 1)
						{
							if(FLY_hitRateAvg_column[r4] == -1) {FLY_finalData[r+1][26] = "";}
							else {FLY_finalData[r+1][26] = Double.toString(FLY_hitRateAvg_column[r4]);} // FLY_hitRate 1stLabAvg 
							
							if(FLY_missRateAvg_column[r4] == -1) {FLY_finalData[r+1][31] = "";}
							else {FLY_finalData[r+1][31] = Double.toString(FLY_missRateAvg_column[r4]);} // FLY_missRate 1stLabAvg 
							
							FLY_finalData[r+1][36] = Double.toString(FLY_totalPlayedAvg_column[r4]); // FLY_totalPlayed 1stLabAvg
							FLY_finalData[r+1][46] = Double.toString(FLY_totalGraduatedAvg_column[r4]); // FLY_totalGraduated 1stLabAvg 
							
							if(FLY_falsePositiveOneAvg_column[r4] == -1) {FLY_finalData[r+1][56] = "";}
							else {FLY_finalData[r+1][56] = Double.toString(FLY_falsePositiveOneAvg_column[r4]);} // FLY_falsePositiveOne 1stLabAvg 
							
							if(FLY_falsePositiveTwoAvg_column[r4] == -1) {FLY_finalData[r+1][66] = "";}
							else {FLY_finalData[r+1][66] = Double.toString(FLY_falsePositiveTwoAvg_column[r4]);} // FLY_flasePositiveTwo 1stLabAvg 
							
							if(FLY_falsePositiveThreeAvg_column[r4] == -1) {FLY_finalData[r+1][76] = "";}
							else {FLY_finalData[r+1][76] = Double.toString(FLY_falsePositiveThreeAvg_column[r4]);} // FLY_falsePositiveThree 1stLabAvg
							
							if(FLY_falsePositiveFourAvg_column[r4] == -1) {FLY_finalData[r+1][86] = "";}
							else {FLY_finalData[r+1][86] = Double.toString(FLY_falsePositiveFourAvg_column[r4]);} // FLY_falsePositiveFour 1stLabAvg 
							
							if(FLY_timeOnTask1Avg_column[r4] == -1) {FLY_finalData[r+1][96] = "";}
							else {FLY_finalData[r+1][96] = Double.toString(FLY_timeOnTask1Avg_column[r4]);} // FLY_timeOnTask1 1stLabAvg 
							
							if(FLY_timeOnTask2Avg_column[r4] == -1) {FLY_finalData[r+1][106] = "";}
							else {FLY_finalData[r+1][106] = Double.toString(FLY_timeOnTask2Avg_column[r4]);} // FLY_timeOnTask2 1stLabAvg
							
							if(FLY_trialsToLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][116] = "";}
							else {FLY_finalData[r+1][116] = Double.toString(FLY_trialsToLevelUpAvg_column[r4]);} // FLY_trialsToLevelUp 1stLabAvg
							
							if(FLY_speedAtLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][126] = "";}
							else {FLY_finalData[r+1][126] = Double.toString(FLY_speedAtLevelUpAvg_column[r4]);} // FLY_speedAtLevelUp 1stLabAvg 
							
							FLY_finalData[r+1][136] = Double.toString(FLY_levelAvg_column[r4]); //FLY_highestLevel 1stLab
							FLY_finalData[r+1][146] = Double.toString(FLY_logSession_day[r4]); //FLY_logSession 1stLab
							FLY_finalData[r+1][156] = Double.toString(FLY_NICT_sessionNumber_day[r4]); //FLY_NICT_sessionNumber 1stLab
						}
						r4++;
					}
					
				}
				
				// Day >=  2nd Lab session and Day < 3rd LabSession (Week2)
				if((FLY_day[r4] >= sessionDate_asNum[r2][3]) && (FLY_day[r4] < sessionDate_asNum[r2][4])) 
				{
					while( FLY_day[r4] < sessionDate_asNum[r2][4] && r4 < lastDayRow) //While Day < 3rd Lab session date
					{						
						if(FLY_sessionLabDay[r4] == 1)
						{
							if(FLY_hitRateAvg_column[r4] == -1) {FLY_finalData[r+1][27] = "";}
							else {FLY_finalData[r+1][27] = Double.toString(FLY_hitRateAvg_column[r4]);} // FLY_hitRate 2ndLabAvg 
							
							if(FLY_missRateAvg_column[r4] == -1) {FLY_finalData[r+1][32] = "";}
							else {FLY_finalData[r+1][32] = Double.toString(FLY_missRateAvg_column[r4]);} // FLY_missRate 2ndLabAvg 
							
							FLY_finalData[r+1][37] = Double.toString(FLY_totalPlayedAvg_column[r4]); // FLY_totalPlayed 2ndLabAvg
							FLY_finalData[r+1][47] = Double.toString(FLY_totalGraduatedAvg_column[r4]); // FLY_totalGraduated 2ndLabAvg 
							
							if(FLY_falsePositiveOneAvg_column[r4] == -1) {FLY_finalData[r+1][57] = "";}
							else {FLY_finalData[r+1][57] = Double.toString(FLY_falsePositiveOneAvg_column[r4]);} // FLY_falsePositiveOne 2ndLabAvg 
							
							if(FLY_falsePositiveTwoAvg_column[r4] == -1) {FLY_finalData[r+1][67] = "";}
							else {FLY_finalData[r+1][67] = Double.toString(FLY_falsePositiveTwoAvg_column[r4]);} // FLY_flasePositiveTwo 2ndLabAvg 
							
							if(FLY_falsePositiveThreeAvg_column[r4] == -1) {FLY_finalData[r+1][77] = "";}
							else {FLY_finalData[r+1][77] = Double.toString(FLY_falsePositiveThreeAvg_column[r4]);} // FLY_falsePositiveThree 2ndLabAvg
							
							if(FLY_falsePositiveFourAvg_column[r4] == -1) {FLY_finalData[r+1][87] = "";}
							else {FLY_finalData[r+1][87] = Double.toString(FLY_falsePositiveFourAvg_column[r4]);} // FLY_falsePositiveFour 2ndLabAvg 
							
							if(FLY_timeOnTask1Avg_column[r4] == -1) {FLY_finalData[r+1][97] = "";}
							else {FLY_finalData[r+1][97] = Double.toString(FLY_timeOnTask1Avg_column[r4]);} // FLY_timeOnTask1 2ndLabAvg 
							
							if(FLY_timeOnTask2Avg_column[r4] == -1) {FLY_finalData[r+1][107] = "";}
							else {FLY_finalData[r+1][107] = Double.toString(FLY_timeOnTask2Avg_column[r4]);} // FLY_timeOnTask2 2ndLabAvg
							
							if(FLY_trialsToLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][117] = "";}
							else {FLY_finalData[r+1][117] = Double.toString(FLY_trialsToLevelUpAvg_column[r4]);} // FLY_trialsToLevelUp 2ndLabAvg
							
							if(FLY_speedAtLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][127] = "";}
							else {FLY_finalData[r+1][127] = Double.toString(FLY_speedAtLevelUpAvg_column[r4]);} // FLY_speedAtLevelUp 2ndLabAvg 
							
							FLY_finalData[r+1][137] = Double.toString(FLY_levelAvg_column[r4]); //FLY_highestLevel 2ndLab
							FLY_finalData[r+1][147] = Double.toString(FLY_logSession_day[r4]); //FLY_logSession 2ndLab
							FLY_finalData[r+1][157] = Double.toString(FLY_NICT_sessionNumber_day[r4]); //FLY_NICT_sessionNumber 2ndLab
						}
						r4++;
					}
				}
				
				// Day >=  3rd Lab session and Day < 4th LabSession (Week3)
				if((FLY_day[r4] >= sessionDate_asNum[r2][4]) && (FLY_day[r4] < sessionDate_asNum[r2][5])) 
				{
					while(FLY_day[r4] < sessionDate_asNum[r2][5] && r4 < lastDayRow) //While Day < 4th Lab session date
					{
						if(FLY_sessionLabDay[r4] == 1)
						{
							if(FLY_hitRateAvg_column[r4] == -1) {FLY_finalData[r+1][28] = "";}
							else {FLY_finalData[r+1][28] = Double.toString(FLY_hitRateAvg_column[r4]);} // FLY_hitRate 3rdLabAvg 
							
							if(FLY_missRateAvg_column[r4] == -1) {FLY_finalData[r+1][33] = "";}
							else {FLY_finalData[r+1][33] = Double.toString(FLY_missRateAvg_column[r4]);} // FLY_missRate 3rdLabAvg 
							
							FLY_finalData[r+1][38] = Double.toString(FLY_totalPlayedAvg_column[r4]); // FLY_totalPlayed 3rdLabAvg
							FLY_finalData[r+1][48] = Double.toString(FLY_totalGraduatedAvg_column[r4]); // FLY_totalGraduated 3rdLabAvg 
							
							if(FLY_falsePositiveOneAvg_column[r4] == -1) {FLY_finalData[r+1][58] = "";}
							else {FLY_finalData[r+1][58] = Double.toString(FLY_falsePositiveOneAvg_column[r4]);} // FLY_falsePositiveOne 3rdLabAvg 
							
							if(FLY_falsePositiveTwoAvg_column[r4] == -1) {FLY_finalData[r+1][68] = "";}
							else {FLY_finalData[r+1][68] = Double.toString(FLY_falsePositiveTwoAvg_column[r4]);} // FLY_flasePositiveTwo 3rdLabAvg 
							
							if(FLY_falsePositiveThreeAvg_column[r4] == -1) {FLY_finalData[r+1][78] = "";}
							else {FLY_finalData[r+1][78] = Double.toString(FLY_falsePositiveThreeAvg_column[r4]);} // FLY_falsePositiveThree 3rdLabAvg
							
							if(FLY_falsePositiveFourAvg_column[r4] == -1) {FLY_finalData[r+1][88] = "";}
							else {FLY_finalData[r+1][88] = Double.toString(FLY_falsePositiveFourAvg_column[r4]);} // FLY_falsePositiveFour 3rdLabAvg 
							
							if(FLY_timeOnTask1Avg_column[r4] == -1) {FLY_finalData[r+1][98] = "";}
							else {FLY_finalData[r+1][98] = Double.toString(FLY_timeOnTask1Avg_column[r4]);} // FLY_timeOnTask1 3rdLabAvg 
							
							if(FLY_timeOnTask2Avg_column[r4] == -1) {FLY_finalData[r+1][108] = "";}
							else {FLY_finalData[r+1][108] = Double.toString(FLY_timeOnTask2Avg_column[r4]);} // FLY_timeOnTask2 3rdLabAvg
							
							if(FLY_trialsToLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][118] = "";}
							else {FLY_finalData[r+1][118] = Double.toString(FLY_trialsToLevelUpAvg_column[r4]);} // FLY_trialsToLevelUp 3rdLabAvg
							
							if(FLY_speedAtLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][128] = "";}
							else {FLY_finalData[r+1][128] = Double.toString(FLY_speedAtLevelUpAvg_column[r4]);} // FLY_speedAtLevelUp 3rdLabAvg 
							
							FLY_finalData[r+1][138] = Double.toString(FLY_levelAvg_column[r4]); //FLY_highestLevel 3rdtLab
							FLY_finalData[r+1][148] = Double.toString(FLY_logSession_day[r4]); //FLY_logSession 3rdLab
							FLY_finalData[r+1][158] = Double.toString(FLY_NICT_sessionNumber_day[r4]); //FLY_NICT_sessionNumber 3rdLab
						}
						r4++;
					}
				}
				
				
				// Day >=  4th Lab session and Day < 5th LabSession (Week4)
				if((FLY_day[r4] >= sessionDate_asNum[r2][5]) && (FLY_day[r4] < sessionDate_asNum[r2][6])) 
				{
					while(FLY_day[r4] < sessionDate_asNum[r2][6] && r4 < lastDayRow ) //While Day < 5th Lab session date
					{
						if(FLY_sessionLabDay[r4] == 1)
						{
							if(FLY_hitRateAvg_column[r4] == -1) {FLY_finalData[r+1][29] = "";}
							else {FLY_finalData[r+1][29] = Double.toString(FLY_hitRateAvg_column[r4]);} // FLY_hitRate 4thLabAvg 
							
							if(FLY_missRateAvg_column[r4] == -1) {FLY_finalData[r+1][34] = "";}
							else {FLY_finalData[r+1][34] = Double.toString(FLY_missRateAvg_column[r4]);} // FLY_missRate 4thLabAvg 
							
							FLY_finalData[r+1][39] = Double.toString(FLY_totalPlayedAvg_column[r4]); // FLY_totalPlayed 4thLabAvg
							FLY_finalData[r+1][49] = Double.toString(FLY_totalGraduatedAvg_column[r4]); // FLY_totalGraduated 4thLabAvg 
							
							if(FLY_falsePositiveOneAvg_column[r4] == -1) {FLY_finalData[r+1][59] = "";}
							else {FLY_finalData[r+1][59] = Double.toString(FLY_falsePositiveOneAvg_column[r4]);} // FLY_falsePositiveOne 4thLabAvg 
							
							if(FLY_falsePositiveTwoAvg_column[r4] == -1) {FLY_finalData[r+1][69] = "";}
							else {FLY_finalData[r+1][69] = Double.toString(FLY_falsePositiveTwoAvg_column[r4]);} // FLY_flasePositiveTwo 4thLabAvg 
							
							if(FLY_falsePositiveThreeAvg_column[r4] == -1) {FLY_finalData[r+1][79] = "";}
							else {FLY_finalData[r+1][79] = Double.toString(FLY_falsePositiveThreeAvg_column[r4]);} // FLY_falsePositiveThree 4thLabAvg
							
							if(FLY_falsePositiveFourAvg_column[r4] == -1) {FLY_finalData[r+1][89] = "";}
							else {FLY_finalData[r+1][89] = Double.toString(FLY_falsePositiveFourAvg_column[r4]);} // FLY_falsePositiveFour 4thLabAvg 
							
							if(FLY_timeOnTask1Avg_column[r4] == -1) {FLY_finalData[r+1][99] = "";}
							else {FLY_finalData[r+1][99] = Double.toString(FLY_timeOnTask1Avg_column[r4]);} // FLY_timeOnTask1 4thLabAvg 
							
							if(FLY_timeOnTask2Avg_column[r4] == -1) {FLY_finalData[r+1][109] = "";}
							else {FLY_finalData[r+1][109] = Double.toString(FLY_timeOnTask2Avg_column[r4]);} // FLY_timeOnTask2 4thLabAvg
							
							if(FLY_trialsToLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][119] = "";}
							else {FLY_finalData[r+1][119] = Double.toString(FLY_trialsToLevelUpAvg_column[r4]);} // FLY_trialsToLevelUp 4thLabAvg
							
							if(FLY_speedAtLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][129] = "";}
							else {FLY_finalData[r+1][129] = Double.toString(FLY_speedAtLevelUpAvg_column[r4]);} // FLY_speedAtLevelUp 4thLabAvg 
							
							FLY_finalData[r+1][139] = Double.toString(FLY_levelAvg_column[r4]); //FLY_highestLevel 4thLab
							FLY_finalData[r+1][149] = Double.toString(FLY_logSession_day[r4]); //FLY_logSession 4thLab
							FLY_finalData[r+1][159] = Double.toString(FLY_NICT_sessionNumber_day[r4]); //FLY_NICT_sessionNumber 4thLab
						}
						r4++;
					}
				}
				
				// Day >=  5th Lab session and Day <= postDate (Week5)				
				if((FLY_day[r4] >= sessionDate_asNum[r2][6]) && (FLY_day[r4] <= sessionDate_asNum[r2][7])) 
				{
					while(FLY_day[r4] < sessionDate_asNum[r2][7] && r4 < lastDayRow) //While Day < postdate
					{
						if(FLY_sessionLabDay[r4] == 1)
						{
							if(FLY_hitRateAvg_column[r4] == -1) {FLY_finalData[r+1][30] = "";}
							else {FLY_finalData[r+1][30] = Double.toString(FLY_hitRateAvg_column[r4]);} // FLY_hitRate 5thLabAvg 
							
							if(FLY_missRateAvg_column[r4] == -1) {FLY_finalData[r+1][35] = "";}
							else {FLY_finalData[r+1][35] = Double.toString(FLY_missRateAvg_column[r4]);} // FLY_missRate 5thLabAvg 
							
							FLY_finalData[r+1][40] = Double.toString(FLY_totalPlayedAvg_column[r4]); // FLY_totalPlayed 5thLabAvg
							FLY_finalData[r+1][50] = Double.toString(FLY_totalGraduatedAvg_column[r4]); // FLY_totalGraduated 5thLabAvg 
							
							if(FLY_falsePositiveOneAvg_column[r4] == -1) {FLY_finalData[r+1][60] = "";}
							else {FLY_finalData[r+1][60] = Double.toString(FLY_falsePositiveOneAvg_column[r4]);} // FLY_falsePositiveOne 5thLabAvg 
							
							if(FLY_falsePositiveTwoAvg_column[r4] == -1) {FLY_finalData[r+1][70] = "";}
							else {FLY_finalData[r+1][70] = Double.toString(FLY_falsePositiveTwoAvg_column[r4]);} // FLY_flasePositiveTwo 5thLabAvg 
							
							if(FLY_falsePositiveThreeAvg_column[r4] == -1) {FLY_finalData[r+1][80] = "";}
							else {FLY_finalData[r+1][80] = Double.toString(FLY_falsePositiveThreeAvg_column[r4]);} // FLY_falsePositiveThree 5thLabAvg
							
							if(FLY_falsePositiveFourAvg_column[r4] == -1) {FLY_finalData[r+1][90] = "";}
							else {FLY_finalData[r+1][90] = Double.toString(FLY_falsePositiveFourAvg_column[r4]);} // FLY_falsePositiveFour 5thLabAvg 
							
							if(FLY_timeOnTask1Avg_column[r4] == -1) {FLY_finalData[r+1][100] = "";}
							else {FLY_finalData[r+1][100] = Double.toString(FLY_timeOnTask1Avg_column[r4]);} // FLY_timeOnTask1 5thLabAvg 
							
							if(FLY_timeOnTask2Avg_column[r4] == -1) {FLY_finalData[r+1][110] = "";}
							else {FLY_finalData[r+1][110] = Double.toString(FLY_timeOnTask2Avg_column[r4]);} // FLY_timeOnTask2 5thLabAvg
							
							if(FLY_trialsToLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][120] = "";}
							else {FLY_finalData[r+1][120] = Double.toString(FLY_trialsToLevelUpAvg_column[r4]);} // FLY_trialsToLevelUp 5thLabAvg
							
							if(FLY_speedAtLevelUpAvg_column[r4] == -1) {FLY_finalData[r+1][130] = "";}
							else {FLY_finalData[r+1][130] = Double.toString(FLY_speedAtLevelUpAvg_column[r4]);} // FLY_speedAtLevelUp 5thLabAvg 
							
							FLY_finalData[r+1][140] = Double.toString(FLY_levelAvg_column[r4]); //FLY_highestLevel 5thLab
							FLY_finalData[r+1][150] = Double.toString(FLY_logSession_day[r4]); //FLY_logSession 5thLab
							FLY_finalData[r+1][160] = Double.toString(FLY_NICT_sessionNumber_day[r4]); //FLY_NICT_sessionNumber 5thLab
						}	
						r4++;
					}
				}
				
				////////---------------------------------------------------------------------------------------------------------------------------------------/////

				//Average from raw data
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString())))) < Integer.parseInt(sessionDate[r2][2].toString())) //First day <  1st Lab session
				{
					System.out.println("Error: There is activity before the 1st labSession for the ID " + FLY_finalData[r+1][0].toString());
					while((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString())))) <= sessionDate_asNum[r2][2] && r5 < r6)
					{
						r5++;
						if(r5 == r6)
							break;
					}
				}
				
				// Day >=  1st Lab session and Day < 2nd LabSession (Week1) // THE IF AND WHILE STATEMENT GO TOGETHER, IF COMMENT ONE MUST COMMENT THE OTHER AND VICE VERSA
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][2]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][3])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][2]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+7))) //THIS IS DIVIDED BY 7 DAYS vs LAB SESSION
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][3] && r5 < r6) //While Day < 2nd Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+7) && r5 < r6) //THIS IS DIVIDED BY 7 DAYS vs LAB SESSION
					{
						
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							FLY_finalData[r+1][11] = Double.toString((Double.parseDouble(FLY_finalData[r+1][11].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							FLY_finalData[r+1][16] = Double.toString((Double.parseDouble(FLY_finalData[r+1][16].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						FLY_finalData[r+1][21] = Double.toString((Double.parseDouble(FLY_finalData[r+1][21].toString())) + (Double.parseDouble(organizedTable2[r5][14].toString()))); //totalPlayed
						FLY_finalData[r+1][41] = Double.toString((Double.parseDouble(FLY_finalData[r+1][41].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							FLY_finalData[r+1][51] = Double.toString((Double.parseDouble(FLY_finalData[r+1][51].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //falsePositiveOne
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							FLY_finalData[r+1][61] = Double.toString((Double.parseDouble(FLY_finalData[r+1][61].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //falsePositiveTwo
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							FLY_finalData[r+1][71] = Double.toString((Double.parseDouble(FLY_finalData[r+1][71].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //falsePositiveThree
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							FLY_finalData[r+1][81] = Double.toString((Double.parseDouble(FLY_finalData[r+1][81].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //falsePositiveFour
							occurrences2[5]++;
						}						
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							FLY_finalData[r+1][91] = Double.toString((Double.parseDouble(FLY_finalData[r+1][91].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //timeOnTask1
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][16].equals("NULL")))
						{
							FLY_finalData[r+1][101] = Double.toString((Double.parseDouble(FLY_finalData[r+1][101].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //timeOnTask2
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							FLY_finalData[r+1][111] = Double.toString((Double.parseDouble(FLY_finalData[r+1][111].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //trialsToLevelUp
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][12].equals("NULL")))
						{
							FLY_finalData[r+1][121] = Double.toString((Double.parseDouble(FLY_finalData[r+1][121].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //speedAtLevelUp
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(FLY_finalData[r+1][131].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							FLY_finalData[r+1][131] = organizedTable2[r5][3].toString(); //Level
						if((Double.parseDouble(FLY_finalData[r+1][141].toString())) <= (Double.parseDouble(organizedTable2[r5][17].toString())))
							FLY_finalData[r+1][141] = organizedTable2[r5][17].toString(); //GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0) {FLY_finalData[r+1][11] = Double.toString((Double.parseDouble(FLY_finalData[r+1][11].toString())/occurrences2[0]));} //Week1 FLY_hitRateAvg
					else {FLY_finalData[r+1][11] = "";} // HERE IT'S PRINTING. CAN CHANGE IT FROM BLANK (NOTHING IN BETWN QUOATES) TO ANY STRING 
					
					if(occurrences2[1] != 0) {FLY_finalData[r+1][16] = Double.toString((Double.parseDouble(FLY_finalData[r+1][16].toString())/occurrences2[1]));} //Week1 FLY_missRateAvg
					else {FLY_finalData[r+1][16] = "";}
					
					FLY_finalData[r+1][21] = Double.toString((Double.parseDouble(FLY_finalData[r+1][21].toString()))); //Week1 FLY_totalPlayedAvg
					FLY_finalData[r+1][41] = Double.toString((Double.parseDouble(FLY_finalData[r+1][41].toString()))); //Week1 FLY_totalGraduatedAvg
						
					if(occurrences2[2] != 0) {FLY_finalData[r+1][51] = Double.toString((Double.parseDouble(FLY_finalData[r+1][51].toString())/occurrences2[2]));} //Week1 FLY_falsePositiveOne
					else {FLY_finalData[r+1][51] = "";}
					
					if(occurrences2[3] != 0) {FLY_finalData[r+1][61] = Double.toString((Double.parseDouble(FLY_finalData[r+1][61].toString())/occurrences2[3]));} //Week1 FLY_falsePositiveTwo
					else {FLY_finalData[r+1][61] = "";}
					
					if(occurrences2[4] != 0) {FLY_finalData[r+1][71] = Double.toString((Double.parseDouble(FLY_finalData[r+1][71].toString())/occurrences2[4]));} //Week1 FLY_falsePositiveThree
					else {FLY_finalData[r+1][71] = "";}
					
					if(occurrences2[5] != 0) {FLY_finalData[r+1][81] = Double.toString((Double.parseDouble(FLY_finalData[r+1][81].toString())/occurrences2[5]));} //Week1 FLY_falsePositiveFour
					else {FLY_finalData[r+1][81] = "";}
					
					if(occurrences2[8] != 0) {FLY_finalData[r+1][91] = Double.toString((Double.parseDouble(FLY_finalData[r+1][91].toString())/occurrences2[8]));} //Week1 FLY_timeOnTask1
					else {FLY_finalData[r+1][91] = "";}
					
					if(occurrences2[9] != 0) {FLY_finalData[r+1][101] = Double.toString((Double.parseDouble(FLY_finalData[r+1][101].toString())/occurrences2[9]));} //Week1 FLY_timeOnTask2
					else {FLY_finalData[r+1][101] = "";}
					
					if(occurrences2[6] != 0) {FLY_finalData[r+1][111] = Double.toString((Double.parseDouble(FLY_finalData[r+1][111].toString())/occurrences2[6]));} //Week1 FLY_trialsToLevelUp
					else {FLY_finalData[r+1][111] = "";}
					
					if(occurrences2[7] != 0) {FLY_finalData[r+1][121] = Double.toString((Double.parseDouble(FLY_finalData[r+1][121].toString())/occurrences2[7]));} //Week1 FLY_speedAtLevelUp
					else {FLY_finalData[r+1][121] = "";}
						
					FLY_finalData[r+1][141] = Double.toString((Double.parseDouble(FLY_finalData[r+1][141].toString()))/4); //Week1 FLY_LogSessionNumber
					FLY_finalData[r+1][151] = Double.toString((Double.parseDouble(FLY_finalData[r+1][141].toString()))/2); //Week1 FLY_NICT_SessionNumber
						
					for(int index = 0 ; index < 10 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 1st labSession and 2nd LabSession (Week 1) for the ID " + FLY_finalData[r+1][0].toString());
				}
				
				// Day >=  2nd Lab session and Day < 3rd LabSession (Week2) // SAME AS WEEK 1 (SAME FOR ALL WEEKS)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][3]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][4])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+7)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+14))) 	
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][4] && r5 < r6) //While Day < 3rd Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+14) && r5 < r6) 	
					{
						
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							FLY_finalData[r+1][12] = Double.toString((Double.parseDouble(FLY_finalData[r+1][12].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							FLY_finalData[r+1][17] = Double.toString((Double.parseDouble(FLY_finalData[r+1][17].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						FLY_finalData[r+1][22] = Double.toString((Double.parseDouble(FLY_finalData[r+1][22].toString())) + (Double.parseDouble(organizedTable2[r5][14].toString()))); //totalPlayed
						FLY_finalData[r+1][42] = Double.toString((Double.parseDouble(FLY_finalData[r+1][42].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							FLY_finalData[r+1][52] = Double.toString((Double.parseDouble(FLY_finalData[r+1][52].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //falsePositiveOne
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							FLY_finalData[r+1][62] = Double.toString((Double.parseDouble(FLY_finalData[r+1][62].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //falsePositiveTwo
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							FLY_finalData[r+1][72] = Double.toString((Double.parseDouble(FLY_finalData[r+1][72].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //falsePositiveThree
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							FLY_finalData[r+1][82] = Double.toString((Double.parseDouble(FLY_finalData[r+1][82].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //falsePositiveFour
							occurrences2[5]++;
						}						
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							FLY_finalData[r+1][92] = Double.toString((Double.parseDouble(FLY_finalData[r+1][92].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //timeOnTask1
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][16].equals("NULL")))
						{
							FLY_finalData[r+1][102] = Double.toString((Double.parseDouble(FLY_finalData[r+1][102].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //timeOnTask2
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							FLY_finalData[r+1][112] = Double.toString((Double.parseDouble(FLY_finalData[r+1][112].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //trialsToLevelUp
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][12].equals("NULL")))
						{
							FLY_finalData[r+1][122] = Double.toString((Double.parseDouble(FLY_finalData[r+1][122].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //speedAtLevelUp
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(FLY_finalData[r+1][132].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							FLY_finalData[r+1][132] = organizedTable2[r5][3].toString(); // Level
						if((Double.parseDouble(FLY_finalData[r+1][142].toString())) <= (Double.parseDouble(organizedTable2[r5][17].toString())))
							FLY_finalData[r+1][142] = organizedTable2[r5][17].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}

					if(occurrences2[0] != 0) {FLY_finalData[r+1][12] = Double.toString((Double.parseDouble(FLY_finalData[r+1][12].toString())/occurrences2[0]));} //Week2 FLY_hitRateAvg
					else {FLY_finalData[r+1][12] = "";}
					
					if(occurrences2[1] != 0) {FLY_finalData[r+1][17] = Double.toString((Double.parseDouble(FLY_finalData[r+1][17].toString())/occurrences2[1]));} //Week2 FLY_missRateAvg
					else {FLY_finalData[r+1][17] = "";}
					
					FLY_finalData[r+1][22] = Double.toString((Double.parseDouble(FLY_finalData[r+1][22].toString()))); //Week2 FLY_totalPlayedAvg
					FLY_finalData[r+1][42] = Double.toString((Double.parseDouble(FLY_finalData[r+1][42].toString()))); //Week2 FLY_totalGraduatedAvg
						
					if(occurrences2[2] != 0) {FLY_finalData[r+1][52] = Double.toString((Double.parseDouble(FLY_finalData[r+1][52].toString())/occurrences2[2]));} //Week2 FLY_falsePositiveOne
					else {FLY_finalData[r+1][52] = "";}
					
					if(occurrences2[3] != 0) {FLY_finalData[r+1][62] = Double.toString((Double.parseDouble(FLY_finalData[r+1][62].toString())/occurrences2[3]));} //Week2 FLY_falsePositiveTwo
					else {FLY_finalData[r+1][62] = "";}
					
					if(occurrences2[4] != 0) {FLY_finalData[r+1][72] = Double.toString((Double.parseDouble(FLY_finalData[r+1][72].toString())/occurrences2[4]));} //Week2 FLY_falsePositiveThree
					else {FLY_finalData[r+1][72] = "";}
					
					if(occurrences2[5] != 0) {FLY_finalData[r+1][82] = Double.toString((Double.parseDouble(FLY_finalData[r+1][82].toString())/occurrences2[5]));} //Week2 FLY_falsePositiveFour
					else {FLY_finalData[r+1][82] = "";}
					
					if(occurrences2[8] != 0) {FLY_finalData[r+1][92] = Double.toString((Double.parseDouble(FLY_finalData[r+1][92].toString())/occurrences2[8]));} //Week2 FLY_timeOnTask1
					else {FLY_finalData[r+1][92] = "";}
					
					if(occurrences2[9] != 0) {FLY_finalData[r+1][102] = Double.toString((Double.parseDouble(FLY_finalData[r+1][102].toString())/occurrences2[9]));} //Week2 FLY_timeOnTask2
					else {FLY_finalData[r+1][102] = "";}
					
					if(occurrences2[6] != 0) {FLY_finalData[r+1][112] = Double.toString((Double.parseDouble(FLY_finalData[r+1][112].toString())/occurrences2[6]));} //Week2 FLY_trialsToLevelUp
					else {FLY_finalData[r+1][112] = "";}
					
					if(occurrences2[7] != 0) {FLY_finalData[r+1][122] = Double.toString((Double.parseDouble(FLY_finalData[r+1][122].toString())/occurrences2[7]));} //Week2 FLY_speedAtLevelUp
					else {FLY_finalData[r+1][122] = "";}
						
					FLY_finalData[r+1][142] = Double.toString((Double.parseDouble(FLY_finalData[r+1][142].toString()))/4); //Week2 FLY_LogSessionNumber
					FLY_finalData[r+1][152] = Double.toString((Double.parseDouble(FLY_finalData[r+1][142].toString()))/2); //Week2 FLY_NICT_SessionNumber
						
					for(int index = 0 ; index < 10 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 2nd labSession and 3rd LabSession (Week 2) for the ID " + FLY_finalData[r+1][0].toString());
				}
				
				// Day >=  3rd Lab session and Day < 4th LabSession (Week3)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][4]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][5])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+14)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+21)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][5] && r5 < r6) //While Day < 4th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+21) && r5 < r6) 
					{
						
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							FLY_finalData[r+1][13] = Double.toString((Double.parseDouble(FLY_finalData[r+1][13].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							FLY_finalData[r+1][18] = Double.toString((Double.parseDouble(FLY_finalData[r+1][18].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						FLY_finalData[r+1][23] = Double.toString((Double.parseDouble(FLY_finalData[r+1][23].toString())) + (Double.parseDouble(organizedTable2[r5][14].toString()))); //totalPlayed
						FLY_finalData[r+1][43] = Double.toString((Double.parseDouble(FLY_finalData[r+1][43].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							FLY_finalData[r+1][53] = Double.toString((Double.parseDouble(FLY_finalData[r+1][53].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //falsePositiveOne
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							FLY_finalData[r+1][63] = Double.toString((Double.parseDouble(FLY_finalData[r+1][63].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //falsePositiveTwo
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							FLY_finalData[r+1][73] = Double.toString((Double.parseDouble(FLY_finalData[r+1][73].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //falsePositiveThree
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							FLY_finalData[r+1][83] = Double.toString((Double.parseDouble(FLY_finalData[r+1][83].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //falsePositiveFour
							occurrences2[5]++;
						}						
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							FLY_finalData[r+1][93] = Double.toString((Double.parseDouble(FLY_finalData[r+1][93].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //timeOnTask1
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][16].equals("NULL")))
						{
							FLY_finalData[r+1][103] = Double.toString((Double.parseDouble(FLY_finalData[r+1][103].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //timeOnTask2
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							FLY_finalData[r+1][113] = Double.toString((Double.parseDouble(FLY_finalData[r+1][113].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //trialsToLevelUp
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][12].equals("NULL")))
						{
							FLY_finalData[r+1][123] = Double.toString((Double.parseDouble(FLY_finalData[r+1][123].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //speedAtLevelUp
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(FLY_finalData[r+1][133].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							FLY_finalData[r+1][133] = organizedTable2[r5][3].toString();// Level
						if((Double.parseDouble(FLY_finalData[r+1][143].toString())) <= (Double.parseDouble(organizedTable2[r5][17].toString())))
							FLY_finalData[r+1][143] = organizedTable2[r5][17].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0) {FLY_finalData[r+1][13] = Double.toString((Double.parseDouble(FLY_finalData[r+1][13].toString())/occurrences2[0]));} //Week3 FLY_hitRateAvg
					else {FLY_finalData[r+1][13] = "";}
					
					if(occurrences2[1] != 0) {FLY_finalData[r+1][18] = Double.toString((Double.parseDouble(FLY_finalData[r+1][18].toString())/occurrences2[1]));} //Week3 FLY_missRateAvg
					else {FLY_finalData[r+1][18] = "";}
					
					FLY_finalData[r+1][23] = Double.toString((Double.parseDouble(FLY_finalData[r+1][23].toString()))); //Week3 FLY_totalPlayedAvg
					FLY_finalData[r+1][43] = Double.toString((Double.parseDouble(FLY_finalData[r+1][43].toString()))); //Week3 FLY_totalGraduatedAvg
						
					if(occurrences2[2] != 0) {FLY_finalData[r+1][53] = Double.toString((Double.parseDouble(FLY_finalData[r+1][53].toString())/occurrences2[2]));} //Week3 FLY_falsePositiveOne
					else {FLY_finalData[r+1][53] = "";}
					
					if(occurrences2[3] != 0) {FLY_finalData[r+1][63] = Double.toString((Double.parseDouble(FLY_finalData[r+1][63].toString())/occurrences2[3]));} //Week3 FLY_falsePositiveTwo
					else {FLY_finalData[r+1][63] = "";}
					
					if(occurrences2[4] != 0) {FLY_finalData[r+1][73] = Double.toString((Double.parseDouble(FLY_finalData[r+1][73].toString())/occurrences2[4]));} //Week3 FLY_falsePositiveThree
					else {FLY_finalData[r+1][73] = "";}
					
					if(occurrences2[5] != 0) {FLY_finalData[r+1][83] = Double.toString((Double.parseDouble(FLY_finalData[r+1][83].toString())/occurrences2[5]));} //Week3 FLY_falsePositiveFour
					else {FLY_finalData[r+1][83] = "";}
					
					if(occurrences2[8] != 0) {FLY_finalData[r+1][93] = Double.toString((Double.parseDouble(FLY_finalData[r+1][93].toString())/occurrences2[8]));} //Week3 FLY_timeOnTask1
					else {FLY_finalData[r+1][93] = "";}
					
					if(occurrences2[9] != 0) {FLY_finalData[r+1][103] = Double.toString((Double.parseDouble(FLY_finalData[r+1][103].toString())/occurrences2[9]));} //Week3 FLY_timeOnTask2
					else {FLY_finalData[r+1][103] = "";}
					
					if(occurrences2[6] != 0) {FLY_finalData[r+1][113] = Double.toString((Double.parseDouble(FLY_finalData[r+1][113].toString())/occurrences2[6]));} //Week3 FLY_trialsToLevelUp
					else {FLY_finalData[r+1][113] = "";}
					
					if(occurrences2[7] != 0) {FLY_finalData[r+1][123] = Double.toString((Double.parseDouble(FLY_finalData[r+1][123].toString())/occurrences2[7]));} //Week3 FLY_speedAtLevelUp
					else {FLY_finalData[r+1][123] = "";}
						
					FLY_finalData[r+1][143] = Double.toString((Double.parseDouble(FLY_finalData[r+1][143].toString()))/4); //Week3 FLY_LogSessionNumber
					FLY_finalData[r+1][153] = Double.toString((Double.parseDouble(FLY_finalData[r+1][143].toString()))/2); //Week3 FLY_NICT_SessionNumber
						
					for(int index = 0 ; index < 10 ; index++)
						occurrences2[index] = 0;
					
				}
				else
				{
					System.out.println("There is no session between 3rd labSession and 4th LabSession (Week 3) for the ID " + FLY_finalData[r+1][0].toString());
				}
				
				// Day >=  4th Lab session and Day < 5th LabSession (Week4)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][5]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][6])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+21)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+28)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][6] && r5 < r6) //While Day < 5th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+28) && r5 < r6) 
					{
						
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							FLY_finalData[r+1][14] = Double.toString((Double.parseDouble(FLY_finalData[r+1][14].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							FLY_finalData[r+1][19] = Double.toString((Double.parseDouble(FLY_finalData[r+1][19].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						FLY_finalData[r+1][24] = Double.toString((Double.parseDouble(FLY_finalData[r+1][24].toString())) + (Double.parseDouble(organizedTable2[r5][14].toString()))); //totalPlayed
						FLY_finalData[r+1][44] = Double.toString((Double.parseDouble(FLY_finalData[r+1][44].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							FLY_finalData[r+1][54] = Double.toString((Double.parseDouble(FLY_finalData[r+1][54].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //falsePositiveOne
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							FLY_finalData[r+1][64] = Double.toString((Double.parseDouble(FLY_finalData[r+1][64].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //falsePositiveTwo
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							FLY_finalData[r+1][74] = Double.toString((Double.parseDouble(FLY_finalData[r+1][74].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //falsePositiveThree
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							FLY_finalData[r+1][84] = Double.toString((Double.parseDouble(FLY_finalData[r+1][84].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //falsePositiveFour
							occurrences2[5]++;
						}						
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							FLY_finalData[r+1][94] = Double.toString((Double.parseDouble(FLY_finalData[r+1][94].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //timeOnTask1
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][16].equals("NULL")))
						{
							FLY_finalData[r+1][104] = Double.toString((Double.parseDouble(FLY_finalData[r+1][104].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //timeOnTask2
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							FLY_finalData[r+1][114] = Double.toString((Double.parseDouble(FLY_finalData[r+1][114].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //trialsToLevelUp
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][12].equals("NULL")))
						{
							FLY_finalData[r+1][124] = Double.toString((Double.parseDouble(FLY_finalData[r+1][124].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //speedAtLevelUp
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(FLY_finalData[r+1][134].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							FLY_finalData[r+1][134] = organizedTable2[r5][3].toString(); // Level
						if((Double.parseDouble(FLY_finalData[r+1][144].toString())) <= (Double.parseDouble(organizedTable2[r5][17].toString())))
							FLY_finalData[r+1][144] = organizedTable2[r5][17].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}

					if(occurrences2[0] != 0) {FLY_finalData[r+1][14] = Double.toString((Double.parseDouble(FLY_finalData[r+1][14].toString())/occurrences2[0]));} //Week4 FLY_hitRateAvg
					else {FLY_finalData[r+1][14] = "";}
					
					if(occurrences2[1] != 0) {FLY_finalData[r+1][19] = Double.toString((Double.parseDouble(FLY_finalData[r+1][19].toString())/occurrences2[1]));} //Week4 FLY_missRateAvg
					else {FLY_finalData[r+1][19] = "";}
					
					FLY_finalData[r+1][24] = Double.toString((Double.parseDouble(FLY_finalData[r+1][24].toString()))); //Week4 FLY_totalPlayedAvg
					FLY_finalData[r+1][44] = Double.toString((Double.parseDouble(FLY_finalData[r+1][44].toString()))); //Week4 FLY_totalGraduatedAvg
						
					if(occurrences2[2] != 0) {FLY_finalData[r+1][54] = Double.toString((Double.parseDouble(FLY_finalData[r+1][54].toString())/occurrences2[2]));} //Week4 FLY_falsePositiveOne
					else {FLY_finalData[r+1][54] = "";}
					
					if(occurrences2[3] != 0) {FLY_finalData[r+1][64] = Double.toString((Double.parseDouble(FLY_finalData[r+1][64].toString())/occurrences2[3]));} //Week4 FLY_falsePositiveTwo
					else {FLY_finalData[r+1][64] = "";}
					
					if(occurrences2[4] != 0) {FLY_finalData[r+1][74] = Double.toString((Double.parseDouble(FLY_finalData[r+1][74].toString())/occurrences2[4]));} //Week4 FLY_falsePositiveThree
					else {FLY_finalData[r+1][74] = "";}
					
					if(occurrences2[5] != 0) {FLY_finalData[r+1][84] = Double.toString((Double.parseDouble(FLY_finalData[r+1][84].toString())/occurrences2[5]));} //Week4 FLY_falsePositiveFour
					else {FLY_finalData[r+1][84] = "";}
					
					if(occurrences2[8] != 0) {FLY_finalData[r+1][94] = Double.toString((Double.parseDouble(FLY_finalData[r+1][94].toString())/occurrences2[8]));} //Week4 FLY_timeOnTask1
					else {FLY_finalData[r+1][94] = "";}
					
					if(occurrences2[9] != 0) {FLY_finalData[r+1][104] = Double.toString((Double.parseDouble(FLY_finalData[r+1][104].toString())/occurrences2[9]));} //Week4 FLY_timeOnTask2
					else {FLY_finalData[r+1][104] = "";}
					
					if(occurrences2[6] != 0) {FLY_finalData[r+1][114] = Double.toString((Double.parseDouble(FLY_finalData[r+1][114].toString())/occurrences2[6]));} //Week4 FLY_trialsToLevelUp
					else {FLY_finalData[r+1][114] = "";}
					
					if(occurrences2[7] != 0) {FLY_finalData[r+1][124] = Double.toString((Double.parseDouble(FLY_finalData[r+1][124].toString())/occurrences2[7]));} //Week4 FLY_speedAtLevelUp
					else {FLY_finalData[r+1][124] = "";}
						
					FLY_finalData[r+1][144] = Double.toString((Double.parseDouble(FLY_finalData[r+1][144].toString()))/4); //Week4 FLY_LogSessionNumber
					FLY_finalData[r+1][154] = Double.toString((Double.parseDouble(FLY_finalData[r+1][144].toString()))/2); //Week4 FLY_NICT_SessionNumber
						
					for(int index = 0 ; index < 10 ; index++)
						occurrences2[index] = 0;
					
				}
				else
				{
					System.out.println("There is no session between 4th labSession and 5th LabSession (Week 4) for the ID " + FLY_finalData[r+1][0].toString());
				}
				
				
				// Day >=  5th Lab session and Day < PostDate (Week5)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][6]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][7])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+28)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+35)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][7] && r5 < r6) //While Day < 5th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+35) && r5 < r6) 	
					{
						
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							FLY_finalData[r+1][15] = Double.toString((Double.parseDouble(FLY_finalData[r+1][15].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							FLY_finalData[r+1][20] = Double.toString((Double.parseDouble(FLY_finalData[r+1][20].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						FLY_finalData[r+1][25] = Double.toString((Double.parseDouble(FLY_finalData[r+1][25].toString())) + (Double.parseDouble(organizedTable2[r5][14].toString()))); //totalPlayed
						FLY_finalData[r+1][45] = Double.toString((Double.parseDouble(FLY_finalData[r+1][45].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							FLY_finalData[r+1][55] = Double.toString((Double.parseDouble(FLY_finalData[r+1][55].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //falsePositiveOne
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							FLY_finalData[r+1][65] = Double.toString((Double.parseDouble(FLY_finalData[r+1][65].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //falsePositiveTwo
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							FLY_finalData[r+1][75] = Double.toString((Double.parseDouble(FLY_finalData[r+1][75].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //falsePositiveThree
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							FLY_finalData[r+1][85] = Double.toString((Double.parseDouble(FLY_finalData[r+1][85].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //falsePositiveFour
							occurrences2[5]++;
						}						
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							FLY_finalData[r+1][95] = Double.toString((Double.parseDouble(FLY_finalData[r+1][95].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //timeOnTask1
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][16].equals("NULL")))
						{
							FLY_finalData[r+1][105] = Double.toString((Double.parseDouble(FLY_finalData[r+1][105].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //timeOnTask2
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							FLY_finalData[r+1][115] = Double.toString((Double.parseDouble(FLY_finalData[r+1][115].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //trialsToLevelUp
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][12].equals("NULL")))
						{
							FLY_finalData[r+1][125] = Double.toString((Double.parseDouble(FLY_finalData[r+1][125].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //speedAtLevelUp
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(FLY_finalData[r+1][135].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							FLY_finalData[r+1][135] = organizedTable2[r5][3].toString(); // Level
						if((Double.parseDouble(FLY_finalData[r+1][145].toString())) <= (Double.parseDouble(organizedTable2[r5][17].toString())))
							FLY_finalData[r+1][145] = organizedTable2[r5][17].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0) {FLY_finalData[r+1][15] = Double.toString((Double.parseDouble(FLY_finalData[r+1][15].toString())/occurrences2[0]));} //Week5 FLY_hitRateAvg
					else {FLY_finalData[r+1][15] = "";}
					
					if(occurrences2[1] != 0) {FLY_finalData[r+1][20] = Double.toString((Double.parseDouble(FLY_finalData[r+1][20].toString())/occurrences2[1]));} //Week5 FLY_missRateAvg
					else {FLY_finalData[r+1][20] = "";}
					
					FLY_finalData[r+1][25] = Double.toString((Double.parseDouble(FLY_finalData[r+1][25].toString()))); //Week5 FLY_totalPlayedAvg
					FLY_finalData[r+1][45] = Double.toString((Double.parseDouble(FLY_finalData[r+1][45].toString()))); //Week5 FLY_totalGraduatedAvg
						
					if(occurrences2[2] != 0) {FLY_finalData[r+1][55] = Double.toString((Double.parseDouble(FLY_finalData[r+1][55].toString())/occurrences2[2]));} //Week5 FLY_falsePositiveOne
					else {FLY_finalData[r+1][55] = "";}
					
					if(occurrences2[3] != 0) {FLY_finalData[r+1][65] = Double.toString((Double.parseDouble(FLY_finalData[r+1][65].toString())/occurrences2[3]));} //Week5 FLY_falsePositiveTwo
					else {FLY_finalData[r+1][65] = "";}
					
					if(occurrences2[4] != 0) {FLY_finalData[r+1][75] = Double.toString((Double.parseDouble(FLY_finalData[r+1][75].toString())/occurrences2[4]));} //Week5 FLY_falsePositiveThree
					else {FLY_finalData[r+1][75] = "";}
					
					if(occurrences2[5] != 0) {FLY_finalData[r+1][85] = Double.toString((Double.parseDouble(FLY_finalData[r+1][85].toString())/occurrences2[5]));} //Week5 FLY_falsePositiveFour
					else {FLY_finalData[r+1][85] = "";}
					
					if(occurrences2[8] != 0) {FLY_finalData[r+1][95] = Double.toString((Double.parseDouble(FLY_finalData[r+1][95].toString())/occurrences2[8]));} //Week5 FLY_timeOnTask1
					else {FLY_finalData[r+1][95] = "";}
					
					if(occurrences2[9] != 0) {FLY_finalData[r+1][105] = Double.toString((Double.parseDouble(FLY_finalData[r+1][105].toString())/occurrences2[9]));} //Week5 FLY_timeOnTask2
					else {FLY_finalData[r+1][105] = "";}
					
					if(occurrences2[6] != 0) {FLY_finalData[r+1][115] = Double.toString((Double.parseDouble(FLY_finalData[r+1][115].toString())/occurrences2[6]));} //Week5 FLY_trialsToLevelUp
					else {FLY_finalData[r+1][115] = "";}
					
					if(occurrences2[7] != 0) {FLY_finalData[r+1][125] = Double.toString((Double.parseDouble(FLY_finalData[r+1][125].toString())/occurrences2[7]));} //Week5 FLY_speedAtLevelUp
					else {FLY_finalData[r+1][125] = "";}
						
					FLY_finalData[r+1][145] = Double.toString((Double.parseDouble(FLY_finalData[r+1][145].toString()))/4); //Week5 FLY_LogSessionNumber
					FLY_finalData[r+1][155] = Double.toString((Double.parseDouble(FLY_finalData[r+1][145].toString()))/2); //Week5 FLY_NICT_SessionNumber
						
					for(int index = 0 ; index < 10 ; index++)
						occurrences2[index] = 0;
					
				}
				else
				{
					System.out.println("There is no session between 5th labSession and PostDate (Week 5) for the ID " + FLY_finalData[r+1][0].toString());
				}
							
			} // If found ID
			
		}
		
		
		System.out.println("\n");
		/*for(int a = 1 ; a < FLY_totalDays ; a++)
		{
			System.out.println(FLY_ID_day[a].toString() + "\t" + FLY_day[a] + "\t" + FLY_sessionLabDay[a] + "\t" + FLY_hitRateAvg_column[a] + "\t" + FLY_missRateAvg_column[a] + "\t" + FLY_totalPlayedAvg_column[a] + "\t" + FLY_totalGraduatedAvg_column[a] +
					"\t" + FLY_falsePositiveOneAvg_column[a] + "\t" + FLY_falsePositiveTwoAvg_column[a] + "\t" + FLY_falsePositiveThreeAvg_column[a] + "\t" + FLY_falsePositiveFourAvg_column[a] +
					"\t" + FLY_timeOnTask1Avg_column[a] + "\t" + FLY_timeOnTask2Avg_column[a] + "\t" + FLY_trialsToLevelUpAvg_column[a] + "\t" + FLY_speedAtLevelUpAvg_column[a]);
		}*/
		
		
		//Writing the data to be stored on an excel file; at the end, will output the entire file with all the new variables that we have calculated
		
		Sheet FLY_AVG = workbook_w.createSheet("FLY_AVG_ByID");
		Cell cell_w;
				
		for(int r = 0 ; r < FLY_usersTotal ; r++)
		{
			Row row = FLY_AVG.createRow(r);
			for(int c = 0 ; c < 162 ; c++)
			{
				if(r == 0)
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(FLY_finalData[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					if(FLY_finalData[r][c].equals(""))
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue(FLY_finalData[r][c].toString());						
					}
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(FLY_finalData[r][c].toString()));
					}
					
				}
			}
		}
		
		//Sheet with ID and Dates
		Sheet FLY_AVG2 = workbook_w.createSheet("FLY_AVG_ByDate");
		
		Row row_w = FLY_AVG2.createRow(0);
		
		cell_w = row_w.createCell(0);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("ID");
		
		cell_w = row_w.createCell(1);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("Day");
		
		cell_w = row_w.createCell(2);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("IsSessionLab");
		
		cell_w = row_w.createCell(3);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_hitRate_AVG");
		
		cell_w = row_w.createCell(4);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_missRate_AVG");
		
		cell_w = row_w.createCell(5);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_totalPlayed");
		
		cell_w = row_w.createCell(6);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_totalGraduated");
		
		cell_w = row_w.createCell(7);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_falsePositiveOne_AVG");
		
		cell_w = row_w.createCell(8);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_falsePositiveTwo_AVG");
		
		cell_w = row_w.createCell(9);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_falsePositiveThree_AVG");
		
		cell_w = row_w.createCell(10);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_falsePositiveFour_AVG");
		
		cell_w = row_w.createCell(11);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_timeOnTask1_AVG");
		
		cell_w = row_w.createCell(12);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_timeOnTask2_AVG");
		
		cell_w = row_w.createCell(13);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_trialsToLevelUp_AVG");
		
		cell_w = row_w.createCell(14);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_speedAtLevelUp_AVG");
		
		cell_w = row_w.createCell(15);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_HighestLevel");
		
		cell_w = row_w.createCell(16);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_logSessionNumber");
		
		cell_w = row_w.createCell(17);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("FLY_NICT_SessionNumber");
		
		
		
		for(int r = 1 ; r < FLY_totalDays ; r++)
		{
			Row row = FLY_AVG2.createRow(r);
			
			cell_w = row.createCell(0);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(Double.parseDouble(FLY_ID_day[r].toString()));
			
			cell_w = row.createCell(1);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(FLY_day[r]);
			
			cell_w = row.createCell(2);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(FLY_sessionLabDay[r]);
			
			cell_w = row.createCell(3);
			if(FLY_hitRateAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_hitRateAvg_column[r]);
			}
			
			cell_w = row.createCell(4);
			if(FLY_missRateAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_missRateAvg_column[r]);
			}
			
			
			cell_w = row.createCell(5);
			if(FLY_totalPlayedAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_totalPlayedAvg_column[r]);
			}
			
			
			cell_w = row.createCell(6);
			if(FLY_totalGraduatedAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_totalGraduatedAvg_column[r]);
			}
			
			cell_w = row.createCell(7);
			if(FLY_falsePositiveOneAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_falsePositiveOneAvg_column[r]);
			}
			
			cell_w = row.createCell(8);
			if(FLY_falsePositiveTwoAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_falsePositiveTwoAvg_column[r]);
			}
			
			cell_w = row.createCell(9);
			if(FLY_falsePositiveThreeAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_falsePositiveThreeAvg_column[r]);
			}
			
			cell_w = row.createCell(10);
			if(FLY_falsePositiveFourAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_falsePositiveFourAvg_column[r]);
			}
			
			cell_w = row.createCell(11);
			if(FLY_timeOnTask1Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_timeOnTask1Avg_column[r]);
			}
			
			cell_w = row.createCell(12);
			if(FLY_timeOnTask2Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_timeOnTask2Avg_column[r]);
			}
			
			cell_w = row.createCell(13);
			if(FLY_trialsToLevelUpAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(FLY_trialsToLevelUpAvg_column[r]);
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_trialsToLevelUpAvg_column[r]);
			}
			
			cell_w = row.createCell(14);
			if(FLY_speedAtLevelUpAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(FLY_speedAtLevelUpAvg_column[r]);
			}
			
			cell_w = row.createCell(15);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(FLY_levelAvg_column[r]);
			
			cell_w = row.createCell(16);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(FLY_logSession_day[r]);
			
			cell_w = row.createCell(17);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(FLY_NICT_sessionNumber_day[r]);
			
		}
		
		//Sheet with ID and Dates
		Sheet FLY_AVG3 = workbook_w.createSheet("FLY_Daily_Progress");
		
		row_w = FLY_AVG3.createRow(0);
			
		for(int _cell = 0 ; _cell < 100 ; _cell++)
		{
			if(_cell == 0)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("ID");
			}
			if(_cell == 1)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("Variable");
			}
			if(_cell > 1)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(("Day_"+ Integer.toString((_cell - 1))));
			}
		}
		
		int rows = 1; // Rows on the third sheet	
		int ID_totalDays = 1; // Store the total days for an specific ID
		int index = 1; //
		int user_row_onSessionDateSheet;
		int[] day = new int[18];
		for(int k = 0 ;  k < 18 ; k++)
		{
			day[k] = 1;
		}
		
		for(int user = 0 ; user <= FLY_usersTotal ; user++)
		{
			c_ID = FLY_ID_day[index];
			rows++;
			ID_totalDays = 0;
			while(c_ID.equals(FLY_ID_day[index].toString()))
			{
				ID_totalDays++;
				index++;
				if(index == FLY_totalDays)
					break;
			}
			for(user_row_onSessionDateSheet = 1 ;  user_row_onSessionDateSheet < sessionDate_row_num ; user_row_onSessionDateSheet++)
			{
				if(c_ID.equals(sessionDate[user_row_onSessionDateSheet][0]))
					break;
			}
			
			for(int r = 1 ; r < 18 ; r++)
			{
				Row row = FLY_AVG3.createRow(rows);
				for(int c = 0 ; c < (ID_totalDays + 2) ;  c++)
				{
					if(c == 0)
					{
						cell_w = row.createCell(c);
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(c_ID.toString()));
					}
					if(c == 1)
					{
						if(r == 1)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("IsLabSession");
						}
						if(r == 2)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("hitRate_Avg");
						}
						if(r == 3)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("missRate_Avg");
						}
						if(r == 4)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("totalPlayed");
						}
						if(r == 5)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("totalGraduated");
						}
						if(r == 6)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("falsePositiveOne_Avg");
						}
						if(r == 7)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("falsePositiveTwo_Avg");
						}
						if(r == 8)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("falsePositiveThree_Avg");
						}
						if(r == 9)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("falsePositiveFour_Avg");
						}
						if(r == 10)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("timeOnTask1_Avg");
						}
						if(r == 11)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("timeOnTask2_Avg");
						}
						if(r == 12)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("trialsToLevelUp_Avg");
						}
						if(r == 13)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("speedAtLevelUp_Avg");
						}
						if(r == 14)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("highestLevel");
						}
						if(r == 15)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("logSessioNumber");
						}
						if(r == 16)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("NICT_sessionNumber");
						}
						if(r == 17)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("Study");
						}
						
					}
					if(c > 1)
					{
						if(r == 1)
						{
							cell_w = row.createCell(c);
							if(FLY_sessionLabDay[day[0]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_sessionLabDay[day[0]]);
							}
							day[0]++;
						}
						if(r == 2)
						{
							cell_w = row.createCell(c);
							if(FLY_hitRateAvg_column[day[1]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_hitRateAvg_column[day[1]]);
							}
							day[1]++;
						}
						if(r == 3)
						{
							cell_w = row.createCell(c);
							if(FLY_missRateAvg_column[day[2]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_missRateAvg_column[day[2]]);
							}
							day[2]++;
						}
						if(r == 4)
						{
							cell_w = row.createCell(c);
							if(FLY_totalPlayedAvg_column[day[3]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_totalPlayedAvg_column[day[3]]);
							}
							day[3]++;
						}
						if(r == 5)
						{
							cell_w = row.createCell(c);
							if(FLY_totalGraduatedAvg_column[day[4]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_totalGraduatedAvg_column[day[4]]);
							}
							day[4]++;
						}
						if(r == 6)
						{
							cell_w = row.createCell(c);
							if(FLY_falsePositiveOneAvg_column[day[5]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_falsePositiveOneAvg_column[day[5]]);
							}
							day[5]++;
						}
						if(r == 7)
						{
							cell_w = row.createCell(c);
							if(FLY_falsePositiveTwoAvg_column[day[6]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_falsePositiveTwoAvg_column[day[6]]);
							}
							day[6]++;
						}
						if(r == 8)
						{
							cell_w = row.createCell(c);
							if(FLY_falsePositiveThreeAvg_column[day[7]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_falsePositiveThreeAvg_column[day[7]]);
							}
							day[7]++;
						}
						if(r == 9)
						{
							cell_w = row.createCell(c);
							if(FLY_falsePositiveFourAvg_column[day[8]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_falsePositiveFourAvg_column[day[8]]);
							}
							day[8]++;
						}
						if(r == 10)
						{
							cell_w = row.createCell(c);
							if(FLY_timeOnTask1Avg_column[day[9]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_timeOnTask1Avg_column[day[9]]);
							}
							day[9]++;
						}
						if(r == 11)
						{
							cell_w = row.createCell(c);
							if(FLY_timeOnTask2Avg_column[day[10]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_timeOnTask2Avg_column[day[10]]);
							}
							day[10]++;
						}
						if(r == 12)
						{
							cell_w = row.createCell(c);
							if(FLY_trialsToLevelUpAvg_column[day[11]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_trialsToLevelUpAvg_column[day[11]]);
							}
							day[11]++;
						}
						if(r == 13)
						{
							cell_w = row.createCell(c);
							if(FLY_speedAtLevelUpAvg_column[day[12]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_speedAtLevelUpAvg_column[day[12]]);
							}
							day[12]++;
						}
						if(r == 14)
						{
							cell_w = row.createCell(c);
							if(FLY_levelAvg_column[day[13]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_levelAvg_column[day[13]]);
							}
							day[13]++;
						}
						if(r == 15)
						{
							cell_w = row.createCell(c);
							if(FLY_logSession_day[day[14]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_logSession_day[day[14]]);
							}
							day[14]++;
						}
						if(r == 16)
						{
							cell_w = row.createCell(c);
							if(FLY_NICT_sessionNumber_day[day[15]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(FLY_NICT_sessionNumber_day[day[15]]);
							}
							day[15]++;
						}
						if(r == 17)
						{
							cell_w = row.createCell(c);
							if(FLY_day[day[6]] < sessionDate_asNum[user_row_onSessionDateSheet][7])
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("NICT");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("PostStudy");
							}
							day[16]++;
						}
					}
					
				}
				rows++;
			}			
				
		}
		
		//Finished FLY  
   }//FLY_GAME()
      
   public static void CTB_GAME_sessionDates(String[][] CTB_table, int CTB_row_num, int CTB_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
		   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
   {
		//Deleted to make more space A.P
		
   }
      
   public static void WCN_GAME_sessionDates(String[][] WCN_table, int WCN_row_num, int WCN_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
		   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
   {

		//======================================================================================================================//
		//======================================================================================================================//		
		//======================================================================================================================//
		//======================================================================================================================//
		
							/* ----------- WCN SHEET ------------ */
		
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		//======================================================================================================================//
		
		
		
		//Operations to be done on WCN sheet
		
		//Organizing the Data
		//Organizing data by ID		
		int c_position = 1; //Current position
		int oTable_row = 1; //Organized table current row
		boolean knownID[] = new boolean[100000]; // It just checks whether a desired operation has been applied to an ID
		String c_ID = new String(); //current ID
		int nextIDPosition;
			
		//Just copying the first row with the titles of each column ( ID, round, gameSession, etc...)
		for(int i = 0 ; i < WCN_col_num ; i++)
			organizedTable[0][i] = WCN_table[0][i];
		
		// Initiate all FLY_IDs as having no operation done to them.
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
		
		
		//reseting both organizedTables
		for(int r = 0 ; r < 100000 ; r++)
		{
			for(int c = 0 ; c < 50 ; c++)
			{
				organizedTable[r][c] = "null";
				organizedTable2[r][c] = "null";
			}
		}
		
		//Just copying the first row with the titles of each column ( ID, round, gameSession, etc...)
		for(int i = 0 ; i < WCN_col_num ; i++)
			organizedTable[0][i] = WCN_table[0][i];
		
		// Initiate all WCN_IDs as having no operation done to them.
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
		
		
		while(c_position < WCN_row_num)
		{
			c_ID = WCN_table[c_position][0].toString();
			for(int position = c_position ; position < WCN_row_num ; position++)
			{
				if(WCN_table[position][0] == c_ID && !(knownID[position]))
				{
					knownID[position] = true;
					for(int col = 0; col < WCN_col_num ; col++)
					{
						organizedTable[oTable_row][col] = WCN_table[position][col];						
					}
					oTable_row++;					
				}
			}
			nextIDPosition = 1;
			while(knownID[nextIDPosition] == true)
			{
				nextIDPosition++;
			}
			c_position = nextIDPosition;
		}
		//Finished organizing by ID (Works)
			
		//Finding the users
	//	System.out.println("\n\nCounting number of users...");
		String[][] WCN_IDs = new String[1000][50]; // Store all the WCN_IDs found and their given information (from the raw data) for WCN
		WCN_IDs[0][0] = organizedTable[1][0]; //Store the first ID
		WCN_IDs[0][1] = organizedTable[1][2]; //Store the round on column 1
		WCN_IDs[0][2] = organizedTable[1][1]; //Store the group on column 2
		WCN_IDs[0][3] = organizedTable[1][3]; //Store the gameSubType on column 3
		int[] WCN_userIndex = new int[1000]; //Store the row where a new ID starts, in the organized by ID data table
		int WCN_usersTotal = 0; //Total of users ID found
		WCN_userIndex[0] = 1;
		
	//	System.out.println("\nNew user found: " + WCN_IDs[0][0].toString() + "\nUser data starts on row: " + WCN_ userIndex[WCN_usersTotal]);
		
		for(int row = 2 ; row < WCN_row_num ; row++)
		{
			if(!(WCN_IDs[WCN_usersTotal][0].equals(organizedTable[row][0])))
			{
				WCN_usersTotal++;
				WCN_IDs[WCN_usersTotal][0] = organizedTable[row][0].toString();
				WCN_IDs[WCN_usersTotal][1] = organizedTable[row][2];
				WCN_IDs[WCN_usersTotal][2] = organizedTable[row][1];
				WCN_userIndex[WCN_usersTotal] = row;
			//	System.out.println("\nNew user found: " + WCN_IDs[WCN_usersTotal][0].toString() + "\nUser data starts on row: " + WCN_userIndex[WCN_usersTotal]);
			}
		}
		WCN_userIndex[(WCN_usersTotal+1)] = WCN_row_num;
		System.out.println("\nTotal number of users on WCN game: " + (WCN_usersTotal+1));
		//Finished finding users (Works)
				
		//Organizing Table by date
		//System.out.println("\n\nOrganizing table by date...");
		
		for(int i = 0 ; i < 100000 ; i++)
			knownID[i] = false;
				
		int date_column = 14; // date column in the WCN sheet
		
		// Variables to help the execution of the algorithm to organize by date
		
		int j;
		int aux = 0;
		int row2 = 1;
		int holdIndex = 1; 
		
		while(row2 < WCN_row_num && aux <= WCN_usersTotal)
		{
			j = WCN_userIndex[aux];
			while(knownID[j] && j < WCN_userIndex[aux+1])
			{
				j++;
			}
			if( j <= (WCN_userIndex[aux+1]-1))
			{
				if(organizedTable[j][date_column] != null)
				{
					double date = Double.parseDouble(organizedTable[j][date_column]);
					for(int k = WCN_userIndex[aux] ; k < WCN_userIndex[aux+1] ; k++)
					{
						if(!knownID[k])
						{
							if(date >= (Double.parseDouble(organizedTable[k][date_column])))
							{
								date = Double.parseDouble(organizedTable[k][date_column]);
								holdIndex = k;						
							}
						}
					}
					for(int w = 0 ; w < WCN_col_num ; w++)
					{
						organizedTable2[row2][w] = organizedTable[holdIndex][w];
						knownID[holdIndex] = true;
					}
					row2++;
				}
				else
				{
					continue;
				}
			}
			else
				aux++;
		}
		for(int w = 0 ; w < WCN_col_num ; w++)
		{
			organizedTable2[0][w] = organizedTable[0][w];
		}
		//Finished organizing table by date (Works)
		//Finished organizing the data
		
		
		//Averages
		//Per Day
					
		String WCN_currentID = new String();
		int WCN_currentDay = 0; //Holds the value of the current day being analyzed
		
		double WCN_hitRate = 0; // Holds the sum of WCN_hitRate as a "double" variable (can have decimals; not necessarily an integer)
		double WCN_errorRate = 0; // Holds the sum of the percentage of errorRate
		double WCN_totalPlayed = 0; // Holds the sum of the TotalPlayed
		double WCN_graduation = 0; // Holds the sum of Graduation
		double WCN_failToRespondRate1 =0; // Holds the sum of failToRespondRate1
		double WCN_failToRespondRate2 =0; // Holds the sum of failToRespondRate2
		double WCN_Speed1 =0; // Holds the sum of Speed1
		double WCN_Speed2 =0; // Holds the sum of Speed2
		double WCN_numTrialsToLevelUp =0; // Holds the sum of numTrialsToLevelUp
		double WCN_speedAtLevelUp =0; // Holds the sum of speedAtLevelUp
		double WCN_timeOnTask1 = 0; // Holds the sum of timeOnTask1 
		double WCN_timeOnTask2 = 0; // Holds the sum of timeOnTask2
		double WCN_highestLevel = 0; // Holds the highest level
		double WCN_gameSession = 0; // Holds the highest gameSession
		double WCN_Speed3 = 0; // Holds the sum of Speed3
		double WCN_Speed4 = 0; // Holds the sum of Speed4
				
		
		//double timeOfDay; // Holds the information whether it's morning, afternoon, evening or night
		
		int[] WCN_day = new int[100000]; // Hold the day ignoring the hour (Vector to create the column of ID/Day)
		String[] WCN_ID_day = new String[100000]; // Hold the ID for that held specific day (Vector to create the column of ID/Day)
		int[] WCN_sessionLabDay = new int[100000]; // Hold the status isLabSessionDay for that held specific day (for the sheet that ignores hours)
		
		
		//double[] WCN_gameSession_day = new double[100000]; //Holds the gameSession# per day
		double[] WCN_logSession_day = new double[100000]; //Holds the logSession# per day
		double[] WCN_NICT_sessionNumber_day = new double [100000];
		
		
		int[] WsessionLabDay = new int[100000]; // It checks whether it is a SessionLab day (considering the hour in the day)
		boolean WCN_isLabSession = false;
		
		double[] WCN_errorRateAvg_column = new double[100000]; // Store the average percentage of errorRate
		double[] WCN_hitRateAvg_column = new double[100000]; // Store the average of hit rate
		double[] WCN_totalPlayedAvg_column = new double[100000]; //Store the average of Total Played
		double[] WCN_graduationAvg_column = new double[100000]; //Store the total Graduated
		double[] WCN_failToRespondRate1Avg_column = new double[100000]; //Store the average of failToRespondRate1
		double[] WCN_failToRespondRate2Avg_column = new double[100000]; //Store the average of failToRespondRate2
		double[] WCN_Speed1Avg_column = new double[100000]; //Store the average of Speed1
		double[] WCN_Speed2Avg_column = new double[100000]; //Store the average of Speed2
		double[] WCN_numTrialsToLevelUpAvg_column = new double[100000]; //Store the average of trialsToLevelUp
		double[] WCN_speedAtLevelUpAvg_column = new double[100000]; //Store the average of speedAtLevelUp
		double[] WCN_timeOnTask1Avg_column = new double[100000]; //Store the average of timeOnTask1
		double[] WCN_timeOnTask2Avg_column = new double[100000]; //Store the average of timeOnTask2
		double[] WCN_levelAvg_column = new double[100000]; //Store the highest Level
		double[] WCN_Speed3Avg_column = new double[100000]; //Store the average of Speed3
		double[] WCN_Speed4Avg_column = new double[100000]; //Store the average of Speed4
		
		int[] WCN_occurrences = new int[12];
		/*
		 * occurrences[0] = hitRate
		 * occurrences[1] = erroRate
		 * occurrences[2] = failToRespondRate1
		 * occurrences[3] = failToRespondRate2
		 * occurrences[4] = speed1
		 * occurrences[5] = speed2
		 * occurrences[6] = speed3
		 * occurrences[7] = speed4
		 * occurrences[8] = numTrialsToLevelUp
		 * occurrences[9] = speedAtLevelUp
		 * occurrences[10] = timeOnTask1
		 * occurrences[11] = timeOnTask2 
		 */
		
		for(int index = 0 ; index < 12 ; index++)
			WCN_occurrences[index] = 0;
		
		int WCN_totalDays = 1;	//index of the table where the WCN_hitRate variables are stored
				
		for(int row = 1 ; row < WCN_row_num ; row++)
		{	
			if(row == 1)
			{	
				WCN_currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString()));
				WCN_currentID = organizedTable2[row][0].toString(); // Holds the current ID being analyzed
				// Holds the current day being analyzed, and converts the string to a double and then to an integer to represent the current day
				
				//Checking whether current date is a lab session				
				for(int r = 1 ; r < sessionDate_row_num ; r++)
				{
					if(WCN_currentID.equals(sessionDate[r][0]))
					{
						for( int c = 2 ; c < (sessionDate_col_num-2) ; c++)
						{
							if(WCN_currentDay == sessionDate_asNum[r][c])
							{
								WCN_isLabSession = true;
							}
						}
					}
					
				}
				if(WCN_isLabSession == true)
				{
					WsessionLabDay[row] = 1;
					//System.out.println("Found a lab session day");
				}
				else
					WsessionLabDay[row] = 2;
				//Finished checking whether it's lab session
				
				
				if(!(organizedTable2[row][4].equals("NULL")))
				{
					WCN_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
					WCN_occurrences[0]++;
				}
				if(!(organizedTable2[row][5].equals("NULL")))
				{
					WCN_errorRate += Double.parseDouble(organizedTable2[row][5].toString());
					WCN_occurrences[1]++;
				}
				if(!(organizedTable2[row][6].equals("NULL")))
				{
					WCN_failToRespondRate1 += Double.parseDouble(organizedTable2[row][6].toString());
					WCN_occurrences[2]++;
				}
				if(!(organizedTable2[row][7].equals("NULL")))
				{
					WCN_failToRespondRate2 += Double.parseDouble(organizedTable2[row][7].toString());
					WCN_occurrences[3]++;
				}
				if(!(organizedTable2[row][8].equals("NULL")))
				{
					WCN_Speed1 += Double.parseDouble(organizedTable2[row][8].toString());
					WCN_occurrences[4]++;
				}
				if(!(organizedTable2[row][9].equals("NULL")))
				{
					WCN_Speed2 += Double.parseDouble(organizedTable2[row][9].toString());
					WCN_occurrences[5]++;
				}
				if(!(organizedTable2[row][10].equals("NULL")))
				{
					WCN_Speed3 += Double.parseDouble(organizedTable2[row][10].toString());
					WCN_occurrences[6]++;
				}
				if(!(organizedTable2[row][11].equals("NULL")))
				{
					WCN_Speed4 += Double.parseDouble(organizedTable2[row][11].toString());
					WCN_occurrences[7]++;
				}
				if(!(organizedTable2[row][13].equals("NULL")))
				{
					WCN_numTrialsToLevelUp += Double.parseDouble(organizedTable2[row][13].toString());
					WCN_occurrences[8]++;
				}
				if(!(organizedTable2[row][15].equals("NULL")))
				{
					WCN_speedAtLevelUp += Double.parseDouble(organizedTable2[row][15].toString());
					WCN_occurrences[9]++;
				}
				if(!(organizedTable2[row][17].equals("NULL")))
				{
					WCN_timeOnTask1 += Double.parseDouble(organizedTable2[row][17].toString());
					WCN_occurrences[10]++;
				}
				if(!(organizedTable2[row][18].equals("NULL")))
				{
					WCN_timeOnTask2 += Double.parseDouble(organizedTable2[row][18].toString());
					WCN_occurrences[11]++;
				}
								
				WCN_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
				WCN_gameSession = Double.parseDouble(organizedTable2[row][19].toString());
				WCN_totalPlayed += Double.parseDouble(organizedTable2[row][16].toString());
				WCN_graduation += Double.parseDouble(organizedTable2[row][12].toString());
				
				// the "++" means "add one to occurrence each time"
				//reseting to false to check for the new day
				WCN_isLabSession = false;
			}
			else
			{
				
				if(WCN_currentID.equals(organizedTable2[row][0]) && ((int)(Double.parseDouble(organizedTable2[row][date_column]))) == WCN_currentDay) //Same ID and same day
				{
					if(!(organizedTable2[row][4].equals("NULL")))
					{
						WCN_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
						WCN_occurrences[0]++;
					}
					if(!(organizedTable2[row][5].equals("NULL")))
					{
						WCN_errorRate += Double.parseDouble(organizedTable2[row][5].toString());
						WCN_occurrences[1]++;
					}
					if(!(organizedTable2[row][6].equals("NULL")))
					{
						WCN_failToRespondRate1 += Double.parseDouble(organizedTable2[row][6].toString());
						WCN_occurrences[2]++;
					}
					if(!(organizedTable2[row][7].equals("NULL")))
					{
						WCN_failToRespondRate2 += Double.parseDouble(organizedTable2[row][7].toString());
						WCN_occurrences[3]++;
					}
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						WCN_Speed1 += Double.parseDouble(organizedTable2[row][8].toString());
						WCN_occurrences[4]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						WCN_Speed2 += Double.parseDouble(organizedTable2[row][9].toString());
						WCN_occurrences[5]++;
					}
					if(!(organizedTable2[row][10].equals("NULL")))
					{
						WCN_Speed3 += Double.parseDouble(organizedTable2[row][10].toString());
						WCN_occurrences[6]++;
					}
					if(!(organizedTable2[row][11].equals("NULL")))
					{
						WCN_Speed4 += Double.parseDouble(organizedTable2[row][11].toString());
						WCN_occurrences[7]++;
					}
					if(!(organizedTable2[row][13].equals("NULL")))
					{
						WCN_numTrialsToLevelUp += Double.parseDouble(organizedTable2[row][13].toString());
						WCN_occurrences[8]++;
					}
					if(!(organizedTable2[row][15].equals("NULL")))
					{
						WCN_speedAtLevelUp += Double.parseDouble(organizedTable2[row][15].toString());
						WCN_occurrences[9]++;
					}
					if(!(organizedTable2[row][17].equals("NULL")))
					{
						WCN_timeOnTask1 += Double.parseDouble(organizedTable2[row][17].toString());
						WCN_occurrences[10]++;
					}
					if(!(organizedTable2[row][18].equals("NULL")))
					{
						WCN_timeOnTask2 += Double.parseDouble(organizedTable2[row][18].toString());
						WCN_occurrences[11]++;
					}
					
					
					WCN_totalPlayed += Double.parseDouble(organizedTable2[row][16].toString());
					WCN_graduation += Double.parseDouble(organizedTable[row][12].toString());					
					if(WCN_highestLevel <= Double.parseDouble(organizedTable2[row][3].toString()))
						WCN_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
					if(WCN_gameSession <= Double.parseDouble(organizedTable2[row][19].toString()))
						WCN_gameSession = Double.parseDouble(organizedTable2[row][19].toString());
					
										
					//timeOfDay = (Double.parseDouble(organizedTable2[row][3]) - WCN_currentDay);
				
				}
				else if(WCN_currentID.equals(organizedTable2[row][0]) && ((int)(Double.parseDouble(organizedTable2[row][date_column]))) != WCN_currentDay) //Same ID but new day
				{
					WCN_ID_day[WCN_totalDays] = organizedTable2[row][0].toString();
					WCN_day[WCN_totalDays] = WCN_currentDay;
					WCN_sessionLabDay[WCN_totalDays] = WsessionLabDay[row-1];
					
					//Taking averages of the variables
					if(WCN_occurrences[0] != 0){WCN_hitRateAvg_column[WCN_totalDays] = WCN_hitRate / WCN_occurrences[0];}
					else {WCN_hitRateAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[1] != 0){WCN_errorRateAvg_column[WCN_totalDays] = WCN_errorRate / WCN_occurrences[1];}
					else {WCN_errorRateAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[2] != 0){WCN_failToRespondRate1Avg_column[WCN_totalDays] = WCN_failToRespondRate1 / WCN_occurrences[2];}
					else {WCN_failToRespondRate1Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[3] != 0){WCN_failToRespondRate2Avg_column[WCN_totalDays] = WCN_failToRespondRate2 / WCN_occurrences[3];}
					else {WCN_failToRespondRate2Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[4] != 0){WCN_Speed1Avg_column[WCN_totalDays] = WCN_Speed1 / WCN_occurrences[4];}
					else{WCN_Speed1Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[5] != 0){WCN_Speed2Avg_column[WCN_totalDays] = WCN_Speed2 / WCN_occurrences[5];}
					else {WCN_Speed2Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[6] != 0){WCN_Speed3Avg_column[WCN_totalDays] = WCN_Speed3 / WCN_occurrences[6];}
					else {WCN_Speed3Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[7] != 0){WCN_Speed4Avg_column[WCN_totalDays] = WCN_Speed4 / WCN_occurrences[7];}
					else {WCN_Speed4Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[8] != 0){WCN_numTrialsToLevelUpAvg_column[WCN_totalDays] = WCN_numTrialsToLevelUp / WCN_occurrences[8];}
					else{WCN_numTrialsToLevelUpAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[9] != 0){WCN_speedAtLevelUpAvg_column[WCN_totalDays] = WCN_speedAtLevelUp / WCN_occurrences[9];}
					else{WCN_speedAtLevelUpAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[10] != 0){WCN_timeOnTask1Avg_column[WCN_totalDays] = WCN_timeOnTask1 / WCN_occurrences[10];}
					else{WCN_timeOnTask1Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[11] != 0){WCN_timeOnTask2Avg_column[WCN_totalDays] = WCN_timeOnTask2 / WCN_occurrences[11];}
					else{WCN_timeOnTask2Avg_column[WCN_totalDays] = -1;}
					
					//Taking the sum of the variables
					WCN_totalPlayedAvg_column[WCN_totalDays] = WCN_totalPlayed; 
					WCN_graduationAvg_column[WCN_totalDays] = WCN_graduation;
					WCN_levelAvg_column[WCN_totalDays] = WCN_highestLevel;
					WCN_logSession_day[WCN_totalDays] = WCN_gameSession/4;
					WCN_NICT_sessionNumber_day[WCN_totalDays] = WCN_gameSession/8;
									
					//now resetting the variables to 0
					WCN_hitRate = 0; 
					WCN_errorRate = 0; 
					WCN_totalPlayed = 0; 
					WCN_graduation = 0; 
					WCN_failToRespondRate1 = 0;
					WCN_failToRespondRate2 = 0;
					WCN_Speed1 = 0;
					WCN_Speed2 = 0;
					WCN_numTrialsToLevelUp = 0;
					WCN_speedAtLevelUp = 0;
					WCN_timeOnTask1 = 0;
					WCN_timeOnTask2 = 0;					
					WCN_Speed3 = 0;
					WCN_Speed4 = 0;
					for(int index = 0 ; index < 12 ; index++)
						WCN_occurrences[index] = 0;
					//finished reseting variables to 0
					
					//Updating variables value if the date has now changed
					WCN_totalDays++; 
					WCN_currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString())); 
					if(!(organizedTable2[row][4].equals("NULL")))
					{
						WCN_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
						WCN_occurrences[0]++;
					}
					if(!(organizedTable2[row][5].equals("NULL")))
					{
						WCN_errorRate += Double.parseDouble(organizedTable2[row][5].toString());
						WCN_occurrences[1]++;
					}
					if(!(organizedTable2[row][6].equals("NULL")))
					{
						WCN_failToRespondRate1 += Double.parseDouble(organizedTable2[row][6].toString());
						WCN_occurrences[2]++;
					}
					if(!(organizedTable2[row][7].equals("NULL")))
					{
						WCN_failToRespondRate2 += Double.parseDouble(organizedTable2[row][7].toString());
						WCN_occurrences[3]++;
					}
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						WCN_Speed1 += Double.parseDouble(organizedTable2[row][8].toString());
						WCN_occurrences[4]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						WCN_Speed2 += Double.parseDouble(organizedTable2[row][9].toString());
						WCN_occurrences[5]++;
					}
					if(!(organizedTable2[row][10].equals("NULL")))
					{
						WCN_Speed3 += Double.parseDouble(organizedTable2[row][10].toString());
						WCN_occurrences[6]++;
					}
					if(!(organizedTable2[row][11].equals("NULL")))
					{
						WCN_Speed4 += Double.parseDouble(organizedTable2[row][11].toString());
						WCN_occurrences[7]++;
					}
					if(!(organizedTable2[row][13].equals("NULL")))
					{
						WCN_numTrialsToLevelUp += Double.parseDouble(organizedTable2[row][13].toString());
						WCN_occurrences[8]++;
					}
					if(!(organizedTable2[row][15].equals("NULL")))
					{
						WCN_speedAtLevelUp += Double.parseDouble(organizedTable2[row][15].toString());
						WCN_occurrences[9]++;
					}
					if(!(organizedTable2[row][17].equals("NULL")))
					{
						WCN_timeOnTask1 += Double.parseDouble(organizedTable2[row][17].toString());
						WCN_occurrences[10]++;
					}
					if(!(organizedTable2[row][18].equals("NULL")))
					{
						WCN_timeOnTask2 += Double.parseDouble(organizedTable2[row][18].toString());
						WCN_occurrences[11]++;
					}
					
					WCN_totalPlayed += Double.parseDouble(organizedTable2[row][16].toString());
					WCN_graduation += Double.parseDouble(organizedTable2[row][12].toString());
					WCN_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
					WCN_gameSession = Double.parseDouble(organizedTable2[row][19].toString());
					
					
					//WCN_occurrences++;
				}
				else if(!(WCN_currentID.equals(organizedTable2[row][0]))) //New ID
				{
					WCN_ID_day[WCN_totalDays] = organizedTable2[row-1][0].toString();
					WCN_day[WCN_totalDays] = WCN_currentDay;
					WCN_sessionLabDay[WCN_totalDays] = WsessionLabDay[row-1];
					
					
					//Taking averages of the variables
					if(WCN_occurrences[0] != 0){WCN_hitRateAvg_column[WCN_totalDays] = WCN_hitRate / WCN_occurrences[0];}
					else {WCN_hitRateAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[1] != 0){WCN_errorRateAvg_column[WCN_totalDays] = WCN_errorRate / WCN_occurrences[1];}
					else {WCN_errorRateAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[2] != 0){WCN_failToRespondRate1Avg_column[WCN_totalDays] = WCN_failToRespondRate1 / WCN_occurrences[2];}
					else {WCN_failToRespondRate1Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[3] != 0){WCN_failToRespondRate2Avg_column[WCN_totalDays] = WCN_failToRespondRate2 / WCN_occurrences[3];}
					else {WCN_failToRespondRate2Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[4] != 0){WCN_Speed1Avg_column[WCN_totalDays] = WCN_Speed1 / WCN_occurrences[4];}
					else{WCN_Speed1Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[5] != 0){WCN_Speed2Avg_column[WCN_totalDays] = WCN_Speed2 / WCN_occurrences[5];}
					else {WCN_Speed2Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[6] != 0){WCN_Speed3Avg_column[WCN_totalDays] = WCN_Speed3 / WCN_occurrences[6];}
					else {WCN_Speed3Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[7] != 0){WCN_Speed4Avg_column[WCN_totalDays] = WCN_Speed4 / WCN_occurrences[7];}
					else {WCN_Speed4Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[8] != 0){WCN_numTrialsToLevelUpAvg_column[WCN_totalDays] = WCN_numTrialsToLevelUp / WCN_occurrences[8];}
					else{WCN_numTrialsToLevelUpAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[9] != 0){WCN_speedAtLevelUpAvg_column[WCN_totalDays] = WCN_speedAtLevelUp / WCN_occurrences[9];}
					else{WCN_speedAtLevelUpAvg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[10] != 0){WCN_timeOnTask1Avg_column[WCN_totalDays] = WCN_timeOnTask1 / WCN_occurrences[10];}
					else{WCN_timeOnTask1Avg_column[WCN_totalDays] = -1;}
					
					if(WCN_occurrences[11] != 0){WCN_timeOnTask2Avg_column[WCN_totalDays] = WCN_timeOnTask2 / WCN_occurrences[11];}
					else{WCN_timeOnTask2Avg_column[WCN_totalDays] = -1;}
					
					//Taking the sum of the variables
					WCN_totalPlayedAvg_column[WCN_totalDays] = WCN_totalPlayed;
					WCN_graduationAvg_column[WCN_totalDays] = WCN_graduation;
					WCN_levelAvg_column[WCN_totalDays] = WCN_highestLevel;
					WCN_logSession_day[WCN_totalDays] = WCN_gameSession/4;
					WCN_NICT_sessionNumber_day[WCN_totalDays] = WCN_gameSession/8;
					
					//now resetting the variables to 0
					WCN_hitRate = 0; 
					WCN_errorRate = 0; 
					WCN_totalPlayed = 0; 
					WCN_graduation = 0; 
					WCN_failToRespondRate1 = 0;
					WCN_failToRespondRate2 = 0;
					WCN_Speed1 = 0;
					WCN_Speed2 = 0;
					WCN_numTrialsToLevelUp = 0;
					WCN_speedAtLevelUp = 0;
					WCN_timeOnTask1 = 0;
					WCN_timeOnTask2 = 0;					
					WCN_Speed3 = 0;
					WCN_Speed4 = 0;
					for(int index = 0 ; index < 12 ; index++)
						WCN_occurrences[index] = 0;
					//finished reseting variables to 0
					
					//Updating the day value
					WCN_totalDays++;
					WCN_currentID = organizedTable2[row][0].toString();
					WCN_currentDay = (int)(Double.parseDouble(organizedTable2[row][date_column].toString())); 
					
					if(!(organizedTable2[row][4].equals("NULL")))
					{
						WCN_hitRate += Double.parseDouble(organizedTable2[row][4].toString());
						WCN_occurrences[0]++;
					}
					if(!(organizedTable2[row][5].equals("NULL")))
					{
						WCN_errorRate += Double.parseDouble(organizedTable2[row][5].toString());
						WCN_occurrences[1]++;
					}
					if(!(organizedTable2[row][6].equals("NULL")))
					{
						WCN_failToRespondRate1 += Double.parseDouble(organizedTable2[row][6].toString());
						WCN_occurrences[2]++;
					}
					if(!(organizedTable2[row][7].equals("NULL")))
					{
						WCN_failToRespondRate2 += Double.parseDouble(organizedTable2[row][7].toString());
						WCN_occurrences[3]++;
					}
					if(!(organizedTable2[row][8].equals("NULL")))
					{
						WCN_Speed1 += Double.parseDouble(organizedTable2[row][8].toString());
						WCN_occurrences[4]++;
					}
					if(!(organizedTable2[row][9].equals("NULL")))
					{
						WCN_Speed2 += Double.parseDouble(organizedTable2[row][9].toString());
						WCN_occurrences[5]++;
					}
					if(!(organizedTable2[row][10].equals("NULL")))
					{
						WCN_Speed3 += Double.parseDouble(organizedTable2[row][10].toString());
						WCN_occurrences[6]++;
					}
					if(!(organizedTable2[row][11].equals("NULL")))
					{
						WCN_Speed4 += Double.parseDouble(organizedTable2[row][11].toString());
						WCN_occurrences[7]++;
					}
					if(!(organizedTable2[row][13].equals("NULL")))
					{
						WCN_numTrialsToLevelUp += Double.parseDouble(organizedTable2[row][13].toString());
						WCN_occurrences[8]++;
					}
					if(!(organizedTable2[row][15].equals("NULL")))
					{
						WCN_speedAtLevelUp += Double.parseDouble(organizedTable2[row][15].toString());
						WCN_occurrences[9]++;
					}
					if(!(organizedTable2[row][17].equals("NULL")))
					{
						WCN_timeOnTask1 += Double.parseDouble(organizedTable2[row][17].toString());
						WCN_occurrences[10]++;
					}
					if(!(organizedTable2[row][18].equals("NULL")))
					{
						WCN_timeOnTask2 += Double.parseDouble(organizedTable2[row][18].toString());
						WCN_occurrences[11]++;
					}
					
					WCN_totalPlayed += Double.parseDouble(organizedTable2[row][16].toString());
					WCN_graduation += Double.parseDouble(organizedTable2[row][12].toString());
					WCN_highestLevel = Double.parseDouble(organizedTable2[row][3].toString());
					WCN_gameSession = Double.parseDouble(organizedTable2[row][19].toString());
					
					//WCN_occurrences++;
				}
				else
				{
					System.out.println("error!!");
				}
				//Checking whether current date is a lab session
				for(int r = 1 ; r < sessionDate_row_num ; r++)
				{
					if(WCN_currentID.equals(sessionDate[r][0]))
					{
						for( int c = 2 ; c < (sessionDate_col_num-1) ; c++)
						{
							if(WCN_currentDay == sessionDate_asNum[r][c])
							{
								WCN_isLabSession = true;
							}
						}
					}
					
				}
				if(WCN_isLabSession == true)
				{
					WsessionLabDay[row] = 1;
					//System.out.println("Found a lab session day");
				}
				else
					WsessionLabDay[row] = 2;
				//Finished checking whether it's lab session
			
				//reseting to false to check for the new day
				WCN_isLabSession = false;
			}
			
		}
				
		//Creating final data table
		
		String[][] WCN_finalData = new String[100000][500]; // Table that receives the final data for WCN
		
		WCN_finalData[0][0] = "ID";
		WCN_finalData[0][1] = "Group";
		WCN_finalData[0][2] = "Round";
		WCN_finalData[0][3] = "1stLabDay";
		WCN_finalData[0][4] = "LastDay";
		WCN_finalData[0][5] = "PreTestDay";
		WCN_finalData[0][6] = "PostTestDay";
		WCN_finalData[0][7] = "1stLabDay_to_LastDay";
		WCN_finalData[0][8] = "LastDayPlayed_to_PostTestDay"; //negative number represents number of days played after the post test
		WCN_finalData[0][9] = "PreTest_to_PostTest";
		WCN_finalData[0][10] = "PreTest_to_1stLabDay";
		
		WCN_finalData[0][11] = "WCN_hitRateAvg_Week1";
		WCN_finalData[0][12] = "WCN_hitRateAvg_Week2";
		WCN_finalData[0][13] = "WCN_hitRateAvg_Week3";
		WCN_finalData[0][14] = "WCN_hitRateAvg_Week4";
		WCN_finalData[0][15] = "WCN_hitRateAvg_Week5";
		
		WCN_finalData[0][16] = "WCN_errorRateAvg_Week1";
		WCN_finalData[0][17] = "WCN_errorRateAvg_Week2";
		WCN_finalData[0][18] = "WCN_errorRateAvg_Week3";
		WCN_finalData[0][19] = "WCN_errorRateAvg_Week4";
		WCN_finalData[0][20] = "WCN_errorRateAvg_Week5";
		
		WCN_finalData[0][21] = "WCN_totalPlayed_Week1";
		WCN_finalData[0][22] = "WCN_totalPlayed_Week2";
		WCN_finalData[0][23] = "WCN_totalPlayed_Week3";
		WCN_finalData[0][24] = "WCN_totalPlayed_Week4";
		WCN_finalData[0][25] = "WCN_totalPlayed_Week5";
		
		WCN_finalData[0][26] = "WCN_hitRateLab1_Avg";
		WCN_finalData[0][27] = "WCN_hitRateLab2_Avg";
		WCN_finalData[0][28] = "WCN_hitRateLab3_Avg";
		WCN_finalData[0][29] = "WCN_hitRateLab4_Avg";
		WCN_finalData[0][30] = "WCN_hitRateLab5_Avg";
		
		WCN_finalData[0][31] = "WCN_errorRateLab1_Avg";
		WCN_finalData[0][32] = "WCN_errorRateLab2_Avg";
		WCN_finalData[0][33] = "WCN_errorRateLab3_Avg";
		WCN_finalData[0][34] = "WCN_errorRateLab4_Avg";
		WCN_finalData[0][35] = "WCN_errorRateLab5_Avg";
		
		WCN_finalData[0][36] = "WCN_totalPlayedLab1";
		WCN_finalData[0][37] = "WCN_totalPlayedLab2";
		WCN_finalData[0][38] = "WCN_totalPlayedLab3";
		WCN_finalData[0][39] = "WCN_totalPlayedLab4";
		WCN_finalData[0][40] = "WCN_totalPlayedLab5";
		
		WCN_finalData[0][41] = "WCN_graduation_Week1";
		WCN_finalData[0][42] = "WCN_graduation_Week2";
		WCN_finalData[0][43] = "WCN_graduation_Week3";
		WCN_finalData[0][44] = "WCN_graduation_Week4";
		WCN_finalData[0][45] = "WCN_graduation_Week5";
		
		WCN_finalData[0][46] = "WCN_graduation_Lab1";
		WCN_finalData[0][47] = "WCN_graduation_Lab2";
		WCN_finalData[0][48] = "WCN_graduation_Lab3";
		WCN_finalData[0][49] = "WCN_graduation_Lab4";
		WCN_finalData[0][50] = "WCN_graduation_Lab5";
		
		WCN_finalData[0][51] = "WCN_failToRespondRate1Avg_Week1";
		WCN_finalData[0][52] = "WCN_failToRespondRate1Avg_Week2";
		WCN_finalData[0][53] = "WCN_failToRespondRate1Avg_Week3";
		WCN_finalData[0][54] = "WCN_failToRespondRate1Avg_Week4";
		WCN_finalData[0][55] = "WCN_failToRespondRate1Avg_Week5";
				
		WCN_finalData[0][56] = "WCN_failToRespondRate1Lab1_Avg";
		WCN_finalData[0][57] = "WCN_failToRespondRate1Lab2_Avg";
		WCN_finalData[0][58] = "WCN_failToRespondRate1Lab3_Avg";
		WCN_finalData[0][59] = "WCN_failToRespondRate1Lab4_Avg";
		WCN_finalData[0][60] = "WCN_failToRespondRate1Lab5_Avg";
		
		WCN_finalData[0][61] = "WCN_failToRespondRate2Avg_Week1";
		WCN_finalData[0][62] = "WCN_failToRespondRate2Avg_Week2";
		WCN_finalData[0][63] = "WCN_failToRespondRate2Avg_Week3";
		WCN_finalData[0][64] = "WCN_failToRespondRate2Avg_Week4";
		WCN_finalData[0][65] = "WCN_failToRespondRate2Avg_Week5";
				
		WCN_finalData[0][66] = "WCN_failToRespondRate2Lab1_Avg";
		WCN_finalData[0][67] = "WCN_failToRespondRate2Lab2_Avg";
		WCN_finalData[0][68] = "WCN_failToRespondRate2Lab3_Avg";
		WCN_finalData[0][69] = "WCN_failToRespondRate2Lab4_Avg";
		WCN_finalData[0][70] = "WCN_failToRespondRate2Lab5_Avg";
		
		WCN_finalData[0][71] = "WCN_Speed1Avg_Week1";
		WCN_finalData[0][72] = "WCN_Speed1Avg_Week2";
		WCN_finalData[0][73] = "WCN_Speed1Avg_Week3";
		WCN_finalData[0][74] = "WCN_Speed1Avg_Week4";
		WCN_finalData[0][75] = "WCN_Speed1Avg_Week5";
				
		WCN_finalData[0][76] = "WCN_Speed1Lab1_Avg";
		WCN_finalData[0][77] = "WCN_Speed1Lab2_Avg";
		WCN_finalData[0][78] = "WCN_Speed1Lab3_Avg";
		WCN_finalData[0][79] = "WCN_Speed1Lab4_Avg";
		WCN_finalData[0][80] = "WCN_Speed1Lab5_Avg";
		
		WCN_finalData[0][81] = "WCN_Speed2Avg_Week1";
		WCN_finalData[0][82] = "WCN_Speed2Avg_Week2";
		WCN_finalData[0][83] = "WCN_Speed2Avg_Week3";
		WCN_finalData[0][84] = "WCN_Speed2Avg_Week4";
		WCN_finalData[0][85] = "WCN_Speed2Avg_Week5";
				
		WCN_finalData[0][86] = "WCN_Speed2Lab1_Avg";
		WCN_finalData[0][87] = "WCN_Speed2Lab2_Avg";
		WCN_finalData[0][88] = "WCN_Speed2Lab3_Avg";
		WCN_finalData[0][89] = "WCN_Speed2Lab4_Avg";
		WCN_finalData[0][90] = "WCN_Speed2Lab5_Avg";
		
		WCN_finalData[0][91] = "WCN_timeOnTask1Avg_Week1";
		WCN_finalData[0][92] = "WCN_timeOnTask1Avg_Week2";
		WCN_finalData[0][93] = "WCN_timeOnTask1Avg_Week3";
		WCN_finalData[0][94] = "WCN_timeOnTask1Avg_Week4";
		WCN_finalData[0][95] = "WCN_timeOnTask1Avg_Week5";
			
		WCN_finalData[0][96] = "WCN_timeOnTask1Lab1_Avg";
		WCN_finalData[0][97] = "WCN_timeOnTask1Lab2_Avg";
		WCN_finalData[0][98] = "WCN_timeOnTask1Lab3_Avg";
		WCN_finalData[0][99] = "WCN_timeOnTask1Lab4_Avg";
		WCN_finalData[0][100] = "WCN_timeOnTask1Lab5_Avg";
		
		WCN_finalData[0][101] = "WCN_timeOnTask2Avg_Week1";
		WCN_finalData[0][102] = "WCN_timeOnTask2Avg_Week2";
		WCN_finalData[0][103] = "WCN_timeOnTask2Avg_Week3";
		WCN_finalData[0][104] = "WCN_timeOnTask2Avg_Week4";
		WCN_finalData[0][105] = "WCN_timeOnTask2Avg_Week5";
				
		WCN_finalData[0][106] = "WCN_timeOnTask2Lab1_Avg";
		WCN_finalData[0][107] = "WCN_timeOnTask2Lab2_Avg";
		WCN_finalData[0][108] = "WCN_timeOnTask2Lab3_Avg";
		WCN_finalData[0][109] = "WCN_timeOnTask2Lab4_Avg";
		WCN_finalData[0][110] = "WCN_timeOnTask2Lab5_Avg";
		
		WCN_finalData[0][111] = "WCN_numTrialsToLevelUpAvg_Week1";
		WCN_finalData[0][112] = "WCN_numTrialsToLevelUpAvg_Week2";
		WCN_finalData[0][113] = "WCN_numTrialsToLevelUpAvg_Week3";
		WCN_finalData[0][114] = "WCN_numTrialsToLevelUpAvg_Week4";
		WCN_finalData[0][115] = "WCN_numTrialsToLevelUpAvg_Week5";
				
		WCN_finalData[0][116] = "WCN_numTrialsToLevelUpLab1_Avg";
		WCN_finalData[0][117] = "WCN_numTrialsToLevelUpLab2_Avg";
		WCN_finalData[0][118] = "WCN_numTrialsToLevelUpLab3_Avg";
		WCN_finalData[0][119] = "WCN_numTrialsToLevelUpLab4_Avg";
		WCN_finalData[0][120] = "WCN_numTrialsToLevelUpLab5_Avg";
		
		WCN_finalData[0][121] = "WCN_speedAtLevelUpAvg_Week1";
		WCN_finalData[0][122] = "WCN_speedAtLevelUpAvg_Week2";
		WCN_finalData[0][123] = "WCN_speedAtLevelUpAvg_Week3";
		WCN_finalData[0][124] = "WCN_speedAtLevelUpAvg_Week4";
		WCN_finalData[0][125] = "WCN_speedAtLevelUpAvg_Week5";
				
		WCN_finalData[0][126] = "WCN_speedAtLevelUpLab1_Avg";
		WCN_finalData[0][127] = "WCN_speedAtLevelUpLab2_Avg";
		WCN_finalData[0][128] = "WCN_speedAtLevelUpLab3_Avg";
		WCN_finalData[0][129] = "WCN_speedAtLevelUpLab4_Avg";
		WCN_finalData[0][130] = "WCN_speedAtLevelUpLab5_Avg";
		
		
		WCN_finalData[0][131] = "WCN_highestLevel_Week1";
		WCN_finalData[0][132] = "WCN_highestLevel_Week2";
		WCN_finalData[0][133] = "WCN_highestLevel_Week3";
		WCN_finalData[0][134] = "WCN_highestLevel_Week4";
		WCN_finalData[0][135] = "WCN_highestLevel_Week5";
				
		WCN_finalData[0][136] = "WCN_highestLevel_Lab1";
		WCN_finalData[0][137] = "WCN_highestLevel_Lab2";
		WCN_finalData[0][138] = "WCN_highestLevel_Lab3";
		WCN_finalData[0][139] = "WCN_highestLevel_Lab4";
		WCN_finalData[0][140] = "WCN_highestLevel_Lab5";
		
		WCN_finalData[0][141] = "WCN_LogSessionNumber_Week1";
		WCN_finalData[0][142] = "WCN_LogSessionNumber_Week2";
		WCN_finalData[0][143] = "WCN_LogSessionNumber_Week3";
		WCN_finalData[0][144] = "WCN_LogSessionNumber_Week4";
		WCN_finalData[0][145] = "WCN_LogSessionNumber_Week5";
				
		WCN_finalData[0][146] = "WCN_LogSessionNumber_Lab1";
		WCN_finalData[0][147] = "WCN_LogSessionNumber_Lab2";
		WCN_finalData[0][148] = "WCN_LogSessionNumber_Lab3";
		WCN_finalData[0][149] = "WCN_LogSessionNumber_Lab4";
		WCN_finalData[0][150] = "WCN_LogSessionNumber_Lab5";
		
		WCN_finalData[0][151] = "WCN_NICT_SessionNumber_Week1";
		WCN_finalData[0][152] = "WCN_NICT_SessionNumber_Week2";
		WCN_finalData[0][153] = "WCN_NICT_SessionNumber_Week3";
		WCN_finalData[0][154] = "WCN_NICT_SessionNumber_Week4";
		WCN_finalData[0][155] = "WCN_NICT_SessionNumber_Week5";
				
		WCN_finalData[0][156] = "WCN_NICT_SessionNumber_Lab1";
		WCN_finalData[0][157] = "WCN_NICT_SessionNumber_Lab2";
		WCN_finalData[0][158] = "WCN_NICT_SessionNumber_Lab3";
		WCN_finalData[0][159] = "WCN_NICT_SessionNumber_Lab4";
		WCN_finalData[0][160] = "WCN_NICT_SessionNumber_Lab5";
		
		WCN_finalData[0][161] = "WCN_Speed3Avg_Week1";
		WCN_finalData[0][162] = "WCN_Speed3Avg_Week2";
		WCN_finalData[0][163] = "WCN_Speed3Avg_Week3";
		WCN_finalData[0][164] = "WCN_Speed3Avg_Week4";
		WCN_finalData[0][165] = "WCN_Speed3Avg_Week5";
				
		WCN_finalData[0][166] = "WCN_Speed3Lab1_Avg";
		WCN_finalData[0][167] = "WCN_Speed3Lab2_Avg";
		WCN_finalData[0][168] = "WCN_Speed3Lab3_Avg";
		WCN_finalData[0][169] = "WCN_Speed3Lab4_Avg";
		WCN_finalData[0][170] = "WCN_Speed3Lab5_Avg";
		
		WCN_finalData[0][171] = "WCN_Speed4Avg_Week1";
		WCN_finalData[0][172] = "WCN_Speed4Avg_Week2";
		WCN_finalData[0][173] = "WCN_Speed4Avg_Week3";
		WCN_finalData[0][174] = "WCN_Speed4Avg_Week4";
		WCN_finalData[0][175] = "WCN_Speed4Avg_Week5";
			
		WCN_finalData[0][176] = "WCN_Speed4Lab1_Avg";
		WCN_finalData[0][177] = "WCN_Speed4Lab2_Avg";
		WCN_finalData[0][178] = "WCN_Speed4Lab3_Avg";
		WCN_finalData[0][179] = "WCN_Speed4Lab4_Avg";
		WCN_finalData[0][180] = "WCN_Speed4Lab5_Avg";
		
		WCN_finalData[0][181] = "LastDayBefore_PostDate";
		
		
		//Finding ID's group, round, 1st lab day, last day, pre_test day, and post_test day (give data) for all WCN IDs
		boolean WCN_foundID = false;
		String WCN_tempID = new String();

		for(int r = 0 ; r <= WCN_usersTotal ; r++)
		{
			WCN_tempID = WCN_IDs[r][0].toString();
			//Look for the ID in the session date table
			int r2 = 1; // Holds the row for the ID in sessionDate sheet
			int r5 = 0; // Holds the row where the ID is found on OrganizedTable 2 ( Table organized by ID and by Date)
			int r6 = 0; // Holds the last row for the ID 
			WCN_foundID = false;
			int[] occurrences2 = new int[12];
			
			do{
				r5++;
				if(WCN_tempID.equals(organizedTable2[r5][0].toString()))
					WCN_foundID = true;
				
			}while(!(WCN_tempID.equals(organizedTable2[r5][0].toString())) && r5 < WCN_row_num && !(WCN_foundID));	
			
			r6 =r5;
			
			while(WCN_tempID.equals(organizedTable2[r6][0].toString()) && r6 < WCN_row_num)
			{
				r6++; //r6 stops one row after the last row for the ID ( always use < r6, not <= r6)
			}
				
			WCN_foundID = false;	
			while((!(WCN_tempID.equals(sessionDate[r2][0].toString()))) && (r2 < (sessionDate_row_num - 1))) //Searching for the ID in the sessionDate Sheet (Try to improve with a do/while)
			{
				r2++;
			}
			if(r2 < sessionDate_row_num && (r2 != sessionDate_row_num - 1)) //Checking whether the ID was found.
			{
				WCN_foundID = true;
			}
			else
			{
				System.out.println("ID: " + WCN_tempID + " not found on session date sheet.");
			}
			if(WCN_foundID)
			{
				WCN_finalData[r+1][0] = WCN_tempID.toString(); // current ID
				WCN_finalData[r+1][1] = WCN_IDs[r][1].toString(); // Group
				WCN_finalData[r+1][2] = WCN_IDs[r][2].toString(); // Round
				WCN_finalData[r+1][3] = sessionDate[r2][2].toString(); // First lab day
				WCN_finalData[r+1][5] = sessionDate[r2][1].toString(); // ID Pre_test day
				WCN_finalData[r+1][6] = sessionDate[r2][7].toString(); // ID Post_test day
				
				//Finding the last day for the ID
				WCN_foundID = false;
				int firstDayRow=0; //Find the first day row for the ID in the vector WCN_ID_day;
				int lastDayRow=0; // Find the last day row for the ID in the vector WCN_ID_day;
				for(int r3 = 1 ; r3 < WCN_totalDays && !WCN_foundID ; r3++)
				{
					if(WCN_tempID.equals(WCN_ID_day[r3]))
					{
						WCN_foundID = true;
						firstDayRow = r3;
						while(WCN_tempID.equals(WCN_ID_day[r3]))
						{
							r3++;
						}
						WCN_finalData[r+1][4] = Integer.toString(WCN_day[r3-1]);
						lastDayRow = (r3-1);
					}
				}
				//Finished finding the last day for the ID
				
				WCN_finalData[r+1][7] = Integer.toString((Integer.parseInt(WCN_finalData[r+1][4].toString()) - Integer.parseInt(WCN_finalData[r+1][3].toString()))); 
				WCN_finalData[r+1][8] = Integer.toString((Integer.parseInt(WCN_finalData[r+1][6].toString()) - Integer.parseInt(WCN_finalData[r+1][4].toString())));
				WCN_finalData[r+1][9] = Integer.toString((Integer.parseInt(WCN_finalData[r+1][6].toString()) - Integer.parseInt(WCN_finalData[r+1][5].toString())));
				WCN_finalData[r+1][10] = Integer.toString((Integer.parseInt(WCN_finalData[r+1][3].toString()) - Integer.parseInt(WCN_finalData[r+1][5].toString())));
				
				for(int col = 11 ; col <= 181 ; col++)
				{
					WCN_finalData[r+1][col] = "0";
					// columns 11, 12, 13, 14, 15 / WCN_hitRate week1Avg, week2Avg, week3Avg, week4Avg, week5Avg
				}
				
				int r4 = firstDayRow;
				while( (WCN_day[r4] < Integer.parseInt(WCN_finalData[r+1][6])) && r4 <= lastDayRow) //Checking if the last day is bigger than postDate
				{
					r4++;
				}
				if(r4 <= lastDayRow)
				{
					//WCN_finalData[r+1][8] = Integer.toString((Integer.parseInt(WCN_finalData[r+1][6].toString()) - WCN_day[r4-1]));
					WCN_finalData[r+1][181] = Integer.toString(WCN_day[r4]);
				}
				
				//Lab Averages
				r4 = firstDayRow;
				
				if((WCN_day[r4]) < Integer.parseInt(sessionDate[r2][2].toString())) //First day <=  1st Lab session
				{
					//System.out.println("Error: There is activity before the 1st labSession for the ID " + WCN_finalData[r+1][0].toString());
					while(WCN_day[r4] <= sessionDate_asNum[r2][2] && r4 <= lastDayRow)
						r4++;
				}
				
				// Day >=  1st Lab session and Day < 2nd LabSession (Week1)
				if((WCN_day[r4] >= sessionDate_asNum[r2][2]) && (WCN_day[r4] < sessionDate_asNum[r2][3])) 
				{
					while((WCN_day[r4]) < sessionDate_asNum[r2][3] && r4 < lastDayRow) //While Day < 2nd Lab session date
					{
						if(WCN_sessionLabDay[r4] == 1)
						{
							if(WCN_hitRateAvg_column[r4] == -1){WCN_finalData[r+1][26] = "";}
							else {WCN_finalData[r+1][26] = Double.toString(WCN_hitRateAvg_column[r4]);} // WCN_hitRate 1stLabAvg 
							
							if (WCN_errorRateAvg_column[r4] == -1) { WCN_finalData[r+1][31] = "";}
							else {WCN_finalData[r+1][31] = Double.toString(WCN_errorRateAvg_column[r4]);} // WCN_errorRate 1stLabAvg 
							
							WCN_finalData[r+1][36] = Double.toString(WCN_totalPlayedAvg_column[r4]); // WCN_totalPlayed 1stLabAvg
							WCN_finalData[r+1][46] = Double.toString(WCN_graduationAvg_column[r4]); // WCN_graduation 1stLabAvg 
							
							if (WCN_failToRespondRate1Avg_column[r4] == -1) {WCN_finalData[r+1][56] = "";}
							else {WCN_finalData[r+1][56] = Double.toString(WCN_failToRespondRate1Avg_column[r4]);} // WCN_failToRespondRate1 1stLabAvg 
							
							if(WCN_failToRespondRate2Avg_column[r4] == -1) {WCN_finalData[r+1][66] = "";}
							else {WCN_finalData[r+1][66] = Double.toString(WCN_failToRespondRate2Avg_column[r4]);} // WCN_failToRespondRate2 1stLabAvg 
							
							if(WCN_Speed1Avg_column[r4] == -1) {WCN_finalData[r+1][76] = "";} 
							else {WCN_finalData[r+1][76] = Double.toString(WCN_Speed1Avg_column[r4]);} // WCN_Speed1 1stLabAvg
							
							if(WCN_Speed2Avg_column[r4] == -1) {WCN_finalData[r+1][86] = "";}
							else {WCN_finalData[r+1][86] = Double.toString(WCN_Speed2Avg_column[r4]);} // WCN_Speed2 1stLabAvg 
							
							if(WCN_timeOnTask1Avg_column[r4] == -1) {WCN_finalData[r+1][96] = "";}
							else {WCN_finalData[r+1][96] = Double.toString(WCN_timeOnTask1Avg_column[r4]);} // WCN_timeOnTask1 1stLabAvg 
							
							if(WCN_timeOnTask2Avg_column[r4] == -1) {WCN_finalData[r+1][106] = "";}
							else {WCN_finalData[r+1][106] = Double.toString(WCN_timeOnTask2Avg_column[r4]);} // WCN_timeOnTask2 1stLabAvg 
							
							if(WCN_numTrialsToLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][116] = "";}
							else {WCN_finalData[r+1][116] = Double.toString(WCN_numTrialsToLevelUpAvg_column[r4]);} // WCN_numTrialsToLevelUp 1stLabAvg
							
							if(WCN_speedAtLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][126] = "";}
							else {WCN_finalData[r+1][126] = Double.toString(WCN_speedAtLevelUpAvg_column[r4]);} // WCN_speedAtLevelUp 1stLabAvg 
							
							WCN_finalData[r+1][136] = Double.toString(WCN_levelAvg_column[r4]); //WCN_highestLevel 1stLab
							WCN_finalData[r+1][146] = Double.toString(WCN_logSession_day[r4]); //WCN_logSession 1stLab
							WCN_finalData[r+1][156] = Double.toString(WCN_NICT_sessionNumber_day[r4]); //WCN_NICT_sessionNumber 1stLab
							
							if(WCN_Speed3Avg_column[r4] == -1) {WCN_finalData[r+1][166] = "";}
							else {WCN_finalData[r+1][166] = Double.toString(WCN_Speed3Avg_column[r4]);} // WCN_Speed3 1stLabAvg
							
							if(WCN_Speed4Avg_column[r4] == -1) {WCN_finalData[r+1][176] = "";}
							else {WCN_finalData[r+1][176] = Double.toString(WCN_Speed4Avg_column[r4]);} // WCN_Speed4 1stLabAvg 
						}
						r4++;
					}
					
				}
				
				// Day >=  2nd Lab session and Day < 3rd LabSession (Week2)
				if((WCN_day[r4] >= sessionDate_asNum[r2][3]) && (WCN_day[r4] < sessionDate_asNum[r2][4])) 
				{
					while( WCN_day[r4] < sessionDate_asNum[r2][4] && r4 < lastDayRow) //While Day < 3rd Lab session date
					{						
						if(WCN_sessionLabDay[r4] == 1)
						{
							if(WCN_hitRateAvg_column[r4] == -1){WCN_finalData[r+1][27] = "";}
							else {WCN_finalData[r+1][27] = Double.toString(WCN_hitRateAvg_column[r4]);} // WCN_hitRate 2ndLabAvg 
							
							if (WCN_errorRateAvg_column[r4] == -1) { WCN_finalData[r+1][32] = "";}
							else {WCN_finalData[r+1][32] = Double.toString(WCN_errorRateAvg_column[r4]);} // WCN_errorRate 2ndLabAvg 
							
							WCN_finalData[r+1][37] = Double.toString(WCN_totalPlayedAvg_column[r4]); // WCN_totalPlayed 2ndLabAvg
							WCN_finalData[r+1][47] = Double.toString(WCN_graduationAvg_column[r4]); // WCN_graduation 2ndLabAvg 
							
							if (WCN_failToRespondRate1Avg_column[r4] == -1) {WCN_finalData[r+1][57] = "";}
							else {WCN_finalData[r+1][57] = Double.toString(WCN_failToRespondRate1Avg_column[r4]);} // WCN_failToRespondRate1 2ndLabAvg 
							
							if(WCN_failToRespondRate2Avg_column[r4] == -1) {WCN_finalData[r+1][67] = "";}
							else {WCN_finalData[r+1][67] = Double.toString(WCN_failToRespondRate2Avg_column[r4]);} // WCN_failToRespondRate2 2ndLabAvg 
							
							if(WCN_Speed1Avg_column[r4] == -1) {WCN_finalData[r+1][77] = "";} 
							else {WCN_finalData[r+1][77] = Double.toString(WCN_Speed1Avg_column[r4]);} // WCN_Speed1 2ndLabAvg
							
							if(WCN_Speed2Avg_column[r4] == -1) {WCN_finalData[r+1][87] = "";}
							else {WCN_finalData[r+1][87] = Double.toString(WCN_Speed2Avg_column[r4]);} // WCN_Speed2 2ndLabAvg 
							
							if(WCN_timeOnTask1Avg_column[r4] == -1) {WCN_finalData[r+1][97] = "";}
							else {WCN_finalData[r+1][97] = Double.toString(WCN_timeOnTask1Avg_column[r4]);} // WCN_timeOnTask1 2ndLabAvg 
							
							if(WCN_timeOnTask2Avg_column[r4] == -1) {WCN_finalData[r+1][107] = "";}
							else {WCN_finalData[r+1][107] = Double.toString(WCN_timeOnTask2Avg_column[r4]);} // WCN_timeOnTask2 2ndLabAvg 
							
							if(WCN_numTrialsToLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][117] = "";}
							else {WCN_finalData[r+1][117] = Double.toString(WCN_numTrialsToLevelUpAvg_column[r4]);} // WCN_numTrialsToLevelUp 2ndLabAvg
							
							if(WCN_speedAtLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][127] = "";}
							else {WCN_finalData[r+1][127] = Double.toString(WCN_speedAtLevelUpAvg_column[r4]);} // WCN_speedAtLevelUp 2ndLabAvg 
							
							WCN_finalData[r+1][137] = Double.toString(WCN_levelAvg_column[r4]); //WCN_highestLevel 2ndLab
							WCN_finalData[r+1][147] = Double.toString(WCN_logSession_day[r4]); //WCN_logSession 2ndLab
							WCN_finalData[r+1][157] = Double.toString(WCN_NICT_sessionNumber_day[r4]); //WCN_NICT_sessionNumber 2ndLab
							
							if(WCN_Speed3Avg_column[r4] == -1) {WCN_finalData[r+1][167] = "";}
							else {WCN_finalData[r+1][167] = Double.toString(WCN_Speed3Avg_column[r4]);} // WCN_Speed3 2ndLabAvg
							
							if(WCN_Speed4Avg_column[r4] == -1) {WCN_finalData[r+1][177] = "";}
							else {WCN_finalData[r+1][177] = Double.toString(WCN_Speed4Avg_column[r4]);} // WCN_Speed4 2ndLabAvg
							
						}
						r4++;
					}
				}
				
				// Day >=  3rd Lab session and Day < 4th LabSession (Week3)
				if((WCN_day[r4] >= sessionDate_asNum[r2][4]) && (WCN_day[r4] < sessionDate_asNum[r2][5])) 
				{
					while(WCN_day[r4] < sessionDate_asNum[r2][5] && r4 < lastDayRow) //While Day < 4th Lab session date
					{
						if(WCN_sessionLabDay[r4] == 1)
						{
							if(WCN_hitRateAvg_column[r4] == -1){WCN_finalData[r+1][28] = "";}
							else {WCN_finalData[r+1][28] = Double.toString(WCN_hitRateAvg_column[r4]);} // WCN_hitRate 3rdLabAvg 
							
							if (WCN_errorRateAvg_column[r4] == -1) { WCN_finalData[r+1][33] = "";}
							else {WCN_finalData[r+1][33] = Double.toString(WCN_errorRateAvg_column[r4]);} // WCN_errorRate 3rdLabAvg 
							
							WCN_finalData[r+1][38] = Double.toString(WCN_totalPlayedAvg_column[r4]); // WCN_totalPlayed 3rdLabAvg
							WCN_finalData[r+1][48] = Double.toString(WCN_graduationAvg_column[r4]); // WCN_graduation 3rdLabAvg 
							
							if (WCN_failToRespondRate1Avg_column[r4] == -1) {WCN_finalData[r+1][58] = "";}
							else {WCN_finalData[r+1][58] = Double.toString(WCN_failToRespondRate1Avg_column[r4]);} // WCN_failToRespondRate1 3rdLabAvg 
							
							if(WCN_failToRespondRate2Avg_column[r4] == -1) {WCN_finalData[r+1][68] = "";}
							else {WCN_finalData[r+1][68] = Double.toString(WCN_failToRespondRate2Avg_column[r4]);} // WCN_failToRespondRate2 3rdLabAvg 
							
							if(WCN_Speed1Avg_column[r4] == -1) {WCN_finalData[r+1][78] = "";} 
							else {WCN_finalData[r+1][78] = Double.toString(WCN_Speed1Avg_column[r4]);} // WCN_Speed1 3rdLabAvg
							
							if(WCN_Speed2Avg_column[r4] == -1) {WCN_finalData[r+1][88] = "";}
							else {WCN_finalData[r+1][88] = Double.toString(WCN_Speed2Avg_column[r4]);} // WCN_Speed2 3rdLabAvg 
							
							if(WCN_timeOnTask1Avg_column[r4] == -1) {WCN_finalData[r+1][98] = "";}
							else {WCN_finalData[r+1][98] = Double.toString(WCN_timeOnTask1Avg_column[r4]);} // WCN_timeOnTask1 3rdLabAvg 
							
							if(WCN_timeOnTask2Avg_column[r4] == -1) {WCN_finalData[r+1][108] = "";}
							else {WCN_finalData[r+1][108] = Double.toString(WCN_timeOnTask2Avg_column[r4]);} // WCN_timeOnTask2 3rdLabAvg 
							
							if(WCN_numTrialsToLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][118] = "";}
							else {WCN_finalData[r+1][118] = Double.toString(WCN_numTrialsToLevelUpAvg_column[r4]);} // WCN_numTrialsToLevelUp 3rdLabAvg
							
							if(WCN_speedAtLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][128] = "";}
							else {WCN_finalData[r+1][128] = Double.toString(WCN_speedAtLevelUpAvg_column[r4]);} // WCN_speedAtLevelUp 3rdLabAvg 
							
							WCN_finalData[r+1][138] = Double.toString(WCN_levelAvg_column[r4]); //WCN_highestLevel 3rdLab
							WCN_finalData[r+1][148] = Double.toString(WCN_logSession_day[r4]); //WCN_logSession 3rdLab
							WCN_finalData[r+1][158] = Double.toString(WCN_NICT_sessionNumber_day[r4]); //WCN_NICT_sessionNumber 3rdLab
							
							if(WCN_Speed3Avg_column[r4] == -1) {WCN_finalData[r+1][168] = "";}
							else {WCN_finalData[r+1][168] = Double.toString(WCN_Speed3Avg_column[r4]);} // WCN_Speed3 3rdLabAvg
							
							if(WCN_Speed4Avg_column[r4] == -1) {WCN_finalData[r+1][178] = "";}
							else {WCN_finalData[r+1][178] = Double.toString(WCN_Speed4Avg_column[r4]);} // WCN_Speed4 3rdLabAvg
						}
						r4++;
					}
				}
				
				// Day >=  4th Lab session and Day < 5th LabSession (Week4)
				if((WCN_day[r4] >= sessionDate_asNum[r2][5]) && (WCN_day[r4] < sessionDate_asNum[r2][6])) 
				{
					while(WCN_day[r4] < sessionDate_asNum[r2][6] && r4 < lastDayRow ) //While Day < 5th Lab session date
					{
						if(WCN_sessionLabDay[r4] == 1)
						{
							if(WCN_hitRateAvg_column[r4] == -1){WCN_finalData[r+1][29] = "";}
							else {WCN_finalData[r+1][29] = Double.toString(WCN_hitRateAvg_column[r4]);} // WCN_hitRate 4thLabAvg 
							
							if (WCN_errorRateAvg_column[r4] == -1) { WCN_finalData[r+1][34] = "";}
							else {WCN_finalData[r+1][34] = Double.toString(WCN_errorRateAvg_column[r4]);} // WCN_errorRate 4thLabAvg 
							
							WCN_finalData[r+1][39] = Double.toString(WCN_totalPlayedAvg_column[r4]); // WCN_totalPlayed 4thLabAvg
							WCN_finalData[r+1][49] = Double.toString(WCN_graduationAvg_column[r4]); // WCN_graduation 4thLabAvg 
							
							if (WCN_failToRespondRate1Avg_column[r4] == -1) {WCN_finalData[r+1][59] = "";}
							else {WCN_finalData[r+1][59] = Double.toString(WCN_failToRespondRate1Avg_column[r4]);} // WCN_failToRespondRate1 4thLabAvg 
							
							if(WCN_failToRespondRate2Avg_column[r4] == -1) {WCN_finalData[r+1][69] = "";}
							else {WCN_finalData[r+1][69] = Double.toString(WCN_failToRespondRate2Avg_column[r4]);} // WCN_failToRespondRate2 4thLabAvg 
							
							if(WCN_Speed1Avg_column[r4] == -1) {WCN_finalData[r+1][79] = "";} 
							else {WCN_finalData[r+1][79] = Double.toString(WCN_Speed1Avg_column[r4]);} // WCN_Speed1 4thLabAvg
							
							if(WCN_Speed2Avg_column[r4] == -1) {WCN_finalData[r+1][89] = "";}
							else {WCN_finalData[r+1][89] = Double.toString(WCN_Speed2Avg_column[r4]);} // WCN_Speed2 4thLabAvg 
							
							if(WCN_timeOnTask1Avg_column[r4] == -1) {WCN_finalData[r+1][99] = "";}
							else {WCN_finalData[r+1][99] = Double.toString(WCN_timeOnTask1Avg_column[r4]);} // WCN_timeOnTask1 4thLabAvg 
							
							if(WCN_timeOnTask2Avg_column[r4] == -1) {WCN_finalData[r+1][109] = "";}
							else {WCN_finalData[r+1][109] = Double.toString(WCN_timeOnTask2Avg_column[r4]);} // WCN_timeOnTask2 4thLabAvg 
							
							if(WCN_numTrialsToLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][119] = "";}
							else {WCN_finalData[r+1][119] = Double.toString(WCN_numTrialsToLevelUpAvg_column[r4]);} // WCN_numTrialsToLevelUp 4thLabAvg
							
							if(WCN_speedAtLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][129] = "";}
							else {WCN_finalData[r+1][129] = Double.toString(WCN_speedAtLevelUpAvg_column[r4]);} // WCN_speedAtLevelUp 4thLabAvg 
							
							WCN_finalData[r+1][139] = Double.toString(WCN_levelAvg_column[r4]); //WCN_highestLevel 4thLab
							WCN_finalData[r+1][149] = Double.toString(WCN_logSession_day[r4]); //WCN_logSession 4thLab
							WCN_finalData[r+1][159] = Double.toString(WCN_NICT_sessionNumber_day[r4]); //WCN_NICT_sessionNumber 4thLab
							
							if(WCN_Speed3Avg_column[r4] == -1) {WCN_finalData[r+1][169] = "";}
							else {WCN_finalData[r+1][169] = Double.toString(WCN_Speed3Avg_column[r4]);} // WCN_Speed3 4thLabAvg
							
							if(WCN_Speed4Avg_column[r4] == -1) {WCN_finalData[r+1][179] = "";}
							else {WCN_finalData[r+1][179] = Double.toString(WCN_Speed4Avg_column[r4]);} // WCN_Speed4 4thLabAvg
						}
						r4++;
					}
				}
				
				// Day >=  5th Lab session and Day <= postDate (Week5)				
				if((WCN_day[r4] >= sessionDate_asNum[r2][6]) && (WCN_day[r4] <= sessionDate_asNum[r2][7])) 
				{
					while(WCN_day[r4] < sessionDate_asNum[r2][7] && r4 < lastDayRow) //While Day < postdate
					{
						if(WCN_sessionLabDay[r4] == 1)
						{
							if(WCN_hitRateAvg_column[r4] == -1){WCN_finalData[r+1][30] = "";}
							else {WCN_finalData[r+1][30] = Double.toString(WCN_hitRateAvg_column[r4]);} // WCN_hitRate 5thLabAvg 
							
							if (WCN_errorRateAvg_column[r4] == -1) { WCN_finalData[r+1][35] = "";}
							else {WCN_finalData[r+1][35] = Double.toString(WCN_errorRateAvg_column[r4]);} // WCN_errorRate 5thLabAvg 
							
							WCN_finalData[r+1][40] = Double.toString(WCN_totalPlayedAvg_column[r4]); // WCN_totalPlayed 5thLabAvg
							WCN_finalData[r+1][50] = Double.toString(WCN_graduationAvg_column[r4]); // WCN_graduation 5thLabAvg 
							
							if (WCN_failToRespondRate1Avg_column[r4] == -1) {WCN_finalData[r+1][60] = "";}
							else {WCN_finalData[r+1][60] = Double.toString(WCN_failToRespondRate1Avg_column[r4]);} // WCN_failToRespondRate1 5thLabAvg 
							
							if(WCN_failToRespondRate2Avg_column[r4] == -1) {WCN_finalData[r+1][70] = "";}
							else {WCN_finalData[r+1][70] = Double.toString(WCN_failToRespondRate2Avg_column[r4]);} // WCN_failToRespondRate2 5thLabAvg 
							
							if(WCN_Speed1Avg_column[r4] == -1) {WCN_finalData[r+1][80] = "";} 
							else {WCN_finalData[r+1][80] = Double.toString(WCN_Speed1Avg_column[r4]);} // WCN_Speed1 5thLabAvg
							
							if(WCN_Speed2Avg_column[r4] == -1) {WCN_finalData[r+1][90] = "";}
							else {WCN_finalData[r+1][90] = Double.toString(WCN_Speed2Avg_column[r4]);} // WCN_Speed2 5thLabAvg 
							
							if(WCN_timeOnTask1Avg_column[r4] == -1) {WCN_finalData[r+1][100] = "";}
							else {WCN_finalData[r+1][100] = Double.toString(WCN_timeOnTask1Avg_column[r4]);} // WCN_timeOnTask1 5thLabAvg 
							
							if(WCN_timeOnTask2Avg_column[r4] == -1) {WCN_finalData[r+1][110] = "";}
							else {WCN_finalData[r+1][110] = Double.toString(WCN_timeOnTask2Avg_column[r4]);} // WCN_timeOnTask2 5thLabAvg 
							
							if(WCN_numTrialsToLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][120] = "";}
							else {WCN_finalData[r+1][120] = Double.toString(WCN_numTrialsToLevelUpAvg_column[r4]);} // WCN_numTrialsToLevelUp 5thLabAvg
							
							if(WCN_speedAtLevelUpAvg_column[r4] == -1) {WCN_finalData[r+1][130] = "";}
							else {WCN_finalData[r+1][130] = Double.toString(WCN_speedAtLevelUpAvg_column[r4]);} // WCN_speedAtLevelUp 5thLabAvg 
							
							WCN_finalData[r+1][140] = Double.toString(WCN_levelAvg_column[r4]); //WCN_highestLevel 5thLab
							WCN_finalData[r+1][150] = Double.toString(WCN_logSession_day[r4]); //WCN_logSession 5thLab
							WCN_finalData[r+1][160] = Double.toString(WCN_NICT_sessionNumber_day[r4]); //WCN_NICT_sessionNumber 5thLab
							
							if(WCN_Speed3Avg_column[r4] == -1) {WCN_finalData[r+1][170] = "";}
							else {WCN_finalData[r+1][170] = Double.toString(WCN_Speed3Avg_column[r4]);} // WCN_Speed3 5thLabAvg
							
							if(WCN_Speed4Avg_column[r4] == -1) {WCN_finalData[r+1][180] = "";}
							else {WCN_finalData[r+1][180] = Double.toString(WCN_Speed4Avg_column[r4]);} // WCN_Speed4 5thLabAvg
						}	
						r4++;
					}
				}
				
				////////---------------------------------------------------------------------------------------------------------------------------------------/////

				//Average from raw data
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString())))) < Integer.parseInt(sessionDate[r2][2].toString())) //First day <  1st Lab session
				{
					System.out.println("Error: There is activity before the 1st labSession for the ID " + WCN_finalData[r+1][0].toString());
					while((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString())))) < sessionDate_asNum[r2][2] && r5 < r6)
					{
						r5++;
						if(r5 == r6)
							break;
					}
				}
				
				for(int index = 0 ; index < 12 ; index++)
					occurrences2[index] = 0;
				
				// Day >=  1st Lab session and Day < 2nd LabSession (Week1)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][2]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][3])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][2]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+7))) 
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][3] && r5 < r6) //While Day < 2nd Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+7) && r5 < r6) 
					{
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							WCN_finalData[r+1][11] = Double.toString((Double.parseDouble(WCN_finalData[r+1][11].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							WCN_finalData[r+1][16] = Double.toString((Double.parseDouble(WCN_finalData[r+1][16].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						WCN_finalData[r+1][21] = Double.toString((Double.parseDouble(WCN_finalData[r+1][21].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //totalPlayed
						WCN_finalData[r+1][41] = Double.toString((Double.parseDouble(WCN_finalData[r+1][41].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							WCN_finalData[r+1][51] = Double.toString((Double.parseDouble(WCN_finalData[r+1][51].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //failToRespondRate1
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							WCN_finalData[r+1][61] = Double.toString((Double.parseDouble(WCN_finalData[r+1][61].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //failToRespondRate2
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							WCN_finalData[r+1][71] = Double.toString((Double.parseDouble(WCN_finalData[r+1][71].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //Speed1
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							WCN_finalData[r+1][81] = Double.toString((Double.parseDouble(WCN_finalData[r+1][81].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Speed2
							occurrences2[5]++;
						}
						if(!(organizedTable2[r5][17].equals("NULL")))
						{
							WCN_finalData[r+1][91] = Double.toString((Double.parseDouble(WCN_finalData[r+1][91].toString())) + (Double.parseDouble(organizedTable2[r5][17].toString()))); //timeOnTask1
							occurrences2[10]++;
						}
						if(!(organizedTable2[r5][18].equals("NULL")))
						{
							WCN_finalData[r+1][101] = Double.toString((Double.parseDouble(WCN_finalData[r+1][101].toString())) + (Double.parseDouble(organizedTable2[r5][18].toString()))); //timeOnTask2
							occurrences2[11]++;
						}
						if(!(organizedTable2[r5][13].equals("NULL")))
						{
							WCN_finalData[r+1][111] = Double.toString((Double.parseDouble(WCN_finalData[r+1][111].toString())) + (Double.parseDouble(organizedTable2[r5][13].toString()))); //trialsToLevelUp
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							WCN_finalData[r+1][121] = Double.toString((Double.parseDouble(WCN_finalData[r+1][121].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //speedAtLevelUp
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][10].equals("NULL")))
						{
							WCN_finalData[r+1][161] = Double.toString((Double.parseDouble(WCN_finalData[r+1][161].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //Speed3
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							WCN_finalData[r+1][171] = Double.toString((Double.parseDouble(WCN_finalData[r+1][171].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //Speed4
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(WCN_finalData[r+1][131].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							WCN_finalData[r+1][131] = organizedTable2[r5][3].toString(); //Level
						if((Double.parseDouble(WCN_finalData[r+1][141].toString())) <= (Double.parseDouble(organizedTable2[r5][19].toString())))
							WCN_finalData[r+1][141] = organizedTable2[r5][19].toString(); //GameSession
					
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0)
						WCN_finalData[r+1][11] = Double.toString((Double.parseDouble(WCN_finalData[r+1][11].toString())/occurrences2[0])); //Week1 WCN_hitRateAvg
					else
						WCN_finalData[r+1][11] = "";
					
					if(occurrences2[1] != 0)
						WCN_finalData[r+1][16] = Double.toString((Double.parseDouble(WCN_finalData[r+1][16].toString())/occurrences2[1])); //Week1 WCN_errorRateAvg
					else
						WCN_finalData[r+1][16] = "";
					
					WCN_finalData[r+1][21] = Double.toString((Double.parseDouble(WCN_finalData[r+1][21].toString()))); //Week1 WCN_totalPlayedAvg
					WCN_finalData[r+1][41] = Double.toString((Double.parseDouble(WCN_finalData[r+1][41].toString()))); //Week1 WCN_graduationAvg
					
					if(occurrences2[2] != 0)
						WCN_finalData[r+1][51] = Double.toString((Double.parseDouble(WCN_finalData[r+1][51].toString())/occurrences2[2])); //Week1 WCN_failToRespondRate1
					else
						WCN_finalData[r+1][51] = "";
					
					if(occurrences2[3] != 0)
						WCN_finalData[r+1][61] = Double.toString((Double.parseDouble(WCN_finalData[r+1][61].toString())/occurrences2[3])); //Week1 WCN_failToRespondRate2
					else
						WCN_finalData[r+1][61] = "";
					
					if(occurrences2[4] != 0)
						WCN_finalData[r+1][71] = Double.toString((Double.parseDouble(WCN_finalData[r+1][71].toString())/occurrences2[4])); //Week1 WCN_Speed1
					else
						WCN_finalData[r+1][71] = "";
					
					if(occurrences2[5] != 0)
						WCN_finalData[r+1][81] = Double.toString((Double.parseDouble(WCN_finalData[r+1][81].toString())/occurrences2[5])); //Week1 WCN_Speed2
					else
						WCN_finalData[r+1][81] = "";
					
					if(occurrences2[10] != 0)
						WCN_finalData[r+1][91] = Double.toString((Double.parseDouble(WCN_finalData[r+1][91].toString())/occurrences2[10])); //Week1 WCN_timeOnTask1
					else
						WCN_finalData[r+1][91] = "";
					
					if(occurrences2[11] != 0)
						WCN_finalData[r+1][101] = Double.toString((Double.parseDouble(WCN_finalData[r+1][101].toString())/occurrences2[11])); //Week1 WCN_timeOnTask2
					else
						WCN_finalData[r+1][101] = "";
					
					if(occurrences2[8] != 0)
						WCN_finalData[r+1][111] = Double.toString((Double.parseDouble(WCN_finalData[r+1][111].toString())/occurrences2[8])); //Week1 WCN_numTrialsToLevelUp
					else
						WCN_finalData[r+1][111] = "";
					
					if(occurrences2[9] != 0)
						WCN_finalData[r+1][121] = Double.toString((Double.parseDouble(WCN_finalData[r+1][121].toString())/occurrences2[9])); //Week1 WCN_speedAtLevelUp
					else
						WCN_finalData[r+1][121] = "";
						
					if(occurrences2[6] != 0)
						WCN_finalData[r+1][161] = Double.toString((Double.parseDouble(WCN_finalData[r+1][161].toString())/occurrences2[6])); //Week1 WCN_Speed3
					else
						WCN_finalData[r+1][161] = "";
					
					if(occurrences2[7] != 0)
						WCN_finalData[r+1][171] = Double.toString((Double.parseDouble(WCN_finalData[r+1][171].toString())/occurrences2[7])); //Week1 WCN_Speed4
					else
						WCN_finalData[r+1][171] = "";
												
					WCN_finalData[r+1][141] = Double.toString((Double.parseDouble(WCN_finalData[r+1][141].toString()))/4); //Week1 WCN_LogSessionNumber
					WCN_finalData[r+1][151] = Double.toString((Double.parseDouble(WCN_finalData[r+1][141].toString()))/2); //Week1 WCN_NICT_SessionNumber	
					
					for(int index = 0 ; index < 12 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 1st labSession and 2nd LabSession (Week 1) for the ID " + WCN_finalData[r+1][0].toString());
				}
				
				// Day >=  2nd Lab session and Day < 3rd LabSession (Week2)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][3]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][4])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+7)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+14))) 	
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][4] && r5 < r6) //While Day < 3rd Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+14) && r5 < r6) 	
					{
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							WCN_finalData[r+1][12] = Double.toString((Double.parseDouble(WCN_finalData[r+1][12].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							WCN_finalData[r+1][17] = Double.toString((Double.parseDouble(WCN_finalData[r+1][17].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						WCN_finalData[r+1][22] = Double.toString((Double.parseDouble(WCN_finalData[r+1][22].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //totalPlayed
						WCN_finalData[r+1][42] = Double.toString((Double.parseDouble(WCN_finalData[r+1][42].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							WCN_finalData[r+1][52] = Double.toString((Double.parseDouble(WCN_finalData[r+1][52].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //failToRespondRate1
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							WCN_finalData[r+1][62] = Double.toString((Double.parseDouble(WCN_finalData[r+1][62].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //failToRespondRate2
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							WCN_finalData[r+1][72] = Double.toString((Double.parseDouble(WCN_finalData[r+1][72].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //Speed1
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							WCN_finalData[r+1][82] = Double.toString((Double.parseDouble(WCN_finalData[r+1][82].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Speed2
							occurrences2[5]++;
						}
						if(!(organizedTable2[r5][17].equals("NULL")))
						{
							WCN_finalData[r+1][92] = Double.toString((Double.parseDouble(WCN_finalData[r+1][92].toString())) + (Double.parseDouble(organizedTable2[r5][17].toString()))); //timeOnTask1
							occurrences2[10]++;
						}
						if(!(organizedTable2[r5][18].equals("NULL")))
						{
							WCN_finalData[r+1][102] = Double.toString((Double.parseDouble(WCN_finalData[r+1][102].toString())) + (Double.parseDouble(organizedTable2[r5][18].toString()))); //timeOnTask2
							occurrences2[11]++;
						}
						if(!(organizedTable2[r5][13].equals("NULL")))
						{
							WCN_finalData[r+1][112] = Double.toString((Double.parseDouble(WCN_finalData[r+1][112].toString())) + (Double.parseDouble(organizedTable2[r5][13].toString()))); //trialsToLevelUp
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							WCN_finalData[r+1][122] = Double.toString((Double.parseDouble(WCN_finalData[r+1][122].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //speedAtLevelUp
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][10].equals("NULL")))
						{
							WCN_finalData[r+1][162] = Double.toString((Double.parseDouble(WCN_finalData[r+1][162].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //Speed3
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							WCN_finalData[r+1][172] = Double.toString((Double.parseDouble(WCN_finalData[r+1][172].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //Speed4
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(WCN_finalData[r+1][132].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							WCN_finalData[r+1][132] = organizedTable2[r5][3].toString(); // Level
						if((Double.parseDouble(WCN_finalData[r+1][142].toString())) <= (Double.parseDouble(organizedTable2[r5][19].toString())))
							WCN_finalData[r+1][142] = organizedTable2[r5][19].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0)
						WCN_finalData[r+1][12] = Double.toString((Double.parseDouble(WCN_finalData[r+1][12].toString())/occurrences2[0])); //Week2 WCN_hitRateAvg
					else
						WCN_finalData[r+1][12] = "";
					
					if(occurrences2[1] != 0)
						WCN_finalData[r+1][17] = Double.toString((Double.parseDouble(WCN_finalData[r+1][17].toString())/occurrences2[1])); //Week2 WCN_errorRateAvg
					else
						WCN_finalData[r+1][17] = "";
					
					WCN_finalData[r+1][22] = Double.toString((Double.parseDouble(WCN_finalData[r+1][22].toString()))); //Week2 WCN_totalPlayedAvg
					WCN_finalData[r+1][42] = Double.toString((Double.parseDouble(WCN_finalData[r+1][42].toString()))); //Week2 WCN_graduationAvg
					
					if(occurrences2[2] != 0)
						WCN_finalData[r+1][52] = Double.toString((Double.parseDouble(WCN_finalData[r+1][52].toString())/occurrences2[2])); //Week2 WCN_failToRespondRate1
					else
						WCN_finalData[r+1][52] = "";
					
					if(occurrences2[3] != 0)
						WCN_finalData[r+1][62] = Double.toString((Double.parseDouble(WCN_finalData[r+1][62].toString())/occurrences2[3])); //Week2 WCN_failToRespondRate2
					else
						WCN_finalData[r+1][62] = "";
					
					if(occurrences2[4] != 0)
						WCN_finalData[r+1][72] = Double.toString((Double.parseDouble(WCN_finalData[r+1][72].toString())/occurrences2[4])); //Week2 WCN_Speed1
					else
						WCN_finalData[r+1][72] = "";
					
					if(occurrences2[5] != 0)
						WCN_finalData[r+1][82] = Double.toString((Double.parseDouble(WCN_finalData[r+1][82].toString())/occurrences2[5])); //Week2 WCN_Speed2
					else
						WCN_finalData[r+1][82] = "";
					
					if(occurrences2[10] != 0)
						WCN_finalData[r+1][92] = Double.toString((Double.parseDouble(WCN_finalData[r+1][92].toString())/occurrences2[10])); //Week2 WCN_timeOnTask1
					else
						WCN_finalData[r+1][92] = "";
					
					if(occurrences2[11] != 0)
						WCN_finalData[r+1][102] = Double.toString((Double.parseDouble(WCN_finalData[r+1][102].toString())/occurrences2[11])); //Week2 WCN_timeOnTask2
					else
						WCN_finalData[r+1][102] = "";
					
					if(occurrences2[8] != 0)
						WCN_finalData[r+1][112] = Double.toString((Double.parseDouble(WCN_finalData[r+1][112].toString())/occurrences2[8])); //Week2 WCN_numTrialsToLevelUp
					else
						WCN_finalData[r+1][112] = "";
					
					if(occurrences2[9] != 0)
						WCN_finalData[r+1][122] = Double.toString((Double.parseDouble(WCN_finalData[r+1][122].toString())/occurrences2[9])); //Week2 WCN_speedAtLevelUp
					else
						WCN_finalData[r+1][122] = "";
						
					if(occurrences2[6] != 0)
						WCN_finalData[r+1][162] = Double.toString((Double.parseDouble(WCN_finalData[r+1][162].toString())/occurrences2[6])); //Week2 WCN_Speed3
					else
						WCN_finalData[r+1][162] = "";
					
					if(occurrences2[7] != 0)
						WCN_finalData[r+1][172] = Double.toString((Double.parseDouble(WCN_finalData[r+1][172].toString())/occurrences2[7])); //Week2 WCN_Speed4
					else
						WCN_finalData[r+1][172] = "";
												
					WCN_finalData[r+1][142] = Double.toString((Double.parseDouble(WCN_finalData[r+1][142].toString()))/4); //Week2 WCN_LogSessionNumber
					WCN_finalData[r+1][152] = Double.toString((Double.parseDouble(WCN_finalData[r+1][142].toString()))/2); //Week2 WCN_NICT_SessionNumber	
					
					for(int index = 0 ; index < 12 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 2nd labSession and 3rd LabSession (Week 2) for the ID " + WCN_finalData[r+1][0].toString());
				}
				
				// Day >=  3rd Lab session and Day < 4th LabSession (Week3)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][4]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][5])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+14)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+21)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][5] && r5 < r6) //While Day < 4th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+21) && r5 < r6) 
					{
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							WCN_finalData[r+1][13] = Double.toString((Double.parseDouble(WCN_finalData[r+1][13].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							WCN_finalData[r+1][18] = Double.toString((Double.parseDouble(WCN_finalData[r+1][18].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						WCN_finalData[r+1][23] = Double.toString((Double.parseDouble(WCN_finalData[r+1][23].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //totalPlayed
						WCN_finalData[r+1][43] = Double.toString((Double.parseDouble(WCN_finalData[r+1][43].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							WCN_finalData[r+1][53] = Double.toString((Double.parseDouble(WCN_finalData[r+1][53].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //failToRespondRate1
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							WCN_finalData[r+1][63] = Double.toString((Double.parseDouble(WCN_finalData[r+1][63].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //failToRespondRate2
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							WCN_finalData[r+1][73] = Double.toString((Double.parseDouble(WCN_finalData[r+1][73].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //Speed1
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							WCN_finalData[r+1][83] = Double.toString((Double.parseDouble(WCN_finalData[r+1][83].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Speed2
							occurrences2[5]++;
						}
						if(!(organizedTable2[r5][17].equals("NULL")))
						{
							WCN_finalData[r+1][93] = Double.toString((Double.parseDouble(WCN_finalData[r+1][93].toString())) + (Double.parseDouble(organizedTable2[r5][17].toString()))); //timeOnTask1
							occurrences2[10]++;
						}
						if(!(organizedTable2[r5][18].equals("NULL")))
						{
							WCN_finalData[r+1][103] = Double.toString((Double.parseDouble(WCN_finalData[r+1][103].toString())) + (Double.parseDouble(organizedTable2[r5][18].toString()))); //timeOnTask2
							occurrences2[11]++;
						}
						if(!(organizedTable2[r5][13].equals("NULL")))
						{
							WCN_finalData[r+1][113] = Double.toString((Double.parseDouble(WCN_finalData[r+1][113].toString())) + (Double.parseDouble(organizedTable2[r5][13].toString()))); //trialsToLevelUp
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							WCN_finalData[r+1][123] = Double.toString((Double.parseDouble(WCN_finalData[r+1][123].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //speedAtLevelUp
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][10].equals("NULL")))
						{
							WCN_finalData[r+1][163] = Double.toString((Double.parseDouble(WCN_finalData[r+1][163].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //Speed3
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							WCN_finalData[r+1][173] = Double.toString((Double.parseDouble(WCN_finalData[r+1][173].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //Speed4
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(WCN_finalData[r+1][133].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							WCN_finalData[r+1][133] = organizedTable2[r5][3].toString();// Level
						if((Double.parseDouble(WCN_finalData[r+1][143].toString())) <= (Double.parseDouble(organizedTable2[r5][19].toString())))
							WCN_finalData[r+1][143] = organizedTable2[r5][19].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0)
						WCN_finalData[r+1][13] = Double.toString((Double.parseDouble(WCN_finalData[r+1][13].toString())/occurrences2[0])); //Week3 WCN_hitRateAvg
					else
						WCN_finalData[r+1][13] = "";
					
					if(occurrences2[1] != 0)
						WCN_finalData[r+1][18] = Double.toString((Double.parseDouble(WCN_finalData[r+1][18].toString())/occurrences2[1])); //Week3 WCN_errorRateAvg
					else
						WCN_finalData[r+1][18] = "";
					
					WCN_finalData[r+1][23] = Double.toString((Double.parseDouble(WCN_finalData[r+1][23].toString()))); //Week3 WCN_totalPlayedAvg
					WCN_finalData[r+1][43] = Double.toString((Double.parseDouble(WCN_finalData[r+1][43].toString()))); //Week3 WCN_graduationAvg
					
					if(occurrences2[2] != 0)
						WCN_finalData[r+1][53] = Double.toString((Double.parseDouble(WCN_finalData[r+1][53].toString())/occurrences2[2])); //Week3 WCN_failToRespondRate1
					else
						WCN_finalData[r+1][53] = "";
					
					if(occurrences2[3] != 0)
						WCN_finalData[r+1][63] = Double.toString((Double.parseDouble(WCN_finalData[r+1][63].toString())/occurrences2[3])); //Week3 WCN_failToRespondRate2
					else
						WCN_finalData[r+1][63] = "";
					
					if(occurrences2[4] != 0)
						WCN_finalData[r+1][73] = Double.toString((Double.parseDouble(WCN_finalData[r+1][73].toString())/occurrences2[4])); //Week3 WCN_Speed1
					else
						WCN_finalData[r+1][73] = "";
					
					if(occurrences2[5] != 0)
						WCN_finalData[r+1][83] = Double.toString((Double.parseDouble(WCN_finalData[r+1][83].toString())/occurrences2[5])); //Week3 WCN_Speed2
					else
						WCN_finalData[r+1][83] = "";
					
					if(occurrences2[10] != 0)
						WCN_finalData[r+1][93] = Double.toString((Double.parseDouble(WCN_finalData[r+1][93].toString())/occurrences2[10])); //Week3 WCN_timeOnTask1
					else
						WCN_finalData[r+1][93] = "";
					
					if(occurrences2[11] != 0)
						WCN_finalData[r+1][103] = Double.toString((Double.parseDouble(WCN_finalData[r+1][103].toString())/occurrences2[11])); //Week3 WCN_timeOnTask2
					else
						WCN_finalData[r+1][103] = "";
					
					if(occurrences2[8] != 0)
						WCN_finalData[r+1][113] = Double.toString((Double.parseDouble(WCN_finalData[r+1][113].toString())/occurrences2[8])); //Week3 WCN_numTrialsToLevelUp
					else
						WCN_finalData[r+1][113] = "";
					
					if(occurrences2[9] != 0)
						WCN_finalData[r+1][123] = Double.toString((Double.parseDouble(WCN_finalData[r+1][123].toString())/occurrences2[9])); //Week3 WCN_speedAtLevelUp
					else
						WCN_finalData[r+1][123] = "";
						
					if(occurrences2[6] != 0)
						WCN_finalData[r+1][163] = Double.toString((Double.parseDouble(WCN_finalData[r+1][163].toString())/occurrences2[6])); //Week3 WCN_Speed3
					else
						WCN_finalData[r+1][163] = "";
					
					if(occurrences2[7] != 0)
						WCN_finalData[r+1][173] = Double.toString((Double.parseDouble(WCN_finalData[r+1][173].toString())/occurrences2[7])); //Week3 WCN_Speed4
					else
						WCN_finalData[r+1][173] = "";
												
					WCN_finalData[r+1][143] = Double.toString((Double.parseDouble(WCN_finalData[r+1][143].toString()))/4); //Week3 WCN_LogSessionNumber
					WCN_finalData[r+1][153] = Double.toString((Double.parseDouble(WCN_finalData[r+1][143].toString()))/2); //Week3 WCN_NICT_SessionNumber	
					
					for(int index = 0 ; index < 12 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 3rd labSession and 4th LabSession (Week 3) for the ID " + WCN_finalData[r+1][0].toString());
				}
				
				// Day >=  4th Lab session and Day < 5th LabSession (Week4)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][5]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][6])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+21)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+28)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][6] && r5 < r6) //While Day < 5th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+28) && r5 < r6) 
					{
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							WCN_finalData[r+1][14] = Double.toString((Double.parseDouble(WCN_finalData[r+1][14].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							WCN_finalData[r+1][19] = Double.toString((Double.parseDouble(WCN_finalData[r+1][19].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						WCN_finalData[r+1][24] = Double.toString((Double.parseDouble(WCN_finalData[r+1][24].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //totalPlayed
						WCN_finalData[r+1][44] = Double.toString((Double.parseDouble(WCN_finalData[r+1][44].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							WCN_finalData[r+1][54] = Double.toString((Double.parseDouble(WCN_finalData[r+1][54].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //failToRespondRate1
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							WCN_finalData[r+1][64] = Double.toString((Double.parseDouble(WCN_finalData[r+1][64].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //failToRespondRate2
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							WCN_finalData[r+1][74] = Double.toString((Double.parseDouble(WCN_finalData[r+1][74].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //Speed1
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							WCN_finalData[r+1][84] = Double.toString((Double.parseDouble(WCN_finalData[r+1][84].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Speed2
							occurrences2[5]++;
						}
						if(!(organizedTable2[r5][17].equals("NULL")))
						{
							WCN_finalData[r+1][94] = Double.toString((Double.parseDouble(WCN_finalData[r+1][94].toString())) + (Double.parseDouble(organizedTable2[r5][17].toString()))); //timeOnTask1
							occurrences2[10]++;
						}
						if(!(organizedTable2[r5][18].equals("NULL")))
						{
							WCN_finalData[r+1][104] = Double.toString((Double.parseDouble(WCN_finalData[r+1][104].toString())) + (Double.parseDouble(organizedTable2[r5][18].toString()))); //timeOnTask2
							occurrences2[11]++;
						}
						if(!(organizedTable2[r5][13].equals("NULL")))
						{
							WCN_finalData[r+1][114] = Double.toString((Double.parseDouble(WCN_finalData[r+1][114].toString())) + (Double.parseDouble(organizedTable2[r5][13].toString()))); //trialsToLevelUp
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							WCN_finalData[r+1][124] = Double.toString((Double.parseDouble(WCN_finalData[r+1][124].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //speedAtLevelUp
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][10].equals("NULL")))
						{
							WCN_finalData[r+1][164] = Double.toString((Double.parseDouble(WCN_finalData[r+1][164].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //Speed3
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							WCN_finalData[r+1][174] = Double.toString((Double.parseDouble(WCN_finalData[r+1][174].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //Speed4
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(WCN_finalData[r+1][134].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							WCN_finalData[r+1][134] = organizedTable2[r5][3].toString(); // Level
						if((Double.parseDouble(WCN_finalData[r+1][144].toString())) <= (Double.parseDouble(organizedTable2[r5][19].toString())))
							WCN_finalData[r+1][144] = organizedTable2[r5][19].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							r5--;
							break;
						}
					}

					if(occurrences2[0] != 0)
						WCN_finalData[r+1][14] = Double.toString((Double.parseDouble(WCN_finalData[r+1][14].toString())/occurrences2[0])); //Week4 WCN_hitRateAvg
					else
						WCN_finalData[r+1][14] = "";
					
					if(occurrences2[1] != 0)
						WCN_finalData[r+1][19] = Double.toString((Double.parseDouble(WCN_finalData[r+1][19].toString())/occurrences2[1])); //Week4 WCN_errorRateAvg
					else
						WCN_finalData[r+1][19] = "";
					
					WCN_finalData[r+1][24] = Double.toString((Double.parseDouble(WCN_finalData[r+1][24].toString()))); //Week4 WCN_totalPlayedAvg
					WCN_finalData[r+1][44] = Double.toString((Double.parseDouble(WCN_finalData[r+1][44].toString()))); //Week4 WCN_graduationAvg
					
					if(occurrences2[2] != 0)
						WCN_finalData[r+1][54] = Double.toString((Double.parseDouble(WCN_finalData[r+1][54].toString())/occurrences2[2])); //Week4 WCN_failToRespondRate1
					else
						WCN_finalData[r+1][54] = "";
					
					if(occurrences2[3] != 0)
						WCN_finalData[r+1][64] = Double.toString((Double.parseDouble(WCN_finalData[r+1][64].toString())/occurrences2[3])); //Week4 WCN_failToRespondRate2
					else
						WCN_finalData[r+1][64] = "";
					
					if(occurrences2[4] != 0)
						WCN_finalData[r+1][74] = Double.toString((Double.parseDouble(WCN_finalData[r+1][74].toString())/occurrences2[4])); //Week4 WCN_Speed1
					else
						WCN_finalData[r+1][74] = "";
					
					if(occurrences2[5] != 0)
						WCN_finalData[r+1][84] = Double.toString((Double.parseDouble(WCN_finalData[r+1][84].toString())/occurrences2[5])); //Week4 WCN_Speed2
					else
						WCN_finalData[r+1][84] = "";
					
					if(occurrences2[10] != 0)
						WCN_finalData[r+1][94] = Double.toString((Double.parseDouble(WCN_finalData[r+1][94].toString())/occurrences2[10])); //Week4 WCN_timeOnTask1
					else
						WCN_finalData[r+1][94] = "";
					
					if(occurrences2[11] != 0)
						WCN_finalData[r+1][104] = Double.toString((Double.parseDouble(WCN_finalData[r+1][104].toString())/occurrences2[11])); //Week4 WCN_timeOnTask2
					else
						WCN_finalData[r+1][104] = "";
					
					if(occurrences2[8] != 0)
						WCN_finalData[r+1][114] = Double.toString((Double.parseDouble(WCN_finalData[r+1][114].toString())/occurrences2[8])); //Week4 WCN_numTrialsToLevelUp
					else
						WCN_finalData[r+1][114] = "";
					
					if(occurrences2[9] != 0)
						WCN_finalData[r+1][124] = Double.toString((Double.parseDouble(WCN_finalData[r+1][124].toString())/occurrences2[9])); //Week4 WCN_speedAtLevelUp
					else
						WCN_finalData[r+1][124] = "";
						
					if(occurrences2[6] != 0)
						WCN_finalData[r+1][164] = Double.toString((Double.parseDouble(WCN_finalData[r+1][164].toString())/occurrences2[6])); //Week4 WCN_Speed3
					else
						WCN_finalData[r+1][164] = "";
					
					if(occurrences2[7] != 0)
						WCN_finalData[r+1][174] = Double.toString((Double.parseDouble(WCN_finalData[r+1][174].toString())/occurrences2[7])); //Week4 WCN_Speed4
					else
						WCN_finalData[r+1][174] = "";
												
					WCN_finalData[r+1][144] = Double.toString((Double.parseDouble(WCN_finalData[r+1][144].toString()))/4); //Week4 WCN_LogSessionNumber
					WCN_finalData[r+1][154] = Double.toString((Double.parseDouble(WCN_finalData[r+1][144].toString()))/2); //Week4 WCN_NICT_SessionNumber	
					
					for(int index = 0 ; index < 12 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 4th labSession and 5th LabSession (Week 4) for the ID " + WCN_finalData[r+1][0].toString());
				}
				
				
				// Day >=  5th Lab session and Day < PostDate (Week5)
				if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= sessionDate_asNum[r2][6]) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][7])) 
				//if((((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) >= (sessionDate_asNum[r2][2]+28)) && (((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+35)))
				{
					while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < sessionDate_asNum[r2][7] && r5 < r6) //While Day < 5th Lab session date
					//while(((int)(Double.parseDouble(organizedTable2[r5][date_column].toString()))) < (sessionDate_asNum[r2][2]+35) && r5 < r6) 	
					{
						if(!(organizedTable2[r5][4].equals("NULL")))
						{
							WCN_finalData[r+1][15] = Double.toString((Double.parseDouble(WCN_finalData[r+1][15].toString())) + (Double.parseDouble(organizedTable2[r5][4].toString()))) ; //hitRate
							occurrences2[0]++;
						}
						if(!(organizedTable2[r5][5].equals("NULL")))
						{
							WCN_finalData[r+1][20] = Double.toString((Double.parseDouble(WCN_finalData[r+1][20].toString())) + (Double.parseDouble(organizedTable2[r5][5].toString()))); //missRate
							occurrences2[1]++;
						}
						
						WCN_finalData[r+1][25] = Double.toString((Double.parseDouble(WCN_finalData[r+1][25].toString())) + (Double.parseDouble(organizedTable2[r5][16].toString()))); //totalPlayed
						WCN_finalData[r+1][45] = Double.toString((Double.parseDouble(WCN_finalData[r+1][45].toString())) + (Double.parseDouble(organizedTable2[r5][12].toString()))); //totalGraduated
						
						if(!(organizedTable2[r5][6].equals("NULL")))
						{
							WCN_finalData[r+1][55] = Double.toString((Double.parseDouble(WCN_finalData[r+1][55].toString())) + (Double.parseDouble(organizedTable2[r5][6].toString()))); //failToRespondRate1
							occurrences2[2]++;
						}
						if(!(organizedTable2[r5][7].equals("NULL")))
						{
							WCN_finalData[r+1][65] = Double.toString((Double.parseDouble(WCN_finalData[r+1][65].toString())) + (Double.parseDouble(organizedTable2[r5][7].toString()))); //failToRespondRate2
							occurrences2[3]++;
						}
						if(!(organizedTable2[r5][8].equals("NULL")))
						{
							WCN_finalData[r+1][75] = Double.toString((Double.parseDouble(WCN_finalData[r+1][75].toString())) + (Double.parseDouble(organizedTable2[r5][8].toString()))); //Speed1
							occurrences2[4]++;
						}
						if(!(organizedTable2[r5][9].equals("NULL")))
						{
							WCN_finalData[r+1][85] = Double.toString((Double.parseDouble(WCN_finalData[r+1][85].toString())) + (Double.parseDouble(organizedTable2[r5][9].toString()))); //Speed2
							occurrences2[5]++;
						}
						if(!(organizedTable2[r5][17].equals("NULL")))
						{
							WCN_finalData[r+1][95] = Double.toString((Double.parseDouble(WCN_finalData[r+1][95].toString())) + (Double.parseDouble(organizedTable2[r5][17].toString()))); //timeOnTask1
							occurrences2[10]++;
						}
						if(!(organizedTable2[r5][18].equals("NULL")))
						{
							WCN_finalData[r+1][105] = Double.toString((Double.parseDouble(WCN_finalData[r+1][105].toString())) + (Double.parseDouble(organizedTable2[r5][18].toString()))); //timeOnTask2
							occurrences2[11]++;
						}
						if(!(organizedTable2[r5][13].equals("NULL")))
						{
							WCN_finalData[r+1][115] = Double.toString((Double.parseDouble(WCN_finalData[r+1][115].toString())) + (Double.parseDouble(organizedTable2[r5][13].toString()))); //trialsToLevelUp
							occurrences2[8]++;
						}
						if(!(organizedTable2[r5][15].equals("NULL")))
						{
							WCN_finalData[r+1][125] = Double.toString((Double.parseDouble(WCN_finalData[r+1][125].toString())) + (Double.parseDouble(organizedTable2[r5][15].toString()))); //speedAtLevelUp
							occurrences2[9]++;
						}
						if(!(organizedTable2[r5][10].equals("NULL")))
						{
							WCN_finalData[r+1][165] = Double.toString((Double.parseDouble(WCN_finalData[r+1][165].toString())) + (Double.parseDouble(organizedTable2[r5][10].toString()))); //Speed3
							occurrences2[6]++;
						}
						if(!(organizedTable2[r5][11].equals("NULL")))
						{
							WCN_finalData[r+1][175] = Double.toString((Double.parseDouble(WCN_finalData[r+1][175].toString())) + (Double.parseDouble(organizedTable2[r5][11].toString()))); //Speed4
							occurrences2[7]++;
						}
						
						//Highest Value
						if((Double.parseDouble(WCN_finalData[r+1][135].toString())) <= (Double.parseDouble(organizedTable2[r5][3].toString())))
							WCN_finalData[r+1][135] = organizedTable2[r5][3].toString(); // Level
						if((Double.parseDouble(WCN_finalData[r+1][145].toString())) <= (Double.parseDouble(organizedTable2[r5][19].toString())))
							WCN_finalData[r+1][145] = organizedTable2[r5][19].toString(); // GameSession
						
						r5++;
						//occurrences2++;
						if(r5 == r6)
						{
							break;
						}
					}
					
					if(occurrences2[0] != 0)
						WCN_finalData[r+1][15] = Double.toString((Double.parseDouble(WCN_finalData[r+1][15].toString())/occurrences2[0])); //Week5 WCN_hitRateAvg
					else
						WCN_finalData[r+1][15] = "";
					
					if(occurrences2[1] != 0)
						WCN_finalData[r+1][20] = Double.toString((Double.parseDouble(WCN_finalData[r+1][20].toString())/occurrences2[1])); //Week5 WCN_errorRateAvg
					else
						WCN_finalData[r+1][20] = "";
					
					WCN_finalData[r+1][25] = Double.toString((Double.parseDouble(WCN_finalData[r+1][25].toString()))); //Week5 WCN_totalPlayedAvg
					WCN_finalData[r+1][45] = Double.toString((Double.parseDouble(WCN_finalData[r+1][45].toString()))); //Week5 WCN_graduationAvg
					
					if(occurrences2[2] != 0)
						WCN_finalData[r+1][55] = Double.toString((Double.parseDouble(WCN_finalData[r+1][55].toString())/occurrences2[2])); //Week5 WCN_failToRespondRate1
					else
						WCN_finalData[r+1][55] = "";
					
					if(occurrences2[3] != 0)
						WCN_finalData[r+1][65] = Double.toString((Double.parseDouble(WCN_finalData[r+1][65].toString())/occurrences2[3])); //Week5 WCN_failToRespondRate2
					else
						WCN_finalData[r+1][65] = "";
					
					if(occurrences2[4] != 0)
						WCN_finalData[r+1][75] = Double.toString((Double.parseDouble(WCN_finalData[r+1][75].toString())/occurrences2[4])); //Week5 WCN_Speed1
					else
						WCN_finalData[r+1][75] = "";
					
					if(occurrences2[5] != 0)
						WCN_finalData[r+1][85] = Double.toString((Double.parseDouble(WCN_finalData[r+1][85].toString())/occurrences2[5])); //Week5 WCN_Speed2
					else
						WCN_finalData[r+1][85] = "";
					
					if(occurrences2[10] != 0)
						WCN_finalData[r+1][95] = Double.toString((Double.parseDouble(WCN_finalData[r+1][95].toString())/occurrences2[10])); //Week5 WCN_timeOnTask1
					else
						WCN_finalData[r+1][95] = "";
					
					if(occurrences2[11] != 0)
						WCN_finalData[r+1][105] = Double.toString((Double.parseDouble(WCN_finalData[r+1][105].toString())/occurrences2[11])); //Week5 WCN_timeOnTask2
					else
						WCN_finalData[r+1][105] = "";
					
					if(occurrences2[8] != 0)
						WCN_finalData[r+1][115] = Double.toString((Double.parseDouble(WCN_finalData[r+1][115].toString())/occurrences2[8])); //Week5 WCN_numTrialsToLevelUp
					else
						WCN_finalData[r+1][115] = "";
					
					if(occurrences2[9] != 0)
						WCN_finalData[r+1][125] = Double.toString((Double.parseDouble(WCN_finalData[r+1][125].toString())/occurrences2[9])); //Week5 WCN_speedAtLevelUp
					else
						WCN_finalData[r+1][125] = "";
						
					if(occurrences2[6] != 0)
						WCN_finalData[r+1][165] = Double.toString((Double.parseDouble(WCN_finalData[r+1][165].toString())/occurrences2[6])); //Week5 WCN_Speed3
					else
						WCN_finalData[r+1][165] = "";
					
					if(occurrences2[7] != 0)
						WCN_finalData[r+1][175] = Double.toString((Double.parseDouble(WCN_finalData[r+1][175].toString())/occurrences2[7])); //Week5 WCN_Speed4
					else
						WCN_finalData[r+1][175] = "";
												
					WCN_finalData[r+1][145] = Double.toString((Double.parseDouble(WCN_finalData[r+1][145].toString()))/4); //Week5 WCN_LogSessionNumber
					WCN_finalData[r+1][155] = Double.toString((Double.parseDouble(WCN_finalData[r+1][145].toString()))/2); //Week5 WCN_NICT_SessionNumber	
					
					for(int index = 0 ; index < 12 ; index++)
						occurrences2[index] = 0;
				}
				else
				{
					System.out.println("There is no session between 5th labSession and PostDate (Week 5) for the ID " + WCN_finalData[r+1][0].toString());
				}
							
			} // If found ID
			
		}
		
		System.out.println("\n");
		/*for(int a = 1 ; a < WCN_totalDays ; a++)
		{
			System.out.println(WCN_ID_day[a].toString() + "\t" + WCN_day[a] + "\t" + WCN_sessionLabDay[a] + "\t" + WCN_hitRateAvg_column[a] + "\t" + WCN_errorRateAvg_column[a] + "\t" + WCN_totalPlayedAvg_column[a] + "\t" + WCN_graduationAvg_column[a] +
					"\t" + WCN_failToRespondRate1Avg_column[a] + "\t" + WCN_failToRespondRate2Avg_column[a] + "\t" + WCN_Speed1Avg_column[a] + "\t" + WCN_Speed2Avg_column[a] +
					"\t" + WCN_timeOnTask1Avg_column[a] + "\t" + WCN_timeOnTask2Avg_column[a] + "\t" + WCN_numTrialsToLevelUpAvg_column[a] + "\t" + WCN_speedAtLevelUpAvg_column[a]);
		}*/
		
		
		//Writing the data to be stored on an excel file; at the end, will output the entire file with all the new variables that we have calculated
		
		Sheet WCN_AVG = workbook_w.createSheet("WCN_AVG_ByID");
		
		Cell cell_w;
				
		for(int r = 0 ; r < WCN_usersTotal ; r++)
		{
			Row row = WCN_AVG.createRow(r);
			for(int c = 0 ; c < 182 ; c++)
			{
				if(r == 0)
				{
					cell_w = row.createCell(c);
					cell_w.setCellType(Cell.CELL_TYPE_STRING);
					cell_w.setCellValue(WCN_finalData[r][c].toString());
				}
				else
				{
					cell_w = row.createCell(c);
					
					if(WCN_finalData[r][c].equals(""))
					{
						cell_w.setCellType(Cell.CELL_TYPE_STRING);
						cell_w.setCellValue(WCN_finalData[r][c].toString());
					}
					else
					{
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(WCN_finalData[r][c].toString()));
					}
					
				}
			}
		}
		
		
		//Sheet with ID and Dates
		Sheet WCN_AVG2 = workbook_w.createSheet("WCN_AVG_ByDate");
		
		Row row_w = WCN_AVG2.createRow(0);
		
		cell_w = row_w.createCell(0);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("ID");
		
		cell_w = row_w.createCell(1);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("Day");
		
		cell_w = row_w.createCell(2);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("IsSessionLab");
		
		cell_w = row_w.createCell(3);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_hitRate_AVG");
		
		cell_w = row_w.createCell(4);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_errorRate_AVG");
		
		cell_w = row_w.createCell(5);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_totalPlayed");
		
		cell_w = row_w.createCell(6);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_graduation");
		
		cell_w = row_w.createCell(7);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_failToRespondRate1_AVG");
		
		cell_w = row_w.createCell(8);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_failToRespondRate2_AVG");
		
		cell_w = row_w.createCell(9);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_Speed1_AVG");
		
		cell_w = row_w.createCell(10);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_Speed2_AVG");
		
		cell_w = row_w.createCell(11);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_timeOnTask1_AVG");
		
		cell_w = row_w.createCell(12);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_timeOnTask2_AVG");
		
		cell_w = row_w.createCell(13);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_numTrialsToLevelUp_AVG");
		
		cell_w = row_w.createCell(14);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_speedAtLevelUp_AVG");
		
		cell_w = row_w.createCell(15);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_highestLevel");
		
		cell_w = row_w.createCell(16);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_logSessionNumber");
		
		cell_w = row_w.createCell(17);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_NICT_SessionNumber");
		
		cell_w = row_w.createCell(18);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_Speed3_AVG");
		
		cell_w = row_w.createCell(19);
		cell_w.setCellType(Cell.CELL_TYPE_STRING);
		cell_w.setCellValue("WCN_Speed4_AVG");
		
		
		
		for(int r = 1 ; r < WCN_totalDays ; r++)
		{
			Row row = WCN_AVG2.createRow(r);
			
			cell_w = row.createCell(0);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(Double.parseDouble(WCN_ID_day[r].toString()));
			
			cell_w = row.createCell(1);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(WCN_day[r]);
			
			cell_w = row.createCell(2);
			cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell_w.setCellValue(WCN_sessionLabDay[r]);
			
			cell_w = row.createCell(3);
			if(WCN_hitRateAvg_column[r] == -1)	
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_hitRateAvg_column[r]);
			}
			
			cell_w = row.createCell(4);
			if(WCN_errorRateAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_errorRateAvg_column[r]);
			}
			
			cell_w = row.createCell(5);
			if(WCN_totalPlayedAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_totalPlayedAvg_column[r]);
			}
			
			cell_w = row.createCell(6);
			if(WCN_graduationAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_graduationAvg_column[r]);
			}
			
			cell_w = row.createCell(7);
			if(WCN_failToRespondRate1Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_failToRespondRate1Avg_column[r]);
			}
			
			cell_w = row.createCell(8);
			if(WCN_failToRespondRate2Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_failToRespondRate2Avg_column[r]);
			}
			
			cell_w = row.createCell(9);
			if(WCN_Speed1Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_Speed1Avg_column[r]);
			}
			
			cell_w = row.createCell(10);
			if(WCN_Speed2Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_Speed2Avg_column[r]);
			}
			
			cell_w = row.createCell(11);
			if(WCN_timeOnTask1Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_timeOnTask1Avg_column[r]);
			}
			
			cell_w = row.createCell(12);
			if(WCN_timeOnTask2Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_timeOnTask2Avg_column[r]);
			}
						
			cell_w = row.createCell(13);
			if(WCN_numTrialsToLevelUpAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_numTrialsToLevelUpAvg_column[r]);
			}
			
			cell_w = row.createCell(14);
			if(WCN_speedAtLevelUpAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_speedAtLevelUpAvg_column[r]);
			}
			
			cell_w = row.createCell(15);
			if(WCN_levelAvg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_levelAvg_column[r]);
			}
			
			cell_w = row.createCell(16);
			if(WCN_logSession_day[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_logSession_day[r]);
			}
			
			cell_w = row.createCell(17);
			if(WCN_NICT_sessionNumber_day[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_NICT_sessionNumber_day[r]);
			}
			
			cell_w = row.createCell(18);
			if(WCN_Speed3Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("");
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_Speed3Avg_column[r]);
			}
			
			cell_w = row.createCell(19);
			if(WCN_Speed4Avg_column[r] == -1)
			{
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(WCN_Speed4Avg_column[r]);
			}
			else
			{
				cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell_w.setCellValue(WCN_Speed4Avg_column[r]);
			}
			
		}
		
		//Sheet with ID and Dates
		Sheet WCN_AVG3 = workbook_w.createSheet("WCN_Daily_Progress");
		
		row_w = WCN_AVG3.createRow(0);
			
		for(int _cell = 0 ; _cell < 100 ; _cell++)
		{
			if(_cell == 0)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("ID");
			}
			if(_cell == 1)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue("Variable");
			}
			if(_cell > 1)
			{
				cell_w = row_w.createCell(_cell);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				cell_w.setCellValue(("Day_"+ Integer.toString((_cell - 1))));
			}
		}
		
		int rows = 1; // Rows on the third sheet	
		int ID_totalDays = 1; // Store the total days for an specific ID
		int index = 1; // 
		int user_row_onSessionDateSheet;
		int[] day = new int[19];
		for(int k = 0 ;  k < 19 ; k++)
		{
			day[k] = 1;
		}
		
		for(int user = 0 ; user <= WCN_usersTotal ; user++)
		{
			c_ID = WCN_ID_day[index];
			rows++;
			ID_totalDays = 0;
			while(c_ID.equals(WCN_ID_day[index].toString()))
			{
				ID_totalDays++;
				index++;
				if(index == WCN_totalDays)
					break;
			}
			for(user_row_onSessionDateSheet = 1 ;  user_row_onSessionDateSheet < sessionDate_row_num ; user_row_onSessionDateSheet++)
			{
				if(c_ID.equals(sessionDate[user_row_onSessionDateSheet][0]))
					break;
			}
			
			for(int r = 1 ; r < 20 ; r++)
			{
				Row row = WCN_AVG3.createRow(rows);
				for(int c = 0 ; c < (ID_totalDays + 2) ;  c++)
				{
					if(c == 0)
					{
						cell_w = row.createCell(c);
						cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell_w.setCellValue(Double.parseDouble(c_ID.toString()));
					}
					if(c == 1)
					{
						if(r == 1)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("IsLabSession");
						}
						if(r == 2)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("hitRate_Avg");
						}
						if(r == 3)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("errorRate_Avg");
						}
						if(r == 4)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("totalPlayed");
						}
						if(r == 5)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("graduation");
						}
						if(r == 6)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("failToRespondRate1_Avg");
						}
						if(r == 7)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("failToRespondRate2_Avg");
						}
						if(r == 8)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("Speed1_Avg");
						}
						if(r == 9)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("Speed2_Avg");
						}
						if(r == 10)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("timeOnTask1_Avg");
						}
						if(r == 11)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("timeOnTask2_Avg");
						}
						if(r == 12)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("numTrialsToLevelUp_Avg");
						}
						if(r == 13)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("speedAtLevelUp_Avg");
						}
						if(r == 14)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("highestLevel");
						}
						if(r == 15)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("logSessioNumber");
						}
						if(r == 16)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("NICT_sessionNumber");
						}
						if(r == 17)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("speed3_Avg");
						}
						if(r == 18)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("speed4_Avg");
						}
						if(r == 19)
						{
							cell_w = row.createCell(c);
							cell_w.setCellType(Cell.CELL_TYPE_STRING);
							cell_w.setCellValue("Study");
						}	
					}
					if(c > 1)
					{
						if(r == 1)
						{
							cell_w = row.createCell(c);
							if(WCN_sessionLabDay[day[0]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_sessionLabDay[day[0]]);
							}
							day[0]++;
						}
						if(r == 2)
						{
							cell_w = row.createCell(c);
							if(WCN_hitRateAvg_column[day[1]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_hitRateAvg_column[day[1]]);
							}
							day[1]++;
						}
						if(r == 3)
						{
							cell_w = row.createCell(c);
							if(WCN_errorRateAvg_column[day[2]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_errorRateAvg_column[day[2]]);
							}
							day[2]++;
						}
						if(r == 4)
						{
							cell_w = row.createCell(c);
							if(WCN_totalPlayedAvg_column[day[3]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_totalPlayedAvg_column[day[3]]);
							}
							day[3]++;
						}
						if(r == 5)
						{
							cell_w = row.createCell(c);
							if(WCN_graduationAvg_column[day[4]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_graduationAvg_column[day[4]]);
							}
							day[4]++;
						}
						if(r == 6)
						{
							cell_w = row.createCell(c);
							if(WCN_failToRespondRate1Avg_column[day[5]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_failToRespondRate1Avg_column[day[5]]);
							}
							day[5]++;
						}
						if(r == 7)
						{
							cell_w = row.createCell(c);
							if(WCN_failToRespondRate2Avg_column[day[6]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_failToRespondRate2Avg_column[day[6]]);
							}
							day[6]++;
						}
						if(r == 8)
						{
							cell_w = row.createCell(c);
							if(WCN_Speed1Avg_column[day[7]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_Speed1Avg_column[day[7]]);
							}
							day[7]++;
						}
						if(r == 9)
						{
							cell_w = row.createCell(c);
							if(WCN_Speed2Avg_column[day[8]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue(WCN_Speed2Avg_column[day[8]]);
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_Speed2Avg_column[day[8]]);
							}
							day[8]++;
						}
						if(r == 10)
						{
							cell_w = row.createCell(c);
							if(WCN_timeOnTask1Avg_column[day[9]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_timeOnTask1Avg_column[day[9]]);
							}
							day[9]++;
						}
						if(r == 11)
						{
							cell_w = row.createCell(c);
							if(WCN_timeOnTask2Avg_column[day[10]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_timeOnTask2Avg_column[day[10]]);
							}
							day[10]++;
						}
						if(r == 12)
						{
							cell_w = row.createCell(c);
							if(WCN_numTrialsToLevelUpAvg_column[day[11]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_numTrialsToLevelUpAvg_column[day[11]]);
							}
							day[11]++;
						}
						if(r == 13)
						{
							cell_w = row.createCell(c);
							if(WCN_speedAtLevelUpAvg_column[day[12]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_speedAtLevelUpAvg_column[day[12]]);
							}
							day[12]++;
						}
						if(r == 14)
						{
							cell_w = row.createCell(c);
							if(WCN_levelAvg_column[day[13]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_levelAvg_column[day[13]]);
							}
							day[13]++;
						}
						if(r == 15)
						{
							cell_w = row.createCell(c);
							if(WCN_logSession_day[day[14]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_logSession_day[day[14]]);
							}
							day[14]++;
						}
						if(r == 16)
						{
							cell_w = row.createCell(c);
							if(WCN_NICT_sessionNumber_day[day[15]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_NICT_sessionNumber_day[day[15]]);
							}
							day[15]++;
						}
						if(r == 17)
						{
							cell_w = row.createCell(c);
							if(WCN_Speed3Avg_column[day[16]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_Speed3Avg_column[day[16]]);
							}
							day[16]++;
						}
						if(r == 18)
						{
							cell_w = row.createCell(c);
							if(WCN_Speed4Avg_column[day[17]] == -1)
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell_w.setCellValue(WCN_Speed4Avg_column[day[17]]);
							}
							day[17]++;
						}
						if(r == 19)
						{
							cell_w = row.createCell(c);
							if(WCN_day[day[6]] < sessionDate_asNum[user_row_onSessionDateSheet][7])
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("NICT");
							}
							else
							{
								cell_w.setCellType(Cell.CELL_TYPE_STRING);
								cell_w.setCellValue("PostStudy");
							}
							day[18]++;
						}
					}
					
				}
				rows++;
			}			
				
		}
		//Finished WCN
   }//WCN_GAME
   
   			//======================================================================================================================//
   
   //Functions for the file based on 7DaysWeek
   
   public static void SWM_GAME_7DaysWeek(String[][] SWM_table, int SWM_row_num, int SWM_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
		   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
{
	
	//Finished SWM
}//SWM_GAME()

   public static void FLY_GAME_7DaysWeek(String[][] FLY_table, int FLY_row_num, int FLY_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
	   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
{
	   }//FLY_GAME()

   public static void CTB_GAME_7DaysWeek(String[][] CTB_table, int CTB_row_num, int CTB_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
	   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
{
	
	
}

   public static void WCN_GAME_7DaysWeek(String[][] WCN_table, int WCN_row_num, int WCN_col_num, String[][] organizedTable, String[][] organizedTable2, double[][] sessionDate_asNum, String[][] sessionDate,
	   int sessionDate_row_num, int sessionDate_col_num, Workbook workbook_w)
{
	//Finished WCN
}//WCN_GAME

   
}//END


































