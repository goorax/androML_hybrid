package androML.dynamic_analysis.clicks;

final public class ClickPosition {
    private int x;
    private int y;
    private String view;

    public ClickPosition(int x, int y, String view) {
        this.x = x;
        this.y = y;
        this.view = view;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getView() {
        return view;
    }
}
