package service;

import dao.AllowanceDAO;
import dao.AttendanceLogDAO;
import dao.DeductionDAO;

public abstract class PayrollService {
    protected AttendanceLogDAO attendanceLogDAO;
    protected AllowanceDAO allowanceDAO;
    protected DeductionDAO deductionDAO;

    public PayrollService(AttendanceLogDAO attendances, AllowanceDAO allowanceDAO, DeductionDAO deductionDAO) {
        this.attendanceLogDAO = attendances;
        this.allowanceDAO = allowanceDAO;
        this.deductionDAO = deductionDAO;
    }
}
