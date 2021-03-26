package org.containers.engine.graph;

import java.util.Iterator;
import java.util.Stack;

import org.containers.model.NodeInfo;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

public class GraphPrinterUtils {
	
	public static String formatIndentation(Stack<NodeInfo> children) {
		StringBuilder buffer = new StringBuilder();
		for (Iterator<NodeInfo> it = children.iterator(); it.hasNext();) {
			buffer.append(it.next().formatIndentation(!it.hasNext()));
		}
		return buffer.toString();
	}

	public static String formatNode(DependencyNode node) {
		StringBuilder buffer = new StringBuilder();
		Artifact a = node.getArtifact();
		Dependency d = node.getDependency();
		buffer.append(a);
		if (d != null && d.getScope().length() > 0) {
			buffer.append(" [").append(d.getScope());
			if (d.isOptional()) {
				buffer.append(", optional");
			}
			buffer.append("]");
		}
		String premanaged = DependencyManagerUtils.getPremanagedVersion(node);
		if (premanaged != null && !premanaged.equals(a.getBaseVersion())) {
			buffer.append(" (version managed from ").append(premanaged).append(")");
		}

		premanaged = DependencyManagerUtils.getPremanagedScope(node);
		if (premanaged != null && !premanaged.equals(d.getScope())) {
			buffer.append(" (scope managed from ").append(premanaged).append(")");
		}
		DependencyNode winner = (DependencyNode) node.getData().get(ConflictResolver.NODE_DATA_WINNER);
		if (winner != null && !ArtifactIdUtils.equalsId(a, winner.getArtifact())) {
			Artifact w = winner.getArtifact();
			buffer.append(" (conflicts with ");
			if (ArtifactIdUtils.toVersionlessId(a).equals(ArtifactIdUtils.toVersionlessId(w))) {
				buffer.append(w.getVersion());
			} else {
				buffer.append(w);
			}
			buffer.append(")");
		}
		return buffer.toString();
	}

}
