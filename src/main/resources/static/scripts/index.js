$(document).ready(function(){

    $('#openFileDialog').click(function () {
        $('#status').text('');
        $('#fileInput').click();
    });

    $('#fileInput').change(function () {
        const file = this.files[0];
        if (file) {
            // Create FormData and append the file
            const formData = new FormData();
            formData.append('file', file);

            // Display status message
            $('#status').text('Uploading...');

            // Send the file to the server via POST
            $.ajax({
                url: '/upload',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    $('#status').text('Upload of file "' + response.name + '" successful!');
                },
                error: function () {
                    $('#status').text('Upload failed. Please try again.');
                }
            });
        }
    });

    $("#refreshFileButton").click(function(){
        $.ajax({
            url: '/listfolder',
            }).done(function(data) {
            console.dir(data);
            var fileHTML = "";
            for(file of data) {
                var isFolder = file.type.includes("folder");
                var buttonDownload = "";
                var buttonDelete = "";
                if (!isFolder) {
                    buttonDownload = '<button class="btn btn-success" onclick="downloadFile(\''+ file.id + '\')">Download</button>'
                    buttonDelete = '<button class="btn btn-danger" onclick="deleteFile(\''+ file.id + '\')">Delete</button>'
                }
                fileHTML += '<ul class="list-group list-group-horizontal" >';
                fileHTML += '<li class="list-group-item">' + file.name + '</li>'
                fileHTML += '<li class="list-group-item">' + file.type + '</li>'
                fileHTML += '<li class="list-group-item">' + new Date(file.lastModifiedDate.value).toDateString() + '</li>'
                fileHTML += '<li class="list-group-item">'+ buttonDownload + '</li>';
                fileHTML += '<li class="list-group-item">'+ buttonDelete + '</li>';
                fileHTML += '</ul>';
            }
            $("#fileListLayout").html(fileHTML);
        });
    });
});


function deleteFile(fileId) {
    $.ajax({
        url:'/delete/' + fileId,
        method: 'DELETE'
    }).done(function(){
        alert('File has been deleted, refresh list.')
    });
}

function downloadFile(fileId) {
    // Navigate to the download endpoint directly
    window.location.href = '/download/' + fileId;
}
