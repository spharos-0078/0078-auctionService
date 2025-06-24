package com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoneyHistoryType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    SELL("판매"),
    REFUND("환불"),
    PIECE_BUY("조각 매수"),
    PIECE_SELL("조각 매도"),
    FUNDING("공모"),
    FEE("수수료"),
    PROFIT("배당금"),
    PRODUCT_BUY("상품 구매"),
    FREEZE("보증금");

    private final String label;

}
