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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import progresapp.model.Administrator;
import progresapp.model.Course;
import progresapp.model.Professor;
import progresapp.service.UniversityService;

public class AdministratorDashboard extends JFrame {
    private final UniversityService service;

    public AdministratorDashboard(UniversityService service) {
        this.service = service;

        setTitle("Espace Administrateur");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1080, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(246, 247, 250));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        Administrator administrator = service.getCurrentAdministrator();
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(39, 74, 108));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Espace Administrateur");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel subtitle = new JLabel(administrator.getFullName() + " | " + administrator.getId());
        subtitle.setForeground(new Color(220, 232, 242));
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
        tabs.addTab("Etudiants", buildStudentManagementTab());
        tabs.addTab("Modules", buildCourseManagementTab());
        tabs.addTab("Enseignants", buildProfessorManagementTab());
        tabs.addTab("Inscriptions", buildEnrollmentTab());
        tabs.addTab("Affectations", buildAssignmentTab());
        tabs.addTab("Resultats finaux", buildFinalResultsTab());
        return tabs;
    }

    private JPanel buildStudentManagementTab() {
        JTextField idField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField birthField = new JTextField("2004-01-01");
        JTextField emailField = new JTextField();
        JTextField departmentField = new JTextField("Informatique");
        JTextField levelField = new JTextField("ING2");
        JTextField yearField = new JTextField("2025/2026");
        JTextField passwordField = new JTextField("pass123");

        JTable table = createTable(
            new String[] {"ID", "Nom", "Prenom", "Naissance", "Email", "Departement", "Niveau"},
            service.getStudentListRows()
        );

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.add(new JLabel("ID"));
        form.add(idField);
        form.add(new JLabel("Prenom"));
        form.add(firstNameField);
        form.add(new JLabel("Nom"));
        form.add(lastNameField);
        form.add(new JLabel("Date naissance"));
        form.add(birthField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Departement"));
        form.add(departmentField);
        form.add(new JLabel("Niveau"));
        form.add(levelField);
        form.add(new JLabel("Annee"));
        form.add(yearField);
        form.add(new JLabel("Mot de passe"));
        form.add(passwordField);

        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(event -> {
            service.addStudent(new progresapp.model.Student(
                idField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                birthField.getText().trim(),
                emailField.getText().trim(),
                departmentField.getText().trim(),
                levelField.getText().trim(),
                yearField.getText().trim(),
                passwordField.getText().trim()
            ));
            refreshTable(table, service.getStudentListRows());
        });

        updateButton.addActionListener(event -> {
            service.updateStudent(new progresapp.model.Student(
                idField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                birthField.getText().trim(),
                emailField.getText().trim(),
                departmentField.getText().trim(),
                levelField.getText().trim(),
                yearField.getText().trim(),
                passwordField.getText().trim()
            ));
            refreshTable(table, service.getStudentListRows());
        });

        deleteButton.addActionListener(event -> {
            service.deleteStudent(idField.getText().trim());
            refreshTable(table, service.getStudentListRows());
        });

        return buildCrudPanel(form, table, addButton, updateButton, deleteButton);
    }

    private JPanel buildCourseManagementTab() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField coefficientField = new JTextField("2");
        JTextField hoursField = new JTextField("45");

        JTable table = createTable(
            new String[] {"ID", "Nom", "Coefficient", "Volume horaire", "Enseignant"},
            service.getCourseRows()
        );

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.add(new JLabel("ID"));
        form.add(idField);
        form.add(new JLabel("Nom"));
        form.add(nameField);
        form.add(new JLabel("Coefficient"));
        form.add(coefficientField);
        form.add(new JLabel("Volume horaire"));
        form.add(hoursField);

        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(event -> {
            service.addCourse(new Course(
                idField.getText().trim(),
                nameField.getText().trim(),
                Integer.parseInt(coefficientField.getText().trim()),
                Integer.parseInt(hoursField.getText().trim()),
                ""
            ));
            refreshTable(table, service.getCourseRows());
        });

        updateButton.addActionListener(event -> {
            Course current = service.findCourseById(idField.getText().trim());
            String professorId = current == null ? "" : current.getProfessorId();
            service.updateCourse(new Course(
                idField.getText().trim(),
                nameField.getText().trim(),
                Integer.parseInt(coefficientField.getText().trim()),
                Integer.parseInt(hoursField.getText().trim()),
                professorId
            ));
            refreshTable(table, service.getCourseRows());
        });

        deleteButton.addActionListener(event -> {
            service.deleteCourse(idField.getText().trim());
            refreshTable(table, service.getCourseRows());
        });

        return buildCrudPanel(form, table, addButton, updateButton, deleteButton);
    }

    private JPanel buildProfessorManagementTab() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField specialtyField = new JTextField();
        JTextField passwordField = new JTextField("profpass");

        JTable table = createTable(
            new String[] {"ID", "Nom", "Specialite"},
            service.getProfessorRows()
        );

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.add(new JLabel("ID"));
        form.add(idField);
        form.add(new JLabel("Nom"));
        form.add(nameField);
        form.add(new JLabel("Specialite"));
        form.add(specialtyField);
        form.add(new JLabel("Mot de passe"));
        form.add(passwordField);

        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(event -> {
            service.addProfessor(new Professor(idField.getText().trim(), nameField.getText().trim(), specialtyField.getText().trim(), passwordField.getText().trim()));
            refreshTable(table, service.getProfessorRows());
        });

        updateButton.addActionListener(event -> {
            service.updateProfessor(new Professor(idField.getText().trim(), nameField.getText().trim(), specialtyField.getText().trim(), passwordField.getText().trim()));
            refreshTable(table, service.getProfessorRows());
        });

        deleteButton.addActionListener(event -> {
            service.deleteProfessor(idField.getText().trim());
            refreshTable(table, service.getProfessorRows());
        });

        return buildCrudPanel(form, table, addButton, updateButton, deleteButton);
    }

    private JPanel buildAssignmentTab() {
        JComboBox<String> courseBox = new JComboBox<>();
        JComboBox<String> professorBox = new JComboBox<>();
        populateCourseBox(courseBox, service.getCourses());
        populateProfessorBox(professorBox);

        JTable table = createTable(
            new String[] {"ID", "Nom", "Coefficient", "Volume horaire", "Enseignant"},
            service.getCourseRows()
        );

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Module"));
        form.add(courseBox);
        form.add(new JLabel("Enseignant"));
        form.add(professorBox);

        JButton assignButton = new JButton("Affecter");
        assignButton.addActionListener(event -> {
            String courseId = extractId(String.valueOf(courseBox.getSelectedItem()));
            String professorId = extractId(String.valueOf(professorBox.getSelectedItem()));
            Course course = service.findCourseById(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Module introuvable.");
                return;
            }
            course.setProfessorId(professorId);
            service.updateCourse(course);
            populateCourseBox(courseBox, service.getCourses());
            populateProfessorBox(professorBox);
            refreshTable(table, service.getCourseRows());
            JOptionPane.showMessageDialog(this, "Affectation reussie!");
        });

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(form, BorderLayout.NORTH);
        panel.add(assignButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildEnrollmentTab() {
        JComboBox<String> studentBox = new JComboBox<>();
        JComboBox<String> courseBox = new JComboBox<>();
        JTextField dateField = new JTextField("2025-09-15");
        populateStudentBox(studentBox);
        populateCourseBox(courseBox, service.getCourses());

        JTable table = createTable(
            new String[] {"ID etudiant", "Etudiant", "ID module", "Module", "Date inscription"},
            service.getEnrollmentRows()
        );

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.add(new JLabel("Etudiant"));
        form.add(studentBox);
        form.add(new JLabel("Module"));
        form.add(courseBox);
        form.add(new JLabel("Date inscription"));
        form.add(dateField);

        JButton enrollButton = new JButton("Inscrire l'etudiant");
        enrollButton.addActionListener(event -> {
            boolean created = service.enrollStudent(
                extractId(String.valueOf(studentBox.getSelectedItem())),
                extractId(String.valueOf(courseBox.getSelectedItem())),
                dateField.getText().trim()
            );
            if (!created) {
                JOptionPane.showMessageDialog(this, "Cet etudiant est deja inscrit dans ce module.");
            }
            refreshTable(table, service.getEnrollmentRows());
        });

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(form, BorderLayout.NORTH);
        panel.add(enrollButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFinalResultsTab() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Etudiants diplomes", new JScrollPane(createTable(
            new String[] {"ID", "Etudiant", "Niveau", "Moyenne finale"},
            service.getGraduatedStudentsRows()
        )));
        tabs.addTab("Statut final", new JScrollPane(createTable(
            new String[] {"ID", "Etudiant", "Moyenne finale", "Statut", "Blocage"},
            service.getFinalStudentStatusRows()
        )));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCrudPanel(JPanel form, JTable table, JButton addButton, JButton updateButton, JButton deleteButton) {
        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 10));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
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

    private void populateProfessorBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        for (Professor professor : service.getProfessors()) {
            comboBox.addItem(professor.getId() + " - " + professor.getFullName());
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

    private String extractId(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        int index = text.indexOf(" - ");
        return index >= 0 ? text.substring(0, index) : text;
    }
}
