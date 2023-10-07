package edu.baylor.gitawayHotel.gui;

import java.io.File;
import java.io.IOException;

//import javax.swing.JButton;
//import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
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
		try {
			final Room[] rooms = objectMapper.readValue(new File("GitawayHotel\\target\\rooms.json"), Room[].class);

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

			// Add event listener to JTable
			table.getModel().addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					// Get row and column of cell that was changed
					int row = e.getFirstRow();
					int column = e.getColumn();

					// Get new value entered by user
					Object newValue = table.getModel().getValueAt(row, column);

					// Update data in rooms list with new value
					Room room = rooms[row];
					if (column == 0) {
						System.out.println("you cant change room numbers!");
					} else if (column == 1) {
						room.setBedQty((String) newValue);
					} else if (column == 2) {
						room.setBedType((String) newValue);
					} else if (column == 3) {
						room.setNoSmoking((String) newValue);
					}

					// Write updated data back to JSON file
					try {
						objectMapper.writeValue(new File("GitawayHotel\\target\\rooms.json"), rooms);
					} catch (StreamWriteException e1) {
						e1.printStackTrace();
					} catch (DatabindException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
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
