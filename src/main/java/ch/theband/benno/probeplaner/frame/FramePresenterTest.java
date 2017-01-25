package ch.theband.benno.probeplaner.frame;

import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by benno on 20.01.2017.
 */
public class FramePresenterTest {

    @Test
    public void xy() {
        LocalDate today = LocalDate.of(2017, 01, 26);
        System.out.println(today.plusDays(120));
    }

}