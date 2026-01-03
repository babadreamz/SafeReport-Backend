package africa.semicolon.safereportbackend.controllers;

import africa.semicolon.safereportbackend.dtos.modeldtos.GhostReporterDto;
import africa.semicolon.safereportbackend.dtos.requests.RecoveryRequest;
import africa.semicolon.safereportbackend.dtos.responses.GhostReporterResponse;
import africa.semicolon.safereportbackend.services.GhostIdentityServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity")
@AllArgsConstructor
public class GhostIdentityController {
    private final GhostIdentityServices ghostIdentityServices;

    @PostMapping("/create")
    public ResponseEntity<GhostReporterResponse> createGhostIdentity(
            @RequestHeader(value = "X-Device-Signature") String deviceSignature
    ){
        GhostReporterResponse response = ghostIdentityServices.createIdentity(deviceSignature);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/recover")
    public ResponseEntity<GhostReporterDto> recoverAccount(
            @RequestBody RecoveryRequest recoveryRequest,
            @RequestHeader(value = "X-Device-Signature") String newDeviceSignature){
        GhostReporterDto ghostReporterDto = ghostIdentityServices.recoverAccount(recoveryRequest, newDeviceSignature);
        return ResponseEntity.ok(ghostReporterDto);
    }
}
