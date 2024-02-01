// src/main/resources/static/js/shorten.js

// Function to fetch and display user URLs
function displayUserUrls() {
   // axios.get('/api/url/getUrlsByUsername?username=' + encodeURIComponent(username))

    axios.get('/api/url/getUrlsByUsername', {
        params: {
            username: encodeURIComponent(username)
        }
    })
        .then(function (response) {
            var dataTable = $('#urlsTable').DataTable({
                paging: true,           // Enable pagination
                searching: true,        // Enable searching
                ordering: true,         // Enable sorting
                info: true,             // Show information about the table
                lengthChange: false,    // Disable changing number of records per page
                pageLength: 10,         // Set the default number of records per page
                responsive: true,       // Enable responsive design
                columnDefs: [
                    { targets: [0, 7, 8, 9, 10], orderable: false } // Disable sorting for specific columns
                ],
                destroy: true,
                data: response.data,
                columns: [
                    { data: null, render: function (data, type, row) {
                            return '<i class="' + (row.linkType === 'bio' ? 'fas fa-file-invoice' : 'fas fa-link') + '"></i>';
                        }},
                    { data: 'id' },
                    { data: 'originalUrl' },
                    { data: null, render: function (data, type, row) {
                            return '<a href="' + getBaseUrl() + row.shortUrl + '">' + row.shortUrl + '</a>';
                        }},
                    { data: 'linkType' },
                    { data: 'hits' },
                    { data: 'uniqueHits' },
                    // Add more columns as needed
                    {
                        data: null, render: function (data, type, row) {
                            return '<i class="fas fa-trash" style="cursor:pointer;" onclick="deleteUserUrl(\'' + row.shortUrl + '\')"></i>';
                        }
                    },
                    {
                        data: null, render: function (data, type, row) {
                            return '<i class="fas fa-copy" style="cursor:pointer;" onclick="copyToClipboard(\'' + getBaseUrl() + row.shortUrl + '\')"></i>';
                        }
                    },
                    {
                        data: null, render: function (data, type, row) {
                            return '<i class="fas fa-share" style="cursor:pointer;" onclick="shareShortUrl(\'' + getBaseUrl() + row.shortUrl + '\')"></i>';
                        }
                    },
                    {
                        data: null, render: function (data, type, row) {
                            return '<i class="fas fa-circle-info" style="cursor:pointer;" onclick="redirectToDetails(\'' + row.shortUrl + '\')"></i>';
                        }
                    },
                    {
                        data: null, render: function (data, type, row) {
                            return '<div><button class="generateQrButton" data-short-url="' + row.shortUrl + '">Generate QR</button></div>';
                        }
                         },
                    {
                        data: null, render: function (data, type, row) {
                            return '<img id="qrImage_' + row.shortUrl + '" ></img>';
                        }
                    }
                ],
                // Add other DataTable options here
            });

            // Refresh DataTable after updating the rows
            dataTable.draw();
        })
        .catch(function (error) {
            console.error('Error:', error.response.data);
        });
}


function fadeElementToInvisible(element) {

    // Clear previous transition and set opacity to 1
    element.style.transition = 'none';
    element.style.opacity = '1';

    // Trigger reflow to apply the style changes immediately
    element.offsetHeight;

    // Enable transition for opacity
    element.style.transition = 'opacity 5s';  //TIME

    // Set opacity to 0 after a delay
    setTimeout(function () {
        element.style.opacity = '0';
    }, 0);

    // Hide the element after the transition completes
    setTimeout(function () {
        element.style.display = 'none';
    }, 5000); // Wait for the transition to complete before hiding the element  //TIME
}

var resultDiv = document.getElementById('resultdiv');
var resultDivBio = document.getElementById('resultdivBio');
var shortenError = document.getElementById('shortenError');
var shortenInfo = document.getElementById('shortenInfo');
var bioError = document.getElementById('bioError');
var bioInfo = document.getElementById('bioInfo');

resultDiv.style.display = 'none';
resultDivBio.style.display = 'none';

// Event listener for the shorten form
document.getElementById('shortenForm').addEventListener('submit', function (event) {
    event.preventDefault();

    var originalUrl = document.getElementById('originalUrl').value;

    axios.post('/api/url/shorten', {
        originalUrl: originalUrl
    })
        .then(function (response) {
            shortenError.style.display = 'none';
            shortenInfo.style.display='block';
            shortenInfo.innerHTML = 'Successfully Shortened';
            fadeElementToInvisible(shortenInfo);
            resultDiv.style.display = 'block';
            document.getElementById('result').innerHTML =  response.data;
            // After shortening, refresh and display user URLs
            displayUserUrls();
        })
        .catch(function (error) {
            resultDiv.style.display = 'none';
            shortenInfo.style.display='none';
            shortenError.style.display='block';
            fadeElementToInvisible(shortenError);
            console.error('Error:', error.response.data);
            document.getElementById('shortenError').innerHTML = 'Error: ' + error.response.data;
        });
});


document.getElementById('bioForm').addEventListener('submit', function (event) {
    event.preventDefault();

    var htmlData = document.getElementById('htmlData').value;
    let data = JSON.stringify({
        "customHtmlContent": htmlData
    });

    let config = {
        method: 'post',
        maxBodyLength: Infinity,
        url: '/api/url/bio/create',
        headers: {
            'Content-Type': 'application/json',
        },
        data : data
    };

    axios.request(config)
        .then((response) => {
            bioError.style.display = 'none';
            bioInfo.style.display='block';
            bioInfo.innerHTML = 'Successfully Created Bio Page';
            fadeElementToInvisible(bioInfo);
            resultDivBio.style.display = 'block';
            document.getElementById('resultBio').innerHTML =  response.data;
            // After shortening, refresh and display user URLs
            displayUserUrls();
        })
        .catch((error) => {
            bioInfo.style.display='none';
            bioError.style.display='block';
            resultDivBio.style.display = 'none';
            fadeElementToInvisible(bioError);
            console.error('Error:', error.response.data);
            document.getElementById('bioError').innerHTML = 'Error: ' + error.response.data;
        });
});

// Call the function to display user URLs on page load
displayUserUrls();


document.addEventListener('DOMContentLoaded', function () {
    // Fetch user details when the page is loaded
    getUserDetails();
});



// Function to delete a user URL
function deleteUserUrl(url) {
    var confirmDelete = window.confirm('Are you sure you want to delete this URL?');
    if (confirmDelete) {
        axios.delete('/api/url/remove', { params: { shortUrl: url } })
            .then(function (response) {
                alert(response+'URL removed successfully!');
                // Refresh and display user URLs after removal
                displayUserUrls();
            })
            .catch(function (error) {
                console.error('Error:', error.response.data);
                alert('Error removing URL: ' + error.response.data);
            });
    }
}

// Function to generate and display QR code for a given shortUrl and display it in the specified image element
function generateAndDisplayQrCode(shortUrl, logoPath, fgColor, bgColor, qrImageId) {
    // Call the endpoint to generate QR code
    axios.get('/api/url/qr/generate', {
        params: {
            shortUrl: shortUrl,
            logoPath: logoPath,
            fgColor: fgColor,
            bgColor: bgColor
        }
    })
        .then(function (response) {
            // Check if response data and qrImageId are defined
            if (response.data && qrImageId) {
                // Display the generated QR code directly in the div
                displayQrCode(shortUrl, response.data, qrImageId);
            } else {
                console.error('Error: Invalid response data or QR image ID is undefined.');
            }
        })
        .catch(function (error) {
            console.error('Error:', error.response.data);
        });
}

// Function to display QR code image for a given shortUrl in the specified image element
function displayQrCode(shortUrl, qrCodeBase64, qrImage) {
    // Convert qrImage to a jQuery object
    var $qrImage = $(qrImage);

    // Call the endpoint to fetch QR code image
    axios.get('/api/url/qr/image/' + encodeURIComponent(shortUrl), { responseType: 'arraybuffer' })
        .then(function (response) {
            // Set the image source and dimensions using jQuery
            $qrImage.attr('src', 'data:image/png;base64,' + qrCodeBase64);
            $qrImage.attr('alt', 'QR Code');
            $qrImage.css('maxWidth', '100px'); // Set a maximum width using jQuery
            $qrImage.addClass('img-fluid'); // Add Bootstrap's img-fluid class for responsiveness using jQuery

            // Display the image in the specified div using jQuery
            $qrImage.parent().css('display', 'block'); // Make the div visible using jQuery
        })
        .catch(function (error) {
            console.error('Error:', error.response.data);
        });
}

// Function to get the base URL dynamically
function getBaseUrl() {
    var port = window.location.port;
    var baseUrl = window.location.protocol + '//' + window.location.hostname;
    if (port) {
        baseUrl += ':' + port;
    }
    return baseUrl+'/';
}

// Function to copy text to clipboard
function copyToClipboard(text) {
    var tempInput = document.createElement('input');
    tempInput.value = text;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand('copy');
    document.body.removeChild(tempInput);
    alert('Copied to clipboard: ' + text);
}

// Function to share short URL using Web Share API
function shareShortUrl(url) {
    if (navigator.share) {
        var shareMessage = 'Check out this short URL: ' + url;

        // Create a clickable link in the shared message
        var linkElement = document.createElement('a');
        linkElement.href = url;
        linkElement.textContent = url;

        navigator.share({
            title: 'Share Short URL',
            text: shareMessage,
            url: url,
        })
            .then(() => console.log('Successful share'))
            .catch((error) => console.log('Error sharing:', error));
    } else {
        alert('Web Share API is not supported in this browser.');
    }
}

// Add these functions to your script
function redirectToDetails(shortUrl) {
    window.location.href = getBaseUrl() + 'details?shortUrl=' + shortUrl;
}

document.getElementById('clipboardBio').addEventListener('click',function (event){
    event.preventDefault();
    var result = document.getElementById('resultBio').value.toString();
   // console.log(result);
   copyToClipboard(result);
});

document.getElementById('clipboardShorten').addEventListener('click',function (event){
    event.preventDefault();
    var result = document.getElementById('result').value.toString();
    //console.log(result);
    copyToClipboard(result);
});

document.getElementById('shortenDetails').addEventListener('click',function (event){
    goToDetailsPage('result');
});

document.getElementById('bioDetails').addEventListener('click',function (event){
    goToDetailsPage('resultBio');
});

function goToDetailsPage(entity) {
    var result = document.getElementById(entity).value.toString();
    var shortUrl = result.substring(result.lastIndexOf('/') + 1);
    var newUrl = getBaseUrl()+'details?shortUrl=' + shortUrl;
    window.location.href = newUrl;
}
function goToProfilePage(entity) {
    window.location.href = getBaseUrl() +'profile?username='+ document.getElementById('usernameSpan').innerText;
}

// Attach click event listener to the buttons
$('#urlsTable').on('click', '.generateQrButton', function () {
    var shortUrl = $(this).data('short-url');
   var img = document.getElementById('qrImage_' + shortUrl);
  //  var img =  $('qrImage_' + shortUrl)
    generateAndDisplayQrCode(shortUrl, '', 'hex_fg_color', 'hex_bg_color', img);
});

// Call the function to display user URLs on page load
displayUserUrls();