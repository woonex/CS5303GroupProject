package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.user.User;

public class ReservationGui implements IGui {
	private JButton backButton;
	private JPanel fullPanel;
	private JTable table;
	private DefaultTableModel model;
	private String[] columnNames = {"Check in Date", "Check out Date", "Room"};
	private ReservationService resService;
	private User user;
	private JScrollPane tableScroller;
	private JButton modifyButton;
	
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
		modifyButton = new JButton("Modify");
		modifyButton.setEnabled(false);
		
		setupTable();
		
		
		fullPanel.add(label, BorderLayout.NORTH);
		fullPanel.add(tableScroller, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.add(backButton);
		buttonPanel.add(modifyButton);
		fullPanel.add(buttonPanel, BorderLayout.SOUTH);
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
		
		ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	manageModifyAvailable();
                }
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);
		
		updateModel();
	}

	/**Internal to manage whether the modification button is available or not
	 * 
	 */
	protected void manageModifyAvailable() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length == 1) {
			modifyButton.setEnabled(true);
		} else {
			modifyButton.setEnabled(false);
		}
	}

	/**Updates the table by querying the reservations and displaying them
	 * 
	 */
	private void updateModel() {
		List<Reservation> reservations = resService.getReservationsByUser(user);
		Collections.sort(reservations);
		model.setRowCount(0);
		
		for (Reservation res : reservations) {
			Object[] row = {res.getStartDate(), res.getEndDate(), res.getRoom()};
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
	
	public JButton getModifyReservationButton() {
		return this.modifyButton;
	}

	public JButton getBackButton() {
		return this.backButton;
	}

	@Override
	public JPanel getFullPanel() {
		updateModel();
		return fullPanel;
	}

	public Reservation getSelectedReservation() {
		int row = table.getSelectedRow();
		Room room = (Room) model.getValueAt(row, 2);
		
		LocalDate startDate = (LocalDate) model.getValueAt(row, 0);//LocalDate.parse(start, LocalDateAdapter.DATE_FORMATTER);
		LocalDate endDate = (LocalDate) model.getValueAt(row, 1); //LocalDate.parse(end, LocalDateAdapter.DATE_FORMATTER);
		
		
		Reservation selected = new Reservation(startDate, endDate, user, room);
//		List<Reservation> allRes = resService.getReservationsByUser(user);
		return selected;
	}

}
