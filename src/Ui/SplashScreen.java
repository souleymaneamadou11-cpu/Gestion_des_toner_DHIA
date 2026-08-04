/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;


/**
 *
 * @author DELL
 */
public class SplashScreen extends JWindow {
    
    private static final Color BG_DARK   = new Color(26, 29, 46);
    private static final Color DHIA_BLUE = new Color(0, 174, 239);
    private static final Color ACCENT    = new Color(60, 52, 137);
    private static final Color TEXT_WHITE= new Color(232, 234, 246);
    private static final Color TEXT_GREY = new Color(107, 114, 153);

    private JProgressBar  progressBar;
    private JLabel        lblEtape;
    private Timer         timerAnim;
    private int           progression = 0;
    private Runnable      onFinish;

    // Étapes affichées pendant le chargement
    private final String[] etapes = {
        "Vérification des droits...",
        "Chargement des stocks...",
        "Calcul des alertes...",
        "Chargement des statistiques...",
        "Préparation du tableau de bord...",
        "Bienvenue !"
    };

    public SplashScreen(String nomUtilisateur, String role, Runnable onFinish) {
        this.onFinish = onFinish;
        setSize(400, 280);
        setLocationRelativeTo(null);
        buildUI(nomUtilisateur, role);
    }

    private void buildUI(String nomUtilisateur, String role) {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Fond dégradé sombre
                GradientPaint gp = new GradientPaint(
                    0, 0, BG_DARK,
                    0, getHeight(), new Color(35, 38, 58));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(30, 36, 28, 36));

        // ── Logo DHIA dessiné ──────────────────────────────
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Arc DHIA bleu
                g2.setColor(DHIA_BLUE);
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(2, 2, getWidth()-16, getHeight()-4, 15, 150);
                // Texte DHIA
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.setColor(Color.WHITE);
                g2.drawString("DHIA", getWidth()/2 - 22, getHeight()/2 + 8);
                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(0, 52));

        // ── Texte de bienvenue ─────────────────────────────
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(10, 0, 16, 0));

        JLabel lblBienvenue = new JLabel("Bienvenue, " + nomUtilisateur);
        lblBienvenue.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBienvenue.setForeground(TEXT_WHITE);
        lblBienvenue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRole = new JLabel(
            role.equals("ADMIN") ? "👑  Administrateur" : "👤  Utilisateur");
        lblRole.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        lblRole.setForeground(DHIA_BLUE);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRole.setBorder(new EmptyBorder(4, 0, 0, 0));

        textPanel.add(lblBienvenue);
        textPanel.add(lblRole);

        // ── Barre de progression ───────────────────────────
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setOpaque(false);

        lblEtape = new JLabel(etapes[0]);
        lblEtape.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEtape.setForeground(TEXT_GREY);
        lblEtape.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEtape.setBorder(new EmptyBorder(0, 0, 6, 0));

        // Barre de progression personnalisée
        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Fond de la barre
                g2.setColor(new Color(45, 50, 75));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                // Remplissage progression
                int fillW = (int)((double) getValue() / getMaximum() * getWidth());
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(
                        0, 0, DHIA_BLUE,
                        fillW, 0, ACCENT);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), getHeight(), getHeight());
                    // Reflet lumineux
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, fillW, getHeight()/2, getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        progressBar.setValue(0);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Pourcentage
        JLabel lblPct = new JLabel("0%") {
            @Override
            public String getText() {
                return progressBar.getValue() + "%";
            }
        };
        lblPct.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPct.setForeground(DHIA_BLUE);
        lblPct.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblPct.setBorder(new EmptyBorder(4, 0, 0, 0));

        progressPanel.add(lblEtape);
        progressPanel.add(progressBar);
        progressPanel.add(lblPct);

        // ── Pied avec version ──────────────────────────────
        JLabel lblVersion = new JLabel("GestionToners DHIA  v1.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(70, 75, 110));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblVersion.setBorder(new EmptyBorder(14, 0, 0, 0));

        // ── Assemblage ─────────────────────────────────────
        root.add(logoPanel,    BorderLayout.NORTH);
        root.add(textPanel,    BorderLayout.CENTER);
        root.add(progressPanel, BorderLayout.SOUTH);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(root,      BorderLayout.CENTER);
        wrap.add(lblVersion, BorderLayout.SOUTH);

        // Bordure arrondie de la fenêtre
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 52, 137, 80));
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 16, 16);
                g2.dispose();
            }
        };
        outer.setOpaque(false);
        outer.add(root);
        setContentPane(outer);
        setBackground(new Color(0, 0, 0, 0));
    }

    /**
     * Lance l'animation et appelle onFinish quand terminé.
     */
    public void demarrer() {
        setVisible(true);
        int[] etapeIdx = {0};

        timerAnim = new Timer(28, e -> {
            progression += 2;
            if (progression > 100) progression = 100;
            progressBar.setValue(progression);
            progressBar.repaint();

            // Change le texte d'étape selon la progression
            int idx = Math.min(
                (int)(progression / 100.0 * (etapes.length - 1)),
                etapes.length - 1);
            if (idx != etapeIdx[0]) {
                etapeIdx[0] = idx;
                lblEtape.setText(etapes[idx]);
            }

            // Fin de l'animation
            if (progression >= 100) {
                timerAnim.stop();
                Timer fermeture = new Timer(400, ev -> {
                    setVisible(false);
                    dispose();
                    onFinish.run(); // ouvre le MainFrame
                });
                fermeture.setRepeats(false);
                fermeture.start();
            }
        });
        timerAnim.start();
    }

}
