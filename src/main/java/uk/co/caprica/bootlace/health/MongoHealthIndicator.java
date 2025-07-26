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

package uk.co.caprica.bootlace.health;

import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * MongoDB Health Indicator for monitoring database connectivity and performance.
 * 
 * This component implements part of Step 4: MongoDB Best Practices
 * - Provides health monitoring for MongoDB connections
 * - Enables proper error detection and reporting
 * - Supports access control monitoring
 */
@Component
public class MongoHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(MongoHealthIndicator.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoClient mongoClient;

    @Override
    public Health health() {
        try {
            // Test basic connectivity
            Document result = mongoTemplate.getDb().runCommand(new Document("ping", 1));
            
            if (result.getDouble("ok") == 1.0) {
                // Get additional database information
                String databaseName = mongoTemplate.getDb().getName();
                long accountCount = mongoTemplate.getCollection("account").estimatedDocumentCount();
                
                // Get server status for additional metrics
                Document serverStatus = mongoTemplate.getDb().runCommand(new Document("serverStatus", 1));
                String version = serverStatus.getString("version");
                Document connections = serverStatus.get("connections", Document.class);
                
                Health.Builder healthBuilder = Health.up()
                    .withDetail("database", databaseName)
                    .withDetail("accountCount", accountCount)
                    .withDetail("mongoVersion", version);
                
                if (connections != null) {
                    healthBuilder
                        .withDetail("connectionsAvailable", connections.getInteger("available", 0))
                        .withDetail("connectionsCurrent", connections.getInteger("current", 0))
                        .withDetail("connectionsTotal", connections.getInteger("totalCreated", 0));
                }
                
                logger.debug("MongoDB health check passed - database: {}, accounts: {}", databaseName, accountCount);
                return healthBuilder.build();
                
            } else {
                logger.warn("MongoDB ping command returned non-OK status: {}", result);
                return Health.down()
                    .withDetail("error", "MongoDB ping failed")
                    .withDetail("response", result.toJson())
                    .build();
            }
            
        } catch (Exception e) {
            logger.error("MongoDB health check failed: {}", e.getMessage(), e);
            return Health.down()
                .withDetail("error", e.getClass().getSimpleName())
                .withDetail("message", e.getMessage())
                .build();
        }
    }
}