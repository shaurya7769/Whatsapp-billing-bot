import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BillingApplication {
    static class Item {
        String name;
        double price;

        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingApplication::createGUI);
    }

    private static void createGUI() {
        // Main Frame
        JFrame frame = new JFrame("Billing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Panel for item input
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();
        JLabel priceLabel = new JLabel("Item Price (₹):");
        JTextField priceField = new JTextField();
        JButton addButton = new JButton("Add Item");

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);
        inputPanel.add(addButton);

        // Panel for added items
        DefaultListModel<String> itemListModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(itemListModel);
        JScrollPane itemScrollPane = new JScrollPane(itemList);

        // Panel for total, phone input, and UPI QR code
        JPanel bottomPanel = new JPanel(new GridLayout(4, 2));
        JLabel totalLabel = new JLabel("Total: ₹0.0");
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField("+91");
        JButton sendButton = new JButton("Send via WhatsApp");
        JLabel upiLabel = new JLabel("Pay via UPI:");
        JLabel qrCodeLabel = new JLabel();

        // Load QR Code Image
        try {
            ImageIcon qrCodeIcon = new ImageIcon(new ImageIcon("Screenshot_2024_1203_232405.jpg")
                    .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            qrCodeLabel.setIcon(qrCodeIcon);
        } catch (Exception e) {
            qrCodeLabel.setText("QR Code not found!");
        }

        bottomPanel.add(totalLabel);
        bottomPanel.add(new JLabel()); // Placeholder
        bottomPanel.add(phoneLabel);
        bottomPanel.add(phoneField);
        bottomPanel.add(sendButton);
        bottomPanel.add(upiLabel);
        bottomPanel.add(qrCodeLabel);

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(itemScrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Variables to store items and total
        List<Item> items = new ArrayList<>();
        final double[] totalCost = {0.0};

        // Add item button action
        addButton.addActionListener(e -> {
            String itemName = nameField.getText().trim();
            String itemPriceText = priceField.getText().trim();

            if (itemName.isEmpty() || itemPriceText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in both fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double itemPrice = Double.parseDouble(itemPriceText);
                Item newItem = new Item(itemName, itemPrice);
                items.add(newItem);

                // Update item list and total cost
                itemListModel.addElement(itemName + " - ₹" + itemPrice);
                totalCost[0] += itemPrice;
                totalLabel.setText("Total: ₹" + totalCost[0]);

                // Clear input fields
                nameField.setText("");
                priceField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid price format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Send via WhatsApp button action
        sendButton.addActionListener(e -> {
            String phoneNumber = phoneField.getText().trim();

            if (phoneNumber.isEmpty() || !phoneNumber.matches("\\+91\\d{10}")) {
                JOptionPane.showMessageDialog(frame, "Enter a valid phone number in the format +91XXXXXXXXXX!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate the bill summary
            StringBuilder billMessage = new StringBuilder("Your Bill Summary:\n");
            for (Item item : items) {
                billMessage.append(item.name).append(": ₹").append(item.price).append("\n");
            }
            billMessage.append("Total: ₹").append(totalCost[0]);
            billMessage.append("\n\nPay via UPI: your-upi-id@bank");

            try {
                // Generate WhatsApp URL
                String encodedMessage = URLEncoder.encode(billMessage.toString(), "UTF-8");
                String whatsappUrl = "https://wa.me/" + phoneNumber + "?text=" + encodedMessage;

                // Open WhatsApp link in browser
                Desktop.getDesktop().browse(new java.net.URI(whatsappUrl));
                JOptionPane.showMessageDialog(frame, "WhatsApp message sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to send WhatsApp message!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Display the frame
        frame.setVisible(true);
    }
}