$(document).ready(function(){

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
            folderRows += createCol('<button class="btn btn-primary upload-btn" data-folder="' + folder.id + '">Upload here</button>'
                + '<input id="' + getName(folder.name, "fd") + '" type="file" class="file-input" data-folder="' + folder.id + '" hidden />'
                + '<div class="spinner-border text-primary" role="status" data-folder="' + folder.id + '" hidden> <span class="visually-hidden">Loading...</span></div>');
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
            fileRows += createCol('<button id="' + getName(file.name, "dw") + '" class="btn btn-success" onclick="downloadFile(\''+ file.id + '\')">Download</button>');
            fileRows += createCol('<button id="' + getName(file.name, "del") + '"class="btn btn-danger" onclick="deleteFile(\''+ file.id + '\')">Delete</button>');
            fileRows += '</tr>';
        });

        $("#fileContainer").html($("#fileContainer").html() + fileRows);
        $('.upload-btn').on('click', function () {
            const folder = $(this).data('folder');
            const input = $('.file-input[data-folder="' + folder + '"]');
            input.click();
        });

        $('.file-input').on('change', function () {
            const folder = $(this).data('folder');
            const file = this.files[0];
            if (file) {
                uploadFile(file, folder);
            }
        });
    });

});

function getName(name, mod) {
    return name.replace(/ /g, "_") + '_' + mod;
}

function createCol(value) {
    return '<td>' + value + '</td>';
}

function deleteFile(fileId) {
    $.ajax({
        url:'/delete/' + fileId,
        method: 'DELETE'
    }).done(function(){
        alert('File has been deleted')
        $("#refreshButton").click();
    });
}

function downloadFile(fileId) {
    // Navigate to the download endpoint directly
    window.location.href = '/download/' + fileId;
}

function uploadFile(file, folder) {
    const spinner = $('.spinner-border[data-folder="' + folder + '"]');
    spinner.removeAttr('hidden');
    const formData = new FormData();
    formData.append('file', file);
    formData.append('folderId', folder);
    $.ajax({
        url: '/upload',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
            console.log('Success:', response);
            alert('File uploaded to ' + folder);
            $("#refreshButton").click();
        },
        error: function (error) {
            console.error('Error:', error);
            alert('Failed to upload to ' + folder);
            $("#refreshButton").click();
        }
    });
}
