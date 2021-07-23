package com.example.tongue.discounts;

import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

public class DateTests {

    public DiscountRepository repository;

    public DateTests(DiscountRepository repository){
        this.repository=repository;
    }

    /*
    public void testDate1(){
        String date = "2020-02-13T18:55:05+03:00";
        TemporalAccessor ta = DateTimeFormatter.ISO_DATE_TIME.parse(date);
        Instant i = Instant.from(ta);
        System.out.println(i);
        LocalDateTime dateTime = LocalDateTime.from(ta);
        System.out.println(dateTime.toString());
    }

    public void DiscountDatesTest(){
        Discount discount1 = new Discount();
        Discount discount2 = new Discount();
        Discount discount3 = new Discount();
        Discount discount4 = new Discount();
        Discount discount5 = new Discount();
        Discount discount6 = new Discount();
        discount1.setId(1L);
        discount2.setId(2L);
        discount3.setId(3L);
        discount4.setId(4L);
        discount5.setId(5L);
        discount6.setId(6L);
        String date1 = "2020-02-13T18:56:25+00:00";
        String date2 = "2020-02-13T18:56:07+00:00";
        String date3 = "2020-02-13T18:57:05+00:00";
        String date4 = "2020-02-13T18:58:01+00:00";
        String date5 = "2020-02-13T18:59:02+00:00";
        String date6 = "2021-07-12T20:55:03-00:00";
        TemporalAccessor ta1 = DateTimeFormatter.ISO_DATE_TIME.parse(date1);

     */
    /*
        TemporalAccessor ta2 = DateTimeFormatter.ISO_DATE_TIME.parse(date2);
        TemporalAccessor ta3 = DateTimeFormatter.ISO_DATE_TIME.parse(date3);
        TemporalAccessor ta4 = DateTimeFormatter.ISO_DATE_TIME.parse(date4);
        TemporalAccessor ta5 = DateTimeFormatter.ISO_DATE_TIME.parse(date5);
        TemporalAccessor ta6 = DateTimeFormatter.ISO_DATE_TIME.parse(date6);
        Instant dateTest1 = Instant.from(Instant.from(ta1));
        Instant dateTest2 = Instant.from(Instant.from(ta2));
        Instant dateTest3 = Instant.from(Instant.from(ta3));
        Instant dateTest4 = Instant.from(Instant.from(ta4));
        Instant dateTest5 = Instant.from(Instant.from(ta5));
        Instant dateTest6 = Instant.from(Instant.from(ta6));
        discount1.setTimeTest(dateTest1);
        discount2.setTimeTest(dateTest2);
        discount3.setTimeTest(dateTest3);
        discount4.setTimeTest(dateTest4);
        discount5.setTimeTest(dateTest5);
        discount6.setTimeTest(dateTest6);
        repository.save(discount1);
        repository.save(discount2);
        repository.save(discount3);
        repository.save(discount4);
        repository.save(discount5);
        repository.save(discount6);

        //List<Discount> discounts = repository.findAllByTimeTestBefore(dateTest4);
        List<Discount> discounts = repository.findAllByTimeTestBetween(dateTest2,dateTest5);
        for (Discount discount:
             discounts) {
            System.out.println(discount.getTimeTest());
        }
/*
        Instant datex = Instant.now();
        System.out.println("Objective date: "+dateTest6.toString());
        System.out.println("Current date: "+datex.toString());
        System.out.println("Comparision objective > current"+dateTest6.compareTo(datex));




    }
/*
    public void DiscountInstantsest(){
        Discount discount1 = new Discount();
        Discount discount2 = new Discount();
        Discount discount3 = new Discount();
        Discount discount4 = new Discount();
        Discount discount5 = new Discount();
        Discount discount6 = new Discount();
        discount1.setId(1L);
        discount2.setId(2L);
        discount3.setId(3L);
        discount4.setId(4L);
        discount5.setId(5L);
        discount6.setId(6L);
        String date1 = "2020-02-13T18:55:05+03:00";
        String date2 = "2020-02-13T18:56:07+04:00";
        String date3 = "2020-02-13T18:57:05+01:00";
        String date4 = "2020-02-13T18:58:01+03:00";
        String date5 = "2020-02-13T18:59:02+03:00";
        String date6 = "2021-07-12T20:55:03-05:00";
        TemporalAccessor ta1 = DateTimeFormatter.ISO_DATE_TIME.parse(date1);
        TemporalAccessor ta2 = DateTimeFormatter.ISO_DATE_TIME.parse(date2);
        TemporalAccessor ta3 = DateTimeFormatter.ISO_DATE_TIME.parse(date3);
        TemporalAccessor ta4 = DateTimeFormatter.ISO_DATE_TIME.parse(date4);
        TemporalAccessor ta5 = DateTimeFormatter.ISO_DATE_TIME.parse(date5);
        TemporalAccessor ta6 = DateTimeFormatter.ISO_DATE_TIME.parse(date6);
        Date dateTest1 = Date.from(Instant.from(ta1));
        Date dateTest2 = Date.from(Instant.from(ta2));
        Date dateTest3 = Date.from(Instant.from(ta3));
        Date dateTest4 = Date.from(Instant.from(ta4));
        Date dateTest5 = Date.from(Instant.from(ta5));
        Date dateTest6 = Date.from(Instant.from(ta6));
        discount1.setDateTest(dateTest1);
        discount2.setDateTest(dateTest2);
        discount3.setDateTest(dateTest3);
        discount4.setDateTest(dateTest4);
        discount5.setDateTest(dateTest5);
        discount6.setDateTest(dateTest6);
        repository.save(discount1);
        repository.save(discount2);
        repository.save(discount3);
        repository.save(discount4);
        repository.save(discount5);
        repository.save(discount6);

        List<Discount> discounts = repository.findAllByDateTestBefore(dateTest4);
        for (Discount discount:
                discounts) {
            System.out.println(discount.getDateTest());
        }
    }
    */
}
