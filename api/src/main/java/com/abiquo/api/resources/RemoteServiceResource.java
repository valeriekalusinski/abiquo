/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.api.resources;

import static com.abiquo.server.core.infrastructure.RemoteService.STATUS_ERROR;
import static com.abiquo.server.core.infrastructure.RemoteService.STATUS_SUCCESS;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

@Parent(RemoteServicesResource.class)
@Path(RemoteServiceResource.REMOTE_SERVICE_PARAM)
@Controller
public class RemoteServiceResource extends AbstractResource
{
    // @Autowired
    @Resource(name = "remoteServiceService")
    private RemoteServiceService service;

    public static final String REMOTE_SERVICE = "remoteservice";

    public static final String REMOTE_SERVICE_PARAM = "{" + REMOTE_SERVICE + "}";

    public static final String CHECK_RESOURCE = "action/" + RemoteServiceService.CHECK_RESOURCE;

    /*
     * REST methods
     */

    @GET
    public RemoteServiceDto getRemoteService(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(REMOTE_SERVICE) String serviceType, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        validatePathParameters(datacenterId, serviceType);

        RemoteServiceType type = RemoteServiceType.valueOf(serviceType.toUpperCase());
        RemoteService remoteService = service.getRemoteService(datacenterId, type);

        return createTransferObject(remoteService, restBuilder);
    }

    @GET
    @Path(CHECK_RESOURCE)
    public RemoteServiceDto pingRemoteService(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(REMOTE_SERVICE) String serviceType, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        RemoteServiceDto rs = getRemoteService(datacenterId, serviceType, restBuilder);

        ErrorsDto errors = service.checkStatus(rs.getType(), rs.getUri());

        rs.setStatus(errors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR);
        return rs;
    }

    @PUT
    public RemoteServiceDto modifyRemoteService(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(REMOTE_SERVICE) String serviceType, RemoteServiceDto remoteService,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, serviceType);

        RemoteServiceType type = RemoteServiceType.valueOf(serviceType.toUpperCase());
        RemoteService old = service.getRemoteService(datacenterId, type);

        RemoteServiceDto r = service.modifyRemoteService(old.getId(), remoteService);

        addLinks(restBuilder, r, datacenterId);

        return r;
    }

    @DELETE
    public void deleteRemoteService(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(REMOTE_SERVICE) String serviceType)
    {
        validatePathParameters(datacenterId, serviceType);

        RemoteServiceType type = RemoteServiceType.valueOf(serviceType.toUpperCase());
        RemoteService remoteService = service.getRemoteService(datacenterId, type);

        service.removeRemoteService(remoteService.getId());
    }

    /*
     * Utility methods TODO: We are duplicating these everywhere, refactor!!
     */

    public static RemoteServiceDto addLinks(IRESTBuilder restBuilder,
        RemoteServiceDto remoteService, Integer datacenterId)
    {
        remoteService.setLinks(restBuilder.buildRemoteServiceLinks(datacenterId, remoteService));
        return remoteService;
    }

    public static RemoteServiceDto createTransferObject(RemoteService remoteService,
        IRESTBuilder restBuilder) throws Exception
    {
        RemoteServiceDto dto = createTransferObject(remoteService);

        dto = addLinks(restBuilder, dto, remoteService.getDatacenter().getId());
        return dto;
    }

    public static RemoteServiceDto createTransferObject(RemoteService remoteService)
    {
        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setId(remoteService.getId());
        dto.setType(remoteService.getType());
        dto.setStatus(remoteService.getStatus());
        dto.setUri(remoteService.getUri());
        return dto;
    }

    private void validatePathParameters(final Integer datacenterId,
        final String remoteServiceMapping) throws NotFoundException
    {
        if (!service.isAssignedTo(datacenterId, remoteServiceMapping))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER);
        }
    }
}
