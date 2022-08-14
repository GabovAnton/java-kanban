package utilityclasses;


import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleDateTimeCell {

    private final LocalDateTime start;
    private final LocalDateTime end;


    public ScheduleDateTimeCell(LocalDateTime initialDate) {
        this.start = constructDateTime(initialDate);
        this.end = constructDateTime(initialDate).plusMinutes(15);

    }


    public static LocalDateTime constructDateTime(LocalDateTime date) {
        date = date.withSecond(0).withNano(0);
        LocalDateTime entry;

        if (date.getMinute() < 15) {
            entry = date.withMinute(0);
        } else if (date.getMinute() < 30) {
            entry = date.withMinute(15);
        } else if (date.getMinute() < 45) {
            entry = date.withMinute(30);
        } else {
            entry = date.withMinute(45);
        }
        return entry.withSecond(0).withNano(0);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (getStart() != null) {
            hash = hash + getStart().hashCode();
        }
        hash = hash * 31;

        if (getEnd() != null) {
            hash = hash + getEnd().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        ScheduleDateTimeCell otherDateTimeCell = (ScheduleDateTimeCell) obj;
        return  Objects.equals(getStart(), otherDateTimeCell.getStart()) &&
                Objects.equals(getEnd(), otherDateTimeCell.getEnd()) &&
                Objects.equals(getClass(), otherDateTimeCell.getClass());
    }


}
