package graphapp;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GraphGUI extends JFrame {
    private GraphDrawer graphDrawer;
    private TextToGraph textGraph;
    private JTextField txtFilePath;
    private JButton btnLoadFile;
    private GraphPath pnlGraphDisplay;
    private JTextField txtBridgeWord1;
    private JTextField txtBridgeWord2;
    private JButton btnQueryBridgeWords;
    private JTextArea txtBridgeWordsResult;
    private JTextField txtInputText;
    private JButton btnGenerateText;
    private JTextArea txtGeneratedTextResult;
    private JTextField txtShortestPathWord1;
    private JTextField txtShortestPathWord2;
    private JButton btnShortestPath;
    private JTextArea txtShortestPathResult;
    private JButton btnRandomWalk;
    private JButton btnStopRandomWalk;
    private JTextArea txtRandomWalkResult;
    private JButton btnAllShortestPaths;
    private JTextField txtShortestPathsFrom;
    private JTextArea txtAllShortestPathsResult;
    private volatile boolean stopRandomWalk;

    public GraphGUI() {
        // 构造函数初始化TextToGraph对象并设置窗口标题、大小和默认关闭操作。使用BorderLayout作为布局管理器。
        textGraph = new TextToGraph();

        setTitle("Graph GUI");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 文件加载面板
        JPanel pnlFileInput = new JPanel();
        txtFilePath = new JTextField(60);
        btnLoadFile = new JButton("Load File");
        pnlFileInput.add(new JLabel("File Path:"));
        pnlFileInput.add(txtFilePath);
        pnlFileInput.add(btnLoadFile);
        add(pnlFileInput, BorderLayout.NORTH);

        // 图形显示面板
        pnlGraphDisplay = new GraphPath();
        add(pnlGraphDisplay, BorderLayout.CENTER);

        // 文件加载按钮的事件监听器
        btnLoadFile.addActionListener(e -> loadFile());

        // 创建一个选项卡面板，允许不同的功能在不同的选项卡中展示。
        JTabbedPane tabbedPane = new JTabbedPane();

        // Bridge Words Panel 桥接词面板
        JPanel pnlBridgeWords = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        txtBridgeWord1 = new JTextField(10);
        txtBridgeWord2 = new JTextField(10);
        btnQueryBridgeWords = new JButton("Query Bridge Words");
        txtBridgeWordsResult = new JTextArea(6, 40);
        txtBridgeWordsResult.setLineWrap(true);
        txtBridgeWordsResult.setWrapStyleWord(true);
        txtBridgeWordsResult.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlBridgeWords.add(new JLabel("Word 1:"), gbc);
        gbc.gridx = 1;
        pnlBridgeWords.add(txtBridgeWord1, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlBridgeWords.add(new JLabel("Word 2:"), gbc);
        gbc.gridx = 1;
        pnlBridgeWords.add(txtBridgeWord2, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pnlBridgeWords.add(btnQueryBridgeWords, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlBridgeWords.add(new JScrollPane(txtBridgeWordsResult), gbc);

        tabbedPane.addTab("Bridge Words", pnlBridgeWords);
        // 监听器 queryBridgeWords
        btnQueryBridgeWords.addActionListener(e -> queryBridgeWords());

        // Generate Text Panel 生成文本面板
        JPanel pnlGenerateText = new JPanel(new GridBagLayout());
        txtInputText = new JTextField(30);
        btnGenerateText = new JButton("Generate Text");
        txtGeneratedTextResult = new JTextArea(6, 40);
        txtGeneratedTextResult.setLineWrap(true);
        txtGeneratedTextResult.setWrapStyleWord(true);
        txtGeneratedTextResult.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlGenerateText.add(new JLabel("Input Text:"), gbc);
        gbc.gridx = 1;
        pnlGenerateText.add(txtInputText, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        pnlGenerateText.add(btnGenerateText, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlGenerateText.add(new JScrollPane(txtGeneratedTextResult), gbc);

        tabbedPane.addTab("Generate Text", pnlGenerateText);

        btnGenerateText.addActionListener(e -> generateNewText());

        // Shortest Path Panel 最短路径面板
        JPanel pnlShortestPath = new JPanel(new GridBagLayout());
        txtShortestPathWord1 = new JTextField(10);
        txtShortestPathWord2 = new JTextField(10);
        btnShortestPath = new JButton("Calculate Shortest Path");
        txtShortestPathResult = new JTextArea(6, 40);
        txtShortestPathResult.setLineWrap(true);
        txtShortestPathResult.setWrapStyleWord(true);
        txtShortestPathResult.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlShortestPath.add(new JLabel("Word 1:"), gbc);
        gbc.gridx = 1;
        pnlShortestPath.add(txtShortestPathWord1, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlShortestPath.add(new JLabel("Word 2:"), gbc);
        gbc.gridx = 1;
        pnlShortestPath.add(txtShortestPathWord2, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pnlShortestPath.add(btnShortestPath, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlShortestPath.add(new JScrollPane(txtShortestPathResult), gbc);

        tabbedPane.addTab("Shortest Path", pnlShortestPath);

        btnShortestPath.addActionListener(e -> calculateShortestPath());

        // Random Walk Panel 随机游走面板
        JPanel pnlRandomWalk = new JPanel(new GridBagLayout());
        btnRandomWalk = new JButton("Random Walk");
        btnStopRandomWalk = new JButton("Stop Random Walk");
        txtRandomWalkResult = new JTextArea(6, 40);
        txtRandomWalkResult.setLineWrap(true);
        txtRandomWalkResult.setWrapStyleWord(true);
        txtRandomWalkResult.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        pnlRandomWalk.add(btnRandomWalk, gbc);
        gbc.gridx = 1;
        pnlRandomWalk.add(btnStopRandomWalk, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        pnlRandomWalk.add(new JScrollPane(txtRandomWalkResult), gbc);

        tabbedPane.addTab("Random Walk", pnlRandomWalk);

        btnRandomWalk.addActionListener(e -> performRandomWalk());
        btnStopRandomWalk.addActionListener(e -> stopRandomWalk = true);


        add(tabbedPane, BorderLayout.SOUTH);
    }

    private void loadFile() {
        String filePath = txtFilePath.getText();
        try {
            textGraph.readTextFile(filePath);
            textGraph.printGraph();
            GraphDrawer.drawDirectedGraph(textGraph.getGraph(), "directed_graph.png");
            pnlGraphDisplay.setGraph(textGraph.getGraph(), textGraph.getFirstWord());
            pnlGraphDisplay.repaint();
            JOptionPane.showMessageDialog(this, "File loaded successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void queryBridgeWords() {
        String word1 = txtBridgeWord1.getText().trim().toLowerCase();
        String word2 = txtBridgeWord2.getText().trim().toLowerCase();
        String result = textGraph.queryBridgeWords(word1, word2);
        txtBridgeWordsResult.setText(result);
    }

    private void generateNewText() {
        String inputText = txtInputText.getText().trim();
        String newText = textGraph.generateNewText(inputText);
        txtGeneratedTextResult.setText(newText);
    }

    private void calculateShortestPath() {
        String word1 = txtShortestPathWord1.getText().trim().toLowerCase();
        String word2 = txtShortestPathWord2.getText().trim().toLowerCase();
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        distances.put(word1, 0);
        pq.add(word1);
        if (word2.isEmpty()) {
            Map<String, List<String>> allPaths = textGraph.calcShortestPathsFrom(word1);
            if (allPaths.isEmpty()) {
                txtShortestPathResult.setText("No paths from \"" + word1 + "\" in the graph!");
            } else {
                StringBuilder result = new StringBuilder("Shortest paths from \"" + word1 + "\":\n");
                for (Map.Entry<String, List<String>> entry : allPaths.entrySet()) {
                    result.append(String.join(" -> ", entry.getValue()));
                    result.append(" (Total weight: ").append(entry.getValue().size() - 1).append(")\n");
                }
                txtShortestPathResult.setText(result.toString());
            }
        } else {
            String result = textGraph.calcShortestPath(word1, word2);
            txtShortestPathResult.setText(result);
            List<String> path = textGraph.getShortestPath(word1, word2);
            pnlGraphDisplay.setHighlightedPath(path);
            pnlGraphDisplay.repaint();
        }
    }

//    private void calculateAllShortestPaths() {
//        String word = txtShortestPathsFrom.getText().trim().toLowerCase();
//        Map<String, List<String>> allPaths = textGraph.calcShortestPathsFrom(word);
//        if (allPaths.isEmpty()) {
//            txtAllShortestPathsResult.setText("No paths from \"" + word + "\" in the graph!");
//        } else {
//            StringBuilder result = new StringBuilder("Shortest paths from \"" + word + "\":\n");
//            for (Map.Entry<String, List<String>> entry : allPaths.entrySet()) {
//                result.append(String.join(" -> ", entry.getValue()));
//                result.append(" (Total weight: ").append(entry.getValue().size() - 1).append(")\n");
//            }
//            txtAllShortestPathsResult.setText(result.toString());
//        }
//    }

    private void performRandomWalk() {
        stopRandomWalk = false;
        txtRandomWalkResult.setText("");
        new Thread(() -> {
            String result = textGraph.randomWalk(
                    () -> stopRandomWalk,
                    step -> SwingUtilities.invokeLater(() -> txtRandomWalkResult.append(step + "\n"))
            );
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Random walk result saved to random_walk.txt\n" + result));
        }).start();
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            GraphGUI frame = new GraphGUI();
//            frame.setVisible(true);
//        });
//    }
}
