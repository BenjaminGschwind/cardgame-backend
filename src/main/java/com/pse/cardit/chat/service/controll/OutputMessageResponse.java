package com.pse.cardit.chat.service.controll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessageResponse {

    private String username;
    private String content;
    private String timestamp;

}
