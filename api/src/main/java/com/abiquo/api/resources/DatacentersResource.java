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

import static com.abiquo.api.resources.DatacenterResource.addLinks;
import static com.abiquo.api.resources.DatacenterResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;

@Path(DatacentersResource.DATACENTERS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Datacenters")
public class DatacentersResource extends AbstractResource
{
    public static final String DATACENTERS_PATH = "admin/datacenters";

    // TODO get allowed datacenters on DatacentersResourcePremium

    @Autowired
    private DatacenterService service;

    @GET
    public DatacentersDto getDatacenters(@Context IRESTBuilder restBuilder) throws Exception
    {
        Collection<Datacenter> all = service.getDatacenters();
        DatacentersDto datacenters = new DatacentersDto();
        for (Datacenter d : all)
        {
            datacenters.add(createTransferObject(d, restBuilder));
        }

        return datacenters;
    }

    @POST
    public DatacenterDto postDatacenter(DatacenterDto datacenter, @Context IRESTBuilder builder)
        throws Exception
    {
        DatacenterDto response = service.addDatacenter(datacenter);
        addLinks(builder, response);

        return response;
    }
}
