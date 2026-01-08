package africa.semicolon.safereportbackend.utils.mappers;

import africa.semicolon.safereportbackend.data.models.Agency;
import africa.semicolon.safereportbackend.data.models.ResponderUnit;
import africa.semicolon.safereportbackend.data.models.Role;
import africa.semicolon.safereportbackend.dtos.requests.AgencyRequest;
import africa.semicolon.safereportbackend.dtos.requests.ResponderRequest;
import africa.semicolon.safereportbackend.dtos.responses.AgencyResponse;
import africa.semicolon.safereportbackend.dtos.responses.ResponderResponse;

import java.util.List;

public class AgencyModelMapper {
    public static Agency mapToAgency(AgencyRequest agencyRequest) {
        Agency agency = new Agency();
        agency.setName(agencyRequest.getName());
        agency.setUsername(agencyRequest.getUsername());
        agency.setPassword(agencyRequest.getPassword());
        agency.setDeleted(false);
        agency.setEmail(agencyRequest.getEmail());
        agency.setRoles(List.of(Role.AGENCY));
        return agency;
    }
    public static AgencyResponse mapToAgencyResponse(Agency agency) {
        AgencyResponse agencyResponse = new AgencyResponse();
        agencyResponse.setName(agency.getName());
        agencyResponse.setUsername(agency.getUsername());
        agencyResponse.setId(agency.getId());
        agencyResponse.setEmail(agency.getEmail());
        return agencyResponse;
    }
    public static ResponderUnit mapToResponderUnit(Agency agency, ResponderRequest request) {
        ResponderUnit responderUnit = new ResponderUnit();
        responderUnit.setName(request.getName());
        responderUnit.setUsername(request.getUsername());
        responderUnit.setPassword(request.getPassword());
        responderUnit.setBaseLatitude(request.getBaseLatitude());
        responderUnit.setBaseLongitude(request.getBaseLongitude());
        responderUnit.setContactNumber(request.getContactNumber());
        responderUnit.setDeleted(false);
        List<Role> roles = List.of(Role.RESPONDER);
        responderUnit.setRoles(roles);
        responderUnit.setActive(true);
        responderUnit.setAgency(agency);
        return responderUnit;
    }
    public static ResponderResponse mapToResponderResponse(ResponderUnit responderUnit) {
        ResponderResponse responderResponse = new ResponderResponse();
        responderResponse.setId(responderUnit.getId());
        responderResponse.setName(responderUnit.getName());
        responderResponse.setAgencyName(responderUnit.getAgency().getName());
        responderResponse.setUsername(responderUnit.getUsername());
        responderResponse.setContactNumber(responderUnit.getContactNumber());
        responderResponse.setBaseLatitude(responderUnit.getBaseLatitude());
        responderResponse.setBaseLongitude(responderUnit.getBaseLongitude());
        responderResponse.setActive(responderUnit.isActive());
        return responderResponse;
    }
}
