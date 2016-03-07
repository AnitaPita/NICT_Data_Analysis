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



public class NICTAnalysisTool {

	public static void main(String[] args) throws Exception
	{
		new NICTAnalysisTool().run();
	}
	private void run() throws ParserConfigurationException, SAXException, IOException {


		//A limit of 100,000 rows in each raw data sheet was established
		//A limit of 50 columns in each raw data sheet was established
		
		//Working with excel (Reading data)

		final int NB_LINES =300;
		String[][] sessionInfoData_table = new String[NB_LINES][100]; // For input session data

		
		String[][] sessionDate_table = new String[NB_LINES][100];
		String[][] nbackByID_table = new String[NB_LINES][500];
		String[][] nbackByDate_table = new String[NB_LINES][100];
		String[][] sudokuByID_table = new String[NB_LINES][100];
		String[][] sudokuByDate_table = new String[NB_LINES][100]; //Added these rows for the 5 organized tables, mirrors "organized table" A.P

		String[][] sessionDate = new String[10000][50]; // Store the information from the sessionDate excel file
		
		int sessionInfoData_row_num = 0;
		int sessionInfoData_col_num = 0;
		
		int sessionDate_row_num = 0; // Total of rows in the SWM excel sheet
		int sessionDate_col_num = 0; // Total of columns in the SWM excel sheet
		
		String[][] timeOfDay = new String[10000][3]; //Store the preDate and postDate time of day
		
				
		//======================================================================================================================//
		//======================================================================================================================//
			
		/*First things first: get the output files and the session data file. Outputs are all in a folder "allReports" (to be in the system 
		 * library where all resources for a program are stored)
		 */
		File[] files = null;
		File[] responses = null;
		try {
			//Start with output files; store them in an array files[]
			File inputFolder = new File("All_Reports_updated"); //this will implements the single file algorithm below
			File correctedReportsFolder = null;
	    	if(inputFolder.isDirectory()) //check if said file is a directory
	    	{
	    		removeLeadingSpaces(inputFolder);
	    		correctedReportsFolder = new File("All_Reports_updated/correctedReports");
	    		//The following MUST OCCUR:
	    		files = inputFolder.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
	    		for (int i = 0; i < files.length; i++) 
	    		{
	    			//System.out.println(files[i].getName()); //get name of each file in the directory; print out
	    			//NEED TO REMOVE FIRST X SPACES IN EACH FILE
				}
	    	}
	    	
	    	//Get the text files containing data with response times
	    	File nbkResponses = new File("NbkResponses");
	    	
	    	if(nbkResponses.isDirectory()) //check if said file is a directory
	    	{
	    		//The following MUST OCCUR:
	    		responses = nbkResponses.listFiles(); // create array to hold all files in the directory, regardless of extension; puts all files in
	    		for (int i = 0; i < responses.length; i++) 
	    		{
	    			System.out.println(responses[i].getName()); //get name of each file in the directory; print out
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
	    	File a = new File("NICT_SessionDatesjan272016.xlsx");
	    	FileInputStream inputStream = new FileInputStream(a);
	    	Workbook sessionDatesInput = new XSSFWorkbook(inputStream);
	    	Sheet sessionInfoData = sessionDatesInput.getSheetAt(0);
	    	int t =sessionInfoData.getLastRowNum();
	    	//boolean proceed = true;
	    	for(Iterator<Row> rit = sessionInfoData.rowIterator(); rit.hasNext();)
	    	{
	    		Row row = rit.next();
	    		//System.out.println(row);
	    		Cell x = row.getCell(0);
	    		
	    		int v = row.getPhysicalNumberOfCells();
//	    		System.out.println(v);
	    		x.setCellType(Cell.CELL_TYPE_STRING);
	    		if(x.getStringCellValue().equals(""))
	    		{
	    			break;
	    		}
	    		for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
	    		{
	    			Cell cell = cit.next(); //Reads the cells in the file A.P
	    			cell.setCellType(Cell.CELL_TYPE_STRING); //reads each column as a string, and later, if it's a number, can convert to a number
//	    			System.out.println(cell.getRowIndex()+"="+cell.getColumnIndex()+"="+ cell.getStringCellValue());
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
			for(int c = 0 ; c < 348 ; c++) //348 is the number of columns
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

	
	private void removeLeadingSpaces(File inputFolder) {
		//The following MUST OCCUR:	
		File dir = new File("All_Reports_updated/correctedReports");
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
					FileOutputStream os= new FileOutputStream("All_Reports_updated/correctedReports/"+fileName);
					//BufferedWriter bw = new BufferedWriter("correctedReports/"+fileName);
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private Date createDate(String date) //Date must be of format YYYY-MM-DD
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
	
	private int getDaysDiff(Date from, Date to)
	{
		long diff = to.getTime() - from.getTime();
		long y = (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
	    //System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
		int x = (int)y; //convert to integer
		return x;
	}
	
	Double getResponseRate(String fileName) throws Exception
	{
		FileInputStream fs= new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line = br.readLine();
		String prevLine = line;
		while(line!=null){
			prevLine = line;
			line = br.readLine();
		}				  
		br.close();
		Double rt = 0.0;
		if(prevLine.startsWith("The average response time "))
		{
			int index1 = prevLine.indexOf("inputs is ");
			index1+= 10;
			int index2 = prevLine.indexOf("ms.");
			String rtAsString = prevLine.substring(index1, index2);
			System.out.println(prevLine);
			if(!rtAsString.isEmpty())
			{
				rt = Double.parseDouble(rtAsString); 
			}
		}
		
		return rt;
	}
	
	private void nbackByIDSheet(File file, File[] rr, String[][] sessionDate_table, String[][] nbackByID, int index) throws ParserConfigurationException, SAXException, IOException {
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
					if(rr[j].getName().contains("_"+gameID+".txt")) //if the files has this particular ending (prevents problems like containing "1" vs "10")
					{
						/*FileInputStream fs= new FileInputStream(rr[j]);
						BufferedReader br = new BufferedReader(new InputStreamReader(fs));
						String line = br.readLine();
						String prevLine = line;
						while(line!=null){
							prevLine = line;
							br.readLine();
						}				  
						
						if(prevLine.startsWith("The average response time "))
						{
							int index1 = prevLine.indexOf("inputs is ");
							index1+= 10;
							int index2 = prevLine.indexOf("ms.");
							String rtAsString = prevLine.substring(index1, index2);
							rt = Double.parseDouble(prevLine); 
						}*/
						rt = getResponseRate(rr[j].getPath());
						
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
        
        //cannot fill in the number back data 
		
        nbackByID[index][27] = String.valueOf(avgHRSpatialwk1);//"HitRateAvg_wk1_img";
		nbackByID[index][28] = String.valueOf(avgHRSpatialwk2);
		nbackByID[index][29] = String.valueOf(avgHRSpatialwk3);
		nbackByID[index][30] = String.valueOf(avgHRSpatialwk4);
		nbackByID[index][31] = String.valueOf(avgHRSpatialwk5);
		nbackByID[index][32] = String.valueOf(avgHRLetNumwk1);//"HitRateAvg_wk1_letterNumber";
		nbackByID[index][33] = String.valueOf(avgHRLetNumwk2);
		nbackByID[index][34] = String.valueOf(avgHRLetNumwk3);
		nbackByID[index][35] = String.valueOf(avgHRLetNumwk4);
		nbackByID[index][36] = String.valueOf(avgHRLetNumwk5);
		nbackByID[index][37] = String.valueOf(avgHRImgwk1);//"HitRateAvg_wk1_spatial";
		nbackByID[index][38] = String.valueOf(avgHRImgwk2);
		nbackByID[index][39] = String.valueOf(avgHRImgwk3);
		nbackByID[index][40] = String.valueOf(avgHRImgwk4);
		nbackByID[index][41] = String.valueOf(avgHRImgwk5);
		
		//More number back data that cannot be filled
		
		nbackByID[index][58] = String.valueOf(avgERImgwk1);//"ErrRateAvg_wk1_img";
		nbackByID[index][59] = String.valueOf(avgERImgwk2);
		nbackByID[index][60] = String.valueOf(avgERImgwk3);
		nbackByID[index][61] = String.valueOf(avgERImgwk4);
		nbackByID[index][62] = String.valueOf(avgERImgwk5);
		nbackByID[index][63] = String.valueOf(avgERLetNumwk1);//"ErrRateAvg_wk1_letterNumber";
		nbackByID[index][64] = String.valueOf(avgERLetNumwk2);
		nbackByID[index][65] = String.valueOf(avgERLetNumwk3);
		nbackByID[index][66] = String.valueOf(avgERLetNumwk4);
		nbackByID[index][67] = String.valueOf(avgERLetNumwk5);
		nbackByID[index][68] = String.valueOf(avgERSpatialwk1);//"ErrRateAvg_wk1_spatial";
		nbackByID[index][69] = String.valueOf(avgERSpatialwk2);
		nbackByID[index][70] = String.valueOf(avgERSpatialwk3);
		nbackByID[index][71] = String.valueOf(avgERSpatialwk4);
		nbackByID[index][72] = String.valueOf(avgERSpatialwk5);
		nbackByID[index][73] = String.valueOf(totalGamesSpatialwk1);//"NumGamesPlayed_wk1_spatial"; 
		nbackByID[index][74] = String.valueOf(totalGamesSpatialwk2); 
		nbackByID[index][75] = String.valueOf(totalGamesSpatialwk3); 
		nbackByID[index][76] = String.valueOf(totalGamesSpatialwk4); 
		nbackByID[index][77] = String.valueOf(totalGamesSpatialwk5);
		nbackByID[index][78] = String.valueOf(totalGamesLetNumwk1);//"NumGamesPlayed_wk1_letterNumber"; 
		nbackByID[index][79] = String.valueOf(totalGamesLetNumwk2); 
		nbackByID[index][80] = String.valueOf(totalGamesLetNumwk3); 
		nbackByID[index][81] = String.valueOf(totalGamesLetNumwk4); 
		nbackByID[index][82] = String.valueOf(totalGamesLetNumwk5);
		nbackByID[index][83] = String.valueOf(totalGamesImgwk1);//"NumGamesPlayed_wk1_img"; 
		nbackByID[index][84] = String.valueOf(totalGamesImgwk2);
		nbackByID[index][85] = String.valueOf(totalGamesImgwk3);
		nbackByID[index][86] = String.valueOf(totalGamesImgwk4);
		nbackByID[index][87] = String.valueOf(totalGamesImgwk5); 
		nbackByID[index][88] = String.valueOf(timeSpatialwk1);//"TimeSpent_wk1_spatial"; IN SECONDS
		nbackByID[index][89] = String.valueOf(timeSpatialwk2);
		nbackByID[index][90] = String.valueOf(timeSpatialwk3);
		nbackByID[index][91] = String.valueOf(timeSpatialwk4);
		nbackByID[index][92] = String.valueOf(timeSpatialwk5);
		nbackByID[index][93] = String.valueOf(timeLetNumwk1);//"TimeSpent_wk1_letterNumber";
		nbackByID[index][94] = String.valueOf(timeLetNumwk2);
		nbackByID[index][95] = String.valueOf(timeLetNumwk3);
		nbackByID[index][96] = String.valueOf(timeLetNumwk4);
		nbackByID[index][97] = String.valueOf(timeLetNumwk5);
		nbackByID[index][98] = String.valueOf(timeImgwk1);//"TimeSpent_wk1_img";
		nbackByID[index][99] = String.valueOf(timeImgwk2);
		nbackByID[index][100] = String.valueOf(timeImgwk3);
		nbackByID[index][101] = String.valueOf(timeImgwk4);
		nbackByID[index][102] = String.valueOf(timeImgwk5);
		nbackByID[index][103] = String.valueOf(avgRTSpatialwk1);//"AvgRT_wk1_spatial";
		nbackByID[index][104] = String.valueOf(avgRTSpatialwk2);
		nbackByID[index][105] = String.valueOf(avgRTSpatialwk3);
		nbackByID[index][106] = String.valueOf(avgRTSpatialwk4);
		nbackByID[index][107] = String.valueOf(avgRTSpatialwk5);
		nbackByID[index][108] = String.valueOf(avgRTLetNumwk1);//"AvgRT_wk1_letterNumber";
		nbackByID[index][109] = String.valueOf(avgRTLetNumwk2);
		nbackByID[index][110] = String.valueOf(avgRTLetNumwk3);
		nbackByID[index][111] = String.valueOf(avgRTLetNumwk4);
		nbackByID[index][112] = String.valueOf(avgRTLetNumwk5);
		nbackByID[index][113] = String.valueOf(avgRTImgwk1);//"AvgRT_wk1_img"; 
		nbackByID[index][114] = String.valueOf(avgRTImgwk2);
		nbackByID[index][115] = String.valueOf(avgRTImgwk3);
		nbackByID[index][116] = String.valueOf(avgRTImgwk4);
		nbackByID[index][117] = String.valueOf(avgRTImgwk5);
		
		nbackByID[index][118] = String.valueOf(avgHRwk1);//"HitRateAvg_wk1";
		nbackByID[index][119] = String.valueOf(avgHRwk2);
		nbackByID[index][120] = String.valueOf(avgHRwk3);
		nbackByID[index][121] = String.valueOf(avgHRwk4);
		nbackByID[index][122] = String.valueOf(avgERwk5);
		nbackByID[index][123] = String.valueOf(avgERwk1); //"ErrRateAvg_wk1";
		nbackByID[index][124] = String.valueOf(avgERwk2);
		nbackByID[index][125] = String.valueOf(avgERwk3);
		nbackByID[index][126] = String.valueOf(avgERwk4);
		nbackByID[index][127] = String.valueOf(avgERwk5);
		nbackByID[index][128] = String.valueOf(totalGameswk1);//"NumGamesPlayed_wk1"; 
		nbackByID[index][129] = String.valueOf(totalGameswk2);
		nbackByID[index][130] = String.valueOf(totalGameswk3);
		nbackByID[index][131] = String.valueOf(totalGameswk4);
		nbackByID[index][132] = String.valueOf(totalGameswk5);
		nbackByID[index][133] = String.valueOf(timewk1);//"TimeSpent_wk1";
		nbackByID[index][134] = String.valueOf(timewk2);
		nbackByID[index][135] = String.valueOf(timewk3);
		nbackByID[index][136] = String.valueOf(timewk4);
		nbackByID[index][137] = String.valueOf(timewk5);
		nbackByID[index][138] = String.valueOf(avgRTwk1);//"AvgRT_wk1";  
		nbackByID[index][139] = String.valueOf(avgRTwk2);
		nbackByID[index][140] = String.valueOf(avgRTwk3);
		nbackByID[index][141] = String.valueOf(avgRTwk4);
		nbackByID[index][142] = String.valueOf(avgRTwk5);
		
		nbackByID[index][143] = String.valueOf(avgHRSpatial);//"HitRateAvg_spatial"; 
		nbackByID[index][144] = String.valueOf(avgHRLetNum);//"HitRateAvg_letterNumber";
		nbackByID[index][145] = String.valueOf(avgHRImg);//"HitRateAvg_img";
		nbackByID[index][146] = String.valueOf(avgERSpatial);//"ErrRateAvg_spatial"; 
		nbackByID[index][147] = String.valueOf(avgERLetNum);//"ErrRateAvg_letterNumber";
		nbackByID[index][148] = String.valueOf(avgERImg);//"ErrRateAvg_img";
		nbackByID[index][149] = String.valueOf(totalGamesSpatial);//"NumGamesPlayed_spatial";
		nbackByID[index][150] = String.valueOf(totalGamesLetNum);//"NumGamesPlayed_letterNumber";
		nbackByID[index][151] = String.valueOf(totalGamesImg);//"NumGamesPlayed_img";
		nbackByID[index][152] = String.valueOf(timeSpatial);//"TimeSpent_spatial"; 
		nbackByID[index][153] = String.valueOf(timeLetNum);//"TimeSpent_letterNumber";
		nbackByID[index][154] = String.valueOf(timeImg);//"TimeSpent_img";
		nbackByID[index][155] = String.valueOf(avgRTSpatial);//"AvgRT_spatial";
		nbackByID[index][156] = String.valueOf(avgRTLetNum);//"AvgRT_letterNumber";
		nbackByID[index][157] = String.valueOf(avgRTImg);//"AvgRT_img";
		
		//Now fill in lab data
		//=====================================LAB================================================================
		nbackByID[index][158] = String.valueOf(avgHRSpatialLab1); //CHECK"LabX_HitRateAvg_spatial" x 5 
		nbackByID[index][159] = String.valueOf(avgHRSpatialLab2); 
		nbackByID[index][160] = String.valueOf(avgHRSpatialLab3); 
		nbackByID[index][161] = String.valueOf(avgHRSpatialLab4); 
		nbackByID[index][162] = String.valueOf(avgHRSpatialLab5); 
		nbackByID[index][163] = String.valueOf(avgHRLetNumLab1);  //CHECK"LabX_HitRateAvg_letterNumber" x 5
		nbackByID[index][164] = String.valueOf(avgHRLetNumLab2); 
		nbackByID[index][165] = String.valueOf(avgHRLetNumLab3);
		nbackByID[index][166] = String.valueOf(avgHRLetNumLab4);
		nbackByID[index][167] = String.valueOf(avgHRLetNumLab5);
		nbackByID[index][168] = String.valueOf(avgHRImgLab1); //CHECK"LabX_HitRateAvg_img" x 5
		nbackByID[index][169] = String.valueOf(avgHRImgLab2); 
		nbackByID[index][170] = String.valueOf(avgHRImgLab3);
		nbackByID[index][171] = String.valueOf(avgHRImgLab4);
		nbackByID[index][172] = String.valueOf(avgHRImgLab5);
		
		//lab errRates
		nbackByID[index][173] = String.valueOf(avgERSpatialLab1);//CHECK"LabX_ErrRateAvg_spatial" x 5
		nbackByID[index][174] = String.valueOf(avgERSpatialLab2);
		nbackByID[index][175] = String.valueOf(avgERSpatialLab3);
		nbackByID[index][176] = String.valueOf(avgERSpatialLab4);
		nbackByID[index][177] = String.valueOf(avgERSpatialLab5);
		nbackByID[index][178] = String.valueOf(avgERLetNumLab1);//CHECK"LabX_ErrRateAvg_letterNumber" x 5
		nbackByID[index][179] = String.valueOf(avgERLetNumLab2);
		nbackByID[index][180] = String.valueOf(avgERLetNumLab3);
		nbackByID[index][181] = String.valueOf(avgERLetNumLab4);
		nbackByID[index][182] = String.valueOf(avgERLetNumLab5);
		nbackByID[index][183] = String.valueOf(avgERImgLab1); //CHECK"LabX_ErrRateAvg_img" x 5
		nbackByID[index][184] = String.valueOf(avgERImgLab2);
		nbackByID[index][185] = String.valueOf(avgERImgLab3);
		nbackByID[index][186] = String.valueOf(avgERImgLab4);
		nbackByID[index][187] = String.valueOf(avgERImgLab5);
		
		//lab number of games played
		nbackByID[index][188] = String.valueOf(totalGamesSpatialLab1); //CHECK"LabX_NumGamesPlayed_spatial" x 5
		nbackByID[index][189] = String.valueOf(totalGamesSpatialLab2);
		nbackByID[index][190] = String.valueOf(totalGamesSpatialLab3);
		nbackByID[index][191] = String.valueOf(totalGamesSpatialLab4);
		nbackByID[index][192] = String.valueOf(totalGamesSpatialLab5);
		nbackByID[index][193] = String.valueOf(totalGamesLetNumLab1); //CHECK"LabX_NumGamesPlayed_letterNumber" x 5
		nbackByID[index][194] = String.valueOf(totalGamesLetNumLab2);
		nbackByID[index][195] = String.valueOf(totalGamesLetNumLab3);
		nbackByID[index][196] = String.valueOf(totalGamesLetNumLab4);
		nbackByID[index][197] = String.valueOf(totalGamesLetNumLab5);
		nbackByID[index][198] = String.valueOf(totalGamesImgLab1); //CHECK"LabX_NumGamesPlayed_img"x 5
		nbackByID[index][199] = String.valueOf(totalGamesImgLab2); 
		nbackByID[index][200] = String.valueOf(totalGamesImgLab3);
		nbackByID[index][201] = String.valueOf(totalGamesImgLab4);
		nbackByID[index][202] = String.valueOf(totalGamesImgLab5);
		
		//lab time spent on each type of game
		nbackByID[index][203] = String.valueOf(timeSpatialLab1); //CHECK"LabX_TimeSpent_spatial" x 5
		nbackByID[index][204] = String.valueOf(timeSpatialLab2);
		nbackByID[index][205] = String.valueOf(timeSpatialLab3);
		nbackByID[index][206] = String.valueOf(timeSpatialLab4); 
		nbackByID[index][207] = String.valueOf(timeSpatialLab5);
		nbackByID[index][208] = String.valueOf(timeLetNumLab1);//CHECK"LabX_TimeSpent_letterNumber" x 5
		nbackByID[index][209] = String.valueOf(timeLetNumLab2);
		nbackByID[index][210] = String.valueOf(timeLetNumLab3);
		nbackByID[index][211] = String.valueOf(timeLetNumLab4);
		nbackByID[index][212] = String.valueOf(timeLetNumLab5);
		nbackByID[index][213] = String.valueOf(timeImgLab1); //CHECK"LabX_TimeSpent_img"x 5
		nbackByID[index][214] = String.valueOf(timeImgLab2);  
		nbackByID[index][215] = String.valueOf(timeImgLab3); 
		nbackByID[index][216] = String.valueOf(timeImgLab4); 
		nbackByID[index][217] = String.valueOf(timeImgLab5); 
		
		//lab average RT
		nbackByID[index][218] = String.valueOf(avgRTSpatialLab1);  //CHECK"LabX_AvgRT_spatial" x 5
		nbackByID[index][219] = String.valueOf(avgRTSpatialLab2); 
		nbackByID[index][220] = String.valueOf(avgRTSpatialLab3); 
		nbackByID[index][221] = String.valueOf(avgRTSpatialLab4); 
		nbackByID[index][222] = String.valueOf(avgRTSpatialLab5); 
		nbackByID[index][223] = String.valueOf(avgRTLetNumLab1); //CHECK"LabX_AvgRT_letterNumber" x 5
		nbackByID[index][224] = String.valueOf(avgRTLetNumLab2);
		nbackByID[index][225] = String.valueOf(avgRTLetNumLab3);
		nbackByID[index][226] = String.valueOf(avgRTLetNumLab4);
		nbackByID[index][227] = String.valueOf(avgRTLetNumLab5);
		nbackByID[index][228] = String.valueOf(avgRTImgLab1); //CHECK"LabX_AvgRT_img"x 5
		nbackByID[index][229] = String.valueOf(avgRTImgLab2);
		nbackByID[index][230] = String.valueOf(avgRTImgLab3);
		nbackByID[index][231] = String.valueOf(avgRTImgLab4);
		nbackByID[index][232] = String.valueOf(avgRTImgLab5);
		
		//Overall data for the labs
		nbackByID[index][218] = String.valueOf(avgHRLab1); 
		nbackByID[index][219] = String.valueOf(avgHRLab2); 
		nbackByID[index][220] = String.valueOf(avgHRLab3); 
		nbackByID[index][221] = String.valueOf(avgHRLab4);
		nbackByID[index][222] = String.valueOf(avgHRLab5);
		nbackByID[index][223] = String.valueOf(avgERLab1); 
		nbackByID[index][224] = String.valueOf(avgERLab2);
		nbackByID[index][225] = String.valueOf(avgERLab3);
		nbackByID[index][226] = String.valueOf(avgERLab4);
		nbackByID[index][227] = String.valueOf(avgERLab5);
		nbackByID[index][228] = String.valueOf(totalGamesLab1); 
		nbackByID[index][229] = String.valueOf(totalGamesLab2);
		nbackByID[index][230] = String.valueOf(totalGamesLab3);
		nbackByID[index][231] = String.valueOf(totalGamesLab4);
		nbackByID[index][232] = String.valueOf(totalGamesLab5);
		nbackByID[index][233] = String.valueOf(timeLab1); 
		nbackByID[index][234] = String.valueOf(timeLab2); 
		nbackByID[index][235] = String.valueOf(timeLab3); 
		nbackByID[index][236] = String.valueOf(timeLab4);
		nbackByID[index][237] = String.valueOf(timeLab5); 
		nbackByID[index][238] = String.valueOf(avgRTLab1);
		nbackByID[index][239] = String.valueOf(avgRTLab2);
		nbackByID[index][240] = String.valueOf(avgRTLab3);
		nbackByID[index][241] = String.valueOf(avgRTLab4);
		nbackByID[index][242] = String.valueOf(avgRTLab5);
		
		//==================================================HOME VARIABLES==================================================
		
		// Repeat for home and each general home
		// Home hitRates
		nbackByID[index][243] = String.valueOf(avgHRSpatialwk1Home); //CHECK"Home_HitRateAvg_spatial_wkX" x 5
		nbackByID[index][244] = String.valueOf(avgHRSpatialwk2Home);
		nbackByID[index][245] = String.valueOf(avgHRSpatialwk3Home);
		nbackByID[index][246] = String.valueOf(avgHRSpatialwk4Home);
		nbackByID[index][247] = String.valueOf(avgHRSpatialwk5Home);
		nbackByID[index][248] = String.valueOf(avgHRLetNumwk1Home);//"Home_HitRateAvg_letterNumber_wk1"; x 5
		nbackByID[index][249] = String.valueOf(avgHRLetNumwk2Home);
		nbackByID[index][250] = String.valueOf(avgHRLetNumwk3Home);
		nbackByID[index][251] = String.valueOf(avgHRLetNumwk4Home);
		nbackByID[index][252] = String.valueOf(avgHRLetNumwk5Home);
		nbackByID[index][253] = String.valueOf(avgHRImgwk1Home);//"Home_HitRateAvg_img_wk1"; x 5
		nbackByID[index][254] = String.valueOf(avgHRImgwk2Home);
		nbackByID[index][255] = String.valueOf(avgHRImgwk3Home);
		nbackByID[index][256] = String.valueOf(avgHRImgwk4Home);
		nbackByID[index][257] = String.valueOf(avgHRImgwk5Home);

		// home errRates
		nbackByID[index][258] = String.valueOf(avgERSpatialwk1Home);//"Home_ErrRateAvg_spatial_wk1"; x 5
		nbackByID[index][259] = String.valueOf(avgERSpatialwk2Home);
		nbackByID[index][260] = String.valueOf(avgERSpatialwk3Home);
		nbackByID[index][261] = String.valueOf(avgERSpatialwk4Home);
		nbackByID[index][262] = String.valueOf(avgERSpatialwk5Home);
		nbackByID[index][263] = String.valueOf(avgERLetNumwk1Home); //"Home_ErrRateAvg_letterNumber_wk1";  x 5
		nbackByID[index][264] = String.valueOf(avgERLetNumwk2Home);
		nbackByID[index][265] = String.valueOf(avgERLetNumwk3Home);
		nbackByID[index][266] = String.valueOf(avgERLetNumwk4Home);
		nbackByID[index][267] = String.valueOf(avgERLetNumwk5Home);
		nbackByID[index][268] = String.valueOf(avgERImgwk1Home);//"Home_ErrRateAvg_img_wk1"; x 5 
		nbackByID[index][269] = String.valueOf(avgERImgwk2Home);
		nbackByID[index][270] = String.valueOf(avgERImgwk3Home);
		nbackByID[index][271] = String.valueOf(avgERImgwk4Home);
		nbackByID[index][272] = String.valueOf(avgERImgwk5Home);

		// home number of games played
		nbackByID[index][273] = String.valueOf(totalGamesSpatialwk1Home);//"Home_NumGamesPlayed_spatial_wk1"; x 5
		nbackByID[index][274] = String.valueOf(totalGamesSpatialwk2Home);
		nbackByID[index][275] = String.valueOf(totalGamesSpatialwk3Home);
		nbackByID[index][276] = String.valueOf(totalGamesSpatialwk4Home);
		nbackByID[index][277] = String.valueOf(totalGamesSpatialwk5Home);
		nbackByID[index][278] = String.valueOf(totalGamesLetNumwk1Home); //"Home_NumGamesPlayed_letterNumber_wk1"; x 5
		nbackByID[index][279] = String.valueOf(totalGamesLetNumwk2Home);
		nbackByID[index][280] = String.valueOf(totalGamesLetNumwk3Home);
		nbackByID[index][281] = String.valueOf(totalGamesLetNumwk4Home);
		nbackByID[index][282] = String.valueOf(totalGamesLetNumwk5Home);
		nbackByID[index][283] = String.valueOf(totalGamesImgwk1Home);//"Home_NumGamesPlayed_img_wk1"; x5
		nbackByID[index][284] = String.valueOf(totalGamesImgwk2Home);
		nbackByID[index][285] = String.valueOf(totalGamesImgwk3Home);
		nbackByID[index][286] = String.valueOf(totalGamesImgwk4Home);
		nbackByID[index][287] = String.valueOf(totalGamesImgwk5Home);

		// home time spent on each type of game
		nbackByID[index][288] = String.valueOf(timeSpatialwk1Home);// "Home_TimeSpent_spatial_wk1"; x 5
		nbackByID[index][289] = String.valueOf(timeSpatialwk2Home);
		nbackByID[index][290] = String.valueOf(timeSpatialwk3Home);
		nbackByID[index][291] = String.valueOf(timeSpatialwk4Home);
		nbackByID[index][292] = String.valueOf(timeSpatialwk5Home);
		nbackByID[index][293] = String.valueOf(timeLetNumwk1Home); //"Home_TimeSpent_letterNumber_wk1"; x 5
		nbackByID[index][294] = String.valueOf(timeLetNumwk2Home);
		nbackByID[index][295] = String.valueOf(timeLetNumwk3Home);
		nbackByID[index][296] = String.valueOf(timeLetNumwk4Home);
		nbackByID[index][297] = String.valueOf(timeLetNumwk5Home);
		nbackByID[index][298] = String.valueOf(timeImgwk1Home); //"Home_TimeSpent_img_wk1"; x 5
		nbackByID[index][299] = String.valueOf(timeImgwk2Home);
		nbackByID[index][300] = String.valueOf(timeImgwk3Home);
		nbackByID[index][301] = String.valueOf(timeImgwk4Home);
		nbackByID[index][302] = String.valueOf(timeImgwk5Home);

		// home average RT
		nbackByID[index][303] = String.valueOf(avgRTSpatialwk1Home);//"Home_AvgRT_spatial_wk1"; x 5
		nbackByID[index][304] = String.valueOf(avgRTSpatialwk2Home);
		nbackByID[index][305] = String.valueOf(avgRTSpatialwk3Home);
		nbackByID[index][306] = String.valueOf(avgRTSpatialwk4Home);
		nbackByID[index][307] = String.valueOf(avgRTSpatialwk5Home);
		nbackByID[index][308] = String.valueOf(avgRTLetNumwk1Home);//"Home_AvgRT_letterNumber_wk1"; x 5
		nbackByID[index][309] = String.valueOf(avgRTLetNumwk2Home); 
		nbackByID[index][310] = String.valueOf(avgRTLetNumwk3Home); 
		nbackByID[index][311] = String.valueOf(avgRTLetNumwk4Home); 
		nbackByID[index][312] = String.valueOf(avgRTLetNumwk5Home); 
		nbackByID[index][313] = String.valueOf(avgRTImgwk1Home);//"Home_AvgRT_img_wk1"; x 5
		nbackByID[index][314] = String.valueOf(avgRTImgwk2Home);
		nbackByID[index][315] = String.valueOf(avgRTImgwk3Home);
		nbackByID[index][316] = String.valueOf(avgRTImgwk4Home);
		nbackByID[index][317] = String.valueOf(avgRTImgwk5Home);

		// Overall data for the labs
		nbackByID[index][318] = String.valueOf(avgHRwk1Home);//"Home_HitRateAvg_wk1"; x 5
		nbackByID[index][319] = String.valueOf(avgHRwk2Home);
		nbackByID[index][320] = String.valueOf(avgHRwk3Home);
		nbackByID[index][321] = String.valueOf(avgHRwk4Home);
		nbackByID[index][322] = String.valueOf(avgHRwk5Home);
		nbackByID[index][323] = String.valueOf(avgERwk1Home);//"Home_ErrRateAvg_wk1"; x 5
		nbackByID[index][324] = String.valueOf(avgERwk2Home);
		nbackByID[index][325] = String.valueOf(avgERwk3Home);
		nbackByID[index][326] = String.valueOf(avgERwk4Home);
		nbackByID[index][327] = String.valueOf(avgERwk5Home);
		nbackByID[index][328] = String.valueOf(totalGameswk1Home);//"Home_NumGamesPlayed_wk1"; x 5
		nbackByID[index][329] = String.valueOf(totalGameswk2Home);
		nbackByID[index][330] = String.valueOf(totalGameswk3Home);
		nbackByID[index][331] = String.valueOf(totalGameswk4Home);
		nbackByID[index][332] = String.valueOf(totalGameswk5Home);
		nbackByID[index][333] = String.valueOf(timewk1Home);//"Home_TimeSpent_wk1"; x 5
		nbackByID[index][334] = String.valueOf(timewk2Home);
		nbackByID[index][335] = String.valueOf(timewk3Home);
		nbackByID[index][336] = String.valueOf(timewk4Home);
		nbackByID[index][337] = String.valueOf(timewk5Home);
		nbackByID[index][338] = String.valueOf(avgRTwk1Home);//"Home_AvgRT_wk1"; x 5
		nbackByID[index][339] = String.valueOf(avgRTwk2Home);
		nbackByID[index][340] = String.valueOf(avgRTwk3Home);
		nbackByID[index][341] = String.valueOf(avgRTwk4Home);
		nbackByID[index][342] = String.valueOf(avgRTwk5Home);
		
		//NEED THE FOLLOWING DATA AT THE END
		nbackByID[index][343] = String.valueOf(totalSessionsWk1);//"TotalSessions_wk1"; //number of logins divided by 2
		nbackByID[index][344] = String.valueOf(totalSessionsWk2);
		nbackByID[index][345] = String.valueOf(totalSessionsWk3);
		nbackByID[index][346] = String.valueOf(totalSessionsWk4);
		nbackByID[index][347] = String.valueOf(totalSessionsWk5);
		
		}
		catch(Exception e) //If it finds an error, don't continue
		{
			e.printStackTrace();
		}
	}
	
	private  String[][] nbackByDateSheet(File file,String[][] sessionDate_table, String[][]nbackByDateRows) throws ParserConfigurationException, SAXException, IOException {
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
	
	private  void sudokuByIDSheet(File file,String[][] sessionDate_table, String[][] nbackByID, int index) throws ParserConfigurationException, SAXException, IOException {
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
	
	private  String[][] sudokuByDateSheet(File file, String[][] sessionDate_table, String[][] sudokuByDateRows) {
		// TODO Auto-generated method stub
		return sudokuByDateRows;
	}

	// Functions
	
	//Functions for the file based on sessionDates
	
	private  String[][] sessionDateSheetInit(String[][] sessionInfoData_table, int sessionInfoData_row_num, int sessionInfoData_col_num,
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
			//System.out.println(sessionInfoData_table[i][0]);
			if(sessionInfoData_table[i][0]!=null) //Skip the row if it does not have any data in it
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
					sessionDate_table[i][12] = sessionInfoData_table[i][5]; //PRE-DATE TIME is the thirteenth Column
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
				for(int j = 0; j<13;j++)
				{
//					System.out.println(sessionDate_table[i][j]);
//					System.out.println(missingInfo[j]);
					if(missingInfo[j]!=null)
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
	private  String[][] nbackByIDSheetInit(String[][] nbackByID_table) {
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
		
		nbackByID_table[0][27] = "HitRateAvg_wk1_spatial";
		nbackByID_table[0][28] = "HitRateAvg_wk2_spatial";
		nbackByID_table[0][29] = "HitRateAvg_wk3_spatial";
		nbackByID_table[0][30] = "HitRateAvg_wk4_spatial";
		nbackByID_table[0][31] = "HitRateAvg_wk5_spatial";
		nbackByID_table[0][32] = "HitRateAvg_wk1_letterNumber";
		nbackByID_table[0][33] = "HitRateAvg_wk2_letterNumber";
		nbackByID_table[0][34] = "HitRateAvg_wk3_letterNumber";
		nbackByID_table[0][35] = "HitRateAvg_wk4_letterNumber";
		nbackByID_table[0][36] = "HitRateAvg_wk5_letterNumber";
		nbackByID_table[0][37] = "HitRateAvg_wk1_img";
		nbackByID_table[0][38] = "HitRateAvg_wk2_img";
		nbackByID_table[0][39] = "HitRateAvg_wk3_img";
		nbackByID_table[0][40] = "HitRateAvg_wk4_img";
		nbackByID_table[0][41] = "HitRateAvg_wk5_img";
		
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
		
		nbackByID_table[0][58] = "ErrRateAvg_wk1_spatial";
		nbackByID_table[0][59] = "ErrRateAvg_wk2_spatial";
		nbackByID_table[0][60] = "ErrRateAvg_wk3_spatial";
		nbackByID_table[0][61] = "ErrRateAvg_wk4_spatial";
		nbackByID_table[0][62] = "ErrRateAvg_wk5_spatial";
		nbackByID_table[0][63] = "ErrRateAvg_wk1_letterNumber";
		nbackByID_table[0][64] = "ErrRateAvg_wk2_letterNumber";
		nbackByID_table[0][65] = "ErrRateAvg_wk3_letterNumber";
		nbackByID_table[0][66] = "ErrRateAvg_wk4_letterNumber";
		nbackByID_table[0][67] = "ErrRateAvg_wk5_letterNumber";
		nbackByID_table[0][68] = "ErrRateAvg_wk1_img";
		nbackByID_table[0][69] = "ErrRateAvg_wk2_img";
		nbackByID_table[0][70] = "ErrRateAvg_wk3_img";
		nbackByID_table[0][71] = "ErrRateAvg_wk4_img";
		nbackByID_table[0][72] = "ErrRateAvg_wk5_img";
		
		nbackByID_table[0][73] = "NumGamesPlayed_wk1_spatial"; //CHECK"NumGamesPlayed_wk1_spatial" "NumGames_wk2_spatial" "NumGames_wk3_spatial" etc x 15
		nbackByID_table[0][74] = "NumGamesPlayed_wk2_spatial"; 
		nbackByID_table[0][75] = "NumGamesPlayed_wk3_spatial"; 
		nbackByID_table[0][76] = "NumGamesPlayed_wk4_spatial"; 
		nbackByID_table[0][77] = "NumGamesPlayed_wk5_spatial"; 
		nbackByID_table[0][78] = "NumGamesPlayed_wk1_letterNumber"; 
		nbackByID_table[0][79] = "NumGamesPlayed_wk2_letterNumber"; 
		nbackByID_table[0][80] = "NumGamesPlayed_wk3_letterNumber"; 
		nbackByID_table[0][81] = "NumGamesPlayed_wk4_letterNumber";  
		nbackByID_table[0][82] = "NumGamesPlayed_wk5_letterNumber"; 
		nbackByID_table[0][83] = "NumGamesPlayed_wk1_img"; 
		nbackByID_table[0][84] = "NumGamesPlayed_wk2_img"; 
		nbackByID_table[0][85] = "NumGamesPlayed_wk3_img"; 
		nbackByID_table[0][86] = "NumGamesPlayed_wk4_img"; 
		nbackByID_table[0][87] = "NumGamesPlayed_wk5_img"; 
		nbackByID_table[0][88] = "TimeSpent_wk1_spatial"; //CHECK"TimeSpent_wk1_spatial" "TimeSpent_wk2_spatial" etc x 15
		nbackByID_table[0][89] = "TimeSpent_wk2_spatial"; //IN SECONDS
		nbackByID_table[0][90] = "TimeSpent_wk3_spatial";
		nbackByID_table[0][91] = "TimeSpent_wk4_spatial";
		nbackByID_table[0][92] = "TimeSpent_wk5_spatial";
		nbackByID_table[0][93] = "TimeSpent_wk1_letterNumber";
		nbackByID_table[0][94] = "TimeSpent_wk2_letterNumber";
		nbackByID_table[0][95] = "TimeSpent_wk3_letterNumber";
		nbackByID_table[0][96] = "TimeSpent_wk4_letterNumber";
		nbackByID_table[0][97] = "TimeSpent_wk5_letterNumber";
		nbackByID_table[0][98] = "TimeSpent_wk1_img";
		nbackByID_table[0][99] = "TimeSpent_wk2_img";
		nbackByID_table[0][100] = "TimeSpent_wk3_img";
		nbackByID_table[0][101] = "TimeSpent_wk4_img";
		nbackByID_table[0][102] = "TimeSpent_wk5_img";
		nbackByID_table[0][103] = "AvgRT_wk1_spatial";//CHECK"AvgRT_wk1_spatial" "AvgRT_wk2_spatial" etc x 15
		nbackByID_table[0][104] = "AvgRT_wk2_spatial";
		nbackByID_table[0][105] = "AvgRT_wk3_spatial";
		nbackByID_table[0][106] = "AvgRT_wk4_spatial";
		nbackByID_table[0][107] = "AvgRT_wk5_spatial";
		nbackByID_table[0][108] = "AvgRT_wk1_letterNumber";
		nbackByID_table[0][109] = "AvgRT_wk2_letterNumber";
		nbackByID_table[0][110] = "AvgRT_wk3_letterNumber";
		nbackByID_table[0][111] = "AvgRT_wk4_letterNumber";
		nbackByID_table[0][112] = "AvgRT_wk5_letterNumber"; 
		nbackByID_table[0][113] = "AvgRT_wk1_img"; 
		nbackByID_table[0][114] = "AvgRT_wk2_img";
		nbackByID_table[0][115] = "AvgRT_wk3_img";
		nbackByID_table[0][116] = "AvgRT_wk4_img";
		nbackByID_table[0][117] = "AvgRT_wk5_img";
		
		nbackByID_table[0][118] = "HitRateAvg_wk1";//CHECK"HitRateAvg_wk1" x 5
		nbackByID_table[0][119] = "HitRateAvg_wk2";
		nbackByID_table[0][120] = "HitRateAvg_wk3";
		nbackByID_table[0][121] = "HitRateAvg_wk4";
		nbackByID_table[0][122] = "HitRateAvg_wk5";
		nbackByID_table[0][123] = "ErrRateAvg_wk1"; //CHECK"ErrRateAvg_wk1" x 5
		nbackByID_table[0][124] = "ErrRateAvg_wk2";
		nbackByID_table[0][125] = "ErrRateAvg_wk3";
		nbackByID_table[0][126] = "ErrRateAvg_wk4";
		nbackByID_table[0][127] = "ErrRateAvg_wk5";
		nbackByID_table[0][128] = "NumGamesPlayed_wk1"; //CHECK"NumGamesPlayed_wk1" x 5
		nbackByID_table[0][129] = "NumGamesPlayed_wk2";
		nbackByID_table[0][130] = "NumGamesPlayed_wk3";
		nbackByID_table[0][131] = "NumGamesPlayed_wk4";
		nbackByID_table[0][132] = "NumGamesPlayed_wk5";
		nbackByID_table[0][133] = "TimeSpent_wk1"; //CHECK"TimeSpent_wk1" x 5
		nbackByID_table[0][134] = "TimeSpent_wk2";
		nbackByID_table[0][135] = "TimeSpent_wk3";
		nbackByID_table[0][136] = "TimeSpent_wk4";
		nbackByID_table[0][137] = "TimeSpent_wk5";
		nbackByID_table[0][138] = "AvgRT_wk1"; //"CHECKAvgRT_wk1" x 5 
		nbackByID_table[0][139] = "AvgRT_wk2";
		nbackByID_table[0][140] = "AvgRT_wk3";
		nbackByID_table[0][141] = "AvgRT_wk4";
		nbackByID_table[0][142] = "AvgRT_wk5";
		
		nbackByID_table[0][143] = "HitRateAvg_spatial"; //CHECK"HitRateAvg_spatial" "HitRateAvg_letterNumber"  "HitRateAvg_img"
		nbackByID_table[0][144] = "HitRateAvg_letterNumber";
		nbackByID_table[0][145] = "HitRateAvg_img";
		nbackByID_table[0][146] = "ErrRateAvg_spatial"; //CHECK"ErrRateAvg_spatial" "ErrRateAvg_letterNumber"  "ErrRateAvg_img"
		nbackByID_table[0][147] = "ErrRateAvg_letterNumber";
		nbackByID_table[0][148] = "ErrRateAvg_img";
		nbackByID_table[0][149] = "NumGamesPlayed_spatial"; //CHECK"NumGamesPlayed_spatial" "NumGamesPlayed_letterNumber"  "NumGamesPlayed_img"
		nbackByID_table[0][150] = "NumGamesPlayed_letterNumber";
		nbackByID_table[0][151] = "NumGamesPlayed_img";
		nbackByID_table[0][152] = "TimeSpent_spatial"; //CHECK"TimeSpent_spatial" x 3
		nbackByID_table[0][153] = "TimeSpent_letterNumber";
		nbackByID_table[0][154] = "TimeSpent_img";
		nbackByID_table[0][155] = "AvgRT_spatial"; //CHECK"AvgRT_spatial" x 3
		nbackByID_table[0][156] = "AvgRT_letterNumber";
		nbackByID_table[0][157] = "AvgRT_img";
		
		
		//=========================================LAB VARIABLES==============================================================
		//Repeat for labs and each general lab
		//Repeat for home and each general home
		//lab hitRates
		nbackByID_table[0][158] = "Home_HitRateAvg_spatial"; //CHECK"LabX_HitRateAvg_spatial" x 5 
		nbackByID_table[0][159] = "Lab2_HitRateAvg_spatial"; 
		nbackByID_table[0][160] = "Lab3_HitRateAvg_spatial";
		nbackByID_table[0][161] = "Lab4_HitRateAvg_spatial";
		nbackByID_table[0][162] = "Lab5_HitRateAvg_spatial";
		nbackByID_table[0][163] = "Home_HitRateAvg_letterNumber"; //CHECK"LabX_HitRateAvg_letterNumber" x 5
		nbackByID_table[0][164] = "Lab2_HitRateAvg_letterNumber"; 
		nbackByID_table[0][165] = "Lab3_HitRateAvg_letterNumber";
		nbackByID_table[0][166] = "Lab4_HitRateAvg_letterNumber";
		nbackByID_table[0][167] = "Lab5_HitRateAvg_letterNumber";
		nbackByID_table[0][168] = "Home_HitRateAvg_img"; //CHECK"LabX_HitRateAvg_img" x 5
		nbackByID_table[0][169] = "Lab2_HitRateAvg_img"; 
		nbackByID_table[0][170] = "Lab3_HitRateAvg_img";
		nbackByID_table[0][171] = "Lab4_HitRateAvg_img";
		nbackByID_table[0][172] = "Lab5_HitRateAvg_img";
		
		//lab errRates
		nbackByID_table[0][173] = "Lab1_ErrRateAvg_spatial"; //CHECK"LabX_ErrRateAvg_spatial" x 5
		nbackByID_table[0][174] = "Lab2_ErrRateAvg_spatial";
		nbackByID_table[0][175] = "Lab3_ErrRateAvg_spatial";
		nbackByID_table[0][176] = "Lab4_ErrRateAvg_spatial";
		nbackByID_table[0][177] = "Lab5_ErrRateAvg_spatial";
		nbackByID_table[0][178] = "Lab1_ErrRateAvg_letterNumber"; //CHECK"LabX_ErrRateAvg_letterNumber" x 5
		nbackByID_table[0][179] = "Lab2_ErrRateAvg_letterNumber"; 
		nbackByID_table[0][180] = "Lab3_ErrRateAvg_letterNumber";
		nbackByID_table[0][181] = "Lab4_ErrRateAvg_letterNumber";
		nbackByID_table[0][182] = "Lab5_ErrRateAvg_letterNumber";
		nbackByID_table[0][183] = "Lab1_ErrRateAvg_img"; //CHECK"LabX_ErrRateAvg_img" x 5
		nbackByID_table[0][184] = "Lab2_ErrRateAvg_img";
		nbackByID_table[0][185] = "Lab3_ErrRateAvg_img";
		nbackByID_table[0][186] = "Lab4_ErrRateAvg_img";
		nbackByID_table[0][187] = "Lab5_ErrRateAvg_img";
		
		//lab number of games played
		nbackByID_table[0][188] = "Lab1_NumGamesPlayed_spatial"; //CHECK"LabX_NumGamesPlayed_spatial" x 5
		nbackByID_table[0][189] = "Lab2_NumGamesPlayed_spatial"; 
		nbackByID_table[0][190] = "Lab3_NumGamesPlayed_spatial"; 
		nbackByID_table[0][191] = "Lab4_NumGamesPlayed_spatial"; 
		nbackByID_table[0][192] = "Lab5_NumGamesPlayed_spatial"; 
		nbackByID_table[0][193] = "Lab1_NumGamesPlayed_letterNumber"; //CHECK"LabX_NumGamesPlayed_letterNumber" x 5
		nbackByID_table[0][194] = "Lab2_NumGamesPlayed_letterNumber";
		nbackByID_table[0][195] = "Lab3_NumGamesPlayed_letterNumber";
		nbackByID_table[0][196] = "Lab4_NumGamesPlayed_letterNumber";
		nbackByID_table[0][197] = "Lab5_NumGamesPlayed_letterNumber";
		nbackByID_table[0][198] = "Lab1_NumGamesPlayed_img"; //CHECK"LabX_NumGamesPlayed_img"x 5
		nbackByID_table[0][199] = "Lab2_NumGamesPlayed_img"; 
		nbackByID_table[0][200] = "Lab3_NumGamesPlayed_img";
		nbackByID_table[0][201] = "Lab4_NumGamesPlayed_img";
		nbackByID_table[0][202] = "Lab5_NumGamesPlayed_img";
		
		//lab time spent on each type of game
		nbackByID_table[0][203] = "Lab1_TimeSpent_spatial"; //CHECK"LabX_TimeSpent_spatial" x 5
		nbackByID_table[0][204] = "Lab2_TimeSpent_spatial"; 
		nbackByID_table[0][205] = "Lab3_TimeSpent_spatial"; 
		nbackByID_table[0][206] = "Lab4_TimeSpent_spatial"; 
		nbackByID_table[0][207] = "Lab5_TimeSpent_spatial"; 
		nbackByID_table[0][208] = "Lab1_TimeSpent_letterNumber"; //CHECK"LabX_TimeSpent_letterNumber" x 5
		nbackByID_table[0][209] = "Lab2_TimeSpent_letterNumber";
		nbackByID_table[0][210] = "Lab3_TimeSpent_letterNumber";
		nbackByID_table[0][211] = "Lab4_TimeSpent_letterNumber";
		nbackByID_table[0][212] = "Lab5_TimeSpent_letterNumber";
		nbackByID_table[0][213] = "Lab1_TimeSpent_img"; //CHECK"LabX_TimeSpent_img"x 5
		nbackByID_table[0][214] = "Lab2_TimeSpent_img"; 
		nbackByID_table[0][215] = "Lab3_TimeSpent_img";
		nbackByID_table[0][216] = "Lab4_TimeSpent_img";
		nbackByID_table[0][217] = "Lab5_TimeSpent_img";
		
		//lab average RT
		nbackByID_table[0][218] = "Lab1_AvgRT_spatial_wk1"; //CHECK"LabX_AvgRT_spatial" x 5
		nbackByID_table[0][219] = "Lab2_AvgRT_spatial"; 
		nbackByID_table[0][220] = "Lab3_AvgRT_spatial"; 
		nbackByID_table[0][221] = "Lab4_AvgRT_spatial"; 
		nbackByID_table[0][222] = "Lab5_AvgRT_spatial"; 
		nbackByID_table[0][223] = "Lab1_AvgRT_letterNumber"; //CHECK"LabX_AvgRT_letterNumber" x 5
		nbackByID_table[0][224] = "Lab2_AvgRT_letterNumber";
		nbackByID_table[0][225] = "Lab3_AvgRT_letterNumber";
		nbackByID_table[0][226] = "Lab4_AvgRT_letterNumber";
		nbackByID_table[0][227] = "Lab5_AvgRT_letterNumber";
		nbackByID_table[0][228] = "Lab1_AvgRT_img"; //CHECK"LabX_AvgRT_img"x 5
		nbackByID_table[0][229] = "Lab2_AvgRT_img"; 
		nbackByID_table[0][230] = "Lab3_AvgRT_img";
		nbackByID_table[0][231] = "Lab4_AvgRT_img";
		nbackByID_table[0][232] = "Lab5_AvgRT_img";
		
		//Overall data for the labs
		nbackByID_table[0][218] = "Lab1_HitRateAvg"; 
		nbackByID_table[0][219] = "Lab2_HitRateAvg"; 
		nbackByID_table[0][220] = "Lab3_HitRateAvg"; 
		nbackByID_table[0][221] = "Lab4_HitRateAvg"; 
		nbackByID_table[0][222] = "Lab5_HitRateAvg"; 
		nbackByID_table[0][223] = "Lab1_ErrRateAvg"; 
		nbackByID_table[0][224] = "Lab2_ErrRateAvg";
		nbackByID_table[0][225] = "Lab3_ErrRateAvg";
		nbackByID_table[0][226] = "Lab4_ErrRateAvg";
		nbackByID_table[0][227] = "Lab5_ErrRateAvg";
		nbackByID_table[0][228] = "Lab1_NumGamesPlayed"; 
		nbackByID_table[0][229] = "Lab2_NumGamesPlayed"; 
		nbackByID_table[0][230] = "Lab3_NumGamesPlayed";
		nbackByID_table[0][231] = "Lab4_NumGamesPlayed";
		nbackByID_table[0][232] = "Lab5_NumGamesPlayed";
		nbackByID_table[0][233] = "Lab1_TimeSpent"; 
		nbackByID_table[0][234] = "Lab2_TimeSpent"; 
		nbackByID_table[0][235] = "Lab3_TimeSpent"; 
		nbackByID_table[0][236] = "Lab4_TimeSpent"; 
		nbackByID_table[0][237] = "Lab5_TimeSpent"; 
		nbackByID_table[0][238] = "Lab1_AvgRT"; 
		nbackByID_table[0][239] = "Lab2_AvgRT";
		nbackByID_table[0][240] = "Lab3_AvgRT";
		nbackByID_table[0][241] = "Lab4_AvgRT";
		nbackByID_table[0][242] = "Lab5_AvgRT";
		
		//==================================================HOME VARIABLES==================================================
		
		// Repeat for home and each general home
		// Home hitRates
		nbackByID_table[0][243] = "Home_HitRateAvg_spatial_wk1"; 
		nbackByID_table[0][244] = "Home_HitRateAvg_spatial_wk2";
		nbackByID_table[0][245] = "Home_HitRateAvg_spatial_wk3";
		nbackByID_table[0][246] = "Home_HitRateAvg_spatial_wk4";
		nbackByID_table[0][247] = "Home_HitRateAvg_spatial_wk5";
		nbackByID_table[0][248] = "Home_HitRateAvg_letterNumber_wk1"; 
		nbackByID_table[0][249] = "Home_HitRateAvg_letterNumber_wk2";
		nbackByID_table[0][250] = "Home_HitRateAvg_letterNumber_wk3";
		nbackByID_table[0][251] = "Home_HitRateAvg_letterNumber_wk4";
		nbackByID_table[0][252] = "Home_HitRateAvg_letterNumber_wk5";
		nbackByID_table[0][253] = "Home_HitRateAvg_img_wk1"; 
		nbackByID_table[0][254] = "Home_HitRateAvg_img_wk2";
		nbackByID_table[0][255] = "Home_HitRateAvg_img_wk3";
		nbackByID_table[0][256] = "Home_HitRateAvg_img_wk4";
		nbackByID_table[0][257] = "Home_HitRateAvg_img_wk5";

		// home errRates
		nbackByID_table[0][258] = "Home_ErrRateAvg_spatial_wk1"; 
		nbackByID_table[0][259] = "Home_ErrRateAvg_spatial_wk2";
		nbackByID_table[0][260] = "Home_ErrRateAvg_spatial_wk3";
		nbackByID_table[0][261] = "Home_ErrRateAvg_spatial_wk4";
		nbackByID_table[0][262] = "Home_ErrRateAvg_spatial_wk5";
		nbackByID_table[0][263] = "Home_ErrRateAvg_letterNumber_wk1"; 
		nbackByID_table[0][264] = "Home_ErrRateAvg_letterNumber_wk2";
		nbackByID_table[0][265] = "Home_ErrRateAvg_letterNumber_wk3";
		nbackByID_table[0][266] = "Home_ErrRateAvg_letterNumber_wk4";
		nbackByID_table[0][267] = "Home_ErrRateAvg_letterNumber_wk5";
		nbackByID_table[0][268] = "Home_ErrRateAvg_img_wk1"; 
		nbackByID_table[0][269] = "Home_ErrRateAvg_img_wk2";
		nbackByID_table[0][270] = "Home_ErrRateAvg_img_wk3";
		nbackByID_table[0][271] = "Home_ErrRateAvg_img_wk4";
		nbackByID_table[0][272] = "Home_ErrRateAvg_img_wk5";

		// home number of games played
		nbackByID_table[0][273] = "Home_NumGamesPlayed_spatial_wk1"; 
		nbackByID_table[0][274] = "Home_NumGamesPlayed_spatial_wk1";
		nbackByID_table[0][275] = "Home_NumGamesPlayed_spatial_wk1";
		nbackByID_table[0][276] = "Home_NumGamesPlayed_spatial_wk1";
		nbackByID_table[0][277] = "Home_NumGamesPlayed_spatial_wk1";
		nbackByID_table[0][278] = "Home_NumGamesPlayed_letterNumber_wk1"; // CHECK"LabX_NumGamesPlayed_letterNumber"
		nbackByID_table[0][279] = "Home_NumGamesPlayed_letterNumber_wk2";
		nbackByID_table[0][280] = "Home_NumGamesPlayed_letterNumber_wk3";
		nbackByID_table[0][281] = "Home_NumGamesPlayed_letterNumber_wk4";
		nbackByID_table[0][282] = "Home_NumGamesPlayed_letterNumber_wk5";
		nbackByID_table[0][283] = "Home_NumGamesPlayed_img_wk1"; // CHECK"LabX_NumGamesPlayed_img"x
		nbackByID_table[0][284] = "Home_NumGamesPlayed_img_wk2";
		nbackByID_table[0][285] = "Home_NumGamesPlayed_img_wk3";
		nbackByID_table[0][286] = "Home_NumGamesPlayed_img_wk4";
		nbackByID_table[0][287] = "Home_NumGamesPlayed_img_wk5";

		// home time spent on each type of game
		nbackByID_table[0][288] = "Home_TimeSpent_spatial_wk1"; // CHECK"LabX_TimeSpent_spatial"
		nbackByID_table[0][289] = "Home_TimeSpent_spatial_wk2";
		nbackByID_table[0][290] = "Home_TimeSpent_spatial_wk3";
		nbackByID_table[0][291] = "Home_TimeSpent_spatial_wk4";
		nbackByID_table[0][292] = "Home_TimeSpent_spatial_wk5";
		nbackByID_table[0][293] = "Home_TimeSpent_letterNumber_wk1"; // CHECK"LabX_TimeSpent_letterNumber"
		nbackByID_table[0][294] = "Home_TimeSpent_letterNumber_wk2";
		nbackByID_table[0][295] = "Home_TimeSpent_letterNumber_wk3";
		nbackByID_table[0][296] = "Home_TimeSpent_letterNumber_wk4";
		nbackByID_table[0][297] = "Home_TimeSpent_letterNumber_wk5";
		nbackByID_table[0][298] = "Home_TimeSpent_img_wk1"; // CHECK"LabX_TimeSpent_img"x
		nbackByID_table[0][299] = "Home_TimeSpent_img_wk2";
		nbackByID_table[0][300] = "Home_TimeSpent_img_wk3";
		nbackByID_table[0][301] = "Home_TimeSpent_img_wk4";
		nbackByID_table[0][302] = "Home_TimeSpent_img_wk5";

		// home average RT
		nbackByID_table[0][303] = "Home_AvgRT_spatial_wk1"; 
		nbackByID_table[0][304] = "Home_AvgRT_spatial_wk2";
		nbackByID_table[0][305] = "Home_AvgRT_spatial_wk3";
		nbackByID_table[0][306] = "Home_AvgRT_spatial_wk4";
		nbackByID_table[0][307] = "Home_AvgRT_spatial_wk5";
		nbackByID_table[0][308] = "Home_AvgRT_letterNumber_wk1"; 
		nbackByID_table[0][309] = "Home_AvgRT_letterNumber_wk2";
		nbackByID_table[0][310] = "Home_AvgRT_letterNumber_wk3";
		nbackByID_table[0][311] = "Home_AvgRT_letterNumber_wk4";
		nbackByID_table[0][312] = "Home_AvgRT_letterNumber_wk5";
		nbackByID_table[0][313] = "Home_AvgRT_img_wk1"; 
		nbackByID_table[0][314] = "Home_AvgRT_img_wk2";
		nbackByID_table[0][315] = "Home_AvgRT_img_wk3";
		nbackByID_table[0][316] = "Home_AvgRT_img_wk4";
		nbackByID_table[0][317] = "Home_AvgRT_img_wk5";

		// Overall data for the labs
		nbackByID_table[0][318] = "Home_HitRateAvg_wk1";
		nbackByID_table[0][319] = "Home_HitRateAvg_wk2";
		nbackByID_table[0][320] = "Home_HitRateAvg_wk3";
		nbackByID_table[0][321] = "Home_HitRateAvg_wk4";
		nbackByID_table[0][322] = "Home_HitRateAvg_wk5";
		nbackByID_table[0][323] = "Home_ErrRateAvg_wk1";
		nbackByID_table[0][324] = "Home_ErrRateAvg_wk2";
		nbackByID_table[0][325] = "Home_ErrRateAvg_wk3";
		nbackByID_table[0][326] = "Home_ErrRateAvg_wk4";
		nbackByID_table[0][327] = "Home_ErrRateAvg_wk5";
		nbackByID_table[0][328] = "Home_NumGamesPlayed_wk1";
		nbackByID_table[0][329] = "Home_NumGamesPlayed_wk2";
		nbackByID_table[0][330] = "Home_NumGamesPlayed_wk3";
		nbackByID_table[0][331] = "Home_NumGamesPlayed_wk4";
		nbackByID_table[0][332] = "Home_NumGamesPlayed_wk5";
		nbackByID_table[0][333] = "Home_TimeSpent_wk1";
		nbackByID_table[0][334] = "Home_TimeSpent_wk2";
		nbackByID_table[0][335] = "Home_TimeSpent_wk3";
		nbackByID_table[0][336] = "Home_TimeSpent_wk4";
		nbackByID_table[0][337] = "Home_TimeSpent_wk5";
		nbackByID_table[0][338] = "Home_AvgRT_wk1";
		nbackByID_table[0][339] = "Home_AvgRT_wk2";
		nbackByID_table[0][340] = "Home_AvgRT_wk3";
		nbackByID_table[0][341] = "Home_AvgRT_wk4";
		nbackByID_table[0][342] = "Home_AvgRT_wk5";
		
		//NEED THE FOLLOWING DATA AT THE END
		nbackByID_table[0][343] = "TotalSessions_wk1"; //number of logins divided by 2
		nbackByID_table[0][344] = "TotalSessions_wk2";
		nbackByID_table[0][345] = "TotalSessions_wk3";
		nbackByID_table[0][346] = "TotalSessions_wk4";
		nbackByID_table[0][347] = "TotalSessions_wk5";
		
		return nbackByID_table;
		
	//=====================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
	//======================================================================================================================//
		
	
	}
	private  String[][] nbackByDateSheetInit(String[][] nbackByDate_table) {
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
	private  String[][] sudokuByIDSheetInit(String[][] sudokuByID_table) {
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
//	private  String[][] sudokuByIDSheetInit(String[][] sessionDate_table, String[][] sudokuReport_table, 
//			int sudokuReport_col_num, int sudokuReport_row_num, String[][] sudokuByID, Workbook workbook_w) { TEMPLATE
//	
	private  String[][] sudokuByDateSheetInit(String[][] sudokuByDate_table) {
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

   
}//END


































