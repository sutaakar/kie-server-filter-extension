/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.custom;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.kie.server.remote.rest.common.resource.KieServerRestImpl;

import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Provider
public class KieServerEndpointContainerRequestFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Check only request targeting KieServerRestImpl
        if (resourceInfo.getResourceClass().isAssignableFrom(KieServerRestImpl.class)) {
            if ("GET".equals(requestContext.getMethod())) {
                // GET requests are allowed
                return;
            } else if (requestContext.getHeaders().containsKey("allowed-modification")) {
                // If request contains header `allowed-modification` then it is allowed
                return;
            } else {
                // Forbidden otherwise
                requestContext.abortWith(Response.status(SERVICE_UNAVAILABLE).entity("Endpoint disabled").build());
            }
        }
    }
}
