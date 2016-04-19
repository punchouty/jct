package com.vmware.jct.controller;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.jct.common.utility.CommonUtility;
import com.vmware.jct.exception.JCTException;
import com.vmware.jct.service.IReportService;
/**
 * 
 * <p><b>Class name:</b> AllReportsController.java</p>
 * <p><b>Author:</b> InterraIT</p>
 * <p><b>Purpose:</b> This class acts as a controller for merged reports..
 * AllReportsController has following public methods:-
 * -generateExcelAllReport()
 * <p><b>Description:</b> This class is responsible for creating merged reports. </p>
 * <p><b>Copyrights:</b> 	All rights reserved by Interra IT and should only be used for its internal application development.</p>
 * <p><b>Created Date:</b> 13/May/2014</p>
 * <p><b>Revision History:</b>
 * 	<li></li>
 * </p>
 */
@Controller
@RequestMapping(value = "/allReports")
public class AllReportsController {
	
	@Autowired
	private IReportService iReportService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AllReportsController.class);
	
	/**
	 * Method receives the request for generating merged report, generates
	 * and write it to the stream.
	 * @param checkedPreference
	 * @param reportName
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/generateExcelAllReport/{checkedPreference}/{reportName}", method = RequestMethod.GET)
	public void generateExcelAllReport(@PathVariable("checkedPreference") String checkedPreference,
			@PathVariable("reportName") String reportName,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info(">>>> AllReportsController.generateExcelAllReport : checkedPreference : " + checkedPreference + ", reportName : " + reportName);
		String[] hiddenTokens = checkedPreference.split("~");
		try {
			Date today = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
	        String date = dateFormat.format(today);
			//Create Workbook instance for xlsx/xls file input stream
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Merged Report - "+date);
            Row row = sheet.createRow(0);
            sheet.createFreezePane(0,1);
            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
      		Font headerFont = workbook.createFont();
      		headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
      		headerFont.setColor(IndexedColors.WHITE.getIndex());
      		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
      		headerStyle.setFont(headerFont);
      		Integer surveyCount = iReportService.getSurveyQuestionCount();
            generateHeader (row, headerStyle, surveyCount);
            
            //Individual Cell Style
            XSSFCellStyle bodyStyle = (XSSFCellStyle) workbook.createCellStyle();
      		Font bodyFont = workbook.createFont();
      		bodyFont.setColor(IndexedColors.BLACK.getIndex());
      		bodyStyle.setFont(bodyFont);
      		bodyStyle.setWrapText(true);
            
            //lets write the excel data to file now
            //FileOutputStream fos = new FileOutputStream("Merged_Report - "+date+".xlsx");
            int autoSizeCol = 592 + surveyCount + 1000; // 1000 is for backup
            for (int allColIndex = 0; allColIndex < autoSizeCol; allColIndex++) {
            	workbook.getSheetAt(0).autoSizeColumn(allColIndex);
            	workbook.getSheetAt(0).setHorizontallyCenter(true);
            	workbook.getSheetAt(0).setVerticallyCenter(true);
            }
            
            List<Object> infoTable = iReportService.getLoginInfoDetails(null);
            
            List<String> emailIdList = new ArrayList<String>();
            
            for ( int index = 0; index < infoTable.size(); index ++ ){
            	Object[] obj = (Object[]) infoTable.get(index);
            	emailIdList.add((String) obj[0]);
            }
            
            int valueRowCounter = 1;
            for (int emailIdIndex = 0; emailIdIndex<emailIdList.size(); emailIdIndex++) {
            	String emailId = (String) emailIdList.get(emailIdIndex);
            	Row valRow = sheet.createRow(valueRowCounter);
            	valRow.createCell(0).setCellValue(emailId);
            	//valRow.getCell(0).setCellStyle(bodyStyle);
            	
            	List<Object> infoList = iReportService.getLoginInfoDetails(emailId);
            	int dtls = 1;
            	for ( int index = 0; index < infoList.size(); index ++ ){
                	Object[] obj = (Object[]) infoList.get(index);
                	String functionGrp = (String) obj[0];
                	String jobLevel = (String) obj[1];
                	String userGroupName = (String) obj[2];
                	
                	valRow.createCell(dtls).setCellValue(functionGrp);
                	dtls = dtls + 1;
                	
                	valRow.createCell(dtls).setCellValue(jobLevel);
                	dtls = dtls + 1;
                	
                	valRow.createCell(dtls).setCellValue(userGroupName);
                	dtls = dtls + 1;
                	
                	//get group creation date
                	java.sql.Timestamp creationDt = iReportService.getGroupCreationDate(userGroupName);
                	valRow.createCell(dtls).setCellValue(creationDt.toString());
                	dtls = dtls + 1;
                	
                	//Get first Start date and last end date
                	List<Object> dateList =  iReportService.getLoggedIndetails(emailId);
                	for ( int dateIndex = 0; dateIndex < dateList.size(); dateIndex ++ ){
                    	Object[] dateObj = (Object[]) dateList.get(dateIndex);
                    	java.sql.Timestamp minStDt = (java.sql.Timestamp) dateObj[0];
                    	java.sql.Timestamp maxEndDt = (java.sql.Timestamp) dateObj[1];
                    	java.math.BigInteger nosOfTimesLoggedIn = (java.math.BigInteger) dateObj[2];
                    	
                    	valRow.createCell(dtls).setCellValue(minStDt.toString());
                    	dtls = dtls + 1;
                    	
                    	valRow.createCell(dtls).setCellValue(maxEndDt.toString());
                    	dtls = dtls + 1;
                    	
                    	long totalMilis = maxEndDt.getTime() - minStDt.getTime();
                    	String totalTimeInHHMMSS = CommonUtility.convertMillis(totalMilis);
                    	
                    	valRow.createCell(dtls).setCellValue(totalTimeInHHMMSS);
                    	dtls = dtls + 1;
                    	
                    	valRow.createCell(dtls).setCellValue(nosOfTimesLoggedIn.toString());
                    	dtls = dtls + 1;
                    	
                    	//Before Sketch
                    	Integer totalTimeSpentBS = null;
                    	try {
                    		totalTimeSpentBS = iReportService.getTotalTimeSpentOnBs(emailId);	
                    	} catch (Exception ex) {}
                    	
                    	if (null == totalTimeSpentBS) {
                    		valRow.createCell(dtls).setCellValue("");
                    	} else {
                    		valRow.createCell(dtls).setCellValue(CommonUtility.convertSeconds(totalTimeSpentBS));
                        }
                    	dtls = dtls + 1;
                    	BigInteger bsTasks = null;
                    	try{
                    		bsTasks = iReportService.getTotalBsTasks(emailId);
                    	} catch (Exception ex) {}
                    	 
                    	int bsmapping = dtls + 1;
                    	if(null == bsTasks) {
                    		valRow.createCell(bsmapping).setCellValue("");
                    	} else {
                    		valRow.createCell(bsmapping).setCellValue(bsTasks.toString());
                    	}
                    	
                    	List<Object> bsDescEnergy = iReportService.getBSDescriptionAndTimeEnergy(emailId);
                    	bsmapping = bsmapping + 6;
                    	for ( int bsDescindex = 0; bsDescindex < bsDescEnergy.size(); bsDescindex ++ ){
                        	Object[] bsDescObj = (Object[]) bsDescEnergy.get(bsDescindex);
                        	if(bsDescindex == 0) {
                        		valRow.createCell(17).setCellValue((String) bsDescObj[0]); 			// bs description
	                        	valRow.createCell(18).setCellValue(((Integer) 
	                        			bsDescObj[1]).toString()); 									// energy
                        	} else if (bsDescindex == 1){
                        		bsmapping = bsmapping + 12;
                        		valRow.createCell(bsmapping).setCellValue((String) bsDescObj[0]); 	// bs description
                        		bsmapping = bsmapping + 1;
                        		valRow.createCell(bsmapping).setCellValue(((Integer) 
                        				bsDescObj[1]).toString()); 									// energy
                        	} else {
                        		bsmapping = bsmapping + 11;
                        		valRow.createCell(bsmapping).setCellValue((String) bsDescObj[0]); 	// bs description
                        		bsmapping = bsmapping + 1;
                        		valRow.createCell(bsmapping).setCellValue(((Integer) 
                        				bsDescObj[1]).toString()); //energy
                        	}
                    	}
                    	
                    	//After Sketch
                    	Integer totalTimeSpentAS = null;
                    	try {
                    		totalTimeSpentAS = iReportService.getTotalTimeSpentOnAs(emailId);
                    	} catch (Exception ex) {}
                    	 
                    	if (null == totalTimeSpentAS) {
                    		valRow.createCell(dtls).setCellValue("NA");
                    	} else if (totalTimeSpentAS == 0 || null == totalTimeSpentAS) {
                    		valRow.createCell(dtls).setCellValue("NA");
                    	} else {
                    		valRow.createCell(dtls).setCellValue(CommonUtility.convertSeconds(totalTimeSpentAS));
                    	}
                    	dtls = dtls + 1;
                    	
                    	List<Object> asTasksRoleFrameCount = iReportService.getTotalAsTasks(emailId);
                    	int asmapping = dtls + 1;
                    	for (int count = 0; count < asTasksRoleFrameCount.size(); count ++ ) {
                    		Object[] objArr = (Object[]) asTasksRoleFrameCount.get(count);
                    		int asTaskCount = ((java.math.BigInteger) objArr[0]).intValue();
                    		if (asTaskCount == 0) {
                    			valRow.createCell(asmapping).setCellValue("NA");
                    		} else {
                    			valRow.createCell(asmapping).setCellValue(asTaskCount+"");
                    		}
                    	}
                    	
                    	asmapping = asmapping + 1;
                    	List<Object> roleFrameList = iReportService.getRoleFrame(emailId);
                    	if (roleFrameList.size() == 0) {
                    		valRow.createCell(asmapping).setCellValue("NA");
                    		valRow.getCell(asmapping).setCellStyle(bodyStyle);
                    	} else {
                    		valRow.createCell(asmapping).setCellValue(roleFrameList.size()+"");
                    		valRow.getCell(asmapping).setCellStyle(bodyStyle);
                    	}
                    	int roleFrameId = 0;
                    	Map<String, Integer> roleMapping = new HashMap<String, Integer>();
                    	for (int count = 0; count < roleFrameList.size(); count ++ ) {
                    		asmapping = asmapping + 1;
                    		valRow.createCell(asmapping).setCellValue(((String) roleFrameList.get(count)+""));
                    		valRow.getCell(asmapping).setCellStyle(bodyStyle);
                    		roleFrameId = roleFrameId + 1;
                    		roleMapping.put(((String) roleFrameList.get(count)+""), roleFrameId);
                    	}
                    	
                    	if (hiddenTokens[1].equals("C")) {											// after sketch selected
                    		populateASDetails (valRow, emailId, roleMapping);
                        	populateAsToBs (valRow, emailId);
                        	populateASTaskLocation (valRow, emailId);
                        	populateMappings (valRow, emailId, roleMapping);
                        }
                    	if (hiddenTokens[2].equals("C")) { 											// reflection question not selected
                    		populateReflectionQuestions (valRow, emailId, bodyStyle);
                        }
                    	if (hiddenTokens[3].equals("C")) { 											// action plans not selected
                    		populateActionPlan(valRow, emailId, bodyStyle);
                        }
                    	populateSurveyQuestion(valRow, emailId, bodyStyle, surveyCount);
                    	
                	}
                }
            	//increment the row for next record
            	valueRowCounter = valueRowCounter + 1;
            	workbook.getSheetAt(0).autoSizeColumn(emailIdIndex);
            }
            if (hiddenTokens[0].equals("N")) { 														// before sketch not selected
            	hideBeforeSketch (sheet);
            }
            if (hiddenTokens[1].equals("N")) { 														// after sketch not selected
            	hideAfterSketch (sheet);
            } 
            if (hiddenTokens[2].equals("N")) { 														// reflection question not selected
            	hideReflectionQuestions (sheet);
            }
            if (hiddenTokens[3].equals("N")) { 														// action plans not selected
            	hideActionPlans (sheet);
            }
            if (hiddenTokens[4].equals("N")) { 														// action plans not selected
            	hideSurveyQuestion (sheet, surveyCount);
            }
            
            // Justin Change - No job level / function group
            sheet.setColumnHidden(1, true);
            sheet.setColumnHidden(2, true);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Merged_Report - "+date+".xlsx");
            workbook.write(response.getOutputStream());
            response.getOutputStream().close();
		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());
			ex.printStackTrace();
		}
		LOGGER.info("<<<< AllReportsController.generateExcelAllReport");
	}
	
	private void hideSurveyQuestion(Sheet sheet, Integer surveyCount) {
		int endIndex = 593 + surveyCount + surveyCount;
		LOGGER.info(">>>> AllReportsController.hideSurveyQuestion");
		for (int index = 593; index < endIndex; index ++ ) {
			sheet.setColumnHidden(index, true);
		}
		LOGGER.info("<<<< AllReportsController.hideSurveyQuestion");
	}
	/**
	 * Method populates the task location of After sketch
	 * @param valRow
	 * @param emailId
	 * @throws JCTException 
	 */
	private void populateASTaskLocation(Row valRow, String emailId) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateASTaskLocation");
		List<Object> taskLocationList = iReportService.populateASTaskLocation(emailId);
		int taskLocationPlot = 25;
		for (int taskLocationIndex = 0; taskLocationIndex < taskLocationList.size(); taskLocationIndex++) {
			String taskLocation = (String) taskLocationList.get(taskLocationIndex);
			valRow.createCell(taskLocationPlot).setCellValue(taskLocation);
			taskLocationPlot = taskLocationPlot + 12;
		}	
		LOGGER.info("<<<< AllReportsController.populateASTaskLocation");
	}
	/**
	 * Method populates action plan
	 * @param valRow
	 * @param emailId
	 * @param bodyStyle 
	 * @throws JCTException 
	 */
	private void populateActionPlan(Row valRow, String emailId, XSSFCellStyle bodyStyle) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateActionPlan");
		int ansPlotIndex = 0;
		List<Object> actionPlanMainQtnList = iReportService.populateActionPlan(emailId);
		if (actionPlanMainQtnList.size() > 0) {
			int mainQtnPlotIndex = 491;
			for (int mainQtnIndex = 0; mainQtnIndex < actionPlanMainQtnList.size(); mainQtnIndex ++){
				String mainQuestion = (String) actionPlanMainQtnList.get(mainQtnIndex);
				valRow.createCell(mainQtnPlotIndex).setCellValue(mainQuestion); 					// question
				valRow.getCell(mainQtnPlotIndex).setCellStyle(bodyStyle);
				mainQtnPlotIndex = mainQtnPlotIndex + 17;
				//get the sub question and answers of the main question
				List<Object> subQtnAnsList = iReportService.populateSubQtnActionPlan(mainQuestion, emailId);
				int subQtnPlottingIndex = 492;
				int loopCtr = 0;
				for (int subQtnIndex = 0; subQtnIndex < subQtnAnsList.size(); subQtnIndex++) {
					String subQtns = (String) subQtnAnsList.get(subQtnIndex);
					loopCtr = loopCtr + 1;
					if (loopCtr == 5) {
						subQtnPlottingIndex = 509;
					} else if (loopCtr == 9) {
						subQtnPlottingIndex = 526;
					} else if (loopCtr == 13) {
						subQtnPlottingIndex = 543;
					} else if (loopCtr == 17) {
						subQtnPlottingIndex = 560;
					} else if (loopCtr == 21) {
						subQtnPlottingIndex = 577;
					}
					valRow.createCell(subQtnPlottingIndex).setCellValue(subQtns); 					// sub question
					valRow.getCell(subQtnPlottingIndex).setCellStyle(bodyStyle);
					subQtnPlottingIndex = subQtnPlottingIndex + 4;
					//get the answers of sub questions
					List<Object> ansList = iReportService.getActionPlanAnswers(mainQuestion, subQtns, emailId);
					if (ansPlotIndex == 0) {
						ansPlotIndex = 492;
					} else if (ansPlotIndex == 492) {
						ansPlotIndex = 496;
					} else if (ansPlotIndex == 496) {
						ansPlotIndex = 500;
					} else if (ansPlotIndex == 500) {
						ansPlotIndex = 504;
					} else if (ansPlotIndex == 504) {
						ansPlotIndex = 509;
					} else if (ansPlotIndex == 509) {
						ansPlotIndex = 513;
					} else if (ansPlotIndex == 513) {
						ansPlotIndex = 517;
					} else if (ansPlotIndex == 517) {
						ansPlotIndex = 521;
					} else if (ansPlotIndex == 521) {
						ansPlotIndex = 526;
					} else if (ansPlotIndex == 526) {
						ansPlotIndex = 530;
					} else if (ansPlotIndex == 530) {
						ansPlotIndex = 534;
					} else if (ansPlotIndex == 534) {
						ansPlotIndex = 538;
					} else if (ansPlotIndex == 538) {
						ansPlotIndex = 543;
					} else if (ansPlotIndex == 543) {
						ansPlotIndex = 547;
					} else if (ansPlotIndex == 547) {
						ansPlotIndex = 551;
					} else if (ansPlotIndex == 551) {
						ansPlotIndex = 555;
					} else if (ansPlotIndex == 555) {
						ansPlotIndex = 560;
					} else if (ansPlotIndex == 560) {
						ansPlotIndex = 564;
					} else if (ansPlotIndex == 564) {
						ansPlotIndex = 568;
					} else if (ansPlotIndex == 568) {
						ansPlotIndex = 572;
					} else if (ansPlotIndex == 572) {
						ansPlotIndex = 577;
					} else if (ansPlotIndex == 577) {
						ansPlotIndex = 581;
					} else if (ansPlotIndex == 581) {
						ansPlotIndex = 585;
					} else if (ansPlotIndex == 585) {
						ansPlotIndex = 589;
					}
					if (ansList.size() > 0) {
						valRow.createCell(ansPlotIndex+1).setCellValue((String) ansList.get(0)); 			// answer
						valRow.getCell(ansPlotIndex+1).setCellStyle(bodyStyle);
						valRow.createCell(ansPlotIndex+2).setCellValue((String) ansList.get(1)); 		// answer
						valRow.getCell(ansPlotIndex+2).setCellStyle(bodyStyle);
						valRow.createCell(ansPlotIndex+3).setCellValue((String) ansList.get(2)); 		// answer
						valRow.getCell(ansPlotIndex+3).setCellStyle(bodyStyle);
					}
				}
			}
		}
		LOGGER.info("<<<< AllReportsController.populateActionPlan");
	}
	/**
	 * Method hides the after sketch panel
	 * @param sheet
	 */
	private void hideAfterSketch (Sheet sheet) {
		LOGGER.info(">>>> AllReportsController.hideAfterSketch");
		sheet.setColumnHidden(10, true);
		hideCols(12, 16, sheet);
		hideCols(19, 28, sheet);
		hideCols(31, 40, sheet);
		hideCols(43, 52, sheet);
		hideCols(55, 64, sheet);
		hideCols(67, 76, sheet);
		hideCols(79, 88, sheet);
		hideCols(91, 100, sheet);
		hideCols(103, 112, sheet);
		hideCols(115, 124, sheet);
		hideCols(127, 136, sheet);
		hideCols(139, 148, sheet);
		hideCols(151, 160, sheet);
		hideCols(163, 172, sheet);
		hideCols(175, 184, sheet);
		hideCols(187, 196, sheet);
		hideCols(199, 208, sheet);
		hideCols(211, 220, sheet);
		hideCols(223, 232, sheet);
		hideCols(235, 244, sheet);
		hideCols(247, 436, sheet);
		LOGGER.info("<<<< AllReportsController.hideAfterSketch");
	}
	private void hideCols (int startIndex, int endIndex, Sheet sheet) {
		LOGGER.info(">>>> AllReportsController.hideCols");
		for (int index = startIndex; index <= endIndex; index ++ ) {
			sheet.setColumnHidden(index, true);
		}
		LOGGER.info("<<<< AllReportsController.hideCols");
	}
	/**
	 * Method populates reflection questions
	 * @param valRow
	 * @param emailId
	 * @param bodyStyle 
	 * @throws JCTException 
	 */
	private void populateReflectionQuestions(Row valRow, String emailId, XSSFCellStyle bodyStyle) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateReflectionQuestions");
		int ansPlotIndex = 439;
		int subQtnPlottingIndex = 438;
		int loopCtr = 0;
		List<Object> questionnaireMainQtnList = iReportService.getReflectionQuestions(emailId);
		if (questionnaireMainQtnList.size() > 0) {
			int mainQtnPlotIndex = 437;
			for (int mainQtnIndex = 0; mainQtnIndex < questionnaireMainQtnList.size(); mainQtnIndex ++){
				String mainQuestion = (String) questionnaireMainQtnList.get(mainQtnIndex);
				valRow.createCell(mainQtnPlotIndex).setCellValue(mainQuestion); 					// question
				valRow.getCell(mainQtnPlotIndex).setCellStyle(bodyStyle);
				mainQtnPlotIndex = mainQtnPlotIndex + 9;
				
				//get the sub question and answers of the main question
				List<Object> subQtnAnsList = iReportService.populateSubQtnQuestionnaire(mainQuestion, emailId);
				for (int subQtnIndex = 0; subQtnIndex < subQtnAnsList.size(); subQtnIndex++) {
					String subQtns = (String) subQtnAnsList.get(subQtnIndex);
					String subQtnVal = subQtns;
					if (subQtns.trim().equals("NA")) {
						subQtnVal = "";
					}
					if (loopCtr == 1) {
						subQtnPlottingIndex = 447;
					} else if (loopCtr == 2) {
						subQtnPlottingIndex = 456;
					} else if (loopCtr == 3) {
						subQtnPlottingIndex = 465;
					} else if (loopCtr == 4) {
						subQtnPlottingIndex = 474;
					} else if (loopCtr == 5) {
						subQtnPlottingIndex = 483;
					}
					valRow.createCell(subQtnPlottingIndex).setCellValue(subQtnVal); 					// sub question
					valRow.getCell(subQtnPlottingIndex).setCellStyle(bodyStyle);
					subQtnPlottingIndex = subQtnPlottingIndex + 2;
					
					//get the answers of sub questions
					List<Object> ansList = iReportService.getQuestionnaireAnswers(mainQuestion, subQtns, emailId);
					if (loopCtr == 1) {
						ansPlotIndex = 448;
					} else if (loopCtr == 2) {
						ansPlotIndex = 457;
					} else if (loopCtr == 3) {
						ansPlotIndex = 466;
					} else if (loopCtr == 4) {
						ansPlotIndex = 475;
					} else if (loopCtr == 5) {
						ansPlotIndex = 484;
					}
					
					valRow.createCell(ansPlotIndex).setCellValue((String) ansList.get(0)); 			// answer
					valRow.getCell(ansPlotIndex).setCellStyle(bodyStyle);
					ansPlotIndex = ansPlotIndex + 2;
				}
				loopCtr = loopCtr + 1;
			}
		}
		LOGGER.info("<<<< AllReportsController.populateReflectionQuestions");
	}
	/**
	 * Method populates the excel header
	 * @param row
	 * @param headerStyle 
	 */
	private void generateHeader(Row row, XSSFCellStyle headerStyle, Integer surveyCount) {
		LOGGER.info(">>>> AllReportsController.generateHeader");
		 row.createCell(0).setCellValue("Email Id");
		 row.createCell(1).setCellValue("Function Group");
         row.createCell(2).setCellValue("Job Level");
         row.createCell(3).setCellValue("User Group Profile");
         row.createCell(4).setCellValue("User Group Profile Creation Date");
         row.createCell(5).setCellValue("First Start date/time");
         row.createCell(6).setCellValue("Last End date/time");
         row.createCell(7).setCellValue("Total Time (HH:MM:SS)");
         row.createCell(8).setCellValue("Number of times logged");
         row.createCell(9).setCellValue("Time Spent on Before Sketch");
         row.createCell(10).setCellValue("Time Spent on After Diagram");
         row.createCell(11).setCellValue("Before - Total Tasks");
         row.createCell(12).setCellValue("After - Total Tasks");
         row.createCell(13).setCellValue("Total Role Frames");
         row.createCell(14).setCellValue("Role Frame 1                        ");         
         row.createCell(15).setCellValue("Role Frame 2                        ");
         row.createCell(16).setCellValue("Role Frame 3                        ");
         row.createCell(17).setCellValue("Task 1 - Before Description");
         row.createCell(18).setCellValue("Task 1 - Before Time / Energy");
         row.createCell(19).setCellValue("Task 1 - After Description_Main");
         row.createCell(20).setCellValue("Task 1 - After Description_Person");
         row.createCell(21).setCellValue("Task 1 - After Time / Energy");
         row.createCell(22).setCellValue("Task 1 - Percent from Before to After");
         row.createCell(23).setCellValue("Task 1 - Status from Before to After");
         row.createCell(24).setCellValue("Task 1 - Description Edit from Before to After");
         row.createCell(25).setCellValue("Task 1 - After Location");
         row.createCell(26).setCellValue("Task 1 - Role Frame 1");
         row.createCell(27).setCellValue("Task 1 - Role Frame 2");
         row.createCell(28).setCellValue("Task 1 - Role Frame 3");
         row.createCell(29).setCellValue("Task 2 - Before Description");
         row.createCell(30).setCellValue("Task 2 - Before Time / Energy");
         row.createCell(31).setCellValue("Task 2 - After Description_Main");
         row.createCell(32).setCellValue("Task 2 - After Description_Person");
         row.createCell(33).setCellValue("Task 2 - After Time / Energy");
         row.createCell(34).setCellValue("Task 2 - Percent from Before to After");
         row.createCell(35).setCellValue("Task 2 - Status from Before to After");
         row.createCell(36).setCellValue("Task 2 - Description Edit from Before to After");
         row.createCell(37).setCellValue("Task 2 - After Location");
         row.createCell(38).setCellValue("Task 2 - Role Frame 1");
         row.createCell(39).setCellValue("Task 2 - Role Frame 2");
         row.createCell(40).setCellValue("Task 2 - Role Frame 3");
         row.createCell(41).setCellValue("Task 3 - Before Description");
         row.createCell(42).setCellValue("Task 3 - Before Time / Energy");
         row.createCell(43).setCellValue("Task 3 - After Description_Main");
         row.createCell(44).setCellValue("Task 3 - After Description_Person");
         row.createCell(45).setCellValue("Task 3 - After Time / Energy");
         row.createCell(46).setCellValue("Task 3 - Percent from Before to After");
         row.createCell(47).setCellValue("Task 3 - Status from Before to After");
         row.createCell(48).setCellValue("Task 3 - Description Edit from Before to After");
         row.createCell(49).setCellValue("Task 3 - After Location");
         row.createCell(50).setCellValue("Task 3 - Role Frame 1");
         row.createCell(51).setCellValue("Task 3 - Role Frame 2");
         row.createCell(52).setCellValue("Task 3 - Role Frame 3");
         row.createCell(53).setCellValue("Task 4 - Before Description");
         row.createCell(54).setCellValue("Task 4 - Before Time / Energy");
         row.createCell(55).setCellValue("Task 4 - After Description_Main");
         row.createCell(56).setCellValue("Task 4 - After Description_Person");
         row.createCell(57).setCellValue("Task 4 - After Time / Energy");
         row.createCell(58).setCellValue("Task 4 - Percent from Before to After");
         row.createCell(59).setCellValue("Task 4 - Status from Before to After");
         row.createCell(60).setCellValue("Task 4 - Description Edit from Before to After");
         row.createCell(61).setCellValue("Task 4 - After Location");
         row.createCell(62).setCellValue("Task 4 - Role Frame 1");
         row.createCell(63).setCellValue("Task 4 - Role Frame 2");
         row.createCell(64).setCellValue("Task 4 - Role Frame 3");
         row.createCell(65).setCellValue("Task 5 - Before Description");
         row.createCell(66).setCellValue("Task 5 - Before Time / Energy");
         row.createCell(67).setCellValue("Task 5 - After Description_Main");
         row.createCell(68).setCellValue("Task 5 - After Description_Person");
         row.createCell(69).setCellValue("Task 5 - After Time / Energy");
         row.createCell(70).setCellValue("Task 5 - Percent from Before to After");
         row.createCell(71).setCellValue("Task 5 - Status from Before to After");
         row.createCell(72).setCellValue("Task 5 - Description Edit from Before to After");
         row.createCell(73).setCellValue("Task 5 - After Location");
         row.createCell(74).setCellValue("Task 5 - Role Frame 1");
         row.createCell(75).setCellValue("Task 5 - Role Frame 2");
         row.createCell(76).setCellValue("Task 5 - Role Frame 3");
         row.createCell(77).setCellValue("Task 6 - Before Description");
         row.createCell(78).setCellValue("Task 6 - Before Time / Energy");
         row.createCell(79).setCellValue("Task 6 - After Description_Main");
         row.createCell(80).setCellValue("Task 6 - After Description_Person");
         row.createCell(81).setCellValue("Task 6 - After Time / Energy");
         row.createCell(82).setCellValue("Task 6 - Percent from Before to After");
         row.createCell(83).setCellValue("Task 6 - Status from Before to After");
         row.createCell(84).setCellValue("Task 6 - Description Edit from Before to After");
         row.createCell(85).setCellValue("Task 6 - After Location");
         row.createCell(86).setCellValue("Task 6 - Role Frame 1");
         row.createCell(87).setCellValue("Task 6 - Role Frame 2");
         row.createCell(88).setCellValue("Task 6 - Role Frame 3");
         row.createCell(89).setCellValue("Task 7 - Before Description");
         row.createCell(90).setCellValue("Task 7 - Before Time / Energy");
         row.createCell(91).setCellValue("Task 7 - After Description_Main");
         row.createCell(92).setCellValue("Task 7 - After Description_Person");
         row.createCell(93).setCellValue("Task 7 - After Time / Energy");
         row.createCell(94).setCellValue("Task 7 - Percent from Before to After");
         row.createCell(95).setCellValue("Task 7 - Status from Before to After");
         row.createCell(96).setCellValue("Task 7 - Description Edit from Before to After");
         row.createCell(97).setCellValue("Task 7 - After Location");
         row.createCell(98).setCellValue("Task 7 - Role Frame 1");
         row.createCell(99).setCellValue("Task 7 - Role Frame 2");
         row.createCell(100).setCellValue("Task 7 - Role Frame 3");
         row.createCell(101).setCellValue("Task 8 - Before Description");
         row.createCell(102).setCellValue("Task 8 - Before Time / Energy");
         row.createCell(103).setCellValue("Task 8 - After Description_Main");
         row.createCell(104).setCellValue("Task 8 - After Description_Person");
         row.createCell(105).setCellValue("Task 8 - After Time / Energy");
         row.createCell(106).setCellValue("Task 8 - Percent from Before to After");
         row.createCell(107).setCellValue("Task 8 - Status from Before to After");
         row.createCell(108).setCellValue("Task 8 - Description Edit from Before to After");
         row.createCell(109).setCellValue("Task 8 - After Location");
         row.createCell(110).setCellValue("Task 8 - Role Frame 1");
         row.createCell(111).setCellValue("Task 8 - Role Frame 2");
         row.createCell(112).setCellValue("Task 8 - Role Frame 3");
         row.createCell(113).setCellValue("Task 9 - Before Description");
         row.createCell(114).setCellValue("Task 9 - Before Time / Energy");
         row.createCell(115).setCellValue("Task 9 - After Description_Main");
         row.createCell(116).setCellValue("Task 9 - After Description_Person");
         row.createCell(117).setCellValue("Task 9 - After Time / Energy");
         row.createCell(118).setCellValue("Task 9 - Percent from Before to After");
         row.createCell(119).setCellValue("Task 9 - Status from Before to After");
         row.createCell(120).setCellValue("Task 9 - Description Edit from Before to After");
         row.createCell(121).setCellValue("Task 9 - After Location");
         row.createCell(122).setCellValue("Task 9 - Role Frame 1");
         row.createCell(123).setCellValue("Task 9 - Role Frame 2");
         row.createCell(124).setCellValue("Task 9 - Role Frame 3");
         row.createCell(125).setCellValue("Task 10 - Before Description");
         row.createCell(126).setCellValue("Task 10 - Before Time / Energy");
         row.createCell(127).setCellValue("Task 10 - After Description_Main");
         row.createCell(128).setCellValue("Task 10 - After Description_Person");
         row.createCell(129).setCellValue("Task 10 - After Time / Energy");
         row.createCell(130).setCellValue("Task 10 - Percent from Before to After");
         row.createCell(131).setCellValue("Task 10 - Status from Before to After");
         row.createCell(132).setCellValue("Task 10 - Description Edit from Before to After");
         row.createCell(133).setCellValue("Task 10 - After Location");
         row.createCell(134).setCellValue("Task 10 - Role Frame 1");
         row.createCell(135).setCellValue("Task 10 - Role Frame 2");
         row.createCell(136).setCellValue("Task 10 - Role Frame 3");
         row.createCell(137).setCellValue("Task 11 - Before Description");
         row.createCell(138).setCellValue("Task 11 - Before Time / Energy");
         row.createCell(139).setCellValue("Task 11 - After Description_Main");
         row.createCell(140).setCellValue("Task 11 - After Description_Person");
         row.createCell(141).setCellValue("Task 11 - After Time / Energy");
         row.createCell(142).setCellValue("Task 11 - Percent from Before to After");
         row.createCell(143).setCellValue("Task 11 - Status from Before to After");
         row.createCell(144).setCellValue("Task 11 - Description Edit from Before to After");
         row.createCell(145).setCellValue("Task 11 - After Location");
         row.createCell(146).setCellValue("Task 11 - Role Frame 1");
         row.createCell(147).setCellValue("Task 11 - Role Frame 2");
         row.createCell(148).setCellValue("Task 11 - Role Frame 3");
         row.createCell(149).setCellValue("Task 12 - Before Description");
         row.createCell(150).setCellValue("Task 12 - Before Time / Energy");
         row.createCell(151).setCellValue("Task 12 - After Description_Main");
         row.createCell(152).setCellValue("Task 12 - After Description_Person");
         row.createCell(153).setCellValue("Task 12 - After Time / Energy");
         row.createCell(154).setCellValue("Task 12 - Percent from Before to After");
         row.createCell(155).setCellValue("Task 12 - Status from Before to After");
         row.createCell(156).setCellValue("Task 12 - Description Edit from Before to After");
         row.createCell(157).setCellValue("Task 12 - After Location");
         row.createCell(158).setCellValue("Task 12 - Role Frame 1");
         row.createCell(159).setCellValue("Task 12 - Role Frame 2");
         row.createCell(160).setCellValue("Task 12 - Role Frame 3");
         row.createCell(161).setCellValue("Task 13 - Before Description");
         row.createCell(162).setCellValue("Task 13 - Before Time / Energy");
         row.createCell(163).setCellValue("Task 13 - After Description_Main");
         row.createCell(164).setCellValue("Task 13 - After Description_Person");
         row.createCell(165).setCellValue("Task 13 - After Time / Energy");
         row.createCell(166).setCellValue("Task 13 - Percent from Before to After");
         row.createCell(167).setCellValue("Task 13 - Status from Before to After");
         row.createCell(168).setCellValue("Task 13 - Description Edit from Before to After");
         row.createCell(169).setCellValue("Task 13 - After Location");
         row.createCell(170).setCellValue("Task 13 - Role Frame 1");
         row.createCell(171).setCellValue("Task 13 - Role Frame 2");
         row.createCell(172).setCellValue("Task 13 - Role Frame 3");		
         row.createCell(173).setCellValue("Task 14 - Before Description");
         row.createCell(174).setCellValue("Task 14 - Before Time / Energy");
         row.createCell(175).setCellValue("Task 14 - After Description_Main");
         row.createCell(176).setCellValue("Task 14 - After Description_Person");
         row.createCell(177).setCellValue("Task 14 - After Time / Energy");
         row.createCell(178).setCellValue("Task 14 - Percent from Before to After");
         row.createCell(179).setCellValue("Task 14 - Status from Before to After");
         row.createCell(180).setCellValue("Task 14 - Description Edit from Before to After");
         row.createCell(181).setCellValue("Task 14 - After Location");
         row.createCell(182).setCellValue("Task 14 - Role Frame 1");
         row.createCell(183).setCellValue("Task 14 - Role Frame 2");
         row.createCell(184).setCellValue("Task 14 - Role Frame 3");		
         row.createCell(185).setCellValue("Task 15 - Before Description");
         row.createCell(186).setCellValue("Task 15 - Before Time / Energy");
         row.createCell(187).setCellValue("Task 15 - After Description_Main");
         row.createCell(188).setCellValue("Task 15 - After Description_Person");
         row.createCell(189).setCellValue("Task 15 - After Time / Energy");
         row.createCell(190).setCellValue("Task 15 - Percent from Before to After");
         row.createCell(191).setCellValue("Task 15 - Status from Before to After");
         row.createCell(192).setCellValue("Task 15 - Description Edit from Before to After");
         row.createCell(193).setCellValue("Task 15 - After Location");
         row.createCell(194).setCellValue("Task 15 - Role Frame 1");
         row.createCell(195).setCellValue("Task 15 - Role Frame 2");
         row.createCell(196).setCellValue("Task 15 - Role Frame 3");
         row.createCell(197).setCellValue("Task 16 - Before Description");
         row.createCell(198).setCellValue("Task 16 - Before Time / Energy");
         row.createCell(199).setCellValue("Task 16 - After Description_Main");
         row.createCell(200).setCellValue("Task 16 - After Description_Person");
         row.createCell(201).setCellValue("Task 16 - After Time / Energy");
         row.createCell(202).setCellValue("Task 16 - Percent from Before to After");
         row.createCell(203).setCellValue("Task 16 - Status from Before to After");
         row.createCell(204).setCellValue("Task 16 - Description Edit from Before to After");
         row.createCell(205).setCellValue("Task 16 - After Location");
         row.createCell(206).setCellValue("Task 16 - Role Frame 1");
         row.createCell(207).setCellValue("Task 16 - Role Frame 2");
         row.createCell(208).setCellValue("Task 16 - Role Frame 3");	
         row.createCell(209).setCellValue("Task 17 - Before Description");
         row.createCell(210).setCellValue("Task 17 - Before Time / Energy");
         row.createCell(211).setCellValue("Task 17 - After Description_Main");
         row.createCell(212).setCellValue("Task 17 - After Description_Person");
         row.createCell(213).setCellValue("Task 17 - After Time / Energy");
         row.createCell(214).setCellValue("Task 17 - Percent from Before to After");
         row.createCell(215).setCellValue("Task 17 - Status from Before to After");
         row.createCell(216).setCellValue("Task 17 - Description Edit from Before to After");
         row.createCell(217).setCellValue("Task 17 - After Location");
         row.createCell(218).setCellValue("Task 17 - Role Frame 1");
         row.createCell(219).setCellValue("Task 17 - Role Frame 2");
         row.createCell(220).setCellValue("Task 17 - Role Frame 3");	
         row.createCell(221).setCellValue("Task 18 - Before Description");
         row.createCell(222).setCellValue("Task 18 - Before Time / Energy");
         row.createCell(223).setCellValue("Task 18 - After Description_Main");
         row.createCell(224).setCellValue("Task 18 - After Description_Person");
         row.createCell(225).setCellValue("Task 18 - After Time / Energy");
         row.createCell(226).setCellValue("Task 18 - Percent from Before to After");
         row.createCell(227).setCellValue("Task 18 - Status from Before to After");
         row.createCell(228).setCellValue("Task 18 - Description Edit from Before to After");
         row.createCell(229).setCellValue("Task 18 - After Location");
         row.createCell(230).setCellValue("Task 18 - Role Frame 1");
         row.createCell(231).setCellValue("Task 18 - Role Frame 2");
         row.createCell(232).setCellValue("Task 18 - Role Frame 3");
         row.createCell(233).setCellValue("Task 19 - Before Description");
         row.createCell(234).setCellValue("Task 19 - Before Time / Energy");
         row.createCell(235).setCellValue("Task 19 - After Description_Main");
         row.createCell(236).setCellValue("Task 19 - After Description_Person");
         row.createCell(237).setCellValue("Task 19 - After Time / Energy");
         row.createCell(238).setCellValue("Task 19 - Percent from Before to After");
         row.createCell(239).setCellValue("Task 19 - Status from Before to After");
         row.createCell(240).setCellValue("Task 19 - Description Edit from Before to After");
         row.createCell(241).setCellValue("Task 19 - After Location");
         row.createCell(242).setCellValue("Task 19 - Role Frame 1");
         row.createCell(243).setCellValue("Task 19 - Role Frame 2");
         row.createCell(244).setCellValue("Task 19 - Role Frame 3");
         row.createCell(245).setCellValue("Task 20 - Before Description");
         row.createCell(246).setCellValue("Task 20 - Before Time / Energy");
         row.createCell(247).setCellValue("Task 20 - After Description_Main");
         row.createCell(248).setCellValue("Task 20 - After Description_Person");
         row.createCell(249).setCellValue("Task 20 - After Time / Energy");
         row.createCell(250).setCellValue("Task 20 - Percent from Before to After");
         row.createCell(251).setCellValue("Task 20 - Status from Before to After");
         row.createCell(252).setCellValue("Task 20 - Description Edit from Before to After");
         row.createCell(253).setCellValue("Task 20 - After Location");
         row.createCell(254).setCellValue("Task 20 - Role Frame 1");
         row.createCell(255).setCellValue("Task 20 - Role Frame 2");
         row.createCell(256).setCellValue("Task 20 - Role Frame 3");
         row.createCell(257).setCellValue("Strength 1a - Description");
         row.createCell(258).setCellValue("Strength 1a - Location");
         row.createCell(259).setCellValue("Strength 1a - Role Frame 1");
         row.createCell(260).setCellValue("Strength 1a - Role Frame 2");
         row.createCell(261).setCellValue("Strength 1a - Role Frame 3");
         row.createCell(262).setCellValue("Strength 1b - Description");
         row.createCell(263).setCellValue("Strength 1b - Location");
         row.createCell(264).setCellValue("Strength 1b - Role Frame 1");
         row.createCell(265).setCellValue("Strength 1b - Role Frame 2");
         row.createCell(266).setCellValue("Strength 1b - Role Frame 3");
         row.createCell(267).setCellValue("Strength 1c - Description");
         row.createCell(268).setCellValue("Strength 1c - Location");
         row.createCell(269).setCellValue("Strength 1c - Role Frame 1");
         row.createCell(270).setCellValue("Strength 1c - Role Frame 2");
         row.createCell(271).setCellValue("Strength 1c - Role Frame 3");
         row.createCell(272).setCellValue("Strength 2a - Description");
         row.createCell(273).setCellValue("Strength 2a - Location");
         row.createCell(274).setCellValue("Strength 2a - Role Frame 1");
         row.createCell(275).setCellValue("Strength 2a - Role Frame 2");
         row.createCell(276).setCellValue("Strength 2a - Role Frame 3");
         row.createCell(277).setCellValue("Strength 2b - Description");
         row.createCell(278).setCellValue("Strength 2b - Location");
         row.createCell(279).setCellValue("Strength 2b - Role Frame 1");
         row.createCell(280).setCellValue("Strength 2b - Role Frame 2");
         row.createCell(281).setCellValue("Strength 2b - Role Frame 3");
         row.createCell(282).setCellValue("Strength 2c - Description");
         row.createCell(283).setCellValue("Strength 2c - Location");
         row.createCell(284).setCellValue("Strength 2c - Role Frame 1");
         row.createCell(285).setCellValue("Strength 2c - Role Frame 2");
         row.createCell(286).setCellValue("Strength 2c - Role Frame 3");
         row.createCell(287).setCellValue("Strength 3a - Description");
         row.createCell(288).setCellValue("Strength 3a - Location");
         row.createCell(289).setCellValue("Strength 3a - Role Frame 1");
         row.createCell(290).setCellValue("Strength 3a - Role Frame 2");
         row.createCell(291).setCellValue("Strength 3a - Role Frame 3");
         row.createCell(292).setCellValue("Strength 3b - Description");
         row.createCell(293).setCellValue("Strength 3b - Location");
         row.createCell(294).setCellValue("Strength 3b - Role Frame 1");
         row.createCell(295).setCellValue("Strength 3b - Role Frame 2");
         row.createCell(296).setCellValue("Strength 3b - Role Frame 3");
         row.createCell(297).setCellValue("Strength 3c - Description");
         row.createCell(298).setCellValue("Strength 3c - Location");
         row.createCell(299).setCellValue("Strength 3c - Role Frame 1");
         row.createCell(300).setCellValue("Strength 3c - Role Frame 2");
         row.createCell(301).setCellValue("Strength 3c - Role Frame 3");
         row.createCell(302).setCellValue("Strength 4a - Description");
         row.createCell(303).setCellValue("Strength 4a - Location");
         row.createCell(304).setCellValue("Strength 4a - Role Frame 1");
         row.createCell(305).setCellValue("Strength 4a - Role Frame 2");
         row.createCell(306).setCellValue("Strength 4a - Role Frame 3");
         row.createCell(307).setCellValue("Strength 4b - Description");
         row.createCell(308).setCellValue("Strength 4b - Location");
         row.createCell(309).setCellValue("Strength 4b - Role Frame 1");
         row.createCell(310).setCellValue("Strength 4b - Role Frame 2");
         row.createCell(311).setCellValue("Strength 4b - Role Frame 3");
         row.createCell(312).setCellValue("Strength 4c - Description");
         row.createCell(313).setCellValue("Strength 4c - Location");
         row.createCell(314).setCellValue("Strength 4c - Role Frame 1");
         row.createCell(315).setCellValue("Strength 4c - Role Frame 2");
         row.createCell(316).setCellValue("Strength 4c - Role Frame 3");
         row.createCell(317).setCellValue("Value 1a - Description");
         row.createCell(318).setCellValue("Value 1a - Location");
         row.createCell(319).setCellValue("Value 1a - Role Frame 1");
         row.createCell(320).setCellValue("Value 1a - Role Frame 2");
         row.createCell(321).setCellValue("Value 1a - Role Frame 3");
         row.createCell(322).setCellValue("Value 1b - Description");
         row.createCell(323).setCellValue("Value 1b - Location");
         row.createCell(324).setCellValue("Value 1b - Role Frame 1");
         row.createCell(325).setCellValue("Value 1b - Role Frame 2");
         row.createCell(326).setCellValue("Value 1b - Role Frame 3");
         row.createCell(327).setCellValue("Value 1c - Description");
         row.createCell(328).setCellValue("Value 1c - Location");
         row.createCell(329).setCellValue("Value 1c - Role Frame 1");
         row.createCell(330).setCellValue("Value 1c - Role Frame 2");
         row.createCell(331).setCellValue("Value 1c - Role Frame 3");
         row.createCell(332).setCellValue("Value 2a - Description");
         row.createCell(333).setCellValue("Value 2a - Location");
         row.createCell(334).setCellValue("Value 2a - Role Frame 1");
         row.createCell(335).setCellValue("Value 2a - Role Frame 2");
         row.createCell(336).setCellValue("Value 2a - Role Frame 3");
         row.createCell(337).setCellValue("Value 2b - Description");
         row.createCell(338).setCellValue("Value 2b - Location");
         row.createCell(339).setCellValue("Value 2b - Role Frame 1");
         row.createCell(340).setCellValue("Value 2b - Role Frame 2");
         row.createCell(341).setCellValue("Value 2b - Role Frame 3");
         row.createCell(342).setCellValue("Value 2c - Description");
         row.createCell(343).setCellValue("Value 2c - Location");
         row.createCell(344).setCellValue("Value 2c - Role Frame 1");
         row.createCell(345).setCellValue("Value 2c - Role Frame 2");
         row.createCell(346).setCellValue("Value 2c - Role Frame 3");
         row.createCell(347).setCellValue("Value 3a - Description");
         row.createCell(348).setCellValue("Value 3a - Location");
         row.createCell(349).setCellValue("Value 3a - Role Frame 1");
         row.createCell(350).setCellValue("Value 3a - Role Frame 2");
         row.createCell(351).setCellValue("Value 3a - Role Frame 3");
         row.createCell(352).setCellValue("Value 3b - Description");
         row.createCell(353).setCellValue("Value 3b - Location");
         row.createCell(354).setCellValue("Value 3b - Role Frame 1");
         row.createCell(355).setCellValue("Value 3b - Role Frame 2");
         row.createCell(356).setCellValue("Value 3b - Role Frame 3");
         row.createCell(357).setCellValue("Value 3c - Description");
         row.createCell(358).setCellValue("Value 3c - Location");
         row.createCell(359).setCellValue("Value 3c - Role Frame 1");
         row.createCell(360).setCellValue("Value 3c - Role Frame 2");
         row.createCell(361).setCellValue("Value 3c - Role Frame 3");
         row.createCell(362).setCellValue("Value 4a - Description");
         row.createCell(363).setCellValue("Value 4a - Location");
         row.createCell(364).setCellValue("Value 4a - Role Frame 1");
         row.createCell(365).setCellValue("Value 4a - Role Frame 2");
         row.createCell(366).setCellValue("Value 4a - Role Frame 3");
         row.createCell(367).setCellValue("Value 4b - Description");
         row.createCell(368).setCellValue("Value 4b - Location");
         row.createCell(369).setCellValue("Value 4b - Role Frame 1");
         row.createCell(370).setCellValue("Value 4b - Role Frame 2");
         row.createCell(371).setCellValue("Value 4b - Role Frame 3");
         row.createCell(372).setCellValue("Value 4c - Description");
         row.createCell(373).setCellValue("Value 4c - Location");
         row.createCell(374).setCellValue("Value 4c - Role Frame 1");
         row.createCell(375).setCellValue("Value 4c - Role Frame 2");
         row.createCell(376).setCellValue("Value 4c - Role Frame 3");
         row.createCell(377).setCellValue("Passion 1a - Description");
         row.createCell(378).setCellValue("Passion 1a - Location");
         row.createCell(379).setCellValue("Passion 1a - Role Frame 1");
         row.createCell(380).setCellValue("Passion 1a - Role Frame 2");
         row.createCell(381).setCellValue("Passion 1a - Role Frame 3");
         row.createCell(382).setCellValue("Passion 1b - Description");
         row.createCell(383).setCellValue("Passion 1b - Location");
         row.createCell(384).setCellValue("Passion 1b - Role Frame 1");
         row.createCell(385).setCellValue("Passion 1b - Role Frame 2");
         row.createCell(386).setCellValue("Passion 1b - Role Frame 3");
         row.createCell(387).setCellValue("Passion 1c - Description");
         row.createCell(388).setCellValue("Passion 1c - Location");
         row.createCell(389).setCellValue("Passion 1c - Role Frame 1");
         row.createCell(390).setCellValue("Passion 1c - Role Frame 2");
         row.createCell(391).setCellValue("Passion 1c - Role Frame 3");
         row.createCell(392).setCellValue("Passion 2a - Description");
         row.createCell(393).setCellValue("Passion 2a - Location");
         row.createCell(394).setCellValue("Passion 2a - Role Frame 1");
         row.createCell(395).setCellValue("Passion 2a - Role Frame 2");
         row.createCell(396).setCellValue("Passion 2a - Role Frame 3");
         row.createCell(397).setCellValue("Passion 2b - Description");
         row.createCell(398).setCellValue("Passion 2b - Location");
         row.createCell(399).setCellValue("Passion 2b - Role Frame 1");
         row.createCell(400).setCellValue("Passion 2b - Role Frame 2");
         row.createCell(401).setCellValue("Passion 2b - Role Frame 3");
         row.createCell(402).setCellValue("Passion 2c - Description");
         row.createCell(403).setCellValue("Passion 2c - Location");
         row.createCell(404).setCellValue("Passion 2c - Role Frame 1");
         row.createCell(405).setCellValue("Passion 2c - Role Frame 2");
         row.createCell(406).setCellValue("Passion 2c - Role Frame 3");
         row.createCell(407).setCellValue("Passion 3a - Description");
         row.createCell(408).setCellValue("Passion 3a - Location");
         row.createCell(409).setCellValue("Passion 3a - Role Frame 1");
         row.createCell(410).setCellValue("Passion 3a - Role Frame 2");
         row.createCell(411).setCellValue("Passion 3a - Role Frame 3");
         row.createCell(412).setCellValue("Passion 3b - Description");
         row.createCell(413).setCellValue("Passion 3b - Location");
         row.createCell(414).setCellValue("Passion 3b - Role Frame 1");
         row.createCell(415).setCellValue("Passion 3b - Role Frame 2");
         row.createCell(416).setCellValue("Passion 3b - Role Frame 3");
         row.createCell(417).setCellValue("Passion 3c - Description");
         row.createCell(418).setCellValue("Passion 3c - Location");
         row.createCell(419).setCellValue("Passion 3c - Role Frame 1");
         row.createCell(420).setCellValue("Passion 3c - Role Frame 2");
         row.createCell(421).setCellValue("Passion 3c - Role Frame 3");
         row.createCell(422).setCellValue("Passion 4a - Description");
         row.createCell(423).setCellValue("Passion 4a - Location");
         row.createCell(424).setCellValue("Passion 4a - Role Frame 1");
         row.createCell(425).setCellValue("Passion 4a - Role Frame 2");
         row.createCell(426).setCellValue("Passion 4a - Role Frame 3");
         row.createCell(427).setCellValue("Passion 4b - Description");
         row.createCell(428).setCellValue("Passion 4b - Location");
         row.createCell(429).setCellValue("Passion 4b - Role Frame 1");
         row.createCell(430).setCellValue("Passion 4b - Role Frame 2");
         row.createCell(431).setCellValue("Passion 4b - Role Frame 3");
         row.createCell(432).setCellValue("Passion 4c - Description");
         row.createCell(433).setCellValue("Passion 4c - Location");
         row.createCell(434).setCellValue("Passion 4c - Role Frame 1");
         row.createCell(435).setCellValue("Passion 4c - Role Frame 2");
         row.createCell(436).setCellValue("Passion 4c - Role Frame 3");
 		
 		 /*//Reflection question
         row.createCell(437).setCellValue("Before Reflection - Question 1                                                                                      ");
         row.createCell(438).setCellValue("Before Reflection - Answer 1                                                                                      ");
         row.createCell(439).setCellValue("Before Reflection - Question 2                                                                                      ");
         row.createCell(440).setCellValue("Before Reflection - Answer 2                                                                                      ");
         row.createCell(441).setCellValue("Before Reflection - Question 3                                                                                      ");
         row.createCell(442).setCellValue("Before Reflection - Answer 3                                                                                      ");
         row.createCell(443).setCellValue("Before Reflection - Question 4                                                                                      ");
         row.createCell(444).setCellValue("Before Reflection - Answer 4                                                                                      ");
         row.createCell(445).setCellValue("Before Reflection - Question 5                                                                                      ");
         row.createCell(446).setCellValue("Before Reflection - Answer 5                                                                                      ");
         row.createCell(447).setCellValue("Before Reflection - Question 6                                                                                      ");
         row.createCell(448).setCellValue("Before Reflection - Answer 6                                                                                      ");*/
         row.createCell(437).setCellValue("Before Reflection - Question 1                                                                                      ");
         row.createCell(438).setCellValue("Before Reflection - Sub Question 1a                                                                                      ");
         row.createCell(439).setCellValue("Before Reflection - Answer 1a                                                                                      ");
         row.createCell(440).setCellValue("Before Reflection - Sub Question 1b                                                                                      ");
         row.createCell(441).setCellValue("Before Reflection - Answer 1b                                                                                      ");
         row.createCell(442).setCellValue("Before Reflection - Sub Question 1c                                                                                      ");
         row.createCell(443).setCellValue("Before Reflection - Answer 1c                                                                                      ");
         row.createCell(444).setCellValue("Before Reflection - Sub Question 1d                                                                                      ");
         row.createCell(445).setCellValue("Before Reflection - Answer 1d                                                                                      ");
         row.createCell(446).setCellValue("Before Reflection - Question 2                                                                                      ");
         row.createCell(447).setCellValue("Before Reflection - Sub Question 2a                                                                                      ");
         row.createCell(448).setCellValue("Before Reflection - Answer 2a                                                                                      ");
         row.createCell(449).setCellValue("Before Reflection - Sub Question 2b                                                                                      ");
         row.createCell(450).setCellValue("Before Reflection - Answer 2b                                                                                      ");
         row.createCell(451).setCellValue("Before Reflection - Sub Question 2c                                                                                      ");
         row.createCell(452).setCellValue("Before Reflection - Answer 2c                                                                                      ");
         row.createCell(453).setCellValue("Before Reflection - Sub Question 2d                                                                                      ");
         row.createCell(454).setCellValue("Before Reflection - Answer 2d                                                                                      ");
         row.createCell(455).setCellValue("Before Reflection - Question 3                                                                                      ");
         row.createCell(456).setCellValue("Before Reflection - Sub Question 3a                                                                                      ");
         row.createCell(457).setCellValue("Before Reflection - Answer 3a                                                                                      ");
         row.createCell(458).setCellValue("Before Reflection - Sub Question 3b                                                                                      ");
         row.createCell(459).setCellValue("Before Reflection - Answer 3b                                                                                      ");
         row.createCell(460).setCellValue("Before Reflection - Sub Question 3c                                                                                      ");
         row.createCell(461).setCellValue("Before Reflection - Answer 3c                                                                                      ");
         row.createCell(462).setCellValue("Before Reflection - Sub Question 3d                                                                                      ");
         row.createCell(463).setCellValue("Before Reflection - Answer 3d                                                                                      ");
         row.createCell(464).setCellValue("Before Reflection - Question 4                                                                                      ");
         row.createCell(465).setCellValue("Before Reflection - Sub Question 4a                                                                                      ");
         row.createCell(466).setCellValue("Before Reflection - Answer 4a                                                                                      ");
         row.createCell(467).setCellValue("Before Reflection - Sub Question 4b                                                                                      ");
         row.createCell(468).setCellValue("Before Reflection - Answer 4b                                                                                      ");
         row.createCell(469).setCellValue("Before Reflection - Sub Question 4c                                                                                      ");
         row.createCell(470).setCellValue("Before Reflection - Answer 4c                                                                                      ");
         row.createCell(471).setCellValue("Before Reflection - Sub Question 4d                                                                                      ");
         row.createCell(472).setCellValue("Before Reflection - Answer 4d                                                                                      ");
         row.createCell(473).setCellValue("Before Reflection - Question 5                                                                                      ");
         row.createCell(474).setCellValue("Before Reflection - Sub Question 5a                                                                                      ");
         row.createCell(475).setCellValue("Before Reflection - Answer 5a                                                                                      ");
         row.createCell(476).setCellValue("Before Reflection - Sub Question 5b                                                                                      ");
         row.createCell(477).setCellValue("Before Reflection - Answer 5b                                                                                      ");
         row.createCell(478).setCellValue("Before Reflection - Sub Question 5c                                                                                      ");
         row.createCell(479).setCellValue("Before Reflection - Answer 5c                                                                                      ");
         row.createCell(480).setCellValue("Before Reflection - Sub Question 5d                                                                                      ");
         row.createCell(481).setCellValue("Before Reflection - Answer 5d                                                                                      ");
         row.createCell(482).setCellValue("Before Reflection - Question 6                                                                                      ");
         row.createCell(483).setCellValue("Before Reflection - Sub Question 6a                                                                                      ");
         row.createCell(484).setCellValue("Before Reflection - Answer 6a                                                                                      ");
         row.createCell(485).setCellValue("Before Reflection - Sub Question 6b                                                                                      ");
         row.createCell(486).setCellValue("Before Reflection - Answer 6b                                                                                      ");
         row.createCell(487).setCellValue("Before Reflection - Sub Question 6c                                                                                      ");
         row.createCell(488).setCellValue("Before Reflection - Answer 6c                                                                                      ");
         row.createCell(489).setCellValue("Before Reflection - Sub Question 6d                                                                                      ");
         row.createCell(490).setCellValue("Before Reflection - Answer 6d                                                                                      ");
         
         //Action Plan
         row.createCell(491).setCellValue("After Action - Question 1                                                                                      ");
         row.createCell(492).setCellValue("After Action - Sub Question 1a                                                                ");
         row.createCell(493).setCellValue("After Action - Answer 1aa                                                                                      ");
         row.createCell(494).setCellValue("After Action - Answer 1ab                                                                                      ");
         row.createCell(495).setCellValue("After Action - Answer 1ac                                                                                      ");
         row.createCell(496).setCellValue("After Action - Sub Question 1b                                                                ");
         row.createCell(497).setCellValue("After Action - Answer 1ba                                                                                      ");
         row.createCell(498).setCellValue("After Action - Answer 1bb                                                                                      ");
         row.createCell(499).setCellValue("After Action - Answer 1bc                                                                                      ");
         row.createCell(500).setCellValue("After Action - Sub Question 1c                                                                ");
         row.createCell(501).setCellValue("After Action - Answer 1ca                                                                                      ");
         row.createCell(502).setCellValue("After Action - Answer 1cb                                                                                      ");
         row.createCell(503).setCellValue("After Action - Answer 1cc                                                                                      ");
         row.createCell(504).setCellValue("After Action - Sub Question 1d                                                                ");
         row.createCell(505).setCellValue("After Action - Answer 1da                                                                                      ");
         row.createCell(506).setCellValue("After Action - Answer 1db                                                                                      ");
         row.createCell(507).setCellValue("After Action - Answer 1dc                                                                                      ");
         row.createCell(508).setCellValue("After Action - Question 2                                                                                      ");
         row.createCell(509).setCellValue("After Action - Sub Question 2a                                                                ");
         row.createCell(510).setCellValue("After Action - Answer 2aa                                                                                      ");
         row.createCell(511).setCellValue("After Action - Answer 2ab                                                                                      ");
         row.createCell(512).setCellValue("After Action - Answer 2ac                                                                                      ");
         row.createCell(513).setCellValue("After Action - Sub Question 2b                                                                ");
         row.createCell(514).setCellValue("After Action - Answer 2ba                                                                                      ");
         row.createCell(515).setCellValue("After Action - Answer 2bb                                                                                      ");
         row.createCell(516).setCellValue("After Action - Answer 2bc                                                                                      ");
         row.createCell(517).setCellValue("After Action - Sub Question 2c                                                                ");
         row.createCell(518).setCellValue("After Action - Answer 2ca                                                                                      ");
         row.createCell(519).setCellValue("After Action - Answer 2cb                                                                                      ");
         row.createCell(520).setCellValue("After Action - Answer 2cc                                                                                      ");
         row.createCell(521).setCellValue("After Action - Sub Question 2d                                                                ");
         row.createCell(522).setCellValue("After Action - Answer 2da                                                                                      ");
         row.createCell(523).setCellValue("After Action - Answer 2db                                                                                      ");
         row.createCell(524).setCellValue("After Action - Answer 2dc                                                                                      ");
         row.createCell(525).setCellValue("After Action - Question 3                                                                                      ");
         row.createCell(526).setCellValue("After Action - Sub Question 3a                                                                ");
         row.createCell(527).setCellValue("After Action - Answer 3aa                                                                                      ");
         row.createCell(528).setCellValue("After Action - Answer 3ab                                                                                      ");
         row.createCell(529).setCellValue("After Action - Answer 3ac                                                                                      ");
         row.createCell(530).setCellValue("After Action - Sub Question 3b                                                                ");
         row.createCell(531).setCellValue("After Action - Answer 3ba                                                                                      ");
         row.createCell(532).setCellValue("After Action - Answer 3bb                                                                                      ");
         row.createCell(533).setCellValue("After Action - Answer 3bc                                                                                      ");
         row.createCell(534).setCellValue("After Action - Sub Question 3c                                                                ");
         row.createCell(535).setCellValue("After Action - Answer 3ca                                                                                      ");
         row.createCell(536).setCellValue("After Action - Answer 3cb                                                                                      ");
         row.createCell(537).setCellValue("After Action - Answer 3cc                                                                                      ");
         row.createCell(538).setCellValue("After Action - Sub Question 3d                                                                ");
         row.createCell(539).setCellValue("After Action - Answer 3da                                                                                      ");
         row.createCell(540).setCellValue("After Action - Answer 3db                                                                                      ");
         row.createCell(541).setCellValue("After Action - Answer 3dc                                                                                      ");
         row.createCell(542).setCellValue("After Action - Question 4                                                                                      ");
         row.createCell(543).setCellValue("After Action - Sub Question 4a                                                                ");
         row.createCell(544).setCellValue("After Action - Answer 4aa                                                                                      ");
         row.createCell(545).setCellValue("After Action - Answer 4ab                                                                                      ");
         row.createCell(546).setCellValue("After Action - Answer 4ac                                                                                      ");
         row.createCell(547).setCellValue("After Action - Sub Question 4b                                                                ");
         row.createCell(548).setCellValue("After Action - Answer 4ba                                                                                      ");
         row.createCell(549).setCellValue("After Action - Answer 4bb                                                                                      ");
         row.createCell(550).setCellValue("After Action - Answer 4bc                                                                                      ");
         row.createCell(551).setCellValue("After Action - Sub Question 4c                                                                ");
         row.createCell(552).setCellValue("After Action - Answer 4ca                                                                                      ");
         row.createCell(553).setCellValue("After Action - Answer 4cb                                                                                      ");
         row.createCell(554).setCellValue("After Action - Answer 4cc                                                                                      ");
         row.createCell(555).setCellValue("After Action - Sub Question 4d                                                                ");
         row.createCell(556).setCellValue("After Action - Answer 4da                                                                                      ");
         row.createCell(557).setCellValue("After Action - Answer 4db                                                                                      ");
         row.createCell(558).setCellValue("After Action - Answer 4dc                                                                                      ");
         row.createCell(559).setCellValue("After Action - Question 5                                                                                      ");
         row.createCell(560).setCellValue("After Action - Sub Question 5a                                                                ");
         row.createCell(561).setCellValue("After Action - Answer 5aa                                                                                      ");
         row.createCell(562).setCellValue("After Action - Answer 5ab                                                                                      ");
         row.createCell(563).setCellValue("After Action - Answer 5ac                                                                                      ");
         row.createCell(564).setCellValue("After Action - Sub Question 5b                                                                ");
         row.createCell(565).setCellValue("After Action - Answer 5ba                                                                                      ");
         row.createCell(566).setCellValue("After Action - Answer 5bb                                                                                      ");
         row.createCell(567).setCellValue("After Action - Answer 5bc                                                                                      ");
         row.createCell(568).setCellValue("After Action - Sub Question 5c                                                                ");
         row.createCell(569).setCellValue("After Action - Answer 5ca                                                                                      ");
         row.createCell(570).setCellValue("After Action - Answer 5cb                                                                                      ");
         row.createCell(571).setCellValue("After Action - Answer 5cc                                                                                      ");
         row.createCell(572).setCellValue("After Action - Sub Question 5d                                                                ");
         row.createCell(573).setCellValue("After Action - Answer 5da                                                                                      ");
         row.createCell(574).setCellValue("After Action - Answer 5db                                                                                      ");
         row.createCell(575).setCellValue("After Action - Answer 5dc                                                                                      ");
         row.createCell(576).setCellValue("After Action - Question 6                                                                                      ");
         row.createCell(577).setCellValue("After Action - Sub Question 6a                                                                ");
         row.createCell(578).setCellValue("After Action - Answer 6aa                                                                                      ");
         row.createCell(579).setCellValue("After Action - Answer 6ab                                                                                      ");
         row.createCell(580).setCellValue("After Action - Answer 6ac                                                                                      ");
         row.createCell(581).setCellValue("After Action - Sub Question 6b                                                                ");
         row.createCell(582).setCellValue("After Action - Answer 6ba                                                                                      ");
         row.createCell(583).setCellValue("After Action - Answer 6bb                                                                                      ");
         row.createCell(584).setCellValue("After Action - Answer 6bc                                                                                      ");
         row.createCell(585).setCellValue("After Action - Sub Question 6c                                                                ");
         row.createCell(586).setCellValue("After Action - Answer 6ca                                                                                      ");
         row.createCell(587).setCellValue("After Action - Answer 6cb                                                                                      ");
         row.createCell(588).setCellValue("After Action - Answer 6cc                                                                                      ");
         row.createCell(589).setCellValue("After Action - Sub Question 6d                                                                ");
         row.createCell(590).setCellValue("After Action - Answer 6da                                                                                      ");
         row.createCell(591).setCellValue("After Action - Answer 6db                                                                                      ");
         row.createCell(592).setCellValue("After Action - Answer 6dc                                                                                      ");
         
         
         // Get maximum number of survey questions answered by individual user
        int surveyColNameCounter = 1;
		int totalColumnCount = 592;
		for (int index=0; index<surveyCount; index++) {
			totalColumnCount = totalColumnCount + 1;
			row.createCell(totalColumnCount).setCellValue("Survey Question: "+surveyColNameCounter+"                                                                       ");
			totalColumnCount = totalColumnCount + 1;
			row.createCell(totalColumnCount).setCellValue("Survey Answer: "+surveyColNameCounter+"                                                                       ");
			surveyColNameCounter = surveyColNameCounter + 1;
		}
		//Apply the header style
         for (int headerIndex = 0; headerIndex <= totalColumnCount; headerIndex++) {
        	 row.getCell(headerIndex).setCellStyle(headerStyle);
         }
         /*//Apply the header style
         for (int headerIndex = 0; headerIndex <= 592; headerIndex++) {
        	 row.getCell(headerIndex).setCellStyle(headerStyle);
         }*/
         LOGGER.info("<<<< AllReportsController.generateHeader");
	}
	/**
	 * Method populates the After sketch details.
	 * @param valRow
	 * @param emailId
	 * @param roleMapping 
	 * @throws JCTException
	 */
	private void populateASDetails(Row valRow, String emailId, Map<String, Integer> roleMapping) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateASDetails");
		List<Object> asDescAddTEList = iReportService.getASDescriptionMainPersonAndTimeEnergy(emailId);
		int check = 43;
    	for (int asCount = 0; asCount < asDescAddTEList.size(); asCount++ ) {
    		Object[] individualObj = (Object[]) asDescAddTEList.get(asCount);
    		if (asCount == 0) {
    			//Get different roles for the task descriptions
    			String taskDesc = (String) individualObj[0];
    			List<String> roleList = iReportService.getRolesForTasks(emailId, taskDesc);
    			
    			valRow.createCell(19).setCellValue((String) individualObj[0]); 						//as main description
    			valRow.createCell(20).setCellValue((String) individualObj[1]); 						//as user description
            	valRow.createCell(21).setCellValue(((Integer) individualObj[2]).toString()); 		//energy
            	
            	if (roleList.size() == 1) {
            		valRow.createCell(26).setCellValue(roleList.get(0).toString()); 
            	} else if (roleList.size() == 2) {
            		valRow.createCell(26).setCellValue(roleList.get(0).toString());
            		valRow.createCell(27).setCellValue(roleList.get(1).toString()); 
            	} else {
            		valRow.createCell(26).setCellValue(roleList.get(0).toString());
            		valRow.createCell(27).setCellValue(roleList.get(1).toString());
            		valRow.createCell(28).setCellValue(roleList.get(2).toString()); 
            	}
            	
            	/*String role = (String) individualObj[3];
            	int roleMapIndx = roleMapping.get(role);
            	if (roleMapIndx == 1) {
            		valRow.createCell(26).setCellValue(role); 
            	} else if (roleMapIndx == 2) {
            		valRow.createCell(27).setCellValue(role); 
            	} else if (roleMapIndx == 3) {
            		valRow.createCell(28).setCellValue(role); 
            	}*/            	
    		} else if (asCount == 1) {
    			//Get different roles for the task descriptions
    			String taskDesc = (String) individualObj[0];
    			List<String> roleList = iReportService.getRolesForTasks(emailId, taskDesc);
    			
    			valRow.createCell(31).setCellValue((String) individualObj[0]); 						//as main description
    			valRow.createCell(32).setCellValue((String) individualObj[1]); 						//as user description
            	valRow.createCell(33).setCellValue(((Integer) individualObj[2]).toString()); 		//energy
            	/*String role = (String) individualObj[3];
            	int roleMapIndx = roleMapping.get(role);
            	if (roleMapIndx == 1) {
            		valRow.createCell(38).setCellValue(role); 
            	} else if (roleMapIndx == 2) {
            		valRow.createCell(39).setCellValue(role); 
            	} else if (roleMapIndx == 3) {
            		valRow.createCell(40).setCellValue(role); 
            	}*/
            	if (roleList.size() == 1) {
            		valRow.createCell(38).setCellValue(roleList.get(0).toString()); 
            	} else if (roleList.size() == 2) {
            		valRow.createCell(38).setCellValue(roleList.get(0).toString());
            		valRow.createCell(39).setCellValue(roleList.get(1).toString()); 
            	} else {
            		valRow.createCell(38).setCellValue(roleList.get(0).toString());
            		valRow.createCell(39).setCellValue(roleList.get(1).toString());
            		valRow.createCell(40).setCellValue(roleList.get(2).toString()); 
            	}
    		} else {
    			//Get different roles for the task descriptions
    			String taskDesc = (String) individualObj[0];
    			List<String> roleList = iReportService.getRolesForTasks(emailId, taskDesc);
    			
    			valRow.createCell(check).setCellValue((String) individualObj[0]); 					//as main description
    			check = check + 1;
    			valRow.createCell(check).setCellValue((String) individualObj[1]); 					//as user description
    			check = check + 1;
            	valRow.createCell(check).setCellValue(((Integer) individualObj[2]).toString()); 	//energy
            	check = check + 10;
            	int rlind = check - 10;
            	/*String role = (String) individualObj[3];
            	int roleMapIndx = roleMapping.get(role);
            	if (roleMapIndx == 1) {
            		valRow.createCell(rlind+5).setCellValue(role); 
            	} else if (roleMapIndx == 2) {
            		valRow.createCell(rlind+6).setCellValue(role); 
            	} else if (roleMapIndx == 3) {
            		valRow.createCell(rlind+7).setCellValue(role); 
            	}*/
            	if (roleList.size() == 1) {
            		valRow.createCell(rlind+5).setCellValue(roleList.get(0).toString()); 
            	} else if (roleList.size() == 2) {
            		valRow.createCell(rlind+5).setCellValue(roleList.get(0).toString());
            		valRow.createCell(rlind+6).setCellValue(roleList.get(1).toString()); 
            	} else {
            		valRow.createCell(rlind+5).setCellValue(roleList.get(0).toString());
            		valRow.createCell(rlind+6).setCellValue(roleList.get(1).toString());
            		valRow.createCell(rlind+7).setCellValue(roleList.get(2).toString()); 
            	}
            	
    		}
    	}
    	LOGGER.info("<<<< AllReportsController.populateASDetails");
	}
	/**
	 * Method populates the before sketch to after sketch changes
	 * @param valRow
	 * @param emailId
	 * @throws JCTException
	 */
	private void populateAsToBs(Row valRow, String emailId) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateAsToBs");
		List<Object> bsToAsList = iReportService.getBsToAs(emailId);
    	int bsPlot = 46;
    	for (int bsToAsIndex = 0; bsToAsIndex < bsToAsList.size(); bsToAsIndex++ ) {
    		Object[] bstoasObj = (Object[]) bsToAsList.get(bsToAsIndex);
    		if (bsToAsIndex == 0) {
    			valRow.createCell(22).setCellValue((String) bstoasObj[0]); 							// diff in energy
    			valRow.createCell(23).setCellValue((String) bstoasObj[1]); 							// diff status
            	valRow.createCell(24).setCellValue((String) bstoasObj[2]); 							// edited text desc
    		} else if (bsToAsIndex == 1) {
    			valRow.createCell(34).setCellValue((String) bstoasObj[0]); 							// diff in energy
    			valRow.createCell(35).setCellValue((String) bstoasObj[1]); 							// diff status
            	valRow.createCell(36).setCellValue((String) bstoasObj[2]); 							// edited text desc
    		} else {
    			valRow.createCell(bsPlot).setCellValue((String) bstoasObj[0]); 						// diff in energy
    			bsPlot = bsPlot + 1;
    			valRow.createCell(bsPlot).setCellValue((String) bstoasObj[1]); 						// diff status
    			bsPlot = bsPlot + 1;
            	valRow.createCell(bsPlot).setCellValue((String) bstoasObj[2]); 						// edited text desc
            	bsPlot = bsPlot + 10;
    		}
    	}
    	LOGGER.info("<<<< AllReportsController.populateAsToBs");
	}
	/**
	 * Method populate only the strengh value and passion elements.
	 * @param valRow
	 * @param emailId
	 * @param roleMapping
	 * @throws JCTException
	 */
	private void populateMappings(Row valRow, String emailId,
			Map<String, Integer> roleMapping) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateMappings");
		List<Object> strValPassList = iReportService.getStrValPassItems(emailId);
		int strengthStartIndex = 257;
		int strengthLocationIndex = 258;
		int strengthLastPlottedIndex = 0;
		String strengthMemory = "";

		int valueStartIndex = 317;
		int valueLocationIndex = 318;
		int valueLastPlottedIndex = 0;
		String valueMemory = "";

		int passionStartIndex = 377;
		int passionLocationIndex = 378;
		int passionLastPlottedIndex = 0;
		String passionMemory = "";
		// jct_as_element_code, jct_as_element_desc, jct_as_role_desc, jct_as_position
		for (int elementIndex = 0; elementIndex < strValPassList.size(); elementIndex++) {
			Object[] elementRow = (Object[]) strValPassList.get(elementIndex);
			String elementCode = (String) elementRow[0];											// jct_as_element_code
			if (elementCode.equals("STR")) {
				String strength = (String) elementRow[1];											// jct_as_element_desc
				if (strengthMemory.equals("") || strengthMemory.equals(strength)) {
					String roleDesc = (String) elementRow[2];										// jct_as_role_desc
					String elementLocation = (String) elementRow[3];	
					int roleMapIndex = roleMapping.get(roleDesc);// jct_as_position
					if (strengthMemory.equals("")) {
						valRow.createCell(strengthStartIndex).setCellValue((String) elementRow[1]);
						strengthStartIndex = strengthStartIndex + 2;
						if (roleMapIndex == 1) {
							valRow.createCell(strengthStartIndex).setCellValue(roleDesc); 				// role desc
							valRow.createCell(strengthLocationIndex).setCellValue(elementLocation); 	// strength location
						} else if (roleMapIndex == 2) {
							valRow.createCell(strengthStartIndex + 1).setCellValue(roleDesc); 			// role desc
							valRow.createCell(strengthLocationIndex).setCellValue(elementLocation); 	// strength location
						} else {
							valRow.createCell(strengthStartIndex + 2).setCellValue(roleDesc); 			// role desc
							valRow.createCell(strengthLocationIndex).setCellValue(elementLocation); 	// strength location
						}
						strengthStartIndex = strengthStartIndex + 3;
						strengthLastPlottedIndex = strengthStartIndex - 3;
						strengthMemory = strength;
						strengthLocationIndex = strengthLocationIndex + 5;
					} else {
						strengthStartIndex = strengthStartIndex - 3;
						strengthLastPlottedIndex = strengthStartIndex + 3;
						strengthMemory = strength;
						strengthLocationIndex = strengthLocationIndex - 5;
						if (roleMapIndex == 1) {
							valRow.createCell(strengthStartIndex).setCellValue(roleDesc); 				// role desc
						} else if (roleMapIndex == 2) {
							valRow.createCell(strengthStartIndex + 1).setCellValue(roleDesc); 			// role desc
						} else {
							valRow.createCell(strengthStartIndex + 2).setCellValue(roleDesc); 			// role desc
						}
						strengthStartIndex = strengthStartIndex + 3;
						strengthLastPlottedIndex = strengthStartIndex - 3;
						strengthMemory = strength;
						strengthLocationIndex = strengthLocationIndex + 5;
					}
					
				} else {
					//For location
					if (strengthLastPlottedIndex == 259
							|| strengthLastPlottedIndex == 260
							|| strengthLastPlottedIndex == 261
							|| strengthLastPlottedIndex == 264
							|| strengthLastPlottedIndex == 265
							|| strengthLastPlottedIndex == 266
							|| strengthLastPlottedIndex == 269
							|| strengthLastPlottedIndex == 270
							|| strengthLastPlottedIndex == 271) {
						strengthStartIndex = 272;
					} else if (strengthLastPlottedIndex == 274
							|| strengthLastPlottedIndex == 275
							|| strengthLastPlottedIndex == 276
							|| strengthLastPlottedIndex == 279
							|| strengthLastPlottedIndex == 280
							|| strengthLastPlottedIndex == 281
							|| strengthLastPlottedIndex == 284
							|| strengthLastPlottedIndex == 285
							|| strengthLastPlottedIndex == 286) {
						strengthStartIndex = 287;
					} else if (strengthLastPlottedIndex == 289
							|| strengthLastPlottedIndex == 290
							|| strengthLastPlottedIndex == 291
							|| strengthLastPlottedIndex == 294
							|| strengthLastPlottedIndex == 295
							|| strengthLastPlottedIndex == 296
							|| strengthLastPlottedIndex == 299
							|| strengthLastPlottedIndex == 300
							|| strengthLastPlottedIndex == 301) {
						strengthStartIndex = 302;
					}

					valRow.createCell(strengthStartIndex).setCellValue((String) elementRow[1]); 	// strength desc
					strengthStartIndex = strengthStartIndex + 2;
					String roleDesc = (String) elementRow[2];
					String elementLocation = (String) elementRow[3];
					int roleMapIndex = roleMapping.get(roleDesc);
					if (roleMapIndex == 1) {
						valRow.createCell(strengthStartIndex).setCellValue(roleDesc); 				// role desc
						valRow.createCell(strengthStartIndex - 1).setCellValue(elementLocation); 	// strength location
					} else if (roleMapIndex == 2) {
						valRow.createCell(strengthStartIndex + 1).setCellValue(roleDesc); 			// role desc
						valRow.createCell(strengthStartIndex - 1).setCellValue(elementLocation); 	// strength location
					} else {
						valRow.createCell(strengthStartIndex + 2).setCellValue(roleDesc); 			// role desc
						valRow.createCell(strengthStartIndex - 1).setCellValue(elementLocation); 	// strength location
					}
					
					strengthStartIndex = strengthStartIndex + 3;
					strengthLastPlottedIndex = strengthStartIndex - 3;
					strengthMemory = strength;
					strengthLocationIndex = strengthLocationIndex + 5;
				}

				/*strengthStartIndex = strengthStartIndex + 3;
				strengthLastPlottedIndex = strengthStartIndex - 3;
				strengthMemory = strength;
				strengthLocationIndex = strengthLocationIndex + 5;*/
			} else if (elementCode.equals("VAL")) {
				String value = (String) elementRow[1];
				if (valueMemory.equals("") || valueMemory.equals(value)) {
					String roleDesc = (String) elementRow[2];
					String elementLocation = (String) elementRow[3];
					int roleMapIndex = roleMapping.get(roleDesc);
					if (valueMemory.equals("")) {
						valRow.createCell(valueStartIndex).setCellValue((String) elementRow[1]); 		// value desc
						valueStartIndex = valueStartIndex + 2;
						if (roleMapIndex == 1) {
							valRow.createCell(valueStartIndex).setCellValue(roleDesc); 					// role desc
							valRow.createCell(valueLocationIndex).setCellValue(elementLocation); 		// value location
						} else if (roleMapIndex == 2) {
							valRow.createCell(valueStartIndex + 1).setCellValue(roleDesc); 				// role desc
							valRow.createCell(valueLocationIndex).setCellValue(elementLocation); 		// value location
						} else {
							valRow.createCell(valueStartIndex + 2).setCellValue(roleDesc); 				// role desc
							valRow.createCell(valueLocationIndex).setCellValue(elementLocation); 		// value location
						}
						valueStartIndex = valueStartIndex + 3;
						valueLastPlottedIndex = valueStartIndex - 3;
						valueMemory = value;
						valueLocationIndex = valueLocationIndex + 5;
					} else {
						valueStartIndex = valueStartIndex - 3;
						valueLastPlottedIndex = valueStartIndex + 3;
						valueMemory = value;
						valueLocationIndex = valueLocationIndex - 5;
						if (roleMapIndex == 1) {
							valRow.createCell(valueStartIndex).setCellValue(roleDesc); 				// role desc
						} else if (roleMapIndex == 2) {
							valRow.createCell(valueStartIndex + 1).setCellValue(roleDesc); 			// role desc
						} else {
							valRow.createCell(valueStartIndex + 2).setCellValue(roleDesc); 			// role desc
						}
						valueStartIndex = valueStartIndex + 3;
						valueLastPlottedIndex = valueStartIndex - 3;
						valueMemory = value;
						valueLocationIndex = valueLocationIndex + 5;
					}
				} else {
					if (valueLastPlottedIndex == 319
							|| valueLastPlottedIndex == 320
							|| valueLastPlottedIndex == 321
							|| valueLastPlottedIndex == 324
							|| valueLastPlottedIndex == 325
							|| valueLastPlottedIndex == 326
							|| valueLastPlottedIndex == 329
							|| valueLastPlottedIndex == 330
							|| valueLastPlottedIndex == 331) { // plotted
						valueStartIndex = 332;
					} else if (valueLastPlottedIndex == 334
							|| valueLastPlottedIndex == 335
							|| valueLastPlottedIndex == 336
							|| valueLastPlottedIndex == 339
							|| valueLastPlottedIndex == 340
							|| valueLastPlottedIndex == 341
							|| valueLastPlottedIndex == 344
							|| valueLastPlottedIndex == 345
							|| valueLastPlottedIndex == 346) {
						valueStartIndex = 347;
					} else if (valueLastPlottedIndex == 349
							|| valueLastPlottedIndex == 350
							|| valueLastPlottedIndex == 351
							|| valueLastPlottedIndex == 354
							|| valueLastPlottedIndex == 355
							|| valueLastPlottedIndex == 356
							|| valueLastPlottedIndex == 359
							|| valueLastPlottedIndex == 360
							|| valueLastPlottedIndex == 361) {
						valueStartIndex = 362;
					}

					valRow.createCell(valueStartIndex).setCellValue(
							(String) elementRow[1]); // value desc
					valueStartIndex = valueStartIndex + 2;
					String roleDesc = (String) elementRow[2];
					String elementLocation = (String) elementRow[3];
					int roleMapIndex = roleMapping.get(roleDesc);
					if (roleMapIndex == 1) {
						valRow.createCell(valueStartIndex).setCellValue(roleDesc); 					// role desc
						valRow.createCell(valueStartIndex - 1).setCellValue(elementLocation); 		// value location
					} else if (roleMapIndex == 2) {
						valRow.createCell(valueStartIndex + 1).setCellValue(roleDesc); 				// role desc
						valRow.createCell(valueStartIndex - 1).setCellValue(elementLocation); 		// value location
					} else {
						valRow.createCell(valueStartIndex + 2).setCellValue(roleDesc); 				// role desc
						valRow.createCell(valueStartIndex - 1).setCellValue(elementLocation); 		// value location
					}
					valueStartIndex = valueStartIndex + 3;
					valueLastPlottedIndex = valueStartIndex - 3;
					valueMemory = value;
					valueLocationIndex = valueLocationIndex + 5;
				}
				/*valueStartIndex = valueStartIndex + 3;
				valueLastPlottedIndex = valueStartIndex - 3;
				valueMemory = value;
				valueLocationIndex = valueLocationIndex + 5;*/
			} else {
				String passion = (String) elementRow[1];
				if (passionMemory.equals("") || passionMemory.equals(passion)) {
					String roleDesc = (String) elementRow[2];
					String elementLocation = (String) elementRow[3];
					int roleMapIndex = roleMapping.get(roleDesc);
					if (passionMemory.equals("")) {
						valRow.createCell(passionStartIndex).setCellValue((String) elementRow[1]); 		// passion desc
						passionStartIndex = passionStartIndex + 2;
						if (roleMapIndex == 1) {
							valRow.createCell(passionStartIndex).setCellValue(roleDesc); 				// role desc
							valRow.createCell(passionLocationIndex).setCellValue(elementLocation); 		// strength location
						} else if (roleMapIndex == 2) {
							valRow.createCell(passionStartIndex + 1).setCellValue(roleDesc); 			// role desc
							valRow.createCell(passionLocationIndex).setCellValue(elementLocation); 		// strength location
						} else {
							valRow.createCell(passionStartIndex + 2).setCellValue(roleDesc); 			// role desc
							valRow.createCell(passionLocationIndex).setCellValue(elementLocation); 		// strength location
						}
						passionStartIndex = passionStartIndex + 3;
						passionLastPlottedIndex = passionStartIndex - 3;
						passionMemory = passion;
						passionLocationIndex = passionLocationIndex + 5;
					} else {
						passionStartIndex = passionStartIndex - 3;
						passionLastPlottedIndex = passionStartIndex + 3;
						passionMemory = passion;
						passionLocationIndex = passionLocationIndex - 5;
						if (roleMapIndex == 1) {
							valRow.createCell(passionStartIndex).setCellValue(roleDesc); 				// role desc
						} else if (roleMapIndex == 2) {
							valRow.createCell(passionStartIndex + 1).setCellValue(roleDesc); 			// role desc
						} else {
							valRow.createCell(passionStartIndex + 2).setCellValue(roleDesc); 			// role desc
						}
						passionStartIndex = passionStartIndex + 3;
						passionLastPlottedIndex = passionStartIndex - 3;
						passionMemory = passion;
						passionLocationIndex = passionLocationIndex + 5;
					}
				} else {
					if (passionLastPlottedIndex == 379
							|| passionLastPlottedIndex == 380
							|| passionLastPlottedIndex == 381
							|| passionLastPlottedIndex == 384
							|| passionLastPlottedIndex == 385
							|| passionLastPlottedIndex == 386
							|| passionLastPlottedIndex == 389
							|| passionLastPlottedIndex == 390
							|| passionLastPlottedIndex == 391) {
						passionStartIndex = 392;
					} else if (passionLastPlottedIndex == 394
							|| passionLastPlottedIndex == 395
							|| passionLastPlottedIndex == 396
							|| passionLastPlottedIndex == 399
							|| passionLastPlottedIndex == 400
							|| passionLastPlottedIndex == 401
							|| passionLastPlottedIndex == 404
							|| passionLastPlottedIndex == 405
							|| passionLastPlottedIndex == 406) {
						passionStartIndex = 407;
					} else if (passionLastPlottedIndex == 409
							|| passionLastPlottedIndex == 410
							|| passionLastPlottedIndex == 411
							|| passionLastPlottedIndex == 414
							|| passionLastPlottedIndex == 415
							|| passionLastPlottedIndex == 416
							|| passionLastPlottedIndex == 419
							|| passionLastPlottedIndex == 420
							|| passionLastPlottedIndex == 421) {
						passionStartIndex = 422;
					}
					valRow.createCell(passionStartIndex).setCellValue((String) elementRow[1]); 		// passion desc
					passionStartIndex = passionStartIndex + 2;
					String roleDesc = (String) elementRow[2];
					String elementLocation = (String) elementRow[3];
					int roleMapIndex = roleMapping.get(roleDesc);
					if (roleMapIndex == 1) {
						valRow.createCell(passionStartIndex).setCellValue(roleDesc); 				// role desc
						valRow.createCell(passionStartIndex - 1).setCellValue(elementLocation); 	// value location
					} else if (roleMapIndex == 2) {
						valRow.createCell(passionStartIndex + 1).setCellValue(roleDesc); 			// role desc
						valRow.createCell(passionStartIndex - 1).setCellValue(elementLocation); 	// value location
					} else {
						valRow.createCell(passionStartIndex + 2).setCellValue(roleDesc); 			// role desc
						valRow.createCell(passionStartIndex - 1).setCellValue(elementLocation); 	// value location
					}
					
					passionStartIndex = passionStartIndex + 3;
					passionLastPlottedIndex = passionStartIndex - 3;
					passionMemory = passion;
					passionLocationIndex = passionLocationIndex + 5;
				}
				/*passionStartIndex = passionStartIndex + 3;
				passionLastPlottedIndex = passionStartIndex - 3;
				passionMemory = passion;
				passionLocationIndex = passionLocationIndex + 5;*/
			}
		}
		LOGGER.info("<<<< AllReportsController.populateMappings");
	}
	/**
	 * Method hides Action plan columns in excel.
	 * @param sheet
	 */
	private void hideActionPlans(Sheet sheet) {
		LOGGER.info(">>>> AllReportsController.hideActionPlans");
		for (int index = 449; index < 551; index ++ ) {
			sheet.setColumnHidden(index, true);
		}
		LOGGER.info("<<<< AllReportsController.hideActionPlans");
	}
	/**
	 * Method hides reflection question columns in excel.
	 * @param sheet
	 */
	private void hideReflectionQuestions(Sheet sheet) {
		LOGGER.info(">>>> AllReportsController.hideReflectionQuestions");
		for (int index = 437; index < 491; index ++ ) {
			sheet.setColumnHidden(index, true);
		}
		LOGGER.info("<<<< AllReportsController.hideReflectionQuestions");
	}
	/**
	 * Method hides before sketch columns in excel.
	 * @param sheet
	 */
	private void hideBeforeSketch(Sheet sheet) {
		LOGGER.info(">>>> AllReportsController.hideBeforeSketch");
		sheet.setColumnHidden(9, true);
		sheet.setColumnHidden(11, true);
		sheet.setColumnHidden(17, true);
		sheet.setColumnHidden(18, true);
		sheet.setColumnHidden(22, true);
		sheet.setColumnHidden(23, true);
		sheet.setColumnHidden(24, true);
		sheet.setColumnHidden(29, true);
		sheet.setColumnHidden(30, true);
		sheet.setColumnHidden(34, true);
		sheet.setColumnHidden(35, true);
		sheet.setColumnHidden(36, true);
		sheet.setColumnHidden(41, true);
		sheet.setColumnHidden(42, true);
		sheet.setColumnHidden(46, true);
		sheet.setColumnHidden(47, true);
		sheet.setColumnHidden(48, true);
		sheet.setColumnHidden(53, true);
		sheet.setColumnHidden(54, true);
		sheet.setColumnHidden(58, true);
		sheet.setColumnHidden(59, true);
		sheet.setColumnHidden(60, true);
		sheet.setColumnHidden(65, true);
		sheet.setColumnHidden(66, true);
		sheet.setColumnHidden(70, true);
		sheet.setColumnHidden(71, true);
		sheet.setColumnHidden(72, true);
		sheet.setColumnHidden(77, true);
		sheet.setColumnHidden(78, true);
		sheet.setColumnHidden(82, true);
		sheet.setColumnHidden(83, true);
		sheet.setColumnHidden(84, true);
		sheet.setColumnHidden(89, true);
		sheet.setColumnHidden(90, true);
		sheet.setColumnHidden(94, true);
		sheet.setColumnHidden(95, true);
		sheet.setColumnHidden(96, true);
		sheet.setColumnHidden(101, true);
		sheet.setColumnHidden(102, true);
		sheet.setColumnHidden(106, true);
		sheet.setColumnHidden(107, true);
		sheet.setColumnHidden(108, true);
		sheet.setColumnHidden(113, true);
		sheet.setColumnHidden(114, true);
		sheet.setColumnHidden(118, true);
		sheet.setColumnHidden(119, true);
		sheet.setColumnHidden(120, true);
		sheet.setColumnHidden(125, true);
		sheet.setColumnHidden(126, true);
		sheet.setColumnHidden(130, true);
		sheet.setColumnHidden(131, true);
		sheet.setColumnHidden(132, true);
		sheet.setColumnHidden(137, true);
		sheet.setColumnHidden(138, true);
		sheet.setColumnHidden(142, true);
		sheet.setColumnHidden(143, true);
		sheet.setColumnHidden(144, true);
		sheet.setColumnHidden(149, true);
		sheet.setColumnHidden(150, true);
		sheet.setColumnHidden(154, true);
		sheet.setColumnHidden(155, true);
		sheet.setColumnHidden(156, true);
		sheet.setColumnHidden(161, true);
		sheet.setColumnHidden(162, true);
		sheet.setColumnHidden(166, true);
		sheet.setColumnHidden(167, true);
		sheet.setColumnHidden(168, true);
		sheet.setColumnHidden(173, true);
		sheet.setColumnHidden(174, true);
		sheet.setColumnHidden(178, true);
		sheet.setColumnHidden(179, true);
		sheet.setColumnHidden(180, true);
		sheet.setColumnHidden(185, true);
		sheet.setColumnHidden(186, true);
		sheet.setColumnHidden(190, true);
		sheet.setColumnHidden(191, true);
		sheet.setColumnHidden(192, true);
		sheet.setColumnHidden(197, true);
		sheet.setColumnHidden(198, true);
		sheet.setColumnHidden(202, true);
		sheet.setColumnHidden(203, true);
		sheet.setColumnHidden(204, true);
		sheet.setColumnHidden(209, true);
		sheet.setColumnHidden(210, true);
		sheet.setColumnHidden(214, true);
		sheet.setColumnHidden(215, true);
		sheet.setColumnHidden(216, true);
		sheet.setColumnHidden(221, true);
		sheet.setColumnHidden(222, true);
		sheet.setColumnHidden(226, true);
		sheet.setColumnHidden(227, true);
		sheet.setColumnHidden(228, true);
		sheet.setColumnHidden(233, true);
		sheet.setColumnHidden(234, true);
		sheet.setColumnHidden(238, true);
		sheet.setColumnHidden(239, true);
		sheet.setColumnHidden(240, true);
		sheet.setColumnHidden(245, true);
		sheet.setColumnHidden(246, true);
		sheet.setColumnHidden(250, true);
		sheet.setColumnHidden(251, true);
		sheet.setColumnHidden(252, true);
		LOGGER.info("<<<< AllReportsController.hideBeforeSketch");
	}
	
	private void populateSurveyQuestion(Row valRow, String emailId,
			XSSFCellStyle bodyStyle, Integer surveyCount) throws JCTException {
		LOGGER.info(">>>> AllReportsController.populateSurveyQuestion");
		List<Object> distinctMainQtnList = iReportService.getSurveyMainQtns(emailId);
		if (distinctMainQtnList.size() > 0) {
			int mainQtnPlotIndex = 593;
			int ansPlotIndex = 594;
			for (int mainQtnIndex = 0; mainQtnIndex < distinctMainQtnList.size(); mainQtnIndex ++ ){
            	Object[] obj = (Object[]) distinctMainQtnList.get(mainQtnIndex);
            	
            	String mainQuestion = (String) obj[0];
            	valRow.createCell(mainQtnPlotIndex).setCellValue(mainQuestion); 					// question
				valRow.getCell(mainQtnPlotIndex).setCellStyle(bodyStyle);
				mainQtnPlotIndex = mainQtnPlotIndex + 2;
				
            	Integer ansType = (Integer) obj[1];
            	// Get the answer of each main qtn
				List<String> ansList = iReportService.getSurveyAnswers(emailId, mainQuestion, ansType);
				//Check the size
				if (ansList.size() > 0) {
					StringBuilder sb = new StringBuilder("");
					for (int ansInd=0; ansInd < ansList.size(); ansInd++) {
						sb.append((String) ansList.get(ansInd));
						sb.append("\n");
					}
					valRow.createCell(ansPlotIndex).setCellValue(sb.toString()); 					// answer
					valRow.getCell(ansPlotIndex).setCellStyle(bodyStyle);
					ansPlotIndex = ansPlotIndex + 2;
				}	
        	}
		}
		LOGGER.info("<<<< AllReportsController.populateSurveyQuestion");
	}
}
