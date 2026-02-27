import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Swing GUI application for the football team demo.
 * Enhanced with Philadelphia Eagles team colors and styling.
 * Uses CSV file for player data.
 */
public class EaglesRosterManagerUnpolished {
    private final String teamName = "Philadelphia Eagles";
    private final String coachName = "Nick Sirianni";
    private final String stadiumName = "Lincoln Financial Field";
    private final String description = "Based in Philadelphia, Pennsylvania, the Philadelphia Eagles are a professional football team that plays in the National Football League's (NFL) East division of the National Football Conference (NFC). Explore the roster below!";
    
    private final java.util.List<Map<String, String>> players = new ArrayList<>();
    
    // Philadelphia Eagles Official Colors
    private static final Color MIDNIGHT_GREEN = new Color(0, 76, 84);      // Primary
    private static final Color SILVER = new Color(165, 172, 175);          // Secondary
    private static final Color BLACK = new Color(0, 0, 0);                 // Accent
    private static final Color WHITE = new Color(255, 255, 255);           // Text
    private static final Color DARK_GREEN = new Color(0, 50, 56);          // Darker shade
    private static final Color LIGHT_GREEN = new Color(0, 95, 106);        // Lighter shade
    private static final Color CHARCOAL = new Color(32, 32, 32);           // Dark background

    public EaglesRosterManagerUnpolished() {
    }

    private void createAndShowGui() {
        JFrame frame = new JFrame(teamName + " - Roster Manager");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(CHARCOAL);
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top: Team info with Eagles styling
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(MIDNIGHT_GREEN);
        top.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SILVER, 3),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Title with larger font
        JLabel title = new JLabel("🦅 " + teamName);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(WHITE);
        top.add(title, BorderLayout.NORTH);

        // Description
        JTextArea desc = new JTextArea(description);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBackground(MIDNIGHT_GREEN);
        desc.setForeground(WHITE);
        desc.setFont(new Font("Arial", Font.PLAIN, 13));
        desc.setBorder(new EmptyBorder(10, 0, 10, 0));
        top.add(desc, BorderLayout.CENTER);

        // Meta information panel
        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        meta.setBackground(DARK_GREEN);
        meta.setBorder(new EmptyBorder(8, 10, 8, 10));
        
        JLabel coachLabel = new JLabel("🏈 Coach: " + coachName);
        coachLabel.setForeground(WHITE);
        coachLabel.setFont(new Font("Arial", Font.BOLD, 13));
        meta.add(coachLabel);
        
        JLabel stadiumLabel = new JLabel("🏟️ Stadium: " + stadiumName);
        stadiumLabel.setForeground(WHITE);
        stadiumLabel.setFont(new Font("Arial", Font.BOLD, 13));
        meta.add(stadiumLabel);
        
        top.add(meta, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);

        // Center: roster list and details
        JSplitPane split = new JSplitPane();
        split.setBackground(CHARCOAL);
        split.setDividerSize(8);

        // Left panel - Roster List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(CHARCOAL);
        leftPanel.setBorder(new EmptyBorder(5, 0, 0, 5));
        
        // Top section with title and dropdown
        JPanel leftTopPanel = new JPanel(new BorderLayout(10, 0));
        leftTopPanel.setBackground(CHARCOAL);
        leftTopPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        JLabel rosterTitle = new JLabel("ROSTER");
        rosterTitle.setFont(new Font("Arial", Font.BOLD, 16));
        rosterTitle.setForeground(SILVER);
        leftTopPanel.add(rosterTitle, BorderLayout.WEST);
        
        leftPanel.add(leftTopPanel, BorderLayout.NORTH);

        DefaultListModel<Map<String, String>> playerListModel = new DefaultListModel<>();
        for (Map<String, String> p : players) {
            playerListModel.addElement(p);
        }
        
        JList<Map<String, String>> rosterList = new JList<>(playerListModel);
        rosterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rosterList.setBackground(DARK_GREEN);
        rosterList.setForeground(WHITE);
        rosterList.setSelectionBackground(LIGHT_GREEN);
        rosterList.setSelectionForeground(WHITE);
        rosterList.setFont(new Font("Arial", Font.PLAIN, 13));
        rosterList.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        rosterList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> player = (Map<String, String>) value;
                    String number = player.getOrDefault("number", "");
                    String name = player.getOrDefault("name", "");
                    String position = player.getOrDefault("position", "");
                    setText("#" + number + " - " + name + " (" + position + ")");
                    setBorder(new EmptyBorder(8, 10, 8, 10));
                    if (isSelected) {
                        setBackground(LIGHT_GREEN);
                        setForeground(WHITE);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setBackground(DARK_GREEN);
                        setForeground(WHITE);
                    }
                }
                return this;
            }
        });

        JScrollPane rosterScroll = new JScrollPane(rosterList);
        rosterScroll.setBorder(new LineBorder(MIDNIGHT_GREEN, 2));
        rosterScroll.getViewport().setBackground(DARK_GREEN);
        leftPanel.add(rosterScroll, BorderLayout.CENTER);
        
        split.setLeftComponent(leftPanel);

        // Right panel - Details (Player, Coach, or Staff)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(CHARCOAL);
        rightPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
        
        JLabel detailsTitle = new JLabel("DETAILS");
        detailsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        detailsTitle.setForeground(SILVER);
        detailsTitle.setBorder(new EmptyBorder(5, 10, 10, 10));
        rightPanel.add(detailsTitle, BorderLayout.NORTH);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setFont(new Font("Arial", Font.PLAIN, 14));
        details.setBackground(DARK_GREEN);
        details.setForeground(WHITE);
        details.setBorder(new EmptyBorder(15, 15, 15, 15));
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        
        JScrollPane detailsScroll = new JScrollPane(details);
        detailsScroll.setBorder(new LineBorder(MIDNIGHT_GREEN, 2));
        detailsScroll.getViewport().setBackground(DARK_GREEN);
        rightPanel.add(detailsScroll, BorderLayout.CENTER);
        
        split.setRightComponent(rightPanel);
        split.setDividerLocation(425);

        root.add(split, BorderLayout.CENTER);

        // Bottom: search and stats
        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBackground(CHARCOAL);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(MIDNIGHT_GREEN);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SILVER, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel searchLabel = new JLabel("🔍 SEARCH ROSTER:");
        searchLabel.setForeground(WHITE);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        
        JTextField search = new JTextField(30);
        search.setFont(new Font("Arial", Font.PLAIN, 14));
        search.setBackground(WHITE);
        search.setForeground(BLACK);
        search.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SILVER, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(search, BorderLayout.CENTER);
        
        bottom.add(searchPanel, BorderLayout.NORTH);

        // Filter panel with dropdowns
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        filterPanel.setBackground(MIDNIGHT_GREEN);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SILVER, 1),
            new EmptyBorder(5, 15, 5, 15)
        ));
        
        JLabel roleLabel = new JLabel("Filter by Role:");
        roleLabel.setForeground(WHITE);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] roles = {"All Roles", "Player", "Coach", "Staff"};
        JComboBox<String> roleDropdown = new JComboBox<>(roles);
        roleDropdown.setBackground(DARK_GREEN);
        roleDropdown.setForeground(WHITE);
        roleDropdown.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel typeLabel = new JLabel("Filter by Type:");
        typeLabel.setForeground(WHITE);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] types = {"All Types", "Offense", "Defense"};
        JComboBox<String> typeDropdown = new JComboBox<>(types);
        typeDropdown.setBackground(DARK_GREEN);
        typeDropdown.setForeground(WHITE);
        typeDropdown.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(WHITE);
        sortLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] sortOptions = {"Alphabetical", "Jersey Number", "Position"};
        JComboBox<String> sortDropdown = new JComboBox<>(sortOptions);
        sortDropdown.setBackground(DARK_GREEN);
        sortDropdown.setForeground(WHITE);
        sortDropdown.setFont(new Font("Arial", Font.PLAIN, 12));
        
        filterPanel.add(roleLabel);
        filterPanel.add(roleDropdown);
        filterPanel.add(typeLabel);
        filterPanel.add(typeDropdown);
        filterPanel.add(sortLabel);
        filterPanel.add(sortDropdown);
        
        bottom.add(filterPanel, BorderLayout.CENTER);

        // Stats panel
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(DARK_GREEN);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MIDNIGHT_GREEN, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel statsTitle = new JLabel("📊 TEAM STATISTICS");
        statsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        statsTitle.setForeground(SILVER);
        statsTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        statsPanel.add(statsTitle, BorderLayout.NORTH);

        JTextArea stats = new JTextArea();
        stats.setEditable(false);
        stats.setBackground(DARK_GREEN);
        stats.setForeground(WHITE);
        stats.setFont(new Font("Arial", Font.PLAIN, 13));
        StringBuilder statsText = new StringBuilder();
        statsText.append("  • Total Players: ").append(players.size()).append("\n");
        statsText.append("  • Total Points (This Season): 379\n");
        statsText.append("  • Total Touchdowns (This Season): 45\n");
        statsText.append("  • Super Bowls Won: 2\n");
        statsText.append("  • Total Seasons: 93\n");
        statsText.append("  • Record (W/L/T): 649/645/27\n");
        stats.setText(statsText.toString());
        stats.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane statsScroll = new JScrollPane(stats);
        statsScroll.setBorder(null);
        statsScroll.setPreferredSize(new Dimension(0, 100));
        statsScroll.getViewport().setBackground(DARK_GREEN);
        statsPanel.add(statsScroll, BorderLayout.CENTER);

        bottom.add(statsPanel, BorderLayout.SOUTH);

        root.add(bottom, BorderLayout.SOUTH);

        // List selection -> details
        rosterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Map<String, String> selected = (Map<String, String>) rosterList.getSelectedValue();
                if (selected != null) {
                    details.setText(formatPlayerDetails(selected));
                } else {
                    details.setText("");
                }
            }
        });
        
        // Search filter with role and type dropdowns
        Runnable updateFilter = () -> {
            String q = search.getText().trim().toLowerCase(Locale.ROOT);
            String selectedRole = (String) roleDropdown.getSelectedItem();
            String selectedType = (String) typeDropdown.getSelectedItem();
            String sortBy = (String) sortDropdown.getSelectedItem();
            
            java.util.List<Map<String, String>> filtered = new ArrayList<>();
            for (Map<String, String> p : players) {
                String name = p.getOrDefault("name", "").toLowerCase(Locale.ROOT);
                String position = p.getOrDefault("position", "").toLowerCase(Locale.ROOT);
                String number = p.getOrDefault("number", "");
                String role = p.getOrDefault("role", "");
                String type = p.getOrDefault("type", "");
                
                // Check search query
                boolean matchesSearch = q.isEmpty() || name.contains(q) || 
                                    number.equals(q) || position.contains(q);
                
                // Check role filter
                boolean matchesRole = "All Roles".equals(selectedRole) || 
                                    role.equals(selectedRole);
                
                // Check type filter
                boolean matchesType = "All Types".equals(selectedType) || 
                                    type.equals(selectedType);
                
                if (matchesSearch && matchesRole && matchesType) {
                    filtered.add(p);
                }
            }
            
            // Sort the filtered results
            switch (sortBy) {
                case "Jersey Number" -> filtered.sort((a, b) -> {
                    String numStrA = a.getOrDefault("number", "");
                    String numStrB = b.getOrDefault("number", "");
                    
                    // Check if either is N/A or empty
                    boolean isNAA = numStrA.isEmpty() || numStrA.equals("N/A") || numStrA.equals("N/a");
                    boolean isNAB = numStrB.isEmpty() || numStrB.equals("N/A") || numStrB.equals("N/a");
                    
                    // If both are N/A, maintain order
                    if (isNAA && isNAB) return 0;
                    // If A is N/A, put it at the end
                    if (isNAA) return 1;
                    // If B is N/A, put it at the end
                    if (isNAB) return -1;
                    
                    // Both have valid numbers, sort numerically
                    try {
                        int numA = Integer.parseInt(numStrA);
                        int numB = Integer.parseInt(numStrB);
                        return Integer.compare(numA, numB);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                });
                case "Position" -> filtered.sort((a, b) -> 
                    a.getOrDefault("position", "").compareTo(b.getOrDefault("position", ""))
                );
                default -> filtered.sort((a, b) -> 
                    a.getOrDefault("name", "").compareTo(b.getOrDefault("name", ""))
                );
            }
            
            playerListModel.clear();
            for (Map<String, String> p : filtered) {
                playerListModel.addElement(p);
            }
        };
        
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateFilter.run(); }
            
            @Override
            public void removeUpdate(DocumentEvent e) { updateFilter.run(); }
            
            @Override
            public void changedUpdate(DocumentEvent e) { updateFilter.run(); }
        });
        
        // Add listener for role dropdown
        roleDropdown.addActionListener(e -> updateFilter.run());
        
        // Add listener for type dropdown
        typeDropdown.addActionListener(e -> updateFilter.run());
        
        // Add listener for sort dropdown
        sortDropdown.addActionListener(e -> updateFilter.run());

        frame.setContentPane(root);
        frame.addWindowListener(new WindowAdapter() {
    @Override
    public void windowClosed(WindowEvent e) {
        // Clean, centered white-card farewell dialog
        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setBackground(WHITE);
        msgPanel.setBorder(new EmptyBorder(12, 18, 12, 18));

        // Title
        JLabel goodbyeTitle = new JLabel("Thanks for using the Philadelphia Eagles Roster Manager!");
        goodbyeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        goodbyeTitle.setForeground(MIDNIGHT_GREEN);
        goodbyeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        goodbyeTitle.setBorder(new EmptyBorder(8, 0, 4, 0));
        msgPanel.add(goodbyeTitle);

        // Subtitle
        JLabel subtitle = new JLabel("We hope you enjoyed managing the roster.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(CHARCOAL);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        msgPanel.add(subtitle);

        // Tagline
        JLabel tagline = new JLabel("Go Birds! 🦅");
        tagline.setFont(new Font("Arial", Font.BOLD, 14));
        tagline.setForeground(LIGHT_GREEN);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(8, 0, 0, 0));
        msgPanel.add(tagline);

        JOptionPane.showMessageDialog(frame, msgPanel,
                "Goodbye - Eagles Roster Manager",
                JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
            }
        });
    
        frame.setIconImage(createEaglesIcon());
        frame.setVisible(true);
    }

    private String formatPlayerDetails(Map<String, String> player) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("  PLAYER INFORMATION\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("  Number:     #").append(player.getOrDefault("number", "N/A")).append('\n');
        sb.append("  Name:       ").append(player.getOrDefault("name", "N/A")).append('\n');
        sb.append("  Position:   ").append(player.getOrDefault("position", "N/A")).append('\n');
        sb.append("  Role:       ").append(player.getOrDefault("role", "N/A")).append('\n');
        sb.append("  Type:       ").append(player.getOrDefault("type", "N/A")).append('\n');
        sb.append("\n═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    private Image createEaglesIcon() {
        // Create a simple Eagles-colored icon
        int size = 64;
        Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle background
        g2d.setColor(MIDNIGHT_GREEN);
        g2d.fillOval(0, 0, size, size);
        
        // Draw border
        g2d.setColor(SILVER);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(2, 2, size - 4, size - 4);
        
        g2d.dispose();
        return img;
    }

    public class UserInfoCSVHandler {
    
    /**
     * Save user information to a CSV file
     */
    public static void saveUserInfo(String name, String email, String favoriteTeam, String filename) throws IOException {
        boolean fileExists = Files.exists(Paths.get(filename));
        
        try (FileWriter fw = new FileWriter(filename, true);
            PrintWriter writer = new PrintWriter(fw)) {
            
            // Write header if file doesn't exist
            if (!fileExists) {
                writer.println("Name,Email,Favorite Team,Login Date/Time");
            }
            
            // Write user data with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String line = String.format("%s,%s,%s,%s",
                escapeCSV(name),
                escapeCSV(email),
                escapeCSV(favoriteTeam),
                timestamp
            );
            writer.println(line);
        }
    }
    
    /**
     * Escape special characters in CSV fields
     */
    private static String escapeCSV(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

    public static void main(String[] args) {
        // Show welcome pop-up and require user input for Name and Favorite Team
        while (true) {
            JTextField nameField = new JTextField(15);
            JTextField emailField = new JTextField(15);
            JTextField teamFanField = new JTextField(15);

            JPanel welcomePanel = new JPanel(new BorderLayout());
            welcomePanel.setBackground(MIDNIGHT_GREEN);
            welcomePanel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JLabel welcomeTitle = new JLabel("🦅 Welcome to the Philadelphia Eagles Roster Manager!");
            welcomeTitle.setFont(new Font("Arial", Font.BOLD, 18));
            welcomeTitle.setForeground(WHITE);
            welcomeTitle.setBorder(new EmptyBorder(8, 8, 12, 8));
            welcomePanel.add(welcomeTitle, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(MIDNIGHT_GREEN);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setForeground(WHITE);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            form.add(nameLabel, gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            form.add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setForeground(WHITE);
            emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            form.add(emailLabel, gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            form.add(emailField, gbc);

            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            JLabel teamFanLabel = new JLabel("Favorite Team:");
            teamFanLabel.setForeground(WHITE);
            teamFanLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            form.add(teamFanLabel, gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            form.add(teamFanField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1.0;
            JLabel hint = new JLabel("Please provide your Name, Email, and Favorite Team to continue.");
            hint.setForeground(SILVER);
            hint.setFont(new Font("Arial", Font.ITALIC, 12));
            hint.setBorder(new EmptyBorder(8, 6, 4, 6));
            form.add(hint, gbc);

            welcomePanel.add(form, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(null, welcomePanel,
                    "Welcome - Eagles Roster Manager",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String teamFan = teamFanField.getText().trim();

            if (name.isEmpty() || teamFan.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter your Name, Email, and Favorite Team to continue.", "Input Required", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            try {
                UserInfoCSVHandler.saveUserInfo(name, email, teamFan, "userinfo.csv");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving user info: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            break;
        }
        
        try {
            ensureTeamCsvExists("team.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error creating team.csv: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            EaglesRosterManagerUnpolished gui = new EaglesRosterManagerUnpolished();
            try {
                gui.loadPlayersFromCSV("team.csv");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading players: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            gui.createAndShowGui();
        });
    }

    /**
     * If `team.csv` is missing, create it and populate with a full default roster.
     */
    private static void ensureTeamCsvExists(String filename) throws IOException {
        if (!Files.exists(Paths.get(filename))) {
            try (FileWriter fw = new FileWriter(filename, false);
                PrintWriter pw = new PrintWriter(fw)) {
                pw.println("Name,Role,Position,Number,Offense/Defense");
                pw.println("Sam Howell,Player,Quarterback,14,Offense");
                pw.println("Jalen Hurts,Player,Quarterback,1,Offense");
                pw.println("Tanner McKee,Player,Quarterback,16,Offense");
                pw.println("Saquon Barkley,Player,Runningback,26,Offense");
                pw.println("Tank Bigsby,Player,Runningback,37,Offense");
                pw.println("AJ Dillon,Player,Runningback,29,Offense");
                pw.println("Will Shipley,Player,Runningback,28,Offense");
                pw.println("Carson Steele,Player,Runningback,42,Offense");
                pw.println("A.J. Brown,Player,Wide Receiver,11,Offense");
                pw.println("Darius Cooper,Player,Wide Receiver,80,Offense");
                pw.println("Britain Covey,Player,Wide Receiver,18,Offense");
                pw.println("Jahan Dotson,Player,Wide Receiver,2,Offense");
                pw.println("Danny Gray,Player,Wide Receiver,46,Offense");
                pw.println("DeVonta Smith,Player,Wide Receiver,6,Offense");
                pw.println("Quez Watkins,Player,Wide Receiver,16,Offense");
                pw.println("Grant Calcaterra,Player,Tight End,81,Offense");
                pw.println("Dallas Goedert,Player,Tight End,88,Offense");
                pw.println("Kylen Granson,Player,Tight End,83,Offense");
                pw.println("E.J. Jenkins,Player,Tight End,84,Offense");
                pw.println("Cameron Latu,Player,Tight End,36,Offense");
                pw.println("Cam Jurgens,Player,Center,51,Offense");
                pw.println("Drew Kendall,Player,Center,66,Offense");
                pw.println("Jake Majors,Player,Center,75,Offense");
                pw.println("Brett Toth,Player,Center,64,Offense");
                pw.println("Landon Dickerson,Player,Guard,69,Offense");
                pw.println("Tyler Steen,Player,Guard,56,Offense");
                pw.println("Fred Johnson,Player,Offensive Tackle,74,Offense");
                pw.println("Lane Johnson,Player,Offensive Tackle,65,Offense");
                pw.println("Jordan Mailata,Player,Offensive Tackle,68,Offense");
                pw.println("John Ojukwu,Player,Offensive Tackle,61,Offense");
                pw.println("Hollin Pierce,Player,Offensive Tackle,63,Offense");
                pw.println("Matt Pryor,Player,Offensive Tackle,79,Offense");
                pw.println("Cameron Williams,Player,Offensive Tackle,73,Offense");
                pw.println("Brandon Graham,Player,Defensive End,55,Defense");
                pw.println("Jose Ramirez,Player,Defensive End,N/A,Defense");
                pw.println("Jalen Carter,Player,Defensive Tackle,98,Defense");
                pw.println("Jordan Davis,Player,Defensive Tackle,90,Defense");
                pw.println("Gabe Hall,Player,Defensive Tackle,96,Defense");
                pw.println("Moro Ojomo,Player,Defensive Tackle,97,Defense");
                pw.println("Ty Robinson,Player,Defensive Tackle,95,Defense");
                pw.println("Jacob Sykes,Player,Defensive Tackle,93,Defense");
                pw.println("Byron Young,Player,Defensive Tackle,94,Defense");
                pw.println("Zack Baun,Player,Linebacker,53,Defense");
                pw.println("Chance Campbell,Player,Linebacker,59,Defense");
                pw.println("Jihaad Campbell,Player,Linebacker,30,Defense");
                pw.println("Nakobe Dean,Player,Linebacker,17,Defense");
                pw.println("Jalyx Hunt,Player,Linebacker,58,Defense");
                pw.println("Smael Mondon Jr,Player,Linebacker,42,Defense");
                pw.println("Jaelan Phillips,Player,Linebacker,50,Defense");
                pw.println("Nolan Smith Jr,Player,Linebacker,3,Defense");
                pw.println("Jeremiah Trotter Jr,Player,Linebacker,54,Defense");
                pw.println("Joshua Uche,Player,Linebacker,0,Defense");
                pw.println("Jakorian Bennett,Player,Cornerback,23,Defense");
                pw.println("Michael Carter II,Player,Cornerback,35,Defense");
                pw.println("Tariq Castro-Fields,Player,Cornerback,46,Defense");
                pw.println("Cooper DeJean,Player,Cornerback,33,Defense");
                pw.println("Adoree' Jackson,Player,Cornerback,8,Defense");
                pw.println("Brandon Johnson,Player,Cornerback,49,Defense");
                pw.println("Mac McWilliams,Player,Cornerback,22,Defense");
                pw.println("Quinyon Mitchell,Player,Cornerback,27,Defense");
                pw.println("Kelee Ringo,Player,Cornerback,7,Defense");
                pw.println("Ambry Thomas,Player,Defensive Back,38,Defense");
                pw.println("Reed Blankenship,Player,Safety,32,Defense");
                pw.println("Sydney Brown,Player,Safety,21,Defense");
                pw.println("Marcus Epps,Player,Safety,39,Defense");
                pw.println("Andre' Sam,Player,Safety,31,Defense");
                pw.println("Nick Sirianni,Coach,Head Coach,N/A,N/A");
                pw.println("Michael Clay,Coach,Specail Teams Coordinator,N/A,N/A");
                pw.println("Vic Fangio,Coach,Defensive Coordinator,N/A,Defense");
                pw.println("Kevin Patullo,Coach,Offensive Coordinator,N/A,Offense");
                pw.println("Roy Anderson,Coach,Cornerbacks Coach,N/A,Defense");
                pw.println("Joe Kasper,Coach,Safties Coach,N/A,Defense");
                pw.println("Bobby King,Coach,Inside Linebackers Coach,N/A,Defense");
                pw.println("Scot Loeffler,Coach,Quarterbacks Coach,N/A,Offense");
                pw.println("Jaon Michael,Coach,Tight Ends Coach,N/A,Offense");
                pw.println("Aaron Moorehead,Coach,Wide Receivers Coach,N/A,Offense");
                pw.println("Don Smolenski,Staff,President,N/A,N/A");
                pw.println("Jeffery Lurie,Staff,Chairman/CEO,N/A,N/A");
                pw.println("Christian Molnar,Staff,Director of Team Relationships,N/A,N/A");
                pw.println("Daniel Goldsmith,Staff,Business Manager,N/A,N/A");
                pw.println("Tara Sutphen,Staff,Operations and Event Director,N/A,N/A");
                pw.println("Howie Roseman,Staff,General Manager,N/A,N/A");
                pw.println("Dom DiSandro,Staff,CSO/Gameday Coaching Operations,N/A,N/A");
                pw.println("Conner Barwin,Staff,Head of Football Development and Strategy,N/A,N/A");
                pw.println("Kevin Dougherty,Staff,Video Director,N/A,N/A");
                pw.println("Dan Ryan,Staff,Director of Team Travel and Football Logistics,N/A,N/A");
                pw.println("Kathy Mair,Staff,Player Resource Coordinator,N/A,N/A");
                pw.println("Nick Church,Staff,Lead Software Innovator,N/A,N/A");
                pw.println("Matt Leo,Staff,Player Development Assistant,N/A,N/A");
                pw.println("Kevin Mahon,Staff,Football Creative Services Producer,N/A,N/A");
                pw.println("Patrick McDowll,Staff,Scout,N/A,N/A");
                pw.println("Grant Reiter,Staff,Football Transactions Coordinator,N/A,N/A");
                pw.println("Molly Rottinghaus,Staff,Football Operations Coordinator,N/A,N/A");
                pw.println("Leif Thorson,Staff,Software Developer,N/A,N/A");
                pw.println("Preston Tiffany,Staff,NFS Scout,N/A,N/A");
                pw.println("Terrance Braxton,Staff,Pro Scout,N/A,N/A");
                pw.println("Ameena Soliman,Staff,Director of Football Opertations/Pro Scout,N/A,N/A");
                pw.println("Julian Lurie,Staff,Business and Football Operations Strategy,N/A,N/A");
                pw.println("Fernando Noriega,Staff,Director of Player Performance and Sports Science,N/A,N/A");
                pw.println("Dustin Woods,Staff,Interpersonal Performance Director,N/A,N/A");
                pw.println("Steven Feldman,Staff,Coordinator of Rehabilitation,N/A,N/A");
                pw.println("Stephanie Coppola,Staff,Performance Nutririon Coordinator,N/A,N/A");
                pw.println("Dr. Arsh S. Dhanota,Staff,Head Team Physician,N/A,N/A");
                pw.println("Dr. Peter DeLuca,Staff,Head Orthopedic Surgeon,N/A,N/A");
                pw.println("Dr. Johannes Roedl,Staff,Musculoskeletal / Interventional Radiologist,N/A,N/A");
                pw.println("Alessandra Lane,Staff,Director of Live Event Production,N/A,N/A");
                pw.println("Summer Gilliam,Staff,Live Events Producer,N/A,N/A");
            }
        }
    }

    private void loadPlayersFromCSV(String filename) throws IOException {
        java.util.List<String> lines = Files.readAllLines(Paths.get(filename));
        
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                Map<String, String> player = new HashMap<>();
                player.put("name", parts[0].trim());
                player.put("role", parts[1].trim());
                player.put("position", parts[2].trim());
                player.put("number", parts[3].trim());
                player.put("type", parts[4].trim());
                
                players.add(player);
            }
        }
    }
}