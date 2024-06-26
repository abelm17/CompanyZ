import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Admin {

    private static String url = "jdbc:mysql://localhost:3306/employeeData";
    private static String user = "root";
    private static String password = "Enter Password Here";
    private HashMap<Integer, Employee> employees;
    // jobs = {jobID : job}
    private HashMap<Integer, Job> jobs;
    // employee_job_titles = {empID : job}
    private HashMap<Integer, Integer> employee_job_titles;
    // divisions = {divID : division}
    private HashMap<Integer, Division> divisions;
    // employee_division {empID : divID}
    private HashMap<Integer, Integer> employee_division;
    private HashMap<Integer, List<Payroll>> payrolls;
    private HashMap<Integer, Address> address;

    public Admin() {
        init();
        clearConsole();
    }

    // region INITIALIZE

    private void init() {
        // Map empID to employee object
        employees = new HashMap<>();
        jobs = new HashMap<>();
        employee_job_titles = new HashMap<>();
        divisions = new HashMap<>();
        employee_division = new HashMap<>();
        payrolls = new HashMap<>();

        // Initialize jobs locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM job_titles";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                Job job = new Job();
                job.setJobID(rs.getInt("job_title_id"));
                job.setTitle(rs.getString("job_title"));

                jobs.put(job.getJobID(), job);
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // Initialize employee_job_titles locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM employee_job_titles";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                employee_job_titles.put(rs.getInt("empid"), rs.getInt("job_title_id"));
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // Initialize divisions locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM division";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                Division division = new Division();
                division.setDivisionID(rs.getInt("ID"));
                division.setName(rs.getString("Name"));
                division.setCity(rs.getString("city"));
                division.setAddressLine1(rs.getString("addressLine1"));
                division.setAddressLine2(rs.getString("addressLine2"));
                division.setState(rs.getString("state"));
                division.setCountry(rs.getString("country"));
                division.setPostalCode(rs.getString("postalCode"));

                divisions.put(division.getDivisionID(), division);
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // Initialize employee_division locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM employee_division";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                employee_division.put(rs.getInt("empid"), rs.getInt("div_ID"));
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // Initialize employees locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM employees";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmpID(rs.getInt("empid"));
                employee.setFirstName(rs.getString("Fname"));
                employee.setLastName(rs.getString("Lname"));
                employee.setEmail(rs.getString("email"));
                employee.setHireDate(rs.getDate("HireDate"));
                employee.setSalary(rs.getDouble("Salary"));
                employee.setSsn(rs.getString("ssn"));
                employees.put(employee.getEmpID(), employee);
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // Initialize address locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM address";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                Address a = new Address();
                a.setEmpID(rs.getInt("empid"));
                a.setGender(rs.getString("gender"));
                a.setPronouns(rs.getString("pronouns"));
                a.setIdentifiedRace(rs.getString("identified_race"));
                a.setDob(rs.getDate("dob"));
                a.setPhone(rs.getString("mobile_phone"));
                a.setCityID(rs.getInt("city_id"));
                a.setStateID(rs.getInt("state_id"));
                address.put(a.getEmpID(), a);
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // Initialize payroll locally
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            String query = "SELECT * FROM payroll";
            ResultSet rs = myStmt.executeQuery(query);

            while (rs.next()) {
                Payroll payroll = new Payroll();
                payroll.setPayID(rs.getInt("payID"));
                payroll.setPayDate(rs.getDate("pay_date"));
                payroll.setEarnings(rs.getDouble("earnings"));
                payroll.setFedTax(rs.getDouble("fed_tax"));
                payroll.setFedMed(rs.getDouble("fed_med"));
                payroll.setFedSS(rs.getDouble("fed_SS"));
                payroll.setStateTax(rs.getDouble("state_tax"));
                payroll.setRetire401k(rs.getDouble("retire_401k"));
                payroll.setHealthCare(rs.getDouble("health_care"));
                payroll.setEmpID(rs.getInt("empid"));

                if (!payrolls.containsKey(payroll.getEmpID())) {
                    List<Payroll> l = new ArrayList<>();
                    l.add(payroll);
                    payrolls.put(payroll.getEmpID(), l);
                } else {
                    payrolls.get(payroll.getEmpID()).add(payroll);
                }
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }

        // for (List<Payroll> l : payrolls.values()) {
        // System.out.println(l.size());
        // }
    }

    // endregion

    // region DISPLAY OPTIONS

    public boolean displayMenu() {
        System.out.println();
        System.out.println("Please select an option from the menu below");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("0: Quit");
        System.out.println("1: Get Employee Information with Pay Statement History");
        System.out.println("2: Get Total Pay for Month by Job Title");
        System.out.println("3: Get Total Pay for Month by Division");
        System.out.println("4: Search for employee(s)");
        System.out.println("5: Update employee(s)");
        System.out.println("6: Add employee(s)");
        System.out.println("7: Delete employee(s)");
        int option = validateIntegerInput(0, 7, true);

        switch (option) {
            case 0:
                clearConsole();
                return false;
            case 1:
                clearConsole();
                displaySearchSelection(true);
                break;
            case 2:
                clearConsole();
                getTotalPayForMonthByJobTitle();
                break;
            case 3:
                clearConsole();
                getTotalPayForMonthByDivision();
                break;
            case 4:
                clearConsole();
                displaySearchSelection(false);
                break;
            case 5:
                clearConsole();
                displayUpdateSelection();
                break;
            case 6:
                clearConsole();
                addEmployee();
                break;
            case 7:
                clearConsole();
                deleteEmployee();
                break;
            default:
                System.out.println("That is not a valid option. Please select an option from the menu");
        }
        return true;
    }

    private List<Employee> displaySearchSelection(boolean payHistory) {
        clearConsole();
        System.out.println("Select an option");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("1: Search by employee ID");
        System.out.println("2: Search by employee SSN");
        System.out.println("3: Search by employee NAME");
        System.out.println("4: Search by employee SALARY");
        System.out.println("5: Search by employee TYPE");
        System.out.println();
        int option = validateIntegerInput(1, 5, true);
        List<Employee> e = new ArrayList<>();

        switch (option) {
            case 1:
                e = getEmployee(getEmployeeByID(), payHistory);
                break;
            case 2:
                e = getEmployee(getEmployeeBySSN(), payHistory);
                break;
            case 3:
                e = getEmployee(getEmployeeByName(), payHistory);
                break;
            case 4:
                e = getEmployee(getEmployeeBySalary(displaySalarySelection()), payHistory);
                break;
            case 5:
                e = getEmployee(getEmployeeByType(), payHistory);
                break;
        }
        System.out.println();
        return e;
    }

    private void displayUpdateSelection() {
        clearConsole();
        System.out.println("What would you like to update?");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("1: First Name");
        System.out.println("2: Last Name");
        System.out.println("3: Email");
        System.out.println("4: Hire Date");
        System.out.println("5: Salary");
        System.out.println("6: SSN");
        int option = validateIntegerInput(1, 6, true);

        switch (option) {
            case 1:
                updateEmployeeFirstName();
                break;
            case 2:
                updateEmployeeLastName();
                break;
            case 3:
                updateEmployeeEmail();
                break;
            case 4:
                updateEmployeeHireDate();
                break;
            case 5:
                HashMap<String, Boolean> choice = new HashMap<>();
                choice.put("batch", displayBatchUpdateSelection());
                choice.put("fixed", displaySalaryAmountSelection());
                choice.put("range", displaySalarySelection());
                updateEmployeeSalary(getEmployee(getEmployeeBySalary(choice.get("range")), false), choice.get("batch"),
                        choice.get("fixed"));
                // updateEmployeeSalary(displayBatchUpdateSelection());
                break;
            case 6:
                updateEmployeeSSN();
                break;
        }
    }

    private boolean displaySalaryAmountSelection() {
        clearConsole();
        System.out.println("How would you like to update employee(s) salary?");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("1: Update by a fixed amount");
        System.out.println("2: Increase by a percentage");
        int option = validateIntegerInput(1, 2, true);

        switch (option) {
            case 1:
                return true;
            case 2:
                return false;
        }
        return false;
    }

    private boolean displaySalarySelection() {
        clearConsole();
        System.out.println("How would you like to select employee(s)?");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("1: Select employee with a salary amount");
        System.out.println("2: Select employee within salary range");
        int option = validateIntegerInput(1, 2, true);

        switch (option) {
            case 1:
                return false;
            case 2:
                return true;
        }
        return false;
    }

    private boolean displayBatchUpdateSelection() {
        clearConsole();
        System.out.println("How would you like to update each employee?");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("1: Update all employees");
        System.out.println("2: Update per employee");
        int option = validateIntegerInput(1, 2, true);

        switch (option) {
            case 1:
                return true;
            case 2:
                return false;
        }
        return false;
    }

    private void displayJobSelection() {
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        for (Job job : jobs.values()) {
            System.out.println("Job ID: " + job.getJobID() + ": " + job.getTitle());
        }
    }

    private void displayDivisionSelection() {
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        for (Division div : divisions.values()) {
            System.out.println("Division ID: " + div.getDivisionID() + ": " + div.getName());
        }
    }

    private void clearConsole() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    // endregion

    // region ADD/DELETE

    public void addEmployee() {
        clearConsole();
        System.out.print("How many employees would you like to add? ");
        int numOfEmp = validateIntegerInput(0, 0, false);

        for (int i = 0; i < numOfEmp; i++) {
            System.out.println();
            System.out.println(String.format("Adding employee %d", i + 1));
            Employee newEmp = createEmployee();
            int empID;
            try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                String sqlCommand = "INSERT INTO employees (Fname, Lname, email, HireDate, Salary, ssn) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = myConn.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, newEmp.getFirstName());
                ps.setString(2, newEmp.getLastName());
                ps.setString(3, newEmp.getEmail());
                ps.setDate(4, newEmp.getHireDate());
                ps.setDouble(5, newEmp.getSalary());
                ps.setString(6, newEmp.getSsn());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                empID = rs.getInt(1);

                sqlCommand = "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?)";
                ps = myConn.prepareStatement(sqlCommand);
                ps.setInt(1, empID);
                ps.setInt(2, newEmp.getJobID());
                ps.executeUpdate();

                sqlCommand = "INSERT INTO employee_division (empid, div_ID) VALUES (?, ?)";
                ps = myConn.prepareStatement(sqlCommand);
                ps.setInt(1, empID);
                ps.setInt(2, newEmp.getDivID());
                ps.executeUpdate();

                newEmp.setEmpID(empID);
                employees.put(empID, newEmp);
                getEmployee(Arrays.asList(newEmp), false);
                myConn.close();
            } catch (Exception e) {
                System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
            }
        }
    }

    private void deleteEmployee() {
        clearConsole();
        List<Employee> employee = displaySearchSelection(false);
        if (!employee.isEmpty()) {
            for (Employee e : employee) {
                int empID = e.getEmpID();
                try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                    Statement stmt = myConn.createStatement();
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                    // delete foreign key restraint first
                    String sqlCommand = "DELETE FROM employee_division WHERE empid = ?";
                    PreparedStatement ps = myConn.prepareStatement(sqlCommand);
                    ps.setInt(1, empID);
                    ps.executeUpdate();

                    // delete employee
                    sqlCommand = "DELETE FROM employees WHERE empid = ?";
                    ps = myConn.prepareStatement(sqlCommand);
                    ps.setInt(1, empID);
                    ps.executeUpdate();

                    employees.remove(empID);
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                    myConn.close();
                } catch (Exception error) {
                    System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
                }
            }
        } else {
            System.out.println("Selection is empty");
        }
    }

    // endregion

    // region CREATE

    private Employee createEmployee() {
        Employee newEmployee = new Employee();
        Calendar calendar = Calendar.getInstance();
        displayJobSelection();
        System.out.println();
        int temp;

        while (true) {
           try {
               System.out.print("Select Job Title (Enter ID): ");
               temp = validateIntegerInput(0, 0, false);
               if (temp == 100 || temp == 900 || temp == 101 || temp == 901 ||
               temp == 102 || temp == 902 || temp == 103 || temp == 200 ||
               temp == 201 || temp == 202) {
                   break; 
               } else {
                   System.out.println("Invalid input. Please enter one of the provided Job ID.");
               }
           } catch (Exception e) {
               System.out.println("ERROR! Enter Job ID again.");
           }
       }
       newEmployee.setJobID(temp);

       displayDivisionSelection();
       System.out.println();
       while (true) {
           try {
               System.out.print("What is the Division Name? (Enter ID): ");
               temp = validateIntegerInput(0, 0, false);
               if (temp == 1 || temp == 2 || temp == 3 || temp == 999) {
                   break; 
               } else {
                   System.out.println("Invalid input. Please enter one of the provided Division ID.");
               }
           } catch (Exception e) {
               System.out.println("ERROR! Enter Division ID again.");
           }
       }
       newEmployee.setDivID(temp);

        System.out.print("What is the First Name? ");
        newEmployee.setFirstName(validateStringInput());
        System.out.print("What is the Last Name? ");
        newEmployee.setLastName(validateStringInput());
        System.out.print("What is the Email? ");
        newEmployee.setEmail(validateStringInput());
        System.out.println("What is the Hire Date?");
        System.out.print("Enter Month: ");
        // Calendar.MONTH starts at index 0 = January ~ 11 = December
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.print("Enter Day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.print("Enter Year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date date = new Date(calendar.getTimeInMillis());
        newEmployee.setHireDate(date);
        System.out.print("What is the Salary? ");
        newEmployee.setSalary(validateDoubleInput());
        System.out.print("What is the SSN (no dashes)? ");
        // first validate SSN is numerical, then convert to String
        newEmployee.setSsn(String.valueOf(validateSSNInput()));

        return newEmployee;
    }

    public void addPayroll() {
        Payroll payroll = createPayroll();

        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            String sqlCommand = "INSERT INTO payroll (payID, pay_date, earnings, fed_tax, fed_med, fed_SS, state_tax, retire_401k, health_care, empid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = myConn.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, payroll.getPayID());
            ps.setDate(2, payroll.getPayDate());
            ps.setDouble(3, payroll.getEarnings());
            ps.setDouble(4, payroll.getFedTax());
            ps.setDouble(5, payroll.getFedMed());
            ps.setDouble(6, payroll.getFedSS());
            ps.setDouble(7, payroll.getStateTax());
            ps.setDouble(8, payroll.getRetire401k());
            ps.setDouble(9, payroll.getHealthCare());
            ps.setInt(10, payroll.getEmpID());
            ps.executeUpdate();
            System.out.println("Executed adding payroll");

            if (!payrolls.containsKey(payroll.getEmpID())) {
                List<Payroll> l = new ArrayList<>();
                l.add(payroll);
                payrolls.put(payroll.getEmpID(), l);
            } else {
                payrolls.get(payroll.getEmpID()).add(payroll);
            }

            myConn.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }
    }

    private Payroll createPayroll() {
        Payroll payroll = new Payroll();
        Calendar calendar = Calendar.getInstance();
        // payID is auto incremented based on current value tied with empID
        System.out.print("What is the Pay ID? ");
        payroll.setPayID(validateIntegerInput(0, 0, false));
        System.out.print("What is the Pay Date? Enter Month: ");
        // Calendar.MONTH starts at index 0 = January ~ 11 = December
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.print("What is the Pay Date? Enter Day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.print("What is the Pay Date? Enter Year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date date = new Date(calendar.getTimeInMillis());

        payroll.setPayDate(date);
        System.out.print("What is the Earnings? ");
        payroll.setEarnings(validateDoubleInput());
        System.out.print("What is the Federal Tax? ");
        payroll.setFedTax(validateDoubleInput());
        System.out.print("What is the Federal Med? ");
        payroll.setFedMed(validateDoubleInput());
        System.out.print("What is the Federal SS? ");
        payroll.setFedSS(validateDoubleInput());
        System.out.print("What is the State Tax? ");
        payroll.setStateTax(validateDoubleInput());
        System.out.print("What is the Retire 401k? ");
        payroll.setRetire401k(validateDoubleInput());
        System.out.print("What is the Health Care? ");
        payroll.setHealthCare(validateDoubleInput());
        System.out.print("What is the Employee ID? ");
        payroll.setEmpID(validateIntegerInput(0, 0, false));

        return payroll;
    }

    private Address createAddress() {
        Address address = new Address();
        Calendar calendar = Calendar.getInstance();

        System.out.println("What is the Gender (M or F)? ");
        address.setGender(validateStringInput());
        System.out.println("What are the pronouns? ");
        address.setPronouns(validateStringInput());
        System.out.println("What is the Identified Race? ");
        address.setIdentifiedRace(validateStringInput());
        System.out.println("What is the Date of Birth? Enter Month: ");
        // Calendar.MONTH starts at index 0 = January ~ 11 = December
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.println("What is the Date of Birth? Enter Day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.println("What is the Date of Birth? Enter Year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date date = new Date(calendar.getTimeInMillis());

        address.setDob(date);
        System.out.println("What is the Mobile Phone Number (no dashes)? ");
        address.setPhone(String.valueOf(validateLongInput()));
        // figure out what city and state ID are...
        System.out.println("What is the City ID? ");
        address.setCityID(validateIntegerInput(0, 0, false));
        System.out.println("What is the State ID? ");
        address.setStateID(validateIntegerInput(0, 0, false));
        return address;
    }

    // endregion

    // region GET REPORTS

    private void getTotalPayForMonthByJobTitle() {
        displayJobSelection();
        System.out.println();
        int jobID;
        while (true) {
            try {
                System.out.print("Select Job Title (Enter ID): ");
                jobID = validateIntegerInput(0, 0, false);
                if (jobID == 100 || jobID == 900 || jobID == 101 || jobID == 901 ||
                jobID == 102 || jobID == 902 || jobID == 103 || jobID == 200 ||
                jobID == 201 || jobID == 202) {
                    break; 
                } else {
                    System.out.println("Invalid input. Please enter one of the provided Job ID.");
                }
            } catch (Exception e) {
                System.out.println("ERROR! Enter Job ID again.");
            }
        }
        Calendar calendar = Calendar.getInstance();
        System.out.println("Select a date range");
        System.out.print("Enter starting month: ");
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.print("Enter starting day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.print("Enter starting year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date startDate = new Date(calendar.getTimeInMillis());

        System.out.print("Enter ending month: ");
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.print("Enter ending day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.print("Enter ending year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date endDate = new Date(calendar.getTimeInMillis());
        double totalPay = 0;
        String job_title = "";
        for (int empID : employee_job_titles.keySet()) {
            int job_title_id = employee_job_titles.getOrDefault(empID, 0);
            if (jobID == job_title_id) {
                job_title = jobs.get(jobID).getTitle();
                for (int eID : payrolls.keySet()) {
                    if (empID == eID) {
                        List<Payroll> p = payrolls.get(eID);
                        for (Payroll pr : p) {
                            if (pr.getPayDate().after(startDate) && pr.getPayDate().before(endDate)) {
                                totalPay += pr.getEarnings();

                            }
                        }
                    }
                }
            }
        }

        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out
                .println("Total Pay between " + startDate + " and " + endDate + " for " + job_title + ": $" + totalPay);
    }

    private void getTotalPayForMonthByDivision() {
        displayDivisionSelection();
        System.out.println();
        int divID; 
        while (true) {
            try {
                System.out.print("Select Division (Enter ID): ");
                divID = validateIntegerInput(0, 0, false);
                if (divID == 1 || divID == 2 || divID == 3 || divID == 999) {
                    break; 
                } else {
                    System.out.println("Invalid input. Please enter one of the provided Division ID.");
                }
            } catch (Exception e) {
                System.out.println("ERROR! Enter Division ID again.");
            }
        }

        Calendar calendar = Calendar.getInstance();
        System.out.println("Select a date range");
        System.out.print("Enter starting month: ");
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.print("Enter starting day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.print("Enter starting year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date startDate = new Date(calendar.getTimeInMillis());

        System.out.print("Enter ending month: ");
        calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
        System.out.print("Enter ending day: ");
        calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
        System.out.print("Enter ending year: ");
        calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
        Date endDate = new Date(calendar.getTimeInMillis());
        double totalPay = 0;
        String divName = "";

        // // check employee_division for matching divID and store empID in list
        // List<Integer> empIDList = new ArrayList<>();
        // for (int i : employee_division.keySet()) {
        // if (i == divID) {
        // empIDList.add(divID);
        // }
        // }

        // // we now have a list of employee IDs and need to grab a list of employees
        // int left = 0;
        // int right = 0;
        // List<Employee> employeeList = new ArrayList<>();
        // for (int empID : employees.keySet()) {
        // if (empIDList.contains(empID)) {
        // employeeList.add(employees.get(empID));
        // }
        // }

        // // now we have a list of employees and check date range
        // for (Employee e : employeeList) {
        // if ()
        // }

        // brute force
        for (int empID : employee_division.keySet()) {
            int div_id = employee_division.getOrDefault(empID, 0);

            if (divID == div_id) {
                divName = divisions.get(divID).getName();
                for (int eID : payrolls.keySet()) {
                    if (empID == eID) {
                        List<Payroll> p = payrolls.get(eID);
                        for (Payroll pr : p) {
                            if (pr.getPayDate().after(startDate) && pr.getPayDate().before(endDate)) {
                                totalPay += pr.getEarnings();
                            }
                        }
                    }
                }
            }
        }

        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.println("Total Pay between " + startDate + " and " + endDate + " for " + divName + ": $" + totalPay);
    }

    // endregion

    // region INPUT VALIDATION

    private long validateSSNInput() {
        long input = 0;
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                input = s.nextLong();
                if (Long.toString(input).length() == 9) {
                    break;
                } else {
                    System.out.println("Invalid SSN (should be 9 digits)");
                }
            } catch (Exception e) {
                System.out.println(e);
                // ignore what user typed to avoid infinite loop
                s.nextLine();
            }
        }
        s.close();
        return input;
    }

    private String validateStringInput() {
        String input = "";
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                input = s.nextLine();
                break;
            } catch (Exception e) {
                System.out.println(e);
                // ignore what user typed to avoid infinite loop
                s.nextLine();
            }
        }
        s.close();
        return input;
    }

    private int validateIntegerInput(int min, int max, boolean limit) {
        int input = 0;
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                input = s.nextInt();
                if (limit) {
                    if (input >= min && input <= max) {
                        break;
                    } else {
                        System.out.println("Invalid selection. Enter again");
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println(e);
                // ignore what user typed to avoid infinite loop
                s.nextLine();
            }
        }
        s.close();
        return input;
    }

    private long validateLongInput() {
        long input = 0;
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                input = s.nextLong();
                break;
            } catch (Exception e) {
                System.out.println(e);
                // ignore what user typed to avoid infinite loop
                s.nextLine();
            }
        }
        s.close();
        return input;
    }

    private double validateDoubleInput() {
        double input = 0.0;
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                input = s.nextDouble();
                break;
            } catch (Exception e) {
                System.out.println(e);
                // ignore what user typed to avoid infinite loop
                s.nextLine();
            }
        }
        return input;
    }

    // endregion

    // region SEARCH EMPLOYEE

    private List<Employee> getEmployee(List<Employee> employee, boolean payHistory) {
        for (Employee e : employee) {
            System.out.println(
                    "---------------------------------------------------------------------------------------------------------");
            System.out.println("Employee ID : " + e.getEmpID());
            System.out.println("Full Name   : " + e.getFirstName() + " " + e.getLastName());
            System.out.println("Email       : " + e.getEmail());
            System.out.println("Hire Date   : " + e.getHireDate());
            System.out.println(String.format("Salary      : $%.2f", e.getSalary()));
            System.out.println("SSN         : " + e.getSsn());
            if (payHistory) {
                System.out.printf("%nPay Statement History for %s %s%n", e.getFirstName(), e.getLastName());
                System.out.println(
                        "---------------------------------------------------------------------------------------------------------");
                System.out.printf("| %-3s | %-10s | %-8s | %-8s | %-8s | %-8s | %-10s | %-11s | %-11s |%n", "ID",
                        "PAY DATE", "EARNINGS", "FED TAX", "FED MED", "FED SS", "STATE TAX", "RETIRE 401K",
                        "HEALTH CARE");
                for (Payroll p : payrolls.get(e.getEmpID())) {
                    System.out.printf("| %-3s | %-10s | %-8s | %-8s | %-8s | %-8s | %-10s | %-11s | %-11s |%n",
                            p.getPayID(), p.getPayDate(), p.getEarnings(), p.getFedTax(), p.getFedMed(), p.getFedSS(),
                            p.getStateTax(), p.getRetire401k(), p.getHealthCare());
                }
            }
        }
        return employee;
    }

    private List<Employee> getEmployeeByID() {
        clearConsole();
        // System.out.print("Enter employee ID or multiple ID separated by a comma (1,
        // 5, 7, 12, 55): ");
        // String employeeStringID[] = validateStringInput().trim().split("[\\s,]+");
        List<Integer> employeeIntID = new ArrayList<>();

        while (true) {
            try {
                System.out.print("Enter employee ID or multiple ID separated by a comma (1, 5, 7, 12, 55): ");
                String employeeStringID[] = validateStringInput().trim().split("[\\s,]+");
                for (String s : employeeStringID) {
                    employeeIntID.add(Integer.parseInt(s));
                }
                break;
            } catch (Exception e) {
                // System.out.println("ERROR: " + e);
                System.out.println("There was an error with the input. Enter input again");
                System.out.println();
            }
        }

        // for (String s : employeeStringID) {
        // employeeIntID.add(Integer.parseInt(s));
        // }
        List<Employee> e = new ArrayList<>();
        for (int i : employeeIntID) {
            if (employees.containsKey(i)) {
                e.add(employees.get(i));
            } else {
                System.out.println("Employee with ID: " + i + " does not exist");
            }
        }
        return e;
    }

    private List<Employee> getEmployeeBySSN() {
        clearConsole();
        System.out.print("Enter employee SSN or multiple SSN separated by a comma (215961123, 214398841, 291298391): ");
        String employeeStringSSN[] = validateStringInput().trim().split("[\\s,]+");
        List<Employee> e = new ArrayList<>();
        for (Employee employee : employees.values()) {
            for (String s : employeeStringSSN) {
                if (s.equals(employee.getSsn())) {
                    e.add(employee);
                }
            }
        }
        return e;
    }

    private List<Employee> getEmployeeByName() {
        clearConsole();
        System.out.print(
                "Enter employee Name or multiple Names separated by a comma (John Doe, Jane Doe, Dwight Schrute): ");
        String employeeStringName[] = validateStringInput().split("[,]+");
        List<Employee> e = new ArrayList<>();
        for (Employee employee : employees.values()) {
            String empName = employee.getFirstName() + " " + employee.getLastName();
            for (String s : employeeStringName) {
                if (empName.toLowerCase().contains(s.trim().toLowerCase())) {
                    e.add(employee);
                }
            }
        }
        return e;
    }

    
    private List<Employee> getEmployeeBySalary(boolean range) {

        clearConsole();
        List<Employee> e = new ArrayList<>();
        try {
            if (!range) {
                System.out.print(
                        "Enter employee Salary or multiple Salaries separated by a comma (: 140000.00, 66000, 88000.00): ");
                String employeeSalaries[] = validateStringInput().trim().split("[\\s,]+");
                for (Employee employee : employees.values()) {
                    for (String s : employeeSalaries) {
                        BigDecimal bds = new BigDecimal(s);
                        BigDecimal bde = new BigDecimal(employee.getSalary());
                        if (bds.compareTo(bde) == 0) {
                            e.add(employee);
                        }
                    }
                }
            } else {
                System.out.println("Enter employee Salary Range");
                System.out.print("What is the minimum (inclusive) Salary amount: ");
                double minSalary = validateDoubleInput();
                System.out.print("What is the maximum (inclusive) Salary amount: ");
                double maxSalary = validateDoubleInput();

                for (Employee employee : employees.values()) {
                    if (employee.getSalary() >= minSalary && employee.getSalary() <= maxSalary) {
                        e.add(employee);
                    }
                }
            }

        } catch (Exception y) {
            System.out.println();
            try {
                for (int i = 7; i >= 1; i--) {
                    System.out.print("\r");
                    System.out.printf("ERROR! Invalid input, going back to previous page. Try again in : %2d", i);
                    Thread.sleep(1000); 
                }
            } catch (Exception z) {
                System.out.println("Error!: ");
            }
            getEmployee(getEmployeeBySalary(displaySalarySelection()), range); // just added range before leaving
        }
        return e;
    }

    private List<Employee> getEmployeeByType() {
        clearConsole();
        System.out.println("Select an employee type");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.print("1: Full Time");
        System.out.print("2: Part Time");
        int input = validateIntegerInput(1, 2, true);
        List<Employee> e = new ArrayList<>();
        switch (input) {
            case 1:
                for (Employee employee : employees.values()) {
                    if (employee.getEmployeeType()) {
                        e.add(employee);
                    }
                }
                break;

            case 2:
                for (Employee employee : employees.values()) {
                    if (!employee.getEmployeeType()) {
                        e.add(employee);
                    }
                }
                break;
        }
        return e;
    }

    // endregion

    // region UPDATE EMPLOYEE

    private void updateEmployeeFirstName() {
        // clearConsole();
        List<Employee> employee = displaySearchSelection(false);
        for (Employee e : employee) {
            int eID = e.getEmpID();
            System.out.print(
                    String.format("For employee %s %s, what is the First Name? ", e.getFirstName(), e.getLastName()));
            String newValue = validateStringInput().trim();

            try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                String sqlCommad = "UPDATE employees SET Fname = ? WHERE empid = ?";
                PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                ps.setString(1, newValue);
                ps.setInt(2, eID);
                ps.executeUpdate();

                e.setFirstName(newValue);
                myConn.close();
            } catch (Exception error) {
                System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
            }
        }
    }

    private void updateEmployeeLastName() {
        List<Employee> employee = displaySearchSelection(false);
        for (Employee e : employee) {
            int eID = e.getEmpID();
            System.out.print(
                    String.format("For employee %s %s, what is the Last Name? ", e.getFirstName(), e.getLastName()));
            String newValue = validateStringInput().trim();

            try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                String sqlCommad = "UPDATE employees SET Lname = ? WHERE empid = ?";
                PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                ps.setString(1, newValue);
                ps.setInt(2, eID);
                ps.executeUpdate();

                e.setLastName(newValue);
                myConn.close();
            } catch (Exception error) {
                System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
            }
        }
    }

    private void updateEmployeeEmail() {
        List<Employee> employee = displaySearchSelection(false);
        for (Employee e : employee) {
            int eID = e.getEmpID();
            System.out.print(String.format("For employee %s %s with email %s, what is the Email? ", e.getFirstName(),
                    e.getLastName(), e.getEmail()));
            String newValue = validateStringInput().trim();

            try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                String sqlCommad = "UPDATE employees SET email = ? WHERE empid = ?";
                PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                ps.setString(1, newValue);
                ps.setInt(2, eID);
                ps.executeUpdate();

                e.setEmail(newValue);
                myConn.close();
            } catch (Exception error) {
                System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
            }
        }
    }

    private void updateEmployeeHireDate() {
        List<Employee> employee = displaySearchSelection(false);
        for (Employee e : employee) {
            int eID = e.getEmpID();
            Calendar calendar = Calendar.getInstance();
            System.out.print("For employee " + e.getFirstName() + " " + e.getLastName() + " with hire date "
                    + e.getHireDate() + ", what is the Hire Date? ");
            System.out.println();
            System.out.print("Enter Month: ");
            // Calendar.MONTH starts at index 0 = January ~ 11 = December
            calendar.set(Calendar.MONTH, validateIntegerInput(1, 12, true) - 1);
            System.out.print("Enter Day: ");
            calendar.set(Calendar.DATE, validateIntegerInput(1, 31, true));
            System.out.print("Enter Year: ");
            calendar.set(Calendar.YEAR, validateIntegerInput(1900, 2024, true));
            Date date = new Date(calendar.getTimeInMillis());
            // String newValue = validateStringInput().trim();

            try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                String sqlCommad = "UPDATE employees SET HireDate = ? WHERE empid = ?";
                PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                ps.setDate(1, date);
                ps.setInt(2, eID);
                ps.executeUpdate();

                e.setHireDate(date);
                myConn.close();
            } catch (Exception error) {
                System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
            }
        }
    }

    private void updateEmployeeSalary(List<Employee> emp, boolean batch, boolean fixed) {
        System.out.println();
        // List<Employee> employee = displaySearchSelection();
        List<Employee> employee = emp;
        // update per employee
        if (!batch) {
            for (Employee e : employee) {
                int eID = e.getEmpID();
                double newValue;
                if (fixed) {
                    // update with fixed $ amount
                    System.out.print(String.format("For employee %s %s with salary $%.2f, what is the Salary? ",
                            e.getFirstName(), e.getLastName(), e.getSalary()));
                    newValue = validateDoubleInput();
                } else {
                    // update with % amount
                    System.out.print(String.format(
                            "For employee %s %s with salary $%.2f, what is the %% increase? (22, 13.5, 18.03) ",
                            e.getFirstName(), e.getLastName(), e.getSalary()));
                    double percentage = validateDoubleInput() / 100.00;
                    newValue = e.getSalary() * percentage;
                }

                try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                    String sqlCommad = "UPDATE employees SET Salary = ? WHERE empid = ?";
                    PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                    ps.setDouble(1, newValue);
                    ps.setInt(2, eID);
                    ps.executeUpdate();

                    e.setSalary(newValue);
                    myConn.close();
                } catch (Exception error) {
                    System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
                }
            }
        } else {
            // update all selected employees in one batch
            if (fixed) {
                // update with fixed $ amount
                System.out.print("What is the new Salary amount: ");
                double newSalary = validateDoubleInput();

                for (Employee e : employee) {
                    int eID = e.getEmpID();

                    try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                        String sqlCommad = "UPDATE employees SET Salary = ? WHERE empid = ?";
                        PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                        ps.setDouble(1, newSalary);
                        ps.setInt(2, eID);
                        ps.executeUpdate();

                        e.setSalary(newSalary);
                        myConn.close();
                    } catch (Exception error) {
                        System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
                    }
                }
            } else {
                // update with % amount
                System.out.print("What is the %% increase? (22, 13.5, 18.03) ");
                double percentage = validateDoubleInput() / 100.00;

                for (Employee e : employee) {
                    int eID = e.getEmpID();
                    double newSalary = e.getSalary() * percentage;

                    try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                        String sqlCommad = "UPDATE employees SET Salary = ? WHERE empid = ?";
                        PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                        ps.setDouble(1, newSalary);
                        ps.setInt(2, eID);
                        ps.executeUpdate();

                        e.setSalary(newSalary);
                        myConn.close();
                    } catch (Exception error) {
                        System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
                    }
                }
            }
            // for (Employee e : employee) {
            // int eID = e.getEmpID();

            // try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            // String sqlCommad = "UPDATE employees SET Salary = ? WHERE empid = ?";
            // PreparedStatement ps = myConn.prepareStatement(sqlCommad);
            // ps.setDouble(1, newSalary);
            // ps.setInt(2, eID);
            // ps.executeUpdate();

            // e.setSalary(newSalary);
            // myConn.close();
            // } catch (Exception error) {
            // System.out.println("ERROR " + error.getLocalizedMessage());
            // }
            // }
        }
    }

    private void updateEmployeeSSN() {
        List<Employee> employee = displaySearchSelection(false);
        for (Employee e : employee) {
            int eID = e.getEmpID();
            System.out.print(String.format("For employee %s %s with ssn %s, what is the SSN? ", e.getFirstName(),
                    e.getLastName(), e.getSsn()));
            String newValue = validateStringInput().trim();

            try (Connection myConn = DriverManager.getConnection(url, user, password)) {
                String sqlCommad = "UPDATE employees SET ssn = ? WHERE empid = ?";
                PreparedStatement ps = myConn.prepareStatement(sqlCommad);
                ps.setString(1, newValue);
                ps.setInt(2, eID);
                ps.executeUpdate();

                e.setSsn(newValue);
                myConn.close();
            } catch (Exception error) {
                System.out.println("ERROR " + error.getLocalizedMessage() + ". Enter input agian");
            }
        }
    }

    // endregion

    // region TESTING

    private void queryTester() {
        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            Statement myStmt = myConn.createStatement();
            // String query = "SELECT e.*, ejt.job_title_id FROM employees e LEFT JOIN
            // employee_job_titles ejt ON e.empid = ejt.empid";
            String query = "SELECT e.*, ejt.job_title_id, ed.div_ID, p.* FROM employees e LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid LEFT JOIN employee_division ed ON e.empid = ed.empid LEFT JOIN payroll p ON e.empid = p.empid";
            // String query = "SELECT * FROM division";
            ResultSet rs = myStmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= colNum; i++) {
                    if (i > 1)
                        System.out.print(", ");
                    String colVal = rs.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + colVal);
                }
                System.out.println();
            }
            myConn.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage() + ". Enter input agian");
        }
    }

    // endregion
}
