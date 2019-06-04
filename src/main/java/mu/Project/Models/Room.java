package mu.Project.Models;

import mu.Project.Connector;
import mu.Project.Logger;
import mu.Project.NotImplementedException;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class Room implements  Model{

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final static String queryAllFiltered = "SELECT hotels.name, hotels.stars, room_type.type_name, " +
            "rooms.room_number, room_type.price, room_type.double_bed * 2 + " +
            "room_type.single_bed, room_type.safe, room_type.sea_view FROM rooms\n" +
            "INNER JOIN hotels ON (rooms.hotel_id = hotels.id)\n" +
            "INNER JOIN room_type ON (room_type.id = rooms.room_type_id)\n" +
            "WHERE room_type.price  <= ? AND\n" +
            "room_type.double_bed * 2 + room_type.single_bed >= ? AND\n" +
            "room_type.sea_view = ? AND\n" +
            "room_type.safe = ? AND\n" +
            "hotels.city LIKE ? AND\n" +
            "hotels.stars >= ? AND\n" +
            "rooms.id NOT IN (SELECT room_id FROM reservations\n" +
            "WHERE ? <= start_date < ? AND\n" +
            "? < end_date <= ?)\n" +
            "GROUP BY rooms.hotel_id, rooms.room_number";

    /**
     * Return table model of available rooms within the bounds of given filters.
     *
     * ResultSet is closing automatically when returned to other modules.
     * Therefore we must build table model in the same class while working with sqlite-jdbc.
     *
     * @param maxBudget Integer
     * @param personCount Integer
     * @param seaView Boolean
     * @param safe Boolean
     * @param city String
     * @param starCount Integer
     * @param startDate Date object
     * @param endDate Date object
     * @return Correspondent table model.
     * @see DefaultTableModel
     * @throws InvalidDateIntervalException if startDate <= now or startDate >= endDate
     */
    public static DefaultTableModel getAvailableRoomsAsTableModel(Integer maxBudget, Integer personCount, Boolean seaView, Boolean safe,
                                                                  String city, Integer starCount, Date startDate, Date endDate)
            throws InvalidDateIntervalException {

        if (startDate.compareTo(endDate) >= 0 || startDate.compareTo(new Date()) <= 0) {
            throw new InvalidDateIntervalException(startDate, endDate);
        }

        DefaultTableModel tableModel = null;
        try (PreparedStatement preparedStatement = Connector.getInstance().prepareStatement(queryAllFiltered)) {
            preparedStatement.setInt(1, maxBudget);
            preparedStatement.setInt(2, personCount);
            preparedStatement.setInt(3, (seaView) ? 1 : 0);
            preparedStatement.setInt(4, (safe) ? 1 : 0);
            preparedStatement.setString(5, (city.equals("All")) ? "%" : city);
            preparedStatement.setInt(6, starCount);

            String startDateString = dateFormat.format(startDate);
            String endDateString = dateFormat.format(endDate);
            preparedStatement.setString(7, startDateString);
            preparedStatement.setString(8, endDateString);
            preparedStatement.setString(9, startDateString);
            preparedStatement.setString(10, endDateString);

            tableModel = buildTableModel(preparedStatement.executeQuery());

        } catch (SQLException e) {
            Logger.getInstance().addLog(e);
        }

        return tableModel;
    }


    public static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> row = new Vector<Object>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                row.add(resultSet.getObject(i));
            }
            rows.add(row);
        }

        return new DefaultTableModel(rows, columnNames);
    }

    public void save() {
        throw new NotImplementedException();
    }

    public void delete() {
        throw new NotImplementedException();
    }
}
