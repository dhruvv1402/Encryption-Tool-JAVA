# Modern Encryption Tool

A secure file encryption and decryption utility with a modern UI that uses industry-standard AES-256 encryption to protect your sensitive files.

![Modern Encryption Tool](https://img.shields.io/badge/Security-AES--256-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

## Features

- **Strong Encryption**: Uses AES-256 encryption in GCM mode for maximum security
- **Secure Key Derivation**: Implements PBKDF2 with 65,536 iterations for secure password-based key generation
- **Modern UI**: Clean, intuitive interface with responsive design
- **Dual Interface**: Use either the GUI or command-line interface
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **File Type Support**: Works with any file type and size


## Requirements

- Java Development Kit (JDK) 8 or higher
- Node.js and npm (for the web interface)

## Installation

### Option 1: Clone from GitHub

```bash
# Clone the repository
git clone https://github.com/yourusername/modern-encryption-tool.git

# Navigate to the project directory
cd modern-encryption-tool

# Install dependencies
npm install
```

### Option 2: Download Release

1. Download the latest release from the [Releases](https://github.com/yourusername/modern-encryption-tool/releases) page
2. Extract the ZIP file to your preferred location
3. Run the installation script (if applicable)

## Usage

### Web Interface

1. Start the server:
   ```bash
   npm start
   ```
2. Open your browser and navigate to `http://localhost:3000`
3. Select a file, enter a password, and click either "Encrypt" or "Decrypt"

### Java GUI

1. Run the application:
   ```bash
   java EncryptionTool
   ```
2. Use the file browser to select a file
3. Enter your password
4. Click "Encrypt" or "Decrypt" as needed

### Command Line

```bash
# For encryption
java EncryptionTool encrypt <file_path> <password>

# For decryption
java EncryptionTool decrypt <file_path> <password>
```

## Security Considerations

- **Password Strength**: Use strong, unique passwords for each file you encrypt
- **Password Storage**: This tool does not store your passwords; you must remember them
- **No Recovery**: There is no way to recover encrypted files if you forget the password
- **Data Integrity**: The GCM mode provides authentication to verify data hasn't been tampered with

## How It Works

This tool uses a Java backend with AES encryption to securely encrypt and decrypt your files. The password you provide is used to generate a secure encryption key through PBKDF2 key derivation function with a random salt. Your files never leave your computer during the encryption process.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Express.js](https://expressjs.com/) for the web server
- [Multer](https://github.com/expressjs/multer) for file handling
- Java Cryptography Architecture for encryption capabilities

## Contact

Your Name - your.email@example.com

Project Link: [https://github.com/yourusername/modern-encryption-tool](https://github.com/yourusername/modern-encryption-tool)
