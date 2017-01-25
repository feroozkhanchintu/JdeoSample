package com.ferooz.sourcecodearchitect.graph;

/**
 * Created by Ferooz on 12/01/17.
 */
public class Node {
    private static Integer num = 0;
    public Integer id;
    private String name;
    private String type;
    public Integer startLine;
    private Integer offset;
    private Integer length;
    private Integer endLine;

    public Node(String name, String type, Integer startLine, Integer offset, Integer length, Integer endLine) {
        this.id = num++;
        this.name = name;
        this.type = type;
        this.startLine = startLine;
        this.offset = offset;
        this.length = length;
        this.endLine = endLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (!name.equals(node.name)) return false;
        if (!type.equals(node.type)) return false;
        if (!startLine.equals(node.startLine)) return false;
        if (!offset.equals(node.offset)) return false;
        if (!length.equals(node.length)) return false;
        return endLine.equals(node.endLine);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + startLine.hashCode();
        result = 31 * result + offset.hashCode();
        result = 31 * result + length.hashCode();
        result = 31 * result + endLine.hashCode();
        return result;
    }

    public String toString(){
        return startLine + "";
    }
}
