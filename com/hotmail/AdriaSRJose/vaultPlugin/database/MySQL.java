package com.hotmail.AdriaSRJose.vaultPlugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.kits.Kit;

public class MySQL {
	private final String host;
	private final String user;
	private final String database;
	private final String password;
	private final int port;

	private Connection connection = null;

	public MySQL(String host, String user, String database, String password, int port) {
		this.host = host;
		this.user = user;
		this.database = database;
		this.password = password;
		this.port = port;
	}

	public MySQL(BaseData data) {
		this.host = data.getHost();
		this.user = data.getUser();
		this.database = data.getDatabase();
		this.password = data.getPassword();
		this.port = data.getPort();
	}

	public Connection OpenConnection() {
		try {
			// Check Class
			Class.forName("com.mysql.jdbc.Driver");

			// Connect
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user,
					password);

			// Print Connected Messaeg
			Bukkit.getConsoleSender().sendMessage("§a[AnnihilationVault] Connected to MySQL");

			// Verify Table
			verifyTables();
		} catch (ClassNotFoundException e) {
			Bukkit.getConsoleSender().sendMessage(
					"§c[AnnihilationVault] Could not connect to MySQL server because: JDBC Driver Not Found");
		} catch (SQLException e) {
			Bukkit.getConsoleSender()
					.sendMessage("§c[AnnihilationVault] Could not connect to MySQL server because: ");
		}
		return connection;
	}

	public boolean Cc() {
		try {
			if (getConnection() == null || getConnection().isClosed()) {
				Connection c = OpenConnection();
				if (c == null) {
					return false;
				}
			}
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	public boolean CheckConnection() {
		return connection != null;
	}

	public Connection getConnection() {
		return connection;
	}

	public void CloseConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				Bukkit.getConsoleSender().sendMessage("§c[AnnihilationVault] Error on Close of the MySQL Connection (The Shop could not be enabled)");
			}
		}
	}

	public ResultSet QuerySQL(String query) throws SQLException {
		Connection c = null;
		Statement s = null;
		ResultSet res = null;

		if (connection != null) {
			c = getConnection();
		} else {
			c = OpenConnection();
		}

		try {
			s = c.createStatement();
			res = s.executeQuery(query);
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("§cError on Execute Query: ");
			e.printStackTrace();
		}
		return res;
	}

	public ResultSet query(String query) throws SQLException {
		return QuerySQL(query);
	}

	public void UpdateSQL(String update) throws SQLException {
		Connection c = null;
		Statement s = null;

		if (connection != null) {
			c = this.getConnection();
		} else {
			c = this.OpenConnection();
		}

		try {
			s = c.createStatement();
			s.executeUpdate(update);
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("§cError on Execute Update: ");
			e.printStackTrace();
		}
	}

	public void update(String up) throws SQLException {
		UpdateSQL(up);
	}

	public boolean existsTable(String table) {
		if (connection == null) {
			return false;
		}

		try {
			ResultSet tables = connection.getMetaData().getTables(null, null, table, null);
			return tables.next();
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("§cFailed to check if table " + table + " exists: " + e.getMessage());
			return false;
		}
	}

	public boolean existsColumn(String table, String column) {
		if (connection == null) {
			return false;
		}

		try {
			if (connection.isClosed()) {
				return false;
			}
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		try {
			ResultSet col = getConnection().getMetaData().getColumns(null, null, table, column);
			return col.next();
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(
					"Failed to check if column " + column + " exists in table " + table + " : " + e.getMessage());
			return false;
		}
	}

	private static final String TABLE = "tbl_vault_player_anni_kits";
	public boolean verifyTables() {
		try {
			if (getConnection() == null || getConnection().isClosed()) {
				return false;
			}
		} catch (SQLException e) {
		}

		try {
			update("CREATE TABLE IF NOT EXISTS " + TABLE + " (ID VARCHAR(40), Kit VARCHAR(20))");
		} catch (SQLException ParamException) {
			ParamException.printStackTrace();
			return false;
		}

		return true;
	}

	public void loadKits(final Player p) {
		final List<String> kits = new ArrayList<String>();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap == null) {
			return;
		}

		try {
			ResultSet set = query("SELECT * FROM " + TABLE + " WHERE ID='" + ap.getID() + "';");
			if (set != null) {
				while (set.next()) {
					kits.add(set.getString("Kit").toLowerCase());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Set Data
		ap.setData("Vault-Kits", kits);
	}

	@SuppressWarnings("unchecked")
	public void addKit(final UUID id, final Kit kit) {
		// Get and Check Player
		final AnniPlayer player = AnniPlayer.getPlayer(id);
		if (player == null) {
			return;
		}

		// Get and check Obj
		Object obj = player.getData("Vault-Kits");
		if (obj == null || !(obj instanceof List)) {
			obj = new ArrayList<String>();
		}
		
		// Add to List
		((List<String>) obj).add(kit.getName().toLowerCase());
	
		// Update Data
		player.setData("Vault-Kits", obj);

		// Insert in the table
		try {
			update("INSERT INTO " + TABLE + " (ID, Kit) VALUES ('" + id.toString() + "', '" + kit.getName() + "');");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}