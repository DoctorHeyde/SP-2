package app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import app.entities.Event;
import app.entities.User;
import app.entities.Role;
import app.exceptions.ApiException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import jakarta.persistence.EntityManagerFactory;

public class HibernateConfig {

    private static EntityManagerFactory instace;
    private static String dbName;

    public static EntityManagerFactory getEntityManagerFactory(boolean testing) {
        if (testing) {
            dbName = "testdb";
            if (instace == null) {
                instace = setupHibernateConfigurationForTesting();
            }
            return instace;
        } else {
            if (instace == null) {
                dbName = getDBName();
                instace = buildEntityFactoryConfig();
            }
            return instace;
        }
    }

    private static EntityManagerFactory buildEntityFactoryConfig() {
        try {
            Configuration configuration = new Configuration();

            Properties props = new Properties();
            String connctionURL = String.format("jdbc:postgresql://localhost:5432/%s?currentSchema=public", dbName);
            props.put("hibernate.connection.url", connctionURL);
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.show_sql", "true"); // show sql in console
            props.put("hibernate.format_sql", "true"); // format sql in console
            props.put("hibernate.use_sql_comments", "true"); // show sql comments in console

            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // dialect for postgresql
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver"); // driver class for postgresql
            props.put("hibernate.archive.autodetection", "class"); // hibernate scans for annotated classes
            props.put("hibernate.current_session_context_class", "thread"); // hibernate current session context
            props.put("hibernate.hbm2ddl.auto", "update"); // hibernate creates tables based on entities

            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory setupHibernateConfigurationForTesting() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
            props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test-db");
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.archive.autodetection", "class");
            // props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "create-drop");
            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory getEntityManagerFactory(Configuration configuration, Properties props) {
        configuration.setProperties(props);

        getAnnotationConfiguration(configuration);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        System.out.println("Hibernate Java Config serviceRegistry created");

        SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
        return sf.unwrap(EntityManagerFactory.class);
    }

    private static void getAnnotationConfiguration(Configuration configuration) {
        // add annotated classes
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(Event.class);

    }

    private static String getDBName() {
        return Utils.getPropertyValue("db.name", "properties-from-pom.properties");
    }

    /**
     * Purpose: Utility class to read properties from a file
     * Author: Thomas Hartmann
     */
    public class Utils {
        public static String getPropertyValue(String propName, String ressourceName) {
            // REMEMBER TO BUILD WITH MAVEN FIRST. Read the property file if not deployed
            // (else read system vars instead)
            // Read from ressources/config.properties or from pom.xml depending on the
            // ressourceName
            try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(ressourceName)) { // "config.properties"
                // or
                // "properties-from-pom.properties"
                Properties prop = new Properties();
                prop.load(is);
                return prop.getProperty(propName);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new ApiException(500, String.format(
                        "Could not read property %s. Did you remember to build the project with MAVEN?", propName));
            }
        }
    }
}