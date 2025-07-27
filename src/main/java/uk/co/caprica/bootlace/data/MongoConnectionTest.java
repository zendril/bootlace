/*
 * This file is part of Bootlace.
 *
 * Copyright (C) 2015
 * Caprica Software Limited (capricasoftware.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.caprica.bootlace.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.mongodb.CommandResult;

/**
 * Component to test MongoDB connectivity and provide connection diagnostics.
 */
@Component
public class MongoConnectionTest {

    /**
     * Log.
     */
    private final Logger logger = LoggerFactory.getLogger(MongoConnectionTest.class);

    /**
     * MongoDB operations interface.
     */
    @Autowired
    private MongoOperations mongoOperations;

    /**
     * Test MongoDB connection and log connection details.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            logger.info("Testing MongoDB connection...");
            
            // Test basic connectivity by performing a simple operation
            long count = mongoOperations.getCollection("account").count();
            
            logger.info("MongoDB connection successful!");
            logger.info("Database name: {}", mongoOperations.getCollection("account").getDB().getName());
            logger.info("Account collection document count: {}", count);
            return true;
        } catch (Exception e) {
            logger.error("MongoDB connection test failed", e);
            return false;
        }
    }

    /**
     * Get MongoDB connection information for diagnostics.
     * 
     * @return connection info string
     */
    public String getConnectionInfo() {
        try {
            String dbName = mongoOperations.getCollection("account").getDB().getName();
            long accountCount = mongoOperations.getCollection("account").count();
            return String.format("Connected to MongoDB database '%s' with %d accounts", dbName, accountCount);
        } catch (Exception e) {
            logger.debug("Could not get connection info", e);
            return "MongoDB connection information unavailable";
        }
    }
}