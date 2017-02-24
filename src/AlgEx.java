import java.util.*;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class AlgEx implements Runnable {
    private static class Vertex {
        Integer key;
        Vertex right;
        Vertex left;
        int minLength;

        public Vertex() {
            key = null;
            right = null;
            left = null;
            minLength = 0;
        }

        public Vertex(Integer num) {
            key = num;
            right = null;
            left = null;
            minLength = 0;
        }
    }

    private static class Tree {
        Vertex root;

        public Tree() {
            root = null;
        }

        public Tree(Vertex vertex) {
            root = vertex;
        }

        public boolean add(Vertex vertex) {
            Vertex iter = root;
            if (root == null) {
                this.root = vertex;
                return true;
            }
            while (iter != null && iter.key != vertex.key) {
                if (vertex.key > iter.key) {
                    if (iter.right == null) {
                        iter.right = vertex;
                        break;
                    } else {
                        iter = iter.right;
                    }
                } else if (vertex.key < iter.key) {
                    if (iter.left == null) {
                        iter.left = vertex;
                        break;
                    } else {
                        iter = iter.left;
                    }
                } else if (vertex.key == iter.key) {
                    return false;
                }
            }
            return true;
        }

        public void directLeftRound(Vertex vertex, FileWriter writer) throws Exception {
            if (vertex != null) {
                writer.write(vertex.key + "\n");
                directLeftRound(vertex.left, writer);
                directLeftRound(vertex.right, writer);
            }
        }

        public boolean leftDelete(Integer value) {
            Vertex iter = root;
            Vertex parent = null;
            while (iter != null && iter.key != value) {
                parent = iter;
                if (value > iter.key) {
                    iter = iter.right;
                } else if (value < iter.key) {
                    iter = iter.left;
                } else if (value == iter.key) {
                    break;
                }
            }
            if (iter == null) {
                return false;
            } else if (iter.right == null && iter.left == null) {
                deleteLeaf(iter, parent);
            } else if (!(iter.right != null && iter.left != null)) {
                deleteOneChildVertex(iter, parent);
            } else {
                deleteTwoChildVertex(iter, parent);
            }
            return true;
        }

        private void deleteLeaf(Vertex leaf, Vertex parent) {
            if (parent != null) {
                if (parent.key > leaf.key) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            } else {
                root = null;
            }
        }

        private void deleteOneChildVertex(Vertex vertex, Vertex parent) {
            Vertex next;
            if (vertex.left != null) {
                next = vertex.left;
            } else {
                next = vertex.right;
            }
            if (parent != null) {
                if (parent.key > vertex.key) {
                    parent.left = next;
                } else {
                    parent.right = next;
                }
            } else {
                if (vertex.left != null) {
                    root = vertex.left;
                } else {
                    root = vertex.right;
                }
            }
        }

        private void deleteTwoChildVertex(Vertex vertex, Vertex parent) {
            Vertex next;
            Vertex nextParent;
            next = vertex.left;
            nextParent = vertex;
            while (next.right != null) {
                nextParent = next;
                next = next.right;
            }
            if (next.left != null) {
                deleteOneChildVertex(next, nextParent);
            } else {
                deleteLeaf(next, nextParent);
            }
            next.right = vertex.right;
            if (vertex.left != next) {
                next.left = vertex.left;
            } else {
                next.left = null;
            }
            if (parent != null) {
                parent.minLength = vertex.minLength;
                if (parent.key > next.key) {
                    parent.left = next;
                } else {
                    parent.right = next;
                }
            } else {
                root = next;
            }
        }

        public void markMinLength(Vertex iter) {
            if (iter != null) {
                markMinLength(iter.left);
                markMinLength(iter.right);
                if (iter.left == null && iter.right == null) {
                    iter.minLength = 0;
                } else if (iter.left != null && iter.right != null) {
                    iter.minLength = Math.min(iter.left.minLength, iter.right.minLength) + 1;
                } else if (iter.left != null) {
                    iter.minLength = iter.left.minLength + 1;
                } else if (iter.right != null) {
                    iter.minLength = iter.right.minLength + 1;
                }
            }
        }

        private List<List<Integer>> findAllMinPaths() {
            List<List<Integer>> list = new ArrayList<>(20);
            List<Integer> path = new ArrayList<>(root.minLength + root.minLength / 3);
            path.add(root.key);
            fillAllMinPaths(list, path, root);
            return list;
        }

        private void fillAllMinPaths(List<List<Integer>> list, List<Integer> path, Vertex vertex) {
            if (vertex.left != null && vertex.right != null) {
                if (vertex.left.minLength == vertex.right.minLength) {
                    List<Integer> clone = new ArrayList<>(path);
                    path.add(vertex.left.key);
                    clone.add(vertex.right.key);
                    fillAllMinPaths(list, path, vertex.left);
                    fillAllMinPaths(list, clone, vertex.right);
                } else if (vertex.left.minLength < vertex.right.minLength) {
                    path.add(vertex.left.key);
                    fillAllMinPaths(list, path, vertex.left);
                } else {
                    path.add(vertex.right.key);
                    fillAllMinPaths(list, path, vertex.right);
                }
            } else if (vertex.left != null) {
                path.add(vertex.left.key);
                fillAllMinPaths(list, path, vertex.left);
            } else if (vertex.right != null) {
                path.add(vertex.right.key);
                fillAllMinPaths(list, path, vertex.right);
            } else {
                list.add(path);
            }
        }

        public void deleteAvgVertexOfMinLength() {
            if ((root.minLength + 1) % 2 == 0) {
                return;
            }
            List<List<Integer>> list = findAllMinPaths();
            Iterator<List<Integer>> iterList = list.iterator();
            Iterator<Integer> iterToDelete;
            List<Integer> tmp;
            Set<Integer> toDelete = new TreeSet<>();
            while (iterList.hasNext()) {
                tmp = iterList.next();
                Collections.sort(tmp);
                toDelete.add(tmp.get((root.minLength + 1) / 2));
            }
            iterToDelete = toDelete.iterator();
            while (iterToDelete.hasNext()) {
                leftDelete(iterToDelete.next());
            }
        }
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("tst.in"));
            FileWriter writer = new FileWriter("tst.out");
            Tree tree = new Tree();
            String line;
            while ((line = reader.readLine()) != null) {
                tree.add(new Vertex(Integer.parseInt(line)));
            }
            tree.markMinLength(tree.root);
            tree.deleteAvgVertexOfMinLength();
            tree.directLeftRound(tree.root, writer);
            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(null, new AlgEx(), "", 64 * 1024 * 1024).start();
    }
}
