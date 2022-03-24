package com.example.tongue.integration.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Artifact {

    private String artifactId;
    private String resource;
    private String resourceKey;
    private String owner;

}
