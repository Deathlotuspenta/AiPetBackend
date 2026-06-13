package com.self.cat.common.enums;

import lombok.Getter;

@Getter
public enum SourceName {

    PET("pet"),
    USER("user"),
    EVENT("event");

    public final String name;

    SourceName(String name) {
        this.name = name;
    }

}
