package boilerplate.desktop.services;

import boilerplate.desktop.models.Donation;
import boilerplate.desktop.models.dto.DonationDto;
import boilerplate.desktop.models.dto.DonationUpdateDto;
import boilerplate.desktop.repositories.ApiClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DonationService {
    
    private final ApiClient apiClient;
    
    public DonationService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CompletableFuture<List<Donation>> getAllDonations() {
        return apiClient.getList("/donations?limit=100", Donation.class);
    }
    
    public CompletableFuture<Donation> getDonationById(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid donation ID"));
        }
        return apiClient.getOne("/donations/" + id, Donation.class);
    }
    
    public CompletableFuture<Donation> createDonation(DonationDto donationDto) {
        if (donationDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Donation data required"));
        }
        return apiClient.post("/donations", donationDto, Donation.class);
    }
    
    public CompletableFuture<Donation> updateDonation(Long id, DonationUpdateDto donationDto) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid donation ID"));
        }
        if (donationDto == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Donation data required"));
        }
        return apiClient.put("/donations/" + id, donationDto, Donation.class);
    }
    
    public CompletableFuture<Void> deleteDonation(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid donation ID"));
        }
        return apiClient.delete("/donations/" + id);
    }
    
    /**
     * Obtiene el monto total de donaciones recibidas.
     * Solo aplica para donaciones monetarias.
     */
    public CompletableFuture<Double> getTotalDonations() {
        return apiClient.getTotalDonations();
    }
    
    /**
     * Obtiene el monto total de donaciones con filtro opcional por tipo.
     */
    public CompletableFuture<Double> getTotalDonations(String donationType) {
        if (donationType != null && !donationType.isBlank()) {
            return apiClient.getOne("/donations/total?donation_type=" + donationType, Double.class)
                    .thenApply(total -> total != null ? total : 0.0);
        }
        return getTotalDonations();
    }
    
    /**
     * Obtiene el conteo total de donaciones con filtro opcional por tipo.
     */
    public CompletableFuture<Long> countDonations(String donationType) {
        if (donationType != null && !donationType.isBlank()) {
            return apiClient.getCount("/donations/count?donation_type=" + donationType);
        }
        return apiClient.getCount("/donations/count");
    }
    
    public CompletableFuture<Long> countDonations() {
        return countDonations(null);
    }
}

