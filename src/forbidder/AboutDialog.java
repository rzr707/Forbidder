package forbidder;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.awt.Font;

public class AboutDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;   //объект кнопки "Понял"

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AboutDialog dialog = new AboutDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setResizable(false); //нельзя менять размер окна
		setTitle("Help");  //заголовок
		setModalityType(ModalityType.APPLICATION_MODAL); //окно модальное (пока не закроешь, нельзя переключиться на другие окна приложения
		setAlwaysOnTop(true);	//всегда вверху приложения
		setBounds(100, 100, 378, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			//инициализация кнопки "Понял"
			okButton = new JButton("\u041F\u043E\u043D\u044F\u043B");
			okButton.setFont(new Font("Sylfaen", Font.PLAIN, 11));
			/*Обработчик событий кнопки */
			okButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) { //если кликнута кнопка мыши - 
					AboutDialog.this.dispose();			 //закрыть данное окно
				}
			});
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
		}
		
		//тут хранится текст окна "справка":
		JLabel lblNewLabel = new JLabel("<html>\r\n<center>\u0418\u043D\u0441\u0442\u0440\u0443\u043A\u0446\u0438\u044F:</center><br>\r\n1) \u041F\u0440\u0438\u043C\u0435\u0440 \u0432\u0432\u043E\u0434\u0430 \u0432\u0435\u0431\u0441\u0430\u0439\u0442\u0430: <i>rutracker.org</i>,<br>\r\n<i>rozetka.com.ua</i> (\u0431\u0435\u0437 http \u0438 www)<br>\r\n2) \u0427\u0442\u043E\u0431\u044B \u0438\u0441\u043A\u043B\u044E\u0447\u0438\u0442\u044C \u0432\u0435\u0431\u0441\u0430\u0439\u0442, \u043D\u0443\u0436\u043D\u043E \u0432\u044B\u0431\u0440\u0430\u0442\u044C \u0435\u0433\u043E \u0438\u0437 \u0441\u043F\u0438\u0441\u043A\u0430 \u0441\u043B\u0435\u0432\u0430 \u0438 \u043D\u0430\u0436\u0430\u0442\u044C \u043A\u043D\u043E\u043F\u043A\u0443 \"\u0418\u0441\u043A\u043B\u044E\u0447\u0438\u0442\u044C\" (\u0438\u043B\u0438 \u0432\u0432\u0435\u0441\u0442\u0438 \u0441\u0430\u043C \u0441\u0430\u0439\u0442 \u0432 \u043F\u043E\u043B\u0435 \u0432\u0432\u043E\u0434\u0430 \u0438 \u043D\u0430\u0436\u0430\u0442\u044C \u043A\u043D\u043E\u043F\u043A\u0443)<br>\r\n3) \u0427\u0442\u043E\u0431\u044B \u043F\u0440\u0430\u0432\u0438\u043B\u0430 \u043F\u0440\u0438\u043C\u0435\u043D\u0438\u043B\u0438\u0441\u044C, \u043D\u0443\u0436\u043D\u043E \u043D\u0430\u0436\u0430\u0442\u044C \u043A\u043D\u043E\u043F\u043A\u0443 \"\u041F\u0440\u0438\u043C\u0435\u043D\u0438\u0442\u044C \u043F\u0440\u0430\u0432\u0438\u043B\u0430\"\r\n</html>\r\n");
		lblNewLabel.setFont(new Font("Sylfaen", Font.BOLD, 14)); //шрифт
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
							.addGap(114))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
					.addGap(29)
					.addComponent(okButton))
		);
		contentPanel.setLayout(gl_contentPanel);
	}

}
