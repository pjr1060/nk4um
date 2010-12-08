package uk.org.onegch.netkernel.liquibase;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ResourceAccessor;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.util.RequestScopeClassLoader;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public abstract class AbstractLiquibaseAccessor extends StandardAccessorImpl {
  protected Liquibase createLiquibase(INKFRequestContext aContext) throws Exception {
    ClassLoader cl= new RequestScopeClassLoader(aContext.getKernelContext().getThisKernelRequest().getRequestScope());
    ResourceAccessor ra= new NetKernelResourceAccessor(aContext, cl);
    
    IHDSNode configNode= aContext.source("res:/etc/system/LiquibaseConfig.xml", IHDSNode.class);
    
    Database database = createDatabaseObject(cl,
                                             (String)configNode.getFirstValue("//jdbcDriver"),
                                             (String)configNode.getFirstValue("//jdbcConnection"),
                                             (String)configNode.getFirstValue("//user"),
                                             (String)configNode.getFirstValue("//password"));
    Liquibase liquibase = new Liquibase((String)configNode.getFirstValue("//changelog"),
                                        ra,
                                        database);
    
    return liquibase;
  }
  
  private Database createDatabaseObject(ClassLoader cl,
                                        String driverClassName,
                                        String databaseUrl,
                                        String username,
                                        String password) throws Exception {
    
    if (driverClassName == null) {
        driverClassName = DatabaseFactory.getInstance().findDefaultDriver(databaseUrl);
    }
    
    if (driverClassName == null) {
        throw new Exception("driver not specified and no default could be found for " + databaseUrl);
    }
    
    Driver driver = (Driver) Class.forName(driverClassName, true, cl).newInstance();
    
    Properties info = new Properties();
    if (username != null) {
      info.put("user", username);
    }
    if (password != null) {
      info.put("password", password);
    }
    
    Connection connection = driver.connect(databaseUrl, info);
    
    if (connection == null) {
        throw new Exception("Connection could not be created to " +
                            databaseUrl +
                            " with driver " +
                            driver.getClass().getName() +
                            ".  Possibly the wrong driver for the given database URL");
    }
    
    return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
  }
}
