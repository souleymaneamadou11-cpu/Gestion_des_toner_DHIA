/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui;
import Dao.MouvementDAO;
import Dao.TonerDAO;
import Model.Toner;
import Model.Utilisateur;
import Service.StockService;
import Ui.Panels.*;
import Util.SessionManager;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author DELL
 */
public class MainFrame extends JFrame {
        public static final Color DHIA_BLUE    = new Color(0, 174, 239);   // Bleu DHIA
    public static final Color SIDEBAR_BG   = new Color(26, 29, 46);    // Fond sidebar sombre
    public static final Color SIDEBAR_ITEM = new Color(37, 42, 64);    // Item sidebar actif
    public static final Color SIDEBAR_TEXT = new Color(155, 163, 200); // Texte sidebar
    public static final Color ACCENT       = new Color(0, 174, 239);   // Accent DHIA bleu
    public static final Color ACCENT_DARK  = new Color(0, 140, 200);   // Bleu foncé hover
    public static final Color PAGE_BG      = new Color(245, 246, 250); // Fond page
    public static final Color CARD_BG      = Color.WHITE;
    public static final Color TEXT_DARK    = new Color(30, 30, 50);
    public static final Color TEXT_GREY    = new Color(110, 110, 135);
    public static final Color TEXT_LIGHT   = new Color(180, 182, 200);
    public static final Color DANGER       = new Color(220, 53, 69);
    public static final Color WARNING      = new Color(255, 152, 0);
    public static final Color SUCCESS      = new Color(40, 167, 69);
    public static final Color BORDER_CLR   = new Color(230, 230, 240);
    public static final Color BADGE_DANGER = new Color(252, 235, 235);
    public static final Color BADGE_WARN   = new Color(250, 238, 218);
    public static final Color BADGE_OK     = new Color(234, 243, 222);
 
    // ── Composants principaux ─────────────────────────────────
    private JPanel  contentPanel;       // Zone centrale (change selon menu)
    private JLabel  lblTopTitle;        // Titre de la page courante
    private JLabel  lblNotifBadge;      // Badge compteur notifications
    private JButton btnNotif;           // Bouton cloche
    private JLabel  lblDate;            // Date/heure en haut à droite
 
    // ── DAO & Services ────────────────────────────────────────
    private final TonerDAO     tonerDAO     = new TonerDAO();
    private final MouvementDAO mouvementDAO = new MouvementDAO();
    private final StockService stockService = new StockService();
 
    // ── Timer actualisation auto (toutes les 5 min) ───────────
    private Timer refreshTimer;
 
    public MainFrame() {
        Utilisateur u = SessionManager.getUtilisateur();
        setTitle("GestionToners DHIA — " + u.getPrenom() + " " + u.getNom());
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(loadIcon());
        buildUI();
        // Afficher le tableau de bord au démarrage
        showDashboard();
        // Vérifier alertes au démarrage (admin seulement)
        if (SessionManager.isAdmin()) {
            SwingUtilities.invokeLater(this::verifierAlertesDemarrage);
        }
        // Timer d'actualisation automatique toutes les 5 minutes
        refreshTimer = new Timer(300_000, e -> {
            if (SessionManager.isAdmin()) mettreAJourBadgeNotif();
        });
        refreshTimer.start();
    }
 
    // ════════════════════════════════════════════════════════
    //  CONSTRUCTION DE L'INTERFACE
    // ════════════════════════════════════════════════════════
 
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
 
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildMainZone(), BorderLayout.CENTER);
 
        setContentPane(root);
    }
 
    // ════════════════════════════════════════════════════════
    //  SIDEBAR GAUCHE
    // ════════════════════════════════════════════════════════
 
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(SIDEBAR_BG);
        sb.setPreferredSize(new Dimension(220, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
 
        // --- Logo DHIA + nom application ---
        sb.add(buildSidebarLogo());
        sb.add(buildSidebarSeparator());
 
        // --- Menu commun (tous les rôles) ---
        sb.add(menuItem("🏠", "Tableau de bord",  "dashboard",  true));
        sb.add(menuItem("📤", "Sortie de toners", "sorties",    false));
 
        // --- Menu Admin uniquement ---
        if (SessionManager.isAdmin()) {
            sb.add(sectionLabel("STOCK"));
            sb.add(menuItem("➕", "Entrée en stock",   "entree",      false));
            sb.add(menuItem("📦", "Gérer les toners",  "toners",      false));
            sb.add(menuItem("📋", "Historique",         "historique",  false));
            sb.add(menuItem("📈", "Statistiques",       "stats",       false));
            sb.add(sectionLabel("ADMINISTRATION"));
            sb.add(menuItem("👥", "Utilisateurs",       "users",       false));
            sb.add(buildNotifMenuItem());
        }
 
        // --- Compte (tous) ---
        sb.add(sectionLabel("COMPTE"));
        sb.add(menuItem("⚙️", "Mon profil", "profil", false));
 
        // Espace flexible
        sb.add(Box.createVerticalGlue());
 
        // --- Pied de sidebar (infos utilisateur) ---
        sb.add(buildSidebarFooter());
 
        return sb;
    }
 
    private JPanel buildSidebarLogo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SIDEBAR_BG);
        p.setBorder(new EmptyBorder(16, 14, 14, 14));
        p.setMaximumSize(new Dimension(220, 70));
 
        // Arc DHIA stylisé en SVG-like avec paint
        JPanel logoArc = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DHIA_BLUE);
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Dessin de l'arc DHIA
                int[] xp = {2, 8, 16, 26, 36};
                int[] yp = {22, 12, 6, 4, 6};
                for (int i = 0; i < xp.length - 1; i++) {
                    g2.drawLine(xp[i], yp[i], xp[i+1], yp[i+1]);
                }
                g2.dispose();
            }
        };
        logoArc.setBackground(SIDEBAR_BG);
        logoArc.setPreferredSize(new Dimension(40, 30));
 
        JPanel textPanel = new JPanel();
        textPanel.setBackground(SIDEBAR_BG);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
 
        JLabel lblDhia = new JLabel("DHIA");
        lblDhia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDhia.setForeground(Color.WHITE);
 
        JLabel lblApp = new JLabel("GestionToners");
        lblApp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblApp.setForeground(new Color(100, 110, 150));
 
        textPanel.add(lblDhia);
        textPanel.add(lblApp);
 
        p.add(logoArc,   BorderLayout.WEST);
        p.add(textPanel, BorderLayout.CENTER);
        return p;
    }
 
    private JSeparator buildSidebarSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(45, 50, 75));
        sep.setBackground(SIDEBAR_BG);
        sep.setMaximumSize(new Dimension(220, 1));
        return sep;
    }
 
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(65, 72, 110));
        lbl.setBorder(new EmptyBorder(14, 18, 3, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(220, 28));
        return lbl;
    }
 
    /**
     * Crée un item de menu dans la sidebar.
     * @param icon    Emoji ou texte court pour l'icône
     * @param label   Libellé du menu
     * @param action  Identifiant de l'action
     * @param active  Actif par défaut ?
     */
    private JButton menuItem(String icon, String label, String action, boolean active) {
        JButton btn = new JButton(icon + "  " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fond arrondi si actif
                if (getBackground().equals(SIDEBAR_ITEM)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 8, 8);
                    // Bord gauche bleu
                    g2.setColor(DHIA_BLUE);
                    g2.fillRoundRect(6, 2, 3, getHeight()-4, 2, 2);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btn.setForeground(active ? Color.WHITE : SIDEBAR_TEXT);
        btn.setBackground(active ? SIDEBAR_ITEM : SIDEBAR_BG);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 18, 9, 14));
        btn.setMaximumSize(new Dimension(220, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setName(action);
 
        // Hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(SIDEBAR_ITEM)) {
                    btn.setForeground(Color.WHITE);
                    btn.setBackground(new Color(35, 40, 62));
                    btn.repaint();
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(SIDEBAR_ITEM)) {
                    btn.setForeground(SIDEBAR_TEXT);
                    btn.setBackground(SIDEBAR_BG);
                    btn.repaint();
                }
            }
        });
 
        // Action
        btn.addActionListener(e -> navigateTo(action, btn));
        return btn;
    }
 
    /** Item spécial Notifications avec badge rouge */
    private JPanel buildNotifMenuItem() {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(SIDEBAR_BG);
        row.setMaximumSize(new Dimension(220, 38));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        btnNotif = new JButton("🔔  Notifications");
        btnNotif.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btnNotif.setForeground(SIDEBAR_TEXT);
        btnNotif.setBackground(SIDEBAR_BG);
        btnNotif.setContentAreaFilled(false);
        btnNotif.setBorderPainted(false);
        btnNotif.setFocusPainted(false);
        btnNotif.setHorizontalAlignment(SwingConstants.LEFT);
        btnNotif.setBorder(new EmptyBorder(9, 18, 9, 0));
        btnNotif.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNotif.addActionListener(e -> navigateTo("notifs", btnNotif));
 
        lblNotifBadge = new JLabel("0");
        lblNotifBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblNotifBadge.setForeground(Color.WHITE);
        lblNotifBadge.setOpaque(true);
        lblNotifBadge.setBackground(DANGER);
        lblNotifBadge.setBorder(new EmptyBorder(1, 5, 1, 5));
        lblNotifBadge.setVisible(false);
 
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setBackground(SIDEBAR_BG);
        right.add(lblNotifBadge);
 
        row.add(btnNotif, BorderLayout.CENTER);
        row.add(right,    BorderLayout.EAST);
 
        mettreAJourBadgeNotif();
        return row;
    }
 
    private JPanel buildSidebarFooter() {
        JPanel p = new JPanel();
        p.setBackground(new Color(20, 23, 38));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 16, 14, 16));
        p.setMaximumSize(new Dimension(220, 80));
 
        Utilisateur u = SessionManager.getUtilisateur();
 
        JLabel lblNom = new JLabel(u.getPrenom() + " " + u.getNom());
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNom.setForeground(Color.WHITE);
        lblNom.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel lblRole = new JLabel(SessionManager.isAdmin() ? "👑 Administrateur" : "👤 Utilisateur");
        lblRole.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lblRole.setForeground(new Color(100, 110, 150));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JButton btnDeco = new JButton("↩  Déconnexion");
        btnDeco.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnDeco.setForeground(new Color(220, 80, 80));
        btnDeco.setContentAreaFilled(false);
        btnDeco.setBorderPainted(false);
        btnDeco.setFocusPainted(false);
        btnDeco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeco.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDeco.setBorder(new EmptyBorder(5, 0, 0, 0));
        btnDeco.addActionListener(e -> deconnecter());
 
        p.add(lblNom);
        p.add(Box.createVerticalStrut(2));
        p.add(lblRole);
        p.add(btnDeco);
        return p;
    }
 
    // ════════════════════════════════════════════════════════
    //  ZONE PRINCIPALE (TopBar + Content)
    // ════════════════════════════════════════════════════════
 
    private JPanel buildMainZone() {
        JPanel zone = new JPanel(new BorderLayout());
        zone.setBackground(PAGE_BG);
        zone.add(buildTopBar(),    BorderLayout.NORTH);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(PAGE_BG);
        zone.add(contentPanel, BorderLayout.CENTER);
        return zone;
    }
 
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setPreferredSize(new Dimension(0, 52));
        bar.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(0, 20, 0, 20)));
 
        // Titre page à gauche
        lblTopTitle = new JLabel("Tableau de bord");
        lblTopTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTopTitle.setForeground(TEXT_DARK);
        bar.add(lblTopTitle, BorderLayout.WEST);
 
        // Droite : date + notifications
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(CARD_BG);
        right.setOpaque(false);
 
        // Date/heure
        lblDate = new JLabel();
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(TEXT_GREY);
        mettreAJourDate();
        // Timer 1 min pour actualiser l'heure
        new Timer(60_000, e -> mettreAJourDate()).start();
 
        right.add(lblDate);
 
        // Bouton notifications (admin seulement)
        if (SessionManager.isAdmin()) {
            JPanel notifWrap = new JPanel(null);
            notifWrap.setPreferredSize(new Dimension(30, 52));
            notifWrap.setOpaque(false);
 
            JLabel bell = new JLabel("🔔");
            bell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            bell.setBounds(0, 14, 24, 24);
            bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            bell.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { navigateTo("notifs", null); }
                public void mouseEntered(MouseEvent e) { bell.setFont(bell.getFont().deriveFont(20f)); }
                public void mouseExited(MouseEvent e)  { bell.setFont(bell.getFont().deriveFont(18f)); }
            });
            notifWrap.add(bell);
        }
 
        bar.add(right, BorderLayout.EAST);
        return bar;
    }
 
    // ════════════════════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════════════════════
 
    /**
     * Change le panel affiché dans la zone centrale
     * et met à jour l'état actif du menu sidebar.
     */
    public void navigateTo(String action, JButton source) {
        // Reset tous les items sidebar
        resetMenuItems();
        if (source != null) {
            source.setBackground(SIDEBAR_ITEM);
            source.setForeground(Color.WHITE);
        }
 
        // Mise à jour du titre
        String titre = switch (action) {
            case "dashboard"  -> "Tableau de bord";
            case "sorties"    -> "Sortie de toners";
            case "entree"     -> "Entrée en stock";
            case "toners"     -> "Gérer les toners";
            case "historique" -> "Historique des mouvements";
            case "stats"      -> "Statistiques";
            case "users"      -> "Gestion des utilisateurs";
            case "notifs"     -> "Notifications";
            case "profil"     -> "Mon profil";
            default           -> action;
        };
        lblTopTitle.setText(titre);
 
        // Chargement du panel correspondant
        JPanel panel = switch (action) {
            case "dashboard"  -> new DashboardPanel(this);
            case "sorties"    -> new SortiePanel(this);
            case "entree"     -> new EntreeStockPanel(this);
            case "toners"     -> new GestionTonerPanel(this);
            case "historique" -> new HistoriquePanel(this);
            case "stats"      -> new StatistiquesPanel(this);
            case "users"      -> new GestionUsersPanel(this);
            case "notifs"     -> new NotificationsPanel(this);
            case "profil"     -> new ProfilPanel(this);
            default           -> new DashboardPanel(this);
        };
 
        afficherPanel(panel);
    }
 
    public void afficherPanel(JPanel panel) {
        contentPanel.removeAll();
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(PAGE_BG);
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
 
    private void showDashboard() {
        navigateTo("dashboard", null);
    }
 
    private void resetMenuItems() {
        // Parcourir récursivement tous les boutons de la sidebar
        resetButtonsIn(getContentPane());
    }
 
    private void resetButtonsIn(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JButton btn && btn.getBackground().equals(SIDEBAR_ITEM)) {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(SIDEBAR_TEXT);
                btn.repaint();
            }
            if (comp instanceof Container) resetButtonsIn((Container) comp);
        }
    }
 
    // ════════════════════════════════════════════════════════
    //  ALERTES & NOTIFICATIONS
    // ════════════════════════════════════════════════════════
 
    /** Vérifie les alertes au démarrage et affiche un popup si critique */
    private void verifierAlertesDemarrage() {
        List<Toner> alertes = tonerDAO.findEnAlerte();
        if (alertes.isEmpty()) return;
 
        // Mise à jour badge
        mettreAJourBadgeNotif();
 
        // Popup d'alerte
        long ruptures = alertes.stream().filter(t -> t.getQuantiteStock() == 0).count();
        StringBuilder msg = new StringBuilder();
 
        if (ruptures > 0) {
            msg.append("🔴 RUPTURE TOTALE (").append(ruptures).append(" toner(s)) :\n");
            alertes.stream()
                .filter(t -> t.getQuantiteStock() == 0)
                .forEach(t -> msg.append("   • ").append(t.getReference())
                    .append(" — ").append(t.getCouleur()).append("\n"));
            msg.append("\n");
        }
 
        long sousSeul = alertes.stream().filter(t -> t.getQuantiteStock() > 0).count();
        if (sousSeul > 0) {
            msg.append("🟠 STOCK FAIBLE (").append(sousSeul).append(" toner(s)) :\n");
            alertes.stream()
                .filter(t -> t.getQuantiteStock() > 0)
                .forEach(t -> msg.append("   • ").append(t.getReference())
                    .append(" : ").append(t.getQuantiteStock()).append(" / seuil ").append(t.getSeuilAlerte()).append("\n"));
        }
 
        JOptionPane.showMessageDialog(
            this, msg.toString(),
            "⚠ Alertes de stock détectées",
            JOptionPane.WARNING_MESSAGE);
    }
 
    /** Met à jour le badge rouge sur l'icône notifications */
    public void mettreAJourBadgeNotif() {
        if (!SessionManager.isAdmin() || lblNotifBadge == null) return;
        int nb = tonerDAO.findEnAlerte().size();
        if (nb > 0) {
            lblNotifBadge.setText(String.valueOf(nb));
            lblNotifBadge.setVisible(true);
        } else {
            lblNotifBadge.setVisible(false);
        }
    }
 
    // ════════════════════════════════════════════════════════
    //  UTILITAIRES
    // ════════════════════════════════════════════════════════
 
    private void mettreAJourDate() {
        String date = new SimpleDateFormat("EEEE dd MMMM yyyy  |  HH:mm",
            new java.util.Locale("fr", "FR")).format(new Date());
        // Capitaliser première lettre
        lblDate.setText(Character.toUpperCase(date.charAt(0)) + date.substring(1));
    }
 
    private void deconnecter() {
        int rep = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment vous déconnecter ?",
            "Déconnexion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (rep == JOptionPane.YES_OPTION) {
            refreshTimer.stop();
            SessionManager.deconnecter();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
 
    /** Tente de charger l'icône de la fenêtre */
    private Image loadIcon() {
        try {
            // Cherche le logo DHIA dans le classpath
            java.net.URL url = getClass().getResource("/resources/logo_dhia.png");
            if (url != null) return new ImageIcon(url).getImage();
        } catch (Exception ignored) {}
        return null;
    }
 
    // ════════════════════════════════════════════════════════
    //  MÉTHODES UTILITAIRES STATIQUES (utilisées par les panels)
    // ════════════════════════════════════════════════════════
 
    /** Crée une carte blanche avec titre et contenu */
    public static JPanel createCard(String titre, JComponent contenu) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
 
        if (titre != null && !titre.isEmpty()) {
            JLabel lbl = new JLabel(titre);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(TEXT_DARK);
            lbl.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(10, 14, 10, 14)));
            card.add(lbl, BorderLayout.NORTH);
        }
 
        card.add(contenu, BorderLayout.CENTER);
        return card;
    }
 
    /** Crée un bouton stylisé bleu DHIA */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(DHIA_BLUE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT_DARK); btn.repaint(); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(DHIA_BLUE);   btn.repaint(); }
        });
        return btn;
    }
 
    /** Crée un bouton secondaire (gris) */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_DARK);
        btn.setBackground(CARD_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(7, 14, 7, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(245,245,250)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(CARD_BG); }
        });
        return btn;
    }
 
    /** Crée un badge coloré */
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(2, 8, 2, 8));
        return lbl;
    }
 
    /** Crée un champ de saisie stylisé */
    public static JTextField createStyledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 182, 200));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight()/2 + 5);
                }
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT_DARK);
        f.setBackground(new Color(248, 248, 252));
        f.setCaretColor(DHIA_BLUE);
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(0, 10, 0, 10)));
        f.setPreferredSize(new Dimension(200, 34));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(DHIA_BLUE, 1),
                    new EmptyBorder(0, 10, 0, 10)));
                f.setBackground(Color.WHITE);
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1),
                    new EmptyBorder(0, 10, 0, 10)));
                f.setBackground(new Color(248, 248, 252));
            }
        });
        return f;
    }
 
    // ════════════════════════════════════════════════════════
    //  GETTERS pour les panels enfants
    // ════════════════════════════════════════════════════════
 
    public TonerDAO     getTonerDAO()     { return tonerDAO; }
    public MouvementDAO getMouvementDAO() { return mouvementDAO; }
    public StockService getStockService() { return stockService; }
}
