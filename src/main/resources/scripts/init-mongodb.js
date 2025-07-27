// MongoDB initialization script for Bootlace application
// Run this script with: mongo bootlace init-mongodb.js

print("Initializing Bootlace MongoDB database...");

// Switch to bootlace database
use bootlace;

// Create account collection if it doesn't exist
if (!db.getCollectionNames().includes("account")) {
    print("Creating account collection...");
    db.createCollection("account");
    print("Account collection created.");
} else {
    print("Account collection already exists.");
}

// Create index on username field
print("Creating index on username field...");
db.account.createIndex({ "username": "text" }, { name: "username" });
print("Username index created.");

// Check if any accounts exist
var accountCount = db.account.count();
print("Current account count: " + accountCount);

if (accountCount === 0) {
    print("No accounts found. Database will be seeded by the application on startup.");
} else {
    print("Accounts already exist in database:");
    db.account.find({}, { username: 1, roles: 1 }).forEach(function(account) {
        print("  - " + account.username + " (roles: " + account.roles.join(", ") + ")");
    });
}

print("MongoDB initialization completed.");