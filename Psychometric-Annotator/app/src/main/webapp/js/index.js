/**
 * Created by smgri on 6/20/2017.
 */

$(document).ready(function() {
    $.post("URNServlet", {
        askResponse: "init",
    },function(responseText){
        console.log(responseText)
        var output = responseText.split(",");
        if(output[0] === "TRUE"){
            $('#lines').show();
        }/*
        if(output[1] === "TRUE"){
            $('#words').show();
        }
        if(output[2] === "TRUE"){
            $('#characters').show();
        }*/
        if(output[3] === "TRUE"){
            $('#letters').show();
        }
    });
});


