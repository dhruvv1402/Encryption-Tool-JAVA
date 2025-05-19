// Simple Express server to connect the web frontend to the Java encryption tool
const express = require('express');
const multer = require('multer');
const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');
const app = express();
const port = 3000;

// Set up storage for uploaded files
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, './uploads/')
  },
  filename: function (req, file, cb) {
    cb(null, file.originalname)
  }
});

const upload = multer({ storage: storage });

// Create uploads directory if it doesn't exist
if (!fs.existsSync('./uploads')) {
  fs.mkdirSync('./uploads');
}

// Serve static files
app.use(express.static('.'));

// Handle file encryption
app.post('/encrypt', upload.single('file'), (req, res) => {
  const filePath = req.file.path;
  const password = req.body.password;
  
  // Execute Java encryption tool
  exec(`java EncryptionTool encrypt "${filePath}" "${password}"`, (error, stdout, stderr) => {
    if (error) {
      console.error(`Error: ${error.message}`);
      return res.status(500).json({ error: error.message });
    }
    
    if (stderr) {
      console.error(`Stderr: ${stderr}`);
      return res.status(500).json({ error: stderr });
    }
    
    // Send encrypted file path back to client
    const encryptedFilePath = `${filePath}.enc`;
    res.json({ 
      success: true, 
      message: stdout,
      filePath: encryptedFilePath,
      fileName: path.basename(encryptedFilePath)
    });
  });
});

// Handle file decryption
app.post('/decrypt', upload.single('file'), (req, res) => {
  const filePath = req.file.path;
  const password = req.body.password;
  
  // Execute Java decryption tool
  exec(`java EncryptionTool decrypt "${filePath}" "${password}"`, (error, stdout, stderr) => {
    if (error) {
      console.error(`Error: ${error.message}`);
      return res.status(500).json({ error: error.message });
    }
    
    if (stderr) {
      console.error(`Stderr: ${stderr}`);
      return res.status(500).json({ error: stderr });
    }
    
    // Determine decrypted file path
    let decryptedFilePath;
    if (filePath.endsWith('.enc')) {
      decryptedFilePath = filePath.substring(0, filePath.length - 4);
    } else {
      decryptedFilePath = `${filePath}.dec`;
    }
    
    res.json({ 
      success: true, 
      message: stdout,
      filePath: decryptedFilePath,
      fileName: path.basename(decryptedFilePath)
    });
  });
});

// Download route
app.get('/download/:filename', (req, res) => {
  const filename = req.params.filename;
  const filepath = path.join(__dirname, 'uploads', filename);
  
  res.download(filepath, (err) => {
    if (err) {
      console.error(`Download error: ${err.message}`);
      res.status(500).send('Error downloading file');
    }
  });
});

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});