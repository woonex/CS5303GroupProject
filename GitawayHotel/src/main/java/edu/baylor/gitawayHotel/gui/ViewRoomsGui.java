package edu.baylor.gitawayHotel.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;

public class ViewRoomsGui implements IGui {
	private JPanel panel;
	private DefaultTableModel model;
	private String[] columnNames = { "Number", "Bed Quantity", "Bed Type", "No Smoking" };
	private JButton saveRoomsButton;
	private RoomServices roomServices;
	private JButton backButton;

	public ViewRoomsGui(RoomServices roomServices) {
		this.roomServices = roomServices;
		layoutMainArea();
	}

	/**Performs the layout of the component
	 * 
	 */
	public void layoutMainArea() {
		panel = new JPanel();
		List<Room> rooms = roomServices.getRooms();
		
		model = new DefaultTableModel(columnNames, 0);

		// adds each object in the rooms.json file to the model 
		for (Room room : rooms) {
			Object[] row = { room.getRoom(), room.getBedQty(), room.getBedType(), room.getNoSmoking() };
			model.addRow(row);
		}

		// makes a table with the rooms.json data
		JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		//add the save rooms button
		saveRoomsButton = new JButton("Save Rooms");
		
		//add the back button
		backButton = new JButton("Back to previous");
		
		
		panel.add(scrollPane);
		panel.add(saveRoomsButton);
		panel.add(backButton);
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
	
	/**
	 * Gets the panel containing all interactable components
	 * 
	 * @return the panel
	 */
	@Override
	public JPanel getFullPanel() {
		return this.panel;
	}

}
