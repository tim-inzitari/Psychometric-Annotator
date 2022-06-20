
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
        let block = new Block(name, start, end);
        this.blocks[block.name] = block;
    }
    initalizeBlocks() {
        this.addBlock("Basic Latin", 0x0000, 0x007F);
        this.addBlock("Latin-1 Supplement", 0x0080, 0x00FF);
        this.addBlock("Hebrew", 0x0590, 0x05FF);
        this.addBlock("Arabic", 0x600, 0x06FF);
        this.addBlock("Syraic", 0x700, 0x74F);
        this.addBlock("Arabic Supplement", 0x750, 0x77F);
        this.addBlock("Alphabetic Presentation Forms", 0xFB00, 0xFB4F)
    }
    getBlockKeys() {
        return Object.keys(this.blocks);
    }

    getBlock(name) {
        var block = this.blocks[name];
        var chars = [];
        for (var i = block.start; i <= block.end; i++){
            chars.push(String.fromCodePoint(i));
        }
        return chars;
    }
}

module.exports= {Block, BlockSet};