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

package com.abiquo.api.services.cloud;

import java.util.Collection;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.PrivateNetworkService;
import com.abiquo.api.services.UserService;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.enterprise.DatacenterLimitsDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

@Service
@Transactional(readOnly = true)
public class VirtualDatacenterService extends DefaultApiService
{

    public static final String FENCE_MODE = "bridge";

    // Used services
    @Autowired
    UserService userService;
    
    @Autowired
    PrivateNetworkService networkService;

    // New repos and DAOs
    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    DatacenterRep datacenterRepo;
    
    @Autowired
    DatacenterLimitsDAO datacenterLimitsDao;
    


    public VirtualDatacenterService()
    {

    }

    // use this to initialize it for tests
    public VirtualDatacenterService(EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new DatacenterRep(em);
        datacenterLimitsDao = new DatacenterLimitsDAO(em);
        userService = new UserService(em);
        datacenterLimitsDao = new DatacenterLimitsDAO(em);
        networkService = new PrivateNetworkService(em);
    }

    public Collection<VirtualDatacenter> getVirtualDatacenters(Enterprise enterprise,
        Datacenter datacenter)
    {
        User user = userService.getCurrentUser();
        return getVirtualDatacenters(enterprise, datacenter, user);
    }

    Collection<VirtualDatacenter> getVirtualDatacenters(Enterprise enterprise,
        Datacenter datacenter, User user)
    {
        boolean findByUser =
            user != null
                && (user.getRole().getType() == Role.Type.USER && !StringUtils.isEmpty(user
                    .getAvailableVirtualDatacenters()));

        if (enterprise == null && user != null)
        {
            enterprise = user.getEnterprise();
        }

        if (findByUser)
        {
            return repo.findByEnterpriseAndDatacenter(enterprise, datacenter, user);
        }
        else
        {
            return repo.findByEnterpriseAndDatacenter(enterprise, datacenter);
        }
    }

    public VirtualDatacenter getVirtualDatacenter(Integer id)
    {
        VirtualDatacenter vdc = repo.findById(id);
        if (vdc == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        }
        return vdc;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualDatacenter createVirtualDatacenter(VirtualDatacenterDto dto,
        Datacenter datacenter, Enterprise enterprise)
    {
        if (!isValidEnterpriseDatacenter(enterprise, datacenter))
        {
            errors.add(APIError.DATACENTER_NOT_ALLOWD);
            flushErrors();
        }

        Network network = createNetwork();
        VirtualDatacenter vdc = createVirtualDatacenter(dto, datacenter, enterprise, network);
        
        //set as default vlan (as it is the first one) and create it.
        dto.getVlan().setDefaultNetwork(Boolean.TRUE);
        networkService.createPrivateNetwork(vdc.getId(), dto.getVlan());
        
        assignVirtualDatacenterToUser(vdc);

        return vdc;
    }

    private void assignVirtualDatacenterToUser(VirtualDatacenter vdc)
    {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole().getType() == Role.Type.USER
            && currentUser.getAvailableVirtualDatacenters() != null)
        {
            String availableVirtualDatacenters =
                currentUser.getAvailableVirtualDatacenters() + "," + vdc.getId();
            currentUser.setAvailableVirtualDatacenters(availableVirtualDatacenters);

            userService.updateUser(currentUser);
        }
    }

    protected boolean isValidEnterpriseDatacenter(Enterprise enterprise, Datacenter datacenter)
    {
        return true;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualDatacenter updateVirtualDatacenter(Integer id, VirtualDatacenterDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(id);

        return updateVirtualDatacenter(vdc, dto);
    }

    protected VirtualDatacenter updateVirtualDatacenter(VirtualDatacenter vdc,
        VirtualDatacenterDto dto)
    {
        vdc.setName(dto.getName());
        setLimits(dto, vdc);

        if (!vdc.isValid())
        {
            validationErrors.addAll(vdc.getValidationErrors());
            flushErrors();
        }
        if (!isValidVlanHardLimitPerVdc(vdc.getVlanHard()))
        {
            errors.add(APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC);
            flushErrors();
        }

        repo.update(vdc);

        return vdc;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualDatacenter(Integer id)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(id);

        if (repo.containsVirtualAppliances(vdc))
        {
            errors.add(APIError.VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES);
        }

        if (repo.containsResources(vdc, "8")) // id 8 -> Volumes. FIXME: add enumeration
        {
            errors.add(APIError.VIRTUAL_DATACENTER_CONTAINS_RESOURCES);
        }

        flushErrors();

        repo.delete(vdc);
    }

    private Network createNetwork()
    {
        Network network = new Network(UUID.randomUUID().toString());
        repo.insertNetwork(network);
        return network;
    }

    private VirtualDatacenter createVirtualDatacenter(VirtualDatacenterDto dto,
        Datacenter datacenter, Enterprise enterprise, Network network)
    {
        VirtualDatacenter vdc =
            new VirtualDatacenter(enterprise,
                datacenter,
                network,
                dto.getHypervisorType(),
                dto.getName());

        setLimits(dto, vdc);
        validateVirtualDatacenter(vdc, dto.getVlan(), datacenter);

        repo.insert(vdc);
        return vdc;
    }

    private void setLimits(VirtualDatacenterDto dto, VirtualDatacenter vdc)
    {
        vdc.setCpuCountLimits(new Limit((long) dto.getCpuCountSoftLimit(), (long) dto
            .getCpuCountHardLimit()));
        vdc.setHdLimitsInMb(new Limit(dto.getHdSoftLimitInMb(), dto.getHdHardLimitInMb()));
        vdc.setRamLimitsInMb(new Limit((long) dto.getRamSoftLimitInMb(), (long) dto
            .getRamHardLimitInMb()));
        vdc.setStorageLimits(new Limit(dto.getStorageSoft(), dto.getStorageHard()));
        vdc.setVlansLimits(new Limit(dto.getVlansSoft(), dto.getVlansHard()));
        vdc.setPublicIPLimits(new Limit(dto.getPublicIpsSoft(), dto.getPublicIpsHard()));
    }

    private void validateVirtualDatacenter(VirtualDatacenter vdc, VLANNetworkDto vlan,
        Datacenter datacenter)
    {
        if (vlan == null)
        {
            errors.add(APIError.NETWORK_INVALID_CONFIGURATION);
        }

        if (!vdc.isValid())
        {
            validationErrors.addAll(vdc.getValidationErrors());
        }
        else if (!isValidVlanHardLimitPerVdc(vdc.getVlanHard()))
        {
            errors.add(APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC);
        }

        if (vdc.getHypervisorType() != null
            && !isValidHypervisorForDatacenter(vdc.getHypervisorType(), datacenter))
        {
            errors.add(APIError.VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE);
        }

        flushErrors();
    }

    private boolean isValidVlanHardLimitPerVdc(long vlansHard)
    {
        String limitS = System.getProperty("abiquo.server.networking.vlanPerVdc", "0");
        int limit = Integer.valueOf(limitS);

        return limit == 0 || limit >= vlansHard;
    }

    private boolean isValidHypervisorForDatacenter(HypervisorType type, Datacenter datacenter)
    {
        return datacenterRepo.findHypervisors(datacenter).contains(type);
    }

}
