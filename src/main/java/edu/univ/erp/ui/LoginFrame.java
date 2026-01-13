package edu.univ.erp.ui;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.components.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("ERP Login");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
    }

    /**
     * Constructs the login UI with username/password fields,
     * error label, login button, and developer mode shortcuts.
     */
    private void buildUI() {

        JPanel background = new JPanel();
        background.setLayout(new GridBagLayout());
        background.setBackground(new Color(245, 247, 250));
        add(background, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(380, 480));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        background.add(card);

        JLabel title = new JLabel("University ERP", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);

        card.add(Box.createVerticalStrut(30));

        JLabel userLabel = new JLabel("Username");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        userLabel.setForeground(new Color(100, 100, 100));
        JPanel userLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userLabelPanel.setBackground(Color.WHITE);
        userLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        userLabelPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        card.add(userLabelPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        passLabel.setForeground(new Color(100, 100, 100));

        JPanel passLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passLabelPanel.setBackground(Color.WHITE);
        passLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        passLabelPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        card.add(passLabelPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(15));

        RoundedButton loginBtn = new RoundedButton("Login", new Color(74, 144, 226));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        loginBtn.addActionListener(e -> performRealLogin());

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(25));

        JLabel devLabel = new JLabel("Developer Mode");
        devLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        devLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        devLabel.setForeground(Color.GRAY);

        card.add(devLabel);
        card.add(Box.createVerticalStrut(10));

        JPanel bubblePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bubblePanel.setOpaque(false);

        JButton devStudentBubble = createBubbleButton(new Color(39, 174, 96));
        JButton devInstructorBubble = createBubbleButton(new Color(74, 144, 226));
        JButton devAdminBubble = createBubbleButton(new Color(231, 76, 60));

        devStudentBubble.addActionListener(e -> openRole("student"));
        devInstructorBubble.addActionListener(e -> openRole("instructor"));
        devAdminBubble.addActionListener(e -> openRole("admin"));

        bubblePanel.add(devStudentBubble);
        bubblePanel.add(devInstructorBubble);
        bubblePanel.add(devAdminBubble);

        add(bubblePanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a small circular colored button for developer mode role shortcuts.
     */
    private JButton createBubbleButton(Color color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(20, 20));
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Opens the main application frame for the specified role,
     * creating a session if needed for developer mode.
     */
    private void openRole(String role) {
        try {
            if (!SessionManager.isLoggedIn()) {
                int fakeUserId = switch (role) {
                    case "admin" -> 1;
                    case "instructor" -> 2;
                    default -> 3;
                };
                SessionManager.createSession(fakeUserId, role.toUpperCase());
            }

            MainFrame mf = new MainFrame(role);
            mf.setVisible(true);
            dispose();
        } catch (Throwable ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error while opening '" + role + "' view:\n" + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Performs user login by validating credentials, checking lockout status,
     * updating failed attempts, and opening the main dashboard on success.
     */
    private void performRealLogin() {
        errorLabel.setText(" ");

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            errorLabel.setForeground(new Color(243, 156, 18));
            return;
        }

        try {
            edu.univ.erp.auth.AuthManager auth = new edu.univ.erp.auth.AuthManager();
            edu.univ.erp.domain.User user = auth.login(username, password);

            if (user != null) {

                if (user.getRole().equalsIgnoreCase("STUDENT")) {
                    System.out.println("DEBUG: Login successful for Student ID: " + user.getUserId());
                    StudentService ss = new StudentService();
                    Student s = ss.getStudent(user.getUserId());
                    if (s != null) {
                        System.out.println("DEBUG: Found Student: " + s.getName());
                        SessionManager.setCurrentStudent(s);
                    } else {
                        System.out.println("DEBUG: StudentService returned null for ID: " + user.getUserId());
                    }
                }

                if (user.getRole().equalsIgnoreCase("INSTRUCTOR")) {
                    System.out.println("DEBUG: Login successful for Instructor ID: " + user.getUserId());
                    InstructorService is = new InstructorService();
                    Instructor ins = is.getInstructor(user.getUserId());
                    if (ins != null) {
                        System.out.println("DEBUG: Found Instructor: " + ins.getName());
                        SessionManager.setCurrentInstructor(ins);
                    } else {
                        System.out.println("DEBUG: InstructorService returned null for ID: " + user.getUserId());
                    }
                }

                // Initialize the session properly
                SessionManager.createSession(user.getUserId(), user.getRole());

                String role = (user.getRole() == null)
                        ? "student"
                        : user.getRole().toLowerCase();

                openRole(role);

            } else {
                edu.univ.erp.service.AuthService authService = new edu.univ.erp.service.AuthService();
                int failedAttempts = authService.getFailedAttempts(username);

                if (failedAttempts >= 5) {
                    errorLabel.setText("Account locked for 15 minutes.");
                    errorLabel.setForeground(new Color(231, 76, 60));
                } else if (failedAttempts > 0) {
                    int attemptsLeft = 5 - failedAttempts;
                    errorLabel.setText("Incorrect password. " + attemptsLeft + " attempts left.");
                    errorLabel.setForeground(new Color(243, 156, 18));
                } else {
                    errorLabel.setText("Wrong username or password.");
                    errorLabel.setForeground(new Color(231, 76, 60));
                }
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
            errorLabel.setText("Authentication server error.");
            errorLabel.setForeground(new Color(231, 76, 60));
        }
    }

    public static void main(String[] args) {
        edu.univ.erp.ui.util.ModernUI.setup();

        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
