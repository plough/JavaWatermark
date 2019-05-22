# Java 水印

本项目实现了使用 Java 绘制水印的功能。
提取于 FR10.0 的水印功能，经过商业项目的检验，质量有保障。也就是说，它可以直接变成类库，而不是简单的 demo。

## 使用方法
只要能获取 Graphics 对象，就能在上面绘制水印。
使用方式如下：
```java
Graphics2D g2d = (Graphics2D)g;
WatermarkAttr watermark = new WatermarkAttr("水印测试\\n12345", Color.BLACK, 24);
WatermarkPainter painter = WatermarkPainter.createPainter(watermark);
painter.paint(g2d, this.getWidth(), this.getHeight());
```

## 应用范围
应用范围主要包括 Java 的 GUI 应用，以及打印、导出等场景。
凡是会用到 Graphics 对象的地方，都可以加水印。

## 效果演示
写了一个测试类，WatermarkTest，可以演示水印的效果：
![](https://raw.githubusercontent.com/plough/JavaWatermark/master/img/watermarkDemo.png)

## 大致的算法
1. 创建一个空画板（BufferedImage），这个画板的大小，根据绘图区域计算出来（要用到三角函数的知识）。要求是，画板倾斜 20 度后，能完整覆盖绘图区域。
2. 在空画板上，按照一定的水平、垂直间隔，填充一条条水印，直到填满整个画板。
3. 旋转角度，将带有水印的画板，覆盖到绘图区域上。超出绘图区域的部分，不会显示。
