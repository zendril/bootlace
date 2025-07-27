'use strict';

var fs = require('fs');
var path = require('path');

// Read the main assets.json
var assets = {};
try {
  var assetsContent = fs.readFileSync('target/assets/assets.json', 'utf8');
  assets = JSON.parse(assetsContent);
} catch (e) {
  // File doesn't exist or is empty, start with empty object
}

// Read the styles assets.json if it exists
try {
  var stylesContent = fs.readFileSync('target/assets/assets-styles.json', 'utf8');
  var stylesAssets = JSON.parse(stylesContent);
  Object.assign(assets, stylesAssets);
} catch (e) {
  // File doesn't exist, skip
}

// Write the merged assets.json
fs.writeFileSync('target/assets/assets.json', JSON.stringify(assets));