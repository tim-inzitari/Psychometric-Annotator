/**
 * Created by smgri on 6/21/2017.
 */
/* Defaults and Globals */



var canvas  = null;
var wordNo = -1;
var lineNo = -1;
var activeAnnotation = 0;
var mainGroup = null;
var annotationList = [];
var annotationChars = [];
var classList = ["a","b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","x","y","z","~","ⴈ","ꝑ","ꝓ", "ꝗ","ꝝ","ꝩ","ꝯ","dot","semi","_","'","other"];
var colorList =  ["#f23568","#6d38ff","#38ffd7","#fff238","#661641","#275fb3","#24a669","#a67b24","#ff38a2",
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
        type:"characterselector"
    },function(responseText){
        console.log(responseText);
        response = responseText.split("-");
        paramUrn = response[0];
        lineNo = response[1];
        wordNo = response[2];
        console.log("line: " + lineNo);
        console.log("word: " + wordNo);
        imgUrn = paramUrn;
        console.log(imgUrn);
        imageDrawer();
        initializeKeyboad(this);
    });
});

//loads in the image, crops it so that only the region of interest is visible, and sets up the annotation tools.
function imageDrawer(){
    document.getElementById("zoomInput").value = 1;
    document.getElementById("brushInput").value = 1;
    canvas = new fabric.Canvas('image_imageCanvas');
    canvas.on('selection:cleared', function() {
        cosole.log(";)")
    });
    canvas.onChange = canvas.renderAll.bind(canvas)
    annotationList.push(new Annotation(1,new fabric.Group()));
    annotationChars.push(null);
    canvas.add(annotationList[0].group);
    canvas.backgroundColor = 'black';
    canvas.on('path:created', function(e){
        var your_path = e.path;
        var AA = annotationList[activeAnnotation].group;
        AA.addWithUpdate(your_path);
        canvas.remove(your_path);
        AA.bringToFront();
    });
    mainGroup = new fabric.Group([],{left: 0, top: 0});
    canvas.add(mainGroup);
    var ctx = canvas.getContext("2d");
    var image = new Image();
    image.onload = function() {
        var loc = getImageLocation(imgUrn);
        var x1 = image.width * loc[0];
        var y1 = image.height * loc[1];
        var width = image.width * loc[2];
        var height = image.height * loc[3];

        var background = new fabric.Image(image, {
            left: -x1,
            top: -y1,
            angle: 0,
            opacity: .5
        });

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
        mainGroup.add(background);
        mainGroup.addWithUpdate(imageInstance);
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
    annotationChars.splice(activeAnnotation+1,0, null);
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
    $('#'+target).css('background-color','black');
    annotationList[target].group.forEachObject(function(path) {
        path.stroke = 'black' ;
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

function setAnnoChar(index){

    var out = classList[index];
    if(out === "ꝝ"){
        annotationChars[activeAnnotation] = "ꝝ";
        $('#'+activeAnnotation).text("ℽ");
    }else if( out === "semi"){
        annotationChars[activeAnnotation] = ";";
        $('#'+activeAnnotation).text(";");
    }else if( out === "dot"){
        $('#'+activeAnnotation).text(".");
        annotationChars[activeAnnotation] =  "."
    }else if( out === "other"){
        $('#'+activeAnnotation).text("other");
        annotationChars[activeAnnotation] =  "?"
    }
    else{
        annotationChars[activeAnnotation] = out;
        $('#'+activeAnnotation).text(out);
    }
}

$("#delete_active_annotation").click(function(){
    clearAnnotation(activeAnnotation);
    if(annotationList.length > 1) {
        canvas.remove(annotationList[activeAnnotation]);
        annotationList.splice(activeAnnotation, 1);
        annotationChars.splice(activeAnnotation, 1);
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
    var zoom = canvas.getZoom();
    canvas.setZoom(1);
    var doc = canvas.getObjects()[0];
    var docx = doc.oCoords.tl.x;
    var docy = doc.oCoords.tl.y;
    var annx = group.oCoords.tl.x;
    var anny = group.oCoords.tl.y;
    var x = annx - docx;
    var y = anny - docy;
    canvas.setZoom(zoom);
    return[x,y];
}

function generateURN(group){
    var zoom = canvas.getZoom();
    var doc = canvas.getObjects()[0];
    var docHeight = doc.height;
    var docWidth = doc.width;
    var outWidth = Math.round(((group.width+4)/docWidth)*10000)/10000;
    var outHeight = Math.round(((group.height+4)/docHeight)*10000)/10000;
    var xy = getRelativeCooridnates(group);
    var outX = Math.round(((xy[0]-2)/docWidth)*10000)/10000;
    var outY = Math.round(((xy[1]-2)/docHeight)*10000)/10000;
    var plainUrn = imgUrn.split("@")[0];
    canvas.setZoom(zoom);
    return plainUrn +"@"+outX+","+outY+","+outWidth+","+outHeight;
}

function initializeKeyboad(){
    var target = $("#annoKeyboard");
    console.log(classList.length)
    for(var x = 0; x < classList.length; x++){
        target.append("<img src=\"buttons/"+classList[x]+".png\" id = \""+classList[x]+"\" onclick = \"setAnnoChar("+x+")\" border=\"1\">");
    }
}


function buildAnnoString(){
    console.log(annotationChars.length);
    var temp = [];
    for(var x = 0; x < annotationChars.length; x++){
        if(annotationChars[x] === null){
            temp[x]="-";
        } else if(annotationChars[x] === "ꝝ"){
            temp[x] = "ℽ"
        } else{
            temp[x] = annotationChars[x]
        }
    }
    return temp.join("");
}

function validateAnnoString(){
    for(var x = 0; x < annotationChars.length; x++){
        if(annotationChars[x] === null){
            window.alert("Please select a character for the positions marked by dashes: " + buildAnnoString());
            return false;
        }
    }
    return true;
}

function validateAnnoImage(){
    for(var x = 0; x < annotationList.length; x++){
        if(annotationList[x].group.getObjects().length <= 0){
            window.alert("Black annotation at position " + (x+1));
            return false;
        }
    }
    return true;
}

$("#submitButton").click(function() {
    if(validateAnnoString() & validateAnnoImage()) {
        $('#image_imageContainer').hide();
        canvas.setZoom(1);
        var outArray = [];
        var xArray = [];
        var yArray = [];
        for (var x = 0; x < annotationList.length; x++) {
            if (annotationList[x].group.getObjects().length > 0) {
                annotationList[x].group.forEachObject(function (path) {
                    path.stroke = 'black';
                });
                var temp = getRelativeCooridnates(annotationList[x].group);
                xArray[x] = temp[0];
                yArray[x] = temp[1];
                outArray.push(generateURN(annotationList[x].group));
            } else {
                annotationList.splice(x, 1);
                annotationChars.splice(x, 1);
                x--;
            }
        }
        canvas.setZoom(1.01);
        canvas.setZoom(1);
        submitPost(0, outArray, xArray, yArray);
    }
});


function submitPost(x,outArray,xArray,yArray){
    canvas.setZoom(1);
    if(x === annotationList.length){
        $.post("URNServlet", {
            askResponse: "res",
            annotation: buildAnnoString(),
            type:"char",
            data: JSON.stringify(outArray),
            wordNo: wordNo,
            lineNo: lineNo
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
            urn:imgUrn,
            id:x,
            x:xArray[x],
            y:yArray[x],
            data:imgRet,
            wordNo: wordNo,
            lineNo: lineNo
        },function(){
            submitPost(x+1,outArray,xArray,yArray);
        });
    }
}
