package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.user.User;

public class BillingGui implements IGui {
	private JPanel panel;
	private JButton backButton;
	private JEditorPane editorPane;
	
	public BillingGui() {
		doLayout();
	}
	
	private void doLayout() {
		panel = new JPanel(new BorderLayout());
		
		editorPane = new JEditorPane();
		editorPane.setContentType("text");
		editorPane.setEditable(false);
		setText("");
		JScrollPane scroller = new JScrollPane(editorPane);
		
		JPanel bottomPanel = new JPanel();
		backButton = new JButton("Back to Previous");
		bottomPanel.add(backButton);
		
		panel.add(scroller);
		panel.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void setText(String string) {
		this.editorPane.setText(string);
	}

	public void setDisplay(User user, List<Reservation> reservations) {
		StringBuilder sb = new StringBuilder();

		sb.append("Guest: ");
		sb.append(user.getUsername() + "\n\n");
		
		double totalCost = 0;
		for (Reservation res : reservations) {
			sb.append(res.getFormattedCost());
			double resCost = res.getFullCost();
			totalCost += resCost;
		}
		
		sb.append("TOTAL BILL:" + " ".repeat(6) + "$ ");
		sb.append(String.format("%.2f", totalCost));
		setText(sb.toString());
	}
	
	@Override
	public JPanel getFullPanel() {
		return this.panel;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JButton getBackButton() {
		return backButton;
	}

	public JEditorPane getEditorPane() {
		return editorPane;
	}

}
