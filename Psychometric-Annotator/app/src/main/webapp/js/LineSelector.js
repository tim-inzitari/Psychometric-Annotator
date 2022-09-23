// special char tool
/*
HELPS ADD SPECIAL CHARACTERS FROM UNICODE TO THE SYSTEM
RIGHT NOW ALL UNICODE CHARACTERS AND TRANSFERED AND USED
USING THIER DECIMAL REPRESENTIVE NUMBER AS THE VALUE
*/

// initialize 
var unicodeBlockSet = new BlockSet();
var label;
var button_label;
var block;
var block_name;


var extraChars= [["unknown/other", "&lt;UNK&gt;"]]

unicodeDict = {}
unicodeNameCSV.forEach(int_name => {
    unicodeDict[int_name[0]] = int_name[1]
    
});

// have a variable to keep track of last focused text box
// so that it can known to additional character buttons
var lastFocused;



//-----------------------------------------------------------
//-----------------------------------------------------------
//-----------------------------------------------------------
// Dialogue
//-----------------------------------------------------------
//-----------------------------------------------------------
//-----------------------------------------------------------
$( "#dialog" ).dialog({
	autoOpen: false,
	width: '75%',
    height: 600,
    overflow: 'scroll',
	buttons: [
        /*
		{
			text: "Ok",
			click: function() {
				$( this ).dialog( "close" );
			}
		},
        */
		{
			text: "Close",
			click: function() {
				$( this ).dialog( "close" );
			}
		}
	]
});
// Hover states on the static widgets
$( "#dialog-link, #icons li" ).hover(
	function() {
		$( this ).addClass( "ui-state-hover" );
	},
	function() {
		$( this ).removeClass( "ui-state-hover" );
	}
);
// Link to open the dialog
$( "#dialog-link" ).click(function( event ) {
	$( "#dialog" ).dialog( "open" );
	event.preventDefault();
});

function labelandbuttonHtmlCharButton(label, button_label){
    var labelHtml = "<label>"+label+"</label>";
    var buttonHtml = "<button id='otherChar_"+label+"'>"+button_label+"</button>"

    var html= labelHtml+buttonHtml
    return html
}

function addText(obj) {
    console.log('last focused' + lastFocused);
    console.log('object ' + obj);
    console.log('object val ' + obj.value);
    lastFocused.value = lastFocused.value + obj.value;
}

var n_e_cols = 8
function makeSuperTable() {
    $('#extra_char_buttons').empty();
    content = "<table id='extra_char_table' border='1px solid black'>";
    content+='<tr><th colspan="'+n_e_cols+'">Additional Character Input Buttons</th></tr>'

    var cur_col = 0;
    var cur_row=0;
    var i = 0;

    extraChars.forEach(extraChar => {
        var label = extraChar[0]
        var char = extraChar[1]

        if(cur_col==0){content+='<tr id="dialog_table_row_'+cur_row+'">';}

        content+= '<td><label>'+label+'</label><button id="extraChar_'+i+'" onclick="addText(this)" value="'+char+'">'+char+'</button></td>';

        cur_col +=1;
        i +=1
        if (cur_col == n_e_cols) {
            cur_col = 0;
            cur_row+=1;
            content += '</tr>'
        }

    });
    content += '</table>'
    $('#extra_char_buttons').append(content);
}


//-----------------------------------------------------------
// Dialogue Table
//-----------------------------------------------------------
function addC(obj){
    var intCode = obj.value;
    extraChars.push([unicodeDict[intCode], String.fromCodePoint(parseInt(intCode,10))]);
    makeSuperTable();
}
var n_d_cols = 5;
// Make the actual table
function makeTable(block_name){
    // initialize table
    var content = '<table id="dialog_table" border="1px solid black">';

    var chars = unicodeBlockSet.getBlock(block_name);
    var n_chars = chars.length;
    console.log('number of chars in blockset: '+n_chars);
    
    var cur_col = 0;
    var cur_row=0;
    var i = 0;
    chars.forEach(char => {
        // Start a row if needed
        intCode = char.charCodeAt(0).toString(10)
        //console.log('here')
        //console.log('ጐ'.charCodeAt(0).toString(10))
        //console.log(unicodeDict[1424])
        if (!(!(intCode in unicodeDict))){ // remove gaps in unicode
            if(cur_col==0){content+='<tr id="dialog_table_row_'+cur_row+'">';}
            content+='<td>'+unicodeDict[intCode] +'<button onclick="addC(this)" id="dialogCharButton_'+i+'" value='+intCode+'>'+String.fromCharCode(intCode)+'</td>';
            // increase col check, end a row if needed
            // increase char number
            cur_col +=1;
            i +=1
            if (cur_col == n_d_cols) {
                cur_col = 0;
                cur_row+=1;
                content += '</tr>'
        }};
    });

    // close table and append it to div
    content+= '</table>';
    $('#dialog_table_div').empty()
    $('#dialog_table_div').append(content);
    
}



//-----------------------------------------------------------
//-----------------------------------------------------------
//-----------------------------------------------------------
// select menus
//-----------------------------------------------------------
//-----------------------------------------------------------
//-----------------------------------------------------------

$( "#selectmenu" ).selectmenu({
    width:  '100%',
    size: 15,
});
function addSelectMenuOption(name){
    html = "<option id='selectMenuOption_"+name+"'>"+name+"</option>"
    return html
}

// Populate select menu with each blockset
function populateSelectMenu(blockset){
    unicodeBlocks = blockset.getBlockKeys();
    unicodeBlocks.forEach(b => {
        $('#selectmenu').append(addSelectMenuOption(b));
    });
}
populateSelectMenu(unicodeBlockSet);




$('#selectmenu').on('selectmenuchange', function(event, ui) {
    makeTable($( "#selectmenu option:selected" ).text());
    makeSuperTable() 
});


makeSuperTable();


/**
 * Created by smgri on 6/21/2017.
 */
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
var annotationList = []
var useLocal = true;
var txtDir = 'rtl'
var imgUrn;


var defaultLang = 'he' // 2 character code for default language of keyboard

var roiArray = [];

//var tsrc = getTileSource


/* Main */
jQuery(function($){
    $('#image_imageContainer').hide();
    var paramUrn;
    $.post("URNServlet", {
            askResponse: "ask",
            type:"lineSelector"
        },function(responseText){
        paramUrn = responseText;
        console.log(responseText);
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
    console.log('urn: ' + imgUrn)
    viewer = OpenSeadragon({
        id: 'image_imageContainer',
        prefixUrl: 'css/images/',
        crossOriginPolicy: "Anonymous",
        defaultZoomLevel: 1,
        tileSources: getTileSources(imgUrn),
        minZoomImageRatio: 0.1, // of viewer size
        maxZoomLevel: 16,
        immediateRender: true
    });
    console.log('opening '+ imgUrn)

    viewer.addHandler('full-screen', function (viewer) {
        console.log('fullscreen');
        refreshRois();
    })


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
        restrictToImage: true,
        onSelection: function(rect) {
            createROI(rect);
            //addRoiOverlay()
        }
    });


    // Openseadragon does not have a ready() function, so here we are…
    setTimeout(function(){
        loadDefaultROI(imgUrn)
        $('#image_imageContainer').show();
    },1000);

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

$(document).on('focus','.focusInputClass', function() {
    lastFocused = this;   
    console.log('lastFocused-> ' + this);
});



function addRoiListing(roiObj){
    console.log(roiObj.roi);
    // image_urnList
    var idForListing = idForMappedUrn(roiObj.index);
    var idForRect = idForMappedROI(roiObj.index);
    var groupClass = "image_roiGroup_" + roiObj.group;
    var txtbox = "<input type='text' size='30' value='' class='keyboardInput focusInputClass' lang='" + defaultLang + "'  id='annoInput_" + idForListing + "' dir='" + txtDir + "' required>";
    var deleteLink = "<a class='deleteLink' id='delete" + idForListing + "' data-index='" + roiObj.index + "'></a>";
    var mappedUrnSpan = "<li class='" + groupClass + "' id='" + idForListing + "'>";
    mappedUrnSpan += deleteLink + roiObj.mappedUrn + '\n' + txtbox + "</li>";

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
        console.log("Delete: " + i)
        deleteRoi(parseInt(i))
    });
    // attach virtual keyboard
    var kb_id = 'annoInput_'+idForListing
    var myInput = document.getElementById(kb_id);
    if (!myInput.VKI_attached) VKI_attach(myInput);
}

function deleteRoi(c){
    var tempArray = []
    for (i = 0; i < roiArray.length; i++){
        console.log("c = " + c + "; i = " + i)
        if (i != c){
            console.log(roiArray[i]);
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
function Annotation(number, str, roiObj){
    this.number = number;
    this.str = str;
    this.roiObj = roiObj;

}

// l2r toggle implement
$(document).on('change', '#l2r',  function() {
    txtDir = $(this).find(":selected").val();
    console.log("txt dir is now "+ txtDir);
    $('input[type=text]').each(function() {
        console.log(this)
        $(this).attr('dir', txtDir);
    });
});

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
    console.log(roi);

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
function validateAnno(){
    // validate all textboxes are filled first
    $(".focusInputClass").each(function() {
        if($(this).val ==''){
            alert('Please fill out all annotations');
            return false;
        }
    });

    // ALL ARE VALIDATED
    
    roiArray.forEach(roiObj => {
        var idForListing = idForMappedUrn(roiObj.index);
        var groupClass = "image_roiGroup_" + roiObj.group;
        var txtbox_id= '#annoInput_'+idForListing;
        
        console.log('roiobj roi ',roiObj.roi);

        var anno = $(txtbox_id).val();
        console.log('anno,group,id:',anno, roiObj.group, idForListing);

        var myAnno = new Annotation(idForListing, anno, roiObj);
        console.log('id '+idForListing)
        annotationList.push(myAnno);
        console.log('length' + annotationList.length)

    });

    return true;
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
    useLocal = false
    testUrn = imgUrn
    if (useLocal){
        var localDir = plainUrn.split(":")[0] + "_" + plainUrn.split(":")[1] + "_" + plainUrn.split(":")[2] + "_" + plainUrn.split(":")[3] + "_/"
        ts = usePath + localDir + imgId + useSuffix
        console.log('local: ' + ts)
    } else {
        ts = usePath + testUrn + useSuffix
        console.log(ts)
    }
   
    console.log("Print: ") 
    console.log(ts)
    return ts
}

$("#submitButton").click(function() {
    if (!validateAnno()){
    console.log("here");
    var outArray = new Array(roiArray.length);
    console.log('here')
    for(x = 0; x < roiArray.length; x++){
        outArray[x] = imgUrn + "@" + roiArray[x].roi;
        console.log('save: '+ outArray[x]);
    }
    $.post("URNServlet", {
        askResponse: "res",
        type:"line",
        data: JSON.stringify(outArray)
    },function(responseText){
        console.log(responseText);
        if(responseText === "TRUE") {
            location.reload();
        }else{
            window.location = "/index.html";
        }
    });
    }
    else{
        console.log('invalid response');
        return false;
    }
});

