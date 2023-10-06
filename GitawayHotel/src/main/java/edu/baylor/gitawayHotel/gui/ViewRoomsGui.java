package edu.baylor.gitawayHotel.gui;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

//import javax.swing.JButton;
//import javax.swing.JLabel;
import javax.swing.JPanel;

//import edu.baylor.gitawayHotel.user.UserType;

public class ViewRoomsGui implements IGui{
    private JPanel panel;
    private JButton viewRoomsButton;

    /**Constructor with default names
	 * 
	 */
	public ViewRoomsGui() {
		this("All Rooms:", "Go Back");
        System.out.println("ViewRoomsGui instantiated");
	}

    
	/**Constructor with configurable names and includes username
	 * @param topLabel
	 * @param bottomButton
	 */
	public ViewRoomsGui(String topLabel, String bottomButton) {
		layoutMainArea();
	}

    public void layoutMainArea () {
        panel = new JPanel();
		// panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // viewRoomsButton = new JButton("View Rooms");
		
		
		// panel.add(viewRoomsButton, BorderLayout.NORTH);
        panel.add(new JLabel("YEEHAW"));
    }

    /**Gets the button to move to the next screen
	 * @return
	 */
	public JButton getViewRoomsButton() {
		return this.viewRoomsButton;
	}

    /**Gets the panel containing all interactable components
	 * @return the panel
	 */
	@Override
	public JPanel getFullPanel() {
        System.out.println("Panel retrieved");
		return this.panel;
	}
}
