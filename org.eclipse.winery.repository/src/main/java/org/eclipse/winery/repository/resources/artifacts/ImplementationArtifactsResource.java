/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.artifacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.repository.resources.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypesResource;

/**
 * ImplementationArtifact instead of TImplementationArtifact has to be used
 * because of difference in the XSD at tImplementationArtifacts vs.
 * tDeploymentArtifacts
 */
public class ImplementationArtifactsResource extends GenericArtifactsResource<ImplementationArtifactResource, ImplementationArtifact> {

	private List<ImplementationArtifact> implementationArtifacts;


	public ImplementationArtifactsResource(List<ImplementationArtifact> implementationArtifact, INodeTypeImplementationResourceOrRelationshipTypeImplementationResource res) {
		super(ImplementationArtifactResource.class, ImplementationArtifact.class, implementationArtifact, res);
		this.implementationArtifacts = implementationArtifact;
	}

	/**
	 * @return a cast to TNodeTypeImplementationResource of the parent of this
	 *         resource.
	 */
	protected NodeTypeImplementationResource getNTI() {
		return (NodeTypeImplementationResource) this.res;
	}

	/**
	 * @return a cast to TNodeTypeImplementationResource of the parent of this
	 *         resource.
	 */
	protected RelationshipTypeImplementationResource getRTI() {
		return (RelationshipTypeImplementationResource) this.res;
	}

	@Override
	public Collection<ImplementationArtifactResource> getAllArtifactResources() {
		Collection<ImplementationArtifactResource> res = new ArrayList<>(this.implementationArtifacts.size());
		for (ImplementationArtifact da : this.implementationArtifacts) {
			ImplementationArtifactResource r = new ImplementationArtifactResource(da, this.implementationArtifacts, this.res);
			res.add(r);
		}
		return res;
	}

	/**
	 * Method to get all interfaces associated to a nodetype or relationshiptype
	 * @return a list of TInterface
	 */
	@Path("interfaces/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<?> getInterfacesOfAssociatedType() {
		boolean isNodeTypeImplementation = this.res instanceof NodeTypeImplementationResource;
		QName type;
		List<Object> interfaces = new ArrayList<>();
		if (isNodeTypeImplementation) {
			type = this.getNTI().getType();
			NodeTypeResource typeResource = (NodeTypeResource) new NodeTypesResource().getComponentInstaceResource(type);
			interfaces.addAll(typeResource.getInterfaces().onGet("true"));
		} else {
			type = this.getRTI().getType();
			RelationshipTypeResource typeResource = (RelationshipTypeResource) new RelationshipTypesResource().getComponentInstaceResource(type);
			interfaces.addAll(typeResource.getSourceInterfaces().onGet("true"));
			interfaces.addAll(typeResource.getTargetInterfaces().onGet("true"));
		}
		return interfaces;
	}

	@Override
	public String getId(ImplementationArtifact entity) {
		return entity.getName();
	}
}
