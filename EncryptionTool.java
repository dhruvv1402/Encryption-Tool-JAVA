import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionTool {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int AES_KEY_BIT = 256;
    private static final int ITERATION_COUNT = 65536;

    private JFrame frame;
    private JTextField filePathField;
    private JPasswordField passwordField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton browseButton;
    private JLabel statusLabel;

    public static void main(String[] args) {
        if (args.length > 0) {
            // Command-line mode
            handleCommandLine(args);
        } else {
            // GUI mode
            SwingUtilities.invokeLater(() -> {
                try {
                    EncryptionTool window = new EncryptionTool();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void handleCommandLine(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java EncryptionTool [encrypt/decrypt] [file_path] [password]");
            return;
        }

        String mode = args[0].toLowerCase();
        String filePath = args[1];
        String password = args[2];

        try {
            if ("encrypt".equals(mode)) {
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                byte[] encryptedData = encrypt(fileContent, password);
                Files.write(Paths.get(filePath + ".enc"), encryptedData);
                System.out.println("File encrypted successfully: " + filePath + ".enc");
            } else if ("decrypt".equals(mode)) {
                byte[] encryptedData = Files.readAllBytes(Paths.get(filePath));
                byte[] decryptedData = decrypt(encryptedData, password);
                String outputPath = filePath;
                if (outputPath.endsWith(".enc")) {
                    outputPath = outputPath.substring(0, outputPath.length() - 4);
                } else {
                    outputPath = outputPath + ".dec";
                }
                Files.write(Paths.get(outputPath), decryptedData);
                System.out.println("File decrypted successfully: " + outputPath);
            } else {
                System.out.println("Invalid mode. Use 'encrypt' or 'decrypt'.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public EncryptionTool() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Retro Encryption Tool");
        frame.setBounds(100, 100, 500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Set retro theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a custom font for retro look
        Font retroFont = new Font("Courier New", Font.BOLD, 14);

        // Main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(0, 0, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3, 1, 10, 10));
        contentPanel.setBackground(new Color(0, 0, 0));

        // File selection panel
        JPanel filePanel = new JPanel(new BorderLayout(10, 0));
        filePanel.setBackground(new Color(0, 0, 0));

        JLabel fileLabel = new JLabel("File:");
        fileLabel.setForeground(new Color(0, 255, 0));
        fileLabel.setFont(retroFont);
        filePanel.add(fileLabel, BorderLayout.WEST);

        filePathField = new JTextField();
        filePathField.setBackground(new Color(20, 20, 20));
        filePathField.setForeground(new Color(0, 255, 0));
        filePathField.setCaretColor(new Color(0, 255, 0));
        filePathField.setFont(retroFont);
        filePathField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0)));
        filePanel.add(filePathField, BorderLayout.CENTER);

        browseButton = new JButton("Browse");
        browseButton.setBackground(new Color(20, 20, 20));
        browseButton.setForeground(new Color(0, 255, 0));
        browseButton.setFont(retroFont);
        browseButton.setFocusPainted(false);
        browseButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0)));
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select File");
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        filePanel.add(browseButton, BorderLayout.EAST);

        // Password panel
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setBackground(new Color(0, 0, 0));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(0, 255, 0));
        passwordLabel.setFont(retroFont);
        passwordPanel.add(passwordLabel, BorderLayout.WEST);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(20, 20, 20));
        passwordField.setForeground(new Color(0, 255, 0));
        passwordField.setCaretColor(new Color(0, 255, 0));
        passwordField.setFont(retroFont);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0)));
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(new Color(0, 0, 0));

        encryptButton = new JButton("Encrypt");
        encryptButton.setBackground(new Color(20, 20, 20));
        encryptButton.setForeground(new Color(0, 255, 0));
        encryptButton.setFont(retroFont);
        encryptButton.setFocusPainted(false);
        encryptButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0)));
        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String filePath = filePathField.getText();
                    String password = new String(passwordField.getPassword());
                    
                    if (filePath.isEmpty() || password.isEmpty()) {
                        statusLabel.setText("Please provide both file and password");
                        return;
                    }
                    
                    byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                    byte[] encryptedData = encrypt(fileContent, password);
                    Files.write(Paths.get(filePath + ".enc"), encryptedData);
                    statusLabel.setText("File encrypted successfully: " + filePath + ".enc");
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(encryptButton);

        decryptButton = new JButton("Decrypt");
        decryptButton.setBackground(new Color(20, 20, 20));
        decryptButton.setForeground(new Color(0, 255, 0));
        decryptButton.setFont(retroFont);
        decryptButton.setFocusPainted(false);
        decryptButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0)));
        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String filePath = filePathField.getText();
                    String password = new String(passwordField.getPassword());
                    
                    if (filePath.isEmpty() || password.isEmpty()) {
                        statusLabel.setText("Please provide both file and password");
                        return;
                    }
                    
                    byte[] encryptedData = Files.readAllBytes(Paths.get(filePath));
                    byte[] decryptedData = decrypt(encryptedData, password);
                    
                    String outputPath = filePath;
                    if (outputPath.endsWith(".enc")) {
                        outputPath = outputPath.substring(0, outputPath.length() - 4);
                    } else {
                        outputPath = outputPath + ".dec";
                    }
                    
                    Files.write(Paths.get(outputPath), decryptedData);
                    statusLabel.setText("File decrypted successfully: " + outputPath);
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(decryptButton);

        // Add panels to content panel
        contentPanel.add(filePanel);
        contentPanel.add(passwordPanel);
        contentPanel.add(buttonPanel);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(0, 0, 0));
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(new Color(0, 255, 0));
        statusLabel.setFont(retroFont);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Add components to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Add title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0));
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("RETRO ENCRYPTION TOOL");
        titleLabel.setForeground(new Color(0, 255, 0));
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Add main panel to frame
        frame.getContentPane().add(mainPanel);
    }

    // Encryption method
    public static byte[] encrypt(byte[] plaintext, String password) throws Exception {
        // Generate random salt and IV
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

        // Derive key from password
        SecretKey aesKeyFromPassword = getAESKeyFromPassword(password.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] cipherText = cipher.doFinal(plaintext);

        // Combine salt, iv, and ciphertext
        ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
        byteBuffer.put(salt);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }

    // Decryption method
    public static byte[] decrypt(byte[] cipherMessage, String password) throws Exception {
        // Extract salt, iv, and ciphertext
        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        // Derive key from password
        SecretKey aesKeyFromPassword = getAESKeyFromPassword(password.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        return cipher.doFinal(cipherText);
    }

    // Helper methods
    private static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private static SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, AES_KEY_BIT);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}