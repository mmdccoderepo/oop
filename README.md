# MotorPH Payroll System

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Gradle (included via wrapper)

## How to Run

### Using Gradle

1. Open a terminal in the project directory
2. Run the application:
   ```bash
   # Windows
   .\gradlew run
   
   # Linux/Mac
   ./gradlew run
   ```

### Using NetBeans/IntelliJ IDEA

1. Open the project in your IDE
2. Wait for Gradle to sync
3. Run the `Main` class or use the IDE's Run button

## Login & Authentication

### Login Screen

When the application starts, you'll see a login window with:

- **Username**: Enter your username (based on email without domain)
- **Password**: Enter your password (employee ID)
- **Log In Button**: Click to proceed

### Sample Login Credentials

| Username        | Password | Role    | Name                  |
|-----------------|----------|---------|-----------------------|
| manuel.garcia   | 10001    | HR      | Manuel III Garcia     |
| antonio.lim     | 10002    | HR      | Antonio Lim           |
| bianca.aquino   | 10003    | Finance | Bianca Sofia Aquino   |
| isabella.reyes  | 10004    | HR      | Isabella Reyes        |

*Authentication is validated against `user_account.csv` and employee records are retrieved from `employees.csv`.*

## Application Structure

### Main Window: Time In / Out (All Users)

**Access**: Automatically opens after successful login

**Features**:
- Manual time-in and time-out entry (HH:mm format, 24-hour)
- View today's time tracking status
- View recent attendance logs
- Role-based navigation via menu bar

**Navigation Menu** (View):
- **HR Users**: Employee Management, Leave Requests
- **Finance Users**: Payroll, Leave Requests
- **All Users**: Leave Requests

**Account Menu**: Logout

### Employee Management Window (HR Only)

**Access**: HR role only - Navigate → Employee Management (from Time In/Out window)

**Features**:
- Create, update, and delete employees
- View all employee records in a table
- Manage employee details:
  - Personal information (name, email, phone, address)
  - Employment Type (Regular/Probationary)
  - Position Level (Managerial/Supervisory/Rank and File)
  - Role (HR/Finance/IT/Employee)
  - Government IDs (SSS, PhilHealth, TIN, Pag-IBIG)
  - Compensation (monthly for Regular, hourly for Probationary)
- Field validation for all inputs

**View Menu**: Time In/Out, Attendance Logs, Leave Requests

**Account Menu**: Logout

**Bottom Actions**: [Create] [Update] [Delete] [Clear] [Refresh]

### Payroll Window (Finance Only)

**Access**: Finance role only - Navigate → Payroll (from Time In/Out window)

**Features**:
- View list of all employees
- Select bi-weekly payroll period (1st-15th or 16th-end of month)
  - Automatically defaults to current period
- View individual employee attendance logs
- Compute payroll for selected employee
- Display detailed payslip showing:
  - Employee information
  - Compensation breakdown
  - Allowances (rice, phone, clothing)
  - Gross income
  - Deductions (SSS, PhilHealth, Pag-IBIG, Withholding Tax)
  - Net pay

**View Menu**: Time In/Out, Attendance Logs

**Account Menu**: Logout

**Payroll Actions**: [Refresh] [View Attendance] [Compute Payroll]

### Leave Management Window (All Users)

**Access**: Navigate → Leave Requests (from any main window)

**Features**:

#### For All Users:
- View their own leave requests
- File new leave requests:
  - Start Date (YYYY-MM-DD)
  - End Date (YYYY-MM-DD)
  - Reason
- View leave status (Pending/Approved/Rejected)
- See mapped leave records from original data

#### Additional Features for HR:
- View ALL employee leave requests
- Approve or reject pending leave requests
- See which employee filed each request

**View Menu**: Time In/Out, Attendance Logs

**Account Menu**: Logout

**Leave Actions**: [File Leave] [Refresh]  
**HR Additional Actions**: [Approve Selected] [Reject Selected]

### Attendance Logs Window (All Users)

**Access**: View → Attendance Logs (from any main window)

**Features**:
- View personal attendance records
- See date, time in, time out, and hours worked
- Display total hours worked across all records
- Reverse chronological order (most recent first)

**Actions**: [Refresh]

## Employee Types & Compensation

### Regular Employees
- **Compensation**: Monthly salary
- **Allowances**: Eligible based on position level
  - Rice Allowance
  - Phone Allowance
  - Clothing Allowance
- **Benefits**: SSS, PhilHealth, Pag-IBIG, Withholding Tax

### Probationary Employees
- **Compensation**: Hourly rate
- **Calculation**: Based on actual hours worked
- **Deductions**: SSS, PhilHealth, Pag-IBIG, Withholding Tax only

## Position Levels

- **Managerial**: Higher allowances, senior positions
- **Supervisory**: Medium allowances, supervisory roles
- **Rank and File**: Standard allowances, staff positions

## Roles (Department Equivalent)

- **HR**: Access to Employee Management
- **Finance**: Access to Payroll Management
- **IT**: Standard employee access
- **Employee**: Standard employee access

## Access Control Matrix

| Feature                      | HR | Finance | IT/Employee |
|------------------------------|----|------------|-------------|
| Time In/Out                  | ✅  | ✅          | ✅           |
| View Own Attendance Logs     | ✅  | ✅          | ✅           |
| Employee Management          | ✅  | ❌          | ❌           |
| Create/Update/Delete Employees| ✅  | ❌          | ❌           |
| Payroll Management           | ❌  | ✅          | ❌           |
| Compute Payroll              | ❌  | ✅          | ❌           |
| View Employee Attendance     | ❌  | ✅          | ❌           |
| View Own Leaves              | ✅  | ✅          | ✅           |
| File Leave Request           | ✅  | ✅          | ✅           |
| View All Leaves              | ✅  | ❌          | ❌           |
| Approve/Reject Leaves        | ✅  | ❌          | ❌           |

## Validation Rules

### Employee Form Validation:
- **First Name**: Required, letters only
- **Last Name**: Required, letters only
- **Email**: Required, valid email format, @motorph.com domain
- **Phone**: Required, format: XXX-XXX-XXX (numeric with dashes)
- **Address**: Required
- **SSS Number**: Required, format: XX-XXXXXXX-X (numeric with dashes)
- **PhilHealth Number**: Required, 12 digits (numeric only)
- **TIN**: Required, format: XXX-XXX-XXX-XXX (numeric with dashes)
- **Pag-IBIG Number**: Required, 12 digits (numeric only)
- **Compensation**: Required, positive number

### Time Entry Validation:
- **Format**: HH:mm (24-hour format)
- **Time Out**: Must be after Time In

### Leave Request Validation:
- **Start Date**: Required, format YYYY-MM-DD
- **End Date**: Required, format YYYY-MM-DD, must be after start date
- **Reason**: Required

## Payroll Calculation

### Bi-Weekly Periods:
- **Period 1**: 1st to 15th of the month
- **Period 2**: 16th to end of month

### Calculation Process:
1. **Hours Worked**: Sum of attendance logs within period
2. **Basic Salary**:
   - Regular: Monthly salary
   - Probationary: Hourly rate × Hours worked
3. **Allowances**: Added for Regular employees based on position level
4. **Gross Income**: Basic salary + Allowances
5. **Deductions**: SSS + PhilHealth + Pag-IBIG + Withholding Tax
6. **Net Pay**: Gross Income - Total Deductions

## Data Storage

All data is stored in CSV files located in `src/main/resources/`:

- **employees.csv** - Employee records with personal and employment details
- **user_account.csv** - Login credentials (username and password)
- **attendance_logs.csv** - Time in/out records
- **leaves.csv** - Leave requests and their status
- **allowances.csv** - Allowance amounts by position level
- **deductions.csv** - Deduction amounts by employee
- **tax_brackets.csv** - Tax bracket information

## Navigation Structure

All windows use a consistent menu bar structure:

### View Menu
- Time In / Out
- Attendance Logs (My Attendance Logs in some windows)
- Employee Management (HR only)
- Payroll (Finance only)
- Leave Requests

### Account Menu
- Logout

*Note: Navigation buttons have been removed in favor of the menu bar for a cleaner, more consistent user experience.*

## Logout

Select **Account → Logout** from the menu bar to return to the login screen.

## Technical Notes

- Built with Java Swing for UI
- Uses DAO pattern for data access
- Service layer for business logic
- CSV-based data persistence
- Gradle build system
- Follows OOP principles with inheritance (Regular/Probationary employees)

