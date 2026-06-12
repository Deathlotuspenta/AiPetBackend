package com.self.cat.model.event.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEventDto {
    private String eventContent;
    private String eventName;
    private Boolean isCompleted;
    private Integer petId;
    private String petName;
    private String eventTime;
}
