package com.hqh.quizserver.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@NoArgsConstructor
public class ApiResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date timeStamp;
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String type;
    private String reason;
    private String message;

    public ApiResponse(int httpStatusCode,
                       HttpStatus httpStatus,
                       String type,
                       String reason,
                       String message) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.type = type;
        this.reason = reason;
        this.message = message;
    }
}
