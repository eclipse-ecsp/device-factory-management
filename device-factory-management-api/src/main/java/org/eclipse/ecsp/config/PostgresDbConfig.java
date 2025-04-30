/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import jakarta.annotation.PostConstruct;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * Configuration class for Postgres database.
 * This class provides the configuration for the Postgres database connection pool.
 */
@Configuration
@EnableScheduling
public class PostgresDbConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDbConfig.class);
    private static final int VALUE_50 = 50;
    
    @Autowired
    private EnvConfig<DeviceInfoQueryProperty> envConfig;
    private volatile boolean isRefreshInProgress = false;
    private String userName;
    private String pswd;

    /**
     * Value of serviceComponentName from deviceinfoquery-api.yaml under vault.json section.
     * Refer /hcp-deviceinfoquery-webapp/src/deploy/kubernetes/helm/deviceinfoquery/templates/deviceinfoquery-api.yaml
     * file
     */

    private DataSource dataSource = null;

    /**
     * Number of Connections a pool will try to acquire upon startup. Should be between minPoolSize and maxPoolSize.
     * Default: 3
     */
    private int initialPoolSize;

    /**
     * Minimum number of Connections a pool will maintain at any given time.
     * Default: 3
     */
    private int minPoolSize;

    /**
     * Maximum number of Connections a pool will maintain at any given time.
     */
    private int maxPoolSize;

    /**
     * Seconds a Connection can remain pooled but unused before being discarded. Zero means idle connections never
     * expire.
     * In second, after that time it will release the unused connections
     */
    private int maxIdleTime;

    /**
     * Determines how many connections at a time c3p0 will try to acquire when the pool is exhausted.
     */
    private int acquireIncrement;

    /**
     * If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this
     * number of seconds.
     */
    private int idleConnectionTestPeriod;

    /**
     * Creates and returns the data source for the Postgres database.
     *
     * @return the data source
     */
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataSource dataSource() {
        LOGGER.debug("## In dataSource creation method......................");
        while (isRefreshInProgress) {
            LOGGER.info("Datasource refresh in progress");
            // Sleep the thread for milli secs and again check the progress
            // status.
            try {
                Thread.sleep(VALUE_50);
            } catch (InterruptedException e) {
                LOGGER.error("## DataSource refresh thread interrupted......");
            }
        }
        if (null == dataSource) {
            throw new RuntimeException("## DataSource was not set properly.");
        }
        return dataSource;
    }

    /**
     * Loads the Postgres properties from the environment configuration.
     */
    @PostConstruct
    private void loadPostgresProperties() {
        initialPoolSize = envConfig.getIntegerValue(DeviceInfoQueryProperty.INITIAL_POOL_SIZE);
        minPoolSize = envConfig.getIntegerValue(DeviceInfoQueryProperty.MIN_POOL_SIZE);
        maxPoolSize = envConfig.getIntegerValue(DeviceInfoQueryProperty.MAX_POOL_SIZE);
        maxIdleTime = envConfig.getIntegerValue(DeviceInfoQueryProperty.MAX_IDLE_TIME);
        acquireIncrement = envConfig.getIntegerValue(DeviceInfoQueryProperty.ACQUIRE_INCREMENT);
        idleConnectionTestPeriod = envConfig.getIntegerValue(DeviceInfoQueryProperty.IDLE_CONNECTION_TEST_PERIOD);
        userName = envConfig.getStringValue(DeviceInfoQueryProperty.POSTGRES_USERNAME);
        pswd = envConfig.getStringValue(DeviceInfoQueryProperty.POSTGRES_PASSWORD);
        dataSource = refreshdataSource();

    }

    /**
     * Refreshes the data source for the Postgres database.
     *
     * @return the refreshed data source
     */
    private DataSource refreshdataSource() {
        cleanupDataSource(dataSource);
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(envConfig.getStringValue(DeviceInfoQueryProperty.POSTGRES_DRIVER_CLASS_NAME));
            cpds.setJdbcUrl(envConfig.getStringValue(DeviceInfoQueryProperty.POSTGRES_URL));
            cpds.setUser(userName);
            cpds.setPassword(pswd);
            cpds.setInitialPoolSize(initialPoolSize);
            cpds.setMinPoolSize(minPoolSize);
            cpds.setMaxPoolSize(maxPoolSize);
            cpds.setMaxIdleTime(maxIdleTime);
            cpds.setAcquireIncrement(acquireIncrement);
            cpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
            LOGGER.info("## DeviceInfoQuery - ConnectionPool properties: initialPoolSize: {}, minPoolSize:{}, "
                    + "maxPoolSize: {}, maxIdleTime: {}, acquireIncrement: {}, idleConnectionTestPeriod: {}",
                cpds.getInitialPoolSize(),
                cpds.getMinPoolSize(), cpds.getMaxPoolSize(), cpds.getMaxIdleTime(), cpds.getAcquireIncrement(),
                cpds.getIdleConnectionTestPeriod()
            );
        } catch (PropertyVetoException e) {
            throw new RuntimeException(
                "## Exception while creating connection pool for DeviceInfoQuery component, Error: ", e);
        }
        return cpds;
    }

    /**
     * Cleans up the data source by closing it.
     *
     * @param ds the data source to clean up
     */
    private void cleanupDataSource(DataSource ds) {
        if (ds instanceof ComboPooledDataSource) {
            ComboPooledDataSource cpds = (ComboPooledDataSource) ds;
            cpds.close();
        }
    }
}
