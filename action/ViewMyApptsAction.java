package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import java.util.List;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;

public class ViewMyApptsAction extends ApptAction {
	private long loggedInMID;
	
	public ViewMyApptsAction(DAOFactory factory, long loggedInMID) {
		super(factory, loggedInMID);
		this.loggedInMID = loggedInMID;
	}
	
	public List<ApptBean> getMyAppointments() throws SQLException {
		return apptDAO.getApptsFor(loggedInMID);
	}
	
	public List<ApptBean> getAllMyAppointments() throws SQLException {
		return apptDAO.getAllApptsFor(loggedInMID);
	}
	
	/**
	 * Gets a user's appointments
	 * 
	 * @param mid the MID of the user
	 * @return a list of the user's appointments
	 * @throws SQLException
	 */
	public List<ApptBean> getAppointments(long MID) throws SQLException {
		return apptDAO.getApptsFor(MID);
	}
}
