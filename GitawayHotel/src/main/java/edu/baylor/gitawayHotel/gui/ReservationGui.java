package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserType;

public class ReservationGui implements IGui {
	private JButton backButton;
	private JPanel fullPanel;
	private JTable table;
	private DefaultTableModel model;
	private String[] columnNames = {"Check in Date", "Check out Date", "Room"};
	private ReservationService resService;
	private User user;
	private JScrollPane tableScroller;
	
	public ReservationGui(ReservationService resService) {
		this.resService = resService;
		layoutMainArea();
	}
	
	public void setUser(User user) {
		this.user = user;
		updateModel();
	}

	/**Lays out themain area of the panel
	 * @return
	 */
	protected JPanel layoutMainArea() {
		fullPanel = new JPanel(new BorderLayout());
		
		JLabel label = new JLabel("View your reservations below");
		
		backButton = new JButton("Back");
		
		setupTable();
		
		
		fullPanel.add(label, BorderLayout.NORTH);
		fullPanel.add(tableScroller, BorderLayout.CENTER);
		fullPanel.add(backButton, BorderLayout.SOUTH);
		return fullPanel;
	}
	
	/**Sets up the table for viewing only of reservations
	 * 
	 */
	private void setupTable() {
		model = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = -4095787113273490124L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		tableScroller = new JScrollPane(table);
		
		updateModel();
	}

	/**Updates the table by querying the reservations and displaying them
	 * 
	 */
	private void updateModel() {
		List<Reservation> reservations = resService.getReservationsByUser(user);
		
		model.setRowCount(0);
		
		for (Reservation res : reservations) {
			Object[] row = {res.getStartDate(), res.getEndDate(), res.getRoom().toString()};
			model.addRow(row);
		}
		
		model.fireTableDataChanged();
		
		if (table != null) {
			table.repaint();
		}
		if (tableScroller != null) {
			tableScroller.repaint();
		}
		fullPanel.repaint();
	}

	public JButton getBackButton() {
		return this.backButton;
	}

	@Override
	public JPanel getFullPanel() {
		return fullPanel;
	}

}
