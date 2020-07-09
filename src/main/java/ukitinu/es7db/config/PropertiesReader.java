package ukitinu.es7db.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesReader.class);

    private static final String PUBLIC_PROP_FILENAME = "es7db.properties";
    private static final String SYSTEM_EXIT = "Failed to read " + PUBLIC_PROP_FILENAME;

    private static final Properties properties = new Properties();

    static {
        try (InputStream is = getConfigurationFileStream()) {
            properties.load(is);
            loadPropertyValues();
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

    private static void loadPropertyValues()
    {
        for (Property property : Property.values()) {
            loadPropertyValue(property);
        }
    }

    private static void loadPropertyValue(Property property)
    {
        String name = property.getName();
        String value = properties.getProperty(name);
        property.setValue(value);
    }
}
