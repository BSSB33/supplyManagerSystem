package com.elte.supplymanagersystem;

import com.elte.supplymanagersystem.controllers.CompanyControllerTest;
import com.elte.supplymanagersystem.controllers.HistoryControllerTest;
import com.elte.supplymanagersystem.controllers.UserControllerTest;
import com.elte.supplymanagersystem.entities.History;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite.SuiteClasses({CompanyControllerTest.class, HistoryControllerTest.class, UserControllerTest.class})
@RunWith(Suite.class)
public class SupplyManagerSystemApplicationTests {
    //TODO restart app / after before tests
}
