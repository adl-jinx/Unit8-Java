package guidemo;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;;

/**
 * A menu full of commands that affect the text shown
 * in a DrawPanel.
 */
public class TextMenu extends JMenu {
	
	private final DrawPanel panel;    // the panel whose text is controlled by this menu
	
	private JCheckBoxMenuItem bold;   // controls whether the text is bold or not.
	private JCheckBoxMenuItem italic; // controls whether the text is italic or not.
	
	/**
	 * Constructor creates all the menu commands and adds them to the menu.
	 * @param owner the panel whose text will be controlled by this menu.
	 */
	public TextMenu(DrawPanel owner) {
			super("Text");
			this.panel = owner;
			final JMenuItem change = new JMenuItem("Change Text...");
			change.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					String currentText = panel.getTextItem().getText();
					String newText = GetTextDialog.showDialog(panel,currentText);
					if (newText != null && newText.trim().length() > 0) {
						panel.getTextItem().setText(newText);
						panel.repaint();
					}
				}
			});
			final JMenuItem size = new JMenuItem("Set Size...");
			size.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int currentSize = panel.getTextItem().getFontSize();
					String s = JOptionPane.showInputDialog(panel, "What font size do you want to use?",currentSize);
					if (s != null && s.trim().length() > 0) {
						try {
							int newSize = Integer.parseInt(s.trim()); // can throw NumberFormatException
							panel.getTextItem().setFontSize(newSize); // can throw IllegalArgumentException
							panel.repaint();
						}
						catch (Exception e) {
							JOptionPane.showMessageDialog(panel, s + " is not a legal text size.\n"
									+"Please enter a positive integer.");
						}
					}
				}
			});
	
			final JMenuItem lineheight = new JMenuItem("Set Line Spacing...");
			lineheight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					Double currentHeight = 1.0;
					String s = JOptionPane.showInputDialog(panel, "Multiply the default line spacing by what amount?", currentHeight);
					if (s != null && s.trim().length() > 0) {
						try {
							double input = Double.parseDouble(s.trim());
			
							if (input >= 1.0 && input <= 5.0) {
								double newHeight = currentHeight * input;
								panel.getTextItem().setLineHeightMultiplier(newHeight);
								panel.repaint();
							} else {
								JOptionPane.showMessageDialog(panel, "Please enter a number between 1 and 5.");
							}
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(panel, "Please enter a valid number.");
						}
					}
				}
			});
			
		final JMenuItem color = new JMenuItem("Set Color...");
		color.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Color currentColor = panel.getTextItem().getColor();
				Color newColor = JColorChooser.showDialog(panel, "Select Text Color", currentColor);
				if (newColor != null) {
					panel.getTextItem().setColor(newColor);
					panel.repaint();
				}
			}
		});
		italic = new JCheckBoxMenuItem("Italic");
		italic.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setItalic(italic.isSelected());
				panel.repaint();
			}
		});
		bold = new JCheckBoxMenuItem("Bold");
		bold.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setBold(bold.isSelected());
				panel.repaint();
			}
		});
		add(change);
		addSeparator();
		add(size);
		add(color);
		add(italic);
		add(bold);
		add(lineheight);
		addSeparator();
		add(makeFontNameSubmenu());
		add(makeJustifyMenu());
		
	}

	/*
	 * Justify text menu 
	 */

	private JMenu makeJustifyMenu() {
		ActionListener setJustifyActionLeft = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		panel.getTextItem().setJustify(TextItem.LEFT);
		panel.repaint();
		}
		};
		ActionListener setJustifyActionCenter = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		panel.getTextItem().setJustify(TextItem.CENTER);
		panel.repaint();
		}
		};
		ActionListener setJustifyActionRight = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		panel.getTextItem().setJustify(TextItem.RIGHT);
		panel.repaint();
		}
		};
		JMenu menu = new JMenu("Justify");
		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem left = new JRadioButtonMenuItem("Left");
		left.addActionListener(setJustifyActionLeft);
		buttonGroup.add(left);
		menu.add(left);
		left.setSelected(true); // Set default justify
		JRadioButtonMenuItem center = new JRadioButtonMenuItem("Center");
		center.addActionListener(setJustifyActionCenter);
		buttonGroup.add(center);
		menu.add(center);
		JRadioButtonMenuItem right = new JRadioButtonMenuItem("Right");
		right.addActionListener(setJustifyActionRight);
		buttonGroup.add(right);
		menu.add(right);
		return menu;
	}

	
	/**
	 * Reset the state of the menu to reflect the default settings for text
	 * in a DrawPanel.  (Sets the italic and bold checkboxes to unselected.)
	 * This method is called by the main program when the user selects the
	 * "New" command, to make sure that the menu state reflects the contents
	 * of the panel.
	 */
	public void setDefaults() {
		italic.setSelected(false);
		bold.setSelected(false);
	}
	
	/**
	 * Create a menu containing a list of all available fonts.
	 * (It turns out this can be very messy, at least on Linux, but
	 * it does show the use what is available and lets the user try
	 * everything!)
	 */
	private JMenu makeFontNameSubmenu() {
		ActionListener setFontAction = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				panel.getTextItem().setFontName(evt.getActionCommand());
				panel.repaint();
			}
		};
		JMenu menu = new JMenu("Font Name");
		String[] basic = { "Serif", "SansSerif", "Monospace" };
		for (String f : basic) {
			JMenuItem m = new JMenuItem(f+ " Default");
			m.setActionCommand(f);
			m.addActionListener(setFontAction);
			m.setFont(new Font(f,Font.PLAIN,12));
			menu.add(m);
		}
		menu.addSeparator();
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		if (fonts.length <= 20) {
			for (String f : fonts) {
				JMenuItem m = new JMenuItem(f);
				m.addActionListener(setFontAction);
				m.setFont(new Font(f,Font.PLAIN,12));
				menu.add(m);
			}
		}
		else { //Too many items for one menu; divide them into several sub-sub-menus.
			char ch1 = 'A';
			char ch2 = 'A';
			JMenu m = new JMenu();
			int i = 0;
			while (i < fonts.length) {
				while (i < fonts.length && (Character.toUpperCase(fonts[i].charAt(0)) <= ch2 || ch2 == 'Z')) {
					JMenuItem item = new JMenuItem(fonts[i]);
					item.addActionListener(setFontAction);
					item.setFont(new Font(fonts[i],Font.PLAIN,12));
					m.add(item);
					i++;
				}
				if (i == fonts.length || (m.getMenuComponentCount() >= 12 && i < fonts.length-4)) {
					if (ch1 == ch2)
						m.setText("" + ch1);
					else
						m.setText(ch1 + " to " + ch2);
					menu.add(m);
					if (i < fonts.length)
						m = new JMenu();
					ch2++;
					ch1 = ch2;
				}
				else 
					ch2++;
			}
		}
		return menu;
	}
	

}
