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

package uk.co.caprica.bootlace.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.co.caprica.bootlace.data.MongoConnectionTest;

/**
 * REST controller for application health checks.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * MongoDB connection test component.
     */
    @Autowired
    private MongoConnectionTest mongoConnectionTest;

    /**
     * Check MongoDB connectivity.
     * 
     * @return health status response
     */
    @RequestMapping(value = "/mongodb", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> checkMongoHealth() {
        Map<String, Object> response = new HashMap<>();
        
        boolean isConnected = mongoConnectionTest.testConnection();
        
        response.put("status", isConnected ? "UP" : "DOWN");
        response.put("database", "MongoDB");
        response.put("details", mongoConnectionTest.getConnectionInfo());
        
        HttpStatus status = isConnected ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(response, status);
    }

    /**
     * Overall application health check.
     * 
     * @return overall health status
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> checkOverallHealth() {
        Map<String, Object> response = new HashMap<>();
        
        boolean mongoHealthy = mongoConnectionTest.testConnection();
        
        response.put("status", mongoHealthy ? "UP" : "DOWN");
        response.put("components", new HashMap<String, String>() {{
            put("mongodb", mongoHealthy ? "UP" : "DOWN");
        }});
        
        HttpStatus status = mongoHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(response, status);
    }
}