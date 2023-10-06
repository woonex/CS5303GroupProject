package edu.baylor.gitawayHotel.gui;

import java.io.File;
import java.io.IOException;

//import javax.swing.JButton;
//import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.baylor.gitawayHotel.Room.Room;


//import edu.baylor.gitawayHotel.user.UserType;

public class ViewRoomsGui implements IGui {
	private JPanel panel;

	public ViewRoomsGui() {
		layoutMainArea();
	}

	public void layoutMainArea() {
		panel = new JPanel();
		ObjectMapper objectMapper = new ObjectMapper();

		// Read the JSON file and convert it to a Java object
		Room[] rooms = null;
		try {
			rooms = objectMapper.readValue(new File("GitawayHotel\\target\\rooms.json"), Room[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] columnNames = { "Number", "Bed Quantity", "Bed Type", "No Smoking" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// adds each object in the rooms.json file to the model 
		for (Room room : rooms) {
			Object[] row = { room.getRoom(), room.getBedQty(), room.getBedType(), room.getNoSmoking() };
			model.addRow(row);
		}

		// makes a table with the rooms.json data
		JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);
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
