/**
 * Created by smgri on 6/21/2017.
 */
/* Defaults and Globals */



var canvas  = null;
var activeAnnotation = 0;
var annotationList = [];
var redo = null;
var colorList =  ["#463827","#f23568","#6d38ff","#38ffd7","#fff238","#661641","#275fb3","#24a669","#a67b24","#ff38a2",
    "#194973","#35f268","#7f441c","#801c79","#2a8ebf","#216616","#d97330","#da32e6","#196d73","#bdff38","#bf3e2a",
    "#3d1973","#30cdd9","#858c1f","#661616"];


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
    // $('form').each(function() { this.reset() });
    // var paramUrn;
    // imgUrn = "urn:cite2:ASAV:vaimg.v1:REG_VAT12_001r@0.3703,0.2692,0.08287,0.01594";
    // imageDrawer();
    $.post("URNServlet", {
        askResponse:"ask",
        type:"characterSelector"
    },function(responseText){
        paramUrn = responseText;
        imgUrn = paramUrn;
        console.log(imgUrn);
        imageDrawer();
    });
});

//loads in the image, crops it so that only the region of interest is visible, and sets up the annotation tools.
function imageDrawer(){
    document.getElementById("zoomInput").value = 1;
    document.getElementById("brushInput").value = 1;
    canvas = new fabric.Canvas('image_imageCanvas');

    canvas.onChange = canvas.renderAll.bind(canvas)
    annotationList.push(new Annotation(1,new fabric.Group()));
    canvas.add(annotationList[0].group);
    canvas.backgroundColor = 'black';
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
        canvas.isDrawingMode = true;
        canvas.freeDrawingBrush.color = 'white';
    }
    image.src = getImageSource(imgUrn);
    rerenderThatActuallyWorks();
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
    annotationList.splice(activeAnnotation+1,0, new Annotation(activeAnnotation+1,new fabric.Group()));
    canvas.add(annotationList[activeAnnotation+1].group);
    $('#active_annotation').append(
        $('<option></option>').val(size+1).html(size+1)
    );
    var newLI = '<li id ="' + size + '" onmouseenter = "highlightAnno(this)" onmouseleave = "unhighlightAnno(this)" onclick = "changeAnnotation(parseInt(this.id))">' + (size+1) + '</li>'
    for(var x = activeAnnotation+1; x < annotationList.length; x++){
        annotationList[x].group.forEachObject(function(path){
            path.stroke = colorList[x % colorList.length];
        });
    }
    $("#image_urnList").append(newLI);
    $("#"+size).css("background-color",colorList[size % colorList.length]);
    changeAnnotation(activeAnnotation+1);
});



$('#clear_active_annotation').click(function(){
    clearAnnotation(activeAnnotation);
});

function clearAnnotation(anno){
    var AA = annotationList[anno].group;
    canvas.remove(AA);
    annotationList[anno].group = new fabric.Group();
    canvas.add(annotationList[anno].group);
    rerenderThatActuallyWorks();
}

$('#undo').click(function(){
    var AA = annotationList[activeAnnotation].group;
    console.log(AA.toDataURL());
    var obj = AA.getObjects();
    AA.removeWithUpdate(obj[obj.length-1]);
    console.log(AA.toDataURL());
    rerenderThatActuallyWorks();
});

function changeAnnotation(target){
    document.getElementById("active_annotation").value = target+1;
    annotationList[activeAnnotation].group.forEachObject(function(path){
        path.stroke = colorList[activeAnnotation % colorList.length];
    });
    $("#"+activeAnnotation).css("background-color",colorList[activeAnnotation % colorList.length]);
    activeAnnotation = target;
    annotationList[activeAnnotation].group.forEachObject(function(path) {path.stroke = 'white';});
    $("#"+activeAnnotation).css("background-color","white");
    rerenderThatActuallyWorks();
}

function highlightAnno(anno){
    var target = parseInt(anno.id);
    $('#'+target).css('background-color','RebeccaPurple');
    annotationList[target].group.forEachObject(function(path) {
        path.stroke = 'RebeccaPurple' ;
    });
    rerenderThatActuallyWorks();
}

function unhighlightAnno(anno){
    var target = parseInt(anno.id);
    $('#'+target).css('background-color',colorList[target % colorList.length]);

    if(target === activeAnnotation){
        $('#'+target).css('background-color','white');
        annotationList[target].group.forEachObject(function(path) {
            path.stroke = 'white';
        });
    }else{
        $('#'+target).css('background-color',colorList[target % colorList.length]);
        annotationList[target].group.forEachObject(function(path) {
            path.stroke = colorList[target % colorList.length];
        });
    }
    rerenderThatActuallyWorks();
}

$("#delete_active_annotation").click(function(){
    clearAnnotation(activeAnnotation);
    if(annotationList.length > 1) {
        canvas.remove(annotationList[activeAnnotation]);
        annotationList.splice(activeAnnotation, 1);
        $('#' + activeAnnotation).remove();
        for (var x = activeAnnotation; x <= annotationList.length; x++) {
            $('#' + x).text(x);
            $('#' + x).prop("id", x - 1);
        }
        activeAnnotation = Math.max(activeAnnotation - 1, 0);
        changeAnnotation(activeAnnotation);
    }
});




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
    return[x,y];
}

function generateURN(group){
    var doc = canvas.getObjects()[0];
    var docHeight = doc.height;
    var docWidth = doc.width;
    var outWidth = Math.round(((group.width+4)/docWidth)*10000)/10000;
    var outHeight = Math.round(((group.height+4)/docHeight)*10000)/10000;
    var xy = getRelativeCooridnates(group);
    var outX = Math.round(((xy[0]-2)/docWidth)*10000)/10000;
    var outY = Math.round(((xy[1]-2)/docHeight)*10000)/10000;
    var plainUrn = imgUrn.split("@")[0];
    return plainUrn +"@"+outX+","+outY+","+outWidth+","+outHeight;
}


$("#submitButton").click(function() {
    canvas.setZoom(1);
    var outArray = [];
    var xArray = [];
    var yArray = [];
    for(var x = 0; x < annotationList.length; x++){
        //console.log(annotationList[x].group.toDataURL());
        var temp = getRelativeCooridnates(annotationList[x].group);
        xArray[x] = temp[0];
        yArray[x] = temp[1];
        outArray.push(generateURN(annotationList[x].group));
    }
    console.log(JSON.stringify(outArray))
    submitPost(0,outArray,xArray,yArray);
    canvas.setZoom(document.getElementById("zoomInput").value);
});

function submitPost(x,outArray,xArray,yArray){
    if(x === annotationList.length){
        $.post("URNServlet", {
            askResponse: "res",
            annotation: $('#full_text_annotation').attr("value"),
            type:"char",
            data: JSON.stringify(outArray)
        },function(responseText){
            if(responseText === "TRUE") {
                location.reload();
            }else{
                window.location = "/index.html";
            }
        });
    }else{
        var imgRet = annotationList[x].group.toDataURL();
        $.post("URNServlet",{
            askResponse: "img",
            x:xArray[x],
            y:yArray[x],
            data:imgRet
        },function(){
            submitPost(x+1,outArray,xArray,yArray);
        });
    }
}
