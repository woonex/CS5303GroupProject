package edu.baylor.gitawayHotel.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**The frame that displays all of the gui components
 * @author Nathan
 *
 */
public class MainFrame {
	private JFrame frame; 
	private JPanel activePanel = new JPanel();
	
	/**Main constructor
	 * 
	 */
	public MainFrame() {
		frame = new JFrame("Gitaway Hotel");
		
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
	}
	
	/**Adds the current panel to the gui and clears the last one and redraws the gui
	 * @param panel
	 */
	public void add(JPanel panel) {
		SwingUtilities.invokeLater(() -> {
			frame.remove(activePanel);
			frame.add(panel);
			activePanel = panel;
			frame.revalidate();
			frame.repaint();
		});
		
	}

	/**Gets the frame object
	 * @return the frame object
	 */
	public JFrame getFrame() {
		return frame;
	}
}
