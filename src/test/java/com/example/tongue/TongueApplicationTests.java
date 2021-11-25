package com.example.tongue;

import com.example.tongue.discounts.DateTests;
import com.example.tongue.discounts.DiscountTests;
import com.example.tongue.discounts.RegexTest;
import com.example.tongue.merchants.repositories.DiscountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TongueApplicationTests {

    private DiscountRepository repository;
    public TongueApplicationTests(@Autowired DiscountRepository repository){
        this.repository=repository;
    }
    @Test
    void contextLoads() {
        //DiscountTests tests = new DiscountTests();
        //tests.testDiscount2();
        //DateTests dateTests = new DateTests(repository);
        //dateTests.DiscountDatesTest();
        //RegexTest regexTest = new RegexTest();
        //regexTest.testProductRegex();
    }

}
