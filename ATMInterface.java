import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ATMInterface extends JFrame {
    private JTextField userIdField;
    private JPasswordField pinField;
    private JTextArea transactionHistoryArea;

    private JButton withdrawButton;
    private JButton depositButton;
    private JButton transferButton;

    private boolean loggedIn = false;
    private String currentUserId;

    private Map<String, Integer> balances = new HashMap<>();
    private Map<String, StringBuilder> transactionHistories = new HashMap<>();

    public ATMInterface() {
        setTitle("ATM Interface");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        loginPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        loginPanel.add(userIdField);
        loginPanel.add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        loginPanel.add(pinField);
        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);
        add(loginPanel, BorderLayout.NORTH);

        transactionHistoryArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(transactionHistoryArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        withdrawButton = new JButton("Withdraw");
        depositButton = new JButton("Deposit");
        transferButton = new JButton("Transfer");
        JButton quitButton = new JButton("Quit");
        buttonPanel.add(withdrawButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(quitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add sample user data
        balances.put("user1", 1000);
        balances.put("user2", 1500);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText();
                String pin = new String(pinField.getPassword());

                if (isValidCredentials(userId, pin)) {
                    loggedIn = true;
                    currentUserId = userId;
                    enableTransactionButtons();
                    showTransactionHistory();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid user ID or PIN. Please try again.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedIn) {
                    performWithdraw();
                } else {
                    showLoginError();
                }
            }
        });

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedIn) {
                    performDeposit();
                } else {
                    showLoginError();
                }
            }
        });

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedIn) {
                    performTransfer();
                } else {
                    showLoginError();
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private boolean isValidCredentials(String userId, String pin) {
        return balances.containsKey(userId) && pin.equals("1234"); // Replace with proper validation
    }

    private void enableTransactionButtons() {
        withdrawButton.setEnabled(true);
        depositButton.setEnabled(true);
        transferButton.setEnabled(true);
    }

    private void showTransactionHistory() {
        StringBuilder history = transactionHistories.getOrDefault(currentUserId, new StringBuilder());
        transactionHistoryArea.setText("Transaction history for user " + currentUserId + ":\n" + history);
    }

    private void performWithdraw() {
        String input = JOptionPane.showInputDialog(null, "Enter withdrawal amount:", "Withdraw",
                JOptionPane.PLAIN_MESSAGE);
        if (input != null) {
            try {
                int amount = Integer.parseInt(input);
                if (amount > 0 && balances.get(currentUserId) >= amount) {
                    balances.put(currentUserId, balances.get(currentUserId) - amount);
                    updateTransactionHistory("Withdrawal: Rs." + amount);
                    showTransactionHistory();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid amount or insufficient balance.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performDeposit() {
        String input = JOptionPane.showInputDialog(null, "Enter deposit amount:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (input != null) {
            try {
                int amount = Integer.parseInt(input);
                if (amount > 0) {
                    balances.put(currentUserId, balances.get(currentUserId) + amount);
                    updateTransactionHistory("Deposit: Rs." + amount);
                    showTransactionHistory();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performTransfer() {
        String recipientId = JOptionPane.showInputDialog(null, "Enter recipient's user ID:", "Transfer",
                JOptionPane.PLAIN_MESSAGE);
        if (recipientId != null && balances.containsKey(recipientId) && !recipientId.equals(currentUserId)) {
            String input = JOptionPane.showInputDialog(null, "Enter transfer amount:", "Transfer",
                    JOptionPane.PLAIN_MESSAGE);
            if (input != null) {
                try {
                    int amount = Integer.parseInt(input);
                    if (amount > 0 && balances.get(currentUserId) >= amount) {
                        balances.put(currentUserId, balances.get(currentUserId) - amount);
                        balances.put(recipientId, balances.get(recipientId) + amount);
                        updateTransactionHistory("Transfer: Rs." + amount + " to " + recipientId);
                        showTransactionHistory();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid amount or insufficient balance.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid recipient ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTransactionHistory(String transaction) {
        StringBuilder history = transactionHistories.getOrDefault(currentUserId, new StringBuilder());
        history.append(transaction).append("\n");
        transactionHistories.put(currentUserId, history);
    }

    private void showLoginError() {
        JOptionPane.showMessageDialog(null, "Please log in to perform transactions.", "Login Required",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ATMInterface atmInterface = new ATMInterface();
                atmInterface.setVisible(true);
                atmInterface.setLocationRelativeTo(null);
            }
        });
    }
}
