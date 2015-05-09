package edu.teco.guerillaSensing.data;


/**
 * This class represents an CardView entry used by the RecyclerView.
 */
public class CardMenuEntry {

    // The data type (header, item or footer).
    private int TYPE;

    // For the "headers".
    private int mHeaderImage;
    private String mHeadLine;
    private String mSubLine;

    // For the "items".
    private int mPicture;
    private String mFirstLine;
    private String mSecondLine;
    private String mThirdLine;

    // For the "footers".
    private String mFooterLine;

    /**
     * Constructor for the header.
     * @param headerImage The header image.
     * @param headLine The header head line.
     * @param subLine The header sub line.
     */
    public CardMenuEntry(int headerImage, String headLine, String subLine) {
        this.TYPE = MenuRecyclerTypes.TYPE_HEADER;
        this.mHeaderImage = headerImage;
        this.mHeadLine = headLine;
        this.mSubLine = subLine;
    }

    /**
     * Constructor for items.
     * @param firstLine The first line.
     * @param secondLine The second line.
     * @param thirdLine The third line.
     * @param picture Reference to Drawable image.
     */
    public CardMenuEntry(String firstLine, String secondLine, String thirdLine, int picture) {
        this.TYPE = MenuRecyclerTypes.TYPE_ITEM;
        this.mPicture = picture;
        this.mFirstLine = firstLine;
        this.mSecondLine = secondLine;
        this.mThirdLine = thirdLine;
    }

    /**
     * Constructor for the footer.
     * @param footerLine The footer line.
     */
    public CardMenuEntry(String footerLine) {
        this.TYPE = MenuRecyclerTypes.TYPE_FOOTER;
        this.mFooterLine = footerLine;
    }

    /**
     * Returns the type of the menu entry.
     * @return the type as integer, as defined in MenuRecyclerTypes.
     */
    public int getType() {
        return TYPE;
    }


    //---------------
    // GETTERS AND SETTERS
    //---------------
    public String getHeadLine() {
        return mHeadLine;
    }

    public void setHeadLine(String mHeadLine) {
        this.mHeadLine = mHeadLine;
    }

    public String getSubLine() {
        return mSubLine;
    }

    public void setSubLine(String mSubLine) {
        this.mSubLine = mSubLine;
    }

    public String getFirstLine() {
        return mFirstLine;
    }

    public void setFirstLine(String mFirstLine) {
        this.mFirstLine = mFirstLine;
    }

    public String getThirdLine() {
        return mThirdLine;
    }

    public void setThirdLine(String mThirdLine) {
        this.mThirdLine = mThirdLine;
    }

    public String getSecondLine() {
        return mSecondLine;
    }

    public void setSecondLine(String mSecondLine) {
        this.mSecondLine = mSecondLine;
    }

    public String getFooterLine() {
        return mFooterLine;
    }

    public void setFooterLine(String mFooterLine) {
        this.mFooterLine = mFooterLine;
    }

    public int getHeaderImage() {
        return mHeaderImage;
    }

    public void setHeaderImage(int mHeaderImage) {
        this.mHeaderImage = mHeaderImage;
    }

    public int getPicture() {
        return mPicture;
    }

    public void setPicture(int mPicture) {
        this.mPicture = mPicture;
    }
}