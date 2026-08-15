package com.example.crm.modules.opportunity.domain.model;

import com.example.crm.shared.exception.BizException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商机阶段值对象：contact → requirement → proposal → negotiation → win/lose（终态）。
 */
public enum OpportunityStage {

    CONTACT("contact"),
    REQUIREMENT("requirement"),
    PROPOSAL("proposal"),
    NEGOTIATION("negotiation"),
    WIN("win"),
    LOSE("lose");

    private static final Set<String> CODES = Arrays.stream(values())
            .map(OpportunityStage::code)
            .collect(Collectors.toSet());

    private final String code;

    OpportunityStage(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public boolean isTerminal() {
        return this == WIN || this == LOSE;
    }

    public static OpportunityStage fromCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BizException("无效的商机阶段: " + code));
    }

    public static boolean isValid(String code) {
        return code != null && CODES.contains(code);
    }
}
