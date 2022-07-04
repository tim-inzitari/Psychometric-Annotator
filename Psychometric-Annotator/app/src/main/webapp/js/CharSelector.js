/**
 * Created by smgri on 6/21/2017.
 */
/* Defaults and Globals */

var viewer = null;

var defaultServerPath = "http://www.homermultitext.org/iipsrv?DeepZoom=/project/homer/pyramidal/VenA/";
var defaultServerSuffix = ".tif.dzi";
var defaultLocalpath = "image_archive/";
var defaultUrn = "";
var lineNo = -1


var serverPath = defaultServerPath;
var serverSuffix = defaultServerSuffix;
var localPath = defaultLocalpath;
var localSuffix = ".dzi";
var usePath = localPath;
var useSuffix = localSuffix;

var useLocal = true;

var imgUrn;


var roiArray = [];
var clipRect = null;

//var tsrc = getTileSource





/* Main */
jQuery(function($){

    var paramUrn;

    $.post("URNServlet", {
            askResponse:"ask",
            type:"characterselector"
        },function(responseText){
        $('#image_imageContainer').hide();
        console.log(responseText);
        var response = responseText.split("-");
        paramUrn = response[0];
        lineNo = response[1];
        wordNo = response[2];
        console.log(lineNo);
        console.log(wordNo);
        console.log(paramUrn);
        setUpUI();
        imgUrn = paramUrn;
        initOpenSeadragon();
    });
});


function initOpenSeadragon() {

    if (viewer != null){
        viewer.destroy();
        viewer = null
    }
    console.log(imgUrn);
    var location = imgUrn.split("@")[1].split(",");
    console.log('word location: '); 
    console.log(location);

    viewer = OpenSeadragon({
        id: 'image_imageContainer',
        prefixUrl: 'css/images/',
        crossOriginPolicy: "Anonymous",
        defaultZoomLevel: 1,
        tileSources: getTileSources(imgUrn),
        minZoomImageRatio: 0.1, // of viewer size
        maxZoomLevel: 16,

        immediateRender: false
    });


    //viewer.setClip();



    viewer.addHandler('full-screen', function (viewer) {
        refreshRois();
    })

    // Openseadragon does not have a ready() function, so here we are…
    setTimeout(function(){
        //loadDefaultROI(imgUrn)
        if (imgUrn.split("@").length > 1) {
            var tiledImage = viewer.world.getItemAt(0);
            var normH = tiledImage.getBounds().height;
            var normW = tiledImage.getBounds().width;
            var roi = imgUrn.split("@");
            var point1 = tiledImage.viewportToImageCoordinates(+location[0]*normW, +location[1]*normH, true);
            var point2 = tiledImage.viewportToImageCoordinates(+location[2]*normW, +location[3]*normH, true);
            clipRect = new OpenSeadragon.Rect(point1.x,point1.y,point2.x,point2.y);
            tiledImage.setClip(clipRect);
            viewer.viewport.fitBoundsWithConstraints(viewer.viewport.imageToViewportRectangle(clipRect));
            imgUrn = imgUrn.split("@")[0];
            $('#image_imageContainer').show();
        }
    },1000);


    // Guides plugin
    viewer.guides({
        allowRotation: false,        // Make it possible to rotate the guidelines (by double clicking them)
        horizontalGuideButton: null, // Element for horizontal guideline button
        verticalGuideButton: null,   // Element for vertical guideline button
        prefixUrl: "css/images/",             // Images folder
        removeOnClose: false,        // Remove guidelines when viewer closes
        useSessionStorage: false,    // Save guidelines in sessionStorage
        navImages: {
            guideHorizontal: {
                REST: 'guidehorizontal_rest.png',
                GROUP: 'guidehorizontal_grouphover.png',
                HOVER: 'guidehorizontal_hover.png',
                DOWN: 'guidehorizontal_pressed.png'
            },
            guideVertical: {
                REST: 'guidevertical_rest.png',
                GROUP: 'guidevertical_grouphover.png',
                HOVER: 'guidevertical_hover.png',
                DOWN: 'guidevertical_pressed.png'
            }
        }
    });

    //selection plugin
    selection = viewer.selection({
        restrictToImage: false,
        onSelection: function(rect) {
            createROI(rect);
        }
    });

}


function loadDefaultROI(imgUrn){
    if (imgUrn.split("@").length > 1){
        var newRoi = imgUrn.split("@")[1];
        var newGroup = getGroup(roiArray.length+1);
        var roiObj = {index: roiArray.length, roi: newRoi, mappedUrn: imgUrn, group: newGroup.toString()};
        roiArray.push(roiObj);
        addRoiOverlay(roiObj);
        addRoiListing(roiObj);
    }
}

function createROI(rect){

    var normH = viewer.world.getItemAt(0).getBounds().height;
    var normW = viewer.world.getItemAt(0).getBounds().width;
    roiRect = viewer.viewport.imageToViewportRectangle(rect);
    var rl = roiRect.x / normW;
    var rt = roiRect.y / normH;
    var rw = roiRect.width / normW;
    var rh = roiRect.height / normH;
    var newRoi = rl.toPrecision(4) + "," + rt.toPrecision(4) + "," + rw.toPrecision(4) + "," + rh.toPrecision(4);
    var newUrn = imgUrn + "@" + newRoi;
    var newGroup = getGroup(roiArray.length+1);
    var roiObj = {index: roiArray.length, roi: newRoi, mappedUrn: newUrn, group: newGroup.toString()};
    roiArray.push(roiObj);
    addRoiOverlay(roiObj);
    addRoiListing(roiObj);
}

function addRoiListing(roiObj){
    // image_urnList
    var idForListing = idForMappedUrn(roiObj.index);
    var idForRect = idForMappedROI(roiObj.index);
    var groupClass = "image_roiGroup_" + roiObj.group;
    var deleteLink = "<a class='deleteLink' id='delete" + idForListing + "' data-index='" + roiObj.index + "'></a>";
    var mappedUrnSpan = "<li class='" + groupClass + "' id='" + idForListing + "'>";
    mappedUrnSpan += deleteLink + roiObj.mappedUrn + "</li>";

    $("#image_urnList").append(mappedUrnSpan);
    // <a class="image_deleteUrn">✖︎</a>
    $("li#" + idForListing ).on("click",function(){
        if ( $(this).hasClass("image_roiGroupSelected")){
            removeAllHighlights();
        } else {
            removeAllHighlights();
            $(this).addClass("image_roiGroupSelected");
            var rectId = urnToRoiId(this.id);
            $("a#"+rectId).addClass("image_roiGroupSelected");
        }
    });

    $("a#delete"+idForListing).on("click",function(){
        var tid = $(this).prop("id")
        var i = tid.replace("deleteimage_mappedUrn_","")
        deleteRoi(parseInt(i))
    });
}

function deleteRoi(c){
    var tempArray = []
    for (i = 0; i < roiArray.length; i++){
        if (i != c){
            tempArray.push(roiArray[i]);
        }
    }
    clearJsRoiArray()
    for (i = 0; i < tempArray.length; i++){
        var newGroup = getGroup(i+1);
        var roiObj = {index: i, roi: tempArray[i].roi, mappedUrn: tempArray[i].mappedUrn, group: newGroup.toString()};
        roiArray.push(roiObj);
        addRoiOverlay(roiArray[i]);
        addRoiListing(roiArray[i]);
    }
}

function refreshRois(){
    var tempArray = []
    for (i = 0; i < roiArray.length; i++){
        tempArray.push(roiArray[i]);
    }
    clearJsRoiArray()
    for (i = 0; i < tempArray.length; i++){
        var newGroup = getGroup(i+1);
        var roiObj = {index: i, roi: tempArray[i].roi, mappedUrn: tempArray[i].mappedUrn, group: newGroup.toString()};
        roiArray.push(roiObj);
        addRoiOverlay(roiArray[i]);
        addRoiListing(roiArray[i]);
    }
}


//get request parameter
function get(name){
    if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
        return decodeURIComponent(name[1]);
}

function addRoiOverlay(roiObj){
    var normH = viewer.world.getItemAt(0).getBounds().height;
    var normW = viewer.world.getItemAt(0).getBounds().width;
    var roi = roiObj.roi;
    var rl = +roi.split(",")[0];
    var rt = +roi.split(",")[1];
    var rw = +roi.split(",")[2];
    var rh = +roi.split(",")[3];
    var tl = rl * normW;
    var tt = rt * normH;
    var tw = rw * normW;
    var th = rh * normH;
    var osdRect = new OpenSeadragon.Rect(tl,tt,tw,th);
    var elt = document.createElement("a");
    elt.id = idForMappedROI(roiObj.index);
    elt.className = "image_mappedROI" + " image_roiGroup_" + roiObj.group + " " + idForMappedUrn(roiObj.index);
    elt.dataset.urn = roiObj.mappedUrn;

    viewer.addOverlay(elt,osdRect);

    $("a#" + elt.id ).on("click",function(){
        if ( $(this).hasClass("image_roiGroupSelected")){
            removeAllHighlights();
        } else {
            removeAllHighlights();
            $(this).addClass("image_roiGroupSelected");
            var liId = roiToUrnId(this.id);
            $("li#"+liId).addClass("image_roiGroupSelected");
        }
    });
}

function removeAllHighlights(){
    for (i = 0; i < roiArray.length; i++){
        var liId = idForMappedUrn(i)
        var rectId = idForMappedROI(i)
        $("li#"+liId).removeClass("image_roiGroupSelected");
        $("a#"+rectId).removeClass("image_roiGroupSelected");
    }
}

function clearJsRoiArray() {
    for (i = 0; i < roiArray.length; i++){
        var tid = "image_mappedROI_" + i
        viewer.removeOverlay(tid)
        //$("a#" + tid).remove()
    }
    roiArray = []
    $("#image_urnList").empty()

}

function idForMappedUrn(i) {
    var s = "image_mappedUrn_" + (i)
    return s
}

function idForMappedROI(i) {
    var s = "image_mappedROI_" + (i)
    return s
}

function roiToUrnId(id) {
    var s = id.replace("image_mappedROI_","image_mappedUrn_")
    return s
}

function urnToRoiId(id) {
    var s = id.replace("image_mappedUrn_","image_mappedROI_")
    return s
}

function getGroup(i){

    var colorArray = ["#f23568", "#6d38ff", "#38ffd7", "#fff238", "#661641", "#275fb3", "#24a669", "#a67b24", "#ff38a2", "#194973", "#35f268", "#7f441c", "#801c79", "#2a8ebf", "#216616", "#d97330", "#da32e6", "#196d73", "#bdff38", "#bf3e2a", "#3d1973", "#30cdd9", "#858c1f", "#661616"	]

    var limit = colorArray.length
    rv = i % limit

    return rv

}

function reloadImage(){
    clearJsRoiArray()
    initOpenSeadragon()
}

function setUpUI() {

    $("div#serverConfigs").hide()
    $("div#localConfigs").show()
    $("#browse_onoffswitch").prop("checked",false)
    $("input#image_serverUrlBox").prop("value",serverPath)
    $("input#image_serverSuffixBox").prop("value",serverSuffix)
    $("input#image_localPathBox").prop("value",localPath)

    $("button#image_changeUrn").on("click", function(){
        var newUrn = $("input#image_urnBox").prop("value").trim()
        imgUrn = newUrn
        reloadImage();
    });

    $("input#image_serverUrlBox").change(function(){
        serverPath = $(this).prop("value")
    });
    $("input#image_serverSuffixBox").change(function(){
        serverSuffix = $(this).prop("value")
    });
    $("input#image_localPathBox").change(function(){
        localPath = $(this).prop("value")
    });

    $("input#image_urnBox").prop("value",imgUrn)

    $("#browse_onoffswitch").on("click", function(){
        if ( $(this).prop("checked") ){
            useLocal = false
            usePath = serverPath
            useSuffix = serverSuffix
            $("div#serverConfigs").show()
            $("div#localConfigs").hide()
            reloadImage()
        } else {
            useLocal = true
            usePath = localPath
            useSuffix = localSuffix
            $("div#serverConfigs").hide()
            $("div#localConfigs").show()
            reloadImage()
        }
    } );
}



function getTileSources(imgUrn){
    var plainUrn = imgUrn.split("@")[0]
    var imgId = plainUrn.split(":")[4]
    var ts = ""
    useLocal=false
    if (useLocal){
        var localDir = plainUrn.split(":")[0] + "_" + plainUrn.split(":")[1] + "_" + plainUrn.split(":")[2] + "_" + plainUrn.split(":")[3] + "_/"
        ts = usePath + localDir + imgId + useSuffix
    } else {
        ts = usePath + plainUrn + useSuffix
    }
    return ts
}

$('#submitButton').click(function() {
    var outArray = new Array(roiArray.length);
    for(x = 0; x < roiArray.length; x++){
        outArray[x] = imgUrn + "@" + roiArray[x].roi;
        console.log('save '+ outArray[x]);
    }
    //console.log(JSON.stringify(roiArray["roi"]));
    $.post("URNServlet", {
        askResponse: "res",
        type:"char",
        annotation: 'c',
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
});
