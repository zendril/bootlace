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

package uk.co.caprica.bootlace.startup;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import uk.co.caprica.bootlace.data.mongo.account.AccountRepository;
import uk.co.caprica.bootlace.domain.account.Account;

/**
 * Component containing operations to execute on application startup.
 * <p>
 * This component initialises the configured database with seed data.
 * <p>
 * <em>This component only exists for purposes of this reference application and would not
 * ordinarily be used in a Production application.</em>
 */
@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Log.
     */
    private final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    /**
     * Lower-level interface to the database, used e.g. to create collections and indexes.
     */
    @Autowired
    private MongoOperations mongoOperations;

    /**
     * Repository for account entities.
     */
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Password encoder component.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("onApplicationEvent(event={})", event);
        
        // Test MongoDB connection before proceeding
        if (!testMongoConnection()) {
            logger.error("MongoDB connection failed - application startup aborted");
            throw new RuntimeException("Unable to connect to MongoDB. Please ensure MongoDB is running on localhost:27017");
        }
        
        createDatabase();
        seedDatabase();
    }

    /**
     * Test MongoDB connection and provide detailed error information if connection fails.
     * 
     * @return true if connection is successful, false otherwise
     */
    private boolean testMongoConnection() {
        try {
            logger.info("Testing MongoDB connection...");
            
            // Test basic connectivity by running a simple command
            mongoOperations.getCollection("test").count();
            
            logger.info("MongoDB connection test successful");
            return true;
        } catch (Exception e) {
            logger.error("MongoDB connection test failed. Please check:", e);
            logger.error("1. MongoDB server is running");
            logger.error("2. MongoDB is accessible on localhost:27017");
            logger.error("3. No firewall is blocking the connection");
            logger.error("4. MongoDB service is started");
            return false;
        }
    }

    /**
     * Create the various database collections and indexes for this application.
     */
    private void createDatabase() {
        logger.debug("createDatabase()");
        try {
            // Check if collection already exists
            if (!mongoOperations.collectionExists("account")) {
                logger.info("Creating 'account' collection...");
                try {
                    mongoOperations.createCollection("account");
                    logger.info("Account collection created successfully");
                } catch (org.springframework.data.mongodb.UncategorizedMongoDbException e) {
                    if (e.getMessage().contains("already exists")) {
                        logger.debug("Account collection already exists (caught during creation)");
                    } else {
                        throw e;
                    }
                }
            } else {
                logger.debug("Account collection already exists");
            }
            
            // Ensure index exists (with error handling for existing indexes)
            logger.debug("Ensuring username index exists...");
            try {
                mongoOperations.getCollection("account").createIndex(
                    new com.mongodb.BasicDBObject("username", 1),
                    new com.mongodb.BasicDBObject("unique", true)
                );
                logger.debug("Username index created");
            } catch (Exception indexException) {
                if (indexException.getMessage().contains("already exists") || 
                    indexException.getMessage().contains("duplicate key")) {
                    logger.debug("Username index already exists");
                } else {
                    logger.warn("Could not create username index: {}", indexException.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error creating database collections or indexes", e);
            throw new RuntimeException("Failed to initialize database structure", e);
        }
    }

    /**
     * Populate the database with seed data, in particular some demo accounts are created so that
     * application logins and role-based authorisations will work.
     */
    private void seedDatabase() {
        logger.debug("seedDatabase()");
        
        try {
            List<String> adminRoles = new ArrayList<>();
            adminRoles.add("ROLE_ADMIN");
            adminRoles.add("ROLE_USER");
            List<String> userRoles = new ArrayList<>();
            userRoles.add("ROLE_USER");
            
            // Create admin account 'mark'
            if (accountRepository.findByUsername("mark") == null) {
                logger.info("Creating admin account: mark");
                Account markAdminAccount = new Account();
                markAdminAccount.setUsername("mark");
                markAdminAccount.setPassword(passwordEncoder.encode("bimble"));
                markAdminAccount.setRoles(adminRoles);
                accountRepository.save(markAdminAccount);
                logger.info("Admin account 'mark' created successfully");
            } else {
                logger.debug("Admin account 'mark' already exists");
            }
            
            // Create admin account 'admin'
            if (accountRepository.findByUsername("admin") == null) {
                logger.info("Creating admin account: admin");
                Account demoAdminAccount = new Account();
                demoAdminAccount.setUsername("admin");
                demoAdminAccount.setPassword(passwordEncoder.encode("admin"));
                demoAdminAccount.setRoles(adminRoles);
                accountRepository.save(demoAdminAccount);
                logger.info("Admin account 'admin' created successfully");
            } else {
                logger.debug("Admin account 'admin' already exists");
            }
            
            // Create user account 'user'
            if (accountRepository.findByUsername("user") == null) {
                logger.info("Creating user account: user");
                Account demoUserAccount = new Account();
                demoUserAccount.setUsername("user");
                demoUserAccount.setPassword(passwordEncoder.encode("user"));
                demoUserAccount.setRoles(userRoles);
                accountRepository.save(demoUserAccount);
                logger.info("User account 'user' created successfully");
            } else {
                logger.debug("User account 'user' already exists");
            }
            
            logger.info("Database seeding completed successfully");
            
        } catch (Exception e) {
            logger.error("Error seeding database with initial data", e);
            throw new RuntimeException("Failed to seed database with initial accounts", e);
        }
    }

}
