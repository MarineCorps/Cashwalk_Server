package com.example.cashwalk.entity;

/**
 * 포인트 적립/사용의 유형을 정의한 Enum
 */
public enum PointsType {
    STEP_REWARD,   // 걸음 수 적립
    STORE_USE,     // 스토어 사용 차감
    AD_REWARD,      // 광고 시청 보상
    INVITE_REWARD,
    MANUAL, //관리자 수동 지급/차감
    RESET,//관리자 초기화용
    ATTENDANCE,
    GIFT_REWARD,
    NWALK,
    LUCKY_CASH
}
