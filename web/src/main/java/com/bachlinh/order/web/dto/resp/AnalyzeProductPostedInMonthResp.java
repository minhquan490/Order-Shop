package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeProductPostedInMonthResp {

    @JsonProperty("first_week")
    private DataPoint pointInFirstWeek;

    @JsonProperty("second_week")
    private DataPoint pointInSecondWeek;

    @JsonProperty("third_week")
    private DataPoint pointInThirdWeek;

    @JsonProperty("last_week")
    private DataPoint pointInLastWeek;

    @Getter
    @RequiredArgsConstructor
    public static class DataPoint {

        @JsonProperty("x")
        private final int min;

        @JsonProperty("y")
        private final int max;
    }

    @Getter
    @Setter
    public static class ResultSet {
        private int first;
        private int second;
        private int third;
        private int fourth;
        private int last;
    }
}
