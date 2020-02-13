package edu.ncsu.csc.itrust.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import edu.ncsu.csc.itrust.beans.HospitalBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.HospitalsDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.DBException;

/**
 * Action class to find experts based on the distance from the user and type of expert.
 *
 */
public class FindExpertAction {
	/**
	 * The radius of the earth in KM
	 */
	//public static final double RADIUS = 6371;
	
	/**
	 * HospitalDAO to grab hospitals from
	 */
	HospitalsDAO hospitalsDAO;
	
	/**
	 * PersonnelDAO to find experts from
	 */
	PersonnelDAO personnelDAO;
	
	/**
	 * PatientDAO to find patients from
	 */
	PatientDAO patientDAO;
	
	/**
	 * Used to instantiate the DAOs
	 */
	DAOFactory factory;

	/**
	 * Constructor simply is used to initialize the DAOs
	 * @param factory used to initialize DAOs
	 */
	public FindExpertAction(DAOFactory factory){
		this.factory = factory;
		patientDAO = factory.getPatientDAO();
		hospitalsDAO = new HospitalsDAO(factory);
		personnelDAO = new PersonnelDAO(factory);
	}
	
	/**
	 * 
	 * Main method used to find the hospitals and all specified personnel within range
	 * 
	 * @param distance The maximum distance that a hospital must be within range in order for it to return
	 * @param specialty The specialty that the user is interested in
	 * @param lat The latitude of the address that the user is located at
	 * @param lng The longitude of the address that the user is located at
	 * @return A relationship between hospitals within the defined proximity and the specified experts at the hospital.
	 */
	public HashMap<HospitalBean, List<PersonnelBean>> findHospitalsBySpecialty(String specialty, long loggedInMID, String scope, String zip){
		HashMap<HospitalBean, List<PersonnelBean>> experts = null;
		try {
			//Grab all hospitals and filter them based on distance
			List<HospitalBean> hospitals = filterHospitals(hospitalsDAO.getAllHospitals(), loggedInMID, Integer.parseInt(scope), zip);
			//Find experts in hospitals
			experts = findExperts(hospitals, specialty);
		} catch (DBException e) {
			//
		}
		return experts;
	}
	
	/**
	 * Method used to find experts of specified specialty from hospitals that are in range
	 * @param hospitals The hospitals within the proximity of the user
	 * @param specialty The expertise specified
	 * @return A relationship between the hospitals within proximity and the personnel with the specified expertise within them.
	 */
	public HashMap<HospitalBean, List<PersonnelBean>> findExperts(List<HospitalBean> hospitals, String specialty){
		HashMap<HospitalBean, List<PersonnelBean>> experts = new HashMap<HospitalBean, List<PersonnelBean>>();
		try{
			//Go through all nearby hospitals
			for(HospitalBean hospital : hospitals){
				//Put the specified experts into a hashmap with the hospital
				experts.put(hospital, personnelDAO.getPersonnelFromHospital(hospital.getHospitalID(), specialty));
			}
		} catch (DBException e){
			//
		}
		return experts;
	}
	
	/**
	 * Filters hospitals down to just the hospitals in the specified range of the user
	 * zipcode
	 * @param hospitals Hospitals to filter
	 * @param patientZip patient's zipcode
	 * @return All hospitals within the specified range of the user
	 */
	
	public List<HospitalBean> filterHospitals(List<HospitalBean> hospitals, long loggedInMID, int scope, String zip){
		List<HospitalBean> inRange = new ArrayList<HospitalBean>();
		String pZip = "00000"; // default patient zip
		String hZip = "00000"; // default hospital zip
		pZip = zip.substring(0,scope); // get patents zip from MID at size scope

		for (int i = 0; i <= hospitals.size()-1;i++){
			try{
				if(hospitals.get(i).getHospitalZip().equals(null)); // todo check with database, null pointer exceptions.
			} catch(NullPointerException e) {
				continue;
			}
			hZip=hospitals.get(i).getHospitalZip().substring(0,scope); //get hospital zip at size scope
			if(hZip.equals(pZip)){ // check vs patients zip
				inRange.add(hospitals.get(i)); // if match add to inRange
			}
		}
		/*System.out.println(scope);
		System.out.println(hZip+", "+pZip);*/
		return inRange;
	}
	
	public String getPatientZip(long loggedInMID) throws DBException{
		return patientDAO.getPatient(loggedInMID).getZip();
	}
	
	/**
	 * Method to calculate the distance between the two GPS coordinates of the user and hospital
	 * @param lat1 Latitude of user
	 * @param lat2 Latitude of hospital
	 * @param lng1 Longitude of user
	 * @param lng2 Longitude of hospital
	 * @return The distance between the user and hospital
	 */
	/*public Double calculateDistance(Double lat1, Double lat2, Double lng1, Double lng2){
		Double deltaLat = Math.toRadians(lat2 - lat1);
		Double deltaLng = Math.toRadians(lng2 - lng1);
		Double rlat1 = Math.toRadians(lat1);
		Double rlat2 = Math.toRadians(lat2);
		
		Double temp = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +Math.sin(deltaLng/2) * Math.sin(deltaLng/2) * Math.cos(rlat1) * Math.cos(rlat2);
		return RADIUS * 2 * Math.atan2(Math.sqrt(temp), Math.sqrt(1-temp));
	}*/
}
