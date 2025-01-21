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

    $("#refreshButton").click(function(){
        window.location.href = 'http://localhost:8080/';
    });

    $.ajax({
        url: '/listfolder',
        }).done(function(data) {
        console.dir(data);
        var list_files =[];
        var list_folders = []
        for(file of data) {
            var isFolder = file.type.includes("folder");
            var element = {id: file.id, name: file.name, type: file.type, lastMod:new Date(file.lastModifiedDate.value).toDateString()};
            if (isFolder){
                list_folders.push(element);
            } else {
                list_files.push(element);
            }
        }

        var folderRows = "";
        list_folders.forEach( folder => {
            folderRows += '<tr>';
            folderRows += createCol(folder.name);
            folderRows += createCol(folder.type);
            folderRows += createCol(folder.lastMod);
            folderRows += createCol("");
            folderRows += createCol("");
            folderRows += '</tr>';
        });
        $("#fileContainer").html($("#fileContainer").html() + folderRows);

        var fileRows = "";
        list_files.forEach( file => {
            fileRows += '<tr>';
            fileRows += createCol(file.name);
            fileRows += createCol(file.type);
            fileRows += createCol(file.lastMod);
            fileRows += createCol('<button class="btn btn-success" onclick="downloadFile(\''+ file.id + '\')">Download</button>');
            fileRows += createCol('<button class="btn btn-danger" onclick="deleteFile(\''+ file.id + '\')">Delete</button>');
            fileRows += '</tr>';
        });

        $("#fileContainer").html($("#fileContainer").html() + fileRows);
    });

});

function createCol(value) {
    return '<td>' + value + '</td>';
}

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
