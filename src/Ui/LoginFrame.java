/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui;
import Config.DatabaseConfig;
import Model.Utilisateur;
import Service.AuthService;
import Util.SessionManager;
import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;


/**
 *
 * @author DELL
 */
public class LoginFrame extends JFrame{
    /*private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnConnexion;
    private JButton btnAdmin, btnUser;
    private JLabel lblErreur;
    private JLabel lblSucces;
    private String selectedRole = "ADMIN";

    // ── Couleurs ──────────────────────────────────────────────
    private static final Color BG_DARK     = new Color(26, 29, 46);
    private static final Color BG_DARK2    = new Color(42, 46, 69);
    private static final Color ACCENT      = new Color(60, 52, 137);
    private static final Color ACCENT_HVR  = new Color(83, 74, 183);
    private static final Color CARD_BG     = new Color(255, 255, 255);
    private static final Color INPUT_BG    = new Color(245, 245, 248);
    private static final Color BORDER_CLR  = new Color(220, 220, 228);
    private static final Color TEXT_MAIN   = new Color(30, 30, 50);
    private static final Color TEXT_MUTED  = new Color(110, 110, 130);
    private static final Color ERROR_BG    = new Color(255, 235, 235);
    private static final Color ERROR_CLR   = new Color(180, 40, 40);
    private static final Color ERROR_BRD   = new Color(240, 180, 180);
    private static final Color SUCCESS_BG  = new Color(232, 245, 232);
    private static final Color SUCCESS_CLR = new Color(40, 130, 60);
    private static final Color SUCCESS_BRD = new Color(160, 210, 160);
    private static final Color ROLE_ACTIVE_BG  = new Color(238, 240, 251);
    private static final Color ROLE_ACTIVE_FG  = new Color(60, 52, 137);
    private static final Color ROLE_ACTIVE_BRD = new Color(60, 52, 137);

    public LoginFrame() {
        setTitle("GestionToners — Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        buildUI();
    }

    private void buildUI() {
        // Conteneur principal avec ombre simulée
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 215), 1));

        // ── HEADER sombre ────────────────────────────────────
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Fond arrondi seulement en haut
                g2.setColor(BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 18, 18);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(420, 155));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 0, 24, 0));

        // Cercle icône
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK2);
                g2.fillOval(0, 0, 64, 64);
                g2.setColor(new Color(58, 63, 92));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, 62, 62);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(64, 64));
        iconCircle.setMaximumSize(new Dimension(64, 64));
        iconCircle.setLayout(new GridBagLayout());

        JLabel iconLbl = new JLabel("\uD83D\uDDA8");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconCircle.add(iconLbl);

        JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrapper.setOpaque(false);
        iconWrapper.add(iconCircle);

        JLabel appName = new JLabel("GestionToners");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(new Color(232, 234, 246));
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSub = new JLabel("Système de gestion de stock");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appSub.setForeground(new Color(107, 114, 153));
        appSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(iconWrapper);
        header.add(Box.createVerticalStrut(12));
        header.add(appName);
        header.add(Box.createVerticalStrut(4));
        header.add(appSub);

        // ── BODY ─────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setBackground(CARD_BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 32, 28, 32));

        // Alerte erreur
        lblErreur = makeAlertLabel(ERROR_BG, ERROR_CLR, ERROR_BRD, "⚠  ");
        lblErreur.setVisible(false);

        // Alerte succès
        lblSucces = makeAlertLabel(SUCCESS_BG, SUCCESS_CLR, SUCCESS_BRD, "✔  ");
        lblSucces.setVisible(false);

    /*//*/ Champ Identifiant
        body.add(makeFieldLabel("IDENTIFIANT"));
        body.add(Box.createVerticalStrut(6));
        txtLogin = makeTextField("Entrez votre login");
        body.add(txtLogin);
        body.add(Box.createVerticalStrut(14));

        // Champ Mot de passe
        body.add(makeFieldLabel("MOT DE PASSE"));
        body.add(Box.createVerticalStrut(6));
        JPanel pwdRow = makePwdRow();
        body.add(pwdRow);
        body.add(Box.createVerticalStrut(16));*/
        
        // Champ Identifiant
    /*body.add(makeFieldLabel("IDENTIFIANT"));
        body.add(Box.createVerticalStrut(6));
        txtLogin = makeTextField("Entrez votre login");
        body.add(txtLogin);
        body.add(Box.createVerticalStrut(14));

        // Champ Mot de passe — même hauteur que login grâce au container
        body.add(makeFieldLabel("MOT DE PASSE"));
        body.add(Box.createVerticalStrut(6));
        //txtPassword = makeTextField("");
        JPanel pwdRow = makePwdRow();
        pwdRow.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
        pwdRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        pwdRow.setMinimumSize(new Dimension(0, 46));
        pwdRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(pwdRow);

        // Toggle rôle
        body.add(makeFieldLabel("PROFIL DE CONNEXION"));
        body.add(Box.createVerticalStrut(8));
        body.add(makeRoleToggle());
        body.add(Box.createVerticalStrut(18));

        // Messages
        body.add(lblErreur);
        body.add(lblSucces);
        body.add(Box.createVerticalStrut(4));

        // Bouton connexion
        btnConnexion = makeLoginButton();
        body.add(btnConnexion);

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);

        // ── Bouton fermer ────────────────────────────────────
        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setForeground(new Color(160, 165, 190));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        btnClose.setBounds(388, 10, 22, 22);
        btnClose.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnClose.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btnClose.setForeground(new Color(160,165,190)); }
        });

        // ── LayeredPane pour superposer le bouton close ──────
        JLayeredPane lp = new JLayeredPane();
        lp.setPreferredSize(new Dimension(420, 580));
        root.setBounds(0, 0, 420, 580);
        lp.add(root, JLayeredPane.DEFAULT_LAYER);
        lp.add(btnClose, JLayeredPane.POPUP_LAYER);

        setContentPane(lp);

        // Drag pour déplacer la fenêtre
        addDragSupport(header);

        // Touche ENTER
        getRootPane().setDefaultButton(btnConnexion);
        txtLogin.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus();
            }
        });
    }

    // ── Composants ───────────────────────────────────────────

    private JLabel makeAlertLabel(Color bg, Color fg, Color border, String prefix) {
        JLabel lbl = new JLabel(prefix);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(fg);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(border, 1),
            new EmptyBorder(8, 12, 8, 12)));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return lbl;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        /*JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 180, 195));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 12, getHeight() / 2 + 5);
                }
            }
        };
        applyInputStyle(f);
        return f;*/
       /* JTextField f = new JTextField() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(180, 182, 200));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(placeholder, 12, getHeight() / 2 + 5);
            }
        }
    };
    applyInputStyle(f); // applique hauteur 46px et style uniforme
    return f;
    }

    private JPanel makePwdRow() {
        /*JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setEchoChar('•');
        applyInputStyle(txtPassword);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Bouton œil pour afficher/masquer
        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setPreferredSize(new Dimension(36, 42));
        eyeBtn.setToolTipText("Afficher / masquer");
        eyeBtn.addActionListener(e -> {
            if (txtPassword.getEchoChar() == '•') {
                txtPassword.setEchoChar((char) 0);
                eyeBtn.setText("🙈");
            } else {
                txtPassword.setEchoChar('•');
                eyeBtn.setText("👁");
            }
        });

        row.add(txtPassword, BorderLayout.CENTER);
        row.add(eyeBtn, BorderLayout.EAST);
        return row;*/
        /*JPanel container = new JPanel(new BorderLayout(0, 0)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    };
    container.setBackground(INPUT_BG);
    container.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
    container.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
    container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    container.setMinimumSize(new Dimension(0, 46));
    container.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Champ mot de passe SANS bordure propre (la bordure est sur le container)
    txtPassword = new JPasswordField();
    txtPassword.setEchoChar('•');
    txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtPassword.setForeground(TEXT_MAIN);
    txtPassword.setBackground(INPUT_BG);
    txtPassword.setCaretColor(ACCENT);
    txtPassword.setBorder(new EmptyBorder(0, 12, 0, 4)); // padding gauche seulement
    txtPassword.setOpaque(false);

    // Bouton œil 👁
    JButton eyeBtn = new JButton(" 👁 ");
    eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
    eyeBtn.setPreferredSize(new Dimension(46, 44));
    eyeBtn.setMinimumSize(new Dimension(46, 44));
    eyeBtn.setMaximumSize(new Dimension(46, 44));
    eyeBtn.setContentAreaFilled(false);
    eyeBtn.setBorderPainted(false);
    eyeBtn.setFocusPainted(false);
    eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    eyeBtn.setToolTipText("Afficher / masquer le mot de passe");
    eyeBtn.setForeground(TEXT_MUTED);

    // Toggle affichage mot de passe
    eyeBtn.addActionListener(e -> {
        if (txtPassword.getEchoChar() == '•') {
            txtPassword.setEchoChar((char) 0); // afficher
            eyeBtn.setText("🙈");
            eyeBtn.setForeground(ACCENT);
        } else {
            txtPassword.setEchoChar('•');      // masquer
            eyeBtn.setText("👁");
            eyeBtn.setForeground(TEXT_MUTED);
        }
        txtPassword.requestFocus();
    });

    // Focus sur le container pour changer la bordure
    txtPassword.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
            container.setBackground(Color.WHITE);
            txtPassword.setBackground(Color.WHITE);
            container.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
        }
        public void focusLost(FocusEvent e) {
            container.setBackground(INPUT_BG);
            txtPassword.setBackground(INPUT_BG);
            container.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        }
    });

    container.add(txtPassword, BorderLayout.CENTER);
    container.add(eyeBtn,      BorderLayout.EAST);
    return container;
    }

    private void applyInputStyle(JTextField f) {
        /*f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT_MAIN);
        f.setBackground(INPUT_BG);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(0, 12, 0, 12)));
        f.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBackground(Color.WHITE);
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2),
                    new EmptyBorder(0, 12, 0, 12)));
            }
            public void focusLost(FocusEvent e) {
                f.setBackground(INPUT_BG);
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1),
                    new EmptyBorder(0, 12, 0, 12)));
            }
        });*/
    /*    f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    f.setForeground(TEXT_MAIN);
    f.setBackground(INPUT_BG);
    f.setCaretColor(ACCENT);
    // Hauteur FIXE identique pour tous les champs
    f.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
    f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    f.setMinimumSize(new Dimension(0, 46));
    f.setAlignmentX(Component.LEFT_ALIGNMENT);
    f.setBorder(new CompoundBorder(
        BorderFactory.createLineBorder(BORDER_CLR, 1),
        new EmptyBorder(0, 12, 0, 12)));
    f.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
            f.setBackground(Color.WHITE);
            f.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                new EmptyBorder(0, 12, 0, 12)));
        }
        public void focusLost(FocusEvent e) {
            f.setBackground(INPUT_BG);
            f.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(0, 12, 0, 12)));
        }
    });
    }

    private JPanel makeRoleToggle() {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setBackground(CARD_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnAdmin = makeRoleButton("👑  Administrateur", true);
        btnUser  = makeRoleButton("👤  Utilisateur",    false);

        btnAdmin.addActionListener(e -> { selectedRole = "ADMIN";       updateRoleButtons(); });
        btnUser.addActionListener(e  -> { selectedRole = "UTILISATEUR"; updateRoleButtons(); });

        p.add(btnAdmin);
        p.add(btnUser);
        return p;
    }

    private JButton makeRoleButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setOpaque(true);
        applyRoleStyle(b, active);
        return b;
    }

    private void applyRoleStyle(JButton b, boolean active) {
        if (active) {
            b.setBackground(ROLE_ACTIVE_BG);
            b.setForeground(ROLE_ACTIVE_FG);
            b.setBorder(BorderFactory.createLineBorder(ROLE_ACTIVE_BRD, 2));
        } else {
            b.setBackground(INPUT_BG);
            b.setForeground(TEXT_MUTED);
            b.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        }
    }

    private void updateRoleButtons() {
        applyRoleStyle(btnAdmin, "ADMIN".equals(selectedRole));
        applyRoleStyle(btnUser,  "UTILISATEUR".equals(selectedRole));
        btnAdmin.repaint();
        btnUser.repaint();
    }

    private JButton makeLoginButton() {
        JButton b = new JButton("  Se connecter") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT_HVR); b.repaint(); }
            public void mouseExited(MouseEvent e)  { b.setBackground(ACCENT);     b.repaint(); }
        });
        b.addActionListener(e -> doLogin());
        return b;
    }

    // ── Logique connexion ─────────────────────────────────────

    private void doLogin() {
        /*clearMessages();
        String login = txtLogin.getText().trim();
        String pwd   = new String(txtPassword.getPassword());

        if (login.isEmpty()) { showError("Veuillez saisir votre identifiant."); return; }
        if (pwd.isEmpty())   { showError("Veuillez saisir votre mot de passe."); return; }

        btnConnexion.setEnabled(false);
        btnConnexion.setText("  Vérification...");

        SwingWorker<Utilisateur, Void> worker = new SwingWorker<>() {
            protected Utilisateur doInBackground() throws Exception {
                return new AuthService().authentifier(login, pwd, selectedRole);
            }
            protected void done() {
                try {
                    Utilisateur u = get();
                    if (u == null) {
                        showError("Identifiants incorrects ou profil non autorisé.");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    } else {
                        SessionManager.setUtilisateur(u);
                        showSucces("Bienvenue " + u.getPrenom() + " — chargement...");
                        Timer t = new Timer(1200, ev -> {
                            dispose();
                            new MainFrame().setVisible(true);
                        });
                        t.setRepeats(false);
                        t.start();
                    }
                } catch (Exception ex) {
                    showError("Erreur de connexion à la base de données.");
                    ex.printStackTrace();
                }
                btnConnexion.setEnabled(true);
                btnConnexion.setText("  Se connecter");
            }
        };
        worker.execute();*/
        /*clearMessages();
    String login = txtLogin.getText().trim();
    String pwd   = new String(txtPassword.getPassword());

    if (login.isEmpty()) { showError("Veuillez saisir votre identifiant."); return; }
    if (pwd.isEmpty())   { showError("Veuillez saisir votre mot de passe."); return; }

    btnConnexion.setEnabled(false);
    btnConnexion.setText("  Vérification...");

    SwingWorker<Utilisateur, Void> worker = new SwingWorker<>() {

        private String erreurConnexion = null; // stocke le type d'erreur

        @Override
        protected Utilisateur doInBackground() {
            try {
                // Teste d'abord si la connexion BDD est possible
                DatabaseConfig.getConnection();
            } catch (Exception e) {
                // BDD inaccessible — on stocke le message d'erreur
                erreurConnexion = "Erreur de connexion à la base de données.\n"
                    + "Vérifiez que MySQL est démarré.";
                return null;
            }

            try {
                // BDD accessible — on tente l'authentification
                return new AuthService().authentifier(login, pwd, selectedRole);
            } catch (Exception e) {
                erreurConnexion = "Erreur lors de l'authentification : " + e.getMessage();
                return null;
            }
        }

        @Override
        protected void done() {
            try {
                Utilisateur u = get();

                if (erreurConnexion != null) {
                    // Erreur BDD ou autre erreur technique
                    showError(erreurConnexion);

                } else if (u == null) {
                    // BDD OK mais identifiants incorrects
                    showError("Identifiants incorrects ou profil non autorisé.");
                    txtPassword.setText("");
                    txtPassword.requestFocus();

                } else {
                    // Connexion réussie
                    SessionManager.setUtilisateur(u);
                    showSucces("Bienvenue " + u.getPrenom() + " — chargement...");
                    Timer t = new Timer(1200, ev -> {
                        dispose();
                        new MainFrame().setVisible(true);
                    });
                    t.setRepeats(false);
                    t.start();
                }

            } catch (Exception ex) {
                // Erreur inattendue dans le worker lui-même
                showError("Erreur inattendue : " + ex.getMessage());
                ex.printStackTrace();
            }

            btnConnexion.setEnabled(true);
            btnConnexion.setText("  Se connecter");
        }
    };

    worker.execute();
    }

    private void clearMessages() {
        lblErreur.setVisible(false);
        lblSucces.setVisible(false);
    }

    private void showError(String msg) {
        lblErreur.setText("  ⚠  " + msg);
        lblErreur.setVisible(true);
        lblSucces.setVisible(false);
        pack();
    }

    private void showSucces(String msg) {
        lblSucces.setText("  ✔  " + msg);
        lblSucces.setVisible(true);
        lblErreur.setVisible(false);
        pack();
    }

    // ── Drag support ──────────────────────────────────────────

    private void addDragSupport(JComponent c) {
        final int[] pos = new int[2];
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pos[0] = e.getX(); pos[1] = e.getY();
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - pos[0],
                            getY() + e.getY() - pos[1]);
            }
        });
    }*/
    private JTextField    txtLogin;
    private JPasswordField txtPassword;
    private JButton       btnConnexion;
    private JButton       btnAdmin, btnUser;
    private JLabel        lblErreur;
    private JLabel        lblSucces;
    private String        selectedRole = "ADMIN";
    private boolean       pwdVisible   = false;

    // ── Couleurs ──────────────────────────────────────────
    private static final Color BG_DARK     = new Color(26, 29, 46);
    private static final Color BG_DARK2    = new Color(42, 46, 69);
    private static final Color ACCENT      = new Color(60, 52, 137);
    private static final Color ACCENT_HVR  = new Color(83, 74, 183);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color INPUT_BG    = new Color(245, 245, 248);
    private static final Color BORDER_CLR  = new Color(220, 220, 228);
    private static final Color TEXT_MAIN   = new Color(30, 30, 50);
    private static final Color TEXT_MUTED  = new Color(150, 152, 170);
    private static final Color ERROR_BG    = new Color(255, 235, 235);
    private static final Color ERROR_CLR   = new Color(180, 40, 40);
    private static final Color ERROR_BRD   = new Color(240, 180, 180);
    private static final Color SUCCESS_BG  = new Color(232, 245, 232);
    private static final Color SUCCESS_CLR = new Color(40, 130, 60);
    private static final Color SUCCESS_BRD = new Color(160, 210, 160);

    public LoginFrame() {
        setTitle("GestionToners — Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 520); // hauteur réduite
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        root.setOpaque(false);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);

        // Bouton fermer
        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setForeground(new Color(180, 185, 210));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        btnClose.setBounds(388, 8, 22, 22);

        JLayeredPane lp = new JLayeredPane();
        lp.setPreferredSize(new Dimension(420, 520));
        root.setBounds(0, 0, 420, 520);
        lp.add(root,     JLayeredPane.DEFAULT_LAYER);
        lp.add(btnClose, JLayeredPane.POPUP_LAYER);

        setContentPane(lp);
        addDragSupport(buildHeader());
        getRootPane().setDefaultButton(btnConnexion);
    }

    // ── HEADER ────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 16, 16);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(420, 130)); // hauteur header réduite
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(20, 0, 18, 0));

        // Cercle icône
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK2);
                g2.fillOval(0, 0, 52, 52);
                g2.setColor(new Color(58, 63, 92));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(1, 1, 50, 50);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(52, 52));
        iconCircle.setMaximumSize(new Dimension(52, 52));
        iconCircle.setLayout(new GridBagLayout());

        // Icône imprimante dessinée en Java
        JPanel printerIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(123, 140, 222));
                // Corps imprimante
                g2.fillRoundRect(2, 8, 20, 12, 3, 3);
                // Papier sortant
                g2.fillRect(5, 4, 14, 6);
                g2.setColor(BG_DARK2);
                g2.fillRect(6, 5, 12, 4);
                // Papier en bas
                g2.setColor(new Color(123, 140, 222));
                g2.fillRect(5, 16, 14, 8);
                g2.setColor(BG_DARK2);
                g2.fillRect(6, 18, 12, 2);
                g2.fillRect(6, 21, 8, 2);
                g2.dispose();
            }
        };
        printerIcon.setOpaque(false);
        printerIcon.setPreferredSize(new Dimension(24, 24));
        iconCircle.add(printerIcon);

        JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrapper.setOpaque(false);
        iconWrapper.add(iconCircle);

        JLabel appName = new JLabel("GestionToners");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(new Color(232, 234, 246));
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSub = new JLabel("Système de gestion de stock");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appSub.setForeground(new Color(107, 114, 153));
        appSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(iconWrapper);
        header.add(Box.createVerticalStrut(8));
        header.add(appName);
        header.add(Box.createVerticalStrut(3));
        header.add(appSub);

        return header;
    }

    // ── BODY ──────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(CARD_BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(18, 28, 20, 28)); // marges réduites

        // Messages
        lblErreur = makeAlertLabel(ERROR_BG, ERROR_CLR, ERROR_BRD);
        lblSucces = makeAlertLabel(SUCCESS_BG, SUCCESS_CLR, SUCCESS_BRD);
        lblErreur.setVisible(false);
        lblSucces.setVisible(false);

        // Champ Login
        body.add(makeFieldLabel("IDENTIFIANT"));
        body.add(Box.createVerticalStrut(5));
        txtLogin = makeTextField("Entrez votre login");
        body.add(txtLogin);
        body.add(Box.createVerticalStrut(12));

        // Champ Mot de passe avec œil dessiné
        body.add(makeFieldLabel("MOT DE PASSE"));
        body.add(Box.createVerticalStrut(5));
        body.add(makePwdRow());
        body.add(Box.createVerticalStrut(14));

        // Toggle rôle
        body.add(makeFieldLabel("PROFIL DE CONNEXION"));
        body.add(Box.createVerticalStrut(6));
        body.add(makeRoleToggle());
        body.add(Box.createVerticalStrut(14));

        // Messages erreur/succès
        body.add(lblErreur);
        body.add(lblSucces);
        body.add(Box.createVerticalStrut(4));

        // Bouton connexion
        btnConnexion = makeLoginButton();
        body.add(btnConnexion);

        return body;
    }

    // ── CHAMP MOT DE PASSE avec œil dessiné en Java ───────
    private JPanel makePwdRow() {
        // Container principal qui ressemble à un champ de saisie
        JPanel container = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        container.setBackground(INPUT_BG);
        container.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        container.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        container.setMinimumSize(new Dimension(0, 42));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Champ password sans bordure
       /* txtPassword = new JPasswordField();
        txtPassword.setEchoChar('•');
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setForeground(TEXT_MAIN);
        txtPassword.setCaretColor(ACCENT);
        txtPassword.setOpaque(false);
        txtPassword.setBorder(new EmptyBorder(0, 12, 0, 4));*/
       
       txtPassword = new JPasswordField() {
    @Override
    protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Affiche le placeholder seulement si vide et pas focus
            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                g2.drawString("Entrez votre mot de passe", 12, getHeight() / 2 + 5);
                g2.dispose();
            }
        }
    };
    txtPassword.setEchoChar('•');
    txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtPassword.setForeground(TEXT_MAIN);
    txtPassword.setCaretColor(ACCENT);
    txtPassword.setOpaque(false);
    txtPassword.setBorder(new EmptyBorder(0, 12, 0, 4));

        // Bouton œil dessiné en Java (pas d'emoji)
        JButton eyeBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int cx = w / 2, cy = h / 2;

                if (pwdVisible) {
                    // Œil ouvert — arc supérieur + arc inférieur
                    g2.setColor(ACCENT);
                    g2.setStroke(new BasicStroke(1.8f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Contour œil
                    GeneralPath eye = new GeneralPath();
                    eye.moveTo(cx - 9, cy);
                    eye.curveTo(cx - 5, cy - 7, cx + 5, cy - 7, cx + 9, cy);
                    eye.curveTo(cx + 5, cy + 7, cx - 5, cy + 7, cx - 9, cy);
                    eye.closePath();
                    g2.draw(eye);
                    // Pupille
                    g2.fillOval(cx - 3, cy - 3, 6, 6);

                } else {
                    // Œil fermé — arc + barre diagonale
                    g2.setColor(TEXT_MUTED);
                    g2.setStroke(new BasicStroke(1.8f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Arc supérieur seulement
                    g2.drawArc(cx - 9, cy - 6, 18, 12, 0, 180);
                    // Pupille
                    g2.fillOval(cx - 2, cy - 2, 5, 5);
                    // Barre oblique (œil barré)
                    g2.setColor(new Color(200, 60, 60));
                    g2.setStroke(new BasicStroke(1.8f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - 8, cy + 6, cx + 8, cy - 8);
                }
                g2.dispose();
            }
        };
        eyeBtn.setPreferredSize(new Dimension(40, 40));
        eyeBtn.setMinimumSize(new Dimension(40, 40));
        eyeBtn.setMaximumSize(new Dimension(40, 40));
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setToolTipText("Afficher / masquer le mot de passe");

        // Clic sur l'œil
        eyeBtn.addActionListener(e -> {
            pwdVisible = !pwdVisible;
            txtPassword.setEchoChar(pwdVisible ? (char) 0 : '•');
            eyeBtn.repaint(); // redessine l'œil ouvert ou fermé
            txtPassword.requestFocus();
        });

        // Bordure bleue au focus
        txtPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                container.setBackground(Color.WHITE);
                container.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
                txtPassword.setOpaque(false);
            }
            public void focusLost(FocusEvent e) {
                container.setBackground(INPUT_BG);
                container.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
            }
        });

        container.add(txtPassword, BorderLayout.CENTER);
        container.add(eyeBtn,      BorderLayout.EAST);
        return container;
    }

    // ── COMPOSANTS ────────────────────────────────────────
    private JLabel makeAlertLabel(Color bg, Color fg, Color border) {
        JLabel lbl = new JLabel("", SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(fg);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(border, 1),
            new EmptyBorder(6, 10, 6, 10)));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return lbl;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(130, 132, 155));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 12, getHeight() / 2 + 5);
                }
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT_MAIN);
        f.setBackground(INPUT_BG);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(0, 12, 0, 12)));
        f.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setMinimumSize(new Dimension(0, 42));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBackground(Color.WHITE);
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2),
                    new EmptyBorder(0, 12, 0, 12)));
            }
            public void focusLost(FocusEvent e) {
                f.setBackground(INPUT_BG);
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1),
                    new EmptyBorder(0, 12, 0, 12)));
            }
        });
        return f;
    }

    private JPanel makeRoleToggle() {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setBackground(CARD_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnAdmin = makeRoleButton("Administrateur", true);
        btnUser  = makeRoleButton("Utilisateur",    false);

        btnAdmin.addActionListener(e -> { selectedRole = "ADMIN";       updateRoleButtons(); });
        btnUser.addActionListener(e  -> { selectedRole = "UTILISATEUR"; updateRoleButtons(); });

        p.add(btnAdmin);
        p.add(btnUser);
        return p;
    }

    private JButton makeRoleButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setOpaque(true);
        applyRoleStyle(b, active);
        return b;
    }

    private void applyRoleStyle(JButton b, boolean active) {
        if (active) {
            b.setBackground(new Color(238, 240, 251));
            b.setForeground(ACCENT);
            b.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
        } else {
            b.setBackground(INPUT_BG);
            b.setForeground(new Color(130, 132, 155));
            b.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        }
    }

    private void updateRoleButtons() {
        applyRoleStyle(btnAdmin, "ADMIN".equals(selectedRole));
        applyRoleStyle(btnUser,  "UTILISATEUR".equals(selectedRole));
        btnAdmin.repaint();
        btnUser.repaint();
    }

    private JButton makeLoginButton() {
        JButton b = new JButton("Se connecter") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT_HVR); b.repaint(); }
            public void mouseExited(MouseEvent e)  { b.setBackground(ACCENT);     b.repaint(); }
        });
        b.addActionListener(e -> doLogin());
        return b;
    }

    // ── LOGIQUE CONNEXION ─────────────────────────────────
    private void doLogin() {
        clearMessages();
        String login = txtLogin.getText().trim();
        String pwd   = new String(txtPassword.getPassword());

        if (login.isEmpty()) { showError("Veuillez saisir votre identifiant."); return; }
        if (pwd.isEmpty())   { showError("Veuillez saisir votre mot de passe."); return; }

        btnConnexion.setEnabled(false);
        btnConnexion.setText("Vérification...");

        SwingWorker<Utilisateur, Void> worker = new SwingWorker<>() {
            private String erreurConnexion = null;

            @Override
            protected Utilisateur doInBackground() {
                try {
                    DatabaseConfig.getConnection();
                } catch (Exception e) {
                    erreurConnexion = "Erreur de connexion à la base de données.\n"
                        + "Vérifiez que MySQL est démarré.";
                    return null;
                }
                try {
                    return new AuthService().authentifier(login, pwd, selectedRole);
                } catch (Exception e) {
                    erreurConnexion = "Erreur : " + e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Utilisateur u = get();
                    if (erreurConnexion != null) {
                        showError(erreurConnexion);
                    } else if (u == null) {
                        showError("Identifiants incorrects ou profil non autorisé.");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    /*} else {
                        SessionManager.setUtilisateur(u);
                        showSucces("Bienvenue " + u.getPrenom() + " — chargement...");
                        Timer t = new Timer(1200, ev -> {
                            dispose();
                            new MainFrame().setVisible(true);
                        });
                        t.setRepeats(false);
                        t.start();
                    }*/
                    } else {
                    SessionManager.setUtilisateur(u);
                    // Ferme le login et lance le splash screen
                    dispose();
                    SplashScreen splash = new SplashScreen(
                        u.getPrenom() + " " + u.getNom(),
                        u.getRole(),
                        () -> SwingUtilities.invokeLater(() ->
                            new MainFrame().setVisible(true))
                    );
                    splash.demarrer();
                }
                } catch (Exception ex) {
                    showError("Erreur inattendue : " + ex.getMessage());
                }
                btnConnexion.setEnabled(true);
                btnConnexion.setText("Se connecter");
            }
        };
        worker.execute();
    }

    private void clearMessages() {
        lblErreur.setVisible(false);
        lblSucces.setVisible(false);
    }

    private void showError(String msg) {
        lblErreur.setText("  ⚠  " + msg);
        lblErreur.setVisible(true);
        lblSucces.setVisible(false);
    }

    private void showSucces(String msg) {
        lblSucces.setText("  ✔  " + msg);
        lblSucces.setVisible(true);
        lblErreur.setVisible(false);
    }

    private void addDragSupport(JComponent c) {
        final int[] pos = new int[2];
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pos[0] = e.getX(); pos[1] = e.getY();
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - pos[0],
                            getY() + e.getY() - pos[1]);
            }
        });
    }  
}
