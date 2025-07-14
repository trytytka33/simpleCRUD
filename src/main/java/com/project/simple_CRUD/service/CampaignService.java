package com.project.simple_CRUD.service;

import com.project.simple_CRUD.model.Campaign;
import java.util.List;

public interface CampaignService {
    Campaign createNewCampaign(Campaign campaign);
    List<Campaign> listAll();
    Campaign updateCampaign(Campaign campaign);
    void deleteCampaign(Long id);
    Campaign findById(Long id);
    List<Campaign> searchCampaigns(String name, String keywords, String town, Boolean status);
}