package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.reservation.LocalDateAdapter;
import edu.baylor.gitawayHotel.user.UserType;
import edu.baylor.gitawayHotel.textPrompt.TextPrompt;

/**A class for viewing rooms or creating reservations for guests
 *
 */
public class ViewRoomsGui implements IGui {
	private static final Logger logger = LogManager.getLogger(ViewRoomsGui.class);
	private static final DateTimeFormatter DATE_FORMATTER = LocalDateAdapter.DATE_FORMATTER;
	private JPanel panel;
	private JPanel datePanel;
	private JPanel actionPanel;
	private DefaultTableModel model;
	private String[] columnNames = { "Number", "Bed Quantity", "Bed Type", "No Smoking" };
	private JButton saveRoomsButton;
	private RoomServices roomServices;
	private JButton backButton;
	private JTextField roomUpdateField;
	private JButton removeRoomButton;
	private JButton addRoomButton;
	private JButton searchButton;
	private JTextField startDateField;
	private TextPrompt startDatePrompt;
	private JTextField endDateField;
	private TextPrompt endDatePrompt;
	private JTable table = null;
	private JScrollPane scrollPane;

	private UserType userType;
	private JButton reserveButton;
	private boolean searchClicked = false;

	public ViewRoomsGui(RoomServices roomServices) {
		this.roomServices = roomServices;
		layoutMainArea();
	}

	/**Internal class to prevent vertically expanding components
	 * @author Nathan
	 *
	 */
	private static class NonVerticalExpanding extends JPanel {
		NonVerticalExpanding(JComponent component) {
			setLayout(new FlowLayout(FlowLayout.CENTER));
			add(component);
		}
	}
	
	/**Performs the layout of the component
	 * 
	 */
	public void layoutMainArea() {
		panel = new JPanel();
		createModel();
		updateModel();
		// makes a table with the rooms.json data
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		
		ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	manageReserveButtonAvailable();
                }
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);
		

		scrollPane = new JScrollPane(table);
		
		// clerk/admin actions
		saveRoomsButton = new JButton("Save Rooms");
		addRoomButton = new JButton("Add Room");
		removeRoomButton = new JButton("Remove Room");
		roomUpdateField = new JTextField(20);

		// guest actions
		startDateField = new JTextField(10);
		endDateField = new JTextField(10);
		searchButton = new JButton("Search");
		reserveButton = new JButton("Reserve Selected Room");
		reserveButton.setEnabled(false);

		// everybody actions
		backButton = new JButton("Back to previous");
		
		if (userType == null) {
			return;
		}
		switch (userType) {
		case ADMIN:
		case HOTEL_CLERK:
			panel.add(scrollPane);
			panel.add(roomUpdateField);
			panel.add(addRoomButton);
			panel.add(removeRoomButton);
			panel.add(saveRoomsButton);
			panel.add(backButton);
			break;
		case GUEST:
		default:
			datePanel = new JPanel(new BorderLayout());
			startDatePrompt = new TextPrompt("Check-in date", startDateField);
			endDatePrompt = new TextPrompt("Check-out date", endDateField);
			startDatePrompt.changeAlpha(0.5f);
			endDatePrompt.changeAlpha(0.5f);
			
			//add a button to the search listener to detect that the search button is clicked
			searchButton.addActionListener(e -> {
				searchClicked = true;
			});
			
			/*define something that listens to the document of the text fields and will flag the search as not clicked
			 * (requires the user to click the search each time after modifying the date)
			*/
			DocumentListener dateFieldListener = new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					changed();
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					changed();
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					changed();
				}
				private void changed() {
					searchClicked = false;
					reserveButton.setEnabled(false);
				}
			};
			startDateField.getDocument().addDocumentListener(dateFieldListener);
			endDateField.getDocument().addDocumentListener(dateFieldListener);
			
			//setup date entry portion
			JPanel topDate = new JPanel(new GridLayout(1, 2));
			topDate.add(new NonVerticalExpanding(startDateField));
			topDate.add(new NonVerticalExpanding(endDateField));
			datePanel.add(topDate, BorderLayout.NORTH);
			
			JPanel middleDate = new JPanel(new GridLayout(1, 2));
			middleDate.add(new NonVerticalExpanding(new JLabel("yyyy-MM-dd")));
			middleDate.add(new NonVerticalExpanding(new JLabel("yyyy-MM-dd")));
			datePanel.add(middleDate, BorderLayout.CENTER);
			
			JPanel bottomDate = new JPanel(new FlowLayout(FlowLayout.CENTER));
			bottomDate.add(searchButton);
			datePanel.add(bottomDate, BorderLayout.SOUTH);
			
			actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			actionPanel.add(backButton);
			actionPanel.add(reserveButton);

			panel.add(datePanel);
			panel.add(scrollPane);
			panel.add(actionPanel);
			
			break;
		}
	}
	
	/**Sets whether the reserve vutton should be available by checking the gui state
	 * 
	 */
	private void manageReserveButtonAvailable() {
		boolean validDates = true;
		try {
			LocalDate start = getStartDate();
			LocalDate end = getEndDate();
		} catch (DateTimeParseException e) {
			validDates = false;
		}
		
		boolean isAvailable = isDateValid();
		
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length == 1 && validDates && isAvailable && searchClicked) {
			reserveButton.setEnabled(true);
		} else {
			reserveButton.setEnabled(false);
		}
	}
	
	/**Creates the model for the table
	 * 
	 */
	private void createModel() {
		model = new DefaultTableModel(columnNames, 0) { // makes table editable for admin and clerk, uneditable for all others
			@Override
			public boolean isCellEditable(int row, int column) {
				switch (userType) {
				case ADMIN:
				case HOTEL_CLERK:
					return true;
				case GUEST:
					return false;
				default:
					return false;
				}
			}
		};
	}

	public void updateModel() {
		List<Room> rooms = roomServices.getRooms();
		
		model.setRowCount(0);
		model.fireTableDataChanged();
		
		// adds each object in the rooms.json file to the model 
		for (Room room : rooms) {
			Object[] row = { room.getRoom(), room.getBedQty(), room.getBedType(), room.getNoSmoking() };
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
	
	/**Gets the rooms in the table that the user has entered
	 * @return list of rooms
	 */
	public List<Room> getRoomsInTable() {
		List<Room> rooms = new ArrayList<Room>();
		
		for (int i = 0; i < this.model.getRowCount(); i++) {
			Room room = new Room();
			for (int j = 0; j < columnNames.length; j++) {
				Object currentVal = model.getValueAt(i, j);
				if (j == 0) {
					room.setRoom(handleIntConversion(currentVal));
				} else if (j == 1) {
					room.setBedQty(handleIntConversion(currentVal));
				} else if (j == 2) {
					room.setBedType((String) currentVal);
				} else if (j == 3) {
					room.setNoSmoking(handleBoolConversion(currentVal));
				}
			}
			rooms.add(room);
		}
		return rooms;
	}
	
	/**Handles boolean coversion of generic object
	 * TODO add error handling and defaulting
	 * @param value
	 * @return
	 */
	private static Boolean handleBoolConversion(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else if (value instanceof String) {
			String val = (String) value;
			return Boolean.parseBoolean(val);
		}
		return false;
	}
	
	/**Handles integer conversion of generic object
	 * TODO add error handling and defaulting
	 * @param value
	 * @return
	 */
	private static Integer handleIntConversion(Object value) {
		if (value instanceof Integer) {
			return (Integer) value;
		} else if (value instanceof String) {
			String val = (String) value;
			return Integer.parseInt(val);
		}
		return -1;
	}
	
	/**Gets the save rooms button
	 * @return
	 */
	public JButton getSaveRoomsButton() {
		return this.saveRoomsButton;
	}

	/**Gets the back button
	 * @return
	 */
	public JButton getBackButton() {
		return this.backButton;
	}

	/**Gets the search button
	 * @return
	 */
	public JButton getSearchButton() {
		return this.searchButton;
	}
	
	/**
	 * Gets the panel containing all interactable components
	 * 
	 * @return the panel
	 */
	@Override
	public JPanel getFullPanel() {
		this.table.clearSelection();
		this.searchClicked = false;
		updateModel();
		return this.panel;
	}

	public JTextField getRoomUpdateField() {
		return roomUpdateField;
	}

	public JButton getRemoveRoomButton() {
		return removeRoomButton;
	}

	public JButton getAddRoomButton() {
		return addRoomButton;
	}
	
	public JButton getReserveRoomButton() {
		return this.reserveButton;
	}
	
	public int getDesiredRoomReservation() {
		int selectedRow = table.getSelectedRow();
		return (int) model.getValueAt(selectedRow, 0);
	}

	/**Gets the check-in date provided by input
	 * @return the username provided
	 */
	public LocalDate getStartDate() {
		DateTimeFormatter formatter = DATE_FORMATTER;
  		String date = this.startDateField.getText();
  		if (date == null || "".equals(date)) {
  			return null;
  		}
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}
	
	/**Gets the check-out date provided by input
	 * @return the password provided
	 */
	public LocalDate getEndDate() {
		DateTimeFormatter formatter = DATE_FORMATTER;
  		String date = this.endDateField.getText();
  		if (date == null || "".equals(date)) {
  			return null;
  		}
  		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}

	/**Sets the user type to determine user view
	 * @param userType the user type
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
		layoutMainArea();
	}

	/**Sets the user type to determine user view
	 * @return the user type
	 */
	public UserType getUserType() {
		return userType;
	}

	/**Sets the table to the filtered value 
	 * @param availableRooms a set of available rooms 
	 */
	public void setFilteredRooms(Set<Room> availableRooms) {
		List<Room> rooms = new ArrayList<>(availableRooms);
		Collections.sort(rooms);
		
		model.setRowCount(0);
		
		// adds each object in availableRooms to the model 
		for (Room room : rooms) {
			Object[] row = { room.getRoom(), room.getBedQty(), room.getBedType(), room.getNoSmoking() };
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
	
	/**Gets if the date input by the user for reservation is valid
	 * @return
	 */
	boolean isDateValid() {
		LocalDate startDate = getStartDate();
		LocalDate endDate = getEndDate();
		
		if (startDate == null || endDate == null) {
			return false;
		} else if (startDate.isBefore(LocalDate.now())) {
			logger.warn("Start date is before today's date");
			return false;
		} else if (startDate.isAfter(endDate)) {
			logger.warn("Start date is after end date");
			return false;
		} else if (startDate.equals(endDate)) {
			logger.warn("Start date and end date must not be the same");
			return false;
		}
		
		return true;
	}

	public void setStartDate(LocalDate startDate) {
		String text = "";
		if (startDate != null) {
			text = DATE_FORMATTER.format(startDate);
		} 
		startDateField.setText(text);
	}

	public void setEndDate(LocalDate endDate) {
		String text = "";
		if (endDate != null) {
			text = DATE_FORMATTER.format(endDate);
		}
		endDateField.setText(text);
	}
	
	public void setRoomField(int newRoomNum) {
		this.roomUpdateField.setText(String.valueOf(newRoomNum));
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

	public void selectTableRowByIndex(int index) {
		table.clearSelection();
		table.setRowSelectionInterval(index, index);
	}

	public void setTableDataInSelectedRow(Room newRoom) {
		int rowNum = table.getSelectedRow();
		model.setValueAt(newRoom.getBedQty(), rowNum, 1);
		model.setValueAt(newRoom.getBedType(), rowNum, 2);
		model.setValueAt(newRoom.getNoSmoking(), rowNum, 3);
	}
}
