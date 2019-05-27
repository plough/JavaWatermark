import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 绘制水印的工具类
 * Created by plough on 2018/5/22.
 */
public class WatermarkPainter {
    private static final double ROTATE_ANGLE_RADIANS = Math.toRadians(20);  // 旋转角度(单位：弧度)
    private static final double SIN_ROTATE_ANGLE = Math.sin(ROTATE_ANGLE_RADIANS);
    private static final double COS_ROTATE_ANGLE = Math.cos(ROTATE_ANGLE_RADIANS);
    private static final String PLACE_HOLDER = "　";  // 一个全角字符

    // 字体全用英文名称，防止乱码
    private static final String[] WINDOWS_FONT_NAMES = {"Microsoft YaHei", "SimHei", "SimSun"};
    private static final String[] MAC_FONT_NAMES = {"PingFang SC", "Hiragino Sans GB", "Arial", "Verdana"};
    private static String watermarkFontName = "";
    static {
        // 找到所有可用的字体名
        List<String> availableFontNames = new ArrayList<>();
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : fonts) {
            availableFontNames.add(font.getName());
        }
        // fontFamily 也加进去
        availableFontNames.addAll(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

        // 挑选合适的字体
        String[] fontNameList = isWindows() ? WINDOWS_FONT_NAMES : MAC_FONT_NAMES;
        for (String fontName : fontNameList) {
            if (availableFontNames.contains(fontName)) {
                watermarkFontName = fontName;
                break;
            }
        }
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    private int horizontalGap = 20;  // 水平间隔（一个中文字符宽度）
    private int verticalGap = 40;  // 垂直间隔（两个中文字符宽度）
    private WatermarkAttr watermark;
    private List<String> watermarkTextLines;

    private WatermarkPainter(WatermarkAttr watermark) {
        if (watermark == null) {
            watermark = new WatermarkAttr();
        }
        this.watermark = watermark;
        this.watermarkTextLines = getLineTextList(watermark.getText());
    }

    private List<String> getLineTextList(String text) {
        return Arrays.asList(text.split("\\\\n"));
    }

    // 尽量用静态工厂方法代替构造器——《effective java》
    public static WatermarkPainter createPainter(WatermarkAttr watermark) {
        return new WatermarkPainter(watermark);
    }

    public WatermarkAttr getWatermark() {
        return watermark;
    }

    private void updateGap(FontMetrics fontMetrics) {
        horizontalGap = fontMetrics.stringWidth(PLACE_HOLDER);
        verticalGap = horizontalGap * 2;
    }

    private int getLineHeight() {
        return horizontalGap;
    }

    private Font getFont(int fontSize) {
        assert !watermarkFontName.isEmpty();
        return new Font(watermarkFontName, Font.PLAIN, fontSize);
    }

    /**
     * 绘制水印
     * 如果是预览模式，显示公式原值；否则显示计算结果值
     * @param rect 水印区域
     */
    public void paint(Graphics2D g2d, Rectangle rect) {
        this.paint(g2d, rect.width, rect.height);
    }

    /**
     * 绘制水印
     * 如果是预览模式，显示公式原值；否则显示计算结果值
     * @param width 水印区域宽度
     * @param height 水印区域高度
     */
    public void paint(Graphics2D g2d, int width, int height) {
        paint(g2d, 0, 0, width, height);
    }

    /**
     * 绘制水印
     * 如果是预览模式，显示公式原值；否则显示计算结果值
     * @param x 水印区域横坐标
     * @param y 水印区域纵坐标
     * @param rect 水印区域
     */
    public void paint(Graphics2D g2d, int x, int y, Rectangle rect) {
        this.paint(g2d, x, y, rect.width, rect.height);
    }

    /**
     * 绘制水印
     * 如果是预览模式，显示公式原值；否则显示计算结果值
     * @param x 水印区域横坐标
     * @param y 水印区域纵坐标
     * @param width 水印区域宽度
     * @param height 水印区域高度
     */
    public void paint(Graphics2D g2d, int x, int y, int width, int height) {
        if (g2d == null || watermark == null || watermark.isEmpty()) {
            return;
        }

        Rectangle oldClip = g2d.getClipBounds();

        if (oldClip != null) {
            // 确保新绘图区域不超过原区域
            x = Math.max(x, oldClip.x);
            y = Math.max(y, oldClip.y);
            width = Math.min(width, oldClip.width);
            height = Math.min(height, oldClip.height);
        }

        g2d.setClip(x, y, width, height);  // 限制绘图区域

        paintRotateWatermarkImage(g2d, x, y, width, height);

        g2d.setClip(oldClip);  // 解除限制
    }

    private void paintRotateWatermarkImage(Graphics2D g, int x, int y, int width, int height) {
        BufferedImage markImage = paintWatermarkImage(width, height);
        double rotateRadians = -ROTATE_ANGLE_RADIANS;
        g.rotate(rotateRadians);
        // 三角函数计算得到左上角坐标
        x = (int)(x - height * SIN_ROTATE_ANGLE * COS_ROTATE_ANGLE);
        y = (int)(y - height * SIN_ROTATE_ANGLE * SIN_ROTATE_ANGLE);

        g.drawImage(markImage, x, y, null);

        g.rotate(-rotateRadians);
    }

    private BufferedImage paintWatermarkImage(int width, int height) {
        // 三角函数算出来的最小宽高，不浪费内存
        width = (int)(width * COS_ROTATE_ANGLE + height * SIN_ROTATE_ANGLE);
        height = (int)(width * SIN_ROTATE_ANGLE + height * COS_ROTATE_ANGLE);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(watermark.getColor());
        g.setFont(getFont(watermark.getFontSize()));
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        updateGap(g.getFontMetrics());
        int blockWidth = getTextBlockWidth(g);
        int blockHeight = getTextBlockHeight(g);
        int evenStartX = -(blockWidth + horizontalGap) / 2;  // 偶数行的起始位置
        int lineNum = 0;  // 当前行号
        for (int y = getLineHeight(); y < height; y += (verticalGap + blockHeight)) {
            // 水印交错排列
            lineNum ++;
            int startX = lineNum % 2 == 0 ? evenStartX : 0;

            for (int x = startX; x < width; x += (horizontalGap + blockWidth)) {
                drawString(g, blockWidth, x, y);
            }
        }
        g.dispose();
        return image;
    }

    private int getTextBlockHeight(Graphics g) {
        return g.getFontMetrics().getHeight() * watermarkTextLines.size();
    }

    private int getTextBlockWidth(Graphics g) {
        int blockWidth = 0;  // 整个文本块的宽度
        for (String line : watermarkTextLines) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            if (lineWidth > blockWidth) {
                blockWidth = lineWidth;
            }
        }
        return blockWidth;
    }

    // 绘制一个水印块，多行文本居中对齐
    private void drawString(Graphics g, int blockWidth, int x, int y) {
        int midX = x + blockWidth / 2;  // 中线的x坐标
        for (String line : watermarkTextLines) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            int lineX = midX - lineWidth / 2;
            g.drawString(line, lineX, y);
            y += g.getFontMetrics().getHeight();
        }
    }
}
