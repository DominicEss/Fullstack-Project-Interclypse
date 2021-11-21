package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.api.UnitOfMeasurement;
import com.starter.fullstack.config.EmbedMongoClientOverrideConfig;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test; 
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test Inventory DAO.
 */
@ContextConfiguration(classes = {EmbedMongoClientOverrideConfig.class})
@DataMongoTest
@RunWith(SpringRunner.class)
public class InventoryDAOTest {
  @Resource
  private MongoTemplate mongoTemplate;
  private InventoryDAO inventoryDAO;
  private static final String NAME = "Amber";
  private static final String PRODUCT_TYPE = "Hops";
  private final BigDecimal[] NUMS = {BigDecimal.valueOf(0), BigDecimal.valueOf(1),  
                                     BigDecimal.valueOf(5), BigDecimal.valueOf(14),
                                     BigDecimal.valueOf(15), BigDecimal.valueOf(24)};
  private final Instant[] DATES = { Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS),
                                    Instant.now().plus(2, ChronoUnit.DAYS), Instant.now().plus(3, ChronoUnit.DAYS),
                                    Instant.now().plus(4, ChronoUnit.DAYS), Instant.now().plus(5, ChronoUnit.DAYS) };
  private final String[] FILTER_TERMS = { "bestBeforeDate", "unitOfMeasurement", "amount" };
  private final String[] FILTER_OPTIONS = { "is", "lt", "gt" };

  @Before
  public void setup() {
    this.inventoryDAO = new InventoryDAO(this.mongoTemplate);
  }

  @After
  public void tearDown() {
    this.mongoTemplate.dropCollection(Inventory.class);
  }



 /**
  * Test Find All method. 
  */
  @Test
  public void findAll() {
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    this.mongoTemplate.save(inventory);

    List<Inventory> actualInventory = this.inventoryDAO.findAll();
    Assert.assertFalse(actualInventory.isEmpty());
  }



 /**
  * Test create method. 
  */
  @Test
 public void create() {
    // create inventory object to put into mongo collection
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);

    // Check to make the ID is correctly set to NULL
    Assert.assertNull(inventory.getId());

    // this is assuming findAll has no bugs 
    List<Inventory> actualInventory = this.inventoryDAO.findAll();

    // There isn't anything in the list now so it should be empty
    Assert.assertTrue(actualInventory.isEmpty());


    // add inventory object with create
    this.inventoryDAO.create(inventory);

    // this is assuming findAll has no bugs 
    actualInventory = this.inventoryDAO.findAll();

    // There should be something in the list now so it shouldn't be empty
    Assert.assertFalse(actualInventory.isEmpty());
  }



  /**
  * Test delete method.
  */
  @Test
 public void delete() {
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    this.mongoTemplate.save(inventory);

    List<Inventory> actualInventory = this.inventoryDAO.findAll();
    Assert.assertFalse(actualInventory.isEmpty());
    
    List<String> idNumber = Arrays.asList(inventory.getId());

    Optional<Inventory> opt = this.inventoryDAO.delete(idNumber);
    Assert.assertTrue(opt.isPresent());

    actualInventory = this.inventoryDAO.findAll();
    Assert.assertTrue(actualInventory.isEmpty());
  }



 /**
  * Test filter method. 
  */
  @Test
 public void filterRetrieve() {
    List<UnitOfMeasurement> MeasurementUnits = Arrays.asList(UnitOfMeasurement.values());
    
    // Add test data *note NUMS size == UnitOfMeasurement size == DATES size*
    for (int i = 0; i < NUMS.length; i++) {
      Inventory inventory = new Inventory();
      inventory.setName(NAME);
      inventory.setProductType(PRODUCT_TYPE);
      inventory.setAmount(NUMS[i]);
      inventory.setUnitOfMeasurement(MeasurementUnits.get(i));
      inventory.setBestBeforeDate(DATES[i]);

      this.mongoTemplate.save(inventory);
    }

    // At this point, the database is loaded with one of each type of amount, measurement unit, & dates

    List<Inventory> filteredList;

    System.out.println("\n\n\nDatabase Data \n\n\n" + this.mongoTemplate.findAll(Inventory.class) + "\n\n\n");



    // Test bestBeforeDate
    for (int i = 0; i < DATES.length; i++) {
      String currentDate = formatToISODate(Date.from(DATES[i]));
      System.out.println("\n\n\n Date string: " + currentDate);

      // FILTER_OPTIONS[0] == is   FILTER TERMS[0] == "bestBeforeDate"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[0], FILTER_OPTIONS[0], currentDate);

      System.out.println("\n\n\n Testing Date["+ i + "] is: " + filteredList.size() + " Should be: 1\n\n\n");

      // There is only one of each amount in the list
      Assert.assertTrue(filteredList.size() == 1);



      // FILTER_OPTIONS[1] == lt   FILTER TERMS[0] == "bestBeforeDate"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[0], FILTER_OPTIONS[1], currentDate);

      System.out.println("\n\n\n Testing Date["+ i + "] lt: " + filteredList.size() + " Should be: " + i + "\n\n\n");
      System.out.println(filteredList);

      // Since the list is full of ascending numbers in order without repeats,
      // the ith index will have i values less than it
      Assert.assertTrue(filteredList.size() == i);



      // FILTER_OPTIONS[2] == gt   FILTER TERMS[0] == "bestBeforeDate"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[0], FILTER_OPTIONS[2], currentDate);

      System.out.println("\n\n\n Testing Date["+ i + "] gt: " + filteredList.size() +
                         " Should be: " + (NUMS.length - i - 1) + "\n\n\n");

      // Since the list is full of ascending numbers in order without repeats,
      // the ith index will have (length of list - i - 1)  values greater than it
      //                                      *note* -1 to exclude the ith index
      Assert.assertTrue(filteredList.size() == (NUMS.length - i - 1));

    }



    // Test unitOfMeasurement
    for (int i = 0; i < UnitOfMeasurement.values().length; i++) {

      // grabs the enum into list
      UnitOfMeasurement[] filterType = UnitOfMeasurement.values();

      // FILTER TERMS[1] == "unitOfMeasurement"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[1], filterType[i].toString(), "0");

      // there was only one of each unitOfMeasurement added to the list
      Assert.assertTrue(filteredList.size() == 1);
    }



    // Test amount
    for (int i = 0; i < NUMS.length; i++) {
      String currentNumber = NUMS[i].toString();

      // FILTER_OPTIONS[0] == is   FILTER TERMS_2 == "amount"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[2], FILTER_OPTIONS[0], currentNumber);

      System.out.println("\n\n\n Testing amount[" + i + "] is: " + filteredList.size() + " Should be: 1\n\n\n");

      // There is only one of each amount in the list
      Assert.assertTrue(filteredList.size() == 1);



      // FILTER_OPTIONS[1] == lt   FILTER TERMS_2 == "amount"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[2], FILTER_OPTIONS[1], currentNumber);

      System.out.println("\n\n\n Testing amount[" + i + "] lt: " + filteredList.size() + " Should be: " + i + "\n\n\n");
      System.out.println(filteredList);

      // Since the list is full of ascending numbers in order without repeats,
      // the ith index will have i values less than it
      Assert.assertTrue(filteredList.size() == i);



      // FILTER_OPTIONS[2] == gt   FILTER TERMS_2 == "amount"
      filteredList = this.inventoryDAO.filterRetrieve(FILTER_TERMS[2], FILTER_OPTIONS[2], currentNumber);

      System.out.println("\n\n\n Testing amount[" + i + "] gt: " + filteredList.size() + 
                         " Should be: " + (NUMS.length - i - 1) + "\n\n\n");

      // Since the list is full of ascending numbers in order without repeats,
      // the ith index will have (length of list - i - 1)  values greater than it
      //                                      *note* -1 to exclude the ith index
      Assert.assertTrue(filteredList.size() == (NUMS.length - i - 1));



      
    }



  }






  /**
   *  format date to database string format 
   *  @param date date to be converted
   *  
   *  @return String date converted to database format
   */
  private String formatToISODate(Date date) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    df.setTimeZone(tz);
    return df.format(date);
  }

}