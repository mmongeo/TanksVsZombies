package cr.ac.ucr.ecci.ci2354.TanksvsZombies.data;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Esta clase fue tomada de un ejemplo de ORMLite. Genera un archivo de configuración que la base de
 * datos necesita.
 * 
 * Database helper class used to manage the creation and upgrading of your database. This class also
 * usually provides the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt");
	}
}
