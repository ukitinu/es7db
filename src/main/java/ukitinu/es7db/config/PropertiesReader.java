package ukitinu.es7db.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

final class PropertiesReader
{
    private PropertiesReader()
    {
        throw new IllegalStateException("Initialisation-only class");
    }

    private static final Logger LOG = LogManager.getLogger(PropertiesReader.class);

    private static final String PUBLIC_PROP_FILENAME = "es7db.properties";
    private static final String SYSTEM_EXIT = "Failed to read " + PUBLIC_PROP_FILENAME;

    private static final Properties properties = new Properties();

    static {
        try (InputStream is = getConfigurationFileStream()) {
            properties.load(is);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            LOG.error(SYSTEM_EXIT);
            System.exit(-1);
        }
    }

    private static InputStream getConfigurationFileStream() throws FileNotFoundException
    {
        return new FileInputStream(PUBLIC_PROP_FILENAME);
    }

    static String getValue(String name)
    {
        return properties.getProperty(name);
    }
}
