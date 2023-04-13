import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

public class Client extends JFrame 
{
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    private JLabel heading=new JLabel("Client Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);

    public Client()
    {
        try 
        {
            System.out.println("Sending request to server");
            socket=new Socket("192.168.144.40",8970);
            
            System.out.println("Connection done");
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvent();
            startReading();
            // startWriting();
        } 
        catch (Exception e) 
        {
            // TODO: handle exception
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
                while (true) 
                {
                    String msg=br.readLine();
                    if(msg.equals("exit"))
                    {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                     }
                    // System.out.println("Server : "+msg);
                    messageArea.append("Server : " + msg + "\n");
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

                try
                {
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
            catch (Exception e) {
 
                System.out.println( "Connection closed");
            }
            };  
            new Thread(r2).start();  
    }

    private void handleEvent()
    {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) 
            {

            }

            @Override
            public void keyPressed(KeyEvent e) 
            {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
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

    private void createGUI()
    {
        this.setTitle("Client Messenger");
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


    public static void main(String[] args) {
        new Client();
    }
}




