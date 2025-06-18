package de.tum.gh_connector.dto;

import lombok.Data;

import java.util.List;

@Data
public class Tree {
    private String sha;
    private List<TreeItem> tree;
}
