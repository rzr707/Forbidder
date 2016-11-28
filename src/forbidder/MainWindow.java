package forbidder;
/*����������� ���������: */
import java.awt.EventQueue;
import forbidder.AboutDialog; //����������� ����� ���� "About"

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.EtchedBorder;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.UIManager;
import javax.swing.JCheckBox;

public class MainWindow {

	public static String strHostsPath = System.getenv("WINDIR") + "\\system32\\drivers\\etc\\hosts"; //������������ ����� hosts
	private JFrame frmForbidder;     //������� �����
	private JTextField textEnterUrl; //���� ��� ������ �����

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) { //������� ������� ����������
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow(); //�������� ������� ���� ����������
					window.frmForbidder.setVisible(true); //������� ���� �������
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/* ����������� ������ MainWindow*/
	public MainWindow() throws IOException { 
		initialize();  //������������� �������� ����
	}

	 /*---------------------------------------------------------------------------------------
	 * ������� ������ ���������:
	 * 
	 * 1) ����������� ����� hosts � hosts_reserve � ������� ������� loadRulesFromHosts(listModel);
	 * 2) �������� ������� � JList<String> �� hosts_reserve loadRulesFromHosts(listModel);
	 * 3) ��� ����� "��������� �������", ������� ������ ������� �� hosts_reserve
	 * 3.1) �������� ����� ������� � cleaned_hosts �� hosts_reserve
	 * 3.2) �������� cleaned_hosts � hosts
	 * 3.3) ����� �������� ����������� ������ � JList<String>
	 * 
	 * P.S. ��������� ������� ����� ������
	 *----------------------------------------------------------------------------------------*/
	private void initialize() throws IOException {				//���������� ������� initialize()
		//�������� ������ ������ (���� ����������� ������ � �������):
		final DefaultListModel<String> listModel = new DefaultListModel<String>(); 
		//�������� ���������� ������ (��������� ���������� ������ �� DefaultListModel<String> listModel):
		final JList<String> listUrls = new JList<String>();	
		//�������� ������ "��������"
		//����� ���� "\u0417\" - ��� ������� ���� UNICODE, ��� ����������� ����������� ���������
		final JButton btnAddUrl = new JButton("\u0414\u043E\u0431\u0430\u0432\u0438\u0442\u044C");
		//�������� �������� (�������) "����� DNS-����"
		//����� ���� "\u0417\" - ��� ������� ���� UNICODE, ��� ����������� ����������� ���������
		final JCheckBox cbxDns = new JCheckBox("\u0421\u0431\u0440\u043E\u0441 \u043A\u044D\u0448\u0430 DNS");
		
		//�������� ������� ����� ��� �������� �������������� ����� ����� �������� ����������
		File f1 = new File("cleaned_hosts");
		f1.deleteOnExit(); //������� "cleaned_hosts" ��� ��������
		//�������� ������� ����� ��� �������� �������������� ����� ����� �������� ����������
		File f2 = new File("hosts_reserve");
		f2.deleteOnExit(); //������� "hosts_reserve" ��� ��������
		
		/*�������� ������ �� hosts*/
		restoreHosts();				    //����� ������� restoreHosts()
		loadRulesFromHosts(listModel);  //����� ������� loadRulesFromHosts()
		clearHostsFromRules(listModel); //����� ������� clearHostsFromRules()
		
		//������������������� ���:
		btnAddUrl.setBackground(new Color(204, 51, 51));  //���������� ���� ����
		frmForbidder = new JFrame();					  //������� ������ ������
		frmForbidder.setTitle("Forbidder");				  //������ ��� ����������
		frmForbidder.setBounds(100, 100, 450, 295);
		frmForbidder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //�������� ������ ��� ����� �� �������
		
		JLabel lblMainLabel = new JLabel("Forbidder");				//�������� ������� ��������� �������
		lblMainLabel.setHorizontalAlignment(SwingConstants.CENTER); //������������� �������
		lblMainLabel.setFont(new Font("Tekton Pro", Font.BOLD, 27));//����� ������ ��� �������
		
		textEnterUrl = new JTextField();	//�������� ������� ���������� ����� (���� �������� �����)					
		/*���������� ������� 1 textEnterUrl: */
		textEnterUrl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {  //���� ������ ������� ����� - 
					btnAddUrl.doClick(250);					  //������������ ���� ���� �� ������ btnAddUrl
				}
			}
		});
		/*���������� ������� 2 textEnterUrl: */
		textEnterUrl.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) { //���� �������� �� textEnterUrl - 
				textEnterUrl.setText("");			   //�������� ����� textEnterUrl
			}
			@Override
			public void focusLost(FocusEvent e) {
				if(textEnterUrl.getText().isEmpty()) {     //���� ������ ��������� � textEnterUrl -
					textEnterUrl.setText("������� ����..");//������� �����-�����������
				}
			}
		});
		/*���������� ������� 3 textEnterUrl: */
		textEnterUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { //���� �������� �� textEnterUrl - 
				textEnterUrl.setText("");		     //�������� ����� textEnterUrl
			}
		});
		textEnterUrl.setBackground(Color.LIGHT_GRAY); //��������� ����� ���� textEnterUrl
		//���������� ��������� ��� textEnterUrl (���������, ���� ��������� ��������� ��� ������� �����)
		//����� ���� "\u0417\" - ��� ������� ���� UNICODE, ��� ����������� ����������� ���������
		textEnterUrl.setToolTipText("\u0417\u0430\u043F\u0440\u0435\u0442\u0438\u0442\u044C \u0441\u0430\u0439\u0442");
		textEnterUrl.setHorizontalAlignment(SwingConstants.CENTER);
		textEnterUrl.setText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u0430\u0439\u0442..");
		textEnterUrl.setColumns(10);
		
		/*���������� ������� ������ btnAddUrl ("��������"): */
		btnAddUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(textEnterUrl.getText().trim().isEmpty()) {  //���� ��������� ���� ������ -
					return;									   //������ �� ������
				}
				//�������� �������� ������ � ������ � DefaultListModel<String> listModel				
				listModel.addElement(textEnterUrl.getText().trim());  //trim() - ������� �� ����� ������� � ������
																	 // ���� " ������  " - ����� "������"
			}
		});
		
		//�������� ������ "���������"
		JButton btnRemoveButton = new JButton("\u0418\u0441\u043A\u043B\u044E\u0447\u0438\u0442\u044C");
		btnRemoveButton.setBackground(new Color(51, 204, 102)); //����
		/*���������� ������� ������ btnRemoveButton, ��� ���������� �����,
		 *  ����������� � ������ JList<String> listUrls:               */
		//���������� ������� ��� ������ btnRemoveButton
		btnRemoveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {	//���� �� ������ �������� ����� -
				if(listUrls.getSelectedIndex() == -1) { //���� � ������ ������ �� ������� ���� -
					listModel.removeElement(textEnterUrl.getText()); //������� ����, ������� ������ � ������ ��� ������
				} else {								//���� ��-���� ������� ���� � ������ -
					listModel.removeElement(listUrls.getSelectedValue().toString()); //������� ��� �� ������
				}
			}
		});
		
		//�������� ������� ������ ��������� (��� ������ JList<String> listUrls)
		JScrollPane scrollPane = new JScrollPane();
		
		//�������� ������ "��������� �������"
		JButton btnOkButton = new JButton("\u041F\u0440\u0438\u043C\u0435\u043D\u0438\u0442\u044C \u043F\u0440\u0430\u0432\u0438\u043B\u043E");
		btnOkButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(listModel.getSize() == 0) {		//���� ������ ������:
					writeToHosts("cleaned_hosts");  //������ cleaned_hosts � hosts
					/*���������� ������ ������ � JList<String> listUrls*/
					listModel.clear();  //�������� ������
					restoreHosts();     //����� ������� restoreHosts() (��. ����)
					clearHostsFromRules(listModel); //����� ������� clearHostsFromRules() (��. ����)
					//����� ����������� ���� � ������ ��������
					javax.swing.JOptionPane.showMessageDialog(frmForbidder, "������� ��������!", "������", JOptionPane.INFORMATION_MESSAGE);
					if(cbxDns.isSelected()) {				//���� ����� ������� "�������� ���"
						executeShell("ipconfig /flushdns"); //�������� ��� (��. ����)
					}
					return;	//��������� �� �������
				}
				//���� ������ �� ������
				for(int i = 0; i < listModel.getSize(); ++i) { 			    //��� ������ ������ � ������ ������ -
					forbidUrl(listModel.getElementAt(i).toString().trim()); //������� ������� forbidUrl() (��. ����)
				}
				writeToHosts("cleaned_hosts");  //������ cleaned_hosts � hosts
				/*���������� ����������*/
				listModel.clear();  //�������� ������
				restoreHosts();     //����� ������� restoreHosts() (��. ����)
				try {
					loadRulesFromHosts(listModel); //����� ������� loadRulesFromHosts() (��. ����)
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//���� �� ������� ������� ������� loadRulesFromHosts() - �������� ���������� ���� � �������:
					javax.swing.JOptionPane.showMessageDialog(frmForbidder, e1.toString(), "������!", JOptionPane.ERROR_MESSAGE);
				} //ok
				clearHostsFromRules(listModel); //�������� ��� (��. ����)
				//����� ����������� ���� � ������ ��������:
				javax.swing.JOptionPane.showMessageDialog(frmForbidder, "������� ��������!", "������", JOptionPane.INFORMATION_MESSAGE);
				if(cbxDns.isSelected()) {				//���� ����� ������� "�������� ���"
					executeShell("ipconfig /flushdns"); //����� cmd.exe �������� ��� (������� windows)
				}
			}
		});
		
		//������� ������ "?" (�������)
		JButton btnHelpButton = new JButton("?");
		btnHelpButton.setContentAreaFilled(false); //������� ������ ����������
		btnHelpButton.addMouseListener(new MouseAdapter() {
			/*���������� ������� ��� ������ "?"*/
			@Override
			public void mouseClicked(MouseEvent e) {  //���� �� ������ ��������
				AboutDialog dlg = new AboutDialog();  //������� ������ ���� ������� AboutDialog
													  //(���� ����������� � ����� AboutDialog.java)
				dlg.setVisible(true);				  //������� ���� ������� �������
			}
		});
		
		//����������� ������ (������������������� ���)
		GroupLayout groupLayout = new GroupLayout(frmForbidder.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblMainLabel, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnAddUrl)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnRemoveButton, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
								.addComponent(textEnterUrl, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
								.addComponent(btnOkButton, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(cbxDns)
									.addGap(74)
									.addComponent(btnHelpButton)))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblMainLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(34)
							.addComponent(textEnterUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(7)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAddUrl)
								.addComponent(btnRemoveButton))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnOkButton)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
									.addComponent(btnHelpButton))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(18)
									.addComponent(cbxDns))))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		
		scrollPane.setViewportView(listUrls);	//���������� ������ ��������� � ������ � �������
		listUrls.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		    //����� �������� ������ 1 ���� �� ����
		listUrls.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null)); //���������� ������ ������
		
				//������� ������ listUrls � ������� listModel (��. ����)
				listUrls.setModel(listModel);
				
				//�������� ������ (����� "��������� ������� ��:")
				JLabel label = new JLabel("\u0417\u0430\u043F\u0440\u0435\u0442\u0438\u0442\u044C \u043F\u0435\u0440\u0435\u0445\u043E\u0434 \u043D\u0430:");
				label.setFont(UIManager.getFont("InternalFrame.titleFont")); //���������� �����
				label.setHorizontalAlignment(SwingConstants.CENTER);         //������������ �����
				scrollPane.setColumnHeaderView(label);
		frmForbidder.getContentPane().setLayout(groupLayout);  //��������� �����������
		
	}
	
	/*���� �Ĩ� ���������� ������� �� ������ ������ � ���� hosts � 
	 * 													   %windir%\system32\drivers\etc\hosts
	 * 1) forbidUrl(String url) - ���� ������ url � ���������� � � ����� ����� "cleaned_hosts"
	 * 2) restoreHosts()        - �������� ����� �� hosts � ��������� ���� "hosts_reserve"
	 * 3) loadRulesFromHosts(DefaultListModel<String> listModel) - 
	 *    ������ hosts_reserve � ��������� � ������ ��� ������� ������ 
	 *    (����� 127.0.0.1 hosts � ����� � �������������)
	 * 4) writeToHosts(String strFromWhere) - ���������� ������� � ���� hosts
	 *    (��� ������ strFromWhere - ��� ���� � ����� hosts)
	 * 5) clearHostsFromRules(DefaultListModel<String> listUrls)
	 *    - ����������� ���� ��� ������ hosts_reserve
	 *    - ��������� ���� ��� ������ cleaned_hosts
	 *    - � cleaned_hosts ������������ �� �� hosts_reserve, ����� ������ (�.�. �������� ���������)
	 * 6) executeShell(String strCommand)  - � windows ����������� �������� 'strCommand' � ��������� ������   
	 *    
	 * */
	
	private void forbidUrl(String url)
	{
		try {
			/*������ � cleaned_hosts*/
			//�������� ������� ��� ������ � ����
			FileWriter fw = new FileWriter("cleaned_hosts", true); //cleaned_hosts - ���� ��� ������
																   //���� true - ������, ������ � ����� �����
			//�������� ������� ������ ������
		    BufferedWriter bw = new BufferedWriter(fw);	
		    //�������� ������� ��� ������ ������
		    PrintWriter out = new PrintWriter(bw);
		    
		    /*��� ��� ������������ ����� ���� "127.0.0.1     WEBSITE.com "*/
		    String strLoopback = "127.0.0.1     ";
		    String strUrl	   = strLoopback + url;			         //"127.0.0.1     WEBSITE.com"
		    String strHttp	   = strLoopback + "http://" + url;      //"127.0.0.1     http://WEBSITE.com"
		    String strWww      = strLoopback + "www." + url;         //"127.0.0.1     www.WEBSITE.com"
		    String strHttps    = strLoopback + "https://" + url;     //"127.0.0.1    https://WEBSITE.com"
		    String strHttpsWww = strLoopback + "https://www." + url; //"127.0.0.1     https://www.WEBSITE.com"
		    
		    //������ ����� � ���� cleaned_hosts:
		    out.println(strUrl);
		    out.println(strHttp);
		    out.println(strWww);
		    out.println(strHttps);
		    out.println(strHttpsWww);
		    out.close();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//���� ���� �� ������� ������� - �������� ���������� ���� � �������:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "������!", JOptionPane.ERROR_MESSAGE);
		}
			
		
	}
	
	/*������ ������������� ����� hosts � ��������� "hosts_reserve" */
	private void restoreHosts()
	{
		FileWriter fw; //������ ������ � ����
		try {
			//������������� ������� ������ � ���� 
			fw = new FileWriter("hosts_reserve");  //hosts_reserve - ����, ���� ����� ������������ ������ strLine
			//�������� ������� ������ ������:
		    BufferedWriter bw = new BufferedWriter(fw);
		    //�������� ������� ��� ������ ������
		    PrintWriter out = new PrintWriter(bw);
		    
		    /*strHostsPath - ������ � ���� � hosts*/
			FileReader fileBackup = new FileReader(strHostsPath);   //strHostsPath ������� ������ ����� MainWindow.java
			//�������� ������� ������ ��� ������ hosts
			BufferedReader reader = new BufferedReader(fileBackup); //����� hosts
			String strLine;			//������, � ������� ����� �������� ������ ������ �� hosts
			
			while((strLine = reader.readLine()) != null) {         //�� ��� ���, ���� �� ��������� ������ � hosts -
				if(((strLine.length() > 0)) && (strLine != "\n")) { //���� ������ �� ������ -
					out.println(strLine);							//�� �������� ������ � ���� hosts_reserve
				}
			}
				
			out.close();   //������� �����
			reader.close();//������� �����
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//���� ���� �� ������� ������� - �������� ���������� ���� � �������:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "������!", JOptionPane.ERROR_MESSAGE);
		}      
	}
	
	/* ������ hosts_reserve � ��������� � ������ ��� ������� ������ 
	 * (����� 127.0.0.1 hosts � ������������)*/
	private void loadRulesFromHosts(DefaultListModel<String> listModel) throws IOException
	{
		FileReader readLocalhost; //������ ������ �� ����� 
		try {
			//������������� ������� ������ �� ����� 
			readLocalhost = new FileReader("hosts_reserve"); //hosts_reserve - ����, ���� ����� ������������ ������ strLine
			//�������� ������� ������ ������:
			BufferedReader reader = new BufferedReader(readLocalhost);
			String strLine;
			String strLocalhost = "localhost";
			String strLoop = "127.0.0.1";
			
			while((strLine = reader.readLine()) != null) {
				if(strLine.contains("#")) {           //���� ������ �������� # (�����������) -
					strLine = "";				      //�� �� ��������� ������
				}
				if(strLine.contains(strLocalhost)) {  //���� ������ �������� "localhost"
					strLine = "";					  //�� �� ��������� ������
				}
				if(strLine.contains("www") || strLine.contains("http")) { //���� ������ �������� "www" ��� "http"
					strLine = "";					         			  //�� �� ��������� ������
				}
				if(strLine.indexOf(strLoop) != -1) {	//���� ������ �������� "127.0.0.1"
					//����������� � ��������� ������ �� ������ "127.0.0.1" �� ���� ������ 
					strLine = strLine.substring(strLine.indexOf("127.0.0.1") + strLoop.length(), strLine.length());
					strLine = strLine.trim();                    //������ ������� ����� � ������ �� ������ � ������
					System.out.println("string is: " + strLine); //����� ���������� � �������� �������
					listModel.addElement(strLine);			     //�������� ������ � ������
				}
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString());
			//���� ���� �� ������� ������� - �������� ���������� ���� � �������:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "������!", JOptionPane.ERROR_MESSAGE);
			//System.exit(1);
		}
		
		
	}
	
	private void writeToHosts(String strFromWhere)
	{
		try {
			//������������� ������� ������ � ���� 
			FileWriter fw = new FileWriter(strHostsPath); //���� hosts
			//�������� ������� ������ ������:
		    BufferedWriter bw = new BufferedWriter(fw);
		    //�������� ������� ������ ��������� ������:
		    PrintWriter out = new PrintWriter(bw);
		    
		    //�������� ������� ������ �� �����:
			FileReader fileBackup = new FileReader(strFromWhere);  //"cleaned_hosts"
			//�������� ������� ������ ������:
			BufferedReader reader = new BufferedReader(fileBackup);
			String strLine; //���� ����� ������������� ������ � ������������ ����� � hosts
			
			while((strLine = reader.readLine()) != null) { //�� ��� ���, ���� ���� ������ � cleaned_hosts
				out.println(strLine);					   //�������� ������ � hosts
			}
				
			out.close();    //������� ����
			reader.close(); //������� ����
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//���� ���� �� ������� ������� - �������� ���������� ���� � �������:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "������!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);  //���� ���� �� �������� - �� ��� ����������� ������ - ������� ����������
		}   
	}
	
	//��� ������� ��������� �� hosts
	private void clearHostsFromRules(DefaultListModel<String> listUrls)
	{
		FileWriter fw; //�������� ������� ������ � ���� cleaned_hosts
		try {
			//������������� ������� ������ � ���� cleaned_hosts
			fw = new FileWriter("cleaned_hosts"); 
			//�������� ������� ������ ������:
		    BufferedWriter bw = new BufferedWriter(fw);
		    //�������� ������� ������ ��������� ������:
		    PrintWriter out = new PrintWriter(bw);
		    
		    //�������� ������� ������ �� ����� hosts_reserve:
			FileReader fileHosts = new FileReader("hosts_reserve");
			//�������� ������� ������ ������:
			BufferedReader reader = new BufferedReader(fileHosts);
			//������-�����, � ������� ����� ���������� �������
			String strLine;
			
			System.out.print("Getmodel size:" + listUrls.getSize()); //�������� ����� � ������� ������� ������
			
			while((strLine = reader.readLine()) != null) {    //�� ��� ���, ���� ���� ������ � hosts_reserve
				for(int i = 0; i < listUrls.getSize(); ++i) { //��� ������� �������� � ������:
					System.out.println("line is: " + listUrls.getElementAt(i).toString() ); //�������� ����� � �������
					//���� � ������ hosts ���������� ������� ������ - �� ��������� ������
					if( strLine.contains( listUrls.getElementAt(i).toString() ) ) { 
						strLine = "";
					}
				}
				out.println(strLine); //�������� ������ � ���� cleaned_hosts
			}
				
			out.close();   //������� ����
			reader.close();//������� ����
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//���� ���� �� ������� ������� - �������� ���������� ���� � �������:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "������!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			//���� ���� �� �������� - �� ��� ����������� ������ - ������� ����������
		}  
	}
	
	private void executeShell(String strCommand)
	{
		try {
			Process proc = Runtime.getRuntime().exec(strCommand); //���������� ������� ��������� ������
			proc.waitFor();
			System.out.println(strCommand + " executed.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString()); //���� �� ������� ��������� - ������� ������� � �������
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString()); //���� �� ������� ��������� - ������� ������� � �������
		}
	}
}
