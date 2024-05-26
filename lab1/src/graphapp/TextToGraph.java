package graphapp;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.Consumer;

public class TextToGraph {
    private Map<String, Map<String, Integer>> graph;
    private String firstWord;

    public TextToGraph() {
        graph = new HashMap<>();
    }

    public void readTextFile(String filePath) throws IOException {
        graph = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));// 读取指定路径的文件内容
        String text = String.join(" ", lines).toLowerCase(); // 将列表中的所有行合并成一个字符串，并转换为小写
        text = text.replaceAll("[^a-z ]", " ");// 使用正则表达式将所有非小写字母和空格的字符替换为空格
        String[] words = text.split("\\s+");// 使用正则表达式将文本字符串按一个或多个空白字符拆分成单词数组
        if (words.length > 0) {// 如果单词数组非空，将第一个单词设置为图的起始点
            firstWord = words[0];
        }

        // 遍历单词数组，从第一个单词到倒数第二个单词，目的是构建单词之间的边
        for (int i = 0; i < words.length - 1; i++) {
            // 当前单词和下一个单词
            String word1 = words[i];
            String word2 = words[i + 1];
            graph.putIfAbsent(word1, new HashMap<>());// 确保图中包含word1节点，如果没有，则添加一个新的空邻居列表

            // 确保图中包含word2节点，如果没有，则添加一个新的空邻居列表
            graph.putIfAbsent(word2, new HashMap<>());

            // 获取word1的邻居列表，将word2添加到该列表中
            // 使用getOrDefault方法获取从word1到word2的当前权重（默认值为0），然后将权重加1
            graph.get(word1).put(word2, graph.get(word1).getOrDefault(word2, 0) + 1);
        }
    }


    public Map<String, Map<String, Integer>> getGraph() {
        return graph;
    }

    public String getFirstWord() {
        return firstWord;
    }

    public void printGraph() {
        // 遍历图中所有节点（键）
        for (String from : graph.keySet()) {
            // 打印当前节点
            System.out.print(from + " -> ");

            // 遍历当前节点的所有相邻节点及对应的权重
            for (String to : graph.get(from).keySet()) {
                // 打印相邻节点及边的权重
                System.out.print(to + " (" + graph.get(from).get(to) + ") ");
            }

            // 打印完一个节点的所有相邻节点后换行
            System.out.println();
        }
    }


    public String queryBridgeWords(String word1, String word2) {
        // 检查图中是否包含word1和word2这两个单词
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            // 如果图中不包含word1和word2中的任何一个或两个，则返回相应的消息
            if (!graph.containsKey(word1) && !graph.containsKey(word2)) {
                return "There are no \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
            } else if (!graph.containsKey(word1)) {
                return "There is no \"" + word1 + "\" in the graph!";
            } else {
                return "There is no \"" + word2 + "\" in the graph!";
            }
        }

        // 存储桥接词的集合
        Set<String> bridgeWords = new HashSet<>();
        // 获取word1的所有邻居节点
        Map<String, Integer> neighbors = graph.get(word1);
        // 遍历word1的所有邻居节点
        for (String neighbor : neighbors.keySet()) {
            // 如果某个邻居节点也连接到word2，则这个邻居节点就是一个桥接词
            if (graph.get(neighbor).containsKey(word2)) {
                bridgeWords.add(neighbor);
            }
        }

        // 如果没有找到任何桥接词，则返回相应的消息
        if (bridgeWords.isEmpty()) {
            return "From \"" + word1 + "\" to \"" + word2 + "there is no bridge word \"!";
        } else {
            String result;
            // 根据桥接词的数量选择
            if (bridgeWords.size() > 1) {
                result = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: ";
            } else {
                result = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: ";
            }
            // 将所有桥接词连接成一个字符串
            result += String.join(", ", bridgeWords);
            return result;
        }
    }

    public String generateNewText(String inputText) {
        //输入文本处理
        String[] words = inputText.toLowerCase().split("\\s+");
        StringBuilder newText = new StringBuilder();
        Random rand = new Random();

        // 遍历单词数组，但不包括最后一个单词，因为最后一个单词后面没有单词需要桥接。
        for (int i = 0; i < words.length - 1; i++) {
            //将当前单词和一个空格添加到newText中
            newText.append(words[i]).append(" ");
            String word1 = words[i];
            String word2 = words[i + 1];
            // 初始化一个Set用于存储桥接词
            Set<String> bridgeWords = new HashSet<>();

            //检查图中是否存在当前单词
            if (graph.containsKey(word1)) {
                for (String neighbor : graph.get(word1).keySet()) {
                    if (graph.get(neighbor).containsKey(word2)) {
                        bridgeWords.add(neighbor);
                    }
                }
            }

            //如果有桥接词,将桥接词集合转换为数组,随机选择一个桥接词,将桥接词和一个空格添加到新文本中。
            if (!bridgeWords.isEmpty()) {
                String[] bridges = bridgeWords.toArray(new String[0]);
                String bridgeWord = bridges[rand.nextInt(bridges.length)];
                newText.append(bridgeWord).append(" ");
            }
        }
        //将最后一个单词添加到新文本中，因为最后一个单词后面没有单词需要桥接
        newText.append(words[words.length - 1]);

        return newText.toString();
    }

    public String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            if (!graph.containsKey(word1) && !graph.containsKey(word2)) {
                return "There are no \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
            } else if (!graph.containsKey(word1)) {
                return "There is no \"" + word1 + "\" in the graph!";
            } else {
                return "There is no \"" + word2 + "\" in the graph!";
            }
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(word1, 0);
        pq.add(word1);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) break;

            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        if (distances.get(word2) == Integer.MAX_VALUE) {
            return "From \"" + word1 + "\" to \"" + word2 + "there is no path \"!";
        }

        List<String> path = new ArrayList<>();
        for (String at = word2; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        StringBuilder result = new StringBuilder("Shortest path from \"" + word1 + "\" to \"" + word2 + "\": ");
        result.append(String.join(" -> ", path));
        result.append(" (Total weight: ").append(distances.get(word2)).append(")");

        return result.toString();
    }

    public List<String> getShortestPath(String word1, String word2) {
        // 检查单词是否在图中
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return Collections.emptyList();
        }


        Map<String, Integer> distances = new HashMap<>();//初始化数据结构
        Map<String, String> previous = new HashMap<>();//存储最短路径中的前一个节点，用于构造路径。
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));//优先队列，用于选择当前距离最小的节点

        for (String node : graph.keySet()) {
            //存储从word1到每个节点的最短距离，初始值为无穷大
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(word1, 0);
        pq.add(word1);

        //Dijkstra算法核心部分
        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) break;

            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        //检查是否存在路径:如果目标节点word2的距离仍为无穷大，说明不存在从word1到word2的路径
        if (distances.get(word2) == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        //构造最短路径
        List<String> path = new ArrayList<>();
        for (String at = word2; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return path;
    }


    public Map<String, List<String>> calcShortestPathsFrom(String word) {
        //初始化返回值和检查节点存在性
        Map<String, List<String>> shortestPaths = new HashMap<>();
        if (!graph.containsKey(word)) {
            return shortestPaths;
        }

        //初始化数据结构
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(word, 0);
        pq.add(word);

        //Dijkstra算法核心部分
        while (!pq.isEmpty()) {
            String current = pq.poll();

            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        //构造所有最短路径
        for (String target : graph.keySet()) {
            if (!target.equals(word) && distances.get(target) != Integer.MAX_VALUE) {
                List<String> path = new ArrayList<>();
                for (String at = target; at != null; at = previous.get(at)) {
                    path.add(at);
                }
                Collections.reverse(path);
                shortestPaths.put(target, path);
            }
        }

        return shortestPaths;
    }

    public String randomWalk(Supplier<Boolean> stopSignal, Consumer<String> stepCallback) {
        // 检查图是否为空
        if (graph.isEmpty()) {
            return "Graph is empty, input a file first";
        }

        // 初始化随机数生成器和起始节点
        Random randomGenerator = new Random();
        List<String> nodes = new ArrayList<>(graph.keySet());
        String currentNode = nodes.get(randomGenerator.nextInt(nodes.size())); // 随机选择起始节点
        StringBuilder walkPath = new StringBuilder(currentNode);
        Set<String> visitedEdges = new HashSet<>();

        // 处理第一个节点的步骤回调
        stepCallback.accept("Step: " + currentNode);

        // 主循环：当前节点存在于图中且有邻居节点。
        while (graph.containsKey(currentNode) && !graph.get(currentNode).isEmpty()) {
            if (stopSignal.get()) {
                break;
            }
            Map<String, Integer> neighbors = graph.get(currentNode);
            List<String> neighborList = new ArrayList<>(neighbors.keySet());
            String nextNode = neighborList.get(randomGenerator.nextInt(neighborList.size())); // 随机选择下一个节点
            String edge = currentNode + "->" + nextNode;

            // 检查是否已经访问过该边
            if (visitedEdges.contains(edge)) {
                break;
            }

            // 添加边到已访问集合，并更新当前节点
            visitedEdges.add(edge);
            currentNode = nextNode;
            walkPath.append(" -> ").append(currentNode);

            // 处理当前步骤的回调
            stepCallback.accept("Step: " + currentNode);

            // 1秒延迟
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 写入结果到文件
        String result = walkPath.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("random_walk.txt"))) {
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



}
