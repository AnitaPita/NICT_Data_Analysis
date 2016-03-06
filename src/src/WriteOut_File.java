import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class WriteOut_File {

	public static void main(String[] args) throws Exception
	{
		File file = new File("C:/Users/Murillo/workspace/Sheida_Java/files/newFile.txt");
		
		if(file.exists())
		{
			System.out.println("There is already a file with that name.");
		}
		else
		{
			try
			{
				file.createNewFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				FileWriter file_W = new FileWriter(file);
				BufferedWriter buffW = new BufferedWriter(file_W);
				buffW.write("This is just a test.");
				buffW.close();
				System.out.println("FileWritten.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		
		//Working with the excel file
		
				Workbook workbook = new HSSFWorkbook();
				Sheet sheet1 = workbook.createSheet("Sheet1");
								
				Cell cell = sheet1.createRow(0).createCell(3);
				cell.setCellValue("Testing.");
				
				
				System.out.println(cell.getRichStringCellValue().toString());
				
				try
				{
					FileOutputStream output = new FileOutputStream("C:/Users/Murillo/workspace/Sheida_Java/files/Test.xlsx");
					workbook.write(output);
					output.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
	}

}
