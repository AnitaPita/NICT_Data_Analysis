import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NICT_TDCSAnalysis_Final {

	final int NB_LINES = 300;
	private String[][] sessionInfoData_table = new String[NB_LINES][100];
	private List<ArrayList<String>> sudokuByIdResults = new ArrayList<ArrayList<String>>();
	private List<ArrayList<String>> sudokuBySessionResults = new ArrayList<ArrayList<String>>();
	private List<ArrayList<String>> nbackByIdResults = new ArrayList<ArrayList<String>>();
	private List<ArrayList<String>> nbackBySessionResults = new ArrayList<ArrayList<String>>();
	private String[][] nbackBySession_table = new String[NB_LINES][400];
	private File[] reportFiles = null;
	private File[] responseFiles = null;


	private Map<String,User> users = new HashMap<String,User>();
	private BufferedWriter bw = null;
//	private BufferedWriter bw2 = null;

	private class User {
		String id; //from xml file name or "name" in avgsreport
		String grp; //from avgsreport of xml
		List<Session> sessions;
		List<SudokuGame> sudokuGames;
		List<NbackGame> nbackGames;
		Date[] weeksLab;
		Date prelab;
//		int prelab_timeOfDay;
		Date postlab;
//		int postlab_timeOfDay;
//		String examiner;

		@Override
		public String toString()
		{
			// TODO Auto-generated method stub
			Date[] w1 = getWeekslab();
			String w1s = "";
			for (int i = 0; i < w1.length; i++)
			{
				w1s += w1[i]+",";
			}
			String res = getId() +" "+getPrelab() +" "+w1s+" "+getPostlab();
			return res;
		}
		public String getId()
		{
			return id;
		}
		public void setId(String id)
		{
			this.id = id;
		}
		public String getGrp()
		{
			return grp;
		}
		public void setGrp(String grp)
		{
			this.grp = grp;
		}
		public List<Session> getSessions()
		{
			return sessions;
		}
		public void setSessions(List<Session> sessions)
		{
			this.sessions = sessions;
		}
		public List<SudokuGame> getSudokuGames()
		{
			return sudokuGames;
		}
		public void setSudokuGames(List<SudokuGame> games)
		{
			this.sudokuGames = games;
		}
		public List<NbackGame> getNbackGames()
		{
			return nbackGames;
		}
		public void setNbackGames(List<NbackGame> nbackGames)
		{
			this.nbackGames = nbackGames;
		}
		public Date[] getWeekslab()
		{
			return weeksLab;
		}
		public void setWeekslab(Date[] weekslab)
		{
			this.weeksLab = weekslab;
		}
		public Date getPrelab()
		{
			return prelab;
		}
		public void setPrelab(Date prelab)
		{
			this.prelab = prelab;
		}
		public Date getPostlab()
		{
			return postlab;
		}
		public void setPostlab(Date postlab)
		{
			this.postlab = postlab;
		}
		public Date getFirstWeek()
		{
			return getWeekslab()[0];
		}
		public boolean isLabDay(Date date)
		{
			if(date == null)
			{
				return false;
			}
			Date[] labDays = getWeekslab();
			for (int i = 0; i < labDays.length; i++)
			{
				if(date.equals(labDays[i]))
				{
					return true;
				}
			}
			return false;
		}

	}

	private class Session {
		Date date;
		Date login1;
		Date login2;
		Date logout1;
		Date logout2;
		int timeOfDay;
		Integer weekNumber;
		boolean inLab;

		@Override
		public String toString()
		{
			return("date="+date+" Time="+timeOfDay+" nbWeek="+weekNumber+" inLab="+inLab);
		}
		public boolean isInLab()
		{
			return inLab;
		}
		public void setInLab(boolean inLab)
		{
			this.inLab = inLab;
		}
		public Integer getWeekNumber()
		{
			return weekNumber;
		}
		public void setWeekNumber(Date firstWeek)
		{
			int daysDiff = getDaysDiff(firstWeek, date);
			if (daysDiff < 0)
			{
				daysDiff = 0;
			}
			weekNumber = daysDiff / 7;
			if (weekNumber > 4)
			{
				weekNumber = 4;
			}

		}
		public Date getDate()
		{
			return date;
		}
		public void setDate(Date date)
		{
			this.date = date;
		}
		public void setLogin1(Date login1)
		{
			this.login1 = login1;
		}
		public void setLogin2(Date login2)
		{
			this.login2 = login2;
		}
		public void setLogout1(Date logout1)
		{
			this.logout1 = logout1;
		}

		public void setLogout2(Date logout2)
		{
			this.logout2 = logout2;
		}
	}

	private class Game {

		String gameID;
		String typeOfGame; //Either sudoku or nback
		Integer duration; //seconds
		Date dateFinished;
		Date timeFinished; //xml has SAME date and time info in both 2 spots for each game, but date finished is displayed like a date, and time like a time
		Integer weekNumber;
		boolean labDay = false;
		@Override
		public String toString()
		{

			return (gameID+" duration="+duration+" dateFinished="+dateFinished+
					" weekNumber="+weekNumber);
		}

		public boolean isLabDay()
		{
			return labDay;
		}

		public void setLabDay(boolean labDay)
		{
			this.labDay = labDay;
		}

		public Integer getWeekNumber()
		{
			return weekNumber;
		}

		public void setWeekNumber(Date firstWeek)
		{
			if (dateFinished == null)
			{
				weekNumber = null;
			}
			else
			{
				int daysDiff = getDaysDiff(firstWeek, dateFinished);
				if(daysDiff < 0)
				{
					daysDiff = 0;
				}
				weekNumber = daysDiff/7;
				if(weekNumber > 4)
				{
					weekNumber = null;
				}
			}
		}

		public boolean isFinished()
		{
			return dateFinished != null && duration != -1;
		}
		public String getGameID()
		{
			return gameID;
		}
		public void setGameID(String gameID)
		{
			this.gameID = gameID;
		}
		public String getTypeOfGame()
		{
			return typeOfGame;
		}
		public void setTypeOfGame(String typeOfGame)
		{
			this.typeOfGame = typeOfGame;
		}
		public int getDuration()
		{
			return duration;
		}
		public void setDuration(int duration)
		{
			this.duration = duration;
		}
		public Date getDateFinished()
		{
			return dateFinished;
		}
		public void setDateFinished(Date dateFinished)
		{
			this.dateFinished = dateFinished;
		}
		public Date getTimeFinished()
		{
			return timeFinished;
		}
		public void setTimeFinished(Date timeFinished)
		{
			this.timeFinished = timeFinished;
		}
		public void setDuration(Integer duration)
		{
			this.duration = duration;
		}
	}
	private class Response {
		boolean correct;
		Integer time; //if time is 0 and correct is false, it's an omission error
		public boolean isCorrect()
		{
			return correct;
		}
		@Override
		public String toString()
		{
			String corr = correct ? "":"in";
			return corr+"correct="+time;
		}
		public void setCorrect(boolean correct)
		{
			this.correct = correct;
		}
		public Integer getTime()
		{
			return time;
		}
		public void setTime(Integer time)
		{
			this.time = time;
		}
	}


	private class GameRT { //nback reponses text file
		String gameID;
		List<Response> responses;
		Double avgResponseTime;
		int numCorrect;
		int numOmission;
		int numIncorrect;
		public GameRT(String gameId)
		{
			super();
			this.gameID = gameId;
		}
		@Override
		public String toString()
		{
			return ("ID="+gameID+" avg="+avgResponseTime+" responses="+responses);
		}
		public String getGameID()
		{
			return gameID;
		}
		public int getNumCorrect() {
			return numCorrect;
		}
		public void setNumCorrect(int numCorrect) {
			this.numCorrect = numCorrect;
		}
		public int getNumOmission() {
			return numOmission;
		}
		public void setNumOmission(int numOmission) {
			this.numOmission = numOmission;
		}
		public int getNumIncorrect() {
			return numIncorrect;
		}
		public void setNumIncorrect(int numIncorrect) {
			this.numIncorrect = numIncorrect;
		}
		public void setGameID(String gameID)
		{
			this.gameID = gameID;
		}
		public List<Response> getResponses()
		{
			return responses;
		}
		public void setResponses(List<Response> responses)
		{
			this.responses = responses;
		}
		public Double getAvgResponseTime()
		{
			return avgResponseTime;
		}
		public void setAvgResponseTime(Double avgResponseTime)
		{
			this.avgResponseTime = avgResponseTime;
		}
	}

	private class SudokuGame extends Game {
		int numWrongSub;
		String difficulty;
		public int getNumWrongSub()
		{
			return numWrongSub;
		}
		public void setNumWrongSub(int numWrongSub)
		{
			this.numWrongSub = numWrongSub;
		}
		public String getDifficulty()
		{
			return difficulty;
		}
		public void setDifficulty(String difficulty)
		{
			this.difficulty = difficulty;
		}
	}

	private class NbackGame extends Game {
		int numCorrect;
		int numWrong;
		int numOmission;
		int numIncorrect;
		double hr;
		double er;
		double eri;
		double ero;
		public double getHr() {
			return hr;
		}
		public void setHr(String type) {
			if(type.equals("Images from folders"))
			{
				hr = ((double) numCorrect)/720.0;
			}
			else
			{
				hr = ((double) numCorrect)/120.0;
			}
		}
		public double getEr() {
			return er;
		}
		public void setEr(String type) {
			if(type.equals("Images from folders"))
			{
				er = ((double) numWrong)/720.0;
			}
			else
			{
				er = ((double) numWrong)/120.0;
			}
		}
		public double getEri() {
			return eri;
		}
		public void setEri(String type) {
			if(type.equals("Images from folders"))
			{
				eri = ((double) numIncorrect)/720.0;
			}
			else
			{
				eri = ((double) numIncorrect)/120.0;
			}
		}
		public double getEro() {
			return ero;
		}
		public void setEro(String type) {
			if(type.equals("Images from folders"))
			{
				ero = ((double) numOmission)/720.0;
			}
			else
			{
				ero = ((double) numOmission)/120.0;
			}
		}
		String type;
		GameRT responseFile;
		public GameRT getResponseFile() {
			return responseFile;
		}
		public void setResponseFile(GameRT responseFile) {
			this.responseFile = responseFile;
		}
		double avgResponseTime;
		public int getNumCorrect()
		{
			return numCorrect;
		}
		public void setNumCorrect(int numCorrect)
		{
			this.numCorrect = numCorrect;
		}
		public int getNumWrong()
		{
			return numWrong;
		}
		public void setNumWrong(int numWrong)
		{
			this.numWrong = numWrong;
		}
		public int getNumOmission()
		{
			return numOmission;
		}
		public void setNumOmission(int numOmission)
		{
			this.numOmission = numOmission;
		}
		public int getNumIncorrect()
		{
			return numIncorrect;
		}
		public void setNumIncorrect(int numIncorrect)
		{
			this.numIncorrect = numIncorrect;
		}
		public String getType()
		{
			return type;
		}
		public void setType(String type)
		{
			this.type = type;
		}
		public double getAvgResponseTime()
		{
			return avgResponseTime;
		}
		public void setAvgResponseTime(double avgResponseTime)
		{
			this.avgResponseTime = avgResponseTime;
		}
	}

	private class ReportLine
	{
		ArrayList<String> reportLine = new ArrayList<String>();
		ArrayList<String> reportTitle = new ArrayList<String>();
		@Override
		public String toString()
		{
			return reportLine.toString();
		}
		public ArrayList<String> getReportTitle()
		{
			return reportTitle;
		}
		private void set5(String titleOrig, Object[] values) throws IOException
		{
			String title;
			for (int i = 0; i < values.length; i++)
			{
				String numStr = String.valueOf(i + 1);
				title = titleOrig.replace("%", numStr);

				if (!reportTitle.contains(title))
				{
					reportTitle.add(title);
				}
				reportLine.add(values[i].toString());
				bw.write(title+"="+values[i]);
				bw.newLine();
			}
		}
		private void set(String title, Object value) throws IOException
		{
			if( ! reportTitle.contains(title))
			{
				reportTitle.add(title);
			}
			reportLine.add(value.toString());
			bw.write(title+"="+value);
			bw.newLine();
		}
		public ArrayList<String> getLine()
		{
			return reportLine;
		}
	}

	public static void main(String[] args) throws Exception
	{
		new NICT_TDCSAnalysis_Final().run();
	}

	private void run() throws ParserConfigurationException, SAXException,
			IOException, ParseException
	{

		readData();

		// Initialize table with titles
//		nbackBySession_table = nbackBySessionInit(nbackBySession_table);
		populateUsers(sessionInfoData_table);
		// Fill in data
		if (reportFiles == null)
		{
			System.out.println("The spreadsheet could not be generated");
			return;
		}


		for (int j = 1; j < sessionInfoData_table.length; j++)
		{

			String element = sessionInfoData_table[j][0];
			if(element == null)
			{
				System.out.println(j+" session files\n");
				break;
			}
			System.out.println("-"+element);
		}


		for (int i = 0; i < reportFiles.length; i++)// For each user's file
		{
			File file = reportFiles[i];
			// System.out.println(file.getName());
			String[][][] raw_data = parseFile(file);

			// First, get the ID of the user that this file pertains to. The
			// file name is of the format "Report_ID_TDCS-.xls"
			String id_string = file.getName();
			boolean found = false;
			String id = "";
			for (int j = 1; j < sessionInfoData_table.length; j++)
			{
				id = sessionInfoData_table[j][0];
				if (id == null)
				{
					break;
				}
				if (id_string.contains(id))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				System.out.println("not found " + id_string);
				continue;
			}

			User user = users.get(id);
			String[][] avgsReport = raw_data[0];
			String grp = avgsReport[2][3];
			user.setGrp(grp);

			List<Session> sessions = createSessions(user,raw_data);
			user.setSessions(sessions);
			populateSudokuGames(file, responseFiles, nbackBySession_table, i + 1,
					sessionInfoData_table, id, raw_data);
			populateNbackGames(file, responseFiles, nbackBySession_table, i + 1,
					sessionInfoData_table, id, raw_data);

			sudokuByIdSheet(sudokuByIdResults, id, i);
			sudokuBySessionSheet(sudokuBySessionResults, id, i);
			nbackByIdSheet(nbackByIdResults, id, i);
			nbackBySessionSheet(nbackBySessionResults, id, i);
		}

		write(reportFiles, sudokuByIdResults, "FilteredSudokuByIdData_", "sudokuById");

		write(reportFiles, sudokuBySessionResults, "FilteredSudokuBySesionData_", "sudokuBySession");

		write(reportFiles, nbackByIdResults, "FilteredNbackByIdData_","nbackById");

		write(reportFiles, nbackBySessionResults, "FilteredNbackBySessionData_", "nbackBySession");

	}

	private boolean nbackBySessionNoTitle = true;
	private void nbackBySessionSheet(List<ArrayList<String>> nbackBySessionResults, String userId, int index) throws IOException {
		User user = users.get(userId);
		bw.write("NBACK BY SESSION");
		bw.newLine();
		bw.newLine();

		List<Session> sessions = user.getSessions();

		for (Session session : sessions)
		{
			ReportLine reportLine = new ReportLine();
			reportLine.set("ID", userId);
			Date date = session.getDate();
			reportLine.set("Date", date);
			reportLine.set("LabSession?", session.isInLab() ? "Yes" : "No");

			Integer numComplete = 0;
			Integer numCompleteSpatial = 0;
			Integer numCompleteLetNum = 0;
			Integer numCompleteImg = 0;
			Integer timeSpent = 0;
			Integer timeSpentSpatial = 0;
			Integer timeSpentLetNum = 0;
			Integer timeSpentImg = 0;
			Double avgHR = 0.0;
			Double avgHRSpatial = 0.0;
			Double avgHRLetNum = 0.0;
			Double avgHRImg = 0.0;
			Double avgER = 0.0;
			Double avgERSpatial = 0.0;
			Double avgERLetNum = 0.0;
			Double avgERImg = 0.0;
			Double avgERO = 0.0;
			Double avgEROSpatial = 0.0;
			Double avgEROLetNum = 0.0;
			Double avgEROImg = 0.0;
			Double avgERI = 0.0;
			Double avgERISpatial = 0.0;
			Double avgERILetNum = 0.0;
			Double avgERIImg = 0.0;
			Double avgRT = 0.0;
			Double avgRTSpatial = 0.0;
			Double avgRTLetNum = 0.0;
			Double avgRTImg = 0.0;

			Double totalHR = 0.0;
			Double totalHRSpatial = 0.0;
			Double totalHRLetNum = 0.0;
			Double totalHRImg = 0.0;
			Double totalER = 0.0;
			Double totalERSpatial = 0.0;
			Double totalERLetNum = 0.0;
			Double totalERImg = 0.0;
			Double totalERO = 0.0;
			Double totalEROSpatial = 0.0;
			Double totalEROLetNum = 0.0;
			Double totalEROImg = 0.0;
			Double totalERI = 0.0;
			Double totalERISpatial = 0.0;
			Double totalERILetNum = 0.0;
			Double totalERIImg = 0.0;
			Double totalRT = 0.0;
			Double totalRTSpatial = 0.0;
			Double totalRTLetNum = 0.0;
			Double totalRTImg = 0.0;

			Double[] hitRate = new Double[5];
			Double[] errRateOverall = new Double[5];
			Double[] errRateOmission = new Double[5];
			Double[] errRateIncorrect = new Double[5];
			Double[] avgResponseTime = new Double[5];
			Integer[] timeSpentGame = new Integer[5];

			String[] typeGame = new String[5];

			// initialization
			for (int i = 0; i < 5; i++)
			{
				hitRate[i] = 0.0;
				errRateOverall[i] = 0.0;
				errRateOmission[i] = 0.0;
				errRateIncorrect[i] = 0.0;
				avgResponseTime[i] = 0.0;
				timeSpentGame[i] = 0;
				typeGame[i] = "";
			}
			int index5 = 0;
			for (Game game1 : user.getNbackGames())
			{
				NbackGame game = (NbackGame) game1;
				Date gameDate = game.getDateFinished();
				if( gameDate == null || ! gameDate.equals(date))
				{
					continue;
				}
				String type = game.getType();
				if (game.isFinished())
				{
					Integer ind1 = game.getWeekNumber();
					if (ind1 == null)
					{
						System.out.println(game.getGameID() + " past 5 weeks");
						continue;
					}

					numComplete++;
					timeSpent += game.getDuration();
					totalHR += game.getHr();
					totalER += game.getEr();
					totalERI += game.getEri();
					totalERO += game.getEro();
					totalRT += game.getAvgResponseTime();

					if (type.equals("Spatial Grid with Brain"))
					{
						numCompleteSpatial++;
						timeSpentSpatial += game.getDuration();
						totalHRSpatial += game.getHr();
						totalERSpatial += game.getEr();
						totalERISpatial += game.getEri();
						totalEROSpatial += game.getEro();
						totalRTSpatial += game.getAvgResponseTime();

					} else if (type.equals("Letters and Numbers"))
					{
						numCompleteLetNum++;
						timeSpentLetNum += game.getDuration();
						totalHRLetNum += game.getHr();
						totalERLetNum += game.getEr();
						totalERILetNum += game.getEri();
						totalEROLetNum += game.getEro();
						totalRTLetNum += game.getAvgResponseTime();

					} else if (type.equals("Images from folders"))
					{
						numCompleteImg++;
						timeSpentImg += game.getDuration();
						totalHRImg += game.getHr();
						totalERImg += game.getEr();
						totalERIImg += game.getEri();
						totalEROImg += game.getEro();
						totalRTImg += game.getAvgResponseTime();

					}

					if( index5 < 5)
					{
						hitRate[index5] = game.getHr();
						errRateOverall[index5] = game.getEr();
						errRateOmission[index5] = game.getEro();
						errRateIncorrect[index5] = game.getEri();
						avgResponseTime[index5] = game.getAvgResponseTime();
						timeSpentGame[index5] = timeSpent;
						typeGame[index5] = type;
						index5++;
					}
				}

			}

			reportLine.set("NumComplete", numComplete);
			reportLine.set("NumCompleteSpatial", numCompleteSpatial);
			reportLine.set("NumCompleteLetNum", numCompleteLetNum);
			reportLine.set("NumCompleteImg", numCompleteImg);

			reportLine.set("TotalTimeSpent (s)", timeSpent);
			reportLine.set("TotalTimeSpentSpatial (s)", timeSpentSpatial);
			reportLine.set("TotalTimeSpentLetNum (s)", timeSpentLetNum);
			reportLine.set("TotalTimeSpentImg (s)", timeSpentImg);




			Integer avgTimeSpent = 0;
			if(numComplete != 0)
			{
				avgTimeSpent = timeSpent/numComplete;
				avgHR = totalHR/((double)numComplete);
				avgER = totalER/((double)numComplete);
				avgERI = totalERI/((double)numComplete);
				avgERO = totalERO/((double)numComplete);
				avgRT = totalRT/((double)numComplete);
			}
			reportLine.set("AverageTimeSpent (s)", avgTimeSpent);
			reportLine.set("AverageHR", avgHR);
			reportLine.set("AverageErrRateOverall", avgER);
			reportLine.set("AverageErrRateOmission", avgERO);
			reportLine.set("AverageErrRateIncorrect", avgERI);
			reportLine.set("AverageResponseTime (s)", avgRT);


			Integer avgTimeSpentSpatial = 0;
			if(numCompleteSpatial != 0)
			{
				avgTimeSpentSpatial = timeSpentSpatial/numCompleteSpatial;
				avgHRSpatial = totalHRSpatial/((double)numCompleteSpatial);
				avgERSpatial = totalERSpatial/((double)numCompleteSpatial);
				avgERISpatial = totalERISpatial/((double)numCompleteSpatial);
				avgEROSpatial = totalEROSpatial/((double)numCompleteSpatial);
				avgRTSpatial = totalRTSpatial/((double)numCompleteSpatial);
			}
			reportLine.set("AverageTimeSpentSpatial (s)", avgTimeSpentSpatial);
			reportLine.set("AverageHRSpatial", avgHRSpatial);
			reportLine.set("AverageErrRateOverallSpatial", avgERSpatial);
			reportLine.set("AverageErrRateOmissionSpatial", avgEROSpatial);
			reportLine.set("AverageErrRateIncorrectSpatial", avgERISpatial);
			reportLine.set("AverageResponseTimeSpatial (s)", avgRTSpatial);

			Integer avgTimeSpentLetNum = 0;
			if(numCompleteLetNum != 0)
			{
				avgTimeSpentLetNum = timeSpentLetNum/numCompleteLetNum;
				avgHRSpatial = totalHRLetNum/((double)numCompleteLetNum);
				avgERLetNum = totalERLetNum/((double)numCompleteLetNum);
				avgERILetNum = totalERILetNum/((double)numCompleteLetNum);
				avgEROLetNum = totalEROLetNum/((double)numCompleteLetNum);
				avgRTLetNum = totalRTLetNum/((double)numCompleteLetNum);
			}
			reportLine.set("AverageTimeSpentLetNum (s)", avgTimeSpentLetNum);
			reportLine.set("AverageHRLetNum", avgHRLetNum);
			reportLine.set("AverageErrRateOverallLetNum", avgERLetNum);
			reportLine.set("AverageErrRateOmissionLetNum", avgEROLetNum);
			reportLine.set("AverageErrRateIncorrectLetNum", avgERILetNum);
			reportLine.set("AverageResponseTimeLetNum (s)", avgRTLetNum);

			Integer avgTimeSpentImg = 0;
			if(numCompleteImg != 0)
			{
				avgTimeSpentImg = timeSpentImg/numCompleteImg;
				avgHRImg = totalHRImg/((double)numCompleteImg);
				avgERImg = totalERImg/((double)numCompleteImg);
				avgERIImg = totalERIImg/((double)numCompleteImg);
				avgEROImg = totalEROImg/((double)numCompleteImg);
				avgRTImg = totalRTImg/((double)numCompleteImg);
			}
			reportLine.set("AverageTimeSpentImg (s)", avgTimeSpentImg);
			reportLine.set("AverageHRImg", avgHRImg);
			reportLine.set("AverageErrRateOverallImg", avgERImg);
			reportLine.set("AverageErrRateOmissionImg", avgEROImg);
			reportLine.set("AverageErrRateIncorrectImg", avgERIImg);
			reportLine.set("AverageResponseTimeImg (s)", avgRTImg);


//			reportLine.set("AverageHR", avgHR);
//			reportLine.set("AverageHRSpatial", avgHRSpatial);
//			reportLine.set("AverageHRLetNum", avgHRLetNum);
//			reportLine.set("AverageHRImg", avgHRImg);
//

			reportLine.set5("HitRate%", hitRate);
			reportLine.set5("ErrRate%", errRateOverall);
			reportLine.set5("ErrRateOmission%", errRateOmission);
			reportLine.set5("ErrRateIncorrect%", errRateIncorrect);
			reportLine.set5("AverageRT% (s)", avgResponseTime);

			reportLine.set5("TimeSpent% (s)", timeSpentGame);
			reportLine.set5("TypeOfGame%", typeGame);



			if (nbackBySessionNoTitle)
			{
				nbackBySessionResults.add(reportLine.getReportTitle());
				nbackBySessionNoTitle = false;
			}

			nbackBySessionResults.add(reportLine.getLine());
		}


	}


	private boolean nbackByIdNoTitle = true;
	private void nbackByIdSheet(List<ArrayList<String>> nbackByIdResults2, String userId, int index) throws IOException {
		User user = users.get(userId);
		bw.write("NBACK BY ID");
		bw.newLine();
		bw.newLine();

		ReportLine reportLine = new ReportLine();

		// Object games = id;

//		String[] line = new String[400];
		// "ID", "Group", "Round", "Age", "First_Lab_Day",
		reportLine.set("ID", userId);
		reportLine.set("Group", user.getGrp());
		reportLine.set("Round", "");
		reportLine.set("Age", "");
		Date firstWeek = user.getFirstWeek();
		reportLine.set("First_Lab_Day", firstWeek);

		List<Session> sessions = user.getSessions();
		System.out.println("session="+sessions.size());
		if( sessions.size() == 0)
		{
			return;
		}
		Session lastSession = sessions.get(sessions.size() - 1);
		Date lastLogin = lastSession.getDate();
		reportLine.set("Last_Login", lastLogin);
		Date preTest = user.getPrelab();
		Date postTest = user.getPostlab();
		reportLine.set("Pre-Test_Day", preTest);
		System.out.println(postTest);
		if(postTest == null)
		{
			postTest = new Date();
		}
		reportLine.set("Post-Test_Day", postTest);

		Integer elapsedDays = getDaysDiff(sessions.get(0).getDate(),
				lastSession.getDate());
		reportLine.set("daysElapsed", elapsedDays);

		elapsedDays = getDaysDiff(lastLogin, postTest);
		reportLine.set("LastDayPlayed_to_PostTestDay", elapsedDays);

		elapsedDays = getDaysDiff(preTest, postTest);
		reportLine.set("Pre-Test_to_Post-Test", elapsedDays);

//		Date firstLabDay = user.getWeekslab()[0];
		elapsedDays = getDaysDiff(preTest, firstWeek);
		reportLine.set("Pre-Test_to_FirstLabDay", elapsedDays);

		Double[] avgHRLab = new Double[5];
		Double[] avgHRSpatialLab = new Double[5];
		Double[] avgHRLetNumLab = new Double[5];
		Double[] avgHRImgLab = new Double[5];
		Double[] totalHRLab = new Double[5];
		Double[] totalHRSpatialLab = new Double[5];
		Double[] totalHRLetNumLab = new Double[5];
		Double[] totalHRImgLab = new Double[5];

		Double[] avgERLab = new Double[5];
		Double[] avgERSpatialLab = new Double[5];
		Double[] avgERLetNumLab = new Double[5];
		Double[] avgERImgLab = new Double[5];
		Double[] totalERLab = new Double[5];
		Double[] totalERSpatialLab = new Double[5];
		Double[] totalERLetNumLab = new Double[5];
		Double[] totalERImgLab = new Double[5];

		Double[] avgEROLab = new Double[5];
		Double[] avgEROSpatialLab = new Double[5];
		Double[] avgEROLetNumLab = new Double[5];
		Double[] avgEROImgLab = new Double[5];
		Double[] totalEROLab = new Double[5];
		Double[] totalEROSpatialLab = new Double[5];
		Double[] totalEROLetNumLab = new Double[5];
		Double[] totalEROImgLab = new Double[5];


		Double[] avgERILab = new Double[5];
		Double[] avgERISpatialLab = new Double[5];
		Double[] avgERILetNumLab = new Double[5];
		Double[] avgERIImgLab = new Double[5];
		Double[] totalERILab = new Double[5];
		Double[] totalERISpatialLab = new Double[5];
		Double[] totalERILetNumLab = new Double[5];
		Double[] totalERIImgLab = new Double[5];

		Double[] avgRTLab = new Double[5];
		Double[] avgRTSpatialLab = new Double[5];
		Double[] avgRTLetNumLab = new Double[5];
		Double[] avgRTImgLab = new Double[5];
		Double[] totalRTLab = new Double[5];
		Double[] totalRTSpatialLab = new Double[5];
		Double[] totalRTLetNumLab = new Double[5];
		Double[] totalRTImgLab = new Double[5];

		Integer[] timeSpentLab = new Integer[5];
		Integer[] timeSpentSpatialLab = new Integer[5];
		Integer[] timeSpentLetNumLab = new Integer[5];
		Integer[] timeSpentImgLab = new Integer[5];
		Integer[] avgTimeSpentLab = new Integer[5];
		Integer[] avgTimeSpentSpatialLab = new Integer[5];
		Integer[] avgTimeSpentLetNumLab = new Integer[5];
		Integer[] avgTimeSpentImgLab = new Integer[5];

		Integer[] numCompleteLab = new Integer[5];
		Integer[] numCompleteSpatialLab = new Integer[5];
		Integer[] numCompleteLetNumLab = new Integer[5];
		Integer[] numCompleteImgLab = new Integer[5];


		Integer[] numSessionsLab = new Integer[5];


		Double[] avgHRHome = new Double[5];
		Double[] avgHRSpatialHome = new Double[5];
		Double[] avgHRLetNumHome = new Double[5];
		Double[] avgHRImgHome = new Double[5];
		Double[] totalHRHome = new Double[5];
		Double[] totalHRSpatialHome = new Double[5];
		Double[] totalHRLetNumHome = new Double[5];
		Double[] totalHRImgHome = new Double[5];

		Double[] avgERHome = new Double[5];
		Double[] avgERSpatialHome = new Double[5];
		Double[] avgERLetNumHome = new Double[5];
		Double[] avgERImgHome = new Double[5];
		Double[] totalERHome = new Double[5];
		Double[] totalERSpatialHome = new Double[5];
		Double[] totalERLetNumHome = new Double[5];
		Double[] totalERImgHome = new Double[5];

		Double[] avgEROHome = new Double[5];
		Double[] avgEROSpatialHome = new Double[5];
		Double[] avgEROLetNumHome = new Double[5];
		Double[] avgEROImgHome = new Double[5];
		Double[] totalEROHome = new Double[5];
		Double[] totalEROSpatialHome = new Double[5];
		Double[] totalEROLetNumHome = new Double[5];
		Double[] totalEROImgHome = new Double[5];

		Double[] avgERIHome = new Double[5];
		Double[] avgERISpatialHome = new Double[5];
		Double[] avgERILetNumHome = new Double[5];
		Double[] avgERIImgHome = new Double[5];
		Double[] totalERIHome = new Double[5];
		Double[] totalERISpatialHome = new Double[5];
		Double[] totalERILetNumHome = new Double[5];
		Double[] totalERIImgHome = new Double[5];

		Double[] avgRTHome = new Double[5];
		Double[] avgRTSpatialHome = new Double[5];
		Double[] avgRTLetNumHome = new Double[5];
		Double[] avgRTImgHome = new Double[5];
		Double[] totalRTHome = new Double[5];
		Double[] totalRTSpatialHome = new Double[5];
		Double[] totalRTLetNumHome = new Double[5];
		Double[] totalRTImgHome = new Double[5];

		Integer[] timeSpentHome = new Integer[5];
		Integer[] timeSpentSpatialHome = new Integer[5];
		Integer[] timeSpentLetNumHome = new Integer[5];
		Integer[] timeSpentImgHome = new Integer[5];

		Integer[] avgTimeSpentHome = new Integer[5];
		Integer[] avgTimeSpentSpatialHome = new Integer[5];
		Integer[] avgTimeSpentLetNumHome = new Integer[5];
		Integer[] avgTimeSpentImgHome = new Integer[5];

		Integer[] numCompleteHome = new Integer[5];
		Integer[] numCompleteSpatialHome = new Integer[5];
		Integer[] numCompleteLetNumHome = new Integer[5];
		Integer[] numCompleteImgHome = new Integer[5];

		Integer[] numSessionsHome = new Integer[5];

		Integer numIncomplete = 0;
		Integer numIncompleteSpatial = 0;
		Integer numIncompleteLetNum = 0;
		Integer numIncompleteImg = 0;

		//initialization
		for (int i = 0; i < numCompleteLab.length; i++)
		{
			numCompleteLab[i] = 0;
			numCompleteSpatialLab[i] = 0;
			numCompleteLetNumLab[i] = 0;
			numCompleteImgLab[i] = 0;
			timeSpentLab[i] = 0;
			timeSpentSpatialLab[i] = 0;
			timeSpentLetNumLab[i] = 0;
			timeSpentImgLab[i] = 0;
			numSessionsLab[i] = 0;
			totalHRLab[i] = 0.0;
			totalHRSpatialLab[i] = 0.0;
			totalHRLetNumLab[i] = 0.0;
			totalHRImgLab[i] = 0.0;
			totalERLab[i] = 0.0;
			totalERSpatialLab[i] = 0.0;
			totalERLetNumLab[i] = 0.0;
			totalERImgLab[i] = 0.0;
			totalERILab[i] = 0.0;
			totalERISpatialLab[i] = 0.0;
			totalERILetNumLab[i] = 0.0;
			totalERIImgLab[i] = 0.0;
			totalEROLab[i] = 0.0;
			totalEROSpatialLab[i] = 0.0;
			totalEROLetNumLab[i] = 0.0;
			totalEROImgLab[i] = 0.0;
			totalRTLab[i] = 0.0;
			totalRTSpatialLab[i] = 0.0;
			totalRTLetNumLab[i] = 0.0;
			totalRTImgLab[i] = 0.0;


			numCompleteHome[i] = 0;
			numCompleteSpatialHome[i] = 0;
			numCompleteLetNumHome[i] = 0;
			numCompleteImgHome[i] = 0;
			timeSpentHome[i] = 0;
			timeSpentSpatialHome[i] = 0;
			timeSpentLetNumHome[i] = 0;
			timeSpentImgHome[i] = 0;
			numSessionsHome[i] = 0;
			totalHRHome[i] = 0.0;
			totalHRSpatialHome[i] = 0.0;
			totalHRLetNumHome[i] = 0.0;
			totalHRImgHome[i] = 0.0;
			totalERHome[i] = 0.0;
			totalERSpatialHome[i] = 0.0;
			totalERLetNumHome[i] = 0.0;
			totalERImgHome[i] = 0.0;
			totalERIHome[i] = 0.0;
			totalERISpatialHome[i] = 0.0;
			totalERILetNumHome[i] = 0.0;
			totalERIImgHome[i] = 0.0;
			totalEROHome[i] = 0.0;
			totalEROSpatialHome[i] = 0.0;
			totalEROLetNumHome[i] = 0.0;
			totalEROImgHome[i] = 0.0;
			totalRTHome[i] = 0.0;
			totalRTSpatialHome[i] = 0.0;
			totalRTLetNumHome[i] = 0.0;
			totalRTImgHome[i] = 0.0;
		}

		for(Session session : sessions)
		{
			Integer weekNumber = session.getWeekNumber();
			if(session.isInLab())
			{
				numSessionsLab[weekNumber]++;
			}
			else
			{
				numSessionsHome[weekNumber]++;
			}
		}

		for (Game game1 : user.getNbackGames())
		{
			NbackGame game = (NbackGame) game1;
//			if( ind1 == null)
//			{
//				continue;  // after week 5
//			}
			String nbackType = game.getType();
			if (game.isFinished())
			{
				Integer ind1 = game.getWeekNumber();
				if(ind1 == null)
				{
					System.out.println(game.getGameID()+" past 5 weeks");
					continue;
				}
				if(game.isLabDay())
				{
					numCompleteLab[ind1]++;
					timeSpentLab[ind1] += game.getDuration();
					totalHRLab[ind1] += game.getHr();
					totalERLab[ind1] += game.getEr();
					totalEROLab[ind1] += game.getEro();
					totalERILab[ind1] += game.getEri();
					totalRTLab[ind1] += game.getAvgResponseTime();

					if (nbackType.equals("Spatial Grid with Brain"))
					{
						numCompleteSpatialLab[ind1]++;
						timeSpentSpatialLab[ind1] += game.getDuration();
						totalHRSpatialLab[ind1] += game.getHr();
						totalERSpatialLab[ind1] += game.getEr();
						totalEROSpatialLab[ind1] += game.getEro();
						totalERISpatialLab[ind1] += game.getEri();
						totalRTSpatialLab[ind1] += game.getAvgResponseTime();

					} else if (nbackType.equals("Letters and Numbers"))
					{
						numCompleteLetNumLab[ind1]++;
						timeSpentLetNumLab[ind1] += game.getDuration();
						totalHRLetNumLab[ind1] += game.getHr();
						totalERLetNumLab[ind1] += game.getEr();
						totalEROLetNumLab[ind1] += game.getEro();
						totalERILetNumLab[ind1] += game.getEri();
						totalRTLetNumLab[ind1] += game.getAvgResponseTime();

					} else if (nbackType.equals("Images from folders"))
					{
						numCompleteImgLab[ind1]++;
						timeSpentImgLab[ind1] += game.getDuration();
						totalHRImgLab[ind1] += game.getHr();
						totalERImgLab[ind1] += game.getEr();
						totalEROImgLab[ind1] += game.getEro();
						totalERIImgLab[ind1] += game.getEri();
						totalRTImgLab[ind1] += game.getAvgResponseTime();
					}
				}
				else
				{
					numCompleteHome[ind1]++;
					timeSpentHome[ind1] += game.getDuration();
					totalHRHome[ind1] += game.getHr();
					totalERHome[ind1] += game.getEr();
					totalEROHome[ind1] += game.getEro();
					totalERIHome[ind1] += game.getEri();
					totalRTHome[ind1] += game.getAvgResponseTime();

					if (nbackType.equals("Spatial Grid with Brain"))
					{
						numCompleteSpatialHome[ind1]++;
						timeSpentSpatialHome[ind1] += game.getDuration();
						totalHRSpatialHome[ind1] += game.getHr();
						totalERSpatialHome[ind1] += game.getEr();
						totalEROSpatialHome[ind1] += game.getEro();
						totalERISpatialHome[ind1] += game.getEri();
						totalRTSpatialHome[ind1] += game.getAvgResponseTime();

					} else if (nbackType.equals("Letters and Numbers"))
					{
						numCompleteLetNumHome[ind1]++;
						timeSpentLetNumHome[ind1] += game.getDuration();
						totalHRLetNumHome[ind1] += game.getHr();
						totalERLetNumHome[ind1] += game.getEr();
						totalEROLetNumHome[ind1] += game.getEro();
						totalERILetNumHome[ind1] += game.getEri();
						totalRTLetNumHome[ind1] += game.getAvgResponseTime();

					} else if (nbackType.equals("Images from folders"))
					{
						numCompleteImgHome[ind1]++;
						timeSpentImgHome[ind1] += game.getDuration();
						totalHRImgHome[ind1] += game.getHr();
						totalERImgHome[ind1] += game.getEr();
						totalEROImgHome[ind1] += game.getEro();
						totalERIImgHome[ind1] += game.getEri();
						totalRTImgHome[ind1] += game.getAvgResponseTime();
					}
				}

			} else
			// unfinished
			{
				numIncomplete++;
//				timesSubmitted[ind1] += game.getNumWrongSub();
				if (nbackType.equals("Spatial Grid with Brain"))
				{
					numIncompleteSpatial++;
//					timesSubmittedEasy[ind1] += game.getNumWrongSub();
				} else if (nbackType.equals("Letters and Numbers"))
				{
					numIncompleteLetNum++;
//					timesSubmittedMedium[ind1] += game.getNumWrongSub();
				} else if (nbackType.equals("Images from folders"))
				{
					numIncompleteImg++;
//					timesSubmittedHard[ind1] += game.getNumWrongSub();
				}
			}

//			AvgTimeSpent_wk1
		}

		//calculate averages.

		for (int i = 0; i < 5; i++)
		{
			//average time per game
			avgTimeSpentLab[i] = 0;
			if(numCompleteLab[i] > 0)
			{
				avgTimeSpentLab[i] = timeSpentLab[i] / numCompleteLab[i];
			}
			avgTimeSpentSpatialLab[i] = 0;
			if(numCompleteSpatialLab[i] > 0)
			{
				avgTimeSpentSpatialLab[i] = timeSpentSpatialLab[i] / numCompleteSpatialLab[i];
			}
			avgTimeSpentLetNumLab[i] = 0;
			if(numCompleteLetNumLab[i] > 0)
			{
				avgTimeSpentLetNumLab[i] = timeSpentLetNumLab[i] / numCompleteLetNumLab[i];
			}
			avgTimeSpentImgLab[i] = 0;
			if(numCompleteImgLab[i] > 0)
			{
				avgTimeSpentImgLab[i] = timeSpentImgLab[i] / numCompleteImgLab[i];
			}

			//average hr LAB
			avgHRLab[i] = 0.0;
			if(numCompleteLab[i] > 0)
			{
				avgHRLab[i] = totalHRLab[i] / ((double)numCompleteLab[i]);
			}
			avgHRSpatialLab[i] = 0.0;
			if(numCompleteSpatialLab[i] > 0)
			{
				avgHRSpatialLab[i] = totalHRSpatialLab[i] / ((double) numCompleteSpatialLab[i]);
			}
			avgHRLetNumLab[i] = 0.0;
			if(numCompleteLetNumLab[i] > 0)
			{
				avgHRLetNumLab[i] = totalHRLetNumLab[i] / ((double)numCompleteLetNumLab[i]);
			}
			avgHRImgLab[i] = 0.0;
			if(numCompleteImgLab[i] > 0)
			{
				avgHRImgLab[i] = totalHRImgLab[i] / ((double)numCompleteImgLab[i]);
			}

			//average er LAB
			avgERLab[i] = 0.0;
			if(numCompleteLab[i] > 0)
			{
				avgERLab[i] = totalERLab[i] / ((double)numCompleteLab[i]);
			}
			avgERSpatialLab[i] = 0.0;
			if(numCompleteSpatialLab[i] > 0)
			{
				avgERSpatialLab[i] = totalERSpatialLab[i] / ((double)numCompleteSpatialLab[i]);
			}
			avgERLetNumLab[i] = 0.0;
			if(numCompleteLetNumLab[i] > 0)
			{
				avgERLetNumLab[i] = totalERLetNumLab[i] / ((double)numCompleteLetNumLab[i]);
			}
			avgERImgLab[i] = 0.0;
			if(numCompleteImgLab[i] > 0)
			{
				avgERImgLab[i] = totalERImgLab[i] / ((double)numCompleteImgLab[i]);
			}

			//average ero LAB
			avgEROLab[i] = 0.0;
			if(numCompleteLab[i] > 0)
			{
				avgEROLab[i] = totalEROLab[i] / ((double) numCompleteLab[i]);
			}
			avgEROSpatialLab[i] = 0.0;
			if(numCompleteSpatialLab[i] > 0)
			{
				avgEROSpatialLab[i] = totalEROSpatialLab[i] / ((double) numCompleteSpatialLab[i]);
			}
			avgEROLetNumLab[i] = 0.0;
			if(numCompleteLetNumLab[i] > 0)
			{
				avgEROLetNumLab[i] = totalEROLetNumLab[i] / ((double)numCompleteLetNumLab[i]);
			}
			avgEROImgLab[i] = 0.0;
			if(numCompleteImgLab[i] > 0)
			{
				avgEROImgLab[i] = totalEROImgLab[i] / ((double)numCompleteImgLab[i]);
			}

			//average eri LAB
			avgERILab[i] = 0.0;
			if(numCompleteLab[i] > 0)
			{
				avgERILab[i] = totalERILab[i] / numCompleteLab[i];
			}
			avgERISpatialLab[i] = 0.0;
			if(numCompleteSpatialLab[i] > 0)
			{
				avgERISpatialLab[i] = totalERISpatialLab[i] / numCompleteSpatialLab[i];
			}
			avgERILetNumLab[i] = 0.0;
			if(numCompleteLetNumLab[i] > 0)
			{
				avgERILetNumLab[i] = totalERILetNumLab[i] / numCompleteLetNumLab[i];
			}
			avgERIImgLab[i] = 0.0;
			if(numCompleteImgLab[i] > 0)
			{
				avgERIImgLab[i] = totalERIImgLab[i] / numCompleteImgLab[i];
			}

			//average RT LAB
			avgRTLab[i] = 0.0;
			if(numCompleteLab[i] > 0)
			{
				avgRTLab[i] = totalRTLab[i] / numCompleteLab[i];
			}
			avgRTSpatialLab[i] = 0.0;
			if(numCompleteSpatialLab[i] > 0)
			{
				avgRTSpatialLab[i] = totalRTSpatialLab[i] / numCompleteSpatialLab[i];
			}
			avgRTLetNumLab[i] = 0.0;
			if(numCompleteLetNumLab[i] > 0)
			{
				avgRTLetNumLab[i] = totalRTLetNumLab[i] / numCompleteLetNumLab[i];
			}
			avgRTImgLab[i] = 0.0;
			if(numCompleteImgLab[i] > 0)
			{
				avgRTImgLab[i] = totalRTImgLab[i] / numCompleteImgLab[i];
			}

			//HOME CALCULATIONS

			avgTimeSpentHome[i] = 0;
			if(numCompleteHome[i] > 0)
			{
				avgTimeSpentHome[i] = timeSpentHome[i] / numCompleteHome[i];
			}
			avgTimeSpentSpatialHome[i] = 0;
			if(numCompleteSpatialHome[i] > 0)
			{
				avgTimeSpentSpatialHome[i] = timeSpentSpatialHome[i] / numCompleteSpatialHome[i];
			}
			avgTimeSpentLetNumHome[i] = 0;
			if(numCompleteLetNumHome[i] > 0)
			{
				avgTimeSpentLetNumHome[i] = timeSpentLetNumHome[i] / numCompleteLetNumHome[i];
			}
			avgTimeSpentImgHome[i] = 0;
			if(numCompleteImgHome[i] > 0)
			{
				avgTimeSpentImgHome[i] = timeSpentImgHome[i] / numCompleteImgHome[i];
			}

			//average hr Home
			avgHRHome[i] = 0.0;
			if(numCompleteHome[i] > 0)
			{
				avgHRHome[i] = totalHRHome[i] / ((double)numCompleteHome[i]);
			}
			avgHRSpatialHome[i] = 0.0;
			if(numCompleteSpatialHome[i] > 0)
			{
				avgHRSpatialHome[i] = totalHRSpatialHome[i] / ((double) numCompleteSpatialHome[i]);
			}
			avgHRLetNumHome[i] = 0.0;
			if(numCompleteLetNumHome[i] > 0)
			{
				avgHRLetNumHome[i] = totalHRLetNumHome[i] / ((double)numCompleteLetNumHome[i]);
			}
			avgHRImgHome[i] = 0.0;
			if(numCompleteImgHome[i] > 0)
			{
				avgHRImgHome[i] = totalHRImgHome[i] / ((double)numCompleteImgHome[i]);
			}

			//average er Home
			avgERHome[i] = 0.0;
			if(numCompleteHome[i] > 0)
			{
				avgERHome[i] = totalERHome[i] / ((double)numCompleteHome[i]);
			}
			avgERSpatialHome[i] = 0.0;
			if(numCompleteSpatialHome[i] > 0)
			{
				avgERSpatialHome[i] = totalERSpatialHome[i] / ((double)numCompleteSpatialHome[i]);
			}
			avgERLetNumHome[i] = 0.0;
			if(numCompleteLetNumHome[i] > 0)
			{
				avgERLetNumHome[i] = totalERLetNumHome[i] / ((double)numCompleteLetNumHome[i]);
			}
			avgERImgHome[i] = 0.0;
			if(numCompleteImgHome[i] > 0)
			{
				avgERImgHome[i] = totalERImgHome[i] / ((double)numCompleteImgHome[i]);
			}

			//average ero Home
			avgEROHome[i] = 0.0;
			if(numCompleteHome[i] > 0)
			{
				avgEROHome[i] = totalEROHome[i] / ((double) numCompleteHome[i]);
			}
			avgEROSpatialHome[i] = 0.0;
			if(numCompleteSpatialHome[i] > 0)
			{
				avgEROSpatialHome[i] = totalEROSpatialHome[i] / ((double) numCompleteSpatialHome[i]);
			}
			avgEROLetNumHome[i] = 0.0;
			if(numCompleteLetNumHome[i] > 0)
			{
				avgEROLetNumHome[i] = totalEROLetNumHome[i] / ((double)numCompleteLetNumHome[i]);
			}
			avgEROImgHome[i] = 0.0;
			if(numCompleteImgHome[i] > 0)
			{
				avgEROImgHome[i] = totalEROImgHome[i] / ((double)numCompleteImgHome[i]);
			}

			//average eri Home
			avgERIHome[i] = 0.0;
			if(numCompleteHome[i] > 0)
			{
				avgERIHome[i] = totalERIHome[i] / numCompleteHome[i];
			}
			avgERISpatialHome[i] = 0.0;
			if(numCompleteSpatialHome[i] > 0)
			{
				avgERISpatialHome[i] = totalERISpatialHome[i] / numCompleteSpatialHome[i];
			}
			avgERILetNumHome[i] = 0.0;
			if(numCompleteLetNumHome[i] > 0)
			{
				avgERILetNumHome[i] = totalERILetNumHome[i] / numCompleteLetNumHome[i];
			}
			avgERIImgHome[i] = 0.0;
			if(numCompleteImgHome[i] > 0)
			{
				avgERIImgHome[i] = totalERIImgHome[i] / numCompleteImgHome[i];
			}

			//average RT Home
			avgRTHome[i] = 0.0;
			if(numCompleteHome[i] > 0)
			{
				avgRTHome[i] = totalRTHome[i] / numCompleteHome[i];
			}
			avgRTSpatialHome[i] = 0.0;
			if(numCompleteSpatialHome[i] > 0)
			{
				avgRTSpatialHome[i] = totalRTSpatialHome[i] / numCompleteSpatialHome[i];
			}
			avgRTLetNumHome[i] = 0.0;
			if(numCompleteLetNumHome[i] > 0)
			{
				avgRTLetNumHome[i] = totalRTLetNumHome[i] / numCompleteLetNumHome[i];
			}
			avgRTImgHome[i] = 0.0;
			if(numCompleteImgHome[i] > 0)
			{
				avgRTImgHome[i] = totalRTImgHome[i] / numCompleteImgHome[i];
			}
		}

		reportLine.set5("NumSessionsLab_wk%", numSessionsLab);
		reportLine.set5("NumSessionsHome_wk%", numSessionsHome);

		//set all variables in the sheet.
		reportLine.set5("NumCompleteLab_wk%", numCompleteLab);
		reportLine.set5("NumCompleteLab_wk%_Spatial", numCompleteSpatialLab);
		reportLine.set5("NumCompleteLab_wk%_LetNum", numCompleteLetNumLab);
		reportLine.set5("NumCompleteLab_wk%_Img", numCompleteImgLab);

		reportLine.set5("AvgHRLab_wk% (rate)", avgHRLab);
		reportLine.set5("AvgHRLab_wk%_Spatial (rate)", avgHRSpatialLab);
		reportLine.set5("AvgHRLab_wk%_LetNum (rate)", avgHRLetNumLab);
		reportLine.set5("AvgHRLab_wk%_Img (rate)", avgHRImgLab);

		reportLine.set5("AvgERLab_wk% (rate)", avgERLab);
		reportLine.set5("AvgERLab_wk%_Spatial (rate)", avgERSpatialLab);
		reportLine.set5("AvgERLab_wk%_LetNum (rate)", avgERLetNumLab);
		reportLine.set5("AvgERLab_wk%_Img (rate)", avgERImgLab);

		reportLine.set5("AvgEROLab_wk% (rate)", avgEROLab);
		reportLine.set5("AvgEROLab_wk%_Spatial (rate)", avgEROSpatialLab);
		reportLine.set5("AvgEROLab_wk%_LetNum (rate)", avgEROLetNumLab);
		reportLine.set5("AvgEROLab_wk%_Img (rate)", avgEROImgLab);

		reportLine.set5("AvgERILab_wk% (rate)", avgERILab);
		reportLine.set5("AvgERILab_wk%_Spatial (rate)", avgERISpatialLab);
		reportLine.set5("AvgERILab_wk%_LetNum (rate)", avgERILetNumLab);
		reportLine.set5("AvgERILab_wk%_Img (rate)", avgERIImgLab);

		reportLine.set5("AvgRTLab_wk% (s)", avgRTLab);
		reportLine.set5("AvgRTLab_wk%_Spatial (s)", avgRTSpatialLab);
		reportLine.set5("AvgRTLab_wk%_LetNum (s)", avgRTLetNumLab);
		reportLine.set5("AvgRTLab_wk%_Img (s)", avgRTImgLab);

		reportLine.set5("AvgTimeSpentLab_wk% (s)", avgTimeSpentLab);
		reportLine.set5("AvgTimeSpentLab_wk%_Spatial (s)", avgTimeSpentSpatialLab);
		reportLine.set5("AvgTimeSpentLab_wk%_LetNum (s)", avgTimeSpentLetNumLab);
		reportLine.set5("AvgTimeSpentLab_wk%_Img (s)", avgTimeSpentImgLab);

		//Now for home.

		reportLine.set5("NumCompleteHome_wk%", numCompleteHome);
		reportLine.set5("NumCompleteHome_wk%_Spatial", numCompleteSpatialHome);
		reportLine.set5("NumCompleteHome_wk%_LetNum", numCompleteLetNumHome);
		reportLine.set5("NumCompleteHome_wk%_Img", numCompleteImgHome);

		reportLine.set5("AvgHRHome_wk% (rate)", avgHRHome);
		reportLine.set5("AvgHRHome_wk%_Spatial (rate)", avgHRSpatialHome);
		reportLine.set5("AvgHRHome_wk%_LetNum (rate)", avgHRLetNumHome);
		reportLine.set5("AvgHRHome_wk%_Img (rate)", avgHRImgHome);

		reportLine.set5("AvgERHome_wk% (rate)", avgERHome);
		reportLine.set5("AvgERHome_wk%_Spatial (rate)", avgERSpatialHome);
		reportLine.set5("AvgERHome_wk%_LetNum (rate)", avgERLetNumHome);
		reportLine.set5("AvgERHome_wk%_Img (rate)", avgERImgHome);

		reportLine.set5("AvgEROHome_wk% (rate)", avgEROHome);
		reportLine.set5("AvgEROHome_wk%_Spatial (rate)", avgEROSpatialHome);
		reportLine.set5("AvgEROHome_wk%_LetNum (rate)", avgEROLetNumHome);
		reportLine.set5("AvgEROHome_wk%_Img (rate)", avgEROImgHome);

		reportLine.set5("AvgERIHome_wk% (rate)", avgERIHome);
		reportLine.set5("AvgERIHome_wk%_Spatial (rate)", avgERISpatialHome);
		reportLine.set5("AvgERIHome_wk%_LetNum (rate)", avgERILetNumHome);
		reportLine.set5("AvgERIHome_wk%_Img (rate)", avgERIImgHome);

		reportLine.set5("AvgRTHome_wk% (s)", avgRTHome);
		reportLine.set5("AvgRTHome_wk%_Spatial (s)", avgRTSpatialHome);
		reportLine.set5("AvgRTHome_wk%_LetNum (s)", avgRTLetNumHome);
		reportLine.set5("AvgRTHome_wk%_Img (s)", avgRTImgHome);

		reportLine.set5("AvgTimeSpentHome_wk% (s)", avgTimeSpentHome);
		reportLine.set5("AvgTimeSpentHome_wk%_Spatial (s)", avgTimeSpentSpatialHome);
		reportLine.set5("AvgTimeSpentHome_wk%_LetNum (s)", avgTimeSpentLetNumHome);
		reportLine.set5("AvgTimeSpentHome_wk%_Img (s)", avgTimeSpentImgHome);

		//Unfinished stuff at the end
		reportLine.set("NumUnfinished", numIncomplete);
		reportLine.set("NumUnfinishedSpatial", numIncompleteSpatial);
		reportLine.set("NumUnfinishedLetNum", numIncompleteLetNum);
		reportLine.set("NumUnfinishedImg", numIncompleteImg);

		if (nbackByIdNoTitle)
		{
			nbackByIdResults.add(reportLine.getReportTitle());
			nbackByIdNoTitle = false;
		}

		nbackByIdResults.add(reportLine.getLine());

	}



	private List<GameRT> nbackResponses = new ArrayList<GameRT>();

	private void populateNbackGames(File file, File[] responseFiles,
			String[][] nbackBySession_table2, int i,
			String[][] sessionInfoData_table2, String id, String[][][] raw_data) throws ParseException
	{
		for (int j = 0; j < responseFiles.length; j++)
		{
			File responseFile = responseFiles[j];
			String gameIdStr = responseFile.getName();
			int i3 = gameIdStr.indexOf("GameID_");
			int i4 = gameIdStr.indexOf(".txt");
			gameIdStr = gameIdStr.substring(i3+7, i4);
			GameRT game = new GameRT(gameIdStr);
			List<Response> responses = new ArrayList<Response>();
			String lines[] = readLines(responseFile);
			int countCorrect = 0;
			int countOmission = 0;
			int countIncorrect = 0;
			Double avg = null;
			for (int k = 0; k < lines.length; k++)
			{
				String line = lines[k];
				if(line.contains("answer was given"))
				{
					int i1 = line.indexOf("time of ");
					int i2 = line.indexOf("ms");
					String numberString = line.substring(i1+8, i2);
					Integer number = Integer.valueOf(numberString);
					Response response = new Response();
					response.setTime(number);
					boolean correct = line.contains("Correct");
					response.setCorrect(correct);
					responses.add(response);
					if(correct)
					{
						countCorrect++;
					}
					else if(number==0)
					{
						countOmission++;
					}
					else
					{
						countIncorrect++;
					}
				}
				else if(line.contains("The average response time"))
				{
					int i1 = line.indexOf("inputs is ");
					int i2 = line.indexOf("ms");
					String numberString = line.substring(i1+10, i2);

					if(numberString.isEmpty())
					{
						break;
					}
					avg = Double.valueOf(numberString);
					game.setAvgResponseTime(avg);
				}
			}
			if(game.getAvgResponseTime()==null)
			{
				continue;
			}
			game.setResponses(responses);
			game.setNumCorrect(countCorrect);
			game.setNumIncorrect(countIncorrect);
			game.setNumOmission(countOmission);
			nbackResponses.add(game);
		}
		//System.out.println(""); //Done adding all GameRT files to nbackresponses list.

		List<NbackGame> games = new ArrayList<NbackGame>();
		User user = users.get(id);
		if (user == null)
		{
			System.out.println("null user " + id);
			return;
		}
		String[][] nbackReport = raw_data[3];
//		boolean start = false;
		int gameCount = 0;
		for (i = 1; i < nbackReport.length; i++)
		{

			String[] line = nbackReport.clone()[i];
//			if(line[0].contains("All Games"))
//			{
//				start = true;
//			}
//			if(! start)
//			{
//				continue;
//			}
			gameCount++;
			String gameId = line[1];
			if (gameId == null || !Character.isDigit(gameId.charAt(0)))
				// does not start with a digit, is not an id
			{
				continue;
			}

			GameRT corrGameRT = null; //corresponding GameRT
			for(GameRT nbackgame : nbackResponses)
			{
				//System.out.println(nbackgame.getGameID());
				if(nbackgame.getGameID().equals(gameId))
				{
					corrGameRT = nbackgame;
					break;
				}
			}

			if(corrGameRT == null) //if it couldn't find the corresponding GameRT file
			{
				continue;
			}

			NbackGame game = new NbackGame();
			game.setGameID(gameId);
			Date dateFinished = createDate2(line[3]);
			game.setDateFinished(dateFinished);
			game.setTimeFinished(createDate2(line[4]));
			game.setLabDay(user.isLabDay(dateFinished));

			int duration = -1;
			try
			{
				duration = Integer.parseInt(line[2]);
			} catch (Exception e)
			{
			}

			int numCorrectSub = -1;
			int numWrongSub = -1; //incorrect and omission
			int numOmissionSub = -1;
			int numIncorrectSub = -1;
			try
			{
				numCorrectSub = Integer.parseInt(line[8]);
				if(numCorrectSub!=corrGameRT.getNumCorrect())
				{
					System.out.println("Num correct responses do not correspond for game "+gameId);
					numCorrectSub=corrGameRT.getNumCorrect();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				numWrongSub = Integer.parseInt(line[9]);
				if(numWrongSub!=(corrGameRT.getNumIncorrect()+corrGameRT.getNumOmission()))
				{
					System.out.println("Num wrong responses do not correspond for game "+gameId);
					numWrongSub = corrGameRT.getNumIncorrect()+corrGameRT.getNumOmission();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			numOmissionSub = corrGameRT.getNumOmission();
			numIncorrectSub = corrGameRT.getNumIncorrect();


			//Set all variables
			game.setDuration(duration);
			game.setType(line[7]);
			game.setTypeOfGame("nback");
			game.setResponseFile(corrGameRT);
			game.setWeekNumber(user.getFirstWeek());
			game.setNumCorrect(numCorrectSub);
			game.setNumWrong(numWrongSub);
			game.setNumOmission(numOmissionSub);
			game.setNumIncorrect(numIncorrectSub);
			game.setAvgResponseTime(corrGameRT.getAvgResponseTime());
			game.setHr(line[7]);
			game.setEr(line[7]);
			game.setEro(line[7]);
			game.setEri(line[7]);


			games.add(game);
		}
		user.setNbackGames(games);

	}

	private String[] readLines(File responseFile)
	{
		List<String> result = new ArrayList<String>();
		try {
			FileInputStream fs= new FileInputStream(responseFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			String line = br.readLine();
			while(line!=null){
				result.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toArray(new String[result.size()]);
	}

	private List<Session> createSessions(User user, String[][][] raw_data) throws ParseException
	{
		String[][] loginReport = raw_data[1];
		List<Session> sessions = new ArrayList<Session>();
		String previousDay = "";
		Session session = null;
		for (int j = 1; j < loginReport.length; j++)
		{
			String[] line = loginReport[j];
			String day = line[0].substring(0, 10);
			Date date = null;
			if (!day.equals(previousDay))
			{
				if (session != null)
				{
					sessions.add(session);
				}
				session = new Session();
				date = createDate2(day);
				session.setDate(date);
				Date login = createDate4(line[1].substring(11));
				session.setLogin1(login);
				Date logout = createDate4(line[2].substring(11));
				session.setLogout1(logout);
				previousDay = day;
				session.setWeekNumber(user.getFirstWeek());
				session.setInLab(user.isLabDay(date));
			}
			else
			{
				Date login = createDate4(line[1].substring(11));
				session.setLogin2(login);
				Date logout = createDate4(line[2].substring(11));
				session.setLogout2(logout);
			}



//			session = null;
		}

		if (session != null)
		{
			sessions.add(session);
		}
		return sessions;
	}

	private void readData() throws IOException
	{

		bw = new BufferedWriter(new FileWriter("results.txt"));

		// Start with output files; store them in an array files[]
//		String folder = "new";
		String folder = "All_Reports_updated";
//		File inputFolder = new File("All_Reports_updated");
		File inputFolder = new File(folder);
		// this will implements the single file algorithm below
		File correctedReportsFolder = null;
		if (inputFolder.isDirectory()) // check if said file is a directory
		{
			removeLeadingSpaces(inputFolder, folder); // Removes first x spaces in
												// each file
			correctedReportsFolder = new File( folder+
					"/correctedReports");
			// The following MUST OCCUR:
			reportFiles = correctedReportsFolder.listFiles();
			// create array to hold all files in the directory,
			// regardless of extension; puts all files in
		}

		// Get the text files containing data with response times
		File nbkResponses = new File(folder+"/NbkResponses");

		if (nbkResponses.isDirectory()) // check if said file is a directory
		{
			// The following MUST OCCUR:
			responseFiles = nbkResponses.listFiles();
			// create array to hold all files in the directory,
			// regardless of extension; puts all files in
		}

		// Next, get the sessionDates as a .xlsx file; put everything into a
		// 2D array "sessionInfoData_table"
		File a = new File("NICT_SessionDates10Apr2016 .xlsx");
		FileInputStream inputStream = new FileInputStream(a);
		Workbook sessionDatesInput = new XSSFWorkbook(inputStream);
		Sheet sessionInfoData = sessionDatesInput.getSheetAt(0);
		// boolean proceed = true;
		for (Iterator<Row> rit = sessionInfoData.rowIterator(); rit.hasNext();)
		{
			Row row = rit.next();
			// System.out.println(row);
			Cell x = row.getCell(0);

			int v = row.getPhysicalNumberOfCells();
			// System.out.println(v);
			x.setCellType(Cell.CELL_TYPE_STRING);
			if (x.getStringCellValue().equals(""))
			{
				break;
			}
			for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();)
			{
				XSSFCell cell = (XSSFCell) cit.next();
				// Reads the cells in the file A.P
				if (!cell.toString().contains("-201"))
				{
					cell.setCellType(Cell.CELL_TYPE_STRING);
					// reads each column as a string, and later, if
					// it's a number, can convert to a number
					sessionInfoData_table[cell.getRowIndex()][cell
							.getColumnIndex()] = cell.getStringCellValue();
				} else
				{
					sessionInfoData_table[cell.getRowIndex()][cell
							.getColumnIndex()] = cell.toString();
				}
			}
		}

		sessionDatesInput.close();

	}

	private void write(File[] reportFiles, List<ArrayList<String>> resultsTable,
			String fileName, String sheetName) throws IOException
	{
	    Workbook outputFile = new XSSFWorkbook(); // Empty Excel output file is initialized

	    // Create output sheet from finalized nbackBySession_table
		Sheet sudokuBySession_sheet = outputFile.createSheet(sheetName);

		Cell cell_w = null;
		for (int r = 0; r < resultsTable.size(); r++)// +1 b/c one row for
		{
			Row row = sudokuBySession_sheet.createRow(r);
			int columns = resultsTable.get(0).size();
			ArrayList<String> line = resultsTable.get(r);
			for (int c = 0; c < columns; c++)
			{
				String element = line.get(c);
				cell_w = row.createCell(c);
				cell_w.setCellType(Cell.CELL_TYPE_STRING);
				if ( element == null)
				{
					cell_w.setCellValue("");
				} else
				{
					cell_w.setCellValue(element.toString());
				}
			}
		}

		// Get the date and time
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
		Date date = new Date();
		String fileDate = dateFormat.format(date);

		try
		{
			FileOutputStream output = new FileOutputStream(fileName
					+ fileDate + ".xlsx"); // Changed directory to fit comp A.P
			outputFile.write(output);
			output.close();
			outputFile.close();
			System.out.println(fileDate); // Print out today's date
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		bw.close();
	}


//	private ArrayList<String> reportTitle = new ArrayList<String>();
	private boolean sudokuBySessionNoTitle = true;
	private void sudokuBySessionSheet(
			List<ArrayList<String>> sudokuBySessionResults, String userId, int index)
			throws IOException
	{

		User user = users.get(userId);
		bw.write("SUDOKU BY SESSION");
		bw.newLine();
		bw.newLine();

		List<Session> sessions = user.getSessions();
		for (Session session : sessions)
		{
			ReportLine reportLine = new ReportLine();
			reportLine.set("ID", userId);
			Date date = session.getDate();
			reportLine.set("Date", date);
			reportLine.set("LabSession?", session.isInLab() ? "Yes" : "No");

			Integer numComplete = 0;
			Integer numCompleteEasy = 0;
			Integer numCompleteMedium = 0;
			Integer numCompleteHard = 0;
			Integer timeSpent = 0;
			Integer timeSpentEasy = 0;
			Integer timeSpentMedium = 0;
			Integer timeSpentHard = 0;
			Integer timesSubmitted = 0;
			Integer timesSubmittedEasy = 0;
			Integer timesSubmittedMedium = 0;
			Integer timesSubmittedHard = 0;
			Integer[] numTimesSubmitted = new Integer[5];
			Integer[] timeSpentGame = new Integer[5];
			String[] difficultyGame = new String[5];
			// initialization
			for (int i = 0; i < 5; i++)
			{
				numTimesSubmitted[i] = 0;
				timeSpentGame[i] = 0;
				difficultyGame[i] = "";
			}
			int index5 = 0;
			for (Game game1 : user.getSudokuGames())
			{
				SudokuGame game = (SudokuGame) game1;
				Date gameDate = game.getDateFinished();
				if( gameDate == null || ! gameDate.equals(date))
				{
					continue;
				}
				String difficulty = game.getDifficulty();
				if (game.isFinished())
				{
					Integer ind1 = game.getWeekNumber();
					if (ind1 == null)
					{
						//System.out.println(game.getGameID() + " past 5 weeks");
						continue;
					}

					numComplete++;
					timeSpent += game.getDuration();
					timesSubmitted += game.getNumWrongSub() + 1;

					if (difficulty.equals("Easy"))
					{
						numCompleteEasy++;
						timeSpentEasy += game.getDuration();
						timesSubmittedEasy += game.getNumWrongSub() + 1;
					} else if (difficulty.equals("Medium"))
					{
						numCompleteMedium++;
						timeSpentMedium += game.getDuration();
						timesSubmittedMedium += game.getNumWrongSub() + 1;
					} else if (difficulty.equals("Hard"))
					{
						numCompleteHard++;
						timeSpentHard += game.getDuration();
						timesSubmittedHard += game.getNumWrongSub() + 1;
					}
				}
				if( index5 < 5)
				{
					numTimesSubmitted[index5] = timesSubmitted;
					timeSpentGame[index5] = timeSpent;
					difficultyGame[index5] = difficulty;
					index5++;
				}
			}

			reportLine.set("NumComplete", numComplete);
			reportLine.set("NumCompleteEasy", numCompleteEasy);
			reportLine.set("NumCompleteMedium", numCompleteMedium);
			reportLine.set("NumCompleteHard", numCompleteHard);
			reportLine.set("NumTimesSubmitted", timesSubmitted);
			reportLine.set("NumTimesSubmittedEasy", timesSubmittedEasy);
			reportLine.set("NumTimesSubmittedMedium", timesSubmittedMedium);
			reportLine.set("NumTimesSubmittedHard", timesSubmittedHard);
			reportLine.set("TotalTimeSpent (s)", timeSpent);
			reportLine.set("TotalTimeSpentEasy (s)", timeSpentEasy);
			reportLine.set("TotalTimeSpentMedium (s)", timeSpentMedium);
			reportLine.set("TotalTimeSpentHard (s)", timeSpentHard);
			Integer avgTimeSpent = 0;
			if(numComplete != 0)
			{
				avgTimeSpent = timeSpent/numComplete;
			}
			reportLine.set("AverageTimeSpent (s)", avgTimeSpent);
			Integer avgTimeSpentEasy = 0;
			if(numCompleteEasy != 0)
			{
				avgTimeSpentEasy = timeSpentEasy/numCompleteEasy;
			}
			reportLine.set("AverageTimeSpentEasy (s)", avgTimeSpentEasy);
			Integer avgTimeSpentMedium = 0;
			if(numCompleteMedium != 0)
			{
				avgTimeSpentMedium = timeSpentMedium/numCompleteMedium;
			}
			reportLine.set("AverageTimeSpentMedium (s)", avgTimeSpentMedium);
			Integer avgTimeSpentHard = 0;
			if(numCompleteHard != 0)
			{
				avgTimeSpentHard = timeSpentHard/numCompleteHard;
			}
			reportLine.set("AverageTimeSpentHard (s)", avgTimeSpentHard);

			reportLine.set5("Difficulty%", difficultyGame);
			reportLine.set5("NumTimesSubmitted%", numTimesSubmitted);
			reportLine.set5("TimeSpent%", timeSpentGame);




			if (sudokuBySessionNoTitle)
			{
				sudokuBySessionResults.add(reportLine.getReportTitle());
				sudokuBySessionNoTitle = false;
			}

			sudokuBySessionResults.add(reportLine.getLine());
		}
	}

	private boolean sudokuByIdNoTitle = true;
	private void sudokuByIdSheet(List<ArrayList<String>> sudokuByIdResults, String userId, int index) throws IOException
	{
		User user = users.get(userId);
		bw.write("SUDOKU BY ID");
		bw.newLine();
		bw.newLine();

		ReportLine reportLine = new ReportLine();

		// Object games = id;

//		String[] line = new String[400];
		// "ID", "Group", "Round", "Age", "First_Lab_Day",
		reportLine.set("ID", userId);
		reportLine.set("Group", user.getGrp());
		reportLine.set("Round", "");
		reportLine.set("Age", "");
		Date firstWeek = user.getFirstWeek();
		reportLine.set("First_Lab_Day", firstWeek);

		List<Session> sessions = user.getSessions();
		System.out.println("session="+sessions.size());
		if( sessions.size() == 0)
		{
			return;
		}
		Session lastSession = sessions.get(sessions.size() - 1);
		Date lastLogin = lastSession.getDate();
		reportLine.set("Last_Login", lastLogin);
		Date preTest = user.getPrelab();
		Date postTest = user.getPostlab();
		reportLine.set("Pre-Test_Day", preTest);
		//System.out.println(postTest);
		if(postTest == null)
		{
			postTest = new Date();
		}
		reportLine.set("Post-Test_Day", postTest);

		Integer elapsedDays = getDaysDiff(sessions.get(0).getDate(),
				lastSession.getDate());
		reportLine.set("daysElapsed", elapsedDays);

		elapsedDays = getDaysDiff(lastLogin, postTest);
		reportLine.set("LastDayPlayed_to_PostTestDay", elapsedDays);

		elapsedDays = getDaysDiff(preTest, postTest);
		reportLine.set("Pre-Test_to_Post-Test", elapsedDays);

//		Date firstLabDay = user.getWeekslab()[0];
		elapsedDays = getDaysDiff(preTest, firstWeek);
		reportLine.set("Pre-Test_to_FirstLabDay", elapsedDays);

		Integer[] numCompleteLab = new Integer[5];
		Integer[] numCompleteEasyLab = new Integer[5];
		Integer[] numCompleteMediumLab = new Integer[5];
		Integer[] numCompleteHardLab = new Integer[5];
		Integer[] timeSpentLab = new Integer[5];
		Integer[] timeSpentEasyLab = new Integer[5];
		Integer[] timeSpentMediumLab = new Integer[5];
		Integer[] timeSpentHardLab = new Integer[5];
		Integer[] timesSubmittedLab = new Integer[5];
		Integer[] timesSubmittedEasyLab = new Integer[5];
		Integer[] timesSubmittedMediumLab = new Integer[5];
		Integer[] timesSubmittedHardLab = new Integer[5];
		Integer[] numSessionsLab = new Integer[5];
		Integer[] avgTimeSpentLab = new Integer[5];
		Integer[] avgTimeSpentEasyLab = new Integer[5];
		Integer[] avgTimeSpentMediumLab = new Integer[5];
		Integer[] avgTimeSpentHardLab = new Integer[5];

		Integer[] numCompleteHome = new Integer[5];
		Integer[] numCompleteEasyHome = new Integer[5];
		Integer[] numCompleteMediumHome = new Integer[5];
		Integer[] numCompleteHardHome = new Integer[5];
		Integer[] timeSpentHome = new Integer[5];
		Integer[] timeSpentEasyHome = new Integer[5];
		Integer[] timeSpentMediumHome = new Integer[5];
		Integer[] timeSpentHardHome = new Integer[5];
		Integer[] timesSubmittedHome = new Integer[5];
		Integer[] timesSubmittedEasyHome = new Integer[5];
		Integer[] timesSubmittedMediumHome = new Integer[5];
		Integer[] timesSubmittedHardHome = new Integer[5];
		Integer[] numSessionsHome = new Integer[5];
		Integer[] avgTimeSpentHome = new Integer[5];
		Integer[] avgTimeSpentEasyHome = new Integer[5];
		Integer[] avgTimeSpentMediumHome = new Integer[5];
		Integer[] avgTimeSpentHardHome = new Integer[5];

		// home vs lab
		// date of test exactly weekn then it's lab otherwise is home

		Integer numIncomplete = 0;
		Integer numIncompleteEasy = 0;
		Integer numIncompleteMedium = 0;
		Integer numIncompleteHard = 0;

		// initialization
		for (int i = 0; i < numCompleteLab.length; i++)
		{
			numCompleteLab[i] = 0;
			numCompleteEasyLab[i] = 0;
			numCompleteMediumLab[i] = 0;
			numCompleteHardLab[i] = 0;
			timeSpentLab[i] = 0;
			timeSpentEasyLab[i] = 0;
			timeSpentMediumLab[i] = 0;
			timeSpentHardLab[i] = 0;
			timesSubmittedLab[i] = 0;
			timesSubmittedEasyLab[i] = 0;
			timesSubmittedMediumLab[i] = 0;
			timesSubmittedHardLab[i] = 0;
			numSessionsLab[i] = 0;

			numCompleteHome[i] = 0;
			numCompleteEasyHome[i] = 0;
			numCompleteMediumHome[i] = 0;
			numCompleteHardHome[i] = 0;
			timeSpentHome[i] = 0;
			timeSpentEasyHome[i] = 0;
			timeSpentMediumHome[i] = 0;
			timeSpentHardHome[i] = 0;
			timesSubmittedHome[i] = 0;
			timesSubmittedEasyHome[i] = 0;
			timesSubmittedMediumHome[i] = 0;
			timesSubmittedHardHome[i] = 0;
			numSessionsHome[i] = 0;
		}

		for(Session session : sessions)
		{
			Integer weekNumber = session.getWeekNumber();
			if(session.isInLab())
			{
				numSessionsLab[weekNumber]++;
			}
			else
			{
				numSessionsHome[weekNumber]++;
			}
		}


		for (Game game1 : user.getSudokuGames())
		{
			SudokuGame game = (SudokuGame) game1;
//			if( ind1 == null)
//			{
//				continue;  // after week 5
//			}
			String difficulty = game.getDifficulty();
			if (game.isFinished())
			{
				Integer ind1 = game.getWeekNumber();
				if(ind1 == null)
				{
					System.out.println(game.getGameID()+" past 5 weeks");
					continue;
				}
				if(game.isLabDay())
				{
					numCompleteLab[ind1]++;
					timeSpentLab[ind1] += game.getDuration();
					timesSubmittedLab[ind1] += game.getNumWrongSub() + 1;

					if (difficulty.equals("Easy"))
					{
						numCompleteEasyLab[ind1]++;
						timeSpentEasyLab[ind1] += game.getDuration();
						timesSubmittedEasyLab[ind1] += game.getNumWrongSub() + 1;
					} else if (difficulty.equals("Medium"))
					{
						numCompleteMediumLab[ind1]++;
						timeSpentMediumLab[ind1] += game.getDuration();
						timesSubmittedMediumLab[ind1] += game.getNumWrongSub() + 1;
					} else if (difficulty.equals("Hard"))
					{
						numCompleteHardLab[ind1]++;
						timeSpentHardLab[ind1] += game.getDuration();
						timesSubmittedHardLab[ind1] += game.getNumWrongSub() + 1;
					}
				}
				else
				{
					numCompleteHome[ind1]++;
					timeSpentHome[ind1] += game.getDuration();
					timesSubmittedHome[ind1] += game.getNumWrongSub()+1;

					if (difficulty.equals("Easy"))
					{
						numCompleteEasyHome[ind1]++;
						timeSpentEasyHome[ind1] += game.getDuration();
						timesSubmittedEasyHome[ind1] += game.getNumWrongSub()+1;
					} else if (difficulty.equals("Medium"))
					{
						numCompleteMediumHome[ind1]++;
						timeSpentMediumHome[ind1] += game.getDuration();
						timesSubmittedMediumHome[ind1] += game.getNumWrongSub()+1;
					} else if (difficulty.equals("Hard"))
					{
						numCompleteHardHome[ind1]++;
						timeSpentHardHome[ind1] += game.getDuration();
						timesSubmittedHardHome[ind1] += game.getNumWrongSub()+1;
					}
				}

			} else
			// unfinished
			{
				numIncomplete++;
//				timesSubmitted[ind1] += game.getNumWrongSub();
				if (difficulty.equals("Easy"))
				{
					numIncompleteEasy++;
//					timesSubmittedEasy[ind1] += game.getNumWrongSub();
				} else if (difficulty.equals("Medium"))
				{
					numIncompleteMedium++;
//					timesSubmittedMedium[ind1] += game.getNumWrongSub();
				} else if (difficulty.equals("Hard"))
				{
					numIncompleteHard++;
//					timesSubmittedHard[ind1] += game.getNumWrongSub();
				}
			}

//			AvgTimeSpent_wk1
		}



		for (int i = 0; i < 5; i++)
		{
			avgTimeSpentLab[i] = 0;
			if(numCompleteLab[i] > 0)
			{
				avgTimeSpentLab[i] = timeSpentLab[i] / numCompleteLab[i];
			}
			avgTimeSpentEasyLab[i] = 0;
			if(numCompleteEasyLab[i] > 0)
			{
				avgTimeSpentEasyLab[i] = timeSpentEasyLab[i] / numCompleteEasyLab[i];
			}
			avgTimeSpentMediumLab[i] = 0;
			if(numCompleteMediumLab[i] > 0)
			{
				avgTimeSpentMediumLab[i] = timeSpentMediumLab[i] / numCompleteMediumLab[i];
			}
			avgTimeSpentHardLab[i] = 0;
			if(numCompleteHardLab[i] > 0)
			{
				avgTimeSpentHardLab[i] = timeSpentHardLab[i] / numCompleteHardLab[i];
			}

			avgTimeSpentHome[i] = 0;
			if(numCompleteHome[i] > 0)
			{
				avgTimeSpentHome[i] = timeSpentHome[i] / numCompleteHome[i];
			}
			avgTimeSpentEasyHome[i] = 0;
			if(numCompleteEasyHome[i] > 0)
			{
				avgTimeSpentEasyHome[i] = timeSpentEasyHome[i] / numCompleteEasyHome[i];
			}
			avgTimeSpentMediumHome[i] = 0;
			if(numCompleteMediumHome[i] > 0)
			{
				avgTimeSpentMediumHome[i] = timeSpentMediumHome[i] / numCompleteMediumHome[i];
			}
			avgTimeSpentHardHome[i] = 0;
			if(numCompleteHardHome[i] > 0)
			{
				avgTimeSpentHardHome[i] = timeSpentHardHome[i] / numCompleteHardHome[i];
			}
		}
		reportLine.set5("NumSessionsLab_wk%", numSessionsLab);
		reportLine.set5("NumSessionsHome_wk%", numSessionsHome);

		// lab or home

		reportLine.set5("NumCompleteLab_wk%", numCompleteLab);
		reportLine.set5("NumCompleteLab_wk%_Easy", numCompleteEasyLab);
		reportLine.set5("NumCompleteLab_wk%_Medium", numCompleteMediumLab);
		reportLine.set5("NumCompleteLab_wk%_Hard", numCompleteHardLab);
		reportLine.set5("TimesSubmittedLab_wk%", timesSubmittedLab);
		reportLine.set5("TimesSubmittedLab_wk%_Easy", timesSubmittedEasyLab);
		reportLine.set5("TimesSubmittedLab_wk%_Medium", timesSubmittedMediumLab);
		reportLine.set5("TimesSubmittedLab_wk%_Hard", timesSubmittedHardLab);
//		reportLine.set("NumUnfinished", numIncomplete);
//		reportLine.set("NumUnfinishedEasy", numIncompleteEasy);
//		reportLine.set("NumUnfinishedMedium", numIncompleteMedium);
//		reportLine.set("NumUnfinishedHard", numIncompleteHard);
		reportLine.set5("AvgTimeSpentLab_wk% (s)", avgTimeSpentLab);
		reportLine.set5("AvgTimeSpentLab_wk%_Easy (s)", avgTimeSpentEasyLab);
		reportLine.set5("AvgTimeSpentLab_wk%_Medium (s)", avgTimeSpentMediumLab);
		reportLine.set5("AvgTimeSpentLab_wk%_Hard (s)", avgTimeSpentHardLab);

		reportLine.set5("NumCompleteHome_wk%", numCompleteHome);
		reportLine.set5("NumCompleteHome_wk%_Easy", numCompleteEasyHome);
		reportLine.set5("NumCompleteHome_wk%_Medium", numCompleteMediumHome);
		reportLine.set5("NumCompleteHome_wk%_Hard", numCompleteHardHome);
		reportLine.set5("TimesSubmittedHome_wk%", timesSubmittedHome);
		reportLine.set5("TimesSubmittedHome_wk%_Easy", timesSubmittedEasyHome);
		reportLine.set5("TimesSubmittedHome_wk%_Medium", timesSubmittedMediumHome);
		reportLine.set5("TimesSubmittedHome_wk%_Hard", timesSubmittedHardHome);

		reportLine.set5("AvgTimeSpentHome_wk% (s)", avgTimeSpentHome);
		reportLine.set5("AvgTimeSpentHome_wk%_Easy (s)", avgTimeSpentEasyHome);
		reportLine.set5("AvgTimeSpentHome_wk%_Medium (s)", avgTimeSpentMediumHome);
		reportLine.set5("AvgTimeSpentHome_wk%_Hard (s)", avgTimeSpentHardHome);

		reportLine.set("NumUnfinished", numIncomplete);
		reportLine.set("NumUnfinishedEasy", numIncompleteEasy);
		reportLine.set("NumUnfinishedMedium", numIncompleteMedium);
		reportLine.set("NumUnfinishedHard", numIncompleteHard);

		if (sudokuByIdNoTitle)
		{
			sudokuByIdResults.add(reportLine.getReportTitle());
			sudokuByIdNoTitle = false;
		}

		sudokuByIdResults.add(reportLine.getLine());
	}

	private int getDaysDiff(Date from, Date to)
	{
		long diff = to.getTime() - from.getTime();
		long y = (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
		int x = (int)y; //convert to integer
		return x;
	}

	private void populateUsers(String[][] sessionInfoData_table)
	{
		for (int i = 1; i < sessionInfoData_table.length; i++)
		{
			User user = new User();
			String[] line = sessionInfoData_table[i];
			String userId = line[0];
			user.setId(line[0]);
			Date preLabDate = null;
			try
			{
				preLabDate = createDate1(line[1]);
			} catch (ParseException e1)
			{

			}
			user.setPrelab(preLabDate);
			Date[] weekLab = new Date[5];
			for (int j = 3; j < 8; j++)
			{
				weekLab[j-3] = null;
				try
				{
					weekLab[j-3] = createDate1(line[j]);
				} catch (Exception e)
				{
				}
			}
			user.setWeekslab(weekLab);
			Date postLabDate = null;
			try
			{
				postLabDate = createDate1(line[8]);
			} catch (ParseException e)
			{
			}
			user.setPostlab(postLabDate);

			users.put(userId,user);
		}
	}

	private Date createDate(String date, String format) throws ParseException
	{
		if(date==null)
		{
			return null;
		}
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(date);
		} catch (Exception e)
		{
			return null;
		}
	}

	private Date createDate1(String date) throws ParseException
	//Date must be of format dd-MMM-yyyy
	{
		return createDate(date, "dd-MMM-yyyy");
	}

	private Date createDate2(String date) throws ParseException
	//Date must be of format yyyy-MM-dd
	{
		return createDate(date, "yyyy-MM-dd");
	}

	private Date createDate3(String date) throws ParseException
	//Date must be of format dd/MM/yyyy
	{
		return createDate(date, "dd/MM/yyyy");
	}

	private Date createDate4(String date) throws ParseException
	//Date must be of format "HH:mm:ss"
	{
		return createDate(date, "HH:mm:ss");
	}

	private String[][][] parseFile(File file)
	{
		try{
			//First, get the ID of the user that this file pertains to. The file name is of the format "Report_ID_TDCS-.xls"

			//Now parse the file.
			String[][] raw_table = new String[2000][20]; //will take in ALL the data from the file; all sheets combined into one, per row
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
	        		if(j >= 20)
	        		{
	        			break;
	        		}
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

	        String[][] login_report = new String[sheet3Start-sheet2Start][20];
	        for(int i = 0; i<login_report.length;i++)
	        {
	        	for(int j = 0;j<20;j++)
	        	{
	        		login_report[i][j] = raw_table[i+sheet2Start][j];
	        	}
	        }

	        String[][] sudoku_report = new String[sheet4Start - sheet3Start][20];
	        for(int i = 0; i<sudoku_report.length;i++)
	        {
	        	for(int j = 0;j<sudoku_report[i].length;j++)
	        	{
	        		sudoku_report[i][j] = raw_table[i+sheet3Start][j]; //changed from sheet3 to sheet4
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

	      String[][][] modTable = new String[4][raw_table.length][raw_table[0].length];
	      modTable[0] = avgs_report;
	      modTable[1] = login_report;
	      modTable[2] = sudoku_report;
	      modTable[3] = nback_report;

	      return modTable;
	    }

		catch(Exception e) //If it finds an error, don't continue
		{
			e.printStackTrace();
		}
		return null;
	}

	private void populateSudokuGames(File file, File[] responses,
			String[][] sudokuByIdTable, int index,
			String[][] sessionInfoData_table, String id, String[][][] raw_data) throws ParseException
	{

		List<SudokuGame> games = new ArrayList<SudokuGame>();
		User user = users.get(id);
		if (user == null)
		{
			System.out.println("null user " + id);
			return;
		}
		String[][] sudokuReport = raw_data[2];
		boolean start = false;
		int gameCount = 0;
		for (int i = 1; i < sudokuReport.length; i++)
		{
			String[] line = sudokuReport.clone()[i];
			if(line[0].contains("All Games"))
			{
				start = true;
			}
			if(! start)
			{
				continue;
			}
			gameCount++;
			String gameId = line[1];
			if (gameId == null || !Character.isDigit(gameId.charAt(0)))
				// does not start with a digit, is not an id
			{
				continue;
			}
			SudokuGame game = new SudokuGame();
			game.setGameID(gameId);
			Date dateFinished = createDate2(line[3]);
			game.setDateFinished(dateFinished);
			game.setTimeFinished(createDate2(line[4]));
			game.setLabDay(user.isLabDay(dateFinished));

			int duration = -1;
			try
			{
				duration = Integer.parseInt(line[2]);
			} catch (Exception e)
			{
			}
			game.setDuration(duration);
			game.setDifficulty(line[7]);
			game.setTypeOfGame("sudoku");
			int numWrongSub = -1;
			try
			{
				numWrongSub = Integer.parseInt(line[8]);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			game.setNumWrongSub(numWrongSub);
			game.setWeekNumber(user.getFirstWeek());
			games.add(game);
		}
		user.setSudokuGames(games);
	}



	private void removeLeadingSpaces(File inputFolder, String folder) {
		File dir = new File(folder +"/correctedReports");
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
					FileOutputStream os= new FileOutputStream(folder +"/correctedReports/"+fileName);
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


	// String title[] = { "ID", "Group", "Round", "Age", "First_Lab_Day",
	// "Last_Login", "Pre-Test_Day", "Post-Test_Day",
	// "daysElapsed", "LastDayPlayed_to_PostTestDay",
	// "Pre-Test_to_Post-Test", "Pre-Test_to_FirstLabDay",
	// "NumComplete_wk1",
	// "NumComplete_wk1_Easy",
	/// "NumComplete_wk2_Medium",
	// "NumComplete_wk3_Medium", "NumComplete_wk4_Medium",
	// "NumComplete_wk5_Medium",
	// "NumComplete_wk1_Hard", "NumComplete_wk2_Hard",
	// "NumComplete_wk3_Hard", "NumComplete_wk4_Hard",
	// "NumComplete_wk5_Hard",
	// "NumIncomplete_wk1", "NumIncomplete_wk2",
	// "NumIncomplete_wk3", "NumIncomplete_wk4",
	// "NumIncomplete_wk5",
	// "NumComplete_wk1_Easy", "NumIncomplete_wk2_Easy",
	// "NumIncomplete_wk3_Easy", "NumIncomplete_wk4_Easy",
	// "NumIncomplete_wk5_Easy",
	// "NumIncomplete_wk1_Medium", "NumIncomplete_wk2_Medium",
	// "NumIncomplete_wk3_Medium", "NumIncomplete_wk4_Medium",
	// "NumIncomplete_wk5_Medium",
	// "NumIncomplete_wk1_Hard", "NumIncomplete_wk2_Hard",
	// "NumIncomplete_wk3_Hard", "NumIncomplete_wk4_Hard",
	// "NumIncomplete_wk5_Hard",


	// "TotalTimesSubmittedForCorrection_wk1",
	// // finished but with wrong submission
	// "TotalTimesSubmittedForCorrection_wk2",
	// "TotalTimesSubmittedForCorrection_wk3",
	// "TotalTimesSubmittedForCorrection_wk4",
	// "TotalTimesSubmittedForCorrection_wk5",
	// "AvgTimesSubmittedForCorrection_wk1_Easy",
	// "AvgTimesSubmittedForCorrection_wk2_Easy",
	// "AvgTimesSubmittedForCorrection_wk3_Easy",
	// "AvgTimesSubmittedForCorrection_wk4_Easy",
	// "AvgTimesSubmittedForCorrection_wk5_Easy",
	// "AvgTimesSubmittedForCorrection_wk1_Medium",
	// "AvgTimesSubmittedForCorrection_wk2_Medium",
	// "AvgTimesSubmittedForCorrection_wk3_Medium",
	// "AvgTimesSubmittedForCorrection_wk4_Medium",
	// "AvgTimesSubmittedForCorrection_wk5_Medium",
	// "AvgTimesSubmittedForCorrection_wk1_Hard",
	// "AvgTimesSubmittedForCorrection_wk2_Hard",
	// "AvgTimesSubmittedForCorrection_wk3_Hard",
	// "AvgTimesSubmittedForCorrection_wk4_Hard",
	// "AvgTimesSubmittedForCorrection_wk5_Hard",



	// "AvgTimeSpent_wk1",
	// "AvgTimeSpent_wk2", "AvgTimeSpent_wk3",
	// "AvgTimeSpent_wk4", "AvgTimeSpent_wk5",
	// "AvgTimeSpentEasy_wk1", "AvgTimeSpentEasy_wk2",
	// "AvgTimeSpentEasy_wk3", "AvgTimeSpentEasy_wk4",
	// "AvgTimeSpentEasy_wk5", "AvgTimeSpentMed_wk1",
	// "AvgTimeSpentMed_wk2", "AvgTimeSpentMed_wk3",
	// "AvgTimeSpentMed_wk4", "AvgTimeSpentMed_wk5",
	// "AvgTimeSpentHard_wk1", "AvgTimeSpentHard_wk2",
	// "AvgTimeSpentHard_wk3", "AvgTimeSpentHard_wk4",
	// "AvgTimeSpentHard_wk5",
	// "NumSessions_wk1", "NumSessions_wk2",
	// "NumSessions_wk3", "NumSessions_wk4",
	// "NumSessions_wk5"};

}
