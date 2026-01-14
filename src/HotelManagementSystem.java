import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class HotelManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "your_db_password"; // change before running

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Load the Drivers
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // System.out.println("Drivers Loaded Successfully!!!");
        } catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            while(true) {
                System.out.println();
                System.out.println("Hotel Management System");

                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve A Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Information About Room");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");

                System.out.print("Choose a option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getInfoAboutRoom(connection, scanner);
                        break;
                    case 4:
                        updateReservations(connection, scanner);
                        break;
                    case 5:
                        deleteReservations(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return; // break the loop by returning from main method
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter guest name: ");
            String guest_name = scanner.nextLine();
            System.out.print("Enter valid room no: ");
            int room_no = scanner.nextInt();
            System.out.print("Enter contact no: ");
            String contact_no = scanner.next();

            String query = "INSERT into reservations (guest_name, room_no, contact_no) " + "VALUES('"+guest_name+"' , "+room_no+", '"+contact_no+"' )";

            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);

            if(affectedRows > 0) {
                System.out.println("Reservation successful!");
            } else {
                System.out.println("Reservation failed!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection) {
        String query = "Select reservation_id, guest_name, room_no, contact_no, reservation_date from reservations;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("======================================================================");
            while(resultSet.next()) {
                int reservation_id = resultSet.getInt("reservation_id");
                String guest_name = resultSet.getString("guest_name");
                int room_no = resultSet.getInt("room_no");
                String contact_no = resultSet.getString("contact_no");
                String reservation_date = resultSet.getTimestamp("reservation_date").toString();

                System.out.println("Reservation_id: " + reservation_id);
                System.out.println("Guest_name: " + guest_name);
                System.out.println("Room_no: " + room_no);
                System.out.println("Contact_no: " + contact_no);
                System.out.println("Reservation_date: " + reservation_date);
                System.out.println("======================================================================");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getInfoAboutRoom(Connection connection, Scanner scanner) {
        System.out.print("Enter Reservation_id: ");
        int reservation_id = scanner.nextInt();
        System.out.print("Enter Guest_name: ");
        String guest_name = scanner.nextLine();

        String query = "Select room_no from reservations where reservation_id = " + reservation_id + "AND guest_name = '" + guest_name + "';";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("======================================================================");
            while (resultSet.next()) {
                int room_no = resultSet.getInt("room_no");
                System.out.println("Reservation_id: " + reservation_id);
                System.out.println("Guest_name: " + guest_name);
                System.out.println("Room_no: " + room_no);
                System.out.println("======================================================================");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservations(Connection connection, Scanner scanner) {
        System.out.print("Enter Reservation_id: ");
        int reservation_id = scanner.nextInt();
        scanner.nextLine();

        if(!reservationExist(connection, reservation_id)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = scanner.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNo = scanner.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNo = scanner.next();
        String query = "UPDATE reservations SET guest_name = '"+ newGuestName +"'"
                + " , room_no = " + newRoomNo + " , contact_no = '"+ newContactNo +"'"
                + " WHERE reservation_id = " + reservation_id;

        try {
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);

            if(affectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation updation failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean reservationExist(Connection connection, int reservationID) {
        String query = "Select reservation_id from reservations WHERE reservation_id = " + reservationID;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            return resultSet.next(); // if there's a result then reservation exists.
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void deleteReservations(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID to delete: ");
        int reservation_id = scanner.nextInt();

        if(!reservationExist(connection, reservation_id)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        String query = "DELETE from reservations WHERE reservation_id = " + reservation_id;

        try {
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);

            if(affectedRows > 0) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Reservation deletion failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 0;
        while(i < 5) {
            System.out.print(".");
            Thread.sleep(500);
            i++;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}