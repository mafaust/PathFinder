import java.util.*;

/**
 * A class representing an undirected graph of generic nodes
 *
 * @param <T> generic type of nodes
 * @author Marek Mazur
 */

public class MyUndirectedGraph<T> implements UndirectedGraph<T>{
    private Map<T, Set<Edge>> nodes;
    private int numberOfEdges;

    MyUndirectedGraph(){
        this.nodes = new HashMap<>();
        this.numberOfEdges = 0;
    }

    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    @Override
    public boolean add(T newNode) {
        if(nodes.containsKey(newNode)){
            return false;
        } else {
            nodes.put(newNode, new HashSet<>());
            return true;
        }
    }

    @Override
    public boolean connect(T node1, T node2, int cost) {
        if(cost < 1){
            return false;
        }

        // if nodes are already connected then change the weight
        if(isConnected(node1, node2)){
            for(Edge e : nodes.get(node1)){
                if(e.nodeTo.equals(node2)){
                    e.weight = cost;
                }
            } for(Edge e : nodes.get(node2)){
                if(e.nodeTo.equals(node1)){
                    e.weight = cost;
                }
            }
        }

        // Connect node with itself
        if(node1.equals(node2)){
            nodes.get(node1).add(new Edge(node1, cost));
            numberOfEdges++;
            return true;
        }

        if(nodes.containsKey(node1) && nodes.containsKey(node2)){
            nodes.get(node1).add(new Edge(node2, cost));
            nodes.get(node2).add(new Edge(node1, cost));
            numberOfEdges++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isConnected(T node1, T node2) {
        if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
            return false;
        }
        // The graph is undirected, so it's enough to test one edge
        for(Edge e : nodes.get(node1)){
            if(e.nodeTo.equals(node2)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCost(T node1, T node2) {
        if (nodes.containsKey(node1) && nodes.containsKey(node2)) {
            if (isConnected(node1, node2)) {
                for (Edge e : nodes.get(node1)) {
                    if (e.nodeTo.equals(node2)) {
                        return e.weight;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * An iterative implementation of the depth first search (DFS)
     * for a given graph.
     *
     * The method begins with cheking if the nodes passed as parameters
     * exist in the given graph. If the start node and the end node are
     * the same, then the path is calculated just by adding the start
     * node to the path.
     *
     * If the given nodes are included in the graph and are different,
     * then the DFS begins. Firstly the start node is pushed onto the
     * stack and added to thevisited nodes list. The DFS continues by
     * picking an arbitary non visited node connected to the node
     * currently on the top of the stack. This is done by a call to
     * the help method getUnvisitedNode(). The returned node is pushed
     * onto the stack and added to the visited nodes list. In case the
     * returned node equals to the end node a path must have been found
     * and the DFS can stop.
     *
     * If the help method getUnvisitedNode() returns a null value (all
     * nodes connected to the visited node are already visited)
     * a backtracking process begins by popping the current node from
     * the stack and a search for an unvisited node begins from the next
     * node stored in the stack. The backtracking continues until
     * an unvisited node is found or the stack is empty.
     *
     * The result of the above-mentioned operations is either an empty
     * or a nonempty stack. If the stack is empty then a path between
     * the given nodes is nonexistent. An empty list is returned.
     * If the stack is nonempty then the path is represented by the
     * nodes currently stored in the stack (the path is in reverse order).
     * The nodes are copied into a list and finally reversed list
     * 'is returned.
     *
     * @param start
     *            Node from which the depth first search should begin.
     * @param end
     *            Node that is the goal of the depth first search.
     *
     * @return a list of nodes between the start and end node
     *          (including both of them) if a path between them exists.
     *          Otherwise, returns an empty list.
     * @throws IllegalArgumentException if the nodes passed as parameters
     *         are not included in the graph.
     */
    @Override
    public List depthFirstSearch(T start, T end) {
        if(!nodes.containsKey(start) || !nodes.containsKey(end)){
            throw new IllegalArgumentException("The given nodes are not part of the graph");
        }

        Stack<T> stack = new Stack<>();
        Set<T> visited = new HashSet<>();
        List<T> path = new LinkedList<>();

        if(start.equals(end)){
            path.add(start);
            return path;
        }

        stack.push(start);
        visited.add(start);
        while(!stack.isEmpty()){
            T currentNode = getUnvisitedNode(stack.peek(), visited);
            if(currentNode == null){
                stack.pop();
            } else {
                visited.add(currentNode);
                stack.push(currentNode);
                if(currentNode.equals(end)){
                    break;
                }
            }
        }

        if (!stack.isEmpty()) {
            while (!stack.isEmpty()) {
                path.add(stack.pop());
            }
            Collections.reverse(path);
        }
        return path;
    }

    private T getUnvisitedNode(T node, Set<T> visited){
        for(Edge e : nodes.get(node)){
            if(!visited.contains((T)e.nodeTo)) {
                return (T)e.nodeTo;
            }
        }
        return null;
    }

    /**
     * Vi måste hålla reda på noder som vi har redan besökt - visited och vägen åt end-noden (via)
     * Vi adderar startnoden till kön och till visited
     * Vi tar bort den sista besökta noden från kön och lägger till alla
     * icke besökta grannar till datastrukturen tills vi är klara med alla noder.
     * Om vi har besökt noden då betyder det att det finns någon väg mellan start och end
     *
     * @param start
     *            startnoden.
     * @param end
     *            slutnoden.
     * @return
     */
    @Override
    public List breadthFirstSearch(T start, T end) {
        if(!nodes.containsKey(start) || !nodes.containsKey(end)){
            throw new IllegalArgumentException("The given nodes are not part of the graph");
        }

        Queue<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();
        Map<T,T> via = new HashMap<>();

        visited.add(start);
        queue.add(start);

        while(!queue.isEmpty()){
            T currentlyVisited = queue.poll();
            for(Edge e : nodes.get(currentlyVisited)){
                T nextNode = (T)e.nodeTo;
                if(!visited.contains(nextNode)){
                    visited.add(nextNode);
                    queue.add(nextNode);
                    via.put(nextNode, currentlyVisited);
                }
            }
        }
        return gatherPath(start, end, via);
    }

    private List<T> gatherPath(T from, T to, Map<T,T> via){
        List<T> path = new ArrayList<>();
        T where = to;
        // Go backward until node = from
        while(!where.equals(from)){
            T node = via.get(where);
            Edge e = getEdgeBetween(node, where);
            path.add((T)e.nodeTo);
            where = node;
        }
        // Add first node to path
        path.add(from);
        Collections.reverse(path);
        return path;
    }

    private Edge getEdgeBetween(T node1, T node2) {
        if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
            return null;
        }
        for(Edge e : nodes.get(node1))
            if (e.nodeTo == node2) {
                return e;
            }
        return null;
    }

    @Override
    public UndirectedGraph minimumSpanningTree() {
        UndirectedGraph<T> minimumSpanning = new MyUndirectedGraph<>();
        Queue<Edge> edges = new PriorityQueue<>();
        List<T> visited = new LinkedList<>();
        Map.Entry<T,Set<Edge>> entry = nodes.entrySet().iterator().next();
        T startNode = entry.getKey();

        addEdges(startNode, visited, edges);
        minimumSpanning.add(startNode);

        // Until all nodes are present in the MST and the priority queue is not empty
        while(!edges.isEmpty() && minimumSpanning.getNumberOfNodes() != this.getNumberOfNodes()){
            Edge minimumEdge = edges.poll();

            if(!visited.contains((T)minimumEdge.nodeTo)){
                T nextNode = (T)minimumEdge.nodeTo;
                minimumSpanning.add(nextNode);
                minimumSpanning.connect((T)minimumEdge.getNodeFrom(), nextNode, minimumEdge.weight);
                addEdges(nextNode, visited, edges);
            } else {
                continue;
            }
        }
        return minimumSpanning;
    }


    private void addEdges(T visitingNode, List<T> visited, Queue<Edge> edges){
        visited.add(visitingNode);
        for(Edge e : nodes.get(visitingNode)){
            e.setNodeFrom(visitingNode);
            if(!visited.contains(e.nodeTo)){
                edges.add(e);
            }
        }
    }

    private static class Edge<T> implements Comparable<Edge<T>> {
        private T nodeTo;
        // nodeFrom is only used in the implemenation of Prims algorithm
        private T nodeFrom;
        private int weight;

        public Edge(T nodeTo, int weight){
            this.nodeTo = nodeTo;
            this.weight = weight;
        }

        public void setNodeFrom(T nodeFrom) {
            this.nodeFrom = nodeFrom;
        }

        public T getNodeFrom() {
            return nodeFrom;
        }

        @Override
        public int compareTo(Edge<T> e) {
            return Integer.compare(weight, e.weight);
        }
    }
}
