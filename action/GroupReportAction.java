package edu.ncsu.csc.itrust.action;

import java.util.List;
import edu.ncsu.csc.itrust.beans.AllergyBean;
import edu.ncsu.csc.itrust.beans.DiagnosisBean;
import edu.ncsu.csc.itrust.beans.FamilyMemberBean;
import edu.ncsu.csc.itrust.beans.GroupReportBean;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.PrescriptionBean;
import edu.ncsu.csc.itrust.beans.ProcedureBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AllergyDAO;
import edu.ncsu.csc.itrust.dao.mysql.FamilyDAO;
import edu.ncsu.csc.itrust.dao.mysql.OfficeVisitDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.report.DemographicReportFilter.DemographicReportFilterType;
import edu.ncsu.csc.itrust.report.MedicalReportFilter.MedicalReportFilterType;
import edu.ncsu.csc.itrust.report.PersonnelReportFilter.PersonnelReportFilterType;
import edu.ncsu.csc.itrust.report.ReportFilter;

/**
 * 
 *
 */
public class GroupReportAction {

	private PatientDAO pDAO;
	private AllergyDAO aDAO;
	private FamilyDAO fDAO;
	private OfficeVisitDAO oDAO;

	/**
	 * 
	 * @param factory
	 */
	public GroupReportAction(DAOFactory factory) {
		pDAO = factory.getPatientDAO();
		aDAO = factory.getAllergyDAO();
		fDAO = factory.getFamilyDAO();
		oDAO = factory.getOfficeVisitDAO();
	}

	/**
	 * 
	 * @param filters
	 * @return
	 */
	public GroupReportBean generateReport(List<ReportFilter> filters) {
		List<PatientBean> patients;

		try {
			patients = getAllPatients();
		} catch (DBException e) {
			return null;
		}

		for (ReportFilter filter : filters) {
			patients = filter.filter(patients);
		}

		return new GroupReportBean(patients, filters);
	}

	/**
	 * 
	 * @param patient
	 * @param filterType
	 * @return
	 */
	public String getComprehensiveDemographicInfo(PatientBean patient, DemographicReportFilterType filterType) {
		switch (filterType) {
		case GENDER:
			return patient.getGender().toString();
		case LAST_NAME:
			return patient.getLastName();
		case FIRST_NAME:
			return patient.getFirstName();
		case CONTACT_EMAIL:
			return patient.getEmail();
		case STREET_ADDR:
			return patient.getStreetAddress1() + " " + patient.getStreetAddress2();
		case CITY:
			return patient.getCity();
		case STATE:
			return patient.getState();
		case ZIP:
			return patient.getZip();
		case PHONE:
			return patient.getPhone();
		case EMER_CONTACT_NAME:
			return patient.getEmergencyName();
		case EMER_CONTACT_PHONE:
			return patient.getEmergencyPhone();
		case INSURE_NAME:
			return patient.getIcName();
		case INSURE_ADDR:
			return patient.getIcAddress1() + " " + patient.getIcAddress2();
		case INSURE_CITY:
			return patient.getIcCity();
		case INSURE_STATE:
			return patient.getIcState();
		case INSURE_ZIP:
			return patient.getIcZip();
		case INSURE_PHONE:
			return patient.getIcPhone();
		case MID:
			return Long.toString(patient.getMID());
		case INSURE_ID:
			return patient.getIcID();
		case PARENT_FIRST_NAME:
			try {
				List<FamilyMemberBean> parents = fDAO.getParents(patient.getMID());
				String out = "";
				for (FamilyMemberBean parent : parents) {
					out += parent.getFirstName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case PARENT_LAST_NAME:
			try {
				List<FamilyMemberBean> parents = fDAO.getParents(patient.getMID());
				String out = "";
				for (FamilyMemberBean parent : parents) {
					out += parent.getLastName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case CHILD_FIRST_NAME:
			try {
				List<FamilyMemberBean> children = fDAO.getChildren(patient.getMID());
				String out = "";
				for (FamilyMemberBean child : children) {
					out += child.getFirstName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case CHILD_LAST_NAME:
			try {
				List<FamilyMemberBean> children = fDAO.getChildren(patient.getMID());
				String out = "";
				for (FamilyMemberBean child : children) {
					out += child.getLastName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case SIBLING_FIRST_NAME:
			try {
				List<FamilyMemberBean> siblings = fDAO.getSiblings(patient.getMID());
				String out = "";
				for (FamilyMemberBean sibling : siblings) {
					out += sibling.getFirstName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case SIBLING_LAST_NAME:
			try {
				List<FamilyMemberBean> siblings = fDAO.getSiblings(patient.getMID());
				String out = "";
				for (FamilyMemberBean sibling : siblings) {
					out += sibling.getLastName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case DEACTIVATED:
			return patient.getDateOfDeactivationStr();
		default:
			break;
		}
		return null;
	}

	/**
	 * 
	 * @param patient
	 * @param filterType
	 * @return
	 */
	public String getComprehensiveMedicalInfo(PatientBean patient, MedicalReportFilterType filterType) {
		switch (filterType) {
		case ALLERGY:
			try {
				List<AllergyBean> allergies = aDAO.getAllergies(patient.getMID());
				String out = "";
				for (AllergyBean allergy : allergies) {
					out += allergy.getNDCode() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case CURRENT_PRESCRIPTIONS:
			try {
				List<PrescriptionBean> prescriptions = pDAO.getCurrentPrescriptions(patient.getMID());
				String out = "";
				for (PrescriptionBean prescription : prescriptions) {
					out += prescription.getMedication().getNDCodeFormatted() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case DIAGNOSIS_ICD_CODE:
			try {
				List<DiagnosisBean> diagnoses = pDAO.getDiagnoses(patient.getMID());
				String out = "";
				for (DiagnosisBean diagnosis : diagnoses) {
					out += diagnosis.getICDCode() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case LOWER_OFFICE_VISIT_DATE:
			try {
				List<OfficeVisitBean> visits = oDAO.getAllOfficeVisits(patient.getMID());
				String out = "";
				for (OfficeVisitBean visit : visits) {
					out += visit.getVisitDateStr() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case PASTCURRENT_PRESCRIPTIONS:
			try {
				List<PrescriptionBean> prescriptions = pDAO.getPrescriptions(patient.getMID());
				String out = "";
				for (PrescriptionBean prescription : prescriptions) {
					out += prescription.getMedication().getNDCodeFormatted() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		case PROCEDURE:
			try {
				List<ProcedureBean> procedures = pDAO.getProcedures(patient.getMID());
				String out = "";
				for (ProcedureBean procedure : procedures) {
					out += procedure.getCPTCode() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		default:
			break;
		}
		return null;
	}

	/**
	 * 
	 * @param patient
	 * @param filterType
	 * @return
	 */
	public String getComprehensivePersonnelInfo(PatientBean patient, PersonnelReportFilterType filterType) {
		switch (filterType) {
		case DLHCP:
			try {
				List<PersonnelBean> dlhcps = pDAO.getDeclaredHCPs(patient.getMID());
				String out = "";
				for (PersonnelBean dlhcp : dlhcps) {
					out += dlhcp.getFullName() + "\n";
				}
				return out;
			} catch (Exception e) {
				break;
			}
		default:
			break;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws DBException
	 */
	private List<PatientBean> getAllPatients() throws DBException {
		return pDAO.getAllPatients();
	}
}
