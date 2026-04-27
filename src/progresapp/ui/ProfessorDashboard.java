package progresapp.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import progresapp.model.Grade;
import progresapp.model.Professor;
import progresapp.model.ProfessorScheduleEntry;
import progresapp.model.StudentRecord;
import progresapp.service.UniversityService;

public class ProfessorDashboard extends JFrame {
    private final UniversityService service;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final DefaultTableModel gradesModel;
    private JComboBox<StudentRecord> studentBox;

    public ProfessorDashboard(UniversityService service) {
        this.service = service;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.gradesModel = new DefaultTableModel(new String[] {"Code", "Module", "Etudiant", "Note"}, 0);

        setTitle("Espace Professeur - Version 3");
        setSize(1160, 700);
        setMinimumSize(new Dimension(1040, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(244, 246, 250));
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(new Color(72, 45, 103));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        Professor professor = service.getProfessor();

        JLabel title = new JLabel("Portail Professeur");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel identity = new JLabel("<html>" + professor.getFullName() + "<br/>" + professor.getId() + "</html>");
        identity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        identity.setForeground(new Color(229, 219, 243));
        identity.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(identity);
        sidebar.add(Box.createRigidArea(new Dimension(0, 28)));

        addMenuButton(sidebar, "Saisie des notes", "grading");
        addMenuButton(sidebar, "Emploi du temps", "schedule");
        addMenuButton(sidebar, "Sections", "sections");
        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = new JButton("Deconnexion");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutButton.setBackground(new Color(244, 236, 255));
        logoutButton.setForeground(new Color(72, 45, 103));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(event -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(97, 64, 136));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.addActionListener(event -> cardLayout.show(contentPanel, cardName));
        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(244, 246, 250));

        main.add(buildHeader(), BorderLayout.NORTH);

        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        contentPanel.setBackground(new Color(244, 246, 250));
        contentPanel.add(buildGradingPage(), "grading");
        contentPanel.add(wrapTable(buildScheduleTable()), "schedule");
        contentPanel.add(buildSectionsPage(), "sections");

        main.add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "grading");
        return main;
    }

    private JPanel buildHeader() {
        Professor professor = service.getProfessor();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 229, 234)),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel title = new JLabel("Espace professeur");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(57, 38, 86));

        JLabel subtitle = new JLabel(professor.getDepartment() + " | Annee " + professor.getAcademicYear());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(106, 101, 117));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);
        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JPanel buildGradingPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(new Color(244, 246, 250));

        JPanel form = new JPanel(new GridLayout(5, 1, 0, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(217, 221, 228)),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        studentBox = new JComboBox<>();
        for (StudentRecord studentRecord : service.getStudentRecords()) {
            studentBox.addItem(studentRecord);
        }
        studentBox.addActionListener(event -> refreshGradesTable());

        JComboBox<String> moduleBox = new JComboBox<>();
        for (String courseCode : service.getProfessorTaughtCourseCodes()) {
            for (StudentRecord studentRecord : service.getStudentRecords()) {
                for (Grade grade : studentRecord.getGrades()) {
                    if (grade.getCourse().getCode().equalsIgnoreCase(courseCode)) {
                        moduleBox.addItem(grade.getCourse().getCode() + " - " + grade.getCourse().getTitle());
                        break;
                    }
                }
                if (moduleBox.getItemCount() > 0 && String.valueOf(moduleBox.getItemAt(moduleBox.getItemCount() - 1)).startsWith(courseCode + " ")) {
                    break;
                }
            }
        }

        JTextField markField = new JTextField();

        form.add(createFieldPanel("Etudiant", studentBox));
        form.add(createFieldPanel("Module", moduleBox));
        form.add(createFieldPanel("Nouvelle note", markField));

        JButton saveGradeButton = new JButton("Ajouter / modifier la note");
        saveGradeButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        saveGradeButton.setBackground(new Color(88, 60, 126));
        saveGradeButton.setForeground(Color.WHITE);
        saveGradeButton.setFocusPainted(false);
        saveGradeButton.addActionListener(event -> {
            String selected = String.valueOf(moduleBox.getSelectedItem());
            String courseCode = selected.split(" - ")[0];
            StudentRecord selectedStudent = (StudentRecord) studentBox.getSelectedItem();
            if (selectedStudent == null) {
                return;
            }
            String registrationNumber = selectedStudent.getStudent().getRegistrationNumber();

            try {
                double newMark = Double.parseDouble(markField.getText().trim());
                if (newMark < 0 || newMark > 20) {
                    JOptionPane.showMessageDialog(this, "La note doit etre entre 0 et 20.", "Valeur invalide", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean updated = service.updateStudentGrade(registrationNumber, courseCode, newMark);
                if (updated) {
                    refreshGradesTable();
                    JOptionPane.showMessageDialog(this, "La note a ete enregistree.");
                } else {
                    JOptionPane.showMessageDialog(this, "Vous ne pouvez modifier que les modules que vous enseignez.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir une note numerique.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveGradeButton, BorderLayout.CENTER);
        form.add(new JPanel());
        form.add(buttonPanel);

        page.add(form, BorderLayout.NORTH);
        page.add(wrapTable(buildGradesTable()), BorderLayout.CENTER);
        return page;
    }

    private JPanel createFieldPanel(String label, java.awt.Component field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldLabel.setForeground(new Color(76, 78, 88));

        if (field instanceof JTextField textField) {
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        }
        if (field instanceof JComboBox<?> comboBox) {
            comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                JLabel itemLabel = new JLabel();
                itemLabel.setOpaque(true);
                itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

                if (value instanceof StudentRecord studentRecord) {
                    itemLabel.setText(formatStudentItem(studentRecord));
                } else {
                    itemLabel.setText(value == null ? "" : value.toString());
                }

                if (isSelected) {
                    itemLabel.setBackground(list.getSelectionBackground());
                    itemLabel.setForeground(list.getSelectionForeground());
                } else {
                    itemLabel.setBackground(list.getBackground());
                    itemLabel.setForeground(list.getForeground());
                }
                return itemLabel;
            });
        }

        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTable buildGradesTable() {
        refreshGradesTable();
        return createTable(gradesModel);
    }

    private JTable buildScheduleTable() {
        String[] columns = {"Jour", "Horaire", "Module", "Section", "Salle"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (ProfessorScheduleEntry entry : service.getProfessorSchedule()) {
            model.addRow(new Object[] {
                entry.getDay(),
                entry.getTime(),
                entry.getModule(),
                entry.getSection(),
                entry.getRoom()
            });
        }

        return createTable(model);
    }

    private JPanel buildSectionsPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(new Color(244, 246, 250));

        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);

        for (String section : service.getProfessorSections()) {
            JPanel card = new JPanel(new GridLayout(2, 1, 0, 8));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(217, 221, 228)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JLabel title = new JLabel(section);
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(new Color(57, 38, 86));

            JLabel subtitle = new JLabel("Section affectee au professeur");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitle.setForeground(new Color(107, 106, 116));

            card.add(title);
            card.add(subtitle);
            grid.add(card);
        }

        page.add(grid, BorderLayout.NORTH);
        return page;
    }

    private JPanel wrapTable(JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(244, 246, 250));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(217, 221, 228)));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(235, 229, 245));
        table.setGridColor(new Color(227, 231, 236));
        return table;
    }

    private void refreshGradesTable() {
        gradesModel.setRowCount(0);
        StudentRecord selectedStudent = studentBox == null ? null : (StudentRecord) studentBox.getSelectedItem();
        String registrationNumber = selectedStudent == null ? "" : selectedStudent.getStudent().getRegistrationNumber();

        for (StudentRecord studentRecord : service.getStudentRecords()) {
            if (!studentRecord.getStudent().getRegistrationNumber().equals(registrationNumber)) {
                continue;
            }

            for (Grade grade : studentRecord.getGrades()) {
                if (service.getProfessorTaughtCourseCodes().contains(grade.getCourse().getCode().toUpperCase())) {
                    gradesModel.addRow(new Object[] {
                        grade.getCourse().getCode(),
                        grade.getCourse().getTitle(),
                        studentRecord.getStudent().getFullName(),
                        grade.getMark()
                    });
                }
            }
        }
    }

    private String formatStudentItem(StudentRecord studentRecord) {
        return studentRecord.getStudent().getFullName() + " - " + studentRecord.getStudent().getRegistrationNumber();
    }

    private void logout() {
        dispose();
        LoginFrame loginFrame = new LoginFrame(service);
        loginFrame.setVisible(true);
    }
}
