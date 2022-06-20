const {BlockSet, Block} = require('../UnicodeBlockSet')

function get_set(a, b){
    var chars = [];
    for (var i = a; i <= b; i++){chars.push(String.fromCodePoint(i));}
    return chars;
}

function isEqual(a, b)
{
    return a.join() == b.join();
}

function isNotEqual(a,b)
{
    return a.join() != b.join();
}


let hebrewBlock = new Block('Hebrew', 0x0590, 0x05FF);
let blockSet = new BlockSet();
//console.log(blockSet.getBlockKeys());
console.assert(isEqual(blockSet.getBlockKeys(), Object.keys(blockSet.blocks)), 'Fail testUnicodeBlockSet.js: BlockSet.getBlockKeys not returning correct values for keys.');

var hebrewChars = get_set(0x0590, 0x05FF);
console.assert(isEqual(hebrewChars, blockSet.getBlock('Hebrew')), "Fail testUnicodeBlockSet.js: Hebrew Chars and Blockset of Hebrew Chars are not equal");
console.assert(isEqual(hebrewChars, hebrewBlock.getChars()), "Fail testUnicodeBlockSet.js: Hebrew Chars and Block of Hebrew Chars are not equal");

var toFailHebrew = get_set(0x0000, 0x007F);
console.assert(isNotEqual(toFailHebrew, blockSet.getBlock('Hebrew')), "Fail: testcodeUnicodeBlockSet.js: Did not Fail hebrew equality check when input Basic Latin block to check");

var linearBSet = get_set(0x10000, 0x1007F);
blockSet.addBlock('Linear B Syllabary', 0x10000, 0x1007F);
console.assert(isEqual(linearBSet, blockSet.getBlock('Linear B Syllabary')), "Fail testUnicodeBlockSet.js: BlockSet.addBlock is not correctly adding Linear B set");


console.log("\tAll asserts ran for 'testUnicodeBlockSet.js'. Errors would be displayed.");