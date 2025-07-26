#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Read the original main.css
const originalCss = fs.readFileSync('src/main/styles/main.css', 'utf8');

// Fix Font Awesome CSS font paths
const fontAwesomeCss = fs.readFileSync('target/assets/font-awesome.min.css', 'utf8');
const fixedFontAwesomeCss = fontAwesomeCss.replace(/url\('\.\.\/fonts\//g, "url('fonts/");
fs.writeFileSync('target/assets/font-awesome.min.css', fixedFontAwesomeCss);

// Create a new main.css that imports from local files instead of node_modules
const processedCss = `/*
 * Main application CSS stylesheet.
 * 
 * This file replaces the original Stylus-based build system with standard CSS imports.
 * All original styling functionality has been preserved.
 */

/* Bootstrap CSS */
@import url('bootstrap.min.css');

/* Font Awesome CSS */
@import url('font-awesome.min.css');

/* Angular Toastr CSS */
@import url('angular-toastr.min.css');

/* Bootswatch Theme - Cerulean */
@import url('bootswatch.min.css');

/* Application Layout Styles (converted from layout.styl) */
${originalCss.split('/* Application Layout Styles (converted from layout.styl) */')[1] || ''}
`;

// Write the processed CSS
fs.writeFileSync('target/assets/main.css', processedCss);

console.log('Built main.css with local imports and fixed Font Awesome font paths');
