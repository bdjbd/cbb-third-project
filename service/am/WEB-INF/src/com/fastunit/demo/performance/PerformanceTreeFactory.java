package com.fastunit.demo.performance;

import com.fastunit.context.ActionContext;
import com.fastunit.support.TreeFactory;
import com.fastunit.view.tree.Tree;

public class PerformanceTreeFactory extends TreeFactory {
	public static final int LEVEL = 3;
	public static final int SIZE = 10;

	public static final int BRANCH_SIZE = 10;

	@Override
	public Tree createTree(ActionContext ac, String treeDoamin) throws Exception {
		int level = ac.getRequestParameterInt("performance.tree.level", LEVEL);
		int size = ac.getRequestParameterInt("performance.tree.size", SIZE);

		Tree root = new Tree();
		root.setId("root");
		root.setName("root");
		for (int i = 0; i < BRANCH_SIZE; i++) {
			addTree(root, Integer.toString(i + 1), level, size);
		}
		return root;
	}

	private void addTree(Tree parent, String id, int level, int size) {
		if (level < 1) {
			return;
		}
		Tree node = new Tree();
		node.setId(id);
		node.setName(id);
		parent.add(node);
		if (level > 1) {
			level--;
			int currentSize = (level <= 1 ? size : BRANCH_SIZE);
			for (int i = 0; i < currentSize; i++) {
				addTree(node, id + "." + Integer.toString(i + 1), level, size);
			}
		}
	}

}
