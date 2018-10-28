import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.FlowLayout;

public class ResultBoxPanel extends JPanel {

	/**
	 * 검색 결과 리스트 패널
	 */
	
	int height = 120;
	int width = 400;
	int margin = 10;
	
	JButton btn1, btn2;
	JLabel lbl;
	JLabel txtArticle;
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.setColor(new Color(255, 255, 255, 100));
	    g.fillRect(0, 0, width, height);
	}
	
	public ResultBoxPanel(String title, String[] contents, String filename, int w) {
		setBackground(new Color(255, 255, 255, 100));
		setLayout(null);
		setOpaque(false);
		
		this.width = w - margin;
				
		btn1 = new JButton();
		btn1.setBorderPainted(false);
		btn1.setFocusPainted(false);
		btn1.setContentAreaFilled(false);
		btn1.setBounds(0, 0, width, height);
		btn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					Desktop.getDesktop().open(new File(filename));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		lbl = new JLabel(title, SwingConstants.CENTER);
		lbl.setForeground(Color.WHITE);
		Font f = new Font("맑은 고딕", Font.BOLD, 15);
		lbl.setFont(f);
		lbl.setBounds(0, 5, width, 28);

		add(lbl);
		add(btn1);
		
		String result = "";
		
		txtArticle = new JLabel(getNewText(contents), SwingConstants.LEADING);
		txtArticle.setVerticalAlignment(SwingConstants.TOP);
		txtArticle.setBounds(0, 35, width, 82);
		txtArticle.setForeground(Color.WHITE);
		txtArticle.setOpaque(false);
		add(txtArticle);

	}
	
	public String getNewText(String[] text) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		for(int i = 0; i < text.length; i++) {
			sb.append(text[i]);
			sb.append("...");
		}
		sb.append("</html>");
		String str = sb.toString();
		str = str.replace("<B>", "<b><span style=\"color: #FFBB00;\">").replace("</B>", "</span></b>");
		
		return str;
	}
}
