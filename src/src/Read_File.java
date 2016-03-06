import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.swing.JFileChooser;
import java.io.*;

//Maximum number of line set as 50,000 

public class Read_File {

	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		
		String[][] table = new String[50000][50];
		int row_num = 0;
		int col_num = 0;
		
		
		
		JFileChooser fileChooser = new JFileChooser(); //Allow the user to choose the file
		int returnValue = fileChooser.showOpenDialog(null); 
		
		//Just to check if the user has clicked on the Open button
		if(returnValue == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				Workbook workbook = new HSSFWorkbook(new FileInputStream(fileChooser.getSelectedFile()));
				Sheet sheet = workbook.getSheetAt(0);
				for(Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();)
				{
					Row row = rit.next();
					for(Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
					{
						Cell cell = cit.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						System.out.print(cell.getStringCellValue()+"\t");
						table[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
					}
					System.out.println();
					col_num = row.getPhysicalNumberOfCells();
				}
				row_num = sheet.getPhysicalNumberOfRows();
				
				
			} catch (FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.println("No file has been chosen.");
		}	
		
		System.out.println("Number of rows: " + row_num + "\nNumber of columns: " + col_num);
		System.out.println();
		
		
		
		
		
		
		
		
		
		
		
		/*for (int row = 0 ; row < row_num ; row++)
		{
			for (int col = 0 ; col < col_num ; col++)
			{
				System.out.print(table[row][col] + "\t");
			}
			System.out.println();
		}
		*/
	}

}
