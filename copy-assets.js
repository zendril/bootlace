'use strict';

var fs = require('fs');
var path = require('path');

// Read the assets.json file
var assetsContent = fs.readFileSync('target/assets/assets.json', 'utf8');
var assets = JSON.parse(assetsContent);

// Create the target classes directory if it doesn't exist
var targetDir = 'target/classes/static/assets';
if (!fs.existsSync('target/classes')) {
    fs.mkdirSync('target/classes');
}
if (!fs.existsSync('target/classes/static')) {
    fs.mkdirSync('target/classes/static');
}
if (!fs.existsSync(targetDir)) {
    fs.mkdirSync(targetDir);
}

// Copy assets and update paths
var updatedAssets = {};
for (var sourceFile in assets) {
    var targetFile = assets[sourceFile];
    var sourcePath = targetFile;
    var fileName = path.basename(targetFile);
    var targetPath = path.join(targetDir, fileName);

    // Copy the file
    fs.writeFileSync(targetPath, fs.readFileSync(sourcePath));

    // Update the asset path to be relative to /assets/
    updatedAssets[sourceFile] = '/assets/' + fileName;
}

// Copy the HTML file
var htmlSource = 'target/assets/index.html';
var htmlTarget = path.join(targetDir, 'index.html');
if (fs.existsSync(htmlSource)) {
    fs.writeFileSync(htmlTarget, fs.readFileSync(htmlSource));
}

// Copy source maps
var mapFiles = fs.readdirSync('target/assets').filter(function (file) {
    return file.endsWith('.map');
});

mapFiles.forEach(function (mapFile) {
    var sourcePath = path.join('target/assets', mapFile);
    var targetPath = path.join(targetDir, mapFile);
    fs.writeFileSync(targetPath, fs.readFileSync(sourcePath));
});

// Write the updated assets.json for reference
fs.writeFileSync('target/assets/assets-final.json', JSON.stringify(updatedAssets, null, 2));

console.log('Assets copied to target/classes/static/assets/');