package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.util.Assert;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


/**
 * Inventory DAO
 */
public class InventoryDAO  {
  private final MongoTemplate mongoTemplate;
  private static final String NAME = "name";
  private static final String PRODUCT_TYPE = "productType";

  /**
   * Default Constructor.
   * @param mongoTemplate MongoTemplate.
   */
  public InventoryDAO(MongoTemplate mongoTemplate) {
    Assert.notNull(mongoTemplate, "MongoTemplate must not be null.");
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * Constructor to build indexes for rate blackout object
   */
  @PostConstruct
  public void setupIndexes() {
    IndexOperations indexOps = this.mongoTemplate.indexOps(Inventory.class);
    indexOps.ensureIndex(new Index(NAME, Sort.Direction.ASC));
    indexOps.ensureIndex(new Index(PRODUCT_TYPE, Sort.Direction.ASC));
  }

  /**
   * Find All Inventory.
   * @return List of found Inventory.
   */
  public List<Inventory> findAll() {
    return this.mongoTemplate.findAll(Inventory.class);
  }

  /**
   * Save Inventory.
   * @param inventory Inventory to Save/Update.
   * @return Created/Updated Inventory.
   */
  public Inventory create(Inventory inventory) {
    // set id to NULL
    //inventory.setId(null);

    System.out.println("In InventoryDAO create " + inventory.getId());

    //Query query = new Query(Criteria.where("_id").is(inventory.getId()));
    Update update = new Update().set( "name", inventory.getName())
                                .set( "productType", inventory.getProductType())
                                .set( "description", inventory.getDescription())
                                .set( "averagePrice", inventory.getAveragePrice())
                                .set( "amount", inventory.getAmount())
                                .set( "unitOfMeasurement", inventory.getUnitOfMeasurement())
                                .set( "bestBeforeDate", inventory.getBestBeforeDate());
                             //   .set( "neverExpires", inventory.neverExpires());

    // insert inventory object into database
    mongoTemplate.upsert(query(where("_id").is(inventory.getId())), update, Inventory.class);

    return inventory;
  }

  /**
   * Retrieve Inventory.
   * @param id Inventory id to Retrieve.
   * @return Found Inventory.
   */
  public Optional<Inventory> retrieve(String id) {
    Inventory foundInventory = null;
    foundInventory = mongoTemplate.findById(id, Inventory.class);
    
    Optional<Inventory> optFoundInv = Optional.ofNullable(foundInventory);
 
    return optFoundInv;
  }

  /**
   * Update Inventory.
   * @param id Inventory id to Update.
   * @param inventory Inventory to Update.
   * @return Updated Inventory.
   */
  public Optional<Inventory> update(String id, Inventory inventory) {
    //Inventory user = mongoTemplate.findOne(query(where("_id").is(id)), Inventory.class);
    

    return Optional.empty();
  }

  /**
   * Delete Inventory By Id.
   * @param id Id of Inventory.
   * @return Deleted Inventory.
   */
  public Optional<Inventory> delete(List<String> id) {
    Inventory deletedInventory = null;

    for (int i = 0; i < id.size(); i++) {
      deletedInventory = mongoTemplate.findAndRemove(query(where("_id").is(id.get(i))), Inventory.class);
    }

    Optional<Inventory> optDeletedInv = Optional.ofNullable(deletedInventory);
 
    return optDeletedInv;
  }
}
