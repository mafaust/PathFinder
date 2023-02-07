import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {
    private Map<T, Set<Edge>> nodes = new HashMap<>();

    @Override
    public void add(Object node) {
        nodes.putIfAbsent((T)node, new HashSet<>());
    }

    @Override
    public void connect(Object node1, Object node2, String name, int weight) {
        if(!nodes.containsKey((T)node1) || !nodes.containsKey((T)node2)){
            throw new NoSuchElementException("Node doesn't exist!");
        }
        if(weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }
        for (Edge e : nodes.get((T)node1))
            if (e.getNodeTo() == (T)node2) {
                throw new IllegalStateException("Connection already exists");
            }

        Set<Edge> se1 = nodes.get((T)node1);
        Edge e1 = new Edge((T)node2, name, weight);
        se1.add(e1);

        Set<Edge> se2 = nodes.get((T)node2);
        Edge e2 = new Edge((T)node1, name, weight);
        se2.add(e2);
    }

    @Override
    public void setConnectionWeight(Object node1, Object node2, int weight) {
        if(!nodes.containsKey((T)node1) || !nodes.containsKey((T)node2)){
            throw new NoSuchElementException("Node doesn't exist!");
        }
        if(weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }

        Edge edgeTo = getEdgeBetween((T)node1, (T)node2);
        Edge edgeFrom = getEdgeBetween((T)node2, (T)node1);

        if(edgeTo != null && edgeFrom != null){
            edgeTo.setWeight(weight);
            edgeFrom.setWeight(weight);
        }else{
            throw new NoSuchElementException("Edge doesn't exist!");
        }
    }

    @Override
    public Set getNodes() {
        return nodes.keySet();
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public Collection<Edge> getEdgesFrom(Object node) {
        if(!nodes.containsKey((T)node)){
            throw new NoSuchElementException("Node doesn't exist!");
        }
        return nodes.get((T)node);
    }

    @Override
    public Edge getEdgeBetween(Object node1, Object node2) {
        if(!nodes.containsKey((T)node1) || !nodes.containsKey((T)node2)){
            throw new NoSuchElementException("Node doesn't exist!");
        }
        for(Edge e : nodes.get((T)node1))
            if (e.getNodeTo() == (T)node2) {
                return e;
            }
        return null;
    }

    @Override
    public void disconnect(Object node1, Object node2) {
        Edge toDelete1 = getEdgeBetween((T)node1, (T)node2);
        Edge toDelete2 = getEdgeBetween((T)node2, (T)node1);
        if(toDelete1 != null && toDelete2 != null ){
            nodes.get((T)node1).remove(toDelete1);
            nodes.get((T)node2).remove(toDelete2);
        }else{
            throw new IllegalStateException("Edge doesn't exist!");
        }
    }

    @Override
    public void remove(Object node) {
        if(!nodes.containsKey((T)node)){
            throw new NoSuchElementException("Node doesn't exist!");
        }
        for (Set<Edge> se : nodes.values())
            se.removeIf(e -> e.getNodeTo() == node);
        nodes.remove((T)node);
    }

    @Override
    public boolean pathExists(Object from, Object to) {
        if(!nodes.containsKey((T)from) || !nodes.containsKey((T)to)){
            return false;
        }
        Set<T> visited = new HashSet<>();
        depthFirstSearch((T)from, visited);
        return visited.contains((T)to);
    }

    private void depthFirstSearch(T where, Set<T> visited){
        // Addera startnoden och vid rekursion den besökta noden
        visited.add(where);
        // Gå igenom alla grannar
        for(Edge e : nodes.get(where))
            // Om visited inte innehåller den besökta noden anropa metoden rekursivt
            if(!visited.contains((T)e.getNodeTo()))
                depthFirstSearch((T)e.getNodeTo(), visited);
    }


    public List<Edge> getPath(Object from, Object to) {
        //Implementera en kö
        LinkedList<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();
        Map<T,T> via = new HashMap<>();
        // Här börjar algoritmen breadth search first
        visited.add((T)from);
        // Addlast för tydlighetens skull
        queue.addLast((T)from);
        // så länge kö är inte tom
        while(!queue.isEmpty()){
            // plocka fram det som står först i kön
            T node = queue.pollFirst();
            for(Edge e : nodes.get(node)){
                // för varje förbindelse spara destinationen
                T dest = (T)e.getNodeTo();
                if(!visited.contains(dest)){
                    visited.add(dest);
                    queue.add(dest);
                    via.put(dest, node);
                }
            }
        }
        if(!visited.contains(to))
            return null;
        else
            return gatherPath((T)from, (T)to, via);
    }



    private List<Edge> gatherPath(T from, T to, Map<T,T> via){
        List<Edge> path = new ArrayList<>();
        // Om visited-set innehåller inte destinationen då kan man returnera null
        T where = to;
        // går bakifrån - tills where är noden=from
        while(!where.equals((T)from)){
            T node = via.get(where);
            Edge e = getEdgeBetween(node, where);
            path.add(e);
            where = node;
        }
        //vända ordningen (to - from) --> (where - to)
        Collections.reverse(path);
        return path;
    }

    @Override
    public String toString(){
        String str = "";
        for (T node : nodes.keySet()) {
            str += node + ": ";
            for (Edge e : nodes.get(node)){
                str += e.toString();
            }
            str += "\n";
        }
        return str;
    }
}
