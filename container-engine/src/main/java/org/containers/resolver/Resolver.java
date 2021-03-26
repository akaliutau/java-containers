package org.containers.resolver;

import org.containers.model.ResolverResult;

public interface Resolver {

	ResolverResult resolve(String coords);

}