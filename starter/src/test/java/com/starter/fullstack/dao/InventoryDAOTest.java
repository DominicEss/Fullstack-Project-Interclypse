package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.api.UnitOfMeasurement;
import com.starter.fullstack.config.EmbedMongoClientOverrideConfig;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
  private final Instant[] DATES = { Instant.now().truncatedTo(ChronoUnit.SECONDS),
                                    Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS),
                                    Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS),
                                    Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS),
                                    Instant.now().plus(4, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS),
                                    Instant.now().plus(5, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS) };
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

    // grabs the enum into list
    UnitOfMeasurement[] filterType = UnitOfMeasurement.values();

    // Add test data *note NUMS size == UnitOfMeasurement size == DATES size*
    for (int i = 0; i < NUMS.length; i++) {
      Inventory inventory = new Inventory();
      inventory.setName(NAME);
      inventory.setProductType(PRODUCT_TYPE);
      inventory.setAmount(NUMS[i]);
      inventory.setUnitOfMeasurement(filterType[i]);
      inventory.setBestBeforeDate(DATES[i]);

      this.inventoryDAO.create(inventory);
    }

    // At this point, the database is loaded with one of each type of amount, measurement unit, & dates

    List<Inventory> filteredList;


    UnitOfMeasurement emptyMeasure = null;
    BigDecimal emptyBigDecimal = null;
    Instant emptyInstant = null;



    // Test bestBeforeDate
    for (int i = 0; i < DATES.length; i++) {

      Instant currentDate = DATES[i];


      filteredList = this.inventoryDAO.filterRetrieve(emptyMeasure, emptyBigDecimal, currentDate);



      // Since the list is full of ascending numbers in order without repeats,
      // the ith index will have i values less than it
      Assert.assertTrue(filteredList.size() == i);
    }




    //Test unitOfMeasurement
    for (int i = 0; i < UnitOfMeasurement.values().length; i++) {

      filteredList = this.inventoryDAO.filterRetrieve(filterType[i], emptyBigDecimal, emptyInstant);


      // There is only one of each unit of measuremenat in the list
      Assert.assertTrue(filteredList.size() == 1);
    }



    // Test amount
    for (int i = 0; i < NUMS.length; i++) {
      String currentNumber = NUMS[i].toString();

      filteredList = this.inventoryDAO.filterRetrieve(emptyMeasure, NUMS[i], emptyInstant);

      // There is only one of each amount in the list
      Assert.assertTrue(filteredList.size() == 1);
    }



    // Test multiple arguments
    filteredList = this.inventoryDAO.filterRetrieve(filterType[1], NUMS[1], DATES[2]);

    Assert.assertTrue(filteredList.size() == 1);



  }
}