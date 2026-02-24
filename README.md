# MotorPH Payroll System

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Apache NetBeans (for development and debugging)

## How to Run

### Run the Application

1. Open the project in NetBeans.
2. Once the project has fully initialized, click the Run button in the toolbar to launch the application.

## Login & Access Control

### Login Screen

When the application starts, you'll see a login window with:

- **Role Dropdown**: Select HR, Finance, or IT
- **Username**: (placeholder - not validated)
- **Password**: (placeholder - not validated)
- **Log In Button**: Click to proceed

### Role-to-Employee Mapping

- **HR** → Employee ID 1
- **Finance** → Employee ID 3
- **IT** → Employee ID 2

*Note: Username and password fields are not currently validated. The role selection determines which employee account is
used.*

## Application Screens

### 1. Employee Management Window (HR Only)

**Access**: HR role only

**Features**:

- Create, update, and delete employees
- View all employee records
- Calculate salaries (gross, allowances, deductions, net)
- Manage employee allowances, deductions, and attendance
- Access leave management for any employee
- **Top Bar**: View All Leave Requests button, Logout button

**Bottom Actions**: [Create] [Update] [Delete] [Clear]

### 2. Leave Management Window

#### For HR Users (via Employee Management)

**Access**: Click "View All Leave Requests" in Employee Management Window

**Features**:

- View all employee leave requests
- Approve or reject leave requests
- File leave requests for themselves
- **Top Bar**: Employee name, Logout button

**Bottom Actions**: [Refresh] [Approve Selected] [Reject Selected] [Close]

#### For Finance/IT Users (Direct Access)

**Access**: Automatically opens after login

**Features**:

- View only their own leave requests
- File new leave requests (start date, end date, reason)
- Cannot approve/reject other employees' leaves
- **Top Bar**: Employee name, Logout button

**Bottom Actions**: [Refresh] [Close]

## Access Control Matrix

| Feature               | HR | Finance | IT |
|-----------------------|----|---------|----|
| Employee Management   | ✅  | ❌       | ❌  |
| View All Leaves       | ✅  | ❌       | ❌  |
| View Own Leaves       | ✅  | ✅       | ✅  |
| File Leave Request    | ✅  | ✅       | ✅  |
| Approve/Reject Leaves | ✅  | ❌       | ❌  |
| Manage Employees      | ✅  | ❌       | ❌  |

## Logout

Click the **Logout** button in the top bar to return to the login screen.

## Data Storage

All data is stored in CSV files located in `src/main/resources/`:

- `employees.csv` - Employee records
- `leaves.csv` - Leave requests
- `allowances.csv` - Employee allowances
- `deductions.csv` - Employee deductions
- `tax_brackets.csv` - Tax bracket information
- `attendance_logs.csv` - Attendance records

