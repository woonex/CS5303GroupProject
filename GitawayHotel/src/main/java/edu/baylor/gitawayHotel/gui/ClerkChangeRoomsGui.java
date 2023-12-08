package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
public class ClerkChangeRoomsGui implements IGui {
	private static final Logger logger = LogManager.getLogger(ClerkChangeRoomsGui.class);
	private static final DateTimeFormatter DATE_FORMATTER = LocalDateAdapter.DATE_FORMATTER;
	private JPanel panel;
	private DefaultTableModel model;
	private String[] columnNames = { "Number", "Bed Quantity", "Bed Type", "No Smoking" , "Daily Cost"};
	private JButton saveRoomsButton;
	private RoomServices roomServices;
	private JButton backButton;
	private JTextField roomUpdateField;
	private JButton removeRoomButton;
	private JButton addRoomButton;
	private JTable table = null;
	private JScrollPane scrollPane;

	public ClerkChangeRoomsGui(RoomServices roomServices) {
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
		saveRoomsButton = new JButton("Save Rooms");
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
//                	System.out.println("Hello");
                }
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);

		scrollPane = new JScrollPane(table);
		
		// clerk/admin actions
		
		addRoomButton = new JButton("Add Room");
		addRoomButton.setEnabled(false);
		removeRoomButton = new JButton("Remove Room");
		removeRoomButton.setEnabled(false);
		roomUpdateField = new JTextField(20);
		
		ActionListener l2 = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateModel();
			}
		};
		addRoomButton.addActionListener(l2);
		removeRoomButton.addActionListener(l2);
		
		DocumentListener roomFieldListener  = new DocumentListener() {
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
				String inputText = roomUpdateField.getText();
				boolean addState = false;
				boolean removeState = false;
				if (inputText == null || inputText.equals("")) {
					addState = false;
					removeState = false;
				} else {
					try {
						int roomNum = Integer.parseInt(inputText);
						Room room = roomServices.getRoomByNumber(roomNum);
						addState = room == null;
						removeState = room != null;
					} catch (Exception e) {
						
					}
				}
				
				addRoomButton.setEnabled(addState);
				removeRoomButton.setEnabled(removeState);
			}
		};
		roomUpdateField.getDocument().addDocumentListener(roomFieldListener);

		// everybody actions
		backButton = new JButton("Back to previous");
		
		panel.add(scrollPane);
		panel.add(roomUpdateField);
		panel.add(addRoomButton);
		panel.add(removeRoomButton);
		panel.add(saveRoomsButton);
		panel.add(backButton);
	}
	
	/**Creates the model for the table
	 * 
	 */
	private void createModel() {
		model = new DefaultTableModel(columnNames, 0) { // makes table editable for admin and clerk, uneditable for all others
			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				}
				return true;
			}
		};
		
		
		
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				//handles the availability of the save button
				if (e.getType() == TableModelEvent.UPDATE) {
					int row = e.getFirstRow();
					int column = e.getColumn();
					if (row >= 0 && column >= 0) {
						Object currentVal = table.getValueAt(row, column);
						
						boolean state = true;
						
						if (column == 2) {
							if ("".equals((String) currentVal)) {
								state = false;
							}
						} else {
							try {
								if (column == 0 || column == 1) {
									handleIntConversion(currentVal);
								} else if (column == 3) {
									handleBoolConversion(currentVal);
								} else if (column == 4) {
									handleDoubleConversion(currentVal);
								}
							} catch (Exception ex) {
								state = false;
							}
						}
						saveRoomsButton.setEnabled(state);
					}
				}
			}
		});
	}

	public void updateModel() {
		List<Room> rooms = roomServices.getRooms();
		saveRoomsButton.setEnabled(false);
		
		model.setRowCount(0);
		model.fireTableDataChanged();
		
		// adds each object in the rooms.json file to the model 
		for (Room room : rooms) {
			Object[] row = { room.getRoom(), room.getBedQty(), room.getBedType(), room.getNoSmoking() , room.getDailyCost()};
			model.addRow(row);
		}
		
		model.fireTableDataChanged();
		
		if (table != null) {
			table.setPreferredScrollableViewportSize(table.getPreferredSize());
			table.revalidate();
			table.repaint();
		}
		if (scrollPane != null) {
			scrollPane.revalidate();
			scrollPane.repaint();
		}
		panel.revalidate();
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
				} else if (j == 4) {
					room.setDailyCost(handleDoubleConversion(currentVal));
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
			if ("true".equalsIgnoreCase(val)) {
				return true;
			} else if ("false".equalsIgnoreCase(val)) {
				return false;
			} else {
				throw new UnsupportedOperationException("Value " + val + " is not a boolean value");
			}
		}
		throw new UnsupportedOperationException("Value " + value.getClass() + " boolean conversion not implemented");
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
	
	private static Double handleDoubleConversion(Object value) {
		if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof String) {
			String val = (String) value;
			return Double.parseDouble(val);
		}
		return -1.0;
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
		this.table.clearSelection();
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
	
	public void setRoomField(Integer newRoomNum) {
		String display = "";
		if (newRoomNum != null) {
			display = String.valueOf(newRoomNum);
		}
		this.roomUpdateField.setText(display);
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
