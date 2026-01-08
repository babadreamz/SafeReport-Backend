package africa.semicolon.safereportbackend.controllers;

import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.modeldtos.ResponderUnitDto;
import africa.semicolon.safereportbackend.dtos.requests.AgencyRequest;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.requests.ResponderRequest;
import africa.semicolon.safereportbackend.dtos.responses.AgencyLoginResponse;
import africa.semicolon.safereportbackend.dtos.responses.AgencyResponse;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;
import africa.semicolon.safereportbackend.dtos.responses.ResponderResponse;
import africa.semicolon.safereportbackend.services.AgencyServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agency")
@RequiredArgsConstructor
public class AgencyController {
    @Autowired
    private final AgencyServices agencyServices;

    @PostMapping("/new")
    public ResponseEntity<AgencyResponse> createAgency(@RequestBody AgencyRequest request){
        AgencyResponse agencyResponse = agencyServices.createAgency(request);
        return new ResponseEntity<>(agencyResponse, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AgencyLoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(agencyServices.login(request));
    }
    @DeleteMapping("/logout")
    public String logout(@RequestHeader("Authorization")String authHeader){
        return agencyServices.logout(authHeader);
    }
    @PostMapping("/unit/create")
    public ResponseEntity<ResponderResponse> createUnit(@RequestParam String agencyId,
                                                        @RequestBody ResponderRequest request){
        ResponderResponse response = agencyServices.createResponderUnit(agencyId,request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/unassigned/{agencyId}")
    public List<ReportSummary> getUnassignedReports(@PathVariable String agencyId){
        return agencyServices.getUnassignedReports(agencyId);
    }
    @PutMapping("/reassign")
    public ResponseEntity<ReportResponse> manuallyAssignReport(@RequestParam String reportId, @RequestParam String responderId){
        return ResponseEntity.ok(agencyServices.manuallyAssignReport(reportId,responderId));
    }
    @PutMapping("/transfer/{reportId}/{targetAgencyId}")
    public void transferReportToAnotherAgency(@PathVariable String reportId,@PathVariable String targetAgencyId){
        agencyServices.transferReportToAnotherAgency(reportId,targetAgencyId);
    }
    @GetMapping("unit/all")
    public List<ResponderUnitDto> getAllResponderUnits(@RequestParam String agencyId){
        return agencyServices.getAllResponderUnits(agencyId);
    }
    @PatchMapping("unit/toggle")
    public void toggleResponderUnitStatus(@RequestParam String agencyId,
                                          @RequestParam String responderUnitId,
                                          @RequestParam boolean isActive){
        agencyServices.toggleResponderUnitStatus(agencyId, responderUnitId, isActive);
    }
}
