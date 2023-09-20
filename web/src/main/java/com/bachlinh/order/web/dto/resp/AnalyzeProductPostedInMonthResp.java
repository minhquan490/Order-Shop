package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnalyzeProductPostedInMonthResp {

    @JsonProperty("first_week")
    private DataPoint pointInFirstWeek;

    @JsonProperty("second_week")
    private DataPoint pointInSecondWeek;

    @JsonProperty("third_week")
    private DataPoint pointInThirdWeek;

    @JsonProperty("last_week")
    private DataPoint pointInLastWeek;

    public DataPoint getPointInFirstWeek() {
        return this.pointInFirstWeek;
    }

    public DataPoint getPointInSecondWeek() {
        return this.pointInSecondWeek;
    }

    public DataPoint getPointInThirdWeek() {
        return this.pointInThirdWeek;
    }

    public DataPoint getPointInLastWeek() {
        return this.pointInLastWeek;
    }

    @JsonProperty("first_week")
    public void setPointInFirstWeek(DataPoint pointInFirstWeek) {
        this.pointInFirstWeek = pointInFirstWeek;
    }

    @JsonProperty("second_week")
    public void setPointInSecondWeek(DataPoint pointInSecondWeek) {
        this.pointInSecondWeek = pointInSecondWeek;
    }

    @JsonProperty("third_week")
    public void setPointInThirdWeek(DataPoint pointInThirdWeek) {
        this.pointInThirdWeek = pointInThirdWeek;
    }

    @JsonProperty("last_week")
    public void setPointInLastWeek(DataPoint pointInLastWeek) {
        this.pointInLastWeek = pointInLastWeek;
    }

    public static class DataPoint {

        @JsonProperty("x")
        private final int min;

        @JsonProperty("y")
        private final int max;

        public DataPoint(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }
    }

    public static class ResultSet {
        private int first;
        private int second;
        private int third;
        private int fourth;
        private int last;

        public int getFirst() {
            return this.first;
        }

        public int getSecond() {
            return this.second;
        }

        public int getThird() {
            return this.third;
        }

        public int getFourth() {
            return this.fourth;
        }

        public int getLast() {
            return this.last;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public void setThird(int third) {
            this.third = third;
        }

        public void setFourth(int fourth) {
            this.fourth = fourth;
        }

        public void setLast(int last) {
            this.last = last;
        }
    }
}
