package edu.ncsu.csc.itrust.action;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import edu.ncsu.csc.itrust.action.base.PatientBaseAction;
import edu.ncsu.csc.itrust.beans.HealthRecord;
import edu.ncsu.csc.itrust.beans.forms.HealthRecordForm;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.HealthRecordsDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.validate.HealthRecordFormValidator;
import edu.ncsu.csc.itrust.action.EditOfficeVisitAction;

/**
 * Edits the health history of a patient, used by editBasicHealth.jsp
 * 
 * 
 */
public class EditHealthHistoryAction extends PatientBaseAction {
	private HealthRecordsDAO hrDAO;
	private PatientDAO patientDAO;
	private AuthDAO authDAO;
	private long loggedInMID;
	private HealthRecordFormValidator validator = new HealthRecordFormValidator();
	private EditOfficeVisitAction ovAction;

	/**
	 * The patient ID is validated by the superclass
	 * 
	 * @param factory The DAOFactory which will be used to generate the DAOs used for this action.
	 * @param loggedInMID The user authorizing this action.
	 * @param pidString The patient (or other user) who is being edited. 
	 * @throws iTrustException
	 */
	public EditHealthHistoryAction(DAOFactory factory, long loggedInMID, String pidString)
			throws iTrustException {
		super(factory, pidString);
		this.hrDAO = factory.getHealthRecordsDAO();
		this.authDAO = factory.getAuthDAO();
		this.patientDAO = factory.getPatientDAO();
		this.loggedInMID = loggedInMID;
		this.ovAction = new EditOfficeVisitAction(factory, loggedInMID, pidString);
	}

	/**
	 * returns the patient name
	 * 
	 * @return patient name
	 * @throws DBException
	 * @throws iTrustException
	 */
	public String getPatientName() throws DBException, iTrustException {
		return authDAO.getUserName(pid);
	}
	
	/**
	 * returns the patients date of birth as a Date object
	 * 
	 * @return Date patient date of birth
	 * @throws DBException
	 */
	public Date getPatientDOB() throws DBException {
		
		return this.patientDAO.getPatient(pid).getDateOfBirth();
		
	}

	/**
	 * Adds a health record for the given patient
	 * 
	 * @param pid  The patient record who is being edited.
	 * @param hr  The filled out health record form to be added.
	 * @return message - "Information Recorded" or exception's message
	 * @throws FormValidationException
	 */
	public String addHealthRecord(long pid, HealthRecordForm hr) throws FormValidationException, iTrustException {
		HealthRecord record;
		try {
			validator.validate(hr);
			record = transferForm(pid, hr);
		} catch (FormValidationException e) {
			return e.getMessage();
		}
		hrDAO.add(record);
		return "Information Recorded";
	}
	
	/**
	 * Deletes a record
	 * @param recordID the record to delete
	 * @return
	 */
	public void removeHealthRecord(long recordID) {
		try {
			hrDAO.delete(recordID);
		} catch (DBException e) {
		}
	}

	
/**
 * Moves the information from the form to a HealthRecord
 * 
 * @param pid Patient of interest
 * @param form Form to be translated
 * @return a HealthRecord containing all the information in the form
 * @throws FormValidationException
 */
	private HealthRecord transferForm(long pid, HealthRecordForm form) throws FormValidationException {
		HealthRecord record = new HealthRecord();
		record.setPatientID(pid);
		record.setPersonnelID(loggedInMID);
		record.setRecordID(Integer.valueOf(form.getRecordID()));
		record.setOfficeVisitID(Integer.valueOf(form.getOfficeVisitID()));
		record.setBloodPressureD(Integer.valueOf(form.getBloodPressureD()));
		record.setBloodPressureN(Integer.valueOf(form.getBloodPressureN()));
		record.setCholesterolHDL(Integer.valueOf(form.getCholesterolHDL()));
		record.setCholesterolLDL(Integer.valueOf(form.getCholesterolLDL()));
		record.setCholesterolTri(Integer.valueOf(form.getCholesterolTri()));
		if (record.getTotalCholesterol() < 100 || record.getTotalCholesterol() > 600)
			throw new FormValidationException("Total cholesterol must be in [100,600]");
		record.setHeight(Double.valueOf(form.getHeight()));
		record.setWeight(Double.valueOf(form.getWeight()));
		record.setHeadCircumference(Double.valueOf(form.getHeadCircumference()));
		record.setSmoker(Integer.valueOf(form.getIsSmoker()));
		record.setHouseholdSmokingStatus(Integer.valueOf(form.getHouseholdSmokingStatus()));
		return record;
	}

	/**
	 * Returns a list of all HealthRecords for the given patient
	 * 
	 * @param pid  The ID of the patient to look up
	 * @return list of HealthRecords
	 * @throws iTrustException
	 */
	public List<HealthRecord> getAllHealthRecords(long pid) throws iTrustException {
		return hrDAO.getAllHealthRecords(pid);
	}
	
	public List<HealthRecord> getHealthRecordsByOfficeVisit(long ovid, long pid) throws iTrustException {
		//Get all health records for the given patient
		List<HealthRecord> records = this.getAllHealthRecords(pid);
		ArrayList<HealthRecord> ovRecords = new ArrayList<HealthRecord>();
		
		System.out.println("Param OVID: " + ovid);
		System.out.println("Number of records: " + records.size());
		
		//Then remove any record that doesn't match the ovid
		for (int i = 0; i < records.size(); i++) {
			System.out.println("Looking at record ID: " + records.get(i).getOfficeVisitID());
			if (records.get(i).getOfficeVisitID() == ovid) {
				System.out.println("Adding Record " + records.get(i).getOfficeVisitID());
				ovRecords.add(records.get(i));
			}
		}
		
		return ovRecords;
		
	}
	
	public String getOfficeVisitDateStr(long ovid) throws Exception {
		return this.ovAction.getOfficeVisit().getVisitDateStr();
	}
}
