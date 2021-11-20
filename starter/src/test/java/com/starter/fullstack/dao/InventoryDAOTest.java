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
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test; 
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
                                    Instant.now().plus(4, ChronoUnit.DAYS), Instant.now().plus(5, ChronoUnit.DAYS), };
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

    System.out.println("\n\n\nDatabase Data \n\n\n" + this.mongoTemplate.findAll(Inventory.class) + "\n\n\n");


    // start with testing amount since its the easiest
    for (int i = 0; i < FILTER_OPTIONS.length; i++) {

      // gets random index from NUMS list to use to test with
      int randIndex = ThreadLocalRandom.current().nextInt(0, NUMS.length);

      List<Inventory> testList = this.inventoryDAO.filterRetrieve("amount", FILTER_OPTIONS[i],
                                                                  NUMS[randIndex].toString());

      // Build direct mongoTemplate call to compare against
      List<Inventory> actualList = directMongoCall("amount", FILTER_OPTIONS[i], NUMS[randIndex].toString());
           
      System.out.println("\n\n\n\n\n\n\n Checkinging amount " + FILTER_OPTIONS[i] + "\n\n" + testList + 
                         "\n\n Against: \n\n" + actualList);

      Assert.assertTrue(testList.equals(actualList));
    }


    // Test bestBeforeDate
    for (int i = 0; i < FILTER_OPTIONS.length; i++) {

      // gets random index from NUMS list to use to test with
      int randIndex = ThreadLocalRandom.current().nextInt(0, DATES.length);

      List<Inventory> testList = this.inventoryDAO.filterRetrieve("bestBeforeDate", FILTER_OPTIONS[i],
                                                                  formatToISODate(Date.from(DATES[randIndex])));

      // Build direct mongoTemplate call to compare against
      List<Inventory> actualList = directMongoCall("bestBeforeDate", FILTER_OPTIONS[i], formatToISODate(Date.from(DATES[randIndex])));
           
      System.out.println("\n\n\n\n\n\n\n Checkinging date " + formatToISODate(Date.from(DATES[randIndex])) + "  " + 
                         FILTER_OPTIONS[i] + "\n\n" + testList + "\n\n Against: \n\n" + actualList);

      Assert.assertTrue(testList.equals(actualList));
    }


  }






  /**
   * direct mongo call to Filter Retrieve Inventory.
   * @param filterTerm Term to filter on. Options:
   *                                    -bestBeforeDate
   *                                    -unitOfMeasurement
   *                                    -amount
   * @param filterType Filter options. Specific options for parameters:
   *                                    -bestBeforeDate -> lt, gt, is
   *                                    -unitOfMeasurement -> c, gal, oz, pt, lb, qt
   *                                    -amount -> lt, gt, is
   * @param filterValue Filter value. Value to filter with:
   *                                    -bestBeforeDate -> date to compare against
   *                                    -unitOfMeasurement -> 0 since it doesn't matter
   *                                    -amount -> value to compare against
   * @return Found Inventory.
   */
  private List<Inventory> directMongoCall(String filterTerm, String filterType, String filterValue) {
    Criteria criteria = new Criteria();

    switch (filterType) {

      case "lt":
        criteria = where(filterTerm).lt(filterValue);
        break;

      case "gt":
        criteria = where(filterTerm).gt(filterValue);
        break;

      case "is":
        criteria = where(filterTerm).is(filterValue);
        break;
    }

    Query query = new Query();
    query.addCriteria(criteria);

    return this.mongoTemplate.find(query, Inventory.class);
  }


  private String formatToISODate(Date date) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    df.setTimeZone(tz);
    return df.format(date);
  }

}