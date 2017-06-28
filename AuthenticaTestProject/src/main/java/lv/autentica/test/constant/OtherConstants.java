/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lv.autentica.test.constant;

/**
 * @see <b>current_date</b> - for Apache Derby database<br>
 * For Oracle will be <b>sysdate</b>
 * @author aks513
 */
public class OtherConstants {
    private OtherConstants(){}
    
    public static String DATABASE_DRIVER_NAME;
    public static String DATABASE_CONNECTION_STRING;
    public static String LOGIN;
    public static String PASSWORD;
    public static String CURRENT_DATE;
    
    public static final String DATE_FORMAT = "dd.mm.yyyy";
    public static final String DB_DATE_FORMAT = "yyyy-mm-dd";
    public static final String GET_ALL_CARS = "select car_name name, car_number number from cars where rent_by is null";
    public static final String GET_ALL_EMPS = "select "
            + "emp_name name, emp_personal_code code, emp_address address, emp_phone phone "
        + "from employees";
    
    public static final String RENT_CARS = "select "
            + "rc.*, emp.emp_name person "
        + "from rent_cars rc "
            + "inner join cars c on rc.car_number = c.car_number and rc.date_to > " + CURRENT_DATE
            + "inner join employees emp on c.rent_by = emp.emp_id";
    
    public static final String INVALIDATE_RENTED_CARS = "update cars set rent_by = null "
            + "where car_number in ("
                + "select car_number from rent_cars where date_to <= " + CURRENT_DATE
            + ")";
    
    public static final String GET_PERSON_ID = "select emp_id from employees where emp_name = ?";
    public static final String RENT_CAR_INSERT = "insert into rent_cars values(?, ?, ?)";
    public static final String UPDATE_CAR = "update cars set rent_by = ? where car_number = ?";
}
