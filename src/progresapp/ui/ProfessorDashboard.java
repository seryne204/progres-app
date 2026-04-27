package progresapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import progresapp.model.Course;
import progresapp.model.Professor;
import progresapp.service.UniversityService;

public class ProfessorDashboard extends JFrame {
    private final UniversityService service;

    public ProfessorDashboard(UniversityService service) {
        this.service = service;

        setTitle("Systeme de Gestion des Etudiants");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1120, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        Professor professor = service.getCurrentProfessor();
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(67, 46, 102));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Espace Enseignant");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel subtitle = new JLabel(professor.getFullName() + " | " + professor.getId() + " | " + professor.getSpecialty());
        subtitle.setForeground(new Color(228, 220, 240));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        left.add(title);
        left.add(Box.createRigidArea(new Dimension(0, 6)));
        left.add(subtitle);

        JButton logoutButton = new JButton("Deconnexion");
        logoutButton.addActionListener(event -> {
            dispose();
            new LoginFrame(service).setVisible(true);
        });

        header.add(left, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Mes modules", buildMyModulesTab());
        tabs.addTab("Etudiants par module", buildModuleStudentsTab());
        tabs.addTab("Saisie des notes", buildGradeEntryTab());
        tabs.addTab("Rattrapage", buildResitTab());
        return tabs;
    }

    private JPanel buildMyModulesTab() {
        JTable table = createTable(
            new String[] {"Code", "Module", "Coefficient", "Volume horaire"},
            service.getCurrentProfessorModuleRows()
        );
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildModuleStudentsTab() {
        JComboBox<String> courseBox = new JComboBox<>();
        populateCourseBox(courseBox, service.getCurrentProfessorCourses());

        JTable table = createTable(
            new String[] {"ID", "Etudiant", "CC", "Examen", "Rattrapage", "Moyenne finale"},
            service.getProfessorStudentRowsForCourse(extractId(String.valueOf(courseBox.getSelectedItem())))
        );

        courseBox.addActionListener(event -> refreshTable(
            table,
            service.getProfessorStudentRowsForCourse(extractId(String.valueOf(courseBox.getSelectedItem())))
        ));

        JPanel top = new JPanel(new GridLayout(2, 1, 10, 10));
        top.add(new JLabel("Module"));
        top.add(courseBox);

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildGradeEntryTab() {
        JComboBox<String> courseBox = new JComboBox<>();
        populateCourseBox(courseBox, service.getCurrentProfessorCourses());

        JComboBox<String> studentBox = new JComboBox<>();
        populateStudentsForCourse(studentBox, extractId(String.valueOf(courseBox.getSelectedItem())));
        courseBox.addActionListener(event -> populateStudentsForCourse(studentBox, extractId(String.valueOf(courseBox.getSelectedItem()))));

        JComboBox<String> typeBox = new JComboBox<>(new String[] {"CC", "Examen", "Rattrapage"});
        JTextField valueField = new JTextField("12");

        JTable table = createTable(
            new String[] {"Code", "Module", "CC", "Examen", "Moyenne", "Decision"},
            service.getGradesBeforeResitRows(extractId(String.valueOf(studentBox.getSelectedItem())))
        );

        studentBox.addActionListener(event -> refreshTable(table, service.getGradesBeforeResitRows(extractId(String.valueOf(studentBox.getSelectedItem())))));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.add(new JLabel("Etudiant"));
        form.add(studentBox);
        form.add(new JLabel("Module enseigne"));
        form.add(courseBox);
        form.add(new JLabel("Type de note"));
        form.add(typeBox);
        form.add(new JLabel("Valeur"));
        form.add(valueField);

        JButton saveButton = new JButton("Ajouter la note");
        saveButton.addActionListener(event -> {
            service.saveGrade(
                extractId(String.valueOf(studentBox.getSelectedItem())),
                extractId(String.valueOf(courseBox.getSelectedItem())),
                String.valueOf(typeBox.getSelectedItem()),
                Double.parseDouble(valueField.getText().trim())
            );
            refreshTable(table, service.getGradesBeforeResitRows(extractId(String.valueOf(studentBox.getSelectedItem()))));
        });

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(form, BorderLayout.NORTH);
        panel.add(saveButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResitTab() {
        JTable resitTable = createTable(
            new String[] {"ID", "Etudiant", "Code module", "Module", "Moyenne avant rattrapage"},
            service.getCurrentProfessorResitRows()
        );

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JScrollPane(resitTable), BorderLayout.CENTER);
        return panel;
    }

    private JTable createTable(String[] columns, List<Object[]> rows) {
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return table;
    }

    private void refreshTable(JTable table, List<Object[]> rows) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    private void populateStudentBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        for (progresapp.model.Student student : service.getStudents()) {
            comboBox.addItem(student.getId() + " - " + student.getFullName());
        }
    }

    private void populateCourseBox(JComboBox<String> comboBox, List<Course> courses) {
        comboBox.removeAllItems();
        for (Course course : courses) {
            comboBox.addItem(course.getId() + " - " + course.getName());
        }
    }

    private void populateStudentsForCourse(JComboBox<String> comboBox, String courseId) {
        comboBox.removeAllItems();
        for (Object[] row : service.getProfessorStudentRowsForCourse(courseId)) {
            comboBox.addItem(String.valueOf(row[0]) + " - " + String.valueOf(row[1]));
        }
    }

    private String extractId(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        int index = text.indexOf(" - ");
        return index >= 0 ? text.substring(0, index) : text;
    }
}
