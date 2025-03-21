package JDBC;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;


public class JDBC {
	private static Scanner choice = new Scanner(System.in);
	public static String dbURL = null;
	public static String username = null;
	public static String password = null;
	public static String[] dbTableColumn = {"orders", "book", "customer"};
	public static String path_to_txt = null;
	public static Connection connection = null;
	public static Scanner scanner= new Scanner(System.in);
	public static String book_txt = null;
	public static String orders_txt = null;
	public static String customer_txt = null;
	

	// 1.1. Create Tables
	
	static void createTable() {
		
		System.out.println();
		System.out.println("Create Table book, order and customer? (Y/N)");
		String QueryCreate = choice.next();
		
		if (QueryCreate.toUpperCase().equals("Y")) {
			
			//Create Table Based on the requirements
			try {
				PreparedStatement[] stmts = {
					connection.prepareStatement("CREATE TABLE IF NOT EXISTS orders (OID varchar(8) NOT NULL, UID varchar(10) NOT NULL, Order_Date date NOT NULL, Order_ISBN varchar(13) NOT NULL, Order_Quantity int unsigned NOT NULL,Shipping_Status varchar(20) NOT NULL, PRIMARY KEY (OID))"),
					connection.prepareStatement("CREATE TABLE IF NOT EXISTS book (ISBN varchar(13) NOT NULL, Title varchar(100) NOT NULL, Authors varchar(50) NOT NULL, Price int unsigned NOT NULL, Inventory_Quantity int unsigned NOT NULL, PRIMARY KEY (ISBN))"),
					connection.prepareStatement("CREATE TABLE IF NOT EXISTS customer (UID varchar(10) NOT NULL, Name varchar(50) NOT NULL, Address varchar(200) NOT NULL, PRIMARY KEY (UID))")
				};

				for (int i = 0; i < stmts.length; i++) {
					stmts[i].execute();
				}
			
				System.out.println("Tables created.");
				
				printMain();
			
			} catch(Exception x) {
				printMain();
				System.err.println("Cannot Create Tables!");
			}
			
		} else {
			printMain();
		}
	}
	
	// 1.2. Load data
	
	static void load(){
		
		System.out.println();
		System.out.println("Load the data? (Y/N)");
		String QueryData = choice.next();
		
		if (QueryData.toUpperCase().equals("Y")) {
		
			try {	
				//Load all data to the 3 tables
				loadBook(connection);
                loadCustomer(connection);
				loadOrder(connection);
				System.out.println("All tables are loaded");
				
				System.out.println();
				printMain();
			} catch (SQLException | IOException ex) {
				System.out.println("Cannot load data.");
			}	
		} else {
			printMain();
		}
	}

	static void loadBook(Connection conn) throws SQLException, FileNotFoundException, IOException {
	    PreparedStatement pstmt = conn.prepareStatement("INSERT INTO book (ISBN, Title, Authors, Price, Inventory_Quantity) VALUES (?, ?, ?, ?, ?)");
	    String filename = path_to_txt + "/book.txt";
	    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	        String line;

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            pstmt.setString(1, data[0]);
	            pstmt.setString(2, data[1]);
	            pstmt.setString(3, data[2]);
	            pstmt.setInt(4, Integer.parseInt(data[3])); // Price is an integer
	            pstmt.setInt(5, Integer.parseInt(data[4])); // inventory quantity is an integer
	            pstmt.executeUpdate();
	        }
		    System.out.println("Book data loaded successfully.");
	    } catch (SQLException |IOException e) {
	        e.printStackTrace();
	    }
	}

	static void loadCustomer(Connection conn) throws SQLException, FileNotFoundException, IOException {
		String filename = path_to_txt + "/customer.txt";
	    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	        String line;
	        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customer (UID, Name, Address) VALUES (?, ?, ?)");

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            pstmt.setString(1, data[0]); // UID is a string
	            pstmt.setString(2, data[1]); // Customer Name is a string
	            pstmt.setString(3, data[2]); // Address is a string
	            pstmt.executeUpdate();
	        }
		    System.out.println("Customer data loaded successfully.");
	    } catch (SQLException | IOException e) {
	        e.printStackTrace();
	    }
	}

	static void loadOrder(Connection conn) throws SQLException, FileNotFoundException, IOException {
		String filename = path_to_txt + "/order.txt";
	    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	        String line;
	        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO orders (OID, UID, Order_Date, Order_ISBN, Order_Quantity, Shipping_Status) VALUES (?, ?, ?, ?, ?, ?)");

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            pstmt.setString(1, data[0]); // OID is a string
	            pstmt.setString(2, data[1]); // UID is a string
	            pstmt.setString(3, data[2]); // OrderDate is a string
	            pstmt.setString(4, data[3]); // OrderISBN is a string
	            pstmt.setInt(5, Integer.parseInt(data[4])); // OrderQuantity is an integer
	            pstmt.setString(6, data[5]); // ShippingStatus is a string
	            pstmt.executeUpdate();
	        }
	        System.out.println("Orders data loaded successfully.");
	    } catch (SQLException | IOException e) {
	        e.printStackTrace();
	    }
	}
	
	// 1.3. Delete Table
	
	static void deleteAllTables() {
		
		System.out.println();
		System.out.println("Drop all the tables? (Y/N)");
		String QueryDrop = choice.next();
		
		if (QueryDrop.toUpperCase().equals("Y")) {
			
			try {
				for (int i = 0; i < dbTableColumn.length; i++) {
					PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS " + dbTableColumn[i]);
					stmt.execute();
				}
				System.out.println("Deleted the tables.");
				
				printMain();
				
			} catch(Exception x) {
				System.err.println("Cannot Delete Table!");
			}
		} else {
			printMain();
		}
	}
	
	// 2.1. Book Search
	
	static void Search() {
		System.out.println();
		System.out.println("Search book by:");
		System.out.println("---------------");
		System.out.println(">1. ISBN");
		System.out.println(">2. Book Title");
		System.out.println(">3. Author Name");
		System.out.println(">4. Back");
		String Query3 = choice.nextLine();
		
		if (Query3.equals("1")) {
			System.out.println();
			System.out.print("Please Enter the ISBN: ");
			
			String Query4 = choice.nextLine();
			
			String query = "select * from book where ISBN = " + "\"" + Query4 + "\"";
			//Prevent SQL Injection
			if (Query4 != "" || Query4.contains("=")) {
				try {
					Statement statement = connection.createStatement();
					ResultSet result = statement.executeQuery(query);
					System.out.println();
					System.out.println("Book information:");
					
					while (result.next()) {
						System.out.print("ISBN: " + result.getString("ISBN"));
						System.out.print("	Title: " + result.getString("Title"));
						System.out.print("	Authors: " + result.getString("Authors"));
						System.out.print("	Price: " + result.getInt("Price"));
						System.out.println("	Inventory Quantity: " + result.getInt("Inventory_Quantity"));
					}			
					
					System.out.println();
					Search();
					
				} catch(Exception x) {
					System.err.println("Cannot print data!");
				}
			} else {
				Search();
			}
		} else if (Query3.equals("2")) {
			System.out.println();
			System.out.print("Please Enter the Title: ");
			
			String Query5 = choice.nextLine();
			
			String query2 = "select * from book where Title = " + "\"" + Query5 + "\"";
			//Prevent SQL Injection
			if (Query5 != "" || Query5.contains("=")) {
				try {
					Statement statement2 = connection.createStatement();
					ResultSet result2 = statement2.executeQuery(query2);
					System.out.println();
					System.out.println("Book information:");
				
					while (result2.next()) {
						System.out.print("ISBN: " + result2.getString("ISBN"));
						System.out.print("	Title: " + result2.getString("Title"));
						System.out.print("	Authors: " + result2.getString("Authors"));
						System.out.print("	Price: " + result2.getInt("Price"));
						System.out.println("	Inventory Quantity: " + result2.getInt("Inventory_Quantity"));
					}	
					
					System.out.println();
					Search();
					
				} catch(Exception x) {
					System.err.println("Cannot print data!");
				}
			} else {
				Search();
			}
		} else if(Query3.equals("3")) {
			System.out.println();
			System.out.print("Please Enter the Author Name: ");
			
			String Query6 = choice.nextLine();
					
			String dbURL3 = "jdbc:mysql://localhost:3306/People";
			String username3 = "root";
			String password3 = "root";
			String query3 = "select * from book where Authors = " + "\"" + Query6 + "\"";
			System.out.println();
			System.out.println("Book information:");
			//Prevent SQL Injection
			if (Query6 != "" || Query6.contains("=")) {
				try {
					connection = DriverManager.getConnection(dbURL3, username3, password3);
					Statement statement3 = connection.createStatement();
					ResultSet result3 = statement3.executeQuery(query3);
				
					while (result3.next()) {
						System.out.print("ISBN: " + result3.getString("ISBN"));
						System.out.print("	Title: " + result3.getString("Title"));
						System.out.print("	Authors: " + result3.getString("Authors"));
						System.out.print("	Price: " + result3.getInt("Price"));
						System.out.println("	Inventory Quantity: " + result3.getInt("Inventory_Quantity"));
					}	
					
					System.out.println();
					Search();
					
				} catch(Exception x) {
					System.err.println("Cannot print data!");
				}
			} else {
				Search();
			}
		} else {
			printMain();
		}
	}
	
	//2.2. Place an Order
	
	static void Order() {
		System.out.println();
		System.out.println("Place an Order");
		System.out.println("--------------");	
		System.out.println("Please enter the book ISBN that you want to order:");
		String Query7 = choice.next();
		System.out.println("Please enter the quantites:");
		int Query8 = choice.nextInt();
		
		String query = "select Inventory_Quantity from book where ISBN = " + "\"" + Query7 + "\"";	
		//Prevent SQL Injection
		if (Query7 != "" || Query7.contains("=")) {
			try {
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query);
				int quantity=-1;
				
				while (result.next()) {
					quantity = result.getInt("Inventory_Quantity");
				}
				
				
				if (quantity >= Query8) {

					String query2 = "update book set book.Inventory_Quantity = book.Inventory_Quantity - " + Query8 + " where ISBN = " + "\"" + Query7 + "\"";
					try {
						Statement statement2 = connection.createStatement();
						statement2.executeUpdate(query2);
						
						System.out.println("Order success.");
						System.out.println();
						System.out.println("Continue to order? (Y/N)");
						String Query11 = choice.next();
						
						if (Query11.toUpperCase().equals("Y")) {
							
							Order();
						} else {
							
							System.out.println();
							System.out.println();
							printMain();
						}
						
					} catch(Exception x) {
						System.err.println("Cannot update data!");
					}
					
					
				} else if (quantity == -1) {
					System.out.println("Order failed, ISBN not found.");
					System.out.println();
					System.out.println("Continue to order? (Y/N)");
					String Query9 = choice.next();
					
					if (Query9.toUpperCase().equals("Y")) {
						
						Order();
					} else {
						
						System.out.println();
						System.out.println();
						printMain();
					}
				} else {
					System.out.println("Order failed, inventory shortage.");
					System.out.println();
					System.out.println("Continue to order? (Y/N)");
					String Query10 = choice.next();		
					if (Query10.toUpperCase().equals("Y")) {
						
						Order();
					} else {
						
						System.out.println();
						System.out.println();
						printMain();
					}					
				}
				
			} catch(Exception x) {
				System.err.println("Cannot print data!");
			}
		} else {
			Order();
		}
	}
	
	// 2.3. Check History Orders
	
	static void History() {
		System.out.println();
		System.out.println("Check users's history orders");
		System.out.println("--------------------");
		System.out.println("Enter username:");
		String Query12 = choice.nextLine();
		
		String query = "select orders.* from customer, orders WHERE customer.UID = orders.UID AND customer.Name = " + "\"" + Query12 + "\"";	
		//Prevent SQL Injection
		if (Query12 != "" || Query12.contains("=")) {
			try {
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query);
				System.out.println();
				System.out.println("Ordered history of " + Query12 + ": ");

				while (result.next()) {
					System.out.print("OID: " + result.getString("OID"));
					System.out.print("	UID: " + result.getString("UID"));
					System.out.print("	Order Date: " + result.getDate("Order_Date"));
					System.out.print("	Order ISBN: " + result.getString("Order_ISBN"));
					System.out.print("	Order Quantity: " + result.getInt("Order_Quantity"));
					System.out.println("	Shipping Status: " + result.getString("Shipping_Status"));
				}
				
				
				System.out.println();
				printMain();
				
			} catch(Exception x) {
				System.err.println("Cannot print data!");
			}
		} else {
			History();
		}
		
	}
	
	// 3.1. Order Update
	
	static void OrderUpdate() {
		System.out.println();
		System.out.println("Order Update");
		System.out.println("------------");
		System.out.print("Please enter the Order ID: ");
		String Query13 = choice.next();
		System.out.println();
		System.out.println("Change the shipping status to: ");
		System.out.println(">1. ordered");
		System.out.println(">2. shipped");
		System.out.println(">3. received");
		System.out.println(">4. Back");
		System.out.println();
		System.out.println("Your choice: ");
		int Query14 = choice.nextInt();
		
		String query = "select orders.Shipping_Status from orders where orders.OID = " + "\"" + Query13 + "\"";
		String status = "";
		
		if (Query13.length() == 8 && (Query14 >= 1 && Query14 <= 3)) {
			
			try {
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query);
				
				while (result.next()) {
					status = result.getString("Shipping_Status");
				}
				
			} catch(Exception x) {
				System.err.println("Cannot print data!");
			}
			
			if (status.equals("ordered") && Query14 == 1) {
				System.out.println("Update failed, order has already been ordered.");
				OrderUpdate();
			} else if (status.equals("shipped") && Query14 == 2) {
				System.out.println("Update failed, order has already been shipped.");
				OrderUpdate();
			} else if (status.equals("received") && Query14 == 3) {
				System.out.println("Update failed, order has already been received.");
				OrderUpdate();
			} else {

				String ShippingStatus = "";
				
				if (Query14 == 1) {
					ShippingStatus = "ordered";
				} else if (Query14 == 2) {
					ShippingStatus = "shipped";
				} else if (Query14 == 3) {
					ShippingStatus = "received";
				}
				
				String query2 = "update orders set orders.Shipping_Status = \"" + ShippingStatus + "\" where orders.OID = \"" + Query13 + "\"";
				
				try {

					Statement statement2 = connection.createStatement();
					statement2.executeUpdate(query2);
					
					System.out.println("Shipping status update success.");
					System.out.println();
					System.out.println("Update another order? (Y/N)");
					String Query15 = choice.next();
					
					if (Query15.toUpperCase().equals("Y")) {
						
						OrderUpdate();
					} else {
						
						System.out.println();
						System.out.println();
						printMain();
					}
					
				} catch(Exception x) {
					System.err.println("Cannot update data!");
				}
			}		
		} else {
			System.out.println();
			System.out.println("Wrong input, please try again.");
			OrderUpdate();
		}	
	}
	
	// 3.2. Order Query
	
	static void OrderQuery() {
		System.out.println();
		System.out.println("Order Query");
		System.out.println("---------------------------------");
		System.out.println("Search other by shipping status: ");
		System.out.println(">1. ordered");
		System.out.println(">2. shipped");
		System.out.println(">3. received");
		System.out.println();
		System.out.print("Your choice: ");
		int Query16 = choice.nextInt();
		System.out.println("");
		
		if (Query16 >= 1 && Query16 <=3) {
			String status2 = "";
			
			if (Query16 == 1) {
				status2 = "ordered";
			} else if (Query16 == 2) {
				status2 = "shipped";
			} else if (Query16 == 3) {
				status2 = "received";
			}
			
			System.out.println("Orders grouped by shipping status: ");

			String query = "select * from orders where Shipping_Status = \"" + status2 + "\"";
			
			try {
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query);
				
				System.out.println();
				System.out.println("Orders information:");
				
				while (result.next()) {
					System.out.print("OID: " + result.getString("OID"));
					System.out.print("	UID: " + result.getString("UID"));
					System.out.print("	Order Date: " + result.getDate("Order_Date"));
					System.out.print("	Order ISBN: " + result.getString("Order_ISBN"));
					System.out.print("	Order Quantity: " + result.getInt("Order_Quantity"));
					System.out.println("	Shipping Status: " + result.getString("Shipping_Status"));
				}
				
				System.out.println();
				System.out.println("Continue to query orders? (Y/N)");
				String Query17 = choice.next();
				
				if (Query17.toUpperCase().equals("Y")) {
					
					OrderQuery();
				} else {
					
					System.out.println();
					System.out.println();
					printMain();
				}
				
			} catch(Exception x) {
				System.err.println("Cannot update data!");
			}	
		} else {
			System.out.println("Wrong input, please try again.");
			OrderQuery();
		}	
	}
	
	// 3.3. N Most Popular Books
	
	static void NPopular() {
		System.out.println();
		System.out.print("Search for the N most popular books: ");
		int Query18 = choice.nextInt();
		
		if (Query18 == (int)Query18) {
			System.out.println();
			System.out.println("The " + Query18 + " most popular books are: ");

			String query = "select book.*, SUM(Order_Quantity) from book, orders where book.ISBN = orders.Order_ISBN group by Order_ISBN order by SUM(Order_Quantity) DESC limit " + Query18;
			
			try {
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query);
				System.out.println();
				
				while (result.next()) {
					System.out.print("ISBN: " + result.getString("ISBN"));
					System.out.print("	Title: " + result.getString("Title"));
					System.out.print("	Authors: " + result.getString("Authors"));
					System.out.print("	Price: " + result.getInt("Price"));
					System.out.print("	Inventory Quantity: " + result.getInt("Inventory_Quantity"));
					System.out.println("	Order Quantity sum: " + result.getInt("SUM(Order_Quantity)"));
				}
				
				String create = "Create view bookorder AS select book.*, SUM(Order_Quantity) from book, orders where book.ISBN = orders.Order_ISBN group by Order_ISBN order by SUM(Order_Quantity) DESC limit " + Query18;
				
				try {
					Statement statement2 = connection.createStatement();
					statement2.execute(create);
				} catch (Exception x) {
					System.err.println("Cannot create view!");
				}
				
				String selectview = "select count(*) from bookorder";
				int viewnum = 0;
				int book0 = 0;
				
				try {
					Statement statement3 = connection.createStatement();
					ResultSet result2 = statement3.executeQuery(selectview);
					
					result2.next();
					viewnum = result2.getInt("count(*)");
					
				} catch (Exception x) {
					System.err.println("Cannot select data!");
				}
				
				book0 = Query18 - viewnum;
				
				String delete = "Drop view bookorder";
				
				try {
					Statement statement4 = connection.createStatement();
					statement4.execute(delete);
				} catch (Exception x) {
					System.err.println("Cannot delete view!");
				}	
				
				String create2 = "Create table noorder AS SELECT ISBN FROM book EXCEPT select Order_ISBN from orders;";
				
				try {
					Statement statement5 = connection.createStatement();
					statement5.execute(create2);
				} catch (Exception x) {
					System.err.println("Cannot create table!");
				}
				
				if (book0 > 0) {
				
					String merge = "select book.* from noorder, book where noorder.ISBN = book.ISBN limit " + book0;
				
					try {
						Statement statement6 = connection.createStatement();
						ResultSet result3 = statement6.executeQuery(merge);
					
						while (result3.next()) {
							System.out.print("ISBN: " + result3.getString("ISBN"));
							System.out.print("	Title: " + result3.getString("Title"));
							System.out.print("	Authors: " + result3.getString("Authors"));
							System.out.print("	Price: " + result3.getInt("Price"));
							System.out.print("	Inventory Quantity: " + result3.getInt("Inventory_Quantity"));
							System.out.println("	Order Quantity sum: 0");
						}		
					} catch (Exception x) {
						System.err.println("Cannot print data!");
					}
				}
				
				String delete2 = "Drop table noorder";
				
				try {
					Statement statement7 = connection.createStatement();
					statement7.execute(delete2);
				} catch (Exception x) {
					System.err.println("Cannot delete view!");
				}
				
				System.out.println();
				System.out.println("Continue to search N popular books? (Y/N)");
				String Query19 = choice.next();
				
				if (Query19.toUpperCase().equals("Y")) {
					
					NPopular();
				} else {
					
					System.out.println();
					System.out.println();
					printMain();
				}
				
			} catch(Exception x) {
				System.err.println("Cannot update data!");
			}			
			
		} else {
			System.out.println("Wrong input, please try again.");
			NPopular();
		}
	}
	
	// Main Interface
	
	static void printMain() {
		System.out.println();
		System.out.println("===== Welcome to Book Ordering Management System =====");
		
		// 4.1. Print current time and date.
		
		System.out.print(" + System Date and Time: ");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		
		// 4.2. Print an overview of all the database records.
		
		System.out.print(" + Database Records: ");

		String countBook = "select count(*) from book";
		String countCustomer = "select count(*) from customer";
		String countOrders = "select count(*) from orders";
		
		try {
		Statement statement = connection.createStatement();
		
		ResultSet resultBook = statement.executeQuery(countBook);
		resultBook.next();
		System.out.print("Books(" + resultBook.getInt("count(*)") + "), ");
		
		ResultSet resultCustomer = statement.executeQuery(countCustomer);
		resultCustomer.next();
		System.out.print("Customers(" + resultCustomer.getInt("count(*)") + "), ");
		
		ResultSet resultOrders = statement.executeQuery(countOrders);
		resultOrders.next();
		System.out.println("Orders(" + resultOrders.getInt("count(*)") + ") ");
		
		} catch(Exception x) {
			System.err.println("You should create the tables first.");
		}
		
		System.out.println("------------------------------------------------------");
		System.out.println(">1. Database Initialization");
		System.out.println(">2. Customer Operation");
		System.out.println(">3. Bookstore Operation");
		System.out.println(">4. Quit");
		System.out.print(">>> Please Enter Your Query: ");
		String Query = choice.nextLine();
		
		if (Query.equals("1")) {
			System.out.println();
			System.out.println("Data Initialization");
			System.out.println("-------------------");
			System.out.println(">1. Create Table");
			System.out.println(">2. Load from Data");
			System.out.println(">3. Delete all Data");
			System.out.println(">4. Back");
			System.out.print(">>> Please Enter Your Query: ");
			String Query1 = choice.nextLine();
			
			if (Query1.equals("1")) {
				createTable();
			} else if(Query1.equals("2")) {		
				load();
			} else if(Query1.equals("3")) {
				deleteAllTables();
			} else if (Query1.equals("4")){
				printMain();
			} else {
				printMain();
			}
			
		} else if (Query.equals("2")) {
			System.out.println();
			System.out.println("Customer Operation");
			System.out.println("------------------");
			System.out.println(">1. Book Search");
			System.out.println(">2. Place an Order");
			System.out.println(">3. Check History Orders");
			System.out.println(">4. Back");
			System.out.print(">>> Please Enter Your Query: ");
			String Query2 = choice.nextLine();
			
			if (Query2.equals("1")) {
				Search();
			} else if(Query2.equals("2")) {		
				Order();
			} else if(Query2.equals("3")) {
				History();
			} else if (Query2.equals("4")){
				printMain();
			} else {
				printMain();
			}
			
		} else if (Query.equals("3")) {
			System.out.println();
			System.out.println("Bookstore Operation");
			System.out.println("-------------------");
			System.out.println(">1. Order Update");
			System.out.println(">2. Order Query");
			System.out.println(">3. N Most Popular Books");
			System.out.println(">4. Back");
			System.out.print(">>> Please Enter Your Query: ");
			String Query3 = choice.nextLine();
			
			if (Query3.equals("1")) {
				OrderUpdate();
			} else if(Query3.equals("2")) {		
				OrderQuery();
			} else if(Query3.equals("3")) {
				NPopular();
			} else if (Query3.equals("4")){
				printMain();
			} else {
				printMain();
			}
			
	    } else if (Query.equals("4")) {
	    	choice.close();
	    	System.exit(0);
	    	
	    } else {
			printMain();
		}
    }
	
	static void FileCheck() {
		book_txt = path_to_txt + "/book.txt";
		orders_txt = path_to_txt + "/order.txt";
		customer_txt = path_to_txt + "/customer.txt";
		boolean book = new File(book_txt).isFile();
		boolean orders = new File(orders_txt).isFile();
		boolean customer = new File(customer_txt).isFile();
		if (!book || !orders || !customer) {
			System.err.println("You input a wrong directory");
			System.out.println("You need to restart the system!");
			System.exit(0);
		}
		
		
	}

	

	public static void main(String[] args) {
		System.out.println("Enter your JDBC address");
		dbURL = scanner.nextLine();
		System.out.println("Enter your JDBC User Name");
		username = scanner.nextLine();
		System.out.println("Enter your JDBC Password");
		password = scanner.nextLine();
		System.out.println("Enter the CORRECT PATH to the txt files containing the data, e.g. /home/data.txt -> input /home");
		path_to_txt = scanner.nextLine();
		FileCheck();
		try {
			Class.forName("com.mysql.jdbc.Driver");		
		} catch(Exception x) {
			System.err.println("Unable to load the driver class!");
		}	
			try {
				connection = DriverManager.getConnection(dbURL, username, password);
			} catch (SQLException e) {
				System.err.println("Unable to Connect to Database!");
				System.out.println("You need to restart the system!");
				System.exit(0);
			}

			printMain();
}
}
