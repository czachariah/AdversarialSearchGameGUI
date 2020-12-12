package Board;

/**
 * This is the Piece class.
 * Will be used in order to populate the Board and play the game.
 */
public class Piece {

    // Global Variables
    public int x;               // this is the x (row) coordinate
    public int y;               // this is the y (column) coordinate
    public int type;            // this is the type of the piece
    public int color;           // this is the color of the piece

    public double probWumpus;   // probability piece is a wumpus
    public double probHero;      // probability piece is a hero
    public double probMage;     // probability piece is a mage
    public double probPit;      // probability piece is a pit


    public boolean hasStench;   // there is at least one Wumpus neighbor
    public boolean hasNoise;    // there is at least one Hero neighbor
    public boolean hasHeat;     // there is at least one Mage neighbor
    public boolean hasBreeze;   // there is at least one Pit neighbor



    /**
     * This is the constructor of the Piece class.
     * @param x the x (row) coordinate
     * @param y the y (column) coordinate
     * @param type the type of the piece
     * @param color the color of the piece
     */
    public Piece(int x , int y , int type , int color, double probWumpus, double probHero, double probMage, double probPit, boolean hasStench, boolean hasNoise, boolean hasHeat, boolean hasBreeze) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = color;
        this.probWumpus = probWumpus;
        this.probHero = probHero;
        this.probMage = probMage;
        this.probPit = probPit;
        this.hasStench = hasStench;
        this.hasNoise = hasNoise;
        this.hasHeat = hasHeat;
        this.hasBreeze = hasBreeze;
    } // ends the Piece() constructor



    /*
    Types:
        0 : Pit (has no color association)
        1 : Wumpus (has color association)
        2 : Hero (has color association)
        3 : Mage (has color association)
        4 : Empty Space
    */

    /*
    Color:
        0 : Red / AI
        1 : White / Human-Player
        2 : Green / Empty Square
        3 : Black / Pit 
    */

} // ends the Piece class