package forbidder;
/*ПОДКЛЮЧЕНИЕ БИБЛИОТЕК: */
import java.awt.EventQueue;
import forbidder.AboutDialog; //подключение файла окна "About"

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

	public static String strHostsPath = System.getenv("WINDIR") + "\\system32\\drivers\\etc\\hosts"; //расположение файла hosts
	private JFrame frmForbidder;     //главная форма
	private JTextField textEnterUrl; //поле для записи сайта

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) { //главная функция приложения
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow(); //создание объекта окна приложения
					window.frmForbidder.setVisible(true); //сделать окно видимым
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/* КОНСТРУКТОР КЛАССА MainWindow*/
	public MainWindow() throws IOException { 
		initialize();  //инициализация главного окна
	}

	 /*---------------------------------------------------------------------------------------
	 * Принцип работы алгоритма:
	 * 
	 * 1) Скопировать текст hosts в hosts_reserve с помощью функции loadRulesFromHosts(listModel);
	 * 2) Записать правила в JList<String> из hosts_reserve loadRulesFromHosts(listModel);
	 * 3) При клике "применить правила", стереть старые правила из hosts_reserve
	 * 3.1) записать новые правила в cleaned_hosts из hosts_reserve
	 * 3.2) записать cleaned_hosts в hosts
	 * 3.3) Снова записать обновленные данные в JList<String>
	 * 
	 * P.S. Реализция функций внизу текста
	 *----------------------------------------------------------------------------------------*/
	private void initialize() throws IOException {				//реализация функции initialize()
		//создание модели списка (сюда загружаются строки с сайтами):
		final DefaultListModel<String> listModel = new DefaultListModel<String>(); 
		//создание строкового списка (визуально отображает строки из DefaultListModel<String> listModel):
		final JList<String> listUrls = new JList<String>();	
		//Создание кнопки "Добавить"
		//текст вида "\u0417\" - это символы типа UNICODE, для корректного изображения кириллицы
		final JButton btnAddUrl = new JButton("\u0414\u043E\u0431\u0430\u0432\u0438\u0442\u044C");
		//Создание чекбокса (галочка) "Сброс DNS-кэша"
		//текст вида "\u0417\" - это символы типа UNICODE, для корректного изображения кириллицы
		final JCheckBox cbxDns = new JCheckBox("\u0421\u0431\u0440\u043E\u0441 \u043A\u044D\u0448\u0430 DNS");
		
		//создание объекта файла для удаления промежуточного файла после закрытия приложения
		File f1 = new File("cleaned_hosts");
		f1.deleteOnExit(); //удалить "cleaned_hosts" при закрытии
		//создание объекта файла для удаления промежуточного файла после закрытия приложения
		File f2 = new File("hosts_reserve");
		f2.deleteOnExit(); //удалить "hosts_reserve" при закрытии
		
		/*загрузка данных из hosts*/
		restoreHosts();				    //вызов функции restoreHosts()
		loadRulesFromHosts(listModel);  //вызов функции loadRulesFromHosts()
		clearHostsFromRules(listModel); //вызов функции clearHostsFromRules()
		
		//Автосгенерированный код:
		btnAddUrl.setBackground(new Color(204, 51, 51));  //установить цвет фона
		frmForbidder = new JFrame();					  //создать объект фрейма
		frmForbidder.setTitle("Forbidder");				  //задать имя приложения
		frmForbidder.setBounds(100, 100, 450, 295);
		frmForbidder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //закрытие фрейма при клике на крестик
		
		JLabel lblMainLabel = new JLabel("Forbidder");				//создание объекта текстовой надписи
		lblMainLabel.setHorizontalAlignment(SwingConstants.CENTER); //центрирование надписи
		lblMainLabel.setFont(new Font("Tekton Pro", Font.BOLD, 27));//выбор шрифта для надписи
		
		textEnterUrl = new JTextField();	//создание объекта текстового ввода (куда вводятся сайты)					
		/*Обработчик событий 1 textEnterUrl: */
		textEnterUrl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {  //если нажата клавиша энтер - 
					btnAddUrl.doClick(250);					  //сымитировать клик мыши по кнопке btnAddUrl
				}
			}
		});
		/*Обработчик событий 2 textEnterUrl: */
		textEnterUrl.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) { //если кликнули на textEnterUrl - 
				textEnterUrl.setText("");			   //очистить текст textEnterUrl
			}
			@Override
			public void focusLost(FocusEvent e) {
				if(textEnterUrl.getText().isEmpty()) {     //если убрали указатель с textEnterUrl -
					textEnterUrl.setText("Введите сайт..");//вернуть текст-приглашение
				}
			}
		});
		/*Обработчик событий 3 textEnterUrl: */
		textEnterUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { //если кликнули на textEnterUrl - 
				textEnterUrl.setText("");		     //очистить текст textEnterUrl
			}
		});
		textEnterUrl.setBackground(Color.LIGHT_GRAY); //установка цвета фона textEnterUrl
		//Установить подсказку для textEnterUrl (всплывает, если подержать указатель над строкой ввода)
		//текст вида "\u0417\" - это символы типа UNICODE, для корректного изображения кириллицы
		textEnterUrl.setToolTipText("\u0417\u0430\u043F\u0440\u0435\u0442\u0438\u0442\u044C \u0441\u0430\u0439\u0442");
		textEnterUrl.setHorizontalAlignment(SwingConstants.CENTER);
		textEnterUrl.setText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u0430\u0439\u0442..");
		textEnterUrl.setColumns(10);
		
		/*Обработчик событий кнопки btnAddUrl ("Добавить"): */
		btnAddUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(textEnterUrl.getText().trim().isEmpty()) {  //если текстовое поле пустое -
					return;									   //ничего не делать
				}
				//добавить введённую строку с сайтом в DefaultListModel<String> listModel				
				listModel.addElement(textEnterUrl.getText().trim());  //trim() - убирает по бокам пробелы в строке
																	 // было " строка  " - стало "строка"
			}
		});
		
		//Создание кнопки "Исключить"
		JButton btnRemoveButton = new JButton("\u0418\u0441\u043A\u043B\u044E\u0447\u0438\u0442\u044C");
		btnRemoveButton.setBackground(new Color(51, 204, 102)); //цвет
		/*Обработчик событий кнопки btnRemoveButton, для исключения сайта,
		 *  выделенного в списке JList<String> listUrls:               */
		//обратотчик события для кнопки btnRemoveButton
		btnRemoveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {	//если по кнопке кликнули мышью -
				if(listUrls.getSelectedIndex() == -1) { //если в списке сайтов не выделен сайт -
					listModel.removeElement(textEnterUrl.getText()); //удалить сайт, который вписан в строке для сайтов
				} else {								//если всё-таки выделен сайт в списке -
					listModel.removeElement(listUrls.getSelectedValue().toString()); //удалить его из списка
				}
			}
		});
		
		//создание объекта полосы прокрутки (для списка JList<String> listUrls)
		JScrollPane scrollPane = new JScrollPane();
		
		//Создание кнопки "Применить правила"
		JButton btnOkButton = new JButton("\u041F\u0440\u0438\u043C\u0435\u043D\u0438\u0442\u044C \u043F\u0440\u0430\u0432\u0438\u043B\u043E");
		btnOkButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(listModel.getSize() == 0) {		//ЕСЛИ список пустой:
					writeToHosts("cleaned_hosts");  //запись cleaned_hosts в hosts
					/*обновление списка сайтов в JList<String> listUrls*/
					listModel.clear();  //очистить список
					restoreHosts();     //вызов функции restoreHosts() (см. ниже)
					clearHostsFromRules(listModel); //вызов функции clearHostsFromRules() (см. ниже)
					//Показ диалогового окна о успехе операции
					javax.swing.JOptionPane.showMessageDialog(frmForbidder, "Правило изменено!", "Готово", JOptionPane.INFORMATION_MESSAGE);
					if(cbxDns.isSelected()) {				//если стоит галочка "очистить кэш"
						executeShell("ipconfig /flushdns"); //очистить кэш (см. ниже)
					}
					return;	//вернуться из функции
				}
				//ЕСЛИ список НЕ пустой
				for(int i = 0; i < listModel.getSize(); ++i) { 			    //для каждой строки в списке сайтов -
					forbidUrl(listModel.getElementAt(i).toString().trim()); //вызвать функцию forbidUrl() (см. ниже)
				}
				writeToHosts("cleaned_hosts");  //запись cleaned_hosts в hosts
				/*обновление исходников*/
				listModel.clear();  //очистить список
				restoreHosts();     //вызов функции restoreHosts() (см. ниже)
				try {
					loadRulesFromHosts(listModel); //вызов функции loadRulesFromHosts() (см. ниже)
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//Если не удалось вызвать функцию loadRulesFromHosts() - показать диалоговое окно с ошибкой:
					javax.swing.JOptionPane.showMessageDialog(frmForbidder, e1.toString(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
				} //ok
				clearHostsFromRules(listModel); //очистить кэш (см. ниже)
				//Показ диалогового окна о успехе операции:
				javax.swing.JOptionPane.showMessageDialog(frmForbidder, "Правило изменено!", "Готово", JOptionPane.INFORMATION_MESSAGE);
				if(cbxDns.isSelected()) {				//если стоит галочка "очистить кэш"
					executeShell("ipconfig /flushdns"); //через cmd.exe очистить кэш (команда windows)
				}
			}
		});
		
		//Создать кнопку "?" (справка)
		JButton btnHelpButton = new JButton("?");
		btnHelpButton.setContentAreaFilled(false); //сделать кнопку прозрачной
		btnHelpButton.addMouseListener(new MouseAdapter() {
			/*Обработчик событий для кнопки "?"*/
			@Override
			public void mouseClicked(MouseEvent e) {  //если по кнопке кликнули
				AboutDialog dlg = new AboutDialog();  //создать объект окна справки AboutDialog
													  //(окно реализовано в файле AboutDialog.java)
				dlg.setVisible(true);				  //Сделать окно справки видимым
			}
		});
		
		//Группировка кнопок (автосгенерированный код)
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
		
		
		scrollPane.setViewportView(listUrls);	//установить полосу прокрутки в список с сайтами
		listUrls.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		    //можно выделить только 1 сайт за клик
		listUrls.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null)); //нарисовать ободок списку
		
				//СВЯЗАТЬ список listUrls с моделью listModel (см. выше)
				listUrls.setModel(listModel);
				
				//Заглавие списка (текст "Запретить переход на:")
				JLabel label = new JLabel("\u0417\u0430\u043F\u0440\u0435\u0442\u0438\u0442\u044C \u043F\u0435\u0440\u0435\u0445\u043E\u0434 \u043D\u0430:");
				label.setFont(UIManager.getFont("InternalFrame.titleFont")); //установить шрифт
				label.setHorizontalAlignment(SwingConstants.CENTER);         //центрировать текст
				scrollPane.setColumnHeaderView(label);
		frmForbidder.getContentPane().setLayout(groupLayout);  //установка группировки
		
	}
	
	/*НИЖЕ ИДЁТ РЕАЛИЗАЦИЯ ФУНКЦИЙ ПО ЗАПИСИ ПРАВИЛ В ФАЙЛ hosts в 
	 * 													   %windir%\system32\drivers\etc\hosts
	 * 1) forbidUrl(String url) - берёт строку url и записывает её в конец файла "cleaned_hosts"
	 * 2) restoreHosts()        - копирует текст из hosts в временный файл "hosts_reserve"
	 * 3) loadRulesFromHosts(DefaultListModel<String> listModel) - 
	 *    парсит hosts_reserve и загружает в список все правила оттуда 
	 *    (кроме 127.0.0.1 hosts и строк с комментариями)
	 * 4) writeToHosts(String strFromWhere) - записывает правила в файл hosts
	 *    (где строка strFromWhere - это путь к файлу hosts)
	 * 5) clearHostsFromRules(DefaultListModel<String> listUrls)
	 *    - открывается файл для чтения hosts_reserve
	 *    - создается файл для записи cleaned_hosts
	 *    - в cleaned_hosts записывается всё из hosts_reserve, кроме правил (т.е. делается заготовка)
	 * 6) executeShell(String strCommand)  - в windows выполняется действие 'strCommand' в командной строке   
	 *    
	 * */
	
	private void forbidUrl(String url)
	{
		try {
			/*запись в cleaned_hosts*/
			//Создание объекта для записи в файл
			FileWriter fw = new FileWriter("cleaned_hosts", true); //cleaned_hosts - файл для записи
																   //если true - значит, запись в конец файла
			//Создание объекта буфера записи
		    BufferedWriter bw = new BufferedWriter(fw);	
		    //создание объекта для записи текста
		    PrintWriter out = new PrintWriter(bw);
		    
		    /*тут идёт формирование строк типа "127.0.0.1     WEBSITE.com "*/
		    String strLoopback = "127.0.0.1     ";
		    String strUrl	   = strLoopback + url;			         //"127.0.0.1     WEBSITE.com"
		    String strHttp	   = strLoopback + "http://" + url;      //"127.0.0.1     http://WEBSITE.com"
		    String strWww      = strLoopback + "www." + url;         //"127.0.0.1     www.WEBSITE.com"
		    String strHttps    = strLoopback + "https://" + url;     //"127.0.0.1    https://WEBSITE.com"
		    String strHttpsWww = strLoopback + "https://www." + url; //"127.0.0.1     https://www.WEBSITE.com"
		    
		    //запись строк в файл cleaned_hosts:
		    out.println(strUrl);
		    out.println(strHttp);
		    out.println(strWww);
		    out.println(strHttps);
		    out.println(strHttpsWww);
		    out.close();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Если файл не удалось открыть - показать диалоговое окно с ошибкой:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
		}
			
		
	}
	
	/*делает промежуточную копию hosts с названием "hosts_reserve" */
	private void restoreHosts()
	{
		FileWriter fw; //объект записи в файл
		try {
			//инициализация объекта записи в файл 
			fw = new FileWriter("hosts_reserve");  //hosts_reserve - файл, куда будут записываться строка strLine
			//создание объекта буфера записи:
		    BufferedWriter bw = new BufferedWriter(fw);
		    //создание объекта для записи текста
		    PrintWriter out = new PrintWriter(bw);
		    
		    /*strHostsPath - строка с путём к hosts*/
			FileReader fileBackup = new FileReader(strHostsPath);   //strHostsPath объявлён вверху файла MainWindow.java
			//создания объекта буфера для чтения hosts
			BufferedReader reader = new BufferedReader(fileBackup); //читаю hosts
			String strLine;			//строка, в которую будет писаться строка текста из hosts
			
			while((strLine = reader.readLine()) != null) {         //до тех пор, пока не закончили строки в hosts -
				if(((strLine.length() > 0)) && (strLine != "\n")) { //если строка не пустая -
					out.println(strLine);							//то записать строку в файл hosts_reserve
				}
			}
				
			out.close();   //закрыть файлы
			reader.close();//закрыть файлы
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Если файл не удалось открыть - показать диалоговое окно с ошибкой:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
		}      
	}
	
	/* парсит hosts_reserve и загружает в список все правила оттуда 
	 * (кроме 127.0.0.1 hosts и комментариев)*/
	private void loadRulesFromHosts(DefaultListModel<String> listModel) throws IOException
	{
		FileReader readLocalhost; //объект чтения из файла 
		try {
			//инициализация объекта чтения из файла 
			readLocalhost = new FileReader("hosts_reserve"); //hosts_reserve - файл, куда будут записываться строка strLine
			//создание объекта буфера чтения:
			BufferedReader reader = new BufferedReader(readLocalhost);
			String strLine;
			String strLocalhost = "localhost";
			String strLoop = "127.0.0.1";
			
			while((strLine = reader.readLine()) != null) {
				if(strLine.contains("#")) {           //если строка содержит # (комментарий) -
					strLine = "";				      //то не заполнять строку
				}
				if(strLine.contains(strLocalhost)) {  //если строка содержит "localhost"
					strLine = "";					  //то не заполнять строку
				}
				if(strLine.contains("www") || strLine.contains("http")) { //если строка содержит "www" или "http"
					strLine = "";					         			  //то не заполнять строку
				}
				if(strLine.indexOf(strLoop) != -1) {	//если строка содержит "127.0.0.1"
					//скопировать в подстроку данные от начала "127.0.0.1" до коца строки 
					strLine = strLine.substring(strLine.indexOf("127.0.0.1") + strLoop.length(), strLine.length());
					strLine = strLine.trim();                    //убрать пробелы слева и справа от текста в строке
					System.out.println("string is: " + strLine); //вывод результата в тестовую консоль
					listModel.addElement(strLine);			     //добавить строку в список
				}
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString());
			//Если файл не удалось открыть - показать диалоговое окно с ошибкой:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
			//System.exit(1);
		}
		
		
	}
	
	private void writeToHosts(String strFromWhere)
	{
		try {
			//инициализация объекта записи в файл 
			FileWriter fw = new FileWriter(strHostsPath); //файл hosts
			//создание объекта буфера записи:
		    BufferedWriter bw = new BufferedWriter(fw);
		    //создание объекта записи текстовых данных:
		    PrintWriter out = new PrintWriter(bw);
		    
		    //создание объекта чтения из файла:
			FileReader fileBackup = new FileReader(strFromWhere);  //"cleaned_hosts"
			//создание объекта буфера чтения:
			BufferedReader reader = new BufferedReader(fileBackup);
			String strLine; //сюда будет формироваться строка и записываться потом в hosts
			
			while((strLine = reader.readLine()) != null) { //до тех пор, пока есть строки в cleaned_hosts
				out.println(strLine);					   //записать строку в hosts
			}
				
			out.close();    //закрыть файл
			reader.close(); //закрыть файл
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Если файл не удалось открыть - показать диалоговое окно с ошибкой:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);  //если файл не открылся - то это критическая ошибка - закрыть приложение
		}   
	}
	
	//все правила удаляются из hosts
	private void clearHostsFromRules(DefaultListModel<String> listUrls)
	{
		FileWriter fw; //создание объекта записи в файл cleaned_hosts
		try {
			//инициализация объекта записи в файл cleaned_hosts
			fw = new FileWriter("cleaned_hosts"); 
			//создание объекта буфера записи:
		    BufferedWriter bw = new BufferedWriter(fw);
		    //создание объекта записи текстовых данных:
		    PrintWriter out = new PrintWriter(bw);
		    
		    //создание объекта чтения из файла hosts_reserve:
			FileReader fileHosts = new FileReader("hosts_reserve");
			//создание объекта буфера чтения:
			BufferedReader reader = new BufferedReader(fileHosts);
			//строка-буфер, в которую будут заливаться правила
			String strLine;
			
			System.out.print("Getmodel size:" + listUrls.getSize()); //тестовый вывод в консоль размера списка
			
			while((strLine = reader.readLine()) != null) {    //до тех пор, пока есть строки в hosts_reserve
				for(int i = 0; i < listUrls.getSize(); ++i) { //для каждого элемента в списке:
					System.out.println("line is: " + listUrls.getElementAt(i).toString() ); //тестовый вывод в консоль
					//если в строке hosts содержится элемент списка - не заполнять строку
					if( strLine.contains( listUrls.getElementAt(i).toString() ) ) { 
						strLine = "";
					}
				}
				out.println(strLine); //записать строку в файл cleaned_hosts
			}
				
			out.close();   //закрыть файл
			reader.close();//закрыть файл
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Если файл не удалось открыть - показать диалоговое окно с ошибкой:
			javax.swing.JOptionPane.showMessageDialog(frmForbidder, e.toString(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			//если файл не открылся - то это критическая ошибка - закрыть приложение
		}  
	}
	
	private void executeShell(String strCommand)
	{
		try {
			Process proc = Runtime.getRuntime().exec(strCommand); //выполнение команды командной строки
			proc.waitFor();
			System.out.println(strCommand + " executed.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString()); //если не удалось выполнить - вывести причину в консоли
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.toString()); //если не удалось выполнить - вывести причину в консоли
		}
	}
}
