package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;

public class ViewRoomStateGui implements IGui {
	private JPanel panel;
	private ReservationService resService;
	private RoomServices roomServices;
	
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scrollPane;
	
	private String[] columnNames = {
			"Room Number", "Reserved?", "Guest", "Check Out Date"
	};
	private JButton backButton;

	public ViewRoomStateGui(RoomServices roomService, ReservationService resService) {
		this.roomServices = roomService;
		this.resService = resService;
		
		doLayout();
	}
	
	private void doLayout() {
		panel = new JPanel(new BorderLayout());
		JPanel tablePanel = new JPanel();
		createModel();
		updateModel();
		
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		scrollPane = new JScrollPane(table);
		
		backButton = new JButton("Back to previous");
		tablePanel.add(scrollPane);
		
		panel.add(tablePanel, BorderLayout.CENTER);
		panel.add(backButton, BorderLayout.SOUTH);
	}
	
	private void createModel() {
		model = new DefaultTableModel(columnNames, 0);
	}

	/**Updates the table of the items with the current state from the services
	 * 
	 */
	public void updateModel() {
		List<Room> rooms = roomServices.getRooms();
		
		model.setRowCount(0);
		model.fireTableDataChanged();
		LocalDate now = LocalDate.now();
		
		// adds each object in the rooms.json file to the model 
		for (Room room : rooms) {
			List<Reservation> reservations = resService.getReservationsForRoom(room);
			Reservation res = null;
			
			for (Reservation current : reservations) {
				LocalDate startDate = current.getStartDate();
				if ((startDate.isAfter(now) || startDate.equals(now))
						&& now.isBefore(current.getEndDate())) {
					res = current;
					break;
				}
			}
			boolean isReserved = res != null;
			String guestName;
			String checkoutDate;
			if (isReserved) {
				guestName = res.getGuest().getUsername();
				checkoutDate = res.getEndDate().toString();
			} else {
				guestName = "Not Reserved";
				checkoutDate = "Not Reserved";
			}
			
			Object[] row = { 
					room.getRoom(), 
					isReserved,
					guestName,
					checkoutDate
					};
			model.addRow(row);
		}
		
		model.fireTableDataChanged();
		
		if (table != null) {
			table.repaint();
		}
		if (scrollPane != null) {
			scrollPane.repaint();
		}
		panel.repaint();
	}
	
	public JButton getBackButton() {
		return this.backButton;
	}
	
	@Override
	public JPanel getFullPanel() {
		this.table.clearSelection();
		updateModel();
		
		return this.panel;
	}
}
