import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.Font;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;
import java.awt.BorderLayout;


class Server extends JFrame
{
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    private JLabel heading=new JLabel("Server Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);

    Server()
    {
        try 
        {
            server=new ServerSocket(8970);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket=server.accept();
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvent();
            startReading();
            // startWriting();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    public void startReading() 
    {
            Runnable r1=()->
            {
                System.out.println("Reader started...");
                try
                {
                while(true) 
                {
                    String msg=br.readLine();
                    if(msg.equals("exit"))
                    {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client Terminated");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                     }
                    // System.out.println("Server : "+msg);
                    messageArea.append("Client : " + msg + "\n");
                }
            }
            catch(Exception e)
            {
                System.out.println("Connection closed");
            }

            };
            new Thread(r1).start();
    }

    
    public void startWriting() 
    {
            Runnable r2=()->
            {
                System.out.println("Writer started");
             try{
                while(true)
                {
                    
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in)); 
                    String content=br1.readLine();
                    out.println(content);  
                    out.flush(); 

                    if(content.equals("exit"))
                    {
                        socket.close();
                        break;
                    }   
                }
            }
            catch (Exception e) 
            {          // TODO: handle exception
                // e.printStackTrace();
                System.out.println("Connection closed");
            }
                
            };  
            new Thread(r2).start();  
    }

    private void createGUI()
    {
        this.setTitle("Server Messenger");
        this.setSize(500,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        ImageIcon icon = new ImageIcon("chat.png");
        ImageIcon resizedIcon = new ImageIcon(icon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
        setIconImage(resizedIcon.getImage());
        heading.setIcon(new ImageIcon("chat.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setEditable(false);
        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        DefaultCaret caret = (DefaultCaret) messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void handleEvent()
    {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) 
            {

            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) 
            {
                
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) 
            {
                if(e.getKeyCode()==10)
                {
                   String contentToSend=messageInput.getText();
                    messageArea.append("You : " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }   
            }
        });  
    }


    public static void main(String[] args) 
    {
         new Server();
    }
}