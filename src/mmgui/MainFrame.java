package mmgui;

import com.apple.eawt.Application;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import mmgui.NetworkInterface.SessionObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cemakpolat
 */
public class MainFrame extends javax.swing.JFrame {
//MainForm.java -----------------------------------------------------    
  
//public void setframeicon(JFrame window, String icon){ 
//    try{ 
//        InputStream imgStream = this.getClass().getResourceAsStream("dai.gif"); 
//        BufferedImage bi = ImageIO.read(imgStream); 
//        ImageIcon myImg = new ImageIcon(bi); 
//        this.setIconImage(myImg.getImage()); 
//        }catch(Exception e){ 
//            System.out.println(e); 
//        }    
//    }; 


    public NetworkInterface ni = new NetworkInterface();


   //

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {

        setResizable(false);
      
 //       setframeicon(this, null);
        //Application application = Application.getApplication();
        //Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/ap.png"));
        //application.setDockIconImage(image);
       
        initComponents();
   
        drawBandwidthPieChart();
        drawBandwidthChart();
        drawAverageDelayChart();
        drawAverageBitrateChart();
        drawAverageJitterChart();
        drawAveragePacketLossChart();
        drawBandwidthPerUserChart();
        
  
        
        jTextArea1.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(jTextArea1));

        // keeps reference of standard output stream
        //  private PrintStream standardOut;
          //standardOut = System.out;

        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);
    
    }

    public void fillTable(String userId) {
        //get related session list
        ArrayList<SessionObject> list = new ArrayList<SessionObject>();
        for (int i = 0; i < ni.profiles.size(); i++) {
            if (ni.profiles.get(i).userId.equalsIgnoreCase(userId)) {
                list = ni.profiles.get(i).session;
            }
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sessions");
        DefaultMutableTreeNode child;
        DefaultMutableTreeNode grandChild1, grandChild2;
        for (int childIndex = 1; childIndex < list.size(); childIndex++) {
            child = new DefaultMutableTreeNode(" " + childIndex);
            root.add(child);
            grandChild1 = new DefaultMutableTreeNode("Visiting Frequency Prob  : " + list.get(childIndex).visitingFrequency);
            grandChild2 = new DefaultMutableTreeNode("Bandwidth Consumption : " + list.get(childIndex).bandwidth +" bytes/s");

            child.add(grandChild1);
            child.add(grandChild2);
        }

        JTree tree2 = new JTree(root);
        tree2.expandRow(2); // Expand children to illustrate leaf icons
        DefaultTreeCellRenderer renderer2 = new DefaultTreeCellRenderer();
        renderer2.setOpenIcon(null);
        renderer2.setClosedIcon(null);
        renderer2.setLeafIcon(null);
        tree2.setCellRenderer(renderer2);
        this.jScrollPaneSessions.getViewport().add(tree2);
        jScrollPaneSessions.validate();

    }
    public boolean drawBWState=true;

    public void drawBandwidthPieChart() {
        Thread a = new Thread(new Runnable() {
            double totalBW = Double.parseDouble(NetworkInterface.uplinkAmount);

            @Override
            public void run() {
                while (drawBWState) {
                    totalBW = Double.parseDouble(NetworkInterface.uplinkAmount);
                    //System.out.println("TOTAL BW:"+totalBW);
                    JPanel11.removeAll();
                    DefaultPieDataset data = new DefaultPieDataset();

                    NetworkInterface.BWObject obj = ni.new BWObject();
                    int a = 0;
                    if (ni.bandwidth.size() > 0) {
                        obj = ni.bandwidth.get(ni.bandwidth.size() - 1);
                        a = (int) totalBW/100 - (int) obj.available/100;
                    }

                    data.setValue("Consumed Bandwidth", new Integer(a));
                    data.setValue("Available Bandwidth", new Integer((int) obj.available/100));


                    JFreeChart chart = ChartFactory.createPieChart("Bandwidth Pie Chart", data, true, true, true);
                    //PiePlot p=(PiePlot)chart.getPlot();
                    ChartPanel CP = new ChartPanel(chart);
                    JPanel11.setPreferredSize(new Dimension(200, 200));
                    JPanel11.setLayout(new java.awt.BorderLayout());

                    JPanel11.add(CP, BorderLayout.CENTER);
                    // jPanel1.updateUI();
                    JPanel11.validate();
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        a.start();
    }

    public void drawBandwidthChart() {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (drawBWState) {

                    jPanel12.removeAll();

                    XYSeries team1_xy_data = new XYSeries("Bandwidth");
                    for (int i = 0; i < ni.bandwidth.size(); i++) {
                        team1_xy_data.add(i, ni.bandwidth.get(i).download);

                    }


                    /* Add all XYSeries to XYSeriesCollection */
                    //XYSeriesCollection implements XYDataset
                    XYSeriesCollection my_data_series = new XYSeriesCollection();
                    // add series using addSeries method
                    my_data_series.addSeries(team1_xy_data);

                    XYLineAndShapeRenderer dot = new XYLineAndShapeRenderer();
                    NumberAxis xax = new NumberAxis("Measurements");
                    NumberAxis yax = new NumberAxis("BW Consumption in Bytes/s");
                    XYPlot plot = new XYPlot(my_data_series, xax, yax, dot);

                    //Use createXYLineChart to create the chart
                    JFreeChart chart2 = new JFreeChart(plot);

                    //JFreeChart XYLineChart = ChartFactory.createXYLineChart("Team - Number of Wins", "Year", "Win Count", my_data_series, PlotOrientation.VERTICAL, true, true, false);
                    jPanel12.setLayout(new java.awt.BorderLayout());
                    ChartPanel CP = new ChartPanel(chart2);
                    jPanel12.add(CP, BorderLayout.CENTER);
                    // jPanel1.updateUI();
                    jPanel12.validate();



                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        a.start();
    }
    public static String selectedUserIdInJList="";
    public boolean drawBWPerUserState=true;
    public ArrayList list=new ArrayList();
    public void drawBandwidthPerUserChart() {
        //selectedUserIdInJList=userId;
        // writeConsole(" Drawing Selected Item 1: "+ selectedUserIdInJList); 
//        Thread a = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (drawBWPerUserState) {
//                     try {
//                        Thread.sleep(5000);
//                    } catch (Exception e) {
//                    }
                    jPanel14.removeAll();
                   // writeConsole(" Drawing Selected Item : "+selectedUserIdInJList);  
                    
                    XYSeries team1_xy_data = new XYSeries("Download");
                    XYSeries team2_xy_data = new XYSeries("Upload");
                 
                    if(list.size()>0){
                        
                        for (int i = 0; i < ni.bwlist.size(); i++) {
                            if(ni.bwlist.get(i).userId.equalsIgnoreCase(selectedUserIdInJList)){                               
                                team1_xy_data.add(i, ni.bwlist.get(i).download);
                                team2_xy_data.add(i, ni.bwlist.get(i).upload);
                            }
                        }
                    }else{
                        drawBWPerUserState=false;
                    }
                    
                    /* Add all XYSeries to XYSeriesCollection */
                    //XYSeriesCollection implements XYDataset
                    XYSeriesCollection my_data_series = new XYSeriesCollection();
                    // add series using addSeries method
                    my_data_series.addSeries(team1_xy_data);
                    my_data_series.addSeries(team2_xy_data);

                    XYLineAndShapeRenderer dot = new XYLineAndShapeRenderer();
                    NumberAxis xax = new NumberAxis("Measurements");
                    NumberAxis yax = new NumberAxis("BW Consumption in Bytes/s ");
                    XYPlot plot = new XYPlot(my_data_series, xax, yax, dot);

                    //Use createXYLineChart to create the chart
                    JFreeChart chart2 = new JFreeChart(plot);

                    //JFreeChart XYLineChart = ChartFactory.createXYLineChart("Team - Number of Wins", "Year", "Win Count", my_data_series, PlotOrientation.VERTICAL, true, true, false);
                    jPanel14.setLayout(new java.awt.BorderLayout());
                    ChartPanel CP = new ChartPanel(chart2);
                    jPanel14.add(CP, BorderLayout.CENTER);
                    // jPanel11.updateUI();
                    jPanel14.validate();

//                  
//                }
//            }
//           });
//           a.start();
        
    }
    public boolean drawQoSState=true;

    public void drawAverageDelayChart() {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (drawQoSState) {
                    jPanel31.removeAll();

                    XYSeries team1_xy_data = new XYSeries("Delay");
                    for (int i = 0; i < ni.qos.size(); i++) {

                        //  team1_xy_data.add(1, new Random().nextInt(10));
                        team1_xy_data.add(i, ni.qos.get(i).adelay);

                    }

                    /* Add all XYSeries to XYSeriesCollection */
                    //XYSeriesCollection implements XYDataset
                    XYSeriesCollection my_data_series = new XYSeriesCollection();
                    // add series using addSeries method
                    my_data_series.addSeries(team1_xy_data);

                    XYLineAndShapeRenderer dot = new XYLineAndShapeRenderer();
                    NumberAxis xax = new NumberAxis("Measurements");
                    NumberAxis yax = new NumberAxis("Delay in Seconds");
                    XYPlot plot = new XYPlot(my_data_series, xax, yax, dot);

                    //Use createXYLineChart to create the chart
                    JFreeChart chart2 = new JFreeChart(plot);

                    //JFreeChart XYLineChart = ChartFactory.createXYLineChart("Team - Number of Wins", "Year", "Win Count", my_data_series, PlotOrientation.VERTICAL, true, true, false);
                    jPanel31.setLayout(new java.awt.BorderLayout());
                    ChartPanel CP = new ChartPanel(chart2);
                    jPanel31.add(CP, BorderLayout.CENTER);
                    // jPanel1.updateUI();
                    jPanel31.validate();



                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }

                }
            }
        });
        a.start();
    }

    public void drawAverageBitrateChart() {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (drawQoSState) {
                    jPanel32.removeAll();

                    XYSeries team1_xy_data = new XYSeries("Bitrate");
                    for (int i = 0; i < ni.qos.size(); i++) {

                        // team1_xy_data.add(1, new Random().nextInt(10));
                        team1_xy_data.add(i, ni.qos.get(i).abitrate);

                    }

                    /* Add all XYSeries to XYSeriesCollection */
                    //XYSeriesCollection implements XYDataset
                    XYSeriesCollection my_data_series = new XYSeriesCollection();
                    // add series using addSeries method
                    my_data_series.addSeries(team1_xy_data);

                    XYLineAndShapeRenderer dot = new XYLineAndShapeRenderer();
                    NumberAxis xax = new NumberAxis("Measurements");
                    NumberAxis yax = new NumberAxis("Bitrate in Byte/sec");
                    XYPlot plot = new XYPlot(my_data_series, xax, yax, dot);

                    //Use createXYLineChart to create the chart
                    JFreeChart chart2 = new JFreeChart(plot);

                    //JFreeChart XYLineChart = ChartFactory.createXYLineChart("Team - Number of Wins", "Year", "Win Count", my_data_series, PlotOrientation.VERTICAL, true, true, false);
                    jPanel32.setLayout(new java.awt.BorderLayout());
                    ChartPanel CP = new ChartPanel(chart2);
                    jPanel32.add(CP, BorderLayout.CENTER);

                    jPanel32.validate();



                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }

                }
            }
        });
        a.start();
    }

    public void drawAverageJitterChart() {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (drawQoSState) {
                    jPanel33.removeAll();

                    XYSeries team1_xy_data = new XYSeries("Jitter");
                    for (int i = 0; i < ni.qos.size(); i++) {

                        // team1_xy_data.add(1, new Random().nextInt(10));
                        team1_xy_data.add(i, ni.qos.get(i).ajitter);

                    }


                    /* Add all XYSeries to XYSeriesCollection */
                    //XYSeriesCollection implements XYDataset
                    XYSeriesCollection my_data_series = new XYSeriesCollection();
                    // add series using addSeries method
                    my_data_series.addSeries(team1_xy_data);

                    XYLineAndShapeRenderer dot = new XYLineAndShapeRenderer();
                    NumberAxis xax = new NumberAxis("Measurements");
                    NumberAxis yax = new NumberAxis("Jitter in Seconds");
                    XYPlot plot = new XYPlot(my_data_series, xax, yax, dot);

                    //Use createXYLineChart to create the chart
                    JFreeChart chart2 = new JFreeChart(plot);

                    //JFreeChart XYLineChart = ChartFactory.createXYLineChart("Team - Number of Wins", "Year", "Win Count", my_data_series, PlotOrientation.VERTICAL, true, true, false);
                    jPanel33.setLayout(new java.awt.BorderLayout());
                    ChartPanel CP = new ChartPanel(chart2);
                    jPanel33.add(CP, BorderLayout.CENTER);
                    // jPanel1.updateUI();
                    jPanel33.validate();



                    try {
                        Thread.sleep(11000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        a.start();
    }
    public void drawAveragePacketLossChart() {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (drawQoSState) {

                    jPanel34.removeAll();
                    jPanel34.setPreferredSize(new Dimension(200, 200));
                    XYSeries team1_xy_data = new XYSeries("Packet Loss");
                    for (int i = 0; i < ni.qos.size(); i++) {

                        // team1_xy_data.add(1, new Random().nextInt(10));
                        team1_xy_data.add(i, ni.qos.get(i).apktLoss);

                    }

                    /* Add all XYSeries to XYSeriesCollection */
                    //XYSeriesCollection implements XYDataset
                    XYSeriesCollection my_data_series = new XYSeriesCollection();
                    // add series using addSeries method
                    my_data_series.addSeries(team1_xy_data);

                    XYLineAndShapeRenderer dot = new XYLineAndShapeRenderer();
                    NumberAxis xax = new NumberAxis("Measurements");
                    NumberAxis yax = new NumberAxis("Packet Loss");
                    XYPlot plot = new XYPlot(my_data_series, xax, yax, dot);

                    //Use createXYLineChart to create the chart
                    JFreeChart chart2 = new JFreeChart(plot);

                    //JFreeChart XYLineChart = ChartFactory.createXYLineChart("Team - Number of Wins", "Year", "Win Count", my_data_series, PlotOrientation.VERTICAL, true, true, false);
                    jPanel34.setLayout(new java.awt.BorderLayout());
                    ChartPanel CP = new ChartPanel(chart2);
                    jPanel34.add(CP, BorderLayout.CENTER);
                    // jPanel1.updateUI();
                    jPanel34.validate();

                    try {
                        Thread.sleep(11000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        a.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel19 = new javax.swing.JLabel();
        jBWUP = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jBWAvailable = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        JPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton6 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelUserId = new javax.swing.JLabel();
        jLabelDailyAuthNum = new javax.swing.JLabel();
        jLabelAuthNum = new javax.swing.JLabel();
        jLabelVisitFreq = new javax.swing.JLabel();
        jLabelSessAvr = new javax.swing.JLabel();
        jLabelBWAv = new javax.swing.JLabel();
        jLabelLastDep = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPaneSessions = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jLabelDelay = new javax.swing.JLabel();
        jLabelBitrate = new javax.swing.JLabel();
        jLabelJitter = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldDelay = new javax.swing.JTextField();
        jTextFieldBitrate = new javax.swing.JTextField();
        jTextFieldJitter = new javax.swing.JTextField();
        jTextFieldPLoss = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jChkBoxBWChange = new javax.swing.JCheckBox();
        jChkBoxQoS = new javax.swing.JCheckBox();
        jChkBoxUserProfileReq = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DAI-Labor MMGUI");
        setIconImage( Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/ap.png")));

        jPanel1.setLayout(new java.awt.GridLayout(2, 2));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Bandwidth"));
        jPanel10.setToolTipText("hello");
        jPanel10.setName("free panel"); // NOI18N
        jPanel10.setPreferredSize(new java.awt.Dimension(373, 299));
        jPanel10.setLayout(null);

        jButton1.setText("Get Last Values");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGetBWValues(evt);
            }
        });
        jPanel10.add(jButton1);
        jButton1.setBounds(16, 30, 140, 29);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jPanel10.add(jScrollPane3);
        jScrollPane3.setBounds(20, 180, 280, 120);

        jLabel19.setText("Available BW: ");
        jPanel10.add(jLabel19);
        jLabel19.setBounds(30, 130, 140, 16);

        jBWUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBWUPActionPerformed(evt);
            }
        });
        jPanel10.add(jBWUP);
        jBWUP.setBounds(120, 90, 130, 28);

        jLabel20.setText("Total BW: ");
        jPanel10.add(jLabel20);
        jLabel20.setBounds(30, 90, 70, 16);
        jPanel10.add(jBWAvailable);
        jBWAvailable.setBounds(120, 120, 170, 28);

        jLabel8.setText("B/s");
        jPanel10.add(jLabel8);
        jLabel8.setBounds(250, 100, 30, 20);

        jLabel9.setText("B/s");
        jPanel10.add(jLabel9);
        jLabel9.setBounds(290, 130, 50, 16);

        jButton7.setText("SetBW");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTotalBandwidthAmount(evt);
            }
        });
        jPanel10.add(jButton7);
        jButton7.setBounds(280, 90, 70, 30);

        jPanel1.add(jPanel10);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Bandwidth Change"));
        jPanel12.setLayout(null);
        jPanel1.add(jPanel12);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Active Users"));
        jPanel13.setPreferredSize(new java.awt.Dimension(373, 325));
        jPanel13.setLayout(null);

        jButton2.setText("Get Active Users");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton2);
        jButton2.setBounds(10, 40, 130, 29);

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane4.setViewportView(jTextArea3);

        jPanel13.add(jScrollPane4);
        jScrollPane4.setBounds(20, 80, 280, 210);

        jPanel1.add(jPanel13);

        JPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Bandwidth"));
        JPanel11.setLayout(null);
        jPanel1.add(JPanel11);

        jTabbedPane2.addTab("BW Requests", jPanel1);

        jPanel2.setLayout(new java.awt.GridLayout(2, 2));

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Users"));
        jPanel20.setPreferredSize(new java.awt.Dimension(259, 400));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fillUserInfo(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jButton6.setText("Get Connected Clients");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGetConnectedClients(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap())
            .add(jButton6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel20Layout.createSequentialGroup()
                .add(jButton6)
                .add(12, 12, 12)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel20);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Profile Instantenous BW Change"));

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 347, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 299, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel14);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Profile"));

        jLabel1.setText("User ID: ");

        jLabel2.setText("Daily Auth. Number:");

        jLabel3.setText("Auth. Number:");

        jLabel4.setText("VisitingFrequency:");

        jLabel5.setText("Session Average:");

        jLabel6.setText("Bandwidth Average: ");

        jLabel7.setText("Last Departure Time:");

        jLabelDailyAuthNum.setText("0");

        jLabelAuthNum.setText("0");

        jLabelVisitFreq.setText("0");

        jLabelSessAvr.setText("00:00:00");

        jLabelBWAv.setText("000000.0");

        jLabelLastDep.setText("0.0.0       00:00:00");

        jLabel16.setText("Active:");

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 347, Short.MAX_VALUE)
            .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel8Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel8Layout.createSequentialGroup()
                            .add(2, 2, 2)
                            .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabel16)
                                .add(jPanel8Layout.createSequentialGroup()
                                    .add(jLabel1)
                                    .add(94, 94, 94)
                                    .add(jLabelUserId))
                                .add(jPanel8Layout.createSequentialGroup()
                                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel2)
                                        .add(jLabel3)
                                        .add(jLabel4)
                                        .add(jLabel5))
                                    .add(18, 18, 18)
                                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabelSessAvr)
                                        .add(jLabelVisitFreq)
                                        .add(jLabelAuthNum)
                                        .add(jLabelDailyAuthNum)))
                                .add(jPanel8Layout.createSequentialGroup()
                                    .add(jLabel7)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                    .add(jLabelLastDep))))
                        .add(jPanel8Layout.createSequentialGroup()
                            .add(jLabel6)
                            .add(18, 18, 18)
                            .add(jLabelBWAv)))
                    .addContainerGap(80, Short.MAX_VALUE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 299, Short.MAX_VALUE)
            .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel8Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jLabel16)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(jLabelUserId))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel2)
                        .add(jLabelDailyAuthNum))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabelAuthNum)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel4)
                        .add(jLabelVisitFreq))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel5)
                        .add(jLabelSessAvr))
                    .add(14, 14, 14)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel6)
                        .add(jLabelBWAv))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabelLastDep)
                        .add(jLabel7))
                    .addContainerGap(79, Short.MAX_VALUE)))
        );

        jPanel2.add(jPanel8);

        jScrollPaneSessions.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Profile Sessions"));
        jPanel2.add(jScrollPaneSessions);

        jTabbedPane2.addTab("User Profiles", jPanel2);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder("QoS Measurements"));
        jPanel30.setLayout(null);

        jButton5.setText("Get QoS");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQoSValues(evt);
            }
        });
        jPanel30.add(jButton5);
        jButton5.setBounds(614, 20, 70, 60);

        jLabelDelay.setText("Average Delay:");
        jPanel30.add(jLabelDelay);
        jLabelDelay.setBounds(30, 30, 100, 16);

        jLabelBitrate.setText("Average Bitrate:");
        jPanel30.add(jLabelBitrate);
        jLabelBitrate.setBounds(30, 60, 100, 16);

        jLabelJitter.setText("Average Jitter:");
        jPanel30.add(jLabelJitter);
        jLabelJitter.setBounds(320, 30, 90, 16);

        jLabel18.setText("Avr. Packet Loss:");
        jPanel30.add(jLabel18);
        jLabel18.setBounds(320, 60, 110, 16);
        jPanel30.add(jTextFieldDelay);
        jTextFieldDelay.setBounds(140, 30, 100, 28);
        jPanel30.add(jTextFieldBitrate);
        jTextFieldBitrate.setBounds(140, 60, 100, 28);
        jPanel30.add(jTextFieldJitter);
        jTextFieldJitter.setBounds(430, 30, 110, 28);
        jPanel30.add(jTextFieldPLoss);
        jTextFieldPLoss.setBounds(430, 60, 110, 28);

        jLabel22.setText("ms");
        jPanel30.add(jLabel22);
        jLabel22.setBounds(240, 40, 53, 16);

        jLabel23.setText("bits/s");
        jPanel30.add(jLabel23);
        jLabel23.setBounds(240, 70, 38, 16);

        jLabel24.setText("ms");
        jPanel30.add(jLabel24);
        jLabel24.setBounds(540, 40, 20, 16);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 698;
        gridBagConstraints.ipady = 94;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel3.add(jPanel30, gridBagConstraints);

        jPanel31.setBorder(javax.swing.BorderFactory.createTitledBorder("Average Delay"));

        org.jdesktop.layout.GroupLayout jPanel31Layout = new org.jdesktop.layout.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 333, Short.MAX_VALUE)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 236, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 333;
        gridBagConstraints.ipady = 236;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel3.add(jPanel31, gridBagConstraints);

        jPanel32.setBorder(javax.swing.BorderFactory.createTitledBorder("Average Bitrate"));

        org.jdesktop.layout.GroupLayout jPanel32Layout = new org.jdesktop.layout.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 347, Short.MAX_VALUE)
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 236, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 347;
        gridBagConstraints.ipady = 236;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel3.add(jPanel32, gridBagConstraints);

        jPanel33.setBorder(javax.swing.BorderFactory.createTitledBorder("Average Jitter"));

        org.jdesktop.layout.GroupLayout jPanel33Layout = new org.jdesktop.layout.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 333, Short.MAX_VALUE)
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 204, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 333;
        gridBagConstraints.ipady = 204;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel3.add(jPanel33, gridBagConstraints);

        jPanel34.setBorder(javax.swing.BorderFactory.createTitledBorder("Average Packet Loss"));

        org.jdesktop.layout.GroupLayout jPanel34Layout = new org.jdesktop.layout.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 347, Short.MAX_VALUE)
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 204, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 347;
        gridBagConstraints.ipady = 204;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel3.add(jPanel34, gridBagConstraints);

        jTabbedPane2.addTab("QoS", jPanel3);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 687;
        gridBagConstraints.ipady = 607;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane2, gridBagConstraints);

        jTabbedPane2.addTab("Console Outputs", jPanel4);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jButton3.setText("Start");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartButton(evt);
            }
        });

        jTextField1.setText("192.168.3.1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel17.setText("AP IP Address:");

        jButton4.setText("Stop");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopButton(evt);
            }
        });

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Periodic Request Management"));

        jChkBoxBWChange.setSelected(true);
        jChkBoxBWChange.setText("Bandwidth Consumption Graphs");
        jChkBoxBWChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChkBoxBWChangeActionPerformed(evt);
            }
        });

        jChkBoxQoS.setSelected(true);
        jChkBoxQoS.setText("QoS Graphs");
        jChkBoxQoS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChkBoxQoSActionPerformed(evt);
            }
        });

        jChkBoxUserProfileReq.setSelected(true);
        jChkBoxUserProfileReq.setText("User Profile Request");
        jChkBoxUserProfileReq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChkBoxUserProfileReqActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(19, 19, 19)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jChkBoxUserProfileReq)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jChkBoxBWChange)
                        .add(63, 63, 63)
                        .add(jChkBoxQoS)))
                .addContainerGap(166, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jChkBoxBWChange)
                    .add(jChkBoxQoS))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jChkBoxUserProfileReq)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(45, 45, 45)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel17)
                        .add(29, 29, 29)
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 194, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jButton3)
                        .add(28, 28, 28)
                        .add(jButton4)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton3)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel17)
                    .add(jButton4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 85;
        gridBagConstraints.ipady = 192;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 16, 369, 49);
        jPanel5.add(jPanel6, gridBagConstraints);

        jTabbedPane2.addTab("Settings", jPanel5);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 740, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 700, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public int bandwidthCounter = 0;
    public boolean bwMessageReceived=true;
    private void jButtonGetBWValues(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetBWValues
        // TODO add your handling code here:
        if (this.jChkBoxBWChange.isSelected() == false) {
            ni.sendBWRequest();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                   
                    while (bwMessageReceived) {
                         try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                        if (ni.bandwidth.size() > 0) {
                            NetworkInterface.BWObject obj = ni.new BWObject();
                            if (ni.bandwidth.size() > 0) {
                                obj = ni.bandwidth.get(ni.bandwidth.size() - 1);
                                //jBWUP.setText(obj.upload + "");
                                jBWUP.setText(NetworkInterface.uplinkAmount);
                                jBWAvailable.setText(obj.available + "");

                                jTextArea2.append(bandwidthCounter + " : " + obj.available + "\n");
                                bandwidthCounter++;
                            }
                            bwMessageReceived = false;
                        } else {
                            writeConsole("Wait for profile Messages ...");
                        }

                    }
                }
            });
            thread.start();
        } else {
            NetworkInterface.BWObject obj = ni.new BWObject();
            if (ni.bandwidth.size() > 0) {
                obj = ni.bandwidth.get(ni.bandwidth.size() - 1);
                    jBWUP.setText(ni.uplinkAmount);
               // this.jBWUP.setText(obj.upload + "");
                this.jBWAvailable.setText(obj.available + "");

                jTextArea2.append(bandwidthCounter + " : " + obj.available + "\n");
                bandwidthCounter++;
            }
        }
    bwMessageReceived=true;
    }//GEN-LAST:event_jButtonGetBWValues

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jTextArea3.setText("");
        ArrayList<String> list = ni.getActiveUsers();
        if(list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                jTextArea3.append(i + " : " + list.get(i).toString() + "\n");

            }
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonStartButton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartButton
        // TODO add your handling code here:

        String ip = jTextField1.getText();

        //check whet
        if (ni.validate(ip)) {
             ni.setServerIP(ip);
             ni.startServerState=true;
             NetworkInterface.periodicBWRequesterByUserConsumedState=true;
             NetworkInterface.periodicBWRequesterState=true;
             NetworkInterface.periodicQosRequesterState=true;
             NetworkInterface.periodicUserProfileRequest=true;
             //NetworkInterface.periodicWorstCongestedUserState=true;
            if (this.jChkBoxBWChange.isSelected()) {
                ni.startBWRequester();
                ni.startBWConsumedByEachUserRequester();
            }
            if (this.jChkBoxQoS.isSelected()) {
                ni.startQoSRequester();
            }
            if (this.jChkBoxUserProfileReq.isSelected()) {
                ni.startAuthUserProfileRequester();
            }
             
           ni.startServer();      

        } else {
            jTextField1.setText("IP Address Format False");
        }
    }//GEN-LAST:event_jButtonStartButton
    public boolean qosMessageReceived=true;

    private void jButtonQoSValues(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQoSValues
        // TODO add your handling code here:  

        ni.sendSingleQoSRequest();
        Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                  
                    while (qosMessageReceived) {
                          try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                        
                       NetworkInterface.QoSObject obj = ni.qosO;
                       if (obj!=null) {
                             
                            jTextFieldDelay.setText(obj.adelay + "");
                            jTextFieldBitrate.setText(obj.abitrate + "");
                            jTextFieldJitter.setText(obj.ajitter + "");
                            jTextFieldPLoss.setText(obj.apktLoss + "");
                         
                            qosMessageReceived = false;
                            ni.qosO=null;
                        } else {
                            writeConsole("Wait for profile Messages ...");
                        }  
                    }
                }
            });
            thread.start();        
         qosMessageReceived=true;

    }//GEN-LAST:event_jButtonQoSValues
    public void writeConsole(String line) {
        System.out.println(line);
     
    }
    private void jChkBoxBWChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChkBoxBWChangeActionPerformed
        // TODO add your handling code here:
        if (this.jChkBoxBWChange.isSelected()) {
            writeConsole("checked");
            this.drawBWState=true;
            this.drawBandwidthChart();
            this.drawBandwidthPieChart();
            NetworkInterface.periodicBWRequesterState=true;
            ni.startBWRequester();
            //drawBandwidthPerUserChart();
            NetworkInterface.periodicBWRequesterByUserConsumedState=true;  
            ni.startBWConsumedByEachUserRequester();
           
        } else {
             this.drawBWState=false;
            NetworkInterface.periodicBWRequesterState=false;
            NetworkInterface.periodicBWRequesterByUserConsumedState=false;

            writeConsole("no check");

        }
    }//GEN-LAST:event_jChkBoxBWChangeActionPerformed

    private void jChkBoxQoSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChkBoxQoSActionPerformed
        // TODO add your handling code here:
        if (this.jChkBoxQoS.isSelected()) {
            writeConsole("checked");
            this.drawQoSState=true;
            this.drawAverageDelayChart();
            this.drawAverageBitrateChart();
            this.drawAverageJitterChart();
            this.drawAveragePacketLossChart();
            NetworkInterface.periodicQosRequesterState=true;
            ni.startQoSRequester();
            
        } else {
            writeConsole("no check");
            this.drawQoSState=false;
            NetworkInterface.periodicQosRequesterState=false;
           
            
        }
    }//GEN-LAST:event_jChkBoxQoSActionPerformed
    public static boolean messageReceived=true;
    public  DefaultListModel listModel = new DefaultListModel();
    private void jButtonGetConnectedClients(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetConnectedClients
        // TODO add your handling code here: You have to implement here by adding profile names in jlist 
     
        this.jList1.removeAll();
        listModel.clear();
        if(this.jChkBoxUserProfileReq.isSelected()==false){
           ni.sendAuthUserProfileReq();
           Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { 
                int counter=0;
               while (messageReceived) {
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                    }
                    if (ni.profiles.size() > 0) {
                        for (int i = 0; i < ni.profiles.size(); i++) {
                           
                            listModel.addElement(ni.profiles.get(i).userId);
                        }
                        messageReceived=false;
                    } else {
                        writeConsole("Wait for profile Messages ...");
                        counter++;
                        if(counter==10){
                             messageReceived=false;
                             writeConsole("Profile Message couldn't be received...");
                        }
                    }  
                     try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        thread.start();
        } else {
            if (ni.profiles.size() > 0) {
                for (int i = 0; i < ni.profiles.size(); i++) {
                    listModel.addElement(ni.profiles.get(i).userId);
                }

            } else {
                writeConsole("Wait for profile Messages ...");
            }
        }
    
        messageReceived=true;
        this.jList1.setModel(listModel);
        
    }//GEN-LAST:event_jButtonGetConnectedClients

    private void jChkBoxUserProfileReqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChkBoxUserProfileReqActionPerformed
        // TODO add your handling code here:
          if (this.jChkBoxUserProfileReq.isSelected()) {
            writeConsole("checked");
            NetworkInterface.periodicUserProfileRequest=true;            
            ni.startAuthUserProfileRequester();
        } else {
            NetworkInterface.periodicUserProfileRequest=false;
            writeConsole("no check");
        }
    }//GEN-LAST:event_jChkBoxUserProfileReqActionPerformed

    private void fillUserInfo(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fillUserInfo
        // TODO add your handling code here:
        

        String selectedItem = (String) jList1.getSelectedValue();
    //  System.out.print("Selected ITEM " + selectedItem);
         this.drawBWPerUserState=true;
        String str=ni.getCorrespondingUserID(selectedItem);
        list.add(str);
        selectedUserIdInJList=str;
        this.drawBandwidthPerUserChart();
        fillTable(selectedItem);
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < ni.profiles.size(); i++) {
             if(ni.profiles.get(i).userId.equalsIgnoreCase(selectedItem)){
            this.jLabelUserId.setText(ni.profiles.get(i).userId);
            this.jLabelAuthNum.setText(ni.profiles.get(i).authenticationNumber + "");
            this.jLabelBWAv.setText(ni.profiles.get(i).bandwidthAverage + "");
            this.jLabelDailyAuthNum.setText(ni.profiles.get(i).dailyAuthenticationNumber + "");
            this.jLabelLastDep.setText(ni.profiles.get(i).lastDepartureTime);
            this.jLabelSessAvr.setText(ni.profiles.get(i).sessionAverage);
            this.jLabelVisitFreq.setText(ni.profiles.get(i).visitFrequencyNumber + "");
         //   fillTable(ni.profiles.get(i).userId);
             }
        }
    
    }//GEN-LAST:event_fillUserInfo

    private void jButtonStopButton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopButton
        // TODO add your handling code here:
        ni.startServerState = false;

        NetworkInterface.periodicBWRequesterByUserConsumedState = false;
        NetworkInterface.periodicBWRequesterState = false;
        NetworkInterface.periodicQosRequesterState = false;
        NetworkInterface.periodicUserProfileRequest = false;
        jButton3.setEnabled(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!ni.startServerConnectionClosed) {
                    
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
                ni.startServerConnectionClosed=false;
                jButton3.setEnabled(true);
            }
        });
        thread.start();

        //  NetworkInterface.periodicWorstCongestedUserState=false;
    }//GEN-LAST:event_jButtonStopButton

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jBWUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBWUPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBWUPActionPerformed

    private void setTotalBandwidthAmount(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTotalBandwidthAmount
            // TODO add your handling code here:
        ni.sendTotalBWAmountRequest(this.jBWUP.getText());
        
        
    }//GEN-LAST:event_setTotalBandwidthAmount
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MainFrame().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanel11;
    private javax.swing.JTextField jBWAvailable;
    private javax.swing.JTextField jBWUP;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JCheckBox jChkBoxBWChange;
    private javax.swing.JCheckBox jChkBoxQoS;
    private javax.swing.JCheckBox jChkBoxUserProfileReq;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAuthNum;
    private javax.swing.JLabel jLabelBWAv;
    private javax.swing.JLabel jLabelBitrate;
    private javax.swing.JLabel jLabelDailyAuthNum;
    private javax.swing.JLabel jLabelDelay;
    private javax.swing.JLabel jLabelJitter;
    private javax.swing.JLabel jLabelLastDep;
    private javax.swing.JLabel jLabelSessAvr;
    private javax.swing.JLabel jLabelUserId;
    private javax.swing.JLabel jLabelVisitFreq;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPaneSessions;
    private javax.swing.JTabbedPane jTabbedPane2;
    public static javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldBitrate;
    private javax.swing.JTextField jTextFieldDelay;
    private javax.swing.JTextField jTextFieldJitter;
    private javax.swing.JTextField jTextFieldPLoss;
    // End of variables declaration//GEN-END:variables
}
