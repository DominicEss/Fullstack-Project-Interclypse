package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.api.UnitOfMeasurement;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


/**
 * Inventory DAO
 */
public class InventoryDAO  {
  private final MongoTemplate mongoTemplate;
  private static final String NAME = "name";
  private static final String PRODUCT_TYPE = "productType";
  private static final String ASC = "asc";

  /**
   * Default Constructor.
   * @param mongoTemplate MongoTemplate.
   */
  public InventoryDAO(MongoTemplate mongoTemplate) {
    Assert.notNull(mongoTemplate, "MongoTemplate must not be null.");
    this.mongoTemplate = mongoTemplate;
    System.out.println("\n\n\n MongoTemplate \n\n\n\n" + mongoTemplate.getCollectionNames() + "\n\n\n");
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
   * @param sortVariable variable to sort by
   * @param direction direction to sort in
   * @return List of found Inventory.
   */
  public List<Inventory> findSorted(String sortVariable, String direction) {
    Query query = new Query();

    Collation collation = Collation.of("en").numericOrderingEnabled();

    if (direction.equals(ASC)) {
      query.with(Sort.by(Sort.Direction.ASC, sortVariable)).collation(collation);
    } else {
      query.with(Sort.by(Sort.Direction.DESC, sortVariable)).collation(collation);
    }

    List<Inventory> myClassList =  mongoTemplate.find(query, Inventory.class);
    return myClassList;
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
    inventory.setId(null);

    mongoTemplate.insert(inventory);

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
   * Filter Retrieve Inventory.
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
  public List<Inventory> filterRetrieve(String filterTerm, String filterType, String filterValue) {
    List<Inventory> foundInventory = null;

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

      // can either be a unitOfMeasurement or garbage at this point
      default:
        // if the filterType doesn't have a valid unit of measurement, 
        // then it is not a valid request
        if (!UnitOfMeasurement.contains(filterType)) {
          return null;
        }
        criteria = where(filterTerm).is(filterType);
    }


    Collation collation = Collation.of("en").numericOrderingEnabled();

    Query query = new Query();
    query.collation(collation);

    query.addCriteria(criteria);


    foundInventory = mongoTemplate.find(query, Inventory.class);


    
    return foundInventory;
  }

  /**
   * Update Inventory.
   * @param id Inventory id to Update.
   * @param inventory Inventory to Update.
   * @return Updated Inventory.
   */
  public Optional<Inventory> update(String id, Inventory inventory) {

    Update update = new Update().set("name", inventory.getName())
                                .set("productType", inventory.getProductType())
                                .set("description", inventory.getDescription())
                                .set("averagePrice", inventory.getAveragePrice())
                                .set("amount", inventory.getAmount())
                                .set("unitOfMeasurement", inventory.getUnitOfMeasurement())
                                .set("bestBeforeDate", inventory.getBestBeforeDate())
                                .set("neverExpires", inventory.getNeverExpires());

    mongoTemplate.upsert(query(where("_id").is(id)), update, Inventory.class);
    

    Optional<Inventory> optFoundInv = Optional.ofNullable(inventory);
 
    return optFoundInv;


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
