package org.containers.engine.graph;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.containers.model.ArtifactWrapper;
import org.containers.model.NodeInfo;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import static org.containers.engine.graph.GraphPrinterUtils.*;

/**
 * A dependency visitor that dumps the graph to the console.
 *  
 * @author akaliutau
 */
public class ConsoleDependencyGraphDumper implements DependencyVisitor {

	private PrintStream out;

	private final Stack<NodeInfo> children = new Stack<>();
	private final List<ArtifactWrapper> dependencies = new ArrayList<>();
	private final Set<String> exclusions = new HashSet<>();
	private final Set<DependencyNode> dropped = new HashSet<>();

	public ConsoleDependencyGraphDumper() {
		this(null);
	}

	public ConsoleDependencyGraphDumper(PrintStream out) {
		this.out = (out != null) ? out : System.out;
	}

	@Override
	public boolean visitEnter(DependencyNode node) {
		out.println(formatIndentation(children) + formatNode(node));
		Artifact a = node.getArtifact();
		String key = getFQN(a);
		if (exclusions.contains(key)) {
			dropped.add(node);
			dropped.addAll(node.getChildren());
		}
		children.add(new NodeInfo(node.getChildren().size()));
		if (!dropped.contains(node)) {
			dependencies.add(new ArtifactWrapper(a));
		}
		return true;
	}

	@Override
	public boolean visitLeave(DependencyNode node) {
		if (!children.isEmpty()) {
			children.pop();
		}
		if (!children.isEmpty()) {
			children.peek().incIndex();
		}
		return true;
	}

	/**
	 * Gets a class path by concatenating the artifact files of the visited
	 * dependency nodes. Nodes with unresolved artifacts are automatically skipped.
	 * 
	 * Note: this is not the optimal way if there are many dependencies
	 * 
	 * @return The class path, using the platform-specific path separator, never
	 *         {@code null}.
	 */
	public String getClassPath() {
		StringBuilder buffer = new StringBuilder();

		for (Iterator<ArtifactWrapper> it = dependencies.iterator(); it.hasNext();) {
			ArtifactWrapper aw = it.next();
			Artifact artifact = aw.getArtifact();
			if (artifact.getFile() != null) {
				buffer.append(artifact.getFile().getAbsolutePath());
				if (it.hasNext()) {
					buffer.append(File.pathSeparatorChar);
				}
			}
		}

		return buffer.toString();
	}
	
	public void addExclusions(String... groupsToExclude) {
		for (String group : groupsToExclude) {
			exclusions.add(group);
		}
	}

	public List<ArtifactWrapper> getDependencies() {
		return dependencies;
	}
	
	private static String getFQN(Artifact a) {
		return String.format("%s:%s", a.getGroupId(), a.getArtifactId());
	}

}
