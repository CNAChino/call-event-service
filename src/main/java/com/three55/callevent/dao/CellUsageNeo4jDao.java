package com.three55.callevent.dao;

import com.three55.callevent.AppConfig;
import com.three55.callevent.model.CellAverageUsage;
import com.three55.callevent.model.CellUsage;
import com.three55.callevent.utils.NetworkType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.AuthenticationException;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CellUsageNeo4jDao implements CellUsageDao, Closeable {

    private static final Logger logger = LogManager.getLogger(CellUsageNeo4jDao.class);

    @Autowired
    public AppConfig config;

    /**
     * Neo4j Driver
     */
    private Driver driver;

    /**
     * Uri to connect to Neo4j server
     */
    private String uri;

    /**
     * User to connect to Neo4j server
     */
    private String user;

    /**
     * password to connect to Neo4j server
     */
    private String password;

    /**
     * maximum lifespan of a pooled connection
     */
    private int maxConnectionLifetimeInMinutes = 30;

    /**
     * maximum number of connection maintained per neo4j server
     */
    private int maxConnectionPoolSize = 5;

    /**
     * maximum wait timer for a free connection
     */
    private int connectionAcquisitionTimeoutInMinutes = 2;

    /**
     * maximum time to keep retrying
     */
    private int maxTransactionRetryTimeInSecons = 15;

    /**
     * Initialize.  Configure driver.
     */
    public void init()
    {
        logger.info("Initializing");
        this.user = this.config.getNeo4jUser();
        this.uri = this.config.getNeo4jUri();
        this.password = this.config.getNeo4jPassword();


        if (uri == null) throw new NullPointerException("Null uri");

        if (user == null) throw new NullPointerException("Null user");

        if (password == null) throw new NullPointerException("Null password");

        Config config = Config.builder()
                .withMaxConnectionLifetime( maxConnectionLifetimeInMinutes, TimeUnit.MINUTES )
                .withMaxConnectionPoolSize( maxConnectionPoolSize )
                .withConnectionAcquisitionTimeout( connectionAcquisitionTimeoutInMinutes, TimeUnit.MINUTES )
                .withConnectionTimeout( 5, TimeUnit.SECONDS )
                .withMaxTransactionRetryTime( maxTransactionRetryTimeInSecons, TimeUnit.SECONDS )
                .build();


        try {
            logger.info("Driver connecting to " + uri);
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Initializing done");
    }

    public void destroy()  {
        logger.info("Disconnecing");
        try {
            this.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Disconneced");
    }


    /**
     * Store cell usage data
     *
     * @param cellUsage
     * @return id of relationship between Cellphone and Telco node
     */
    @Override
    public String saveCellphoneUsage(CellUsage cellUsage) {
        logger.debug("Saving Cellphone usage");
        String stmt = "MERGE (phone:Cellphone {brand : $BRAND, model : $MODEL}) " +
            "MERGE (telco:Telco {name : $TELCO_NAME, mcc : $MCC, mnc : $MNC}) " +
            "CREATE (phone)-[usedIn:USED_IN]->(telco) " +
            "  SET usedIn.date = date(), usedIn.SignalDB = $SIGNAL_DB, usedIn.networkType = $NETWORK_TYPE " +
            "RETURN id(phone)";

        Map<String, Object> params = new HashMap<>();
        params.put("BRAND", cellUsage.getDeviceBrand());
        params.put("MODEL", cellUsage.getDeviceModel());
        params.put("TELCO_NAME", cellUsage.getNetworkName());
        params.put("MCC", cellUsage.getMcc());
        params.put("MNC", cellUsage.getMnc());
        params.put("SIGNAL_DB", cellUsage.getDeviceSignalDb());
        params.put("NETWORK_TYPE", NetworkType.toString(cellUsage.getNetworkType()));

        try ( Session session = driver.session() ) {
            // usageId is id of relationship between Cellphone and Telco node
            logger.debug("Running statement: " + stmt);
            String usageId = session.writeTransaction(tx -> {
                StatementResult result = tx.run( stmt, params );
                int id = result.single().get( 0 ).asInt();
                return id+"";
            });
            return usageId;
        } catch ( ServiceUnavailableException ex){
            logger.error(ex.getMessage(), ex);
        } catch ( Exception e ) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     *
     *
     * @param deviceBrand
     * @param networkName
     */
    @Override
    public int deleteCellPhoneUsage(String deviceBrand, String networkName) {
        logger.debug("Deleting Cellphone usage");
        String stmt = "MATCH (p:Cellphone {brand : $BRAND}) MATCH (t:Telco {name :  $TELCO_NAME}) " +
                "DETACH DELETE (p) DETACH DELETE (t)";

        Map<String, Object> params = new HashMap<>();
        params.put("BRAND", deviceBrand);
        params.put("TELCO_NAME", networkName);

        try ( Session session = driver.session() ) {
            logger.debug("Running statement: " + stmt);
            return session.run(stmt, params).summary().counters().nodesDeleted();

        } catch ( ServiceUnavailableException ex){
            logger.error(ex.getMessage(), ex);
        } catch ( Exception e ) {
            logger.error(e.getMessage(), e);
        }

        return 0;
    }

    /**
     * get the cellphone usage by brand return the number of occurences it has been used
     * and the average signal strength.
     *
     * @param deviceBrand
     * @return
     */
    @Override
    public CellAverageUsage getCellphoneAverageUsage(String deviceBrand) {
        logger.debug("Getting Cellphone average usage");
        String stmt =
                "MATCH (c:Cellphone {brand : $BRAND})-[usedIn:USED_IN]->(:Telco) " +
                "RETURN c.brand AS BRAND, count(usedIn) AS USAGE_COUNT, " +
                        "avg(usedIn.SignalDB) AS AVG_SIGNAL_STRENGTH";

        Map<String, Object> params = new HashMap<>();
        params.put("BRAND", deviceBrand);

        try ( Session session = driver.session() ) {
            StatementResult result = session.run(stmt, params);
            Record record = result.next();
            CellAverageUsage acu = new CellAverageUsage();
            acu.setBrand(record.get( "BRAND" ).asString());
            acu.setModel(record.get( "MODEL" ).asString());
            acu.setUsageCount(record.get( "USAGE_COUNT" ).asInt());
            acu.setAvgSignalStrength(record.get( "AVG_SIGNAL_STRENGTH" ).asFloat());
            logger.debug("CellAverageUsage usageCount = " + acu.getUsageCount());
            return acu;
        } catch ( ServiceUnavailableException ex){
            logger.error(ex.getMessage(), ex);
        } catch ( Exception e ) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxConnectionLifetimeInMinutes() {
        return maxConnectionLifetimeInMinutes;
    }

    public void setMaxConnectionLifetimeInMinutes(int maxConnectionLifetimeInMinutes) {
        this.maxConnectionLifetimeInMinutes = maxConnectionLifetimeInMinutes;
    }

    public int getMaxConnectionPoolSize() {
        return maxConnectionPoolSize;
    }

    public void setMaxConnectionPoolSize(int maxConnectionPoolSize) {
        this.maxConnectionPoolSize = maxConnectionPoolSize;
    }

    public int getConnectionAcquisitionTimeoutInMinutes() {
        return connectionAcquisitionTimeoutInMinutes;
    }

    public void setConnectionAcquisitionTimeoutInMinutes(int connectionAcquisitionTimeoutInMinutes) {
        this.connectionAcquisitionTimeoutInMinutes = connectionAcquisitionTimeoutInMinutes;
    }

    public int getMaxTransactionRetryTimeInSecons() {
        return maxTransactionRetryTimeInSecons;
    }

    public void setMaxTransactionRetryTimeInSecons(int maxTransactionRetryTimeInSecons) {
        this.maxTransactionRetryTimeInSecons = maxTransactionRetryTimeInSecons;
    }

    public AppConfig getConfig() {
        return config;
    }

    public void setConfig(AppConfig config) {
        this.config = config;
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing driver");
        driver.close();
    }
}
