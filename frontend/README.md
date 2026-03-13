# SK SONS Cultivaters Management System - Frontend

This repository contains the completely independent, detached Frontend for the SK SONS Cultivaters Management System. 
It is built with vanilla HTML, CSS, and JavaScript. Space is neatly separated from the Spring Boot backend layer.

## Setup Instructions

Since this is a standard HTML application without a build process, it is extremely easy to run and deploy:

**Option 1: Live Server (VS Code)**
1. Open this `SK SONS Cultivaters Frontend` folder in Visual Studio Code.
2. Install the "Live Server" extension.
3. Right-click on `index.html` and select **"Open with Live Server"**.

**Option 2: Direct Browser Open**
1. Simply double-click `login.html` to open it in your browser.
2. *Note: For full API features and CORS, running it on a local server (like Option 1) is recommended.*

## Backend Connection

This frontend expects the Spring Boot `SK SONS Cultivaters Management System` backend to be running on:
`http://localhost:8080`

If the backend port changes, modify the `API_BASE_URL` inside `js/api.js`.
