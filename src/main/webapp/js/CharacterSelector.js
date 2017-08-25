/**
 * Created by smgri on 6/21/2017.
 */
/* Defaults and Globals */



var canvas  = null;
var activeAnnotation = 0;
var annotationList = [];
var redo = null;
var colorList = ['red','blue','green','yellow'];


// var viewer = null;
//
// var defaultServerPath = "http://www.homermultitext.org/iipsrv?DeepZoom=/project/homer/pyramidal/VenA/";
// var defaultServerSuffix = ".tif.dzi";
// var defaultLocalpath = "image_archive/";
// var defaultUrn = "";
//
// var serverPath = defaultServerPath;
// var serverSuffix = defaultServerSuffix;
// var localPath = defaultLocalpath;
// var localSuffix = "_RAW.jpg";
// var usePath = localPath;
// var useSuffix = localSuffix;
//
// var useLocal = true;
//
// var imgUrn;
//
//
// var roiArray = [];
//var tsrc = getTileSource


/* Main */
jQuery(function($){
    $('form').each(function() { this.reset() });
    var paramUrn;
    imgUrn = "urn:cite2:ASAV:vaimg.v1:REG_VAT12_001r@0.3703,0.2692,0.08287,0.01594";
    imageDrawer();
    // $.post("URNServlet", {
    //     askResponse:"ask",
    //     type:"characterSelector",
    // },function(responseText){
    //     $('#image_imageContainer').hide();
    //     paramUrn = responseText;
    //     imgUrn = "urn:cite2:ASAV:vaimg.v1:REG_VAT12_001r@0.3703,0.2692,0.08287,0.01594";
    //     $("#image_imageContainer").append("<img id = \"raw_document\" src = \"" + getImageSource(imgUrn) + "\" visible = \"False\"></img>");
    //     imageDrawer();
    // });
});

//loads in the image, crops it so that only the region of interest is visible, and sets up the annotation tools.
function imageDrawer(){
    document.getElementById("zoomInput").value = 1;
    document.getElementById("brushInput").value = 1;
    canvas = new fabric.Canvas('image_imageCanvas');
    canvas.onChange = canvas.renderAll.bind(canvas)
    annotationList.push(new Annotation(1,new fabric.Group()));
    canvas.add(annotationList[0].group);
    canvas.on('path:created', function(e){
        var your_path = e.path;
        var AA = annotationList[activeAnnotation].group;
        AA.addWithUpdate(your_path);
        canvas.remove(your_path);
        AA.bringToFront();
    });
    var ctx = canvas.getContext("2d");
    var image = new Image();
    image.onload = function() {
        console.log("W: " + image.width);
        console.log("H: " + image.height);
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
             },
            lockMovementX: true,
            lockMovementY: true,
            lockRotation: true,
            lockScalingX: true,
            lockScalingY: true,
            lockSkewingX: true,
            lockSkewingY: true,
            hasBorders: false,
            hasControls: false
        });
        canvas.add(imageInstance);
        imageInstance.selectable = true;
        canvas.renderAll();
        canvas.isDrawingMode = true
    }
    image.src = getImageSource(imgUrn);








    // $.jCanvas.defaults.fromCenter = false;
    // //$.jCanvas.defaults.cropFromCenter = false;
    // var $canvas = $("#image_imageCanvas");
    //
    // var image = new Image();
    //
    // var loc = getImageLocation(imgUrn);
    // var magic = Math.max(image.height, image.width);
    // //$(image).onready(function(){
    //     var x1 = image.width * loc[0];
    //     var y1 = image.height * loc[1];
    //     var width = image.width * loc[2];
    //     var height = image.height * loc[3];
    //     // $canvas.attr( 'width', width*5);
    //     // $canvas.attr( 'height',height*5);
    //     $canvas.drawImage({
    //         source: image,
    //         sx: x1, sy:y1,
    //         sWidth: width,
    //         sHeight: height,
    //     });
    //     //$canvas.image
    //
    // //});
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
    console.log("1: " + localDir);
    ts = "image_archive/" + localDir + imgId + "_RAW.jpg";
    console.log("2: " + ts);
    return ts;
}

function updateZoomValue(){
    //$("#image_imageCanvas").css("size","value")
    canvas.setZoom(document.getElementById("zoomInput").value);
}
function updateBrushValue(){
    canvas.freeDrawingBrush.width = document.getElementById("brushInput").value
}

function updateAnnoValue(){
    changeAnnotation(document.getElementById("active_annotation").value-1);
}

function Annotation(number, group){
    this.number = number;
    this.group = group;
}

$('#new_annotation_button').click(function(){
    var size = annotationList.length;
    console.log("Adding Annotation # " + size);
    annotationList.push(new Annotation(size,new fabric.Group()));
    canvas.add(annotationList[size].group);
    $('#active_annotation').append(
        $('<option></option>').val(size+1).html(size+1)
    );
    changeAnnotation(size);
});



$('#delete_active_annotation').click(function(){
    var AA = annotationList[activeAnnotation].group;
    canvas.remove(AA);
    annotationList[activeAnnotation].group = new fabric.Group();
    canvas.add(annotationList[activeAnnotation].group);
    rerenderThatActuallyWorks();
});

$('#undo').click(function(){
    var AA = annotationList[activeAnnotation].group;
    var obj = AA.getObjects();
    console.log(obj.length);
    var undo = obj[obj.length-1];
    console.log(undo);
    console.log(undo.toDataURL());
    AA.remove(undo);
    rerenderThatActuallyWorks();
});

function changeAnnotation(target){

    document.getElementById("active_annotation").value = target+1;
    annotationList[activeAnnotation].group.forEachObject(function(path) {
        path.stroke = colorList[activeAnnotation % 4];
        path.dirty = true;
    });
    activeAnnotation = target;
    annotationList[activeAnnotation].group.forEachObject(function(path) {path.stroke = 'black';});
    var temp = new fabric.Rect({
        left: 100,
        top: 100,
        fill: 'red',
        width: 20,
        height: 20
    })
    rerenderThatActuallyWorks();
}

function rerenderThatActuallyWorks(){
    canvas.setZoom(document.getElementById("zoomInput").value-.01);
    canvas.renderAll();
    canvas.setZoom(document.getElementById("zoomInput").value);
}

function getRelativeCooridnates(group){
    var doc = canvas.getObjects()[0];
    var docx = doc.oCoords.tl.x;
    var docy = doc.oCoords.tl.y;
    var annx = group.oCoords.tl.x;
    var anny = group.oCoords.tl.y;
    var x = annx - docx;
    var y = anny - docy;
    return[x,y]
}

$("#submitButton").click(function() {
    // var outArray = new Array(roiArray.length);
    // for(x = 0; x < roiArray.length; x++){
    //     outArray[x] = imgUrn + "@" + roiArray[x].roi;
    // }
    // $.post("URNServlet", {
    //     askResponse: "res",
    //     type:"char",
    //     data: JSON.stringify(outArray)
    // },function(responseText){
    //     if(responseText === "TRUE") {
    //         location.reload();
    //     }else{
    //         window.location = "/index.html";
    //     }
    // });
    canvas.setZoom(1);
    for(x = 0; x < annotationList.length; x++){
        console.log(annotationList[x].group.toDataURL());
        var temp = getRelativeCooridnates(annotationList[x].group);
        console.log(temp[0]);
        console.log(temp[1]);
    }
    canvas.setZoom(document.getElementById("zoomInput").value);
});
