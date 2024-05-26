package graphapp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphDrawer {

//    public static void main(String[] args) {
//        Map<String, Map<String, Integer>> graph = getGraph();
//
//        // 绘制有向图并保存为PNG文件
//        drawDirectedGraph(graph, "directed_graph.png");
//    }

//    // 获取示例有向图
//    private static Map<String, Map<String, Integer>> getGraph() {
//        // 示例有向图数据
//        Map<String, Map<String, Integer>> graph = new HashMap<>();
//        Map<String, Integer> edgesA = new HashMap<>();
//        edgesA.put("B", 5);
//        edgesA.put("C", 3);
//        graph.put("A", edgesA);
//
//        Map<String, Integer> edgesB = new HashMap<>();
//        edgesB.put("C", 2);
//        edgesB.put("D", 4);
//        graph.put("B", edgesB);
//
//        Map<String, Integer> edgesC = new HashMap<>();
//        edgesC.put("D", 1);
//        graph.put("C", edgesC);
//
//        graph.put("D", new HashMap<>());
//
//        return graph;
//    }

    // 绘制有向图并保存为PNG文件
    public static void showDirectedGraph(Map<String, Map<String, Integer>> graph, String filename) {
        // 创建图像
        int width = 2000;
        int height = 1000;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 绘制图形
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2));
        drawGraph(graph, g2d, width, height);

        // 保存图像到文件
        try {
            ImageIO.write(image, "PNG", new File(filename));
            System.out.println("有向图已保存为 " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            g2d.dispose();
        }
    }

    // 绘制有向图
    private static void drawGraph(Map<String, Map<String, Integer>> graph, Graphics2D g2d, int width, int height) {
        int margin = 20;
        int nodeSize = 50;  // 增大节点尺寸

        // 为简单定位节点（可以更改此逻辑以获得更好的布局）
        int nodeIndex = 0;
        int totalNodes = graph.size();
        Map<String, Point> nodePositions = new HashMap<>();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY) - margin;

        // 计算节点位置
        for (String node : graph.keySet()) {
            double angle = 2 * Math.PI * nodeIndex / totalNodes;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            nodePositions.put(node, new Point(x, y));
            nodeIndex++;
        }

        // 绘制边
        for (String from : graph.keySet()) {
            Point startPoint = nodePositions.get(from);
            Map<String, Integer> edges = graph.get(from);
            if (edges != null) {
                for (String to : edges.keySet()) {
                    Point endPoint = nodePositions.get(to);
                    int weight = edges.get(to);
                    drawCurvedArrow(g2d, startPoint, endPoint);
                    drawWeight(g2d, startPoint, endPoint, weight);
                }
            }
        }

        // 绘制节点
        for (String node : graph.keySet()) {
            Point point = nodePositions.get(node);
            drawNode(g2d, point, nodeSize, node);
        }
    }

    // 绘制节点
    private static void drawNode(Graphics2D g2d, Point center, int size, String label) {
        // 绘制白色的圆形
        g2d.setColor(Color.WHITE);
        g2d.fillOval(center.x - size / 2, center.y - size / 2, size, size);

        // 绘制黑色的边框
        g2d.setColor(Color.BLACK);
        g2d.drawOval(center.x - size / 2, center.y - size / 2, size, size);

        // 在圆形内部绘制节点的名字
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        int labelHeight = fm.getAscent();
        g2d.drawString(label, center.x - labelWidth / 2, center.y + labelHeight / 4);
    }


    // 绘制权重在曲线的1/3处
    private static void drawWeight(Graphics2D g2d, Point from, Point to, int weight) {
        double ctrlX = (from.x + to.x) / 2 + (to.y - from.y) / 4;
        double ctrlY = (from.y + to.y) / 2 - (to.x - from.x) / 4;

        double t = 1.0 / 3.0; // 1/3处
        double x = Math.pow(1 - t, 2) * from.x + 2 * (1 - t) * t * ctrlX + Math.pow(t, 2) * to.x;
        double y = Math.pow(1 - t, 2) * from.y + 2 * (1 - t) * t * ctrlY + Math.pow(t, 2) * to.y;

        // 计算权重文字的偏移量，使其远离曲线
        double dx = 10 * Math.cos(Math.atan2(to.y - ctrlY, to.x - ctrlX));
        double dy = 10 * Math.sin(Math.atan2(to.y - ctrlY, to.x - ctrlX));

        g2d.drawString(String.valueOf(weight), (int) (x + dx), (int) (y + dy));
    }


    // 绘制曲线箭头
    private static void drawCurvedArrow(Graphics2D g2d, Point from, Point to) {
        g2d.setColor(Color.BLACK);
        double ctrlX = (from.x + to.x) / 2 + (to.y - from.y) / 4;
        double ctrlY = (from.y + to.y) / 2 - (to.x - from.x) / 4;

        QuadCurve2D q = new QuadCurve2D.Double();
        q.setCurve(from.x, from.y, ctrlX, ctrlY, to.x, to.y);
        g2d.draw(q);

        // 计算箭头位置和角度
        double angle = Math.atan2(to.y - ctrlY, to.x - ctrlX);
        double arrowLength = 20;  // 箭头长度
        double arrowAngle = Math.PI / 6;  // 箭头夹角

        // 计算箭头顶点位置
        int xArrow = (int) (to.x - arrowLength * Math.cos(angle - Math.PI + arrowAngle));
        int yArrow = (int) (to.y - arrowLength * Math.sin(angle - Math.PI + arrowAngle));

        // 绘制箭头
        g2d.drawLine(to.x, to.y, xArrow, yArrow);

        // 计算箭头两边的点
        int xArr1 = (int) (to.x - arrowLength * Math.cos(angle - Math.PI - arrowAngle));
        int yArr1 = (int) (to.y - arrowLength * Math.sin(angle - Math.PI - arrowAngle));
        int xArr2 = (int) (to.x - arrowLength * Math.cos(angle - Math.PI + arrowAngle));
        int yArr2 = (int) (to.y - arrowLength * Math.sin(angle - Math.PI + arrowAngle));

        // 绘制箭头两边
        g2d.drawLine(to.x, to.y, xArr1, yArr1);
        g2d.drawLine(to.x, to.y, xArr2, yArr2);
    }

}
