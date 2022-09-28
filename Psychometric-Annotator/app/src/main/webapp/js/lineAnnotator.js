

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
var defaultLang = 'he'
var block_name;
var txtbox = "<input type='text' size='30' value='' class='keyboardInput focusInputClass' lang='" + defaultLang + "' maxlength='4' id='annoInput'  required>";


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
    lastFocused.value = obj.value;
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



$('#selectmenu').on('selectmenuchange', function(event, ui) {
    makeTable($( "#selectmenu option:selected" ).text());
    makeSuperTable() 
});










/************************************************
 *************************************************
 ************************************************
CHARACTER ANNOTATOR
************************************************
************************************************
**************************************************/
/* Defaults and Globals */
var viewer = null;
var latinList = ["a","b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","x","y","z","~","ⴈ","ꝑ","ꝓ", "ꝗ","ꝝ","ꝩ","ꝯ","dot","semi","","'","other"];
var oldhebrewList = ['𐤀', '𐤁', '𐤂', '𐤃', '𐤄', '𐤅', '𐤆', '𐤇', '𐤈', '𐤉', '𐤊', '𐤋', '𐤌', '𐤍', '𐤎', '𐤏', '𐤐', '𐤑', '𐤒', '𐤓', '𐤔', '𐤕',"dot","semi",'\'','other'];
var hebrewList = ['א', 'ב', 'ג','ד','ה', 'ו', 'ז', 'ח' ,'ט', 'י', 'כ' ,'ך', 'ל', 'מ', 'ם', 'נ', 'ן', 'ס', 'ע', 'פ', 'ף', 'צ', 'ץ', 'ק', 'ר', 'ש', 'ת', 'other']


classList = hebrewList
var anno = "";
var lineNo = -1;
var wordNo = -1;
var letterNo = -1;
var imgUrn;
var startTime;
//var tsrc = getTileSource
$(document).on('focus','.focusInputClass', function() {
    lastFocused = this;   
    console.log('lastFocused-> ' + this);
});




/* Main */
jQuery(function($){
    $('form').each(function() { this.reset() });
    var paramUrn;
    $('#input_form').hide();
    $('#image_imageContainer').hide();
    $('#loadImageButton').hide();
    $.post("URNServlet", {
        askResponse:"ask",
        type:"lineann"
    },function(responseText){
        var response = responseText.split("-");
        paramUrn = response[0];
        lineNo = response[1];
        setUpUI();
        imgUrn = paramUrn;
        console.log(responseText);
        imageDrawer();
        initializeKeyboad();
    });


});

$(document).ready(function(){
    $('#inputBox').append(txtbox);
    populateSelectMenu(unicodeBlockSet);
    makeSuperTable();
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
    // attach virtual keyboard
    var kb_id = 'annoInput';
    var myInput = document.getElementById(kb_id);
    if (!myInput.VKI_attached) VKI_attach(myInput);
}

function setUpUI(){
    $('#loadImageButton').show();

}
var new_width = 0;
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
        new_width = width;
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
    canvas.setZoom(1.75);
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

// l2r toggle implement
$(document).on('change', '#l2r',  function() {
    txtDir = $(this).find(":selected").val();
    console.log("txt dir is now "+ txtDir);
    $('input[type=text]').each(function() {
        $(this).attr('dir', txtDir);
    });
});

function updateValue(){
    var x = document.getElementById("difficulty_range").value;
    document.getElementById("difficulty_view").innerHTML = x;
}

$('#loadImageButton').click(function() {
    $('#image_imageContainer').show();
    $('#loadImageButton').hide();
    $('#input_form').show();
    $('#submitButton').show();
    startTime = new Date().getTime();
});

$('#submitButton').click(function() {
    if ($('#annoInput').val() == ''){alert('you must mark an annotation.')}else{
    anno = $('#annoInput').val();
    $.post("URNServlet", {
        askResponse: "res",
        type:"lineanno",
        timer: new Date().getTime() - startTime,
        annotation: anno,
        difficulty: $('#difficulty_range').val(),
        lineNo: lineNo,
        urn: imgUrn
    },function(responseText){
        if(responseText === "TRUE") {
            location.reload();
        }else{
            window.location = "/index.html";
        }
    });}
});

