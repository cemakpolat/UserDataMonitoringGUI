/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmgui;

import conn.JavaToJavaClient;
import conn.JavaToJavaServerTimeOut;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author cemakpolat
 */
public class NetworkInterface {

    public class BWObject{
        public String userId;
        public double upload,download,available;
        BWObject(){}
    }
    public static String BANDWIDTH = "1";
    public static String QOSLIST = "6";
    public static String AUTHUSERCOUNT = "3";
    public static String AUTHPROFILES = "4";
    public static String oneQOSREQUEST = "2";
    public static String WORSTCONGEST = "5";
    public static String UsedBWByEachUser="7";
    public static String SETUPLINK="8";
    public ArrayList<BWObject> bandwidth = new ArrayList<BWObject>();
    public ArrayList<QoSObject> qos = new ArrayList<QoSObject>();
    public ArrayList<Profile> profiles = new ArrayList<Profile>();
    public NetworkInterface.QoSObject qosO;// = new NetworkInterface.QoSObject();
    
    public String worstCongestedUser = "";
    public double bandwidthCon = 0;
    public int AuthenticatedUserCount = 0;
    public static int APSocketNumber = 33111;
    public static int APListenetrServerSN = 33112;
    
    public static JavaToJavaClient mainClient;
    public static JavaToJavaServerTimeOut accessPointMessageServer;
    public  String serverIP = "192.168.126.1";//Access Point Server Address
    public static Thread serverThread;
    public String consoleOutput="";
    public static String uplinkAmount="7500000";
    protected void sendRequestToAP(String str) {
        // open a socket connection to Measurement Point
        String ourResponse = str;
        
        System.out.println("send Results: " + str);

        try {
            mainClient = new JavaToJavaClient(APSocketNumber);// we need to //
            // determine //
            // Port number
            writeConsole("IPAddress of Server " + serverIP);
            long QoSvalue = mainClient.send(ourResponse, serverIP);
            if (QoSvalue < 0) {
                writeConsole("No connection ");
            } else if (QoSvalue == 1) {
                writeConsole("Socket connection timeout, continuing ");
            } else if (QoSvalue == 2) {
                writeConsole("Socket connection error, continuing ");
            } else {
                writeConsole("QoE successfully sended");
            }
        } catch (Exception e) {
            System.out.println("Problem occurred by sending test results");
        }
    }
    public void setServerIP(String ip){
        this.serverIP=ip;
    }
    public String currentBW;

    public ArrayList<String> getActiveUsers(){
        ArrayList<String> list=new ArrayList<String>();
        
        for(int i=0;i<profiles.size();i++){

            list.add(profiles.get(i).userId);
        }
     
        return list;
    }
    int timeToWaitForRequest=4000;

    
    public void sendAuthUserProfileReq(){
        this.writeConsole("Request For Autheticated User Profiles.");
        sendRequestToAP(AUTHPROFILES);
    }
    public void sendAutheUserCountReq(){
      this.writeConsole("Request For Autheticated User Count.");

       qosO = new NetworkInterface.QoSObject();
        sendRequestToAP(AUTHUSERCOUNT);
    }
    public void sendSingleQoSRequest(){
        this.writeConsole("Request For Unique QoS Measurement.");
        sendRequestToAP(oneQOSREQUEST);
    }
    public void sendBWRequest(){
        
        this.writeConsole("Request For Bandwidth Reuqest.");
        sendRequestToAP(BANDWIDTH);
    }
    public void sendTotalBWAmountRequest(String amount){
        this.writeConsole("Request For Setting Total Bandwidth.");
        String message=SETUPLINK+":"+amount;
        this.uplinkAmount=amount;
        sendRequestToAP(message);

    }
    public static boolean periodicWorstCongestedUserState=true;
    public void startWorstCongestedUser(){
//         Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (periodicWorstCongestedUserState) {
//                    try {
//                        Thread.sleep(2000);
//                      //  sendRequestToAP(WORSTCONGEST);
//                         Thread.sleep(20000);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        thread.start();
    }
    
    public static boolean periodicQosRequesterState=true;
    public void startQoSRequester(){
          Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (periodicQosRequesterState) {
                    try {
                       Thread.sleep(3000);
                             writeConsole("Request For Unique QoS ");
                        sendRequestToAP(QOSLIST);
                           Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
  
    public static boolean periodicBWRequesterState=true;
    public void startBWRequester(){
          Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (periodicBWRequesterState) {
                    try {
                    Thread.sleep(1000);
                      writeConsole("Request For instantanous Bandwidth for all users.");
                    sendRequestToAP(BANDWIDTH);
                    Thread.sleep(10000);    
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    
    public static boolean periodicBWRequesterByUserConsumedState=true;
    public void startBWConsumedByEachUserRequester(){
          Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (periodicBWRequesterByUserConsumedState) {
                    try {
                       Thread.sleep(1000);
                         writeConsole("Request For instantanous Bandwidth for unique user.");
                        sendRequestToAP(UsedBWByEachUser);
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    
    public static boolean periodicUserProfileRequest=true;
    public void startAuthUserProfileRequester(){
          Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (periodicUserProfileRequest) {
                      writeConsole("\n Request For Authenticated Profiles.");
                    try {
                         Thread.sleep(500);
                         sendRequestToAP(AUTHPROFILES);
                         Thread.sleep(10000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    
    public boolean startServerState=true;
    public boolean startServerConnectionClosed=false;
    public void startServer() {
          try {
        writeConsole("creating the Server and waiting for commands from Gateways");
        accessPointMessageServer = new JavaToJavaServerTimeOut(APListenetrServerSN);
        serverThread = new Thread(new Runnable() {
             
            public void run() {
                try {
                     writeConsole("Socket connection to AP is being opened...");
                while (startServerState) {

                    String message = accessPointMessageServer.getMessage();
                    writeConsole("Message Received: " + message);
                    // force to hand over due to unappropriate conf
                    String[] mess = message.split("===");
                    if (mess.length > 1) {
                        if (!mess[1].equalsIgnoreCase("")) {
                            if (mess[0].contains("Profiles")) {
                                fillProfile(mess[1]);

                            } else if (mess[0].contains("QoSList")) {
                                fillQoS(mess[1]);

                            } else if (mess[0].contains("Bandwidth")) {
                                getAvailableBandwidth(mess[1]);
                               
                            } else if (mess[0].contains("AuthUserCount")) {
                                AuthenticatedUserCount = Integer.parseInt(mess[1]);
                            } else if (mess[0].contains("oneQoSRequest")) {
                                fillOneQoSRequest(mess[1]);
                            } else if (mess[0].contains("WorstCongested")) {
                                worstCongestedUser = mess[1];
                            }else if (mess[0].contains("UsedBWByEachUser")) {
                                writeConsole("BW For Each user");
                                fillBWUsageByUsers(mess[1]);
                            }
                        }
                    }

                }
                writeConsole("Socket connection to AP is being closed...");
                accessPointMessageServer.serverSocket.close();
                startServerConnectionClosed=true;
                }
                catch(SocketException e){
                    writeConsole("Socket exception occured, in 2 seconds socket connection is going to be established.");
                     startServer() ;
                }
                catch(IOException e){
                    e.printStackTrace();
                     startServer() ;
                 }
            }
        });

            serverThread.start();
          } 
          catch (Exception e) {
            serverThread.run();
        }

    }
 
    public ArrayList<BWObject> bwlist=new ArrayList<BWObject>();
    private void fillBWUsageByUsers(String str){
        
        writeConsole("For each user:"+str);
         if (bwlist.size() > 50) {
              bwlist.remove(0);
         }
        
        String[] list = str.split(":");
        writeConsole("\nTotal User count is :"+list.length+"\n");
        
        for (int i = 0; i < list.length; i++) {
            String[] obj = list[i].split(",");
              BWObject bwObj = new BWObject();

            for (int j = 0; j < obj.length; j++) {
               String[] val = obj[j].split("=");
                 if (val[0].contains("USER")) {
                    bwObj.userId = val[1];
                } else if (val[0].contains("UP")) {
                    bwObj.upload = Double.parseDouble(val[1]);
                } else if (val[0].contains("DOWN")) {
                    bwObj.download = Double.parseDouble(val[1]);
                } 
            }
            bwlist.add(bwObj);
            //writeConsole("List Size: "+bwlist.size() );
        }
    }

    public void fillOneQoSRequest(String str) {
            String[] obj = str.split(",");
           qosO= new QoSObject();

            for (int j = 0; j < obj.length; j++) {

                String[] val = obj[j].split("=");
                if (val[0].contains("ADEL")) {
                    qosO.adelay = Double.parseDouble(val[1]);
                } else if (val[0].contains("ABIT")) {
                    qosO.abitrate = Double.parseDouble(val[1]);
                } else if (val[0].contains("AJIT")) {
                    qosO.ajitter = Double.parseDouble(val[1]);
                } else if (val[0].contains("APL")) {
                    qosO.apktLoss = Double.parseDouble(val[1]);
                }
            }
    }
    //Available Function is no
    private void getAvailableBandwidth(String str) {
        if (bandwidth.size() > 20) {
            bandwidth.remove(0);
        }
        double available=0;double download=0;
         double totalBW=Double.parseDouble(uplinkAmount);
        BWObject bwObj = new BWObject();
        String[] list = str.split(",");
        for (int i = 0; i < list.length; i++) {
            String[] val = list[i].split("=");
            
          if (val[0].contains("AVAILABLE")) {
                available = Double.parseDouble(val[1]);
                download=totalBW-available;
          }
        }
         bwObj.available=available;
         bwObj.download=download;
         bandwidth.add(bwObj);

    }
    
    private void fillQoS(String str) {
        
        qos.clear();
        String[] list = str.split(":");
        for (int i = 0; i < list.length; i++) {
            String[] obj = list[i].split(",");
            QoSObject qosObj = new QoSObject();

            for (int j = 0; j < obj.length; j++) {

                String[] val = obj[j].split("=");
                if (val[0].contains("ADEL")) {
                    qosObj.adelay = Double.parseDouble(val[1]);
                } else if (val[0].contains("ABIT")) {
                    qosObj.abitrate = Double.parseDouble(val[1]);
                } else if (val[0].contains("AJIT")) {
                    
                    qosObj.ajitter = Double.parseDouble(val[1]);
                } else if (val[0].contains("APL")) {
                    
                    qosObj.apktLoss = Double.parseDouble(val[1]);
                }
            }
            qos.add(qosObj);
        }
    }

    /**
     * Message Format:
     * TCPAverage=0,UDPAverage=0,lastDepartureTime=2013/05/13-23:12:29,dailyAuthenticationNumber=1,
     * visitFrequencyNumber=33,authenticationNumber=70,bandwidthAverage=2186.45,userId=0013ce43b37e,
     * sessionAverage=00:01:00,sessions=VF:0;BW:0|VF:0.030303;
     *
     * @param str
     */
    private void fillProfile(String str) {
        profiles.clear();
        //writeConsole(str);
        String[] root = str.split("==");
        //writeConsole(root[0]);
        //  writeConsole(""+root.length);

        for (int j = 0; j < root.length; j++) {
           //  writeConsole(root[j]);
            String[] list = root[j].split(",");
            Profile profile = new Profile();
            for (int i = 0; i < list.length; i++) {
           //     writeConsole(list[i]);
                String[] sub = list[i].split("=");
                if (sub[0].contains("TCPAverage")) {
                } else if (sub[0].contains("UDPAverage")) {
                } else if (sub[0].contains("lastDepartureTime")) {
                    profile.lastDepartureTime = sub[1];
                } else if (sub[0].contains("dailyAuthenticationNumber")) {
                    profile.dailyAuthenticationNumber = Integer.parseInt(sub[1]);
                } else if (sub[0].contains("visitFrequencyNumber")) {
                    profile.visitFrequencyNumber = Integer.parseInt(sub[1]);
                } else if (sub[0].contains("authenticationNumber")) {
                    profile.authenticationNumber = Integer.parseInt(sub[1]);
                } else if (sub[0].contains("bandwidthAverage")) {
                    profile.bandwidthAverage = Double.parseDouble(sub[1]);
                } else if (sub[0].contains("userId")) {
                   // profile.userId = sub[1];
                    String user=sub[1];
                   // System.out.println("USER ----->: "+user);
                    profile.userId=generateAnonymUserId(user);
                } else if (sub[0].contains("sessionAverage")) {
                    profile.sessionAverage = sub[1];
                } else if (sub[0].contains("sessions")) {
                 
                    String[] sesObj = sub[1].split("\\|");
                     
                    for (int k = 0; k < sesObj.length; k++) {
                        
                        String[] subSessioObj = sesObj[k].split(";");
                        NetworkInterface.SessionObject session = new NetworkInterface.SessionObject();
                        for (int m = 0; m < subSessioObj.length; m++) {
                            String[] subSubSessioObj = subSessioObj[m].split(":");
                            if (subSubSessioObj[0].contains("VF")) {
                               // writeConsole(subSubSessioObj[1]);
                                session.visitingFrequency = Double.parseDouble(subSubSessioObj[1]);
                            } else if (subSubSessioObj[0].contains("BW")) {
                               // writeConsole(subSubSessioObj[1]);
                                session.bandwidth = Double.parseDouble(subSubSessioObj[1]);
                            }
                        }
                        profile.session.add(session);
                    }
                }
            }
            profiles.add(profile);
        }

    }
    
    
    public String getCorrespondingUserID(String alias) {

        String user = "";
        if (alias != null || alias != "") {
            for (int i = 0; i < this.userIdMatch.size(); i++) {
               // writeConsole("userid found!: " +  this.userIdMatch.get(i).alias);
                if (this.userIdMatch.get(i).alias.equalsIgnoreCase(alias)) {
                    user = this.userIdMatch.get(i).userId;
                    //  writeConsole("userid found!: "+user);
                    break;
                }
            }
        }

        return user;
    }
    public String generateAnonymUserId(String userId){
        String generatedId="";
        generatedId=getAlreadyGeneratedUserId(userId);
        return generatedId;
    }
    public String getAlreadyGeneratedUserId(String userId){
        Random ran=new Random();

        String user="";
        boolean userIdFounded=false;
        for(int i=0;i<this.userIdMatch.size();i++){
             //  writeConsole("userid : " +userId+ "current id"+ this.userIdMatch.get(i).userId);
            if(this.userIdMatch.get(i).userId.contains(userId)){
              //    writeConsole("userid esixt "+this.userIdMatch.size());
                userIdFounded=true;
                user=this.userIdMatch.get(i).alias;
            }

        }
        if(!userIdFounded){
             //  writeConsole("userid not esixt");
         int generatedNumber=ran.nextInt(100);
             user="user_"+generatedNumber;
             this.userIdMatch.add(new UserIDMatch(userId,user));
        }
        return user;
    }
    public class Profile {

        long visitFrequencyNumber = 0;
        long dailyAuthenticationNumber = 0;
        String lastDepartureTime = "";
        long dailyVFProbability = 0;
        long authenticationNumber = 0;
        String userId = "";
        String sessionAverage = "00:00:00";
        double bandwidthAverage = 0;
        ArrayList<SessionObject> session = new ArrayList<SessionObject>();

        Profile() {
        }
        
    }
    //TODO: For user Id match
    ArrayList<UserIDMatch> userIdMatch = new ArrayList<UserIDMatch>();
    public class UserIDMatch{
        String userId;
        String alias;
        UserIDMatch(String userId,String assignedID){
            this.userId=userId;
            this.alias=assignedID;
        }
    }
    public class SessionObject {

        double visitingFrequency = 0;
        double bandwidth = 0;

        SessionObject() {}

        SessionObject(double vf, double b) {
            this.bandwidth = b;
            this.visitingFrequency = vf;
        }
    }

    public class QoSObject {

        double adelay;
        double abitrate;
        double ajitter;
        double apktLoss;

        QoSObject() { }

        QoSObject(double delay, double bitrate, double jitter, double pkLoss) {
            this.adelay = delay;
            this.abitrate = bitrate;
            this.ajitter = jitter;
            this.apktLoss = pkLoss;
        }
    }

    public void writeConsole(String line) {
        System.out.println(line);
        consoleOutput=line;
     
//        MainFrame.jTextArea1.append(line);
    }
    private static final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public  boolean validate( String ip) {
           
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	
   }

}

