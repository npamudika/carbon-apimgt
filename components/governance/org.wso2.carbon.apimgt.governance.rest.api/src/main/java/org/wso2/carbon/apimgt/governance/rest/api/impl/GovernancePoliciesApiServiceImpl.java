
/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.governance.rest.api.impl;

import org.wso2.carbon.apimgt.governance.api.GovernanceAPIConstants;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceExceptionCodes;
import org.wso2.carbon.apimgt.governance.api.manager.PolicyManager;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicy;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicyList;
import org.wso2.carbon.apimgt.governance.impl.PolicyManagerImpl;
import org.wso2.carbon.apimgt.governance.rest.api.GovernancePoliciesApiService;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.governance.rest.api.dto.GovernancePolicyDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.GovernancePolicyListDTO;
import org.wso2.carbon.apimgt.governance.rest.api.mappings.PolicyMappingUtil;
import org.wso2.carbon.apimgt.governance.rest.api.util.GovernanceAPIUtil;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

/**
 * This is the implementation class for the Governance Policies API.
 */
public class GovernancePoliciesApiServiceImpl implements GovernancePoliciesApiService {

    /**
     * Create a new Governance Policy
     *
     * @param governancePolicyDTO Governance Policy  with Ruleset Ids
     * @param messageContext      Message Context
     * @return Response
     * @throws GovernanceException If an error occurs while creating the policy
     */
    public Response createGovernancePolicy(GovernancePolicyDTO governancePolicyDTO,
                                           MessageContext messageContext) throws GovernanceException {

        GovernancePolicyDTO createdPolicyDTO;
        URI createdPolicyURI;

        try {
            PolicyManager policyManager = new PolicyManagerImpl();
            GovernancePolicy governancePolicy =
                    PolicyMappingUtil.fromDTOtoGovernancePolicy(governancePolicyDTO);

            String username = GovernanceAPIUtil.getLoggedInUsername();
            String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

            governancePolicy.setCreatedBy(username);
            governancePolicy = policyManager.createGovernancePolicy(organization,
                    governancePolicy);

            // TODO: Assess compliance for APIs
            createdPolicyDTO = PolicyMappingUtil.
                    fromGovernancePolicyToGovernancePolicyDTO(governancePolicy);
            createdPolicyURI = new URI(
                    GovernanceAPIConstants.POLICY_PATH + "/" + createdPolicyDTO.getId());

        } catch (URISyntaxException e) {
            String error = String.format("Error while creating URI for new Governance Policy %s",
                    governancePolicyDTO.getName());
            throw new GovernanceException(error, e, GovernanceExceptionCodes.INTERNAL_SERVER_ERROR);
        }
        return Response.created(createdPolicyURI).entity(createdPolicyDTO).build();
    }

    /**
     * Get Governance Policy by ID
     *
     * @param policyId       Policy ID
     * @param messageContext Message Context
     * @return Response
     * @throws GovernanceException If an error occurs while retrieving the policy
     */
    public Response getGovernancePolicyById(String policyId, MessageContext messageContext) throws GovernanceException {
        PolicyManager policyManager = new PolicyManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

        GovernancePolicy policy = policyManager.getGovernancePolicyByID(organization, policyId);
        GovernancePolicyDTO policyDTO = PolicyMappingUtil.fromGovernancePolicyToGovernancePolicyDTO(policy);
        return Response.status(Response.Status.OK).entity(policyDTO).build();
    }

    /**
     * Get Governance Policies
     *
     * @param messageContext Message Context
     * @return Response
     * @throws GovernanceException If an error occurs while retrieving the policies
     */
    public Response getGovernancePolicies(MessageContext messageContext) throws GovernanceException {
        PolicyManager policyManager = new PolicyManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);
        GovernancePolicyList policyList = policyManager.getGovernancePolicies(organization);

        GovernancePolicyListDTO policyListDTO = PolicyMappingUtil.
                fromGovernancePolicyListToGovernancePolicyListDTO(policyList);

        return Response.status(Response.Status.OK).entity(policyListDTO).build();
    }

    /**
     * Delete a Governance Policy
     *
     * @param policyId       Policy ID
     * @param messageContext Message Context
     * @return Response
     * @throws GovernanceException If an error occurs while deleting the policy
     */
    public Response deleteGovernancePolicy(String policyId, MessageContext messageContext) throws GovernanceException {
        PolicyManager policyManager = new PolicyManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);
        policyManager.deletePolicy(policyId, organization);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Update a Governance Policy
     *
     * @param policyId            Policy ID
     * @param governancePolicyDTO Governance Policy  with Ruleset Ids
     * @param messageContext      Message Context
     * @return Response
     * @throws GovernanceException If an error occurs while updating the policy
     */
    public Response updateGovernancePolicyById(String policyId, GovernancePolicyDTO
            governancePolicyDTO, MessageContext messageContext) throws GovernanceException {
        PolicyManager policyManager = new PolicyManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);
        String username = GovernanceAPIUtil.getLoggedInUsername();

        GovernancePolicy governancePolicy =
                PolicyMappingUtil.
                        fromDTOtoGovernancePolicy(governancePolicyDTO);

        governancePolicy.setUpdatedBy(username);
        GovernancePolicy updatedPolicy = policyManager.updateGovernancePolicy
                (policyId, organization, governancePolicy);

        GovernancePolicyDTO updatedPolicyDTO = PolicyMappingUtil.
                fromGovernancePolicyToGovernancePolicyDTO(updatedPolicy);

        //TODO: Access Compliance of existing components

        return Response.status(Response.Status.OK).entity(updatedPolicyDTO).build();
    }
}
