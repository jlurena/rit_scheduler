package me.jlurena.ritscheduler.models;

/**
 * models.ReservedSeat.java
 * Represents a Reserved Seat.
 */

public class ReservedSeat {

    public class ReservedSeatDetails {
        public static final String TYPE = "reservedSeatDetails";
        private String description;
        private String total;
        private String id;
        private int intCap;
        private String cap;
        private String startDate;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIntCap() {
            return intCap;
        }

        public void setIntCap(int intCap) {
            this.intCap = intCap;
        }

        public String getCap() {
            return cap;
        }

        public void setCap(String cap) {
            this.cap = cap;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }
    }

    public static final String TYPE = "reservedSeat";
    private String description;
    private String descr;
    private String major;
    private ReservedSeatDetails[] infoArray;
    private String toolTip;
    private String end;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public ReservedSeatDetails[] getInfoArray() {
        return infoArray;
    }

    public void setInfoArray(ReservedSeatDetails[] infoArray) {
        this.infoArray = infoArray;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
