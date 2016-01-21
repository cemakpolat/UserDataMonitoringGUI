/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmgui;


/**
 *
 * @author cemakpolat
 */
public class MMGUI {
    
    public static String serverIPAddress="192.168.178.64";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
          /* Create and display the form */
        try{
        if(args.length>0){
            serverIPAddress=args[0];
        }else{
            writeConsole("Default IP Address is set!");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
                
            }
            
        });
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
       public static void writeConsole(String line) {
        System.out.println(line);
    }
}
