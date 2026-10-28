package com.tradeflux.user_service.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileSummaryResponse {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;
}