package com.tradeflux.portfolio_service.dtos;


import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioPageResponse {

    private List<HoldingResponse> holdings;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
    private Boolean last;
}