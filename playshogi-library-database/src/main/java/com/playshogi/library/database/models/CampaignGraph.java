package com.playshogi.library.database.models;

import java.util.List;

public class CampaignGraph {

    private final String campaignId;
    private final String title;
    private final String description;
    private final List<CampaignLessonNode> nodes;

    public CampaignGraph(String campaignId, String title, String description, List<CampaignLessonNode> nodes) {
        this.campaignId = campaignId;
        this.title = title;
        this.description = description;
        this.nodes = nodes;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<CampaignLessonNode> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "CampaignGraph{" +
                "campaignId=" + campaignId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
