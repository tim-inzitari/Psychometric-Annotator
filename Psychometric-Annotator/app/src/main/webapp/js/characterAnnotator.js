/* Defaults and Globals */

var viewer = null;
var latinList = ["a","b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","x","y","z","~","ⴈ","ꝑ","ꝓ", "ꝗ","ꝝ","ꝩ","ꝯ","dot","semi","","'","other"];
var hebrewList = ['א','ב','ג','ד','ה','ו','ז','י','כך','ל','מם','ן','ס','ע','פף','צץ','ק','ר','ש','ת',"dot","semi",'\'','other'];


classList = hebrewList
var anno = "";
var lineNo = -1;
var wordNo = -1;
var letterNo = -1;
var imgUrn;
var startTime;
//var tsrc = getTileSource



/* Main */
jQuery(function($){
    $('form').each(function() { this.reset() });
    var paramUrn;
    $('#input_form').hide();
    $('#image_imageContainer').hide();
    $('#loadImageButton').hide();
    $.post("URNServlet", {
        askResponse:"ask",
        type:"charAnn"
    },function(responseText){
        initializeKeyboad();
        var response = responseText.split("-");
        paramUrn = response[0];
        lineNo = response[1];
        wordNo = response[2];
        leterNo = response[3];
        setUpUI();
        imgUrn = paramUrn;
        console.log(responseText);
        imageDrawer();
    });
});

function setAnnoChar(index){
    $('#submitButton').show();
    var out = classList[index];
    if(out === "ꝝ"){
        $("#currentSelection").text("ℽ");
        anno = "ꝝ";
    }else if( out === "semi"){
        $("#currentSelection").text(";");
        anno = ";"
    }else if( out === "dot"){
        $("#currentSelection").text(".");
        anno = "."
    }else if( out === "other"){
        $("#currentSelection").text("other");
        anno = "?"
    }
    else{
        $("#currentSelection").text(out);
        anno = out;
    }

}

function initializeKeyboad(){
    var target = $("#annoKeyboard");
    console.log(classList.length)
    for(var x = 0; x < classList.length; x++){
        target.append("<img src=\"buttons/"+classList[x]+".png\" id = \""+classList[x]+"\" onclick = \"setAnnoChar("+x+")\" border=\"1\">");
    }
}

function setUpUI(){
    $('#loadImageButton').show();

}

function imageDrawer(){
    canvas = new fabric.Canvas('image_imageCanvas');

    var ctx = canvas.getContext("2d");
    var image = new Image();
    image.onload = function() {
        console.log(imgUrn);
        var loc = getImageLocation(imgUrn);
        var x1 = image.width * loc[0];
        var y1 = image.height * loc[1];
        var width = image.width * loc[2];
        var height = image.height * loc[3];
        var imageInstance = new fabric.Image(image, {
            left: -x1,
            top: -y1,
            angle: 0,
            opacity: 1,
            clipTo: function (ctx) {
                ctx.rect(x1 - (image.width / 2), y1 - (image.height / 2), width, height);
            }
        });
        // ,
        // lockMovementX: true,
        //     lockMovementY: true,
        //     lockRotation: true,
        //     lockScalingX: true,
        //     lockScalingY: true,
        //     lockSkewingX: true,
        //     lockSkewingY: true,
        //     hasBorders: false,
        //     hasControls: false
        canvas.add(imageInstance);
        canvas.renderAll();
    }
    image.src = getImageSource(imgUrn);
    canvas.setZoom(3);
    canvas.renderAll();
}


function getImageLocation(imgUrn){
    var splitUrn = imgUrn.split("@");
    if(splitUrn.length <= 1){
        return [0,0,1,1];
    }else{
        var vals = splitUrn[1].split(",");
        return [vals[0]+0,vals[1]+0,vals[2]+0,vals[3]+0]
    }
}

function getImageSource(imgUrn){
    var plainUrn = imgUrn.split("@")[0];
    var imgId = plainUrn.split(":")[4];
    var ts = "";
    var localDir = plainUrn.split(":")[0] + "_" + plainUrn.split(":")[1] + "_" + plainUrn.split(":")[2] + "_" + plainUrn.split(":")[3] + "_/";
    ts = "image_archive/"  + plainUrn + "_RAW.jpg";
    console.log();
    return ts;
}

function updateValue(){
    var x = document.getElementById("difficulty_range").value;
    document.getElementById("difficulty_view").innerHTML = x;
}

$('#loadImageButton').click(function() {
    $('#image_imageContainer').show();
    $('#loadImageButton').hide();
    $('#input_form').show();
    $('#submitButton').hide();
    startTime = new Date().getTime();
});

$('#submitButton').click(function() {
    $.post("URNServlet", {
        askResponse: "res",
        type:"anno",
        timer: new Date().getTime() - startTime,
        annotation: anno,
        difficulty: $('#difficulty_range').val(),
        lineNo: lineNo,
        wordNo: wordNo,
        letterNo: letterNo,
        urn: imgUrn
    },function(responseText){
        if(responseText === "TRUE") {
            location.reload();
        }else{
            window.location = "/index.html";
        }
    });
});