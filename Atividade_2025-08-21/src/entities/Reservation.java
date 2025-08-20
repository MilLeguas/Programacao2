package entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reservation {

    private Integer roomNumber;
    private Date checkin;
    private Date checkout;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public Reservation(Integer roomNumber, Date checkin, Date checkout) {
        if (!checkout.after(checkin)) {
            throw new IllegalArgumentException("Error in reservation: Check-out date must be after check-in date");
        }
        this.roomNumber = roomNumber;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public Date getCheckin() {
        return checkin;
    }

    public Date getCheckout() {
        return checkout;
    }

    public long duration() {
        long diff = checkout.getTime() - checkin.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void updateDates(Date checkin, Date checkout) {
        Date now = new Date();

        if (checkin.before(now) || checkout.before(now)) {
            throw new IllegalArgumentException("Error in reservation: Reservation dates for update must be future dates");
        }

        if (!checkout.after(checkin)) {
            throw new IllegalArgumentException("Error in reservation: Check-out date must be after check-in date");
        }

        this.checkin = checkin;
        this.checkout = checkout;
    }

    @Override
    public String toString() {
        return "Reservation: Room "
                + roomNumber
                + ", check-in: "
                + sdf.format(checkin)
                + ", check-out: "
                + sdf.format(checkout)
                + ", "
                + duration()
                + " nights";
    }
}