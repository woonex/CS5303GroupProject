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
	private static final String TAB = " ".repeat(5);
	
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
		double dayCost = 50;
		sb.append("User: ");
		sb.append(user.getUsername() + "\n\n");
		
		long totalCost = 0;
		for (Reservation res : reservations) {
			long qtyDays = ChronoUnit.DAYS.between(res.getStartDate(), res.getEndDate());
			sb.append("Reservation:\n");
			sb.append(TAB);
			sb.append("Start date: ");
			sb.append(res.getStartDate());
			sb.append(TAB + "End Date: ");
			sb.append(res.getEndDate());
			sb.append("\n");
			
			sb.append(TAB);
			sb.append("Days: ");
			sb.append(TAB);
			sb.append("*" + TAB + "Day Cost");
			sb.append(TAB);
			sb.append("*" + TAB + "Modifier");
			sb.append(TAB);
			sb.append("=" + TAB + "Total Reservation Cost");
			sb.append("\n");
			
			sb.append(TAB.repeat(2));
			sb.append(qtyDays);
			sb.append(TAB.repeat(3));
			sb.append("$ " + String.format("%.2f", dayCost));
			sb.append(TAB.repeat(3));
			
			double resCost;
			if (res.wasCancelled()) {
				sb.append("80%");
				resCost = dayCost * qtyDays * .8;
			} else {
				sb.append("100%");
				resCost = dayCost * qtyDays;
			}
			
			sb.append(TAB.repeat(2) + "=");
			sb.append(TAB.repeat(2) + "$" + String.format("%.2f", resCost));
			sb.append("\n\n");			
			totalCost += resCost;
		}
		
		sb.append("TOTAL COST: $");
		sb.append(totalCost);
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
