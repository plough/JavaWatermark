import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Created by plough on 2019/5/22.
 */
public class WatermarkTest {
    private WatermarkPainter watermarkPainter;

    public WatermarkTest() {
        initWatermarkPainter();
        initGUI();
    }

    private void initWatermarkPainter() {
        WatermarkAttr watermark = new WatermarkAttr("水印测试\\n12345", Color.BLACK, 24);
        watermarkPainter = WatermarkPainter.createPainter(watermark);
    }

    private void initGUI() {
        JFrame frame = new JFrame();
        frame.setSize(600, 600);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        JPanel watermarkPane = getWatermarkPane();
        watermarkPane.setBackground(Color.CYAN);
        JPanel normalPane = new JPanel();
        normalPane.setBackground(Color.GREEN);
        normalPane.setPreferredSize(new Dimension(200, 600));
        contentPane.add(watermarkPane, BorderLayout.CENTER);
        contentPane.add(normalPane, BorderLayout.WEST);

        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

    private JPanel getWatermarkPane() {
        return new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D)g;
                watermarkPainter.paint(g2d, this.getWidth(), this.getHeight());
            }
        };
    }


    public static void main(String[] args) {
        new WatermarkTest();
    }

}
