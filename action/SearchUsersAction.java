package edu.ncsu.csc.itrust.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.DBException;


public class SearchUsersAction {
	private PatientDAO patientDAO;
	private PersonnelDAO personnelDAO;


	/**
	 * Set up defaults
	 * 
	 * @param factory The DAOFactory used to create the DAOs used in this action.
	 * @param loggedInMID The MID of the user who is performing the search.
	 */
	public SearchUsersAction(DAOFactory factory, long loggedInMID) {
		this.patientDAO = factory.getPatientDAO();
		this.personnelDAO = factory.getPersonnelDAO();
	}
	

	/**
	 * Searches for all personnel with the first name and last name specified in the parameter list.
	 * @param firstName The first name to be searched.
	 * @param lastName The last name to be searched.
	 * @return A java.util.List of PersonnelBeans for the users who matched.
	 */
	public List<PersonnelBean> searchForPersonnelWithName(String firstName, String lastName) {
		
		try {	
			if("".equals(firstName))
				firstName = "%";
			if("".equals(lastName))
				lastName = "%";
			return personnelDAO.searchForPersonnelWithName(firstName, lastName);
		}
		catch (DBException e) {
			
			return null;
		}
	}
	
	/**
	 * Search for all patients with first name and last name given in parameters.
	 * @param firstName The first name of the patient being searched.
	 * @param lastName The last name of the patient being searched.
	 * @return A java.util.List of PatientBeans
	 */
	public List<PatientBean> searchForPatientsWithName(String firstName, String lastName) {
	
		try {	
			if("".equals(firstName))
				firstName = "%";
			if("".equals(lastName))
				lastName = "%";
			return patientDAO.searchForPatientsWithName(firstName, lastName);
		}
		catch (DBException e) {
			
			return null;
		}
	}
	
	/**
	 * Search for all patients with first name and last name given in parameters.
	 * @param firstName The first name of the patient being searched.
	 * @param lastName The last name of the patient being searched.
	 * @return A java.util.List of PatientBeans
	 * @throws DBException 
	 */
	public List<PatientBean> fuzzySearchForPatients(String query) {
		return fuzzySearchForPatients(query,false);
	}
	
	/**
	 * Search for all patients with first name and last name given in parameters.
	 * @param firstName The first name of the patient being searched.
	 * @param lastName The last name of the patient being searched.
	 * @return A java.util.List of PatientBeans
	 * @throws DBException 
	 */
	public List<PatientBean> fuzzySearchForPatients(String query, boolean allowDeactivated) {
		String[] subqueries=null;
		
		Set<PatientBean> patientsSet = new TreeSet<PatientBean>();
		if(query!=null && query.length()>0){
			subqueries = query.split(" ");
			Set<PatientBean>[] patients = new Set[subqueries.length];
			int i=0;
			for(String q : subqueries){
				try {
					patients[i] = new TreeSet<PatientBean>();
					List<PatientBean> first = patientDAO.fuzzySearchForPatientsWithName(q, "");				
					List<PatientBean> last = patientDAO.fuzzySearchForPatientsWithName("",q);
					patients[i].addAll(first);
					patients[i].addAll(last);
					
					try{
						long mid = Long.valueOf(q);
						patients[i].add(patientDAO.getPatient(mid));
					}catch(NumberFormatException e){}
					i++;
				} catch (DBException e1) {
					e1.printStackTrace();
				}
			}
			
			patientsSet.addAll(patients[0]);
			for(Set<PatientBean> results : patients){
				try{
					patientsSet.retainAll(results);
				}catch(NullPointerException e){}
			}
		}
		ArrayList<PatientBean> results=new ArrayList<PatientBean>(patientsSet);
		
		if(allowDeactivated==false){
			for(int i=results.size()-1;i>=0;i--){
				if(!results.get(i).getDateOfDeactivationStr().equals("")){
					results.remove(i);
				}
			}
		}
		
		return results;
	}
}
