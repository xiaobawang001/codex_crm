package com.example.crm.modules.opportunity.application;

import java.math.BigDecimal;

public class OpportunityQuery {

    private long current;
    private long size;
    private String keyword;
    private Long customerId;
    private String stage;
    private Long ownerId;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private String expectedCloseStart;
    private String expectedCloseEnd;

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(BigDecimal amountMin) {
        this.amountMin = amountMin;
    }

    public BigDecimal getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(BigDecimal amountMax) {
        this.amountMax = amountMax;
    }

    public String getExpectedCloseStart() {
        return expectedCloseStart;
    }

    public void setExpectedCloseStart(String expectedCloseStart) {
        this.expectedCloseStart = expectedCloseStart;
    }

    public String getExpectedCloseEnd() {
        return expectedCloseEnd;
    }

    public void setExpectedCloseEnd(String expectedCloseEnd) {
        this.expectedCloseEnd = expectedCloseEnd;
    }
}
