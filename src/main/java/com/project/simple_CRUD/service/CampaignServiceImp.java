package com.project.simple_CRUD.service;

import com.project.simple_CRUD.model.Campaign;
import com.project.simple_CRUD.repository.CampaignRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CampaignServiceImp implements CampaignService{

    private final CampaignRepository campaignRepository;

    public CampaignServiceImp(CampaignRepository campaignRepository)
    {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public Campaign createNewCampaign(Campaign campaign){
        if(campaignRepository.existsByName(campaign.getName())){
            throw new IllegalArgumentException("Kampania o nazwie '" + campaign.getName() + "' już istnieje!");
        }

        checkIfValid(campaign);
        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> listAll() {
        return campaignRepository.findAll();
    }

    @Override
    public Campaign updateCampaign(Campaign campaign)
    {
        Optional<Campaign> existing = campaignRepository.findById(campaign.getId());
        if(existing.isEmpty()){
            throw new IllegalArgumentException("Kampania o ID " + campaign.getId() + " nie istnieje.");
        }


        Optional<Campaign> campaignWithSameName = campaignRepository.findByName(campaign.getName());
        if(campaignWithSameName.isPresent() && !campaignWithSameName.get().getId().equals(campaign.getId())) {
            throw new IllegalArgumentException("Kampania o nazwie '" + campaign.getName() + "' już istnieje!");
        }
        checkIfValid(campaign);

        Campaign toUpdate = existing.get();
        toUpdate.setName(campaign.getName());
        toUpdate.setKeyWords(campaign.getKeyWords());
        toUpdate.setBidAmount(campaign.getBidAmount());
        toUpdate.setCampaignFund(campaign.getCampaignFund());
        toUpdate.setStatus(campaign.getStatus());
        toUpdate.setTown(campaign.getTown());
        toUpdate.setRadius(campaign.getRadius());

        return campaignRepository.save(toUpdate);
    }

    @Override
    public void deleteCampaign(Long id)
    {
        if(!campaignRepository.existsById(id)){
            throw new IllegalArgumentException("Kampania o ID " + id + " nie istnieje.");
        }
        campaignRepository.deleteById(id);
    }

    private void checkIfValid(Campaign campaign) {
        if(campaign.getName() == null || campaign.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Nazwa kampanii nie może być pusta!");
        }
        if(campaign.getKeyWords() == null ||campaign.getKeyWords().isEmpty()){
            throw new IllegalArgumentException("Słowa kluczowe nie mogą być puste!");
        }
        if(campaign.getBidAmount() == null || campaign.getBidAmount().signum() <= 0){
            throw new IllegalArgumentException("Stawka musi być większa od 0!");
        }
        if(campaign.getCampaignFund() == null || campaign.getCampaignFund().signum() <= 0){
            throw new IllegalArgumentException("Fundusz kampanii musi być większy od 0!");
        }
        if(campaign.getTown() == null || campaign.getTown().trim().isEmpty()){
            throw new IllegalArgumentException("Miasto nie może być puste!");
        }
        if(campaign.getRadius() <= 0){
            throw new IllegalArgumentException("Promień musi być większy od 0!");
        }
        if(campaign.getStatus() == null){
            throw new IllegalArgumentException("Status kampanii musi być określony!");
        }
    }

    @Override
    public Campaign findById(Long id){
        return campaignRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Kampania o ID " + id + " nie istnieje."));
    }

    @Override
    public List<Campaign> searchCampaigns(String name, String keywords, String town, Boolean status){
        List<Campaign> allCampaigns = campaignRepository.findAll();

        return allCampaigns.stream().filter(campaign -> {
            if(name != null && !name.trim().isEmpty()){
                        if(!campaign.getName().toLowerCase().contains(name.toLowerCase())){
                            return false;
                        }
                    }
            if(keywords != null && !keywords.trim().isEmpty()){
                if(campaign.getKeyWords() == null || campaign.getKeyWords().isEmpty()){
                            return false;
                        }
                boolean keywordMatch = campaign.getKeyWords().stream().anyMatch(keyword -> keyword.toLowerCase().contains(keywords.toLowerCase()));
                        if(!keywordMatch){
                            return false;
                        }
                    }
            if(town != null && !town.trim().isEmpty()){
                if(!campaign.getTown().equalsIgnoreCase(town)){
                            return false;
                        }
                    }
            if(status != null){
                if(!campaign.getStatus().equals(status)){
                            return false;
                        }
                    }
            return true;
        }).collect(Collectors.toList());
    }
}