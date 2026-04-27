package progresapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import progresapp.service.UniversityService;

public class LoginFrame extends JFrame {
    private final UniversityService service;
    private final JTextField registrationField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleBox;

    public LoginFrame(UniversityService service) {
        this.service = service;
        this.registrationField = new JTextField("2023-INFO-1452");
        this.passwordField = new JPasswordField("etudiant123");
        this.roleBox = new JComboBox<>(new String[] {"Etudiant", "Enseignant", "Administrateur"});

        setTitle("Connexion Portail Universitaire");
        setSize(540, 420);
        setMinimumSize(new Dimension(520, 380));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(233, 240, 247));
        root.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        setContentPane(root);

        root.add(buildWelcomePanel(), BorderLayout.NORTH);
        root.add(buildFormPanel(), BorderLayout.CENTER);
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setOpaque(false);

        JLabel title = new JLabel("Portail Universitaire", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(24, 58, 95));

        JLabel subtitle = new JLabel("Connexion etudiant, enseignant ou administrateur", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(76, 98, 120));

        panel.add(title);
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 48, 0, 48));

        JPanel form = new JPanel(new GridLayout(5, 1, 0, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(205, 216, 228)),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        form.add(createFieldPanel("Role", roleBox));
        form.add(createFieldPanel("Identifiant", registrationField));
        form.add(createFieldPanel("Mot de passe", passwordField));

        JButton loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(14, 104, 163));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(event -> handleLogin());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton, BorderLayout.CENTER);
        form.add(new JPanel());
        form.add(buttonPanel);

        wrapper.add(form, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFieldPanel(String label, javax.swing.JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldLabel.setForeground(new Color(69, 83, 97));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void handleLogin() {
        String registration = registrationField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = String.valueOf(roleBox.getSelectedItem());

        if ("Etudiant".equals(role) && service.authenticateStudent(registration, password)) {
            dispose();
            ProgresDashboard dashboard = new ProgresDashboard(service);
            dashboard.setVisible(true);
            return;
        }

        if ("Enseignant".equals(role) && service.authenticateProfessor(registration, password)) {
            dispose();
            ProfessorDashboard dashboard = new ProfessorDashboard(service);
            dashboard.setVisible(true);
            return;
        }

        if ("Administrateur".equals(role) && service.authenticateAdministrator(registration, password)) {
            dispose();
            AdministratorDashboard dashboard = new AdministratorDashboard(service);
            dashboard.setVisible(true);
            return;
        }

        JOptionPane.showMessageDialog(this, "Identifiant ou mot de passe incorrect.", "Connexion refusee", JOptionPane.ERROR_MESSAGE);
    }
}
