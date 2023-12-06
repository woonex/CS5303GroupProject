package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.ArrayList;
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
import edu.baylor.gitawayHotel.user.User;

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
			resService.removeReservation(res);
			res.setCheckinStatus(true);
			resService.addReservation(res);
			updateModel();
		});
		this.checkoutButton = new JButton("Check Out Guest");
		checkoutButton.addActionListener(l -> {
			Reservation res = this.getSelectedReservation();
			resService.removeReservation(res);
			res.setCheckinStatus(false);
			resService.addReservation(res);
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
			List<Reservation> current = getReservationsForToday(reservations);
			boolean isReserved = !current.isEmpty();
			
			User guestName;
			String checkoutDate;
			String checkinStatus;
			
			if (isReserved) {
				for (Reservation res : current) {
					guestName = res.getGuest();
					checkoutDate = res.getEndDate().toString();
					checkinStatus = res.getCheckinStatus() ? "Checked In" : "Not Checked In";
					Object[] row = { 
							room.getRoom(), 
							isReserved,
							guestName,
							checkoutDate,
							checkinStatus
							};
					model.addRow(row);
				}
			} else {
				guestName = new User("Not Reserved");
				checkoutDate = "Not Reserved";
				checkinStatus = "Not Reserved";
				Object[] row = { 
						room.getRoom(), 
						isReserved,
						guestName,
						checkoutDate,
						checkinStatus
						};
				model.addRow(row);
			}
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
	private static List<Reservation> getReservationsForToday(List<Reservation> reservations) {
		LocalDate now = LocalDate.now();
		List<Reservation> found = new ArrayList<Reservation>();
		for (Reservation current : reservations) {
			LocalDate startDate = current.getStartDate();
			LocalDate endDate = current.getEndDate();
			//get the current reservation
			if ((startDate.isBefore(now) || startDate.equals(now))
					&& (now.equals(endDate) || now.isBefore(endDate))) {
				found.add(current);
			}
		}
		return found;
	}
	
	/**Gets the selected reservation
	 * @return
	 */
	public Reservation getSelectedReservation() {
		int row = table.getSelectedRow();
		
		Integer roomNum = (Integer) model.getValueAt(row, 0);
		Room room = roomServices.getRoomByNumber(roomNum);
		List<Reservation> reservations = getReservationsForToday(resService.getReservationsForRoom(room));
		if (reservations.size() == 1) {
			return reservations.get(0);
		} else if (reservations.isEmpty()) {
			return null;
		} else {
			for (Reservation res : reservations) {
				String checkoutDate = (String) model.getValueAt(row, 3);
				if (res.getEndDate().equals(LocalDate.parse(checkoutDate))) {
					return res;
				}
			}
			System.out.println("This should never be reached");
			return null;
		}
	}
	
	public void selectTableRowByRoomNum(int roomNum) {
		table.clearSelection();
		
		for (int i = 0; i < table.getRowCount(); i++) {
            int roomNumberInTable = (int) table.getValueAt(i, 0);
            if (roomNumberInTable == roomNum) {
                // Select the row if the room number matches
                table.setRowSelectionInterval(i, i);
                break; // Stop iterating once the row is found
            }
        }
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
	
	public JButton getCheckInButton() {
		return this.checkInButton;
	}
	
	public JButton getCheckOutButton() {
		return this.checkoutButton;
	}
	
	@Override
	public JPanel getFullPanel() {
		this.table.clearSelection();
		updateModel();
		manageButtonAvailability();
		
		return this.panel;
	}
}
