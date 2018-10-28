import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class MainFrame extends JFrame{

	private JPanel contentPane;
	static JPanel panel;
	private JTextField txtSearch;
	
	static ArrayList<MemberSet> arrMember = new ArrayList<MemberSet>();
	static String[] name;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setSize(800, 520);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")
	public MainFrame() {
		try {
			for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
			}
			
		} catch (Exception exc) {
			System.err.println("Theme error");
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 520);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		panel = new JPanel();
		panel.setBounds(0, 0, 784, 481);
		panel.setLayout(null);
		contentPane.add(panel);
		
		txtSearch = new JTextField();
		txtSearch.setForeground(Color.WHITE);
		txtSearch.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		txtSearch.setCaretColor(Color.WHITE);
		txtSearch.setOpaque(false);
		txtSearch.setBorder(BorderFactory.createEmptyBorder());
		txtSearch.setBounds(216, 273, 318, 37);
		panel.add(txtSearch);
		txtSearch.setColumns(10);
		txtSearch.addActionListener(new AbstractAction() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	goSearch();
		    }
		});
		
		JButton btnSearch = new JButton();
		btnSearch.setBounds(539, 273, 39, 37);
		btnSearch.setOpaque(false);
		btnSearch.setBorderPainted(false);
		btnSearch.setFocusPainted(false);
		btnSearch.setContentAreaFilled(false);
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				goSearch();
			}
		});
		
		panel.add(btnSearch);
		
		JButton btnIndexer = new JButton("색인");
		btnIndexer.setBounds(675, 448, 97, 23);
		btnIndexer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IndexerPanel ip = new IndexerPanel(panel);
				getContentPane().add(ip);
				ip.setVisible(true);
				panel.setVisible(false);
			}
		});
		panel.add(btnIndexer);
		
		JLabel imgBackground = new JLabel();
		imgBackground.setIcon(new ImageIcon(MainFrame.class.getResource("/img/bg.jpg")));
		imgBackground.setBounds(0, 0, 784, 481);
		panel.add(imgBackground);
		
		arrMember = new Member().getMembers();
		name = new String[arrMember.size()];
		for(int i = 0; i < arrMember.size(); i++) {
			name[i] = arrMember.get(i).korean;
		}
	}
		
	public void goSearch() {
		String searchText = txtSearch.getText().toString();
		SearchPanel sp = new SearchPanel(searchText, panel);
		getContentPane().add(sp);
		sp.setVisible(true);
		panel.setVisible(false);
	}
}
