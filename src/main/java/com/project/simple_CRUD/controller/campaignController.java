package com.project.simple_CRUD.controller;

import com.project.simple_CRUD.model.Campaign;
import com.project.simple_CRUD.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*")
public class campaignController {

    private final CampaignService campaignService;

    public campaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public ResponseEntity<?> createCampaign(@RequestBody Campaign campaign) {
        try {
            Campaign savedCampaign = campaignService.createNewCampaign(campaign);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCampaign);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Blad walidacji", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad serwera", "Nie udało się utworzyć kampanii"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCampaigns() {
        try {
            List<Campaign> campaigns = campaignService.listAll();
            return ResponseEntity.ok(campaigns);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad serwera", "Nie udało się pobrać kampanii"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCampaignById(@PathVariable Long id) {
        try {
            Campaign campaign = campaignService.findById(id);
            return ResponseEntity.ok(campaign);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Nie znaleziono", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad serwera", "Nie udało się pobrać kampanii"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id, @RequestBody Campaign campaign) {
        try {
            campaign.setId(id);
            Campaign updated = campaignService.updateCampaign(campaign);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Blad aktualizacji", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad serwera", "Nie udało się zaktualizować kampanii"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
        try {
            campaignService.deleteCampaign(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Nie można usunąć", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad serwera", "Nie udało się usunąć kampanii"));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        try {
            Campaign campaign = campaignService.findById(id);
            campaign.setStatus(!campaign.getStatus());
            Campaign updated = campaignService.updateCampaign(campaign);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Blad zmiany statusu", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blda serwera", "Nie udało się zmienić statusu"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCampaigns(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String town,
            @RequestParam(required = false) Boolean status) {
        try {
            List<Campaign> results = campaignService.searchCampaigns(name, keywords, town, status);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad wyszukiwania", "Nie udało się wyszukać kampanii"));
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Blad walidacji", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Blad serwera", "Wystapil nieoczekiwany Blad"));
    }

    public static class ErrorResponse {
        private String error;
        private String message;
        private long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}