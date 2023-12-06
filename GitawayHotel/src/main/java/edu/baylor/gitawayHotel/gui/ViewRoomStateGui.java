package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
	private JButton checkInButton;
	private JButton checkoutButton;
	
	private String[] columnNames = {
			"Room Number", "Reserved?", "Guest", "Check Out Date", "Check In Status"
	};
	private JButton backButton;

	public ViewRoomStateGui(RoomServices roomService, ReservationService resService) {
		this.roomServices = roomService;
		this.resService = resService;
		
		doLayout();
	}
	
	/**Performs the layout of the main panel
	 * 
	 */
	private void doLayout() {
		panel = new JPanel(new BorderLayout());
		JPanel tablePanel = new JPanel(new BorderLayout());
		createTableAndModel();
		updateModel();
		
		
		scrollPane = new JScrollPane(table);
		
		JPanel checkPanel = new JPanel();
		this.checkInButton = new JButton("Check In Guest");
		checkInButton.addActionListener(l -> {
			Reservation res = this.getSelectedReservation();
			res.setCheckinStatus(true);
			updateModel();
		});
		this.checkoutButton = new JButton("Check Out Guest");
		checkoutButton.addActionListener(l -> {
			Reservation res = this.getSelectedReservation();
			res.setCheckinStatus(false);
			updateModel();
		});
		
		checkPanel.add(checkInButton);
		checkPanel.add(checkoutButton);
		
		backButton = new JButton("Back to previous");
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(checkPanel, BorderLayout.SOUTH);
		
		panel.add(tablePanel, BorderLayout.CENTER);
		panel.add(backButton, BorderLayout.SOUTH);
	}
	
	/**Creates table and underlying model
	 * 
	 */
	private void createTableAndModel() {
		model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	manageButtonAvailability();
                }
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);
	}

	/**Updates the table of the items with the current state from the services
	 * 
	 */
	public void updateModel() {
		List<Room> rooms = roomServices.getRooms();
		
		model.setRowCount(0);
		model.fireTableDataChanged();
		
		// adds each object in the rooms.json file to the model 
		for (Room room : rooms) {
			List<Reservation> reservations = resService.getReservationsForRoom(room);
			Reservation res = getReservationForToday(reservations);
			boolean isReserved = res != null;
			String guestName;
			String checkoutDate;
			String checkinStatus;
			if (isReserved) {
				guestName = res.getGuest().getUsername();
				checkoutDate = res.getEndDate().toString();
				checkinStatus = res.getCheckinStatus() ? "Checked In" : "Not Checked In";
				
			} else {
				guestName = "Not Reserved";
				checkoutDate = "Not Reserved";
				checkinStatus = "Not Reserved";
			}
			
			Object[] row = { 
					room.getRoom(), 
					isReserved,
					guestName,
					checkoutDate,
					checkinStatus
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
	
	/**Gets the reservation that is today
	 * @param reservations
	 * @return
	 */
	private static Reservation getReservationForToday(List<Reservation> reservations) {
		LocalDate now = LocalDate.now();
		for (Reservation current : reservations) {
			LocalDate startDate = current.getStartDate();
			LocalDate endDate = current.getEndDate();
			//get the current reservation
			if ((startDate.isAfter(now) || startDate.equals(now))
					&& (now.equals(endDate) || now.isBefore(endDate))) {
				return current;
			}
		}
		return null;
	}
	
	/**Gets the selected reservation
	 * @return
	 */
	public Reservation getSelectedReservation() {
		int row = table.getSelectedRow();
		
		Integer roomNum = (Integer) model.getValueAt(row, 0);
		Room room = roomServices.getRoomByNumber(roomNum);
		
		Reservation res = getReservationForToday(resService.getReservationsForRoom(room));
		return res;
	}
	
	/**manages whether the buttons should be available for the user to ineract with
	 * 
	 */
	private void manageButtonAvailability() {
		int[] selectedRows = table.getSelectedRows();
		boolean checkinState = false;
		boolean checkoutState = false;
		if (selectedRows.length == 1) {
			Reservation res = getSelectedReservation();
			if (res != null) {
				boolean checkinStatus = res.getCheckinStatus();
				checkinState = !checkinStatus;
				checkoutState = checkinStatus;
			}
		} 
		this.checkInButton.setEnabled(checkinState);
		this.checkoutButton.setEnabled(checkoutState);
	}
	
	public JButton getBackButton() {
		return this.backButton;
	}
	
	@Override
	public JPanel getFullPanel() {
		this.table.clearSelection();
		updateModel();
		manageButtonAvailability();
		
		return this.panel;
	}
}
