class Block {
    /*Block of Unicode
    input: name of the block, aswell as start char code and end char code
    output: block by the name block.name, and then their start and end points

    We avoid storing the entire list at start to keep space usage lower at the cost of minimal computation speed on call that would only effect large blocks such as Chinese/Japanese/Korean character sets.
    
    Example: Block("Basic Latin", 0x0000, 0x007F) would be used to generate the basic 128 ASCII Set that
    is defined here https://en.wikipedia.org/wiki/Basic_Latin_(Unicode_block) 
    */
    constructor(name, start, end){
        this.name = name;
        this.start = start;
        this.end = end;

    }

    getChars() {
        var chars = [];
        console.log(this.start)
        console.log(this.end)
        console.log(this.name)
        for (var i = this.start; i <= this.end; i++){
            //console.log(String.fromCodePoint(parseInt(i,16)));
            chars.push(String.fromCodePoint(parseInt(i,10)));
        }
        return chars;
    }

    getHexes() {
        var hexes = [];
        for (var i = this.start; i <= this.end; i++){
            hexes.push(parseInt(i,16))
        }
        return hexes
    }

}
class BlockSet {
    /* Collection of Unicode Blocksets that can be called
    for our selector to minimize loading times.

    Each set range will be defined based on 
    https://en.wikipedia.org/wiki/Unicode_block#List_of_blocks

    Input: 
    Output: 
    */

    constructor() {
        this.blocks = {};
        this.initalizeBlocks();
    }
    addBlock(name, start, end){
        let bl = new Block(name, start, end);
        this.blocks[bl.name] = bl;
    }
    initalizeBlocks() {
        
        // Load Unicode blocks
        // from unicodeNames.js

        blockRangeCSV.forEach(b => {
            this.addBlock(b[0], b[1], b[2]);
        });


    }
    getBlockKeys() {
        return Object.keys(this.blocks);
    }


    // Gets the chars from a given block
    getBlock(name) {
        var block = this.blocks[name];
        //console.log(name)
        var chars = block.getChars();
        return chars;
    }

    getCharHexes(name) {
        var block = this.blocks[name];
        //console.log(name)
        var hexes = block.getHexes();
        return hexes;
    }
}

