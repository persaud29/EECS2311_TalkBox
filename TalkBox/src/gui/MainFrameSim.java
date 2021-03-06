package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import controller.Controller;
import utils.Stereo;
import java.awt.Color;

public class MainFrameSim extends JFrame {
	
	private Stereo audioPlayer;
	private int idx;
	private Map<Integer, String> map = new HashMap<>();
	private JPanel buttonPanel;
	private boolean swap1Pressed = false;
	private boolean swap2Pressed = false;
	private boolean soundPlayed = false;
	private Controller controller;
	private JLabel label = new JLabel();
	private MainFrame mf;
	
	//The following fields are to change the look of the simulator
	private Color innerBorderTitle_color = Color.WHITE;
	private Color outerBorderTitle_color = Color.WHITE;
	private Color button_fg_color = Color.WHITE;
	private Color button_bg_color = Color.DARK_GRAY;
	private Color bg_color_1 = Color.DARK_GRAY;
	private Color bg_color_2 = Color.DARK_GRAY;
	private Color text_color = Color.WHITE;
	private Color menuBar_color = Color.GRAY;
	//

	public MainFrameSim(Controller controller, MainFrame mf) {
				
		super("TalkBox Simulator");
		getContentPane().setBackground(bg_color_1);
		setVisible(false);
		getContentPane().setLayout(new BorderLayout());
		setJMenuBar(createMenuBar());
		this.controller = controller;
		this.mf = mf;
		
		
		audioPlayer = new Stereo();
		
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int action = JOptionPane.showConfirmDialog(MainFrameSim.this, "Are you sure?", "Exit", JOptionPane.YES_OPTION);
				if (action == JOptionPane.YES_OPTION) {
					MainFrameSim.this.mf.setVisible(true);
					MainFrameSim.this.mf.getToolBarS().turnOnStart();
					controller.log("Window closed");
					dispose();
					System.gc();
				}

			}

		});
	}

	
	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public void showIt() {
		setSize(440,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void setButtons(int idx) {
		if (this.buttonPanel != null) {
			this.remove(buttonPanel);
			this.idx = idx;
		}
		if (idx < 1) { 
			label.setText("Audio Set " + (idx + 1));
			label.setForeground(text_color);
		} else {
			label.setText(controller.getAudioSetname(idx));
		}
		label.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(label, BorderLayout.NORTH);
		String[][] audioFileSet = controller.getFileNames();
		String[] audioSet = audioFileSet[idx];
		List<List<String>> iconDataList = controller.getAudioIconData();
		List<String> iconData= iconDataList.get(idx);
		
		JPanel panel = new JPanel();
		this.buttonPanel = panel;
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		Border innerBorder = BorderFactory.createTitledBorder("Play Audio");
		((TitledBorder) innerBorder).setTitleColor(Color.WHITE);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		//((TitledBorder) outerBorder).setTitleColor(outerBorderTitle_color);
		panel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		panel.setBackground(bg_color_2);
		
		for(int i = 0; i < audioSet.length; i++) {
			JButton add;
			String file = audioSet[i].substring(0, audioSet[i].length() - 4);
			if(iconData.get(i).equals("")) {
			add = new JButton(file);
			add.setForeground(button_fg_color);
			add.setBackground(button_bg_color);
			} else {
     		ImageIcon playIcon = new ImageIcon(iconData.get(i));
			playIcon.setImage(Controller.scaleIcon(playIcon, 8));
			add = new JButton(playIcon);
			add.setText(file);
			add.setBackground(button_bg_color);
			add.setForeground(button_fg_color);
			add.setVerticalTextPosition( SwingConstants.BOTTOM );
			add.setVerticalAlignment( SwingConstants.TOP );
			add.setHorizontalTextPosition( SwingConstants.CENTER );
            }
			add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JButton eventButton = (JButton) e.getSource();
					play(eventButton);
					
				}
				
			});
			panel.add(add);
			
		}
	
		
		getContentPane().add(panel, BorderLayout.CENTER);
	}
	
	
	
	public void setSwapButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.setBackground(bg_color_1);
		
		Border innerBorder = BorderFactory.createTitledBorder("Swap Audio Set");
		((TitledBorder) innerBorder).setTitleColor(Color.WHITE);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		
		panel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		String[] baseSets = {"Audio Set 1"};
		Object[] temp1 = controller.getAudiosetNameslist().toArray();
		Object[] temp2 = Arrays.copyOfRange(temp1, 1, temp1.length);
		String[] nameListHalf = Arrays.copyOf(temp2, temp2.length, String[].class);
		String[] nameList = Stream.of(baseSets, nameListHalf).flatMap(Stream::of).toArray(String[]::new);
		JComboBox setList = new JComboBox(nameList);
		for (int i = 0; i < nameList.length; i++) {
			System.out.println(nameList[i]);
		}

		setList.setSelectedIndex(idx);
		setList.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent event) {
		        JComboBox setList = (JComboBox) event.getSource();
		        Object selected = setList.getSelectedItem();
                int idx = setList.getSelectedIndex();
                //idx--;
                setButtons(idx);
                MainFrameSim.this.revalidate();
				MainFrameSim.this.repaint();
		    }
		});
		panel.add(setList);
	}
	
	
//	public void setSwapButtons() {
//		int numberOfAudioSets = controller.getNumberOfAudioSets();
//		JPanel panel = new JPanel();
//		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
//		Border innerBorder = BorderFactory.createTitledBorder("Swap Audio Set");
//		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
//		panel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
//		this.add(panel, BorderLayout.SOUTH);
//			
//		JButton swap1 = new JButton("1");
//		swap1.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				JButton button = (JButton) e.getSource();
//				int idx = Integer.parseInt(button.getText());
//				idx--;
//				setButtons(idx);
//				swap1Pressed = true;
//				swap2Pressed = false;
//				MainFrameSim.this.revalidate();
//				MainFrameSim.this.repaint();
//			}
//			
//		});
//		
//		JButton swap2 = new JButton("Swap");
//		swap2.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				JButton button = (JButton) e.getSource();
//				int swapIdx = 2;
//				swapIdx--;
//				if (swap1Pressed || !swap2Pressed) {
//					setButtons(swapIdx);
//					swap1Pressed = false;
//					swap2Pressed = true;
//				} else {
//					
//					if (MainFrameSim.this.idx == (numberOfAudioSets - 1)) {
//						MainFrameSim.this.idx = -1;
//					}
//					setButtons(++MainFrameSim.this.idx);
//				
//				}
//				MainFrameSim.this.revalidate();
//				MainFrameSim.this.repaint();
//			}
//			
//		});
//		
//		panel.add(swap1);
//		panel.add(swap2);
//	}
	
	private void play(JButton button) {
		String path = controller.getPath().toString();
		String completePath = path + "\\" + button.getText(); //**
		audioPlayer.playMusic(button.getText() + ".wav"); 
		controller.log("(" + button.getText() + ".wav)" + " played");
		soundPlayed = true;
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Menu");
		JMenuItem exitItem = new JMenuItem("Exit");
		
		//colors for menu bar
		menuBar.setBackground(menuBar_color);
		fileMenu.setForeground(text_color);
	
		
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int action = JOptionPane.showConfirmDialog(MainFrameSim.this, "Are you sure?", "Exit", JOptionPane.YES_OPTION);
				if (action == JOptionPane.YES_OPTION) {
					MainFrameSim.this.mf.setVisible(true);
					MainFrameSim.this.mf.getToolBarS().turnOnStart();

					System.exit(0);
				}
			}
		});
		
		return menuBar;
	}
	
	public boolean getSoundPlayed() {
		return soundPlayed;
	}
	
}
