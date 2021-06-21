package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.config.EmbedMongoClientOverrideConfig;
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
  private static final String PRODUCT_TYPE = "hops";

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
    
    String idNumber = inventory.getId();
 
    Optional<Inventory> opt = this.inventoryDAO.delete(idNumber);
    Assert.assertTrue(opt.isPresent());

    actualInventory = this.inventoryDAO.findAll();
    Assert.assertTrue(actualInventory.isEmpty());
  }
}
