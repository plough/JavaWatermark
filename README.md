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
