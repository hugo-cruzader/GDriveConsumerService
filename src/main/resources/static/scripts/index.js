$(document).ready(function(){

    $("#simpleUpload").click(function(){
        $.ajax({
            url: '/create',
            success: function() {
                alert("File uploaded complete");
            }
        });
    });


    $("#refreshFileButton").click(function(){
        $.ajax({
            url: '/listfiles',
            }).done(function(data) {
            console.dir(data);
            var fileHTML = "";
            for(file of data) {
                fileHTML += '<ul class="list-group list-group-horizontal" >';
                fileHTML += '<li class="list-group-item">' + file.name + '</li>'
                fileHTML += '<li class="list-group-item">' + file.type + '</li>'
                fileHTML += '<li class="list-group-item">' + new Date(file.lastModifiedDate.value).toDateString() + '</li>'
                fileHTML += '<li class="list-group-item">'
                    + '<button class="btn btn-danger" onclick="deleteFile(\''+ file.id + '\')">Delete</button></li>';
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
