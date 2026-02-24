package dao;

import model.Leave;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVLeaveDAO extends CSVBaseDAO implements LeaveDAO {
    private final String filePath = getResourceFilePath("leaves.csv");

    @Override
    public List<Leave> getAll() {
        List<Leave> leaves = new ArrayList<>();
        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5) {
                    String reason = values.length >= 6 ? values[5].trim() : "";
                    Leave leave = new Leave(
                            Integer.parseInt(values[0].trim()),
                            Integer.parseInt(values[1].trim()),
                            LocalDate.parse(values[2].trim()),
                            LocalDate.parse(values[3].trim()),
                            values[4].trim(),
                            reason
                    );
                    leaves.add(leave);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return leaves;
    }

    @Override
    public Leave getLeaveById(int id) {
        return getAll().stream()
                .filter(leave -> leave.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Leave> getLeavesByEmployeeId(int employeeId) {
        List<Leave> result = new ArrayList<>();
        for (Leave leave : getAll()) {
            if (leave.getEmployeeId() == employeeId) {
                result.add(leave);
            }
        }
        return result;
    }

    @Override
    public void addLeave(Leave leave) {
        List<Leave> leaves = getAll();
        int newId = leaves.stream().mapToInt(Leave::getId).max().orElse(0) + 1;
        leave.setId(newId);
        leaves.add(leave);
        saveAllLeaves(leaves);
    }

    @Override
    public void updateLeave(Leave leave) {
        List<Leave> leaves = getAll();
        for (int i = 0; i < leaves.size(); i++) {
            if (leaves.get(i).getId() == leave.getId()) {
                leaves.set(i, leave);
                break;
            }
        }
        saveAllLeaves(leaves);
    }

    @Override
    public void deleteLeave(int id) {
        List<Leave> leaves = getAll();
        leaves.removeIf(leave -> leave.getId() == id);
        saveAllLeaves(leaves);
    }

    private void saveAllLeaves(List<Leave> leaves) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,employeeId,startDate,endDate,status,reason");
            bw.newLine();
            for (Leave leave : leaves) {
                bw.write(String.format("%d,%d,%s,%s,%s,%s",
                        leave.getId(),
                        leave.getEmployeeId(),
                        leave.getStartDate(),
                        leave.getEndDate(),
                        leave.getStatus(),
                        leave.getReason() != null ? leave.getReason() : ""));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

