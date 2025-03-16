const API_BASE_URL = 'http://localhost:8080/api/v1';

// DOM Elements
const csvFileInput = document.getElementById('csvFile');
const uploadBtn = document.getElementById('uploadBtn');
const refreshBtn = document.getElementById('refreshBtn');
const requestsList = document.getElementById('requestsList');

// Event Listeners
uploadBtn.addEventListener('click', uploadCSV);
refreshBtn.addEventListener('click', loadRequests);

// Load requests when page loads
document.addEventListener('DOMContentLoaded', loadRequests);

async function uploadCSV() {
    uploadBtn.disabled = true; // Disable button immediately
    
    const file = csvFileInput.files[0];
    if (!file) {
        showAlert('warning', 'Please select a CSV file first');
        uploadBtn.disabled = false; // Re-enable button if no file
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch(`${API_BASE_URL}/files/public/upload/csv-file`, {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json'
            }
        });
        
        if (response.ok) {
            const result = await response.json();
            showAlert('success', 'File uploaded successfully!');
            csvFileInput.value = '';
            loadRequests();
        } else {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Upload failed');
        }
    } catch (error) {
        showAlert('danger', error.message);
    } finally {
        uploadBtn.disabled = false; // Re-enable button after upload completes or fails
    }
}

function displayRequests(requests) {
    if (!requests || requests.length === 0) {
        requestsList.innerHTML = `
            <tr>
                <td colspan="4" class="text-center py-4">
                    <div class="text-muted">No results found</div>
                </td>
            </tr>
        `;
        return;
    }

    requestsList.innerHTML = requests.map(request => `
        <tr>
            <td>${request.requestId}</td>
            <td>
                <span class="status-badge status-${request.status.toLowerCase()}">
                    ${request.status}
                </span>
            </td>
            <td>${new Date(request.createdTs).toLocaleString()}</td>
            <td>
                ${request.status !== 'COMPLETED' ? 
                    `<button class="btn btn-sm btn-info btn-action" onclick="checkStatus(${request.requestId})">
                        Check Status
                    </button>` : ''
                }
                <button class="btn btn-sm btn-success btn-action" onclick="downloadCSV(${request.requestId})">
                    Download CSV
                </button>
            </td>
        </tr>
    `).join('');
}

// Add this new function for better alerts
function showAlert(type, message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    // Insert alert at the top of the container
    const container = document.querySelector('.container');
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto dismiss after 5 seconds
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

async function loadRequests() {
    try {
        const response = await fetch(`${API_BASE_URL}/files/public/processing-requests`);
        const requests = await response.json();
        displayRequests(requests);
    } catch (error) {
        alert('Error loading requests: ' + error.message);
    }
}

async function checkStatus(requestId) {
    try {
        const response = await fetch(`${API_BASE_URL}/files/public/processing-status/${requestId}`);
        const status = await response.json();
        showAlert('info', `Status for Request ${requestId}: ${status.status}`);
    } catch (error) {
        showAlert('danger', 'Error checking status: ' + error.message);
    }
}

async function downloadCSV(requestId) {
    try {
        const response = await fetch(`${API_BASE_URL}/files/public/download-csv-file/${requestId}`);
        
        if (response.status === 400) {
            showAlert('warning', 'Process not completed yet, you can download later.');
            return;
        }
        
        if (!response.ok) {
            throw new Error('Download failed');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `request-${requestId}.csv`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        showAlert('success', 'File downloaded successfully!');
    } catch (error) {
        showAlert('danger', 'Error downloading CSV: ' + error.message);
    }
}