package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.validate.ApptBeanValidator;

public class EditApptAction extends ApptAction {
	
	private ApptBeanValidator validator = new ApptBeanValidator();
	
	public EditApptAction(DAOFactory factory, long loggedInMID) {
		super(factory, loggedInMID);
	}
	
	/**
	 * Retrieves an appointment from the database, given its ID.
	 * Returns null if there is no match, or multiple matches.
	 * 
	 * @param apptID
	 * @return ApptBean with matching ID
	 */
	public ApptBean getAppt(int apptID) {
		try {
			List<ApptBean> apptBeans = apptDAO.getAppt(apptID);
			if (apptBeans.size() == 1)
				return apptBeans.get(0);
		} catch (SQLException e) {
			
		}
		return null;
	}
	
	/**
	 * Updates an existing appointment
	 * 
	 * @param appt Appointment Bean containing the updated information
	 * @return Message to be displayed
	 * @throws FormValidationException
	 * @throws SQLException 
	 */
	public String editAppt(ApptBean appt, boolean ignoreConflicts) throws FormValidationException, SQLException {
		validator.validate(appt);
		if(appt.getDate().before(new Timestamp(System.currentTimeMillis())))
			return "The scheduled date of this appointment ("+appt.getDate()+") has already passed.";
		
		if(!ignoreConflicts){
			if(getConflictsForAppt(appt.getHcp(), appt).size()>0){
				return "Warning! This appointment conflicts with other appointments";
			}
		}
		
		try {
			apptDAO.editAppt(appt);
			return "Success: Appointment changed";
		} catch (SQLException e) {
			
			return e.getMessage();
		} 
	}
	
	
	
	/**
	 * Removes an existing appointment
	 * 
	 * @param appt Appointment Bean containing the ID of the appointment to be removed.
	 * @return Message to be displayed
	 */
	public String removeAppt(ApptBean appt) {
		try {
			apptDAO.removeAppt(appt);
			return "Success: Appointment removed";
		} catch (SQLException e) {
			
			return e.getMessage();
		}
	}

}
