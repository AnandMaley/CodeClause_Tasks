import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HospitalInformationSystem extends JFrame {
    private JTextField nameField, ageField, genderField, historyField;
    private JButton addPatientButton, viewPatientsButton;

    public HospitalInformationSystem() {
        setTitle("Hospital Information System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        add(panel);
        placeComponents(panel, gbc);

        setVisible(true);
    }

    private void placeComponents(JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Medical History:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        gbc.gridy++;
        ageField = new JTextField(20);
        panel.add(ageField, gbc);
        gbc.gridy++;
        genderField = new JTextField(20);
        panel.add(genderField, gbc);
        gbc.gridy++;
        historyField = new JTextField(20);
        panel.add(historyField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        panel.add(new JLabel(" "), gbc); // spacing

        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        addPatientButton = new JButton("Add Patient");
        panel.add(addPatientButton, gbc);

        gbc.gridy++;
        panel.add(new JLabel(" "), gbc); // spacing

        gbc.gridy++;
        viewPatientsButton = new JButton("View Patients");
        panel.add(viewPatientsButton, gbc);

        addPatientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });

        viewPatientsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPatients();
            }
        });
    }

    private void addPatient() {
        String dbUrl = "jdbc:mysql://localhost/hospital";
        String dbUser = "root";
        String dbPassword = "maley@03";
    
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO patients (name, age, gender, medical_history) VALUES (?, ?, ?, ?)")) {
    
            statement.setString(1, nameField.getText());
            statement.setInt(2, Integer.parseInt(ageField.getText()));
            statement.setString(3, genderField.getText());
            statement.setString(4, historyField.getText());
    
            statement.executeUpdate();
    
            JOptionPane.showMessageDialog(this, "Patient added successfully!");
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding patient: " + ex.getMessage());
        }
    }
    

    private void viewPatients() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hospital", "root", "maley@03");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM patients");

            StringBuilder patientsInfo = new StringBuilder("Patients:\n");
            while (resultSet.next()) {
                patientsInfo.append("ID: ").append(resultSet.getInt("id"))
                        .append(", Name: ").append(resultSet.getString("name"))
                        .append(", Age: ").append(resultSet.getInt("age"))
                        .append(", Gender: ").append(resultSet.getString("gender"))
                        .append(", Medical History: ").append(resultSet.getString("medical_history"))
                        .append("\n");
            }

            connection.close();
            JOptionPane.showMessageDialog(this, patientsInfo.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error viewing patients.");
        }
    }
    public static void main(String[] args) {
        new HospitalInformationSystem();
    }
}

