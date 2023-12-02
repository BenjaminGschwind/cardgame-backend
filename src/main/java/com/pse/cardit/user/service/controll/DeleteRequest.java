package com.pse.cardit.user.service.controll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class did not exist but was used? Maybe it should be part of the security package.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRequest {
    private String authToken;
}
