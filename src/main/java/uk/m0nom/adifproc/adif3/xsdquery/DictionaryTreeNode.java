package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.Data;

@Data
public class DictionaryTreeNode {
    private DictionaryTreeNode left;
    private DictionaryTreeNode right;
    private DictionaryTreeNode parent;
    private Character value;
    private String keyword;

    public DictionaryTreeNode(DictionaryTreeNode parent, Character value) {
        this.parent = parent;
        this.value = value;
    }

    public DictionaryTreeNode add(Character newValue) {
        int compare = newValue.compareTo(value);
        if (compare < 0) {
            if (left == null) {
                left = new DictionaryTreeNode(this, newValue);
            }
            return left;
        } else if (compare > 0) {
            if (right == null) {
                right = new DictionaryTreeNode(this, newValue);
            }
            return right;
        }
        return this;
    }

    @Override
    public String toString() { return "" + value; }
}
