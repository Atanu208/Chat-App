import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;

class ServerExtraFeature extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    DataInputStream dis;
    DataOutputStream dos;
    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private JButton sendFileBtn = new JButton("Send File");
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    ServerExtraFeature() {
        try {
            server = new ServerSocket(8970);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            createGUI();
            handleEvent();
            startReading();
            // startWriting();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client Terminated");
                        messageInput.setEnabled(false);
                        sendFileBtn.setEnabled(false);
                        socket.close();
                        break;
                    } else if (msg.startsWith("file")) {
                        String[] fileData = msg.split(" ");
                        String fileName = fileData[1];
                        long fileSize = Long.parseLong(fileData[2]);
                        receiveFile(fileName, fileSize);
                    } else {
                        messageArea.append("Client : " + msg + "\n");
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started");
            try {
                while (true) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }

        };
        new Thread(r2).start();
    }

    private void createGUI() {
        this.setTitle("Server Messenger");
        this.setSize(500, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        sendFileBtn.setFont(font);
        ImageIcon icon = new ImageIcon("chat.png");
        ImageIcon resized= new ImageIcon(icon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
        JLabel headingIcon = new JLabel(resized);
        heading.setIconTextGap(20);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);
        this.add(sendFileBtn, BorderLayout.WEST);
        this.add(headingIcon, BorderLayout.EAST);
        this.setVisible(true);
        }
        private void handleEvent() {
            messageInput.addActionListener(new ActionListener() {
        
                @Override
                public void actionPerformed(ActionEvent e) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me : " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                }
            });
        
            sendFileBtn.addActionListener(new ActionListener() {
        
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Choose a file");
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF and Images", "pdf", "jpg", "jpeg", "png");
                    fileChooser.addChoosableFileFilter(filter);
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String filePath = selectedFile.getAbsolutePath();
                        long fileSize = selectedFile.length();
                        sendFile(filePath, fileSize);
                    }
                }
            });
        }
        
        private void sendFile(String filePath, long fileSize) {
            try {
                dos.writeUTF("file " + filePath + " " + fileSize);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[4096];
                while (fis.read(buffer) > 0) {
                    dos.write(buffer);
                }
                fis.close();
                dos.flush();
                JOptionPane.showMessageDialog(this, "File sent successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "File sending failed");
                e.printStackTrace();
            }
        }
        
        private void receiveFile(String fileName, long fileSize) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save file");
                fileChooser.setSelectedFile(new File(fileName));
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String savePath = fileChooser.getSelectedFile().getAbsolutePath();
                    FileOutputStream fos = new FileOutputStream(savePath);
                    byte[] buffer = new byte[4096];
                    int totalBytesRead = 0;
                    while (totalBytesRead < fileSize) {
                        int bytesReceived = dis.read(buffer);
                        fos.write(buffer, 0, bytesReceived);
                        totalBytesRead += bytesReceived;
                    }
                    fos.close();
                    JOptionPane.showMessageDialog(this, "File received successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "File receive canceled by user");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "File receiving failed");
                e.printStackTrace();
            }
        }
        
        public static void main(String[] args) {
            new ServerExtraFeature();
        }       
        }