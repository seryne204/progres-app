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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import progresapp.model.Student;
import progresapp.service.UniversityService;

public class ProgresDashboard extends JFrame {
    private final UniversityService service;

    public ProgresDashboard(UniversityService service) {
        this.service = service;

        setTitle("Espace Etudiant");
        setSize(1100, 700);
        setMinimumSize(new Dimension(980, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 245, 249));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        Student student = service.getCurrentStudent();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 91, 137));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Portail Etudiant");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel subtitle = new JLabel(student.getFullName() + " | " + student.getId() + " | " + student.getLevel());
        subtitle.setForeground(new Color(220, 235, 247));
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
        tabs.addTab("Profil", buildProfilePanel());
        tabs.addTab("Emploi du temps", wrapTable(createTable(new String[] {"Code", "Module", "Date inscription"}, service.getCurrentStudentEnrollmentRows())));
        tabs.addTab("Notes avant rattrapage", wrapTable(createTable(new String[] {"Code", "Module", "CC", "Examen", "Moyenne", "Decision"}, service.getCurrentStudentBeforeResitRows())));
        tabs.addTab("Notes apres rattrapage", wrapTable(createTable(new String[] {"Code", "Module", "Rattrapage", "Moyenne finale", "Decision"}, service.getCurrentStudentAfterResitRows())));
        return tabs;
    }

    private JPanel buildProfilePanel() {
        Student student = service.getCurrentStudent();
        JPanel panel = new JPanel(new GridLayout(3, 2, 16, 16));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createInfoCard("Nom complet", student.getFullName()));
        panel.add(createInfoCard("Email", student.getEmail()));
        panel.add(createInfoCard("Date de naissance", student.getBirthDate()));
        panel.add(createInfoCard("Departement", student.getDepartment()));
        panel.add(createInfoCard("Moyenne avant rattrapage", String.format("%.2f / 20", service.getCurrentStudentAverageBeforeResit())));
        panel.add(createInfoCard("Moyenne apres rattrapage", String.format("%.2f / 20", service.getCurrentStudentAverageAfterResit())));
        panel.add(createInfoCard("Statut final", service.getCurrentStudentFinalStatus()));
        panel.add(createInfoCard("Modules bloquants", service.getCurrentStudentBlockingModules()));
        return panel;
    }

    private JPanel createInfoCard(String label, String value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 219, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel top = new JLabel(label);
        top.setForeground(new Color(96, 109, 122));
        top.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel bottom = new JLabel(value);
        bottom.setForeground(new Color(20, 38, 66));
        bottom.setFont(new Font("Segoe UI", Font.BOLD, 20));

        card.add(top);
        card.add(bottom);
        return card;
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

    private JScrollPane wrapTable(JTable table) {
        return new JScrollPane(table);
    }
}
