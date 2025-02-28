package com.springboot.api.dto.naverClova;

import java.util.List;

public record ParamsDTO(
                String service,
                String domain,
                String lang,
                String completion,
                String callback,
                DiarizationDTO diarization,
                SedDTO sed,
                List<BoostingDTO> boostings,
                String forbiddens,
                boolean wordAlignment,
                boolean fullText,
                boolean noiseFiltering,
                boolean resultToObs,
                int priority,
                UserDataDTO userdata) {
}
