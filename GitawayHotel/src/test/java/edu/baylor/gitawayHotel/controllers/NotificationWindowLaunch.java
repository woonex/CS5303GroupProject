package edu.baylor.gitawayHotel.controllers;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;

/**Gets if a window has been launched and programatically clicks okay on the notification and gets if the text of the window matches the expected text
 * @author Nathan
 *
 */
public class NotificationWindowLaunch implements AWTEventListener {
	
	private Set<JLabel> labels = new HashSet<JLabel>();
	private Set<JButton> buttons = new HashSet<JButton>();
	
	@Override
    public void eventDispatched(AWTEvent event) {
        if (event instanceof WindowEvent) {
            WindowEvent windowEvent = (WindowEvent) event;
            if (windowEvent.getID() == WindowEvent.WINDOW_OPENED) {
//                System.out.println("New window launched: " + windowEvent.getWindow().getName());
                
                parseWindowComponents(windowEvent.getWindow());

                pressOkayButton();
            }
        }
    }
	
	
	private void pressOkayButton() {
		for (JButton button : buttons) {
			if (button.getText().equals("OK")) {
				button.doClick();
			}
		}
	}


	private void parseWindowComponents(Component component) {
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            labels.add(label);
//            System.out.println("Found JLabel: " + label.getText());
        } else if (component instanceof JButton) {
        	JButton button = (JButton) component;
        	buttons.add(button);
        }
//        System.out.println(component.getClass() + " ".repeat(10) + component.getName());

        if (component instanceof Container) {
            Component[] components = ((Container) component).getComponents();
            for (Component child : components) {
                parseWindowComponents(child); // Recursive call
            }
        }
    }
	
	/**Gets if the window launched matches the string
	 * @param queryString
	 * @return
	 */
	public boolean windowLaunchedMatchesString(String queryString) {
		String[] queries = queryString.split("\n");
		Map<String, Boolean> queriesFound = Arrays.asList(queries).stream()
				.collect(Collectors.toMap(
						s -> s, 
						s -> false));
		for (JLabel label : labels) {
			String labelName = label.getText();
			if (queriesFound.containsKey(labelName)) {
				queriesFound.put(labelName, true);
			}
			
		}
		
		//returns if the items in the map are all true in the value position
		return queriesFound.values().stream().allMatch(Boolean::valueOf);
	}
}
