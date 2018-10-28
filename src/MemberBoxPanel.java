import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MemberBoxPanel extends JPanel {

	/**
	 * 검색된 국회의원 정보를 보여줄 패널
	 */
	
	int height = 140;
	int width = 400;
	int margin = 10;
	
	JButton btn1, btn2;
	JLabel lbl;
	JLabel txtArticle;
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.setColor(new Color(255, 255, 255, 0));
	    g.fillRect(0, 0, width, height);
	}
	
	public MemberBoxPanel(String name, String info, String num, int w) {
		setBackground(new Color(255, 255, 255, 0));
		setLayout(null);
		setOpaque(false);
		
		this.width = w - 10 - margin;
				
		JLabel lblNewLabel = new JLabel("");
		
		ImageIcon imageIcon = new ImageIcon(StringStorage.MEMBER_IMAGE + num + ".jpg"); // load the image to a imageIcon
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(120, 150,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(newimg);  // transform it back
		
		System.err.println(StringStorage.MEMBER_IMAGE + num + ".jpg");
		lblNewLabel.setIcon(imageIcon);
		lblNewLabel.setBounds(12, 20, 120, 153);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(name);
		lblNewLabel_1.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(144, 20, 188, 32);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel(info);
		lblNewLabel_2.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_2.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setBounds(144, 62, 242, 111);
		add(lblNewLabel_2);
		
		JButton btn1 = new JButton();
		btn1.setBorderPainted(false);
		btn1.setFocusPainted(false);
		btn1.setContentAreaFilled(false);
		btn1.setBounds(0, 0, width, height);
		btn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					Desktop.getDesktop().open(new File(StringStorage.MEMBER_PAGE + num + ".html"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		add(btn1);
	}

}
