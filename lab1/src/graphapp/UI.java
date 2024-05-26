package graphapp;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.File;


public class UI extends JFrame {
    private TextToGraph textGraph;
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

    public UI() {
        // 构造函数初始化TextToGraph对象并设置窗口标题、大小和默认关闭操作。使用BorderLayout作为布局管理器。
        textGraph = new TextToGraph();

        setTitle("Graph GUI");
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 文件加载面板
        // 文件加载面板
        JPanel pnlFileInput = new JPanel();
        btnLoadFile = new JButton("Load File");
        pnlFileInput.add(btnLoadFile);
        add(pnlFileInput, BorderLayout.NORTH);

        // 图形显示面板
        pnlGraphDisplay = new GraphPath();
        add(pnlGraphDisplay, BorderLayout.CENTER);

        // 文件加载按钮的事件监听器
        btnLoadFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                loadFile(selectedFile.getAbsolutePath());
            }
        });

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
        btnGenerateText = new JButton("Text Generate");
        txtGeneratedTextResult = new JTextArea(6, 40);
        txtGeneratedTextResult.setLineWrap(true);
        txtGeneratedTextResult.setWrapStyleWord(true);
        txtGeneratedTextResult.setEditable(false);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5); // 添加一些边距

        // 添加Input Text标签和文本框
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.anchor = GridBagConstraints.EAST; // 标签靠右对齐
        pnlGenerateText.add(new JLabel("Input Text:"), gbc2);

        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.anchor = GridBagConstraints.WEST; // 文本框靠左对齐
        pnlGenerateText.add(txtInputText, gbc2);

        // 添加生成按钮
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.gridwidth = 2;
        gbc2.anchor = GridBagConstraints.CENTER;
        pnlGenerateText.add(btnGenerateText, gbc2);

        // 添加结果显示区域
        gbc2.gridx = 0;
        gbc2.gridy = 2;
        gbc2.gridwidth = 2;
        gbc2.fill = GridBagConstraints.BOTH;
        pnlGenerateText.add(new JScrollPane(txtGeneratedTextResult), gbc2);

        tabbedPane.addTab("Generate Text", pnlGenerateText);

        btnGenerateText.addActionListener(e -> generateNewText());


        // Shortest Path Panel 最短路径面板
        JPanel pnlShortestPath = new JPanel(new GridBagLayout());
        txtShortestPathWord1 = new JTextField(10);
        txtShortestPathWord2 = new JTextField(10);
        btnShortestPath = new JButton("Shortest Path");
        txtShortestPathResult = new JTextArea(6, 40);
        txtShortestPathResult.setLineWrap(true);
        txtShortestPathResult.setWrapStyleWord(true);
        txtShortestPathResult.setEditable(false);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(5, 5, 5, 5); // 添加一些边距

        // 添加Word 1标签和文本框
        gbc3.gridx = 0;
        gbc3.gridy = 0;
        gbc3.anchor = GridBagConstraints.WEST;
        pnlShortestPath.add(new JLabel("Word 1:"), gbc3);

        gbc3.gridx = 1;
        pnlShortestPath.add(txtShortestPathWord1, gbc3);

        // 添加Word 2标签和文本框
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.anchor = GridBagConstraints.WEST;
        pnlShortestPath.add(new JLabel("Word 2:"), gbc3);

        gbc3.gridx = 1;
        pnlShortestPath.add(txtShortestPathWord2, gbc3);

        // 添加按钮
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.gridwidth = 2;
        gbc3.anchor = GridBagConstraints.CENTER;
        pnlShortestPath.add(btnShortestPath, gbc3);

        // 添加结果显示区域
        gbc3.gridx = 0;
        gbc3.gridy = 3;
        gbc3.gridwidth = 2;
        gbc3.fill = GridBagConstraints.BOTH;
        pnlShortestPath.add(new JScrollPane(txtShortestPathResult), gbc3);

        tabbedPane.addTab("Shortest Path", pnlShortestPath);

        btnShortestPath.addActionListener(e -> calculateShortestPath());



        // Random Walk Panel 随机游走面板
        JPanel pnlRandomWalk = new JPanel(new GridBagLayout());
        btnRandomWalk = new JButton("Start");
        btnStopRandomWalk = new JButton("Stop");
        txtRandomWalkResult = new JTextArea(6, 40);
        txtRandomWalkResult.setLineWrap(true);
        txtRandomWalkResult.setWrapStyleWord(true);
        txtRandomWalkResult.setEditable(false);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 0;
        gbc4.gridwidth = 1;
        pnlRandomWalk.add(btnRandomWalk, gbc4);
        gbc4.gridx = 1;
        pnlRandomWalk.add(btnStopRandomWalk, gbc4);
        gbc4.gridx = 0;
        gbc4.gridy = 1;
        gbc4.gridwidth = 2;
        gbc4.fill = GridBagConstraints.BOTH;
        pnlRandomWalk.add(new JScrollPane(txtRandomWalkResult), gbc4);

        tabbedPane.addTab("Random Walk", pnlRandomWalk);

        btnRandomWalk.addActionListener(e -> performRandomWalk());
        btnStopRandomWalk.addActionListener(e -> stopRandomWalk = true);


//        JPanel shortestPathsFromPanel = new JPanel();
//        txtShortestPathsFrom = new JTextField(10);
//        btnAllShortestPaths = new JButton("Calculate All Paths");
//        txtAllShortestPathsResult = new JTextArea(6, 40);
//        txtAllShortestPathsResult.setLineWrap(true);
//        txtAllShortestPathsResult.setWrapStyleWord(true);
//        txtAllShortestPathsResult.setEditable(false);
//        shortestPathsFromPanel.setLayout(new BorderLayout());
//        JPanel shortestPathsFromInputPanel = new JPanel();
//        shortestPathsFromInputPanel.add(new JLabel("From:"));
//        shortestPathsFromInputPanel.add(txtShortestPathsFrom);
//        shortestPathsFromInputPanel.add(btnAllShortestPaths);
//        shortestPathsFromPanel.add(shortestPathsFromInputPanel, BorderLayout.NORTH);
//        shortestPathsFromPanel.add(new JScrollPane(txtAllShortestPathsResult), BorderLayout.CENTER);
//        tabbedPane.addTab("All Paths", shortestPathsFromPanel);
//
//        btnAllShortestPaths.addActionListener(e -> calculateAllShortestPaths());


        add(tabbedPane, BorderLayout.SOUTH);
    }

    private void loadFile(String filePath) {
        try {
            textGraph.readTextFile(filePath);
            textGraph.printGraph();
            GraphDrawer.showDirectedGraph(textGraph.getGraph(), "directed_graph.png");
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

//    private void calculateAllShortestPaths() {
//        // 获取用户输入并进行预处理
//        String word = txtShortestPathsFrom.getText().trim().toLowerCase();
//
//        // 从文本图中计算所有从输入词开始的最短路径
//        Map<String, List<String>> allPaths = textGraph.calcShortestPathsFrom(word);
//
//        // 如果没有找到路径，显示相应的提示信息
//        if (allPaths.isEmpty()) {
//            txtAllShortestPathsResult.setText("No paths from \"" + word + "\" in the graph!");
//        } else {
//            // 构建结果字符串
//            StringBuilder result = new StringBuilder("Shortest paths from \"" + word + "\":\n");
//            for (Map.Entry<String, List<String>> entry : allPaths.entrySet()) {
//                // 拼接路径
//                result.append(String.join(" -> ", entry.getValue()));
//                // 添加路径总权重
//                result.append(" (Total weight: ").append(entry.getValue().size() - 1).append(")\n");
//            }
//            // 将结果设置到文本区域中
//            txtAllShortestPathsResult.setText(result.toString());
//        }
//    }


    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
        UI frame = new UI();
        frame.setVisible(true);
//        });
    }
}
