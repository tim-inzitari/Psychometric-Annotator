/* Defaults and Globals */

var viewer = null;

var defaultServerPath = "http://www.homermultitext.org/iipsrv?DeepZoom=/project/homer/pyramidal/VenA/";
var defaultServerSuffix = ".tif.dzi";
var defaultLocalpath = "image_archive/";
var defaultUrn = "";


var serverPath = defaultServerPath;
var serverSuffix = defaultServerSuffix;
var localPath = defaultLocalpath;
var localSuffix = ".dzi";
var usePath = localPath;
var useSuffix = localSuffix;

var useLocal = true;

var imgUrn;

var roiArray = [];
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
        paramUrn = responseText;
        setUpUI();
        imgUrn = paramUrn;
        console.log(responseText);
        imageDrawer();
    });
});

function setUpUI(){
    $('#loadImageButton').show();

}

function imageDrawer(){
    console.log("not");
    canvas = new fabric.Canvas('image_imageCanvas');
    console.log("here");
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
    canvas.setZoom(10);
    image.src = getImageSource(imgUrn);
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
    ts = "image_archive/" + localDir + imgId + "_RAW.jpg";
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
    startTime = new Date().getTime();
});

$(submitButton).click(function() {
    $.post("URNServlet", {
        askResponse: "res",
        type:"anno",
        timer: new Date().getTime() - startTime,
        annotation: $('#annobox').val(),
        difficulty: $('#difficulty_range').val()
    },function(responseText){
        if(responseText === "TRUE") {
            location.reload();
        }else{
            window.location = "/index.html";
        }
    });
});