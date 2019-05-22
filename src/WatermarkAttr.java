import java.awt.Color;

/**
 * 模版的水印属性
 * Created by plough on 2018/5/15.
 */
public class WatermarkAttr {
    public static final String XML_TAG = "WatermarkAttr";
    private static final String TEXT_TAG = "Text";
    private static final int ALPHA = 25;  // 0.1 * 255
    private static final long serialVersionUID = 7753960898492887656L;

    private String text;
    private int fontSize;
    private Color color;

    public WatermarkAttr() {
        this("", Color.black, 14);
    }

    public WatermarkAttr(String text, Color color, int fontSize) {
        this.text = text;
        this.color = color;
        this.fontSize = fontSize;
    }

    /**
     * 水印是否为空
     * */
    public boolean isEmpty() {
        return text.isEmpty();
    }

    public String getText() {
        return text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Color getColor() {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), ALPHA);
    }

}