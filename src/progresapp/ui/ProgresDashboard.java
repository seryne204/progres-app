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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import progresapp.model.Absence;
import progresapp.model.Grade;
import progresapp.model.ScheduleEntry;
import progresapp.model.Student;
import progresapp.service.UniversityService;

public class ProgresDashboard extends JFrame {
    private final UniversityService service;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    public ProgresDashboard(UniversityService service) {
        this.service = service;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        setTitle("Portail Etudiant - Version 2");
        setSize(1160, 700);
        setMinimumSize(new Dimension(1040, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(239, 244, 249));
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(19, 56, 92));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        Student student = service.getStudent();

        JLabel title = new JLabel("PROGRES Campus");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel("<html>" + student.getFullName() + "<br/>" + student.getRegistrationNumber() + "</html>");
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        name.setForeground(new Color(210, 226, 240));
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(name);
        sidebar.add(Box.createRigidArea(new Dimension(0, 28)));

        addMenuButton(sidebar, "Tableau de bord", "dashboard");
        addMenuButton(sidebar, "Notes", "grades");
        addMenuButton(sidebar, "Emploi du temps", "schedule");
        addMenuButton(sidebar, "Absences", "absences");

        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = new JButton("Deconnexion");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutButton.setBackground(new Color(238, 246, 255));
        logoutButton.setForeground(new Color(19, 56, 92));
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
        button.setBackground(new Color(30, 75, 118));
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
        main.setBackground(new Color(239, 244, 249));

        main.add(buildHeader(), BorderLayout.NORTH);

        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        contentPanel.setBackground(new Color(239, 244, 249));
        contentPanel.add(buildDashboardPage(), "dashboard");
        contentPanel.add(wrapTable(buildGradesTable()), "grades");
        contentPanel.add(wrapTable(buildScheduleTable()), "schedule");
        contentPanel.add(wrapTable(buildAbsenceTable()), "absences");

        main.add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "dashboard");
        return main;
    }

    private JPanel buildHeader() {
        Student student = service.getStudent();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 255, 255));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 226, 235)),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel title = new JLabel("Espace etudiant");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(24, 39, 75));

        JLabel subtitle = new JLabel(student.getDepartment() + " | " + student.getLevel() + " | Annee " + student.getAcademicYear());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(93, 108, 121));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);
        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JPanel buildDashboardPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(new Color(239, 244, 249));

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(createInfoCard("Moyenne generale", String.format("%.2f / 20", service.calculateAverage())));
        cards.add(createInfoCard("Credits valides", String.valueOf(service.calculateValidatedCredits())));
        cards.add(createInfoCard("Nombre d'absences", String.valueOf(service.getAbsences().size())));

        JPanel profile = new JPanel(new GridLayout(2, 2, 16, 16));
        profile.setOpaque(false);
        profile.add(createInfoCard("Matricule", service.getStudent().getRegistrationNumber()));
        profile.add(createInfoCard("Etudiant", service.getStudent().getFullName()));
        profile.add(createInfoCard("Departement", service.getStudent().getDepartment()));
        profile.add(createInfoCard("Niveau", service.getStudent().getLevel()));

        page.add(cards, BorderLayout.NORTH);
        page.add(profile, BorderLayout.CENTER);
        return page;
    }

    private JPanel createInfoCard(String label, String value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(213, 221, 228)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(new Color(98, 109, 121));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueText.setForeground(new Color(23, 45, 76));

        card.add(labelText);
        card.add(valueText);
        return card;
    }

    private JTable buildGradesTable() {
        String[] columns = {"Code", "Module", "Coef", "Credits", "Note", "Decision"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Grade grade : service.getGrades()) {
            model.addRow(new Object[] {
                grade.getCourse().getCode(),
                grade.getCourse().getTitle(),
                grade.getCourse().getCoefficient(),
                grade.getCourse().getCredits(),
                grade.getMark(),
                grade.getStatus()
            });
        }

        return createTable(model);
    }

    private JTable buildScheduleTable() {
        String[] columns = {"Jour", "Horaire", "Module", "Salle"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (ScheduleEntry entry : service.getSchedule()) {
            model.addRow(new Object[] {
                entry.getDay(),
                entry.getTime(),
                entry.getModule(),
                entry.getRoom()
            });
        }

        return createTable(model);
    }

    private JTable buildAbsenceTable() {
        String[] columns = {"Module", "Date", "Motif", "Justifiee"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Absence absence : service.getAbsences()) {
            model.addRow(new Object[] {
                absence.getModule(),
                absence.getDate(),
                absence.getReason(),
                absence.isJustified() ? "Oui" : "Non"
            });
        }

        return createTable(model);
    }

    private JPanel wrapTable(JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(239, 244, 249));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(213, 221, 228)));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(226, 236, 245));
        table.setGridColor(new Color(224, 231, 238));
        return table;
    }

    private void logout() {
        dispose();
        LoginFrame loginFrame = new LoginFrame(service);
        loginFrame.setVisible(true);
    }
}
