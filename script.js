/**
 * Modern Encryption Tool - Frontend Script
 * 
 * This script handles the UI interactions and encryption/decryption operations
 * for the Modern Encryption Tool web interface.
 * 
 * @author Modern Encryption Tool Team
 * @version 1.0.0
 * @license MIT
 */

document.addEventListener('DOMContentLoaded', function() {
    /**
     * DOM Element References
     * Cache DOM elements for better performance and readability
     */
    const fileInput = document.getElementById('file');
    const filePathDisplay = document.getElementById('file-path');
    const browseBtn = document.getElementById('browse-btn');
    const passwordInput = document.getElementById('password');
    const encryptBtn = document.getElementById('encrypt-btn');
    const decryptBtn = document.getElementById('decrypt-btn');
    const statusMessage = document.getElementById('status-message');
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const contactSubmitBtn = document.querySelector('.contact-submit');
    
    // Tab functionality
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove active class from all buttons and contents
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            
            // Add active class to clicked button and corresponding content
            button.classList.add('active');
            const tabId = button.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });
    
    // Contact form submission
    if (contactSubmitBtn) {
        contactSubmitBtn.addEventListener('click', function() {
            const name = document.getElementById('contact-name').value;
            const email = document.getElementById('contact-email').value;
            const message = document.getElementById('contact-message').value;
            
            if (name && email && message) {
                alert('Thank you for your message! The developer will get back to you soon.');
                document.getElementById('contact-name').value = '';
                document.getElementById('contact-email').value = '';
                document.getElementById('contact-message').value = '';
            } else {
                alert('Please fill in all fields before submitting.');
            }
        });
    }
    
    // File selection handling
    browseBtn.addEventListener('click', function() {
        fileInput.click();
    });
    
    fileInput.addEventListener('change', function() {
        if (fileInput.files.length > 0) {
            filePathDisplay.textContent = fileInput.files[0].name;
        } else {
            filePathDisplay.textContent = 'No file selected';
        }
    });
    
    // Window controls functionality (simplified for modern UI)
    if (document.querySelector('.minimize')) {
        document.querySelector('.minimize').addEventListener('click', function() {
            // Minimize animation
            document.querySelector('.main-card').style.transform = 'scale(0.1)';
            setTimeout(() => {
                document.querySelector('.main-card').style.transform = 'scale(1)';
            }, 300);
        });
    }
    
    if (document.querySelector('.maximize')) {
        document.querySelector('.maximize').addEventListener('click', function() {
            document.querySelector('.main-card').classList.toggle('maximized');
        });
    }
    
    if (document.querySelector('.close')) {
        document.querySelector('.close').addEventListener('click', function() {
            // Close animation
            document.querySelector('.main-card').style.opacity = '0';
            setTimeout(() => {
                document.querySelector('.main-card').style.opacity = '1';
            }, 500);
        });
    }
    
    // Encryption/Decryption functionality
    encryptBtn.addEventListener('click', function() {
        if (!validateInputs()) return;
        
        const file = fileInput.files[0];
        const password = passwordInput.value;
        
        // Call the server to encrypt the file
        uploadFile(file, password, 'encrypt');
    });
    
    decryptBtn.addEventListener('click', function() {
        if (!validateInputs()) return;
        
        const file = fileInput.files[0];
        const password = passwordInput.value;
        
        // Call the server to decrypt the file
        uploadFile(file, password, 'decrypt');
    });
    
    /**
     * Validates user inputs before processing
     * @returns {boolean} True if all inputs are valid, false otherwise
     */
    function validateInputs() {
        // Reset status message
        statusMessage.textContent = '';
        
        // Validate file selection
        if (fileInput.files.length === 0) {
            statusMessage.textContent = 'Please select a file';
            return false;
        }
        
        // Validate file size (100MB limit)
        const maxSize = 100 * 1024 * 1024; // 100MB in bytes
        if (fileInput.files[0].size > maxSize) {
            statusMessage.textContent = 'File size exceeds 100MB limit';
            return false;
        }
        
        // Validate password
        if (passwordInput.value.trim() === '') {
            statusMessage.textContent = 'Please enter a password';
            return false;
        }
        
        // Warn about weak passwords (optional)
        if (passwordInput.value.length < 8) {
            statusMessage.textContent = 'Warning: Using a weak password. Consider using a stronger password.';
            // Still return true as this is just a warning
        }
        
        return true;
    }
    
    /**
     * Handles file upload, encryption/decryption, and download
     * @param {File} file - The file to process
     * @param {string} password - The encryption/decryption password
     * @param {string} operation - Either 'encrypt' or 'decrypt'
     */
    function uploadFile(file, password, operation) {
        // Update UI to show processing state
        statusMessage.textContent = operation === 'encrypt' ? 'Encrypting file...' : 'Decrypting file...';
        encryptBtn.disabled = true;
        decryptBtn.disabled = true;
        
        // Prepare form data
        const formData = new FormData();
        formData.append('file', file);
        formData.append('password', password);
        
        // Set timeout for long operations
        const timeoutId = setTimeout(() => {
            statusMessage.textContent = `${operation} is taking longer than expected. Please wait...`;
        }, 5000);
        
        // Send request to server
        fetch(`/${operation}`, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            clearTimeout(timeoutId);
            
            if (!response.ok) {
                // Handle HTTP errors
                if (response.status === 413) {
                    throw new Error('File too large');
                } else if (response.status === 401) {
                    throw new Error('Authentication required');
                } else if (response.status >= 500) {
                    throw new Error('Server error. Please try again later.');
                } else {
                    throw new Error(`Server error: ${response.status}`);
                }
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Handle successful operation
                statusMessage.textContent = `File ${operation}ed successfully! Downloading...`;
                
                // Create a download link
                const downloadLink = document.createElement('a');
                downloadLink.href = `/download/${data.fileName}`;
                downloadLink.download = data.fileName;
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink);
                
                // Clear password field for security
                passwordInput.value = '';
            } else {
                // Handle operation error
                statusMessage.textContent = `Error: ${data.error || 'Unknown error'}`;
            }
        })
        .catch(error => {
            // Handle network or other errors
            console.error('Error:', error);
            statusMessage.textContent = `Error: ${error.message}`;
        })
        .finally(() => {
            // Re-enable buttons
            encryptBtn.disabled = false;
            decryptBtn.disabled = false;
        });
    }
    }
    
    // Create SVG icons
    createSVGIcons();
});

function createSVGIcons() {
    // Create Start button icon
    const startIcon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    startIcon.setAttribute('width', '20');
    startIcon.setAttribute('height', '20');
    startIcon.setAttribute('viewBox', '0 0 20 20');
    startIcon.innerHTML = `
        <rect x="2" y="2" width="16" height="16" rx="2" fill="#00ff00" />
        <path d="M4 10 L16 4 L16 16 Z" fill="#000" />
    `;
    
    // Create encryption icon
    const encryptIcon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    encryptIcon.setAttribute('width', '16');
    encryptIcon.setAttribute('height', '16');
    encryptIcon.setAttribute('viewBox', '0 0 16 16');
    encryptIcon.innerHTML = `
        <rect x="3" y="7" width="10" height="7" rx="1" fill="#00ff00" />
        <path d="M5 7 L5 5 C5 3.5 6.5 2 8 2 C9.5 2 11 3.5 11 5 L11 7" stroke="#00ff00" fill="none" stroke-width="1.5" />
        <circle cx="8" cy="10" r="1.5" fill="#000" />
    `;
    
    // Save SVGs to files
    const startIconBlob = new Blob([startIcon.outerHTML], {type: 'image/svg+xml'});
    const encryptIconBlob = new Blob([encryptIcon.outerHTML], {type: 'image/svg+xml'});
    
    // Create URLs for the SVGs
    const startIconURL = URL.createObjectURL(startIconBlob);
    const encryptIconURL = URL.createObjectURL(encryptIconBlob);
    
    // Set the icon sources
    document.querySelector('.start-icon').src = startIconURL;
    document.querySelector('.taskbar-icon').src = encryptIconURL;
}