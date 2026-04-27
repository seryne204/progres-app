package progresapp;

import javax.swing.SwingUtilities;

import progresapp.service.UniversityService;
import progresapp.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UniversityService service = new UniversityService();
            LoginFrame loginFrame = new LoginFrame(service);
            loginFrame.setVisible(true);
        });
    }
}
